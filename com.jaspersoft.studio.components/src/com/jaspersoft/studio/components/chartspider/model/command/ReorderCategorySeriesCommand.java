/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chartspider.model.command;

import net.sf.jasperreports.charts.design.JRDesignCategorySeries;
import net.sf.jasperreports.components.spiderchart.StandardSpiderDataset;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.components.chart.model.series.category.MCategorySeries;
import com.jaspersoft.studio.components.chartspider.model.MChartSpiderDataset;

/*
 * The Class ReorderElementCommand.
 */
public class ReorderCategorySeriesCommand extends Command {

	/** The new index. */
	private int oldIndex, newIndex;

	private JRDesignCategorySeries jrElement;
	private StandardSpiderDataset jrGroup;

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
	public ReorderCategorySeriesCommand(MCategorySeries child,
			MChartSpiderDataset parent, int newIndex) {
		super(Messages.common_reorder_elements);
		this.newIndex = Math.max(0, newIndex);
		this.jrElement = (JRDesignCategorySeries) child.getValue();
		this.jrGroup = (StandardSpiderDataset) parent.getValue();
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

		if (newIndex >= 0 && newIndex < jrGroup.getSeriesList().size())
			jrGroup.addCategorySeries(newIndex, jrElement);
		else
			jrGroup.addCategorySeries(jrElement);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		jrGroup.removeCategorySeries(jrElement);

		if (oldIndex >= 0 && oldIndex < jrGroup.getSeriesList().size())
			jrGroup.addCategorySeries(oldIndex, jrElement);
		else
			jrGroup.addCategorySeries(jrElement);
	}

}
