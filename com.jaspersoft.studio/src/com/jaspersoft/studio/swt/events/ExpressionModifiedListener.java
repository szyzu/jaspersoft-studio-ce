/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.swt.events;

import com.jaspersoft.studio.swt.widgets.WTextExpression;

/**
 * This listener is meant to be used when dealing with expression widgets, like for example the {@link WTextExpression}.
 *  
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 * @see ExpressionModifiedEvent
 * @see WTextExpression
 *
 */
public interface ExpressionModifiedListener {
	/**
	 * Invoked when an expression is modified.
	 * 
	 * @param event the modification event
	 */
	void expressionModified(ExpressionModifiedEvent event);
}
