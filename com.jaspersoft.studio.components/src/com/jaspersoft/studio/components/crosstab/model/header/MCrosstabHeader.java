/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.model.header;

import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;

import com.jaspersoft.studio.components.crosstab.CrosstabCell;
import com.jaspersoft.studio.components.crosstab.CrosstabNodeIconDescriptor;
import com.jaspersoft.studio.components.crosstab.messages.Messages;
import com.jaspersoft.studio.components.crosstab.model.MCrosstab;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.IContainerEditPart;
import com.jaspersoft.studio.model.IGraphicElement;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MRoot;
import com.jaspersoft.studio.model.util.IIconDescriptor;

import net.sf.jasperreports.crosstabs.design.JRCrosstabOrigin;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.utils.compatibility.CompatibilityConstants;

public class MCrosstabHeader extends ANode implements IGraphicElement,
		IContainerEditPart {
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
			iconDescriptor = new CrosstabNodeIconDescriptor("cell"); //$NON-NLS-1$
		return iconDescriptor;
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

	/**
	 * Instantiates a new m field.
	 */
	public MCrosstabHeader() {
		super();
	}

	/**
	 * Instantiates a new m field.
	 * 
	 * @param parent
	 *            the parent
	 * @param jfRield
	 *            the jf rield
	 * @param newIndex
	 *            the new index
	 */
	public MCrosstabHeader(ANode parent, int index) {
		super(parent, index);
	}

	@Override
	public Color getForeground() {
		return UIUtils.getSystemColor(CompatibilityConstants.Colors.COLOR_WIDGET_DISABLED_FOREGROUND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getDisplayText()
	 */
	public String getDisplayText() {
		return Messages.MCrosstabHeader_header_cell;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getImagePath()
	 */
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getToolTip()
	 */
	@Override
	public String getToolTip() {
		return getIconDescriptor().getToolTip();
	}

	public static final CrosstabCell cell = new CrosstabCell(
			JRCrosstabOrigin.TYPE_HEADER_CELL);

	@Override
	public Rectangle getBounds() {
		return getCrosstab().getCrosstabManager().getBounds(cell);
	}

	@Override
	public int getDefaultWidth() {
		return 100;
	}

	@Override
	public int getDefaultHeight() {
		return 100;
	}

	@Override
	public JRDesignElement createJRElement(JasperDesign jasperDesign) {
		return null;
	}

}
