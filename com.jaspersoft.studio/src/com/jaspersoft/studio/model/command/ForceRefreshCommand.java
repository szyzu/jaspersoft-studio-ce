/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.command;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.IGraphicalPropertiesHandler;
import com.jaspersoft.studio.model.genericElement.MGenericElement;

/**
 * Command to force the graphical refresh of a single element. Both the execute
 * or the undo will do the refresh of the element
 * 
 * @author Orlandin Marco
 *
 */
public class ForceRefreshCommand extends Command {

	/**
	 * Element to refresh
	 */
	private ANode element;
	
	/**
	 * Create the command
	 *  
	 * @param element not null element to refresh
	 */
	public ForceRefreshCommand(ANode element){
		this.element = element;
	}
	
	@Override
	public boolean canUndo() {
		return true;
	}
	
	/**
	 * Set the flag to refresh the element to true and the fire the event to repaint it
	 */
	@Override
	public void execute() {
		if (element instanceof IGraphicalPropertiesHandler){
			((IGraphicalPropertiesHandler)element).setChangedProperty(true);
		}
		PropertyChangeEvent event = new PropertyChangeEvent(element, MGenericElement.FORCE_GRAPHICAL_REFRESH, null, null);
		element.propertyChange(event);
	}
	
	/**
	 * Call the execute
	 */
	@Override
	public void undo() {
		execute();
	}
	
	
}
