/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.order;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.action.ACachedSelectionAction;
import com.jaspersoft.studio.editor.action.IGlobalAction;
import com.jaspersoft.studio.editor.outline.OutlineTreeEditPartFactory;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.MGraphicElement;

/*
 * /* The Class BringForwardAction.
 */
public class BringForwardAction extends ACachedSelectionAction implements IGlobalAction {

	/** The Constant ID. */
	public static final String ID = "bring_forward"; //$NON-NLS-1$

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *          The part for this action
	 */
	public BringForwardAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	/**
	 * Create a command to create the selected objects.
	 * 
	 * @param objects
	 *          The objects to be deleted.
	 * @return The command to remove the selected objects.
	 */
	@Override
	public Command createCommand() {
		List<Object> graphicalElements = editor.getSelectionCache().getSelectionModelForType(MGraphicElement.class);
		if (graphicalElements.isEmpty())
			return null;

		OrderUtil.reorderReverseElements(graphicalElements);

		JSSCompoundCommand compoundCmd = new JSSCompoundCommand("Bring Forward", null); //$NON-NLS-1$
		compoundCmd.enableSelectionRestore(true);
		for (Object model : graphicalElements) {
			Command cmd = null;
			ANode parent = (ANode) ((MGraphicElement) model).getParent();
			compoundCmd.setReferenceNodeIfNull(parent);
			if (parent != null && parent.getChildren() != null) {
				int newIndex = parent.getChildren().indexOf(model) + 1;
				if (newIndex < parent.getChildren().size()) {
					cmd = OutlineTreeEditPartFactory.getReorderCommand((ANode) model, parent, newIndex);
				} else
					return null;
				if (cmd != null)
					compoundCmd.add(cmd);
			}
		}
		return compoundCmd;
	}

	/**
	 * Performs the create action on the selected objects.
	 */
	public void run() {
		execute(command);
	}

	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setText(Messages.BringForwardAction_bring_forward);
		setToolTipText(Messages.BringForwardAction_bring_forward_tool_tip);
		setId(BringForwardAction.ID);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor(
				"icons/eclipseapps/elcl16/bring_forward.gif")); //$NON-NLS-1$
		setDisabledImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor(
				"icons/eclipseapps/dlcl16/bring_forward.gif")); //$NON-NLS-1$
		setEnabled(false);
	}
}
