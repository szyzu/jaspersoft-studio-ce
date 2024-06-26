/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.properties.dialog;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TPropertyLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		PropertyDTO dto = (PropertyDTO) element;
		switch (columnIndex) {
		case 0:
			return dto.getName();
		case 1:
			return dto.getValue();
		}
		return ""; //$NON-NLS-1$
	}
}
