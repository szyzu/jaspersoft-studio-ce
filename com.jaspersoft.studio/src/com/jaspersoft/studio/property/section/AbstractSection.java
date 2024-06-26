/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.editor.report.EditorContributor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.properties.internal.IHighlightPropertyWidget;
import com.jaspersoft.studio.properties.internal.IWidgetsProviderSection;
import com.jaspersoft.studio.properties.internal.WidgetDescriptor;
import com.jaspersoft.studio.properties.view.AbstractPropertySection;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetWidgetFactory;
import com.jaspersoft.studio.properties.view.validation.ValidationError;
import com.jaspersoft.studio.property.ISetValueCommandProvider;
import com.jaspersoft.studio.property.SetValueCommand;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.SPWidgetFactory;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

/*
 * Abstract class for a section in a tab in the properties view.
 */
public abstract class AbstractSection extends AbstractPropertySection
		implements PropertyChangeListener, IWidgetsProviderSection {
	protected Map<Object, ASPropertyWidget<?>> widgets = new HashMap<>();

	protected JasperReportsConfiguration jasperReportsContext;
	private List<APropertyNode> elements;
	private APropertyNode element;
	private EditDomain editDomain;

	protected HashMap<Object, WidgetDescriptor> providedProperties = null;

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.view.ITabbedPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		setRefreshing(true);
		APropertyNode el = getElement();
		if (el != null) {
			el.getPropertyDescriptors();
			for (Object key : widgets.keySet())
				widgets.get(key).setData(el, el.getPropertyValue(key));
		}
		setRefreshing(false);
	}

	public ASPropertyWidget<?> createWidget4Property(Composite composite, Object property) {
		return createWidget4Property(composite, property, true);
	}

	public ASPropertyWidget<?> createWidget4Property(Composite composite, Object property, boolean showLabel) {
		return createWidget4Property(getElement(), composite, property, showLabel);
	}

	public ASPropertyWidget<?> createWidget(Composite composite, Object property, boolean showLabel,
			IPropertyDescriptor pd) {
		CLabel label = createLabel(composite, showLabel, pd);
		ASPropertyWidget<?> widget = SPWidgetFactory.createWidget(composite, this, pd);
		if (widget != null) {
			widget.setLabel(label);
			widgets.put(pd.getId(), widget);
			return widget;
		}
		return null;
	}

	public ASPropertyWidget<?> createWidget4Property(APropertyNode element, Composite composite, Object property,
			boolean showLabel) {
		if (element != null) {
			IPropertyDescriptor[] pds = element.getPropertyDescriptors();
			for (IPropertyDescriptor pd : pds) {
				if (pd.getId().equals(property)) {
					CLabel label = createLabel(composite, showLabel, pd);
					ASPropertyWidget<?> widget = SPWidgetFactory.createWidget(composite, this, pd);
					if (widget != null) {
						widget.setLabel(label);
						widgets.put(pd.getId(), widget);
						return widget;
					}
					break;
				}
			}
		}
		return null;
	}

	@Override
	public void resetErrors() {
		for (ASPropertyWidget<?> w : widgets.values())
			w.resetErrors();
	}

	@Override
	public void showErrors(List<ValidationError> errors) {
		for (ValidationError ve : errors) {
			List<String> ids = ve.getProps();
			for (String id : ids) {
				ASPropertyWidget<?> w = widgets.get(id);
				if (w != null)
					w.showErrors(ve.getMessage(), ve.isWarning());
			}
		}
	}

	private CLabel createLabel(Composite composite, boolean showLabel, IPropertyDescriptor pd) {
		CLabel label = null;
		if (showLabel) {
			label = getWidgetFactory().createCLabel(composite, pd.getDisplayName(), SWT.NONE);
			label.setToolTipText(pd.getDescription());
		}
		return label;
	}

	public IPropertyDescriptor getPropertyDesriptor(Object property) {
		if (getElement() != null) {
			IPropertyDescriptor[] pds = getElement().getPropertyDescriptors();
			for (IPropertyDescriptor pd : pds) {
				if (pd.getId().equals(property)) {
					return pd;
				}
			}
		}
		return null;
	}

	/**
	 * Return always the selected element, in many case this is equals to
	 * getElement, but some section can override it to return something else.
	 * This method instead is thought to return always the selected element
	 */
	@Override
	public APropertyNode getSelectedElement() {
		return element;
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySection#setInput(org.eclipse.ui.IWorkbenchPart,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		setInputC(part, selection);
	}

	protected void setInputC(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			element = null;
			elements = new ArrayList<>();
			List<?> selected = ((IStructuredSelection) selection).toList();
			for (Object item : selected) {
				if (item instanceof EditPart) {
					APropertyNode model = getModelFromEditPart(item);
					if (model != null) {
						jasperReportsContext = model.getJasperConfiguration();
						if (element == null) {
							EditorContributor provider = part.getAdapter(EditorContributor.class);
							if (provider != null)
								setEditDomain(provider.getEditDomain());
							if (getElement() != model) {
								if (getElement() != null)
									getElement().getPropertyChangeSupport().removePropertyChangeListener(this);
								setElement(model);
								getElement().getPropertyChangeSupport().removePropertyChangeListener(this);
								getElement().getPropertyChangeSupport().addPropertyChangeListener(this);
							}
						}

						elements.add(model);
					}
				}
			}
		}
	}

	protected APropertyNode getModelFromEditPart(Object item) {
		Object model = ((EditPart) item).getModel();
		if (model instanceof APropertyNode)
			return (APropertyNode) model;
		return null;
	}

	public void setElement(APropertyNode element) {
		this.element = element;
		if (element != null && element.getJasperConfiguration() != null)
			jasperReportsContext = element.getJasperConfiguration();
	}

	public EditDomain getEditDomain() {
		return editDomain;
	}

	public void setEditDomain(EditDomain editDomain) {
		this.editDomain = editDomain;
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.view.ITabbedPropertySection#aboutToBeShown()
	 */
	@Override
	public void aboutToBeShown() {
		if (getElement() != null) {
			getElement().getPropertyChangeSupport().removePropertyChangeListener(this);
			getElement().getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.view.ITabbedPropertySection#aboutToBeHidden()
	 */
	@Override
	public void aboutToBeHidden() {
		if (getElement() != null)
			getElement().getPropertyChangeSupport().removePropertyChangeListener(this);
	}

	protected Composite parent;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		this.parent = parent;
	}

	public boolean isDisposed() {
		if (parent != null)
			return parent.isDisposed();
		return true;
	}

	@Override
	public void dispose() {
		if (getElement() != null)
			getElement().getPropertyChangeSupport().removePropertyChangeListener(this);
		super.dispose();
	}

	/**
	 * Get the element.
	 * 
	 * @return the element.
	 */
	public APropertyNode getElement() {
		return element;
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (!isDisposed()) {
			String n = evt.getPropertyName();
			setRefreshing(true);
			APropertyNode el = getElement();
			if (el != null) {
				el.getPropertyDescriptors();
				for (Object key : widgets.keySet()) {
					if (n.equals(key))
						widgets.get(key).setData(el, el.getPropertyValue(key));
				}
			}
			UIUtils.getDisplay().syncExec(() -> getTabbedPropertySheetPage().showErrors());

			setRefreshing(false);
		}
	}

	/**
	 * Flag used to know if the section is refreshing or not
	 */
	private boolean isRefreshing = false;

	/**
	 * Return if the section is refreshing or not, it is synchronized to avoid
	 * concurrent modifications, due for example to a change property call
	 */
	public boolean isRefreshing() {
		synchronized (this) {
			return isRefreshing;
		}
	}

	/**
	 * set if the section is refreshing or not, it is synchronized to avoid
	 * concurrent modifications, due for example to a change property call
	 */
	public void setRefreshing(boolean value) {
		synchronized (this) {
			isRefreshing = value;
		}
	}

	public boolean changeProperty(Object property, Object newValue) {
		return changeProperty(property, newValue, null);
	}

	/**
	 * Create the compound command where the subcommands can be added
	 * 
	 * @param name the name of the command
	 * @param node the node used as reference for the compound command
	 * @return a not null {@link JSSCompoundCommand}
	 */
	protected JSSCompoundCommand getCompoundCommand(String name, ANode node) {
		return new JSSCompoundCommand(name, node);
	}

	public boolean changeProperty(Object property, Object newValue, List<Command> commands) {
		if (!isRefreshing() && elements != null && !elements.isEmpty() && getEditDomain() != null) {
			CommandStack cs = getEditDomain().getCommandStack();
			JSSCompoundCommand cc = getCompoundCommand("Set " + property, null);
			for (APropertyNode n : elements) {
				cc.setReferenceNodeIfNull(n);
				if (isChanged(property, newValue, n)) {
					Command c = getChangePropertyCommand(property, newValue, n);
					if (c != null)
						cc.add(c);
				}
			}
			if (!cc.getCommands().isEmpty()) {
				if (commands != null)
					for (Command c : commands)
						cc.add(c);
				cs.execute(cc);
				getTabbedPropertySheetPage().showErrors();
				return true;
			}
		}
		return false;
	}

	/**
	 * Run in the stack of the current domain the passed command
	 */
	public boolean runCommand(JSSCompoundCommand cc) {
		if (!isRefreshing() && getEditDomain() != null) {
			CommandStack cs = getEditDomain().getCommandStack();
			if (!cc.getCommands().isEmpty()) {
				cs.execute(cc);
				getTabbedPropertySheetPage().showErrors();
				return true;
			}
		}
		return false;
	}

	public boolean changePropertyOn(Object property, Object newValue, List<APropertyNode> nodes,
			List<Command> commands) {
		if (!isRefreshing() && nodes != null && !nodes.isEmpty() && getEditDomain() != null) {
			CommandStack cs = getEditDomain().getCommandStack();
			JSSCompoundCommand cc = getCompoundCommand("Set " + property, null);
			for (APropertyNode n : nodes) {
				cc.setReferenceNodeIfNull(n);
				if (isChanged(property, newValue, n)) {
					Command c = getChangePropertyCommand(property, newValue, n);
					if (c != null)
						cc.add(c);
				}
			}
			if (!cc.getCommands().isEmpty()) {
				if (commands != null)
					for (Command c : commands)
						cc.add(c);
				cs.execute(cc);
				getTabbedPropertySheetPage().showErrors();
				return true;
			}
		}
		return false;
	}

	public void changePropertyOn(Object property, Object newValue, APropertyNode n) {
		changePropertyOn(property, newValue, n, null);
	}

	public void changePropertyOn(Object property, Object newValue, APropertyNode n, List<Command> commands) {
		if (!isRefreshing() && elements != null && !elements.isEmpty() && getEditDomain() != null) {
			CommandStack cs = getEditDomain().getCommandStack();
			if (isChanged(property, newValue, n)) {
				JSSCompoundCommand cc = getCompoundCommand("Set " + property, n);
				Command c = getChangePropertyCommand(property, newValue, n);
				if (c != null)
					cc.add(c);
				if (!cc.getCommands().isEmpty()) {
					if (commands != null)
						for (Command c1 : commands)
							cc.add(c1);
					cs.execute(cc);
					getTabbedPropertySheetPage().showErrors();
				}
			}
		}
	}

	protected boolean isChanged(Object property, Object newValue, APropertyNode n) {
		Object oldValue = n.getPropertyValue(property);
		if (oldValue == null && newValue == null)
			return false;
		if (oldValue != null && newValue != null && oldValue.equals(newValue) && getEditDomain() != null)
			return false;
		return true;

	}

	public Command getChangePropertyCommand(Object property, Object newValue, APropertyNode n) {
		if (isChanged(property, newValue, n)) {
			// Check if the node provide a SetValueCommand provide and use it in
			// case, otherwise
			// create a standard SetValueCommand
			ISetValueCommandProvider provider = (ISetValueCommandProvider) n.getAdapter(ISetValueCommandProvider.class);
			if (provider != null) {
				return provider.getSetValueCommand(n, n.getDisplayText(), property, newValue);
			} else {
				SetValueCommand setCommand = new SetValueCommand(n.getDisplayText());
				setCommand.setTarget(n);
				setCommand.setPropertyId(property);
				setCommand.setPropertyValue(newValue);
				return setCommand;
			}
		}
		return null;
	}

	public List<APropertyNode> getElements() {
		return elements;
	}

	public void setElements(List<APropertyNode> elements) {
		this.elements = elements;
	}

	public static Composite createNewRow(Composite parent) {
		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setBackground(parent.getBackground());
		RowLayout rl = new RowLayout();
		rl.fill = true;
		rl.wrap = true;
		cmp.setLayout(rl);
		return cmp;
	}

	public static CLabel createLabel(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, String txt,
			int width) {
		CLabel lbl = widgetFactory.createCLabel(parent, txt, SWT.RIGHT);
		if (parent.getLayout() instanceof RowLayout) {
			RowData rd = new RowData();
			rd.width = width;
			lbl.setLayoutData(rd);
		} else if (parent.getLayout() instanceof GridLayout) {
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
			if (width > 0)
				gd.minimumWidth = width;
			lbl.setLayoutData(gd);
		}
		return lbl;
	}

	/**
	 * Create the map of the provided properties, empty
	 */
	protected void initializeProvidedProperties() {
		providedProperties = new HashMap<>();
	}

	/**
	 * Return a list of all the properties in the map
	 */
	public List<Object> getHandledProperties() {
		if (providedProperties == null)
			initializeProvidedProperties();
		return new ArrayList<>(providedProperties.keySet());
	}

	public WidgetDescriptor getPropertyInfo(Object propertyId) {
		if (providedProperties == null)
			initializeProvidedProperties();
		return providedProperties.get(propertyId);
	}

	protected void addProvidedProperties(Object id, String propertyName) {
		providedProperties.put(id, new WidgetDescriptor(propertyName));
	}

	@Override
	public void expandForProperty(Object propertyId) {
	}

	public IHighlightPropertyWidget getWidgetForProperty(Object propertyId) {
		return widgets.get(propertyId);
	}

	public JasperReportsConfiguration getJasperReportsContext() {
		return jasperReportsContext;
	}
}
