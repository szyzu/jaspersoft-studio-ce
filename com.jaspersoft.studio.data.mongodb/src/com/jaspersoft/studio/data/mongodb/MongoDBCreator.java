/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.mongodb;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jaspersoft.mongodb.adapter.MongoDbDataAdapterImpl;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.adapter.IDataAdapterCreator;

/**
 * Creator to build a JSS MongoDB data adapter from the xml definition of an iReport MongoDB 
 * data adapter
 * 
 * @author Orlandin Marco
 */
public class MongoDBCreator implements IDataAdapterCreator {

	@Override
	public DataAdapterDescriptor buildFromXML(Document docXML) {
		MongoDbDataAdapterImpl result = new MongoDbDataAdapterImpl();
		
		NamedNodeMap rootAttributes = docXML.getChildNodes().item(0).getAttributes();
		String connectionName = rootAttributes.getNamedItem("name").getTextContent();
		result.setName(connectionName);
		
		NodeList children = docXML.getChildNodes().item(0).getChildNodes();
		for(int i=0; i<children.getLength(); i++){
			Node node = children.item(i);
			if (node.getNodeName().equals("connectionParameter")){
		
				String paramName = node.getAttributes().getNamedItem("name").getTextContent();
				
				if (paramName.equals("username")) result.setUsername(node.getTextContent()) ;
				if (paramName.equals("MongoDB URI")) result.setMongoURI(node.getTextContent());
				if (paramName.equals("password")) result.setPassword(node.getTextContent());
			}
		}

		MongoDbDataAdapterDescriptor desc = new MongoDbDataAdapterDescriptor();
		desc.setDataAdapter(result);
		return desc;
	}

	@Override
	public String getID() {
		return "com.jaspersoft.ireport.mongodb.connection.MongoDbConnection";
	}


}
