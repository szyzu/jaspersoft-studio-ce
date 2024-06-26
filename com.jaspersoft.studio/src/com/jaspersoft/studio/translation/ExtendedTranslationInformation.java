/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.translation;

import com.jaspersoft.translation.resources.TranslationInformation;

/**
 * Contains the informations and resources of a translation of a single plugin and plus
 * other information to generate a fragment for the plugin
 * 
 * @author Orlandin Marco
 *
 */
public class ExtendedTranslationInformation extends TranslationInformation {

	/**
	 * The plugin name of the host, typically this is the same of the plugin name of the superclass
	 */
	private String hostPluginName = "";
	
	/**
	 * Minimum version of the host plugin
	 */
	private String hostPluginVersion = "";
	
	/**
	 * Name of the fragment generated from this Translation information
	 */
	private String bundleName = "";
	
	/**
	 * Version of the fragment
	 */
	private String bundleVersion = "";
	
	/**
	 * Producer of the fragment 
	 */
	private String bundleProducer = "";
	
	/**
	 * Create an instance of the class
	 * 
	 * @param baseInfo The translation informations
	 */
	public ExtendedTranslationInformation(TranslationInformation baseInfo) {
		super(baseInfo.getPluginName(), baseInfo.getResources());
	}
	
	/**
	 * Set the plugin name of the host
	 * 
	 * @param hostPluginName the new name. Must be not null and not empty
	 */
	public void setHostPluginName(String hostPluginName){
		this.hostPluginName = hostPluginName;
	}
	
	/**
	 * Set the Minimum version of the host plugin
	 * 
	 * @param hostPluginVersion the new version. Must be not null and not empty
	 */
	public void setHostPluginVersion(String hostPluginVersion){
		this.hostPluginVersion = hostPluginVersion;
	}
	
	/**
	 * Set the name of the fragment generated from this Translation information
	 *  
	 * @param bundleName the new name. Must be not null and not empty
	 */
	public void setBundleName(String bundleName){
		this.bundleName = bundleName;
	}
	
	/**
	 * The version of the fragment
	 * 
	 * @param bundleVersion the new version. Must be not null and not empty
	 */
	public void setBundleVersion(String bundleVersion){
		this.bundleVersion = bundleVersion;
	}
	
	/**
	 * set the name of the producer of the fragment 
	 * 
	 * @param bundleProducer  the new name. Must be not null and not empty
	 */
	public void setBundleProducer(String bundleProducer){
		this.bundleProducer = bundleProducer;
	}
	
	/**
	 * Return the host plugin name
	 * 
	 */
	public String getHostPluginName(){
		return hostPluginName;
	}
	
	/**
	 * Return the Minimum version of the host plugin
	 * 
	 */
	public String getHostPluginVersion(){
		return hostPluginVersion;
	}
	
	/**
	 * Return the name of the fragment generated from this Translation information
	 * 
	 */
	public String getBundleName(){
		return bundleName;
	}
	
	/**
	 * Return the version of the fragment
	 * 
	 */
	public String getBundleVersion(){
		return bundleVersion;
	}
	
	/**
	 * Return the name of the producer of the fragment 
	 * 
	 */
	public String getBundleProducer(){
		return bundleProducer;
	}

}
