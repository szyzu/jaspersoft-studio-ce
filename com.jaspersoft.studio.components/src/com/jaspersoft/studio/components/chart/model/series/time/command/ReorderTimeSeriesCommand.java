/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.series.time.command;

import net.sf.jasperreports.charts.design.JRDesignTimeSeries;
import net.sf.jasperreports.charts.design.JRDesignTimeSeriesDataset;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.components.chart.model.dataset.MChartDataset;
import com.jaspersoft.studio.components.chart.model.series.time.MTimeSeries;

/*
 * The Class ReorderElementCommand.
 */
public class ReorderTimeSeriesCommand extends Command {

	/** The new index. */
	private int oldIndex, newIndex;

	private JRDesignTimeSeries jrElement;
	private JRDesignTimeSeriesDataset jrGroup;

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
	public ReorderTimeSeriesCommand(MTimeSeries child, MChartDataset parent,
			int newIndex) {
		super(Messages.common_reorder_elements);
		this.newIndex = Math.max(0, newIndex);
		this.jrElement = (JRDesignTimeSeries) child.getValue();
		this.jrGroup = (JRDesignTimeSeriesDataset) parent.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		oldIndex = jrGroup.getSeriesList().indexOf(jrElement);

		jrGroup.removeTimeSeries(jrElement);
		if (newIndex >= 0 && newIndex < jrGroup.getSeriesList().size())
			jrGroup.addTimeSeries(newIndex, jrElement);
		else
			jrGroup.addTimeSeries(jrElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		jrGroup.removeTimeSeries(jrElement);
		if (oldIndex >= 0 && oldIndex < jrGroup.getSeriesList().size())
			jrGroup.addTimeSeries(oldIndex, jrElement);
		else
			jrGroup.addTimeSeries(jrElement);
	}

}
