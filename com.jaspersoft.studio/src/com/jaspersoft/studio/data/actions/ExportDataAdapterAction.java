/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.actions;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterEditorPart;
import com.jaspersoft.studio.data.DataAdapterManager;
import com.jaspersoft.studio.data.DataAdapterUtils;
import com.jaspersoft.studio.data.MDataAdapter;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;

public class ExportDataAdapterAction extends Action {
	public static final String ID = "exportDataAdapteraction"; //$NON-NLS-1$
	private TreeViewer treeViewer;

	public ExportDataAdapterAction(TreeViewer treeViewer) {
		super();
		setId(ID);
		this.treeViewer = treeViewer;
		setText(Messages.ExportDataAdapterAction_exportName);
		setDescription(Messages.ExportDataAdapterAction_exportDescription);
		setToolTipText(Messages.ExportDataAdapterAction_exportTooltip);
		setImageDescriptor(
				AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/etool16/export_wiz.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		setDisabledImageDescriptor(
				AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/dtool16/export_wiz.gif")); // $NON-NLS-1 //$NON-NLS-1$ //$NON-NLS-2$

	}

	@Override
	public boolean isEnabled() {
		Object firstElement = ((TreeSelection) treeViewer.getSelection()).getFirstElement();
		return firstElement != null && (firstElement instanceof MDataAdapter);
	}

	@Override
	public void run() {
		TreeSelection s = (TreeSelection) treeViewer.getSelection();
		TreePath[] p = s.getPaths();

		for (int i = 0; i < p.length; i++) {
			Object obj = p[i].getLastSegment();
			if (obj instanceof MDataAdapter) {
				SaveAsDialog saveAsDialog = new SaveAsDialog(UIUtils.getShell());
				saveAsDialog.setOriginalName(((MDataAdapter) obj).getValue().getName().replace(" ", "") + DataAdapterUtils.DOTTED_FILE_EXTENSION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				saveAsDialog.open();
				IPath path = saveAsDialog.getResult();
				if (path != null)
					saveFile(obj, path);
			}
		}
	}

	private void saveFile(final Object obj, IPath path) {
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (file != null) {
			ProgressMonitorDialog pm = new ProgressMonitorDialog(UIUtils.getShell());
			try {
				pm.run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						JasperReportsConfiguration jConf = JasperReportsConfiguration.getDefaultJRConfig(file);
						try {
							DataAdapterDescriptor m = ((MDataAdapter) obj).getValue();
							String xml = DataAdapterManager.toDataAdapterFile(m, jConf);
							if (file.exists())
								file.setContents(new ByteArrayInputStream(xml.getBytes(FileUtils.UTF8_ENCODING)), true,
										true, monitor);
							else
								file.create(new ByteArrayInputStream(xml.getBytes(FileUtils.UTF8_ENCODING)), true,
										monitor);
							// Force the default editor for the data adapter
							// file
							// so that it can be opened with the same one in the
							// future
							IDE.setDefaultEditor(file, DataAdapterEditorPart.ID);
						} catch (Throwable e) {
							throw new InvocationTargetException(e);
						} finally {
							jConf.dispose();
							monitor.done();
						}
					}

				});
			} catch (InvocationTargetException e) {
				UIUtils.showError(e.getCause());
			} catch (InterruptedException e) {
				UIUtils.showError(e);
			}
		}
	}
}
