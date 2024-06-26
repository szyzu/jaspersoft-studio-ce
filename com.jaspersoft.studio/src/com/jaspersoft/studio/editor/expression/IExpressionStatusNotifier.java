/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.expression;

/**
 * Classes that implement this interface are supposed to monitor 
 * directly or indirectly the expression modification in order to
 * notify a status change of the expression itself.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public interface IExpressionStatusNotifier {

	/**
	 * Add a new listener to the list of the listeners that will
	 * be notified when the expression status changes. 
	 * 
	 * @param listener the new listener
	 */
	void addExpressionStatusChangeListener(IExpressionStatusChangeListener listener);
	
	/**
	 * Remove an existing listener to the list of the listeners that will
	 * be notified when the expression status changes. 
	 * 
	 * @param listener the listener to be removed
	 */
	void removeExpressionStatusChangeListener(IExpressionStatusChangeListener listener);
	
	/**
	 * Notifies the expression status change to the 
	 * collections of listeners.
	 * 
	 * @param status the expression status information
	 */
	void notifyExpressionStatusChanged(ExpressionStatus status);
	
}
