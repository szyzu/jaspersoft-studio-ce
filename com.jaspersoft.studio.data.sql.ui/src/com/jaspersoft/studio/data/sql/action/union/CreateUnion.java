/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.action.union;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

import com.jaspersoft.studio.data.sql.Util;
import com.jaspersoft.studio.data.sql.action.AAction;
import com.jaspersoft.studio.data.sql.messages.Messages;
import com.jaspersoft.studio.data.sql.model.ISubQuery;
import com.jaspersoft.studio.data.sql.model.query.AMKeyword;
import com.jaspersoft.studio.data.sql.model.query.MHaving;
import com.jaspersoft.studio.data.sql.model.query.MUnion;
import com.jaspersoft.studio.data.sql.model.query.expression.MExpressionGroup;
import com.jaspersoft.studio.data.sql.model.query.select.MSetOperator;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;

public class CreateUnion extends AAction {

	public CreateUnion(TreeViewer treeViewer) {
		super(Messages.CreateUnion_0, treeViewer);
	}

	@Override
	public boolean calculateEnabled(Object[] selection) {
		super.calculateEnabled(selection);
		return selection != null && selection.length == 1 && isInSelect(selection[0]);
	}

	public static boolean isInSelect(Object element) {
		return element instanceof AMKeyword && !(element instanceof MExpressionGroup || element instanceof MSetOperator || element instanceof ISubQuery);
	}

	@Override
	public void run() {
		AMKeyword sel = (AMKeyword) selection[0];
		ANode parent = sel.getParent();
		if (sel != null && parent != null) {
			if (parent instanceof ISubQuery)
				parent = parent.getParent();
			MUnion munion = createUnion(parent);
			selectInTree(munion);
			treeViewer.expandToLevel(munion, 1);
		}
	}

	public static MUnion createUnion(ANode parent) {
		List<INode> children = parent.getChildren();
		int i = 0;
		for (i = 0; i < children.size(); i++) {
			INode n = children.get(i);
			if (n instanceof MHaving)
				break;
		}
		MUnion munion = new MUnion(parent, i + 1);
		Util.createSelect(munion);
		return munion;
	}

}
