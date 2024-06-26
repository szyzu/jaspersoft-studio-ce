/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.utils;

import java.io.File;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Utility class for Velocity Template Engine operations.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class VelocityUtils {
	
	private VelocityUtils(){
		// prevent instantiation...
	}
	
	/*
	 * @return a simple "naked" {@link VelocityEngine} instance not yet initialized
	 */
	private static VelocityEngine getSimpleVelocityEngine(){
		return new VelocityEngine();
	}

	/**
	 * Returns a "standard" pre-configured {@link VelocityEngine} instance already initialized.
	 * <p>
	 * 
	 * Here are the properties set:
	 * <ul>
	 * 	<li>to load resources (i.e: templates) from classpath</li>
	 * 	<li>to use {@link NullLogChute} logger in order to prevent Velocity from
	 * 	trying to produce the velocity.log file inside the JSS installation folder.
	 * 	It has been verified as problematic in Windows because of folder permissions.</li>
	 * </ul>
	 * 
	 * @return the configured {@link VelocityEngine} instance
	 */
	public static VelocityEngine getConfiguredVelocityEngine(){
		VelocityEngine ve = getSimpleVelocityEngine();
		File sysTempDir = new File(System.getProperty("java.io.tmpdir"), "velocity.log");
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
		ve.setProperty("runtime.log", sysTempDir.getAbsolutePath());
		ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
		ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
		ve.init();
		return ve;
	}
}
