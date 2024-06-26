/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.property.section.theme;

import net.sf.jasperreports.chartthemes.simple.LegendSettings;
import net.sf.jasperreports.chartthemes.simple.TitleSettings;

import com.jaspersoft.studio.components.chart.model.theme.MLegendSettings;
import com.jaspersoft.studio.components.chart.model.theme.MTitleSettings;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.property.section.text.FontSection;

public class CTFontSection extends FontSection {
	@Override
	protected APropertyNode getModelFromEditPart(Object item) {
		APropertyNode md = super.getModelFromEditPart(item);
		if (md instanceof MTitleSettings)
			return (APropertyNode) md.getPropertyValue(TitleSettings.PROPERTY_font);
		if (md instanceof MLegendSettings)
			return (APropertyNode) md.getPropertyValue(LegendSettings.PROPERTY_font);
		return md;
	}
}
