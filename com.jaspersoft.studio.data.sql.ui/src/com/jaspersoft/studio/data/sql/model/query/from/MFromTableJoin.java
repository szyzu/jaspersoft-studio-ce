/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.model.query.from;

import net.sf.jasperreports.engine.JRConstants;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;

import com.jaspersoft.studio.data.sql.model.metadata.MSqlTable;
import com.jaspersoft.studio.data.sql.model.query.AMKeyword;
import com.jaspersoft.studio.data.sql.model.query.subquery.MQueryTable;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.preferences.fonts.utils.FontUtils;

public class MFromTableJoin extends MFromTable {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MFromTableJoin(MFromTable parent, MSqlTable value) {
		this(parent, value, -1);
	}

	public MFromTableJoin(MFromTable parent, MSqlTable value, int index) {
		super(parent, value, index);

	}

	@Override
	public void setParent(ANode newparent, int newIndex) {
		if (newparent == null) {
			if (tableJoin != null)
				tableJoin.getFromTable().removeTableJoin(tableJoin);
			tableJoin = null;
		} else {
			((MFromTable) newparent).removeTableJoin(tableJoin);
			tableJoin = new TableJoin(this, (MFromTable) newparent);
		}
		super.setParent(newparent, newIndex);
	}

	@Override
	public MSqlTable getValue() {
		return (MSqlTable) super.getValue();
	}

	private String joinKey = "ON";
	private String join = AMKeyword.INNER_JOIN;

	public String getJoin() {
		return join;
	}

	public void setJoin(String join) {
		this.join = join;
	}

	public void setJoinKey(String joinKey) {
		this.joinKey = joinKey;
	}

	public String getJoinKey() {
		return joinKey;
	}

	@Override
	public String getToolTip() {
		return join + " " + super.getToolTip() + " " + joinKey + " ";
	}

	@Override
	public String getDisplayText() {
		String s = " " + join + " ";
		if (getValue() instanceof MQueryTable)
			return s + "(";
		return s + super.getDisplayText() + " " + joinKey + " ";
	}

	@Override
	public StyledString getStyledDisplayText() {
		Styler ks = FontUtils.getKeywordStyler();
		StyledString dt = new StyledString(join + " ", ks);
		String tbltext = super.getDisplayText();
		if (getValue() instanceof MQueryTable)
			return dt.append("(");
		int ind = (join + " " + tbltext).indexOf(" AS ");
		dt.append(tbltext);
		if (ind >= 0)
			dt.setStyle(ind, " AS ".length(), ks);
		dt.append(" " + joinKey + " ", ks);
		return dt;
	}

	private TableJoin tableJoin;

	public TableJoin getTableJoin() {
		return tableJoin;
	}

	public void setTableJoin(TableJoin tableJoin) {
		this.tableJoin = tableJoin;
	}

	public String toSQLString() {
		String sql = "";
		if (getValue() instanceof MQueryTable) {
			// sql = ((IQueryString) getValue()).toSQLString();
			return "\n\t" + join + " (";
		}
		sql += getValue().toSQLString();
		sql += addAlias();
		return "\n\t" + join + " " + sql + " " + joinKey + " ";
	}

}
