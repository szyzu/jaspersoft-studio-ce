/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section.obj;

import net.sf.jasperreports.engine.design.JRDesignField;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.section.AbstractSection;

public class FieldSection extends AbstractSection {
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayout(new GridLayout(3, false));

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		createWidget4Property(parent, JRDesignField.PROPERTY_NAME).getControl().setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		createWidget4Property(parent, JRDesignField.PROPERTY_DESCRIPTION).getControl().setLayoutData(gd);
		createWidget4Property(parent, JRDesignField.PROPERTY_VALUE_CLASS_NAME);
	}
	
	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRDesignField.PROPERTY_NAME, Messages.common_name);
		addProvidedProperties(JRDesignField.PROPERTY_DESCRIPTION, Messages.common_description);
		addProvidedProperties(JRDesignField.PROPERTY_VALUE_CLASS_NAME, Messages.common_class);
	}
}
