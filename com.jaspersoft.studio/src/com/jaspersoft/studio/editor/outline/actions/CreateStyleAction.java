/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.outline.actions;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.jaspersoft.studio.editor.palette.JDPaletteCreationFactory;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.style.MStyle;
import com.jaspersoft.studio.model.style.MStyles;
import com.jaspersoft.studio.model.style.MStylesTemplate;

/*
 * The Class CreateStyleAction.
 */
public class CreateStyleAction extends ACreateAndSelectAction {

	/** The Constant ID. */
	public static final String ID = "create_style"; //$NON-NLS-1$

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *          The part for this action
	 */
	public CreateStyleAction(IWorkbenchPart part) {
		super(part);
		setCreationFactory(new JDPaletteCreationFactory(MStyle.class));
	}
	
	@Override
	protected boolean calculateEnabled() {
		if(!checkSingleSelectedObject(MStyles.class) && !checkSingleSelectedObject(MStylesTemplate.class)){
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
		setText(Messages.CreateStyleAction_create_style);
		setToolTipText(Messages.CreateStyleAction_create_style_tool_type);
		setId(CreateStyleAction.ID);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD_DISABLED));
		setEnabled(false);
	}

}
