/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.dataset.fields.table;

import java.util.Set;

import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.properties.PropertyMetadata;

public class TColumn {

	private String propertyName;
	private String label;
	private String description;
	private int weight = 100;
	private String type = "property";
	private boolean readOnly = false;
	private String propertyType = "java.lang.String";
	private String defaultValue;
	private transient PropertyMetadata propertyMetadata;
	private transient Object value;
	private transient Object value1;
	private transient boolean labelEditable = false;
	private transient Set<?> hideEnumValues;

	public void setHideEnumValues(Set<?> hev) {
		hideEnumValues = hev;
	}

	public Set<?> getHideEnumValues() {
		return hideEnumValues;
	}

	public void setLabelEditable(boolean labelEditable) {
		this.labelEditable = labelEditable;
	}

	public boolean isLabelEditable() {
		return labelEditable;
	}

	public PropertyMetadata getPropertyMetadata() {
		return propertyMetadata;
	}

	public void setPropertyMetadata(PropertyMetadata propertyMetadata) {
		this.propertyMetadata = propertyMetadata;
		setPropertyName(propertyMetadata.getName());
		setLabel(Misc.nvl(propertyMetadata.getLabel(), propertyMetadata.getName()));
		setDescription(propertyMetadata.getDescription());
		setPropertyType(propertyMetadata.getValueType());
	}

	public String getDefaultValue() {
		if (defaultValue == null && propertyMetadata != null) {
			String d = propertyMetadata.getDefaultValue();
			if (d != null && d.equals("N/A"))
				d = null;
			return d;
		}
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Object getValue1() {
		return value1;
	}

	public void setValue1(Object value1) {
		this.value1 = value1;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
