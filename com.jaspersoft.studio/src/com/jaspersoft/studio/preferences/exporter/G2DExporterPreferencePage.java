/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.exporter;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.help.HelpSystem;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.StudioPreferencePage;
import com.jaspersoft.studio.preferences.editor.number.FloatFieldEditor;
import com.jaspersoft.studio.preferences.util.FieldEditorOverlayPage;
import com.jaspersoft.studio.preferences.util.PropertiesHelper;

import net.sf.jasperreports.eclipse.util.Misc;

/*
 * 
 */
public class G2DExporterPreferencePage extends FieldEditorOverlayPage {

	public static final String NSF_EXPORT_G2D_MINJOBSIZE = "net.sf.jasperreports.export.graphics2d.min.job.size"; //$NON-NLS-1$
	public static final String NSF_EXPORT_G2D_ZOOM_RATIO = "net.sf.jasperreports.export.graphics2d.zoom.ratio"; //$NON-NLS-1$

	public G2DExporterPreferencePage() {
		super(GRID);
		setPreferenceStore(JaspersoftStudioPlugin.getInstance().getPreferenceStore());
		setDescription(Messages.G2DExporterPreferencePage_2);
	}

	/**
	 *
	 */
	public void createFieldEditors() {
		BooleanFieldEditor bf = new BooleanFieldEditor(NSF_EXPORT_G2D_MINJOBSIZE, Messages.G2DExporterPreferencePage_3,
				getFieldEditorParent());
		addField(bf);
		HelpSystem.setHelp(bf.getDescriptionControl(getFieldEditorParent()),
				StudioPreferencePage.REFERENCE_PREFIX + bf.getPreferenceName());

		FloatFieldEditor fe = new FloatFieldEditor(NSF_EXPORT_G2D_ZOOM_RATIO, Messages.G2DExporterPreferencePage_4,
				getFieldEditorParent());
		fe.setValidRange(0, Float.MAX_VALUE);
		addField(fe);
		HelpSystem.setHelp(fe.getTextControl(getFieldEditorParent()),
				StudioPreferencePage.REFERENCE_PREFIX + fe.getPreferenceName());
		
		//Eventually create the extensions for the page
		super.createFieldEditors();
	}

	public static void getDefaults(IPreferenceStore store) {
		store.setDefault(NSF_EXPORT_G2D_MINJOBSIZE, PropertiesHelper.DPROP.getProperty(NSF_EXPORT_G2D_MINJOBSIZE));
		store.setDefault(NSF_EXPORT_G2D_ZOOM_RATIO,
				Misc.nvl(PropertiesHelper.DPROP.getProperty(NSF_EXPORT_G2D_ZOOM_RATIO), "1")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	public String getPageId() {
		return "com.jaspersoft.studio.preferences.exporter.G2DExporterPreferencePage.property"; //$NON-NLS-1$
	}

}
