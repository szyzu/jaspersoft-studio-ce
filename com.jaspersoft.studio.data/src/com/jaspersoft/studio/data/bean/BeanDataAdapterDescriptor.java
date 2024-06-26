/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.bean;

import net.sf.jasperreports.data.bean.BeanDataAdapter;
import net.sf.jasperreports.data.bean.BeanDataAdapterImpl;
import net.sf.jasperreports.engine.JRConstants;

import org.eclipse.swt.graphics.Image;

import com.jaspersoft.studio.data.Activator;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterEditor;

public class BeanDataAdapterDescriptor extends DataAdapterDescriptor {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	@Override
	public BeanDataAdapter getDataAdapter() {
		if (dataAdapter == null) {
			dataAdapter = new BeanDataAdapterImpl();
			((BeanDataAdapter) dataAdapter).setFactoryClass("com.jaspersoft.studio.data.sample.SampleJRDataSourceFactory");
			((BeanDataAdapter) dataAdapter).setMethodName("createBeanCollection");
		}
		return (BeanDataAdapter) dataAdapter;
	}

	@Override
	public DataAdapterEditor getEditor() {
		return new BeanDataAdapterEditor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.DataAdapterFactory#getIcon(int)
	 */
	@Override
	public Image getIcon(int size) {
		if (size == 16) {
			return Activator.getDefault().getImage("icons/beans.png");
		}
		return null;
	}
}
