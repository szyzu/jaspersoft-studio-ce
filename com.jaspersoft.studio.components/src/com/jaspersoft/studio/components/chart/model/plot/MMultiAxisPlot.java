/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.plot;

import com.jaspersoft.studio.components.chart.messages.Messages;

import net.sf.jasperreports.charts.JRMultiAxisPlot;
import net.sf.jasperreports.engine.JRConstants;

public class MMultiAxisPlot extends MChartPlot {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MMultiAxisPlot(JRMultiAxisPlot value) {
		super(value);
	}

	@Override
	public String getDisplayText() {
		return Messages.MMultiAxisPlot_multi_axis_plot;
	}
}
