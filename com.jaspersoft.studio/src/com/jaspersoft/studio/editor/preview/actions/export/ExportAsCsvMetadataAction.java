/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.actions.export;

import java.io.File;

import net.sf.jasperreports.eclipse.viewer.IReportViewer;
import net.sf.jasperreports.engine.export.JRCsvMetadataExporter;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.export.SimpleCsvMetadataReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class ExportAsCsvMetadataAction extends AExportAction {

	public ExportAsCsvMetadataAction(IReportViewer viewer, JasperReportsConfiguration jContext,
			ExportMenuAction parentMenu) {
		super(viewer, jContext, parentMenu);

		setText(Messages.ExportAsCsvMetadataAction_title);
		setToolTipText(Messages.ExportAsCsvMetadataAction_tooltip);

		setFileExtensions(new String[] { "*.csv" }); //$NON-NLS-1$
		setFilterNames(new String[] { Messages.ExportAsCsvAction_filtername });
		setDefaultFileExtension("csv"); //$NON-NLS-1$
	}

	@Override
	protected JRCsvMetadataExporter getExporter(JasperReportsConfiguration jContext, JRExportProgressMonitor monitor,
			File file) {
		JRCsvMetadataExporter exp = new JRCsvMetadataExporter(jContext);
		exp.setExporterOutput(new SimpleWriterExporterOutput(file));

		SimpleCsvMetadataReportConfiguration rconf = new SimpleCsvMetadataReportConfiguration();
		setupReportConfiguration(rconf, monitor);
		exp.setConfiguration(rconf);

		return exp;
	}
}
