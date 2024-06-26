/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.messages.MessagesByKeys;
import com.jaspersoft.studio.swt.events.ChangeEvent;
import com.jaspersoft.studio.swt.events.ChangeListener;
import com.jaspersoft.studio.swt.widgets.table.ListContentProvider;
import com.jaspersoft.studio.swt.widgets.table.MoveT2TButtons;
import com.jaspersoft.studio.templates.DefaultTemplateProvider;
import com.jaspersoft.studio.templates.StudioTemplateManager;
import com.jaspersoft.studio.templates.TemplateProvider;
import com.jaspersoft.studio.wizards.BuiltInCategories;
import com.jaspersoft.studio.wizards.ContextHelpIDs;
import com.jaspersoft.studio.wizards.JSSWizardPage;
import com.jaspersoft.studio.wizards.category.ReportTemplatesWizardPage;
import com.jaspersoft.templates.TemplateBundle;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;

/**
 * Page of the wizard used to assign the categories to an exported report as template, 
 * and to define its type
 * 
 * @author Orlandin Marco
 *
 */
public class CategoriesPage extends JSSWizardPage {
	
	private class Category {
		
		private String name;
		
		private boolean isNew;
		
		public Category(String name, boolean isNew){
			this.name = name;
			this.isNew = isNew;
		}
	}
	
	/**
	 * List of available categories
	 */
	protected List<Category> inFields;
	
	/**
	 * List of selected categories
	 */
	protected List<Category> outFileds;
	
	/**
	 * List of the selected categories
	 */
	protected List<String> categoriesSelected = new ArrayList<String>();

	/**
	 * Table where the selected categories are shown
	 */
	protected Table rightTable;
	
	/**
	 * Table where the available categories are shown
	 */
	private Table leftTable;
	
	/**
	 * Combo box used to select the type of the Template, the type define the engine to
	 * use to generate report from that template
	 */
	private Combo engineCombo = null;
	
	/**
	 * String of the available key for the report type selection
	 */
	protected String[] engineKeys;
	
	/**
	 * Set of buttons to manage the list...
	 */
	private MoveT2TButtons mt2t = null;
	
	/**
	 * Map of all the available Template provider, the key is the key provided by the provider itself
	 */
	private HashMap<String, TemplateProvider> providersMap = new HashMap<String, TemplateProvider>();
	
	protected CategoriesPage() {
		super("addcategories"); //$NON-NLS-1$
		setTitle(Messages.CategoriesPage_pageTitle);
		setDescription(Messages.CategoriesPage_pageDescription);
		
		//Store the categories name already found in a set, to avoid duplicates
		HashSet<String> foundCategories = new HashSet<String>();
		
		List<String> builtInCat = BuiltInCategories.getCategoriesList();
		builtInCat = builtInCat.subList(1, builtInCat.size());
		inFields = new ArrayList<Category>();
		
		for(String cat : builtInCat){
			inFields.add(new Category(cat, false));
			foundCategories.add(cat.toLowerCase());
		}
		
		//add the custom categories
		List<TemplateBundle> bundles = StudioTemplateManager.getInstance().getTemplateBundles();
		for (TemplateBundle b : bundles) {
			Object templateCategory = b.getProperty(BuiltInCategories.CATEGORY_KEY);
			if (templateCategory != null) {
				String[] strCategoryList = templateCategory.toString().split(ReportTemplatesWizardPage.TEMPLATE_CATEGORY_SEPARATOR); 
				for (String cat : strCategoryList) {
					String trimmedCategory = cat.trim();
					if (!trimmedCategory.isEmpty()) {
						String lowerCaseCateogry = trimmedCategory.toLowerCase();
						if (!foundCategories.contains(lowerCaseCateogry)) {
							String categoryLocalizedName = trimmedCategory;
							inFields.add(new Category(categoryLocalizedName, false));
							foundCategories.add(lowerCaseCateogry);
						}
					}
				}
			}
		}
		
		outFileds = new ArrayList<Category>();
	}
	
	private void createTopPanel(Composite parent){
		Composite topPanel = new Composite(parent, SWT.NONE);
		topPanel.setLayout(new GridLayout(2,false));
		
		new Label(topPanel,SWT.NONE).setText(Messages.CategoriesPage_typeLabel);
		
		engineCombo = new Combo(topPanel, SWT.READ_ONLY);

		
		List<TemplateProvider>  templateProviders = new ArrayList<TemplateProvider>();
		templateProviders.add(new DefaultTemplateProvider());
		templateProviders.addAll(JaspersoftStudioPlugin.getExtensionManager().getTemplateProviders());
		
		engineKeys = new String[templateProviders.size()];
		String[] engineNames = new String[templateProviders.size()];
		for(int i=0; i<templateProviders.size(); i++){
			TemplateProvider actualProvider = templateProviders.get(i);
			engineKeys[i] = actualProvider.getProviderKey();
			providersMap.put(engineKeys[i], actualProvider);
			engineNames[i] = actualProvider.getProviderName();
		}
		
		engineCombo.setItems(engineNames);
		engineCombo.select(0);
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout(1,false));
		setControl(panel);
	
		createTopPanel(panel);
		
		
		Composite composite = new Composite(panel, SWT.NONE);	
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label categoryDescriptionsLabel = new Label(composite, SWT.NONE);
		categoryDescriptionsLabel.setText(Messages.CategoriesPage_categoriesLabel);
		categoryDescriptionsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		

		Composite leftPanel = new Composite(composite, SWT.NONE);
		
		GridLayout leftLayout = new GridLayout(2,false);
		leftLayout.horizontalSpacing = 0;
		leftLayout.verticalSpacing = 5;
		leftLayout.marginHeight = 0;
		leftLayout.marginWidth = 0;
		
		leftPanel.setLayout(leftLayout);
		leftPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		leftTable = new Table(leftPanel, SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.widthHint = 300;
		leftTable.setLayoutData(gd);
		leftTable.setHeaderVisible(true);

		TableColumn[] col = new TableColumn[1];
		col[0] = new TableColumn(leftTable, SWT.NONE);
		col[0].setText(Messages.CategoriesPage_availabelCatLabel);
		col[0].pack();

		TableLayout tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(100, false));
		leftTable.setLayout(tlayout);
		
		final Text customCategory = new Text(leftPanel, SWT.BORDER);
		customCategory.setToolTipText(Messages.CategoriesPage_customCatBox);
		customCategory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final TableViewer leftTView = new TableViewer(leftTable);
		leftTView.setContentProvider(new ListContentProvider());
		setLabelProvider(leftTView);
		createMenu(leftTView);

		Composite bGroup = new Composite(composite, SWT.NONE);
		bGroup.setLayout(new GridLayout(1, false));
		bGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		// -----------------------------------
		rightTable = new Table(composite, SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.minimumWidth = 300;
		rightTable.setLayoutData(gd);
		rightTable.setHeaderVisible(true);

		createColumns();

		final TableViewer rightTView = new TableViewer(rightTable);
		rightTView.setContentProvider(new ListContentProvider());
		setLabelProvider(rightTView);
		createMenu(rightTView);
		
		leftTView.setInput(inFields);
		rightTView.setInput(outFileds);
		
		final Button addCustomButton = new Button(leftPanel, SWT.NONE);
		addCustomButton.setText(Messages.CategoriesPage_addButton);
		addCustomButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				inFields.add(new Category(customCategory.getText(), true));
				customCategory.setText("");
				leftTView.refresh();
			}
		});
		addCustomButton.setEnabled(false);
		
		customCategory.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String text = customCategory.getText().trim();
				if (text.isEmpty() || isCategoryAlreadyExisting(text)){
					addCustomButton.setEnabled(false);
				} else {
					addCustomButton.setEnabled(true);
				}
			}
		});
		
		mt2t = new MoveT2TButtons();
		mt2t.createButtons(bGroup, leftTView, rightTView);
		
		// Add listener to check for changes in the list...
		mt2t.addChangeListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event) {
					storeSettings();
			}
		});
	}
	
	/**
	 * Check if a category already exist, excluding the case
	 * 
	 * @param newCategory the categoty name
	 * @return true if it already exist, false otherwise
	 */
	private boolean isCategoryAlreadyExisting(String newCategory){
		for(Category category : inFields){
			String name = category.name;
			if (newCategory.equalsIgnoreCase(name)){
				return true;
			}
		}
		for(Category category : outFileds){
			String name = category.name;
			if (newCategory.equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
	public void storeSettings()
	{
		categoriesSelected.clear();
		for(TableItem item : rightTable.getItems()){
			categoriesSelected.add(((Category)item.getData()).name);
		}
	}
	
	public void finish(String reportName, String destinationPath){
		 Properties props = new Properties();
		 props.setProperty(BuiltInCategories.NAME_KEY, reportName);
		 
		 String categories = ""; //$NON-NLS-1$
		 for(String cat : categoriesSelected){
			 categories = categories.concat(cat).concat(ReportTemplatesWizardPage.TEMPLATE_CATEGORY_SEPARATOR);
		 }
		 if (!categories.isEmpty()) {
			 categories = categories.substring(0, categories.length()-1);
			 props.setProperty(BuiltInCategories.CATEGORY_KEY, categories);
		 }
		 
		 String engine = engineCombo != null ? engineKeys[engineCombo.getSelectionIndex()] : engineKeys[0];
		 props.setProperty(BuiltInCategories.ENGINE_KEY, engine);
		 
     String path = destinationPath.substring(0, destinationPath.lastIndexOf(".jrxml")); //$NON-NLS-1$
     path = path.concat("_descriptor.properties"); //$NON-NLS-1$
     File f = new File(path);
     OutputStream out;
		try {
			out = new FileOutputStream( f );
	    props.store(out,""); //$NON-NLS-1$
	    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Get a JasperDesign and check if that JasperDesign can be used as Template for the selected
	 * Template type
	 * 
	 * @param design the design to check
	 * @param jrContext context of the design to check
	 * @return a List of founded error, the list is void if no error are found
	 */
	public List<String> validateWithSelectedEngine(JasperReportsContext jrContext, JasperDesign design){
		String engine = engineCombo != null ? engineKeys[engineCombo.getSelectionIndex()] : engineKeys[0];
		return providersMap.get(engine).validateTemplate(jrContext, design);
	}
	
	protected void createColumns() {
		TableColumn[] col;
		TableLayout tlayout;
		col = new TableColumn[1];
		col[0] = new TableColumn(rightTable, SWT.NONE);
		col[0].setText(Messages.CategoriesPage_selectedCatLabel);
		col[0].pack();

		tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(100, false));
		rightTable.setLayout(tlayout);
	}
	
	protected void setLabelProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				Category category = (Category)element;
				String name = category.name;
				if (MessagesByKeys.hasTranslation(name)) return MessagesByKeys.getString(name);
				else return name;
			}
		});
	}

	protected void createMenu(final TableViewer tableViewer){
		final Table table = tableViewer.getTable();
		final Menu menu = new Menu(table);
		table.setMenu(menu);
		menu.addMenuListener(new MenuAdapter() {
			
	        public void menuShown(MenuEvent e)
	        {
	        	//remove and add every time the items (if necessary) to avoid to show the menu
	        	//on the not deletable items
	            MenuItem[] items = menu.getItems();
	            for (int i = 0; i < items.length; i++)
	            {
	                items[i].dispose();
	            }
	        	
	            final int selected = table.getSelectionIndex();
	            final List<?> input = (List<?>)tableViewer.getInput();

	            if(selected < 0 || selected >= input.size())
	                return;
	            
	            Category selectedCategory = (Category)input.get(selected);
	            if (!selectedCategory.isNew){
	            	return;
	            }
	            
	            MenuItem newItem = new MenuItem(menu, SWT.NONE);
	            newItem.setText(Messages.common_delete);
	            newItem.setImage(ResourceManager.getPluginImage(JaspersoftStudioPlugin.PLUGIN_ID, "/icons/resources/delete_style.gif"));
	            newItem.addSelectionListener(new SelectionAdapter() {
	            
	            	@Override
	            	public void widgetSelected(SelectionEvent e) {
	            		input.remove(selected);
	            		tableViewer.refresh();
	            	}
				});
	        }
		});
	}
	
	@Override
	protected String getContextName() {
		return ContextHelpIDs.WIZARD_EXPORTED_CATEGORY;
	}

}
