/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.series.gantt.command;

import net.sf.jasperreports.charts.design.JRDesignGanttDataset;
import net.sf.jasperreports.charts.design.JRDesignGanttSeries;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.model.dataset.MChartDataset;
import com.jaspersoft.studio.components.chart.model.series.gantt.MGanttSeries;
/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class DeleteGanttSeriesCommand extends Command {

	private JRDesignGanttDataset jrGroup;
	private JRDesignGanttSeries jrElement;

	/** The element position. */
	private int oldIndex = 0;

	/**
	 * Instantiates a new delete element command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 */
	public DeleteGanttSeriesCommand(MChartDataset destNode, MGanttSeries srcNode) {
		super();
		this.jrElement = (JRDesignGanttSeries) srcNode.getValue();
		this.jrGroup = (JRDesignGanttDataset) destNode.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		oldIndex = jrGroup.getSeriesList().indexOf(jrElement);

		jrGroup.removeGanttSeries(jrElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		if (jrGroup == null || jrElement == null)
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
		if (oldIndex >= 0 && oldIndex < jrGroup.getSeriesList().size())
			jrGroup.addGanttSeries(oldIndex, jrElement);
		else
			jrGroup.addGanttSeries(jrElement);
	}
}
