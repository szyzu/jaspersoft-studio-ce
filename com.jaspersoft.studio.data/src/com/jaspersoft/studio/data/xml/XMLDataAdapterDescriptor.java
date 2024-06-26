/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jaspersoft.studio.data.AWizardDataEditorComposite;
import com.jaspersoft.studio.data.Activator;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterEditor;
import com.jaspersoft.studio.data.FieldTypeGuesser;
import com.jaspersoft.studio.data.IWizardDataEditorProvider;
import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.property.dataset.dialog.DataQueryAdapters;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.XMLUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.utils.parameter.ParameterUtil;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.DataFile;
import net.sf.jasperreports.data.DataFileStream;
import net.sf.jasperreports.data.DataFileUtils;
import net.sf.jasperreports.data.xml.XmlDataAdapterImpl;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.engine.util.xml.JRXPathExecuter;
import net.sf.jasperreports.engine.util.xml.JRXPathExecuterFactory;
import net.sf.jasperreports.engine.util.xml.JRXPathExecuterUtils;

public class XMLDataAdapterDescriptor extends DataAdapterDescriptor
		implements IFieldsProvider, IWizardDataEditorProvider {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	private boolean recursiveFind;
	private boolean considerEmptyNodes;

	@Override
	public XmlDataAdapterImpl getDataAdapter() {
		if (dataAdapter == null)
			dataAdapter = new XmlDataAdapterImpl();
		return (XmlDataAdapterImpl) dataAdapter;
	}

	@Override
	public DataAdapterEditor getEditor() {
		return new XMLDataAdapterEditor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.DataAdapterFactory#getIcon(int)
	 */
	@Override
	public Image getIcon(int size) {
		if (size == 16) {
			return Activator.getDefault().getImage("icons/blue-document-code.png"); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public boolean supportsGetFieldsOperation(JasperReportsConfiguration jConfig, JRDataset jDataset) {
		return true;
	}

	@Override
	public List<JRDesignField> getFields(DataAdapterService con, JasperReportsConfiguration jConfig, JRDataset jDataset)
			throws JRException, UnsupportedOperationException {
		IProgressMonitor monitor = (IProgressMonitor) jConfig.getMap().get(DataQueryAdapters.PROGRESSMONITOR);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("REPORT_PARAMETERS_MAP", new HashMap<String, Object>());
		con.contributeParameters(parameters);
		ParameterUtil.setParameters(jConfig, jDataset, parameters);
		parameters.put(JRParameter.REPORT_MAX_COUNT, FieldTypeGuesser.SAMPLESIZE);

		if (monitor != null && monitor.isCanceled())
			return null;

		setRecursiveRetrieval(jConfig);
		setConsiderEmptyNodes(jConfig);
		ArrayList<JRDesignField> fields = new ArrayList<>();
		XmlDataAdapterImpl d = getDataAdapter();
		DataFile df = d.getDataFile();
		Document doc = null;
		DataFileStream ins = null;
		try {
			// FIXME - We need to proper populate the map!!!
			ins = DataFileUtils.instance(new ParameterContributorContext(jConfig, null, null)).getDataStream(df,
					parameters);
			doc = JRXmlUtils.parse(ins, XMLUtils.isNamespaceAware(d, jConfig.getJasperDesign()));
		} catch (Exception e) {
			UIUtils.showError(e);
		} finally {
			IOUtils.closeQuietly(ins);
		}
		if (doc != null)
			fields.addAll(getFieldsFromDocument(doc, jConfig, jDataset));

		JRDataSource ds = (JRDataSource) parameters.get(JRParameter.REPORT_DATA_SOURCE);
		if (ds == null)
			ds = new JRXPathQueryExecuterFactory()
					.createQueryExecuter(jConfig, jDataset, ParameterUtil.convertMap(parameters, jDataset))
					.createDatasource();

		FieldTypeGuesser.guessTypes(ds, fields, ds.next(), monitor);
		return fields;
	}

	/**
	 * Returns the list of fields provided by an XML document and the related query.
	 * 
	 * @param doc      the W3C XML document
	 * @param jConfig  the JasperReports configuration instance
	 * @param jDataset the current dataset
	 * @return the list of fields
	 * @throws JRException
	 */
	protected List<JRDesignField> getFieldsFromDocument(Document doc, JasperReportsConfiguration jConfig,
			JRDataset jDataset) throws JRException {
		JRXPathExecuterFactory xPathExecuterFactory = JRXPathExecuterUtils.getXPathExecuterFactory(jConfig);
		JRXPathExecuter xPathExecuter = xPathExecuterFactory.getXPathExecuter();
		NodeList nodes = xPathExecuter.selectNodeList(doc, jDataset.getQuery().getText());
		LinkedHashMap<String, JRDesignField> fieldsMap = new LinkedHashMap<>();
		for (int nIdx = 0; nIdx < nodes.getLength(); nIdx++) {
			Node currNode = nodes.item(nIdx);
			if (considerEmptyNodes || StringUtils.isNotBlank(XMLUtils.getChildText(currNode))) {
				addMainNodeField(fieldsMap, currNode);
			}
			findDirectChildrenAttributes(currNode, fieldsMap, "");
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				NodeList childNodes = currNode.getChildNodes();
				findChildFields(childNodes, fieldsMap, "");
			}
		}
		return new ArrayList<>(fieldsMap.values());
	}

	/*
	 * Finds and adds a possible list of attributes for the specified node.
	 */
	private void findDirectChildrenAttributes(Node node, LinkedHashMap<String, JRDesignField> fieldsMap,
			String prefix) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				if (item.getNodeType() == Node.ATTRIBUTE_NODE) {
					addNewField(item.getNodeName(), fieldsMap, item, prefix);
				}
			}
		}
	}

	/*
	 * Finds and adds a possible list of children nodes for the specified node.
	 */
	private void findChildFields(NodeList nodes, LinkedHashMap<String, JRDesignField> fieldsMap, String prefix) {
		if (nodes != null) {
			List<String> childrenNames = new ArrayList<>(); // temp list
															// to avoid
															// duplicates
															// at the
															// same
															// level
			for (int i = 0; i < nodes.getLength(); i++) {
				Node item = nodes.item(i);
				String nodeName = item.getNodeName();
				if ((item.getNodeType() == Node.ELEMENT_NODE || item.getNodeType() == Node.ATTRIBUTE_NODE)
						&& !childrenNames.contains(nodeName)) {
					if (recursiveFind) {
						findDirectChildrenAttributes(item, fieldsMap, prefix + nodeName + "/");
					}
					if (considerEmptyNodes || StringUtils.isNotBlank(XMLUtils.getChildText(item))) {
						addNewField(nodeName, fieldsMap, item, prefix);
					}
					if (recursiveFind && item.hasChildNodes()) {
						findChildFields(item.getChildNodes(), fieldsMap, prefix + nodeName + "/");
					}
				}
			}
		}
	}

	/**
	 * Verifies if the recursive retrieval of fields is expected and sets the proper
	 * flag for it.
	 * 
	 * @param jconfig the JasperReports context
	 */
	protected void setRecursiveRetrieval(JasperReportsConfiguration jconfig) {
		recursiveFind = jconfig.getPropertyBoolean(XMLQueryEditorPreferencePage.P_USE_RECURSIVE_RETRIEVAL, false);
	}

	/**
	 * Verifies if the empty nodes should be considered during the retrieval of
	 * fields and sets the proper flag for it.
	 * 
	 * @param jconfig the JasperReports context
	 */
	protected void setConsiderEmptyNodes(JasperReportsConfiguration jConfig) {
		considerEmptyNodes = jConfig.getPropertyBoolean(XMLQueryEditorPreferencePage.P_CONSIDER_EMPTY_NODES, false);
	}

	/*
	 * Adds a new JRDesignField to the current map. A proper name is generated if
	 * the node one can not be used.
	 */
	private void addNewField(String nodeName, LinkedHashMap<String, JRDesignField> fieldsMap, Node item,
			String prefix) {
		JRDesignField f = new JRDesignField();
		String description = "";
		f.setName(ModelUtils.getNameForField(new ArrayList<JRDesignField>(fieldsMap.values()), nodeName));
		f.setValueClass(String.class);
		if (item.getNodeType() == Node.ATTRIBUTE_NODE) {
			description = prefix + "@" + item.getNodeName();
			f.setDescription(description); // $NON-NLS-1$
		} else {
			description = prefix + item.getNodeName();
			f.setDescription(description);
		}
		f.getPropertiesMap().setProperty("net.sf.jasperreports.xpath.field.expression", description);
		// Let's consider the description indicating the XPath query
		// as unique and therefore as map key.
		if (!fieldsMap.containsKey(description)) {
			fieldsMap.put(description, f);
		}
	}

	/*
	 * Adds a field for the main node.
	 */
	private void addMainNodeField(LinkedHashMap<String, JRDesignField> fieldsMap, Node item) {
		JRDesignField f = new JRDesignField();
		f.setName(ModelUtils.getNameForField(new ArrayList<JRDesignField>(fieldsMap.values()), item.getNodeName()));
		f.setValueClass(String.class);
		f.setDescription(".");
		f.getPropertiesMap().setProperty("net.sf.jasperreports.xpath.field.expression", ".");
		fieldsMap.put(".", f);
	}

	@Override
	public AWizardDataEditorComposite createDataEditorComposite(Composite parent, WizardPage page) {
		return new XMLWizardDataEditorComposite(parent, page, this);
	}

	@Override
	public String[] getLanguages() {
		return new String[] { JRXPathQueryExecuter.CANONICAL_LANGUAGE, "xpath2" };
	}
}
