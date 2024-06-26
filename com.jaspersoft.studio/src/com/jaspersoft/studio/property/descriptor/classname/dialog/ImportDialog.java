/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.classname.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.editor.table.TableLabelProvider;
import com.jaspersoft.studio.swt.widgets.table.DeleteButton;
import com.jaspersoft.studio.swt.widgets.table.INewElement;
import com.jaspersoft.studio.swt.widgets.table.ListContentProvider;
import com.jaspersoft.studio.swt.widgets.table.NewButton;

import net.sf.jasperreports.eclipse.ui.util.PersistentLocationDialog;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.StringUtils;

public class ImportDialog extends PersistentLocationDialog {
	private String value;
	private TableViewer tableViewer;
	private List<String> imports;
	private NotEmptyValidator importValidator = new NotEmptyValidator();

	public ImportDialog(Shell parentShell, String value) {
		super(parentShell);
		this.value = value;
	}

	public String getImports() {
		value = ""; //$NON-NLS-1$
		for (String str : imports) {
			value += str + ";"; //$NON-NLS-1$
		}

		return value;
	}

	/**
	 * Configure Shell attributes like setText
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.ImportDialog_2);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// Control control = super.createDialogArea(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		buildTable(composite);

		Composite bGroup = new Composite(composite, SWT.NONE);
		bGroup.setLayout(new GridLayout(1, false));
		bGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		bGroup.setBackground(parent.getBackground());

		NewButton bnew = new NewButton();
		bnew.createNewButtons(bGroup, tableViewer, new INewElement() {

			public Object newElement(List<?> input, int pos) {
				String[] results = null;
				SelectionDialog dialog = JavaUI.createPackageDialog(getShell(), new ProgressMonitorDialog(getShell()),
						SearchEngine.createWorkspaceScope(), true, true, null);
				dialog.setTitle(Messages.ImportDialog_3);
				if (dialog.open() == Dialog.OK) {
					Object[] objects = dialog.getResult();
					if (objects != null && objects.length > 0) {
						results = new String[objects.length];
						for (int i = 0; i < objects.length; i++) {
							JavaElement jpf = (JavaElement) objects[i];
							results[i] = jpf.getElementName() + ".*"; //$NON-NLS-1$
						}
					}
				}
				return results;
			}
		});
		bnew.setButtonText(Messages.ImportDialog_4);

		bnew = new NewButton();
		bnew.createNewButtons(bGroup, tableViewer, new INewElement() {

			public Object newElement(List<?> input, int pos) {
				try {
					String[] results = null;
					IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
					SelectionDialog dialog = JavaUI.createTypeDialog(getShell(), new ProgressMonitorDialog(getShell()),
							searchScope, IJavaElementSearchConstants.CONSIDER_ALL_TYPES, true);
					dialog.setTitle(Messages.ClassTypeCellEditor_open_type);
					dialog.setMessage(Messages.ClassTypeCellEditor_dialog_message);
					if (dialog.open() == Window.OK) {
						Object[] objects = dialog.getResult();
						if (objects != null && objects.length > 0) {
							results = new String[objects.length];
							for (int i = 0; i < objects.length; i++) {
								if (objects[i] instanceof IType) {
									IType bt = (IType) objects[i];
									results[i] = bt.getFullyQualifiedName('.');
								}
							}
						}
						return results;
					}
				} catch (JavaModelException e) {
					UIUtils.showError(e);
				}
				return null;
			}
		});
		bnew.setButtonText(Messages.ImportDialog_5);
		
		NewButton customBtn = new NewButton();
		customBtn.createNewButtons(bGroup, tableViewer, new INewElement() {
			@Override
			public Object newElement(List<?> input, int pos) {
				InputDialog id = new InputDialog(getShell(), 
						Messages.ImportDialog_AddCustomTitle, 
						Messages.ImportDialog_AddCustomMsg, null, importValidator);
				if(id.open() == Window.OK) {
					return new String[] { id.getValue() };
				}
				else { 
					return null;
				}
			}
		});
		customBtn.setButtonText(Messages.ImportDialog_AddCustomBtn);

		DeleteButton bdel = new DeleteButton();
		bdel.createDeleteButton(bGroup, tableViewer);

		imports = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(value, ";"); //$NON-NLS-1$
		while (st.hasMoreTokens())
			imports.add(st.nextToken());

		tableViewer.setInput(imports);
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TableItem[] selection = tableViewer.getTable().getSelection();
				TableItem selectedItem = null;
				if(selection!=null && selection.length==1) {
					selectedItem = selection[0];
				}
				if(selectedItem !=null) {
					int selectionIndex = tableViewer.getTable().getSelectionIndex();
					InputDialog id = new InputDialog(getShell(), 
							Messages.ImportDialog_EditCustomTitle, 
							Messages.ImportDialog_EditCustomMsg, selectedItem.getText(), importValidator);
	
					if(id.open() == Window.OK) {
						imports.remove(selectionIndex);
						imports.add(selectionIndex, id.getValue());
						tableViewer.setInput(imports);
					}
				}
			}
		});

		return composite;
	}
	
	private class NotEmptyValidator implements IInputValidator {

		@Override
		public String isValid(String newText) {
			if(StringUtils.isNullOrEmpty(newText)) {
				return Messages.ImportDialog_EmptyImportErrorMsg;
			}
			return null;
		}
	}

	private void buildTable(Composite composite) {
		Table table = new Table(composite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumHeight = 400;
		gd.minimumWidth = 400;
		table.setLayoutData(gd);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		tableViewer = new TableViewer(table);
		attachContentProvider(tableViewer);
		attachLabelProvider(tableViewer);

		TableLayout tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(100));
		table.setLayout(tlayout);

		TableColumn[] column = new TableColumn[1];
		column[0] = new TableColumn(table, SWT.NONE);

		for (int i = 0, n = column.length; i < n; i++)
			column[i].pack();

	}

	private void attachLabelProvider(TableViewer viewer) {
		viewer.setLabelProvider(new TableLabelProvider());
	}

	private void attachContentProvider(TableViewer viewer) {
		viewer.setContentProvider(new ListContentProvider());
	}
}
