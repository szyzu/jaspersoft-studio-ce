/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.model.cell;

import net.sf.jasperreports.crosstabs.JRCrosstabCell;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabCell;

import com.jaspersoft.studio.components.crosstab.messages.Messages;
import com.jaspersoft.studio.model.ANode;

/**
 * A group cell is used when a cell is generated from a JRCrosstabCell. This
 * because a JRCrosstabCell keep the reference to the name of the group which
 * it is associated. So if the group name change this cells must be searched
 * and updated too
 * 
 * @author Orlandin Marco
 *
 */
public class MGroupCell extends MCell {

	private static final long serialVersionUID = 101787093761783437L;
	
	/**
	 * The cell with the content that generated this model cell
	 */
	private JRDesignCrosstabCell cell;
	
	public MGroupCell(ANode parent, JRCrosstabCell cell) {
		super(parent, cell.getContents(), "");
		this.cell = (JRDesignCrosstabCell)cell;
	}
	
	@Override
	public String getDisplayText() {
		String colName = cell.getColumnTotalGroup();
		if (colName == null) colName = Messages.CrosstabComponentFactory_detail;
		String rowName = cell.getRowTotalGroup();
		if (rowName == null) rowName = Messages.CrosstabComponentFactory_detail;
		return colName + "/" + rowName;
	}
	
	/**
	 * Return the cell
	 * 
	 * @return a not null cell
	 */
	public JRDesignCrosstabCell getCell(){
		return cell;
	}
}
