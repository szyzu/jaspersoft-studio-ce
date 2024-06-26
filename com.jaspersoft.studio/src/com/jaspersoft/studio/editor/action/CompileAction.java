/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.backward.JRVersionPreferencesPages;
import com.jaspersoft.studio.editor.AbstractJRXMLEditor;
import com.jaspersoft.studio.editor.JrxmlEditor;
import com.jaspersoft.studio.editor.preview.view.control.JRErrorHandler;
import com.jaspersoft.studio.editor.preview.view.control.JRMarkerErrorHandler;
import com.jaspersoft.studio.editor.report.AbstractVisualEditor;
import com.jaspersoft.studio.editor.report.ReportEditor;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.utils.Console;
import com.jaspersoft.studio.utils.SelectionHelper;
import com.jaspersoft.studio.utils.SubreportsUtil;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.builder.JasperReportErrorHandler;
import net.sf.jasperreports.eclipse.builder.JasperReportsBuilder;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JasperDesign;

public class CompileAction extends SelectionAction implements IMenuCreator {

	public static final String ID = "compileAction"; //$NON-NLS-1$

	/**
	 * Drop down menu of the action, where the backward compatibility options
	 * are shown
	 */
	private Menu menu;

	public CompileAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
		setMenuCreator(this);
	}

	protected void init() {
		super.init();
		setText(Messages.CompileAction_actionName);
		setToolTipText(Messages.CompileAction_actionTooltip);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("/icons/build_tab.gif")); //$NON-NLS-1$
		setId(ID);
		setEnabled(true);
	}

	@Override
	public void run() {
		final JasperReportsConfiguration jConfig = getMDatasetToShow();
		final Console console = getCleanConsole();
		if (jConfig != null) {
			Job job = new Job(Messages.CompileAction_jobName) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					SubMonitor subM = SubMonitor.convert(monitor,100);
					IWorkbenchPart part = getWorkbenchPart();
					if (part != null && part.getSite() != null && part.getSite().getPage() != null
							&& part.getSite().getPage().getActiveEditor() != null) {
						IEditorPart editor = part.getSite().getPage().getActiveEditor();
						if (editor instanceof ISaveablePart && ((ISaveablePart) editor).isDirty())
							editor.doSave(subM.split(5));
					}
					subM.setWorkRemaining(5);
					IStatus status = actionCompile(jConfig, subM.split(95), true, console);
					return status;
				}
			};
			job.setPriority(Job.BUILD);
			job.schedule();
		}
	}

	/**
	 * Execute the action of compilation of the report and its subreports,
	 * adding the errors to the error view and various information to the
	 * console.
	 * 
	 * @param jConfig
	 *            jasper configuration of the compiled report
	 * @param monitor
	 *            monitor for the execution
	 * @param compileMain
	 *            true if the main file need to be compiled, false to compile
	 *            only the subreports
	 * @param console
	 *            console where print info and errors
	 * @return Status.OK_STATUS if the compilation finished without exception
	 *         (this dosen't means that the report hasen't Compilation error),
	 *         Status.CANCEL_STATUS otherwise
	 */
	public IStatus actionCompile(final JasperReportsConfiguration jConfig, IProgressMonitor monitor,
			boolean compileMain, final Console console) {

		IFile mfile = (IFile) jConfig.get(FileUtils.KEY_FILE);
		if (mfile != null) {
			try {
				if (console != null) {
					console.addMessage(MessageFormat.format(Messages.CompileAction_consoleMessage1, mfile.getName()));
				}

				// Use a custom builder that redirect the output errors found
				// during the compilation process to the console
				JasperReportsBuilder builder = new JasperReportsBuilder() {

					@Override
					protected JasperReportErrorHandler getErrorHandler(IFile resource) {
						JRErrorHandler errorHandler = new JRMarkerErrorHandler(console, resource);
						errorHandler.reset();
						return errorHandler;
					};
				};
				
				SubMonitor submon = SubMonitor.convert(monitor,100);
				// ATTENTION! this can generate possible errors, because we are
				// not calling builders in the right order
				// we are also not looking very good for for subreports, because
				// expression evaluation is not good
				// file.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD,
				// monitor);
				if (compileMain) {
					monitor.subTask(Messages.CompileAction_SubtaskCompilingMainReport);
					IFile destFIle = builder.compileJRXML(mfile, submon.split(10), jConfig);
					if (console != null && destFIle != null) {
						File file = destFIle.getRawLocation().toFile();
						if (file.exists()) {
							console.addMessage(
									MessageFormat.format(Messages.CompileAction_consoleMessage2, file.toString()));
						} else {
							console.addMessage(Messages.CompileAction_consoleMessage3);
						}
					}
				}
				submon.setWorkRemaining(90);
				monitor.subTask(Messages.CompileAction_SubtaskRetrievingAllSubs);
				Map<File, IFile> fmap = getSubreports(jConfig, mfile, jConfig.getJasperDesign(), submon.split(80));
								
				monitor.subTask(Messages.CompileAction_SubtaskCompilingAllSubs);
				SubMonitor subcompileMon = submon.split(10).setWorkRemaining(fmap.size());
				for (File f : fmap.keySet()) {
					IFile file = fmap.get(f);
					if (file != null) {
						builder.compileJRXML(file, subcompileMon.split(1), jConfig);
					} else {
						try {
							JasperCompileManager.compileReportToFile(f.getAbsolutePath());
						} catch (JRException e) {
							e.printStackTrace();
						}
					}
				}
				submon.setWorkRemaining(0);
			} catch (CoreException ex) {
				return Status.CANCEL_STATUS;
			}
		}
		return Status.OK_STATUS;
	}

	protected Map<File, IFile> getSubreports(JasperReportsConfiguration jConfig, IFile mfile, JasperDesign jd,
			IProgressMonitor monitor) {
		return SubreportsUtil.getSubreportFiles(jConfig, mfile, jConfig.getJasperDesign(), monitor);
	}

	/**
	 * Return the output console for the current opened editor, the console is
	 * cleaned before the return. If the console can not be found the null is
	 * returned
	 * 
	 * @return reference to a clean console of the current editor or null if it
	 *         is not available
	 */
	protected Console getCleanConsole() {
		AbstractJRXMLEditor editor = (AbstractJRXMLEditor) SelectionHelper.getActiveJRXMLEditor();
		if (editor != null) {
			final Console console = editor.getConsole();
			if (console != null) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						console.clearConsole();
					}
				});
			}
			return editor.getConsole();
		} else
			return null;
	}

	protected JasperReportsConfiguration getMDatasetToShow() {
		ISelection selection = getSelection();
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof EditPart && ((EditPart) firstElement).getModel() instanceof ANode) {
				ANode currentNode = (ANode) ((EditPart) firstElement).getModel();
				return currentNode.getJasperConfiguration();
			}
		}
		final AbstractVisualEditor part = (AbstractVisualEditor) getWorkbenchPart();
		if (part instanceof ReportEditor) {
			ReportEditor rpeditor = (ReportEditor) part;
			return rpeditor.getJrContext();
		} else if (!part.getModel().getChildren().isEmpty()) {
			ANode firstChild = (ANode) part.getModel().getChildren().get(0);
			return firstChild.getJasperConfiguration();
		}
		IEditorPart activeJRXMLEditor = SelectionHelper.getActiveJRXMLEditor();
		if (activeJRXMLEditor != null && activeJRXMLEditor instanceof JrxmlEditor) {
			JrxmlEditor jrEditor = (JrxmlEditor) activeJRXMLEditor;
			return ((ANode) jrEditor.getModel()).getJasperConfiguration();
		}
		return null;
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	public static IStatus doRun(final JasperReportsConfiguration jConfig, IProgressMonitor monitor,
			boolean compileMain) {
		IFile mfile = (IFile) jConfig.get(FileUtils.KEY_FILE);
		if (mfile != null)
			try {
				SubMonitor submon = SubMonitor.convert(monitor,100);
				// ATTENTION! this can generate possible errors, because we are
				// not calling builders in the right order
				// we are also not looking very good for for subreports, because
				// expression evaluation is not good
				// file.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD,
				// monitor);

				JasperReportsBuilder builder = new JasperReportsBuilder();
				if (compileMain) {
					monitor.subTask(Messages.CompileAction_SubtaskCompilingMainReport);
					builder.compileJRXML(mfile, submon.split(10), jConfig);
				}
				submon.setWorkRemaining(90);
				monitor.subTask(Messages.CompileAction_SubtaskRetrievingAllSubs);
				Map<File, IFile> fmap = SubreportsUtil.getSubreportFiles(jConfig, mfile, jConfig.getJasperDesign(),submon.split(80));
				
				monitor.subTask(Messages.CompileAction_SubtaskCompilingAllSubs);
				SubMonitor subcompileMon = submon.split(10).setWorkRemaining(fmap.size());
				for (File f : fmap.keySet()) {
					IFile file = fmap.get(f);
					if (file != null) {
						builder.compileJRXML(file, subcompileMon.split(1), jConfig);
					} else {
						try {
							JasperCompileManager.compileReportToFile(f.getAbsolutePath());
						} catch (JRException e) {
							e.printStackTrace();
						}
					}
					if (monitor.isCanceled())
						break;
				}
				submon.setWorkRemaining(0);
			} catch (CoreException e) {
				return Status.CANCEL_STATUS;
			}
		return Status.OK_STATUS;
	}

	/**
	 * Return the menu used to handle the backward compatibility
	 */
	@Override
	public Menu getMenu(Control parent) {
		if (menu != null) {
			menu.dispose();
		}
		menu = new Menu(parent);

		MenuItem manage = new MenuItem(menu, SWT.PUSH);
		manage.setText(Messages.CompileAction_manage);
		manage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final JasperReportsConfiguration jConfig = getMDatasetToShow();
				IFile f = (IFile) jConfig.get(FileUtils.KEY_FILE);
				if (f != null) {
					PreferenceDialog pref = PreferencesUtil.createPropertyDialogOn(UIUtils.getShell(), f.getProject(),
							JRVersionPreferencesPages.PAGE_ID, null, null);
					if (pref != null)
						pref.open();
				}
			}
		});
		return menu;
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}
}
