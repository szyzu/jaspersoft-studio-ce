/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.gef.parts.EditableFigureEditPart;
import com.jaspersoft.studio.editor.gef.parts.SubreportFigureEditPart;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;

/**
 * This class allow to open the report pointed by a subreport with a contextual action
 * 
 * @author Orlandin Marco
 *
 */
public class OpenEditorAction extends SelectionAction {
	public static final String ID = "openSubreportAction"; //$NON-NLS-1$

	public OpenEditorAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	protected void init() {
		super.init();
		setText(Messages.OpenEditorAction_actionName);
		setToolTipText(Messages.OpenEditorAction_actionTooltip);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("/icons/resources/blue-folder-open-document.png")); //$NON-NLS-1$
		setId(ID);
		setEnabled(true);
	}

	@Override
	public void run() {
		for(Object selectedElement : getSelectedObjects()){
			if (selectedElement instanceof SubreportFigureEditPart){
				SubreportFigureEditPart subReport = (SubreportFigureEditPart)selectedElement;
				Object value = ((ANode) subReport.getModel()).getValue();
				IEditorPart editorPart = ((DefaultEditDomain) subReport.getViewer().getEditDomain()).getEditorPart();
				EditableFigureEditPart.openEditor(value, editorPart, (ANode) subReport.getModel());
			}
		}
	}

	@Override
	protected boolean calculateEnabled() {
		for(Object selectedElement : getSelectedObjects()){
			if (selectedElement instanceof SubreportFigureEditPart) return true;
		}
		return false;
	}

}
