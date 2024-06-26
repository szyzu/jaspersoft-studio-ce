/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.reader;

import net.sf.jasperreports.engine.JRScriptletException;

/**
 * Custom exception that should be raised when a data preview task is interrupted.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class DataPreviewInterruptedException extends JRScriptletException {
	
	private static final long serialVersionUID = 1L;
	
	public DataPreviewInterruptedException(String message) {
		super(message);
	}

}
