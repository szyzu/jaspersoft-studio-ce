/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

import com.jaspersoft.studio.server.messages.Messages;

import net.sf.jasperreports.eclipse.util.FileUtils;

/**
 * Extracts the CRL distribution points from the certificate (if available) and
 * checks the certificate revocation status against the CRLs coming from the
 * distribution points. Supports HTTP, HTTPS, FTP and LDAP based URLs.
 */
public class CRLVerifier {

	public static void verifyCertificateCRLs(X509Certificate cert) throws CertificateException {
		try {
			List<String> crlDistPoints = getCrlDistributionPoints(cert);
			for (String crlDP : crlDistPoints) {
				X509CRL crl = downloadCRL(crlDP);
				if (crl.isRevoked(cert))
					throw new CertificateException(Messages.CRLVerifier_0 + crlDP);
			}
		} catch (CertificateException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CertificateException(Messages.CRLVerifier_1 + cert.getSubjectX500Principal());
		}
	}

	/**
	 * Downloads CRL from given URL. Supports http, https, ftp and ldap based URLs.
	 */
	private static X509CRL downloadCRL(String crlURL)
			throws IOException, CRLException, CertificateException, NamingException {
		if (crlURL.startsWith(Messages.CRLVerifier_2) || crlURL.startsWith("https://") || crlURL.startsWith("ftp://")) //$NON-NLS-2$ //$NON-NLS-3$
			return downloadCRLFromWeb(crlURL);
		if (crlURL.startsWith("ldap://")) //$NON-NLS-1$
			return downloadCRLFromLDAP(crlURL);
		throw new CertificateException(Messages.CRLVerifier_6 + crlURL);
	}

	/**
	 * Downloads a CRL from given LDAP url, e.g.
	 * ldap://ldap.infonotary.com/dc=identity-ca,dc=infonotary,dc=com
	 */
	private static X509CRL downloadCRLFromLDAP(String ldapURL)
			throws NamingException, CRLException, CertificateException {
		Map<String, String> env = new HashMap<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, Messages.CRLVerifier_7);
		env.put(Context.PROVIDER_URL, ldapURL);

		DirContext ctx = new InitialDirContext((Hashtable<String, String>) env);
		Attributes avals = ctx.getAttributes(""); //$NON-NLS-1$
		Attribute aval = avals.get(Messages.CRLVerifier_9);
		byte[] val = (byte[]) aval.get();
		if ((val == null) || (val.length == 0))
			throw new CertificateException(Messages.CRLVerifier_10 + ldapURL);
		InputStream inStream = new ByteArrayInputStream(val);
		CertificateFactory cf = CertificateFactory.getInstance("X.509"); //$NON-NLS-1$
		return (X509CRL) cf.generateCRL(inStream);
	}

	/**
	 * Downloads a CRL from given HTTP/HTTPS/FTP URL, e.g.
	 * http://crl.infonotary.com/crl/identity-ca.crl
	 */
	private static X509CRL downloadCRLFromWeb(String crlURL) throws IOException, CertificateException, CRLException {
		InputStream crlStream = new URL(crlURL).openStream();
		try {
			return (X509CRL) CertificateFactory.getInstance("X.509").generateCRL(crlStream); //$NON-NLS-1$
		} finally {
			FileUtils.closeStream(crlStream);
		}
	}

	/**
	 * Extracts all CRL distribution point URLs from the "CRL Distribution Point"
	 * extension in a X.509 certificate. If CRL distribution point extension is
	 * unavailable, returns an empty list.
	 */
	public static List<String> getCrlDistributionPoints(X509Certificate cert) throws IOException {
		byte[] crldpExt = cert.getExtensionValue(Extension.cRLDistributionPoints.getId());
		if (crldpExt == null)
			return new ArrayList<>();
		List<String> crlUrls = new ArrayList<>();
		ASN1InputStream oAsnInStream = null;
		ASN1InputStream oAsnInStream2 = null;
		try {
			oAsnInStream = new ASN1InputStream(new ByteArrayInputStream(crldpExt));
			ASN1Primitive derObjCrlDP = oAsnInStream.readObject();
			DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
			byte[] crldpExtOctets = dosCrlDP.getOctets();
			oAsnInStream2 = new ASN1InputStream(new ByteArrayInputStream(crldpExtOctets));
			CRLDistPoint distPoint = CRLDistPoint.getInstance(oAsnInStream2.readObject());

			for (DistributionPoint dp : distPoint.getDistributionPoints()) {
				DistributionPointName dpn = dp.getDistributionPoint();
				// Look for URIs in fullName
				if (dpn != null && dpn.getType() == DistributionPointName.FULL_NAME) {
					GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
					// Look for an URI
					for (int j = 0; j < genNames.length; j++)
						if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier)
							crlUrls.add(DERIA5String.getInstance(genNames[j].getName()).getString());
				}
			}
		} finally {
			FileUtils.closeStream(oAsnInStream);
			FileUtils.closeStream(oAsnInStream2);
		}
		return crlUrls;
	}

}
