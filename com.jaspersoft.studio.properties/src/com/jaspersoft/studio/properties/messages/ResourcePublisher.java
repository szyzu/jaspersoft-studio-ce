/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.properties.messages;

import java.util.ArrayList;
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
public final class ResourcePublisher extends IResourcesInput{

	protected static List<AbstractResourceDefinition> propertiesCache = null;
	
	protected ClassLoader getClassLoader(){
		return this.getClass().getClassLoader();
	}
	
	@Override
	public String getPluginName() {
		return "com.jaspersoft.studio.properties";
	}

	protected void initializeProperties(){
		propertiesCache = new ArrayList<AbstractResourceDefinition>();
		propertiesCache.add(new PackageResourceDefinition("en_EN", 
												 "com.jaspersoft.studio.properties.messages", 
												 "messages.properties", 
												 "",
												 getClassLoader(),
												 "com/jaspersoft/studio/properties/messages/messages.properties", this));
		
		propertiesCache.add(new PackageResourceDefinition("en_EN", 
												 null, 
												 "plugin.properties", 
												 "",
												 getClassLoader(),
												 "plugin.properties", this));
	}

	@Override
	public List<AbstractResourceDefinition> getResourcesElements() {
		if (propertiesCache == null) initializeProperties();
		return propertiesCache;
	}

	@Override
	public String getContextId() {
		return null;
	}

}
