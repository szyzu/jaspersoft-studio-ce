/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.model.query.expression;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;

import com.jaspersoft.studio.data.sql.model.query.from.MFromTableJoin;
import com.jaspersoft.studio.data.sql.model.query.operand.AOperand;
import com.jaspersoft.studio.data.sql.model.query.subquery.MQueryTable;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.preferences.fonts.utils.FontUtils;

public class MExpressionX extends AMExpression<Object> {
	public static final String[] FUNCTIONS = new String[] { JRJdbcQueryExecuter.CLAUSE_ID_EQUAL,
			JRJdbcQueryExecuter.CLAUSE_ID_NOTEQUAL, JRJdbcQueryExecuter.CLAUSE_ID_GREATER,
			JRJdbcQueryExecuter.CLAUSE_ID_GREATER_OR_EQUAL, JRJdbcQueryExecuter.CLAUSE_ID_LESS,
			JRJdbcQueryExecuter.CLAUSE_ID_LESS_OR_EQUAL, JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN,
			JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_CLOSED, JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_LEFT_CLOSED,
			JRJdbcQueryExecuter.CLAUSE_ID_BETWEEN_RIGHT_CLOSED, JRJdbcQueryExecuter.CLAUSE_ID_IN,
			JRJdbcQueryExecuter.CLAUSE_ID_NOTIN };

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MExpressionX(ANode parent, Object value, int newIndex) {
		super(parent, value, newIndex);
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

		dt += "$X{" + function;
		String sep = ",";
		for (AOperand op : operands)
			if (op.toXString().contains(",")) {
				sep = "|";
				break;
			}
		for (AOperand op : operands)
			dt += sep + op.toXString();
		dt += "}";
		return dt + isLastInGroup(getParent(), this);
	}

	@Override
	public StyledString getStyledDisplayText() {
		String dt = getDisplayText();
		StyledString ss = new StyledString(dt);
		if (!isFirst()) {
			Styler ks = FontUtils.getKeywordStyler();
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
		ss.setStyle(dt.lastIndexOf("$X{"), 3, FontUtils.CLASSTYPE_STYLER);
		ss.setStyle(dt.lastIndexOf("}"), 1, FontUtils.CLASSTYPE_STYLER);
		return ss;
	}

	private String function = JRJdbcQueryExecuter.CLAUSE_ID_EQUAL;

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

}
