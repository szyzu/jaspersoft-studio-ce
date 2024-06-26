/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.properties;

/**
 * A list of status codes for this plug-in.
 * 
 * @author Anthony Hunter
 */
public final class TabbedPropertyViewStatusCodes {

	/**
	 * This class should not be instantiated since it is a static constant
	 * class.
	 */
	private TabbedPropertyViewStatusCodes() {
		/* not used */
	}

	/**
	 * Status code indicating that everything is OK.
	 */
	public static final int OK = 0;

	/**
	 * Status code indicating that a tab was not found for the given tab id.
	 */
	public static final int NO_TAB_ERROR = 1;

	/**
	 * Status code indicating that issue was found loading the section extension
	 * configuration element.
	 */
	public static final int SECTION_ERROR = 2;

	/**
	 * Status code indicating that issue was found loading the tab extension
	 * configuration element.
	 */
	public static final int TAB_ERROR = 3;

	/**
	 * Status code indicating that issue was found loading the contributor
	 * extension configuration element.
	 */
	public static final int CONTRIBUTOR_ERROR = 4;
}
