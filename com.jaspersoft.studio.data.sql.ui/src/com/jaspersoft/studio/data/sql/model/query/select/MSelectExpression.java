/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.model.query.select;

import net.sf.jasperreports.engine.JRConstants;

import com.jaspersoft.studio.data.sql.model.query.AMQueryAliased;
import com.jaspersoft.studio.model.ANode;

public class MSelectExpression extends AMQueryAliased<String> {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MSelectExpression(ANode parent, String value) {
		super(parent, value, null);
	}

	public MSelectExpression(ANode parent, String value, int index) {
		super(parent, value, null, index);
	}

	@Override
	public String getToolTip() {
		return getValue() + addAlias();
	}

}
