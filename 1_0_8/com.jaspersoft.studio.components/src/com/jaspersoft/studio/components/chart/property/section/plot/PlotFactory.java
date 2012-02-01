/*
 * JasperReports - Free Java Reporting Library. Copyright (C) 2001 - 2011 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program is part of JasperReports.
 * 
 * JasperReports is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * JasperReports is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with JasperReports. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.components.chart.property.section.plot;

import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.components.chart.model.plot.MAreaPlot;
import com.jaspersoft.studio.components.chart.model.plot.MBar3DPlot;
import com.jaspersoft.studio.components.chart.model.plot.MBarPlot;
import com.jaspersoft.studio.components.chart.model.plot.MBubblePlot;
import com.jaspersoft.studio.components.chart.model.plot.MCandlestickPlot;
import com.jaspersoft.studio.components.chart.model.plot.MChartPlot;
import com.jaspersoft.studio.components.chart.model.plot.MHighLowPlot;
import com.jaspersoft.studio.components.chart.model.plot.MLinePlot;
import com.jaspersoft.studio.components.chart.model.plot.MMeterPlot;
import com.jaspersoft.studio.components.chart.model.plot.MMultiAxisPlot;
import com.jaspersoft.studio.components.chart.model.plot.MPie3DPlot;
import com.jaspersoft.studio.components.chart.model.plot.MPiePlot;
import com.jaspersoft.studio.components.chart.model.plot.MScatterPlot;
import com.jaspersoft.studio.components.chart.model.plot.MThermometerPlot;
import com.jaspersoft.studio.components.chart.model.plot.MTimeSeriesPlot;
import com.jaspersoft.studio.property.section.AbstractSection;

public class PlotFactory {
	public static APlot getPlot(MChartPlot plot, Composite parent,
			AbstractSection section) {
		if (plot instanceof MAreaPlot)
			return new AreaPlot(parent, section);
		if (plot instanceof MBar3DPlot)
			return new Bar3dPlot(parent, section);
		if (plot instanceof MBarPlot)
			return new BarPlot(parent, section);
		if (plot instanceof MBubblePlot)
			return new BubblePlot(parent, section);
		if (plot instanceof MCandlestickPlot)
			return new CandlestickPlot(parent, section);
		if (plot instanceof MHighLowPlot)
			return new HighLowPlot(parent, section);
		if (plot instanceof MLinePlot)
			return new LinePlot(parent, section);
		if (plot instanceof MMeterPlot)
			return new MeterPlot(parent, section);
		if (plot instanceof MMultiAxisPlot)
			return new MultiAxisPlot(parent, section);
		if (plot instanceof MPie3DPlot)
			return new Pie3dPlot(parent, section);
		if (plot instanceof MPiePlot)
			return new PiePlot(parent, section);
		if (plot instanceof MScatterPlot)
			return new ScatterPlot(parent, section);
		if (plot instanceof MThermometerPlot)
			return new ThermometerPlot(parent, section);
		if (plot instanceof MTimeSeriesPlot)
			return new TimeSeriesPlot(parent, section);

		return null;
	}
}
