/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.engine;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;

import com.jaspersoft.studio.templates.TemplateProvider;
import com.jaspersoft.templates.TemplateBundle;

/**
 * Provide the TemplateBundles for all the template jrxml inside the folder
 * template/table
 * 
 * @author Orlandin Marco
 *
 */
public class JSTemplateProvider implements TemplateProvider {

	/**
	 * List of loaded templates
	 */
	public static List<TemplateBundle> cache = null;
	
	/**
	 * Key used to distinguish the tabular template from the other templates
	 */
	public static final String tableTemplateKey="jasperserver_template";
	
	/**
	 * Read all the templates jrxml in the folder templates/table, the subdirectories are excluded
	 */
	@Override
	public List<TemplateBundle> getTemplateBundles() {
		List<TemplateBundle> templates = new ArrayList<TemplateBundle>();	
		return templates;
	}
	
	/**
	 * Return the key that identify this engine
	 */
	@Override
	public String getProviderKey() {
		return tableTemplateKey;
	}

	/**
	 * return a human readable name for the engine
	 */
	@Override
	public String getProviderName() {
		return "JasperServer Template";
	}

	/**
	 * Get a JasperDesign and check if that JasperDesign can be used as Template and processed
	 * by the engine used inside this provider
	 * 
	 * @param jrContext context of the design to check
	 * @param design the design to check
	 * @return a List of founded error, the list is void if no error are found
	 */
	@Override
	public List<String> validateTemplate(JasperReportsContext jrContext, JasperDesign design) {
		return JasperServerTemplateEngine.validateJasperDesig(design);
	}
}
