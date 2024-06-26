/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.utils;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.preferences.GlobalPreferencePage;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRScriptlet;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRValidationException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JRParameterDefaultValuesEvaluator;
import net.sf.jasperreports.engine.util.JRExpressionUtil;
import net.sf.jasperreports.utils.JasperReportsModelUtils;

public class ExpressionUtil {

	/**
	 * Cache of the expression interpreter for every dataset, the key is the
	 * reference to the dataset for whose the interpreter was created
	 */
	private static Map<JRDesignDataset, ExpressionInterpreter> datasetsIntepreters = Collections
			.synchronizedMap(new HashMap<JRDesignDataset, ExpressionInterpreter>());

	/**
	 * Set the listener (only where they are not already set) to listen the
	 * changes to a dataset and discard the cached interpreter for that dataset
	 * when they happen. The listeners are set on both the dataset and his
	 * children
	 * 
	 * @param parentDataset
	 */
	private static void setDatasetListners(JRDesignDataset parentDataset) {
		addEventIfnecessary(parentDataset, parentDataset);
		for (JRVariable var : parentDataset.getVariables()) {
			if (var instanceof JRChangeEventsSupport)
				addEventIfnecessary((JRChangeEventsSupport) var, parentDataset);
		}

		for (JRSortField sortField : parentDataset.getSortFields()) {
			if (sortField instanceof JRChangeEventsSupport)
				addEventIfnecessary((JRChangeEventsSupport) sortField, parentDataset);
		}

		for (JRParameter parameter : parentDataset.getParameters()) {
			if (parameter instanceof JRChangeEventsSupport)
				addEventIfnecessary((JRChangeEventsSupport) parameter, parentDataset);
		}

		for (JRField field : parentDataset.getFields()) {
			if (field instanceof JRChangeEventsSupport)
				addEventIfnecessary((JRChangeEventsSupport) field, parentDataset);
		}
		for (JRScriptlet scriptlet : parentDataset.getScriptlets()) {
			if (scriptlet instanceof JRChangeEventsSupport)
				addEventIfnecessary((JRChangeEventsSupport) scriptlet, parentDataset);
		}
	}

	/**
	 * Add to the a report design a design change listener, but only if it
	 * hasen't already a listener of this type
	 * 
	 * @param design the element
	 * @param parentDataset config the configuration of the report that will be
	 * used to remove from the cache every intepreter that was created for the
	 * report, when this event is called
	 */
	private static void setDesignListener(JasperDesign design, JasperReportsConfiguration config) {
		if (!hasListener(design, DesignChanges.class)) {
			design.getEventSupport().addPropertyChangeListener(new DesignChanges(config));
		}
	}

	/**
	 * Add to an object that support the event change a dataset change listener,
	 * but only if it hasen't already a listener of this type
	 * 
	 * @param support the element
	 * @param parentDataset the dataset that will be removed from the cache if
	 * the listener is called
	 */
	private static void addEventIfnecessary(JRChangeEventsSupport support, JRDesignDataset parentDataset) {
		if (!hasListener(support, DatasetChanges.class)) {
			support.getEventSupport().addPropertyChangeListener(new DatasetChanges(parentDataset));
		}
	}

	/**
	 * check if an object has a listener of a specific type
	 * 
	 * @param support object from where the listeners are obtained
	 * @param listenerClass class to search
	 * @return true if the support object has a listener that has exactly the
	 * type listenerClass, otherwise false
	 */
	private static boolean hasListener(JRChangeEventsSupport support, Class<?> listenerClass) {
		PropertyChangeListener[] listeners = support.getEventSupport().getPropertyChangeListeners();
		for (PropertyChangeListener listener : listeners) {
			if (listener.getClass() == listenerClass)
				return true;
		}
		return false;
	}

	/**
	 * check if the evaluation of the expression is disabled for security
	 * reasons
	 * 
	 * @return true if the expressions should not be evaluated, false otherwise
	 */
	protected static boolean isEvaluationDisabled() {
		return JaspersoftStudioPlugin.getInstance().getPreferenceStore()
				.getBoolean(GlobalPreferencePage.JSS_DISABLE_EXPRESSION_EVALUATION);
	}

	/**
	 * Resolve an expression and return its value or null if it can not be
	 * resolve. First it will try to use a simple evaluation since it is much
	 * faster. If this can't resolve the expression then an interpreter for the
	 * current report is created and cached (since create and interpreter is
	 * very slow)
	 * 
	 * @param exp expression to resolve
	 * @param project project of the report
	 * @param jConfig Configuration of the report to evaluate the expression
	 * @param dataset the context of the expression resolution
	 * @return resolved expression or null it it can't be resolved
	 */
	public static Object cachedExpressionEvaluation(JRExpression exp, JasperReportsConfiguration jConfig,
			JRDesignDataset dataset) {

		// check if the evaluation of the expression is disabled and in case
		// return null
		if (isEvaluationDisabled())
			return null;

		String evaluatedExpression = null;
		String expString = exp != null ? exp.getText() : "";
		try {
			evaluatedExpression = JRExpressionUtil.getSimpleExpressionText(exp);
			if (evaluatedExpression == null && dataset != null) {
				// Unable to interpret the expression, lets try with a more
				// advanced (and slow,
				// so its cached) interpreter
				JasperDesign jd = jConfig.getJasperDesign();
				ExpressionInterpreter interpreter = null;
				boolean interpreterCreated = false;
				synchronized (datasetsIntepreters) {
					interpreter = datasetsIntepreters.get(dataset);
					if (interpreter == null) {
						if (exp != null && jd != null) {
							interpreter = new ExpressionInterpreter(dataset, jd, jConfig);
							interpreterCreated = true;
							datasetsIntepreters.put(dataset, interpreter);
						}
					}
				}
				if (interpreterCreated) {
					// The dataset was added to the cache, check if it has the
					// listener and add them
					// where are needed
					setDatasetListners(dataset);
					setDesignListener(jd, jConfig);
				}
				if (interpreter != null) {
					return interpreter.interpretExpression(expString);

					// JRFillDataset.createCalculator(jConfig, jasperReport,
					// jasperReport.getMainDataset());
					// Object expressionValue =
					// interpreter.interpretExpression(expString);
					// if (expressionValue != null) evaluatedExpression =
					// expressionValue.toString();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return evaluatedExpression;
	}

	public static ExpressionInterpreter getCachedInterpreter(JRDesignDataset ds, JasperDesign jd,
			JasperReportsConfiguration jConfig) {
		ExpressionInterpreter interpreter = null;
		boolean interpreterCreated = false;
		synchronized (datasetsIntepreters) {
			interpreter = datasetsIntepreters.get(ds);
			if (interpreter == null) {
				if (jd != null) {
					interpreter = new ExpressionInterpreter(ds, jd, jConfig);
					interpreterCreated = true;
					datasetsIntepreters.put(ds, interpreter);
				}
			}
			if (interpreterCreated) {
				// The dataset was added to the cache, check if it has the
				// listener and add them
				// where are needed
				setDatasetListners(ds);
				setDesignListener(jd, jConfig);
			}
			return interpreter;
		}
	}

	/**
	 * This method evaluate the expression and convert the result into a string,
	 * can return null
	 * 
	 * @param exp the expression to evaluate
	 * @param jConfig the current jasper configuration
	 * @param dataset the dataset to where the expression belong
	 * @return the evaluated expression as string if it can be interpreted or
	 * null otherwise
	 */
	public static String cachedExpressionEvaluationString(JRExpression exp, JasperReportsConfiguration jConfig,
			JRDesignDataset dataset) {
		Object obj = cachedExpressionEvaluation(exp, jConfig, dataset);
		if (obj != null)
			return obj.toString();
		return null;
	}

	/**
	 * Resolve an expression and return its value or null if it can not be
	 * resolve. First it will try to use a simple evaluation since it is much
	 * faster. If this can't resolve the expression then an interpreter for the
	 * current report is created and cached (since create and interpreter is
	 * very slow)
	 * 
	 * @param exp expression to resolve
	 * @param jConfig Configuration of the report to evaluate the expression
	 * @return resolved expression or null it it can't be resolved
	 */
	public static Object cachedExpressionEvaluation(JRExpression exp, JasperReportsConfiguration jConfig) {
		JRDesignDataset dataset = null;
		if (jConfig.getJasperDesign() != null) {
			dataset = jConfig.getJasperDesign().getMainDesignDataset();
		}
		return cachedExpressionEvaluation(exp, jConfig, dataset);
	}

	/**
	 * Resolve an expression and convert it to a string. The resolution is done
	 * using
	 * {@link #cachedExpressionEvaluation(JRExpression, JasperReportsConfiguration)}
	 * and if the result is not null then it is returned
	 * 
	 * @param exp the expression to resolve
	 * @param jConfig the current configuration
	 * @return the resolved expression as string or null if it can't be resolved
	 */
	public static String cachedExpressionEvaluationString(JRExpression exp, JasperReportsConfiguration jConfig) {
		Object obj = cachedExpressionEvaluation(exp, jConfig);
		if (obj != null)
			return obj.toString();
		return null;
	}

	/**
	 * Remove an expression interpreter from the cache. An intepreter must be
	 * removed when something change in the dataset that has generated it
	 * 
	 * @param dataset dataset for whose the intepreter was created
	 */
	public static void removeCachedInterpreter(JRDesignDataset dataset) {
		datasetsIntepreters.remove(dataset);
	}

	/**
	 * Remove all the interpreters cached for a report
	 * 
	 * @param reportsConfiguration Configuration for the report
	 */
	public static void removeAllReportInterpreters(JasperReportsConfiguration reportsConfiguration) {
		synchronized (datasetsIntepreters) {
			List<JRDesignDataset> datasetsToRemove = new ArrayList<>();
			for (Entry<JRDesignDataset, ExpressionInterpreter> intepreter : datasetsIntepreters.entrySet()) {
				if (intepreter.getValue().getJasperReportsConfiguration() == reportsConfiguration) {
					datasetsToRemove.add(intepreter.getKey());
				}
			}
			for (JRDesignDataset dataset : datasetsToRemove) {
				datasetsIntepreters.remove(dataset);
			}
		}
	}

	/**
	 * Remove an expression interpreter from the cache
	 * 
	 * @param jConfig JasperReportConfiguration project for which the
	 * interpreter should be removed
	 */
	public static void removeCachedInterpreter(JasperReportsConfiguration jConfig) {
		if (jConfig != null) {
			removeCachedInterpreter(jConfig.getJasperDesign().getMainDesignDataset());
		}
	}

	public static final String eval(JRExpression expr, JasperReportsConfiguration jConfig, JasperDesign jd) {
		if (expr == null)
			return null;
		if (jd == null)
			return JRExpressionUtil.getSimpleExpressionText(expr);

		Object eval = eval(expr, jd.getMainDesignDataset(), jConfig, jd);
		if (eval != null)
			return eval.toString();
		return null;
	}

	public static final String eval(JRExpression expr, JasperReportsConfiguration jConfig) {
		return eval(expr, jConfig, jConfig.getJasperDesign());
	}

	public static final Object eval(JRExpression expr, JRDataset jrd, JasperReportsConfiguration jConfig) {
		return eval(expr, jrd, jConfig, jConfig.getJasperDesign());
	}

	public static final Object eval(JRExpression expr, JRDataset jrd, JasperReportsConfiguration jConfig,
			JasperDesign jd) {
		if (expr == null || jrd == null || jd == null || isEvaluationDisabled())
			return null;
		return getInterpreter((JRDesignDataset) jrd, jConfig, jd).interpretExpression(expr.getText());
	}

	public static final ExpressionInterpreter getInterpreter(JRDesignDataset jrd, JasperReportsConfiguration jConfig,
			JasperDesign jd) {
		if (jrd == null || jd == null)
			return null;
		return new ExpressionInterpreter(jrd, jd, jConfig);
	}

	public static void initBuiltInParameters(JasperReportsConfiguration jrConfig, JasperReport jr) throws JRException {
		initBuiltInParameters(jrConfig, jr, null, jrConfig.getJRParameters());
	}

	public static void initBuiltInParameters(JasperReportsConfiguration jrConfig, JasperReport jr, JRDataset ds,
			Map<String, Object> inmap) throws JRException {
		Map<String, Object> prms = inmap;
		if (ds != null) {
			JasperDesign jd = new JasperDesign();
			jd.setName(jrConfig.getJasperDesign().getName());
			for (JRParameter prm : ds.getParameters()) {
				if (!jd.getParametersMap().containsKey(prm.getName())) {
					JRDesignParameter np = new JRDesignParameter();
					np.setName(prm.getName());
					np.setDefaultValueExpression(prm.getDefaultValueExpression());
					np.setValueClass(prm.getValueClass());
					jd.addParameter(np);
				}
			}
			JasperReportsModelUtils.addImportStatements(jrConfig.getJasperDesign(), jd);
			jr = JasperCompileManager.getInstance(jrConfig).compile(jd);
		} else if (jr == null) {
			try {
				jr = JasperCompileManager.getInstance(jrConfig).compile(jrConfig.getJasperDesign());
			} catch (JRValidationException ex) {
				// The original report doesn't compile. Try to create a dummy
				// report with the
				// parameters of the original, used only
				// for the compilation and initialization of the parameters
				JasperDesign jd = new JasperDesign();
				jd.setName(jrConfig.getJasperDesign().getName());
				for (JRParameter prm : jrConfig.getJasperDesign().getParameters()) {
					if (!jd.getParametersMap().containsKey(prm.getName())) {
						JRDesignParameter np = new JRDesignParameter();
						np.setName(prm.getName());
						np.setDefaultValueExpression(prm.getDefaultValueExpression());
						np.setValueClass(prm.getValueClass());
						jd.addParameter(np);
					}
				}
				JasperReportsModelUtils.addImportStatements(jrConfig.getJasperDesign(), jd);
				jr = JasperCompileManager.getInstance(jrConfig).compile(jd);
			}
		}
		if (prms == null) {
			prms = new HashMap<>();
		}
		try {
			Map<String, Object> newP = JRParameterDefaultValuesEvaluator.evaluateParameterDefaultValues(jrConfig, jr,
					prms);
			// if all is ok, let's replace parameters
			prms.putAll(newP);
		} catch (JRExpressionEvalException e) {
			// let's try to eliminate wrong parameters
			if (e.getExpression() != null)
				for (JRParameter p : jr.getParameters()) {
					if (p.isSystemDefined())
						continue;
					if (p.getDefaultValueExpression() == e.getExpression()) {
						JasperDesign jd = new JasperDesign();
						jd.setName(jr.getName());
						for (JRParameter prm : jr.getParameters()) {
							if (prm.isSystemDefined() || prm == p)
								continue;
							JRDesignParameter np = new JRDesignParameter();
							np.setName(prm.getName());
							np.setDefaultValueExpression(prm.getDefaultValueExpression());
							np.setValueClass(prm.getValueClass());
							jd.addParameter(np);
						}
						initBuiltInParameters(jrConfig, JasperCompileManager.getInstance(jrConfig).compile(jd), ds,
								inmap);
						break;
					}
				}
			throw e;
		}
		removeAllReportInterpreters(jrConfig);
	}

	/**
	 * @return an expression with empty string
	 */
	public static final JRDesignExpression getEmptyStringExpression() {
		return new JRDesignExpression("\"\"");
	}

	/**
	 * Compare two expressions and check if the text inside them is the same or
	 * if they are both null
	 * 
	 * @param exp1 the first expression, can be null
	 * @param exp2 the second expression, can be null
	 * @return true if the content of the expressions is the same, false
	 * otherwise
	 */
	public static boolean ExpressionEquals(JRExpression exp1, JRExpression exp2) {
		if (exp1 == null)
			return exp2 == null;
		else if (exp2 == null)
			return false;
		else {
			String text1 = exp1.getText();
			String text2 = exp2.getText();
			return ModelUtils.safeEquals(text1, text2);
		}
	}

	/**
	 * Extract the value of a variable provided like an annotation
	 * 
	 * @param variableName the name of the variable
	 * @param expString the text fo the expression, should be not null
	 * @return the value of the variable between double quotes, can be null if
	 * notthing is found
	 */
	public static String extractValueForVariable(String variableName, String expString) {
		int indexStartComments = expString.indexOf("/*");
		int indexEndComments = indexStartComments != -1 ? expString.indexOf("*/", indexStartComments) : -1;
		if (indexEndComments != -1 && indexEndComments != -1) {
			String content = expString.substring(indexStartComments, indexEndComments + 2);
			int annotationPos = content.indexOf(variableName);
			if (annotationPos != -1) {
				boolean variableFound = false;
				boolean parsingError = false;
				boolean equalsFound = false;
				boolean escaping = false;
				int valueStart = -1;
				int valueEnd = -1;
				int currentIndex = annotationPos + variableName.length();
				while (!(variableFound || parsingError)) {
					if (currentIndex < content.length()) {
						char currentChar = content.charAt(currentIndex);
						if (currentChar == '=' && !escaping) {
							if (valueStart == -1) {
								parsingError = equalsFound;
								equalsFound = true;
							}
						} else if (currentChar == '"' && !escaping) {
							if (equalsFound) {
								if (valueStart == -1) {
									valueStart = currentIndex;
								} else {
									valueEnd = currentIndex;
									variableFound = true;
								}
							} else {
								parsingError = true;
							}
						}
						if (currentChar == '\\' && !escaping) {
							escaping = true;
						} else {
							escaping = false;
						}
						currentIndex++;
					} else {
						parsingError = true;
					}
				}
				if (!parsingError && variableFound) {
					return content.substring(valueStart, valueEnd + 1);
				}
			}
		}
		return null;
	}
}
