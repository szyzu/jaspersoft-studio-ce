/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.barcode.model.barcode4j;

import java.util.HashSet;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.components.barcode.messages.Messages;
import com.jaspersoft.studio.editor.defaults.DefaultManager;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.checkbox.CheckBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.DoublePropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.JSSComboPropertyDescriptor;

import net.sf.jasperreports.components.barcode4j.Barcode4jComponent;
import net.sf.jasperreports.components.barcode4j.Code39Component;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.component.ComponentKey;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;

public class MCode39 extends MBarcode4j {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private static IPropertyDescriptor[] descriptors;

	public MCode39() {
		super();
	}

	public MCode39(ANode parent, JRDesignComponentElement jrBarcode, int newIndex) {
		super(parent, jrBarcode, newIndex);
	}

	@Override
	public JRDesignComponentElement createJRElement(JasperDesign jasperDesign, boolean applyDefault) {
		JRDesignComponentElement el = new JRDesignComponentElement();
		Code39Component component = new Code39Component();
		JRDesignExpression exp = new JRDesignExpression();
		exp.setText("\"123456789\""); //$NON-NLS-1$
		component.setCodeExpression(exp);
		el.setComponent(component);
		el.setComponentKey(
				new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "Code39")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		if (applyDefault) {
			DefaultManager.INSTANCE.applyDefault(this.getClass(), el);
		}

		return el;
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
		super.createPropertyDescriptors(desc);
		DoublePropertyDescriptor wideFactorD = new DoublePropertyDescriptor(Code39Component.PROPERTY_WIDE_FACTOR,
				Messages.common_wide_factor);
		wideFactorD.setDescription(Messages.MCode39_wide_factor_description);
		desc.add(wideFactorD);
		wideFactorD.setBounds(0, 1);

		DoublePropertyDescriptor intercharD = new DoublePropertyDescriptor(Code39Component.PROPERTY_INTERCHAR_GAP_WIDTH,
				Messages.common_interchar_gap_width);
		intercharD.setDescription(Messages.MCode39_interchar_gap_width_description);
		desc.add(intercharD);

		CheckBoxPropertyDescriptor displayChecksumD = new CheckBoxPropertyDescriptor(
				Code39Component.PROPERTY_DISPLAY_CHECKSUM, Messages.common_display_checksum, NullEnum.NULL);
		displayChecksumD.setDescription(Messages.MCode39_display_checksum_description);
		desc.add(displayChecksumD);

		CheckBoxPropertyDescriptor displayStartStopD = new CheckBoxPropertyDescriptor(
				Code39Component.PROPERTY_DISPLAY_START_STOP, Messages.MCode39_display_start_stop, NullEnum.NULL);
		displayStartStopD.setDescription(Messages.MCode39_display_start_stop_description);
		desc.add(displayStartStopD);

		CheckBoxPropertyDescriptor extendedCharsetD = new CheckBoxPropertyDescriptor(
				Code39Component.PROPERTY_EXTENDED_CHARSET_ENABLED, Messages.MCode39_extended_charset_enabled,
				NullEnum.NULL);
		extendedCharsetD.setDescription(Messages.MCode39_extended_charset_enabled_description);
		desc.add(extendedCharsetD);

		JSSComboPropertyDescriptor checksumModeD = new JSSComboPropertyDescriptor(
				Code39Component.PROPERTY_CHECKSUM_MODE, Messages.common_checksum_mode, ChecksumMode.getItems());
		checksumModeD.setDescription(Messages.MCode39_checksum_mode_description);
		desc.add(checksumModeD);

		DoublePropertyDescriptor vertQuietZoneD = new DoublePropertyDescriptor(
				Barcode4jComponent.PROPERTY_VERTICAL_QUIET_ZONE, Messages.MBarcode4j_vertical_quiet_zone);
		vertQuietZoneD.setDescription(Messages.MBarcode4j_vertical_quiet_zone_description);
		desc.add(vertQuietZoneD);
		vertQuietZoneD.setCategory(Messages.common_properties_category);

		checksumModeD.setCategory(Messages.MCode39_properties_category);
		extendedCharsetD.setCategory(Messages.MCode39_properties_category);
		displayChecksumD.setCategory(Messages.MCode39_properties_category);
		displayStartStopD.setCategory(Messages.MCode39_properties_category);
		wideFactorD.setCategory(Messages.MCode39_properties_category);
		intercharD.setCategory(Messages.MCode39_properties_category);
	}

	@Override
	public Object getPropertyValue(Object id) {
		JRDesignComponentElement jrElement = (JRDesignComponentElement) getValue();
		Code39Component jrList = (Code39Component) jrElement.getComponent();

		if (id.equals(Code39Component.PROPERTY_WIDE_FACTOR))
			return jrList.getWideFactor();
		if (id.equals(Code39Component.PROPERTY_INTERCHAR_GAP_WIDTH))
			return jrList.getIntercharGapWidth();

		if (id.equals(Code39Component.PROPERTY_DISPLAY_CHECKSUM))
			return jrList.isDisplayChecksum();
		if (id.equals(Code39Component.PROPERTY_DISPLAY_START_STOP))
			return jrList.isDisplayStartStop();
		if (id.equals(Code39Component.PROPERTY_EXTENDED_CHARSET_ENABLED))
			return jrList.isExtendedCharSetEnabled();

		if (id.equals(Code39Component.PROPERTY_CHECKSUM_MODE))
			return ChecksumMode.getPos4ChecksumMode(jrList.getChecksumMode());

		return super.getPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		JRDesignComponentElement jrElement = (JRDesignComponentElement) getValue();
		Code39Component jrList = (Code39Component) jrElement.getComponent();

		if (id.equals(Code39Component.PROPERTY_WIDE_FACTOR))
			jrList.setWideFactor((Double) value);
		else if (id.equals(Code39Component.PROPERTY_INTERCHAR_GAP_WIDTH))
			jrList.setIntercharGapWidth((Double) value);

		else if (id.equals(Code39Component.PROPERTY_CHECKSUM_MODE))
			jrList.setChecksumMode(ChecksumMode.getChecksumMode4Pos((Integer) value));

		else if (id.equals(Code39Component.PROPERTY_DISPLAY_CHECKSUM))
			jrList.setDisplayChecksum((Boolean) value);
		else if (id.equals(Code39Component.PROPERTY_DISPLAY_START_STOP))
			jrList.setDisplayStartStop((Boolean) value);
		else if (id.equals(Code39Component.PROPERTY_EXTENDED_CHARSET_ENABLED))
			jrList.setExtendedCharSetEnabled((Boolean) value);
		else
			super.setPropertyValue(id, value);
	}

	@Override
	public HashSet<String> generateGraphicalProperties() {
		HashSet<String> properties = super.generateGraphicalProperties();
		properties.add(Code39Component.PROPERTY_WIDE_FACTOR);
		properties.add(Code39Component.PROPERTY_INTERCHAR_GAP_WIDTH);
		properties.add(Code39Component.PROPERTY_CHECKSUM_MODE);
		properties.add(Code39Component.PROPERTY_DISPLAY_CHECKSUM);
		properties.add(Code39Component.PROPERTY_DISPLAY_START_STOP);
		properties.add(Code39Component.PROPERTY_EXTENDED_CHARSET_ENABLED);
		return properties;
	}

	@Override
	public void trasnferProperties(JRElement target) {
		super.trasnferProperties(target);

		JRDesignComponentElement jrSourceElement = (JRDesignComponentElement) getValue();
		Code39Component jrSourceBarcode = (Code39Component) jrSourceElement.getComponent();

		JRDesignComponentElement jrTargetElement = (JRDesignComponentElement) target;
		Code39Component jrTargetBarcode = (Code39Component) jrTargetElement.getComponent();

		jrTargetBarcode.setWideFactor(jrSourceBarcode.getWideFactor());
		jrTargetBarcode.setIntercharGapWidth(jrSourceBarcode.getIntercharGapWidth());
		jrTargetBarcode.setChecksumMode(jrSourceBarcode.getChecksumMode());
		jrTargetBarcode.setDisplayChecksum(jrSourceBarcode.isDisplayChecksum());
		jrTargetBarcode.setDisplayStartStop(jrSourceBarcode.isDisplayStartStop());
		jrTargetBarcode.setExtendedCharSetEnabled(jrSourceBarcode.isExtendedCharSetEnabled());
	}
}
