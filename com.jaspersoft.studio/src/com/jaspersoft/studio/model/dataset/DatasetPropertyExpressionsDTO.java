/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.dataset;

import java.util.List;

import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.property.descriptor.propexpr.PropertyExpressionDTO;
import com.jaspersoft.studio.property.descriptor.propexpr.PropertyExpressionsDTO;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.design.DesignDatasetPropertyExpression;
import net.sf.jasperreports.engine.type.ExpressionTypeEnum;
import net.sf.jasperreports.engine.type.PropertyEvaluationTimeEnum;

public class DatasetPropertyExpressionsDTO extends PropertyExpressionsDTO {

	public DatasetPropertyExpressionsDTO(Object jrElement, ExpressionContext eContext) {
		super(jrElement, eContext);
	}

	public DatasetPropertyExpressionsDTO(JRPropertyExpression[] propExpressions, JRPropertiesMap propMap,
			Object jrElement, ExpressionContext eContext) {
		this(jrElement, eContext);
		if (propExpressions != null) {
			for (JRPropertyExpression prop : propExpressions) {
				DatasetPropertyExpressionDTO newProp = 
						new DatasetPropertyExpressionDTO(true, prop.getName(), prop.getValueExpression().getText(),
								ExpressionTypeEnum.SIMPLE_TEXT == prop.getValueExpression().getType(),
								((DesignDatasetPropertyExpression) prop).getEvaluationTime());
				newProp.seteContext(eContext);
				newProp.setJrElement(jrElement);
				properties.add(newProp);
			}
		}
		if (propMap != null) {
			for (String prop : propMap.getPropertyNames()) {
				DatasetPropertyExpressionDTO newProp = 
						new DatasetPropertyExpressionDTO(false, prop, propMap.getProperty(prop),false,null);
				newProp.seteContext(eContext);
				newProp.setJrElement(jrElement);
				properties.add(newProp);
			}
		}
	}

	public DatasetPropertyExpressionsDTO(List<PropertyExpressionDTO> properties, Object jrElement,
			ExpressionContext eContext) {
		super(properties, jrElement, eContext);
	}

	@Override
	public boolean addProperty(String name, String value, boolean isExpression, boolean isSimpleText) {
		if (!hasProperty(name, isExpression)) {
			DatasetPropertyExpressionDTO newProp = 
					new DatasetPropertyExpressionDTO(isExpression, name, value,isSimpleText,
					isExpression ? PropertyEvaluationTimeEnum.REPORT : null);
			newProp.seteContext(geteContext());
			newProp.setJrElement(getJrElement());
			properties.add(newProp);
			return true;
		}
		return false;
	}

	/**
	 * Add a property to the list into a specific position, only if a property with the same name and of the same type is
	 * not already present
	 * 
	 * @param name
	 *          the name of the property
	 * @param value
	 *          the value of the property
	 * @param isExpression
	 *          true if the property is an expression property, false if it is a standard property
	 * @param position
	 *          the position where the property should be inserted
	 * @return true if a property with the same name\type was not found and the new one was inserted, false otherwise
	 */
	public boolean addProperty(String name, String value, boolean isExpression, boolean isSimpleText, int position) {
		if (!hasProperty(name, isExpression)) {
			DatasetPropertyExpressionDTO newProp = 
					new DatasetPropertyExpressionDTO(isExpression, name, value,isSimpleText,isExpression ? PropertyEvaluationTimeEnum.REPORT : null);
			newProp.seteContext(geteContext());
			newProp.setJrElement(getJrElement());
			properties.add(position, newProp);
			return true;
		}
		return false;
	}

	@Override
	public DatasetPropertyExpressionsDTO clone() {
		DatasetPropertyExpressionsDTO copy = new DatasetPropertyExpressionsDTO(getJrElement(), geteContext());
		for (PropertyExpressionDTO prop : getProperties()) {
			boolean isExpr = prop.isExpression();
			copy.addProperty(prop.getName(), prop.getValue(), isExpr, prop.isSimpleText());
			if (isExpr) {
				((DatasetPropertyExpressionDTO) copy.getProperty(prop.getName(), isExpr))
						.setEvalTime(((DatasetPropertyExpressionDTO) prop).getEvalTime());
			}
		}
		return copy;
	}
}
