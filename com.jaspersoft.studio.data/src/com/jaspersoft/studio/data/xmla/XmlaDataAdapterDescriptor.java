/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.xmla;

import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.jaspersoft.studio.data.Activator;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterEditor;
import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.xmla.XmlaDataAdapter;
import net.sf.jasperreports.data.xmla.XmlaDataAdapterImpl;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.olap.JRMdxQueryExecuterFactory;

public class XmlaDataAdapterDescriptor extends DataAdapterDescriptor implements IFieldsProvider {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected XmlaFieldsProvider fprovider;

	@Override
	public XmlaDataAdapter getDataAdapter() {
		if (dataAdapter == null)
			dataAdapter = new XmlaDataAdapterImpl();
		return (XmlaDataAdapter) dataAdapter;
	}

	@Override
	public DataAdapterEditor getEditor() {
		return new XmlaDataAdapterEditor();
	}

	public void getFieldProvider() {
		if (fprovider == null)
			fprovider = new XmlaFieldsProvider();
	}

	public boolean supportsGetFieldsOperation(JasperReportsConfiguration jConfig, JRDataset jDataset) {
		getFieldProvider();
		return fprovider.supportsGetFieldsOperation(jConfig, jDataset);
	}

	@Override
	public List<JRDesignField> getFields(DataAdapterService con, JasperReportsConfiguration jConfig, JRDataset jDataset)
			throws JRException, UnsupportedOperationException {
		getFieldProvider();
		return fprovider.getFields(con, jConfig, jDataset);
	}

	/**
	 * Return an Image. By default this method returns a simple database icon
	 */
	public Image getIcon(int size) {
		if (size == 16) {
			return Activator.getDefault().getImage("icons/database.png"); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public String[] getLanguages() {
		return new String[] { JRMdxQueryExecuterFactory.CANONICAL_LANGUAGE };
	}
}
