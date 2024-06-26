/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.fonts.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wb.swt.ResourceManager;

import com.jaspersoft.studio.preferences.DesignerPreferencePage;
import com.jaspersoft.studio.preferences.util.PreferencesUtils;
import com.jaspersoft.studio.utils.UIUtil;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class FontUtils {
	/** Styler for the parameters */
	public static final Styler KEYWORDS_STYLER;
	/** Styler for the parameters */
	public static final Styler PARAMETER_STYLER;
	/** Styler for the variables */
	public static final Styler VARIABLE_STYLER;
	/** Styler for the fields */
	public static final Styler FIELD_STYLER;
	/** Styler for the class types */
	public static final Styler CLASSTYPE_STYLER;

	static {
		// Styling info
		JFaceResources.getColorRegistry().put("PARAMETER_DARKRED_COLOR", new RGB(127, 0, 83)); //$NON-NLS-1$
		JFaceResources.getColorRegistry().put("PARAMETER_RED_COLOR", new RGB(190, 39, 39)); //$NON-NLS-1$
		JFaceResources.getColorRegistry().put("VARIABLE_BLUE_COLOR", new RGB(41, 41, 255)); //$NON-NLS-1$
		JFaceResources.getColorRegistry().put("FIELD_GREEN_COLOR", new RGB(39, 144, 39)); //$NON-NLS-1$
		JFaceResources.getColorRegistry().put("GRAY_CLASS_TYPE", new RGB(143, 143, 143)); //$NON-NLS-1$
		KEYWORDS_STYLER = new BoldStyler("PARAMETER_DARKRED_COLOR", null); //$NON-NLS-1$
		PARAMETER_STYLER = new BoldStyler("PARAMETER_RED_COLOR", null); //$NON-NLS-1$
		VARIABLE_STYLER = StyledString.createColorRegistryStyler("VARIABLE_BLUE_COLOR", null); //$NON-NLS-1$
		FIELD_STYLER = StyledString.createColorRegistryStyler("FIELD_GREEN_COLOR", null); //$NON-NLS-1$
		CLASSTYPE_STYLER = StyledString.createColorRegistryStyler("GRAY_CLASS_TYPE", null); //$NON-NLS-1$
	}

	public static String separator = "__________________";

	/**
	 * Styler for the QUERY EDITORS
	 * 
	 * @return
	 */
	public static Styler getKeywordStyler() {
		JFaceResources.getColorRegistry().put("PARAMETER_KEYWORD_COLOR", new RGB(206, 92, 0)); //$NON-NLS-1$
		return new BoldStyler("PARAMETER_KEYWORD_COLOR", null);
	}

	/**
	 * Convert a list of array of string into a single array of string, ready to
	 * be inserted into a combo
	 * 
	 * @param fontsList List of array of fonts (every list is a category)
	 * @param useSeparator specify if a separator must be used between each
	 * category
	 * 
	 * @return List of combo item
	 */
	public static String[] stringToItems(List<String[]> fontsList, boolean useSeparator) {
		List<String> itemsList = new ArrayList<>();
		for (int index = 0; index < fontsList.size(); index++) {
			String[] fonts = fontsList.get(index);
			for (String element : fonts)
				itemsList.add(element);
			if (index + 1 != fontsList.size() && fonts.length > 0 && useSeparator)
				itemsList.add(separator);
		}
		String[] result = new String[itemsList.size()];
		return itemsList.toArray(result);
	}

	/**
	 * Convert a list of array of string into a single array of string, ready to
	 * be inserted into a combo
	 * 
	 * @param fontsList List of array of fonts, between every array will be
	 * inserted a separator
	 * @return List of combo item
	 */
	public static String[] stringToItems(List<String[]> fontsList) {
		return stringToItems(fontsList, true);
	}

	/**
	 * Gets the shared font across JSS editors (i.e: expression editor, query
	 * editors).
	 * 
	 * @return the shared font for JSS editors
	 */
	public static Font getEditorsFont(JasperReportsConfiguration jconfig) {
		String fontDataStr = null;
		if (jconfig != null) {
			fontDataStr = jconfig.getProperty(DesignerPreferencePage.P_INTERNAL_EDITORS_FONT,
					getTextEditorFontDataAsString());
		}
		if (fontDataStr == null) {
			// Gets default from JaspersoftStudio plugin
			fontDataStr = PreferencesUtils.getJaspersoftStudioPrefStore()
					.getString(DesignerPreferencePage.P_INTERNAL_EDITORS_FONT);
		}
		FontData[] fontDataArray = PreferenceConverter.basicGetFontData(fontDataStr);
		return ResourceManager.getFont(fontDataArray[0].getName(), fontDataArray[0].getHeight(),
				fontDataArray[0].getStyle());

	}

	/**
	 * Gets the {@link FontData} instance representing the basic text font.
	 * 
	 * @see JFaceResources#TEXT_FONT
	 */
	public static FontData getTextEditorFontData() {
		FontData textFontData = PreferenceConverter.getFontData(PreferenceConstants.getPreferenceStore(),
				JFaceResources.TEXT_FONT);
		return textFontData;
	}

	/**
	 * Gets the string preference property representing the basic text font.
	 * 
	 * @see JFaceResources#TEXT_FONT
	 */
	public static String getTextEditorFontDataAsString() {
		FontData editorTextFontData = getTextEditorFontData();
		if (editorTextFontData != null) {
			return PreferenceConverter.getStoredRepresentation(new FontData[] { editorTextFontData });
		}
		return null;
	}

}
