/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.property.descriptor.seriescolor.dialog;

import java.util.Collection;
import java.util.SortedSet;

import net.sf.jasperreports.engine.base.JRBaseChartPlot.JRBaseSeriesColor;

import org.eclipse.jface.wizard.Wizard;

import com.jaspersoft.studio.components.chart.messages.Messages;

public class SeriesColorEditor extends Wizard {
	private Collection<JRBaseSeriesColor> value;
	private SeriesColorPage page0;

	public Collection<?> getValue() {
		if (page0 != null)
			return page0.getValue();
		return value;
	}

	public void setValue(SortedSet<JRBaseSeriesColor> value) {
		if (page0 != null)
			page0.setValue(value);
		this.value = value;
	}

	public SeriesColorEditor() {
		super();
		setWindowTitle(Messages.common_series_colors);
		setNeedsProgressMonitor(false);
	}

	@Override
	public void addPages() {
		page0 = new SeriesColorPage("SeriesColor"); //$NON-NLS-1$
		page0.setValue((SortedSet<JRBaseSeriesColor>) value);
		addPage(page0);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
