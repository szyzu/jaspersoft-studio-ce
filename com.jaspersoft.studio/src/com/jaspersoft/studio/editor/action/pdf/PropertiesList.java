/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.pdf;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Static class used to register a PDF properties, needed to have a common and
 * easy expandable list of properties. Knowing them it is important because when
 * a new property is set for a model, all other pdf properties must be deleted
 * (a model can have only one pdf property).
 * 
 * @author Orlandin Marco
 *
 */
public class PropertiesList {

	/**
	 * Contain the list of registered properties
	 */
	private static HashSet<String> propertyList = new HashSet<>();

	/**
	 * Register a new property
	 * 
	 * @param newItem id of the new property
	 */
	public static void addItem(String newItem) {
		propertyList.add(newItem);

	}

	/**
	 * Return an iterator to a property of the list
	 * 
	 * @return Iterator to a property, use HasNex()t to know it there are an
	 * element to read and Next() to read it
	 */
	public static Iterator<String> getIterator() {
		return propertyList.iterator();
	}

	/**
	 * Size of the list
	 * 
	 * @return number of registered elements
	 */
	public static int size() {
		return propertyList.size();
	}

}
