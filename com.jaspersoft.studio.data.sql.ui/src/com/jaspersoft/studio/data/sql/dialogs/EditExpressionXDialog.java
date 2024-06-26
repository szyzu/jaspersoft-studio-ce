/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.jaspersoft.studio.data.designer.AQueryDesigner;
import com.jaspersoft.studio.data.designer.IFilterQuery;
import com.jaspersoft.studio.data.designer.SelectParameterDialog;
import com.jaspersoft.studio.data.sql.Util;
import com.jaspersoft.studio.data.sql.model.query.AMKeyword;
import com.jaspersoft.studio.data.sql.model.query.expression.MExpressionX;
import com.jaspersoft.studio.data.sql.model.query.operand.AOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.FieldOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.ParameterPOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.UnknownOperand;
import com.jaspersoft.studio.data.sql.widgets.AOperandWidget;
import com.jaspersoft.studio.data.sql.widgets.Factory;
import com.jaspersoft.studio.data.sql.widgets.ParameterWidget;

import net.sf.jasperreports.eclipse.ui.ATitledDialog;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;

public class EditExpressionXDialog extends ATitledDialog implements IFilterQuery {
	private MExpressionX value;
	private AQueryDesigner designer;

	public EditExpressionXDialog(Shell parentShell, AQueryDesigner designer) {
		super(parentShell);
		this.designer = designer;
		setTitle("Expression ${X} Dialog");
		setDescription("SQL syntax is different in case of NULL or not NULL comparison. Example: b = 1 , b IS NULL."
				+ " Before query execution\nthe $X{} function will adapt the SQL string in the correct way, depending if parameter is null or not null.");
	}

	@Override
	public boolean close() {
		if (isTwoOperands() && operands.size() > 1)
			Util.removeFrom(operands, 1);
		else if (isThreeOperands() && operands.size() > 2)
			Util.removeFrom(operands, 2);
		return super.close();
	}

	public void setValue(MExpressionX value) {
		this.value = value;
		setFunction(value.getFunction());
		setPrevcond(value.getPrevCond());
		operands = new ArrayList<>(value.getOperands());
	}

	private java.util.List<AOperand> operands;
	private String prevcond;
	private String function;

	public String getPrevcond() {
		return prevcond;
	}

	public void setPrevcond(String prevcond) {
		this.prevcond = prevcond;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public java.util.List<AOperand> getOperands() {
		return operands;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cmp = (Composite) super.createDialogArea(parent);
		cmp.setLayout(new GridLayout(5, false));

		DataBindingContext bindingContext = new DataBindingContext();

		if (!value.isFirst()) {
			Composite c = new Composite(cmp, SWT.NONE);
			GridLayout layout = new GridLayout(5, false);
			layout.marginWidth = 0;
			c.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 5;
			c.setLayoutData(gd);

			new Label(c, SWT.NONE).setText("Previous condition ");

			Combo prevoperator = new Combo(c, SWT.READ_ONLY);
			prevoperator.setItems(AMKeyword.CONDITIONS);

			new Label(c, SWT.NONE).setText(" this one.");

			bindingContext.bindValue(
					WidgetProperties.widgetSelection().observe(prevoperator),
					PojoProperties.value("prevcond").observe(this)); //$NON-NLS-1$
		} else {
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 5;
			new Label(cmp, SWT.NONE).setLayoutData(gd);
		}

		createSQLWidget(cmp);

		Combo operator = new Combo(cmp, SWT.READ_ONLY);
		operator.setItems(MExpressionX.FUNCTIONS);
		operator.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		operator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showRight();
			}
		});

		rcmp = new Composite(cmp, SWT.NONE);
		stackLayout = new StackLayout();
		stackLayout.marginHeight = 0;
		stackLayout.marginWidth = 0;
		rcmp.setLayout(stackLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 150;
		gd.widthHint = 300;
		gd.verticalSpan = 2;
		gd.horizontalSpan = 3;
		rcmp.setLayoutData(gd);

		showRight();
		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(operator),
				PojoProperties.value("function").observe(this)); //$NON-NLS-1$
		return cmp;
	}

	protected void createSQLWidget(Composite cmp) {
		Composite parent = new Composite(cmp, SWT.NONE);
		GridLayout gd = new GridLayout(2, false);
		gd.marginHeight = 0;
		parent.setLayout(gd);
		Set<Class<? extends AOperand>> mop = new HashSet<>();
		mop.add(FieldOperand.class);
		mop.add(UnknownOperand.class);
		Factory.createWidget(parent, operands, 0, value, mop, designer);
	}

	private Map<String, Composite> map = new HashMap<>();
	private Composite rcmp;
	private StackLayout stackLayout;

	private void showRight() {
		Composite cmp = map.get(getFunction());
		if (cmp == null) {
			if (isTwoOperands()) {
				cmp = new Composite(rcmp, SWT.NONE);
				GridLayout layout = new GridLayout(2, false);
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				cmp.setLayout(layout);

				if (operands.size() < 2)
					operands.add(new ParameterPOperand(value, false));

				AOperandWidget<?> w = Factory.createWidget(cmp, operands.get(1), designer);
				w.setOperandMap(w.getOperandMap());
				w.setOperands(operands, 0);
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.widthHint = 200;
				w.setLayoutData(gd);

			} else if (isThreeOperands()) {
				cmp = new Composite(rcmp, SWT.NONE);
				GridLayout layout = new GridLayout(3, false);
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				cmp.setLayout(layout);

				if (operands.size() < 2)
					operands.add(new ParameterPOperand(value, false));
				AOperandWidget<?> w = Factory.createWidget(cmp, operands.get(1), designer);
				w.setOperandMap(w.getOperandMap());
				w.setOperands(operands, 0);
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.widthHint = 200;
				w.setLayoutData(gd);

				new Label(cmp, SWT.NONE).setText("AND");

				if (operands.size() < 3)
					operands.add(new ParameterPOperand(value, false));
				w = Factory.createWidget(cmp, operands.get(2), designer);
				w.setOperandMap(w.getOperandMap());
				w.setOperands(operands, 0);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.widthHint = 200;
				w.setLayoutData(gd);
			} else if (isManyOperands()) {
				cmp = new Composite(rcmp, SWT.NONE);
				GridLayout layout = new GridLayout(2, false);
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				cmp.setLayout(layout);

				createInList(cmp);
			}
		}
		stackLayout.topControl = cmp;
		rcmp.layout(true);
	}

	protected boolean isManyOperands() {
		return function.equals("IN") || function.equals("NOTIN");
	}

	protected boolean isThreeOperands() {
		return function.equals(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN)
				|| function.equals(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_CLOSED)
				|| function.equals(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_LEFT_CLOSED)
				|| function.equals(JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_RIGHT_CLOSED);
	}

	protected boolean isTwoOperands() {
		return function.equals(JRJdbcQueryExecuter.CLAUSE_ID_EQUAL)
				|| function.equals(JRJdbcQueryExecuter.CLAUSE_ID_NOTEQUAL)
				|| function.equals(JRJdbcQueryExecuter.CLAUSE_ID_GREATER)
				|| function.equals(JRJdbcQueryExecuter.CLAUSE_ID_GREATER_OR_EQUAL)
				|| function.equals(JRJdbcQueryExecuter.CLAUSE_ID_LESS)
				|| function.equals(JRJdbcQueryExecuter.CLAUSE_ID_LESS_OR_EQUAL);
	}

	protected void createInList(Composite cmp) {
		final List inlist = new List(cmp, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.verticalSpan = 3;
		gd.widthHint = 200;
		inlist.setLayoutData(gd);

		Button op3 = new Button(cmp, SWT.PUSH);
		op3.setText("&Add");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		op3.setLayoutData(gd);
		op3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAddInList(inlist);
			}
		});

		op3 = new Button(cmp, SWT.PUSH);
		op3.setText("&Edit");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		op3.setLayoutData(gd);
		op3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEditInList(inlist);
			}
		});

		op3 = new Button(cmp, SWT.PUSH);
		op3.setText("&Delete");
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		op3.setLayoutData(gd);
		op3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = inlist.getSelectionIndex() + 1;
				if (index >= 0 && index < operands.size()) {
					operands.remove(index);
					showInList(inlist);
				}
			}
		});
		inlist.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				handleEditInList(inlist);
			}
		});

		showInList(inlist);
	}

	private void handleAddInList(List inlist) {
		SelectParameterDialog d = new SelectParameterDialog(rcmp.getShell(), designer, this);
		if (d.open() == Dialog.OK) {
			int index = inlist.getSelectionIndex();
			ParameterPOperand op = new ParameterPOperand(value);
			op.setJrParameter(d.getPname());
			if (index >= 0 && index < operands.size())
				operands.add(index + 1, op);
			else
				operands.add(op);
			showInList(inlist);
		}
	}

	private void handleEditInList(List inlist) {
		int index = inlist.getSelectionIndex() + 1;
		if (index >= 0 && index < operands.size()) {
			ParameterPOperand pop = (ParameterPOperand) operands.get(index);
			SelectParameterDialog d = new SelectParameterDialog(rcmp.getShell(), designer, pop.getJrParameter(), this);
			if (d.open() == Dialog.OK) {
				pop.setJrParameter(d.getPname());
				showInList(inlist);
			}
		}
	}

	private void showInList(List inlist) {
		String[] ilarray = new String[Math.max(operands.size() - 1, 0)];
		if (!operands.isEmpty())
			for (int i = 1; i < operands.size(); i++)
				ilarray[i - 1] = operands.get(i).toXString();
		inlist.setItems(ilarray);
	}

	@Override
	public String getFilterQuery() {
		return ParameterWidget.getFilterQueryObject(operands);
	}

	@Override
	public String getLanguage() {
		return "sql";
	}

}
