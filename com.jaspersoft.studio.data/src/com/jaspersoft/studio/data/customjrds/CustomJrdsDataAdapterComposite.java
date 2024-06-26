/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.customjrds;

import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.data.ADataAdapterComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.messages.Messages;
import com.jaspersoft.studio.swt.widgets.ClassType;
import com.jaspersoft.studio.swt.widgets.ClasspathComponent;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.ds.DataSourceDataAdapter;
import net.sf.jasperreports.engine.JasperReportsContext;

public class CustomJrdsDataAdapterComposite extends ADataAdapterComposite {

	private ClassType textFactoryClass;
	private Text textMethodToCall;
	private ClasspathComponent cpath;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CustomJrdsDataAdapterComposite(Composite parent, int style, JasperReportsContext jrContext) {

		/*
		 * UI ELEMENTS
		 */
		super(parent, style, jrContext);
		setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText(Messages.CustomJrdsDataAdapterComposite_0);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		lblNewLabel.setLayoutData(gd);

		textFactoryClass = new ClassType(this,
				Messages.CustomJrdsDataAdapterComposite_1);
		textFactoryClass
				.setClassType(Messages.CustomJrdsDataAdapterComposite_2);

		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setText(Messages.CustomJrdsDataAdapterComposite_3);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		lblNewLabel_1.setLayoutData(gd);

		textMethodToCall = new Text(this, SWT.BORDER);
		textMethodToCall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		textMethodToCall.setLayoutData(gd);

		cpath = new ClasspathComponent(this);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		cpath.getControl().setLayoutData(gd);
	}

	@Override
	protected void bindWidgets(DataAdapter dataAdapter) {
		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(textFactoryClass.getControl()),
				PojoProperties.value("factoryClass").observe(dataAdapter)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(textMethodToCall),
				PojoProperties.value("methodToCall").observe(dataAdapter)); //$NON-NLS-1$

		DataSourceDataAdapter dsDataAdapter = (DataSourceDataAdapter) dataAdapter;
		cpath.setClasspaths(dsDataAdapter.getClasspath());
	}

	public DataAdapterDescriptor getDataAdapter() {
		if (dataAdapterDesc == null) {
			dataAdapterDesc = new CustomJrdsDataAdapterDescriptor();
		}

		DataSourceDataAdapter dsDataAdapter = (DataSourceDataAdapter) dataAdapterDesc
				.getDataAdapter();

		dsDataAdapter.setFactoryClass(textFactoryClass.getClassType().trim());
		dsDataAdapter.setMethodToCall(textMethodToCall.getText().trim());

		dsDataAdapter.setClasspath(cpath.getClasspaths());

		return dataAdapterDesc;
	}
	
	@Override
	public String getHelpContextId() {
		return PREFIX.concat("adapter_custom");
	}
}
