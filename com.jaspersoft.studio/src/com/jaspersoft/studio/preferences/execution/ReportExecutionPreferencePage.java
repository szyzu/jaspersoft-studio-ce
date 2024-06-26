/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.execution;

import java.util.Locale;
import java.util.TimeZone;

import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.fill.JRFiller;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.help.HelpSystem;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.StudioPreferencePage;
import com.jaspersoft.studio.preferences.editor.JSSComboFieldEditor;
import com.jaspersoft.studio.preferences.editor.LocaleFieldEditor;
import com.jaspersoft.studio.preferences.editor.TimeZoneFieldEditor;
import com.jaspersoft.studio.preferences.editor.number.SpinnerFieldEditor;
import com.jaspersoft.studio.preferences.util.FieldEditorOverlayPage;

public class ReportExecutionPreferencePage extends FieldEditorOverlayPage {
	public static final String JSS_VIRTUALIZER_USE = "com.jaspersoft.studio.virtualizer.use"; //$NON-NLS-1$
	public static final String JSS_VIRTUALIZER_TYPE = "com.jaspersoft.studio.virtualizer.type"; //$NON-NLS-1$
	public static final String JSS_VIRTUALIZER_MAX_SIZE = "com.jaspersoft.studio.virtualizer.maxsize"; //$NON-NLS-1$
	public static final String JSS_VIRTUALIZER_TMP = "com.jaspersoft.studio.virtualizer.tmp"; //$NON-NLS-1$
	public static final String JSS_VIRTUALIZER_BLOCK_SIZE = "com.jaspersoft.studio.virtualizer.block.size"; //$NON-NLS-1$
	public static final String JSS_VIRTUALIZER_MIN_GROW_COUNT = "com.jaspersoft.studio.virtualizer.min.grow.count"; //$NON-NLS-1$

	public static final String JSS_VIRTUALIZER_PAGE_ELEMENT_SIZE = "net.sf.jasperreports.virtual.page.element.size"; //$NON-NLS-1$

	public static final String JSS_EXECPREFIX = "com.jaspersoft.studio.execute."; //$NON-NLS-1$
	public static final String JSS_LIMIT_RECORDS = JSS_EXECPREFIX + "limitrecords"; //$NON-NLS-1$
	public static final String JSS_MAX_RECORDS = JSS_EXECPREFIX + JRDesignParameter.REPORT_MAX_COUNT; // $NON-NLS-1$

	public static final String JSS_IGNOREPAGINATION = JSS_EXECPREFIX + JRDesignParameter.IS_IGNORE_PAGINATION; // $NON-NLS-1$

	public static final String JSS_REPORT_LOCALE = JRFiller.PROPERTY_DEFAULT_LOCALE; // $NON-NLS-1$
	public static final String JSS_REPORT_TIMEZONE = JRFiller.PROPERTY_DEFAULT_TIMEZONE; // $NON-NLS-1$
	public static final String JSS_REPORT_FORCE_PARAMETER_TIMEZONE = "com.jaspersoft.studio.parameters.usetimezone"; //$NON-NLS-1$

	public static final String JSS_RUNREPORTONDACHANGE = "com.jaspersoft.studio.run.report.on.da.change"; //$NON-NLS-1$

	private BooleanFieldEditor bfeONEXIT;
	private JSSComboFieldEditor cfeType;
	private SpinnerFieldEditor msfe;
	private DirectoryFieldEditor dfeTMP;
	private BooleanFieldEditor bfeUSE;
	private SpinnerFieldEditor sfeBLOCKSIZE;
	private SpinnerFieldEditor sfeMINGROWCOUNT;
	private SpinnerFieldEditor sfePAGEELSIZE;

	public ReportExecutionPreferencePage() {
		super(GRID);
		setPreferenceStore(JaspersoftStudioPlugin.getInstance().getPreferenceStore());
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		BooleanFieldEditor brunOnDaChange = new BooleanFieldEditor(JSS_RUNREPORTONDACHANGE,
				"Run report on Data Adapter change", getFieldEditorParent());
		brunOnDaChange.getDescriptionControl(getFieldEditorParent())
				.setToolTipText("Run report on Data Adapter change");
		addField(brunOnDaChange);

		BooleanFieldEditor timezoneCheckBox = new BooleanFieldEditor(JSS_REPORT_FORCE_PARAMETER_TIMEZONE,
				"Calculate date ranges for input parameters using system time zone", getFieldEditorParent());
		timezoneCheckBox.getDescriptionControl(getFieldEditorParent()).setToolTipText(
				"Calculates date ranges for input parameters using the system time zone. Set to false to use the time zone set in the�report�(REPORT_TIME_ZONE).");
		addField(timezoneCheckBox);

		addField(new LocaleFieldEditor(JSS_REPORT_LOCALE, Messages.ReportExecutionPreferencePage_localeLabel,
				getFieldEditorParent()));
		addField(new TimeZoneFieldEditor(JSS_REPORT_TIMEZONE, Messages.ReportExecutionPreferencePage_timeZoneLabel,
				getFieldEditorParent()));

		bLimRec = new BooleanFieldEditor(JSS_LIMIT_RECORDS, Messages.ReportExecutionPreferencePage_limitNumberLabel,
				getFieldEditorParent());
		addField(bLimRec);
		mnumrec = new SpinnerFieldEditor(JSS_MAX_RECORDS, Messages.ReportExecutionPreferencePage_maxNumberLabel,
				getFieldEditorParent(), 0);
		mnumrec.setMinimum(-1);
		mnumrec.setMaximum(Integer.MAX_VALUE);
		mnumrec.getLabelControl(getFieldEditorParent())
				.setToolTipText(Messages.ReportExecutionPreferencePage_maxNumberTooltip);
		addField(mnumrec);

		addField(new BooleanFieldEditor(JSS_IGNOREPAGINATION,
				Messages.ReportExecutionPreferencePage_ignorePaginationLabel, getFieldEditorParent()));

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(gd);

		bfeUSE = new BooleanFieldEditor(JSS_VIRTUALIZER_USE, Messages.ReportExecutionPreferencePage_useVritualizerLabel,
				getFieldEditorParent());
		addField(bfeUSE);

		bfeONEXIT = new BooleanFieldEditor(JRFileVirtualizer.PROPERTY_TEMP_FILES_SET_DELETE_ON_EXIT,
				Messages.ReportExecutionPreferencePage_deleteTempLabel, getFieldEditorParent());
		addField(bfeONEXIT);

		sfePAGEELSIZE = new SpinnerFieldEditor(JSS_VIRTUALIZER_PAGE_ELEMENT_SIZE,
				Messages.ReportExecutionPreferencePage_pageElementSizeLabel, getFieldEditorParent(), 0);
		sfePAGEELSIZE.setMinimum(1);
		sfePAGEELSIZE.setMaximum(Integer.MAX_VALUE);
		addField(sfePAGEELSIZE);
		HelpSystem.setHelp(sfePAGEELSIZE.getSpinnerControl(),
				StudioPreferencePage.REFERENCE_PREFIX + sfePAGEELSIZE.getPreferenceName());

		cfeType = new JSSComboFieldEditor(JSS_VIRTUALIZER_TYPE, Messages.ReportExecutionPreferencePage_typeLabel,
				new String[][] {
						{ Messages.ReportExecutionPreferencePage_fileVritualizerEntry,
								JRFileVirtualizer.class.getName() },
						{ Messages.ReportExecutionPreferencePage_gzipMemoryVirtualizer,
								JRGzipVirtualizer.class.getName() },
						{ Messages.ReportExecutionPreferencePage_singleSwapFileVirtualizer,
								JRSwapFileVirtualizer.class.getName() } },
				getFieldEditorParent());
		addField(cfeType);

		msfe = new SpinnerFieldEditor(JSS_VIRTUALIZER_MAX_SIZE, Messages.ReportExecutionPreferencePage_maxSizeLabel,
				getFieldEditorParent(), 0);
		msfe.setMinimum(0);
		msfe.setMaximum(Integer.MAX_VALUE);
		msfe.getLabelControl(getFieldEditorParent())
				.setToolTipText(Messages.ReportExecutionPreferencePage_maximumSizeTooltip);
		addField(msfe);

		dfeTMP = new DirectoryFieldEditor(JSS_VIRTUALIZER_TMP, Messages.ReportExecutionPreferencePage_tempPathLabel,
				getFieldEditorParent());
		addField(dfeTMP);

		sfeBLOCKSIZE = new SpinnerFieldEditor(JSS_VIRTUALIZER_BLOCK_SIZE,
				Messages.ReportExecutionPreferencePage_blockSizeLabel, getFieldEditorParent(), 0);
		sfeBLOCKSIZE.setMinimum(0);
		sfeBLOCKSIZE.setMaximum(Integer.MAX_VALUE);
		addField(sfeBLOCKSIZE);

		sfeMINGROWCOUNT = new SpinnerFieldEditor(JSS_VIRTUALIZER_MIN_GROW_COUNT,
				Messages.ReportExecutionPreferencePage_minGrowLabel, getFieldEditorParent(), 0);
		sfeMINGROWCOUNT.setMinimum(0);
		sfeMINGROWCOUNT.setMaximum(Integer.MAX_VALUE);
		addField(sfeMINGROWCOUNT);

		vtype = getVirtualizerType(getPreferenceStore().getString(JSS_VIRTUALIZER_TYPE));

		enableVirtualizers(getPreferenceStore().getBoolean(JSS_VIRTUALIZER_USE));
		enableLimitRecords(getPreferenceStore().getBoolean(JSS_LIMIT_RECORDS));

		enableSwapVirtualizer(vtype.equals(VirtualizerType.SWAP));

		// Eventually create the extensions for the page
		super.createFieldEditors();
	}

	private VirtualizerType getVirtualizerType(String type) {
		if (type.equals(JRFileVirtualizer.class.getName()))
			return VirtualizerType.FILE;
		if (type.equals(JRSwapFileVirtualizer.class.getName()))
			return VirtualizerType.SWAP;
		return VirtualizerType.ZIP;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getProperty().equals("field_editor_value")) { //$NON-NLS-1$
			if (event.getSource() == bfeUSE)
				enableVirtualizers((Boolean) event.getNewValue());
			else if (event.getSource() == cfeType) {
				vtype = getVirtualizerType((String) event.getNewValue());
				enableSwapVirtualizer(vtype.equals(VirtualizerType.SWAP));
			} else if (event.getSource() == mnumrec)
				enableLimitRecords((Integer) event.getNewValue() >= 0);
			else if (event.getSource() == bLimRec)
				enableLimitRecords((Boolean) event.getNewValue());
		}
	}

	enum VirtualizerType {
		SWAP, FILE, ZIP;
	}

	private VirtualizerType vtype = VirtualizerType.FILE;
	private BooleanFieldEditor bLimRec;
	private SpinnerFieldEditor mnumrec;

	private void enableLimitRecords(boolean newVal) {
		mnumrec.setEnabled(newVal, getFieldEditorParent());
	}

	private void enableSwapVirtualizer(boolean isSwap) {
		bfeONEXIT.setEnabled(vtype.equals(VirtualizerType.FILE), getFieldEditorParent());
		dfeTMP.setEnabled(isSwap || vtype.equals(VirtualizerType.FILE), getFieldEditorParent());
		sfeBLOCKSIZE.setEnabled(isSwap, getFieldEditorParent());
		sfeMINGROWCOUNT.setEnabled(isSwap, getFieldEditorParent());
	}

	private void enableVirtualizers(boolean newVal) {
		bfeONEXIT.setEnabled(newVal && vtype.equals(VirtualizerType.FILE), getFieldEditorParent());
		cfeType.setEnabled(newVal, getFieldEditorParent());
		msfe.setEnabled(newVal, getFieldEditorParent());
		enableSwapVirtualizer(newVal && vtype.equals(VirtualizerType.SWAP));
		sfePAGEELSIZE.setEnabled(newVal, getFieldEditorParent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	public static void getDefaults(IPreferenceStore store) {
		store.setDefault(JRFileVirtualizer.PROPERTY_TEMP_FILES_SET_DELETE_ON_EXIT, "true"); //$NON-NLS-1$
		store.setDefault(JSS_VIRTUALIZER_USE, "false"); //$NON-NLS-1$
		store.setDefault(JSS_VIRTUALIZER_TYPE, JRFileVirtualizer.class.getName());
		store.setDefault(JSS_VIRTUALIZER_MAX_SIZE, 100);
		store.setDefault(JSS_VIRTUALIZER_TMP, ""); //$NON-NLS-1$
		store.setDefault(JSS_VIRTUALIZER_BLOCK_SIZE, 100);
		store.setDefault(JSS_VIRTUALIZER_MIN_GROW_COUNT, 100);
		store.setDefault(JSS_VIRTUALIZER_PAGE_ELEMENT_SIZE, 1);

		store.setDefault(JRFileVirtualizer.PROPERTY_TEMP_FILES_SET_DELETE_ON_EXIT, "false"); //$NON-NLS-1$

		store.setDefault(JSS_LIMIT_RECORDS, "false");//$NON-NLS-1$
		store.setDefault(JSS_MAX_RECORDS, -1);// $NON-NLS-1$
		store.setDefault(JSS_IGNOREPAGINATION, "false");//$NON-NLS-1$
		store.setDefault(JSS_REPORT_LOCALE, Locale.getDefault().toString());// $NON-NLS-1$
		store.setDefault(JSS_REPORT_TIMEZONE, TimeZone.getDefault().getID());// $NON-NLS-1$
		store.setDefault(JSS_REPORT_FORCE_PARAMETER_TIMEZONE, false);
		store.setDefault(JSS_RUNREPORTONDACHANGE, "true");
	}

	@Override
	public String getPageId() {
		return "com.jaspersoft.studio.preferences.execution.ReportExecutionPreferencePage.property"; //$NON-NLS-1$
	}

}
