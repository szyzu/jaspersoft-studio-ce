/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.dataset.fields.table.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.jaspersoft.studio.property.dataset.fields.table.TColumn;

import net.sf.jasperreports.eclipse.util.Misc;

public abstract class AWControl {
	protected AWidget aw;
	private Label lbl;

	public AWControl(AWidget aw) {
		this.aw = aw;
	}

	public abstract void addDisposeListener(DisposeListener dlistener);

	protected abstract void createControl(Composite parent);

	protected abstract void fillValue();

	protected void createLabel(Composite parent, TColumn c) {
		lbl = new Label(parent, SWT.NONE);
		lbl.setText(Misc.nvl(c.getLabel(), c.getPropertyName()));
	}

	protected String getText() {
		return Misc.nvl(aw.getValue(), "");
	}

	public abstract void setEnabled(boolean en);

	public void dispose() {
		if (lbl != null && !lbl.isDisposed())
			lbl.dispose();
	}
}
