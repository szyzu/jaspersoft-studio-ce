/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.data.AFileDataAdapterComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DateNumberFormatWidget;
import com.jaspersoft.studio.data.messages.Messages;
import com.jaspersoft.studio.swt.events.ChangeEvent;
import com.jaspersoft.studio.swt.events.ChangeListener;
import com.jaspersoft.studio.swt.widgets.table.ListContentProvider;
import com.jaspersoft.studio.swt.widgets.table.ListOrderButtons;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.csv.CsvDataAdapter;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.eclipse.util.StringUtils;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

public class CSVDataAdapterComposite extends AFileDataAdapterComposite {

	private Button btnCheckQEMode;
	private TableViewer tableViewer;
	private Table table;
	private Button btnDelete;
	private Button btnCheckSkipFirstLine;
	private Group grpFieldSeparator;
	private Button btnRadioFieldComma;
	private Button btnRadioFieldTab;
	private Button btnRadioFieldNewLineUnix;
	private Button btnRadioFieldSpace;
	private Button btnRadioFieldSemicolon;
	private Button btnRadioFieldOther;
	private Text textFieldOther;
	private Group grpRowSeparator;
	private Button btnRadioRowComma;
	private Button btnRadioRowTab;
	private Button btnRadioRowNewLineUnix;
	private Button btnRadioRowNewLineWin;
	private Button btnRadioRowSpace;
	private Button btnRadioRowSemicolon;
	private Button btnRadioRowOther;
	private Text textRowOther;

	private DateNumberFormatWidget dnf;

	// The data model
	private java.util.List<String> rows;
	private Combo cEncoding;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CSVDataAdapterComposite(Composite parent, int style, JasperReportsContext jrContext) {

		/*
		 * UI ELEMENTS
		 */
		super(parent, style, jrContext);
		setLayout(new GridLayout(1, false));

		// data model init
		rows = new ArrayList<>();

		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gl_composite = new GridLayout(3, false);
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		createFileNameWidgets(composite);

		btnCheckQEMode = new Button(this, SWT.CHECK);
		btnCheckQEMode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnCheckQEMode.setText(Messages.CSVDataAdapterComposite_2);

		Composite composite_1 = new Composite(this, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		CTabFolder tabFolder = new CTabFolder(composite_1, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.setSelectionBackground(
				Display.getDefault().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmColumns = new CTabItem(tabFolder, SWT.NONE);
		tbtmColumns.setText(Messages.CSVDataAdapterComposite_3);
		tabFolder.setSelection(0);

		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmColumns.setControl(composite_2);
		composite_2.setLayout(new GridLayout(1, false));

		Group grpColumnNames = new Group(composite_2, SWT.NONE);
		grpColumnNames.setText(Messages.CSVDataAdapterComposite_4);
		grpColumnNames.setLayout(new GridLayout(1, false));
		grpColumnNames.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Button btnGetCSVColumnsName = new Button(grpColumnNames, SWT.NONE);
		btnGetCSVColumnsName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnGetCSVColumnsName.setText(Messages.CSVDataAdapterComposite_5);

		Composite composite_3 = new Composite(grpColumnNames, SWT.NONE);
		GridLayout gl_composite_3 = new GridLayout(2, false);
		gl_composite_3.marginWidth = 0;
		gl_composite_3.marginHeight = 0;
		composite_3.setLayout(gl_composite_3);
		GridData gdComposite3 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdComposite3.heightHint = 150;
		composite_3.setLayoutData(gdComposite3);
		composite_3.setBounds(0, 0, 64, 64);

		table = new Table(composite_3, SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 100;
		table.setLayoutData(gd);
		table.setHeaderVisible(true);

		TableLayout tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(100, false));
		table.setLayout(tlayout);

		tableViewer = new TableViewer(table);

		TableViewerColumn vcol = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn col = vcol.getColumn();
		col.setText(Messages.CSVDataAdapterComposite_6);

		tableViewer.setContentProvider(new ListContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
		vcol.setEditingSupport(new EditingSupport(tableViewer) {

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(tableViewer.getTable());
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected Object getValue(Object element) {
				return element;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void setValue(Object element, Object value) {
				int s = table.getSelectionIndex();
				List<String> lstr = (List<String>) tableViewer.getInput();
				if (s >= 0 && s < lstr.size())
					lstr.set(s, (String) value);
				tableViewer.refresh(true);
			}

		});

		tableViewer.setInput(rows);

		Composite composite_4 = new Composite(composite_3, SWT.NONE);
		GridLayout gl_composite_4 = new GridLayout(1, false);
		gl_composite_4.marginWidth = 0;
		gl_composite_4.marginHeight = 0;
		composite_4.setLayout(gl_composite_4);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

		Button btnAdd = new Button(composite_4, SWT.NONE);
		GridData gd_btnAdd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnAdd.widthHint = 100;
		btnAdd.setLayoutData(gd_btnAdd);
		btnAdd.setText(Messages.CSVDataAdapterComposite_7);

		btnDelete = new Button(composite_4, SWT.NONE);
		GridData gd_btnDelete = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnDelete.widthHint = 100;
		btnDelete.setLayoutData(gd_btnDelete);
		btnDelete.setText(Messages.CSVDataAdapterComposite_8);
		btnDelete.setEnabled(false);

		ListOrderButtons lb = new ListOrderButtons();
		lb.createOrderButtons(composite_4, tableViewer);
		lb.addChangeListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event) {
				pchangesuport.firePropertyChange("dirty", false, true);
			}
		});

		Group grpOther = new Group(composite_2, SWT.NONE);
		grpOther.setText(Messages.CSVDataAdapterComposite_9);
		grpOther.setLayout(new GridLayout(3, false));
		grpOther.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		dnf = new DateNumberFormatWidget(grpOther);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		dnf.setLayoutData(gd);

		btnCheckSkipFirstLine = new Button(grpOther, SWT.CHECK);
		btnCheckSkipFirstLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		btnCheckSkipFirstLine.setText(Messages.CSVDataAdapterComposite_14);

		Label lbl = new Label(grpOther, SWT.NONE);
		lbl.setText(Messages.CSVDataAdapterComposite_37);
		lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		cEncoding = new Combo(grpOther, SWT.SINGLE | SWT.BORDER);
		gd = new GridData();
		gd.horizontalSpan = 2;
		cEncoding.setItems(StringUtils.getEncodings());
		cEncoding.setLayoutData(gd);

		CTabItem tbtmSeparators = new CTabItem(tabFolder, SWT.NONE);
		tbtmSeparators.setText(Messages.CSVDataAdapterComposite_15);

		Composite composite_5 = new Composite(tabFolder, SWT.NONE);
		tbtmSeparators.setControl(composite_5);
		composite_5.setLayout(new GridLayout(1, false));

		grpFieldSeparator = new Group(composite_5, SWT.NONE);
		grpFieldSeparator.setText(Messages.CSVDataAdapterComposite_16);
		grpFieldSeparator.setLayout(new GridLayout(3, true));
		grpFieldSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpFieldSeparator.setBounds(0, 0, 70, 82);

		btnRadioFieldComma = new Button(grpFieldSeparator, SWT.RADIO);
		btnRadioFieldComma.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioFieldComma.setText(Messages.CSVDataAdapterComposite_17);

		btnRadioFieldTab = new Button(grpFieldSeparator, SWT.RADIO);
		btnRadioFieldTab.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioFieldTab.setText(Messages.CSVDataAdapterComposite_18);

		btnRadioFieldNewLineUnix = new Button(grpFieldSeparator, SWT.RADIO);
		btnRadioFieldNewLineUnix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioFieldNewLineUnix.setText(Messages.CSVDataAdapterComposite_19);

		btnRadioFieldSpace = new Button(grpFieldSeparator, SWT.RADIO);
		btnRadioFieldSpace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioFieldSpace.setText(Messages.CSVDataAdapterComposite_20);

		btnRadioFieldSemicolon = new Button(grpFieldSeparator, SWT.RADIO);
		btnRadioFieldSemicolon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioFieldSemicolon.setText(Messages.CSVDataAdapterComposite_21);

		Composite fieldComposite = new Composite(grpFieldSeparator, SWT.NONE);
		fieldComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite_6 = new GridLayout(2, false);
		gl_composite_6.marginWidth = 0;
		gl_composite_6.marginHeight = 0;
		fieldComposite.setLayout(gl_composite_6);
		btnRadioFieldOther = new Button(fieldComposite, SWT.RADIO);
		btnRadioFieldOther.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRadioFieldOther.setText(Messages.CSVDataAdapterComposite_22);

		textFieldOther = new Text(fieldComposite, SWT.BORDER);
		textFieldOther.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textFieldOther.setEnabled(false);
		textFieldOther.setTextLimit(1);

		grpRowSeparator = new Group(composite_5, SWT.NONE);
		grpRowSeparator.setText(Messages.CSVDataAdapterComposite_23);
		grpRowSeparator.setLayout(new GridLayout(3, true));
		grpRowSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnRadioRowComma = new Button(grpRowSeparator, SWT.RADIO);
		btnRadioRowComma.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioRowComma.setText(Messages.CSVDataAdapterComposite_24);

		btnRadioRowTab = new Button(grpRowSeparator, SWT.RADIO);
		btnRadioRowTab.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioRowTab.setText(Messages.CSVDataAdapterComposite_25);

		btnRadioRowNewLineUnix = new Button(grpRowSeparator, SWT.RADIO);
		btnRadioRowNewLineUnix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioRowNewLineUnix.setText(Messages.CSVDataAdapterComposite_26);

		btnRadioRowNewLineWin = new Button(grpRowSeparator, SWT.RADIO);
		btnRadioRowNewLineWin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioRowNewLineWin.setText(Messages.CSVDataAdapterComposite_27);

		btnRadioRowSpace = new Button(grpRowSeparator, SWT.RADIO);
		btnRadioRowSpace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioRowSpace.setText(Messages.CSVDataAdapterComposite_28);

		btnRadioRowSemicolon = new Button(grpRowSeparator, SWT.RADIO);
		btnRadioRowSemicolon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRadioRowSemicolon.setText(Messages.CSVDataAdapterComposite_29);

		Composite rowComposite = new Composite(grpRowSeparator, SWT.NONE);
		rowComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite_7 = new GridLayout(2, false);
		gl_composite_7.marginWidth = 0;
		gl_composite_7.marginHeight = 0;
		rowComposite.setLayout(gl_composite_7);
		btnRadioRowOther = new Button(rowComposite, SWT.RADIO);
		btnRadioRowOther.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRadioRowOther.setText(Messages.CSVDataAdapterComposite_30);

		textRowOther = new Text(rowComposite, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 100;
		textRowOther.setLayoutData(gd);
		textRowOther.setEnabled(false);
		new Label(grpRowSeparator, SWT.NONE);
		new Label(grpRowSeparator, SWT.NONE);

		Group grpSpecialCharacters = new Group(composite_5, SWT.NONE);
		grpSpecialCharacters.setText(Messages.CSVDataAdapterComposite_31);
		grpSpecialCharacters.setLayout(new GridLayout(4, false));
		grpSpecialCharacters.setLayoutData(new GridData(GridData.FILL_BOTH));

		lbl = new Label(grpSpecialCharacters, SWT.NONE | SWT.BOLD);
		lbl.setText("\\n"); //$NON-NLS-1$
		com.jaspersoft.studio.utils.UIUtil.setBold(lbl);

		new Label(grpSpecialCharacters, SWT.NONE).setText(Messages.CSVDataAdapterComposite_32);

		lbl = new Label(grpSpecialCharacters, SWT.NONE | SWT.BOLD);
		lbl.setText("\\r"); //$NON-NLS-1$
		com.jaspersoft.studio.utils.UIUtil.setBold(lbl);

		Label text_b = new Label(grpSpecialCharacters, SWT.NONE);
		text_b.setText(Messages.CSVDataAdapterComposite_33);

		lbl = new Label(grpSpecialCharacters, SWT.NONE | SWT.BOLD);
		lbl.setText("\\r\\n"); //$NON-NLS-1$
		com.jaspersoft.studio.utils.UIUtil.setBold(lbl);

		Label text_c = new Label(grpSpecialCharacters, SWT.NONE);
		text_c.setText(Messages.CSVDataAdapterComposite_34);
		gd = new GridData();
		gd.widthHint = 250;
		text_c.setLayoutData(gd);

		lbl = new Label(grpSpecialCharacters, SWT.NONE | SWT.BOLD);
		lbl.setText("\\t"); //$NON-NLS-1$
		com.jaspersoft.studio.utils.UIUtil.setBold(lbl);

		Label text_d = new Label(grpSpecialCharacters, SWT.NONE);
		text_d.setText(Messages.CSVDataAdapterComposite_35);

		lbl = new Label(grpSpecialCharacters, SWT.NONE | SWT.BOLD);
		lbl.setText("\\\\"); //$NON-NLS-1$
		com.jaspersoft.studio.utils.UIUtil.setBold(lbl);

		Label text_e = new Label(grpSpecialCharacters, SWT.NONE);
		text_e.setText(Messages.CSVDataAdapterComposite_36);

		new Label(grpSpecialCharacters, SWT.NONE);

		// get CSV file columns
		btnGetCSVColumnsName.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getCSVColumns();
				} catch (IOException e1) {
					UIUtils.showError(e1);
				} catch (Exception e2) {
					UIUtils.showError(e2);
				}
			}
		});

		// add an entry and set selection on it
		btnAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				rows.add(createDataModelEntry());

				// tableViewer.addPostSelectionChangedListener can't cover
				// the first row added, so we need to manually set delete
				// button enabled for this case.
				if (rows.size() == 1) {
					btnDelete.setEnabled(true);
				}

				tableViewer.refresh();
				setTableSelection(-1);
				pchangesuport.firePropertyChange("dirty", false, true);
			}
		});

		// delete selected entries and set selection on last table item
		btnDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeEntries();
			}
		});

		// keys listener
		table.addKeyListener(new KeyListener() {

			public void keyReleased(KeyEvent e) {
				// nothing
			}

			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL)
					removeEntries();
			}
		});

		// When no table items,
		// turns disabled the delete button
		// and set unchecked the skip first line button
		tableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (rows.size() <= 0) {
					btnDelete.setEnabled(false);
					btnCheckSkipFirstLine.setSelection(false);
				} else {
					btnDelete.setEnabled(true);
				}
			}
		});

		Button[] buttons = new Button[] { btnRadioFieldComma, btnRadioFieldNewLineUnix, btnRadioFieldSemicolon,
				btnRadioFieldSpace, btnRadioFieldTab, btnRadioFieldOther, btnRadioRowComma, btnRadioRowNewLineUnix,
				btnRadioRowNewLineWin, btnRadioRowSemicolon, btnRadioRowSpace, btnRadioRowTab, btnRadioRowOther };
		for (Button button : buttons) {

			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					enableOtherButton(((Button) e.widget));
				}
			});
		}
	}

	@Override
	protected void bindWidgets(DataAdapter dataAdapter) {
		doBindFileNameWidget(dataAdapter);
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnCheckQEMode),
				PojoProperties.value("queryExecuterMode").observe(dataAdapter)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnCheckSkipFirstLine),
				PojoProperties.value("useFirstRowAsHeader").observe(dataAdapter)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.text().observe(cEncoding),
				PojoProperties.value("encoding").observe(dataAdapter)); //$NON-NLS-1$
		
		CsvDataAdapter csvDataAdapter = (CsvDataAdapter) dataAdapter;
		List<String> listColumnNames = csvDataAdapter.getColumnNames();
		if (listColumnNames != null && listColumnNames.size() > 0) {
			for (String str : listColumnNames) {
				rows.add(str);
			}
			tableViewer.refresh();
			setTableSelection(-1);
			btnDelete.setEnabled(true);
		}

		dnf.bindWidgets(csvDataAdapter, bindingContext, csvDataAdapter.getLocale(), csvDataAdapter.getTimeZone());

		Proxy proxy = new Proxy(csvDataAdapter);
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioFieldComma),
				PojoProperties.value("fieldComma").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioFieldTab),
				PojoProperties.value("fieldTab").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioFieldSpace),
				PojoProperties.value("fieldSpace").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioFieldSemicolon),
				PojoProperties.value("fieldSemicolon").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioFieldNewLineUnix),
				PojoProperties.value("fieldNewLineUnix").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(textFieldOther),
				PojoProperties.value("fieldDelimiter").observe(proxy)); //$NON-NLS-1$

		String fieldDelimiter = csvDataAdapter.getFieldDelimiter();
		if (fieldDelimiter != null) { // should never be null
			if (",".equals(fieldDelimiter)) //$NON-NLS-1$
				btnRadioFieldComma.setSelection(true);
			else if ("\t".equals(fieldDelimiter)) //$NON-NLS-1$
				btnRadioFieldTab.setSelection(true);
			else if (" ".equals(fieldDelimiter)) //$NON-NLS-1$
				btnRadioFieldSpace.setSelection(true);
			else if (";".equals(fieldDelimiter)) //$NON-NLS-1$
				btnRadioFieldSemicolon.setSelection(true);
			else if ("\n".equals(fieldDelimiter)) //$NON-NLS-1$
				btnRadioFieldNewLineUnix.setSelection(true);
			else {
				btnRadioFieldOther.setSelection(true);
				textFieldOther.setText(Misc.addSlashesString(fieldDelimiter));
				textFieldOther.setEnabled(true);
			}
		}

		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioRowComma),
				PojoProperties.value("rowComma").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioRowTab),
				PojoProperties.value("rowTab").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioRowSpace),
				PojoProperties.value("rowSpace").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioRowSemicolon),
				PojoProperties.value("rowSemicolon").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioRowNewLineUnix),
				PojoProperties.value("rowNewLineUnix").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.buttonSelection().observe(btnRadioRowNewLineWin),
				PojoProperties.value("rowNewLineWin").observe(proxy)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(textRowOther),
				PojoProperties.value("recordDelimiter").observe(proxy)); //$NON-NLS-1$

		String recordDelimitier = csvDataAdapter.getRecordDelimiter();
		if (recordDelimitier != null) { // should never be null
			if (",".equals(recordDelimitier)) //$NON-NLS-1$
				btnRadioRowComma.setSelection(true);
			else if ("\t".equals(recordDelimitier)) //$NON-NLS-1$
				btnRadioRowTab.setSelection(true);
			else if (" ".equals(recordDelimitier)) //$NON-NLS-1$
				btnRadioRowSpace.setSelection(true);
			else if (";".equals(recordDelimitier)) //$NON-NLS-1$
				btnRadioRowSemicolon.setSelection(true);
			else if ("\n".equals(recordDelimitier)) //$NON-NLS-1$
				btnRadioRowNewLineUnix.setSelection(true);
			else if ("\r\n".equals(recordDelimitier)) //$NON-NLS-1$
				btnRadioRowNewLineWin.setSelection(true);
			else {
				btnRadioRowOther.setSelection(true);
				textRowOther.setText(Misc.addSlashesString(recordDelimitier));
				textRowOther.setEnabled(true);
			}
		}
	}

	class Proxy {
		private CsvDataAdapter da;

		public Proxy(CsvDataAdapter da) {
			this.da = da;
		}

		public boolean isFieldComma() {
			return da.getFieldDelimiter() != null && da.getFieldDelimiter().equals(","); //$NON-NLS-1$
		}

		public void setFieldComma(boolean b) {
			da.setFieldDelimiter(","); //$NON-NLS-1$
		}

		public boolean isFieldTab() {
			return da.getFieldDelimiter() != null && da.getFieldDelimiter().equals("\t"); //$NON-NLS-1$
		}

		public void setFieldTab(boolean b) {
			da.setFieldDelimiter("\t"); //$NON-NLS-1$
		}

		public boolean isFieldSpace() {
			return da.getFieldDelimiter() != null && da.getFieldDelimiter().equals(" "); //$NON-NLS-1$
		}

		public void setFieldSpace(boolean b) {
			da.setFieldDelimiter(" "); //$NON-NLS-1$
		}

		public boolean isFieldSemicolon() {
			return da.getFieldDelimiter() != null && da.getFieldDelimiter().equals(";"); //$NON-NLS-1$
		}

		public void setFieldSemicolon(boolean b) {
			da.setFieldDelimiter(";"); //$NON-NLS-1$
		}

		public boolean isFieldNewLineUnix() {
			return da.getFieldDelimiter() != null && da.getFieldDelimiter().equals("\n"); //$NON-NLS-1$
		}

		public void setFieldNewLineUnix(boolean b) {
			da.setFieldDelimiter("\n"); //$NON-NLS-1$
		}

		public boolean isRowComma() {
			return da.getRecordDelimiter() != null && da.getRecordDelimiter().equals(","); //$NON-NLS-1$
		}

		public void setRowComma(boolean b) {
			da.setRecordDelimiter(","); //$NON-NLS-1$
		}

		public boolean isRowTab() {
			return da.getRecordDelimiter() != null && da.getRecordDelimiter().equals("\t"); //$NON-NLS-1$
		}

		public void setRowTab(boolean b) {
			da.setRecordDelimiter("\t"); //$NON-NLS-1$
		}

		public boolean isRowSpace() {
			return da.getRecordDelimiter() != null && da.getRecordDelimiter().equals(" "); //$NON-NLS-1$
		}

		public void setRowSpace(boolean b) {
			da.setRecordDelimiter(" "); //$NON-NLS-1$
		}

		public boolean isRowSemicolon() {
			return da.getRecordDelimiter() != null && da.getRecordDelimiter().equals(";"); //$NON-NLS-1$
		}

		public void setRowSemicolon(boolean b) {
			da.setRecordDelimiter(";"); //$NON-NLS-1$
		}

		public boolean isRowNewLineUnix() {
			return da.getRecordDelimiter() != null && da.getRecordDelimiter().equals("\n"); //$NON-NLS-1$
		}

		public void setRowNewLineUnix(boolean b) {
			da.setRecordDelimiter("\n"); //$NON-NLS-1$
		}

		public boolean isRowNewLineWin() {
			return da.getRecordDelimiter() != null && da.getRecordDelimiter().equals("\r\n"); //$NON-NLS-1$
		}

		public void setRowNewLineWin(boolean b) {
			da.setRecordDelimiter("\r\n"); //$NON-NLS-1$
		}

	}

	/**
	 * Get the CSV DataAdapter with the values from the UI elements.
	 * 
	 * @return
	 */
	public DataAdapterDescriptor getDataAdapter() {
		if (dataAdapterDesc == null)
			dataAdapterDesc = new CSVDataAdapterDescriptor();

		CsvDataAdapter csvDataAdapter = (CsvDataAdapter) dataAdapterDesc.getDataAdapter();

		csvDataAdapter.setQueryExecuterMode(btnCheckQEMode.getSelection());
		csvDataAdapter.setUseFirstRowAsHeader(btnCheckSkipFirstLine.getSelection());
		csvDataAdapter.setColumnNames(rows);

		csvDataAdapter.setDatePattern(dnf.getTextDatePattern());
		csvDataAdapter.setNumberPattern(dnf.getTextNumberPattern());
		csvDataAdapter.setLocale(dnf.getLocale());
		csvDataAdapter.setTimeZone(dnf.getTimeZone());

		if (btnRadioFieldComma.getSelection())
			csvDataAdapter.setFieldDelimiter(","); //$NON-NLS-1$
		else if (btnRadioFieldTab.getSelection())
			csvDataAdapter.setFieldDelimiter("\t"); //$NON-NLS-1$
		else if (btnRadioFieldSpace.getSelection())
			csvDataAdapter.setFieldDelimiter(" "); //$NON-NLS-1$
		else if (btnRadioFieldSemicolon.getSelection())
			csvDataAdapter.setFieldDelimiter(";"); //$NON-NLS-1$
		else if (btnRadioFieldNewLineUnix.getSelection())
			csvDataAdapter.setFieldDelimiter("\n"); //$NON-NLS-1$
		else if (btnRadioFieldOther.getSelection())
			csvDataAdapter.setFieldDelimiter(Misc.removeSlashesString(textFieldOther.getText() + " ")); //$NON-NLS-1$
		if (csvDataAdapter.getFieldDelimiter() == null || csvDataAdapter.getFieldDelimiter().isEmpty())
			csvDataAdapter.setFieldDelimiter(";"); //$NON-NLS-1$

		if (btnRadioRowComma.getSelection())
			csvDataAdapter.setRecordDelimiter(","); //$NON-NLS-1$
		else if (btnRadioRowTab.getSelection())
			csvDataAdapter.setRecordDelimiter("\t"); //$NON-NLS-1$
		else if (btnRadioRowSpace.getSelection())
			csvDataAdapter.setRecordDelimiter(" "); //$NON-NLS-1$
		else if (btnRadioRowSemicolon.getSelection())
			csvDataAdapter.setRecordDelimiter(";"); //$NON-NLS-1$
		else if (btnRadioRowNewLineUnix.getSelection())
			csvDataAdapter.setRecordDelimiter("\n"); //$NON-NLS-1$
		else if (btnRadioRowNewLineWin.getSelection())
			csvDataAdapter.setRecordDelimiter("\r\n"); //$NON-NLS-1$
		else if (btnRadioRowOther.getSelection())
			csvDataAdapter.setRecordDelimiter(Misc.removeSlashesString(textRowOther.getText()));

		return dataAdapterDesc;
	}

	/**
	 * This creates and returns a new entry for the data model
	 * 
	 * @return StringBuffer
	 */
	private String createDataModelEntry() {

		int i = 0;
		StringBuffer column = new StringBuffer("COLUMN_" + i); //$NON-NLS-1$

		while (!isColumnValid(column.toString())) {
			i++;
			column.setLength(0);
			column.append("COLUMN_" + i); //$NON-NLS-1$
		}

		return column.toString();
	}

	/**
	 * Removes selected entries from the data model
	 */
	private void removeEntries() {
		int[] indices = table.getSelectionIndices();
		if (indices.length > 0) {
			List<String> toDel = new ArrayList<>();
			for (int i = 0; i < indices.length; i++)
				toDel.add(rows.get(indices[i]));
			rows.removeAll(toDel);
			tableViewer.refresh();
			setTableSelection(indices[0]);
			pchangesuport.firePropertyChange("dirty", false, true);
		}
	}

	/**
	 * This set selection to the table's item represented by the given index. Any
	 * index out of table's range will select the last item.
	 * 
	 * @param index
	 */
	private void setTableSelection(int index) {

		if (rows != null && rows.size() > 0) {

			if (index == 0) {
				table.setSelection(index);
			} else if ((0 < index) && (index < rows.size() - 1)) {
				table.setSelection(index - 1);
			} else {
				table.setSelection(rows.size() - 1);
			}
		}
	}

	/**
	 * Because the radio button "Other" is not in the same component as the other
	 * radio buttons, we need to manually make the switch.
	 * 
	 * @param Button
	 */
	private void enableOtherButton(Button button) {

		if (btnRadioFieldOther.equals(button)) {

			textFieldOther.setEnabled(true);

			btnRadioFieldComma.setSelection(false);
			btnRadioFieldNewLineUnix.setSelection(false);
			btnRadioFieldSemicolon.setSelection(false);
			btnRadioFieldSpace.setSelection(false);
			btnRadioFieldTab.setSelection(false);

		} else if (btnRadioRowOther.equals(button)) {

			textRowOther.setEnabled(true);

			btnRadioRowComma.setSelection(false);
			btnRadioRowNewLineUnix.setSelection(false);
			btnRadioRowNewLineWin.setSelection(false);
			btnRadioRowSemicolon.setSelection(false);
			btnRadioRowSpace.setSelection(false);
			btnRadioRowTab.setSelection(false);

		} else {

			if (grpFieldSeparator.equals(button.getParent())) {
				btnRadioFieldOther.setSelection(false);
				textFieldOther.setEnabled(false);

			} else if (grpRowSeparator.equals(button.getParent())) {
				btnRadioRowOther.setSelection(false);
				textRowOther.setEnabled(false);
			}
		}
	}

	@Override
	protected void fireFileChanged(boolean showWarning) {
		try {
			super.fireFileChanged(showWarning);
			if (showWarning) {
				if (UIUtils.showConfirmation(Messages.CSVDataAdapterComposite_0, Messages.CSVDataAdapterComposite_1))
					getCSVColumns();
			} else
				getCSVColumns();
		} catch (IOException e) {
			UIUtils.showError(e);
		} catch (Exception e) {
			UIUtils.showError(e);
		}
	}

	/**
	 * This method will populate the data model with the CSV columns This also
	 * checks the button "Skip the first line " and enables the delete button
	 * 
	 * @throws IOException
	 *             , Exception
	 */
	private void getCSVColumns() throws IOException, Exception {
		if (Misc.isNullOrEmpty(textFileName.getText()))
			return;
		JRCsvDataSource ds = new JRCsvDataSource(getJrContext(), textFileName.getText());
		ds.setUseFirstRowAsHeader(true);

		if (btnRadioFieldComma.getSelection())
			ds.setFieldDelimiter(',');
		else if (btnRadioFieldTab.getSelection())
			ds.setFieldDelimiter('\t');
		else if (btnRadioFieldSpace.getSelection())
			ds.setFieldDelimiter(' ');
		else if (btnRadioFieldSemicolon.getSelection())
			ds.setFieldDelimiter(';');
		else if (btnRadioFieldNewLineUnix.getSelection())
			ds.setFieldDelimiter('\n');
		else if (btnRadioFieldOther.getSelection())
			ds.setFieldDelimiter(Misc.removeSlashesString(textFieldOther.getText() + " ").charAt(0)); //$NON-NLS-1$
		else if (ds.getFieldDelimiter() == ' ')
			ds.setFieldDelimiter(';');

		if (btnRadioRowComma.getSelection())
			ds.setRecordDelimiter(","); //$NON-NLS-1$
		else if (btnRadioRowTab.getSelection())
			ds.setRecordDelimiter("\t"); //$NON-NLS-1$
		else if (btnRadioRowSpace.getSelection())
			ds.setRecordDelimiter(" "); //$NON-NLS-1$
		else if (btnRadioRowSemicolon.getSelection())
			ds.setRecordDelimiter(";"); //$NON-NLS-1$
		else if (btnRadioRowNewLineUnix.getSelection())
			ds.setRecordDelimiter("\n"); //$NON-NLS-1$
		else if (btnRadioRowNewLineWin.getSelection())
			ds.setRecordDelimiter("\r\n"); //$NON-NLS-1$
		else if (btnRadioRowOther.getSelection())
			ds.setRecordDelimiter(Misc.removeSlashesString(textRowOther.getText()));
		else if (ds.getRecordDelimiter().equals("")) //$NON-NLS-1$
			ds.setRecordDelimiter("\n"); //$NON-NLS-1$

		// empty data model
		rows.clear();
		ds.next();
		Map<String, Integer> names = ds.getColumnNames();
		if (names != null) {
			SortedMap<Integer, String> map = new TreeMap<>();
			for (Map.Entry<String, Integer> entry : names.entrySet())
				map.put(entry.getValue(), entry.getKey());
			for (Map.Entry<Integer, String> entry : map.entrySet())
				rows.add(entry.getValue());
		}

		tableViewer.refresh();
		setTableSelection(-1);
		btnDelete.setEnabled(true);
		pchangesuport.firePropertyChange("dirty", false, true);
		ds.close();
	}

	/**
	 * Check the validity of the column name. It is valid only if it is not null,
	 * not empty and not already existed.
	 * 
	 * @param string
	 * @return true or false
	 */
	private boolean isColumnValid(String column) {

		if (column == null || "".equals(column)) //$NON-NLS-1$
			return false;

		for (String row : rows) {
			if (row.equals(column)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getHelpContextId() {
		return PREFIX.concat("adapter_csv"); //$NON-NLS-1$
	}

	@Override
	protected String[] getFileExtensions() {
		return new String[] { "*.csv", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
