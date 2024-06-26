/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.wizard.fragments.data.series;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.charts.JRXySeries;
import net.sf.jasperreports.charts.design.JRDesignXySeries;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;

import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;

public class XySerie implements ISeriesFactory<JRXySeries> {

	public JRDesignXySeries createSerie() {
		return createSerie(new JRDesignExpression("\"SERIES 1\""), null);
	}

	@Override
	public JRDesignXySeries createSerie(JRDesignExpression expr, JRXySeries prev) {
		JRDesignXySeries f = new JRDesignXySeries();
		f.setAutoSort(true);
		f.setSeriesExpression(expr);
		if (prev == null) {
			f.setXValueExpression(new JRDesignExpression("0"));
			f.setYValueExpression(new JRDesignExpression("0"));
		} else {
			f.setXValueExpression(ExprUtil.clone(prev.getXValueExpression()));
			f.setYValueExpression(ExprUtil.clone(prev.getYValueExpression()));
			f.setLabelExpression(ExprUtil.clone(prev.getLabelExpression()));
		}
		return f;
	}

	public String getColumnText(Object element, int columnIndex) {
		JRXySeries dcs = (JRXySeries) element;
		switch (columnIndex) {
		case 0:
			if (dcs.getSeriesExpression() != null && dcs.getSeriesExpression().getText() != null)
				return dcs.getSeriesExpression().getText();
		}
		return ""; //$NON-NLS-1$
	}

	public Object getValue(JRXySeries element, String property) {
		JRXySeries prop = (JRXySeries) element;
		if ("NAME".equals(property)) { //$NON-NLS-1$
			return prop.getSeriesExpression();
		}
		return ""; //$NON-NLS-1$
	}

	public void modify(JRXySeries element, String property, Object value) {
		JRDesignXySeries data = (JRDesignXySeries) element;
		if ("NAME".equals(property) && value instanceof JRExpression) //$NON-NLS-1$
			data.setSeriesExpression((JRExpression) value);
	}

	private List<JRXySeries> vlist;

	public List<JRXySeries> getList() {
		return vlist;
	}

	public void setList(List<JRXySeries> vlist) {
		this.vlist = new ArrayList<JRXySeries>(vlist);
	}
}
