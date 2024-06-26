/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.model.title.command;

import net.sf.jasperreports.crosstabs.design.DesignCrosstabColumnCell;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.crosstab.model.MCrosstab;
import com.jaspersoft.studio.components.crosstab.model.title.MTitleCell;

/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class DeleteTitleCommand extends Command {

	private JRDesignCrosstab jrCrosstab;
	private DesignCrosstabColumnCell jrCell;

	/**
	 * Instantiates a new delete parameter command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 */
	public DeleteTitleCommand(MCrosstab destNode, MTitleCell srcNode) {
		super();
		this.jrCrosstab = (JRDesignCrosstab) destNode.getValue();
		if (srcNode != null && srcNode.getValue() != null)
			this.jrCell = (DesignCrosstabColumnCell) srcNode.getCrosstabColumnCell();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		jrCrosstab.setTitleCell(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		if (jrCrosstab == null || jrCell == null)
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
		jrCrosstab.setTitleCell(jrCell);
	}

}
