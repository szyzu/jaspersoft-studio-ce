/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.parameter;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.IExpressionContextSetter;
import com.jaspersoft.studio.property.descriptor.text.NTextPropertyDescriptor;

public class ParameterPropertyDescriptor extends NTextPropertyDescriptor implements IExpressionContextSetter {
	private ExpressionContext expContext;
	
	public ParameterPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		ParameterCellEditor editor = new ParameterCellEditor(parent);
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
		this.expContext=expContext;
	}
}
