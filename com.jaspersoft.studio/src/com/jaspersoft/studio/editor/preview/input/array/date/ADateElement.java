/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.input.array.date;

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.jaspersoft.studio.editor.preview.input.ADataInput;
import com.jaspersoft.studio.editor.preview.input.array.AWElement;

public abstract class ADateElement extends AWElement {

	protected CDateTime date;

	protected abstract int getStyle();

	protected abstract Date getDate();

	@Override
	public Control createControl(Composite parent) {
		date = new CDateTime(parent, CDT.BORDER | getStyle() | CDT.DROP_DOWN);
		GridData gd = new GridData();
		gd.horizontalIndent = 8;
		gd.widthHint = 25 * ADataInput.getCharWidth(date);
		date.setLayoutData(gd);
		date.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setValue(getDate());
			}
		});
		if (getValue() != null && getValue() instanceof Date)
			date.setSelection((Date) getValue());
		return date;
	}
}
