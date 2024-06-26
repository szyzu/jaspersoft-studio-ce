/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.dataset.descriptor;

import net.sf.jasperreports.data.DataAdapterParameterContributorFactory;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.model.dataset.MDataset;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.section.AbstractSection;

/**
 * Section used to display the control regarding the main dataset in the report tab
 * 
 * @author Orlandin Marco
 * 
 */
public class MainDatasetSection extends AbstractSection {

	private ExpandableComposite section;
	
	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		Composite group = getWidgetFactory().createSection(parent, Messages.ReportSection_Dataset_Label, true, 2);
		section = (ExpandableComposite)group.getParent();
		group.setLayoutData(gd);

		group.setLayout(new GridLayout(2, false));

		gd = new GridData();
		createWidget4Property(group, JRDesignDataset.PROPERTY_WHEN_RESOURCE_MISSING_TYPE).getControl().setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		createWidget4Property(group, JRDesignDataset.PROPERTY_SCRIPTLET_CLASS).getControl().setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		createWidget4Property(group, JRDesignDataset.PROPERTY_RESOURCE_BUNDLE).getControl().setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		createWidget4Property(group, DataAdapterParameterContributorFactory.PROPERTY_DATA_ADAPTER_LOCATION).getControl().setLayoutData(gd);
		
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.horizontalSpan = 2;
		createWidget4Property(group, JRDesignDataset.PROPERTY_QUERY, false).getControl().setLayoutData(gd);
	}
	
	@Override
	public void expandForProperty(Object propertyId) {
		if (section != null && !section.isExpanded()) section.setExpanded(true);
	}
	
	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRDesignDataset.PROPERTY_WHEN_RESOURCE_MISSING_TYPE, Messages.MDataset_when_resource_missing_type);
		addProvidedProperties(JRDesignDataset.PROPERTY_SCRIPTLET_CLASS, Messages.MDataset_scriplet_class);
		addProvidedProperties(JRDesignDataset.PROPERTY_RESOURCE_BUNDLE, Messages.MDataset_resource_bundle);
		addProvidedProperties(JRDesignDataset.PROPERTY_QUERY, Messages.common_query);
		addProvidedProperties(DataAdapterParameterContributorFactory.PROPERTY_DATA_ADAPTER_LOCATION, Messages.DatasetSection_defaultAdapter);
	}
	

	@Override
	protected APropertyNode getModelFromEditPart(Object item) {
		APropertyNode md = super.getModelFromEditPart(item);
		if (md instanceof MReport) {
			MDataset dataset = (MDataset) md.getPropertyValue(JasperDesign.PROPERTY_MAIN_DATASET);
			return dataset;
		}
		return md;
	}
}
