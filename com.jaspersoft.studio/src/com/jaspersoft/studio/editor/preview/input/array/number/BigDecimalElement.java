/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.input.array.number;

import java.math.BigDecimal;
import java.util.Locale;

import org.apache.commons.validator.routines.BigDecimalValidator;

public class BigDecimalElement extends ANumberElement {

	@Override
	public Class<?> getSupportedType() {
		return BigDecimal.class;
	}

	protected boolean isValid(String number) {
		return BigDecimalValidator.getInstance().isValid(number, Locale.US);
	}

	@Override
	protected Object convertString(String str) {
		return new BigDecimal(str);
	}
}
