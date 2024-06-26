/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.engine.JasperReportsContext;

import org.eclipse.swt.graphics.Image;

import com.jaspersoft.studio.data.adapter.IDataAdapterCreator;

/*
 * 
 * @author gtoffoli
 */
public interface DataAdapterFactory {

	/**
	 * Creates a new instance of IReportConnection
	 * 
	 * @return an instance of IReportConnection
	 */
	public DataAdapterDescriptor createDataAdapter();

	/**
	 * This method returns the class name of the DataAdapter implementation. This is used from the code that must check if
	 * this connection factory is the good one to instance the connection serialized with a specific class name. Since due
	 * to the ClassLoading limitation JSS may not be able to instance the class by its self, it looks for the appropriate
	 * registered DataAdapterFactory
	 * 
	 * @return the class name of the DataAdapter implementation created by this factory class
	 */
	public String getDataAdapterClassName();

	public DataAdapterService createDataAdapterService(JasperReportsContext jasperReportsContext, DataAdapter dataAdapter);

	/**
	 * This method provides the label of the data adapter type. I.e.: JDBC connection
	 */
	public String getLabel();

	/**
	 * This method provides a short description of the data adapter type. I.e.: connection to a database using JDBC
	 */
	public String getDescription();

	/**
	 * This method provides an icon for this data adapter. The icon size can be 32 or 48.
	 * 
	 */
	public Image getIcon(int size);

	/**
	 * Return a converter that can be used to build a JSS data adapter from an iReport data adapter definition
	 */
	public IDataAdapterCreator iReportConverter();

	/**
	 * Verifies if the current data adapter factory is deprecated.
	 */
	public boolean isDeprecated();
}
