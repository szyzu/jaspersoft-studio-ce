/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.wizard.find;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public final class FindWizardDialog extends WizardDialog {
	public FindWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
		setHelpAvailable(false);
	}

	@Override
	public void finishPressed() {
		super.finishPressed();
	}

	@Override
	public void run(boolean fork, boolean cancelable, final IRunnableWithProgress runnable)
			throws InvocationTargetException, InterruptedException {
		ProgressMonitorPart mpart = (ProgressMonitorPart) getProgressMonitor();
		mpart.setVisible(true);
		try {

			ModalContext.run(runnable, fork, getProgressMonitor(), getShell().getDisplay());
		} finally {
			if (mpart.isDisposed())
				return;
			mpart.done();
			mpart.setVisible(false);
		}
	}
}
