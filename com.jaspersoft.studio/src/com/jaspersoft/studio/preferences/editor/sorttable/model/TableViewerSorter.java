/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.editor.sorttable.model;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class TableViewerSorter extends ViewerSorter {

	private int sortingColumn;

	private boolean ascending = true;

	private Viewer viewer;

	private ITableContentProvider contentProvider;

	public TableViewerSorter(Viewer viewer, ITableContentProvider contentProvider) {
		this.viewer = viewer;
		this.contentProvider = contentProvider;
	}

	public int getSortingColumn() {
		return (this.sortingColumn);
	}

	public void setSortingColumn(int columnIndex) {
		this.sortingColumn = columnIndex;
	}

	public boolean isAscending() {
		return (this.ascending);
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public void sort() {
		this.viewer.refresh();
	}

	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	public int compare(Viewer viewer, Object e1, Object e2) {
		int category1 = this.category(e1);
		int category2 = this.category(e2);
		if (category1 != category2) {
			return (category1 - category2);
		}

		Object value1 = this.contentProvider.getColumnValue(e1, this.getSortingColumn());

		Object value2 = this.contentProvider.getColumnValue(e2, this.getSortingColumn());

		if (value1 instanceof String && value2 instanceof String) {

			if (value1 == null) {
				value1 = "";
			}
			if (value2 == null) {
				value2 = "";
			}

			return (this.isAscending() ? this.getComparator().compare((String)value1, (String)value2) : (-this.getComparator().compare(
					(String)value1, (String)value2)));
		} else {
			if (value1 == null && value2 == null) {

				return (0);
			} else if (value1 != null && value2 == null) {

				return (-1);
			} else if (value1 == null && value2 != null) {

				return (1);
			} else if (value1 instanceof Comparable && value2 instanceof Comparable) {
				return (this.isAscending() ? ((Comparable) value1).compareTo(value2) : -((Comparable) value1).compareTo(value2));
			} else {

				return (this.isAscending() ? this.getComparator().compare((String)value1, (String)value2) : (-this.getComparator().compare(
						(String)value1, (String)value2)));
			}
		}
	}
}
