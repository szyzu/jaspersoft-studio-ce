/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.expression;

/**
 * Classes that implement this interface provides a method
 * that deals with the events that are generated when the
 * status of an expression (being edited) changes. 
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public interface IExpressionStatusChangeListener {

	/**
	 * Handles the expression status change.
	 * 
	 * @param status the expression status
	 */
	void statusChanged(ExpressionStatus status);
		
}
