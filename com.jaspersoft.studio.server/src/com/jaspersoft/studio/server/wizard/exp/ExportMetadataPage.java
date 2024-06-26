/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.wizard.exp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.SystemUtils;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationUpdater;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.server.ContextHelpIDs;
import com.jaspersoft.studio.wizards.JSSHelpWizardPage;

import net.sf.jasperreports.eclipse.ui.validator.EmptyStringValidator;
import net.sf.jasperreports.eclipse.util.Misc;

public class ExportMetadataPage extends JSSHelpWizardPage {

	private Text tfile;

	private DataBindingContext bindingContext;
	private ExportOptions value = new ExportOptions();

	private Button bIncRepPerm;
	private Button bIncRepJobs;
	private Button bIncAccEvt;
	private Button bIncAudEvt;
	private Button bIncMonEvt;

	protected ExportMetadataPage() {
		super("exportmetadata"); //$NON-NLS-1$
		setTitle(Messages.ExportMetadataPage_0);
		setDescription(Messages.ExportMetadataPage_1);
		bindingContext = new DataBindingContext();
		try {
			value.setFile(SystemUtils.getUserDir().getCanonicalPath() + File.separator + "export.zip"); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout(2, false));
		setControl(cmp);

		Label lbl = new Label(cmp, SWT.NONE);
		lbl.setText(Messages.ExportMetadataPage_3);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		lbl.setLayoutData(gd);

		tfile = new Text(cmp, SWT.BORDER | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		tfile.setLayoutData(gd);

		Button bfile = new Button(cmp, SWT.PUSH);
		bfile.setText(Messages.common_browse);
		bfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				fd.setFileName("export.zip"); //$NON-NLS-1$
				if (Misc.isNullOrEmpty(tfile.getText()))
					fd.setFilterPath(System.getProperty("user.home"));
				else
					fd.setFilterPath(new File(tfile.getText()).getParent());
				fd.setFilterExtensions(new String[] { "*.zip", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				String selection = fd.open();
				if (selection != null)
					tfile.setText(Misc.nvl(selection));
			}
		});

		bIncRepPerm = new Button(cmp, SWT.CHECK);
		bIncRepPerm.setText(Messages.ExportMetadataPage_5);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		bIncRepPerm.setLayoutData(gd);
		bIncRepPerm.setSelection(true);

		bIncRepJobs = new Button(cmp, SWT.CHECK);
		bIncRepJobs.setText(Messages.ExportMetadataPage_6);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		bIncRepJobs.setLayoutData(gd);
		bIncRepJobs.setSelection(true);

		bIncAccEvt = new Button(cmp, SWT.CHECK);
		bIncAccEvt.setText(Messages.ExportMetadataPage_7);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		bIncAccEvt.setLayoutData(gd);
		bIncAccEvt.setSelection(true);

		bIncAudEvt = new Button(cmp, SWT.CHECK);
		bIncAudEvt.setText(Messages.ExportMetadataPage_8);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		bIncAudEvt.setLayoutData(gd);
		bIncAudEvt.setSelection(true);

		bIncMonEvt = new Button(cmp, SWT.CHECK);
		bIncMonEvt.setText(Messages.ExportMetadataPage_9);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		bIncMonEvt.setLayoutData(gd);
		bIncMonEvt.setSelection(true);

		Binding binding = bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(tfile),
				PojoProperties.value("file").observe(value), //$NON-NLS-1$
				new UpdateValueStrategy().setAfterConvertValidator(new EmptyStringValidator()), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT, null, new ControlDecorationUpdater());
		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(bIncRepPerm),
				PojoProperties.value("incRepositoryPermission").observe(value)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(bIncRepJobs),
				PojoProperties.value("incReportJobs").observe(value)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(bIncAccEvt),
				PojoProperties.value("includeAccessEvents").observe(value)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(bIncAudEvt),
				PojoProperties.value("includeAuditEvents").observe(value)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(bIncMonEvt),
				PojoProperties.value("includeMonitoringEvents").observe(value)); //$NON-NLS-1$
	}

	public ExportOptions getValue() {
		return value;
	}

	@Override
	protected String getContextName() {
		return ContextHelpIDs.WIZARD_SERVER_EXPORTMETADATA_PAGE;
	}
}
