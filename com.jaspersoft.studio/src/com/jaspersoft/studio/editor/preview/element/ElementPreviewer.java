/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterManager;
import com.jaspersoft.studio.data.empty.EmptyDataAdapterDescriptor;
import com.jaspersoft.studio.data.reader.DataPreviewScriptlet;
import com.jaspersoft.studio.data.reader.DatasetReader;
import com.jaspersoft.studio.data.storage.ADataAdapterStorage;
import com.jaspersoft.studio.data.storage.JRDefaultDataAdapterStorage;
import com.jaspersoft.studio.editor.preview.datasnapshot.DataSnapshotManager;
import com.jaspersoft.studio.property.dataset.dialog.DataQueryAdapters;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.data.cache.DataSnapshotException;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.viewer.BrowserUtils;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.JRScriptlet;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class ElementPreviewer {
	private Composite cmp;
	private Browser browser;
	private String previewTitle;
	private String previewMessage;

	public ElementPreviewer(Composite parent, String previewTitle, String previewMessage) {
		cmp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		cmp.setLayout(layout);

		browser = BrowserUtils.getSWTBrowserWidget(cmp, SWT.NONE);
		browser.setJavascriptEnabled(true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		browser.setLayoutData(gd);
		
		this.previewTitle = previewTitle;
		this.previewMessage = previewMessage;
	}

	public Composite getControl() {
		return cmp;
	}

	private JasperDesign jd;

	public String runReport(JasperReportsConfiguration jConf, JRElement element, boolean fromCache,
			IProgressMonitor monitor) {
		if (jd == null)
			UIUtils.getDisplay().asyncExec(() -> {
				StringBuffer sb = new StringBuffer();
				sb.append("<!DOCTYPE html>").append("<html >").append("<head>")
						.append("    <title>").append(previewTitle).append("</title>  ").append("    <style>")
						.append("        .container{").append("            display: flex;")
						.append("            align-items: center;").append("            justify-content: center;")
						.append("            height:95%;").append("        }").append("        body, html{")
						.append("            height:95%;").append("        }        ").append("        .loading { ")
						.append("          font-size: 1.2em; ").append("          font-family: Georgia;")
						.append("        }        ").append("    </style>").append("    <script>")
						.append("        i = 0;").append("        setInterval(function() {")
						.append("            i = ++i % 4;")
						.append("            document.querySelector('.loading').innerHTML = \"").append(previewMessage).append("\" + Array(i+1).join(\".\");")
						.append("        }, 800);").append("    </script>").append("</head>").append("<body>")
						.append("    <div class=\"container\">")
						.append("        <div class=\"loading\">").append(previewMessage).append("</div>").append("    </div>  ")
						.append("</body>").append("</html>");
				browser.setText(sb.toString());
			});
		ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(jConf.getClassLoader());
		Map<String, Object> hm = null;
		JasperDesign jDesign = jConf.getJasperDesign();
		JasperReport jrobj = null;
		DataAdapterDescriptor da = null;
		try {
			// initialise the report
			if (jd == null) {
				jd = getJasperDesign(jConf);
				jd.setUUID(jDesign.getUUID());
				for (JRStyle s : jDesign.getStyles())
					jd.addStyle(s);
				for (JRReportTemplate s : jDesign.getTemplates())
					jd.addTemplate(s);
			}
			setupDatasets(jConf, jDesign);
			replaceElement((JRDesignElement) element.clone(), jd);

			jrobj = DatasetReader.compile(jConf, jd, monitor);
			if (jrobj == null)
				return null;
			hm = DatasetReader.prepareParameters(jConf, 100);

			da = prepareDataAdapter(jConf, jDesign, hm);
			DataSnapshotManager.setDataSnapshot(hm, !fromCache);
			if (monitor.isCanceled())
				return null;
			return doRunReport(jConf, hm, jDesign, jrobj, da);
		} catch (DataSnapshotException e) {
			DataSnapshotManager.setDataSnapshot(hm, true);
			try {
				return doRunReport(jConf, hm, jDesign, jrobj, da);
			} catch (Exception e1) {
				handleException(e);
			}
		} catch (Exception e) {
			handleException(e);
		} finally {
			Thread.currentThread().setContextClassLoader(originalClassLoader);
		}

		return null;
	}

	protected void handleException(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));

		final String message = sw.getBuffer().toString();
		UIUtils.getDisplay().asyncExec(() -> browser.setText("<HTML><BODY><pre>" + message + "</pre></body></html>"));
	}

	private DatasetReader dr;

	public void cancel() {
		if (dr != null)
			dr.stop();
	}

	protected String doRunReport(JasperReportsConfiguration jConf, Map<String, Object> hm, JasperDesign jDesign,
			JasperReport jrobj, DataAdapterDescriptor da) throws JRException, IOException {
		if (dr != null)
			dr.stop();
		dr = new DatasetReader();
		JasperPrint jrPrint = dr.fillReport(jConf, jDesign.getMainDesignDataset(), da, jrobj, hm);

		// create a temp dir and a temp file for html
		File destDir = FileUtils.createTempDir();
		String dest = new File(destDir, "index.html").getAbsolutePath();
		JasperExportManager.getInstance(jConf).exportToHtmlFile(jrPrint, dest);
		System.out.println(dest);
		UIUtils.getDisplay().asyncExec(() -> {
			if (browser.isDisposed())
				return;
			browser.setToolTipText(dest);
			String url = dest;
			if(UIUtils.isWindows()) {
				url = "file:///"+ dest.replace("\\", "/");
			}
			browser.setUrl(url);
		});
		return dest;
	}

	protected void setupDatasets(JasperReportsConfiguration jConf, JasperDesign jDesign) throws JRException {
		for (JRDataset ds : jd.getDatasets())
			jd.removeDataset(ds);
		List<String> columns = new ArrayList<>();
		for (JRField f : jDesign.getMainDataset().getFields())
			columns.add(f.getName());
		DatasetReader.setupDataset(jd, (JRDesignDataset) jDesign.getMainDesignDataset(), jConf, columns);

		for (JRDataset ds : jDesign.getDatasets())
			jd.addDataset((JRDesignDataset) ds.clone());
	}

	protected JasperDesign getJasperDesign(JasperReportsConfiguration jConfig) throws IOException, JRException {
		FileInputStream is = null;
		try {
			String reportLocation = JaspersoftStudioPlugin.getInstance()
					.getFileLocation(DataPreviewScriptlet.PREVIEW_REPORT_PATH);
			is = new FileInputStream(reportLocation);
			JasperDesign tjd = JRXmlLoader.load(jConfig, is);

			tjd.setLeftMargin(0);
			tjd.setRightMargin(0);
			tjd.setTopMargin(0);
			tjd.setBottomMargin(0);
			tjd.setTitle(null);
			tjd.setPageHeader(null);
			tjd.setColumnHeader(null);
			((JRDesignSection) tjd.getDetailSection()).removeBand(0);
			tjd.setColumnFooter(null);
			tjd.setPageFooter(null);
			tjd.setLastPageFooter(null);
			tjd.setNoData(null);
			tjd.setBackground(null);
			tjd.setScriptletClass(null);
			tjd.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
			for (JRScriptlet s : tjd.getScriptlets())
				tjd.removeScriptlet(s);
			return tjd;
		} finally {
			FileUtils.closeStream(is);
		}
	}

	public DataAdapterDescriptor prepareDataAdapter(JasperReportsConfiguration jConf, JasperDesign jDesign,
			Map<String, Object> hm) {
		return prepareDataAdapter(jConf, jDesign.getMainDesignDataset(), hm);
	}

	public DataAdapterDescriptor prepareDataAdapter(JasperReportsConfiguration jConf, JRDesignDataset jDataset,
			Map<String, Object> hm) {
		JRDefaultDataAdapterStorage defaultStorage = DataAdapterManager.getJRDefaultStorage(jConf);
		DataAdapterDescriptor da = null;
		String defAdapter = jDataset.getPropertiesMap().getProperty(DataQueryAdapters.DEFAULT_DATAADAPTER);
		if (defAdapter != null) {
			da = defaultStorage.getDefaultJRDataAdapter(defAdapter);
			if (da == null)
				da = DataAdapterManager.getPreferencesStorage().findDataAdapter(defAdapter);
			if (da == null) {
				IFile f = (IFile) jConf.get(FileUtils.KEY_FILE);
				if (f != null) {
					ADataAdapterStorage st = DataAdapterManager.getProjectStorage(f.getProject());
					if (st != null)
						da = st.findDataAdapter(defAdapter);
				}
			}
		}
		if (da == null)
			da = defaultStorage.getDefaultJRDataAdapter(jDataset);
		if (da == null) {
			// I think we should use some predefined datasets
			da = new EmptyDataAdapterDescriptor();
		}
		return da;
	}

	protected void replaceElement(final JRDesignElement element, JasperDesign jd) {
		JRDesignBand bs = (JRDesignBand) jd.getSummary();
		for (JRElement jrel : bs.getElements())
			bs.removeElement((JRDesignElement) jrel);
		bs.addElement(element);
		element.setX(0);
		element.setY(0);
		UIUtils.getDisplay().syncExec(() -> {
			element.setWidth(Math.max(20, browser.getBounds().width - 20));
			element.setHeight(Math.max(20, browser.getBounds().height - 20));
		});

		jd.setPageHeight(element.getHeight());
		jd.setPageWidth(element.getWidth());
		jd.setColumnWidth(jd.getPageWidth());
		bs.setHeight(element.getHeight());
	}
}
