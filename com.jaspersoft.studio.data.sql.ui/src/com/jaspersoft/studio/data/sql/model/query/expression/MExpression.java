/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.model.query.expression;

import java.text.MessageFormat;

import net.sf.jasperreports.engine.JRConstants;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;

import com.jaspersoft.studio.data.sql.model.enums.Operator;
import com.jaspersoft.studio.data.sql.model.query.from.MFromTableJoin;
import com.jaspersoft.studio.data.sql.model.query.operand.AOperand;
import com.jaspersoft.studio.data.sql.model.query.subquery.MQueryTable;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.preferences.fonts.utils.FontUtils;

public class MExpression extends AMExpression<Object> {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MExpression(ANode parent, Object value, int newIndex) {
		super(parent, value, newIndex);
	}

	@Override
	public MExpression clone() {
		MExpression clone = (MExpression) super.clone();
		for (AOperand op : clone.getOperands())
			op.setExpression(clone);
		return clone;
	}

	@Override
	public String getDisplayText() {
		String dt = "";
		if (!isFirst()) {
			if (getParent() instanceof MFromTableJoin && getParent().getValue() instanceof MQueryTable) {
				MFromTableJoin mftj = (MFromTableJoin) getParent();
				dt += ") " + mftj.addAlias() + " ON ";
			} else
				dt += prevCond + " ";
		}
		String[] ops = null;
		if (operator.getNrOperands() > 3) {
			ops = new String[] { "", "" };
			String sep = "";
			for (int i = 0; i < operands.size(); i++) {
				if (i == 0)
					ops[i] = operands.get(i).toSQLString();
				else {
					ops[1] += sep + operands.get(i).toSQLString();
					sep = ",";
				}
			}
		} else {
			ops = new String[operands.size()];
			for (int i = 0; i < ops.length; i++)
				ops[i] = operands.get(i).toSQLString();
		}
		return dt + MessageFormat.format(operator.getFormat(operator), (Object[]) ops)
				+ isLastInGroup(getParent(), this);
	}

	@Override
	public StyledString getStyledDisplayText() {
		String dt = getDisplayText();
		StyledString ss = new StyledString(dt);
		Styler ks = FontUtils.getKeywordStyler();
		if (!isFirst()) {
			if (getParent() instanceof MFromTableJoin && getParent().getValue() instanceof MQueryTable) {
				int ind = dt.indexOf(" AS ");
				if (ind >= 0)
					ss.setStyle(ind, " AS ".length(), ks);
				ind = (dt).indexOf(" ON ");
				if (ind >= 0)
					ss.setStyle(ind, " ON ".length(), ks);
			} else
				ss.setStyle(0, (prevCond + " ").length(), ks);
		}
		if (operator.getNrOperands() != 2 || (operator.getNrOperands() == 2 && operator == Operator.LIKE)) {
			String sqlname = " " + operator.getSqlname() + " ";
			int ind = dt.indexOf(sqlname);
			if (ind >= 0)
				ss.setStyle(ind, sqlname.length(), ks);
		}
		if (operator.getNrOperands() == 3 && (operator == Operator.BETWEEN || operator == Operator.NOTBETWEEN))
			ss.setStyle(dt.indexOf(" AND "), " AND ".length(), ks);
		return ss;
	}

	private Operator operator = Operator.EQUALS;

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

}
