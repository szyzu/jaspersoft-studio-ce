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
import com.jaspersoft.studio.property.descriptors.JSSComboPropertyDescriptor;

import net.sf.jasperreports.components.barcode4j.EAN13Component;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.component.ComponentKey;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;

public class MEAN13 extends MBarcode4j {
	
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static IPropertyDescriptor[] descriptors;

	public MEAN13() {
		super();
	}

	public MEAN13(ANode parent, JRDesignComponentElement jrBarcode, int newIndex) {
		super(parent, jrBarcode, newIndex);
	}

	@Override
	public JRDesignComponentElement createJRElement(JasperDesign jasperDesign, boolean applyDefault) {
		JRDesignComponentElement el = new JRDesignComponentElement();
		EAN13Component component = new EAN13Component();
		JRDesignExpression exp = new JRDesignExpression();
		exp.setText("\"123456789012\""); //$NON-NLS-1$
		component.setCodeExpression(exp);
		el.setComponent(component);
		el.setComponentKey(new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "EAN13")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
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

		JSSComboPropertyDescriptor checksumModeD = new JSSComboPropertyDescriptor(
				EAN13Component.PROPERTY_CHECKSUM_MODE,
				Messages.common_checksum_mode, ChecksumMode.getItems());
		checksumModeD.setDescription(Messages.MEAN13_checksum_mode_description);
		desc.add(checksumModeD);

		checksumModeD.setCategory(Messages.MEAN13_properties_category);
	}

	@Override
	public Object getPropertyValue(Object id) {
		JRDesignComponentElement jrElement = (JRDesignComponentElement) getValue();
		EAN13Component jrList = (EAN13Component) jrElement.getComponent();

		if (id.equals(EAN13Component.PROPERTY_CHECKSUM_MODE))
			return ChecksumMode.getPos4ChecksumMode(jrList.getChecksumMode());
		return super.getPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		JRDesignComponentElement jrElement = (JRDesignComponentElement) getValue();
		EAN13Component jrList = (EAN13Component) jrElement.getComponent();

		if (id.equals(EAN13Component.PROPERTY_CHECKSUM_MODE))
			jrList.setChecksumMode(ChecksumMode
					.getChecksumMode4Pos((Integer) value));

		super.setPropertyValue(id, value);
	}
	
	@Override
	public HashSet<String> generateGraphicalProperties() {
		HashSet<String> properties = super.generateGraphicalProperties();
		properties.add(EAN13Component.PROPERTY_CHECKSUM_MODE);
		return properties;
	}
	
	@Override
	public void trasnferProperties(JRElement target){
		super.trasnferProperties(target);
		
		JRDesignComponentElement jrSourceElement = (JRDesignComponentElement) getValue();
		EAN13Component jrSourceBarcode = (EAN13Component) jrSourceElement.getComponent();
		
		JRDesignComponentElement jrTargetElement = (JRDesignComponentElement) target;
		EAN13Component jrTargetBarcode = (EAN13Component) jrTargetElement.getComponent();
		
		jrTargetBarcode.setChecksumMode(jrSourceBarcode.getChecksumMode());
	}
}
