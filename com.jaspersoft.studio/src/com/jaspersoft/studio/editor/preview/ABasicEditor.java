/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.DeltaVisitor;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.engine.JasperReportsContext;

public abstract class ABasicEditor extends EditorPart {

	protected boolean listenResource;

	protected JasperReportsConfiguration jrContext;

	private IPartListener partListener;

	private ResourceTracker resourceListener;

	public ABasicEditor(boolean listenResource) {
		this.listenResource = listenResource;
		if (listenResource) {
			partListener = new ResourcePartListener();
			resourceListener = new ResourceTracker();
		}
	}

	class ResourceTracker implements IResourceChangeListener {
		public void resourceChanged(final IResourceChangeEvent event) {
			switch (event.getType()) {
			case IResourceChangeEvent.PRE_DELETE:
				break;
			case IResourceChangeEvent.POST_CHANGE:
				try {
					DeltaVisitor visitor = new DeltaVisitor(ABasicEditor.this);
					event.getDelta().accept(visitor);
				} catch (CoreException e) {
					UIUtils.showError(e);
				}
				break;
			case IResourceChangeEvent.PRE_BUILD:
			case IResourceChangeEvent.POST_BUILD:
				break;
			}
		}
	}

	class ResourcePartListener implements IPartListener {
		// If an open, unsaved file was deleted, query the user to either do a
		// "Save As"
		// or close the editor.
		public void partActivated(IWorkbenchPart part) {
			if (part != ABasicEditor.this)
				return;
			if (!((IFileEditorInput) getEditorInput()).getFile().exists())
				getSite().getPage().closeEditor(ABasicEditor.this, false);
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			if (!file.exists()) {
				try {
					file.delete(true, new NullProgressMonitor());
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
		}
	};

	@Override
	public void dispose() {
		if (partListener != null)
			getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
		partListener = null;
		if (resourceListener != null)
			((IFileEditorInput) getEditorInput()).getFile().getWorkspace()
					.removeResourceChangeListener(resourceListener);
		disposeContext();
		super.dispose();
	}

	@Override
	protected void setInput(IEditorInput input) {
		// The workspace never changes for an editor. So, removing and re-adding
		// the
		// resourceListener is not necessary. But it is being done here for the
		// sake
		// of proper implementation. Plus, the resourceListener needs to be
		// added
		// to the workspace the first time around.
		if (resourceListener != null && getEditorInput() != null) {
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.getWorkspace().removeResourceChangeListener(resourceListener);
		}

		super.setInput(input);

		if (resourceListener != null && getEditorInput() != null) {
			if (getEditorInput() instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) getEditorInput()).getFile();
				file.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.PRE_CLOSE
						| IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
				setPartName(file.getName());
			} else if (getEditorInput() instanceof FileStoreEditorInput) {
			}
		}
	}

	@Override
	protected void setSite(IWorkbenchPartSite site) {
		super.setSite(site);
		if (partListener != null)
			getSite().getWorkbenchWindow().getPartService().addPartListener(partListener);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		IFile file = null;
		if (input instanceof FileStoreEditorInput) {
			input = FileUtils.checkAndConvertEditorInput(input, new NullProgressMonitor());
			init(site, input);
			return;
		} else if (input instanceof IFileEditorInput) {
			file = ((IFileEditorInput) input).getFile();
		}

		try {
			initJRContext(file);
			JaspersoftStudioPlugin.getExtensionManager().onInitContext(jrContext);
			setSite(site);
			setPartName(input.getName());
			setInput(input);
		} catch (Exception e) {
			throw new PartInitException(e.getMessage(), e);
		}
	}

	public JasperReportsConfiguration getJrContext() {
		return jrContext;
	}

	/**
	 * If the jrContext was not set yet it create a new one basing on the passed
	 * file. The jrContext created this way will be disposed at the end. It it
	 * safe to dispose a JRConfig created this way, since it is a not shared
	 * instance
	 */
	protected void initJRContext(IFile file) throws CoreException, JavaModelException {
		if (jrContext == null)
			jrContext = JasperReportsConfiguration.getDefaultJRConfig(file);
	}

	/**
	 * Method called to dispose the current context, can be overridden to
	 * provide a different behavior
	 */
	protected void disposeContext() {
		if (jrContext != null)
			jrContext.dispose();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == JasperReportsContext.class)
			return jrContext;
		return super.getAdapter(adapter);
	}

	protected boolean isDirty = false;

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSaveAs() {
		isDirty = false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		isDirty = false;
	}

	protected void handleValueChanged() {
		getSite().getWorkbenchWindow().getShell().getDisplay().asyncExec(() -> {
			isDirty = true;
			firePropertyChange(ISaveablePart.PROP_DIRTY);
		});
	}
}
