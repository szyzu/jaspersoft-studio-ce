/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.dataset.descriptor;

import net.sf.jasperreports.engine.design.JRDesignElementDataset;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.SPIncrementType;
import com.jaspersoft.studio.property.section.widgets.SPResetType;

/*
 * The location section on the location tab.
 * 
 * @author Chicu Veaceslav
 */
public class DatasetSection extends AbstractSection {

	protected ExpandableComposite datasetRunSection;

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayout(new GridLayout(2, false));
		Composite cmp = preCreateProperties(parent);
		createProperties(cmp);
	}

	protected Composite preCreateProperties(Composite parent) {
		return parent;
	}

	protected void createProperties(Composite parent) {
		IPropertyDescriptor pd = getPropertyDesriptor(JRDesignElementDataset.PROPERTY_INCREMENT_TYPE);
		IPropertyDescriptor gpd = getPropertyDesriptor(JRDesignElementDataset.PROPERTY_INCREMENT_GROUP);
		getWidgetFactory().createCLabel(parent, pd.getDisplayName());
		widgets.put(pd.getId(), new SPIncrementType(parent, this, pd, gpd));

		createWidget4Property(parent, JRDesignElementDataset.PROPERTY_INCREMENT_WHEN_EXPRESSION);

		pd = getPropertyDesriptor(JRDesignElementDataset.PROPERTY_DATASET_RESET_TYPE);
		gpd = getPropertyDesriptor(JRDesignElementDataset.PROPERTY_RESET_GROUP);
		getWidgetFactory().createCLabel(parent, pd.getDisplayName());
		widgets.put(pd.getId(), new SPResetType(parent, this, pd, gpd));

		Composite group = getWidgetFactory().createSection(parent, "Dataset Run", true, 2, 2);
		datasetRunSection = (ExpandableComposite) group.getParent();

		createWidget4Property(group, JRDesignElementDataset.PROPERTY_DATASET_RUN);
	}

	@Override
	public void expandForProperty(Object propertyId) {
		if (propertyId.equals(datasetRunSection) && datasetRunSection != null && !datasetRunSection.isExpanded())
			datasetRunSection.setExpanded(true);
	}

	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRDesignElementDataset.PROPERTY_INCREMENT_TYPE, Messages.common_increment_type);
		addProvidedProperties(JRDesignElementDataset.PROPERTY_INCREMENT_WHEN_EXPRESSION,
				Messages.MElementDataset_increment_when_expression);
		addProvidedProperties(JRDesignElementDataset.PROPERTY_DATASET_RESET_TYPE, Messages.common_reset_type);
		addProvidedProperties(JRDesignElementDataset.PROPERTY_DATASET_RUN, Messages.MElementDataset_dataset_run);
	}
}
