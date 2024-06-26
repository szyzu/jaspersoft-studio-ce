/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jaspersoft.translation.resources.AbstractResourceDefinition;
import com.jaspersoft.translation.resources.IResourcesInput;
import com.jaspersoft.translation.resources.PackageResourceDefinition;


/**
 * Publish the resource that can be translated through the translation plugin
 * 
 * @author Orlandin Marco
 *
 */
public class ResourcePublisher extends IResourcesInput{

	protected static HashMap<String,List<AbstractResourceDefinition>> propertiesCache = new HashMap<String, List<AbstractResourceDefinition>>();
	
	@Override
	public String getPluginName() {
		return "com.jaspersoft.studio.data.sql.ui";
	}
	
	protected ClassLoader getClassLoader(){
		return this.getClass().getClassLoader();
	}
	
	protected List<AbstractResourceDefinition> initializeProperties(){
		List<AbstractResourceDefinition> result = new ArrayList<AbstractResourceDefinition>();
		result.add(new PackageResourceDefinition("en_EN", 
																						 "com.jaspersoft.studio.data.sql.messages", 
																						 "messages.properties", 
																						 "In this file there are the standard strings used by the SQL plugin of jaspersoft studio",
																						 getClassLoader(),
																						 "com/jaspersoft/studio/data/sql/messages/messages.properties", this));
		
		result.add(new PackageResourceDefinition("en_EN", 
																						 null, 
																						 "plugin.properties", 
																						 "In this file there are the standard strings used by the SQL plugin of jaspersoft studio",
																						 getClassLoader(),
																						 "plugin.properties", this));
		
		propertiesCache.put(getPluginName(), result);
		return result;
	}

	@Override
	public List<AbstractResourceDefinition> getResourcesElements() {
		List<AbstractResourceDefinition> result = propertiesCache.get(getPluginName());
		if (result == null) result = initializeProperties();
		return result;
	}

	@Override
	public String getContextId() {
		return null;
	}


}
