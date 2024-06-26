/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.parameter;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.help.HelpReferenceBuilder;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.DefaultValue;
import com.jaspersoft.studio.model.IDragable;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.model.util.NodeIconDescriptor;
import com.jaspersoft.studio.property.descriptor.classname.ClassTypeComboCellEditor;
import com.jaspersoft.studio.property.descriptor.classname.NClassTypePropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.combo.RWComboBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.JSSValidatedTextPropertyDescriptor;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.SPClassTypeCombo;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.utils.compatibility.CompatibilityConstants;

/*
 * The Class MParameterSystem.
 * 
 * @author Chicu Veaceslav
 */
public class MParameterSystem extends APropertyNode implements IDragable {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	private IPropertyDescriptor[] descriptors;

	private static ParameterNameValidator validator;

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new NodeIconDescriptor("parameter-report"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/**
	 * Instantiates a new m parameter system.
	 */
	public MParameterSystem() {
		super();
		setEditable(false);
	}

	/**
	 * Instantiates a new m parameter system.
	 * 
	 * @param parent
	 *          the parent
	 * @param jrParameter
	 *          the jr parameter
	 * @param newIndex
	 *          the new index
	 */
	public MParameterSystem(ANode parent, JRDesignParameter jrParameter, int newIndex) {
		super(parent, newIndex);
		setEditable(false);
		setValue(jrParameter);
	}

	@Override
	public JRDesignParameter getValue() {
		return (JRDesignParameter) super.getValue();
	}

	@Override
	public Color getForeground() {
		return UIUtils.getSystemColor(CompatibilityConstants.Colors.COLOR_WIDGET_DISABLED_FOREGROUND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getImagePath()
	 */
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getDisplayText()
	 */
	public String getDisplayText() {
		return ((JRDesignParameter) getValue()).getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getToolTip()
	 */
	@Override
	public String getToolTip() {
		return getIconDescriptor().getToolTip();
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
	protected void postDescriptors(IPropertyDescriptor[] descriptors) {
		super.postDescriptors(descriptors);
		// Set into the validator the actual reference
		updateNameValidator();
	}

	protected void updateNameValidator() {
		if (validator == null) {
			validator = new ParameterNameValidator();
		}
		validator.setTargetNode(this);
	}
	
	/**
	 * Creates the property descriptors.
	 * 
	 * @param desc
	 *          the desc
	 */
	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		updateNameValidator();
		JSSValidatedTextPropertyDescriptor nameD = new JSSValidatedTextPropertyDescriptor(JRDesignParameter.PROPERTY_NAME,
				Messages.common_name, validator);
		nameD.setDescription(Messages.MParameterSystem_name_description);
		desc.add(nameD);

		NClassTypePropertyDescriptor classD = new NClassTypePropertyDescriptor(JRDesignParameter.PROPERTY_VALUE_CLASS_NAME,
				Messages.common_class, ClassTypeComboCellEditor.DEFAULT_ITEMS) {
			@Override
			public ASPropertyWidget<RWComboBoxPropertyDescriptor> createWidget(Composite parent, AbstractSection section) {
				SPClassTypeCombo<RWComboBoxPropertyDescriptor> classNameWidget = new SPClassTypeCombo<RWComboBoxPropertyDescriptor>(
						parent, section, this);
				classNameWidget.setClassesOfType(classes);
				classNameWidget.setReadOnly(readOnly);
				return classNameWidget;
			}
		};
		classD.setDescription(Messages.MParameterSystem_class_description);
		desc.add(classD);
		classD.setHelpRefBuilder(
				new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#parameter_class"));
	}

	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();
		defaultsMap.put(JRDesignParameter.PROPERTY_VALUE_CLASS_NAME, new DefaultValue("java.lang.String", false)); //$NON-NLS-1$
		return defaultsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		JRDesignParameter jrParameter = (JRDesignParameter) getValue();
		if (id.equals(JRDesignParameter.PROPERTY_NAME))
			return jrParameter.getName();
		if (id.equals(JRDesignParameter.PROPERTY_VALUE_CLASS_NAME))
			return jrParameter.getValueClassName();
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (JRDesignParameter.PROPERTY_NAME.equals(evt.getPropertyName())) {
			JRDesignDataset d = ModelUtils.getDataset(this);
			JRDesignParameter jrParameter = (JRDesignParameter) getValue();
			if (d != null) {
				d.getParametersMap().remove(evt.getOldValue());
				d.getParametersMap().put(jrParameter.getName(), jrParameter);
			}
		}
		super.propertyChange(evt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if (!isEditable())
			return;
		JRDesignParameter jrParameter = (JRDesignParameter) getValue();
		if (id.equals(JRDesignParameter.PROPERTY_NAME)) {
			if (!value.equals("")) {
				JRDesignDataset d = ModelUtils.getDataset(this);
				for (JRParameter p : d.getParametersList()) {
					if (p == jrParameter)
						continue;
					if (p.getName().equals(value)) {
						// warn?
						return;
					}
				}
				jrParameter.setName((String) value);
			}
		} else if (id.equals(JRDesignParameter.PROPERTY_VALUE_CLASS_NAME)) {
			jrParameter.setValueClassName((String) value);
		}
	}

	/**
	 * Creates the jr parameter.
	 * 
	 * @param jrDataset
	 *          the jr dataset
	 * @return the jR design parameter
	 */
	public static JRDesignParameter createJRParameter(JRDesignDataset jrDataset) {
		JRDesignParameter jrDesignParameter = new JRDesignParameter();
		jrDesignParameter.setSystemDefined(true);
		jrDesignParameter.setName(ModelUtils.getDefaultName(jrDataset.getParametersMap(), "Parameter")); //$NON-NLS-1$
		return jrDesignParameter;
	}
}
