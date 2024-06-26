/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.fonts;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.jaspersoft.studio.messages.Messages;

import net.sf.jasperreports.eclipse.ui.ATitledDialog;
import net.sf.jasperreports.engine.fonts.FontSet;

public class SelectFontSetSetDialog extends ATitledDialog {
	private List<FontSet> sets;
	private FontSet value;

	public SelectFontSetSetDialog(Shell parentShell, List<FontSet> sets) {
		super(parentShell);
		setTitle(Messages.SelectFontSetSetDialog_0);
		setDefaultSize(300, 400);
		this.sets = sets;
	}

	public FontSet getValue() {
		return value;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cmp = (Composite) super.createDialogArea(parent);
		cmp.setLayout(new GridLayout());

		final org.eclipse.swt.widgets.List lst = new org.eclipse.swt.widgets.List(cmp, SWT.SINGLE | SWT.BORDER);
		lst.setLayoutData(new GridData(GridData.FILL_BOTH));
		String[] items = new String[sets.size()];
		for (int i = 0; i < sets.size(); i++)
			items[i] = sets.get(i).getName();
		lst.setItems(items);
		lst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				value = sets.get(lst.getSelectionIndex());
			}
		});

		return cmp;
	}
}
