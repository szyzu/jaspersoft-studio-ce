/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.series.xyzseries.command;

import net.sf.jasperreports.charts.design.JRDesignXyzDataset;
import net.sf.jasperreports.charts.design.JRDesignXyzSeries;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.model.dataset.MChartDataset;
import com.jaspersoft.studio.components.chart.model.series.xyzseries.MXYZSeries;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.series.XyzSerie;

/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class CreateXYZSeriesCommand extends Command {

	private JRDesignXyzSeries jrElement;
	private JRDesignXyzDataset jrDataset;

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
	public CreateXYZSeriesCommand(MChartDataset destNode, MXYZSeries srcNode,
			int newIndex) {
		super();
		this.jrElement = (JRDesignXyzSeries) srcNode.getValue();
		this.jrDataset = (JRDesignXyzDataset) destNode.getValue();
		this.index = newIndex;
	}

	/**
	 * Creates the object.
	 */
	protected void createObject() {
		if (jrElement == null) {
			// here put a wizard
			jrElement = new XyzSerie().createSerie();
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
				jrDataset.addXyzSeries(index, jrElement);
			else
				jrDataset.addXyzSeries(jrElement);
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
		jrDataset.removeXyzSeries(jrElement);
	}

}
