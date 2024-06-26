/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.actions.export;

import java.io.File;

import net.sf.jasperreports.eclipse.viewer.IReportViewer;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.export.SimpleTextExporterConfiguration;
import net.sf.jasperreports.export.SimpleTextReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class ExportAsTextAction extends AExportAction {

	public ExportAsTextAction(IReportViewer viewer, JasperReportsConfiguration jContext, ExportMenuAction parentMenu) {
		super(viewer, jContext, parentMenu);
		setText(Messages.ExportAsTextAction_title);
		setToolTipText(Messages.ExportAsTextAction_tooltip);

		setFileExtensions(new String[] { "*.txt" }); //$NON-NLS-1$
		setFilterNames(new String[] { Messages.ExportAsTextAction_filtername });
		setDefaultFileExtension("txt"); //$NON-NLS-1$
	}

	@Override
	protected JRTextExporter getExporter(JasperReportsConfiguration jContext, JRExportProgressMonitor monitor, File file) {
		JRTextExporter exp = new JRTextExporter(jContext);
		exp.setExporterOutput(new SimpleWriterExporterOutput(file));

		exp.setConfiguration(new SimpleTextExporterConfiguration());

		SimpleTextReportConfiguration rconf = new SimpleTextReportConfiguration();
		setupReportConfiguration(rconf, monitor);
		exp.setConfiguration(rconf);

		return exp;
	}

}
