/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.barcode.property;

import net.sf.jasperreports.components.barcode4j.DataMatrixComponent;

import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.components.barcode.messages.Messages;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.section.AbstractSection;

public class DataMatrixSection extends AbstractSection {

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite group = getWidgetFactory().createSection(parent, "DataMatrix", false, 2);

		createWidget4Property(group, DataMatrixComponent.PROPERTY_SHAPE);

		createWidget4Property(group, DataMatrixComponent.PROPERTY_MIN_SYMBOL_HEIGHT);
		createWidget4Property(group, DataMatrixComponent.PROPERTY_MAX_SYMBOL_HEIGHT);
		createWidget4Property(group, DataMatrixComponent.PROPERTY_MIN_SYMBOL_WIDTH);
		createWidget4Property(group, DataMatrixComponent.PROPERTY_MAX_SYMBOL_WIDTH);
	}

	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(DataMatrixComponent.PROPERTY_SHAPE, Messages.MDataMatrix_shape);
	}
}
