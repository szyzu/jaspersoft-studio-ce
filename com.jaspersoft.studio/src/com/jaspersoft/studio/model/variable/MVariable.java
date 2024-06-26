/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.help.HelpReferenceBuilder;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.DefaultValue;
import com.jaspersoft.studio.model.ICopyable;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.model.util.NodeIconDescriptor;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.classname.NClassTypePropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.combo.RWComboBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;
import com.jaspersoft.studio.property.descriptor.expression.JRExpressionPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.text.NTextPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.NamedEnumPropertyDescriptor;
import com.jaspersoft.studio.utils.EnumHelper;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.util.StringUtils;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.fill.JRIncrementerFactory;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;

/*
 * The Class MVariable.
 * 
 * @author Chicu Veaceslav
 */
public class MVariable extends MVariableSystem implements ICopyable {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	private static NamedEnumPropertyDescriptor<CalculationEnum> calculationD;
	private static NamedEnumPropertyDescriptor<ResetTypeEnum> resetTypeD;
	private static NamedEnumPropertyDescriptor<IncrementTypeEnum> incrementTypeD;
	private RWComboBoxPropertyDescriptor resetGroupD;
	private RWComboBoxPropertyDescriptor incrementGroupD;
	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;
	private IPropertyDescriptor[] descriptors;

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
	 * Instantiates a new m variable.
	 */
	public MVariable() {
		super();
		setEditable(true);
	}

	@Override
	public Color getForeground() {
		return null;
	}

	/**
	 * Instantiates a new m variable.
	 * 
	 * @param parent
	 *          the parent
	 * @param jrVariable
	 *          the jr variable
	 * @param newIndex
	 *          the new index
	 */
	public MVariable(ANode parent, JRDesignVariable jrVariable, int newIndex) {
		super(parent, jrVariable, newIndex);
		setEditable(true);
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
	 * When the descriptor are read the group information are updated
	 */
	@Override
	protected void postDescriptors(IPropertyDescriptor[] descriptors) {
		super.postDescriptors(descriptors);
		// Don't update the descriptor when the parent is null (element removed)
		if (getParent() != null) {
			String[] items = getGroupList();
			if (items != null) {
				resetGroupD.setItems(items);
				incrementGroupD.setItems(items);
			}
		}
	}

	/**
	 * Creates the property descriptors.
	 * 
	 * @param desc
	 *          the desc
	 */
	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		super.createPropertyDescriptors(desc);
		
		NTextPropertyDescriptor descriptionD = new NTextPropertyDescriptor(JRDesignVariable.PROPERTY_DESCRIPTION,
				Messages.common_description);
		descriptionD.setDescription(Messages.MVariable_description_description);
		desc.add(descriptionD);
		descriptionD.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#variableDescription")); //$NON-NLS-1$

		resetGroupD = new RWComboBoxPropertyDescriptor(JRDesignVariable.PROPERTY_RESET_GROUP, Messages.common_reset_group,
				new String[] { "" }, NullEnum.NULL); //$NON-NLS-1$
		resetGroupD.setDescription(Messages.MVariable_reset_group_description);
		desc.add(resetGroupD);

		incrementGroupD = new RWComboBoxPropertyDescriptor(JRDesignVariable.PROPERTY_INCREMENT_GROUP,
				Messages.common_increment_group, new String[] { "" }, NullEnum.NULL); //$NON-NLS-1$
		incrementGroupD.setDescription(Messages.MVariable_increment_group_description);
		desc.add(incrementGroupD);

		calculationD = new NamedEnumPropertyDescriptor<CalculationEnum>(JRDesignVariable.PROPERTY_CALCULATION,
				Messages.MVariable_calculation, CalculationEnum.AVERAGE, NullEnum.NOTNULL);
		calculationD.setDescription(Messages.MVariable_calculation_description);
		desc.add(calculationD);

		resetTypeD = new NamedEnumPropertyDescriptor<ResetTypeEnum>(JRDesignVariable.PROPERTY_RESET_TYPE,
				Messages.common_reset_type, ResetTypeEnum.COLUMN, NullEnum.NOTNULL);
		resetTypeD.setDescription(Messages.MVariable_reset_type_description);
		desc.add(resetTypeD);

		incrementTypeD = new NamedEnumPropertyDescriptor<IncrementTypeEnum>(JRDesignVariable.PROPERTY_INCREMENT_TYPE,
				Messages.common_increment_type, IncrementTypeEnum.COLUMN, NullEnum.NOTNULL);
		incrementTypeD.setDescription(Messages.MVariable_increment_type_description);
		desc.add(incrementTypeD);

		JRExpressionPropertyDescriptor expressionD = new JRExpressionPropertyDescriptor(
				JRDesignVariable.PROPERTY_EXPRESSION, Messages.common_expression);
		expressionD.setDescription(Messages.MVariable_expression_description);
		desc.add(expressionD);
		expressionD.setHelpRefBuilder(
				new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#variableExpression")); //$NON-NLS-1$

		JRExpressionPropertyDescriptor iniValExprD = new JRExpressionPropertyDescriptor(
				JRDesignVariable.PROPERTY_INITIAL_VALUE_EXPRESSION, Messages.MVariable_initial_value_expression);
		iniValExprD.setDescription(Messages.MVariable_initial_value_expression_description);
		desc.add(iniValExprD);
		iniValExprD.setHelpRefBuilder(
				new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#initialValueExpression")); //$NON-NLS-1$

		NClassTypePropertyDescriptor factoryClassName = new NClassTypePropertyDescriptor(
				JRDesignVariable.PROPERTY_INCREMENTER_FACTORY_CLASS_NAME, Messages.MVariable_incrementer_factory_class_name);
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(JRIncrementerFactory.class);
		factoryClassName.setClasses(classes);
		factoryClassName.setDescription(Messages.MVariable_incrementer_factory_class_name_description);
		desc.add(factoryClassName);

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#variable"); //$NON-NLS-1$
	}

	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();

		defaultsMap.put(JRDesignVariable.PROPERTY_RESET_TYPE, new DefaultValue(ResetTypeEnum.REPORT, false));
		defaultsMap.put(JRDesignVariable.PROPERTY_INCREMENT_TYPE, new DefaultValue(IncrementTypeEnum.NONE, false));

		int calculationValue = NamedEnumPropertyDescriptor.getIntValue(CalculationEnum.NOTHING, NullEnum.NOTNULL,
				CalculationEnum.NOTHING);
		defaultsMap.put(JRDesignVariable.PROPERTY_CALCULATION, new DefaultValue(calculationValue, false));

		return defaultsMap;
	}

	public ExpressionContext getExpressionContext() {
		JRDesignDataset dataSet = getDataSet();
		JasperReportsConfiguration conf = getJasperConfiguration();
		if (dataSet != null && conf != null)
			return new ExpressionContext(dataSet, conf);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.variable.MVariableSystem#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		JRDesignVariable jrVariable = (JRDesignVariable) getValue();
		Object s = super.getPropertyValue(id);
		if (s != null)
			return s;
		if (id.equals(JRDesignVariable.PROPERTY_DESCRIPTION)) {
			return jrVariable.getDescription();
		}
		if (id.equals(JRDesignVariable.PROPERTY_RESET_GROUP)) {
			if (jrVariable.getResetTypeValue().equals(ResetTypeEnum.GROUP) && resetGroupD != null) {
				if (jrVariable.getResetGroup() != null) {
					return jrVariable.getResetGroup().getName();
				}
			}
			return ""; //$NON-NLS-1$
		}
		if (id.equals(JRDesignVariable.PROPERTY_INCREMENT_GROUP)) {
			if (jrVariable.getIncrementTypeValue().equals(IncrementTypeEnum.GROUP) && incrementGroupD != null) {
				if (jrVariable.getIncrementGroup() != null) {
					return jrVariable.getIncrementGroup().getName();
				}
			}
			return ""; //$NON-NLS-1$
		}
		if (id.equals(JRDesignVariable.PROPERTY_CALCULATION))
			return calculationD.getIntValue(jrVariable.getCalculationValue());
		if (id.equals(JRDesignVariable.PROPERTY_RESET_TYPE))
			return jrVariable.getResetTypeValue();
		if (id.equals(JRDesignVariable.PROPERTY_INCREMENT_TYPE))
			return jrVariable.getIncrementTypeValue();
		if (id.equals(JRDesignVariable.PROPERTY_INCREMENTER_FACTORY_CLASS_NAME))
			return jrVariable.getIncrementerFactoryClassName();
		if (id.equals(JRDesignVariable.PROPERTY_EXPRESSION)) {
			return ExprUtil.getExpression(jrVariable.getExpression());
		}
		if (id.equals(JRDesignVariable.PROPERTY_INITIAL_VALUE_EXPRESSION)) {
			return ExprUtil.getExpression(jrVariable.getInitialValueExpression());
		}
		return null;
	}

	/**
	 * Return the list of group for the dataset parent of the variable or null if there isn't any group
	 * 
	 * @return An array of string with the name of every group inside the variable dataset, or null if there aren't groups
	 */
	private String[] getGroupList() {
		JRDesignDataset jrDataset = getDataSet();
		JRGroup[] groups = jrDataset.getGroups();
		String[] items = null;
		if (groups != null) {
			items = new String[groups.length + 1];
			items[0] = ""; //$NON-NLS-1$
			for (int j = 0; j < groups.length; j++) {
				items[j + 1] = groups[j].getName();
			}
		}
		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.variable.MVariableSystem#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		super.setPropertyValue(id, value);
		JRDesignVariable jrVariable = (JRDesignVariable) getValue();
		if (id.equals(JRDesignVariable.PROPERTY_RESET_GROUP)) {
			if (value != null && !value.equals("")) { //$NON-NLS-1$
				JRDesignDataset jrDataset = getDataSet();
				JRGroup group = (JRGroup) jrDataset.getGroupsMap().get(value);
				jrVariable.setResetGroup(group);
			} else {
				jrVariable.setResetGroup(null);
			}
		} else if (id.equals(JRDesignVariable.PROPERTY_DESCRIPTION)) { 
			if (StringUtils.isNullOrEmpty((String) value)) {
				jrVariable.setDescription(null);
			} else {
				jrVariable.setDescription((String) value);
			}
		} else if (id.equals(JRDesignVariable.PROPERTY_EXPRESSION))
			jrVariable.setExpression(ExprUtil.setValues(jrVariable.getExpression(), value));
		else if (id.equals(JRDesignVariable.PROPERTY_INITIAL_VALUE_EXPRESSION))
			jrVariable.setInitialValueExpression(ExprUtil.setValues(jrVariable.getInitialValueExpression(), value));
		else if (id.equals(JRDesignVariable.PROPERTY_INCREMENT_GROUP)) {
			if (value != null && !value.equals("")) { //$NON-NLS-1$
				JRDesignDataset jrDataset = getDataSet();
				JRGroup group = (JRGroup) jrDataset.getGroupsMap().get(value);
				jrVariable.setIncrementGroup(group);
			} else
				jrVariable.setIncrementGroup(null);
		} else if (id.equals(JRDesignVariable.PROPERTY_CALCULATION))
			jrVariable.setCalculation(calculationD.getEnumValue(value));
		else if (id.equals(JRDesignVariable.PROPERTY_RESET_TYPE)) {
			jrVariable.setResetType(EnumHelper.getEnumByObjectValue(ResetTypeEnum.values(), value));
			if (!jrVariable.getResetTypeValue().equals(ResetTypeEnum.GROUP))
				jrVariable.setResetGroup(null);
		} else if (id.equals(JRDesignVariable.PROPERTY_INCREMENT_TYPE)) {
			jrVariable.setIncrementType(EnumHelper.getEnumByObjectValue(incrementTypeD.getEnumElements(), value));
			if (!jrVariable.getIncrementTypeValue().equals(IncrementTypeEnum.GROUP))
				jrVariable.setIncrementGroup(null);
		} else if (id.equals(JRDesignVariable.PROPERTY_INCREMENTER_FACTORY_CLASS_NAME)) {
			if (value == null || ((String) value).isEmpty()) {
				jrVariable.setIncrementerFactoryClassName(null);
			} else {
				jrVariable.setIncrementerFactoryClassName((String) value);
			}
		}
	}

	protected JRDesignDataset getDataSet() {
		return ModelUtils.getDataset(this);
	}

	/**
	 * Creates the jr variable.
	 * 
	 * @param jrDataset
	 *          the jr dataset
	 * @return the jR design variable
	 */
	public static JRDesignVariable createJRVariable(JRDesignDataset jrDataset) {
		JRDesignVariable jrDesignVariable = new JRDesignVariable();
		jrDesignVariable.setSystemDefined(false);
		jrDesignVariable.setName(ModelUtils.getDefaultName(jrDataset.getVariablesMap(), "Variable_")); //$NON-NLS-1$
		return jrDesignVariable;
	}

	public ICopyable.RESULT isCopyable2(Object parent) {
		if (parent instanceof MVariables)
			return ICopyable.RESULT.COPYABLE;
		return ICopyable.RESULT.CHECK_PARENT;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (ExpressionContext.class.equals(adapter)) {
			return getExpressionContext();
		}
		return super.getAdapter(adapter);
	}

	@Override
	public boolean isCuttable(ISelection currentSelection) {
		return true;
	}
}
