/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chartspider.property.section;

import net.sf.jasperreports.components.spiderchart.SpiderChartComponent;
import net.sf.jasperreports.components.spiderchart.StandardChartSettings;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.SPEvaluationTime;

/*
 * The location section on the location tab.
 * 
 * @author Chicu Veaceslav
 */
public class ChartSection extends AbstractSection {

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayout(new GridLayout(2, false));

		createWidget4Property(parent, StandardChartSettings.PROPERTY_BACKCOLOR);
		createWidget4Property(parent,StandardChartSettings.PROPERTY_CUSTOMIZER_CLASS);
		createWidget4Property(parent,StandardChartSettings.PROPERTY_RENDER_TYPE);

		IPropertyDescriptor pd = getPropertyDesriptor(SpiderChartComponent.PROPERTY_EVALUATION_TIME);
		IPropertyDescriptor gpd = getPropertyDesriptor(SpiderChartComponent.PROPERTY_EVALUATION_GROUP);
		getWidgetFactory().createCLabel(parent, pd.getDisplayName());
		widgets.put(pd.getId(), new SPEvaluationTime(parent, this, pd, gpd));
	}
	
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(StandardChartSettings.PROPERTY_BACKCOLOR, Messages.MChartPlot_backcolor);
		addProvidedProperties(StandardChartSettings.PROPERTY_CUSTOMIZER_CLASS, Messages.MChart_customizer_class);
		addProvidedProperties(StandardChartSettings.PROPERTY_RENDER_TYPE, Messages.MChart_renderer_type);
		addProvidedProperties(SpiderChartComponent.PROPERTY_EVALUATION_TIME, Messages.MChart_evaluation_time);
	}

}
