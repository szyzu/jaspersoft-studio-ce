/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.style.wizard;

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
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.jaspersoft.studio.backward.JRVersionPreferencesPages;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.SelectionHelper;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.builder.jdt.JDTUtils;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.wizard.project.ProjectUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRSimpleTemplate;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.xml.JRXmlBaseWriter;
import net.sf.jasperreports.engine.xml.JRXmlTemplateWriter;

/*
 * This is a sample new wizard. Its role is to create a new file resource in the provided container. If the container
 * resource (a folder or a project) is selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "jrxml". If a sample multi-page editor (also
 * available as a template) is registered for the same extension, it will be able to open it.
 */

public class StyleTemplateNewWizard extends Wizard implements INewWizard {
	public static final String WIZARD_ID = "com.jaspersoft.studio.wizards.StyleTemplateNewWizard"; //$NON-NLS-1$
	private static final String NEW_STYLE_NAME = "NEW_STYLE";//$NON-NLS-1$
	private static final String NEW_STYLE_EXT = ".jrtx";//$NON-NLS-1$
	protected static final String NEW_STYLE_JRTX = NEW_STYLE_NAME + NEW_STYLE_EXT;
	protected WizardNewFileCreationPage step1;
	protected ISelection selection;

	protected IFile file = null;

	protected IFile reportFile = null;

	/**
	 * Constructor for ReportNewWizard.
	 */
	public StyleTemplateNewWizard() {
		super();
		setWindowTitle(Messages.StyleTemplateNewWizard_wizardtitle);
		setNeedsProgressMonitor(true);
		JDTUtils.deactivateLinkedResourcesSupport();
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		step1 = getDestinationPage();
		setupNewFileName();
		addPage(step1);
	}

	public void setupNewFileName() {
		String filename = NEW_STYLE_JRTX;
		if (selection != null) {
			if (selection instanceof TreeSelection) {
				TreeSelection s = (TreeSelection) selection;
				if (s.getFirstElement() instanceof IFile) {
					IFile file = (IFile) s.getFirstElement();

					String f = file.getProjectRelativePath().removeLastSegments(1).toOSString() + "/" + filename;//$NON-NLS-1$

					int i = 1;
					while (file.getProject().getFile(f).exists()) {
						filename = NEW_STYLE_NAME + i + NEW_STYLE_EXT;
						f = file.getProjectRelativePath().removeLastSegments(1).toOSString() + "/" + filename;//$NON-NLS-1$
						i++;
					}
				}
			}
			step1.setFileName(filename);
		}
	}

	/**
	 * Return the WizardPage used to select the destination resource for the
	 * template reference
	 * 
	 * @return a not null {@link WizardNewFileCreationPage}
	 */
	protected WizardNewFileCreationPage getDestinationPage() {
		WizardHelpNewFileCreationPage page = new WizardHelpNewFileCreationPage("newFilePage1", //$NON-NLS-1$
				(IStructuredSelection) selection);
		page.setTitle(Messages.StyleTemplateNewWizard_title);
		page.setDescription(Messages.StyleTemplateNewWizard_description);
		page.setFileExtension("jrtx");//$NON-NLS-1$
		return page;
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		JDTUtils.restoreLinkedResourcesSupport();
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

	@Override
	public boolean canFinish() {
		if (JDTUtils.isVirtualResource(step1.getContainerFullPath())) {
			step1.setMessage(Messages.StyleTemplateNewWizard_VirtualFolderErr, IStatus.ERROR);
			return false;
		}
		return super.canFinish();
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

	private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2); //$NON-NLS-1$
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (resource.isVirtual()) {
			throwCoreException("Container \"" + containerName + "\" is a virtual folder."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				file = step1.createNewFile();
			}
		});

		InputStream in = null;
		try {
			in = openContentStream();
			if (file.exists()) {
				file.setContents(in, true, true, monitor);
			} else {
				file.create(in, true, monitor);
			}
			reportFile = file;
		} finally {
			FileUtils.closeStream(in);
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing..."); //$NON-NLS-1$
		openEditor(file);
		monitor.worked(1);
	}

	protected void openEditor(final IFile file) {
		UIUtils.getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public IFile getReportFile() {
		return reportFile;
	}

	/**
	 * We will initialize file contents with a sample text.
	 */
	protected InputStream openContentStream() {
		JasperReportsConfiguration jConf = null;
		try {
			JRSimpleTemplate tmp = new JRSimpleTemplate();

			JRDesignStyle jrDesignStyle = new JRDesignStyle();
			jrDesignStyle.setName("SimpleStyle"); //$NON-NLS-1$
			tmp.addStyle(jrDesignStyle);

			if (file != null)
				jConf = JasperReportsConfiguration.getDefaultJRConfig(file);
			else if (reportFile != null)
				jConf = JasperReportsConfiguration.getDefaultJRConfig(reportFile);
			else
				jConf = JasperReportsConfiguration.getDefaultJRConfig();

			jConf.setProperty(JRXmlBaseWriter.PROPERTY_REPORT_VERSION,
					jConf.getProperty(JRVersionPreferencesPages.JSS_COMPATIBILITY_VERSION));
			String contents = JRXmlTemplateWriter.writeTemplate(jConf, tmp);
			return new ByteArrayInputStream(contents.getBytes());
		} catch (JRException e) {
			e.printStackTrace();
		} finally {
			jConf.dispose();
		}
		return null;
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "com.jaspersoft.studio", IStatus.OK, message, null); //$NON-NLS-1$
		throw new CoreException(status);
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
						this.selection = new TreeSelection(
								new TreePath(new Object[] { ((FileEditorInput) ein).getFile() }));
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
						this.selection = new TreeSelection(new TreePath(new Object[] { p.getFile(NEW_STYLE_JRTX) }));
						return;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			for (IProject p : prjs) {
				try {
					if (ProjectUtil.isOpen(p)) {
						p.open(progressMonitor);
						this.selection = new TreeSelection(new TreePath(new Object[] { p.getFile(NEW_STYLE_JRTX) }));
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
