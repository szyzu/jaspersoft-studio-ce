/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.barcode.model.barcode4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.components.barcode.BarcodeNodeIconDescriptor;
import com.jaspersoft.studio.components.barcode.messages.Messages;
import com.jaspersoft.studio.components.barcode.model.MBarcode;
import com.jaspersoft.studio.editor.defaults.DefaultManager;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.DefaultValue;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;
import com.jaspersoft.studio.property.descriptors.DoublePropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.IntegerPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.NamedEnumPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.PixelPropertyDescriptor;
import com.jaspersoft.studio.utils.EnumHelper;

import net.sf.jasperreports.components.barbecue.StandardBarbecueComponent;
import net.sf.jasperreports.components.barcode4j.Barcode4jComponent;
import net.sf.jasperreports.components.barcode4j.ErrorCorrectionLevelEnum;
import net.sf.jasperreports.components.barcode4j.QRCodeComponent;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.component.ComponentKey;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

public class MQRCode extends MBarcode {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private static IPropertyDescriptor[] descriptors;

	private static NamedEnumPropertyDescriptor<ErrorCorrectionLevelEnum> errLevelD;

	public MQRCode() {
		super();
	}

	public MQRCode(ANode parent, JRDesignComponentElement jrBarcode, int newIndex) {
		super(parent, newIndex);
		setValue(jrBarcode);
	}

	private static IIconDescriptor iconDescriptor;

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new BarcodeNodeIconDescriptor("barcode"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.MGeneric#getDisplayText()
	 */
	@Override
	public String getDisplayText() {
		String p = getElementNameProperty();
		return Misc.isNullOrEmpty(p) ? getIconDescriptor().getTitle() : p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.MGeneric#getImagePath()
	 */
	@Override
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.MGeneric#getToolTip()
	 */
	@Override
	public String getToolTip() {
		return getIconDescriptor().getToolTip();
	}

	@Override
	public JRDesignComponentElement createJRElement(JasperDesign jasperDesign, boolean applyDefault) {
		JRDesignComponentElement el = new JRDesignComponentElement();
		QRCodeComponent component = new QRCodeComponent();
		JRDesignExpression exp = new JRDesignExpression();
		exp.setText("\"123456789\""); //$NON-NLS-1$
		component.setCodeExpression(exp);
		el.setComponent(component);
		el.setComponentKey(
				new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "QRCode")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

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
	 * @param desc the desc
	 */
	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		super.createPropertyDescriptors(desc);

		errLevelD = new NamedEnumPropertyDescriptor<>(QRCodeComponent.PROPERTY_ERROR_CORRECTION_LEVEL,
				Messages.MQRCode_2, ErrorCorrectionLevelEnum.H, NullEnum.NOTNULL);
		errLevelD.setDescription(Messages.MQRCode_3);
		errLevelD.setCategory(Messages.MQRCode_4);
		desc.add(errLevelD);

		PixelPropertyDescriptor margind = new PixelPropertyDescriptor(QRCodeComponent.PROPERTY_MARGIN,
				Messages.MQRCode_5);
		margind.setDescription(Messages.MQRCode_6);
		margind.setCategory("QR Code"); //$NON-NLS-1$
		desc.add(margind);

		DoublePropertyDescriptor vertQuietZoneD = new DoublePropertyDescriptor(
				Barcode4jComponent.PROPERTY_VERTICAL_QUIET_ZONE, Messages.MBarcode4j_vertical_quiet_zone);
		vertQuietZoneD.setDescription(Messages.MBarcode4j_vertical_quiet_zone_description);
		desc.add(vertQuietZoneD);
		vertQuietZoneD.setCategory(Messages.common_properties_category);

		IntegerPropertyDescriptor qrVersion = new IntegerPropertyDescriptor(QRCodeComponent.PROPERTY_QR_VERSION,
				Messages.MQRCode_0);
		qrVersion.setDescription(Messages.MQRCode_desc);
		qrVersion.setBounds(1, 40);
		desc.add(qrVersion);
		qrVersion.setCategory(Messages.common_properties_category);
	}

	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();

		defaultsMap.put(QRCodeComponent.PROPERTY_ERROR_CORRECTION_LEVEL,
				new DefaultValue(ErrorCorrectionLevelEnum.H, false));
		defaultsMap.put(QRCodeComponent.PROPERTY_MARGIN, new DefaultValue(new Integer(1), false));

		return defaultsMap;
	}

	@Override
	public Object getPropertyValue(Object id) {
		JRDesignComponentElement jrElement = (JRDesignComponentElement) getValue();
		QRCodeComponent qrCodeComponent = (QRCodeComponent) jrElement.getComponent();

		if (id.equals(QRCodeComponent.PROPERTY_ERROR_CORRECTION_LEVEL))
			return errLevelD.getIntValue(qrCodeComponent.getErrorCorrectionLevel());
		if (id.equals(QRCodeComponent.PROPERTY_MARGIN))
			return qrCodeComponent.getMargin();
		if (id.equals(QRCodeComponent.PROPERTY_QR_VERSION))
			return qrCodeComponent.getQrVersion();
		if (id.equals(StandardBarbecueComponent.PROPERTY_CODE_EXPRESSION))
			return ExprUtil.getExpression(qrCodeComponent.getCodeExpression());
		if (id.equals(StandardBarbecueComponent.PROPERTY_EVALUATION_TIME))
			return qrCodeComponent.getEvaluationTimeValue();
		return super.getPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		JRDesignComponentElement jrElement = (JRDesignComponentElement) getValue();
		QRCodeComponent qrcodeComponent = (QRCodeComponent) jrElement.getComponent();

		if (id.equals(QRCodeComponent.PROPERTY_ERROR_CORRECTION_LEVEL))
			qrcodeComponent.setErrorCorrectionLevel(errLevelD.getEnumValue(value));
		else if (id.equals(QRCodeComponent.PROPERTY_MARGIN))
			qrcodeComponent.setMargin((Integer) value);
		else if (id.equals(QRCodeComponent.PROPERTY_QR_VERSION))
			qrcodeComponent.setQrVersion((Integer) value);
		else if (id.equals(StandardBarbecueComponent.PROPERTY_CODE_EXPRESSION))
			qrcodeComponent.setCodeExpression(ExprUtil.setValues(qrcodeComponent.getCodeExpression(), value, null));
		else if (id.equals(StandardBarbecueComponent.PROPERTY_EVALUATION_TIME)) {
			EvaluationTimeEnum evalTime = EnumHelper.getEnumByObjectValue(EvaluationTimeEnum.values(), value);
			qrcodeComponent.setEvaluationTimeValue(evalTime);
			if (evalTime != null && !evalTime.equals(EvaluationTimeEnum.GROUP)) {
				qrcodeComponent.setEvaluationGroup(null);
			}
		}
		super.setPropertyValue(id, value);
	}

	@Override
	public void trasnferProperties(JRElement target) {
		super.trasnferProperties(target);

		JRDesignComponentElement jrSourceElement = (JRDesignComponentElement) getValue();
		QRCodeComponent jrSourceBarcode = (QRCodeComponent) jrSourceElement.getComponent();

		JRDesignComponentElement jrTargetElement = (JRDesignComponentElement) target;
		QRCodeComponent jrTargetBarcode = (QRCodeComponent) jrTargetElement.getComponent();

		jrTargetBarcode.setMargin(jrSourceBarcode.getMargin());
		jrTargetBarcode.setQrVersion(jrSourceBarcode.getQrVersion());
		jrTargetBarcode.setErrorCorrectionLevel(jrSourceBarcode.getErrorCorrectionLevel());
	}

	@Override
	public HashSet<String> generateGraphicalProperties() {
		HashSet<String> properties = super.generateGraphicalProperties();
		properties.add(QRCodeComponent.PROPERTY_ERROR_CORRECTION_LEVEL);
		properties.add(QRCodeComponent.PROPERTY_MARGIN);
		return properties;
	}
}
