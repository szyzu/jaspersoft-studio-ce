/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.action.resource;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.server.ResourceFactory;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.model.AFileResource;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.model.MReference;
import com.jaspersoft.studio.server.model.MReportUnit;
import com.jaspersoft.studio.server.model.server.MServerProfile;
import com.jaspersoft.studio.server.utils.ReferenceResolver;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.viewer.BrowserUtils;

public class OpenInBrowserAction extends Action {
	private static final String ID = "OPENINBROWSER"; //$NON-NLS-1$
	private TreeViewer treeViewer;

	public OpenInBrowserAction(TreeViewer treeViewer) {
		super();
		setId(ID);
		setText(Messages.OpenInBrowserAction_1);
		setToolTipText(Messages.OpenInBrowserAction_2);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/jrs.png")); //$NON-NLS-1$
		this.treeViewer = treeViewer;
	}

	@Override
	public boolean isEnabled() {
		TreeSelection treeSelection = (TreeSelection) treeViewer.getSelection();
		Object firstElement = treeSelection.getFirstElement();
		return treeSelection.size() == 1 && isResourceOpenable(firstElement);
	}
	
	private boolean isResourceOpenable(Object selectedEl) {
		if(selectedEl instanceof MReportUnit) {
			//dedicated check: MReportUnit is actually a subclass of AFileResource
			return true;
		}
		else if(selectedEl instanceof MServerProfile) {
			//the server entry in the repository explorer
			return true;
		}
		else if(selectedEl instanceof AMResource && !(selectedEl instanceof AFileResource)) {
			//all generic resources not "file" like (see #JS-60178)
			return true;
		}
		// fallback
		return false;
	}

	@Override
	public void run() {
		try {
			TreeSelection s = (TreeSelection) treeViewer.getSelection();
			Object obj = s.getFirstElement();
			if (obj instanceof MServerProfile) {
				MServerProfile msp = (MServerProfile) obj;
				BrowserUtils.openExternalBrowser(msp.getValue().getUrl());
			} else if (obj instanceof AMResource) {
				final AMResource mr = (AMResource) obj;
				MServerProfile msp = (MServerProfile) mr.getRoot();

				final String uri = msp.getValue().getUrl();

				try {
					if (mr instanceof MReference) {
						Job job = new Job(Messages.FindReportUnit_jobname) {
							@Override
							protected IStatus run(IProgressMonitor monitor) {
								monitor.beginTask("Resolving reference", IProgressMonitor.UNKNOWN);
								try {
									ResourceDescriptor rd = ReferenceResolver.resolveReference((MReference) mr,
											monitor);
									AMResource mres = ResourceFactory.getResource(null, rd, -1);
									if (mres != null)
										openInBrowser(mres, uri);
									return Status.OK_STATUS;
								} catch (Exception e) {
									UIUtils.showError(e);
								}
								return Status.CANCEL_STATUS;
							}
						};
						job.setPriority(Job.LONG);
						job.schedule();
					} else
						openInBrowser(mr, uri);
				} catch (UnsupportedEncodingException e) {
					UIUtils.showError(e);
				}
			}
		} catch (MalformedURLException | URISyntaxException e) {
			UIUtils.showError(e);
		}
	}

	private void openInBrowser(AMResource mr, String uri) throws UnsupportedEncodingException {
		String jrsUrl = mr.getJRSUrl();
		if (jrsUrl == null) {
			UIUtils.showInformation("This resource url can't be opened directly from here.");
			return;
		}
		uri += jrsUrl;
		BrowserUtils.openExternalBrowser(uri);
	}

}
