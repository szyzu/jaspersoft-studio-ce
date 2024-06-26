/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.color;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.help.HelpSystem;
import com.jaspersoft.studio.help.IHelp;
import com.jaspersoft.studio.help.IHelpRefBuilder;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.IPropertyDescriptorWidget;
import com.jaspersoft.studio.property.section.widgets.SPColor;

public class ColorPropertyDescriptor extends org.eclipse.ui.views.properties.ColorPropertyDescriptor implements
		IPropertyDescriptorWidget, IHelp {
	private NullEnum canBeNull;
	private boolean useTransparency;

	public ColorPropertyDescriptor(Object id, String displayName, NullEnum canBeNull) {
		this(id,displayName,canBeNull,true);
	}
	
	public ColorPropertyDescriptor(Object id, String displayName, NullEnum canBeNull, boolean useTransparency) {
		super(id, displayName);
		this.canBeNull = canBeNull;
		this.useTransparency = useTransparency;
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new ColorCellEditor(parent);
		if (getValidator() != null)
			editor.setValidator(getValidator());
		HelpSystem.bindToHelp(this, editor.getControl());
		return editor;
	}

	@Override
	public ILabelProvider getLabelProvider() {
		if (isLabelProviderSet()) {
			return super.getLabelProvider();
		}
		return new ColorLabelProvider(canBeNull);
	}

	public ASPropertyWidget<?> createWidget(Composite parent, AbstractSection section) {
		return new SPColor(parent, section, this);
	}

	private IHelpRefBuilder refBuilder;

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
	
	public boolean supportsTransparency() {
		return this.useTransparency;
	}
}
