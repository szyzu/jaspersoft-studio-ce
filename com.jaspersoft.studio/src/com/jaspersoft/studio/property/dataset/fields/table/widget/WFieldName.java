/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.dataset.fields.table.widget;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.property.dataset.fields.table.TColumn;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;

public class WFieldName extends WProperty {
	private JRDesignDataset dataset;

	public WFieldName(Composite parent, TColumn c, Object element, JasperReportsConfiguration jConfig) {
		super(parent, c, element, jConfig);
		this.dataset = (JRDesignDataset) c.getValue();
	}

	@Override
	public void setValue(Object value) {
		if (element == null)
			return;
		JRDesignField field = (JRDesignField) element;
		boolean exists = false;
		for (JRField f : dataset.getFieldsList()) {
			exists = f.getName().equals(value);
			if (exists)
				break;
		}
		if (!exists) {
			String oldName = field.getName();
			dataset.getFieldsMap().remove(oldName);
			field.setName((String) value);
			dataset.getFieldsMap().put(field.getName(), field);

			Map<String, JRSortField> sortFields = dataset.getSortFieldsMap();
			JRSortField sf = sortFields.get(oldName + "|" + SortFieldTypeEnum.FIELD.getName());
			// If a field with the same name is not present or if it is present but with a different type then show it
			if (sf != null) {
				dataset.removeSortField(sf);
				((JRDesignSortField) sf).setName(field.getName());
				try {
					dataset.addSortField(sf);
				} catch (JRException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
