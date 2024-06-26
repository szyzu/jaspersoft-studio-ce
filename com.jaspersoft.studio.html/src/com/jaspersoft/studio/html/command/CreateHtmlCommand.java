/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.html.command;

import net.sf.jasperreports.engine.design.JRDesignElement;

import org.eclipse.draw2d.geometry.Rectangle;

import com.jaspersoft.studio.html.model.MHtml;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.MElementGroup;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.model.band.MBand;
import com.jaspersoft.studio.model.command.CreateElementCommand;
import com.jaspersoft.studio.model.frame.MFrame;
/*
 * link nodes & together.
 * 
 * @author Narcis Marcu
 */
public class CreateHtmlCommand extends CreateElementCommand {

	/**
	 * @param destNode
	 * @param srcNode
	 * @param index
	 */
	public CreateHtmlCommand(MElementGroup destNode, MGraphicElement srcNode, int index) {
		super(destNode, srcNode, index);
	}

	/**
	 * Instantiates a new creates the element command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 * @param index
	 *          the index
	 */
	public CreateHtmlCommand(MFrame destNode, MGraphicElement srcNode, int index) {
		super(destNode, srcNode, index);
	}

	/**
	 * Instantiates a new creates the element command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 * @param index
	 *          the index
	 */
	public CreateHtmlCommand(MBand destNode, MGraphicElement srcNode, int index) {
		super(destNode, srcNode, index);
	}

	/**
	 * Instantiates a new creates the element command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 * @param position
	 *          the position
	 * @param index
	 *          the index
	 */
	public CreateHtmlCommand(ANode destNode, MGraphicElement srcNode, Rectangle position, int index) {
		super(destNode, srcNode, position, index);
	}

	/**
	 * Creates the object.
	 */
	protected void createObject() {
		if (getJrElement() == null) {
			srcNode = new MHtml();
			if (srcNode.getValue() == null) {
				jrElement = srcNode.createJRElement(jasperDesign);
			} else {
				jrElement = (JRDesignElement) srcNode.getValue();
			}

			if (jrElement != null) {
				setElementBounds();
			}
		}
	}

}
