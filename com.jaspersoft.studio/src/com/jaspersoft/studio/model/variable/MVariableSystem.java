/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.variable;

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
import com.jaspersoft.studio.property.descriptors.JSSTextPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.JSSValidatedTextPropertyDescriptor;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.SPClassTypeCombo;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.utils.compatibility.CompatibilityConstants;

/*
 * The Class MVariableSystem.
 * 
 * @author Chicu Veaceslav
 */
public class MVariableSystem extends APropertyNode implements IDragable {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	private static IPropertyDescriptor[] descriptors;

	private static VariableNameValidator validator;

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new NodeIconDescriptor("variable"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/**
	 * Instantiates a new m variable system.
	 */
	public MVariableSystem() {
		super();
		setEditable(false);
	}

	/**
	 * Instantiates a new m variable system.
	 * 
	 * @param parent
	 *          the parent
	 * @param jrVariable
	 *          the jr variable
	 * @param newIndex
	 *          the new index
	 */
	public MVariableSystem(ANode parent, JRDesignVariable jrVariable, int newIndex) {
		super(parent, newIndex);
		setEditable(false);
		setValue(jrVariable);
	}

	@Override
	public JRDesignVariable getValue() {
		return (JRDesignVariable) super.getValue();
	}

	@Override
	public Color getForeground() {
		return UIUtils.getSystemColor(CompatibilityConstants.Colors.COLOR_WIDGET_DISABLED_FOREGROUND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getDisplayText()
	 */
	public String getDisplayText() {
		return ((JRDesignVariable) getValue()).getName();
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
			validator = new VariableNameValidator();
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
		JSSTextPropertyDescriptor nameD = new JSSValidatedTextPropertyDescriptor(JRDesignVariable.PROPERTY_NAME,
				Messages.common_name, validator);
		nameD.setDescription(Messages.MVariableSystem_name_description);
		desc.add(nameD);

		NClassTypePropertyDescriptor classD = new NClassTypePropertyDescriptor(JRDesignVariable.PROPERTY_VALUE_CLASS_NAME,
				Messages.common_value_class_name, ClassTypeComboCellEditor.DEFAULT_ITEMS) {
			@Override
			public ASPropertyWidget<RWComboBoxPropertyDescriptor> createWidget(Composite parent, AbstractSection section) {
				SPClassTypeCombo<RWComboBoxPropertyDescriptor> classNameWidget = new SPClassTypeCombo<RWComboBoxPropertyDescriptor>(
						parent, section, this);
				classNameWidget.setClassesOfType(classes);
				classNameWidget.setReadOnly(readOnly);
				return classNameWidget;
			}
		};
		classD.setDescription(Messages.MVariableSystem_value_class_name_description);
		desc.add(classD);
		classD.setHelpRefBuilder(
				new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#variable_class"));
	}

	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();
		defaultsMap.put(JRDesignVariable.PROPERTY_VALUE_CLASS_NAME, new DefaultValue("java.lang.String", false)); //$NON-NLS-1$
		return defaultsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		JRDesignVariable jrVariable = (JRDesignVariable) getValue();
		if (id.equals(JRDesignVariable.PROPERTY_NAME))
			return jrVariable.getName();
		if (id.equals(JRDesignVariable.PROPERTY_VALUE_CLASS_NAME))
			return jrVariable.getValueClassName();
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (JRDesignVariable.PROPERTY_NAME.equals(evt.getPropertyName())) {
			JRDesignDataset d = ModelUtils.getDataset(this);
			JRDesignVariable jrVariable = (JRDesignVariable) getValue();
			if (d != null) {
				d.getVariablesMap().remove(evt.getOldValue());
				d.getVariablesMap().put(jrVariable.getName(), jrVariable);
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
		JRDesignVariable jrVariable = (JRDesignVariable) getValue();
		if (id.equals(JRDesignVariable.PROPERTY_NAME)) {
			if (!value.equals("")) {
				JRDesignDataset d = ModelUtils.getDataset(this);
				for (JRVariable p : d.getVariablesList()) {
					if (p == jrVariable)
						continue;
					if (p.getName().equals(value)) {
						// warn?
						return;
					}
				}
				jrVariable.setName((String) value);
			}
		} else if (id.equals(JRDesignVariable.PROPERTY_VALUE_CLASS_NAME))
			jrVariable.setValueClassName((String) value);
	}

	/**
	 * Creates the jr variable.
	 * 
	 * @param jrDesign
	 *          the jr design
	 * @return the jR design variable
	 */
	public static JRDesignVariable createJRVariable(JasperDesign jrDesign) {
		JRDesignVariable jrDesignVariable = new JRDesignVariable();
		jrDesignVariable.setSystemDefined(true);
		jrDesignVariable.setName(ModelUtils.getDefaultName(jrDesign.getVariablesMap(), "Variable_")); //$NON-NLS-1$
		return jrDesignVariable;
	}

}
