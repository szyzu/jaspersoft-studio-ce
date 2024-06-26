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
import net.sf.jasperreports.components.barcode4j.POSTNETComponent;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.component.ComponentKey;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;

public class MPOSTNET extends MBarcode4j {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private static IPropertyDescriptor[] descriptors;

	public MPOSTNET() {
		super();
	}

	public MPOSTNET(ANode parent, JRDesignComponentElement jrBarcode, int newIndex) {
		super(parent, jrBarcode, newIndex);
	}

	@Override
	public JRDesignComponentElement createJRElement(JasperDesign jasperDesign, boolean applyDefault) {
		JRDesignComponentElement el = new JRDesignComponentElement();
		POSTNETComponent component = new POSTNETComponent();
		JRDesignExpression exp = new JRDesignExpression();
		exp.setText("\"123456789\""); //$NON-NLS-1$
		component.setCodeExpression(exp);
		el.setComponent(component);
		el.setComponentKey(
				new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "POSTNET")); //$NON-NLS-1$

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

		DoublePropertyDescriptor shortBarHeightD = new DoublePropertyDescriptor(
				POSTNETComponent.PROPERTY_SHORT_BAR_HEIGHT, Messages.MPOSTNET_short_bar_height);
		shortBarHeightD.setDescription(Messages.MPOSTNET_short_bar_height_description);
		desc.add(shortBarHeightD);

		DoublePropertyDescriptor intercharD = new DoublePropertyDescriptor(
				POSTNETComponent.PROPERTY_INTERCHAR_GAP_WIDTH, Messages.common_interchar_gap_width);
		intercharD.setDescription(Messages.MPOSTNET_interchar_gap_width_description);
		desc.add(intercharD);

		CheckBoxPropertyDescriptor displayChecksumD = new CheckBoxPropertyDescriptor(
				POSTNETComponent.PROPERTY_DISPLAY_CHECKSUM, Messages.common_display_checksum, NullEnum.NULL);
		displayChecksumD.setDescription(Messages.MPOSTNET_display_checksum_description);
		desc.add(displayChecksumD);

		JSSComboPropertyDescriptor checksumModeD = new JSSComboPropertyDescriptor(
				POSTNETComponent.PROPERTY_CHECKSUM_MODE, Messages.common_checksum_mode, ChecksumMode.getItems());
		checksumModeD.setDescription(Messages.MPOSTNET_checksum_mode_description);
		desc.add(checksumModeD);

		JSSComboPropertyDescriptor baselinePositionD = new JSSComboPropertyDescriptor(
				POSTNETComponent.PROPERTY_BASELINE_POSITION, Messages.MPOSTNET_baseline_position,
				BaselinePosition.getItems());
		baselinePositionD.setDescription(Messages.MPOSTNET_baseline_position_description);
		desc.add(baselinePositionD);

		DoublePropertyDescriptor vertQuietZoneD = new DoublePropertyDescriptor(
				Barcode4jComponent.PROPERTY_VERTICAL_QUIET_ZONE, Messages.MBarcode4j_vertical_quiet_zone);
		vertQuietZoneD.setDescription(Messages.MBarcode4j_vertical_quiet_zone_description);
		desc.add(vertQuietZoneD);
		vertQuietZoneD.setCategory(Messages.common_properties_category);

		shortBarHeightD.setCategory(Messages.MPOSTNET_properties_category);
		baselinePositionD.setCategory(Messages.MPOSTNET_properties_category);
		checksumModeD.setCategory(Messages.MPOSTNET_properties_category);
		displayChecksumD.setCategory(Messages.MPOSTNET_properties_category);
		intercharD.setCategory(Messages.MPOSTNET_properties_category);
	}

	@Override
	public Object getPropertyValue(Object id) {
		JRDesignComponentElement jrElement = (JRDesignComponentElement) getValue();
		POSTNETComponent jrList = (POSTNETComponent) jrElement.getComponent();

		if (id.equals(POSTNETComponent.PROPERTY_SHORT_BAR_HEIGHT))
			return jrList.getShortBarHeight();
		if (id.equals(POSTNETComponent.PROPERTY_INTERCHAR_GAP_WIDTH))
			return jrList.getIntercharGapWidth();
		if (id.equals(POSTNETComponent.PROPERTY_DISPLAY_CHECKSUM))
			return jrList.getDisplayChecksum();
		if (id.equals(POSTNETComponent.PROPERTY_CHECKSUM_MODE))
			return ChecksumMode.getPos4ChecksumMode(jrList.getChecksumMode());
		if (id.equals(POSTNETComponent.PROPERTY_BASELINE_POSITION))
			return BaselinePosition.getPos4BaselinePosition(jrList.getBaselinePosition());

		return super.getPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		JRDesignComponentElement jrElement = (JRDesignComponentElement) getValue();
		POSTNETComponent jrList = (POSTNETComponent) jrElement.getComponent();

		if (id.equals(POSTNETComponent.PROPERTY_SHORT_BAR_HEIGHT))
			jrList.setShortBarHeight((Double) value);
		else if (id.equals(POSTNETComponent.PROPERTY_INTERCHAR_GAP_WIDTH))
			jrList.setIntercharGapWidth((Double) value);
		else if (id.equals(POSTNETComponent.PROPERTY_CHECKSUM_MODE))
			jrList.setChecksumMode(ChecksumMode.getChecksumMode4Pos((Integer) value));
		else if (id.equals(POSTNETComponent.PROPERTY_BASELINE_POSITION))
			jrList.setBaselinePosition(BaselinePosition.getBaselinePosition4Pos((Integer) value));

		else if (id.equals(POSTNETComponent.PROPERTY_DISPLAY_CHECKSUM))
			jrList.setDisplayChecksum((Boolean) value);
		else
			super.setPropertyValue(id, value);
	}

	@Override
	public HashSet<String> generateGraphicalProperties() {
		HashSet<String> properties = super.generateGraphicalProperties();
		properties.add(POSTNETComponent.PROPERTY_SHORT_BAR_HEIGHT);
		properties.add(POSTNETComponent.PROPERTY_INTERCHAR_GAP_WIDTH);
		properties.add(POSTNETComponent.PROPERTY_CHECKSUM_MODE);
		properties.add(POSTNETComponent.PROPERTY_BASELINE_POSITION);
		properties.add(POSTNETComponent.PROPERTY_DISPLAY_CHECKSUM);
		return properties;
	}

	@Override
	public void trasnferProperties(JRElement target) {
		super.trasnferProperties(target);

		JRDesignComponentElement jrSourceElement = (JRDesignComponentElement) getValue();
		POSTNETComponent jrSourceBarcode = (POSTNETComponent) jrSourceElement.getComponent();

		JRDesignComponentElement jrTargetElement = (JRDesignComponentElement) target;
		POSTNETComponent jrTargetBarcode = (POSTNETComponent) jrTargetElement.getComponent();

		jrTargetBarcode.setShortBarHeight(jrSourceBarcode.getShortBarHeight());
		jrTargetBarcode.setIntercharGapWidth(jrSourceBarcode.getIntercharGapWidth());
		jrTargetBarcode.setChecksumMode(jrSourceBarcode.getChecksumMode());
		jrTargetBarcode.setBaselinePosition(jrSourceBarcode.getBaselinePosition());
		jrTargetBarcode.setDisplayChecksum(jrSourceBarcode.getDisplayChecksum());
	}
}
