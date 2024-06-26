/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section.report.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.jaspersoft.studio.swt.widgets.NullableSpinner;

/**
 * Couple of widget composed from a nullable spinner to set a numeric value and
 * a combo to select a measure unit
 */
public class ValueUnitsWidget {

	private Unit unit;

	private Unit maxPixels;

	private Combo unitc;

	private NullableSpinner val;

	private ValidatedMeasureUnitFormat spinnerValidator = new ValidatedMeasureUnitFormat(Unit.PX);

	private final class SpinerSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			unit.setValue(val.getValueAsDouble(), Unit.getUnits()[unitc.getSelectionIndex()]);
		}
	}

	public ValueUnitsWidget() {
		unit = new Unit(0, Unit.PX);
		maxPixels = new Unit(Integer.MAX_VALUE, Unit.PX);
	}

	public void createComponent(Composite parent, String label, String toolTip) {
		Label lbl = new Label(parent, SWT.NONE);
		lbl.setText(label);
		val = new NullableSpinner(parent, SWT.BORDER | SWT.RIGHT);
		val.setFormat(spinnerValidator);
		val.setToolTipText(toolTip);
		val.setNullable(false);
		GridData gd = new GridData();
		gd.widthHint = 80;
		val.setLayoutData(gd);

		unitc = new Combo(parent, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		unitc.setItems(Unit.getUnits());
		unitc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String u = Unit.getUnits()[unitc.getSelectionIndex()];
				if (unit.setUnit(u)) {
					setSpinerValue(u);
				}
			}
		});

		val.addSelectionListener(new SpinerSelectionListener());

		unitc.select(0);
		setSpinerValue(unit.getUnit());
	}

	public void setMaxPixels(int maxPixels) {
		this.maxPixels.setValue(maxPixels, Unit.PX);
		String currentUnit = Unit.getUnits()[unitc.getSelectionIndex()];
		val.setMaximum(this.maxPixels.getValue(currentUnit));
	}

	private void setSpinerValue(String u) {
		spinnerValidator.setMeasureUnit(u);
		val.setMinimum(0);
		val.setMaximum(this.maxPixels.getValue(u));
		val.setIncrement(1);
		val.setValue(unit.getValue(u));
	}

	public void addSelectionListener(SelectionListener listener) {
		val.addSelectionListener(listener);
	}

	public void setUnit(String u) {
		if (unit.setUnit(u)) {
			unitc.select(Unit.getUnitIndex(u));
			setSpinerValue(u);
		}
	}

	public int getValue() {
		return unit.getPxValue();
	}

	public String getUnit() {
		return unit.getUnit();
	}

	public void setValue(int px) {
		unit.setValue(px, Unit.PX);
		setSpinerValue(unit.getUnit());
	}
}
