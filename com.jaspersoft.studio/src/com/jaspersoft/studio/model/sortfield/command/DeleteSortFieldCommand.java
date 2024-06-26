/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.sortfield.command;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignSortField;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.model.sortfield.MSortField;
import com.jaspersoft.studio.model.sortfield.MSortFields;

/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class DeleteSortFieldCommand extends Command {

	/** The jr dataset. */
	private JRDesignDataset jrDataset;

	/** The jr field. */
	private JRSortField jrField;

	/** The element position. */
	private int elementPosition = 0;

	/**
	 * Instantiates a new delete field command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 */
	public DeleteSortFieldCommand(MSortFields destNode, MSortField srcNode) {
		super();
		this.jrDataset = (JRDesignDataset) destNode.getValue();
		this.jrField = (JRDesignSortField) srcNode.getValue();
	}

	public DeleteSortFieldCommand(JRDesignDataset destNode, JRSortField srcNode) {
		super();
		this.jrDataset = destNode;
		this.jrField = srcNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		elementPosition = jrDataset.getSortFieldsList().indexOf(jrField);
		jrDataset.removeSortField(jrField);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		if (jrDataset == null || jrField == null)
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
		try {
			if (elementPosition < 0 || elementPosition > jrDataset.getSortFieldsList().size())
				jrDataset.addSortField(jrField);
			else
				jrDataset.addSortField(elementPosition, jrField);
		} catch (JRException e) {
			e.printStackTrace();
		}

	}
}
