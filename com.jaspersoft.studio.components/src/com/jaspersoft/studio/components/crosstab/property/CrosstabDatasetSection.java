/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.property;

import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabDataset;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.components.crosstab.messages.Messages;
import com.jaspersoft.studio.components.crosstab.model.MCrosstab;
import com.jaspersoft.studio.components.crosstab.model.MCrosstabDataset;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.dataset.descriptor.DatasetSection;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;

public class CrosstabDatasetSection extends DatasetSection {
	
	private APropertyNode selectedCrosstab;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		createWidget4Property(parent,
				JRDesignCrosstabDataset.PROPERTY_DATA_PRE_SORTED, false)
				.getControl().setLayoutData(gd);
	}

	@Override
	protected APropertyNode getModelFromEditPart(Object item) {
		APropertyNode md = super.getModelFromEditPart(item);
		if (md instanceof MCrosstab) {
			selectedCrosstab = md;
			MCrosstabDataset dataset = (MCrosstabDataset) md
					.getPropertyValue(JRDesignCrosstab.PROPERTY_DATASET);
			return dataset;
		}
		return md;
	}
	
	@Override
	public APropertyNode getSelectedElement() {
		return selectedCrosstab;
	}
	
	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRDesignCrosstabDataset.PROPERTY_DATA_PRE_SORTED, Messages.MCrosstabDataset_data_presorted);
	}
}
