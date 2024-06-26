/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.returnvalue;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.property.descriptor.text.NTextPropertyDescriptor;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.IPropertyDescriptorWidget;
import com.jaspersoft.studio.property.section.widgets.SPSubreportReturnValuesButton;

public class RVPropertyDescriptor extends NTextPropertyDescriptor implements IPropertyDescriptorWidget {

	public RVPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		return new RVPropertiesCellEditor(parent);
	}

	@Override
	public ILabelProvider getLabelProvider() {
		if (isLabelProviderSet())
			return super.getLabelProvider();
		return new RVPropertiesLabelProvider();
	}

	@Override
	public ASPropertyWidget<RVPropertyDescriptor> createWidget(Composite parent, AbstractSection section) {
		return new SPSubreportReturnValuesButton<RVPropertyDescriptor>(parent, section, this, getDisplayName());
	}
}
