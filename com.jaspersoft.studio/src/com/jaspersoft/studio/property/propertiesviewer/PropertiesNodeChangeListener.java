/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.propertiesviewer;

/**
 * Classes which implement this interface provide method that deals
 * with the events that are generated when the node selection inside 
 * a {@link TreePropertiesViewerPanel} widget is changed.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public interface PropertiesNodeChangeListener {

	/**
	 * Sent when a new {@link PropertiesViewerNode} element
	 * is selected in the tree.
	 * 
	 * @param node the new node that is currently selected
	 */
	void nodeChanged(IPropertiesViewerNode node);

}
