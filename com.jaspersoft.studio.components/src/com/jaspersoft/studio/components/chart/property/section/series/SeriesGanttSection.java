/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.property.section.series;

import net.sf.jasperreports.charts.design.JRDesignGanttSeries;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.section.AbstractSection;

/*
 * The location section on the location tab.
 * 
 * @author Chicu Veaceslav
 */
public class SeriesGanttSection extends AbstractSection {

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayout(new GridLayout(2, false));

		createWidget4Property(parent,
				JRDesignGanttSeries.PROPERTY_START_DATE_EXPRESSION);
		createWidget4Property(parent,
				JRDesignGanttSeries.PROPERTY_END_DATE_EXPRESSION);
		createWidget4Property(parent,
				JRDesignGanttSeries.PROPERTY_TASK_EXPRESSION);
		createWidget4Property(parent,
				JRDesignGanttSeries.PROPERTY_SUBTASK_EXPRESSION);
		createWidget4Property(parent,
				JRDesignGanttSeries.PROPERTY_LABEL_EXPRESSION);
		createWidget4Property(parent,
				JRDesignGanttSeries.PROPERTY_SERIES_EXPRESSION);
		createWidget4Property(parent,
				JRDesignGanttSeries.PROPERTY_PERCENT_EXPRESSION);
	}
	
	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRDesignGanttSeries.PROPERTY_START_DATE_EXPRESSION, Messages.common_start_date_expression);
		addProvidedProperties(JRDesignGanttSeries.PROPERTY_END_DATE_EXPRESSION, Messages.common_end_date_expression);
		addProvidedProperties(JRDesignGanttSeries.PROPERTY_TASK_EXPRESSION, Messages.MGanttSeries_task_expression);
		addProvidedProperties(JRDesignGanttSeries.PROPERTY_SUBTASK_EXPRESSION, Messages.MGanttSeries_subtask_expression);
		addProvidedProperties(JRDesignGanttSeries.PROPERTY_LABEL_EXPRESSION, Messages.common_label_expression);
		addProvidedProperties(JRDesignGanttSeries.PROPERTY_SERIES_EXPRESSION, Messages.common_series_expression);
		addProvidedProperties(JRDesignGanttSeries.PROPERTY_PERCENT_EXPRESSION, Messages.MGanttSeries_percent_expression);
	}

}
