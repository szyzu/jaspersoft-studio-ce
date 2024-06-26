/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.jrdsprovider;

import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.jaspersoft.studio.data.ADataAdapterComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.messages.Messages;
import com.jaspersoft.studio.swt.widgets.ClassType;
import com.jaspersoft.studio.swt.widgets.ClasspathComponent;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.provider.DataSourceProviderDataAdapter;
import net.sf.jasperreports.engine.JasperReportsContext;

public class JrdsProviderDataAdapterComposite extends ADataAdapterComposite {

	private ClassType factoryText;
	private ClasspathComponent cpath;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public JrdsProviderDataAdapterComposite(Composite parent, int style, JasperReportsContext jrContext) {
		super(parent, style, jrContext);
		setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText(Messages.JrdsProviderDataAdapterComposite_0);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		lblNewLabel.setLayoutData(gd);

		factoryText = new ClassType(this,
				Messages.JrdsProviderDataAdapterComposite_1);
		factoryText
				.setClassType("com.jaspersoft.ireport.examples.SampleJRDataSourceFactory"); //$NON-NLS-1$

		cpath = new ClasspathComponent(this);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		cpath.getControl().setLayoutData(gd);
	}

	@Override
	protected void bindWidgets(DataAdapter dataAdapter) {
		DataSourceProviderDataAdapter da = (DataSourceProviderDataAdapter) dataAdapter;

		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(factoryText.getControl()),
				PojoProperties.value("providerClass").observe(dataAdapter)); //$NON-NLS-1$ 

		cpath.setClasspaths(da.getClasspath());
	}

	public DataAdapterDescriptor getDataAdapter() {
		if (dataAdapterDesc == null)
			dataAdapterDesc = new JrdsProviderDataAdapterDescriptor();

		DataSourceProviderDataAdapter da = (DataSourceProviderDataAdapter) dataAdapterDesc
				.getDataAdapter();
		da.setProviderClass(factoryText.getClassType().trim());
		da.setClasspath(cpath.getClasspaths());

		return dataAdapterDesc;
	}

	@Override
	public String getHelpContextId() {
		return PREFIX.concat("adapter_jrdatasourceprovider");
	}
}
