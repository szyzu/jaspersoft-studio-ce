/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.table.action;

import net.sf.jasperreports.components.table.DesignCell;
import net.sf.jasperreports.engine.JRStyle;

import org.eclipse.gef.commands.Command;

/**
 * The command to remove the style of a table cell, support the undo
 * 
 * @author Orlandin Marco
 *
 */
public class RemoveStyleCommand extends Command{
	
	private DesignCell cell;
	
	private JRStyle oldStyle;
	
	private String oldStyleReference;
	
	public RemoveStyleCommand(DesignCell cell){
		this.cell = cell;
		oldStyle = null;
		oldStyleReference = null;
	}
	

	@Override
	public void execute() {
		oldStyle = cell.getStyle();
		oldStyleReference = cell.getStyleNameReference();
		cell.setStyle(null);
		cell.setStyleNameReference(null);
	}
	
	@Override
	public void undo() {
		cell.setStyle(oldStyle);
		cell.setStyleNameReference(oldStyleReference);
	}
	
	/**
	 * Undo is available if the cell is not null. A null value for the style is acceptable since a null 
	 * style means no style
	 */
	@Override
	public boolean canUndo() {
		return (cell != null);
	}

}
