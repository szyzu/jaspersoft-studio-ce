/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.fonts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.messages.Messages;

import net.sf.jasperreports.eclipse.ui.ATitledDialog;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.fonts.SimpleFontSet;

public class FontSetDialog extends ATitledDialog {
	private SimpleFontSet fs;

	public FontSetDialog(Shell parentShell, SimpleFontSet fs) {
		super(parentShell);
		setTitle(Messages.FontSetDialog_0);
		setDefaultSize(400, 140);
		this.fs = fs;
	}

	public SimpleFontSet getValue() {
		return fs;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cmp = (Composite) super.createDialogArea(parent);
		cmp.setLayout(new GridLayout(2, false));

		new Label(cmp, SWT.NONE).setText(Messages.FontSetDialog_1);

		final Text txt = new Text(cmp, SWT.BORDER);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt.setText(Misc.nvl(fs.getName()));
		txt.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				fs.setName(txt.getText());
			}
		});

		return cmp;
	}
}
