/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.model.metadata;

import java.sql.ResultSet;

import net.sf.jasperreports.engine.JRConstants;

import com.jaspersoft.studio.model.IDragable;

public class MView extends MSqlTable implements IDragable {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MView(MTables parent, String value, ResultSet rs) {
		super(parent, "View", "icons/table.png");
		setRemarks(rs);
	}

}
