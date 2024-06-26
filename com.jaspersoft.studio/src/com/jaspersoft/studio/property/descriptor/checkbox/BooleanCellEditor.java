/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.checkbox;

import org.eclipse.swt.widgets.Composite;

public class BooleanCellEditor extends StandardComboBoxCellEditor {
	protected static final int TRUE_INDEX = 0, FALSE_INDEX = 1;

	public BooleanCellEditor(Composite parent) {
		super(parent, new String[] { "true", "false" }, new Object[] { Boolean.TRUE, Boolean.FALSE });
	}

	/**
	 * Return an error message if this is not a valid boolean
	 */
	@Override
	protected String isCorrectObject(Object value) {
		if (value == null || value instanceof Boolean)
			return null;

		return "warning";
	}

}
