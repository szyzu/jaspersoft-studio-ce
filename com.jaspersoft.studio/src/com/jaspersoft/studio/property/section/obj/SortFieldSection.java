/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section.obj;

import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.sortfield.MSortField;
import com.jaspersoft.studio.model.sortfield.MSortFields;
import com.jaspersoft.studio.model.sortfield.command.ChangeSortFieldNameCommand;
import com.jaspersoft.studio.model.sortfield.command.ChangeSortFieldTypeCommand;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.descriptors.NamedEnumPropertyDescriptor;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;

public class SortFieldSection extends AbstractSection {
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayout(new GridLayout(2, false));

		ASPropertyWidget<?> nameWidget = createWidget4Property(parent, JRDesignSortField.PROPERTY_NAME);
		nameWidget.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createWidget4Property(parent, JRDesignSortField.PROPERTY_TYPE);
		createWidget4Property(parent, JRDesignSortField.PROPERTY_ORDER);
	}

	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRDesignSortField.PROPERTY_NAME, Messages.common_name);
		addProvidedProperties(JRDesignSortField.PROPERTY_TYPE, Messages.MSortField_typeTitle);
		addProvidedProperties(JRDesignSortField.PROPERTY_ORDER, Messages.common_order);
	}

	@Override
	public boolean changeProperty(Object property, Object newValue) {
		boolean result = super.changeProperty(property, newValue);
		if (result)
			refresh();
		return result;
	}

	@Override
	public Command getChangePropertyCommand(Object property, Object newValue, APropertyNode n) {
		if (property.equals(JRDesignSortField.PROPERTY_TYPE)) {
			// reopen the wizard to select an unique name
			if (newValue == null)
				return null;
			if (newValue instanceof Integer)
				newValue = ((NamedEnumPropertyDescriptor<SortFieldTypeEnum>) getPropertyDesriptor(JRDesignSortField.PROPERTY_TYPE))
						.getEnumValue((Integer) newValue);
			return new ChangeSortFieldTypeCommand((MSortFields) n.getParent(), (MSortField) n, (SortFieldTypeEnum) newValue);
		} else if (property.equals(JRDesignSortField.PROPERTY_NAME)) {
			return new ChangeSortFieldNameCommand((MSortFields) n.getParent(), (MSortField) n, (String) newValue);
		} else {
			return super.getChangePropertyCommand(property, newValue, n);
		}
	}
}
