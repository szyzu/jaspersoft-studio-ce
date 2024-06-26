/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.ui.gef.figures;

import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

public class SqlTableFigure extends Figure {
	public static final Insets INSETS = new Insets(3, 3, 3, 3);

	private class NameLabel extends Label {

		private NameLabel(String s) {
			super(s);
			setIconAlignment(PositionConstants.RIGHT);
			setForegroundColor(UIUtils.getSystemColor(SWT.COLOR_BLACK));
		}

		@Override
		public Insets getInsets() {
			return INSETS;
		}
	}

	public static final Color classColor = SWTResourceManager.getColor(255, 255, 206);
	private Label lblName;
	private ColumnsFigure attributeFigure = new ColumnsFigure();
	private LineBorder brd = new LineBorder(ColorConstants.black, 1);

	public SqlTableFigure(String name) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setSpacing(5);
		setLayoutManager(layout);
		setBorder(brd);
		setBackgroundColor(classColor);
		setOpaque(true);

		lblName = new NameLabel(name);
		add(lblName);
		add(attributeFigure);
	}

	public void add(IFigure figure, Object constraint, int index) {
		if (figure instanceof ColumnFigure)
			attributeFigure.add(figure);
		else
			super.add(figure, constraint, index);
	}

	public void setName(String name) {
		lblName.setText(name);
	}

	public void setLabelIcon(Image ico) {
		lblName.setIcon(ico);
	}

	public void showSelectedBorder() {
		brd.setWidth(2);
		invalidate();
		repaint();
	}

	public void hideSelectedBorder() {
		brd.setWidth(1);
		invalidate();
		repaint();
	}
}
