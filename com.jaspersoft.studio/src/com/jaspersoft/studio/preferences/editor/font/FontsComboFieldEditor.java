/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.editor.font;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.preferences.ITooltipSupport;
import com.jaspersoft.studio.preferences.fonts.utils.FontUtils;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

/**
 * A custom combo field editor to propose the list of all availables fonts.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public class FontsComboFieldEditor extends ComboFieldEditor implements ITooltipSupport {

	private String tooltipText;

	public FontsComboFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, getAllFonts(), parent);
	}

	@Override
	public void setTooltipText(String tooltipText) {
		this.tooltipText = tooltipText;
		if (getLabelControl() != null) {
			getLabelControl().setToolTipText(this.tooltipText);
		}
	}

	private static String[][] getAllFonts() {
		String[] fonts = FontUtils
				.stringToItems(
						ModelUtils.getFontNames(new JasperReportsConfiguration(DefaultJasperReportsContext.getInstance(), null)),
						false);
		String[][] results = new String[fonts.length][2];
		for (int i = 0; i < fonts.length; i++) {
			results[i][0] = results[i][1] = fonts[i];
		}
		return results;
	}

}
