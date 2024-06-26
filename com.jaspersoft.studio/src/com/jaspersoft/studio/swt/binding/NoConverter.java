/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.swt.binding;

import org.eclipse.core.databinding.conversion.Converter;

public class NoConverter extends Converter {

	public NoConverter(Class<?> type) {
		super(type, type);
	}

	@Override
	public Object convert(Object fromObject) {
		return fromObject;
	}

}
