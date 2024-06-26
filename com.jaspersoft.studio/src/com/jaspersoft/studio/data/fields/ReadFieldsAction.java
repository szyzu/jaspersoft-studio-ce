/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.fields;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;

public class ReadFieldsAction extends Action implements IMenuCreator {
	public static final String ID = "FIELDPROVIDERSACTION"; //$NON-NLS-1$ 

	public ReadFieldsAction() {
		super();
		setId(ID);
		setMenuCreator(this);

		setText(" Read Fields ");
		setDescription("Read fields");
		setToolTipText("Read fields");
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	@Override
	public void run() {
		System.out.println();
	}

	private Menu listMenu;

	public void dispose() {
		if (listMenu != null)
			listMenu.dispose();
	}

	public Menu getMenu(final Control parent) {
		if (listMenu != null)
			listMenu.dispose();
		listMenu = new Menu(parent);

		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MenuItem menuItem = (MenuItem) e.getSource();
				menuItem.setSelection(true);

				setText(menuItem.getText());
				if (parent != null) {
					ToolBar toolBar = (ToolBar) parent;
					toolBar.pack(true);
					toolBar.getParent().getParent().layout(true);
				}
				run();

			}
		};
		MenuItem m1 = new MenuItem(listMenu, SWT.PUSH);
		m1.setText("Read Fields (Automatically) ");
		m1.addSelectionListener(listener);
		m1.setData("da.key", "aa");

		m1 = new MenuItem(listMenu, SWT.PUSH);
		m1.setText("Read Fields (Manually) ");
		m1.addSelectionListener(listener);
		m1.setData("da.key", "aa");

		return listMenu;
	}

	public Menu getMenu(Menu parent) {
		return null;
	}

}
