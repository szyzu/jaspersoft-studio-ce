/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.theme;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.chartthemes.simple.AxisSettings;
import net.sf.jasperreports.chartthemes.simple.PaintProvider;
import net.sf.jasperreports.engine.JRConstants;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jfree.ui.RectangleInsets;

import com.jaspersoft.studio.components.chart.model.enums.JFreeChartAxisLocationEnum;
import com.jaspersoft.studio.components.chart.model.theme.paintprovider.PaintProviderPropertyDescriptor;
import com.jaspersoft.studio.components.chart.model.theme.util.PadUtil;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.DefaultValue;
import com.jaspersoft.studio.model.text.MFont;
import com.jaspersoft.studio.model.text.MFontUtil;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.checkbox.CheckBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.text.FontPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.DegreePropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.DoublePropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.FloatPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.IntegerPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.NamedEnumPropertyDescriptor;

public class MAxisSettings extends APropertyNode {
	
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROP_LABEL = "LABEL";
	
	private static NamedEnumPropertyDescriptor<JFreeChartAxisLocationEnum> location;
	
	private String displayText;
	
	private IPropertyDescriptor[] descriptors;
	
	private MFont lFont;
	
	private MFont tlFont;

	public MAxisSettings(MChartThemeSettings parent, AxisSettings as,
			String displayText) {
		super(parent, -1);
		setValue(as);
		this.displayText = displayText;
	}

	@Override
	public AxisSettings getValue() {
		return (AxisSettings) super.getValue();
	}

	@Override
	public ImageDescriptor getImagePath() {
		return null;
	}

	@Override
	public String getDisplayText() {
		return displayText;
	}

	@Override
	public IPropertyDescriptor[] getDescriptors() {
		return descriptors;
	}

	@Override
	public void setDescriptors(IPropertyDescriptor[] descriptors1) {
		descriptors = descriptors1;
	}

	/**
	 * Creates the property descriptors.
	 * 
	 * @param desc
	 *            the desc
	 */
	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		PropertyDescriptor pd = new CheckBoxPropertyDescriptor(
				AxisSettings.PROPERTY_visible, "Visible");
		pd.setDescription("Visible");
		desc.add(pd);

		pd = new CheckBoxPropertyDescriptor(AxisSettings.PROPERTY_lineVisible,
				"Visible");
		pd.setDescription("Line Visible");
		pd.setCategory("Line");
		desc.add(pd);

		pd = new CheckBoxPropertyDescriptor(AxisSettings.PROPERTY_labelVisible,
				"Visible");
		pd.setDescription("Label Visible");
		pd.setCategory("Label");
		desc.add(pd);

		pd = new CheckBoxPropertyDescriptor(
				AxisSettings.PROPERTY_tickLabelsVisible, "Visible");
		pd.setDescription("Tick Labels Visible");
		pd.setCategory("Tick Labels");
		desc.add(pd);

		pd = new CheckBoxPropertyDescriptor(
				AxisSettings.PROPERTY_tickMarksVisible, "Visible");
		pd.setDescription("Tick Marks Visible");
		pd.setCategory("Tick Mark");
		desc.add(pd);

		pd = new CheckBoxPropertyDescriptor(
				AxisSettings.PROPERTY_axisIntegerUnit, "Axis Integer Unit");
		pd.setDescription("Axis Integer Unit");
		desc.add(pd);

		pd = new FontPropertyDescriptor(AxisSettings.PROPERTY_labelFont, "Font");
		pd.setDescription("Label Font");
		pd.setCategory("Label");
		desc.add(pd);

		pd = new FontPropertyDescriptor(AxisSettings.PROPERTY_tickLabelFont,
				"Font");
		pd.setDescription("Tick Label Font");
		pd.setCategory("Tick Labels");
		desc.add(pd);

		pd = new FloatPropertyDescriptor(
				AxisSettings.PROPERTY_tickMarksInsideLength, "Inside Lenght");
		pd.setDescription("Tick Marks Inside Lenght");
		pd.setCategory("Tick Mark");
		desc.add(pd);

		pd = new FloatPropertyDescriptor(
				AxisSettings.PROPERTY_tickMarksOutsideLength, "Outside Lenght");
		pd.setDescription("Tick Marks Outside Lenght");
		pd.setCategory("Tick Mark");
		desc.add(pd);

		pd = new DegreePropertyDescriptor(AxisSettings.PROPERTY_labelAngle,
				"Angle");
		pd.setDescription("Label Angle");
		pd.setCategory("Label");
		desc.add(pd);

		pd = new DoublePropertyDescriptor(AxisSettings.PROPERTY_tickInterval,
				"Interval");
		pd.setDescription("Tick Interval");
		pd.setCategory("Tick");
		desc.add(pd);

		pd = new IntegerPropertyDescriptor(AxisSettings.PROPERTY_tickCount,
				"Count");
		pd.setDescription("Tick Count");
		pd.setCategory("Tick");
		desc.add(pd);

		PadUtil.createPropertyDescriptors(desc, PROP_LABEL, "Label Padding");

		PadUtil.createPropertyDescriptors(desc, "Tick Label Padding");

		pd = new PaintProviderPropertyDescriptor(
				AxisSettings.PROPERTY_linePaint, "Paint");
		pd.setDescription("Line paint");
		pd.setCategory("Line");
		desc.add(pd);

		pd = new PaintProviderPropertyDescriptor(
				AxisSettings.PROPERTY_labelPaint, "Paint");
		pd.setDescription("Label paint");
		pd.setCategory("Label");
		desc.add(pd);

		pd = new PaintProviderPropertyDescriptor(
				AxisSettings.PROPERTY_tickLabelPaint, "Paint");
		pd.setDescription("Tick Label paint");
		pd.setCategory("Tick Labels");
		desc.add(pd);

		pd = new PaintProviderPropertyDescriptor(
				AxisSettings.PROPERTY_tickMarksPaint, "Paint");
		pd.setDescription("Tick Mark paint");
		pd.setCategory("Tick Mark");
		desc.add(pd);

		location = new NamedEnumPropertyDescriptor<JFreeChartAxisLocationEnum>(
				AxisSettings.PROPERTY_location, "Location",
				JFreeChartAxisLocationEnum.BOTTOM_OR_LEFT, NullEnum.NOTNULL);
		location.setDescription("Location");
		desc.add(location);

		setHelpPrefix(
				desc,
				"net.sf.jasperreports.doc/docs/sample.reference/chartthemes/index.html#chartthemes");
	}
	
	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();
		
		defaultsMap.put(AxisSettings.PROPERTY_linePaint, new DefaultValue(null, true));
		defaultsMap.put(AxisSettings.PROPERTY_labelPaint, new DefaultValue(null, true));
		defaultsMap.put(AxisSettings.PROPERTY_tickLabelPaint, new DefaultValue(null, true));
		defaultsMap.put(AxisSettings.PROPERTY_tickMarksPaint, new DefaultValue(null, true));

		defaultsMap.put(AxisSettings.PROPERTY_visible, new DefaultValue(Boolean.TRUE, false));
		defaultsMap.put(AxisSettings.PROPERTY_lineVisible, new DefaultValue(Boolean.TRUE, false));
		defaultsMap.put(AxisSettings.PROPERTY_labelVisible, new DefaultValue(Boolean.TRUE, false));
		defaultsMap.put(AxisSettings.PROPERTY_tickLabelsVisible, new DefaultValue(Boolean.TRUE, false));
		defaultsMap.put(AxisSettings.PROPERTY_tickMarksVisible, new DefaultValue(Boolean.TRUE, false));
		defaultsMap.put(AxisSettings.PROPERTY_axisIntegerUnit, new DefaultValue(Boolean.TRUE, false));
		
		PadUtil.createDefaults(PROP_LABEL, defaultsMap);
		PadUtil.createDefaults("", defaultsMap);
		
		return defaultsMap;
	}

	@Override
	public Object getPropertyValue(Object id) {
		AxisSettings ts = getValue();
		if (id.equals(AxisSettings.PROPERTY_visible))
			return ts.getVisible();
		if (id.equals(AxisSettings.PROPERTY_lineVisible))
			return ts.getLineVisible();
		if (id.equals(AxisSettings.PROPERTY_labelVisible))
			return ts.getLabelVisible();
		if (id.equals(AxisSettings.PROPERTY_tickLabelsVisible))
			return ts.getTickLabelsVisible();
		if (id.equals(AxisSettings.PROPERTY_tickMarksVisible))
			return ts.getTickMarksVisible();
		if (id.equals(AxisSettings.PROPERTY_axisIntegerUnit))
			return ts.getAxisIntegerUnit();
		if (id.equals(AxisSettings.PROPERTY_labelFont)) {
			lFont = MFontUtil.getMFont(lFont, ts.getLabelFont(), null, this);
			return lFont;
		}
		if (id.equals(AxisSettings.PROPERTY_tickLabelFont)) {
			tlFont = MFontUtil.getMFont(tlFont, ts.getTickLabelFont(), null,
					this);
			return tlFont;
		}
		if (id.equals(AxisSettings.PROPERTY_tickMarksInsideLength))
			return ts.getTickMarksInsideLength();
		if (id.equals(AxisSettings.PROPERTY_tickMarksOutsideLength))
			return ts.getTickMarksOutsideLength();
		if (id.equals(AxisSettings.PROPERTY_labelAngle))
			return ts.getLabelAngle();
		if (id.equals(AxisSettings.PROPERTY_tickInterval))
			return ts.getTickInterval();
		if (id.equals(AxisSettings.PROPERTY_tickCount))
			return ts.getTickCount();
		if (id.equals(AxisSettings.PROPERTY_linePaint))
			return ts.getLinePaint();
		if (id.equals(AxisSettings.PROPERTY_labelPaint))
			return ts.getLabelPaint();
		if (id.equals(AxisSettings.PROPERTY_tickLabelPaint))
			return ts.getTickLabelPaint();
		if (id.equals(AxisSettings.PROPERTY_tickMarksPaint))
			return ts.getTickMarksPaint();
		if (id.equals(AxisSettings.PROPERTY_location))
			return location.getIntValue(JFreeChartAxisLocationEnum.getValue(ts
					.getLocation()));

		Object pad = PadUtil.getPropertyValue(id, ts.getTickLabelInsets());
		if (pad != null)
			return pad;
		pad = PadUtil.getPropertyValue(id, ts.getLabelInsets(), PROP_LABEL);
		if (pad != null)
			return pad;
		return null;
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		AxisSettings ts = getValue();
		if (id.equals(AxisSettings.PROPERTY_visible))
			ts.setVisible((Boolean) value);
		else if (id.equals(AxisSettings.PROPERTY_lineVisible))
			ts.setLineVisible((Boolean) value);
		else if (id.equals(AxisSettings.PROPERTY_labelVisible))
			ts.setLabelVisible((Boolean) value);
		else if (id.equals(AxisSettings.PROPERTY_tickLabelsVisible))
			ts.setTickLabelsVisible((Boolean) value);
		else if (id.equals(AxisSettings.PROPERTY_tickMarksVisible))
			ts.setTickMarksVisible((Boolean) value);
		else if (id.equals(AxisSettings.PROPERTY_axisIntegerUnit))
			ts.setAxisIntegerUnit((Boolean) value);
		else if (id.equals(AxisSettings.PROPERTY_labelFont))
			ts.setLabelFont(MFontUtil.setMFont(value));
		else if (id.equals(AxisSettings.PROPERTY_tickLabelFont))
			ts.setTickLabelFont(MFontUtil.setMFont(value));
		else if (id.equals(AxisSettings.PROPERTY_tickMarksInsideLength))
			ts.setTickMarksInsideLength((Float) value);
		else if (id.equals(AxisSettings.PROPERTY_tickMarksOutsideLength))
			ts.setTickMarksOutsideLength((Float) value);
		else if (id.equals(AxisSettings.PROPERTY_labelAngle))
			ts.setLabelAngle((Double) value);
		else if (id.equals(AxisSettings.PROPERTY_tickInterval))
			ts.setTickInterval((Double) value);
		else if (id.equals(AxisSettings.PROPERTY_tickCount))
			ts.setTickCount((Integer) value);
		else if (id.equals(AxisSettings.PROPERTY_linePaint))
			ts.setLinePaint((PaintProvider) value);
		else if (id.equals(AxisSettings.PROPERTY_labelPaint))
			ts.setLabelPaint((PaintProvider) value);
		else if (id.equals(AxisSettings.PROPERTY_tickLabelPaint))
			ts.setTickLabelPaint((PaintProvider) value);
		else if (id.equals(AxisSettings.PROPERTY_tickMarksPaint))
			ts.setTickMarksPaint((PaintProvider) value);
		else if (id.equals(AxisSettings.PROPERTY_location))
			ts.setLocation(location.getEnumValue(value).getJFreeChartValue());

		RectangleInsets ri = PadUtil.setPropertyValue(id, value,
				ts.getTickLabelInsets());
		if (ri != null)
			ts.setTickLabelInsets(ri);

		ri = PadUtil.setPropertyValue(id, value, ts.getLabelInsets());
		if (ri != null)
			ts.setLabelInsets(ri);
	}

}
