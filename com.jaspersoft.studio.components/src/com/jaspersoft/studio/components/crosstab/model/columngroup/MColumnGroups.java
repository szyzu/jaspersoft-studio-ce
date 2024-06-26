/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.model.columngroup;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.components.crosstab.CrosstabNodeIconDescriptor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MCollection;
import com.jaspersoft.studio.model.util.IIconDescriptor;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.engine.JRConstants;

public class MColumnGroups extends MCollection {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new CrosstabNodeIconDescriptor("columngroups"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/** The descriptors. */
	protected static IPropertyDescriptor[] descriptors;

	public MColumnGroups(ANode parent, JRCrosstab jrDataset, String property) {
		super(parent, jrDataset, property);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getDisplayText()
	 */
	public String getDisplayText() {
		return getIconDescriptor().getTitle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getImagePath()
	 */
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// PropertyChangeEvent newEvent = evt;
		if (evt.getPropertyName().equals(JRDesignCrosstab.PROPERTY_COLUMN_GROUPS) && evt.getSource() == getValue()) {
			if (evt.getOldValue() == null && evt.getNewValue() != null) {
				return;
			}
		}
		super.propertyChange(evt);
	}

	@Override
	public Map<String, List<ANode>> getUsedStyles() {
		Map<String, List<ANode>> result = super.getUsedStyles();
		for (INode node : getChildren()) {
			if (node instanceof ANode) {
				mergeElementStyle(result, ((ANode) node).getUsedStyles());
			}
		}
		return result;
	}

	@Override
	public Object getPropertyValue(Object id) {
		// nothing to do here, no properties
		return null;
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		// nothing to do here, no properties
	}

	@Override
	public void setDescriptors(IPropertyDescriptor[] descriptors1) {
		descriptors = descriptors1;
	}

	@Override
	public IPropertyDescriptor[] getDescriptors() {
		return descriptors;
	}

	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		// nothing to do here, no properties
	}
}
