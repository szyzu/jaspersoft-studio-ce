/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.model.query.from;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.jaspersoft.studio.data.sql.model.MSQLRoot;
import com.jaspersoft.studio.data.sql.model.metadata.MSqlTable;
import com.jaspersoft.studio.data.sql.model.query.AMQueryAliased;
import com.jaspersoft.studio.data.sql.model.query.subquery.MQueryTable;
import com.jaspersoft.studio.data.sql.text2model.ConvertUtil;
import com.jaspersoft.studio.model.ANode;

public class MFromTable extends AMQueryAliased<MSqlTable> {
	public static final String SHOW_MODE_PROPERTY = "showMode";
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MFromTable(ANode parent, MSqlTable value) {
		super(parent, value, null);
	}

	public MFromTable(ANode parent, MSqlTable value, int index) {
		super(parent, value, null, index);
	}

	@Override
	public String toSQLString() {
		String sql = super.toSQLString();
		if (getValue() instanceof MQueryTable)
			return "(";
		return sql;
	}

	@Override
	public String getToolTip() {
		MSqlTable mc = getValue();
		String tooltip = ConvertUtil.cleanDbNameFull(mc.toSQLString());
		tooltip += addAlias();
		if (getValue().getRemarks() != null)
			tooltip += "\n" + mc.getRemarks();
		return tooltip;
	}

	private Rectangle bounds;

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	private List<TableJoin> tableJoins;

	public void addTableJoin(TableJoin tjoin) {
		if (tableJoins == null)
			tableJoins = new ArrayList<TableJoin>();
		if (!tableJoins.contains(tjoin))
			tableJoins.add(tjoin);
	}

	public void removeTableJoin(TableJoin tjoin) {
		if (tableJoins != null)
			tableJoins.remove(tjoin);
	}

	public List<TableJoin> getTableJoins() {
		return tableJoins;
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(PROP_X) && value instanceof Point) {
			setNoEvents(true);
			super.setPropertyValue(PROP_X, ((Point) value).x);
			setNoEvents(false);
			super.setPropertyValue(PROP_Y, ((Point) value).y);
			return;
		}
		super.setPropertyValue(id, value);
	}

	public static final String PROP_X = "x";
	public static final String PROP_Y = "y";

	public List<TableJoinDetail> getTableJoinDetails() {
		MSQLRoot r = getRoot();
		if (r != null)
			return r.getJoins();
		return null;
	}
}
