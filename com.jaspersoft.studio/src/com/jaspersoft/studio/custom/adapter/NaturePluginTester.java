/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.custom.adapter;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * Class to evaluate the nature of a project and check if it is a Plugin Project
 * 
 * @author Orlandin Marco
 *
 */
public class NaturePluginTester extends PropertyTester {

	/**
	 * Check if a project is a plugin project
	 * 
	 * @param receiver an IProject, if the parameter has a different type
	 * the method return false
	 * @return true if the receiver is an IProject with a plugin project nature, false otherwise
	 */
	public static boolean evaluateElementNature(Object receiver){
		if (receiver instanceof IProject){
			IProject project = (IProject)receiver;  
		     try {
		    	if (project.isOpen() && project.hasNature("org.eclipse.pde.PluginNature")) return true;
		    	return false;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	    return false;
	}
	

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof Collection){
			Collection<?> selection = (Collection<?>) receiver;
			boolean allRight = !selection.isEmpty();
			for (Iterator<?> it = selection.iterator(); it.hasNext() && allRight;) {
				allRight = evaluateElementNature(it.next());
			}
			return allRight;
		} else {
			return evaluateElementNature(receiver);
		}

	}

}
