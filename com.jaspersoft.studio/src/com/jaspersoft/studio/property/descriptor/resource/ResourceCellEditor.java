/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.resource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.property.descriptor.ATextDialogCellEditor;

public class ResourceCellEditor extends ATextDialogCellEditor {

	public ResourceCellEditor(Composite parent) {
		super(parent);
	}

	public ResourceCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		Shell shell = cellEditorWindow.getShell();
		FilteredResourcesSelectionDialog dialog = new FilteredResourcesSelectionDialog(shell, false, ResourcesPlugin
				.getWorkspace().getRoot(), IResource.FILE);
		dialog.setTitle(Messages.ResourceCellEditor_open_resource);
		dialog.setInitialPattern("*.properties"); //$NON-NLS-1$
		// dialog.setMessage("Please choose the Resource bundle:");
		// dialog.setMessage("Enter the name prefix or pattern (?, *, or camel case)");
		if (dialog.open() == Window.OK) {
			IFile file = (IFile) dialog.getFirstResult();
			if (file != null)
				return convertFile2Value(file);
		}
		return null;
	}

	protected String convertFile2Value(IFile f) {
		return f.getProjectRelativePath().toOSString();
	}
}
