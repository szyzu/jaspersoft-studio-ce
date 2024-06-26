/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.templates;

import java.net.URL;

/**
 * Exception thrown when trying to load the JasperDesign of a template bundle and
 * something goes wrong
 * 
 * @author Orlandin Marco
 *
 */
public class TemplateLoadingException extends RuntimeException {

	private static final long serialVersionUID = 3801898572713727964L;
	
	private URL templateURL;

	public TemplateLoadingException(String message, Throwable cause, URL templateURL) {
	    super(message, cause);
	    this.templateURL = templateURL;
	}

	public URL getTemplateURL() {
		return templateURL;
	}
	
}
