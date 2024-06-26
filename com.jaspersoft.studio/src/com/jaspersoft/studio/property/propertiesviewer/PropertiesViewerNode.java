/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.propertiesviewer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Generic node that should be extended by all the other kind of nodes.
 * 
 * @author mrabbi
 *
 */
public class PropertiesViewerNode implements IPropertiesViewerNode {

	protected Control control;
	
	// Attributes
	private String id;
	private String category;
	private String name;
	private String description;
	private Collection<String> keywords;

	public PropertiesViewerNode(String id, String name, String description){
		this(id,name,description,null,null);
	}
	
	public PropertiesViewerNode(String id, String name, String description, String category){
		this(id,name,description,category,null);
	}
	
	public PropertiesViewerNode(String id, String name, String description, Collection<String> keywords){
		this(id,name,description,null,keywords);
	}
	
	public PropertiesViewerNode(String id, String name, String description, String category, Collection<String> keywords){
		// Sanity checks - Must have ID and NAME
		Assert.isNotNull(id);
		Assert.isNotNull(name);
		
		this.id=id;
		this.name=name;
		this.description=description;
		this.category=category;
		this.keywords=keywords;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public Control getControl() {
		return control;
	}

	public void createControl(Composite parent) {
		checkControl();
		control=new Composite(parent,SWT.NONE);
	}
	
	public Collection<String> getNodeKeywords() {
		if (keywords==null){
			keywords=new ArrayList<String>();
		}
		return keywords;
	}
	
	public void setNodeKeywords(Collection<String> keywords) {
		this.keywords = keywords;
	}
	
	public void update(){
		// DO NOTHING - should be overridden by subclasses
		// that want to perform custom update operations on
		// the main control.
	}
	
	/**
	 * Verifies if the control has already been created.
	 * If so, it throws an {@link IllegalStateException}.
	 */
	protected void checkControl(){
		if(control!=null){
			throw new IllegalStateException(
					MessageFormat.format("The control associated to the current node with name '{0}' and id '{1}' was already created.", new Object[]{name,id}));
		}
	}

	@Override
	public String getHelpContextID() {
		return null;
	}

}
