/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.rcp.p2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.internal.p2.ui.model.ElementUtils;
import org.eclipse.equinox.internal.p2.ui.model.MetadataRepositoryElement;
import org.eclipse.equinox.p2.ui.ProvisioningUI;

import com.jaspersoft.studio.rcp.Activator;
import com.jaspersoft.studio.rcp.messages.Messages;

import net.sf.jasperreports.eclipse.util.BundleCommonUtils;

/**
 * Utility class for P2 related tasks.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class P2Util {

	/** 
	 * Sets the defaults repositories to look into.
	 * <p>
	 * 
	 * We programmatically set the repositories as alternative to the p2.inf method.
	 * In fact, if the RCP application is installed into a user-write protected directory, 
	 * p2.inf will fail to be able to add the repositories (as it tries to modify the 
	 * configuration on the first RCP run).
	 * 
	 * @param repositoryURLs the list of repository URLs
	 */
	public static void setRepositories(List<String> repositoryURLs){
		try {
			List<MetadataRepositoryElement> repos = new ArrayList<MetadataRepositoryElement>(repositoryURLs.size());
			for(String url : repositoryURLs){
				MetadataRepositoryElement repoEl = 
						new MetadataRepositoryElement(null, new URI(url), true);
				repos.add(repoEl);
			}
			ElementUtils.updateRepositoryUsingElements(
					ProvisioningUI.getDefaultUI(), repos.toArray(new MetadataRepositoryElement[repos.size()]));
		} catch (URISyntaxException e) {
			BundleCommonUtils.logError(Activator.PLUGIN_ID,Messages.P2Util_ErrorMessage, e);
		}
	}
	
}
