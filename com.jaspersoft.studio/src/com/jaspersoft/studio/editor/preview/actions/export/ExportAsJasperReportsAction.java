/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.actions.export;

import java.io.File;

import net.sf.jasperreports.eclipse.util.FileExtension;
import net.sf.jasperreports.eclipse.viewer.IReportViewer;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.util.JRSaver;

import org.eclipse.swt.custom.BusyIndicator;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class ExportAsJasperReportsAction extends AExportAction {

	public ExportAsJasperReportsAction(IReportViewer viewer, JasperReportsConfiguration jContext,
			ExportMenuAction parentMenu) {
		super(viewer, jContext, parentMenu);

		setText(Messages.ExportAsJasperReportsAction_title);
		setToolTipText(Messages.ExportAsJasperReportsAction_tooltip);

		setFileExtensions(new String[] { "*." + FileExtension.JRPRINT }); //$NON-NLS-1$
		setFilterNames(new String[] { Messages.ExportAsJasperReportsAction_filtername });
		setDefaultFileExtension(FileExtension.JRPRINT);
	}

	@Override
	protected void exportWithProgress(File file, JRExportProgressMonitor monitor) throws Throwable {
		final java.io.File f = file.getAbsoluteFile();
		final Throwable[] ex = new Throwable[1];
		BusyIndicator.showWhile(null, () -> {
			try {
				JRSaver.saveObject(getReportViewer().getReport(), f);
			} catch (Throwable e) {
				ex[0] = e;
			}
		});
		if (ex[0] != null)
			throw ex[0];
	}

	@Override
	protected JRAbstractExporter<?, ?, ?, ?> getExporter(JasperReportsConfiguration jContext,
			JRExportProgressMonitor monitor, File file) {
		return null;
	}
}
