/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.dataset.fields.table.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import net.sf.jasperreports.eclipse.util.Misc;

public class WBoolean extends AWControl {
	public WBoolean(AWidget aw) {
		super(aw);
	}

	protected boolean refresh = false;
	protected Combo cmb;
	private Composite cmp;

	@Override
	protected void createControl(Composite parent) {
		cmp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		cmp.setLayout(layout);

		cmb = new Combo(cmp, SWT.BORDER | SWT.READ_ONLY);
		cmb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cmb.setItems(getValues());
		cmb.addModifyListener(e -> {
			if (refresh)
				return;
			handleValueChanged();
		});
	}

	protected void handleValueChanged() {
		if (cmb.getSelectionIndex() == 0)
			aw.setValue(null);
		else if (cmb.getSelectionIndex() == 1)
			aw.setValue(true);
		else if (cmb.getSelectionIndex() == 2)
			aw.setValue(false);
		cmb.setToolTipText(aw.getToolTipText());
	}

	protected String[] getValues() {
		return new String[] { "", "true", "false" };
	}

	@Override
	protected void fillValue() {
		String v = getText();
		try {
			refresh = true;
			cmb.setText(Misc.nvl(v, ""));
		} finally {
			refresh = false;
		}
		cmb.setToolTipText(aw.getToolTipText());
	}

	@Override
	public void addDisposeListener(DisposeListener dlistener) {
		cmb.addDisposeListener(dlistener);
	}

	@Override
	public void setEnabled(boolean en) {
		cmb.setEnabled(en);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (cmp != null && !cmp.isDisposed())
			cmp.dispose();
	}
}
