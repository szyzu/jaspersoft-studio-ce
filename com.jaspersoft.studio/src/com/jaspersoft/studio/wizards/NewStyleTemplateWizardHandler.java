/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.wizards;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.NewWizardAction;
import org.eclipse.ui.wizards.IWizardRegistry;

import com.jaspersoft.studio.editor.style.wizard.StyleTemplateNewWizard;

/**
 * Custom wizard handler for the command that launches the wizard that creates 
 * a new style template.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class NewStyleTemplateWizardHandler extends JSSWizardHandler {

	@Override
	protected IAction createWizardChooserDialogAction(IWorkbenchWindow window) {
		return new NewWizardAction(window);
	}

	@Override
	protected String getWizardIdParameterId() {
		return StyleTemplateNewWizard.WIZARD_ID;
	}

	@Override
	protected IWizardRegistry getWizardRegistry() {
		return PlatformUI.getWorkbench().getNewWizardRegistry();

	}

}
