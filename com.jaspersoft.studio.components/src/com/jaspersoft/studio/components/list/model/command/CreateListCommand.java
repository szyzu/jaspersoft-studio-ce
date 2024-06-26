/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.list.model.command;

import net.sf.jasperreports.components.list.DesignListContents;
import net.sf.jasperreports.components.list.StandardListComponent;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignElement;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.jaspersoft.studio.components.list.model.command.wizard.ListWizard;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MElementGroup;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.model.band.MBand;
import com.jaspersoft.studio.model.command.CreateElementCommand;
import com.jaspersoft.studio.model.frame.MFrame;

public class CreateListCommand extends CreateElementCommand {

	/**
	 * Instantiates a new creates the element command.
	 * 
	 * @param destNode
	 *            the dest node
	 * @param srcNode
	 *            the src node
	 * @param index
	 *            the index
	 */
	public CreateListCommand(MElementGroup destNode, MGraphicElement srcNode, int index) {
		super(destNode, srcNode, index);
	}

	/**
	 * Instantiates a new creates the element command.
	 * 
	 * @param destNode
	 *            the dest node
	 * @param srcNode
	 *            the src node
	 * @param index
	 *            the index
	 */
	public CreateListCommand(MFrame destNode, MGraphicElement srcNode, int index) {
		super(destNode, srcNode, index);
	}

	/**
	 * Instantiates a new creates the element command.
	 * 
	 * @param destNode
	 *            the dest node
	 * @param srcNode
	 *            the src node
	 * @param index
	 *            the index
	 */
	public CreateListCommand(MBand destNode, MGraphicElement srcNode, int index) {
		super(destNode, srcNode, index);
	}

	/**
	 * Instantiates a new creates the element command.
	 * 
	 * @param destNode
	 *            the dest node
	 * @param srcNode
	 *            the src node
	 * @param position
	 *            the position
	 * @param index
	 *            the index
	 */
	public CreateListCommand(ANode destNode, MGraphicElement srcNode, Rectangle position, int index) {
		super(destNode, srcNode, position, index);
	}

	/**
	 * Check if the source is inside the destination recursively
	 * 
	 * @param dest
	 *            the destination node
	 * @param actaulSource
	 *            the actual source node of the recursion
	 * @return true if the destination node is the same as the source node or one of
	 *         its children, false otherwise
	 */
	private boolean sourceContainsDestination(INode dest, INode actaulSource) {
		if (dest == actaulSource)
			return true;
		else {
			for (INode child : actaulSource.getChildren()) {
				boolean contains = sourceContainsDestination(dest, child);
				if (contains)
					return true;
			}
		}
		return false;

	}

	@Override
	public boolean canExecute() {
		return !sourceContainsDestination(destNode, srcNode) && super.canExecute();
	}

	/**
	 * Creates the object.
	 */
	@Override
	protected void createObject() {
		if (jrElement == null) {
			// Pass to the wizard the default width and height if available, otherwise
			// uses the default ones
			int suggestedWidth = location != null ? location.width : -1;
			int suggestedHeight = location != null ? location.height : -1;
			ListWizard wizard = new ListWizard(suggestedWidth, suggestedHeight);
			wizard.setConfig(jConfig, false);

			WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);

			dialog.create();
			if (dialog.open() == Dialog.OK) {
				srcNode = wizard.getList();
				addCommands(wizard.getCommands());
				if (srcNode.getValue() == null)
					jrElement = srcNode.createJRElement(jasperDesign);
				else {
					jrElement = (JRDesignElement) srcNode.getValue();
					if (location != null) {
						location.width = Math.max(location.width, jrElement.getWidth());
						location.height = Math.max(location.height, jrElement.getHeight());
					}
				}
				if (jrElement != null) {
					setElementBounds();
					JRDesignComponentElement jrel = (JRDesignComponentElement) jrElement;
					StandardListComponent jrList = (StandardListComponent) jrel.getComponent();
					DesignListContents contents = (DesignListContents) jrList.getContents();
					contents.setHeight(jrElement.getHeight());
					contents.setWidth((Integer) jrElement.getWidth());
				}
			}
		}
	}

}
