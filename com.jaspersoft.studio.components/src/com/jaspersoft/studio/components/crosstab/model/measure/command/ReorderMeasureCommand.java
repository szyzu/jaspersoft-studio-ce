/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.model.measure.command;

import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabMeasure;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.crosstab.messages.Messages;
import com.jaspersoft.studio.components.crosstab.model.measure.MMeasure;
import com.jaspersoft.studio.components.crosstab.model.measure.MMeasures;
/*
 * The Class ReorderParameterCommand.
 */
public class ReorderMeasureCommand extends Command {

	/** The new index. */
	private int oldIndex, newIndex;

	/** The jr parameter. */
	private JRDesignCrosstabMeasure jrMeasure;

	/** The jr dataset. */
	private JRDesignCrosstab jrCrosstab;

	/**
	 * Instantiates a new reorder parameter command.
	 * 
	 * @param child
	 *          the child
	 * @param parent
	 *          the parent
	 * @param newIndex
	 *          the new index
	 */
	public ReorderMeasureCommand(MMeasure child, MMeasures parent, int newIndex) {
		super(Messages.common_reorder_elements);

		this.newIndex = Math.max(0, newIndex);
		this.jrCrosstab = (JRDesignCrosstab) parent.getValue();
		this.jrMeasure = (JRDesignCrosstabMeasure) child.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		oldIndex = jrCrosstab.getMesuresList().indexOf(jrMeasure);

		jrCrosstab.getMesuresList().remove(jrMeasure);
		jrCrosstab.getEventSupport().fireCollectionElementRemovedEvent(JRDesignCrosstab.PROPERTY_MEASURES, jrMeasure,
				oldIndex);
		if (newIndex >= 0 && newIndex < jrCrosstab.getMesuresList().size())
			jrCrosstab.getMesuresList().add(newIndex, jrMeasure);
		else
			jrCrosstab.getMesuresList().add(jrMeasure);

		jrCrosstab.getEventSupport().fireCollectionElementAddedEvent(JRDesignCrosstab.PROPERTY_MEASURES, jrMeasure,
				newIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		jrCrosstab.getMesuresList().remove(jrMeasure);
		jrCrosstab.getEventSupport().fireCollectionElementRemovedEvent(JRDesignCrosstab.PROPERTY_MEASURES, jrMeasure,
				newIndex);
		if (oldIndex >= 0 && oldIndex < jrCrosstab.getMesuresList().size())
			jrCrosstab.getMesuresList().add(oldIndex, jrMeasure);
		else
			jrCrosstab.getMesuresList().add(jrMeasure);
		jrCrosstab.getEventSupport().fireCollectionElementAddedEvent(JRDesignCrosstab.PROPERTY_MEASURES, jrMeasure,
				oldIndex);
	}
}
