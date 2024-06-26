/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.model.query.subquery;

import net.sf.jasperreports.engine.JRConstants;

import com.jaspersoft.studio.data.sql.model.metadata.MSQLColumn;
import com.jaspersoft.studio.data.sql.model.query.AMQueryAliased;
import com.jaspersoft.studio.model.ANode;

public class MQueryColumn extends MSQLColumn {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	protected AMQueryAliased<?> col;

	public MQueryColumn(ANode parent, AMQueryAliased<?> value) {
		super(parent, value.getDisplayText(), null);
		this.col = value;
	}
}
