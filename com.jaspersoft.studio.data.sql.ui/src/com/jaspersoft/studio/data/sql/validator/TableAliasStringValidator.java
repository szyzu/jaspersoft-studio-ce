/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.eclipse.messages.Messages;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.jaspersoft.studio.data.querydesigner.sql.text.SQLScanner;

public class TableAliasStringValidator implements IValidator {
	private Pattern pattern = Pattern.compile("^[A-Za-z0-9_-]{0,100}$"); //$NON-NLS-1$

	public IStatus validate(Object value) {
		if (value == null || ((String) value).isEmpty())
			return Status.OK_STATUS;
		String str = (String) value;

		Matcher matcher = pattern.matcher(str);
		if (!matcher.matches())
			return ValidationStatus.error(Messages.IDStringValidator_InvalidChars);
		if (SQLScanner.SQL_KEYWORDS.contains(str.trim().toLowerCase()))
			return ValidationStatus.error("Please do not use SQL reserved keywords");
		return Status.OK_STATUS;
	}
}
