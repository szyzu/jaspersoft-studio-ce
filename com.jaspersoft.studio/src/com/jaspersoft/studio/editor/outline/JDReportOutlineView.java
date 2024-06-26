/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.outline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ResourceTransfer;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.IGraphicalEditor;
import com.jaspersoft.studio.editor.action.CustomDeleteAction;
import com.jaspersoft.studio.editor.action.order.BringBackwardAction;
import com.jaspersoft.studio.editor.action.order.BringForwardAction;
import com.jaspersoft.studio.editor.dnd.ImageResourceDropTargetListener;
import com.jaspersoft.studio.editor.dnd.ImageURLTransfer;
import com.jaspersoft.studio.editor.dnd.JSSTemplateTransferDropTargetListener;
import com.jaspersoft.studio.editor.gef.parts.MainDesignerRootEditPart;
import com.jaspersoft.studio.editor.java2d.J2DLightweightSystem;
import com.jaspersoft.studio.editor.java2d.JSSScrollingGraphicalViewer;
import com.jaspersoft.studio.editor.java2d.figure.JSSScrollableThumbnail;
import com.jaspersoft.studio.editor.menu.AppContextMenuProvider;
import com.jaspersoft.studio.editor.outline.part.TreeEditPart;
import com.jaspersoft.studio.editor.report.AbstractVisualEditor;
import com.jaspersoft.studio.editor.report.EditorContributor;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.IDragable;
import com.jaspersoft.studio.model.MRoot;
import com.jaspersoft.studio.model.band.MBand;
import com.jaspersoft.studio.model.parameter.MParameters;
import com.jaspersoft.studio.model.util.EditPartVisitor;
import com.jaspersoft.studio.model.variable.MVariables;
import com.jaspersoft.studio.preferences.DesignerPreferencePage;
import com.jaspersoft.studio.preferences.bindings.BindingsPreferencePersistence;

import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.type.BandTypeEnum;

/*
 * The Class JDReportOutlineView.
 */
public class JDReportOutlineView extends ContentOutlinePage implements IAdaptable {

	/** The editor. */
	protected IGraphicalEditor editor;

	/** The page book. */
	private PageBook pageBook;

	/** The outline. */
	private Control outline;

	/** The overview. */
	private Canvas overview;

	/** The show overview action. */
	private IAction showOutlineAction, showOverviewAction;

	/** The Constant ID_OUTLINE. */
	public static final String ID_ACTION_OUTLINE = "showOutlineAction";

	/** The Constant ID_OVERVIEW. */
	public static final String ID_ACTION_OVERVIEW = "showOverviewAction";

	/** The thumbnail. */
	private JSSScrollableThumbnail thumbnail;

	/** The dispose listener. */
	private DisposeListener disposeListener;

	private Point mousePosition = new Point(-1, -1);

	/**
	 * On linux the click event on the arrow to expand a tree node is not
	 * catched if the tree element hasen't the focus. So we need a trick to have
	 * here the same behavior of the others operative systems
	 */
	private boolean enableFocusFix = Util.isLinux();

	/**
	 * Instantiates a new jD report outline view.
	 * 
	 * @param editor the editor
	 * @param viewer the viewer
	 */
	public JDReportOutlineView(IGraphicalEditor editor, EditPartViewer viewer) {
		super(viewer);
		this.editor = editor;
	}

	public IGraphicalEditor getEditor() {
		return editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#init(org.eclipse.ui.part.IPageSite)
	 */
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		ActionRegistry registry = editor.getActionRegistry();
		IActionBars bars = pageSite.getActionBars();
		for (Iterator<IAction> it = registry.getActions(); it.hasNext();) {
			IAction ia = it.next();
			// it is necessary to do this because otherwise the save handler
			// will be binded to a different context
			// this will cause the save button in the toolbar not set as enabled
			// when the report is edited by an
			// outline action (like a creation of a field)
			if (!ActionFactory.SAVE.getId().equals(ia.getId())) {
				bars.setGlobalActionHandler(ia.getId(), ia);
			}
		}
		IResource r = null;
		if (editor.getModel() instanceof ANode) {
			ANode n = (ANode) editor.getModel();
			if (n.getJasperConfiguration() != null)
				r = (IResource) n.getJasperConfiguration().get(FileUtils.KEY_FILE);
		}
		JaspersoftStudioPlugin.getInstance().addPreferenceListener(preferenceListener, r);

		bars.updateActionBars();
	}

	private PreferenceListener preferenceListener = new PreferenceListener();

	/**
	 * Listener for the preferences, when the flags to show or hide default
	 * parameters and variables changes then it refresh all the MParameters and
	 * Mvariables nodes on the tree
	 */
	private final class PreferenceListener implements IPropertyChangeListener {

		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			String property = event.getProperty();
			if (property.equals(DesignerPreferencePage.P_SHOW_VARIABLES_DEFAULTS)) {
				RootEditPart rep = getViewer().getRootEditPart();
				new EditPartVisitor<String>(rep) {
					@Override
					public boolean visit(EditPart n) {
						if (n.getModel() instanceof MParameters || n.getModel() instanceof MVariables) {
							// We must be sure that the node has the widget
							// already created, a refresh on a
							// node
							// without widget throw an NPE
							if (((TreeEditPart) n).getWidget() != null)
								n.refresh();
							// We can't stop the research when an MParameters
							// and an MVariables are found,
							// because
							// in this way we will miss to hide the default
							// values of subdatasets
							return false;
						}
						return true;
					}
				};
			}
		}
	}

	protected void initActions(ActionRegistry registry, IActionBars bars) {

	}

	protected ContextMenuProvider getMenuContentProvider() {
		return new AppContextMenuProvider(getViewer(), editor.getActionRegistry());
	}

	/**
	 * Check if the outline page was closed. When the outline page is closed the
	 * control inside the viewer is disposed and removed (so this method will
	 * always catch the getViewer().getControl() == null when the outline was
	 * closed).
	 * 
	 * @return true if the outline was closed and its control disposed. False
	 * otherwise
	 */
	public boolean isDisposed() {
		return (getViewer() == null || getViewer().getControl() == null || getViewer().getControl().isDisposed());
	}

	/**
	 * Configure outline viewer.
	 */
	protected void configureOutlineViewer() {
		final EditPartViewer viewer = getViewer();
		viewer.setEditDomain(editor.getEditDomain());
		viewer.setEditPartFactory(getEditPartFactory());
		ContextMenuProvider provider = getMenuContentProvider();
		viewer.setContextMenu(provider);

		viewer.addDropTargetListener(new JSSTemplateTransferDropTargetListener(viewer));
		viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer) {
			@Override
			protected Object getTemplate() {
				List<Object> models = new ArrayList<Object>();
				Object obj = super.getTemplate();
				if (obj == null) {
					List<?> selection = getViewer().getSelectedEditParts();
					for (Object it : selection) {
						if (it instanceof EditPart) {
							Object model = ((EditPart) it).getModel();
							if (model instanceof IDragable) {
								models.add(model);
							}
							if (model instanceof MBand) {
								BandTypeEnum bandType = ((MBand) model).getBandType();
								if (BandTypeEnum.DETAIL.equals(bandType) || BandTypeEnum.GROUP_FOOTER.equals(bandType)
										|| BandTypeEnum.GROUP_HEADER.equals(bandType)) {
									models.add(model);
								}
							}
						}
					}
				}
				return models;
			}
		});
		// Add images drop listeners
		viewer.addDropTargetListener(new ImageResourceDropTargetListener(viewer, ResourceTransfer.getInstance()));
		viewer.addDropTargetListener(new ImageResourceDropTargetListener(viewer, FileTransfer.getInstance()));
		viewer.addDropTargetListener(new ImageResourceDropTargetListener(viewer, ImageURLTransfer.getInstance()));

		IPageSite site = getSite();
		// team menu was shown, if we register this on the site
		// site.registerContextMenu(provider.getId(), provider,
		// site.getSelectionProvider());

		IToolBarManager tbm = site.getActionBars().getToolBarManager();
		registerToolbarAction(tbm);

		showPage(ID_ACTION_OUTLINE);
	}

	/**
	 * Create on the table manger the toolbar actions for the outline. The
	 * actions are created only if the toolbar manager dosen't contains them
	 * already. Actually the added action are the one the show the standard
	 * outline and the one to show the thumbnail of the report.
	 * 
	 * @param tbm the toolbar manager for the outline.
	 */
	public void registerToolbarAction(IToolBarManager tbm) {
		IContributionItem items[] = tbm.getItems();
		HashSet<String> existingItems = new HashSet<String>();
		for (IContributionItem item : items) {
			existingItems.add(item.getId());
		}

		showOutlineAction = new Action() {
			@Override
			public void run() {
				showPage(ID_ACTION_OUTLINE);
			}
		};
		showOutlineAction.setId(ID_ACTION_OUTLINE);
		showOutlineAction
				.setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/outline.gif")); //$NON-NLS-1$
		showOutlineAction.setToolTipText(Messages.JDReportOutlineView_show_outline_tool_tip);
		if (!existingItems.contains(ID_ACTION_OUTLINE)) {
			ActionContributionItem showOutlineItem = new ActionContributionItem(showOutlineAction);
			showOutlineItem.setVisible(true);
			tbm.add(showOutlineItem);
		}

		showOverviewAction = new Action() {
			@Override
			public void run() {
				showPage(ID_ACTION_OVERVIEW);
			}
		};
		showOverviewAction.setId(ID_ACTION_OVERVIEW);
		showOverviewAction
				.setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/overview.gif")); //$NON-NLS-1$
		showOverviewAction.setToolTipText(Messages.JDReportOutlineView_show_overview_tool_tip);
		if (!existingItems.contains(ID_ACTION_OVERVIEW)) {
			ActionContributionItem showOverviewItem = new ActionContributionItem(showOverviewAction);
			showOverviewItem.setVisible(true);
			tbm.add(showOverviewItem);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.ContentOutlinePage#createControl(org.eclipse.
	 * swt. widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		pageBook = new PageBook(parent, SWT.NONE);

		outline = getViewer().createControl(pageBook);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(outline, "com.jaspersoft.studio.doc.view_outline");

		overview = new Canvas(pageBook, SWT.NONE);
		pageBook.showPage(outline);
		configureOutlineViewer();
		hookOutlineViewer();
		setContents(editor.getModel());
		if (outline instanceof Tree) {
			final Tree tree = (Tree) outline;

			tree.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
					mousePosition.setLocation(-1, -1);
				}

				@Override
				public void focusGained(FocusEvent e) {
					if (enableFocusFix && mousePosition.x != -1) {
						EditPart part = getViewer().findObjectAt(mousePosition);
						if (part != null && part.getModel() instanceof MRoot) {
							EditPart translatedPart = getViewer()
									.findObjectAt(new Point(mousePosition.x + 10, mousePosition.y));
							if (translatedPart != null && translatedPart.getModel() != part.getModel()) {
								TreeItem item = (TreeItem) ((TreeEditPart) translatedPart).getWidget();
								item.setExpanded(!item.getExpanded());
								tree.deselectAll();
								tree.select(item);
								tree.layout(true);

							}
						}
					}
				}
			});

			tree.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					if (e.getSource() instanceof Tree) {
						Tree t = (Tree) e.getSource();
						TreeItem[] ti = t.getSelection();
						if (ti != null && ti.length > 0) {
							Object obj = ti[0].getData();
							if (obj instanceof EditPart) {
								// if the part is openable and understand the
								// open request then perform the
								// request
								EditPart part = (EditPart) obj;
								SelectionRequest request = new SelectionRequest();
								request.setType(RequestConstants.REQ_OPEN);
								if (part.understandsRequest(request)) {
									part.performRequest(request);
									return;
								}
							}
							if (ti.length == 1 && ti[0].getItemCount() > 0) {
								ti[0].setExpanded(!ti[0].getExpanded());
							}
						}
					}
				}
			});

			// listener to move the selected elements up and down on keypress
			tree.addKeyListener(new KeyAdapter() {

				private static final String ACTION_UP_BINDING = "com.jaspersoft.studio.editor.action.order.BringForwardAction";

				private static final String ACTION_DOWN_BINDING = "com.jaspersoft.studio.editor.action.order.BringBackwardAction";

				@Override
				public void keyPressed(KeyEvent e) {
					if (BindingsPreferencePersistence.isPressed(ACTION_DOWN_BINDING)) {
						BringForwardAction action = new BringForwardAction((AbstractVisualEditor) getEditor());
						action.setLazyEnablementCalculation(true);
						if (action.isEnabled()) {
							action.run();
						}
					} else if (BindingsPreferencePersistence.isPressed(ACTION_UP_BINDING)) {
						BringBackwardAction action = new BringBackwardAction((AbstractVisualEditor) getEditor());
						action.setLazyEnablementCalculation(true);
						if (action.isEnabled()) {
							action.run();
						}
					} else if (e.keyCode == SWT.BS) {
						CustomDeleteAction action = new CustomDeleteAction((AbstractVisualEditor) getEditor());
						action.setSelectionProvider(getViewer());
						action.update();
						if (action.isEnabled())
							action.run();
					}

				}
			});

			// This listener display the tooltip text for the abbreviated nodes
			// names
			tree.addMouseMoveListener(new MouseMoveListener() {

				public void mouseMove(MouseEvent e) {
					mousePosition.setLocation(e.x, e.y);
					EditPart part = getViewer().findObjectAt(new Point(e.x, e.y));
					Tree t = (Tree) e.getSource();
					if (part != null && part.getModel() != null && !(part.getModel() instanceof MRoot)) {
						Object model = part.getModel();
						String toolTipText = Misc.nvl(((ANode) model).getToolTip());
						String displayText = Misc.nvl(((ANode) model).getDisplayText());
						String text = "";
						if (!toolTipText.isEmpty() && !toolTipText.equals(displayText))
							text = toolTipText + "\n";
						text += displayText;
						t.setToolTipText(text);
						return;
					}
					t.setToolTipText(null);
				}

			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	@Override
	public void dispose() {
		JaspersoftStudioPlugin.getInstance().removePreferenceListener(preferenceListener);
		unhookOutlineViewer();
		if (thumbnail != null)
			thumbnail = null;
		super.dispose();
	}

	private EditorContributor editorContributor;

	private EditPartFactory editPartFactory;

	public EditPartFactory getEditPartFactory() {
		if (editPartFactory == null)
			editPartFactory = new OutlineTreeEditPartFactory();
		return editPartFactory;
	}

	public void setEditPartFactory(EditPartFactory editPartFactory) {
		this.editPartFactory = editPartFactory;
		getViewer().setEditPartFactory(getEditPartFactory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		if (type == ZoomManager.class)
			return editor.getGraphicalViewer().getProperty(ZoomManager.class.toString());
		if (type == EditorContributor.class) {
			if (editorContributor == null)
				editorContributor = new EditorContributor(editor.getEditDomain());
			return editorContributor;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.ContentOutlinePage#getControl()
	 */
	@Override
	public Control getControl() {
		return pageBook;
	}

	/**
	 * Hook outline viewer.
	 */
	protected void hookOutlineViewer() {
		editor.getSelectionSynchronizer().addViewer(getViewer());
	}

	/**
	 * Initialize overview.
	 */
	protected void initializeOverview() {
		LightweightSystem lws = new J2DLightweightSystem(overview);

		RootEditPart rep = editor.getGraphicalViewer().getRootEditPart();
		if (rep instanceof MainDesignerRootEditPart) {
			MainDesignerRootEditPart root = (MainDesignerRootEditPart) rep;
			thumbnail = new JSSScrollableThumbnail((Viewport) root.getFigure(),
					(MRoot) getViewer().getContents().getModel());
			thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
			lws.setContents(thumbnail);
			disposeListener = new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					if (thumbnail != null) {
						thumbnail.deactivate();
						thumbnail = null;
					}
				}
			};
			editor.getEditor().addDisposeListener(disposeListener);
		}
		lws.setControl(overview);
	}

	/**
	 * Sets the contents.
	 * 
	 * @param contents the new contents
	 */
	public void setContents(Object contents) {
		if (getViewer().getEditPartFactory() != null) {
			// Really important to deselect all the element before set the
			// content
			// otherwise the treeviewer can try to select the old elements after
			// the set
			// content but it can may be disposed cause the change of content
			getViewer().deselectAll();
			getViewer().setContents(contents);
		}
		if (outline instanceof Tree) {
			Tree tree = (Tree) outline;
			if (!tree.isDisposed() && tree.getItems() != null && tree.getItems().length > 0)
				tree.getItem(0).setExpanded(true);
		}
	}

	/**
	 * Show page.
	 * 
	 * @param id the id
	 */
	protected void showPage(String id) {
		if (ID_ACTION_OUTLINE.equals(id)) {
			showOutlineAction.setChecked(true);
			showOverviewAction.setChecked(false);
			pageBook.showPage(outline);
			((JSSScrollingGraphicalViewer) editor.getGraphicalViewer()).setPaintOnlyVisibleElements(true);
			if (thumbnail != null)
				thumbnail.setVisible(false);
		} else if (ID_ACTION_OVERVIEW.equals(id)) {
			((JSSScrollingGraphicalViewer) editor.getGraphicalViewer()).setPaintOnlyVisibleElements(false);
			if (thumbnail == null)
				initializeOverview();
			showOutlineAction.setChecked(false);
			showOverviewAction.setChecked(true);
			pageBook.showPage(overview);
			thumbnail.setVisible(true);
		}
	}

	/**
	 * Unhook outline viewer.
	 */
	protected void unhookOutlineViewer() {
		editor.getSelectionSynchronizer().removeViewer(getViewer());
		FigureCanvas editor2 = editor.getEditor();
		if (disposeListener != null && editor2 != null && !editor2.isDisposed())
			editor2.removeDisposeListener(disposeListener);
	}

	public void setTreeSelection(ISelection s) {
		if (s != null && s instanceof StructuredSelection && outline instanceof Tree) {
			StructuredSelection sel = (StructuredSelection) s;
			List<?> sobj = sel.toList();
			List<TreeItem> toSelect = new ArrayList<TreeItem>();
			Tree tree = (Tree) outline;
			tree.getItemCount();
			checkItems(tree.getItems(), toSelect, sobj);
			if (!toSelect.isEmpty())
				tree.setSelection(toSelect.toArray(new TreeItem[toSelect.size()]));
		} else
			setSelection(s);
	}

	public void checkItems(TreeItem[] items, List<TreeItem> toSelect, List<?> sobj) {
		if (items == null)
			return;
		for (TreeItem ti : items) {
			for (Object obj : sobj) {
				if (obj != null && ti.getData() != null) {
					if (obj == ti.getData())
						toSelect.add(ti);
					else if (obj instanceof EditPart && ti.getData() instanceof EditPart) {
						if (((EditPart) obj).getModel() == ((EditPart) ti.getData()).getModel())
							toSelect.add(ti);
					}
				}
			}
			checkItems(ti.getItems(), toSelect, sobj);
		}
	}
}
