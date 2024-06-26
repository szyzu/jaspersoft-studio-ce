/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.prm.prefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

import com.jaspersoft.studio.help.TableHelpListener;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.editor.table.TableFieldEditor;
import com.jaspersoft.studio.prm.ParameterSet;
import com.jaspersoft.studio.prm.ParameterSetProvider;
import com.jaspersoft.studio.prm.dialog.ParameterSetDialog;
import com.jaspersoft.studio.wizards.ContextHelpIDs;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Misc;

public class ParameterSetFieldEditor extends TableFieldEditor {

	protected Button editButton;

	public ParameterSetFieldEditor() {
		super();
	}

	public ParameterSetFieldEditor(String name, Composite parent) {
		super(name, "", new String[] { Messages.ParameterSetFieldEditor_0 }, new int[] { 150 }, parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		super.adjustForNumColumns(numColumns);
		((GridData) getLabelControl().getLayoutData()).exclude = true;
		getLabelControl().setVisible(false);
	}

	@Override
	protected String createList(String[][] items) {
		return ""; //$NON-NLS-1$
	}

	@Override
	protected String[][] parseString(String string) {
		return new String[0][0];
	}

	/**
	 * Notifies that the Add button has been pressed.
	 */
	@Override
	protected void addPressed() {
		setPresentsDefaultValue(false);
		ParameterSetDialog dialog = new ParameterSetDialog(UIUtils.getShell(), table);
		if (dialog.open() == Window.OK) {
			String[] newInputObject = new String[] { dialog.getPValue().getName() };
			if (newInputObject != null) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setText(newInputObject);
				tableItem.setData(ParameterSet.PARAMETER_SET, dialog.getPValue());
				selectionChanged();
				table.showColumn(table.getColumn(0));
				table.showItem(tableItem);
				table.showSelection();
			}
		}
	}

	protected void doStore() {
		TableItem[] items = getTable().getItems();
		String str = ""; //$NON-NLS-1$
		IPreferenceStore pstore = getPreferenceStore();
		for (String key : removed)
			pstore.setValue(key, pstore.getDefaultString(key));
		for (int i = 0; i < items.length; i++) {
			TableItem it = items[i];
			String pname = it.getText(0);
			str += pname + "\n"; //$NON-NLS-1$
			ParameterSet pset = (ParameterSet) it.getData(ParameterSet.PARAMETER_SET);
			ParameterSetProvider.storeParameterSet(pset, pstore);
		}
		try {
			pstore.setValue(ParameterSet.PARAMETER_SETS, Misc.encodeBase64String(str, FileUtils.LATIN1_ENCODING));
		} catch (IOException e) {
			e.printStackTrace();
		}
		removed.clear();
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad() {
		if (getTable() != null) {
			removed.clear();
			getTable().removeAll();
			String str = getPreferenceStore().getString(ParameterSet.PARAMETER_SETS);
			if (str != null) {
				try {
					str = Misc.decodeBase64String(str, FileUtils.LATIN1_ENCODING);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				String[] sets = str.split("\n"); //$NON-NLS-1$

				for (String key : sets) {
					TableItem tableItem = new TableItem(getTable(), SWT.NONE);
					tableItem.setText(new String[] { key });
				}
			}
			// Add an help listener to the table
			TableHelpListener.setTableHelp(getTable());
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault() {
		doLoad();
	}

	@Override
	protected boolean isFieldEditable(int col, int row) {
		return false;
	}

	@Override
	protected void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, ContextHelpIDs.PREFERENCES_PROPERTIES);
		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				editPressed();
			}
		});
	}

	private List<String> removed = new ArrayList<String>();

	@Override
	public void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addButton) {
					addPressed();
				} else if (widget == duplicateButton) {
					duplicatePressed();
				} else if (widget == removeButton) {
					int index = table.getSelectionIndex();
					if (index >= 0)
						removed.add(ParameterSet.PARAMETER_SET + "." + table.getItem(index).getText(0)); //$NON-NLS-1$
					removePressed();
				} else if (widget == editButton) {
					editPressed();
				} else if (widget == table) {
					selectionChanged();
				}
			}
		};
	}

	protected void duplicatePressed() {
		setPresentsDefaultValue(false);
		int index = table.getSelectionIndex();

		if (index >= 0) {
			TableItem[] selection = table.getSelection();
			Assert.isTrue(selection.length >= 1);
			for (int i = 0; i < selection.length;) {
				TableItem tableItem = selection[0];
				ParameterSet pvalue = (ParameterSet) tableItem.getData(ParameterSet.PARAMETER_SET);
				if (pvalue == null)
					pvalue = ParameterSetProvider.getParameterSet(tableItem.getText(0), getPreferenceStore());
				if (pvalue != null) {
					pvalue.setName("CopyOf_" + pvalue.getName()); //$NON-NLS-1$
					ParameterSetDialog dialog = new ParameterSetDialog(UIUtils.getShell(), -1, pvalue, table);
					if (dialog.open() == Window.OK) {
						ParameterSet newPValue = dialog.getPValue();
						tableItem = new TableItem(table, SWT.NONE);
						tableItem.setData(ParameterSet.PARAMETER_SET, newPValue);
						tableItem.setText(0, newPValue.getName());
					} else
						return;
				}
				break;
			}
		}
		table.setSelection(table.getItemCount());
		table.showItem(table.getItem(table.getItemCount() - 1));
		table.showSelection();
		table.showColumn(table.getColumn(0));
		selectionChanged();
	}

	private void editPressed() {
		int selIdx = table.getSelectionIndex();
		if (selIdx != -1) {
			TableItem item = table.getItem(selIdx);
			ParameterSet pvalue = (ParameterSet) item.getData(ParameterSet.PARAMETER_SET);
			if (pvalue == null)
				pvalue = ParameterSetProvider.getParameterSet(item.getText(0), getPreferenceStore());
			if (pvalue != null) {
				ParameterSetDialog dialog = new ParameterSetDialog(UIUtils.getShell(), selIdx, pvalue, table);
				if (dialog.open() == Window.OK) {
					ParameterSet newPValue = dialog.getPValue();
					item.setData(ParameterSet.PARAMETER_SET, newPValue);
					item.setText(0, newPValue.getName());
				}
			}
		}
	}

	protected void selectionChanged() {
		super.selectionChanged();
		int index = table.getSelectionIndex();
		int size = table.getItemCount();
		boolean isMultiSelection = table.getSelectionCount() > 1;
		if (editButton != null)
			editButton.setEnabled(!isMultiSelection && size >= 1 && index >= 0 && index < size && isEditable(index));
	}

	protected boolean isEditable(int row) {
		return true;
	}

	@Override
	protected void createButtons(Composite box) {
		addButton = createPushButton(box, Messages.common_add);
		duplicateButton = createPushButton(box, Messages.PropertyListFieldEditor_duplicateButton);
		removeButton = createPushButton(box, Messages.common_delete);
		editButton = createPushButton(box, Messages.common_edit);
	}

	@Override
	protected String[] getNewInputObject() {
		return null;
	}
}
