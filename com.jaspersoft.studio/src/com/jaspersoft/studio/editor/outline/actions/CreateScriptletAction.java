/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.outline.actions;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.jaspersoft.studio.editor.palette.JDPaletteCreationFactory;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.scriptlet.MScriptlet;
import com.jaspersoft.studio.model.scriptlet.MScriptlets;
/*/*
 * The Class CreateScriptletAction.
 */
public class CreateScriptletAction extends ACreateAndSelectAction {

	/** The Constant ID. */
	public static final String ID = "create_scriptlet"; //$NON-NLS-1$

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *          The part for this action
	 */
	public CreateScriptletAction(IWorkbenchPart part) {
		super(part);
		setCreationFactory(new JDPaletteCreationFactory(MScriptlet.class));
	}

	@Override
	protected boolean calculateEnabled() {
		if(!checkSingleSelectedObject(MScriptlets.class)){
			return false;
		}
		return super.calculateEnabled();
	}
	
	/**
	 * Initializes this action's text and images.
	 */
	@Override
	protected void init() {
		super.init();
		setText(Messages.CreateScriptletAction_create_scriptlet);
		setToolTipText(Messages.CreateScriptletAction_create_scriptlet_tool_tip);
		setId(CreateScriptletAction.ID);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD_DISABLED));
		setEnabled(false);
	}

}
