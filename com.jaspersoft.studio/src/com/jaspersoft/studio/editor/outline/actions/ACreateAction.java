/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.outline.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.editor.action.ACachedSelectionAction;

/*
 * The Class ACreateAction.
 */
public abstract class ACreateAction extends ACachedSelectionAction {

	/** The creation factory. */
	protected CreationFactory creationFactory;

	/** The location. */
	protected Point location = new Point(0, 0);

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *          The part for this action
	 */
	public ACreateAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	/**
	 * Sets the creation factory.
	 * 
	 * @param creationFactory
	 *          the new creation factory
	 */
	public void setCreationFactory(CreationFactory creationFactory) {
		this.creationFactory = creationFactory;
	}

	/**
	 * Get the CreationRequest for the EditParts, can be null
	 */
	protected CreateRequest getCreateRequest(List<Object> objects) {
		CreateRequest createReq = new CreateRequest(RequestConstants.REQ_CREATE);
		createReq.setLocation(location);
		createReq.setFactory(creationFactory);
		Map<Object, Object> map = new HashMap<Object, Object>();
		if (!setExtendedData(map, objects))
			return null;
		createReq.setExtendedData(map);
		return createReq;
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
		List<Object> objects = getSelectedObjects();
		if (objects.isEmpty())
			return null;
		if (!(objects.get(0) instanceof EditPart))
			return null;

		CreateRequest createReq = getCreateRequest(objects);
		if (createReq == null){
			return null;
		}

		JSSCompoundCommand jssCcmd = new JSSCompoundCommand(null);		
		for (int i = 0; i < objects.size(); i++) {
			Object obj = objects.get(i);
			if (obj instanceof EditPart) {
				EditPart object = (EditPart) obj;
				//Set the node if necessary to disable the refresh
				jssCcmd.setReferenceNodeIfNull(object.getModel());	
				Command cmd = object.getCommand(createReq);
				if (cmd != null) {
					jssCcmd.add(cmd);
				}
			}
		}
		if(!jssCcmd.isEmpty()) {
			return jssCcmd;
		}
		else {
			return null;
		}
	}

	protected boolean setExtendedData(Map<Object, Object> map, List<?> objects) {
		return true;
	}

	/**
	 * Performs the create action on the selected objects.
	 */
	@Override
	public void run() {
		execute(createCommand());
	}

}
