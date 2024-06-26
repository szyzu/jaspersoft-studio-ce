/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.part;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.crosstab.figure.EmptyCellFigure;
import com.jaspersoft.studio.components.crosstab.model.MCrosstab;
import com.jaspersoft.studio.components.crosstab.model.header.MCrosstabHeader;
import com.jaspersoft.studio.editor.gef.figures.APageFigure;
import com.jaspersoft.studio.editor.gef.parts.FigureEditPart;
import com.jaspersoft.studio.editor.gef.parts.editPolicy.PageLayoutEditPolicy;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.MPage;

/*
 * BandEditPart creates the figure for the band. The figure is actually just the bottom border of the band. This allows
 * to drag this border to resize the band. The PageEditPart sets a specific contraint for the BandEditPart elements in
 * order to make them move only vertically. The BandMoveEditPolicy is responsable for the feedback when the band is
 * dragged.
 * 
 * @author Chicu Veaceslav, Giulio Toffoli
 * 
 */
public class CrosstabHeaderEditPart extends ACrosstabCellEditPart {

	@Override
	public MCrosstabHeader getModel() {
		return (MCrosstabHeader) super.getModel();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new PageLayoutEditPolicy() {
			@Override
			protected Command getCreateCommand(ANode parent, Object obj, Rectangle constraint, int index,
					Request request) {
				if (parent instanceof MPage)
					parent = getModel();
				return super.getCreateCommand(parent, obj, constraint, index, request);
			}
		});
	}

	@Override
	protected void setupFigure(IFigure rect) {
		updateContainerSize();
		MCrosstabHeader model = getModel();
		rect.setToolTip(new Label(model.getToolTip()));

		Rectangle bounds = model.getBounds();
		int x = bounds.x + APageFigure.PAGE_BORDER.left;
		int y = bounds.y + APageFigure.PAGE_BORDER.top;

		rect.setLocation(new Point(x, y));

		((EmptyCellFigure) rect).setJRElement(getDrawVisitor(), new Dimension(bounds.width, bounds.height));
		rect.setSize(bounds.width, bounds.height);

		updateRulers();

		if (getSelected() == 1)
			updateRulers();
		else {
			List<?> selected = getViewer().getSelectedEditParts();
			if (selected.isEmpty())
				updateRulers();
			else
				for (Object obj : selected) {
					if (obj instanceof FigureEditPart) {
						FigureEditPart figEditPart = (FigureEditPart) obj;
						if (figEditPart.getModel().getParent() == getModel())
							figEditPart.updateRulers();
					}
				}
		}
	}

	@Override
	protected MCrosstab getCrosstab() {
		return getModel().getCrosstab();
	}

}
