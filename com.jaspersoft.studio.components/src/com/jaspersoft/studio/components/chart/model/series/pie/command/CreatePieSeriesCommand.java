/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.series.pie.command;

import net.sf.jasperreports.charts.design.JRDesignPieDataset;
import net.sf.jasperreports.charts.design.JRDesignPieSeries;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.chart.model.dataset.MChartDataset;
import com.jaspersoft.studio.components.chart.model.series.pie.MPieSeries;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.series.PieSerie;

/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class CreatePieSeriesCommand extends Command {

	private JRDesignPieSeries jrElement;
	private JRDesignPieDataset jrDataset;

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
	public CreatePieSeriesCommand(MChartDataset destNode, MPieSeries srcNode,
			int newIndex) {
		super();
		this.jrElement = (JRDesignPieSeries) srcNode.getValue();
		this.jrDataset = (JRDesignPieDataset) destNode.getValue();
		this.index = newIndex;
	}

	/**
	 * Creates the object.
	 */
	protected void createObject() {
		if (jrElement == null) {
			// here put a wizard
			jrElement = new PieSerie().createSerie();
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
				jrDataset.addPieSeries(index, jrElement);
			else
				jrDataset.addPieSeries(jrElement);
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
		jrDataset.removePieSeries(jrElement);
	}

}
