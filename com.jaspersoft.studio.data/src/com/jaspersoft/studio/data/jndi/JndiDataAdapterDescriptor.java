/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.jndi;

import java.util.List;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.jndi.JndiDataAdapter;
import net.sf.jasperreports.data.jndi.JndiDataAdapterImpl;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.data.AWizardDataEditorComposite;
import com.jaspersoft.studio.data.Activator;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.IWizardDataEditorProvider;
import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.data.jdbc.JDBCFieldsProvider;
import com.jaspersoft.studio.data.ui.WizardQueryEditorComposite;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class JndiDataAdapterDescriptor extends DataAdapterDescriptor
		implements IFieldsProvider, IWizardDataEditorProvider {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	@Override
	public JndiDataAdapter getDataAdapter() {
		if (dataAdapter == null)
			dataAdapter = new JndiDataAdapterImpl();
		return (JndiDataAdapter) dataAdapter;
	}

	@Override
	public JndiDataAdapterEditor getEditor() {
		return new JndiDataAdapterEditor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.DataAdapterFactory#getIcon(int)
	 */
	@Override
	public Image getIcon(int size) {
		if (size == 16) {
			return Activator.getDefault().getImage("icons/datasource_jndi.png");
		}
		return null;
	}

	private IFieldsProvider fprovider;

	public List<JRDesignField> getFields(DataAdapterService con, JasperReportsConfiguration jConfig, JRDataset jDataset)
			throws JRException, UnsupportedOperationException {
		getFieldProvider();
		return fprovider.getFields(con, jConfig, jDataset);
	}

	private void getFieldProvider() {
		if (fprovider == null)
			fprovider = new JDBCFieldsProvider();
	}

	public boolean supportsGetFieldsOperation(JasperReportsConfiguration jConfig, JRDataset jDataset) {
		getFieldProvider();
		return fprovider.supportsGetFieldsOperation(jConfig, jDataset);
	}

	@Override
	public AWizardDataEditorComposite createDataEditorComposite(Composite parent, WizardPage page) {
		return new WizardQueryEditorComposite(parent, page, this, JRJdbcQueryExecuter.CANONICAL_LANGUAGE);
	}

	@Override
	public String[] getLanguages() {
		return new String[] { JRJdbcQueryExecuter.CANONICAL_LANGUAGE };
	}
}
