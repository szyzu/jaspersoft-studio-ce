/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.actions.export.xls;

import net.sf.jasperreports.eclipse.viewer.IReportViewer;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

import com.jaspersoft.studio.editor.preview.actions.export.ExportMenuAction;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

@Deprecated
public class ExportAsXlsAction extends AExportXlsAction {

	public ExportAsXlsAction(IReportViewer viewer, JasperReportsConfiguration jContext, ExportMenuAction parentMenu) {
		super(viewer, jContext, parentMenu);

		setText(Messages.ExportAsXlsAction_title);
		setToolTipText(Messages.ExportAsXlsAction_tooltip);

		setFileExtensions(new String[] { "*.xls" }); //$NON-NLS-1$
		setFilterNames(new String[] { Messages.ExportAsXlsAction_filternames });
		setDefaultFileExtension("xls"); //$NON-NLS-1$
	}

	@Override
	protected JRXlsExporter createExporter(JasperReportsConfiguration jContext, JRExportProgressMonitor monitor) {
		JRXlsExporter exp = new JRXlsExporter(jContext);

		SimpleXlsReportConfiguration rconf = new SimpleXlsReportConfiguration();
		setupReportConfiguration(rconf, monitor);
		exp.setConfiguration(rconf);

		return exp;
	}
}
