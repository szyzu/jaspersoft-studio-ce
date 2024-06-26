/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.jaspersoft.studio.editor.IGraphicalEditor;
import com.jaspersoft.studio.editor.gef.ui.actions.RZoomComboContributionItem;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.toolbars.CommonToolbarHandler;

/*
 * Manages the installation/deinstallation of global actions for multi-page editors. Responsible for the redirection of
 * global actions to the active editor. Multi-page contributor replaces the contributors for the individual editors in
 * the multi-page editor.
 * 
 * @author Chicu Veaceslav
 */
public class JrtxEditorContributor extends MultiPageEditorActionBarContributor {

	/** The global action keys. */
	private List<String> globalActionKeys = new ArrayList<String>();

	/** The retarget actions. */
	private List<RetargetAction> retargetActions = new ArrayList<RetargetAction>();

	/** The registry. */
	private ActionRegistry registry = new ActionRegistry();

	/** The zoom combo. */
	private RZoomComboContributionItem zoomCombo;

	/**
	 * Creates a multi-page contributor.
	 */
	public JrtxEditorContributor() {
		super();
		createActions();
	}

	/**
	 * Initialization.
	 * 
	 * @param bars
	 *          the bars
	 */
	public void init(IActionBars bars) {
		buildActions();
		declareGlobalActionKeys();
		super.init(bars);
	}

	/**
	 * Builds the actions.
	 * 
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(new DeleteRetargetAction());

		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());

	}

	/**
	 * Adds the retarded actions.
	 * 
	 * @param action
	 *          The action to add
	 */
	protected void addRetargetAction(RetargetAction action) {
		addAction(action);
		retargetActions.add(action);
		getPage().addPartListener(action);
		addGlobalActionKey(action.getId());
	}

	/**
	 * Adds global action key.
	 * 
	 * @param key
	 *          The key to add
	 */
	protected void addGlobalActionKey(String key) {
		globalActionKeys.add(key);
	}

	/**
	 * Adds to action registry an action.
	 * 
	 * @param action
	 *          The action to add
	 */
	protected void addAction(IAction action) {
		getActionRegistry().registerAction(action);
	}

	/**
	 * Gets the registry.
	 * 
	 * @return ActionRegistry The registry
	 */
	protected ActionRegistry getActionRegistry() {
		return registry;
	}

	/**
	 * Declares the global action keys.
	 * 
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.COPY.getId());
		addGlobalActionKey(ActionFactory.PASTE.getId());
		addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
		addGlobalActionKey(ActionFactory.DELETE.getId());
	}

	/**
	 * Gets the action.
	 * 
	 * @param id
	 *          the id
	 * @return the action
	 */
	protected IAction getAction(String id) {
		return getActionRegistry().getAction(id);
	}

	/**
	 * Sets the page to active status.
	 * 
	 * @param activeEditor
	 *          The active editor
	 */
	public void setActivePage(IEditorPart activeEditor) {
		CommonToolbarHandler.clearToolbars(getActionBars());
		IActionBars bars = getActionBars();
		removeZoom(bars.getToolBarManager());
		bars.clearGlobalActionHandlers();
		if (activeEditor instanceof ITextEditor) {
			if (textEditorContributor == null) {
				textEditorContributor = new TextEditorActionContributor();
				textEditorContributor.init(bars, activeEditor.getSite().getPage());
			}
			textEditorContributor.setActiveEditor(activeEditor);
		} else if (activeEditor instanceof IGraphicalEditor) {
			addZoom(bars.getToolBarManager());
			if (activeEditor instanceof IGraphicalEditor) {
				GraphicalViewer graphicalViewer = ((IGraphicalEditor) activeEditor).getGraphicalViewer();
				ZoomManager property = (ZoomManager) graphicalViewer.getProperty(ZoomManager.class.toString());
				if (property != null)
					zoomCombo.setZoomManager(property);
			}
			ActionRegistry registry = (ActionRegistry) activeEditor.getAdapter(ActionRegistry.class);
			if (registry != null)
				for (String id : globalActionKeys) {
					bars.setGlobalActionHandler(id, registry.getAction(id));
				}
		}
		bars.updateActionBars();
	}

	/**
	 * Returns the action registed with the given text editor.
	 * 
	 * @param editor
	 *          the editor
	 * @param actionID
	 *          the action id
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(ITextEditor editor, String actionID) {
		return (editor == null ? null : editor.getAction(actionID));
	}

	/**
	 * Creates the actions.
	 */
	private void createActions() {
	}

	/**
	 * Adds the undo and redo items to the toolbar.
	 * 
	 * @param tbm
	 *          The IToolBarManager
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(IToolBarManager)
	 */
	public void contributeToToolBar(IToolBarManager tbm) {
		tbm.add(getAction(ActionFactory.UNDO.getId()));
		tbm.add(getAction(ActionFactory.REDO.getId()));

		tbm.add(new Separator());

		addZoom(tbm);
	}

	private void addZoom(IToolBarManager tbm) {
		tbm.add(getAction(GEFActionConstants.ZOOM_IN));
		tbm.add(getAction(GEFActionConstants.ZOOM_OUT));
		if (zoomCombo == null)
			zoomCombo = new RZoomComboContributionItem();
		zoomCombo.setEnabled(true);
		tbm.add(zoomCombo);
		tbm.update(true);
	}

	private void removeZoom(IToolBarManager tbm) {
		tbm.remove(GEFActionConstants.ZOOM_IN);
		tbm.remove(GEFActionConstants.ZOOM_OUT);
		tbm.remove(zoomCombo.getId());
		zoomCombo.setEnabled(false);
		tbm.update(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void contributeToMenu(IMenuManager manager) {
		super.contributeToMenu(manager);

		MenuManager viewMenu = new MenuManager(Messages.JrxmlEditorContributor_view);
		viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
		viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));

	}

	private TextEditorActionContributor textEditorContributor = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.EditorActionBarContributor#contributeToStatusLine(org.eclipse.jface.action.IStatusLineManager)
	 */
	@Override
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
		super.contributeToStatusLine(statusLineManager);
		if (textEditorContributor != null)
			textEditorContributor.contributeToStatusLine(statusLineManager);

		statusLineManager.setMessage(""); //$NON-NLS-1$
	}

	/**
	 * Disposes the contributor. Removes all {@link RetargetAction}s that were {@link org.eclipse.ui.IPartListener}s on
	 * the {@link org.eclipse.ui.IWorkbenchPage} and disposes them. Also disposes the action registry.
	 * <P>
	 * Subclasses may extend this method to perform additional cleanup.
	 * 
	 * @see org.eclipse.ui.part.EditorActionBarContributor#dispose()
	 */
	public void dispose() {
		for (int i = 0; i < retargetActions.size(); i++) {
			RetargetAction action = retargetActions.get(i);
			getPage().removePartListener(action);
			action.dispose();
		}
		registry.dispose();
		retargetActions = null;
		registry = null;
	}

}
