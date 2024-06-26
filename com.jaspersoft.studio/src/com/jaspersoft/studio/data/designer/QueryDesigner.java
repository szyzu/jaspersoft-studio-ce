/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.designer;

import java.util.regex.Matcher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.fonts.utils.FontUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.wizards.ContextHelpIDs;

import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;

public class QueryDesigner extends AQueryDesigner {
	private boolean refresh = false;

	public final class QueryListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			doSourceTextChanged();
		}

	}

	protected void doSourceTextChanged() {
		if (!refresh) {
			refresh = true;
			if (jDataset != null && jDataset.getQuery() != null)
				((JRDesignQuery) jDataset.getQuery()).setText(Misc.nvl(getQueryFromWidget()));
			refresh = false;
		}
	}

	protected StyledText control;
	private Composite tbCompo;
	private Button btn;
	protected UndoRedoImpl undoHandlrer;

	public QueryDesigner() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.IQueryDesigner#getControl()
	 */
	public Control getControl() {
		return control;
	}

	public Control getToolbarControl() {
		return tbCompo;
	}

	@Override
	protected void parameterNameChanged(String oldValue, String newValue) {
		String q = control.getText();
		q = q.replaceAll("\\$P\\{" + oldValue + "\\}", Matcher.quoteReplacement("$P{" + newValue + "}"));
		updateQueryText(q);
	}

	private final static String LineDelimiter = System.getProperty("line.separator");

	protected String getNewLineDelimiter() {
		return LineDelimiter;
	}

	public Control createControl(Composite parent) {
		control = new StyledText(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER) {
			@Override
			public String getLineDelimiter() {
				return getNewLineDelimiter();
			}
		};

		control.addModifyListener(new QueryListener());
		setupSourceEditorFont();
		undoHandlrer = new UndoRedoImpl(control);
		return control;
	}

	protected void setupSourceEditorFont() {
		control.setFont(FontUtils.getEditorsFont(jConfig));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.IQueryDesigner#setQuery(java.lang.String)
	 */
	public void setQuery(JasperDesign jDesign, JRDataset jDataset, JasperReportsConfiguration jConfig) {
		super.setQuery(jDesign, jDataset, jConfig);
		refresh = true;
		updateQueryText(jDataset.getQuery().getText());
		if (undoHandlrer != null)
			undoHandlrer.clearStack();
		refresh = false;
	}

	protected void updateQueryText(String txt) {
		control.setText(txt);
	}

	protected String getQueryFromWidget() {
		return control.getText();
	}

	public void dispose() {
	}

	public Control createToolbar(Composite parent) {
		if (parent != null) {
			tbCompo = new Composite(parent, SWT.NONE);
			tbCompo.setBackgroundMode(SWT.INHERIT_FORCE);
			GridLayout layout = new GridLayout(1, false);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			tbCompo.setLayout(layout);
			tbCompo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			btn = new Button(tbCompo, SWT.PUSH);
			btn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
			btn.setText(Messages.QueryDesigner_readFieldsButton);
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					container.doGetFields();
				}

			});
			setFieldProviderEnabled(true);
		}
		return tbCompo;
	}

	public void setDataAdapter(DataAdapterDescriptor da) {
		boolean enable = (da instanceof IFieldsProvider
				&& ((IFieldsProvider) da).supportsGetFieldsOperation(container.getjConfig(), getjDataset()));
		setFieldProviderEnabled(enable);
	}

	protected void setFieldProviderEnabled(boolean enable) {
		if (btn != null)
			btn.setEnabled(enable);
	}

	@Override
	public String getContextHelpId() {
		return ContextHelpIDs.WIZARD_QUERY_DIALOG;
	}
}
