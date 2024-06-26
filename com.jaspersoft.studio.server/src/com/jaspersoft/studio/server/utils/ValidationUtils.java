/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.utils;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jaspersoft.studio.server.messages.Messages;

/**
 * Validation utility functions for the JasperServer plug-in.
 * <p>
 * 
 * <b>NOTE</b>: initial code contributed from iReport.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * @author gtoffoli
 */
public class ValidationUtils {

	public static final int MAX_LENGTH_NAME = 100;
	public static final int MAX_LENGTH_LABEL = 100;
	public static final int MAX_LENGTH_DESC = 250;
	private static final Pattern PATTERN_NAME = Pattern.compile("(\\p{L}|\\p{N}|(\\_)|(\\.)|(\\-)|[;@])+"); //$NON-NLS-1$

	public static String validateName(String name) {
		if (name == null || name.length() == 0){
			return Messages.ValidationUtils_NameEmptyErr;
		}
		Matcher mat = PATTERN_NAME.matcher(name.trim());
		if (!mat.matches()){
			return Messages.ValidationUtils_NameInvalidCharsErr;
		}
		if (name.trim().length() > MAX_LENGTH_NAME) {
			return MessageFormat.format(
					Messages.ValidationUtils_NameTooLongErr,
					new Object[] { new Integer(MAX_LENGTH_NAME) });
		}
		return null;
	}

	public static String validateLabel(String name) {
		if (name == null || name.length() == 0){
			return Messages.ValidationUtils_LabelEmptyErr;
		}
		if (name.trim().length() > MAX_LENGTH_LABEL) {
			return MessageFormat.format(
					Messages.ValidationUtils_LabelTooLongErr,
					new Object[] { new Integer(MAX_LENGTH_LABEL) });
		}
		return null;
	}

	public static String validateDesc(String name) {
		if (name != null && name.trim().length() > MAX_LENGTH_DESC){
			return MessageFormat.format(
					Messages.ValidationUtils_DescriptionTooLongErr,
					new Object[] { new Integer(MAX_LENGTH_DESC) });
		}
		return null;
	}
	
}
