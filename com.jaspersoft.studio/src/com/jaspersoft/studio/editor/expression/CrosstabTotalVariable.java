/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.expression;

import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabMeasure;
import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;

/**
 * 
 * @author gtoffoli
 */
public class CrosstabTotalVariable extends ExpObject {

	private JRCrosstabColumnGroup columnGroup = null;
	private JRCrosstabRowGroup rowGroup = null;
	private JRCrosstabMeasure measure = null;

	public CrosstabTotalVariable(JRCrosstabMeasure measure, JRCrosstabRowGroup rowGroup, JRCrosstabColumnGroup columnGroup) {
		this.measure = measure;
		this.rowGroup = rowGroup;
		this.columnGroup = columnGroup;

		setClassType(measure.getValueClassName());
		setName(toString());
		setType(TYPE_VARIABLE);
	}

	public JRCrosstabColumnGroup getColumnGroup() {
		return columnGroup;
	}

	public void setColumnGroup(JRCrosstabColumnGroup columnGroup) {
		this.columnGroup = columnGroup;
	}

	public JRCrosstabRowGroup getRowGroup() {
		return rowGroup;
	}

	public void setRowGroup(JRCrosstabRowGroup rowGroup) {
		this.rowGroup = rowGroup;
	}

	public JRCrosstabMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(JRCrosstabMeasure measure) {
		this.measure = measure;
	}

	@Override
	public String toString() {
		String s = measure.getName();
		if (columnGroup == null && rowGroup == null)
			return s;
		if (columnGroup == null) {
			return s + " (total by " + rowGroup.getName() + ")";
		} else if (rowGroup == null) {
			return s + " (total by " + columnGroup.getName() + ")";
		} else {
			return s + " (total by " + rowGroup.getName() + " and " + columnGroup.getName() + ")";
		}
	}

	@Override
	public String getExpression() {
		String s = "$V{" + measure.getName();
		if (rowGroup != null) {
			s += "_" + rowGroup.getName();
		}

		if (columnGroup != null) {
			s += "_" + columnGroup.getName();
		}

		return s + "_ALL}";
	}
}
