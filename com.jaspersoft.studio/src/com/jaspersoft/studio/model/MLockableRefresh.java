/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model;

import java.util.HashSet;

/**
 * Class extended by the almost root node, for example MPage or 
 * MReport, that are the almost upper node of an editor. In this 
 * node there are the flag to block the refresh of the editor.
 * 
 * @author Orlandin Marco
 *
 */
public abstract class MLockableRefresh extends APropertyNode {

	private static final long serialVersionUID = 9079670894822888806L;
	
	/**
	 * Instead of a flag an HashSet is used, in this way the lock 
	 * can be required from more sources and the refresh will be enabled
	 * only when there are no lock. The key in the map is the reference of 
	 * who has requested the lock
	 */
	protected HashSet<Object> lockMap = new HashSet<Object>(); 	
	

	public MLockableRefresh() {
		super();
	}

	public MLockableRefresh(ANode parent, int newIndex) {
		super(parent, newIndex);
	}

	/*@Override
	public void propertyChange(PropertyChangeEvent evt) {
		synchronized(lockMap){
		//	if (lockMap.isEmpty()) 
				super.propertyChange(evt);
		}
	}*/
	
	/*@Override
	protected void firePropertyChange(PropertyChangeEvent evt) {
		synchronized(lockMap){
			if (lockMap.isEmpty()) 
				super.firePropertyChange(evt);
		}
	}*/

	/**
	 * Create or remove a lock for the refresh 
	 * 
	 * @param ignore true if the refresh should be ignored, false otherwise
	 * @param caller who wants to put or to remove its previous lock
	 */
	public void setIgnoreEvents(boolean ignore, Object caller){
		synchronized (lockMap) {
			if (ignore) lockMap.add(caller);
			else lockMap.remove(caller);
		}	
	}

	/**
	 * check if there are locks
	 * 
	 * @return true if there are at least a lock on the refresh, false otherwise
	 */
	public boolean isRefreshEventIgnored(){
		synchronized (lockMap) {
			return !lockMap.isEmpty();
		}
	}

}
