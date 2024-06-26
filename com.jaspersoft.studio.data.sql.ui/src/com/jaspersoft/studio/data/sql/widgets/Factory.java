/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.widgets;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.jaspersoft.studio.data.designer.AQueryDesigner;
import com.jaspersoft.studio.data.sql.messages.Messages;
import com.jaspersoft.studio.data.sql.model.query.expression.AMExpression;
import com.jaspersoft.studio.data.sql.model.query.operand.AOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.FieldOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.FunctionOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.ParameterNotPOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.ParameterPOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.ScalarOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.UnknownOperand;
import com.jaspersoft.studio.data.sql.widgets.scalar.DateWidget;
import com.jaspersoft.studio.data.sql.widgets.scalar.NumberWidget;
import com.jaspersoft.studio.data.sql.widgets.scalar.StringWidget;
import com.jaspersoft.studio.data.sql.widgets.scalar.TimeWidget;
import com.jaspersoft.studio.data.sql.widgets.scalar.TimestampWidget;

public class Factory {
	public static final String OPERANDWIDGET = "operandwidget"; //$NON-NLS-1$
	public static final String OPERAND = "operand"; //$NON-NLS-1$
	public static final String OPERANDS = "OPERANDS"; //$NON-NLS-1$
	public static final String OPERANDS_INDEX = "OPERANDS_INDEX"; //$NON-NLS-1$

	public static Control createWidget(Composite parent, List<AOperand> operands, int index, AMExpression<?> mexpr,
			AQueryDesigner designer) {
		return createWidget(parent, operands, index, mexpr, false, designer);
	}

	public static Control createWidget(Composite parent, List<AOperand> operands, int index, AMExpression<?> mexpr,
			boolean exludeField, AQueryDesigner designer) {
		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new FillLayout());

		AOperand op = null;
		if (index >= 0 && index < operands.size())
			op = operands.get(index);
		else {
			op = new ScalarOperand<String>(mexpr, "Venice"); //$NON-NLS-1$
			operands.add(op);
		}

		AOperandWidget<?> w = createWidget(cmp, op, designer);
		w.setExludeField(exludeField);
		w.setOperandMap(w.getOperandMap());
		w.setOperands(operands, index);
		createWidgetMenu(w, operands, index, mexpr);

		return cmp;
	}

	public static Control createWidget(Composite parent, List<AOperand> operands, int index, AMExpression<?> mexpr,
			Set<Class<? extends AOperand>> menuOperand, AQueryDesigner designer) {
		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new FillLayout());

		AOperand op = null;
		if (index >= 0 && index < operands.size())
			op = operands.get(index);
		else
			op = new FieldOperand(null, null, mexpr);

		AOperandWidget<?> w = createWidget(cmp, op, designer);
		w.setMenuOperands(menuOperand);
		w.setOperandMap(w.getOperandMap());
		w.setOperands(operands, index);
		createWidgetMenu(w, operands, index, mexpr);
		return cmp;
	}

	protected static void createWidgetMenu(AOperandWidget<?> w, List<AOperand> operands, int index,
			AMExpression<?> mexpr) {
		for (Control c : w.getChildren()) {
			Menu popupMenu = new Menu(c);
			buildMenu(popupMenu, w, operands, index, mexpr);
			c.setMenu(popupMenu);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends AOperand> AOperandWidget<?> createWidget(Composite parent, T operand,
			AQueryDesigner designer) {
		AOperandWidget<T> w = null;
		if (operand instanceof FieldOperand)
			return new FieldWidget(parent, (FieldOperand) operand, designer);
		if (operand instanceof ParameterNotPOperand)
			return new ParameterWidget(parent, (ParameterNotPOperand) operand, designer);
		if (operand instanceof ParameterPOperand)
			return new ParameterWidget(parent, (ParameterPOperand) operand, designer);		
		if (operand instanceof ScalarOperand) {
			Class<?> type = ((ScalarOperand<?>) operand).getType();
			if (Number.class.isAssignableFrom(type))
				return new NumberWidget(parent, (ScalarOperand<Number>) operand, designer);
			if (Time.class.isAssignableFrom(type))
				return new TimeWidget(parent, (ScalarOperand<Time>) operand, designer);
			if (java.sql.Date.class.isAssignableFrom(type))
				return new DateWidget(parent, (ScalarOperand<Date>) operand, designer);
			if (Date.class.isAssignableFrom(type))
				return new TimestampWidget(parent, (ScalarOperand<Timestamp>) operand, designer);
			return new StringWidget(parent, (ScalarOperand<String>) operand, designer);

			// ? extends T opval = ((ScalarOperand<?>) operand).getValue();
			// if (opval instanceof String)
			// return new StringWidget(parent, (ScalarOperand<String>) operand);
			// if (opval instanceof Number)
			// return new NumberWidget(parent, (ScalarOperand<Number>) operand);
			// if (opval instanceof Time)
			// return new TimeWidget(parent, (ScalarOperand<Time>) operand);
			// if (opval instanceof Timestamp)
			// return new TimestampWidget(parent, (ScalarOperand<Timestamp>)
			// operand);
			// if (opval instanceof Date)
			// return new DateWidget(parent, (ScalarOperand<Date>) operand);
		}
		if(operand instanceof FunctionOperand)
			return new FunctionWidget(parent, (FunctionOperand)operand, designer);
		if (operand instanceof UnknownOperand)
			return new UnknownOperandWidget(parent, (UnknownOperand) operand, designer);
		return w;
	}

	public static void buildMenu(Menu pMenu, AOperandWidget<?> w, List<AOperand> operands, int index,
			AMExpression<?> mexpr) {
		Map<String, AOperand> opMap = buildMap(w, mexpr);
		Menu newMenu = null;
		for (String key : opMap.keySet()) {
			MenuItem mi1 = null;
			AOperand aOperand = opMap.get(key);
			if (!w.isMenuOperands(aOperand.getClass()))
				continue;
			if (aOperand instanceof FieldOperand && w.isExludeField())
				continue;
			if (aOperand instanceof ScalarOperand) {
				if (newMenu == null) {
					MenuItem cmi = new MenuItem(pMenu, SWT.CASCADE);
					cmi.setText(Messages.Factory_5);

					newMenu = new Menu(pMenu);
					cmi.setMenu(newMenu);
				}
				mi1 = new MenuItem(newMenu, SWT.CHECK);
			} else
				mi1 = new MenuItem(pMenu, SWT.CHECK);

			mi1.setText(key);
			mi1.setData(OPERAND, aOperand);
			setupMenuItem(mi1, w, operands, index);
			if (w.getValue() == aOperand
					|| (w.getValue() != null && !(w.getValue() instanceof ScalarOperand)
							&& w.getValue().getClass().equals(aOperand.getClass()))
					|| (w.getValue() instanceof ScalarOperand && aOperand instanceof ScalarOperand
							&& ((ScalarOperand<?>) w.getValue()).getType()
									.equals(((ScalarOperand<?>) aOperand).getType())))
				mi1.setSelection(true);
		}
	}

	private static void setupMenuItem(MenuItem mi1, AOperandWidget<?> w, List<AOperand> operands, int index) {
		mi1.setData(OPERANDWIDGET, w);
		mi1.setData(OPERANDS, operands);
		mi1.setData(OPERANDS_INDEX, index);
		mi1.addSelectionListener(slistener);
	}

	private static SelectionAdapter slistener = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			MenuItem mi = (MenuItem) e.getSource();
			AOperandWidget<?> w = (AOperandWidget<?>) mi.getData(OPERANDWIDGET);
			AOperand op = (AOperand) mi.getData(OPERAND);
			List<AOperand> operands = (List<AOperand>) mi.getData(OPERANDS);
			int index = (Integer) mi.getData(OPERANDS_INDEX);
			Composite parent = w.getParent();
			for (Control c : parent.getChildren())
				c.dispose();
			if (index < operands.size())
				operands.set(index, op);
			else
				operands.add(op);

			AOperandWidget<?> neww = createWidget(parent, operands.get(index), w.getDesigner());
			neww.setOperandMap(w.getOperandMap());
			neww.setMenuOperands(w.getMenuOperands());
			neww.setOperands(operands, index);
			createWidgetMenu(neww, operands, index, op.getExpression());
			parent.layout(true);
		}
	};

	public static Map<String, AOperand> buildMap(AOperandWidget<?> w, AMExpression<?> mexpr) {
		Map<String, AOperand> opMap = w.getOperandMap();
		if (opMap == null) {
			opMap = new LinkedHashMap<>();
			opMap.put(Messages.Factory_6, getOperand(w, new ParameterPOperand(mexpr)));
			opMap.put(Messages.Factory_7, getOperand(w, new ParameterNotPOperand(mexpr)));
			opMap.put(Messages.Factory_8, getOperand(w, new FieldOperand(null, null, mexpr)));
			opMap.put(Messages.Factory_9, getOperand(w, getDefaultOperand(mexpr)));
			opMap.put(Messages.Factory_10, getOperand(w, new ScalarOperand<BigDecimal>(mexpr, BigDecimal.ZERO)));
			opMap.put(Messages.Factory_11,
					getOperand(w, new ScalarOperand<java.sql.Date>(mexpr, new java.sql.Date(new Date().getTime()))));
			opMap.put(Messages.Factory_12,
					getOperand(w, new ScalarOperand<Time>(mexpr, new Time(new Date().getTime()))));
			opMap.put(Messages.Factory_13,
					getOperand(w, new ScalarOperand<Timestamp>(mexpr, new Timestamp(new Date().getTime()))));
			opMap.put(Messages.Factory_14, getOperand(w, new UnknownOperand(mexpr, ""))); // $NON-NLS-2$
			w.setOperandMap(opMap);
		}
		return opMap;
	}

	private static AOperand getOperand(AOperandWidget<?> w, AOperand operand) {
		if (w.getValue().getClass().isInstance(operand)) {
			if (operand instanceof ScalarOperand<?>) {
				ScalarOperand<?> op = (ScalarOperand<?>) operand;
				ScalarOperand<?> wop = (ScalarOperand<?>) w.getValue();
				if (op.getType().isAssignableFrom(wop.getType()))
					return w.getValue();
			} else
				return w.getValue();
		}
		return operand;
	}

	public static final AOperand getDefaultOperand(AMExpression<?> mexpr) {
		return new ScalarOperand<String>(mexpr, Messages.Factory_16);
	}
}
