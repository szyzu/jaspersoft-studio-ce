/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.wizard.fragments.data.widget;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.components.chart.wizard.fragments.data.ADSComponent;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.DSCategory;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.DSGantt;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.DSHighLow;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.DSPie;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.DSTimePeriod;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.DSTimeSeries;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.DSValue;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.DSXy;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.DSXyz;
import com.jaspersoft.studio.components.chartspider.wizard.action.DSSpider;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.IExpressionContextSetter;
import com.jaspersoft.studio.jasper.JSSDrawVisitor;
import com.jaspersoft.studio.jasper.JSSReportConverter;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.charts.design.JRDesignCategoryDataset;
import net.sf.jasperreports.charts.design.JRDesignGanttDataset;
import net.sf.jasperreports.charts.design.JRDesignHighLowDataset;
import net.sf.jasperreports.charts.design.JRDesignPieDataset;
import net.sf.jasperreports.charts.design.JRDesignTimePeriodDataset;
import net.sf.jasperreports.charts.design.JRDesignTimeSeriesDataset;
import net.sf.jasperreports.charts.design.JRDesignValueDataset;
import net.sf.jasperreports.charts.design.JRDesignXyDataset;
import net.sf.jasperreports.charts.design.JRDesignXyzDataset;
import net.sf.jasperreports.components.spiderchart.StandardSpiderDataset;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;
import net.sf.jasperreports.engine.design.JasperDesign;

public class DatasetSeriesWidget implements IExpressionContextSetter{
	private JRDesignElementDataset eDataset;
	private JasperDesign jrDesign;
	private JRDesignElement jrChart;
	private Map<Class<? extends JRDesignElementDataset>, ADSComponent> map = new HashMap<Class<? extends JRDesignElementDataset>, ADSComponent>();
	private StackLayout stacklayout;
	private Composite sComposite;
	private JSSDrawVisitor dv;
	private JasperReportsConfiguration jrContext;
	private ExpressionContext expContext;
	

	public DatasetSeriesWidget(Composite parent,
			JasperReportsConfiguration jrContext) {
		createDataset(parent);
		this.jrContext = jrContext;
	}

	public void createDataset(Composite composite) {
		sComposite = new Composite(composite, SWT.NONE);
		stacklayout = new StackLayout();
		sComposite.setLayout(stacklayout);
		sComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		map.put(JRDesignCategoryDataset.class, new DSCategory(sComposite, this));
		map.put(JRDesignGanttDataset.class, new DSGantt(sComposite, this));
		map.put(JRDesignHighLowDataset.class, new DSHighLow(sComposite, this));
		map.put(JRDesignPieDataset.class, new DSPie(sComposite, this));
		map.put(JRDesignTimePeriodDataset.class, new DSTimePeriod(sComposite,
				this));
		map.put(JRDesignTimeSeriesDataset.class, new DSTimeSeries(sComposite,
				this));
		map.put(JRDesignValueDataset.class, new DSValue(sComposite, this));
		map.put(JRDesignXyDataset.class, new DSXy(sComposite, this));
		map.put(JRDesignXyzDataset.class, new DSXyz(sComposite, this));
		// here we can add other datasources ...
		map.put(StandardSpiderDataset.class, new DSSpider(sComposite, this));

		stacklayout.topControl = map.get(JRDesignCategoryDataset.class)
				.getControl();
	}

	public String getName(Class<? extends JRDesignElementDataset> key) {
		ADSComponent c = map.get(key);
		if (c != null) {
			return c.getName();
		}
		return "noname";
	}

	public void setDataset(JasperDesign jrDesign, JRDesignElement jrChart,
			JRDesignElementDataset eDataset) {
		this.eDataset = eDataset;
		if (jrDesign != null && this.jrDesign != jrDesign) {
			this.jrDesign = jrDesign;
		}
		this.jrChart = jrChart;
		fillData();
	}

	private void fillData() {
		ADSComponent c = null;
		if (eDataset != null)
			c = map.get(eDataset.getClass());
		if (c != null) {
			dv = new JSSDrawVisitor(new JSSReportConverter(jrContext, jrDesign, true), c.getGraphics2D());
			c.setData(dv, jrChart, eDataset, jrContext);
			stacklayout.topControl = c.getControl();
		} else {
			// a label, with not implemented ...
		}
		sComposite.layout(true);
	}

	public void setExpressionContext(ExpressionContext expContext) {
		this.expContext=expContext;
		for(ADSComponent c : map.values()){
			c.setExpressionContext(this.expContext);
		}
	}

}
