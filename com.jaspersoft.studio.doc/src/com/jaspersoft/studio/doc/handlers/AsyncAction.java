/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.doc.handlers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;

/**
 * Abstract class to execute an action asynchronously. This is necessary to avoid to block 
 * the flow of the cheatsheet when a dialog is disposed. Infact when a dialog is opened using an action the 
 * body of the action will not terminate until the dialog is closed. But the cheatsheet must advance even 
 * when the dialog is opened (since it can be a step of a wizard), so it must be opened in a separate thread
 * 
 * @author Orlandin Marco
 *
 */
public abstract class AsyncAction extends Action {

	protected WizardDialog dialogToOpen;
	
	/**
	 * body of the action to execute
	 */
	protected void doAction(){
		if (dialogToOpen != null)
			dialogToOpen.open();
	}
	
	protected abstract void loadDialog();
	
	private Display getDisplay(){
		if (dialogToOpen != null) return dialogToOpen.getShell().getDisplay();
		else return Display.getCurrent();
	}
	
	/**
	 * Run the body of the action
	 */
	@Override
	public void run() {
		loadDialog();
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (dialogToOpen != null) {
					dialogToOpen.getShell().setSize(
				                Math.max(500, dialogToOpen.getShell().getSize().x),
				                500);
					  PlatformUI.getWorkbench().getHelpSystem().setHelp(dialogToOpen.getShell(),
								IWorkbenchHelpContextIds.NEW_WIZARD);
				}
				doAction();
		    }
		});
	}
	
}
