/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.table.model.cell.command;

import net.sf.jasperreports.components.table.DesignCell;
import net.sf.jasperreports.engine.design.JRDesignElementGroup;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.table.model.column.MCell;
import com.jaspersoft.studio.model.MElementGroup;

public class CreateElementGroupCommand extends Command {
	private MElementGroup srcNode;
	private JRDesignElementGroup jrElement;

	private DesignCell jrCell;

	private Point location;

	private int index;

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
	public CreateElementGroupCommand(MCell destNode, MElementGroup srcNode,
			int index) {
		super();
		this.jrElement = (JRDesignElementGroup) srcNode.getValue();
		this.jrCell = destNode.getCell();
		this.index = index;
		this.srcNode = srcNode;
	}

	/**
	 * Creates the object.
	 */
	protected void createObject() {
		if (jrElement == null) {
			jrElement = srcNode.createJRElementGroup();

			if (jrElement != null) {
				if (location == null)
					location = new Point(0, 0);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		createObject();
		if (jrElement != null) {
			if (index >= 0 && index <= jrCell.getChildren().size())
				jrCell.addElementGroup(index, jrElement);
			else
				jrCell.addElementGroup(jrElement);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		if (jrCell == null || jrElement == null)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		jrCell.removeElementGroup(jrElement);
	}

}
