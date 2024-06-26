/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.properties.internal;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.jaspersoft.studio.properties.layout.StackLayout;
import com.jaspersoft.studio.properties.view.ITabDescriptor;
import com.jaspersoft.studio.properties.view.TabContents;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetWidgetFactory;

/**
 * Composite responsible for drawing the tabbed property sheet page.
 * 
 * @author Anthony Hunter & Orlandin Marco
 */
public class TabbedPropertyComposite extends Composite {

	/**
	 * State of a tab
	 */
	public enum TabState {
		TAB_NOT_DEFINED, TAB_ALREADY_VISIBLE, TAB_SET_VISIBLE, TAB_DYNAMIC_VISIBLE, TAB_NOT_VISIBLE
	};

	/**
	 * Factory widget, used to build stuff
	 */
	private TabbedPropertySheetWidgetFactory factory;

	/**
	 * The page of the property sheet
	 */
	private TabbedPropertySheetPage page;

	/**
	 * The main container of all the controls
	 */
	private Composite mainComposite;

	/**
	 * The composite where the controls of the selected tab are placed
	 */
	private Composite tabComposite;

	/**
	 * The title control
	 */
	private TabbedPropertyTitle title;

	/**
	 * The search control
	 */
	private TabbedPropertySearch searchBar;

	/**
	 * The control with the list of the available tabs
	 */
	private TabbedPropertyList listComposite;

	/**
	 * Flag to show or not the title control
	 */
	private boolean displayTitle;

	/**
	 * Stack layout of the tabComposite
	 */
	private StackLayout cachedLayout;

	/**
	 * Map of the stacked controls
	 */
	private HashMap<ITabDescriptor, Control> cacheMap;

	/**
	 * Constructor for a TabbedPropertyComposite
	 * 
	 * @param parent
	 *          the parent widget.
	 * @param page
	 *          the page that contains this control
	 * @param displayTitle
	 *          if true then the title bar will be displayed.
	 */
	public TabbedPropertyComposite(Composite parent, TabbedPropertySheetPage page, boolean displayTitle) {
		super(parent, SWT.NO_FOCUS);
		this.page = page;
		this.factory = page.getWidgetFactory();
		this.displayTitle = displayTitle;

		createMainComposite();
	}

	/**
	 * Create the main composite.
	 */
	protected void createMainComposite() {
		mainComposite = factory.createComposite(this, SWT.NO_FOCUS);
		mainComposite.setBackgroundMode(SWT.INHERIT_FORCE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		mainComposite.setLayout(layout);

		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		formData.top = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, 0);
		mainComposite.setLayoutData(formData);

		createMainContents();
	}

	/**
	 * Create the contents in the main composite.
	 */
	protected void createMainContents() {
		Composite parent = new Composite(mainComposite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginBottom=1;
		parent.setLayout(layout);
		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (displayTitle) {
			title = new TabbedPropertyTitle(parent, factory);
			title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		// Create the search bar for the properties
		searchBar = new TabbedPropertySearch(parent, page);
		searchBar.setLayoutData(new GridData(GridData.FILL_BOTH));

		listComposite = new TabbedPropertyList(mainComposite);
		listComposite.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(mainComposite, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tabComposite = factory.createComposite(mainComposite, SWT.NO_FOCUS);
		tabComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		cachedLayout = new StackLayout();
		tabComposite.setLayout(cachedLayout);
		cacheMap = new HashMap<ITabDescriptor, Control>();
	}

	/**
	 * Can make the page appear empty, by hiding all it contents
	 * 
	 * @param value
	 *          true if the page should appear empty, false otherwise
	 */
	public void showEmptyPage(boolean value) {
		if (value) {
			if (title != null)
				title.setVisible(false);
			searchBar.setVisible(false);
			tabComposite.setVisible(false);
		} else {
			if (title != null && !title.isVisible())
				title.setVisible(true);
			if (!searchBar.isVisible())
				searchBar.setVisible(true);
			if (!tabComposite.isVisible())
				tabComposite.setVisible(true);
		}
	}

	/**
	 * Put on the top of the stack layout the tab of a specific descriptor. The
	 * composite of the tab must be created and cached before. To do this use the
	 * createTabContents method
	 * 
	 * @param tab
	 *          descriptor of the tab to show
	 * @param contents
	 * 			the contents of the tab to show
	 * @return the state of a tab, it can be TabState.TAB_NOT_DEFINED if the tab
	 *         was still undefined and must be created TabState.TAB_SET_VISIBLE
	 *         the tab was created before but it was not visible, now it is
	 *         visible TabState.TAB_ALREADY_VISIBLE the tab was created before and
	 *         it is already visible. TAB_DYNAMIC_VISIBLE is similar to TAB_ALREADY_VISIBLE
	 *         but remark that the content inside the tab are shown dynamically, so it need
	 *         to be layouted every time  
	 */
	public TabState showTabContents(ITabDescriptor tab, TabContents contents) {
		if (tab == null)
			showEmptyPage(true);
		Control control = cacheMap.get(tab);
		if (control == null || control.isDisposed())
			return TabState.TAB_NOT_DEFINED;
		else {
			if (cachedLayout.setTopControl(control)) {
				return TabState.TAB_SET_VISIBLE;
			}
			if (contents.hasDynamicContent()) return TabState.TAB_DYNAMIC_VISIBLE;
			else return TabState.TAB_ALREADY_VISIBLE;
		}
	}
	
	public TabState getTabState(ITabDescriptor tab){
		Control control = cacheMap.get(tab);
		if (control == null || control.isDisposed()) { 
			return TabState.TAB_NOT_DEFINED;
		} else {
			if (cachedLayout.getTopControl() == control) {
				return TabState.TAB_ALREADY_VISIBLE;
			} else {
				return TabState.TAB_NOT_VISIBLE;
			}
		}
	}

	/**
	 * do the layout of the tab composite area
	 * 
	 * @param all
	 *          flag to do also the layout of the children of the composite
	 */
	@Override
	public void layout() {
		tabComposite.layout();
		mainComposite.layout();
	}

	/**
	 * Calculate the real height of displayed sections in the properties tab. Then
	 * Update the minimum height of the scrolled composite, to make the scrollbars
	 * appear only when they are needed. The update is done only if it is
	 * necessary
	 * 
	 * @param minimumWidth
	 *          the minimum width of the page, if the width of the properties view
	 *          is lower of this value then the bottom scrollbar is shown
	 */
	public void updatePageMinimumSize() {
		Control topControl = cachedLayout.getTopControl();
		if (topControl != null && topControl instanceof ScrolledComposite) {
			ScrolledComposite scrolledComposite = (ScrolledComposite) topControl;
			// When i calculate the height it is really important to give the real width
			// of the composite, since it is used to calculate the number of columns
			Point compositeSize = scrolledComposite.getContent().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			scrolledComposite.setMinHeight(compositeSize.y);
			scrolledComposite.setMinWidth(compositeSize.x);
		}
	}

	/**
	 * Create a composite for a tab and cache it. The composite is also returned.
	 * This composite can be shown on the page using the showTabContents content
	 * method
	 * 
	 * @param tab
	 *          Tab for which the composite is created, it's also its key in the
	 *          cache
	 * @return a composite with a grid layout with one column
	 */
	public Composite createTabContents(ITabDescriptor tab) {
		Composite comp = null;

		if (tab.getScrollable()) {
			ScrolledComposite scrolledComposite = factory.createScrolledComposite(tabComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_FOCUS);
			scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			scrolledComposite.setAlwaysShowScrollBars(false);
			scrolledComposite.setExpandVertical(true);
			scrolledComposite.setExpandHorizontal(true);
			scrolledComposite.setShowFocusedControl(true);

			comp = new Composite(scrolledComposite, SWT.NONE);
			GridLayout layout2 = new GridLayout();
			layout2.marginWidth = 0;
			layout2.marginHeight = 0;
			layout2.makeColumnsEqualWidth = true;
			comp.setLayout(layout2);
			comp.setLayoutData(new GridData(GridData.FILL_BOTH));

			scrolledComposite.setContent(comp);
			comp.setData(scrolledComposite);
			cacheMap.put(tab, scrolledComposite);
		} else {
			comp = new Composite(tabComposite, SWT.NONE);
			GridLayout layout2 = new GridLayout();
			layout2.marginWidth = 0;
			layout2.marginHeight = 0;
			layout2.makeColumnsEqualWidth = true;
			comp.setLayout(layout2);
			comp.setLayoutData(new GridData(GridData.FILL_BOTH));
			cacheMap.put(tab, comp);
		}
		return comp;
	}
	
	/**
	 * Dispose the content of a tab and remove it from the cache
	 * 
	 * @param tab the tab to dispose
	 */
	public void destroyTabContents(ITabDescriptor tab){
		Control cont = cacheMap.get(tab);
		if (cont != null){
			cont.dispose();
			cacheMap.remove(tab);
		}
	}
		
	public Rectangle getPropertiesArea(){
		return tabComposite.getClientArea();
	}

	/**
	 * Get the tabbed property list, which is the list of tabs on the left hand
	 * side of this composite.
	 * 
	 * @return the tabbed property list.
	 */
	public TabbedPropertyList getList() {
		return listComposite;
	}

	/**
	 * Get the tabbed property title bar.
	 * 
	 * @return the tabbed property title bar or <code>null</code> if not used.
	 */
	public TabbedPropertyTitle getTitle() {
		return title;
	}

	/**
	 * Return the search bar for the search of a property
	 * 
	 */
	public TabbedPropertySearch getSearchBar() {
		return searchBar;
	}

	/**
	 * Get the widget factory.
	 * 
	 * @return the widget factory.
	 */
	protected TabbedPropertySheetWidgetFactory getFactory() {
		return factory;
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		listComposite.getControl().dispose();
		if (displayTitle) {
			title.dispose();
		}
		super.dispose();
	}

}
