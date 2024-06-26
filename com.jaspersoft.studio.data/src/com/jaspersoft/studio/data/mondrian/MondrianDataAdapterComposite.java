/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.mondrian;

import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.jdbc.JDBCDataAdapterComposite;
import com.jaspersoft.studio.data.messages.Messages;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.mondrian.MondrianDataAdapter;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JasperReportsContext;

public class MondrianDataAdapterComposite extends JDBCDataAdapterComposite {
	private Text textCatalogURI;

	public MondrianDataAdapterComposite(Composite parent, int style, JasperReportsContext jrContext) {
		super(parent, style, jrContext);
	}

	@Override
	protected void createPreWidgets(Composite parent) {
		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout(3, false));
		cmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(cmp, SWT.NONE).setText(Messages.MondrianDataAdapterComposite_0);

		textCatalogURI = new Text(cmp, SWT.BORDER);
		textCatalogURI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button btnBrowse = new Button(cmp, SWT.NONE);
		btnBrowse.setText(Messages.MondrianDataAdapterComposite_1);

		btnBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				FileDialog fd = new FileDialog(UIUtils.getShell());
				fd.setFilterPath(root.getLocation().toOSString());
				fd.setFileName(textCatalogURI.getText());
				fd.setFilterExtensions(new String[] { "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				String selection = fd.open();
				if (selection != null)
					textCatalogURI.setText("file:" + selection);
			}
		});
	}

	@Override
	protected void bindWidgets(DataAdapter dataAdapter) {
		super.bindWidgets(dataAdapter);
		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(textCatalogURI),
				PojoProperties.value("catalogURI").observe(dataAdapter)); //$NON-NLS-1$
	}

	@Override
	public DataAdapterDescriptor getDataAdapter() {
		if (dataAdapterDesc == null)
			dataAdapterDesc = new MondrianDataAdapterDescriptor();
		super.getDataAdapter();

		MondrianDataAdapter mDataAdapter = (MondrianDataAdapter) dataAdapterDesc.getDataAdapter();
		mDataAdapter.setCatalogURI(textCatalogURI.getText());

		return dataAdapterDesc;
	}

}
