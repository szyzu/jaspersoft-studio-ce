/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.exporter;

import net.sf.jasperreports.eclipse.viewer.BrowserUtils;
import net.sf.jasperreports.export.CsvExporterConfiguration;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.IWorkbench;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.help.HelpSystem;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.StudioPreferencePage;
import com.jaspersoft.studio.preferences.editor.text.NStringFieldEditor;
import com.jaspersoft.studio.preferences.util.FieldEditorOverlayPage;
import com.jaspersoft.studio.preferences.util.PropertiesHelper;

/*
 * 
 */
public class CSVExporterPreferencePage extends FieldEditorOverlayPage {

	public static final String PAGE_ID = "com.jaspersoft.studio.preferences.exporter.CSVExporterPreferencePage.property";

	public CSVExporterPreferencePage() {
		super(GRID);
		setPreferenceStore(JaspersoftStudioPlugin.getInstance().getPreferenceStore());
		setDescription(Messages.CSVExporterPreferencePage_title);
	}

	/**
	 *
	 */
	public void createFieldEditors() {
		NStringFieldEditor tf = new NStringFieldEditor(CsvExporterConfiguration.PROPERTY_RECORD_DELIMITER,
				Messages.CSVExporterPreferencePage_3, 4, getFieldEditorParent());
		tf.setEmptyStringAllowed(false);
		tf.setTextLimit(10);
		addField(tf);
		HelpSystem.setHelp(tf.getTextControl(getFieldEditorParent()),
				StudioPreferencePage.REFERENCE_PREFIX + tf.getPreferenceName());

		tf = new NStringFieldEditor(CsvExporterConfiguration.PROPERTY_FIELD_DELIMITER,
				Messages.CSVExporterPreferencePage_4, 4, getFieldEditorParent());
		tf.setEmptyStringAllowed(false);
		tf.setTextLimit(10);
		addField(tf);
		HelpSystem.setHelp(tf.getTextControl(getFieldEditorParent()),
				StudioPreferencePage.REFERENCE_PREFIX + tf.getPreferenceName());

		Browser lbl = BrowserUtils.getSWTBrowserWidget(getFieldEditorParent(), SWT.MULTI);
		lbl.setText(Messages.CSVExporterPreferencePage_5);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		lbl.setLayoutData(gd);
		
		//Eventually create the extensions for the page
		super.createFieldEditors();

	}

	public static void getDefaults(IPreferenceStore store) {
		store.setDefault(CsvExporterConfiguration.PROPERTY_RECORD_DELIMITER,
				PropertiesHelper.DPROP.getProperty(CsvExporterConfiguration.PROPERTY_RECORD_DELIMITER));
		store.setDefault(CsvExporterConfiguration.PROPERTY_FIELD_DELIMITER,
				PropertiesHelper.DPROP.getProperty(CsvExporterConfiguration.PROPERTY_FIELD_DELIMITER));
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
		return PAGE_ID; //$NON-NLS-1$
	}

}
