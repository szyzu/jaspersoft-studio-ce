/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.designer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.jaspersoft.studio.JaspersoftStudioPlugin;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.Misc;

public class QueryStatus extends AQueryStatus {
	private ToolItem msgItem;
	private ToolBar toolBar;

	public QueryStatus(Composite composite) {
		createStatusBar(composite);
	}

	public void showError(final Throwable t) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				if (msgItem.isDisposed())
					return;
				msgItem.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/eclipse/obj16/error_tsk.gif"));
				setMessage(t, t.getMessage(), true);
			}
		});
	}

	public void showError(final String message, final Throwable t) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				if (msgItem.isDisposed())
					return;
				msgItem.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/eclipse/obj16/error_tsk.gif"));
				setMessage(t, message, true);
			}
		});
	}

	public void showWarning(final String msg) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				if (msgItem.isDisposed())
					return;
				msgItem.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/eclipse/obj16/warn_tsk.gif"));
				setMessage(null, msg, true);
			}
		});
	}

	public void showInfo(final String msg) {
		UIUtils.getDisplay().syncExec(new Runnable() {

			public void run() {
				if (msgItem.isDisposed())
					return;
				msgItem.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/eclipse/obj16/info_tsk.gif"));
				setMessage(null, msg, false);
			}
		});
	}

	protected void setMessage(final Throwable t, String message, boolean enabled) {
		super.setMessage(t, message, enabled);
		cutMessage();
		msgItem.setToolTipText(Misc.nvl(msg));
		msgItem.setEnabled(enabled);
		toolBar.setVisible(msg != null && !msg.trim().isEmpty());
		toolBar.getParent().layout();
	}

	protected void createStatusBar(final Composite comp) {
		toolBar = new ToolBar(comp, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		msgItem = new ToolItem(toolBar, SWT.PUSH);
		showInfo("Build a query for your report.");
		msgItem.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (t != null)
					UIUtils.showError(msg, t);
				else {
					UIUtils.showWarning(msg);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		toolBar.addControlListener(new ControlListener() {

			public void controlResized(ControlEvent e) {
				cutMessage();
			}

			public void controlMoved(ControlEvent e) {
			}
		});
	}

	public void cutMessage() {
		String str = Misc.nvl(msg);
		int endIndex = str.length();
		if (endIndex > 0) {
			int w = toolBar.getBounds().width - 70;
			GC gc = new GC(toolBar);
			try {
				endIndex = Math.max(w / gc.getFontMetrics().getAverageCharWidth(), 0);
			} finally {
				gc.dispose();
			}
			if (str.contains("\n")) {
				// avoid new line
				endIndex = Math.min(str.indexOf('\n'), endIndex);
			}
			if (endIndex - 3 < str.length() && endIndex > 0) {
				msgItem.setText(str.substring(0, endIndex - 3) + "...");
				return;
			}
		}
		msgItem.setText(str);
	}

}
