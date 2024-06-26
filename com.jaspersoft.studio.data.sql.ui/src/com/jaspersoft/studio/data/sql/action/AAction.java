/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

public abstract class AAction extends Action {
	protected Object[] selection;
	protected TreeViewer treeViewer;

	public AAction(String text, TreeViewer treeViewer) {
		super(text);
		this.treeViewer = treeViewer;
	}

	public boolean calculateEnabled(ISelection iselection) {
		List<Object> lst = new ArrayList<Object>();
		if (iselection instanceof TreeSelection) {
			for (TreePath tp : ((TreeSelection) iselection).getPaths())
				lst.add(tp.getLastSegment());
		}
		return calculateEnabled(lst.toArray());
	}

	public boolean calculateEnabled(Object[] selection) {
		this.selection = selection;
		return true;
	}

	public void selectInTree(Object sel) {
		treeViewer.refresh(true);
		treeViewer.setSelection(new TreeSelection(new TreePath(new Object[] { sel })));
		treeViewer.reveal(sel);
	}
}
