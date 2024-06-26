/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.property;

import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabCell;
import net.sf.jasperreports.engine.base.JRBaseStyle;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.components.crosstab.messages.Messages;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.section.AbstractSection;

public class CrosstabCellSection extends AbstractSection {

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(final Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayout(new GridLayout(2, false));

		createWidget4Property(parent, JRBaseStyle.PROPERTY_MODE);
		createWidget4Property(parent, JRBaseStyle.PROPERTY_BACKCOLOR);
		createWidget4Property(parent, JRDesignCellContents.PROPERTY_STYLE);
		createWidget4Property(parent, JRDesignCrosstabCell.PROPERTY_WIDTH);
		createWidget4Property(parent, JRDesignCrosstabCell.PROPERTY_HEIGHT);
	}
	
	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRBaseStyle.PROPERTY_MODE, Messages.MCell_transparent);
		addProvidedProperties(JRBaseStyle.PROPERTY_BACKCOLOR, Messages.MCell_backcolor);
		addProvidedProperties(JRDesignCellContents.PROPERTY_STYLE, Messages.MCell_parent_style);
		addProvidedProperties(JRDesignCrosstabCell.PROPERTY_WIDTH, Messages.common_width);
		addProvidedProperties(JRDesignCrosstabCell.PROPERTY_HEIGHT, Messages.common_height);
	}
}
