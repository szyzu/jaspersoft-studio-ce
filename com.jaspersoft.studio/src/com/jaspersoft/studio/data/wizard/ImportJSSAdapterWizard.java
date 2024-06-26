/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.wizard;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.w3c.dom.Node;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterFactory;
import com.jaspersoft.studio.data.DataAdapterManager;
import com.jaspersoft.studio.data.adapter.IReportDescriptor;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.util.PreferencesUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.util.CastorUtil;
import net.sf.jasperreports.util.JacksonRuntimException;
import net.sf.jasperreports.util.JacksonUtil;

/**
 * Wizard to import one of more data adapters definition from the previous installations of Jaspersoft Studio
 * 
 * @author Orlandin Marco
 * 
 */
public class ImportJSSAdapterWizard extends Wizard implements IImportWizard {

	/**
	 * Page that list the Jaspersoft Studio workspace
	 */
	private SelectWorkspacePage page0 = new SelectWorkspacePage();

	/**
	 * Page that list the available data adapters into a precise configurations
	 */
	private ShowJSSAdaptersPage page1 = new ShowJSSAdaptersPage();

	/**
	 * Page that list the available properties into a precise configuration
	 */
	private JSSPropertiesPage page2 = new JSSPropertiesPage();

	@Override
	public void addPages() {
		addPage(page0);
		addPage(page1);
		addPage(page2);
	}

	/**
	 * Return the descriptor of the configuration selected into the first step
	 * 
	 * @return a configuration descriptor file
	 */
	public IReportDescriptor getSelectedConfiguration() {
		return page0.getSelection();
	}

	private void addAdapters() {
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						List<?> adapterNodes = page1.getSelectedAdapter();
						for (int i = 0; i < adapterNodes.size(); ++i) {
							Node adapterNode = (Node) adapterNodes.get(i);

							if (adapterNode.getNodeType() == Node.ELEMENT_NODE) {
								// 1. Find out the class of this data adapter...
								String adapterClassName = adapterNode.getAttributes().getNamedItem("class").getNodeValue(); //$NON-NLS-1$

								DataAdapterFactory factory = DataAdapterManager.findFactoryByDataAdapterClass(adapterClassName);

								if (factory == null) {
									// we should at least log a warning here....
									JaspersoftStudioPlugin
											.getInstance()
											.getLog()
											.log(
													new Status(Status.WARNING, JaspersoftStudioPlugin.getUniqueIdentifier(), Status.OK,
															Messages.DataAdapterManager_nodataadapterfound + adapterClassName, null));
									continue;
								}

								DataAdapterDescriptor dataAdapterDescriptor = factory.createDataAdapter();
								DataAdapter dataAdapter = null;

								byte[] bytes = nodeToString(adapterNode).getBytes();
								try {
									try {
										dataAdapter = JacksonUtil.getInstance(JasperReportsConfiguration.getDefaultInstance()).loadXml(new ByteArrayInputStream(bytes), DataAdapter.class);
									} catch (JacksonRuntimException e) {
										dataAdapter = (DataAdapter) CastorUtil.getInstance(JasperReportsConfiguration.getDefaultInstance()).read(new ByteArrayInputStream(bytes));
									}
									dataAdapterDescriptor.setDataAdapter(dataAdapter);
									DataAdapterManager.getPreferencesStorage().addDataAdapter(dataAdapterDescriptor);
								} finally {
									//nothing to do
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				Transformer t;

				private String nodeToString(Node node) throws TransformerFactoryConfigurationError, TransformerException {
					StringWriter sw = new StringWriter();
					if (t == null) {
						t = TransformerFactory.newInstance().newTransformer();
						t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
						t.setOutputProperty(OutputKeys.INDENT, "yes");
					}
					t.transform(new DOMSource(node), new StreamResult(sw));
					return sw.toString();
				}
			});
		} catch (InvocationTargetException e) {
			UIUtils.showError(e.getCause());
		} catch (InterruptedException e) {
			UIUtils.showError(e.getCause());
		}
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	/**
	 * Get the XML definition of every data adapter selected into the step one, and from this build the data adapter and
	 * add it to the configuration
	 */
	@Override
	public boolean performFinish() {
		addAdapters();
		List<String> proeprties = page2.getProperties();
		String[] keys = proeprties.toArray(new String[proeprties.size()]);
		String[] values = new String[proeprties.size()];
		for (int i = 0; i < keys.length; i++) {
			values[i] = page2.getProperyValue(keys[i]);
		}
		PreferencesUtils.storeJasperReportsProperty(keys, values);
		return true;
	}

}
