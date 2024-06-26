/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.templates;

import org.eclipse.jface.preference.PathEditor;
import org.eclipse.ui.IWorkbench;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.util.FieldEditorOverlayPage;

/**
 * Preference page for the locations of the JRXML templates.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class TemplateLocationsPreferencePage extends FieldEditorOverlayPage {
	public static final String TPP_TEMPLATES_LOCATIONS_LIST = "TEMPLATES_LOCATIONS_LIST"; //$NON-NLS-1$
	public static final String PAGE_ID = "com.jaspersoft.studio.preferences.templates.TemplateLocationsPreferencePage.property"; //$NON-NLS-1$

	public TemplateLocationsPreferencePage() {
		super(GRID);
		setPreferenceStore(JaspersoftStudioPlugin.getInstance().getPreferenceStore());
		setDescription(Messages.TemplateLocationsPreferencePage_Description);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public String getPageId() {
		return PAGE_ID;
	}

	@Override
	protected void createFieldEditors() {
		addField(
				new PathEditor(TPP_TEMPLATES_LOCATIONS_LIST,
						Messages.TemplateLocationsPreferencePage_Locations, 
						Messages.TemplateLocationsPreferencePage_Message,getFieldEditorParent()));
		
		//Eventually create the extensions for the page
		super.createFieldEditors();
	}

}
