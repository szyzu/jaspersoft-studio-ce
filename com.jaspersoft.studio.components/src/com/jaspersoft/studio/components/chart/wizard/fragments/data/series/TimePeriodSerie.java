/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.wizard.fragments.data.series;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.charts.JRTimePeriodSeries;
import net.sf.jasperreports.charts.design.JRDesignTimePeriodSeries;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;

import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;

public class TimePeriodSerie implements ISeriesFactory<JRTimePeriodSeries> {

	public JRDesignTimePeriodSeries createSerie() {
		return createSerie(new JRDesignExpression("\"SERIES 1\""), null);
	}

	@Override
	public JRDesignTimePeriodSeries createSerie(JRDesignExpression expr, JRTimePeriodSeries prev) {
		JRDesignTimePeriodSeries f = new JRDesignTimePeriodSeries();
		f.setSeriesExpression(expr);
		if (prev == null) {
			f.setValueExpression(new JRDesignExpression("0"));
			f.setStartDateExpression(new JRDesignExpression("new java.util.Date()"));
			f.setEndDateExpression(new JRDesignExpression("new java.util.Date()"));
		} else {
			f.setValueExpression(ExprUtil.clone(prev.getValueExpression()));
			f.setStartDateExpression(ExprUtil.clone(prev.getStartDateExpression()));
			f.setEndDateExpression(ExprUtil.clone(prev.getEndDateExpression()));
			f.setLabelExpression(ExprUtil.clone(prev.getLabelExpression()));
		}
		return f;
	}

	public String getColumnText(Object element, int columnIndex) {
		JRTimePeriodSeries dcs = (JRTimePeriodSeries) element;
		switch (columnIndex) {
		case 0:
			if (dcs.getSeriesExpression() != null && dcs.getSeriesExpression().getText() != null)
				return dcs.getSeriesExpression().getText();
		}
		return ""; //$NON-NLS-1$
	}

	public Object getValue(JRTimePeriodSeries element, String property) {
		JRTimePeriodSeries prop = (JRTimePeriodSeries) element;
		if ("NAME".equals(property)) { //$NON-NLS-1$
			return prop.getSeriesExpression();
		}
		return ""; //$NON-NLS-1$
	}

	public void modify(JRTimePeriodSeries element, String property, Object value) {
		JRDesignTimePeriodSeries data = (JRDesignTimePeriodSeries) element;
		if ("NAME".equals(property) && value instanceof JRExpression) //$NON-NLS-1$
			data.setSeriesExpression((JRExpression) value);
	}

	private List<JRTimePeriodSeries> vlist;

	public List<JRTimePeriodSeries> getList() {
		return vlist;
	}

	public void setList(List<JRTimePeriodSeries> vlist) {
		this.vlist = new ArrayList<JRTimePeriodSeries>(vlist);
	}
}
