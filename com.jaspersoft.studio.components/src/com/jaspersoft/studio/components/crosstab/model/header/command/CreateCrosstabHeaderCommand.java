/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.model.header.command;

import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.crosstab.model.MCrosstab;
import com.jaspersoft.studio.components.crosstab.model.header.MCrosstabHeader;

/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class CreateCrosstabHeaderCommand extends Command {

	private JRDesignCellContents jrCell;
	private JRDesignCrosstab jrCrosstab;

	/**
	 * Instantiates a new creates the parameter command.
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
	public CreateCrosstabHeaderCommand(MCrosstab destNode,
			MCrosstabHeader srcNode) {
		super();
		this.jrCrosstab = destNode.getValue();
		if (srcNode != null && srcNode.getValue() != null)
			this.jrCell = (JRDesignCellContents) srcNode.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (jrCell == null) {
			this.jrCell = new JRDesignCellContents();
		}
		if (jrCell != null) {
			jrCrosstab.setHeaderCell(jrCell);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		jrCrosstab.setHeaderCell(null);
	}
}
