/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.wizard;

import java.lang.reflect.InvocationTargetException;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.jaspersoft.studio.server.WSClientHelper;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.model.server.MServerProfile;
import com.jaspersoft.studio.server.wizard.pages.ServerProfilePage;

public class ServerProfileWizard extends Wizard {
	private ServerProfilePage page0;

	public ServerProfileWizard(MServerProfile sprofile) {
		super();
		setWindowTitle(Messages.ServerProfileWizard_0);
		this.serverProfile = sprofile;
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		page0 = new ServerProfilePage(serverProfile);
		addPage(page0);

	}

	private MServerProfile serverProfile;

	public MServerProfile getServerProfile() {
		return serverProfile;
	}

	@Override
	public boolean performFinish() {
		page0.performFinishInvoked();
		return true;
	}

	public void bindTestButton(ServerProfileWizardDialog c) {
		c.addTestListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleConnect(true);
			}
		});

	}

	private void handleConnect(final boolean onlycheck) {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					connect(onlycheck, monitor);
				}

			});
		} catch (InvocationTargetException e) {
			UIUtils.showError(e.getTargetException());
		} catch (InterruptedException e) {
			UIUtils.showError(e);
		}
	}

	private void connectionOK() {
		UIUtils.getDisplay().asyncExec(() -> {
			MessageDialog.openInformation(getShell(), Messages.ServerProfileWizard_1, Messages.ServerProfileWizard_2);
			page0.connectionOK();
		});
	}

	private IStatus connect(final boolean onlycheck, IProgressMonitor monitor) throws InvocationTargetException {
		try {
			page0.connect();
			monitor.beginTask(Messages.ServerProfileWizard_3, IProgressMonitor.UNKNOWN);
			if (onlycheck) {
				if (WSClientHelper.checkConnection(serverProfile, monitor))
					connectionOK();
			} else
				WSClientHelper.connectGetData(serverProfile, monitor);
			UIUtils.getDisplay().syncExec(() -> page0.showServerInfo());
		} catch (Throwable e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}
