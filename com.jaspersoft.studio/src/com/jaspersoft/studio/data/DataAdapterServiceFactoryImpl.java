/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterContributorFactory;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.DefaultDataAdapterServiceFactory;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ParameterContributorContext;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JRBaseBand.java 4319 2011-05-17 09:22:14Z teodord $
 */
public class DataAdapterServiceFactoryImpl implements DataAdapterContributorFactory {

	/**
	 *
	 */
	private static final DataAdapterServiceFactoryImpl INSTANCE = new DataAdapterServiceFactoryImpl();

	/**
	 *
	 */
	public static DataAdapterServiceFactoryImpl getInstance() {
		return INSTANCE;
	}

	@Override
	public DataAdapterService getDataAdapterService(ParameterContributorContext context, DataAdapter dataAdapter) {
		DataAdapterService dataAdapterService = null;

		DataAdapterFactory daf = DataAdapterManager.findFactoryByDataAdapterClass(dataAdapter.getClass().getName());
		JasperReportsContext jContext = context.getJasperReportsContext();
		if (daf != null)
			dataAdapterService = daf.createDataAdapterService(jContext, dataAdapter);
		if (daf == null)
			return DefaultDataAdapterServiceFactory.getInstance().getDataAdapterService(
					new ParameterContributorContext(jContext, null, null), dataAdapter);
		return dataAdapterService;
	}

}
