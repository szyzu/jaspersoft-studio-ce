/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.wizard.pages.DataAdapterEditorPage;
import com.jaspersoft.studio.messages.Messages;

public class DataAdapterWizardDialog extends WizardDialog {

	List<SelectionListener> listeners = new ArrayList<>();
	Button testButton = null;

	public DataAdapterWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	public void addTestListener(SelectionListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}

	}

	public void removeTestListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	private void fireTestPressed(SelectionEvent e) {
		for (SelectionListener listener : listeners) {
			listener.widgetSelected(e);
		}

	}

	@Override
	public void updateButtons() {
		super.updateButtons();
		boolean canFinish = getWizard().canFinish();
		testButton.setEnabled(canFinish);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		((GridLayout) parent.getLayout()).numColumns = 1;
		testButton = new Button(parent, SWT.NONE);
		testButton.setFont(parent.getFont());
		testButton.setText(Messages.DataAdapterWizardDialog_0);
		setButtonLayoutData(testButton);
		testButton.setEnabled(false);
		testButton.setVisible(false);
		testButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				fireTestPressed(event);
			}
		});
		super.createButtonsForButtonBar(parent);
	}

	/*
	 * Checks if the Test button is supposed to be visible.
	 */
	private boolean isTestVisible() {
		boolean isTestVisible = false;
		if (getWizard() instanceof AbstractDataAdapterWizard && getCurrentPage() instanceof DataAdapterEditorPage) {
			DataAdapterDescriptor dataAdapterDesc = ((DataAdapterEditorPage) getCurrentPage()).getDataAdapter();
			isTestVisible = (dataAdapterDesc != null) ? dataAdapterDesc.doSupportTest() : false;
		}
		return isTestVisible;
	}

	public void setTestButtonEnabled(boolean b) {
		boolean testVisible = isTestVisible();
		testButton.setEnabled(b && testVisible && getWizard().canFinish());
		testButton.setVisible(testVisible);
	}
}
