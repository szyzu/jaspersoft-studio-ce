/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.outline.editpolicy;

import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.TreeEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.TreeContainerEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.editor.action.create.CreateElementAction;
import com.jaspersoft.studio.editor.gef.util.CreateRequestUtil;
import com.jaspersoft.studio.editor.outline.OutlineTreeEditPartFactory;
import com.jaspersoft.studio.model.ANode;

/*
 * The Class JDTreeContainerEditPolicy.
 */
public class JDTreeContainerEditPolicy extends TreeContainerEditPolicy {

	/**
	 * Creates the create command.
	 * 
	 * @param child
	 *            the child
	 * @param index
	 *            the index
	 * @return the command
	 */
	protected Command createCreateCommand(ANode child, int index, boolean typeAdd) {
		return OutlineTreeEditPartFactory.getCreateCommand((ANode) getHost().getModel(), child, null, index, null, typeAdd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.TreeContainerEditPolicy#getAddCommand(org.
	 * eclipse.gef.requests.ChangeBoundsRequest)
	 */
	@Override
	protected Command getAddCommand(ChangeBoundsRequest request) {
		JSSCompoundCommand command = new JSSCompoundCommand(null);
		List<?> editparts = request.getEditParts();
		int index = findIndexOfTreeItemAt(request.getLocation());
		for (int i = 0; i < editparts.size(); i++) {
			EditPart child = (EditPart) editparts.get(i);
			command.setReferenceNodeIfNull(child.getModel());
			if (isAncestor(child, getHost())) {
				command.add(UnexecutableCommand.INSTANCE);
			} else {
				ANode childModel = (ANode) child.getModel();
				// System.out.println(childModel.getDisplayText());
				command.add(createCreateCommand(childModel, index, true));
			}
		}
		return command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.TreeContainerEditPolicy#getCreateCommand(org
	 * .eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		int index = -1;
		if (((TreeEditPart) getHost()).getWidget() != null)
			index = findIndexOfTreeItemAt(request.getLocation());
		if (index < 0)
			index = CreateRequestUtil.getNewIndex(request);

		if (request.getNewObject() instanceof ANode) {
			return createCreateCommand((ANode) request.getNewObject(), index, false);
		} else if (request.getNewObject() instanceof CreateElementAction) {
			CreateElementAction action = (CreateElementAction) request.getNewObject();
			action.dropInto(getHost().getModel(), new Rectangle(), index);
			action.run();
			return action.getCommand();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.TreeContainerEditPolicy#
	 * getMoveChildrenCommand(org.eclipse.gef.requests. ChangeBoundsRequest)
	 */
	@Override
	protected Command getMoveChildrenCommand(ChangeBoundsRequest request) {
		JSSCompoundCommand command = new JSSCompoundCommand(null);
		//command.enableSelectionRestore(true);
		List<?> editparts = request.getEditParts();
		List<?> children = getHost().getChildren();
		int newIndex = findIndexOfTreeItemAt(request.getLocation());
		for (int i = 0; i < editparts.size(); i++) {
			EditPart child = (EditPart) editparts.get(i);
			command.setReferenceNodeIfNull(child.getModel());
			int tempIndex = newIndex;
			int oldIndex = children.indexOf(child);
			if (oldIndex == tempIndex || oldIndex + 1 == tempIndex) {
				command.add(UnexecutableCommand.INSTANCE);
				return command;
			} else if (oldIndex <= tempIndex) {
				tempIndex--;
			} else
				tempIndex += i;
			command.add(OutlineTreeEditPartFactory.getReorderCommand((ANode) child.getModel(),
					(ANode) getHost().getModel(), tempIndex));
		}
		return command;
	}

	/**
	 * Checks if is ancestor.
	 * 
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return true, if is ancestor
	 */
	protected boolean isAncestor(EditPart source, EditPart target) {
		ANode targetModel = (ANode) target.getModel();
		// if (source == target)
		// return true;
		if (targetModel.getValue() == null)
			return true;
		// if (target.getParent() != null)
		// return isAncestor(source, target.getParent());
		return false;
	}
}
