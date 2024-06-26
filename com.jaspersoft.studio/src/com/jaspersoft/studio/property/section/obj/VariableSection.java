/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section.obj;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.SPIncrementType;
import com.jaspersoft.studio.property.section.widgets.SPResetType;

import net.sf.jasperreports.engine.design.JRDesignVariable;

public class VariableSection extends AbstractSection {
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayout(new GridLayout(2, false));
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		
		createWidget4Property(parent, JRDesignVariable.PROPERTY_DESCRIPTION).getControl().setLayoutData(gd);
		
		createWidget4Property(parent, JRDesignVariable.PROPERTY_CALCULATION).getControl().setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		createWidget4Property(parent, JRDesignVariable.PROPERTY_EXPRESSION).getControl().setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		createWidget4Property(parent, JRDesignVariable.PROPERTY_INITIAL_VALUE_EXPRESSION).getControl().setLayoutData(gd);

		IPropertyDescriptor pd = getPropertyDesriptor(JRDesignVariable.PROPERTY_INCREMENT_TYPE);
		IPropertyDescriptor gpd = getPropertyDesriptor(JRDesignVariable.PROPERTY_INCREMENT_GROUP);
		getWidgetFactory().createCLabel(parent, pd.getDisplayName());
		SPIncrementType winctype = new SPIncrementType(parent, this, pd, gpd);
		winctype.getControl();
		widgets.put(pd.getId(), winctype);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		createWidget4Property(parent, JRDesignVariable.PROPERTY_INCREMENTER_FACTORY_CLASS_NAME).getControl().setLayoutData(gd);

		pd = getPropertyDesriptor(JRDesignVariable.PROPERTY_RESET_TYPE);
		gpd = getPropertyDesriptor(JRDesignVariable.PROPERTY_RESET_GROUP);
		getWidgetFactory().createCLabel(parent, pd.getDisplayName());
		SPResetType wrestype = new SPResetType(parent, this, pd, gpd);
		wrestype.getControl();
		widgets.put(pd.getId(), wrestype);
	}

	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRDesignVariable.PROPERTY_DESCRIPTION, Messages.common_description);
		addProvidedProperties(JRDesignVariable.PROPERTY_CALCULATION, Messages.MVariable_calculation);
		addProvidedProperties(JRDesignVariable.PROPERTY_EXPRESSION, Messages.common_expression);
		addProvidedProperties(JRDesignVariable.PROPERTY_INITIAL_VALUE_EXPRESSION,
				Messages.MVariable_initial_value_expression);
		addProvidedProperties(JRDesignVariable.PROPERTY_INCREMENT_TYPE, Messages.common_increment_type);
		addProvidedProperties(JRDesignVariable.PROPERTY_INCREMENTER_FACTORY_CLASS_NAME,
				Messages.MVariable_incrementer_factory_class_name);
		addProvidedProperties(JRDesignVariable.PROPERTY_RESET_TYPE, Messages.common_reset_type);
	}
}
