/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.xls;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.xls.XlsDataAdapterImpl;
import net.sf.jasperreports.engine.JasperReportsContext;

import org.eclipse.swt.graphics.Image;

import com.jaspersoft.studio.data.Activator;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterFactory;
import com.jaspersoft.studio.data.adapter.IDataAdapterCreator;
import com.jaspersoft.studio.data.messages.Messages;

public class XLSDataAdapterFactory implements DataAdapterFactory {

	public DataAdapterDescriptor createDataAdapter() {
		return new XLSDataAdapterDescriptor();
	}

	public String getDataAdapterClassName() {
		return XlsDataAdapterImpl.class.getName();
	}

	public String getLabel() {
		return Messages.ExcelDataAdapterFactory_label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.DataAdapterFactory#getDescription()
	 */
	public String getDescription() {

		return Messages.ExcelDataAdapterFactory_description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.DataAdapterFactory#getIcon(int)
	 */
	public Image getIcon(int size) {
		if (size == 16) {
			return Activator.getDefault().getImage("icons/document-excel.png"); //$NON-NLS-1$
		}
		return null;
	}

	public DataAdapterService createDataAdapterService(JasperReportsContext jasperReportsContext, DataAdapter dataAdapter) {
		return null;
	}

	@Override
	public IDataAdapterCreator iReportConverter() {
		return new XLSCreator();
	}

	@Override
	public boolean isDeprecated() {
		return true;
	}
}
