/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.formatting.actions;

import java.util.List;

import net.sf.jasperreports.engine.design.JRDesignElement;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.property.SetValueCommand;

public class RemoveHSpaceAction extends AbstractFormattingAction {

	/** The Constant ID. */
	public static final String ID = "removehspace"; //$NON-NLS-1$
	
	public RemoveHSpaceAction(IWorkbenchPart part) {
		super(part);
		setText(Messages.RemoveHSpaceAction_actionName);
		setToolTipText(Messages.RemoveHSpaceAction_actionDescription);
		setId(ID);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/resources/elem_add_hspace_zero.png"));  //$NON-NLS-1$
	}
	
	public static JSSCompoundCommand generateCommand(List<APropertyNode> nodes){
		JSSCompoundCommand command = new JSSCompoundCommand(null);
		
		if (nodes.isEmpty()) return command;
		List<APropertyNode> sortedElements = sortXY(nodes);

		JRDesignElement jrElement = (JRDesignElement)sortedElements.get(0).getValue();
    int current_x = jrElement.getX() + jrElement.getWidth();

    for (int i=1; i<sortedElements.size(); ++i)
    {
    		APropertyNode element = sortedElements.get(i);
    		command.setReferenceNodeIfNull(element);
        jrElement = (JRDesignElement)element.getValue();
        SetValueCommand setCommand = new SetValueCommand();
  			setCommand.setTarget(element);
  			setCommand.setPropertyId(JRDesignElement.PROPERTY_X);
  			setCommand.setPropertyValue(current_x);
	      command.add(setCommand);
        current_x += jrElement.getWidth();
    }
    
    return command;
	}

	@Override
	protected Command createCommand() {
		List<APropertyNode> nodes = getOperationSet();
		JSSCompoundCommand command = null;
		if (!nodes.isEmpty()){
			command = generateCommand(nodes);
			command.setDebugLabel(getText());
		}
		return command;
	}

	@Override
	protected boolean calculateEnabled() {
		return getOperationSet().size()>1;
	}

}
