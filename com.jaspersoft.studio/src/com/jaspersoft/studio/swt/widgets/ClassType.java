/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.property.descriptor.classname.ClassTypeCellEditor;

import net.sf.jasperreports.eclipse.util.Misc;

public class ClassType {
	private Text factoryText;
	private Button btnNewButton;

	public ClassType(Composite parent, String tooltip) {
		createComponent(parent, tooltip);
	}

	public void createComponent(Composite parent, String tooltip) {
		factoryText = new Text(parent, SWT.BORDER);
		factoryText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		factoryText.setToolTipText(tooltip);

		btnNewButton = new Button(parent, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String classname = ClassTypeCellEditor.getJavaClassDialog(factoryText.getShell(), null);
				if (classname != null)
					factoryText.setText(classname);
			}
		});
		btnNewButton.setText("...");
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	}

	public Text getControl() {
		return factoryText;
	}

	public void setClassType(String classtype) {
		factoryText.setText(Misc.nvl(classtype, ""));
	}

	public String getClassType() {
		return factoryText.getText().trim();
	}

	public void addListener(ModifyListener listener) {
		factoryText.addModifyListener(listener);
	}

	public void removeListener(ModifyListener listener) {
		factoryText.removeModifyListener(listener);
	}
}
