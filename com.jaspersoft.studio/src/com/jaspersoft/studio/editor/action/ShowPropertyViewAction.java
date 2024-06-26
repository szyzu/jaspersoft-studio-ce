/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

/*
 * /* The Class BringBackwardAction.
 */
public class ShowPropertyViewAction extends SelectionAction {

	/** The Constant ID. */
	public static final String ID = "show_property_view"; //$NON-NLS-1$

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *          The part for this action
	 */
	public ShowPropertyViewAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	/**
	 * Returns <code>true</code> if the selected objects can be created. Returns <code>false</code> if there are no
	 * objects selected or the selected objects are not {@link EditPart}s.
	 * 
	 * @return if the command should be enabled
	 */
	protected boolean calculateEnabled() {
		return true;
	}

	/**
	 * Performs the create action on the selected objects.
	 */
	public void run() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IViewPart p = page.showView(IPageLayout.ID_PROP_SHEET,null,IWorkbenchPage.VIEW_VISIBLE);
			page.activate(p);
		} catch (PartInitException e) {
			UIUtils.showError(e);
		}
	}

	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setText(Messages.ShowPropertyViewAction_show_properties);
		setToolTipText(Messages.ShowPropertyViewAction_show_properties_tool_tip);
		setId(ShowPropertyViewAction.ID);
		setImageDescriptor(
				JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/resources/eclipse/properties_view.gif")); //$NON-NLS-1$
		setDisabledImageDescriptor(
				JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/resources/eclipse/properties_view.gif")); //$NON-NLS-1$
		setEnabled(false);
	}

}
