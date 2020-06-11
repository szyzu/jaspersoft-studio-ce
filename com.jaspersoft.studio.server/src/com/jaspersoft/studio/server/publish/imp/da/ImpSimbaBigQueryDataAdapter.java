/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package com.jaspersoft.studio.server.publish.imp.da;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

import net.sf.jasperreports.data.jdbc.GbqSimbaDataAdapterService;

public class ImpSimbaBigQueryDataAdapter extends AImpJdbcDataAdapter {
	public static final String DNAME = "com.simba.googlebigquery.jdbc41.Driver";

	public ImpSimbaBigQueryDataAdapter() {
		super(DNAME, new String[][] { { GbqSimbaDataAdapterService.GBQ_CONNECTION_PARAMETER_PRIVATE_KEY,
				ResourceDescriptor.TYPE_SECURE_FILE } });
	}

	@Override
	protected String getJdbcPrefix() {
		return "jdbc:bigquery:";
	}

}