/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.view.report.html;

import net.sf.jasperreports.eclipse.viewer.ReportViewer;

import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.editor.preview.actions.export.AExportAction;
import com.jaspersoft.studio.editor.preview.actions.export.html.ExportAsLHtmlAction;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class LayeredHTMLViewer extends HTMLViewer {

	public LayeredHTMLViewer(Composite parent, JasperReportsConfiguration jContext) {
		super(parent, jContext);
	}

	protected AExportAction createExporter(ReportViewer rptv) {
		return new ExportAsLHtmlAction(rptv, jContext, null);
	}

}
