/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;

import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterServiceUtil;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.ParameterContributor;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.ParameterContributorFactory;
import net.sf.jasperreports.repo.DataAdapterResource;
import net.sf.jasperreports.repo.RepositoryUtil;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: DataAdapterParameterContributorFactory.java 4734 2011-10-21
 * 12:13:21Z teodord $
 */
public final class DataAdapterParameterContributorFactory implements ParameterContributorFactory {

	public static final String PARAMETER_DATA_ADAPTER = "PARAMETER_DATA_ADAPTER";
	private static final DataAdapterParameterContributorFactory INSTANCE = new DataAdapterParameterContributorFactory();

	private DataAdapterParameterContributorFactory() {
	}

	/**
	 * 
	 */
	public static DataAdapterParameterContributorFactory getInstance() {
		return INSTANCE;
	}

	/**
	 *
	 */
	public List<ParameterContributor> getContributors(ParameterContributorContext context) throws JRException {
		List<ParameterContributor> contributors = new ArrayList<>();

		DataAdapter dataAdapter = null;
		Object param = context.getParameterValues().get(PARAMETER_DATA_ADAPTER);
		if (param instanceof DataAdapter)
			dataAdapter = (DataAdapter) param;
		if (dataAdapter == null) {
			String dataAdapterUri = JRPropertiesUtil.getInstance(context.getJasperReportsContext())
					.getProperty(context.getDataset(), "net.sf.jasperreports.data.adapter");
			if (dataAdapterUri != null) {
				if (dataAdapterUri.startsWith("../") || dataAdapterUri.startsWith("./")
						|| !dataAdapterUri.startsWith("/")) {
					Object obj = ((JasperReportsConfiguration) context.getJasperReportsContext())
							.get(FileUtils.KEY_FILE);
					if (obj instanceof IFile) {
						IFile f = (IFile) obj;
						String pref = "";
						if (dataAdapterUri.startsWith("../") && f.getParent() != null
								&& f.getParent().getParent() != null) {
							pref = f.getParent().getParent().getProjectRelativePath().toString();
							dataAdapterUri = pref + dataAdapterUri.substring(2);
						} else if (dataAdapterUri.startsWith("./") && f.getParent() != null) {
							pref = f.getParent().getProjectRelativePath().toString();
							dataAdapterUri = pref + dataAdapterUri.substring(1);
						} else if (!dataAdapterUri.startsWith("/")) {
							pref = f.getParent().getProjectRelativePath().toString();
							dataAdapterUri = pref + "/" + dataAdapterUri;
						}
					}
				}
				DataAdapterResource dataAdapterResource = RepositoryUtil.getInstance(context.getJasperReportsContext())
						.getResourceFromLocation(dataAdapterUri, DataAdapterResource.class);
				dataAdapter = dataAdapterResource.getDataAdapter();
			}
		}
		if (dataAdapter != null) {
			ParameterContributor dataAdapterService = DataAdapterServiceUtil.getInstance(context)
					.getService(dataAdapter);

			return Collections.singletonList(dataAdapterService);
		}
		return contributors;
	}
}
