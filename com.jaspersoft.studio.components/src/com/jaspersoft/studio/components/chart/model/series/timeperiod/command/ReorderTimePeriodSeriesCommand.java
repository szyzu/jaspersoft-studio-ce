/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.series.timeperiod.command;

import net.sf.jasperreports.charts.design.JRDesignTimePeriodDataset;
import net.sf.jasperreports.charts.design.JRDesignTimePeriodSeries;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.components.chart.model.dataset.MChartDataset;
import com.jaspersoft.studio.components.chart.model.series.timeperiod.MTimePeriodSeries;

/*
 * The Class ReorderElementCommand.
 */
public class ReorderTimePeriodSeriesCommand extends Command {

	/** The new index. */
	private int oldIndex, newIndex;

	private JRDesignTimePeriodSeries jrElement;
	private JRDesignTimePeriodDataset jrGroup;

	/**
	 * Instantiates a new reorder element command.
	 * 
	 * @param child
	 *            the child
	 * @param parent
	 *            the parent
	 * @param newIndex
	 *            the new index
	 */
	public ReorderTimePeriodSeriesCommand(MTimePeriodSeries child,
			MChartDataset parent, int newIndex) {
		super(Messages.common_reorder_elements);
		this.newIndex = Math.max(0, newIndex);
		this.jrElement = (JRDesignTimePeriodSeries) child.getValue();
		this.jrGroup = (JRDesignTimePeriodDataset) parent.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		oldIndex = jrGroup.getSeriesList().indexOf(jrElement);

		if (newIndex >= 0 && newIndex < jrGroup.getSeriesList().size())
			jrGroup.addTimePeriodSeries(newIndex, jrElement);
		else
			jrGroup.addTimePeriodSeries(jrElement);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		if (oldIndex >= 0 && oldIndex < jrGroup.getSeriesList().size())
			jrGroup.addTimePeriodSeries(oldIndex, jrElement);
		else
			jrGroup.addTimePeriodSeries(jrElement);
	}

}
