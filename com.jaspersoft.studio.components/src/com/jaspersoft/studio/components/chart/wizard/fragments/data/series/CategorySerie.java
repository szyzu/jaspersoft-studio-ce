/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.wizard.fragments.data.series;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.charts.JRCategorySeries;
import net.sf.jasperreports.charts.design.JRDesignCategorySeries;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;

import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;

public class CategorySerie implements ISeriesFactory<JRCategorySeries> {

	public JRDesignCategorySeries createSerie() {
		return createSerie(new JRDesignExpression("\"SERIES 1\""), null);
	}

	@Override
	public JRDesignCategorySeries createSerie(JRDesignExpression expr, JRCategorySeries prev) {
		JRDesignCategorySeries f = new JRDesignCategorySeries();
		f.setSeriesExpression(expr);
		if (prev == null) {
			f.setCategoryExpression(new JRDesignExpression("0"));
			f.setValueExpression(new JRDesignExpression("0"));
		} else {
			f.setCategoryExpression(ExprUtil.clone(prev.getCategoryExpression()));
			f.setValueExpression(ExprUtil.clone(prev.getValueExpression()));
			f.setLabelExpression(ExprUtil.clone(prev.getLabelExpression()));
		}
		return f;
	}

	public String getColumnText(Object element, int columnIndex) {
		JRCategorySeries dcs = (JRCategorySeries) element;
		switch (columnIndex) {
		case 0:
			if (dcs.getSeriesExpression() != null && dcs.getSeriesExpression().getText() != null)
				return dcs.getSeriesExpression().getText();
		}
		return ""; //$NON-NLS-1$
	}

	public Object getValue(JRCategorySeries element, String property) {
		JRCategorySeries prop = (JRCategorySeries) element;
		if ("NAME".equals(property)) { //$NON-NLS-1$
			return prop.getSeriesExpression();
		}
		return ""; //$NON-NLS-1$
	}

	public void modify(JRCategorySeries element, String property, Object value) {
		JRDesignCategorySeries data = (JRDesignCategorySeries) element;
		if ("NAME".equals(property) && value instanceof JRExpression) //$NON-NLS-1$
			data.setSeriesExpression((JRExpression) value);
	}

	private List<JRCategorySeries> vlist;

	public List<JRCategorySeries> getList() {
		return vlist;
	}

	public void setList(List<JRCategorySeries> vlist) {
		this.vlist = new ArrayList<JRCategorySeries>(vlist);
	}
}
