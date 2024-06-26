/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.part.editpolicy;

import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.components.crosstab.CrosstabCell;
import com.jaspersoft.studio.components.crosstab.CrosstabManager;
import com.jaspersoft.studio.components.crosstab.model.MCrosstab;
import com.jaspersoft.studio.components.crosstab.model.cell.MCell;
import com.jaspersoft.studio.components.crosstab.model.header.MCrosstabHeader;
import com.jaspersoft.studio.components.crosstab.model.header.MCrosstabHeaderCell;
import com.jaspersoft.studio.components.crosstab.model.nodata.MCrosstabWhenNoDataCell;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.util.ModelVisitor;
import com.jaspersoft.studio.property.SetValueCommand;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.crosstabs.design.JRCrosstabOrigin;
import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabCell;

public class CreateResize {
	public static Command createResizeCommand(ChangeBoundsRequest request,
			GraphicalEditPart editPart) {
		Object emodel = editPart.getModel();
		MCell model = null;
		if (emodel instanceof MCrosstabHeader) {
			MCrosstabHeader chmodel = (MCrosstabHeader) emodel;
			MCrosstab crosstab = chmodel.getCrosstab();
			CrosstabManager cManager = crosstab.getCrosstabManager();
			List<CrosstabCell> cells = null;
			if (request.getResizeDirection() == PositionConstants.SOUTH){
				cells = cManager.getBottomOf(MCrosstabHeader.cell);
			} else if (request.getResizeDirection() == PositionConstants.EAST) {
				cells = cManager.getRightOf(MCrosstabHeader.cell);
			}
			model = getNearHeader(model, crosstab, cells);
		} else if (emodel instanceof MCell)
			model = (MCell) emodel;
		if (model == null)
			return null;
		Dimension sizeDelta = request.getSizeDelta();
		CrosstabManager cManager = model.getMCrosstab().getCrosstabManager();

		JSSCompoundCommand c = new JSSCompoundCommand("Change Cell Size", model); //$NON-NLS-1$

		PrecisionRectangle deltaRect = new PrecisionRectangle(new Rectangle(0,0, sizeDelta.width, sizeDelta.height));
		editPart.getFigure().translateToRelative(deltaRect);

		JRDesignCellContents jrdesign = (JRDesignCellContents) model.getValue();

		if (request.getSizeDelta().width != 0) {
			int w = deltaRect.width;
			if (request.getResizeDirection() == PositionConstants.WEST){
				//find the cell on the left and resize it to take the widh removed from the resized celll
				List<CrosstabCell> cells = cManager.getLeftOf(getCrosstabCell(model));
				MCell leftCell = getNearCellModel(model, cells);
				if (leftCell != null){
					JRDesignCellContents leftDesign = (JRDesignCellContents) leftCell.getValue();
					int width = leftDesign.getWidth() - w;
					if (width >= 0){
						SetValueCommand setCommand = new SetValueCommand();
						setCommand.setTarget(leftCell);
						setCommand.setPropertyId(JRDesignCrosstabCell.PROPERTY_WIDTH);
						setCommand.setPropertyValue(width);
						c.add(setCommand);
					}
				}
			} else if (request.getResizeDirection() == PositionConstants.EAST){
				//If the request is a drag to east and the flag is enabled take the space from the next column
				MCrosstab crosstab = model.getCrosstab();
				if (crosstab != null){
					/*	if (crosstab.hasColumnsAutoresizeProportional()){
						MColumn next = model.getNextColumn();
						if (next != null){
							StandardBaseColumn nextCol = next.getValue();
							int newWidth = nextCol.getWidth() - deltaRect.width;
							if (newWidth < 0) newWidth = 0;;
							c.setWidth(next, newWidth);
						}
					} else if (crosstab.hasColumnsAutoresizeNext()){
						MColumn next = model.getNextColumn();
						if (next != null){
							StandardBaseColumn nextCol = next.getValue();
							int newWidth = nextCol.getWidth() - deltaRect.width;
							if (newWidth < 0){
								//newWidth = 0;
								return null;
							}
							c.setWidth(next, newWidth);
						} 
					}*/
				} 
			}
			int width = jrdesign.getWidth() + w;
			if (width < 0){
				return null;
			}
			
			SetValueCommand setCommand = new SetValueCommand();
			setCommand.setTarget(model);
			setCommand.setPropertyId(JRDesignCrosstabCell.PROPERTY_WIDTH);
			setCommand.setPropertyValue(width);
			c.add(setCommand);
		}
		if (request.getSizeDelta().height != 0) {
			int h = deltaRect.height;
			if (request.getResizeDirection() == PositionConstants.NORTH) {
				List<CrosstabCell> cells = cManager.getTopOf(getCrosstabCell(model));
				MCell upperCell = getNearCellModel(model, cells);
				if (upperCell != null){
					JRDesignCellContents upperDesign = (JRDesignCellContents) upperCell.getValue();
					int height = upperDesign.getHeight() - h;
					if (height >= 0){
						SetValueCommand setCommand = new SetValueCommand();
						setCommand.setTarget(upperCell);
						setCommand.setPropertyId(JRDesignCrosstabCell.PROPERTY_HEIGHT);
						setCommand.setPropertyValue(height);
						c.add(setCommand);
					}
				}
			}
			int height = jrdesign.getHeight() + h;
			if (height < 0)
				return null;
			SetValueCommand setCommand = new SetValueCommand();
			setCommand.setTarget(model);
			setCommand.setPropertyId(JRDesignCrosstabCell.PROPERTY_HEIGHT);
			setCommand.setPropertyValue(height);
			c.add(setCommand);
		}

		if (c.isEmpty())
			return null;
		return c;
	}

	public static MCell getNearHeader(MCell model, MCrosstab crosstab,
			List<CrosstabCell> cells) {
		if (cells == null || cells.isEmpty())
			return null;
		for (CrosstabCell c : cells) {
			if (c.cell == null)
				continue;
			final JRDesignCellContents cell = c.cell;
			model = new ModelVisitor<MCell>(crosstab) {

				@Override
				public boolean visit(INode n) {
					if (n instanceof MCell) {
						MCell mcell = (MCell) n;
						if (ModelUtils.safeEquals(mcell.getValue(),cell)) {
							setObject(mcell);
							return false;
						}

					}
					return true;
				}
			}.getObject();
			if (model != null)
				break;
		}
		return model;
	}

	public static MCell getNearCellModel(MCell model,
			final List<CrosstabCell> cells) {
		if (cells == null || cells.isEmpty())
			return null;
		return new ModelVisitor<MCell>(model.getCrosstab()) {

			@Override
			public boolean visit(INode n) {
				if (n instanceof MCell) {
					MCell mcell = (MCell) n;
					if (mcell.getValue() != null && mcell.getValue().equals(cells.get(cells.size() - 1).cell)) {
						setObject(mcell);
						return false;
					}
				}
				return true;
			}
		}.getObject();
	}

	public static CrosstabCell getCrosstabCell(MCell model) {
		CrosstabCell c = new CrosstabCell(model.getValue());
		if (model.getValue() == null) {
			if (model instanceof MCrosstabHeaderCell)
				c.type = JRCrosstabOrigin.TYPE_HEADER_CELL;
			else if (model instanceof MCrosstabWhenNoDataCell)
				c.type = JRCrosstabOrigin.TYPE_WHEN_NO_DATA_CELL;
		}
		return c;
	}
}
