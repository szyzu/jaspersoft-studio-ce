/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.model.cell.command;

import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabColumnGroup;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.crosstab.model.columngroup.MColumnCrosstabHeaderCell;
import com.jaspersoft.studio.components.crosstab.model.columngroup.MColumnGroup;

/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class DeleteColumnCrosstabHeaderCommand extends Command {

	private JRDesignCrosstabColumnGroup jrColGroup;
	private JRDesignCellContents jrCell;

	/**
	 * Instantiates a new delete parameter command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 */
	public DeleteColumnCrosstabHeaderCommand(MColumnGroup destNode, MColumnCrosstabHeaderCell srcNode) {
		super();
		this.jrColGroup = destNode.getValue();
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
		jrColGroup.setCrosstabHeader(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		if (jrColGroup == null || jrCell == null)
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
		jrColGroup.setCrosstabHeader(jrCell);
	}

}
