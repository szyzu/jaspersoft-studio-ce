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
 * /* The Class BringToBackAction.
 */
public class BringToBackAction extends ACachedSelectionAction implements IGlobalAction {

	/** The Constant ID. */
	public static final String ID = "bring_back"; //$NON-NLS-1$

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *          The part for this action
	 */
	public BringToBackAction(IWorkbenchPart part) {
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

		OrderUtil.reorderElements(graphicalElements);
		JSSCompoundCommand compoundCmd = new JSSCompoundCommand("Bring To Back", null); //$NON-NLS-1$
		int j = 0;
		for (Object model : graphicalElements) {
			Command cmd = null;
			ANode parent = (ANode) ((MGraphicElement) model).getParent();
			compoundCmd.setReferenceNodeIfNull(parent);
			if (parent != null && parent.getChildren().indexOf(model) > 0) {
				cmd = OutlineTreeEditPartFactory.getReorderCommand((ANode) model, parent, j);
				j++;
			} else
				return null;
			if (cmd != null)
				compoundCmd.add(cmd);
		}
		return compoundCmd;
	}

	/**
	 * Performs the create action on the selected objects.
	 */
	public void run() {
		execute(createCommand());
	}

	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setText(Messages.BringToBackAction_send_to_back);
		setToolTipText(Messages.BringToBackAction_send_to_back_tool_tip);
		setId(BringToBackAction.ID);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor(
				"icons/eclipseapps/elcl16/send_to_back.gif")); //$NON-NLS-1$
		setDisabledImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor(
				"icons/eclipseapps/elcl16/send_to_back.gif")); //$NON-NLS-1$
		setEnabled(false);
	}
}
