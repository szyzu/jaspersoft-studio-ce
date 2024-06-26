/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jasperreports.data.DataFile;
import net.sf.jasperreports.data.DataFileStream;
import net.sf.jasperreports.data.DataFileUtils;
import net.sf.jasperreports.data.xml.XmlDataAdapter;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.WorkbenchJob;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.jaspersoft.studio.data.ATreeWizardDataEditorComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.designer.tree.NodeBoldStyledLabelProvider;
import com.jaspersoft.studio.data.messages.Messages;
import com.jaspersoft.studio.data.querydesigner.xpath.XMLDocumentManager;
import com.jaspersoft.studio.data.querydesigner.xpath.XMLTreeCustomStatus;
import com.jaspersoft.studio.data.querydesigner.xpath.XPathTreeViewerContentProvider;
import com.jaspersoft.studio.model.datasource.xml.XMLNode;
import com.jaspersoft.studio.utils.XMLUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

/**
 * Editor composite for the Xpath query language. This is supposed to used by
 * {@link XMLDataAdapterDescriptor}.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public class XMLWizardDataEditorComposite extends ATreeWizardDataEditorComposite {

	private static final int JOB_DELAY = 300;
	private XMLDocumentManager documentManager;
	private DecorateTreeViewerJob decorateJob;
	private NodeBoldStyledLabelProvider<XMLNode> treeLabelProvider;
	private XPathTreeViewerContentProvider treeContentProvider;

	public XMLWizardDataEditorComposite(Composite parent, WizardPage page,
			DataAdapterDescriptor dataAdapterDescriptor) {
		super(parent, page, dataAdapterDescriptor);
	}

	@Override
	protected void init() {
		super.init();
		this.documentManager = new XMLDocumentManager();
		this.decorateJob = new DecorateTreeViewerJob();
		this.treeLabelProvider = new NodeBoldStyledLabelProvider<XMLNode>();
		this.treeContentProvider = new XPathTreeViewerContentProvider();
	}

	@Override
	protected IBaseLabelProvider getTreeLabelProvider() {
		return this.treeLabelProvider;
	}

	@Override
	protected IContentProvider getTreeContentProvider() {
		return this.treeContentProvider;
	}

	@Override
	protected void refreshTreeViewerContent(final DataAdapterDescriptor da) {
		if (da != null && da.getDataAdapter() instanceof XmlDataAdapter) {
			treeViewer.setInput(XMLTreeCustomStatus.LOADING_XML);
			UIUtils.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					JasperReportsConfiguration jConfig = getJasperReportsConfiguration();
					try {
						documentManager.setDocument(getXMLDocument(da));
						documentManager.setJasperConfiguration(jConfig);
						treeViewer.setInput(documentManager.getXMLDocumentModel());
						treeViewer.expandToLevel(2);
						decorateTreeUsingQueryText();
					} catch (Exception e) {
						getStatusBar().showError(e);
						treeViewer.getTree().removeAll();
						treeViewer.setInput(XMLTreeCustomStatus.ERROR_LOADING_XML);
					}
				}
			});
		} else {
			treeViewer.getTree().removeAll();
			treeViewer.setInput(XMLTreeCustomStatus.FILE_NOT_FOUND);
		}
	}

	@Override
	protected void createTreeViewer(Composite parent) {
		super.createTreeViewer(parent);
		addDoubleClickSupport();
	}

	/*
	 * Adds support for generating the Xpath query expression, using the current
	 * selected node as input.
	 */
	private void addDoubleClickSupport() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeSelection s = (TreeSelection) treeViewer.getSelection();
				if (s.getFirstElement() instanceof XMLNode) {
					XMLNode xmlNode = (XMLNode) s.getFirstElement();
					String xPathExpression = documentManager.getXPathExpression(null, xmlNode);
					queryTextArea.setText((xPathExpression != null) ? xPathExpression : ""); //$NON-NLS-1$
				}
			}
		});
	}

	@Override
	protected void decorateTreeUsingQueryText() {
		if (documentManager.isDocumentSet()) {
			decorateJob.cancel();
			decorateJob.schedule(JOB_DELAY);
		}
	}

	/*
	 * Job that is responsible to update the treeviewer presentation depending
	 * on the nodes selected by the XPath query.
	 */
	private final class DecorateTreeViewerJob extends WorkbenchJob {

		public DecorateTreeViewerJob() {
			super(Messages.XPathWizardDataEditorComposite_RefreshJobTitle);
			setSystem(true);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (!isDisposed()) {
				monitor.beginTask(Messages.XPathWizardDataEditorComposite_TaskName, IProgressMonitor.UNKNOWN);
				String query = queryTextArea.getText();
				treeLabelProvider.setSelectedNodes(documentManager.getSelectableNodes(query));
				treeViewer.refresh();
				monitor.done();
				return Status.OK_STATUS;
			} else {
				return Status.CANCEL_STATUS;
			}
		}

	}

	@Override
	public void dispose() {
		if (decorateJob != null) {
			decorateJob.cancel();
			decorateJob = null;
		}
		super.dispose();
	}

	@Override
	public String getQueryLanguage() {
		return "xPath"; //$NON-NLS-1$
	}

	/**
	 * Get the W3C DOM {@link Document} representation for the XML information
	 * inside the
	 * 
	 * @param da
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws JRException
	 */
	protected Document getXMLDocument(final DataAdapterDescriptor da)
			throws SAXException, IOException, ParserConfigurationException, JRException {
		Document doc = null;
		DataFileStream ins = null;
		try {
			DataFile df = ((XmlDataAdapter) da.getDataAdapter()).getDataFile();
			Map<String, Object> parameters = new HashMap<String, Object>();
			// FIXME - We need to proper populate the map!!!
			ins = DataFileUtils.instance(new ParameterContributorContext(getJasperReportsConfiguration(), null, null))
					.getDataStream(df, parameters);
			doc = JRXmlUtils.parse(ins, XMLUtils.isNamespaceAware((XmlDataAdapter) da.getDataAdapter(),
					getJasperReportsConfiguration().getJasperDesign()));
		} finally {
			IOUtils.closeQuietly(ins);
		}
		return doc;
	}
}
