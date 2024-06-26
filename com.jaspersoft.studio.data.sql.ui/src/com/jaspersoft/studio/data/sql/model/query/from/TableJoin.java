/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.model.query.from;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;

public class TableJoin implements Serializable {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	private MFromTable fromTable;
	private MFromTableJoin joinTable;

	public TableJoin(MFromTableJoin foreignTable, MFromTable primaryTable) {
		super();
		this.fromTable = primaryTable;
		this.joinTable = foreignTable;
		fromTable.addTableJoin(this);
	}

	public MFromTableJoin getJoinTable() {
		return joinTable;
	}

	public MFromTable getFromTable() {
		return fromTable;
	}

	public void setFromTable(MFromTable targetPrimaryKey) {
		this.fromTable = targetPrimaryKey;
	}

	public void setJoinTable(MFromTableJoin sourceForeignKey) {
		this.joinTable = sourceForeignKey;
	}

}
