/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.actions.export.html;

import java.io.File;

import net.sf.jasperreports.eclipse.viewer.IReportViewer;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;

import com.jaspersoft.studio.editor.preview.actions.export.AExportAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportMenuAction;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class ExportAsLHtmlAction extends AExportAction {

	public ExportAsLHtmlAction(IReportViewer viewer, JasperReportsConfiguration jContext, ExportMenuAction parentMenu) {
		super(viewer, jContext, parentMenu);

		setText(Messages.ExportAsLHtmlAction_title);
		setToolTipText(Messages.ExportAsLHtmlAction_title);

		setFileExtensions(new String[] { "*.html" }); //$NON-NLS-1$ //$NON-NLS-2$
		setFilterNames(new String[] { Messages.ExportAsHtmlAction_filternames1 });
		setDefaultFileExtension("html"); //$NON-NLS-1$
	}

	@Override
	protected HtmlExporter getExporter(JasperReportsConfiguration jContext, JRExportProgressMonitor monitor, File file) {
		HtmlExporter exp = new HtmlExporter(jContext);
		exp.setExporterOutput(new SimpleHtmlExporterOutput(file));

		SimpleHtmlReportConfiguration rconf = new SimpleHtmlReportConfiguration();
		setupReportConfiguration(rconf, monitor);
		exp.setConfiguration(rconf);
		return exp;
	}
}
