/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.combo;

import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;

import com.jaspersoft.studio.help.HelpSystem;
import com.jaspersoft.studio.help.IHelp;
import com.jaspersoft.studio.help.IHelpRefBuilder;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.IPropertyDescriptorWidget;
import com.jaspersoft.studio.property.section.widgets.SPRWCombo;

public class RWComboBoxPropertyDescriptor extends ComboBoxPropertyDescriptor implements IPropertyDescriptorWidget,IHelp {
	
	protected String[] labels;
	
	protected boolean caseSensitive;
	
	protected SPRWCombo<RWComboBoxPropertyDescriptor> combo;

	private NullEnum canBeNull;
	
	protected RWComboBoxCellEditor cellEditor;

	private IHelpRefBuilder refBuilder;

	public RWComboBoxPropertyDescriptor(Object id, String displayName, String[] labelsArray, NullEnum canBeNull) {
		this(id, displayName, labelsArray, canBeNull, true);
	}

	public RWComboBoxPropertyDescriptor(Object id, String displayName, String[] labelsArray, NullEnum canBeNull,
			boolean caseSensitive) {
		super(id, displayName, labelsArray);
		labels = labelsArray;
		this.canBeNull = canBeNull;
		this.caseSensitive = caseSensitive;
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		cellEditor = new RWComboBoxCellEditor(parent, labels);
		if (getValidator() != null)
			cellEditor.setValidator(getValidator());
		HelpSystem.bindToHelp(this, cellEditor.getControl());
		return cellEditor;
	}

	public ILabelProvider getLabelProvider() {
		if (isLabelProviderSet()) {
			return super.getLabelProvider();
		}
		return new RWComboBoxLabelProvider(labels, canBeNull);
	}

	public void setItems(String[] items) {
		labels = items;
		//Avoid to set the items if they are already the same
		if (cellEditor != null && !cellEditor.getComboBox().isDisposed() && !Arrays.equals(labels, cellEditor.getItems())){
			cellEditor.setItems(items);
		}
		if (combo != null && !combo.getControl().isDisposed() && !Arrays.equals(labels, combo.getItems())){
			combo.setNewItems(this);
		}
	}

	public String[] getItems() {
		return labels;
	}
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	public ASPropertyWidget<RWComboBoxPropertyDescriptor> createWidget(Composite parent, AbstractSection section) {
		combo = new SPRWCombo<RWComboBoxPropertyDescriptor>(parent, section, this);
		return combo;
	}

	@Override
	public void setHelpRefBuilder(IHelpRefBuilder refBuilder) {
		this.refBuilder = refBuilder;
	}

	@Override
	public String getHelpReference() {
		if (refBuilder != null)
			return refBuilder.getHelpReference();
		return null;
	}
}
