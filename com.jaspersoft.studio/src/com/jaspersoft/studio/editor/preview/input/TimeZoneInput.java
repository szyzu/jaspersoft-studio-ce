/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.input;

import java.util.Map;
import java.util.TimeZone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.jaspersoft.studio.swt.widgets.WTimeZone;

public class TimeZoneInput extends ADataInput {
	private WTimeZone txt;

	public boolean isForType(Class<?> valueClass) {
		return TimeZone.class.isAssignableFrom(valueClass);
	}

	@Override
	public void createInput(Composite parent, final IParameter param, final Map<String, Object> params) {
		super.createInput(parent, param, params);
		if (TimeZone.class.isAssignableFrom(param.getValueClass())) {
			txt = new WTimeZone(parent, SWT.DROP_DOWN | SWT.BORDER);
			txt.setToolTipText(param.getDescription());
			txt.addFocusListener(focusListener);
			txt.addTraverseListener(keyListener);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalIndent = 8;
			txt.setLayoutData(gd);
			txt.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateModel(txt.getTimeZone());
					updateInput();
				}

			});
			txt.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					updateModel(txt.getTimeZone());
					updateInput();
				}
			});

			updateInput();
			for (Control c : txt.getChildren())
				setNullable(param, c);
		}
	}

	public void updateInput() {
		Object value = params.get(param.getName());
		if (value != null && value instanceof String)
			value = TimeZone.getTimeZone((String) value);
		if (value != null && value instanceof TimeZone)
			txt.setSelection((TimeZone) params.get(param.getName()));
		setDecoratorNullable(param);
	}
}
