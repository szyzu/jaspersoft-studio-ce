/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.series.xyzseries;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.charts.design.JRDesignXyzSeries;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.design.JRDesignHyperlink;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.components.chart.ChartNodeIconDescriptor;
import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.DefaultValue;
import com.jaspersoft.studio.model.MHyperLink;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.property.descriptor.JRPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;
import com.jaspersoft.studio.property.descriptor.expression.JRExpressionPropertyDescriptor;

public class MXYZSeries extends APropertyNode {
	
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	private static IPropertyDescriptor[] descriptors;
	
	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new ChartNodeIconDescriptor("xyzseries"); //$NON-NLS-1$
		return iconDescriptor;
	}

	public MXYZSeries() {
		super();
	}

	public MXYZSeries(ANode parent, JRDesignXyzSeries value, int newIndex) {
		super(parent, -1);
		setValue(value);
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

		JRExpressionPropertyDescriptor xValueExpD = new JRExpressionPropertyDescriptor(
				JRDesignXyzSeries.PROPERTY_X_VALUE_EXPRESSION,
				Messages.common_x_value_expression);
		xValueExpD
				.setDescription(Messages.MXYZSeries_x_value_expression_description);
		desc.add(xValueExpD);

		JRExpressionPropertyDescriptor yValueExpD = new JRExpressionPropertyDescriptor(
				JRDesignXyzSeries.PROPERTY_Y_VALUE_EXPRESSION,
				Messages.common_y_value_expression);
		yValueExpD
				.setDescription(Messages.MXYZSeries_y_value_expression_description);
		desc.add(yValueExpD);

		JRExpressionPropertyDescriptor zValueExpD = new JRExpressionPropertyDescriptor(
				JRDesignXyzSeries.PROPERTY_Z_VALUE_EXPRESSION,
				Messages.MXYZSeries_z_value_expression);
		zValueExpD
				.setDescription(Messages.MXYZSeries_z_value_expression_description);
		desc.add(zValueExpD);

		JRExpressionPropertyDescriptor seriesExprD = new JRExpressionPropertyDescriptor(
				JRDesignXyzSeries.PROPERTY_SERIES_EXPRESSION,
				Messages.common_series_expression);
		seriesExprD
				.setDescription(Messages.MXYZSeries_series_expression_description);
		desc.add(seriesExprD);

		JRPropertyDescriptor itemHyperLinkD = new JRPropertyDescriptor(
				JRDesignXyzSeries.PROPERTY_ITEM_HYPERLINK,
				Messages.common_item_hyperlink);
		itemHyperLinkD
				.setDescription(Messages.MXYZSeries_item_hyperlink_description);
		desc.add(itemHyperLinkD);
	}
	
	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();
		
		defaultsMap.put(JRDesignXyzSeries.PROPERTY_X_VALUE_EXPRESSION, new DefaultValue(null, true));
		defaultsMap.put(JRDesignXyzSeries.PROPERTY_Y_VALUE_EXPRESSION, new DefaultValue(null, true));
		defaultsMap.put(JRDesignXyzSeries.PROPERTY_Z_VALUE_EXPRESSION, new DefaultValue(null, true));
		defaultsMap.put(JRDesignXyzSeries.PROPERTY_SERIES_EXPRESSION, new DefaultValue(null, true));
		defaultsMap.put(JRDesignXyzSeries.PROPERTY_ITEM_HYPERLINK, new DefaultValue(null, true));
		
		return defaultsMap;
	}

	private MHyperLink mHyperLink;

	public Object getPropertyValue(Object id) {
		JRDesignXyzSeries jrElement = (JRDesignXyzSeries) getValue();

		if (id.equals(JRDesignXyzSeries.PROPERTY_ITEM_HYPERLINK)) {
			JRHyperlink itemHyperLink = jrElement.getItemHyperlink();
			if (itemHyperLink == null)
				itemHyperLink = new JRDesignHyperlink();
			mHyperLink = new MHyperLink(itemHyperLink);
			setChildListener(mHyperLink);
			return mHyperLink;
		}
		if (id.equals(JRDesignXyzSeries.PROPERTY_X_VALUE_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getXValueExpression());
		if (id.equals(JRDesignXyzSeries.PROPERTY_Y_VALUE_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getYValueExpression());
		if (id.equals(JRDesignXyzSeries.PROPERTY_Z_VALUE_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getZValueExpression());
		if (id.equals(JRDesignXyzSeries.PROPERTY_SERIES_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getSeriesExpression());

		return null;
	}

	public void setPropertyValue(Object id, Object value) {
		JRDesignXyzSeries jrElement = (JRDesignXyzSeries) getValue();

		if (id.equals(JRDesignXyzSeries.PROPERTY_X_VALUE_EXPRESSION))
			jrElement.setXValueExpression(ExprUtil.setValues(
					jrElement.getXValueExpression(), value));
		else if (id.equals(JRDesignXyzSeries.PROPERTY_Y_VALUE_EXPRESSION))
			jrElement.setYValueExpression(ExprUtil.setValues(
					jrElement.getYValueExpression(), value));
		else if (id.equals(JRDesignXyzSeries.PROPERTY_Z_VALUE_EXPRESSION))
			jrElement.setZValueExpression(ExprUtil.setValues(
					jrElement.getZValueExpression(), value));
		else if (id.equals(JRDesignXyzSeries.PROPERTY_SERIES_EXPRESSION))
			jrElement.setSeriesExpression(ExprUtil.setValues(
					jrElement.getSeriesExpression(), value));
	}

	@Override
	public JRDesignXyzSeries getValue() {
		return (JRDesignXyzSeries) super.getValue();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof JRDesignHyperlink) {
			JRHyperlink hl = (JRHyperlink) evt.getSource();
			if (getValue().getItemHyperlink() == null)
				getValue().setItemHyperlink(hl);
		}
		super.propertyChange(evt);
	}

	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	public String getDisplayText() {
		return getIconDescriptor().getTitle();
	}

}
