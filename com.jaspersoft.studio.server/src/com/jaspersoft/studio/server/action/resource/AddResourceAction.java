/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.action.resource;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.wizard.resource.AddResourceWizard;

public class AddResourceAction extends Action {
	private static final String ID = "ADDRESOURCEDESCRIPTOR";
	private TreeViewer treeViewer;

	public AddResourceAction(TreeViewer treeViewer) {
		super();
		setId(ID);
		setText(Messages.common_new);
		setToolTipText(Messages.common_new);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD_DISABLED));
		this.treeViewer = treeViewer;
	}

	@Override
	public boolean isEnabled() {
		Object firstElement = ((TreeSelection) treeViewer.getSelection()).getFirstElement();
		boolean b = firstElement != null;
		if (b) {
			if (firstElement instanceof AMResource) {
				AMResource mres = (AMResource) firstElement;
				int pmask = mres.getValue().getPermissionMask(mres.getWsClient());
				b = b && (pmask == 1 || (pmask & 4) == 4);
				if (isOrganizations(mres))
					return false;
			}
		}
		return b;
	}

	public static boolean isSpecialFolder(AMResource mres) {
		if (isOrganizations(mres))
			return true;
//		if (mres.getParent() instanceof AMResource && isOrganizations((AMResource) mres.getParent()))
//			return true;
		return false;
	}

	public static boolean isOrganizations(AMResource mres) {
		if (mres.getValue().getUriString() != null && mres.getValue().getUriString().equals("/organizations"))
			return true;
		return false;
	}

	@Override
	public void run() {
		TreeSelection s = (TreeSelection) treeViewer.getSelection();
		TreePath[] p = s.getPaths();
		for (int i = 0; i < p.length;) {
			final Object obj = p[i].getLastSegment();
			if (obj instanceof ANode) {
				ANode parent = (ANode) obj;
				AddResourceWizard wizard = new AddResourceWizard(parent);
				WizardDialog dialog = new WizardDialog(UIUtils.getShell(), wizard);
				dialog.create();
				dialog.open();
			}
			break;
		}

	}

}
