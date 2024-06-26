/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.EditPart;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterEditorPart;
import com.jaspersoft.studio.data.DataAdapterFactory;
import com.jaspersoft.studio.data.DataAdapterManager;
import com.jaspersoft.studio.data.DataAdapterUtils;
import com.jaspersoft.studio.data.wizard.pages.DataAdapterEditorPage;
import com.jaspersoft.studio.data.wizard.pages.DataAdaptersListPage;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.SelectionHelper;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.wizards.ContextData;
import com.jaspersoft.studio.wizards.ContextHelpIDs;

import net.sf.jasperreports.eclipse.builder.jdt.JDTUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.wizard.project.ProjectUtil;

public class NewFileDataAdapterWizard extends AbstractDataAdapterWizard implements INewWizard, SelectionListener {
	/** The wizard ID */
	public static final String WIZARD_ID = "com.jaspersoft.studio.data.wizard.NewFileDataAdapterWizard"; //$NON-NLS-1$
	private ISelection selection;
	private WizardNewFileCreationPage step1;

	/**
	 * This constructor will set the data adapter wizard. The data adapters list is displayed as first page, then the edit
	 * page is shown.
	 */
	public NewFileDataAdapterWizard() {
		setWindowTitle(Messages.DataAdapterWizard_windowtitle);
		this.storage = DataAdapterManager.getPreferencesStorage();
		setConfig(JasperReportsConfiguration.getDefaultJRConfig(), true);
		JDTUtils.deactivateLinkedResourcesSupport();
	}

	/**
	 * Pass to this constructor a dataAdapter to be edited. This will set the wizard directly to edit page.
	 * 
	 * @param dataAdapter
	 */
	public NewFileDataAdapterWizard(DataAdapterDescriptor dataAdapter) {
		this();
		this.dataAdapter = dataAdapter;
	}

	private class WizardNewAdapterPage extends WizardNewFileCreationPage implements ContextData {

		protected String contextName;

		public WizardNewAdapterPage(String pageName, IStructuredSelection selection, String contextName) {
			super(pageName, selection);
			this.contextName = contextName;
		}

		/**
		 * Set the root control of the wizard, and also add a listener to do the perform help action and set the context of
		 * the top control.
		 */
		@Override
		protected void setControl(Control newControl) {
			super.setControl(newControl);
			newControl.addListener(SWT.Help, new Listener() {
				@Override
				public void handleEvent(Event event) {
					performHelp();
				}
			});
			setHelpData();
		};

		/**
		 * Set the help data that should be seen in this step
		 */
		@Override
		public void setHelpData() {
			if (contextName != null) {
				PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), contextName);
			}
		}

		/**
		 * Set and show the help data if a context, that bind this wizard with the data, is provided
		 */
		@Override
		public void performHelp() {
			if (contextName != null) {
				PlatformUI.getWorkbench().getHelpSystem().displayHelp(contextName);
			}
		};

		@Override
		public boolean canFlipToNextPage() {
			if (JDTUtils.isVirtualResource(getContainerFullPath())) {
				setErrorMessage(Messages.NewFileDataAdapterWizard_VirtualFolderError);
				return false;
			}
			IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(getContainerFullPath());
			String fileName = getFileName();
			IFile file = r.getProject().getFile(fileName);
			if (file.exists()) {
				return false;
			}
			if (!fileName.endsWith(DataAdapterUtils.DOTTED_FILE_EXTENSION)) {
				fileName += DataAdapterUtils.DOTTED_FILE_EXTENSION; 
			}
			file = r.getProject().getFile(fileName);
			return !file.exists();
		}

		@Override
		public boolean isPageComplete() {
			return !JDTUtils.isVirtualResource(getContainerFullPath()) && super.isPageComplete();
		}

		@Override
		public void setVisible(boolean visible) {
			JDTUtils.deactivateLinkedResourcesSupport(visible);
			super.setVisible(visible);
		}
	}

	@Override
	public void addPages() {
		step1 = new WizardNewAdapterPage("newFilePage1", (IStructuredSelection) selection, //$NON-NLS-1$
				ContextHelpIDs.WIZARD_NEW_DATAAPDATER);
		step1.setTitle(Messages.NewFileDataAdapterWizard_1);
		step1.setDescription(Messages.NewFileDataAdapterWizard_2);
		step1.setFileExtension(DataAdapterUtils.FILE_EXTENSION);//$NON-NLS-1$
		setupNewFileName();
		addPage(step1);

		if (dataAdapter == null) {
			dataAdapterListPage = new DataAdaptersListPage();
			addPage(dataAdapterListPage);
		}

		dataAdapterEditorPage = new DataAdapterEditorPage();
		if (dataAdapter != null) {
			dataAdapterEditorPage.setEditMode(true);
		}
		addPage(dataAdapterEditorPage);
	}

	public void setupNewFileName() {
		String filename = DataAdapterUtils.NEW_DATA_ADAPTER_FILENAME;
		if (selection != null) {
			if (selection instanceof TreeSelection) {
				TreeSelection s = (TreeSelection) selection;
				if (s.getFirstElement() instanceof IFile) {
					IFile file = (IFile) s.getFirstElement();

					String f = file.getProjectRelativePath().removeLastSegments(1).toOSString() + "/" + filename; //$NON-NLS-1$

					int i = 1;
					while (file.getProject().getFile(f).exists()) {
						filename = "DataAdapter" + i + DataAdapterUtils.DOTTED_FILE_EXTENSION; //$NON-NLS-1$ //$NON-NLS-2$
						f = file.getProjectRelativePath().removeLastSegments(1).toOSString() + "/" + filename; //$NON-NLS-1$
						i++;
					}
				}
			}
			step1.setFileName(filename);
		}
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == step1) {
			IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(step1.getContainerFullPath());
			IFile file = r.getProject().getFile(step1.getContainerFullPath() + Messages.ReportNewWizard_1 + step1.getFileName());
			getConfig().init(file);
		}
		if (page == dataAdapterListPage) {// && event.getTargetPage() == dataAdapterEditorPage) {
			// Update the layout of the editor page with the proper data adapter editor
			// provided by the new data adapter
			DataAdapterFactory factory = dataAdapterListPage.getSelectedFactory();

			java.text.MessageFormat fm = new java.text.MessageFormat(Messages.DataAdapterWizard_newdataadaptername);
			// 1. instance a new dataAdapter using the factory
			try {
				DataAdapterDescriptor newDataAdapter = factory.createDataAdapter();
				newDataAdapter.getDataAdapter(getConfig()).setName(fm.format(new Object[] { 1 }));

				// 2. set in the wizard page the data adapter to edit
				if (selectedFactory != factory) {
					dataAdapterEditorPage.setJrContext(getConfig());
					dataAdapterEditorPage.setDataAdapter(newDataAdapter);

					selectedFactory = factory;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.getNextPage(page);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);

		if (this.dataAdapter != null) {
			DataAdapterDescriptor editedDataAdapter = DataAdapterManager.cloneDataAdapter(this.dataAdapter, getConfig());
			dataAdapterEditorPage.setDataAdapter(editedDataAdapter);
		}
	}

	// Save the new adapter using the manager
	@Override
	public boolean performFinish() {
		JDTUtils.restoreLinkedResourcesSupport();
		DataAdapterDescriptor editedDataAdapter = dataAdapterEditorPage.getDataAdapter();
		dataAdapterEditorPage.performFinishInvoked();

		if (this.dataAdapter == null) {
			this.dataAdapter = editedDataAdapter;
		} else // We are modifying an existing adapter....
		{
			// ... let's update with the adapter just modified ...
			String oldName = this.dataAdapter.getName();
			dataAdapter.setDataAdapter(editedDataAdapter.getDataAdapter());
			dataAdapter.getDataAdapter().setName(oldName);
		}
		final String containerName = step1.getContainerFullPath().toPortableString();
		final String fileName = step1.getFileName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage()); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		JDTUtils.restoreLinkedResourcesSupport();
		return super.performCancel();
	}

	private IFile file;

	private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2); //$NON-NLS-1$
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			String message = "Container \"" + containerName + "\" does not exist."; //$NON-NLS-1$ //$NON-NLS-2$
			IStatus status = new Status(IStatus.ERROR, "com.jaspersoft.studio", IStatus.OK, message, null); //$NON-NLS-1$
			throw new CoreException(status);
		}
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				file = step1.createNewFile();
			}
		});

		InputStream in = null;
		try {
			String xml = DataAdapterManager.toDataAdapterFile(dataAdapter, getConfig());
			in = new ByteArrayInputStream(xml.getBytes());
			if (file.exists())
				file.setContents(in, true, true, monitor);
			else
				file.create(in, true, monitor);
			DataAdapterManager.getDataAdapter(file, file.getProject(), getConfig());
		} finally {
			FileUtils.closeStream(in);
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing..."); //$NON-NLS-1$
		openEditor(file);
		monitor.worked(1);
	}

	protected void openEditor(final IFile file) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					// Force the default editor for the data adapter file
					// so that it can be opened with the same one in the future
					IDE.setDefaultEditor(file, DataAdapterEditorPart.ID);
					IDE.openEditor(page, file, DataAdapterEditorPart.ID);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (selection instanceof StructuredSelection) {
			if (selection.getFirstElement() instanceof IProject || selection.getFirstElement() instanceof IFile
					|| selection.getFirstElement() instanceof IFolder
					|| selection.getFirstElement() instanceof IPackageFragment) {
				this.selection = selection;
				return;
			}
			for (Object obj : selection.toList()) {
				if (obj instanceof EditPart) {
					IEditorInput ein = SelectionHelper.getActiveJRXMLEditor().getEditorInput();
					if (ein instanceof FileEditorInput) {
						this.selection = new TreeSelection(new TreePath(new Object[] { ((FileEditorInput) ein).getFile() }));
						return;
					}
				}
			}
			IProgressMonitor progressMonitor = new NullProgressMonitor();
			IProject[] prjs = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject p : prjs) {
				try {
					if (ProjectUtil.isOpen(p) && p.getNature(JavaCore.NATURE_ID) != null) {
						p.open(progressMonitor);
						this.selection = new TreeSelection(new TreePath(new Object[] { p.getFile(DataAdapterUtils.NEW_DATA_ADAPTER_FILENAME) }));
						return;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			for (IProject p : prjs) {
				try {
					if (p.isAccessible()) {
						p.open(progressMonitor);
						this.selection = new TreeSelection(new TreePath(new Object[] { p.getFile(DataAdapterUtils.NEW_DATA_ADAPTER_FILENAME) }));
						return;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		this.selection = selection;
	}

}
