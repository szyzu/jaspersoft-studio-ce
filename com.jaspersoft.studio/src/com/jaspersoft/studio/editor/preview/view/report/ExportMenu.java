/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.view.report;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

import com.jaspersoft.studio.editor.preview.actions.export.ExportAsCsvAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsCsvMetadataAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsDocxAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsJasperReportsAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsJsonMetadataAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsOdtAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsPdfAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsPptxAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsRtfAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsTextAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsXmlAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportAsXmlWithImagesAction;
import com.jaspersoft.studio.editor.preview.actions.export.ExportMenuAction;
import com.jaspersoft.studio.editor.preview.actions.export.html.ExportAsLHtmlAction;
import com.jaspersoft.studio.editor.preview.actions.export.xls.ExportAsOdsAction;
import com.jaspersoft.studio.editor.preview.actions.export.xls.ExportAsXlsAction;
import com.jaspersoft.studio.editor.preview.actions.export.xls.ExportAsXlsMetadataAction;
import com.jaspersoft.studio.editor.preview.actions.export.xls.ExportAsXlsxAction;
import com.jaspersoft.studio.editor.preview.actions.export.xls.ExportAsXlsxMetadataAction;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.viewer.IReportViewer;

public class ExportMenu {
	public static ExportMenuAction getExportMenu(IReportViewer rptviewer, JasperReportsConfiguration jContext) {
		ExportMenuAction exportMenu = new ExportMenuAction(rptviewer);

		MenuManager mm = exportMenu.getMenuManager();
		mm.add(new ExportAsJasperReportsAction(rptviewer, jContext, exportMenu));
		mm.add(new Separator());

		mm.add(new ExportAsPdfAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsLHtmlAction(rptviewer, jContext, exportMenu));
		mm.add(new Separator());

		mm.add(new ExportAsRtfAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsDocxAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsOdtAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsOdsAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsPptxAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsTextAction(rptviewer, jContext, exportMenu));

		mm.add(new Separator());
		mm.add(new ExportAsXlsxAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsCsvAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsXlsxMetadataAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsCsvMetadataAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsJsonMetadataAction(rptviewer, jContext, exportMenu));

		mm.add(new Separator());
		mm.add(new ExportAsXmlAction(rptviewer, jContext, exportMenu));
		mm.add(new ExportAsXmlWithImagesAction(rptviewer, jContext, exportMenu));
		// exportMenu.setDefaultAction(pdfAction);

		return exportMenu;
	}
}
