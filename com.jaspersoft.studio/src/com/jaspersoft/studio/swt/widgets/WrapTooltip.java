/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

public class WrapTooltip extends org.eclipse.jface.window.ToolTip {
	private Label label;
	private String text;

	public WrapTooltip(Control control) {
		super(control, SWT.NONE, true);
		setRespectDisplayBounds(true);
		setRespectMonitorBounds(true);
	}

	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.marginHeight = layout.marginWidth = 5;
		composite.setLayout(layout);
		composite.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		this.label = new Label(composite, SWT.WRAP);
		this.label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		this.label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		this.label.setText(this.text);
		return composite;
	}

	/**
	 * Sets the tooltip text
	 * 
	 * @param text
	 *          the tooltip text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * returns the tooltip text
	 * 
	 * @return the tooltip text
	 */
	public String getText() {
		return this.text;
	}
}
