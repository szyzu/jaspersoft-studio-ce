/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.rcp.p2;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.equinox.p2.ui.Policy;
import org.eclipse.swt.SWT;
import org.osgi.service.prefs.Preferences;

import com.jaspersoft.studio.rcp.Activator;

/**
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		Preferences node = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		// default values
		node.putBoolean(PreferenceConstants.REPOSITORIES_VISIBLE, false);
		node.putBoolean(PreferenceConstants.SHOW_LATEST_VERSION_ONLY, true);
		node.putBoolean(PreferenceConstants.AVAILABLE_SHOW_ALL_BUNDLES, false);
		node.putBoolean(PreferenceConstants.INSTALLED_SHOW_ALL_BUNDLES, false);
		node.putBoolean(PreferenceConstants.AVAILABLE_GROUP_BY_CATEGORY, true);
		node.putBoolean(PreferenceConstants.SHOW_DRILLDOWN_REQUIREMENTS, false);
		node.putInt(PreferenceConstants.RESTART_POLICY, Policy.RESTART_POLICY_PROMPT_RESTART_OR_APPLY);
		node.putInt(PreferenceConstants.UPDATE_WIZARD_STYLE, Policy.UPDATE_STYLE_MULTIPLE_IUS);
		node.putBoolean(PreferenceConstants.FILTER_ON_ENV, false);
		node.putInt(PreferenceConstants.UPDATE_DETAILS_HEIGHT, SWT.DEFAULT);
		node.putInt(PreferenceConstants.UPDATE_DETAILS_WIDTH, SWT.DEFAULT);
	}
}
