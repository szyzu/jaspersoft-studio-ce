/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.barcode.command;

import net.sf.jasperreports.engine.design.JRDesignElement;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.MElementGroup;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.model.band.MBand;
import com.jaspersoft.studio.model.command.CreateElementCommand;
import com.jaspersoft.studio.model.frame.MFrame;

/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class CreateBarcodeCommand extends CreateElementCommand {

	/**
	 * @param destNode
	 * @param srcNode
	 * @param index
	 */
	public CreateBarcodeCommand(MElementGroup destNode, MGraphicElement srcNode, int index) {
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
	public CreateBarcodeCommand(MFrame destNode, MGraphicElement srcNode, int index) {
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
	public CreateBarcodeCommand(MBand destNode, MGraphicElement srcNode, int index) {
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
	public CreateBarcodeCommand(ANode destNode, MGraphicElement srcNode, Rectangle position, int index) {
		super(destNode, srcNode, position, index);
	}

	/**
	 * Creates the object.
	 */
	@Override
	protected void createObject() {
		if (getJrElement() == null) {
			// here put a wizard
			BarcodeWizard wizard = new BarcodeWizard();
			WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
			dialog.create();
			if (dialog.open() == Dialog.OK) {
				srcNode = wizard.getBarcode();
				if (srcNode.getValue() == null)
					jrElement = srcNode.createJRElement(jasperDesign, true);
				else
					jrElement = (JRDesignElement) srcNode.getValue();

				if (jrElement != null)
					setElementBounds();
			}
		}
	}

}
