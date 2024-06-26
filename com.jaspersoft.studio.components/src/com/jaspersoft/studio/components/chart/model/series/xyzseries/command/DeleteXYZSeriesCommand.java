/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.series.xyzseries.command;

import net.sf.jasperreports.charts.design.JRDesignXyzDataset;
import net.sf.jasperreports.charts.design.JRDesignXyzSeries;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.model.dataset.MChartDataset;
import com.jaspersoft.studio.components.chart.model.series.xyzseries.MXYZSeries;
/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class DeleteXYZSeriesCommand extends Command {

	private JRDesignXyzDataset jrGroup;
	private JRDesignXyzSeries jrElement;

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
	public DeleteXYZSeriesCommand(MChartDataset destNode, MXYZSeries srcNode) {
		super();
		this.jrElement = (JRDesignXyzSeries) srcNode.getValue();
		this.jrGroup = (JRDesignXyzDataset) destNode.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		oldIndex = jrGroup.getSeriesList().indexOf(jrElement);

		jrGroup.removeXyzSeries(jrElement);
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
		jrGroup.removeXyzSeries(jrElement);
		if (oldIndex >= 0 && oldIndex < jrGroup.getSeriesList().size())
			jrGroup.addXyzSeries(oldIndex, jrElement);
		else
			jrGroup.addXyzSeries(jrElement);
	}
}
