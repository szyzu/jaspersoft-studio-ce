/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.wizards.functions;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * Configuration information for the Functions Library generation.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class GenerationInfo {

	private IJavaProject javaProject;
	private IPackageFragmentRoot packageFragmentRoot;
	private IPackageFragment packageFragment;
	private String libraryName;
	private String packageName;

	public GenerationInfo(IJavaProject javaProject, IPackageFragmentRoot packageFragmentRoot,
			IPackageFragment packageFragment, String libraryName, String packageName) {
		this.javaProject = javaProject;
		this.packageFragmentRoot = packageFragmentRoot;
		this.packageFragment = packageFragment;
		this.libraryName = libraryName;
		this.packageName = packageName;
	}
	
	public IJavaProject getJavaProject() {
		return javaProject;
	}
	
	public IPackageFragmentRoot getPackageFragmentRoot() {
		return packageFragmentRoot;
	}
	
	public IPackageFragment getPackageFragment() {
		return packageFragment;
	}
	
	public String getLibraryName() {
		return libraryName;
	}
	
	public String getPackageName() {
		return packageName;
	}
}
