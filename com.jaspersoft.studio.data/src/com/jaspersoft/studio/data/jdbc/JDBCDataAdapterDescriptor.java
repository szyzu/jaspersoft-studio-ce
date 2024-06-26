/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.jdbc;

import java.util.List;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.jdbc.JdbcDataAdapterImpl;
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
import com.jaspersoft.studio.data.DataAdapterEditor;
import com.jaspersoft.studio.data.IWizardDataEditorProvider;
import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.data.ui.WizardQueryEditorComposite;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

/*
 * @author gtoffoli
 *
 */
public class JDBCDataAdapterDescriptor extends DataAdapterDescriptor
		implements IFieldsProvider, IWizardDataEditorProvider {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	@Override
	public DataAdapter getDataAdapter() {
		if (dataAdapter == null)
			dataAdapter = new JdbcDataAdapterImpl();
		return dataAdapter;
	}

	@Override
	public DataAdapterEditor getEditor() {

		return new JDBCDataAdapterEditor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.DataAdapterFactory#getIcon(int)
	 */
	@Override
	public Image getIcon(int size) {
		if (size == 16) {
			return Activator.getDefault().getImage("icons/database.png");
		}
		return null;
	}

	protected IFieldsProvider fprovider;

	public List<JRDesignField> getFields(DataAdapterService con, JasperReportsConfiguration jConfig, JRDataset jDataset)
			throws JRException, UnsupportedOperationException {
		getFieldProvider();
		return fprovider.getFields(con, jConfig, jDataset);
	}

	public void getFieldProvider() {
		if (fprovider == null)
			fprovider = new JDBCFieldsProvider();
	}

	public boolean supportsGetFieldsOperation(JasperReportsConfiguration jConfig, JRDataset jDataset) {
		getFieldProvider();
		return fprovider.supportsGetFieldsOperation(jConfig, jDataset);
	}

	/**
	 * 
	 * @see com.jaspersoft.studio.data.IWizardDataEditorProvider#createDataEditorComposite(java.awt.Composite,
	 *      org.eclipse.jface.wizard.WizardPage)
	 * 
	 * @param Composite
	 *            parent - the parent composite
	 * @param WizardPage
	 *            page - the page used to show the composite, it can be used to
	 *            access the nested Wizard (probably JSSWizard)
	 * 
	 * @return an editor composite extending AWizardDataEditorComposite
	 */
	@Override
	public AWizardDataEditorComposite createDataEditorComposite(Composite parent, WizardPage page) {
		return new WizardQueryEditorComposite(parent, page, this, JRJdbcQueryExecuter.CANONICAL_LANGUAGE);
	}

	@Override
	public String[] getLanguages() {
		return new String[] { JRJdbcQueryExecuter.CANONICAL_LANGUAGE };
	}
}
