/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.view.report.swt.action;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.viewer.IReportViewer;
import net.sf.jasperreports.eclipse.viewer.ReportViewer;
import net.sf.jasperreports.eclipse.viewer.action.AReportAction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;

/**
 * Action to export a sample image of the current page of the generated report
 * 
 * @author Orlandin Marco
 * 
 */
public class ExportImageAction extends AReportAction {

	public ExportImageAction(IReportViewer rviewer) {
		super(rviewer);
		setText(Messages.ExportImageAction_actionName);
		setToolTipText(Messages.ExportImageAction_actionTooltip);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/resources/image-export.png"));//$NON-NLS-1$
		setDisabledImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor(
				"icons/resources/image-export.png"));//$NON-NLS-1$
	}

	public boolean isActionEnabled() {
		return rviewer.hasReport();
	}

	@Override
	public void run() {
		FileDialog fd = new FileDialog(UIUtils.getShell(), SWT.SAVE);
		fd.setText(Messages.ExportImageAction_saveDialogTitle);
		String[] filterExt = { "*.png" }; //$NON-NLS-1$
		if (rviewer instanceof ReportViewer)
			fd.setFilterPath(((ReportViewer) rviewer).getReportPath());
		fd.setFileName(rviewer.getReportName());
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if (selected != null) {
			rviewer.exportImage(selected, -1, -1);
		}
	}

}
