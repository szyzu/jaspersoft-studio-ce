/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.sortfield.command;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignSortField;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.sortfield.MSortField;
import com.jaspersoft.studio.model.sortfield.MSortFields;
/*
 * The Class ReorderFieldCommand.
 */
public class ReorderSortFieldCommand extends Command {

	/** The new index. */
	private int oldIndex, newIndex;

	/** The jr field. */
	private JRDesignSortField jrField;

	/** The jr dataset. */
	private JRDesignDataset jrDataset;

	/**
	 * Instantiates a new reorder field command.
	 * 
	 * @param child
	 *          the child
	 * @param parent
	 *          the parent
	 * @param newIndex
	 *          the new index
	 */
	public ReorderSortFieldCommand(MSortField child, MSortFields parent, int newIndex) {
		super(Messages.common_reorder_elements);

		this.newIndex = Math.max(0, newIndex);
		this.jrDataset = (JRDesignDataset) parent.getValue();
		this.jrField = (JRDesignSortField) child.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		try {
			oldIndex = jrDataset.getSortFieldsList().indexOf(jrField);
			jrDataset.removeSortField(jrField);
			if (newIndex < 0 || newIndex > jrDataset.getSortFieldsList().size())
				jrDataset.addSortField(jrField);
			else
				jrDataset.addSortField(newIndex, jrField);
		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		try {
			jrDataset.removeSortField(jrField);
			if (oldIndex < 0 || oldIndex > jrDataset.getSortFieldsList().size())
				jrDataset.addSortField(jrField);
			else
				jrDataset.addSortField(oldIndex, jrField);
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
}
