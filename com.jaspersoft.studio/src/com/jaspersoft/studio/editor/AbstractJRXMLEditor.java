/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.IStorageDocumentProvider;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.xml.sax.InputSource;

import com.jaspersoft.studio.ExternalStylesManager;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.compatibility.JRXmlWriterHelper;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.editor.context.EditorContextUtil;
import com.jaspersoft.studio.editor.defaults.DefaultManager;
import com.jaspersoft.studio.editor.outline.page.EmptyOutlinePage;
import com.jaspersoft.studio.editor.outline.page.MultiOutlineView;
import com.jaspersoft.studio.editor.preview.PreviewContainer;
import com.jaspersoft.studio.editor.xml.XMLEditor;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.preferences.DesignerPreferencePage;
import com.jaspersoft.studio.property.dataset.dialog.DataQueryAdapters;
import com.jaspersoft.studio.utils.Console;
import com.jaspersoft.studio.utils.JRXMLUtils;
import com.jaspersoft.studio.utils.SyncDatasetRunParameters;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.JasperReportsPlugin;
import net.sf.jasperreports.eclipse.builder.JasperReportsBuilder;
import net.sf.jasperreports.eclipse.builder.Markers;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlDigesterFactory;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * This abstract class contains the basic that should be extended by clients in
 * order to create a JRXML-like editor.
 * 
 */
public abstract class AbstractJRXMLEditor extends MultiPageEditorPart
		implements IResourceChangeListener, IMultiEditor, IGotoMarker {

	/** The constant for the page index of Design Editor */
	public static final int PAGE_DESIGNER = 0;

	/** The constant for the page index of Source Editor */
	public static final int PAGE_SOURCEEDITOR = 1;

	/** The constant for the page index of Preview Editor */
	public static final int PAGE_PREVIEW = 2;

	/**
	 * Listener to execute the zoom-in or zoom-out operation when requested. It
	 * is static becuase it is placed on the display, so one get all the events
	 */
	private static Listener mouseWheelListener = new JRXMLEditorZoomListener();

	/** Model associated to the JRXML file */
	protected INode model = null;

	/** The configuration for the JRXML file */
	protected JasperReportsConfiguration jrContext;

	/** The outline associated to the editor */
	protected MultiOutlineView outlinePage;

	/** Flag for the XML status */
	protected boolean xmlFresh = true;

	protected boolean toXML = false;

	protected boolean isRefreshing = false;

	protected boolean closing = false;

	/** The current active page index */
	// FIXME - Is it really necessary?? we should use getActivePage()
	protected int activePage = 0;

	/** Temporary reference to the current selection */
	protected ISelection tmpselection;

	/** The JasperReports version used to write this JRXML */
	protected String version = JRXmlWriterHelper.LAST_VERSION;

	/** Xml editor used in page 1. */
	protected XMLEditor xmlEditor;

	/** Preview editor used in page 2. */
	protected PreviewEditor previewEditor;

	protected boolean partActivated = true;

	/**
	 * Listener called when the source editor is modified
	 */
	protected IDocumentListener sourceEditorModified = new IDocumentListener() {

		public void documentChanged(DocumentEvent event) {
			if (!isRefreshing) {
				xmlFresh = false;
				firePropertyChange(ISaveablePart.PROP_DIRTY);
			}
		}

		public void documentAboutToBeChanged(DocumentEvent event) {
			// nothing to do
		}
	};

	protected class StateListener implements IElementStateListener {

		@Override
		public void elementDirtyStateChanged(Object element, boolean isDirty) {
			// nothing to do
		}

		@Override
		public void elementContentAboutToBeReplaced(Object element) {
			// nothing to do
		}

		@Override
		public void elementContentReplaced(Object element) {
			// nothing to do
		}

		@Override
		public void elementDeleted(Object element) {
			IFile resource = getCurrentFile();
			String path = resource.getRawLocation().toOSString();
			DefaultManager.INSTANCE.removeDefaultFile(path);

			Display.getDefault().asyncExec(() -> getSite().getPage().closeEditor(AbstractJRXMLEditor.this, false));
		}

		@Override
		public void elementMoved(Object originalElement, Object movedElement) {
			// nothing to do
		}
	}

	public AbstractJRXMLEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
		ExternalStylesManager.initListeners();
		JasperReportsPlugin.initializeKeyListener();
	}

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (closing)
			return;
		// FIXME: THIS IS NOT THE RIGHT PLACE TO LOAD MODEL, WE SHOULD LOAD FROM
		// TEXT EDITOR TO AVOID 2 TIME READING THE FILE
		NullProgressMonitor monitor = new NullProgressMonitor();
		editorInput = FileUtils.checkAndConvertEditorInput(editorInput, monitor);
		super.init(site, editorInput);
		setPartName(editorInput.getName());
		InputStream in = null;
		IFile file = null;
		try {
			if (editorInput instanceof IFileEditorInput) {
				file = ((IFileEditorInput) editorInput).getFile();
				if (!file.getProject().isOpen()) {
					file.getProject().open(monitor);
				}
				file.refreshLocal(0, monitor);
				if (!file.exists()) {
					closeEditorOnErrors();
					return;
				}

				in = file.getContents();
			} else if (editorInput instanceof IStorageEditorInput) {
				in = ((IStorageEditorInput) editorInput).getStorage().getContents();
			} else
				throw new PartInitException("Invalid Input: Must be IFileEditorInput or FileStoreEditorInput"); //$NON-NLS-1$

			getJrContext(file);
			if (!isRefreshing) {
				final InputStream inp = in;
				final IFile ifile = file;
				doInitModel(monitor, getEditorInput(), inp, ifile);
			}
		} catch (Exception e) {
			setModel(null);
			handleJRException(editorInput, e, false);
		}
	}

	public void changeContext(String c, boolean reset) {
		jrContext.changeContext(c, reset);
		if (previewEditor != null) {
			previewEditor.setMode(jrContext.getEditorContext().getDefaultRunMode());
			if (activePage == PAGE_PREVIEW)
				previewEditor.runReport();
		}
	}

	/**
	 * Returns the {@link JasperReportsContext} associated to the specified
	 * report file.
	 * 
	 * @param file the JRXML file reference
	 * @return the JasperReports context of the report file
	 */
	public JasperReportsConfiguration getJrContext(IFile file) {
		if (jrContext == null) {
			jrContext = JasperReportsConfiguration.getDefaultJRConfig(file);
			jrContext.put(AMultiEditor.THEEDITOR, this);
			jrContext.setJRParameters(new HashMap<>());
		}
		return jrContext;
	}

	public JasperReportsConfiguration getJrContext() {
		return jrContext;
	}

	protected abstract INode createEditorModel();

	/**
	 * 
	 * 
	 * @param monitor
	 * @param editorInput
	 * @param in
	 * @param file
	 */
	protected void doInitModel(IProgressMonitor monitor, IEditorInput editorInput, InputStream in, IFile file) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(jrContext.getClassLoader());
			String charset = FileUtils.UTF8_ENCODING;
			if (file != null) {
				charset = file.getCharset(true);
			}
			in = JRXMLUtils.getXML(jrContext, editorInput, charset, in, version);
			JasperDesign jd = new JRXmlLoader(jrContext, JRXmlDigesterFactory.createDigester(jrContext))
					.loadXML(new InputSource(in));
			JaspersoftStudioPlugin.getExtensionManager().onLoad(jd, this);
			jrContext.setJasperDesign(jd);

			UIUtils.getDisplay().syncExec(() -> {
				setModel(createEditorModel());
				MReport report = getMReport();
				if (report != null) {
					SyncDatasetRunParameters.sync(report);
				}
			});
		} catch (CoreException e) {
			if (e.getMessage().startsWith("File not found")) { //$NON-NLS-1$
				closeEditorOnErrors();
			} else {
				setModel(null);
				handleJRException(editorInput, e, false);
			}
		} catch (Exception e) {
			setModel(null);
			handleJRException(editorInput, e, false);
		} finally {
			FileUtils.closeStream(in);
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.
	 * eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (isRefreshing)
			return;
		Job job = new Job("Post file change") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				switch (event.getType()) {
				case IResourceChangeEvent.PRE_CLOSE:
					Display.getDefault().asyncExec(() -> {
						IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
						for (int i = 0; i < pages.length; i++) {
							if (((FileEditorInput) xmlEditor.getEditorInput()).getFile().getProject()
									.equals(event.getResource())) {
								IEditorPart editorPart = pages[i].findEditor(xmlEditor.getEditorInput());
								pages[i].closeEditor(editorPart, true);
							}
						}
					});
					break;
				case IResourceChangeEvent.PRE_DELETE:
					break;
				case IResourceChangeEvent.POST_CHANGE:
					try {
						DeltaVisitor visitor = new DeltaVisitor(AbstractJRXMLEditor.this);
						event.getDelta().accept(visitor);
						if (jrContext != null && getEditorInput() != null) {
							IFile old = jrContext.getAssociatedReportFile();
							IFile newf = ((IFileEditorInput) getEditorInput()).getFile();
							if (!old.equals(newf)) {
								jrContext.init(newf);
								JaspersoftStudioPlugin.getExtensionManager().onRename(old, newf, jrContext,
										new NullProgressMonitor());
							}
						}
					} catch (CoreException e) {
						UIUtils.showError(e);
					}
					break;
				case IResourceChangeEvent.PRE_BUILD:
				case IResourceChangeEvent.POST_BUILD:
					break;
				}

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.LONG);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	@Override
	protected void createPages() {
		CTabFolder c = (CTabFolder) getContainer();
		c.setTopRight(EditorContextUtil.createSwitch(c, this), SWT.RIGHT);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(getContainer(), getEditorHelpID());
		if (jrContext != null)
			try {
				createDesignEditorPage();
				createSourceEditorPage();
				Display.getDefault().asyncExec(() -> {
					try {
						createPreviewEditorPage();
						bindActionToKeys();
					} catch (PartInitException e) {
						UIUtils.showError(new Exception(Messages.common_error_creating_nested_visual_editor));
					}
				});
			} catch (PartInitException e) {
				UIUtils.showError(new Exception(Messages.common_error_creating_nested_visual_editor));
			} catch (Throwable e) {
				closeEditorOnErrors();
				JaspersoftStudioPlugin.getInstance().logError(e);
			}
		IPartListener2 pl = new IPartListener2() {

			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				if (partRef.getPart(false) == getSite().getPart()) {
					editorActivated();
				}

			}

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				// nothing to do
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				// nothing to do
			}

			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
				if (partRef.getPart(false) == getSite().getPart()) {
					editorDeactivated();
				}
			}

			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
				// nothing to do
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
				if (partRef.getPart(false) == getSite().getPart()) {
					editorHidden();
				}
			}

			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
				if (partRef.getPart(false) == getSite().getPart()) {
					editorVisible();
				}
			}

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
				// nothing to do
			}

		};
		getSite().getPage().addPartListener(pl);
	}

	public boolean isPartActivated() {
		return partActivated;
	}

	public void setPartActivated(boolean partActivated) {
		this.partActivated = partActivated;
	}

	protected void editorActivated() {
		partActivated = true;
	}

	protected void editorDeactivated() {
		partActivated = false;
	}

	protected void editorVisible() {

	}

	protected void editorHidden() {

	}

	/**
	 * Closes the editor if there are some errors.
	 */
	protected void closeEditorOnErrors() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			final IWorkbenchPage apage = activeWorkbenchWindow.getActivePage();
			if (apage != null)
				Display.getDefault().asyncExec(() -> {
					closing = true;
					apage.closeEditor(AbstractJRXMLEditor.this, false);
				});
		}
	}

	/**
	 * Handles the {@link JRException} when it happens. Markers can be used in
	 * order to give a better user experience.
	 * 
	 * @param editorInput the editor input
	 * @param exception the exception to be handled
	 * @param mute flag to determine if the exception should be presented in a
	 * dialog
	 */
	public void handleJRException(IEditorInput editorInput, final Exception exception, boolean mute) {
		if (!mute)
			UIUtils.showError(exception);
		try {
			isRefreshing = true;
			if (editorInput instanceof IFileEditorInput) {
				IResource resource = ((IFileEditorInput) editorInput).getFile();
				if (!resource.exists())
					return;

				// Delete old markers and set fresh ones
				Markers.deleteMarkers(resource);
				final IMarker marker = Markers.addMarker(resource, exception);
				marker.setAttribute(IMarker.TRANSIENT, true);

				// Try to locate the error position for a possible fix action
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						gotoMarker(marker);
						toXML = true;
						setActivePage(PAGE_SOURCEEDITOR); // FIXME -> This could
															// be removed???
															// we already force
															// source editor in
															// gotoMarker()
						isRefreshing = false;
					}
				});
			}
		} catch (CoreException e1) {
			JaspersoftStudioPlugin.getInstance().logError(e1);
		}
	}

	@Override
	public void gotoMarker(IMarker marker) {
		setActivePage(PAGE_SOURCEEDITOR);
		IDE.gotoMarker(xmlEditor, marker);
	}

	/**
	 * Creates the Source Editor page.
	 * 
	 * @throws PartInitException
	 * 
	 * @see #PAGE_SOURCEEDITOR
	 */
	protected void createSourceEditorPage() throws PartInitException {
		xmlEditor = new XMLEditor(jrContext);
		int index = addPage(xmlEditor, getEditorInput());
		setPageText(index, Messages.common_source);
		IDocument doc = xmlEditor.getDocumentProvider().getDocument(xmlEditor.getEditorInput());
		doc.addDocumentListener(sourceEditorModified);
	}

	/**
	 * Creates the Preview Editor page.
	 * 
	 * @throws PartInitException
	 * 
	 * @see #PAGE_PREVIEW
	 */
	protected void createPreviewEditorPage() throws PartInitException {
		previewEditor = new PreviewEditor(false, jrContext);

		int index = addPage(previewEditor, getEditorInput());
		setPageText(index, Messages.JrxmlEditor_preview);

		xmlEditor.getDocumentProvider().addElementStateListener(new StateListener());

		/*
		 * JSS has the function to disable the automatic run when entering in
		 * the Preview editor with the shift key pressed. Typically this used
		 * the method JasperReportsPlugin.isPressed(SWT.SHIFT) to know if shift
		 * was pressed. This however can't work anymore since the tab changing
		 * made the app loose the focus and this cancel the keyboard keys press
		 * map. To avoid to loose this every time a tab is changed it is check
		 * if the shift key was pressed and the method on the preview editor to
		 * enable or disable the run is called
		 */
		((CTabFolder) getContainer()).addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				previewEditor.setRunWhenInitilizing((e.stateMask & SWT.SHIFT) == 0);
			}

		});
	}

	/**
	 * Bind some key combination to specific, it remove eventually the old ones
	 * to assure to have only on filter
	 * 
	 * FIXME: Verify the usage of the removeFilter and addFilter!
	 */
	private void bindActionToKeys() {
		getContainer().getDisplay().removeFilter(org.eclipse.swt.SWT.MouseWheel, mouseWheelListener);
		getContainer().getDisplay().removeFilter(org.eclipse.swt.SWT.KeyDown, mouseWheelListener);
		getContainer().getDisplay().addFilter(org.eclipse.swt.SWT.MouseWheel, mouseWheelListener);
		getContainer().getDisplay().addFilter(org.eclipse.swt.SWT.KeyDown, mouseWheelListener);
	}

	/**
	 * @return the ID for the editor contextual help
	 */
	protected abstract String getEditorHelpID();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(final IProgressMonitor monitor) {
		try {
			isRefreshing = true;

			final IFile resource = getCurrentFile();
			if (resource == null) {
				MessageDialog.openError(UIUtils.getShell(), Messages.AbstractJRXMLEditor_1,
						Messages.AbstractJRXMLEditor_2 + Messages.AbstractJRXMLEditor_3);
				return;
			}
			try {
				if (!resource.exists()) {
					resource.create(new ByteArrayInputStream("".getBytes(FileUtils.UTF8_ENCODING)), true, monitor); //$NON-NLS-1$
				}

				resource.setCharset(FileUtils.UTF8_ENCODING, monitor);
				((IStorageDocumentProvider) xmlEditor.getDocumentProvider()).setEncoding(getEditorInput(),
						FileUtils.UTF8_ENCODING);
			} catch (CoreException e) {
				UIUtils.showError(e);
			} catch (UnsupportedEncodingException e) {
				UIUtils.showError(e);
			}
			version = JRXmlWriterHelper.getVersion(resource, jrContext, true);
			if ((!xmlEditor.isDirty() && getDesignEditor().isDirty()) || getActiveEditor() != xmlEditor) {
				model2xml(version);
			} else {
				IDocumentProvider dp = xmlEditor.getDocumentProvider();
				IDocument doc = dp.getDocument(xmlEditor.getEditorInput());
				try {
					xml2model();
				} catch (Throwable e) {
					Markers.addMarker(resource, e);
					doSaveEditors(monitor);// on eclipse 4.2.1 on first first
											// save, for some reasons save is
											// not working .., so
											// we'll do it manually
					resource.setContents(new ByteArrayInputStream(doc.get().getBytes(FileUtils.UTF8_ENCODING)),
							IFile.KEEP_HISTORY | IFile.FORCE, monitor);
					finishSave(resource);
					return;
				}
			}
			if (Misc.isNullOrEmpty(JRXMLUtils.getFileExtension(getEditorInput()))) { // $NON-NLS-1$
				// save binary
				try {
					new JasperReportsBuilder().compileJRXML(resource, monitor, getJrContext());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			UIUtils.getDisplay().syncExec(() -> {
				if (isDirty())
					JaspersoftStudioPlugin.getExtensionManager().onSave(jrContext, monitor);
				try {
					String xml = model2xml(version);
					doSaveEditors(monitor);
					// Delete the markers on save, they will be regenerated
					// by the build process (either automatic or manual)
					Markers.deleteMarkers(resource);
					// on eclipse 4.2.1 on first first save, for some
					// reasons save is not working .., so we'll do it
					// manually
					resource.setContents(new ByteArrayInputStream(xml.getBytes(FileUtils.UTF8_ENCODING)),
							IFile.KEEP_HISTORY | IFile.FORCE, monitor);
					finishSave(resource);
				} catch (Throwable e) {
					try {
						Markers.addMarker(resource, e);
					} catch (CoreException e1) {
						e1.printStackTrace();
					}
					UIUtils.showError(e);
				}
			});
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	protected void doSaveEditors(final IProgressMonitor monitor) {
		xmlEditor.doSave(monitor);
		getDesignEditor().doSave(monitor);
		previewEditor.doSave(monitor);

		xmlEditor.isDirty();
		getDesignEditor().isDirty();
		previewEditor.isDirty();

		xmlFresh = true;
	}

	protected void finishSave(IFile resource) {
		String resourceAbsolutePath = resource.getRawLocation().toOSString();
		if (DefaultManager.INSTANCE.isCurrentDefault(resourceAbsolutePath)) {
			DefaultManager.INSTANCE.reloadCurrentDefault();
		}
		UIUtils.getDisplay().asyncExec(() -> {
			isRefreshing = false;
			firePropertyChange(ISaveablePart.PROP_DIRTY);
		});
	}

	@Override
	public void doSaveAs() {
		SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
		IFile oldFile = ((FileEditorInput) getEditorInput()).getFile();
		saveAsDialog.setOriginalFile(oldFile);
		if (saveAsDialog.open() == Dialog.OK) {
			IPath path = saveAsDialog.getResult();
			if (path != null) {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (file != null) {
					IProgressMonitor monitor = getActiveEditor().getEditorSite().getActionBars().getStatusLineManager()
							.getProgressMonitor();
					JaspersoftStudioPlugin.getExtensionManager().onSaveAs(oldFile, file, jrContext, monitor);
					try {
						if (getActiveEditor() == xmlEditor) {
							IDocumentProvider dp = xmlEditor.getDocumentProvider();
							IDocument doc = dp.getDocument(xmlEditor.getEditorInput());
							ByteArrayInputStream in = new ByteArrayInputStream(
									doc.get().getBytes(FileUtils.UTF8_ENCODING));
							if (!file.exists())
								file.create(in, true, monitor);
							else
								file.setContents(in, true, true, monitor);
						} else if (!file.exists()) {
							file.create(new ByteArrayInputStream("".getBytes(FileUtils.UTF8_ENCODING)), true, monitor); //$NON-NLS-1$
						}

						IFileEditorInput modelFile = new FileEditorInput(file);
						setInputWithNotify(modelFile);
						xmlEditor.setInput(modelFile);
						setPartName(file.getName());
						jrContext.init(file);

						doSave(monitor);
					} catch (CoreException e) {
						UIUtils.showError(e);
					} catch (UnsupportedEncodingException e) {
						UIUtils.showError(e);
					}
				}
			}
		}
	}

	@Override
	protected void handlePropertyChange(final int propertyId) {
		if (!isRefreshing) {
//			JaspersoftStudioPlugin.getInstance().logError(new Exception());
			if (propertyId == ISaveablePart.PROP_DIRTY && previewEditor != null)
				setPreviewDirty(true);
			// Can indirectly refresh the widgets so it must be executed inside
			// the
			// graphic thread, but to avoid concurrency problems it is a sync
			// exec
			UIUtils.getDisplay().syncExec(() -> AbstractJRXMLEditor.super.handlePropertyChange(propertyId));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	protected IContextActivation context;

	@Override
	protected void pageChange(final int newPageIndex) {
		if (newPageIndex == PAGE_DESIGNER || newPageIndex == PAGE_SOURCEEDITOR || newPageIndex == PAGE_PREVIEW) {
			if (activePage == PAGE_DESIGNER) {
				if (outlinePage != null) {
					tmpselection = outlinePage.getSite().getSelectionProvider().getSelection();
				} else {
					tmpselection = getDesignerPageSelection();
				}
			}
			String ver = JRXmlWriterHelper.getVersion(getCurrentFile(), jrContext, false);
			IContextService service = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
			switch (newPageIndex) {
			case PAGE_DESIGNER:
				if (activePage == PAGE_SOURCEEDITOR && !xmlFresh) {
					try {
						xml2model();
					} catch (Exception e) {
						toXML = true;
						handleJRException(getEditorInput(), e, false);
					}
					updateVisualView();
				} else if (activePage == PAGE_DESIGNER) {
					updateVisualView();
				} else {
					// stop running reports
					previewEditor.getReportControler().stop();
				}
				Display.getDefault().syncExec(() -> {
					if (outlinePage != null) {
						outlinePage.getSite().getSelectionProvider().setSelection(tmpselection);
					} else {
						setDesignerPageSelection(tmpselection);
					}
				});
				if (context == null)
					context = service.activateContext("com.jaspersoft.studio.context"); //$NON-NLS-1$
				break;
			case PAGE_SOURCEEDITOR:
				if (toXML)
					toXML = false;
				else {
					// This flag avoid to have the xml editor dirty when
					// switching
					// because of the timestamp
					isRefreshing = true;
					model2xml(ver);
					isRefreshing = false;
				}
				if (context != null)
					// it could be activated somewhere else, we don't know, so I
					// add this dirty :(
					for (int i = 0; i < 10; i++)
						service.deactivateContext(context);
				break;
			case PAGE_PREVIEW:
				if (activePage == PAGE_SOURCEEDITOR && !xmlFresh)
					try {
						xml2model();
						setPreviewDirty(true);
					} catch (Exception e) {
						handleJRException(getEditorInput(), e, false);
					}
				else if (isDesignerDirty()) {
					isRefreshing = true;
					model2xml(ver);
					isRefreshing = false;
				}
				Job job = new Job(Messages.AbstractJRXMLEditor_8) {
					@Override
					protected IStatus run(final IProgressMonitor monitor) {
						monitor.beginTask(Messages.AbstractJRXMLEditor_9, IProgressMonitor.UNKNOWN);
						if (jrContext.getEditorContext().saveOnPreview() || jrContext
								.getPropertyBoolean(DesignerPreferencePage.P_SAVE_ON_PREVIEW, Boolean.FALSE)) {
							monitor.subTask(Messages.AbstractJRXMLEditor_10);
							if (isDirty())
								UIUtils.getDisplay().syncExec(() -> doSave(monitor));
						}
						UIUtils.getDisplay().syncExec(() -> {
							model2preview();
							AbstractJRXMLEditor.super.pageChange(newPageIndex);
							updateContentOutline(getActivePage());
							activePage = newPageIndex;
						});
						return Status.OK_STATUS;
					}
				};
				job.setSystem(true);
				job.schedule();
				return;
			}
		}
		super.pageChange(newPageIndex);
		updateContentOutline(getActivePage());
		activePage = newPageIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getAdapter(java
	 * .lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class type) {
		if (type == JasperReportsContext.class)
			return jrContext;
		if (type == IContentOutlinePage.class) {
			if (outlinePage == null)
				outlinePage = new MultiOutlineView(this);
			Display.getDefault().syncExec(new Runnable() {
				private boolean isUpdateOutline = false;

				public void run() {
					if (isUpdateOutline) {
						isUpdateOutline = true;
						updateContentOutline(getActivePage());
						isUpdateOutline = false;
					}
				}
			});
			return outlinePage;
		}
		return super.getAdapter(type);
	}

	/**
	 * Updates the Outline view content depending on the editor page currently
	 * opened.
	 * 
	 * @param page the page index
	 */
	protected void updateContentOutline(int page) {
		if (outlinePage == null)
			return;
		IContentOutlinePage outline = (IContentOutlinePage) getEditor(page).getAdapter(IContentOutlinePage.class);
		if (outline == null)
			outline = new EmptyOutlinePage();
		outlinePage.setPageActive(outline);
	}

	/**
	 * Converts the source XML to the corresponding model instance.
	 * 
	 * @throws JRException
	 */
	protected void xml2model() throws Exception {
		InputStream in = null;
		try {
			IDocumentProvider dp = xmlEditor.getDocumentProvider();
			IDocument doc = dp.getDocument(xmlEditor.getEditorInput());
			in = new ByteArrayInputStream(doc.get().getBytes(FileUtils.UTF8_ENCODING));
			JasperDesign jd = new JRXmlLoader(jrContext, JRXmlDigesterFactory.createDigester(jrContext)).loadXML(in);
			jrContext.setJasperDesign(jd);
			JaspersoftStudioPlugin.getExtensionManager().onLoad(jd, this);
			setModel(createEditorModel());
		} finally {
			FileUtils.closeStream(in);
		}
	}

	/**
	 * Converts the current model to XML, specifying a version it should be
	 * compliant with.
	 * 
	 * @param the JasperReports version that should be used for compliance.
	 */
	protected String model2xml(String version) {
		String xml = null;
		try {
			JasperDesign report = null;
			MReport mReport = getMReport();
			if (mReport != null) {
				report = mReport.getJasperDesign();
				if (report.getProperty(DataQueryAdapters.DEFAULT_DATAADAPTER) == null) {
					Object obj = mReport.getParameter(DataQueryAdapters.DEFAULT_DATAADAPTER);
					if (obj != null && obj instanceof DataAdapterDescriptor) {
						String dataAdapterDesc = previewEditor.getDataAdapterDesc().getName();
						report.removeProperty(DataQueryAdapters.DEFAULT_DATAADAPTER);
						report.setProperty(DataQueryAdapters.DEFAULT_DATAADAPTER, dataAdapterDesc);
					}
				}
			}

			xml = JRXmlWriterHelper.writeReport(jrContext, report, FileUtils.UTF8_ENCODING, version);
			IDocumentProvider dp = xmlEditor.getDocumentProvider();
			IDocument doc = dp.getDocument(xmlEditor.getEditorInput());
			xmlFresh = true;
			if (xml != null && !Arrays.equals(doc.get().getBytes(), xml.getBytes())) {
				doc.set(xml);
			}
		} catch (Throwable e) {
			UIUtils.showError(e);
		}
		return xml;
	}

	/**
	 * Check if the current editor is dirty
	 * 
	 * @return true if the design or the preview editor are dirty, or if the xml
	 * inside the xml editor is not fresh. False otherwise
	 */
	@Override
	public boolean isDirty() {
		boolean designDirty = getDesignEditor() != null && getDesignEditor().isDirty();
		if (designDirty)
			return true;
		boolean previewDirty = previewEditor != null && previewEditor.isDirty();
		if (previewDirty)
			return true;
		return !xmlFresh;
	}

	protected void model2xml() {
		model2xml(JRXmlWriterHelper.LAST_VERSION);
	}

	/**
	 * Tells the preview editor that the model should be updated.
	 */
	protected void model2preview() {
		previewEditor.setJasperDesign(jrContext);
	}

	/**
	 * Update the visual designer.
	 */
	public abstract void updateVisualView();

	/**
	 * @return the model instance
	 */
	public INode getModel() {
		return model;
	}

	/**
	 * Sets the model.
	 * 
	 * @param model the new model
	 */
	public void setModel(INode model) {
		this.model = model;
		updateVisualView();
		if (jrContext != null) {
			jrContext.setJasperDesign(getJasperDesign());
		}
	}

	/**
	 * @return the current report instance
	 */
	protected MReport getMReport() {
		if (model != null)
			return (MReport) model.getChildren().get(0);
		return null;
	}

	/**
	 * @return the jasper design of the current report
	 */
	protected JasperDesign getJasperDesign() {
		MReport mreport = getMReport();
		if (mreport != null)
			return mreport.getValue();
		return null;
	}

	/**
	 * Return the console area if available, null otherwise
	 */
	public Console getConsole() {
		if (previewEditor != null) {
			return previewEditor.getConsole();
		}
		return null;
	}

	@Override
	public IEditorPart getActiveEditor() {
		return super.getActiveEditor();
	}

	/**
	 * In case of multipage editor implementation (like the JRXML editor) this
	 * return the internal editor currently active
	 */
	public IEditorPart getActiveInnerEditor() {
		return getActiveEditor();
	}

	@Override
	public IEditorPart getEditor(int pageIndex) {
		// FIXME: Is it really necessary?????
		// Maybe we are doing something wrong before if we are forced
		// to verify that the container is disposed
		if (getContainer().isDisposed() || getPageCount() <= pageIndex)
			return null;
		return super.getEditor(pageIndex);
	}

	/**
	 * @return the current {@link IFile} associated to the editor if possible,
	 * <code>null</code> otherwise
	 */
	protected IFile getCurrentFile() {
		if (getEditorInput() instanceof IFileEditorInput)
			return ((IFileEditorInput) getEditorInput()).getFile();
		return null;
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		setModel(null);
		if (jrContext != null)
			jrContext.dispose();
		super.dispose();
		if (context != null) {
			IContextService service = (IContextService) getSite().getService(IContextService.class);
			service.deactivateContext(context);
		}
	}

	/**
	 * Set the current preview type
	 * 
	 * @param viewerKey key of the type to show
	 * @param refresh flag to set if the preview should also be refreshed
	 */
	public void setPreviewOutput(String key, boolean refresh) {
		previewEditor.setCurrentViewer(key, refresh);
	}

	/**
	 * Return the actual preview type key on the preview editor
	 * 
	 * @return String representing the actual output of the preview editor
	 */
	public String getDefaultViewerKey() {
		return previewEditor.getDefaultViewerKey();
	}

	/**
	 * Set the preview editor to dirty, this will refresh the preview when
	 * switching into it
	 * 
	 * @param dirty true to set the editor dirty, false otherwise
	 */
	public void setPreviewDirty(boolean dirty) {
		previewEditor.setDirty(dirty);
	}

	@Override
	public String getTitleToolTip() {
		return JaspersoftStudioPlugin.getExtensionManager().getTitleToolTip(jrContext, super.getTitleToolTip());
	}

	// Accessory classes

	protected final class PreviewEditor extends PreviewContainer {
		public PreviewEditor(boolean listenResource, JasperReportsConfiguration jrContext) {
			super(listenResource, jrContext);
		}

		@Override
		public boolean runReport(com.jaspersoft.studio.data.DataAdapterDescriptor myDataAdapterDesc, boolean prmDirty,
				boolean daAction) {
			Boolean set = jrContext.getEditorContext().setDataAdapter(myDataAdapterDesc, getMReport(), daAction);
			if (set == null)
				return false;
			if (set)
				setDirty(set);

			super.runReport(myDataAdapterDesc, prmDirty, daAction);
			return true;
		}

		private boolean saving = false;

		@Override
		public void doSave(IProgressMonitor monitor) {
			if (saving)
				return;
			saving = true;
			try {
				super.doSave(monitor);
				if (!isRefreshing)
					AbstractJRXMLEditor.this.doSave(monitor);
			} finally {
				saving = false;
			}
		}

		/**
		 * Set the dirty flag of the preview area, but only if it isn't
		 * refreshing
		 */
		@Override
		public void setDirty(boolean dirty) {
			if (!isRefreshing) {
				super.setDirty(dirty);
			}
		}
	}

	// ABSTRACT METHODS

	/**
	 * @return <code>true</code> if the main report designer is dirty,
	 * <code>false</code> otherwise
	 */
	protected abstract boolean isDesignerDirty();

	protected abstract ISelection getDesignerPageSelection();

	protected abstract void setDesignerPageSelection(ISelection newSelection);

	/**
	 * Creates the Design Editor page.
	 * 
	 * @see #PAGE_DESIGNER
	 */
	protected abstract void createDesignEditorPage() throws PartInitException;

	protected abstract EditorPart getDesignEditor();
}
