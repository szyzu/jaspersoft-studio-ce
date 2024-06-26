/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.table.model.columngroup;

import java.beans.PropertyChangeEvent;

import net.sf.jasperreports.components.table.DesignCell;
import net.sf.jasperreports.components.table.StandardBaseColumn;
import net.sf.jasperreports.components.table.StandardColumnGroup;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.design.events.CollectionElementAddedEvent;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.components.table.TableNodeIconDescriptor;
import com.jaspersoft.studio.components.table.model.AMCollection;
import com.jaspersoft.studio.components.table.model.MTable;
import com.jaspersoft.studio.components.table.model.column.MCell;
import com.jaspersoft.studio.components.table.util.TableColumnNumerator;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.util.IIconDescriptor;

public class MColumnGroupCell extends MCell {
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
			iconDescriptor = new TableNodeIconDescriptor("tablecolumngroup"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/** The descriptors. */
	protected static IPropertyDescriptor[] descriptors;

	public MColumnGroupCell(ANode parent, StandardColumnGroup jrDataset,
			DesignCell cell, String name, int index) {
		super(parent, jrDataset, cell, name, index);
	}

	@Override
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	@Override
	public String getToolTip() {
		return getIconDescriptor().getToolTip() + ": " + getDisplayText();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		AMCollection section = getSection();
		if (section != null) {
			if (evt.getPropertyName().equals(
					StandardColumnGroup.PROPERTY_COLUMNS)) {
				if (evt.getSource() instanceof StandardColumnGroup
						&& evt.getSource() == getValue()) {
					if (evt.getOldValue() == null && evt.getNewValue() != null) {
						int newIndex = -1;
						if (evt instanceof CollectionElementAddedEvent) {
							newIndex = ((CollectionElementAddedEvent) evt)
									.getAddedIndex();
						}
						StandardBaseColumn bc = (StandardBaseColumn) evt
								.getNewValue();
						if (section != null) {
							section.createColumn(this, bc, 122, newIndex);
						}
					} else if (evt.getOldValue() != null
							&& evt.getNewValue() == null) {
						// delete
						for (INode n : getChildren()) {
							if (n.getValue() == evt.getOldValue()) {
								removeChild((ANode) n);
								break;
							}
						}
					} else {
						// changed
						for (INode n : getChildren()) {
							if (n.getValue() == evt.getOldValue())
								n.setValue(evt.getNewValue());
						}
					}
				}
				MTable mTable = (MTable) section.getParent();
				mTable.getTableManager().refresh();
				TableColumnNumerator.renumerateColumnNames(mTable);
			}
		}
		super.propertyChange(evt);
	}
}
