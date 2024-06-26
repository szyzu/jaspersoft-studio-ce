/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.rcp.intro.action;

import java.util.Properties;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

public class OpenFileAction implements IIntroAction {

	public void run(final IIntroSite site, Properties params) {
		String prj = (String) params.get("prj");
		String file = (String) params.get("file");

		IWorkspace ws = ResourcesPlugin.getWorkspace();
		IProject project = ws.getRoot().getProject(prj);

		final IFile f = project.getFile(file);
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				try {
					IEditorPart ep = IDE.openEditor(site.getPage(), f, true);
					ep.setFocus();

				} catch (PartInitException e) {
					UIUtils.showError(e);
				}
			}
		});

	}

}
