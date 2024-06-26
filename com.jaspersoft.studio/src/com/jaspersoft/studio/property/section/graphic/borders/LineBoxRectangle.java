/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section.graphic.borders;

import java.awt.Graphics2D;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import com.jaspersoft.studio.editor.gef.figures.ComponentFigure;
import com.jaspersoft.studio.model.ILineBox;
import com.jaspersoft.studio.model.style.MStyle;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.JRBasePrintText;

/**
 * Widget that contains the method to represent the border of an element and
 * permit to view or edit their properties
 * 
 * @author Marco Orlandin
 * 
 */
public class LineBoxRectangle extends RectangleFigure {

	/**
	 * The drawer of the border
	 */
	private LineBoxDrawer bd;

	/**
	 * The section of the element
	 */
	private BordersSection section;

	public LineBoxRectangle(LineBoxDrawer drawer, BordersSection section) {
		bd = drawer;
		this.section = section;
	}

	/**
	 * Request the painting of the border
	 */
	@Override
	public void paint(Graphics graphics) {
		try {
			Graphics2D g = ComponentFigure.getG2D(graphics);
			if (g != null) {

				Rectangle b = getBounds();

				JRPrintElement pe = new JRBasePrintText(null);
				pe.setX(b.x + 10);
				pe.setY(b.y + 10);
				pe.setWidth(b.width - 20);
				pe.setHeight(b.height - 20);
				if (section.getElement() instanceof ILineBox
						&& ((ILineBox) section.getElement()).getBoxContainer() != null)
					bd.drawBox(g, ((ILineBox) section.getElement()).getBoxContainer().getLineBox(), pe);
				else if (section.getElement() instanceof MStyle) {
					MStyle styleModel = (MStyle) section.getElement();
					bd.drawBox(g, styleModel.getValue().getLineBox(), pe);
				}
			} else {
				graphics.drawRectangle(0, 0, 100, 100);
			}
		} catch (Exception e) {
			// when a font is missing exception is thrown by DrawVisitor
			// FIXME: maybe draw something, else?
			e.printStackTrace();
		}
	}
}
