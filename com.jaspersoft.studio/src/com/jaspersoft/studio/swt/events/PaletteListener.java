/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.swt.events;

import java.util.List;

/**
 * Classes which implement this interface usually deal with the palette
 * modification events manipulating the new updated colors available.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 */
public interface PaletteListener {

	/**
	 * Notification method invoked when a new list of colors is available.
	 * 
	 * @param newHexColors
	 *            the new colors that belong to the palette.
	 */
	void paletteModified(List<String> newHexColors);
	
}
