/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section.report.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class UnitsWidget {

	private Combo unitc;

	public void createComponent(Composite parent, String label, String toolTip, int span) {
		Label lbl = new Label(parent, SWT.NONE);
		lbl.setText(label);
		lbl.setBackground(parent.getBackground());

		unitc = new Combo(parent, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		unitc.setItems(Unit.getUnits());
		unitc.setToolTipText(toolTip);
		unitc.select(0);

		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		unitc.setLayoutData(gd);
	}

	public String getUnit() {
		return Unit.getUnits()[unitc.getSelectionIndex()];
	}

	public void addSelectionListener(SelectionListener listener) {
		unitc.addSelectionListener(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		unitc.removeSelectionListener(listener);
	}

	public void setUnit(String key) {
		unitc.select(Unit.getUnitIndex(key));
	}
}
