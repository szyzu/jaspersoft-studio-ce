/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model;

import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.draw2d.geometry.Rectangle;
/*
 * The Interface IGraphicElement.
 * 
 * @author Chicu Veaceslav
 */
public interface IGraphicElement {

	/**
	 * Gets the bounds.
	 * 
	 * @return the bounds
	 */
	public Rectangle getBounds();

	/**
	 * Gets the default width.
	 * 
	 * @return the default width
	 */
	public int getDefaultWidth();

	/**
	 * Gets the default height.
	 * 
	 * @return the default height
	 */
	public int getDefaultHeight();

	/**
	 * Creates the jr element.
	 * 
	 * @param jasperDesign
	 *          the jasper design
	 * @return the jR design element
	 */
	public abstract JRDesignElement createJRElement(JasperDesign jasperDesign);
}
