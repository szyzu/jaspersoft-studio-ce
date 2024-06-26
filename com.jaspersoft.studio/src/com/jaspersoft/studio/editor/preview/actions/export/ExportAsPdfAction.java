/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.actions.export;

import java.io.File;

import net.sf.jasperreports.eclipse.viewer.IReportViewer;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class ExportAsPdfAction extends AExportAction {

	public ExportAsPdfAction(IReportViewer viewer, JasperReportsConfiguration jContext, ExportMenuAction parentMenu) {
		super(viewer, jContext, parentMenu);

		setText(Messages.ExportAsPdfAction_title);
		setToolTipText(Messages.ExportAsPdfAction_tooltip);

		setFileExtensions(new String[] { "*.pdf" }); //$NON-NLS-1$
		setFilterNames(new String[] { Messages.ExportAsPdfAction_filtername });
		setDefaultFileExtension("pdf"); //$NON-NLS-1$
	}

	@Override
	protected JRPdfExporter getExporter(JasperReportsConfiguration jContext, JRExportProgressMonitor monitor, File file) {
		JRPdfExporter exp = new JRPdfExporter(jContext);

		exp.setExporterOutput(new SimpleOutputStreamExporterOutput(file));

		SimplePdfExporterConfiguration conf = new SimplePdfExporterConfiguration();
		exp.setConfiguration(conf);

		SimplePdfReportConfiguration rconf = new SimplePdfReportConfiguration();
		setupReportConfiguration(rconf, monitor);
		exp.setConfiguration(rconf);

		return exp;
	}
}
