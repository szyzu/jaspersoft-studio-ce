/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.style.view.text;

import java.awt.Graphics2D;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.JRBasePrintText;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import com.jaspersoft.studio.editor.gef.figures.ComponentFigure;
import com.jaspersoft.studio.property.section.graphic.borders.LineBoxDrawer;

/**
 * Widget that contains the method to represent the border of an element and permit to view or edit their properties
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
	private JRLineBox lineBox;

	public LineBoxRectangle(LineBoxDrawer drawer, JRLineBox lineBox) {
		bd = drawer;
		this.lineBox = lineBox;
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
				bd.drawBox(g, lineBox, pe);
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
