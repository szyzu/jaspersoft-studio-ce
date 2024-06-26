/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;

import com.jaspersoft.studio.model.INode;

public interface IGraphicalEditor {
	public ActionRegistry getActionRegistry();

	public DefaultEditDomain getEditDomain();

	public INode getModel();

	public GraphicalViewer getGraphicalViewer();

	public SelectionSynchronizer getSelectionSynchronizer();

	public FigureCanvas getEditor();
}
