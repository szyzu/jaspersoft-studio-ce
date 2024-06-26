/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.hyperlink.parameter;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.IExpressionContextSetter;
import com.jaspersoft.studio.property.descriptor.parameter.GenericParameterLabelProvider;
import com.jaspersoft.studio.property.descriptor.text.NTextPropertyDescriptor;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.SPHyperlinkParameter;

public class HyperlinkParameterPropertyDescriptor extends NTextPropertyDescriptor implements IExpressionContextSetter {

	private ExpressionContext expContext;

	public HyperlinkParameterPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public ExpressionContext getExpContext() {
		return expContext;
	}

	public CellEditor createPropertyEditor(Composite parent) {
		HyperlinkParameterCellEditor editor = new HyperlinkParameterCellEditor(parent);
		editor.setExpressionContext(expContext);
		return editor;
	}

	@Override
	public ILabelProvider getLabelProvider() {
		if (isLabelProviderSet()) {
			return super.getLabelProvider();
		}
		return new GenericParameterLabelProvider();
	}

	public void setExpressionContext(ExpressionContext expContext) {
		this.expContext = expContext;
	}

	public ASPropertyWidget<?> createWidget(Composite parent, AbstractSection section) {
		return new SPHyperlinkParameter(parent, section, this);
	}
}
