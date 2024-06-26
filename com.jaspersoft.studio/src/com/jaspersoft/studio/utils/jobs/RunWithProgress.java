/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.utils.jobs;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class RunWithProgress implements IRunnableContext {

	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException {
		ProgressMonitorDialog pm = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
		pm.run(true, true, runnable);
	}

}
