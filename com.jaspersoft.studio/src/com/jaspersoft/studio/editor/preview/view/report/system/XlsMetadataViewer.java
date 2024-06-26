/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.view.report.system;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.editor.preview.actions.export.AExportAction;
import com.jaspersoft.studio.editor.preview.actions.export.xls.ExportAsXlsMetadataAction;
import com.jaspersoft.studio.preferences.exporter.ExcelExporterPreferencePage;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.viewer.ReportViewer;
import net.sf.jasperreports.engine.JasperPrint;

@Deprecated
public class XlsMetadataViewer extends ASystemViewer {

	public XlsMetadataViewer(Composite parent, JasperReportsConfiguration jContext) {
		super(parent, jContext);
	}

	@Override
	protected AExportAction createExporter(ReportViewer rptv) {
		return new ExportAsXlsMetadataAction(rptv, jContext, null);
	}

	@Override
	protected String getExtension(JasperPrint jrprint) {
		return ".xls";
	}

	@Override
	public PreferencePage getPreferencePage() {
		return new ExcelExporterPreferencePage();
	}
}
