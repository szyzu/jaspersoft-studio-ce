/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.translation;

import java.util.Locale;

import org.eclipse.swt.graphics.ImageData;

/**
 * Simple container to bind a Locale to an image that represent that locale.
 * For example the image could be a flag representing the language of the locale
 * 
 * @author Orlandin Marco
 *
 */
public class ImageLocale {
	
	/**
	 * The locale
	 */
	private Locale locale;
	
	/**
	 * The image to associate to the locale
	 */
	private ImageData image;
	
	/**
	 * Create an instance of the class
	 * 
	 * @param locale The locale
	 * @param image image to associate to the locale
	 */
	public ImageLocale(Locale locale, ImageData image){
		this.locale = locale;
		this.image = image;
	}
	
	/**
	 * Return the image associated to the locale
	 * 
	 * @return an swt image, could be null
	 */
	public ImageData getImage(){
		return image;
	}
	
	/**
	 * Return the locale
	 * 
	 * @return a locale
	 */
	public Locale getLocale(){
		return locale;
	}
	
}
