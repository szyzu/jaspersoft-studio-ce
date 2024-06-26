/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.jrexpressions.ui.support.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.wb.swt.ResourceManager;

import com.jaspersoft.studio.editor.expression.CrosstabTotalVariable;
import com.jaspersoft.studio.editor.expression.ExpObject;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.ExpressionEditorSupportUtil;
import com.jaspersoft.studio.editor.expression.FunctionsLibraryUtil;
import com.jaspersoft.studio.editor.jrexpressions.functions.AdditionalStaticFunctions;
import com.jaspersoft.studio.editor.jrexpressions.ui.JRExpressionsUIPlugin;
import com.jaspersoft.studio.editor.jrexpressions.ui.messages.Messages;
import com.jaspersoft.studio.editor.jrexpressions.ui.support.ObjectCategoryItem;
import com.jaspersoft.studio.editor.jrexpressions.ui.support.ObjectCategoryItem.Category;
import com.jaspersoft.studio.editor.jrexpressions.ui.support.ObjectItemStyledLabelProvider;
import com.jaspersoft.studio.editor.jrexpressions.ui.support.TreeArrayContentProvider;
import com.jaspersoft.studio.preferences.ExpressionEditorPreferencePage;
import com.jaspersoft.studio.utils.RecentExpressions;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabMeasure;
import net.sf.jasperreports.crosstabs.JRCrosstabParameter;
import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabMeasure;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabParameter;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabRowGroup;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.expressions.annotations.JRExprFunctionBean;

/**
 * The details panel (composite) for a specific object category ({@link ObjectCategoryItem}).
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * @see JavaExpressionEditorComposite
 */
public class ObjectCategoryDetailsPanel extends Composite {

	// Widgets stuff
	private TreeViewer categoryContent;
	private Composite additionalDetailsCmp;
	private StackLayout additionalDetailsStackLayout;
	private ToolItem hideBuiltinParams;
	private ToolItem hideBuiltinVariables;
	private ToolItem sortIncreaseItems;
	private ToolItem sortDecreaseItems;
	private ObjectItemStyledLabelProvider categoryContentLblProvider;
	private SashForm panelSashForm;
	private ToolBar buttonsToolbar;
	
	// Support data structures
	private ExpressionContext exprContext;
	private Map<String, Control> additionalDetailControls=new HashMap<String, Control>(); // cache map of the details controls
	private ObjectCategoryItem selItem;
	private List<Object> categoryDetails;
	private boolean functionMode=false;
	private EditingAreaHelper editingAreaInfo;
	private Category previousCategory;

	/**
	 * Creates the details panel composite.
	 * 
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 */
	public ObjectCategoryDetailsPanel(Composite parent, int style) {
		super(parent, style);
		GridLayout layout = new GridLayout(1,true);
		layout.marginWidth=0;
		layout.marginHeight=0;
		this.setLayout(layout);
		panelSashForm = new SashForm(this, SWT.HORIZONTAL);
		final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.widthHint=600;
		panelSashForm.setLayoutData(layoutData);
		
		Composite categoryContentCmp=new Composite(panelSashForm, SWT.NONE);
		final GridLayout layout2 = new GridLayout(1,false);
		layout2.marginWidth=0;
		layout2.marginHeight=0;
		categoryContentCmp.setLayout(layout2);
		
		FilteredTree ft = new FilteredTree(categoryContentCmp, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER, new PatternFilter(), true);
		categoryContent=ft.getViewer();
		GridData catContentGD=new GridData(SWT.FILL, SWT.FILL, true, true);
		catContentGD.heightHint=350;
		ft.setLayoutData(catContentGD);
		ColumnViewerToolTipSupport.enableFor(categoryContent);
		categoryContentLblProvider = new ObjectItemStyledLabelProvider();
		categoryContent.setLabelProvider(categoryContentLblProvider);
		categoryContent.setContentProvider(new TreeArrayContentProvider());
		categoryContent.addSelectionChangedListener(new ISelectionChangedListener() {
			 
			public void selectionChanged(SelectionChangedEvent event) {
				Category currCategory = ObjectCategoryDetailsPanel.this.selItem.getCategory();
				if(!ObjectCategoryItem.Category.RECENT_EXPRESSIONS.equals(currCategory) && 
						!ObjectCategoryItem.Category.USER_DEFINED_EXPRESSIONS.equals(currCategory)){
					Object selItem = ((IStructuredSelection)event.getSelection()).getFirstElement();
					if(selItem!=null){
						refreshAdditionalDetailsUI(selItem);
						if(functionMode && !editingAreaInfo.isUpdate()){
							showFunctionDetailsPanel();
							functionMode=false;
						}
					}
				}
			}

		});
		categoryContent.addDoubleClickListener(new IDoubleClickListener() {
		 
			public void doubleClick(DoubleClickEvent event) {
				Object selObject=((IStructuredSelection)categoryContent.getSelection()).getFirstElement();
				if(selObject instanceof ExpObject){
					// Parameters, Variables, Fields
					editingAreaInfo.setUpdate(true);
					editingAreaInfo.insertAtCurrentLocation(((ExpObject) selObject).getExpression(),false, true);
					editingAreaInfo.setUpdate(false);
				}
				else if (selObject instanceof JRExprFunctionBean){
					// Functions
					editingAreaInfo.setUpdate(true);
					editingAreaInfo.insertAtCurrentLocation(((JRExprFunctionBean) selObject).getId()+"( )",false, false); //$NON-NLS-1$
					editingAreaInfo.moveCaretToNextParenthesis();
					editingAreaInfo.setUpdate(false);
					showFunctionDetailsPanel();
				}
				else if (selObject instanceof String){
					// Recent or user defined expressions
					editingAreaInfo.setUpdate(true);
					editingAreaInfo.insertAtCurrentLocation((String)selObject,false, true);
					editingAreaInfo.setUpdate(false);
				}
			}
		});
		addDragSupport(categoryContent);
		
		buttonsToolbar = new ToolBar(categoryContentCmp, SWT.FLAT);
		buttonsToolbar.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
		hideBuiltinParams = new ToolItem(buttonsToolbar, SWT.CHECK);
		hideBuiltinParams.setImage(
				ResourceManager.getPluginImage(JRExpressionsUIPlugin.PLUGIN_ID, "/resources/icons/filter-parameters.png")); //$NON-NLS-1$
		hideBuiltinParams.setEnabled(false);
		hideBuiltinParams.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInParameters());
		hideBuiltinParams.addSelectionListener(new SelectionAdapter() {
			 
			public void widgetSelected(SelectionEvent e) {
				if(additionalDetailsStackLayout.topControl!=null) {
					additionalDetailsStackLayout.topControl.setVisible(ExpressionEditorSupportUtil.toggleShowBuiltInParameters());
					refreshPanelUI(selItem,true);
				}
				else {
					ExpressionEditorSupportUtil.toggleShowBuiltInParameters();
					refreshPanelUI(selItem,true);
				}
			}
		});
		hideBuiltinParams.setToolTipText(Messages.ObjectCategoryDetailsPanel_HideBuiltInParametersTooltip);
		hideBuiltinVariables = new ToolItem(buttonsToolbar, SWT.CHECK);
		hideBuiltinVariables.setImage(
				ResourceManager.getPluginImage(JRExpressionsUIPlugin.PLUGIN_ID, "/resources/icons/filter-variables.png")); //$NON-NLS-1$
		hideBuiltinVariables.setEnabled(false);
		hideBuiltinVariables.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInVariables());
		hideBuiltinVariables.addSelectionListener(new SelectionAdapter() {
			 
			public void widgetSelected(SelectionEvent e) {
				if(additionalDetailsStackLayout.topControl!=null) {
					additionalDetailsStackLayout.topControl.setVisible(ExpressionEditorSupportUtil.toggleShowBuiltInVariables());
					refreshPanelUI(selItem,true);
				}
				else {
					ExpressionEditorSupportUtil.toggleShowBuiltInVariables();
					refreshPanelUI(selItem,true);
				}
			}
		});
		hideBuiltinVariables.setToolTipText(Messages.ObjectCategoryDetailsPanel_HideBuiltinVars);
		sortIncreaseItems = new ToolItem(buttonsToolbar, SWT.CHECK);
		sortIncreaseItems.setImage(
				ResourceManager.getPluginImage(JRExpressionsUIPlugin.PLUGIN_ID, "/resources/icons/sort_increase.png")); //$NON-NLS-1$
		sortIncreaseItems.setEnabled(true);
		sortIncreaseItems.setSelection(false);
		sortIncreaseItems.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExpressionEditorSupportUtil.toggleSortIncreaseItems(getExpObjectType(selItem));
				sortDecreaseItems.setSelection(false);
				refreshPanelUI(selItem,true);
			}
		});
		sortDecreaseItems = new ToolItem(buttonsToolbar, SWT.CHECK);
		sortDecreaseItems.setImage(
				ResourceManager.getPluginImage(JRExpressionsUIPlugin.PLUGIN_ID, "/resources/icons/sort_decrease.png")); //$NON-NLS-1$
		sortDecreaseItems.setEnabled(true);
		sortDecreaseItems.setSelection(false);
		sortDecreaseItems.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExpressionEditorSupportUtil.toggleSortDecreaseItems(getExpObjectType(selItem));
				sortIncreaseItems.setSelection(false);
				refreshPanelUI(selItem,true);
			}
		});
		
		additionalDetailsCmp=new Composite(panelSashForm, SWT.NONE);
		additionalDetailsCmp.setLayoutData(layoutData);
		additionalDetailsStackLayout=new StackLayout();
		additionalDetailsCmp.setLayout(additionalDetailsStackLayout);
		
		panelSashForm.setWeights(new int[]{40,60});
	}
	
	private int getExpObjectType(ObjectCategoryItem selItem) {
		switch (selItem.getCategory()) {
		case FDATASET:
		case FIELDS:
			return ExpObject.TYPE_FIELD;
		case PDATASET:
		case PARAMETERS:
			return ExpObject.TYPE_PARAM;
		case RESOURCE_KEYS:
			return ExpObject.TYPE_RBKEY;
		case VDATASET:
		case VARIABLES:
			return ExpObject.TYPE_VARIABLE;
		default:
			return -1;
		}
	}

	/**
	 * Refreshes the UI of the panel using the new selected item information.
	 * 
	 * @param selItem the new category selected
	 * @param force flag to force the refresh anyway
	 */
	public void refreshPanelUI(ObjectCategoryItem selItem) {
		refreshPanelUI(selItem,false);
	}
	
	/**
	 * Refreshes the UI of the panel using the new selected item information.
	 * 
	 * @param selItem the new category selected
	 * @param force flag to force the refresh anyway
	 */
	public void refreshPanelUI(ObjectCategoryItem selItem, boolean force) {
		this.selItem=selItem;
		categoryContentLblProvider.setCategory(this.selItem.getCategory());		
		if(force || !selItem.getCategory().equals(previousCategory)){
			updateCategoryChildren(selItem);
			this.previousCategory = this.selItem.getCategory();
		}
		
		// We are inside a JRFunction
		if(selItem.getCategory()==Category.BUILT_IN_FUNCTIONS && 
				editingAreaInfo!=null && editingAreaInfo.getCurrentLibraryFunctionName()!=null){
			final String currFunctionName = editingAreaInfo.getCurrentLibraryFunctionName();
			for(TreeItem item : categoryContent.getTree().getItems()){
				JRExprFunctionBean function = (JRExprFunctionBean)item.getData();
				if(function.getId().equals(currFunctionName)){
					functionMode=true;
					categoryContent.setSelection(new StructuredSelection(item.getData()),true);
					break;
				}
			}
		}

		// Otherwise preselect the first one (if possible)
		else if(categoryContent.getTree().getItemCount()>0){
			ISelection currSelection = categoryContent.getSelection();
			if(currSelection==null || StructuredSelection.EMPTY.equals(currSelection)){
				TreeItem item = categoryContent.getTree().getItem(0);
				if(!item.getData().equals(((StructuredSelection)currSelection).getFirstElement())){
					categoryContent.setSelection(new StructuredSelection(item.getData()),true);
				}
			}
		}
	}

	/*
	 *	Update the list of category children.
	 */
	@SuppressWarnings("unchecked")
	private void updateCategoryChildren(ObjectCategoryItem selItem) {
		categoryContent.getTree().clearAll(true);
		categoryDetails = new ArrayList<Object>();		
		switch (selItem.getCategory()) {
			case PDATASET: 
			case PARAMETERS:
				hideBuiltinVariables.setEnabled(false);
				hideBuiltinVariables.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInVariables());
				hideBuiltinParams.setEnabled(true);
				hideBuiltinParams.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInParameters());
				sortDecreaseItems.setSelection(ExpressionEditorSupportUtil.isSortDecreaseItems(ExpObject.TYPE_PARAM));
				sortIncreaseItems.setSelection(ExpressionEditorSupportUtil.isSortIncreaseItems(ExpObject.TYPE_PARAM));
	            List<ExpObject> paramsList = new ArrayList<ExpObject>();
	            Object paramsNodedata = selItem.getData();
	            if(paramsNodedata instanceof List<?>) {
	            	paramsList.addAll(getParametersDTOList(((List<JRParameter>)paramsNodedata).iterator()));
	            }
	            categoryDetails.addAll(paramsList);
				break;
			case VDATASET:
			case VARIABLES:
				hideBuiltinVariables.setEnabled(true);
				hideBuiltinVariables.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInVariables());
				hideBuiltinParams.setEnabled(false);
				hideBuiltinParams.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInParameters());
				sortDecreaseItems.setSelection(ExpressionEditorSupportUtil.isSortDecreaseItems(ExpObject.TYPE_VARIABLE));
				sortIncreaseItems.setSelection(ExpressionEditorSupportUtil.isSortIncreaseItems(ExpObject.TYPE_VARIABLE));
	            List<ExpObject> variablesList=new ArrayList<ExpObject>();
	            Object variablesNodeData = selItem.getData();
	            if(variablesNodeData instanceof List<?>){
	            	variablesList.addAll(getVariablesDTOList(((List<JRVariable>)variablesNodeData).iterator()));
	            }
	            categoryDetails.addAll(variablesList);
	            break;
			case FDATASET:
			case FIELDS:
				hideBuiltinVariables.setEnabled(false);
				hideBuiltinVariables.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInVariables());
				hideBuiltinParams.setEnabled(false);
				hideBuiltinParams.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInParameters());
				sortDecreaseItems.setSelection(ExpressionEditorSupportUtil.isSortDecreaseItems(ExpObject.TYPE_FIELD));
				sortIncreaseItems.setSelection(ExpressionEditorSupportUtil.isSortIncreaseItems(ExpObject.TYPE_FIELD));
	            List<ExpObject> fieldsList=new ArrayList<ExpObject>();
	            Object fieldsNodeData = selItem.getData();
	            if(fieldsNodeData instanceof List<?>){
	            	fieldsList.addAll(getFieldsDTOList(((List<JRField>)fieldsNodeData).iterator()));
	            }
	            categoryDetails.addAll(fieldsList);
	            break;
			case RESOURCE_KEYS:
				hideBuiltinVariables.setEnabled(false);
				hideBuiltinVariables.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInVariables());
				hideBuiltinParams.setEnabled(false);
				hideBuiltinParams.setSelection(!ExpressionEditorSupportUtil.isShowBuiltInParameters());
				sortDecreaseItems.setSelection(ExpressionEditorSupportUtil.isSortDecreaseItems(ExpObject.TYPE_RBKEY));
				sortIncreaseItems.setSelection(ExpressionEditorSupportUtil.isSortIncreaseItems(ExpObject.TYPE_RBKEY));
	            List<ExpObject> rbKeysList=new ArrayList<ExpObject>();
	            Object rbKeysListData = selItem.getData();
	            if(rbKeysListData instanceof List<?>){
	            	rbKeysList.addAll(getRBKeysDTOList((List<String>) rbKeysListData));
	            }
	            categoryDetails.addAll(rbKeysList);
				break;
			case FUNCTION_CATEGORY:
				List<JRExprFunctionBean> categoryFunctions = FunctionsLibraryUtil.getFunctionsByCategory((String)selItem.getData());
				Collections.sort(categoryFunctions);
				categoryDetails.addAll(categoryFunctions);
				((GridData)buttonsToolbar.getLayoutData()).exclude=true;
				buttonsToolbar.getParent().layout();
				break;
			case RECENT_EXPRESSIONS:
				categoryDetails.addAll(RecentExpressions.getRecentExpressionsList());
				configureMinimalLayout();
				break;
			case USER_DEFINED_EXPRESSIONS:
				categoryDetails.addAll(ExpressionEditorPreferencePage.getUserDefinedExpressionList());
				configureMinimalLayout();
				break;
			case CROSSTAB:
                JRDesignCrosstab crosstab = (JRDesignCrosstab) selItem.getData();
                List<JRCrosstabRowGroup> rowGroups = crosstab.getRowGroupsList();
                List<JRCrosstabColumnGroup> columnGroups = crosstab.getColumnGroupsList();

                Iterator<JRCrosstabMeasure> measures = crosstab.getMesuresList().iterator();
                while (measures.hasNext()) {
                    JRDesignCrosstabMeasure measure = (JRDesignCrosstabMeasure)measures.next();
                    categoryDetails.add(new ExpObject(measure.getVariable()));

                    for (int i=0; i<rowGroups.size(); ++i) {
                        JRDesignCrosstabRowGroup rowGroup = (JRDesignCrosstabRowGroup)rowGroups.get(i);
                        categoryDetails.add(new CrosstabTotalVariable(measure, rowGroup, null));

                        for (int j=0; j<columnGroups.size(); ++j) {
                            JRDesignCrosstabColumnGroup columnGroup = (JRDesignCrosstabColumnGroup)columnGroups.get(j);
                            if (j==0) {
                            	categoryDetails.add(new CrosstabTotalVariable(measure, null, columnGroup));
                            }
                            categoryDetails.add(new CrosstabTotalVariable(measure, rowGroup, columnGroup));
                        }
                    }
                }

                for (int i=0; i<rowGroups.size(); ++i) {
                    JRDesignCrosstabRowGroup rowGroup = (JRDesignCrosstabRowGroup)rowGroups.get(i);
                    categoryDetails.add(new ExpObject(rowGroup.getVariable()));
                }

                for (int i=0; i<columnGroups.size(); ++i) {
                    JRDesignCrosstabColumnGroup columnGroup = (JRDesignCrosstabColumnGroup)columnGroups.get(i);
                    categoryDetails.add(new ExpObject(columnGroup.getVariable()));
                }

                List<JRCrosstabParameter> crosstabParameters = crosstab.getParametersList();
                for (int i=0; i<crosstabParameters.size(); ++i) {
                    JRDesignCrosstabParameter parameter = (JRDesignCrosstabParameter)crosstabParameters.get(i);
                    categoryDetails.add(new ExpObject(parameter));
                }
				break;
			case BUILT_IN_FUNCTIONS:
				// Show all functions from all the categories
				List<JRExprFunctionBean> allFunctionsLst=FunctionsLibraryUtil.getAllFunctions();
				Collections.sort(allFunctionsLst);
				categoryDetails.addAll(allFunctionsLst);
				categoryDetails.addAll(AdditionalStaticFunctions.getAllFunctions());
				((GridData)buttonsToolbar.getLayoutData()).exclude=true;
				buttonsToolbar.getParent().layout();
				break;
			case STATIC_FUNCTION_CATEGORY:
				categoryDetails.addAll((List<JRExprFunctionBean>) selItem.getData());
				((GridData)buttonsToolbar.getLayoutData()).exclude=true;
				buttonsToolbar.getParent().layout();
				break;
		default:
			break;
		}
		categoryContent.setInput(categoryDetails.toArray());
	}

	/*
	 * Configure a minimal layout for those categories that do not need
	 */
	private void configureMinimalLayout() {
		panelSashForm.setWeights(new int[]{100,0});
		panelSashForm.SASH_WIDTH=0;
		buttonsToolbar.setVisible(false);
		((GridData)buttonsToolbar.getLayoutData()).exclude=true;
		buttonsToolbar.getParent().layout();
	}
	
	/*
	 * Refresh the UI of the additional information panel.
	 * This is usually category specific.
	 */
	private void refreshAdditionalDetailsUI(Object selItem) {
		String key=getItemKey(selItem);
		Control currentControl = additionalDetailControls.get(key);
		if(currentControl==null){
			if(selItem instanceof ExpObject){
				// (Flat) TreeViewer
				Composite cmp=new Composite(additionalDetailsCmp, SWT.NONE);
				GridLayout gl=new GridLayout(1,true);
				gl.marginHeight=0;
				gl.marginWidth=0;
				cmp.setLayout(gl);
				
				FilteredTree ft = new FilteredTree(cmp, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER | SWT.BORDER_SOLID, new PatternFilter(), true);
				ft.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
				final TreeViewer tv = ft.getViewer();
				ColumnViewerToolTipSupport.enableFor(tv);
				addDragSupport(tv);
				currentControl=cmp;
				
				List<String> methodFirms = getExpObjectMethodFirms((ExpObject)selItem);
				tv.setContentProvider(new TreeArrayContentProvider());
				tv.setLabelProvider(new ObjectItemStyledLabelProvider());
				Collections.sort(methodFirms);
				tv.setInput(methodFirms.toArray());
				tv.addDoubleClickListener(new IDoubleClickListener() {
					public void doubleClick(DoubleClickEvent event) {
						Object selElement=((IStructuredSelection)tv.getSelection()).getFirstElement();
						Object categoryContentSel=((IStructuredSelection)categoryContent.getSelection()).getFirstElement();
						if(selElement instanceof String && categoryContentSel instanceof ExpObject){
							String detailStr=(String)selElement;
							editingAreaInfo.setUpdate(true);
							editingAreaInfo.insertAtCurrentLocation(
									((ExpObject)categoryContentSel).getExpression() + "." + //$NON-NLS-1$
									detailStr.substring(0,detailStr.lastIndexOf(')')+1),false, true);
							editingAreaInfo.setUpdate(false);
						}
					}
				});
			}
			else if(selItem instanceof JRExprFunctionBean){
				FunctionDetailsComposite fdetails=new FunctionDetailsComposite(additionalDetailsCmp, SWT.NONE, (JRExprFunctionBean) selItem);
				fdetails.setLayoutData(new GridData(GridData.FILL_BOTH));
				fdetails.setEditingAreaInfo(this.editingAreaInfo);
				currentControl=fdetails;
			}
			
			if(currentControl!=null){
				additionalDetailControls.put(key, currentControl);
			}
		}
		
		Control[] children = additionalDetailsCmp.getChildren();
		for(int i=0;i<children.length;i++){
			if(children[i]!=currentControl){
				children[i].setVisible(false);
			}
		}
		
		currentControl.setVisible(true);
		additionalDetailsStackLayout.topControl=currentControl;
		additionalDetailsCmp.layout();
	}
	
	/*
	 * Shows the current top control panel with details on the selected function item. 
	 */
	private void showFunctionDetailsPanel() {
		FunctionDetailsComposite functionDetails=(FunctionDetailsComposite)additionalDetailsStackLayout.topControl;
		functionDetails.showParametersPanel(true);
	}

	/**
	 * Sets the expression context that is supposed to be used for operations on the jrexpression.
	 * 
	 * @param context the expression context
	 */
	public void setExpressionContext(ExpressionContext context){
		this.exprContext=context;
	}
	
	 
	public void setVisible(boolean visible) {
		if(additionalDetailsStackLayout.topControl!=null){
			if(categoryDetails==null || categoryDetails.isEmpty()){
				additionalDetailsStackLayout.topControl.setVisible(false);
			}
			else{
				additionalDetailsStackLayout.topControl.setVisible(visible);
			}
		}
		super.setVisible(visible);
	}

	/**
	 * Sets the helper reference to the editing area.
	 * 
	 * @param editingAreaInfo helper reference
	 */
	public void setEditingAreaInfo(EditingAreaHelper editingAreaInfo){
		this.editingAreaInfo=editingAreaInfo;
	}
	
	/* Utility methods */
	
	/*
	 * Returns a human-readable text for a type.
	 */
	private String getPrintableTypeName(String type) {
		if (type == null)
			return "void"; //$NON-NLS-1$

		if (type.endsWith(";")) //$NON-NLS-1$
			type = type.substring(0, type.length() - 1);

		while (type.startsWith("[")) { //$NON-NLS-1$
			type = type.substring(1) + "[]"; //$NON-NLS-1$
			if (type.startsWith("[")) //$NON-NLS-1$
				continue;
			if (type.startsWith("L")) //$NON-NLS-1$
				type = type.substring(1);
			if (type.startsWith("Z")) //$NON-NLS-1$
				type = "boolean" + type.substring(1); //$NON-NLS-1$
			if (type.startsWith("B")) //$NON-NLS-1$
				type = "byte" + type.substring(1); //$NON-NLS-1$
			if (type.startsWith("C")) //$NON-NLS-1$
				type = "char" + type.substring(1); //$NON-NLS-1$
			if (type.startsWith("D")) //$NON-NLS-1$
				type = "double" + type.substring(1); //$NON-NLS-1$
			if (type.startsWith("F")) //$NON-NLS-1$
				type = "float" + type.substring(1); //$NON-NLS-1$
			if (type.startsWith("I")) //$NON-NLS-1$
				type = "int" + type.substring(1); //$NON-NLS-1$
			if (type.startsWith("J")) //$NON-NLS-1$
				type = "long" + type.substring(1); //$NON-NLS-1$
			if (type.startsWith("S")) //$NON-NLS-1$
				type = "short" + type.substring(1); //$NON-NLS-1$
		}

		if (type.startsWith("java.lang.")) { //$NON-NLS-1$
			type = type.substring("java.lang.".length()); //$NON-NLS-1$
			if (type.indexOf(".") > 0) { //$NON-NLS-1$
				type = "java.lang." + type; //$NON-NLS-1$
			}
		}
		return type;
	}
	
	/*
	 * Computes a key string to be used in the cache map.
	 */
	private String getItemKey(Object selItem) {
		String key = selItem.toString();
		if(selItem instanceof ExpObject){
			key=((ExpObject) selItem).getName()+"_"+((ExpObject) selItem).getClassType(); //$NON-NLS-1$
		}
		else if(selItem instanceof JRExprFunctionBean){
			key=((JRExprFunctionBean) selItem).getId()+"_"+((JRExprFunctionBean) selItem).getClass().getCanonicalName(); //$NON-NLS-1$
		}
		return key;
	}

	/*
	 * Returns a list of the method firms for the specified ExpObject instance.
	 */
	private List<String> getExpObjectMethodFirms(ExpObject selItem) {
		List<String> methodFirms=new ArrayList<String>();
		
		JasperReportsConfiguration jasperConfiguration = exprContext.getJasperReportsConfiguration();
		Class<?> loadClass=null;
		try {
			loadClass = jasperConfiguration.getClassLoader().loadClass(selItem.getClassType());
		} catch (ClassNotFoundException e) {
		}
		
		if (loadClass != null) {
			Method[] methods = loadClass.getMethods();
			for (int i = 0; i < methods.length; ++i) {
				if ((methods[i].getModifiers() & java.lang.reflect.Modifier.PUBLIC) != 0) {
					String method_firm = methods[i].getName() + "("; //$NON-NLS-1$
					Class<?>[] params = methods[i].getParameterTypes();
					int j = 0;
					for (j = 0; j < params.length; ++j) {

						if (j > 0)
							method_firm += ", "; //$NON-NLS-1$
						else
							method_firm += " "; //$NON-NLS-1$
						method_firm += getPrintableTypeName(params[j].getName());
					}
					if (j > 0)
						method_firm += " "; //$NON-NLS-1$
					method_firm += ") "; //$NON-NLS-1$

					String rname = methods[i].getReturnType().getName();
					if (rname.equals("void")) //$NON-NLS-1$
						continue; // we have to return something always!
					method_firm += getPrintableTypeName(rname);
					methodFirms.add(method_firm);
				}
			}
		}
		
		return methodFirms;
	}
	
	private List<ExpObject> getParametersDTOList(Iterator<JRParameter> parameters){
		List<ExpObject> tmpParamList=new ArrayList<ExpObject>();
        while (parameters.hasNext()) {
            JRParameter parameter = (JRParameter) parameters.next();
            if(!ExpressionEditorSupportUtil.isShowBuiltInParameters() && parameter.isSystemDefined()){
            	continue;
            }
            tmpParamList.add(new ExpObject(parameter));
        }
        reorderList(tmpParamList);
		return tmpParamList;
	}
	
	private List<ExpObject> getVariablesDTOList(Iterator<JRVariable> variables){
        List<ExpObject> tmpVarList=new ArrayList<ExpObject>();
        while (variables.hasNext()) {
            JRVariable variable = (JRVariable) variables.next();
            if(!ExpressionEditorSupportUtil.isShowBuiltInVariables() && variable.isSystemDefined()){
            	continue;
            }
            tmpVarList.add(new ExpObject(variable));
        }
        reorderList(tmpVarList);
		return tmpVarList;
	}
	
	private List<ExpObject> getFieldsDTOList(Iterator<JRField> fields){
        List<ExpObject> tmpFieldList=new ArrayList<ExpObject>();
        while (fields.hasNext()) {
            ExpObject eo = new ExpObject(fields.next());
            tmpFieldList.add(eo);
        }
        reorderList(tmpFieldList);
		return tmpFieldList;
	}
	
	private List<ExpObject> getRBKeysDTOList(List<String> rbkeys){
		List<ExpObject> rbKeysList=new ArrayList<ExpObject>();
    	for(String rbk : rbkeys){
    		rbKeysList.add(new ExpObject(rbk, ExpObject.TYPE_RBKEY, "java.lang.String")); //$NON-NLS-1$
    	}
        reorderList(rbKeysList);
		return rbKeysList;
	}
	
	private void reorderList(List<ExpObject> items) {
		if(ExpressionEditorSupportUtil.isSortDecreaseItems(getExpObjectType(selItem))){
			Collections.sort(items);
			Collections.reverse(items);
		}
		else if(ExpressionEditorSupportUtil.isSortIncreaseItems(getExpObjectType(selItem))){
			Collections.sort(items);
		}
	}
	
	/*
	 * Add drag support to the specified tree widget
	 */
	private void addDragSupport(final TreeViewer tv){
		DragSource dragSrc = new DragSource(tv.getTree(), DND.DROP_DEFAULT | DND.DROP_COPY);
		dragSrc.setTransfer(new Transfer[]{TextTransfer.getInstance()});
		dragSrc.addDragListener(new DragSourceListener() {
			
			private int textLength = -1;
			private boolean moveNextParenthesis = false;
			
			@Override
			public void dragStart(DragSourceEvent event) {
				TreeItem[] selection = tv.getTree().getSelection();
				if(selection!=null && selection.length==1){
					event.doit = true;
				}
				editingAreaInfo.setUpdate(true);
			}
			
			@Override
			public void dragSetData(DragSourceEvent event) {
				StringBuffer dataTxtSB = new StringBuffer();
				// always add the first piece of the txt in the central panel
				Object categoryContentSel=((IStructuredSelection)categoryContent.getSelection()).getFirstElement();
				if(categoryContentSel instanceof ExpObject) {
					dataTxtSB.append(((ExpObject)categoryContentSel).getExpression());
				}
				else if (categoryContentSel instanceof JRExprFunctionBean){
					JRExprFunctionBean funct=(JRExprFunctionBean)categoryContentSel;
					dataTxtSB.append(funct.getId()).append("()");
					moveNextParenthesis = true;
				}
				else if (categoryContentSel instanceof String){
					dataTxtSB.append(categoryContentSel);
				}
				else {
					dataTxtSB.append("");
				}
				// possibly add the second piece of the txt using the right panel
				if(tv!=categoryContent){
					Object selElement=((IStructuredSelection)tv.getSelection()).getFirstElement();
					if(selElement instanceof String){
						String detailStr=(String)selElement;
						dataTxtSB.append(".").append(detailStr.substring(0,detailStr.lastIndexOf(')')+1));
					}
				}
				String text = dataTxtSB.toString();
				textLength = text.length();
				event.data = text;
			}
			
			@Override
			public void dragFinished(DragSourceEvent event) {
				if(event.doit && textLength>0){
					// drag finished correctly
					if(moveNextParenthesis){
						editingAreaInfo.moveCaretToNextParenthesis();
					}
					else {
						editingAreaInfo.moveCaretAhead(textLength);
					}
				}
				// reset info
				moveNextParenthesis = false;
				textLength = -1;
				editingAreaInfo.setUpdate(false);
			}
		});

	}
	
}
