/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.report;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySource2;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.IJROBjectEditor;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.ExpressionContext.Visibility;
import com.jaspersoft.studio.editor.expression.ExpressionEditorSupportUtil;
import com.jaspersoft.studio.editor.name.NamedSubeditor;
import com.jaspersoft.studio.editor.part.MultiPageToolbarEditorPart;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.model.MPage;
import com.jaspersoft.studio.model.MRoot;
import com.jaspersoft.studio.model.parameter.MParameter;
import com.jaspersoft.studio.model.style.StyleTemplateFactory;
import com.jaspersoft.studio.plugin.ExtensionManager;
import com.jaspersoft.studio.properties.Activator;
import com.jaspersoft.studio.properties.preferences.PropertiesPreferencePage;
import com.jaspersoft.studio.properties.view.ITabbedPropertySheetPageContributor;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.descriptor.expression.dialog.JRExpressionEditor;
import com.jaspersoft.studio.utils.ExpressionUtil;
import com.jaspersoft.studio.utils.SelectionHelper;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileExtension;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRSimpleTemplate;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRTemplateReference;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignReportTemplate;
import net.sf.jasperreports.engine.design.JRDesignScriptlet;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryUtil;
import net.sf.jasperreports.repo.SimpleRepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryResourceContext;

/*
 * The Class ReportContainer.
 * 
 * @author Chicu Veaceslav
 */
public class ReportContainer extends MultiPageToolbarEditorPart
		implements ITabbedPropertySheetPageContributor, IJROBjectEditor, CachedSelectionProvider {

	/**
	 * Key used to save, retrieve the selection cache from the jasper reports
	 * configuration
	 */
	public static final String SELECTION_CACHE_KEY = "SELECTION_CACHE_PROVIDER";

	/**
	 * Property used by an element to ask to the container to check if for that
	 * element there is an editor opened and in that case close it. The property
	 * change event must have the source value set with the JRelement that it is
	 * requesting the editor closing
	 */
	public static final String CLOSE_EDITOR_PROPERTY = "closeElementEditor";

	/**
	 * Property used by an element to ask to the container to check if for that
	 * element there is an editor opened and in that case refresh its name. The
	 * property change event must have the source value set with the JRelement that
	 * it is requesting the editor closing
	 */
	public static final String RENAME_EDITOR_PROPERTY = "renamedElementEditor";

	/**
	 * Flag used to know if in the current editor the background image is in edit
	 * mode, if available
	 */
	private boolean editBackgroundImage = false;

	/**
	 * The model.
	 */
	private INode model = null;

	/**
	 * The editors.
	 */
	private List<AbstractVisualEditor> editors = new ArrayList<AbstractVisualEditor>();

	/**
	 * Map of the subeditors opened, the key is the JRObject edited in the subeditor
	 * the value it the subeditor used to edit it
	 */
	private Map<Object, AbstractVisualEditor> ccMap = new HashMap<Object, AbstractVisualEditor>();

	/**
	 * The extension manager
	 */
	private ExtensionManager m = JaspersoftStudioPlugin.getExtensionManager();

	/**
	 * The selection cache used by all the editors in this container (report editor
	 * and eventually its subeditors) The selection cache is passed to the
	 * subeditors trough the jasper configuration. The cached is stored when this
	 * container is created and can be retrieved with the SELECTION_CACHE_KEY
	 */
	private CommonSelectionCacheProvider selectionCache;

	/** The parent. */
	private EditorPart parent;
	private PropertyChangeSupport propertyChangeSupport;
	private JasperReportsConfiguration jrContext;

	public PropertyChangeSupport getPropertyChangeSupport() {
		if (propertyChangeSupport == null)
			propertyChangeSupport = new PropertyChangeSupport(this);
		return propertyChangeSupport;
	}

	/**
	 * Instantiates a new report container.
	 * 
	 * @param parent
	 *            the parent
	 */
	public ReportContainer(EditorPart parent, JasperReportsConfiguration jrContext) {
		this.parent = parent;
		this.jrContext = jrContext;
		this.selectionCache = new CommonSelectionCacheProvider();
		selectionCache.setAllowingDishomogeneousSelection(true);
		// Store the selection cache
		jrContext.put(SELECTION_CACHE_KEY, selectionCache);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getActiveEditor()
	 */
	@Override
	public IEditorPart getActiveEditor() {
		return super.getActiveEditor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	@Override
	protected void createPages() {
		try {
			reportEditor = createReportEditor(jrContext);
			int index = addPage(reportEditor, getEditorInput());
			setPageText(index, Messages.common_main_report);
			setPageImage(index, reportEditor.getPartImage());
			editors.add(reportEditor);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), Messages.common_error_creating_nested_visual_editor, null,
					e.getStatus());
		}
		getEditorSite().getActionBarContributor();
	}

	/**
	 * Create the editor used to edit visually the report
	 * 
	 * @param the
	 *            current jasper reports configuration
	 * @return a not null report editor
	 */
	protected ReportEditor createReportEditor(JasperReportsConfiguration context) {
		return new ReportEditor(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		for (AbstractVisualEditor editor : editors) {
			editor.doSave(monitor);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		for (AbstractVisualEditor editor : editors) {
			editor.doSaveAs();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	private PropertyChangeListener modelListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent evt) {

			String propertyName = evt.getPropertyName();
			if (propertyName.equals(CLOSE_EDITOR_PROPERTY)) {
				AbstractVisualEditor obj = ccMap.get(evt.getSource());
				if (obj != null)
					removeEditorPage(evt, obj);
			} else if (propertyName.equals(RENAME_EDITOR_PROPERTY)) {
				AbstractVisualEditor obj = ccMap.get(evt.getSource());
				if (obj != null && obj instanceof NamedSubeditor) {
					((NamedSubeditor) obj).updateEditorName();
				}
			} else if (evt.getNewValue() != null && evt.getOldValue() == null) {
				// createEditorPage(evt.getNewValue());
			} else if (evt.getNewValue() == null && evt.getOldValue() != null) {
				AbstractVisualEditor obj = ccMap.get(evt.getOldValue());
				if (obj != null)
					removeEditorPage(evt, obj);
			}
			getPropertyChangeSupport().firePropertyChange(evt);
			if (!propertyName.equals(MGraphicElement.FORCE_GRAPHICAL_REFRESH)
					&& !propertyName.equals(JSSCompoundCommand.REFRESH_UI_EVENT)) {
				firePropertyChange(ISaveablePart.PROP_DIRTY);
			}
		}

	};

	/**
	 * Sets the model.
	 * 
	 * @param model
	 *            the new model
	 */
	public void setModel(INode model) {
		if (this.model != null && this.model.getChildren() != null && !this.model.getChildren().isEmpty())
			this.model.getChildren().get(0).getPropertyChangeSupport().removePropertyChangeListener(modelListener);
		if (model != null && model.getChildren() != null && !model.getChildren().isEmpty())
			model.getChildren().get(0).getPropertyChangeSupport().addPropertyChangeListener(modelListener);
		this.model = model;
		updateVisualView();
	}

	private AbstractVisualEditor createEditorPage(Object obj) {
		AbstractVisualEditor ave = ccMap.get(obj);
		try {
			if (ave == null) {
				JasperDesign jd = getModel().getJasperDesign();
				MRoot root = new MRoot(null, jd);
				root.setJasperConfiguration(jrContext);
				MPage rep = new MPage(root, jd);
				rep.setJasperConfiguration(jrContext);
				ANode node = m.createNode(rep, obj, -1);
				if (node != null) {
					// Initialize the subeditors
					node.createSubeditor();

					ave = m.getEditor(obj, jrContext);
					if (ave != null) {
						ave.getEditDomain().setCommandStack(reportEditor.getEditDomain().getCommandStack());
						// Necessary to create element with the drag and drop
						// inside a subeditor
						ave.getEditDomain().setPaletteViewer(reportEditor.getEditDomain().getPaletteViewer());

						final int index = addPage(ave, getEditorInput());

						editors.add(ave);
						ccMap.put(node.getValue(), ave);
						ave.setModel(root);
						setPageText(index, ave.getPartName());
						setPageImage(index, ave.getPartImage());

						rep.getPropertyChangeSupport().addPropertyChangeListener(modelListener);

						ave.addPropertyListener(titleListener);
					}
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return ave;
	}

	private IPropertyListener titleListener = new IPropertyListener() {

		@Override
		public void propertyChanged(Object source, int propId) {
			if (propId == IWorkbenchPart.PROP_TITLE) {
				int ind = editors.indexOf((AbstractVisualEditor) source);
				setPageText(ind, ((AbstractVisualEditor) source).getPartName());
			}
		}
	};

	private void removeEditorPage(PropertyChangeEvent evt, AbstractVisualEditor ave) {
		if (ave.getModel() != null && modelListener != null)
			ave.getModel().getPropertyChangeSupport().addPropertyChangeListener(modelListener);
		ave.setModel(null);
		ave.removePropertyListener(titleListener);
		int ind = editors.indexOf(ave);
		// Check if the removed page is the current one
		boolean switchToMainPage = ind == getActivePage();
		if (switchToMainPage) {
			// If the removed page was the current one go back to the main page
			switchEditorPage(0);
		}
		if (ind >= 0 && ind < getPageCount()) {
			removePage(ind);
		}
		editors.remove(ind);
		if (evt != null) {
			ccMap.remove(evt.getOldValue());
		} else {
			Object okey = null;
			for (Object key : ccMap.keySet()) {
				AbstractVisualEditor value = ccMap.get(key);
				if (value != null && value == ave) {
					okey = key;
					break;
				}
			}
			ccMap.remove(okey);
		}
		ave.dispose();
	}

	/**
	 * Update visual view.
	 */
	public void updateVisualView() {
		if (!editors.isEmpty()) {
			editors.get(0).setModel(this.model);
			while (editors.size() > 1) {
				AbstractVisualEditor ave = editors.get(1);
				removeEditorPage(null, ave);
			}
			setActiveEditor(editors.get(0));
		}
		for (AbstractVisualEditor ave : editors) {
			ave.setModel(this.model);
		}

		// AbstractVisualEditor ave = getMainEditor();
		// if (ave != null)
		// ave.setModel(this.model);
	}

	public AbstractVisualEditor getMainEditor() {
		if (editors != null && !editors.isEmpty())
			return editors.get(0);
		return null;
	}

	/**
	 * Check if there are subeditors opened
	 * 
	 * @return true if there are subeditors opened, false otherwise
	 */
	public boolean hasSubeditorOpened() {
		return (ccMap != null && !ccMap.isEmpty());
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	public INode getModel() {
		return model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		if (type == IPropertySource.class)
			return getPropertySheetPage();
		if (type == IPropertySource2.class)
			return getPropertySheetPage();
		if (type == IPropertySheetPage.class)
			return getPropertySheetPage();
		return super.getAdapter(type);
	}

	/** The property sheet page. */
	private TabbedPropertySheetPage propertySheetPage;

	private ReportEditor reportEditor;

	/**
	 * Gets the property sheet page.
	 * 
	 * @return the property sheet page
	 */
	public TabbedPropertySheetPage getPropertySheetPage() {
		propertySheetPage = new TabbedPropertySheetPage(ReportContainer.this, true);
		return propertySheetPage;
	}

	public CommonSelectionCacheProvider getSelectionCache() {
		return selectionCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.
	 * ITabbedPropertySheetPageContributor#getContributorId()
	 */
	public String getContributorId() {
		return "com.jaspersoft.studio.editor.report.ReportContainer"; //$NON-NLS-1$
	}

	/**
	 * Create a fake command to force the refresh of the editor and outline panels,
	 * this override the disable refresh flag, so calling this the editor area is
	 * always updated
	 */
	protected void refreshVisuals(INode report) {
		if (report != null) {
			PropertyChangeEvent event = new PropertyChangeEvent(report.getJasperDesign(),
					JSSCompoundCommand.REFRESH_UI_EVENT, null, null);
			report.getPropertyChangeSupport().firePropertyChange(event);
		}
	}

	/**
	 * Get the object that is modified by a subeditor. It calculated searching the
	 * last node of the mpage of the subeditor (since the first are the styles, the
	 * dataset...)
	 * 
	 * @param searchNode
	 *            the starting node
	 * @return the node modified by the subeditor
	 */
	/*
	 * private INode getInnerModel(INode searchNode){ INode actualNode = searchNode;
	 * if (actualNode instanceof MRoot && actualNode.getChildren().size() >0){
	 * return getInnerModel(actualNode.getChildren().get(0)); } else if (actualNode
	 * instanceof MPage && actualNode.getChildren().size()>0){ return
	 * actualNode.getChildren().get(actualNode.getChildren().size()-1); } return
	 * actualNode; }
	 */

	@Override
	protected void postPageChange(int newPageIndex, int oldPageIndex) {
		AbstractVisualEditor activeEditor = editors.get(newPageIndex);
		// FIXME: This forced refresh should be not necessary since now every
		// element has it's own full model and the event
		// are handled natively
		// request the rapaint of the element on the main editor node when
		// switching between the subeditors, supposing they
		// were modified in the subeditor
		// if (oldPageIndex > 0){
		// AbstractVisualEditor oldEditor = editors.get(oldPageIndex);
		// INode subModel = getInnerModel(oldEditor.getModel());
		// ((JRChangeEventsSupport)subModel.getValue()).getEventSupport().firePropertyChange(MGraphicElement.FORCE_GRAPHICAL_REFRESH,
		// null, null);
		// }
		IEditorActionBarContributor contributor = parent.getEditorSite().getActionBarContributor();
		if (contributor != null && contributor instanceof MultiPageEditorActionBarContributor) {
			MultiPageEditorActionBarContributor toolbarContributor = (MultiPageEditorActionBarContributor) contributor;
			toolbarContributor.setActivePage(activeEditor);
		}
	}

	public void openEditor(Object obj, ANode node) {
		if (getEditorInput() instanceof FileEditorInput) {
			if (obj instanceof JRDesignReportTemplate || obj instanceof JRSimpleTemplate || obj instanceof JRStyle
					|| obj instanceof JRConditionalStyle || obj instanceof JRTemplateReference) {
				StyleTemplateFactory.openEditor(obj, getEditorInput(), node);
				return;
			}
			if (obj instanceof JRDesignSubreport) {
				IFile file = (IFile) jrContext.get(FileUtils.KEY_FILE);
				String parentPath = file.getParent().getLocation().toFile().getAbsolutePath();
				SimpleRepositoryResourceContext context = SimpleRepositoryResourceContext.of(parentPath);
				RepositoryContext repoContext = SimpleRepositoryContext.of(jrContext, context);
				RepositoryUtil ru = RepositoryUtil.getInstance(repoContext);
				if (getEditorInput() instanceof FileEditorInput) {
					JRDesignSubreport s = (JRDesignSubreport) obj;
					if (s.getExpression() != null) {
						String path = ExpressionUtil.cachedExpressionEvaluationString(s.getExpression(), jrContext);
						if (path != null) {
							String fpath = path.replaceAll("\\.jasper$", FileExtension.PointJRXML);
							try {
								ru.getBytesFromLocation(fpath);
							} catch (JRException e) {
								if (e.getMessage().startsWith("Byte data not found at:")
										&& s.getExpression().getText().trim().contains("$P{SUBREPORT_DIR}")) {
									String p = ExpressionUtil.cachedExpressionEvaluationString(
											new JRDesignExpression("$P{SUBREPORT_DIR}"), jrContext);
									p = StringUtils.remove(path, p);
									if (p != null) {
										fpath = p.replaceAll("\\.jasper$", FileExtension.PointJRXML);
										try {
											ru.getBytesFromLocation(fpath);
										} catch (JRException e1) {
											e1.printStackTrace();
											try {
												ru.getBytesFromLocation(p);
												if (!UIUtils.showConfirmation("Subreport File",
														String.format(
																"File %s does not exists, do you want to open %s?",
																fpath, path)))
													return;
												fpath = p;
											} catch (JRException e2) {
												UIUtils.showError(e2);
												return;
											}
										}
									}
								} else {
									e.printStackTrace();
									try {
										ru.getBytesFromLocation(path);
										if (!UIUtils.showConfirmation("Subreport File", String.format(
												"File %s does not exists, do you want to open %s?", fpath, path)))
											return;
										fpath = path;
									} catch (JRException e1) {
										UIUtils.showError(e1);
										return;
									}
								}
							}
							SelectionHelper.openEditor((FileEditorInput) getEditorInput(), fpath);
						}
					}
				}
				return;
			}
			if (obj instanceof JRDesignImage) {
				if (getEditorInput() instanceof FileEditorInput) {
					JRDesignImage s = (JRDesignImage) obj;
					if (s.getExpression() != null)
						SelectionHelper.openEditor((FileEditorInput) getEditorInput(),
								ExpressionUtil.cachedExpressionEvaluationString(s.getExpression(), jrContext));
				}
				return;
			}
		}
		if (obj instanceof JRDesignScriptlet) {
			String str = ((JRDesignScriptlet) obj).getValueClassName();
			IProject prj = ((FileEditorInput) getEditorInput()).getFile().getProject();
			IJavaProject javaProject = JavaCore.create(prj);
			if (javaProject != null)
				try {
					IType type = javaProject.findType(str);
					if (type != null)
						JavaUI.openInEditor(type);
				} catch (PartInitException e) {
					e.printStackTrace();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}

		}
		if (obj instanceof JasperDesign) {
			setActivePage(0);
		} else {
			AbstractVisualEditor ave = ccMap.get(obj);
			if (ave == null) {
				// need to create the new subeditor
				ave = createEditorPage(obj);
				/**
				 * If was created another editor with inside an mpage the i save the parent of
				 * the current node inside the page. Doing this it is always possible from a
				 * node get its real parent and go back into the hierarchy. This information
				 * need only to be saved here since when an element change parent all the open
				 * editors for the element are closed
				 */
				if (ave != null && ave.getModel().getChildren().size() > 0
						&& ave.getModel().getChildren().get(0) instanceof MPage) {
					MPage pageElement = (MPage) ave.getModel().getChildren().get(0);
					pageElement.setRealParent(node.getParent());
				}
			}

			if (ave != null) {
				if (getActiveEditor() != ave) {
					int index = editors.indexOf(ave);
					if (index > 0 && index <= editors.size() - 1) {
						setActivePage(index);
						final Composite prnt = getContainer().getParent();
						final Point size = prnt.getSize();
						prnt.getParent().setSize(size.x - 2, size.y - 2);
						UIUtils.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								prnt.getParent().setSize(size.x, size.y);
							}
						});
					}
				}
			}
		}
		if (obj instanceof JRDesignParameter && node instanceof MParameter) {
			MParameter param = (MParameter) node;
			JRDesignExpression defaultValueExpression = (JRDesignExpression) param
					.getPropertyValue(JRDesignParameter.PROPERTY_DEFAULT_VALUE_EXPRESSION);
			ExpressionContext expContext = param.getExpressionContext();
			if (expContext != null) {
				expContext.setVisibilities(EnumSet.of(Visibility.SHOW_PARAMETERS));
			}
			if (!ExpressionEditorSupportUtil.isExpressionEditorDialogOpen()) {
				JRExpressionEditor wizard = new JRExpressionEditor();
				wizard.setValue(defaultValueExpression);
				wizard.setExpressionContext(expContext);
				WizardDialog dialog = ExpressionEditorSupportUtil.getExpressionEditorWizardDialog(UIUtils.getShell(),
						wizard);
				if (dialog.open() == Dialog.OK) {
					JRDesignExpression value = wizard.getValue();
					param.setPropertyValue(JRDesignParameter.PROPERTY_DEFAULT_VALUE_EXPRESSION, value);
				}
			}
		}
	}

	/**
	 * Check if in the current editor the background image should be editable
	 * 
	 * @return true if the background image is editable, false otherwise
	 */
	public boolean isBackgroundImageEditable() {
		return editBackgroundImage;
	}

	/**
	 * Set in the current editor the edit mode for the background image. However it
	 * will be shown only if there is a background image defined
	 * 
	 * @param value
	 *            true if the background should be editable, false otherwise
	 */
	public void setBackgroundImageEditable(boolean value) {
		editBackgroundImage = value;
	}

	/**
	 * Return as default selected page in the properties view the first page or the
	 * last page (advanced page) considering what is set in the preferences
	 */
	@Override
	public int getDefaultSelectedPageIndex() {
		if (propertySheetPage != null && getModel() instanceof MRoot) {
			if (getModel().getChildren().size() > 0) {
				JasperReportsConfiguration jConfig = ((ANode) getModel().getChildren().get(0)).getJasperConfiguration();
				if (jConfig != null) {
					boolean defaultValue = Activator.getDefault().getPreferenceStore().getBoolean(PropertiesPreferencePage.P_DEFAULT_ADVANCED_TAB);
					boolean advancedDefault = jConfig.getPropertyBoolean(PropertiesPreferencePage.P_DEFAULT_ADVANCED_TAB, defaultValue);
					if (advancedDefault) {
						return propertySheetPage.getCurrentTabs().size() - 1;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * When the page change force a visual refresh of the content of the editor
	 */
	protected void pageChange(final int newPageIndex, final int oldPageIndex) {
		super.pageChange(newPageIndex, oldPageIndex);
		AbstractVisualEditor activeEditor = (AbstractVisualEditor) getActiveEditor();
		JSSCompoundCommand.forceRefreshVisuals(JSSCompoundCommand.getMainNode(activeEditor.getModel()));
	}
}
