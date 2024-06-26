/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.jaspersoft.studio.data.AWizardDataEditorComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.data.DataAdapterServiceUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;

/**
 * Empty editor composite that is supposed to be used by those
 * {@link DataAdapterDescriptor} that directly provide the facility to retrieve
 * fields (CVS, Excel, etc.).
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class EmptyWizardDataEditorComposite extends AWizardDataEditorComposite {

	private DataAdapterDescriptor dataAdapterDescriptor;
	private JRDesignDataset dataset;

	public EmptyWizardDataEditorComposite(Composite parent, WizardPage page,
			DataAdapterDescriptor dataAdapterDescriptor) {
		super(parent, page);
		this.dataAdapterDescriptor = dataAdapterDescriptor;
		this.setLayout(new FillLayout(SWT.HORIZONTAL));
		Label msg = new Label(this, SWT.NONE);
		msg.setText(Messages.EmptyWizardDataEditorComposite_TitleMsg);
	}

	@Override
	public String getQueryString() {
		// A query string does not make sense
		return null;
	}

	@Override
	public String getQueryLanguage() {
		// A query language does not make sense
		return null;
	}

	@Override
	public List<JRDesignField> readFields() throws Exception {
		if (getDataAdapterDescriptor() != null && getDataAdapterDescriptor() instanceof IFieldsProvider) {
			try {
				JasperReportsConfiguration jConf = getJasperReportsConfiguration();
				JRDesignDataset ds = getDataset();
				return ((IFieldsProvider) getDataAdapterDescriptor()).getFields(DataAdapterServiceUtil
						.getInstance(new ParameterContributorContext(jConf, ds, jConf.getJRParameters()))
						.getService(getDataAdapterDescriptor().getDataAdapter()), jConf, ds);

			} catch (JRException ex) {
				// Cleanup of the error. JRException are a very low meaningful
				// exception when working
				// with data, what the user is interested into is the underline
				// error (i.e. an SQL error).
				// That's why we rise the real cause, if any instead of rising
				// the highlevel exception...
				if (ex.getCause() != null && ex.getCause() instanceof Exception) {
					throw (Exception) ex.getCause();
				}
				throw ex;
			}
		}
		return new ArrayList<JRDesignField>();
	}

	/**
	 * Convenient way to crate a dataset object to be passed to the
	 * IFieldsProvider.getFields method
	 * 
	 * @return JRDesignDataset return a dataset with the proper query and
	 * language set...
	 */
	public JRDesignDataset getDataset() {
		if (dataset == null) {
			dataset = new JRDesignDataset(getJasperReportsConfiguration(), true);
		}
		return dataset;
	}

	/**
	 * @return the dataAdapterDescriptor
	 */
	public DataAdapterDescriptor getDataAdapterDescriptor() {
		return dataAdapterDescriptor;
	}

	@Override
	public List<JRDesignParameter> readParameters() throws Exception {
		return null;
	}

}
