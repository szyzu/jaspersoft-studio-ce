/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.properties.action;

import com.jaspersoft.studio.server.properties.ASection;

public class EditOkAction extends AAction {
	public static final String ID = "saveproperties-js";

	public EditOkAction() {
		super("OK");
		setId(ID);
	}

	@Override
	public void run() {
		for (ASection s : sections)
			s.saveProperties();
	}
}
