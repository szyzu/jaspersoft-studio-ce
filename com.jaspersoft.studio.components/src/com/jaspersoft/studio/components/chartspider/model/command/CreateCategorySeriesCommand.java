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
public class CreateCategorySeriesCommand extends Command {

	private JRDesignCategorySeries jrElement;
	private StandardSpiderDataset jrDataset;

	private int index;

	/**
	 * Instantiates a new creates the element command.
	 * 
	 * @param destNode
	 *            the dest node
	 * @param srcNode
	 *            the src node
	 * @param index
	 *            the index
	 */
	public CreateCategorySeriesCommand(MChartSpiderDataset destNode,
			MCategorySeries srcNode, int newIndex) {
		super();
		this.jrElement = (JRDesignCategorySeries) srcNode.getValue();
		this.jrDataset = (StandardSpiderDataset) destNode.getValue();
		this.index = newIndex;
	}

	/**
	 * Creates the object.
	 */
	protected void createObject() {
		if (jrElement == null) {
			// here put a wizard
			jrElement = new JRDesignCategorySeries();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		createObject();
		if (jrElement != null) {
			if (index >= 0 && index < jrDataset.getSeriesList().size())
				jrDataset.addCategorySeries(index, jrElement);
			else
				jrDataset.addCategorySeries(jrElement);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		if (jrDataset == null || jrElement == null)
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
		jrDataset.removeCategorySeries(jrElement);
	}

}
