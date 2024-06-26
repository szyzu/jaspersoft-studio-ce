/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.gef.parts.text;

import net.sf.jasperreports.engine.base.JRBaseStaticText;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.jaspersoft.studio.editor.gef.parts.FigureEditPart;
import com.jaspersoft.studio.editor.gef.parts.directeditor.TextEditManager;
import com.jaspersoft.studio.property.SetValueCommand;

/*
 * The Class FigureEditPart.
 */
public class StaticTextFigureEditPart extends FigureEditPart {

	public StaticTextFigureEditPart() {
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPolicy() {

			@Override
			protected void showCurrentEditValue(DirectEditRequest request) {
				getFigure().getUpdateManager().performUpdate();
			}

			@Override
			protected Command getDirectEditCommand(DirectEditRequest request) {
				SetValueCommand cmd = new SetValueCommand();
				cmd.setTarget((IPropertySource) getHost().getModel());
				cmd.setPropertyId(JRBaseStaticText.PROPERTY_TEXT);
				CellEditor cellEditor = request.getCellEditor();
				cmd.setPropertyValue((String) cellEditor.getValue());
				return cmd;
			}
		});
	}

	@Override
	public void deactivate() {
		if (manager != null) {
			manager.dispose();
			manager = null;
		}
		super.deactivate();
	}

	protected TextEditManager manager;

	public void performRequest(Request request) {

		if (request.getType() == RequestConstants.REQ_OPEN) {

			if (manager == null) {
				manager = new TextEditManager(this, new LabelCellEditorLocator(getFigure()));
			}
			manager.show();
		}

		/*
		 * if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) { if (manager == null) { manager = new
		 * DirectEditManager(this, TextCellEditor.class, new LabelCellEditorLocator(getFigure()), null) {
		 * 
		 * @Override protected void initCellEditor() { MStaticText model = (MStaticText) getModel();
		 * getCellEditor().setValue(model.getPropertyValue(JRBaseStaticText.PROPERTY_TEXT));
		 * getCellEditor().getControl().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE)); } }; }
		 * manager.show(); }
		 */
	}

}
