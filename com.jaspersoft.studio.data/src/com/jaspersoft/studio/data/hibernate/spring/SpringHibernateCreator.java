/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.hibernate.spring;

import net.sf.jasperreports.data.hibernate.spring.SpringHibernateDataAdapterImpl;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.adapter.IDataAdapterCreator;

/**
 * Creator to build a JSS Spring Hibernate data adapter from the xml definition of an iReport Spring Hibernate 
 * data adapter
 * 
 * @author Orlandin Marco
 */
public class SpringHibernateCreator implements IDataAdapterCreator {

	@Override
	public DataAdapterDescriptor buildFromXML(Document docXML) {
		SpringHibernateDataAdapterImpl result = new SpringHibernateDataAdapterImpl();
		
		NamedNodeMap rootAttributes = docXML.getChildNodes().item(0).getAttributes();
		String connectionName = rootAttributes.getNamedItem("name").getTextContent();
		result.setName(connectionName);
		
		NodeList children = docXML.getChildNodes().item(0).getChildNodes();
		for(int i=0; i<children.getLength(); i++){
			Node node = children.item(i);
			if (node.getNodeName().equals("connectionParameter")){
				String paramName = node.getAttributes().getNamedItem("name").getTextContent();
				
				if (paramName.equals("spring.loaded.hibernate.spring.config")) result.setSpringConfig(node.getTextContent());
				if (paramName.equals("spring.loaded.hibernate.session.factory.id")) result.setBeanId(node.getTextContent());				
			}
		}

		SpringHibernateDataAdapterDescriptor desc = new SpringHibernateDataAdapterDescriptor();
		desc.setDataAdapter(result);
		return desc;
	}

	@Override
	public String getID() {
		return "com.jaspersoft.ireport.designer.connection.JRSpringLoadedHibernateConnection";
	}
}
