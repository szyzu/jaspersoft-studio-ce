/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.protocol.restv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;

import com.jaspersoft.studio.ConfigurationManager;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.protocol.CRLVerifier;
import com.jaspersoft.studio.server.utils.Pass;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Misc;

public class CertChainValidator {
	private static String fname = System.getProperty("javax.net.ssl.trustStore"); //$NON-NLS-1$
	private static String fksname = System.getProperty("javax.net.ssl.trustStore"); //$NON-NLS-1$
	private static final char[] spass = Misc.nvl(System.getProperty("javax.net.ssl.trustStorePassword")).toCharArray(); //$NON-NLS-1$
	public static final char[] kpass = Pass
			.getPassKeyStore(Misc.nvl(System.getProperty("javax.net.ssl.keyStorePassword"), "keystore")) //$NON-NLS-1$
			.toCharArray();
	private static String stype = System.getProperty("javax.net.ssl.trustStoreType"); //$NON-NLS-1$

	/**
	 * FIXME - This method is used as workaround because of the "faulty"
	 * Security Provider class found in the TIBCO Progress Google Big Query driver.
	 * See for the details the related bug: JSS-3175.
	 */
	private static void removeFaultySecurityProviders() {
		Security.removeProvider("DDPKIProvider");
	}
	
	public static KeyStore getDefaultTrustStore()
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		removeFaultySecurityProviders();
		stype = System.getProperty("javax.net.ssl.trustStoreType"); //$NON-NLS-1$
		KeyStore trustStore = KeyStore.getInstance(Misc.isNullOrEmpty(stype) ? KeyStore.getDefaultType() : stype);
		fname = System.getProperty("javax.net.ssl.trustStore"); //$NON-NLS-1$
		File f = null;
		if (Misc.isNullOrEmpty(fname)) {
			f = new File(ConfigurationManager.getStorage("certificates"), "cert.ks"); //$NON-NLS-1$ //$NON-NLS-2$
			fname = f.getAbsolutePath();
		} else
			f = new File(fname);
		if (!f.exists()) {
			trustStore.load(null, spass);
		} else {
			FileInputStream fos = null;
			try {
				fos = new FileInputStream(fname);
				trustStore.load(fos, spass);
			} finally {
				FileUtils.closeStream(fos);
			}
		}
		return trustStore;
	}

	public static KeyStore getDefaultKeyStore()
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		String kstype = System.getProperty("javax.net.ssl.keyStoreType"); //$NON-NLS-1$
		KeyStore store = KeyStore.getInstance(Misc.isNullOrEmpty(stype) ? KeyStore.getDefaultType() : kstype);
		fksname = System.getProperty("javax.net.ssl.keyStore"); //$NON-NLS-1$
		File f = null;
		if (Misc.isNullOrEmpty(fksname)) {
			f = new File(ConfigurationManager.getStorage("certificates"), "certkeystore.ks"); //$NON-NLS-1$ //$NON-NLS-2$
			fksname = f.getAbsolutePath();
		} else
			f = new File(fksname);
		if (!f.exists()) {
			store.load(null, kpass);
		} else {
			FileInputStream fos = null;
			try {
				fos = new FileInputStream(fksname);
				store.load(fos, kpass);
			} finally {
				FileUtils.closeStream(fos);
			}
		}
		return store;
	}

	public static void writeKeyStore(KeyStore trustStore)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		writeKeyStore(fksname, trustStore);
	}

	protected static void writeKeyStore(String fname, KeyStore trustStore)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fname);
			trustStore.store(fos, kpass);
		} finally {
			FileUtils.closeStream(fos);
		}
	}

	public static void writeTrustStore(KeyStore trustStore)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		writeTrustStore(fname, trustStore);
	}

	protected static void writeTrustStore(String fname, KeyStore trustStore)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fname);
			trustStore.store(fos, spass);
		} finally {
			FileUtils.closeStream(fos);
		}
	}

	private static CertificateFactory cf;
	private static CertPathValidator validator;

	public static void validateKeyChain(X509Certificate[] chain, KeyStore trustStore)
			throws IOException, GeneralSecurityException {
		if (cf == null)
			cf = CertificateFactory.getInstance("X.509"); //$NON-NLS-1$
		if (validator == null)
			validator = CertPathValidator.getInstance("PKIX"); //$NON-NLS-1$
		Set<X509Certificate> trustCertificates = new HashSet<>();
		Enumeration<String> alias = trustStore.aliases();
		while (alias.hasMoreElements()) {
			Certificate c = trustStore.getCertificate(alias.nextElement());
			if (chain[0].equals(c))
				return;
			if (c instanceof X509Certificate)
				trustCertificates.add((X509Certificate) c);
		}
		try {
			chain[0].checkValidity();
		} catch (CertificateExpiredException e) {
			confirmCertificate(chain[0], chain, trustStore, e);
			return;
		}
		if (isSelfSigned(chain[0])) {
			confirmCertificate(chain[0], chain, trustStore,
					new CertificateException("This is a Self-Signed certificate"));
			return;
		}

		if (trustCertificates.contains(chain[chain.length - 1])) {
			List<X509Certificate> intermediateCerts = new ArrayList<>();
			for (int i = chain.length - 1; i > 0; i--)
				intermediateCerts.add(chain[i]);
			try {
				verifyCertificate(chain[0], trustCertificates, intermediateCerts, false);
				CRLVerifier.verifyCertificateCRLs(chain[0]);
			} catch (CertificateException e) {
				confirmCertificate(chain[0], chain, trustStore, e);
			}
		} else
			confirmCertificate(chain[chain.length - 1], new X509Certificate[] { chain[chain.length - 1] }, trustStore,
					new CertificateException(Messages.CertChainValidator_10));
	}

	private static PKIXCertPathBuilderResult verifyCertificate(X509Certificate cert,
			Set<X509Certificate> trustedRootCerts, List<X509Certificate> intermediateCerts,
			boolean verifySelfSignedCert) throws GeneralSecurityException {

		// Create the selector that specifies the starting certificate
		X509CertSelector selector = new X509CertSelector();
		selector.setCertificate(cert);

		// Create the trust anchors (set of root CA certificates)
		Set<TrustAnchor> trustAnchors = new HashSet<>();
		for (X509Certificate trustedRootCert : trustedRootCerts) {
			trustAnchors.add(new TrustAnchor(trustedRootCert, null));
		}

		// Configure the PKIX certificate builder algorithm parameters
		PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(trustAnchors, selector);

		// Disable CRL checks (this is done manually as additional step)
		pkixParams.setRevocationEnabled(false);

		// Specify a list of intermediate certificates
		CertStore intermediateCertStore = CertStore.getInstance("Collection", //$NON-NLS-1$
				new CollectionCertStoreParameters(intermediateCerts));
		pkixParams.addCertStore(intermediateCertStore);

		// Build and verify the certification chain
		CertPathBuilder builder = CertPathBuilder.getInstance("PKIX"); //$NON-NLS-1$
		return (PKIXCertPathBuilderResult) builder.build(pkixParams);
	}

	static class ShowDialog implements Runnable {
		public int result = Dialog.CANCEL;
		private CertificateException e;
		private X509Certificate client;
		private X509Certificate[] chain;

		public ShowDialog(X509Certificate client, X509Certificate[] chain, CertificateException e) {
			this.client = client;
			this.e = e;
			this.chain = chain;
		}

		public void run() {
			CertificateDialog d = new CertificateDialog(UIUtils.getShell(), e.getMessage(), client, chain);
			result = d.open();
		}
	};

	private static void confirmCertificate(X509Certificate client, X509Certificate[] chain, final KeyStore trustStore,
			CertificateException e)
			throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException {
		ShowDialog r = new ShowDialog(client, chain, e);
		UIUtils.getDisplay().syncExec(r);
		if (r.result == Dialog.OK) {
			trustStore.setCertificateEntry(UUID.randomUUID().toString().replaceAll("-", ""), client); //$NON-NLS-1$ //$NON-NLS-2$
			writeTrustStore(fname, trustStore);
			throw new RuntimeException(new InterruptedException());
		}
		throw e;
	}

	public static boolean isSelfSigned(X509Certificate cert)
			throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
		try {
			cert.verify(cert.getPublicKey());
			return true;
		} catch (SignatureException | InvalidKeyException sigEx) {
			return false;
		}
	}

}
