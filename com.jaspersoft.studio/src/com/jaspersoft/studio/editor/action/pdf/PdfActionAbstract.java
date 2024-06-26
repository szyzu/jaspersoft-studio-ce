/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.pdf;

import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.editor.action.CustomSelectionAction;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.property.SetValueCommand;

/**
 * The Class PdfActionAbstract implements common action (most UI related) of the
 * pdf action
 */
public abstract class PdfActionAbstract extends CustomSelectionAction {

	/** Id of the actions */
	public final String ID_Full; // $NON-NLS-1$
	public final String ID_Start; // $NON-NLS-1$
	public final String ID_End;
	public final String ID_None; // $NON-NLS-1$

	/** Possible values of the action: start, end, full or none */
	protected Position action_position;

	/**
	 * Abstract method to return the property name
	 * 
	 * @return Property for which one the value must be changed
	 */
	protected abstract String getPropertyName();

	/**
	 * Base constructor, construct the inner common object of an action
	 * 
	 * @param part The part for this action
	 * @param action_position Identify The position of the label
	 * @param ID_Full Id of the action when click on full
	 * @param ID_Start Id of the action when click on Start
	 * @param ID_End Id of the action when click on End
	 * @param ID_None Id of the action when click on None
	 */
	public PdfActionAbstract(IWorkbenchPart part, Position action_position, String ID_Full, String ID_Start,
			String ID_End, String ID_None) {
		super(part, IAction.AS_CHECK_BOX);
		this.action_position = action_position;
		this.ID_Full = ID_Full;
		this.ID_Start = ID_Start;
		this.ID_End = ID_End;
		this.ID_None = ID_None;
		// the property need to be registered
		PropertiesList.addItem(getPropertyName());
		initUI();
	}

	@Override
	public boolean isChecked() {
		ischecked = true;
		List<Object> graphicalElements = editor.getSelectionCache().getSelectionModelForType(MGraphicElement.class);
		if (graphicalElements.isEmpty()) {
			ischecked = false;
		} else {
			String attributeId = getPropertyName();
			String value = getPropertyValue();
			for (Object element : graphicalElements) {
				MGraphicElement model = (MGraphicElement) element;
				JRPropertiesMap v = model.getPropertiesMap();
				if (v == null) {
					ischecked = false;
					break;
				} else {
					Object oldValue = v.getProperty(attributeId);
					if (oldValue == null || !oldValue.equals(value)) {
						ischecked = false;
						break;
					}
				}
			}
		}
		return ischecked;
	}

	/**
	 * Create the contextual menu with the label
	 */
	protected void initUI() {
		switch (action_position) {
		case Full:
			setId(ID_Full);
			setText(Messages.PdfAction_Full);
			setToolTipText(null);
			setImageDescriptor(null); // $NON-NLS-1$
			setDisabledImageDescriptor(null); // $NON-NLS-1$
			break;
		case Start:
			setId(ID_Start);
			setText(Messages.PdfAction_Start);
			setToolTipText(null);
			setImageDescriptor(null); // $NON-NLS-1$
			setDisabledImageDescriptor(null); // $NON-NLS-1$
			break;
		case End:
			setId(ID_End);
			setText(Messages.PdfAction_End);
			setToolTipText(null);
			setImageDescriptor(null); // $NON-NLS-1$
			setDisabledImageDescriptor(null); // $NON-NLS-1$
			break;
		case None:
			setId(ID_None);
			setText(Messages.PdfAction_None);
			setToolTipText(null);
			setImageDescriptor(null); // $NON-NLS-1$
			setDisabledImageDescriptor(null); // $NON-NLS-1$
			break;
		}
	}

	/**
	 * Return a string that represent the property value, associating a
	 * <code>Position</code> to a string
	 * 
	 * @return a string representing the position value
	 */
	protected String getPropertyValue() {
		String value = "";
		switch (action_position) {
		case Full:
			value = "full";
			break;
		case Start:
			value = "start";
			break;
		case End:
			value = "end";
			break;
		case None:
			value = null;
			break;
		}
		return value;

	}

	/**
	 * Create the command for the selected action
	 * 
	 * @param model Model of the selected item
	 * @return the command to execute
	 */
	public Command createCommand(MGraphicElement model) {
		SetValueCommand cmd = new SetValueCommand();
		cmd.setTarget(model);
		cmd.setPropertyId(APropertyNode.PROPERTY_MAP);
		String name = getPropertyName();
		JRPropertiesMap v = (JRPropertiesMap) model.getPropertyValue(APropertyNode.PROPERTY_MAP);
		Object oldValue = null;
		if (v == null) {
			v = new JRPropertiesMap();
		} else {
			oldValue = v.getProperty(name);
			v.removeProperty(name);
		}
		String value = getPropertyValue();
		if (value != null && !value.equals(oldValue))
			v.setProperty(name, value);
		cmd.setPropertyValue(v);
		return cmd;
	}

	@Override
	protected Command createCommand() {
		List<Object> graphicalElements = editor.getSelectionCache().getSelectionModelForType(MGraphicElement.class);
		if (graphicalElements.isEmpty())
			return null;
		JSSCompoundCommand command = new JSSCompoundCommand(getText(), null);
		for (Object element : graphicalElements) {
			MGraphicElement grModel = (MGraphicElement) element;
			command.setReferenceNodeIfNull(grModel);
			command.add(createCommand(grModel));
		}
		return command;
	}

	/**
	 * Performs the create action on the selected objects.
	 */
	@Override
	public void run() {
		execute(createCommand());
	}

}
