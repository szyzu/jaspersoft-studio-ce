/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.wizard.fragments.data.series;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.charts.JRPieSeries;
import net.sf.jasperreports.charts.design.JRDesignPieSeries;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;

import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;

public class PieSerie implements ISeriesFactory<JRPieSeries> {

	public JRDesignPieSeries createSerie() {
		return createSerie(new JRDesignExpression("\"SERIES 1\""), null);
	}

	@Override
	public JRDesignPieSeries createSerie(JRDesignExpression expr, JRPieSeries prev) {
		JRDesignPieSeries f = new JRDesignPieSeries();
		f.setKeyExpression(expr);
		if (prev == null)
			f.setValueExpression(new JRDesignExpression("0"));
		else {
			f.setValueExpression(ExprUtil.clone(prev.getValueExpression()));
			f.setLabelExpression(ExprUtil.clone(prev.getLabelExpression()));
		}
		return f;
	}

	public String getColumnText(Object element, int columnIndex) {
		JRPieSeries dcs = (JRPieSeries) element;
		switch (columnIndex) {
		case 0:
			if (dcs.getKeyExpression() != null && dcs.getKeyExpression().getText() != null)
				return dcs.getKeyExpression().getText();
		}
		return ""; //$NON-NLS-1$
	}

	public Object getValue(JRPieSeries element, String property) {
		JRDesignPieSeries prop = (JRDesignPieSeries) element;
		if ("NAME".equals(property)) { //$NON-NLS-1$
			return prop.getKeyExpression();
		}
		return ""; //$NON-NLS-1$
	}

	public void modify(JRPieSeries element, String property, Object value) {
		JRDesignPieSeries data = (JRDesignPieSeries) element;
		if ("NAME".equals(property) && value instanceof JRExpression) //$NON-NLS-1$
			data.setKeyExpression((JRExpression) value);
	}

	private List<JRPieSeries> vlist;

	public List<JRPieSeries> getList() {
		return vlist;
	}

	public void setList(List<JRPieSeries> vlist) {
		this.vlist = new ArrayList<JRPieSeries>(vlist);
	}
}
