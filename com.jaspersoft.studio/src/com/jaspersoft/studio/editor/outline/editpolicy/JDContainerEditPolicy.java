/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.outline.editpolicy;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.editor.outline.OutlineTreeEditPartFactory;
import com.jaspersoft.studio.model.ANode;
/*
 * The Class JDContainerEditPolicy.
 * 
 * @author Chicu Veaceslav
 */
public class JDContainerEditPolicy extends ContainerEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getOrphanChildrenCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	@Override
	public Command getOrphanChildrenCommand(GroupRequest request) {
		List<?> parts = request.getEditParts();
		JSSCompoundCommand result = new JSSCompoundCommand("orphans", null); //$NON-NLS-1$
		for (int i = 0; i < parts.size(); i++) {
			ANode child = (ANode) ((EditPart) parts.get(i)).getModel();
			result.setReferenceNodeIfNull(child);
			result.add(OutlineTreeEditPartFactory.getOrphanCommand((ANode) getHost().getModel(), child));

		}
		return result.unwrap();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}

}
