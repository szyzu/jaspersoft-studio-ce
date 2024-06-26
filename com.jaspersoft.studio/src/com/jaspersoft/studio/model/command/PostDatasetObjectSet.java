/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.command;

import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignVariable;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.views.properties.IPropertySource;

import com.jaspersoft.studio.model.MQuery;
import com.jaspersoft.studio.model.dataset.MDataset;
import com.jaspersoft.studio.model.field.MField;
import com.jaspersoft.studio.model.parameter.MParameter;
import com.jaspersoft.studio.model.variable.MVariable;
import com.jaspersoft.studio.property.IPostSetValue;

public class PostDatasetObjectSet implements IPostSetValue {

	@Override
	public Command postSetValue(IPropertySource target, Object prop, Object newValue, Object oldValue) {
		if (target instanceof MField && prop.equals(JRDesignField.PROPERTY_NAME))
			return new RenameDatasetObjectNameCommand((MField) target, (String) oldValue);
		if (target instanceof MVariable && prop.equals(JRDesignVariable.PROPERTY_NAME))
			return new RenameDatasetObjectNameCommand((MVariable) target, (String) oldValue);
		if (target instanceof MParameter && prop.equals(JRDesignParameter.PROPERTY_NAME)
				&& ((MParameter) target).getParent() != null)
			return new RenameDatasetObjectNameCommand((MParameter) target, (String) oldValue);
		if (target instanceof MDataset && prop.equals(JRDesignDataset.PROPERTY_QUERY))
			return new SyncDatasetRunCommand((MDataset) target, (MQuery) newValue, (MQuery) oldValue);
		if (target instanceof MQuery && prop.equals(JRDesignQuery.PROPERTY_LANGUAGE))
			return new SyncDatasetRunCommand((MQuery) target, (String) newValue, (String) oldValue);
		return null;
	}

}
