/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.toolbars;

import java.util.ArrayList;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.action.CustomSelectionAction;
import com.jaspersoft.studio.editor.gef.decorator.IElementDecorator;

/**
 * This class implements an action that when run open a contextual menu on the pointer location.
 * This contextual menu will contains all the exporter related tags action  
 * 
 * @author Orlandin Marco
 *
 */
public class ExporterContributionItem extends CommonToolbarHandler{

	/**
	 * Menu to open
	 */
	private Menu popupMenu;
	
	/**
	 * Manager that contain the structure of the menu
	 */
	private MenuManager manager;
	
	/**
	 * Recursive method to create a menu from the contribute items of a manger
	 * 
	 * @param menu actual menu
	 * @param items manager contributor items
	 */
	private void createMenu(Menu menu, IContributionItem[] items){
		for(IContributionItem item : items){
			if (item instanceof MenuManager){
				MenuManager manager = (MenuManager) item;
				MenuItem subMenuItem = new MenuItem(menu,SWT.CASCADE);
				subMenuItem.setText(manager.getMenuText());
				Menu subMenu = new Menu(Display.getCurrent().getActiveShell(), SWT.DROP_DOWN);
				subMenuItem.setMenu(subMenu);
				createMenu(subMenu, manager.getItems());
			} else if (item instanceof ActionContributionItem){
				ActionContributionItem actionItem = (ActionContributionItem) item;
				if (actionItem.getAction() instanceof CustomSelectionAction){
					final CustomSelectionAction action = (CustomSelectionAction)actionItem.getAction();
					MenuItem actionEnrty = new MenuItem(menu,SWT.CHECK);
					action.setSelection(getLastRawSelection());
					actionEnrty.setText(actionItem.getAction().getText());
					actionEnrty.setSelection(action.isChecked());
					actionEnrty.setEnabled(action.canExecute());
					actionEnrty.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							action.run();
						}
					});
				}
			}
		}
	}
	
	/**
	 * Create the popoup menu about all the exporters. If the menu is already build
	 * this method do nothing.
	 */
	private void createPopupMenu(){
		
		if (popupMenu != null && !popupMenu.isDisposed()) popupMenu.dispose();
		
		manager = new MenuManager();
		ActionRegistry registry = new ActionRegistry();

		IWorkbenchPart activePart = getWorkbenchPart();
		ISelection actualSelection =  getLastRawSelection();
		for(IElementDecorator decorator : JaspersoftStudioPlugin.getDecoratorManager().getDecorators()){
			decorator.registerActions(registry, new ArrayList<String>(), activePart);
			decorator.fillContextMenu(registry,manager, (IStructuredSelection)actualSelection);
		}
		popupMenu = new Menu(Display.getCurrent().getActiveShell());
		createMenu(popupMenu, manager.getItems());
	}
	
	@Override
	protected Control createControl(Composite parent) {
		ToolBar buttons = new ToolBar(parent, SWT.FLAT | SWT.WRAP);
		
		ToolItem changeImage = new ToolItem(buttons, SWT.PUSH);
		changeImage.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/equalizer--arrow.png"));
		changeImage.setToolTipText("Set exporter properties");
		changeImage.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				 createPopupMenu();
				 if (popupMenu.isVisible()) {
		    	 popupMenu.setVisible(false);
		     } else {
		    	 locatePopupMenu(popupMenu);
		       popupMenu.setVisible(true);
		     }
			}
		});
		
		return buttons;
	}
	
	@Override
	protected boolean fillWithToolItems(ToolBar parent) {
		ToolItem changeImage = new ToolItem(parent, SWT.PUSH);
		changeImage.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/equalizer--arrow.png"));
		changeImage.setToolTipText("Set exporter properties");
		changeImage.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				 createPopupMenu();
				 if (popupMenu.isVisible()) {
		    	 popupMenu.setVisible(false);
		     } else {
		    	 locatePopupMenu(popupMenu);
		       popupMenu.setVisible(true);
		     }
			}
		});
		getToolItems().add(changeImage);
		return true;
	}
	
	/**
	 * Set the location of the popup menu on the mouse cursor
	 * @param menu
	 */
  protected void locatePopupMenu(Menu menu) {
    menu.setLocation(Display.getCurrent().getCursorLocation());
	}
  
	@Override
	public void dispose() {
		super.dispose();
		if (popupMenu != null){
			popupMenu.dispose();
			popupMenu = null;
		}
		if (manager != null){
			manager.dispose();
			manager = null;
		}
	}
}
