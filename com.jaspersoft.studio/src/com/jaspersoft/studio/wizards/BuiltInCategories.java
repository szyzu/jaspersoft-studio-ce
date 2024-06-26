/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.wizards;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the key of the categories embedded in JSS. This categories
 * are also supported by the translation system. 
 * 
 * @author Orlandin Marco
 *
 */
public class BuiltInCategories {
	
	/**
	 * Key of the engine value stored in the template properties
	 */
	public static final String ENGINE_KEY = "template.engine";
	
	/**
	 * Key of the name value stored in the template properties
	 */
	public static final String NAME_KEY = "template.name";
	
	/**
	 * Key of the minimum required JR version, stored in the template properties
	 */
	public static final String REQUIRED_JR_VERSION = "jr.required.version";
	
	/**
	 * Key of the category values stored in the template properties
	 */
	public static final String CATEGORY_KEY = "template.category";
	
	/**
	 * Identify all the templates
	 */
	public static final String ALL_TYPES_KEY = "all_types";
	
	/**
	 * Only the templates with A4 format
	 */
	public static final String A4_KEY = "format_a4";
	
	/**
	 * Only the templates with landscape format
	 */
	public static final String LANDSCAPE_KEY = "format_landscape";
	
	/**
	 * Normal templates
	 */
	public static final String NORMAL_KEY = "normal";
	
	/**
	 * Tabular templates only
	 */
	public static final String TABULAR_KEY = "tabular";
	
	/**
	 * Prefix for the built in category for the localisation
	 */
	public static final String CATEGORY_PREFIX = "template.category.";
	
	/**
	 * Return a list of all the embedded categories key
	 * Note that the first position must be the all template 
	 * category
	 * 
	 * @return a list of string where every string is the key
	 * of a category
	 */
	public static List<String> getCategoriesList(){
		List<String> result = new ArrayList<String>();
		result.add(ALL_TYPES_KEY);
		result.add(NORMAL_KEY);
		result.add(TABULAR_KEY);
		result.add(A4_KEY);
		result.add(LANDSCAPE_KEY);
		return result;
	}
}
