/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.input.array.number;

import java.text.NumberFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.editor.preview.input.ADataInput;
import com.jaspersoft.studio.editor.preview.input.array.StringElement;

public abstract class ANumberElement extends StringElement {
	@Override
	protected int getStyle() {
		return super.getStyle() | SWT.RIGHT;
	}

	@Override
	public Control createControl(Composite parent) {
		super.createControl(parent);
		text.addListener(SWT.Verify, new Listener() {

			public void handleEvent(Event e) {
				try {
					ADataInput.hideError(text);
					String number = e.text;
					String oldText = ((Text) e.widget).getText();
					if (e.start != e.end)
						oldText = oldText.substring(0, e.start) + oldText.substring(e.end);
					number = oldText.substring(0, e.start) + e.text;
					if (oldText.length() - 1 > e.start + 1)
						number += oldText.substring(e.start + 1);

					if (number.equals("-")) //$NON-NLS-1$
						number = "-0";//$NON-NLS-1$
					if (number.equals(".")) //$NON-NLS-1$
						number = "0.";//$NON-NLS-1$

					if (number.isEmpty()) {
						e.doit = true;
						return;
					}
					e.doit = isValid(number);
				} catch (NumberFormatException ne) {
					e.doit = false;
				}
			}
		});
		try {
			if (getValue() != null && getValue() instanceof Number)
				text.setText(NumberFormat.getInstance().format(getValue()));
		} catch (Throwable e) {
		}
		return text;
	}

	protected abstract boolean isValid(String number);
}
