/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.gef.util;

import org.eclipse.gef.requests.CreateRequest;

public class CreateRequestUtil {
	public static final String NEWINDEX = "newindex";

	public static int getNewIndex(CreateRequest request) {
		int index = -1;
		if (request.getExtendedData() != null && request.getExtendedData().get(NEWINDEX) != null)
			index = (Integer) request.getExtendedData().get(NEWINDEX);
		return index;
	}
}
