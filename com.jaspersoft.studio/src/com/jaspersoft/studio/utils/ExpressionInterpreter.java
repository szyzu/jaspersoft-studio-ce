/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.utils;

import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JasperDesign;

import com.jaspersoft.studio.utils.expr.AInterpreter;
import com.jaspersoft.studio.utils.expr.GroovyInterpretter;
import com.jaspersoft.studio.utils.expr.JavaInterpreter;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

/*
 * 
 * @author gtoffoli
 */
public class ExpressionInterpreter {
	
	private AInterpreter interpreter;

	private JasperReportsConfiguration jConfig;
	
	public ExpressionInterpreter(JRDesignDataset dataset, JasperReportsConfiguration jConfig) {
		this(dataset, jConfig.getJasperDesign(), jConfig);
	}

	public ExpressionInterpreter(JRDesignDataset dataset, JasperDesign jasperDesign, JasperReportsConfiguration jConfig) {
		try {
			this.jConfig = jConfig;
			if (jasperDesign.getLanguage().equalsIgnoreCase(JRReport.LANGUAGE_JAVA))
				interpreter = new JavaInterpreter();
			else if (jasperDesign.getLanguage().equalsIgnoreCase(JRReport.LANGUAGE_GROOVY))
				interpreter = new GroovyInterpretter();
			else if (jasperDesign.getLanguage().equalsIgnoreCase("bsh"))
				interpreter = new JavaInterpreter();
			
			if (interpreter != null) interpreter.prepareExpressionEvaluator(dataset, jasperDesign, jConfig);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Return the jasper configuration of the report for whose the interpreter was created
	 * 
	 * @return a JasperReportsConfiguration
	 */
	public JasperReportsConfiguration getJasperReportsConfiguration(){
		return jConfig;
	}

	/**
	 * Try to interpret the java expression passed as argument. If dataset is provided, the parameters are recursively
	 * interpreted. If a classloader is specified, it is used to load classes referred in the expression.
	 */
	public Object interpretExpression(String expression) {
		if (interpreter == null)
			return null;
		return interpreter.interpretExpression(expression);
	}

}
