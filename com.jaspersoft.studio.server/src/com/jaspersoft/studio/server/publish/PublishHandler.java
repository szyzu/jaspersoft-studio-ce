/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package com.jaspersoft.studio.server.publish;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.jaspersoft.studio.editor.AbstractJRXMLEditor;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.publish.action.JrxmlPublishAction;
import com.jaspersoft.studio.server.publish.wizard.PublishFile2ServerWizard;
import com.jaspersoft.studio.utils.JRXMLUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.ui.util.PersistentLocationWizardDialog;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileExtension;
import net.sf.jasperreports.eclipse.util.Misc;

public class PublishHandler extends AbstractHandler {
	private static IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<IFile> file = new ArrayList<>();
		JasperReportsConfiguration jContext = null;
		boolean disposeJrContext = false;
		ISelection sel = HandlerUtil.getCurrentSelection(event);
		if (sel instanceof StructuredSelection) {
			Object obj = ((StructuredSelection) sel).getFirstElement();
			if (obj instanceof IFile) {
				file.add((IFile) obj);
			} else if (obj instanceof JarPackageFragmentRoot) {
				try {
					ZipFile zf = ((JarPackageFragmentRoot) obj).getJar();
					if (zf != null)
						file.add(getFileFromURI(new URI(zf.getName())));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (obj instanceof CompilationUnit) {
				file.add(getFileFromURI(((CompilationUnit) obj).getPath().toFile().toURI()));
			} else if (obj instanceof IProject || obj instanceof IFolder || obj instanceof IPackageFragment
					|| obj instanceof PackageFragmentRoot || obj instanceof ClassPathContainer) {
				UIUtils.showInformation(
						"Currently this type of operation is not supported, only publishing files is supported");
				return null;
			}
		}
		if (file.isEmpty()) {
			IEditorInput ei = com.jaspersoft.studio.utils.compatibility.HandlerUtil.getActiveEditorInput(event);
			if (ei instanceof IFileEditorInput) {
				IFile f = ((IFileEditorInput) ei).getFile();
				IEditorPart ep = HandlerUtil.getActiveEditor(event);
				if (ep instanceof AbstractJRXMLEditor) {
					try {
						jContext = ((AbstractJRXMLEditor) ep).getJrContext(f);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (ep instanceof MultiPageEditorPart) {
					MultiPageEditorPart mep = (MultiPageEditorPart) ep;
					Object obj = mep.getAdapter(IFile.class);
					if (obj instanceof List)
						file.addAll((List<IFile>) obj);
				}
			}
		}
		if (file.isEmpty())
			UIUtils.showInformation(Messages.PublishHandler_0);
		else {
			IFile f = file.get(0);
			String ext = f.getFileExtension();
			if (ext.equals(FileExtension.JRXML) || ext.equals(FileExtension.JASPER) || Misc.isNullOrEmpty(ext)) {
				if (jContext == null) {
					jContext = JasperReportsConfiguration.getDefaultJRConfig(f);
					disposeJrContext = true;
					try {
						jContext.setJasperDesign(JRXMLUtils.getJasperDesign(jContext, f.getContents(), null));
					} catch (Exception e) {
						e.printStackTrace();
						jContext.dispose();
						jContext = null;
					}
				}
				if (jContext != null) {
					JrxmlPublishAction publishAction = new JrxmlPublishAction(1, null);
					publishAction.setJrConfig(jContext);
					publishAction.run();
					// Check if the context was created internally and must be disposed
					if (disposeJrContext)
						jContext.dispose();
					return null;
				}
			}
			PublishFile2ServerWizard wizard = new PublishFile2ServerWizard(file, 1);
			new PersistentLocationWizardDialog(UIUtils.getShell(), wizard).open();
		}
		return null;
	}

	private IFile getFileFromURI(URI uri) {
		IFile[] res = root.findFilesForLocationURI(uri);
		if (res != null && res.length > 0)
			return res[0];
		return null;
	}
}
