/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.plot;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.components.chart.model.MChartItemLabel;
import com.jaspersoft.studio.components.chart.property.descriptor.PlotPropertyDescriptor;
import com.jaspersoft.studio.help.HelpReferenceBuilder;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.DefaultValue;
import com.jaspersoft.studio.model.text.MFont;
import com.jaspersoft.studio.model.text.MFontUtil;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.checkbox.CheckBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.color.ColorPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;
import com.jaspersoft.studio.property.descriptor.expression.JRExpressionPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.text.FontPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.text.NTextPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.DegreePropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.DoublePropertyDescriptor;
import com.jaspersoft.studio.utils.AlfaRGB;
import com.jaspersoft.studio.utils.Colors;

import net.sf.jasperreports.charts.JRBar3DPlot;
import net.sf.jasperreports.charts.design.JRDesignBar3DPlot;
import net.sf.jasperreports.charts.design.JRDesignPiePlot;
import net.sf.jasperreports.engine.JRConstants;

public class MBar3DPlot extends MChartPlot {
	
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static IPropertyDescriptor[] descriptors;
	
	private MFont clFont;
	
	private MFont ctFont;
	
	private MFont vlFont;
	
	private MFont vtFont;
	
	private MChartItemLabel chartItemLabel;

	public MBar3DPlot(JRBar3DPlot value) {
		super(value);
	}

	@Override
	public String getDisplayText() {
		return Messages.MBar3DPlot_bar3d_plot;
	}

	@Override
	public IPropertyDescriptor[] getDescriptors() {
		return descriptors;
	}

	@Override
	public void setDescriptors(IPropertyDescriptor[] descriptors1) {
		descriptors = descriptors1;
	}

	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		super.createPropertyDescriptors(desc);

		ColorPropertyDescriptor catAxisLabelColorD = new ColorPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_COLOR, Messages.common_category_axis_label_color, NullEnum.NULL);
		catAxisLabelColorD.setDescription(Messages.MBar3DPlot_category_axis_label_color_description);
		desc.add(catAxisLabelColorD);

		JRExpressionPropertyDescriptor catAxisLabelExprD = new JRExpressionPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_EXPRESSION, Messages.common_category_axis_label_expression);
		catAxisLabelExprD.setDescription(Messages.MBar3DPlot_category_axis_label_expression_description);
		desc.add(catAxisLabelExprD);
		catAxisLabelExprD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#categoryAxisLabelExpression"));

		FontPropertyDescriptor catAxisLabelFontD = new FontPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_FONT, Messages.common_category_axis_label_font);
		catAxisLabelFontD.setDescription(Messages.MBar3DPlot_category_axis_label_font_description);
		desc.add(catAxisLabelFontD);
		catAxisLabelFontD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#labelFont"));

		ColorPropertyDescriptor catAxisTickLabelColorD = new ColorPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_COLOR, Messages.common_category_axis_tick_label_color,
				NullEnum.NULL);
		catAxisTickLabelColorD.setDescription(Messages.MBar3DPlot_category_axis_tick_label_color_description);
		desc.add(catAxisTickLabelColorD);

		FontPropertyDescriptor catAxisTickLabelFontD = new FontPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_FONT, Messages.common_category_axis_tick_label_font);
		catAxisTickLabelFontD.setDescription(Messages.MBar3DPlot_category_axis_tick_label_font_description);
		desc.add(catAxisTickLabelFontD);
		catAxisTickLabelFontD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#tickLabelFont"));

		ColorPropertyDescriptor catAxisLineColorD = new ColorPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LINE_COLOR, Messages.common_category_axis_line_color, NullEnum.NULL);
		catAxisLineColorD.setDescription(Messages.MBar3DPlot_category_axis_line_color_description);
		desc.add(catAxisLineColorD);

		ColorPropertyDescriptor valAxisLabelColorD = new ColorPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_COLOR, Messages.common_value_axis_label_color, NullEnum.NULL);
		valAxisLabelColorD.setDescription(Messages.MBar3DPlot_value_axis_label_color_description);
		desc.add(valAxisLabelColorD);

		JRExpressionPropertyDescriptor valAxisLabelExprD = new JRExpressionPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_EXPRESSION, Messages.MBar3DPlot_value_axis_label_expression);
		valAxisLabelExprD.setDescription(Messages.MBar3DPlot_value_axis_label_expression_description);
		desc.add(valAxisLabelExprD);
		valAxisLabelExprD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#valueAxisLabelExpression"));

		FontPropertyDescriptor valAxisLabelFontD = new FontPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_FONT, Messages.common_value_axis_label_font);
		valAxisLabelFontD.setDescription(Messages.MBar3DPlot_value_axis_label_font_description);
		desc.add(valAxisLabelFontD);
		valAxisLabelFontD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#labelFont"));

		ColorPropertyDescriptor valAxisTickLabelColorD = new ColorPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_COLOR, Messages.common_value_axis_tick_label_color, NullEnum.NULL);
		valAxisTickLabelColorD.setDescription(Messages.MBar3DPlot_value_axis_tick_label_color_description);
		desc.add(valAxisTickLabelColorD);

		FontPropertyDescriptor valAxisTickLabelFontD = new FontPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_FONT, Messages.common_value_axis_tick_label_font);
		valAxisTickLabelFontD.setDescription(Messages.MBar3DPlot_value_axis_tick_label_font_description);
		desc.add(valAxisTickLabelFontD);
		valAxisTickLabelFontD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#tickLabelFont"));

		PlotPropertyDescriptor itemLabelD = new PlotPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_ITEM_LABEL, Messages.common_item_label);
		itemLabelD.setDescription(Messages.MBar3DPlot_item_label_description);
		desc.add(itemLabelD);
		itemLabelD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#itemLabel"));

		ColorPropertyDescriptor valAxisLineColorD = new ColorPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LINE_COLOR, Messages.common_value_axis_line_color, NullEnum.NULL);
		valAxisLineColorD.setDescription(Messages.MBar3DPlot_value_axis_line_color_description);
		desc.add(valAxisLineColorD);

		JRExpressionPropertyDescriptor rangeAxisMinExprD = new JRExpressionPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_RANGE_AXIS_MINVALUE_EXPRESSION, Messages.common_range_axis_minvalue_expression);
		rangeAxisMinExprD.setDescription(Messages.MBar3DPlot_range_axis_minvalue_expression_description);
		desc.add(rangeAxisMinExprD);
		rangeAxisMinExprD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#rangeAxisMinValueExpression"));

		JRExpressionPropertyDescriptor rangeAxisMaxExprD = new JRExpressionPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_RANGE_AXIS_MAXVALUE_EXPRESSION, Messages.common_range_axis_maxvalue_expression);
		rangeAxisMaxExprD.setDescription(Messages.MBar3DPlot_range_axis_maxvalue_expression_description);
		desc.add(rangeAxisMaxExprD);
		rangeAxisMaxExprD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#rangeAxisMaxValueExpression"));

		JRExpressionPropertyDescriptor domainAxisMinExprD = new JRExpressionPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_DOMAIN_AXIS_MINVALUE_EXPRESSION, Messages.common_domain_axis_minvalue_expression);
		domainAxisMinExprD.setDescription(Messages.MBar3DPlot_domain_axis_minvalue_expression_description);
		desc.add(domainAxisMinExprD);
		domainAxisMinExprD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#domainAxisMinValueExpression"));

		JRExpressionPropertyDescriptor domainAxisMaxExprD = new JRExpressionPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_DOMAIN_AXIS_MAXVALUE_EXPRESSION, Messages.common_domain_axis_maxvalue_expression);
		domainAxisMaxExprD.setDescription(Messages.MBar3DPlot_domain_axis_maxvalue_expression_description);
		desc.add(domainAxisMaxExprD);
		domainAxisMaxExprD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#domainAxisMaxValueExpression"));

		CheckBoxPropertyDescriptor catAxisVertTickLabelD = new CheckBoxPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_VERTICAL_TICK_LABELS,
				Messages.common_category_axis_vertical_tick_labels, NullEnum.NULL);
		catAxisVertTickLabelD.setDescription(Messages.MBar3DPlot_category_axis_vertical_tick_labels_description);
		catAxisVertTickLabelD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#axisFormat_verticalTickLabels"));
		desc.add(catAxisVertTickLabelD);

		CheckBoxPropertyDescriptor valAxisVertTickLabelD = new CheckBoxPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_VERTICAL_TICK_LABELS, Messages.common_value_axis_vertical_tick_labels,
				NullEnum.NULL);
		valAxisVertTickLabelD.setDescription(Messages.MBar3DPlot_value_axis_vertical_tick_labels_description);
		valAxisVertTickLabelD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#axisFormat_verticalTickLabels"));
		desc.add(valAxisVertTickLabelD);

		NTextPropertyDescriptor catAxisTickLabelMaskD = new NTextPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_MASK, Messages.common_category_axis_tick_label_mask);
		catAxisTickLabelMaskD.setDescription(Messages.MBar3DPlot_category_axis_tick_label_mask_description);
		catAxisTickLabelMaskD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#axisFormat_tickLabelMask"));
		desc.add(catAxisTickLabelMaskD);

		NTextPropertyDescriptor valAxisTickLabelMaskD = new NTextPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_MASK, Messages.common_value_axis_tick_label_mask);
		valAxisTickLabelMaskD.setDescription(Messages.MBar3DPlot_value_axis_tick_label_mask_description);
		valAxisTickLabelMaskD.setHelpRefBuilder(new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#axisFormat_tickLabelMask"));
		desc.add(valAxisTickLabelMaskD);

		DoublePropertyDescriptor catAxisTickLabelRotation = new DegreePropertyDescriptor(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_ROTATION, Messages.common_category_axis_tick_label_rotation);
		catAxisTickLabelRotation.setDescription(Messages.MBar3DPlot_category_axis_tick_label_rotation_description);
		desc.add(catAxisTickLabelRotation);

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#axisFormat");

		CheckBoxPropertyDescriptor showLabelsD = new CheckBoxPropertyDescriptor(JRDesignBar3DPlot.PROPERTY_SHOW_LABELS, Messages.common_show_labels, NullEnum.NULL);
		showLabelsD.setDescription(Messages.MBar3DPlot_show_labels_description);
		desc.add(showLabelsD);

		DoublePropertyDescriptor xoffsetD = new DoublePropertyDescriptor(JRDesignBar3DPlot.PROPERTY_X_OFFSET, Messages.MBar3DPlot_x_offset);
		xoffsetD.setDescription(Messages.MBar3DPlot_x_offset_description);
		desc.add(xoffsetD);

		DoublePropertyDescriptor yoffsetD = new DoublePropertyDescriptor(JRDesignBar3DPlot.PROPERTY_Y_OFFSET, Messages.MBar3DPlot_y_offset);
		yoffsetD.setDescription(Messages.MBar3DPlot_y_offset_description);
		desc.add(yoffsetD);

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#bar3DPlot");

	}
	
	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();
		defaultsMap.put(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_ROTATION, new DefaultValue(true));
		defaultsMap.put(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_COLOR, new DefaultValue(AlfaRGB.getFullyOpaque(new RGB(0, 0, 0)), true));
		defaultsMap.put(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LINE_COLOR, new DefaultValue(AlfaRGB.getFullyOpaque(new RGB(0, 0, 0)), true));
		defaultsMap.put(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_COLOR, new DefaultValue(AlfaRGB.getFullyOpaque(new RGB(0, 0, 0)), true));
		defaultsMap.put(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_COLOR, new DefaultValue(AlfaRGB.getFullyOpaque(new RGB(0, 0, 0)), true));
		defaultsMap.put(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LINE_COLOR, new DefaultValue(AlfaRGB.getFullyOpaque(new RGB(0, 0, 0)), true));
		defaultsMap.put(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_COLOR, new DefaultValue(AlfaRGB.getFullyOpaque(new RGB(0, 0, 0)), true));
		defaultsMap.putAll(((APropertyNode)getPropertyValue(JRDesignPiePlot.PROPERTY_ITEM_LABEL)).getDefaultsPropertiesMap());
		return defaultsMap;
	}

	@Override
	public Object getPropertyActualValue(Object id) {
		JRDesignBar3DPlot jrElement = (JRDesignBar3DPlot) getValue();
		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getCategoryAxisLabelColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getCategoryAxisTickLabelColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LINE_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getCategoryAxisLineColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getValueAxisLabelColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getValueAxisTickLabelColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LINE_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getValueAxisLineColor());
		else
			return super.getPropertyActualValue(id);
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java
	 * .lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		JRDesignBar3DPlot jrElement = (JRDesignBar3DPlot) getValue();
		if (chartItemLabel == null) {
			chartItemLabel = new MChartItemLabel(jrElement.getItemLabel());
			setChildListener(chartItemLabel);
		}
		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getOwnCategoryAxisLabelColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getOwnCategoryAxisTickLabelColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LINE_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getOwnCategoryAxisLineColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getOwnValueAxisLabelColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getOwnValueAxisTickLabelColor());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LINE_COLOR))
			return Colors.getSWTRGB4AWTGBColor(jrElement.getOwnValueAxisLineColor());

		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_VERTICAL_TICK_LABELS))
			return jrElement.getCategoryAxisVerticalTickLabels();
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_VERTICAL_TICK_LABELS))
			return jrElement.getValueAxisVerticalTickLabels();
		if (id.equals(JRDesignBar3DPlot.PROPERTY_SHOW_LABELS))
			return jrElement.getShowLabels();

		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_MASK))
			return jrElement.getCategoryAxisTickLabelMask();
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_MASK))
			return jrElement.getValueAxisTickLabelMask();
		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_ROTATION))
			return jrElement.getCategoryAxisTickLabelRotation();

		if (id.equals(JRDesignBar3DPlot.PROPERTY_X_OFFSET))
			return jrElement.getXOffsetDouble();
		if (id.equals(JRDesignBar3DPlot.PROPERTY_Y_OFFSET))
			return jrElement.getYOffsetDouble();

		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getCategoryAxisLabelExpression());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getValueAxisLabelExpression());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_RANGE_AXIS_MAXVALUE_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getRangeAxisMaxValueExpression());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_RANGE_AXIS_MINVALUE_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getRangeAxisMinValueExpression());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_DOMAIN_AXIS_MAXVALUE_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getDomainAxisMaxValueExpression());
		if (id.equals(JRDesignBar3DPlot.PROPERTY_DOMAIN_AXIS_MINVALUE_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getDomainAxisMinValueExpression());

		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_FONT)) {
			clFont = MFontUtil.getMFont(clFont, jrElement.getCategoryAxisLabelFont(), null, this);
			return clFont;
		}
		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_FONT)) {
			ctFont = MFontUtil.getMFont(ctFont, jrElement.getCategoryAxisTickLabelFont(), null, this);
			return ctFont;
		}
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_FONT)) {
			vlFont = MFontUtil.getMFont(vlFont, jrElement.getValueAxisLabelFont(), null, this);
			return vlFont;
		}
		if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_FONT)) {
			vtFont = MFontUtil.getMFont(vtFont, jrElement.getValueAxisTickLabelFont(), null, this);
			return vtFont;
		}
		if (id.equals(JRDesignPiePlot.PROPERTY_ITEM_LABEL)) {
			return chartItemLabel;
		} else {
			Object value = chartItemLabel.getPropertyValue(id);
			if (value == null)
				value = super.getPropertyValue(id);
			return value;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		JRDesignBar3DPlot jrElement = (JRDesignBar3DPlot) getValue();

		if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_FONT)) {
			jrElement.setCategoryAxisLabelFont(MFontUtil.setMFont(value));
		} else if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_FONT)) {
			jrElement.setCategoryAxisTickLabelFont(MFontUtil.setMFont(value));
		} else if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_FONT)) {
			jrElement.setValueAxisLabelFont(MFontUtil.setMFont(value));
		} else if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_FONT)) {
			jrElement.setValueAxisTickLabelFont(MFontUtil.setMFont(value));
		} else if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_COLOR) && (value == null || value instanceof AlfaRGB))
			jrElement.setCategoryAxisLabelColor(Colors.getAWT4SWTRGBColor((AlfaRGB) value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_COLOR) && (value == null || value instanceof AlfaRGB))
			jrElement.setCategoryAxisTickLabelColor(Colors.getAWT4SWTRGBColor((AlfaRGB) value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LINE_COLOR) && (value == null || value instanceof AlfaRGB))
			jrElement.setCategoryAxisLineColor(Colors.getAWT4SWTRGBColor((AlfaRGB) value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_COLOR) && (value == null || value instanceof AlfaRGB))
			jrElement.setValueAxisLabelColor(Colors.getAWT4SWTRGBColor((AlfaRGB) value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_COLOR) && (value == null || value instanceof AlfaRGB))
			jrElement.setValueAxisTickLabelColor(Colors.getAWT4SWTRGBColor((AlfaRGB) value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LINE_COLOR) && (value == null || value instanceof AlfaRGB))
			jrElement.setValueAxisLineColor(Colors.getAWT4SWTRGBColor((AlfaRGB) value));

		else if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_VERTICAL_TICK_LABELS))
			jrElement.setCategoryAxisVerticalTickLabels((Boolean) value);
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_VERTICAL_TICK_LABELS))
			jrElement.setValueAxisVerticalTickLabels((Boolean) value);
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_SHOW_LABELS))
			jrElement.setShowLabels((Boolean) value);

		else if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_MASK))
			jrElement.setCategoryAxisTickLabelMask((String) value);
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_TICK_LABEL_MASK))
			jrElement.setValueAxisTickLabelMask((String) value);
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_TICK_LABEL_ROTATION))
			jrElement.setCategoryAxisTickLabelRotation((Double) value);

		else if (id.equals(JRDesignBar3DPlot.PROPERTY_X_OFFSET))
			jrElement.setXOffset((Double) value);
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_Y_OFFSET))
			jrElement.setYOffset((Double) value);

		else if (id.equals(JRDesignBar3DPlot.PROPERTY_CATEGORY_AXIS_LABEL_EXPRESSION))
			jrElement.setCategoryAxisLabelExpression(ExprUtil.setValues(jrElement.getCategoryAxisLabelExpression(), value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_VALUE_AXIS_LABEL_EXPRESSION))
			jrElement.setValueAxisLabelExpression(ExprUtil.setValues(jrElement.getValueAxisLabelExpression(), value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_RANGE_AXIS_MAXVALUE_EXPRESSION))
			jrElement.setRangeAxisMaxValueExpression(ExprUtil.setValues(jrElement.getRangeAxisMaxValueExpression(), value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_RANGE_AXIS_MINVALUE_EXPRESSION))
			jrElement.setRangeAxisMinValueExpression(ExprUtil.setValues(jrElement.getRangeAxisMinValueExpression(), value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_DOMAIN_AXIS_MAXVALUE_EXPRESSION))
			jrElement.setDomainAxisMaxValueExpression(ExprUtil.setValues(jrElement.getDomainAxisMaxValueExpression(), value));
		else if (id.equals(JRDesignBar3DPlot.PROPERTY_DOMAIN_AXIS_MINVALUE_EXPRESSION))
			jrElement.setDomainAxisMinValueExpression(ExprUtil.setValues(jrElement.getDomainAxisMinValueExpression(), value));
		else
			chartItemLabel.setPropertyValue(id, value);
		super.setPropertyValue(id, value);

	}
}
