/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.table.model.table.command;

import java.util.ArrayList;
import java.util.Arrays;

import net.sf.jasperreports.engine.design.JRDesignStyle;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.table.model.MTable;
import com.jaspersoft.studio.components.table.model.dialog.ApplyTableStyleAction;
import com.jaspersoft.studio.components.table.model.dialog.TableStyle;

/**
 * The command to update the TableStyle of a table, support the undo
 * 
 * @author Orlandin Marco
 *
 */
public class UpdateStyleCommand extends Command{
	
	/**
	 * The model of the table
	 */
	private MTable table;
	
	/**
	 * The styles of the table before the change
	 */
	private JRDesignStyle[] oldStyles;
	
	/**
	 * The new styles
	 */
	private TableStyle newStyleTemplate;
	
	/**
	 * True if the new styles will overwrite the old ones, false if the old ones will keep and 
	 * the new ones will have a different name
	 */
	private boolean updateOldStyles;
	
	/**
	 * Create the command to change the TableStyle of a table
	 * 
	 * @param table The model of the table
	 * @param newStyle The new styles to apply to the table
	 * @param updateOldStyles True if the new styles will overwrite the old ones, false if the old ones will keep and 
	 * the new ones will have a different name and the Table element will use these new ones
	 */
	public UpdateStyleCommand(MTable table, TableStyle newStyle, boolean updateOldStyles){
		this.table = table;
		this.newStyleTemplate = newStyle;
		oldStyles = null;
		this.updateOldStyles = updateOldStyles;
	}
	

	@Override
	public void execute() {
		ApplyTableStyleAction applyAction = new ApplyTableStyleAction(newStyleTemplate, table.getValue()); 
		//Save the old styles for the undo
		JRDesignStyle[] tableStyles = applyAction.getStylesFromTable(table.getJasperDesign());
		oldStyles = new JRDesignStyle[tableStyles.length];
		for(int i = 0; i < tableStyles.length; i++){
			JRDesignStyle currentStyle = tableStyles[i];
			if (currentStyle != null){
				oldStyles[i] = (JRDesignStyle)currentStyle.clone();
			}
		}
		//Apply the new style, the old one if not overwritten are not removed
		applyAction.updateStyle(table.getJasperDesign(), newStyleTemplate, updateOldStyles, false);
		table.setChangedProperty(true);
	}
	
	@Override
	public void undo() {
		ArrayList<JRDesignStyle> styles =  new ArrayList<JRDesignStyle>(Arrays.asList(oldStyles));
		ApplyTableStyleAction applyAction = new ApplyTableStyleAction(styles, table.getValue()); 
		//Restore the new style, if the update has created new styles they will be also removed
		applyAction.updateStyle(table.getJasperDesign(), styles, updateOldStyles, true);
		oldStyles = null;
		table.setChangedProperty(true);
	}
	
	/**
	 * Undo is available if the table and the styles previous the update are available 
	 */
	@Override
	public boolean canUndo() {
		return (table != null && oldStyles != null);
	}

}
