/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.wizards;

import java.lang.reflect.InvocationTargetException;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;



/**
 * 
 * A wizard runnable page is a wizard page that tries to run some code before move to the next page.
 * When Next is clicked, the run method is invoked, and the wizard continue only if there are no errors.
 * 
 * @author gtoffoli
 *
 */
public abstract class JSSWizardRunnablePage extends JSSWizardPage {

	/** id for the settings information related to a possible exception raised during the custom code run */
	public static final String EXECUTION_EXCEPTION_RAISED = "exception_raised_during_execution"; //$NON-NLS-1$
	
	/**
	 * Special exception that a process can rise to avoid to see a message box with the error
	 * description.
	 */
	public static final Exception USER_CANCEL_EXCEPTION = new Exception("process_canceled_by_the_user"); //$NON-NLS-1$

	/**
	 * Variable to mark the final result of an elaboration.
	 * This is not the cleaner way to check the elaboration result, but we assume that a runnable page cannot
	 * be validated more than once at time....
	 */
	protected boolean processResult = true;
	
	public JSSWizardRunnablePage(String pageName) {
		super(pageName);
	}

	/**
	 * The method invoked before going to the next page.
	 */
	public abstract void run(IProgressMonitor monitor) throws Exception;

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IWizardPage getNextPage() {
		if (requireElaboration()) {
			processResult = true;
			if (getWizard() != null && getWizard() instanceof JSSWizard) {
				((JSSWizard) getWizard()).run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask("Processing, please wait", IProgressMonitor.UNKNOWN); // to be translated!
						try {
							JSSWizardRunnablePage.this.run(monitor);
						} catch (Exception e) {
							getSettings().put(EXECUTION_EXCEPTION_RAISED, true);
							if (e != USER_CANCEL_EXCEPTION) {
								UIUtils.showError(e);
							}
							processResult = false;
						} finally {
							monitor.done();
						}
					}
				});
			}

			if (!processResult) {
				return null;
			}
		}
		return super.getNextPage();			
	}

	
	/**
	 * The last change to decide to kick the elaboration or not.
	 * subclasses may override to change the default based on the conditions.
	 * 
	 * @return boolean (true by default). Subclasses should return true if an elaboration is required, or false
	 * 				 if the current status of the page does not require to trigger an elaboration
	 * 
	 */
	public boolean requireElaboration()
	{
		return true;
	}
	
}
