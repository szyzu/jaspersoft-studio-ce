/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences;

import java.util.Properties;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.editor.properties.SearchPropertyListFieldEditor;
import com.jaspersoft.studio.preferences.util.FieldEditorOverlayPage;

import net.sf.jasperreports.eclipse.util.FilePrefUtil;
import net.sf.jasperreports.eclipse.util.FileUtils;

/*
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing
 * <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself. <p> This page is used to modify preferences only. They
 * are stored in the preference store that belongs to the main plug-in class. That way, preferences can be accessed
 * directly via the preference store.
 */

public class PropertiesPreferencePage extends FieldEditorOverlayPage {

	public PropertiesPreferencePage() {
		super(GRID);
		setPreferenceStore(JaspersoftStudioPlugin.getInstance().getPreferenceStore());
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new SearchPropertyListFieldEditor("properties_list", //$NON-NLS-1$
				Messages.PropertiesPreferencePage_jrPropertiesTitle, getFieldEditorParent()));

		// Eventually create the extensions for the page
		super.createFieldEditors();
	}

	public static void getDefaults(IPreferenceStore store) {
		store.setDefault(FilePrefUtil.NET_SF_JASPERREPORTS_JRPROPERTIES,
				FileUtils.getPropertyAsString(new Properties()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	public String getPageId() {
		return "com.jaspersoft.studio.preferences.PropertiesPreferencePage.property"; //$NON-NLS-1$
	}

}
