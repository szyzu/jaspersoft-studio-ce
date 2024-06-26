/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.properties.view;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Represents a tab descriptor provider for the tabbed property view.
 * 
 * @author Anthony Hunter
 * @since 3.4
 */
public interface ITabDescriptorProvider {

	/**
	 * Returns all tab descriptors.
	 * 
	 * @param part
	 *            the workbench part containing the selection
	 * @param selection
	 *            the current selection.
	 * @return all section descriptors.
	 */
	public ITabDescriptor[] getTabDescriptors(IWorkbenchPart part,
			ISelection selection);
}
