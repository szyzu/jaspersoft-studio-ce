/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.properties.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.jaspersoft.studio.properties.internal.TabbedPropertyComposite;
import com.jaspersoft.studio.properties.internal.TabbedPropertyComposite.TabState;
import com.jaspersoft.studio.properties.internal.TabbedPropertyRegistry;
import com.jaspersoft.studio.properties.internal.TabbedPropertyRegistryFactory;
import com.jaspersoft.studio.properties.internal.TabbedPropertyTitle;
import com.jaspersoft.studio.properties.internal.TabbedPropertyViewer;
import com.jaspersoft.studio.properties.view.validation.IValidable;
import com.jaspersoft.studio.properties.view.validation.ValidationError;

import net.sf.jasperreports.eclipse.util.Pair;

/**
 * A property sheet page that provides a tabbed UI. It's is based on the
 * TabbedPropertySheetPage made by Anthony Hunter inside eclipse, but with some
 * optimization to be faster with Jaspersoft Studio. The pages are cached but to
 * avoid to cache too much widgets the page tab not used in the last 10 hours
 * are discarded the waiting time before a page tab is discarded is defined by
 * the variable PAGE_EXPIRATION_TIME, the discard of the page is done when a new
 * one is loaded
 * 
 * @author Anthony Hunter & Orlandin Marco
 */
public class TabbedPropertySheetPage extends Page implements IPropertySheetPage {

	/**
	 * Class to collect every page created and will watched to be disposed after too
	 * much time in the cache
	 * 
	 * @author Orlandin Marco
	 *
	 */
	private static class PagesWatcher {

		/**
		 * The list of the watched page
		 */
		private List<TabbedPropertySheetPage> pages = new ArrayList<>();

		/**
		 * Process the pages, removing the disposed ones and to the not disposed is
		 * called the method to dispose unused tabs
		 */
		public void processPages() {
			for (TabbedPropertySheetPage page : new ArrayList<TabbedPropertySheetPage>(pages)) {
				if (page.tabbedPropertyComposite.isDisposed()) {
					pages.remove(page);
				} else {
					page.removeExpiredTabs();
				}
			}

		}

		/**
		 * Add a page that will be watched
		 * 
		 * @param page
		 *            a not null page
		 */
		public void addPage(TabbedPropertySheetPage page) {
			if (page != null)
				pages.add(page);
		}

		/**
		 * Remove a watched page, typically called in the dispose of the page itsel
		 * 
		 * @param page
		 *            the page
		 */
		public void removePage(TabbedPropertySheetPage page) {
			if (page != null)
				pages.remove(page);
		}
	}

	/**
	 * 
	 */
	private static PagesWatcher WATCHER = new PagesWatcher();

	/**
	 * The page expiration time is 10 hours
	 */
	private Long PAGE_EXPIRATION_TIME = 36000000l;

	/**
	 * Composite where the controls are shown
	 */
	private TabbedPropertyComposite tabbedPropertyComposite;

	/**
	 * Utility to create graphical widgets
	 */
	private TabbedPropertySheetWidgetFactory widgetFactory;

	private ITabbedPropertySheetPageContributor contributor;

	private TabbedPropertyRegistry registry;

	/**
	 * The currently active contributor id, which may not match the contributor id
	 * from the workbench part that created this instance.
	 */
	private String currentContributorId;

	/**
	 * The provider of the tab contents to show
	 */
	protected IStructuredContentProvider tabListContentProvider;

	/**
	 * The current selection
	 */
	private ISelection currentSelection;

	/**
	 * The viewer for the tab contents
	 */
	private TabbedPropertyViewer tabbedPropertyViewer;

	/**
	 * The current tab shown
	 */
	private TabContents currentTab;

	/**
	 * Map to cache every tabcontents to its descriptor
	 */
	private Map<ITabDescriptor, TabContents> descriptorToTab;

	/**
	 * Map to cache every tabcontents with the composite where it is shown
	 */
	private Map<TabContents, Pair<Composite, Long>> tabToComposite;

	/**
	 * Map of the last tab selected for each element
	 */
	private Map<Object, String> lastSelectedTabForElement;

	/**
	 * Flag to show or not the title bar
	 */
	private boolean hasTitleBar;

	private ControlDecoration cd;

	private List<ValidationError> errors;

	/**
	 * SelectionChangedListener for the ListViewer.
	 */
	class SelectionChangedListener implements ISelectionChangedListener {

		/**
		 * Shows the tab associated with the selection.
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			synchronized (TabbedPropertySheetPage.this) {
				if (!tabbedPropertyComposite.isDisposed()) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();

					TabContents tab = null;
					ITabDescriptor descriptor = (ITabDescriptor) selection.getFirstElement();
					if (currentTab != null)
						currentTab.aboutToBeHidden();
					if (descriptor != null) {
						tab = getTab(descriptor);
						if (tabbedPropertyViewer != null && tabbedPropertyViewer.getInput() != null) {
							// force widgets to be resized
							tab.setInput(tabbedPropertyViewer.getWorkbenchPart(),
									(ISelection) tabbedPropertyViewer.getInput());
							TabState state = tabbedPropertyComposite.showTabContents(descriptor, tab);
							if (state == TabState.TAB_NOT_DEFINED) {
								// Tab not defined, it need to be created
								Composite tabComposite = createTabComposite(descriptor);
								tabToComposite.put(tab, new Pair<Composite, Long>(tabComposite, -1l));
								tab.createControls(tabComposite, TabbedPropertySheetPage.this);
								tabbedPropertyComposite.showTabContents(descriptor, tab);
							}

							Pair<Composite, Long> showedComposite = tabToComposite.get(tab);
							if (showedComposite != null) {
								showedComposite.setValue(System.currentTimeMillis());
							}
							WATCHER.processPages();

							// store tab selection
							storeCurrentTabSelection(descriptor);
							tab.refresh();
							currentTab = tab;
							currentTab.aboutToBeShown();
							if (state != TabState.TAB_ALREADY_VISIBLE || state == TabState.TAB_DYNAMIC_VISIBLE) {
								// The layout is done only if the tab was not
								// visible
								tabbedPropertyComposite.layout();
								tabbedPropertyComposite.updatePageMinimumSize();
							}
							showErrors();
						}
					}
				}
			}
		}
	}

	public void showErrors() {
		IStructuredSelection selection = (IStructuredSelection) tabbedPropertyViewer.getSelection();
		TabContents tab = null;
		ITabDescriptor descriptor = (ITabDescriptor) selection.getFirstElement();
		if (descriptor != null) {
			tab = getTab(descriptor);
			if (tabbedPropertyViewer != null && tabbedPropertyViewer.getInput() != null) {
				Object obj = tabbedPropertyViewer.getInput();
				if (obj instanceof StructuredSelection && ((StructuredSelection) obj).size() == 1) {
					Object m = ((StructuredSelection) obj).getFirstElement();
					if (m instanceof EditPart)
						m = ((EditPart) m).getModel();
					if (m instanceof IValidable) {
						errors = ((IValidable) m).validate();
						if (errors != null && !errors.isEmpty()) {
							getErrorControlDecorator();
							String error = "";
							String del = "";
							cd.setImage(PlatformUI.getWorkbench().getSharedImages()
									.getImage(ISharedImages.IMG_DEC_FIELD_WARNING));
							for (ValidationError ve : errors) {
								error += del + ve.getMessage();
								del = "\n";
								if (!ve.isWarning())
									cd.setImage(PlatformUI.getWorkbench().getSharedImages()
											.getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
							}
							cd.setDescriptionText(error);
							cd.show();
							for (ISection sec : tab.getSections())
								if (sec instanceof AbstractPropertySection)
									((AbstractPropertySection) sec).showErrors(errors);
							return;
						}
						resetErrors(tab);
					}
				}
			}
		}
	}

	protected ControlDecoration getErrorControlDecorator() {
		if (cd == null) {
			final TabbedPropertyTitle title = tabbedPropertyComposite.getTitle();
			cd = new ControlDecoration(title.getLabel(), SWT.DOWN | SWT.LEFT);
			cd.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
			cd.addSelectionListener(new SelectionAdapter() {
				private ErrorsDialog ed;

				@Override
				public void widgetSelected(SelectionEvent e) {
					cd.hideHover();
					if (ed != null) {
						ed.close();
						return;
					}
					cd.setShowHover(false);
					ed = new ErrorsDialog() {
						protected void close() {
							super.close();
							cd.setShowHover(true);
							ed = null;
						}
					};
					Rectangle r = Display.getDefault().map(title.getLabel(), null, 0, 0, 0, 0);
					ed.createDialog(TabbedPropertySheetPage.this, new Point(r.x + 4, r.y + 6), errors);
				}

			});
		}
		return cd;
	}

	public void resetErrors(TabContents tab) {
		errors = null;
		cd = getErrorControlDecorator();
		cd.hide();

		for (ISection s : tab.getSections())
			if (s instanceof AbstractPropertySection)
				((AbstractPropertySection) s).resetErrors();
	}

	/**
	 * create a new tabbed property sheet page.
	 * 
	 * @param tabbedPropertySheetPageContributor
	 *            the tabbed property sheet page contributor.
	 * @param showTitleBar
	 *            boolean indicating if the title bar should be shown
	 */
	public TabbedPropertySheetPage(ITabbedPropertySheetPageContributor tabbedPropertySheetPageContributor,
			boolean showTitleBar) {
		hasTitleBar = showTitleBar;
		contributor = tabbedPropertySheetPageContributor;
		currentContributorId = contributor.getContributorId();
		tabToComposite = new HashMap<>();
		descriptorToTab = new HashMap<>();
		lastSelectedTabForElement = new HashMap<>();
		validateRegistry();
		WATCHER.addPage(this);
	}

	/**
	 * create tab from the descriptor if necessary. then the tab is cached to make
	 * faster the next request
	 * 
	 * @param descriptor
	 *            a tab descriptor
	 * @return a TabContents created from the descriptor
	 */
	private TabContents getTab(ITabDescriptor descriptor) {
		// can not cache based on the id - tabs may have the same id,
		// but different section depending on the selection
		TabContents tab = descriptorToTab.get(descriptor);
		if (tab == null) {
			tab = descriptor.createTab();
			descriptorToTab.put(descriptor, tab);
		}
		return tab;
	}

	/**
	 * Take a TabContents and search a TabDescriptor for that tab
	 */
	private ITabDescriptor getTabDescriptor(TabContents tab) {
		for (Entry<ITabDescriptor, TabContents> tabEntry : descriptorToTab.entrySet()) {
			if (tabEntry.getValue() == tab) {
				return tabEntry.getKey();
			}
		}
		return null;
	}

	/**
	 * Change the selected tab with the one passed by parameter
	 */
	public void setSelection(TabContents tab) {
		tabbedPropertyViewer.setSelectionToWidget(getTabDescriptor(tab).getId(), 0);
	}

	/**
	 * Update width and height of the current tab inside the scroll area, this is
	 * usually called when the size changes and the scrollbar need to be refreshed
	 * 
	 */
	public void updatePageMinimumSize() {
		if (currentTab != null) {
			tabbedPropertyComposite.updatePageMinimumSize();
		}
	}

	/**
	 * Create the page control
	 * 
	 * @param parent
	 *            parent of the page
	 */
	public void createControl(Composite parent) {
		widgetFactory = new TabbedPropertySheetWidgetFactory(this);
		tabbedPropertyComposite = new TabbedPropertyComposite(parent, this, hasTitleBar);

		tabbedPropertyComposite.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				/*
				 * Check the page height when the composite area is resized because the column
				 * layout could be changed
				 */
				updatePageMinimumSize();
			}
		});

		PlatformUI.getWorkbench().getHelpSystem().setHelp(tabbedPropertyComposite,
				"com.jaspersoft.studio.doc.view_properties");

		tabbedPropertyComposite.setLayout(new FormLayout());
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		formData.top = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, 0);
		tabbedPropertyComposite.setLayoutData(formData);

		tabbedPropertyViewer = new TabbedPropertyViewer(tabbedPropertyComposite.getList());
		tabbedPropertyViewer.setContentProvider(tabListContentProvider);
		tabbedPropertyViewer.addSelectionChangedListener(new SelectionChangedListener());

		if (hasTitleBar && registry == null && currentContributorId != null) {
			initContributor(currentContributorId);
		}
	}

	public TabbedPropertyComposite getTabbedPropertyComposite() {
		return tabbedPropertyComposite;
	}

	/**
	 * Initialize the contributor with the provided contributor id.
	 * 
	 * @param contributorId
	 *            the contributor id.
	 */
	private void initContributor(String contributorId) {
		registry = TabbedPropertyRegistryFactory.getInstance().createRegistry(contributor);
		tabListContentProvider = getTabListContentProvider();
		hasTitleBar = hasTitleBar && registry.getLabelProvider() != null;
		if (tabbedPropertyViewer != null) {
			tabbedPropertyViewer.setContentProvider(tabListContentProvider);
		}
	}

	/**
	 * Gets the tab list content provider for the contributor.
	 * 
	 * @return the tab list content provider for the contributor.
	 */
	protected IStructuredContentProvider getTabListContentProvider() {
		return registry.getTabListContentProvider();
	}

	/**
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	public void dispose() {
		WATCHER.removePage(this);
		if (widgetFactory != null) {
			widgetFactory.dispose();
			widgetFactory = null;
		}

		if (registry != null) {
			TabbedPropertyRegistryFactory.getInstance().disposeRegistry(contributor);
			registry = null;
		}

		contributor = null;
		currentContributorId = null;
		currentSelection = null;
		disposeTabs();
	}

	/**
	 * Dispose all the tabs and their controls
	 */
	private void disposeTabs() {
		for (Entry<TabContents, Pair<Composite, Long>> pair : tabToComposite.entrySet()) {
			Composite composite = pair.getValue().getKey();
			pair.getKey().dispose();
			if (composite != null) {
				composite.dispose();
			}
		}
		tabToComposite.clear();
		descriptorToTab.clear();
	}

	/**
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	public Control getControl() {
		return tabbedPropertyComposite;
	}

	/**
	 * @see org.eclipse.ui.part.IPage#setActionBars(org.eclipse.ui.IActionBars)
	 */
	public void setActionBars(IActionBars actionBars) {
		// Override the undo and redo global action handlers
		// to use the contributor action handlers
		IActionBars partActionBars = null;
		if (contributor instanceof IEditorPart) {
			IEditorPart editorPart = (IEditorPart) contributor;
			partActionBars = editorPart.getEditorSite().getActionBars();
		} else if (contributor instanceof IViewPart) {
			IViewPart viewPart = (IViewPart) contributor;
			partActionBars = viewPart.getViewSite().getActionBars();
		}

		if (partActionBars != null) {
			IAction action = partActionBars.getGlobalActionHandler(ActionFactory.UNDO.getId());
			if (action != null) {
				actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), action);
			}
			action = partActionBars.getGlobalActionHandler(ActionFactory.REDO.getId());
			if (action != null) {
				actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), action);
			}
		}
	}

	/**
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	public void setFocus() {
		getControl().setFocus();
	}

	/**
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			selection = new StructuredSelection();
		setInput(part, selection);
	}

	/**
	 * Stores the current tab label in the selection queue. Tab labels are used to
	 * carry the tab context from one input object to another. The queue specifies
	 * the selection priority. So if the first tab in the queue is not available for
	 * the input we try the second tab and so on. If none of the tabs are available
	 * we default to the first tab available for the input.
	 */
	private void storeCurrentTabSelection(ITabDescriptor tab) {
		lastSelectedTabForElement.put(getSelectedObject(), tab.getId());
	}

	/**
	 * Return the first element of the current selection or null if it is not
	 * available.
	 * 
	 * @return the first element of the selection or null if it's empty or not a
	 *         structured selection
	 */
	public Object getSelectedObject() {
		if (currentSelection instanceof IStructuredSelection)
			return ((IStructuredSelection) currentSelection).getFirstElement();
		else
			return null;
	}

	/**
	 * Helper method for creating property tab composites.
	 * 
	 * @return the property tab composite.
	 */
	private Composite createTabComposite(ITabDescriptor tab) {
		return tabbedPropertyComposite.createTabContents(tab);
	}

	private void setInput(IWorkbenchPart part, ISelection selection) {
		if (selection.equals(currentSelection)) {
			return;
		}
		this.currentSelection = selection;

		// update tabs list
		tabbedPropertyViewer.setInput(part, currentSelection);
		String selectedTabIndex = lastSelectedTabForElement.get(getSelectedObject());
		if (tabbedPropertyViewer.getElements().size() > 0) {
			tabbedPropertyComposite.showEmptyPage(false);
			int defaultValue = 0;
			if (selectedTabIndex == null)
				defaultValue = contributor.getDefaultSelectedPageIndex();
			tabbedPropertyViewer.setSelectionToWidget(selectedTabIndex, defaultValue);
			refreshTitleBar();
		} else {
			tabbedPropertyComposite.showEmptyPage(true);
		}
	}

	/**
	 * Get the currently active tab.
	 * 
	 * @return the currently active tab.
	 * @since 3.4
	 */
	public TabContents getCurrentTab() {
		return currentTab;
	}

	/**
	 * Get the widget factory.
	 * 
	 * @return the widget factory.
	 */
	public TabbedPropertySheetWidgetFactory getWidgetFactory() {
		return widgetFactory;
	}

	/**
	 * Update the title bar of the contributor has a label provider.
	 */
	private void refreshTitleBar() {
		if (hasTitleBar && registry != null) {
			TabbedPropertyTitle title = tabbedPropertyComposite.getTitle();
			if (currentTab == null) {
				/**
				 * No tabs are shown so hide the title bar, otherwise you see "No properties
				 * available" and a title bar for the selection.
				 */
				title.setTitle(null, null);
			} else {
				String text = registry.getLabelProvider().getText(currentSelection);
				Image image = registry.getLabelProvider().getImage(currentSelection);
				title.setTitle(text, image);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProviderListener#labelProviderChanged(org.eclipse.jface.viewers.LabelProviderChangedEvent)
	 */
	public void labelProviderChanged(LabelProviderChangedEvent event) {
		refreshTitleBar();
	}

	/**
	 * The workbench part creates this instance of the TabbedPropertySheetPage and
	 * implements ITabbedPropertySheetPageContributor which is unique contributor
	 * id. This unique contributor id is used to load a registry with the extension
	 * point This id matches the registry.
	 * <p>
	 * It is possible for elements in a selection to implement
	 * ITabbedPropertySheetPageContributor to provide a different contributor id and
	 * thus a differenent registry.
	 * 
	 * @param selection
	 *            the current selection in the active workbench part.
	 */
	private void validateRegistry() {
		/**
		 * All the elements in the selection implement a new contributor id, so use that
		 * id.
		 */
		initContributor(currentContributorId);
		overrideActionBars();
	}

	/**
	 * Override the action bars for the selection based contributor.
	 */
	private void overrideActionBars() {
		if (registry.getActionProvider() != null) {
			IActionProvider actionProvider = registry.getActionProvider();
			actionProvider.setActionBars(contributor, getSite().getActionBars());
		}
	}

	/**
	 * Return a list of all the available TabContents actually visible
	 */
	public List<TabContents> getCurrentTabs() {
		List<TabContents> result = new ArrayList<>();
		for (ITabDescriptor desc : tabbedPropertyViewer.getElements()) {
			result.add(getTab(desc));
		}
		return result;
	}

	/**
	 * Check if a tab associated to a descriptor is visible or not
	 * 
	 * @param descriptor
	 *            the descriptor, must be not null
	 * @return true if it is visible, false otherwise
	 */
	private boolean isTabVisible(ITabDescriptor descriptor) {
		return tabbedPropertyComposite.getTabState(descriptor) == TabState.TAB_ALREADY_VISIBLE;
	}

	/**
	 * Cycle all the tabs of this page to remove the not visible and not used ones
	 */
	protected void removeExpiredTabs() {
		synchronized (TabbedPropertySheetPage.this) {
			long currentTime = System.currentTimeMillis();
			Map<TabContents, Pair<Composite, Long>> mapCopy = new HashMap<>(tabToComposite);
			for (Entry<TabContents, Pair<Composite, Long>> entry : mapCopy.entrySet()) {
				Pair<Composite, Long> entryValues = entry.getValue();
				Long lastTimeUsed = entryValues.getValue();
				if (lastTimeUsed != -1 && (currentTime - lastTimeUsed) > PAGE_EXPIRATION_TIME) {
					ITabDescriptor descriptor = getTabDescriptor(entry.getKey());
					if (descriptor != null && !isTabVisible(descriptor)) {
						tabbedPropertyComposite.destroyTabContents(descriptor);
						tabToComposite.remove(entry.getKey());
					}
				}
			}
		}
	}
}
