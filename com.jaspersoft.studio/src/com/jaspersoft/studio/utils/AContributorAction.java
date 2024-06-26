/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.utils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.jface.action.Action;

import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public abstract class AContributorAction extends Action {

	protected JasperReportsConfiguration jrConfig;

	public AContributorAction(String id, String text) {
		super();
		setId(id);
		setText(text);
	}

	public void setJrConfig(JasperReportsConfiguration jrConfig) {
		this.jrConfig = jrConfig;
	}

	protected JasperDesign getJasperDesignCopy() throws JRException {
		return ModelUtils.copyJasperDesign(jrConfig, jrConfig.getJasperDesign());
	}
}
