/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.model.columngroup;

import java.beans.PropertyChangeEvent;

import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.components.crosstab.messages.Messages;
import com.jaspersoft.studio.components.crosstab.model.MCrosstab;
import com.jaspersoft.studio.components.crosstab.model.cell.MCell;
import com.jaspersoft.studio.components.crosstab.model.header.MCrosstabHeader;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MRoot;

import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabColumnGroup;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.utils.compatibility.CompatibilityConstants;

public class MColumnCrosstabHeaderCell extends MCell {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MColumnCrosstabHeaderCell() {
		super();
	}

	public MColumnCrosstabHeaderCell(ANode parent, JRCellContents jfRield, int index) {
		super(parent, jfRield, Messages.MCrosstabHeaderCell_crosstab_header, index);
	}

	@Override
	public Color getForeground() {
		if (getValue() == null)
			return UIUtils.getSystemColor(CompatibilityConstants.Colors.COLOR_WIDGET_DISABLED_FOREGROUND);
		return null;
	}
	
	@Override
	protected void postDescriptors(IPropertyDescriptor[] descriptors) {
		if (getValue() != null) super.postDescriptors(descriptors);
	}
	
	@Override
	public Rectangle getBounds() {
		if (getValue() == null){
			return getCrosstab().getCrosstabManager().getBounds(MCrosstabHeader.cell);
		} else return super.getBounds();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(JRDesignCrosstabColumnGroup.PROPERTY_CROSSTAB_HEADER)) {
			for (INode n : getChildren()) {
				if (n instanceof MColumnCrosstabHeaderCell) {
					n.setValue(evt.getNewValue());
					break;
				}
			}
		}
		super.propertyChange(evt);
	}
	
	public MCrosstab getCrosstab() {
		INode node = this;
		while (node != null && node.getParent() != null
				&& !(node instanceof MCrosstab) && !(node instanceof MRoot)) {
			node = node.getParent();
		}
		if (node instanceof MCrosstab)
			return (MCrosstab) node;
		return null;
	}
}
