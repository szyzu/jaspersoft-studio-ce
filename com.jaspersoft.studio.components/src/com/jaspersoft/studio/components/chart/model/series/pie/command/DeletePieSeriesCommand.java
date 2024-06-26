/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.series.pie.command;

import net.sf.jasperreports.charts.design.JRDesignPieDataset;
import net.sf.jasperreports.charts.design.JRDesignPieSeries;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.model.dataset.MChartDataset;
import com.jaspersoft.studio.components.chart.model.series.pie.MPieSeries;
/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class DeletePieSeriesCommand extends Command {

	private JRDesignPieDataset jrGroup;
	private JRDesignPieSeries jrElement;

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
	public DeletePieSeriesCommand(MChartDataset destNode, MPieSeries srcNode) {
		super();
		this.jrElement = (JRDesignPieSeries) srcNode.getValue();
		this.jrGroup = (JRDesignPieDataset) destNode.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		oldIndex = jrGroup.getSeriesList().indexOf(jrElement);

		jrGroup.removePieSeries(jrElement);
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
			jrGroup.addPieSeries(oldIndex, jrElement);
		else
			jrGroup.addPieSeries(jrElement);
	}
}
