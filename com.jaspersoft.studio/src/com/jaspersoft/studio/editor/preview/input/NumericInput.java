/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.input;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import com.jaspersoft.studio.editor.preview.view.control.VParameters;

import net.sf.jasperreports.eclipse.util.Misc;

public class NumericInput extends ADataInput {
	private Spinner num;
	private int min;
	private int max;
	private int digits;
	private int increment;
	private int pageIncrement;

	public boolean isForType(Class<?> valueClass) {
		return Integer.class.isAssignableFrom(valueClass) || Short.class.isAssignableFrom(valueClass)
				|| Byte.class.isAssignableFrom(valueClass);
	}

	@Override
	public void createInput(Composite parent, final IParameter param, final Map<String, Object> params) {
		super.createInput(parent, param, params);
		Class<?> valueClass = param.getValueClass();
		if (Number.class.isAssignableFrom(param.getValueClass())) {
			min = 0;
			max = 0;
			digits = 0;
			increment = 1;
			pageIncrement = 10;
			if (valueClass.equals(Integer.class)) {
				min = Integer.MIN_VALUE;
				max = Integer.MAX_VALUE;
			} else if (valueClass.equals(Short.class)) {
				min = (int) Short.MIN_VALUE;
				max = (int) Short.MAX_VALUE;
			} else if (valueClass.equals(Byte.class)) {
				min = (int) Byte.MIN_VALUE;
				max = (int) Byte.MAX_VALUE;
			}

			num = new Spinner(parent, SWT.BORDER);
			num.addFocusListener(focusListener);
			num.addTraverseListener(keyListener);
			num.setToolTipText(VParameters.createToolTip(param));
			updateInput();
			final ModifyListener listener2 = new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					num.removeModifyListener(this);
					Number n = null;
					if (param.getValueClass().equals(Integer.class)) {
						n = new Integer(Misc.nvl(num.getText()));
					} else if (param.getValueClass().equals(Byte.class)) {
						n = new Byte(Misc.nvl(num.getText()));
					} else if (param.getValueClass().equals(Short.class)) {
						n = new Short(Misc.nvl(num.getText()));
					}
					updateModel(n);
					updateInput();
					num.addModifyListener(this);
				}
			};
			num.addModifyListener(listener2);

			if (param.getMinValue() != null) {
				int minval = new Integer(param.getMinValue()).intValue();
				if (!param.isStrictMin())
					minval++;
				num.setMinimum(minval);
			}
			if (param.getMaxValue() != null) {
				int maxval = new Integer(param.getMaxValue()).intValue();
				if (!param.isStrictMax())
					maxval--;
				num.setMaximum(maxval);
			}

			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalIndent = 8;
			num.setLayoutData(gd);

			setMandatory(param, num);
			setNullable(param, num);
		}
	}

	protected Number getNumber(String number) throws NumberFormatException {
		if (param.getValueClass().equals(Long.class)) {
			return new Long(number);
		} else if (param.getValueClass().equals(BigInteger.class)) {
			return new BigInteger(number);
		} else if (param.getValueClass().equals(Float.class)) {
			return new Float(number);
		} else if (param.getValueClass().equals(Double.class)) {
			return new Double(number);
		} else if (param.getValueClass().equals(Integer.class)) {
			return new Integer(number);
		} else if (param.getValueClass().equals(Short.class)) {
			return new Short(number);
		} else if (param.getValueClass().equals(Byte.class)) {
			return new Byte(number);
		} else if (param.getValueClass().equals(BigDecimal.class)) {
			return new BigDecimal(number);
		}
		return null;
	}

	public void updateInput() {
		Object value = params.get(param.getName());
		if (value != null && value instanceof String)
			value = getNumber((String) value);
		if (value != null && value instanceof Number) {
			int val = 0;
			if (value != null)
				if (digits == 0)
					val = ((Number) value).intValue();
				else
					val = (int) (((Number) value).doubleValue() * Math.pow(10000, 1));
			num.setValues(val, min, max, digits, increment, pageIncrement);
		}
		setDecoratorNullable(param);
	}
}
