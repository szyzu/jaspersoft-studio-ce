/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chartspider.model.command;

import net.sf.jasperreports.charts.design.JRDesignCategorySeries;
import net.sf.jasperreports.components.spiderchart.StandardSpiderDataset;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.model.series.category.MCategorySeries;
import com.jaspersoft.studio.components.chartspider.model.MChartSpiderDataset;
/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class DeleteCategorySeriesCommand extends Command {

	private StandardSpiderDataset jrGroup;
	private JRDesignCategorySeries jrElement;

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
	public DeleteCategorySeriesCommand(MChartSpiderDataset destNode, MCategorySeries srcNode) {
		super();
		this.jrElement = (JRDesignCategorySeries) srcNode.getValue();
		this.jrGroup = (StandardSpiderDataset) destNode.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		oldIndex = jrGroup.getSeriesList().indexOf(jrElement);

		jrGroup.removeCategorySeries(jrElement);
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
			jrGroup.addCategorySeries(oldIndex, jrElement);
		else
			jrGroup.addCategorySeries(jrElement);
	}
}
