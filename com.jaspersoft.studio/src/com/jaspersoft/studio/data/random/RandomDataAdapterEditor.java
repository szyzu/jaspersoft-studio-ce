/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.random;

import net.sf.jasperreports.engine.JasperReportsContext;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.data.ADataAdapterComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterEditor;

public class RandomDataAdapterEditor implements DataAdapterEditor {

	RandomDataAdapterComposite composite = null;

	public void setDataAdapter(DataAdapterDescriptor dataAdapter) {
		if (composite != null && dataAdapter instanceof RandomDataAdapterDescriptor) {
			this.composite.setDataAdapter((RandomDataAdapterDescriptor) dataAdapter);
		}
	}

	public DataAdapterDescriptor getDataAdapter() {
		return this.composite.getDataAdapter();
	}

	public ADataAdapterComposite getComposite(Composite parent, int style, WizardPage wizardPage,
			JasperReportsContext jrContext) {
		if (composite == null) {
			composite = new RandomDataAdapterComposite(parent, style, jrContext);
		}
		return composite;
	}

	public String getHelpContextId() {
		return composite.getHelpContextId();
	}
}
