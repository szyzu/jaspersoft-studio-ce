/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.dataset.command;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.model.dataset.MDataset;

/*
 * /* The Class ReorderDatasetCommand.
 */
public class ReorderDatasetCommand extends Command {

	/** The new index. */
	private int oldIndex, newIndex;

	/** The jr field. */
	private JRDesignDataset jrField;

	/** The jr design. */
	private JasperDesign jrDesign;

	/**
	 * Instantiates a new reorder dataset command.
	 * 
	 * @param child
	 *          the child
	 * @param parent
	 *          the parent
	 * @param newIndex
	 *          the new index
	 */
	public ReorderDatasetCommand(MReport child, MDataset parent, int newIndex) {
		super(Messages.common_reorder_elements);

		this.newIndex = Math.max(0, newIndex);
		this.jrDesign = child.getJasperDesign();
		this.jrField = parent.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		try {
			oldIndex = jrDesign.getDatasetsList().indexOf(jrField);
			jrDesign.removeDataset(jrField);
			if (newIndex < 0 || newIndex > jrDesign.getDatasetsList().size())
				jrDesign.addDataset(jrField);
			else
				jrDesign.addDataset(newIndex, jrField);
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
			jrDesign.removeDataset(jrField);
			if (oldIndex < 0 || oldIndex > jrDesign.getDatasetsList().size())
				jrDesign.addDataset(jrField);
			else
				jrDesign.addDataset(oldIndex, jrField);
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
}
