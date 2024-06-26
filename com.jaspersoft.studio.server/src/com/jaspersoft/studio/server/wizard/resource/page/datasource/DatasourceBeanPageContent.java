/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.wizard.resource.page.datasource;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.wizard.resource.APageContent;
import com.jaspersoft.studio.utils.UIUtil;

import net.sf.jasperreports.data.bean.BeanDataAdapter;

public class DatasourceBeanPageContent extends APageContent {

	private Text tmethod;
	private Text tname;

	public DatasourceBeanPageContent(ANode parent, AMResource resource, DataBindingContext bindingContext) {
		super(parent, resource, bindingContext);
	}

	public DatasourceBeanPageContent(ANode parent, AMResource resource) {
		super(parent, resource);
	}

	@Override
	public String getPageName() {
		return "com.jaspersoft.studio.server.page.datasource.bean";
	}

	@Override
	public String getName() {
		return Messages.RDDatasourceBeanPage_DatasourceTabItem;
	}

	public Control createContent(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		UIUtil.createLabel(composite, Messages.RDDatasourceBeanPage_BeanName);

		tname = new Text(composite, SWT.BORDER);
		tname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtil.createLabel(composite, Messages.RDDatasourceBeanPage_BeanMethod);

		tmethod = new Text(composite, SWT.BORDER);
		tmethod.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button importDA = new Button(composite, SWT.NONE);
		importDA.setText(Messages.RDDatasourceBeanPage_ImportButton);
		importDA.setToolTipText(Messages.RDDatasourceBeanPage_ImportButtonTooltip);
		importDA.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 2, 1));
		importDA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ImportDataSourceInfoFromDA<BeanDataAdapter> dialog = new ImportDataSourceInfoFromDA<BeanDataAdapter>(Display.getDefault().getActiveShell(), "Bean", BeanDataAdapter.class); //$NON-NLS-1$
				if (dialog.open() == Window.OK) {
					// get information from the selected DA
					BeanDataAdapter da = dialog.getSelectedDataAdapter();
					if (da != null) {
						tname.setText(da.getFactoryClass());
						tmethod.setText(da.getMethodName());
					} else {
						tname.setText(""); //$NON-NLS-1$
						tmethod.setText(""); //$NON-NLS-1$
					}
				}
			}
		});

		rebind();
		return composite;
	}

	@Override
	protected void rebind() {
		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(tname),
				PojoProperties.value("beanName").observe(res.getValue())); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(tmethod),
				PojoProperties.value("beanMethod").observe(res.getValue())); //$NON-NLS-1$				
	}

	@Override
	public String getHelpContext() {
		return "com.jaspersoft.studio.doc.adapter_javabeans";
	}
}
