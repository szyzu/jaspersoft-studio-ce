/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.action.groupby;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;

import com.jaspersoft.studio.data.sql.SQLQueryDesigner;
import com.jaspersoft.studio.data.sql.Util;
import com.jaspersoft.studio.data.sql.action.AAction;
import com.jaspersoft.studio.data.sql.action.table.CreateTable;
import com.jaspersoft.studio.data.sql.dialogs.FromTableColumnsDialog;
import com.jaspersoft.studio.data.sql.messages.Messages;
import com.jaspersoft.studio.data.sql.model.metadata.MSQLColumn;
import com.jaspersoft.studio.data.sql.model.metadata.MSqlTable;
import com.jaspersoft.studio.data.sql.model.query.from.MFrom;
import com.jaspersoft.studio.data.sql.model.query.from.MFromTable;
import com.jaspersoft.studio.data.sql.model.query.groupby.MGroupBy;
import com.jaspersoft.studio.data.sql.model.query.groupby.MGroupByColumn;
import com.jaspersoft.studio.data.sql.model.query.groupby.MGroupByExpression;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;

import net.sf.jasperreports.eclipse.util.Misc;

public class CreateGroupByColumn extends AAction {
	private CreateTable ct;
	private SQLQueryDesigner designer;

	public CreateGroupByColumn(SQLQueryDesigner designer, TreeViewer treeViewer) {
		super(Messages.CreateGroupByColumn_0, treeViewer);
		this.designer = designer;
	}

	@Override
	public boolean calculateEnabled(Object[] selection) {
		super.calculateEnabled(selection);
		return selection != null && selection.length == 1 && isInSelect(selection[0]);
	}

	public static boolean isInSelect(Object element) {
		boolean b = element instanceof MGroupBy || element instanceof MGroupByColumn
				|| element instanceof MGroupByExpression;
		if (b) {
			MFrom mfrom = Util.getKeyword((ANode) ((ANode) element).getRoot(), MFrom.class);
			if (mfrom != null)
				return !Misc.isNullOrEmpty(mfrom.getChildren());
			return false;
		}
		return b;
	}

	@Override
	public void run() {
		FromTableColumnsDialog dialog = new FromTableColumnsDialog(treeViewer.getControl().getShell());
		dialog.setSelection((ANode) selection[0]);
		if (dialog.open() == Window.OK)
			run(dialog.getColumns());
	}

	public void run(Map<MSQLColumn, MFromTable> cols) {
		Object sel = selection[0];
		for (MSQLColumn t : cols.keySet()) {
			MFromTable mftable = cols.get(t);
			if (sel instanceof MGroupBy)
				sel = run(t, mftable, (MGroupBy) sel, 0);
			else if (sel instanceof MGroupByColumn)
				sel = run(t, mftable, (MGroupByColumn) sel);
			else if (sel instanceof MGroupByExpression)
				sel = run(t, mftable, (MGroupByExpression) sel);
		}
		selectInTree(sel);
	}

	public void run(Collection<MSQLColumn> nodes) {
		Object sel = selection[0];
		List<MFromTable> tbls = Util.getFromTables((ANode) sel);
		for (MSQLColumn t : nodes) {
			MSqlTable tbl = (MSqlTable) t.getParent();
			MFromTable mftable = null;
			for (MFromTable ft : tbls) {
				if (ft.getValue().equals(tbl)) {
					mftable = ft;
					break;
				}
			}
			if (mftable == null) {
				if (ct == null)
					ct = new CreateTable(designer, treeViewer);
				ANode r = Util.getQueryRoot((ANode) sel);
				for (INode n : r.getChildren()) {
					if (n instanceof MFrom) {
						mftable = ct.run(tbl, (MFrom) n, -1);
						break;
					}
				}
			}
			if (sel instanceof MGroupBy)
				sel = run(t, mftable, (MGroupBy) sel, 0);
			else if (sel instanceof MGroupByColumn) {
				sel = run(t, mftable, (MGroupByColumn) sel);
			}
		}
		selectInTree(sel);
	}

	protected MGroupByColumn run(MSQLColumn node, MFromTable mfTable, MGroupByColumn mtable) {
		MGroupBy mfrom = (MGroupBy) mtable.getParent();
		return run(node, mfTable, mfrom, mfrom.getChildren().indexOf(mtable) + 1);
	}

	protected MGroupByColumn run(MSQLColumn node, MFromTable mfTable, MGroupByExpression mtable) {
		MGroupBy mfrom = (MGroupBy) mtable.getParent();
		return run(node, mfTable, mfrom, mfrom.getChildren().indexOf(mtable) + 1);
	}

	public MGroupByColumn run(MSQLColumn node, MFromTable mfTable, MGroupBy select, int index) {
		return new MGroupByColumn(select, node, mfTable, index);
	}

}
