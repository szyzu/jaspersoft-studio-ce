/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.series.time.command;

import net.sf.jasperreports.charts.design.JRDesignTimeSeries;
import net.sf.jasperreports.charts.design.JRDesignTimeSeriesDataset;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.model.dataset.MChartDataset;
import com.jaspersoft.studio.components.chart.model.series.time.MTimeSeries;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.series.TimeSerie;

/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class CreateTimeSeriesCommand extends Command {

	private JRDesignTimeSeries jrElement;
	private JRDesignTimeSeriesDataset jrDataset;

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
	public CreateTimeSeriesCommand(MChartDataset destNode, MTimeSeries srcNode,
			int newIndex) {
		super();
		this.jrElement = (JRDesignTimeSeries) srcNode.getValue();
		this.jrDataset = (JRDesignTimeSeriesDataset) destNode.getValue();
		this.index = newIndex;
	}

	/**
	 * Creates the object.
	 */
	protected void createObject() {
		if (jrElement == null) {
			// here put a wizard
			jrElement = new TimeSerie().createSerie();
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
				jrDataset.addTimeSeries(index, jrElement);
			else
				jrDataset.addTimeSeries(jrElement);
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
		jrDataset.removeTimeSeries(jrElement);
	}

}
