/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.compatibility.JRXmlWriterHelper;
import com.jaspersoft.studio.compatibility.VersionConstants;
import com.jaspersoft.studio.editor.AbstractJRXMLEditor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.preferences.ExpressionEditorPreferencePage;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.SelectionHelper;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.expressions.annotations.JRExprFunctionBean;

/**
 * Utility methods that allow the user to work with the expression editor "world".
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public class ExpressionEditorSupportUtil {
	
	public static final int EXPEDITOR_INITIAL_HEIGHT = 680;
	public static final int EXPEDITOR_INITIAL_WIDTH = 750;
	private static final IExpressionEditorSupportFactory supportFactory;
	private static final Map<String, ExpressionEditorSupport> editorSupports;
	private static ExpressionContext currentContext;
	private static Boolean isExpressionEditorDialogOpen=Boolean.FALSE;
	private static boolean showBuiltInParameters=true;
	private static boolean showBuiltInVariables=true;
	private static ExpObjectSortingInfo variablesSorting=new ExpObjectSortingInfo(ExpObject.TYPE_VARIABLE);
	private static ExpObjectSortingInfo fieldsSorting=new ExpObjectSortingInfo(ExpObject.TYPE_FIELD);
	private static ExpObjectSortingInfo parametersSorting=new ExpObjectSortingInfo(ExpObject.TYPE_PARAM);
	private static ExpObjectSortingInfo rbkeysSorting=new ExpObjectSortingInfo(ExpObject.TYPE_RBKEY,false,true);

	static {
		supportFactory = JaspersoftStudioPlugin.getExtensionManager().getExpressionEditorSupportFactory();
		editorSupports = new HashMap<String, ExpressionEditorSupport>(3); // java, groovy, javascript are languages that
																																			// should be supported
	}

	/**
	 * Checks if it exists a support factory for the Expression Editor.
	 * 
	 * @return <code>true</code> if a support factory exists, <code>false</code> otherwise
	 */
	public static boolean existsExpressionEditorSupportFactory() {
		return supportFactory != null;
	}

	/**
	 * Gets the current support factory for the Expression Editor.
	 * 
	 * @return the support factory, <code>null</code> if none
	 */
	public static IExpressionEditorSupportFactory getExpressionEditorSupportFactory() {
		return supportFactory;
	}

	/**
	 * Checks if it exists editor support for the specified language.
	 * 
	 * @param language
	 *          the language for which support is searched
	 * @return <code>true</code> if language is supported, <code>false</code> otherwise
	 */
	public static boolean existsSupportForLanguage(String language) {
		return getEditorSupportForLanguage(language) != null;
	}

	/**
	 * Gets the editor support for the specified language.
	 * 
	 * @param language
	 *          the language for which support is searched
	 * @return the editor support, <code>null</code> if none
	 */
	public static ExpressionEditorSupport getEditorSupportForLanguage(String language) {
		if (supportFactory == null || language == null) {
			return null;
		}
		ExpressionEditorSupport expressionEditorSupport = null;
		if (!editorSupports.containsKey(language)) {
			expressionEditorSupport = supportFactory.getExpressionEditorSupport(language);
			if (expressionEditorSupport != null) {
				editorSupports.put(language, expressionEditorSupport);
			}
		} else {
			expressionEditorSupport = editorSupports.get(language);
		}
		return expressionEditorSupport;
	}

	/**
	 * Gets the editor support for a specific expression context.
	 * <p>
	 * 
	 * The expression context is used to retrieve the language from the JasperDesign model. 
	 * If none can be found then as fallback solution it tries to invoke the 
	 * {@link #getEditorSupportForReportLanguage()} method.
	 * 
	 * @param exprContext
	 *          the expression context
	 * @return the editor supports, <code>null</code> if none
	 */
	public static ExpressionEditorSupport getEditorSupport(ExpressionContext exprContext) {
		if(exprContext!=null) {
			JasperDesign jasperDesign = exprContext.getJasperReportsConfiguration().getJasperDesign();
			if(jasperDesign!=null) {
				return ExpressionEditorSupportUtil.getEditorSupportForLanguage(jasperDesign.getLanguage());
			}
		}
		// fallback solution
		return ExpressionEditorSupportUtil.getEditorSupportForReportLanguage();
	}
	
	/**
	 * Gets the editor support for the language attribute set for the report.
	 * 
	 * @return the editor support, <code>null</code> if none
	 */
	public static ExpressionEditorSupport getEditorSupportForReportLanguage() {
		IEditorPart activeJRXMLEditor = SelectionHelper.getActiveJRXMLEditor();
		if (activeJRXMLEditor != null && activeJRXMLEditor instanceof AbstractJRXMLEditor) {
			return getEditorSupportForLanguage(((AbstractJRXMLEditor) activeJRXMLEditor).getModel().getJasperDesign().getLanguage());
		} else {
			return null;
		}
	}

	/**
	 * Gets a "generic" {@link ExpressionContext} object that uses the main report dataset as information.
	 * 
	 * @return a "generic" expression context
	 * @see ExpressionContext
	 */
	public static ExpressionContext getReportExpressionContext() {
		IEditorPart activeJRXMLEditor = SelectionHelper.getActiveJRXMLEditor();
		if (activeJRXMLEditor != null && activeJRXMLEditor instanceof AbstractJRXMLEditor) {
			final ANode mroot = (ANode) ((AbstractJRXMLEditor) activeJRXMLEditor).getModel();
			if (mroot != null) {
				final ANode mreport = (ANode) mroot.getChildren().get(0);
				JRDataset mainDS = mreport.getJasperDesign().getMainDataset();
				ExpressionContext exprContext = new ExpressionContext((JRDesignDataset) mainDS,
						mreport.getJasperConfiguration());
				return exprContext;
			}
		}
		return null;
	}
	
	/**
	 * Gets a comprehensive expression context based on the currently opened report.
	 * It uses all the datasets (main one included) and the crosstabs as source of information.
	 * 
	 * @return an expression context comprehensive of information coming from all crosstabs and datasets
	 * @see ExpressionContext
	 */
	public static ExpressionContext getReportExtendedExpressionContext(){
		IEditorPart activeJRXMLEditor = SelectionHelper.getActiveJRXMLEditor();
		if (activeJRXMLEditor != null && activeJRXMLEditor instanceof AbstractJRXMLEditor) {
			final ANode mroot = (ANode) ((AbstractJRXMLEditor) activeJRXMLEditor).getModel();
			if (mroot != null) {
				ANode mreport = (ANode) mroot.getChildren().get(0);
				JasperDesign jd = mreport.getJasperDesign();
				JRDataset mainDS = jd.getMainDataset();
				ExpressionContext exprContext = new ExpressionContext((JRDesignDataset) mainDS,
						mreport.getJasperConfiguration());
				// add all other datasets
				for(JRDataset ds : jd.getDatasets()){
					if(ds instanceof JRDesignDataset) {
						exprContext.addDataset((JRDesignDataset) ds);
					}
				}
				// and crosstabs
				for(JRCrosstab ct : ModelUtils.getAllCrosstabs(jd)){
					if(ct instanceof JRDesignCrosstab) {
						exprContext.addCrosstab((JRDesignCrosstab) ct);
					}
				}
				return exprContext;
			}
		}
		return null;
	}

	/**
	 * Add a list of static imports to the specified {@link JasperDesign} instance in order to correctly use the functions
	 * library inside the expressions.
	 * 
	 * @param jd
	 *          the jasper design object to be enriched
	 * @param jrContext
	 *          the JasperReports context associated
	 */
	@Deprecated
	public static void addFunctionsLibraryImports(JasperDesign jd, JasperReportsConfiguration jrContext) {
		Assert.isNotNull(jd);
		Collection<JRExpression> collectedExpressions = JRExpressionCollector.collector(jrContext, jd).getExpressions();
		List<String> libraryClasses = getStaticImportsForExpressions(collectedExpressions);
		for (String clazzName : libraryClasses) {
			jd.addImport("static " + clazzName + ".*");
		}
	}

	/**
	 * Remove a list of static imports to the specified {@link JasperDesign} instance.
	 * <p>
	 * 
	 * The imports removed are those ones needed by the functions library used in the new expression editor.
	 * 
	 * @param jd
	 *          the jasper design object to be updated
	 */
	public static void removeFunctionsLibraryImports(JasperDesign jd) {
		Assert.isNotNull(jd);
		List<String> libraryClasses = FunctionsLibraryUtil.getLibraryClasses();
		for (String clazzName : libraryClasses) {
			jd.removeImport("static " + clazzName + ".*");
		}
	}

	/**
	 * Update the functions library information for the specified jasper design.
	 * <p>
	 * 
	 * If allowed the static imports related to the functions library are added to the jasper design, otherwise they are
	 * removed.
	 * 
	 * @param jasperDesign
	 *          the jasper design to modify
	 * @param jrContext
	 *          the JasperReports context associated
	 * 
	 * @see ExpressionEditorPreferencePage#P_INCLUDE_FUCTIONS_LIBRARY_IMPORTS
	 */
	@Deprecated
	public static void updateFunctionsLibraryImports(JasperDesign jasperDesign, JasperReportsConfiguration jrContext) {
		Assert.isNotNull(jasperDesign);
		// Always remove previously set functions library imports
		ExpressionEditorSupportUtil.removeFunctionsLibraryImports(jasperDesign);
		// Add the imports needed for the functions library, if preference is set
		boolean useImports = jrContext
				.getPropertyBoolean(ExpressionEditorPreferencePage.P_INCLUDE_FUCTIONS_LIBRARY_IMPORTS, false);
		if (useImports && JRXmlWriterHelper.isCompatibleVersionMinor(jrContext, VersionConstants.VERSION_5_1_0, true)) {
			ExpressionEditorSupportUtil.addFunctionsLibraryImports(jasperDesign, jrContext);
		}
	}

	/**
	 * Retrieves a list of needed static imports for expression functions depending on the specified list of
	 * {@link JRExpression}.
	 * 
	 * @param expressions
	 *          a collection of expressions from which to extract the needed static imports
	 * @return a list of static imports that should be added to the main report
	 */
	@Deprecated
	public static List<String> getStaticImportsForExpressions(Collection<JRExpression> expressions) {
		Set<String> importsSet = new HashSet<String>();
		for (JRExpression jre : expressions) {
			List<JRExprFunctionBean> functions = FunctionsLibraryUtil.findFunctions(jre);
			for (JRExprFunctionBean f : functions) {
				importsSet.add(f.getFunctionClassName());
			}
		}
		return Arrays.asList(importsSet.toArray(new String[] {}));
	}

	/**
	 * Sets the current expression context that is currently used in the Expression Editor UI.
	 * <p>
	 * 
	 * Should be reset to <code>null</null> when the editor UI is closed.
	 * 
	 * @param exprContext
	 *          the current expression context to set
	 */
	public static void setCurrentExpressionContext(ExpressionContext exprContext) {
		currentContext = exprContext;
	}

	/**
	 * @return the current expression context if any, <code>null</code> otherwise
	 */
	public static ExpressionContext getCurrentExpressionContext() {
		return currentContext;
	}

	/**
	 * @return the current expression context if any is set, otherwise tries to return the default expression context for
	 *         the currently opened report
	 */
	public static ExpressionContext safeGetCurrentExpressionContext() {
		ExpressionContext expContext = getCurrentExpressionContext();
		if (expContext == null) {
			return getReportExpressionContext();
		} else {
			return expContext;
		}
	}

	/**
	 * Creates and returns a wizard dialog for the expression editor wizard passed as parameter.
	 * <p>
	 * 
	 * NOTE: We need to ensure that only one expression editor dialog is open at a time.
	 * Unfortunately in Linux it seems that the dialog does open modeless, while in Windows and MacOSX
	 * it opens correctly as modal.
	 * 
	 * @param parentShell the parent shell
	 * @param newWizard the wizard that the dialog will work on
	 * @return the wizard dialog newly created instance, <code>null</code> if it can not be created
	 */
	public static WizardDialog getExpressionEditorWizardDialog(Shell parentShell,Wizard newWizard){
		synchronized (isExpressionEditorDialogOpen) {
			if(isExpressionEditorDialogOpen){
				return null;
			}
			else {
				isExpressionEditorDialogOpen=Boolean.TRUE;
				return new ExpressionPersistentWizardDialog(parentShell, newWizard);
			}
		}
	}
	
	/**
	 * Updates the dedicated flag once the expression editor dialog is closed.
	 */
	public static void notifyExpressionEditorDialogClosing() {
		synchronized (isExpressionEditorDialogOpen) {
			isExpressionEditorDialogOpen=Boolean.FALSE;
		}
	}
	
	/**
	 * @return <code>true</code> if expression editor dialog is open, <code>false</code> otherwise
	 */
	public static boolean isExpressionEditorDialogOpen(){
		return isExpressionEditorDialogOpen.booleanValue();
	}

	/**
	 * @return <code>true</code> if the built-in parameters should be shown in the expression editor,
	 * 				<code>false</code> otherwise.
	 */
	public static synchronized boolean isShowBuiltInParameters() {
		return showBuiltInParameters;
	}

	/**
	 * Changes the flag that decides if built-in parameters should be shown in the expression editor.
	 * It returns the status just set.
	 * 
	 * @return the new status
	 */
	public static synchronized boolean toggleShowBuiltInParameters() {
		showBuiltInParameters = !showBuiltInParameters;
		return showBuiltInParameters;
	}

	/**
	 * @return <code>true</code> if the built-in variables should be shown in the expression editor,
	 * 				<code>false</code> otherwise.
	 */
	public static synchronized boolean isShowBuiltInVariables() {
		return showBuiltInVariables;
	}

	/**
	 * Changes the flag that decides if built-in variables should be shown in the expression editor.
	 * It returns the status just set.
	 * 
	 * @return the new status
	 */
	public static synchronized boolean toggleShowBuiltInVariables() {
		showBuiltInVariables = !showBuiltInVariables;
		return showBuiltInVariables;
	}
	
	/**
	 * Changes the flag that decides if items should be shown in increasing alphabetical order.
	 * 
	 * @param the type of {@link ExpObject}
	 * @return the new status
	 */
	public static synchronized boolean toggleSortIncreaseItems(int type) {
		return getSortingInfo(type).toggleSortIncreaseItems();
	}

	/**
	 * Changes the flag that decides if items should be shown in decreasing alphabetical order.
	 * 
	 * @param the type of {@link ExpObject}
	 * @return the new status
	 */
	public static synchronized boolean toggleSortDecreaseItems(int type) {
		return getSortingInfo(type).toggleSortDecreaseItems();
	}
	
	/**
	 * @param the type of {@link ExpObject}
	 * @return <code>true</code> if the items should be shown in decreasing alphabetical order, <code>false</code>
	 *         otherwise.
	 */
	public static synchronized boolean isSortDecreaseItems(int type) {
		return getSortingInfo(type).isSortDecreaseItems();
	}

	/**
	 * @param the type of {@link ExpObject}
	 * @return <code>true</code> if the items should be shown in increasing alphabetical order, <code>false</code>
	 *         otherwise.
	 */
	public static synchronized boolean isSortIncreaseItems(int type) {
		return getSortingInfo(type).isSortIncreaseItems();
	}
	
	/**
	 * @param the type of {@link ExpObject}
	 * @return <code>true</code> if the items should not be ordered, <code>false</code> otherwise.
	 */
	public static synchronized boolean isShowUnorderedItems(int type) {
		ExpObjectSortingInfo sortingInfo = getSortingInfo(type);
		return !sortingInfo.isSortIncreaseItems() && !sortingInfo.isSortDecreaseItems();
	}
	
	private static ExpObjectSortingInfo getSortingInfo(int type) {
		switch (type) {
		case ExpObject.TYPE_FIELD:
			return fieldsSorting;
		case ExpObject.TYPE_PARAM:
			return parametersSorting;
		case ExpObject.TYPE_RBKEY:
			return rbkeysSorting;
		case ExpObject.TYPE_VARIABLE:
			return variablesSorting;
		default:
			throw new RuntimeException("The specified expession object type is invalid!");
		}
	}
	
	/**
	 * Checks if the flag for remembering expression editor window size is set. 
	 * 
	 * @return <code>true</code> if the flag is set, <code>false</code> otherwise
	 */
	public static boolean shouldRememberExpEditorDialogSize() {
		return JaspersoftStudioPlugin.getInstance().getPreferenceStore().getBoolean(ExpressionEditorPreferencePage.P_REMEMBER_EXPEDITOR_SIZE);
	}

	/**
	 * Checks if the flag for remembering expression editor window location is set. 
	 * 
	 * @return <code>true</code> if the flag is set, <code>false</code> otherwise
	 */
	public static boolean shouldRememberExpEditorDialogLocation() {
		return JaspersoftStudioPlugin.getInstance().getPreferenceStore().getBoolean(ExpressionEditorPreferencePage.P_REMEMBER_EXPEDITOR_LOCATION);
	}
	
	/**
	 * Stores the details about the size of the expression editor dialog.
	 *  
	 * @param width dialog width
	 * @param height dialog height
	 */
	public static void saveExpEditorDialogSize(int width, int height) {
		JaspersoftStudioPlugin.getInstance().getPreferenceStore().setValue(ExpressionEditorPreferencePage.V_EXPEDITOR_SIZE_WIDTH, width);
		JaspersoftStudioPlugin.getInstance().getPreferenceStore().setValue(ExpressionEditorPreferencePage.V_EXPEDITOR_SIZE_HEIGHT, height);
	}
	
	/**
	 * Stores the details about the location of the expression editor dialog.
	 *  
	 * @param x dialog X location
	 * @param y dialog Y location
	 */
	public static void saveExpEditorDialogLocation(int x, int y) {
		JaspersoftStudioPlugin.getInstance().getPreferenceStore().setValue(ExpressionEditorPreferencePage.V_EXPEDITOR_LOCATION_X, x);
		JaspersoftStudioPlugin.getInstance().getPreferenceStore().setValue(ExpressionEditorPreferencePage.V_EXPEDITOR_LOCATION_Y, y);
	}
	
	public static Point getExpEditorDialogLocation(){
		IPreferenceStore store = JaspersoftStudioPlugin.getInstance().getPreferenceStore();
		if (store.contains(ExpressionEditorPreferencePage.V_EXPEDITOR_LOCATION_X) &&
					(store.contains(ExpressionEditorPreferencePage.V_EXPEDITOR_LOCATION_Y))){
			return new Point(store.getInt(ExpressionEditorPreferencePage.V_EXPEDITOR_LOCATION_X),
												store.getInt(ExpressionEditorPreferencePage.V_EXPEDITOR_LOCATION_Y));
		} return null;
	}
	
	public static Point getExpEditorDialogSize(){
		IPreferenceStore store = JaspersoftStudioPlugin.getInstance().getPreferenceStore();
		if (store.contains(ExpressionEditorPreferencePage.V_EXPEDITOR_SIZE_WIDTH) &&
					(store.contains(ExpressionEditorPreferencePage.V_EXPEDITOR_SIZE_HEIGHT))){
			return new Point(store.getInt(ExpressionEditorPreferencePage.V_EXPEDITOR_SIZE_WIDTH),
												store.getInt(ExpressionEditorPreferencePage.V_EXPEDITOR_SIZE_HEIGHT));
		} else return null;
	}
	
}
