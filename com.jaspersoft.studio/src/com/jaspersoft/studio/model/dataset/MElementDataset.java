/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.dataset;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.help.HelpReferenceBuilder;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.IContainer;
import com.jaspersoft.studio.model.IContainerEditPart;
import com.jaspersoft.studio.model.dataset.descriptor.DatasetRunPropertyDescriptor;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.model.util.NodeIconDescriptor;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.combo.RComboBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;
import com.jaspersoft.studio.property.descriptor.expression.JRExpressionPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.NamedEnumPropertyDescriptor;
import com.jaspersoft.studio.utils.EnumHelper;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.DatasetResetTypeEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;

public class MElementDataset extends APropertyNode implements IContainer, IContainerEditPart {
	private static IIconDescriptor iconDescriptor;

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private IPropertyDescriptor[] descriptors;

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new NodeIconDescriptor("dataset"); //$NON-NLS-1$
		return iconDescriptor;
	}

	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	public String getDisplayText() {
		return getIconDescriptor().getTitle();
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

	public MElementDataset(JRElementDataset value, JasperDesign jasperDesign) {
		super();
		setValue(value);
		this.jasperDesign = jasperDesign;
	}

	public MElementDataset(ANode parent, JRElementDataset value, JasperDesign jasperDesign) {
		super(parent, -1);
		setValue(value);
		this.jasperDesign = jasperDesign;
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
		resetTypeD = new NamedEnumPropertyDescriptor<>(JRDesignElementDataset.PROPERTY_DATASET_RESET_TYPE,
				Messages.common_reset_type, ResetTypeEnum.COLUMN, NullEnum.NOTNULL);
		resetTypeD.setDescription(Messages.MElementDataset_reset_type_description);
		desc.add(resetTypeD);

		inctypeD = new NamedEnumPropertyDescriptor<>(JRDesignElementDataset.PROPERTY_INCREMENT_TYPE,
				Messages.common_increment_type, IncrementTypeEnum.COLUMN, NullEnum.NOTNULL);
		inctypeD.setDescription(Messages.MElementDataset_increment_type_description);
		desc.add(inctypeD);

		JRExpressionPropertyDescriptor incWhenExprD = new JRExpressionPropertyDescriptor(
				JRDesignElementDataset.PROPERTY_INCREMENT_WHEN_EXPRESSION,
				Messages.MElementDataset_increment_when_expression);
		incWhenExprD.setDescription(Messages.MElementDataset_increment_when_expression_description);
		desc.add(incWhenExprD);
		incWhenExprD.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#incrementWhenExpression"));

		resetGroupD = new RComboBoxPropertyDescriptor(JRDesignElementDataset.PROPERTY_RESET_GROUP,
				Messages.common_reset_group, new String[] { "" }); //$NON-NLS-1$
		resetGroupD.setDescription(Messages.MElementDataset_reset_group_description);
		desc.add(resetGroupD);

		incGroupD = new RComboBoxPropertyDescriptor(JRDesignElementDataset.PROPERTY_INCREMENT_GROUP,
				Messages.common_increment_group, new String[] { "" }); //$NON-NLS-1$
		incGroupD.setDescription(Messages.MElementDataset_increment_group_description);
		desc.add(incGroupD);

		DatasetRunPropertyDescriptor datasetRunD = new DatasetRunPropertyDescriptor(
				JRDesignElementDataset.PROPERTY_DATASET_RUN, Messages.MElementDataset_dataset_run);
		datasetRunD.setDescription(Messages.MElementDataset_dataset_run_description);
		desc.add(datasetRunD);
		datasetRunD.setHelpRefBuilder(
				new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#datasetRun"));

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#dataset");
	}

	@Override
	protected void postDescriptors(IPropertyDescriptor[] descriptors) {
		super.postDescriptors(descriptors);
		// initialize style
		JasperDesign jd = getJasperDesign();
		if (jd != null && getValue() != null) {
			JRDataset dataset = getElementDataset();
			// Calculate the groups list for the current element
			if (dataset != null) {
				JRGroup[] groups = dataset.getGroups();
				String[] items = new String[groups.length + 1];
				items[0] = ""; // always add empty for <NULL>
				for (int j = 0; j < groups.length; j++) {
					items[j + 1] = groups[j].getName();
				}
				setGroupItems(items);
			}
		}
	}

	/**
	 * Return the dataset used by the element
	 * 
	 * @return the dataset nearest to this element
	 */
	public JRDataset getElementDataset() {
		JRDataset dataset = ModelUtils.getDataset(this);
		if (dataset == null && getJasperDesign() != null) {
			dataset = getJasperDesign().getMainDataset();
		}
		return dataset;
	}

	public void setGroupItems(String[] items) {
		if (resetGroupD == null) {
			for (IPropertyDescriptor p : getPropertyDescriptors()) {
				if (p.getId().equals(JRDesignElementDataset.PROPERTY_RESET_GROUP))
					((RComboBoxPropertyDescriptor) p).setItems(items);
				else if (p.getId().equals(JRDesignElementDataset.PROPERTY_INCREMENT_GROUP))
					((RComboBoxPropertyDescriptor) p).setItems(items);
			}
		}
		if (resetGroupD != null)
			resetGroupD.setItems(items);
		if (incGroupD != null)
			incGroupD.setItems(items);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.
	 * lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		JRDesignElementDataset jrElement = (JRDesignElementDataset) getValue();
		if (jrElement == null)
			return null;
		if (id.equals(JRDesignElementDataset.PROPERTY_DATASET_RESET_TYPE))
			return jrElement.getDatasetResetType();
		if (id.equals(JRDesignElementDataset.PROPERTY_INCREMENT_TYPE))
			return jrElement.getIncrementTypeValue();
		if (id.equals(JRDesignElementDataset.PROPERTY_INCREMENT_WHEN_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getIncrementWhenExpression());
		if (id.equals(JRDesignElementDataset.PROPERTY_RESET_GROUP)) {
			if (jrElement.getResetGroup() != null)
				return jrElement.getResetGroup().getName();
			return ""; //$NON-NLS-1$
		}
		if (id.equals(JRDesignElementDataset.PROPERTY_INCREMENT_GROUP)) {
			if (jrElement.getIncrementGroup() != null)
				return jrElement.getIncrementGroup().getName();
			return ""; //$NON-NLS-1$
		}
		if (id.equals(JRDesignElementDataset.PROPERTY_DATASET_RUN)) {
			JRDatasetRun j = jrElement.getDatasetRun();
			if (j == null)
				j = new JRDesignDatasetRun();
			if (mDatasetRun != null)
				mDatasetRun.setValue(j);
			else
				mDatasetRun = new MDatasetRun(j, getJasperDesign());
			setChildListener(mDatasetRun);
			return mDatasetRun;
		}
		return null;
	}

	private MDatasetRun mDatasetRun;
	private RComboBoxPropertyDescriptor incGroupD;
	private RComboBoxPropertyDescriptor resetGroupD;

	private JasperDesign jasperDesign;
	private static NamedEnumPropertyDescriptor<ResetTypeEnum> resetTypeD;
	private static NamedEnumPropertyDescriptor<IncrementTypeEnum> inctypeD;

	@Override
	public JasperDesign getJasperDesign() {
		return jasperDesign;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.
	 * lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		JRDesignElementDataset jrElement = (JRDesignElementDataset) getValue();
		if (jrElement == null)
			return;
		if (id.equals(JRDesignElementDataset.PROPERTY_INCREMENT_TYPE))
			jrElement.setIncrementType(EnumHelper.getEnumByObjectValue(IncrementTypeEnum.values(), value));
		else if (id.equals(JRDesignElementDataset.PROPERTY_DATASET_RESET_TYPE))
			jrElement.setResetType(EnumHelper.getEnumByObjectValue(DatasetResetTypeEnum.values(), value));
		else if (id.equals(JRDesignElementDataset.PROPERTY_INCREMENT_WHEN_EXPRESSION))
			jrElement.setIncrementWhenExpression(ExprUtil.setValues(jrElement.getIncrementWhenExpression(), value));
		else if (id.equals(JRDesignElementDataset.PROPERTY_INCREMENT_GROUP)) {
			if (value != null && !value.equals("")) { //$NON-NLS-1$
				JRGroup group = getJasperDesign().getGroupsMap().get(value);
				jrElement.setIncrementGroup(group);
			}
		} else if (id.equals(JRDesignElementDataset.PROPERTY_RESET_GROUP)) {
			if (value != null && !value.equals("")) { //$NON-NLS-1$
				JRGroup group = getJasperDesign().getGroupsMap().get(value);
				jrElement.setResetGroup(group);
			}
		} else if (id.equals(JRDesignElementDataset.PROPERTY_DATASET_RUN)) {
			if (value == null) {
				jrElement.setDatasetRun(null);
			} else {
				MDatasetRun mdr = (MDatasetRun) value;
				JRDesignDatasetRun dr = mdr.getValue();
				if (dr.getDatasetName() != null)
					jrElement.setDatasetRun(dr);
				else
					jrElement.setDatasetRun(null);
			}
		}
	}

}
