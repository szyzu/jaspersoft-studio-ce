/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.property.section.series;

import net.sf.jasperreports.charts.design.JRDesignXySeries;

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
public class SeriesXySection extends AbstractSection {

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayout(new GridLayout(2, false));

		createWidget4Property(parent, JRDesignXySeries.PROPERTY_X_VALUE_EXPRESSION);
		createWidget4Property(parent, JRDesignXySeries.PROPERTY_Y_VALUE_EXPRESSION);
		createWidget4Property(parent, JRDesignXySeries.PROPERTY_LABEL_EXPRESSION);
		createWidget4Property(parent, JRDesignXySeries.PROPERTY_SERIES_EXPRESSION);
		createWidget4Property(parent, JRDesignXySeries.PROPERTY_AUTO_SORT, false);
	}

	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRDesignXySeries.PROPERTY_X_VALUE_EXPRESSION, Messages.common_x_value_expression);
		addProvidedProperties(JRDesignXySeries.PROPERTY_Y_VALUE_EXPRESSION, Messages.common_y_value_expression);
		addProvidedProperties(JRDesignXySeries.PROPERTY_LABEL_EXPRESSION, Messages.common_label_expression);
		addProvidedProperties(JRDesignXySeries.PROPERTY_SERIES_EXPRESSION, Messages.common_series_expression);
		addProvidedProperties(JRDesignXySeries.PROPERTY_AUTO_SORT, Messages.MXYSeries_autoSortTitle);
	}

}
