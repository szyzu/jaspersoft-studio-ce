/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;

import com.jaspersoft.studio.callout.CalloutEditPart;
import com.jaspersoft.studio.callout.pin.PinEditPart;
import com.jaspersoft.studio.editor.gef.parts.FigureEditPart;
import com.jaspersoft.studio.editor.outline.part.TreeEditPart;
import com.jaspersoft.studio.model.style.MStylesTemplate;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.model.util.NodeIconDescriptor;
import com.jaspersoft.studio.model.util.ReportFactory;
import com.jaspersoft.studio.properties.IEditablePropertySource;
import com.jaspersoft.studio.properties.view.validation.IValidable;
import com.jaspersoft.studio.properties.view.validation.ValidationError;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSimpleTemplate;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.design.JRDesignElementGroup;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.events.CollectionElementAddedEvent;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;

/*
 * The Class ANode.
 * 
 * @author Chicu Veaceslav
 */
public abstract class ANode implements INode, Serializable, IAdaptable, Cloneable, IValidable, IEditablePropertySource {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * The parent.
	 */
	private ANode parent;

	/**
	 * The children.
	 */
	private List<INode> children;

	/**
	 * The value.
	 */
	private Object value;

	/**
	 * The property change support.
	 */
	private PropertyChangeSupport propertyChangeSupport;

	/**
	 * flag to know if the node is visible or not. The visibility can be set and
	 * in case of new nodes it is inherited by the parent
	 */
	private boolean visible = true;

	/**
	 * List of validation error for the current element. The error messages can
	 * be cached using the flag redoValidation
	 */
	private List<ValidationError> validationErrors = null;

	/**
	 * Flag used to know when something in the element change. If something
	 * change the validation must be redone and the list of error messages
	 * regenerated
	 */
	private boolean redoValidation = true;

	/**
	 * Used to know if the node is makred as editable or not
	 */
	private boolean editable = true;

	/**
	 * Instantiates a new a node.
	 */
	public ANode() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getToolTip()
	 */
	public String getToolTip() {
		return getDisplayText();
	}

	@Override
	public StyledString getStyledDisplayText() {
		return new StyledString(getDisplayText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getFont()
	 */
	public Font getFont() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getForeground()
	 */
	public Color getForeground() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getBackground()
	 */
	public Color getBackground() {
		return null;
	}

	/**
	 * Instantiates a new a node.
	 * 
	 * @param parent the parent
	 * @param newIndex the new index
	 */
	public ANode(ANode parent, int newIndex) {
		if (parent != null)
			setParent(parent, newIndex);
	}

	public ANode(ANode parent, Object value, int newIndex) {
		if (parent != null)
			setParent(parent, newIndex);
		setValue(value);
	}

	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null) {
			iconDescriptor = new NodeIconDescriptor("report"); //$NON-NLS-1$
		}
		return iconDescriptor;
	}

	/**
	 * Gets the root.
	 * 
	 * @return the root
	 */
	public INode getRoot() {
		if (root == null && getParent() != null)
			root = getParent().getRoot();
		//
		// INode node = this;
		// while (!(node instanceof MReport) && !(node instanceof MRoot)) {
		// if (node == null || node.getParent() == null)
		// return this;
		// node = node.getParent();
		// }
		return root;
	}

	private transient INode root;

	public void register() {
		root = null;
		INode troot = getRoot();
		if (troot != null && troot instanceof ANode)
			((ANode) troot).register(this);
	}

	public void unregister() {
		if (root != null && root instanceof ANode)
			((ANode) root).unregister(this);
		root = null;
	}

	public void register(Object key, ANode value) {
	}

	public void unregister(Object key) {
	}

	public void register(ANode n) {
		register(n.getValue(), n);
	}

	public void unregister(ANode n) {
		unregister(n.getValue());
	}

	/**
	 * Sets the parent.
	 * 
	 * @param newparent the parent
	 * @param newIndex the new index
	 */
	public void setParent(ANode newparent, int newIndex) {
		if (parent != null) {
			unregister();
			getPropertyChangeSupport().removePropertyChangeListener(parent);
			if (parent.getChildren() != null)
				parent.getChildren().remove(this);
			this.parent = null;
			unsetDependents();
		}
		if (newparent != null) {
			// Inherit visibility
			this.visible = newparent.visible;
			register();
			this.parent = newparent;
			if (newIndex >= 0 && newIndex < newparent.getChildren().size())
				newparent.getChildren().add(newIndex, this);
			else
				newparent.getChildren().add(this);
			getPropertyChangeSupport().addPropertyChangeListener(newparent);
		}
	}

	private Set<ANode> dependents;

	public void setChildListener(ANode child) {
		unsetChildListener(child);
		if (child != null)
			child.getPropertyChangeSupport().addPropertyChangeListener(this);
		if (dependents == null)
			dependents = new HashSet<>();
		dependents.add(child);
	}

	public void unsetChildListener(ANode child) {
		if (child != null)
			child.getPropertyChangeSupport().removePropertyChangeListener(this);
		if (dependents != null)
			dependents.remove(child);
	}

	public void unsetDependents() {
		if (dependents != null) {
			for (ANode n : dependents)
				n.getPropertyChangeSupport().removePropertyChangeListener(this);
			dependents.clear();
		}
	}

	/**
	 * Adds the child.
	 * 
	 * @param child the child
	 */
	public void addChild(ANode child) {
		child.setParent(this, -1);
	}

	public void addChild(ANode child, int index) {
		child.setParent(this, index);
	}

	public boolean hasParent(ANode node) {
		if (parent == null)
			return false;
		return parent.equals(node) || parent.hasParent(node);
	}

	public ANode[] flatten() {
		ArrayList<ANode> result = new ArrayList<>();
		doFlatten(this, result);
		return result.toArray(new ANode[result.size()]);
	}

	private void doFlatten(ANode node, ArrayList<ANode> all) {
		// add the gadget and its children to the list
		all.add(node);
		List<INode> tchildren = node.getChildren();
		for (INode n : tchildren) {
			doFlatten((ANode) n, all);
		}
	}

	/**
	 * Removes the child.
	 * 
	 * @param child the child
	 */
	public void removeChild(ANode child) {
		// Set the jasperconfiguration before to remove the parent, because if a
		// selection is
		// fired on the child it will search for the jasperconfiguration on the
		// parent,
		// and not
		// finding it the result will be null. But this broke a lot of things on
		// the
		// selection
		// event
		child.setJasperConfiguration(getJasperConfiguration());
		child.setParent(null, -1);
		if (children != null)
			children.remove(child);
	}

	/**
	 * Removes the children.
	 */
	public void removeChildren() {
		if (children != null)
			removeChildren(children);
	}

	/**
	 * Removes the children.
	 */
	public void removeChildren(List<? extends INode> children) {
		Object[] array = children.toArray();
		for (int i = 0; i < array.length; i++)
			removeChild((ANode) array[i]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getPropertyChangeSupport()
	 */
	public PropertyChangeSupport getPropertyChangeSupport() {
		if (propertyChangeSupport == null)
			propertyChangeSupport = new PropertyChangeSupport(this);
		return propertyChangeSupport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// Redo the validation when something changes, for this element and the
		// children
		revalidateChildren();
		if (evt.getPropertyName().equals(JRDesignElementGroup.PROPERTY_CHILDREN) && evt.getSource() == getValue()) {
			if (evt.getOldValue() == null && evt.getNewValue() != null) {
				int newIndex = -1;
				if (evt instanceof CollectionElementAddedEvent) {
					newIndex = ((CollectionElementAddedEvent) evt).getAddedIndex();
				}
				// add the node to this parent
				ANode n = ReportFactory.createNode(this, evt.getNewValue(), newIndex);
				if (evt.getNewValue() instanceof JRElementGroup) {
					JRElementGroup jrFrame = (JRElementGroup) evt.getNewValue();
					ReportFactory.createElementsForBand(n, jrFrame.getChildren());
				}

			} else if (evt.getOldValue() != null && evt.getNewValue() == null) {
				// delete
				for (INode n : getChildren()) {
					if (n.getValue() == evt.getOldValue()) {
						removeChild((ANode) n);
						break;
					}
				}
			} else {
				// changed
				for (INode n : getChildren()) {
					if (n.getValue() == evt.getOldValue())
						n.setValue(evt.getNewValue());
				}
			}
		}
		// PropertyChangeEvent newEvent = evt;
		// if (evt.getSource() instanceof ANode) {
		// ANode enode = (ANode) evt.getSource();
		// if (dependents.contains(enode)) {
		// newEvent = new PropertyChangeEvent(this, evt.getPropertyName(),
		// evt.getOldValue(),
		// evt.getNewValue());
		// }
		// } else {
		// newEvent = new PropertyChangeEvent(evt.getSource(),
		// evt.getPropertyName(),
		// evt.getOldValue(),
		// evt.getNewValue());
		// }
		firePropertyChange(evt);
	}

	protected void firePropertyChange(PropertyChangeEvent evt) {
		getPropertyChangeSupport().firePropertyChange(evt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getChildren()
	 */
	public List<INode> getChildren() {
		if (children == null)
			children = new ArrayList<>();
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getParent()
	 */
	public ANode getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getValue()
	 */
	public Object getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		if (this.value == value)
			return;
		unregister();
		if (this.value instanceof JRChangeEventsSupport) {
			((JRChangeEventsSupport) this.value).getEventSupport().removePropertyChangeListener(this);
		}
		if (value != null) {
			this.value = value;
			if (this.value instanceof JRChangeEventsSupport)
				((JRChangeEventsSupport) this.value).getEventSupport().addPropertyChangeListener(this);
			register();
			return;
		}
		this.value = value;
	}

	public EditPart getFigureEditPart() {
		for (Object o : propertyChangeSupport.getPropertyChangeListeners()) {
			if (o instanceof FigureEditPart || o instanceof CalloutEditPart || o instanceof PinEditPart)
				return (EditPart) o;
		}
		return null;
	}

	public EditPart getTreeEditPart() {
		for (Object o : propertyChangeSupport.getPropertyChangeListeners()) {
			if (o instanceof TreeEditPart)
				return (EditPart) o;
		}
		return null;
	}

	public JRSimpleTemplate getStylesTemplate() {
		for (INode node : getRoot().getChildren())
			if (node instanceof MStylesTemplate)
				return (JRSimpleTemplate) node.getValue();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getJasperDesign()
	 */
	public JasperDesign getJasperDesign() {
		INode r = getRoot();
		if (r != null && r.getValue() instanceof JasperDesign)
			return (JasperDesign) r.getValue();
		if (getJasperConfiguration() != null)
			return getJasperConfiguration().getJasperDesign();
		return null;
	}

	private transient JasperReportsConfiguration jConfig;

	public void setJasperConfiguration(JasperReportsConfiguration jConfig) {
		this.jConfig = jConfig;
	}

	public JasperReportsConfiguration getJasperConfiguration() {
		if (jConfig != null)
			return jConfig;
		if (parent != null)
			return parent.getJasperConfiguration();
		return null;
	}

	public int findParent(ANode n) {
		int depth = 0;
		ANode node = this;
		while (node != null) {
			if (node == n)
				return depth;
			node = node.getParent();
			depth++;
		}
		return -1;
	}

	public int findElement(Object obj) {
		if (obj == null)
			return -1;
		List<INode> children2 = getChildren();
		for (int i = 0; i < children2.size(); i++) {
			if (children2.get(i).getValue() == obj)
				return i;
		}
		return -1;
	}

	private boolean cut = false;

	public boolean isCut() {
		return cut;
	}

	public void setCut(boolean cut) {
		this.cut = cut;
	}

	final void intReset() {
		children = null;
		parent = null;
	}

	public ANode clone() {
		try {
			ANode clone = (ANode) super.clone();
			if (children != null) {
				clone.intReset();
				List<INode> oldChildren = new ArrayList<>(children);
				// fixed java.util.ConcurrentModificationException
				List<ANode> ch = new ArrayList<>();
				for (INode n : children)
					ch.add(((ANode) n).clone());
				for (ANode n : ch)
					clone.addChild(n);
				for (INode n : oldChildren)
					addChild((ANode) n);
			}
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new JRRuntimeException(e);
		}
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == ANode.class || adapter == INode.class)
			return this;
		return null;
	}

	public boolean isFirst() {
		if (parent != null && parent.getChildren() != null && !parent.getChildren().isEmpty())
			return parent.getChildren().indexOf(this) == 0;
		return true;
	}

	/**
	 * This method should be overridden by sub-classes whenever a node during
	 * its life-cycle can be in a state where new children can not be added.
	 * <p>
	 * 
	 * Default value is <code>true</code> in order to ensure back-compatibility
	 * 
	 * @param child the child that should be added to the element (useful for
	 * example for typecheck)
	 * @return <code>true</code> if new children can be added,
	 * <code>false</code> otherwise
	 * 
	 */
	public boolean canAcceptChildren(ANode child) {
		return true;
	}

	/**
	 * Return the visibility of the node
	 * 
	 * @return true if the node is visible, false otherwise
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Set the visibility of the node. This dosen't affect the visibility of the
	 * children
	 * 
	 * @param visible true if the node is visible, false otherwise
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Boolean flag to declare if the children are visible or not. By default
	 * are always visible. But some elements are editable only inside a
	 * subeditor, in this cases the children can be shown only when the user is
	 * inside the subeditor
	 * 
	 * @return true if the children are visible, false otherwise
	 */
	public boolean showChildren() {
		return true;
	}

	/**
	 * Some elements can be edited inside a subeditor. This method can be
	 * overridden to create the subeditor, like putting styles or dataset item.
	 * Every model that can be edited inside a subeditor can define how it is
	 * initialized
	 */
	public void createSubeditor() {

	}

	/**
	 * Validate the current element and return a list of validation error. By
	 * default this method doesn't do any validation but can be overridden
	 * 
	 * @return null if the validation doesn't return errors, the list of errors
	 * otherwise
	 */
	protected List<ValidationError> doValidation() {
		return null;
	}

	/**
	 * Request to redo the validation on the current element and on it's
	 * children. This because in some case the validation of an element depends
	 * from the size of the parent. Can be overridden to provide a different
	 * behavior
	 */
	public void revalidateChildren() {
		redoValidation = true;
		for (INode node : getChildren()) {
			((ANode) node).revalidateChildren();
		}
	}

	/**
	 * Validate the current element and return a list of validation error. The
	 * result is cached until something changes on the node. To avoid to do the
	 * validation when it isn't necessary
	 * 
	 * @return null if the validation doesn't return errors, the list of errors
	 * otherwise
	 */
	public List<ValidationError> validate() {
		if (redoValidation) {
			validationErrors = doValidation();
			redoValidation = false;
		}
		return validationErrors;
	}

	/**
	 * Return the size available for the placement of elements, it depends from
	 * the page size. There is a minimum size that is 1000x1000, if the value is
	 * lower that this one then 1000 is used
	 * 
	 * @return the space available outside the page. By default it is fourtime
	 * the page size, and a minimum of 1000x1000
	 */
	public Point getAvailableSize() {
		JasperDesign jd = getJasperDesign();
		int w = jd.getPageWidth() + 20;
		int h = jd.getPageHeight() + 20;
		w = w * 4;
		if (w < 1000)
			w = 1000;
		h = h * 4;
		if (h < 1000)
			h = 1000;
		return new Point(w, h);
	}

	/**
	 * Add a style to the map of styles for this element. If the style was not
	 * already in the map it is added with a list containing only this element,
	 * otherwise this element is appended to the end of the list associated with
	 * the style
	 * 
	 * @param style the style to add, if null this doensn't do nothing
	 * @param map map where the style are added, must be not null
	 */
	protected void addElementStyle(JRStyle style, Map<String, List<ANode>> map) {
		if (style == null)
			return;
		List<ANode> list = map.get(style.getName());
		if (list == null) {
			list = new ArrayList<>();
			list.add(this);
			map.put(style.getName(), list);
		} else {
			if (!list.contains(this)) {
				list.add(this);
			}
		}
	}

	/**
	 * Merge to styles map into one, by also concatenated the list of elements
	 * when a style is present in both
	 * 
	 * @param destination first map and destination of the merge
	 * @param origin the map that will be merged with the first one
	 */
	protected void mergeElementStyle(Map<String, List<ANode>> destination, Map<String, List<ANode>> origin) {
		for (Entry<String, List<ANode>> entry : origin.entrySet()) {
			String style = entry.getKey();
			List<ANode> elements = entry.getValue();
			List<ANode> targetElements = destination.get(style);
			if (targetElements == null) {
				destination.put(style, elements);
			} else {
				targetElements.addAll(elements);
			}
		}
	}

	/**
	 * Return the styles used by this element and eventually by its children.
	 * 
	 * @return a not null map with the names of all the styles used by this
	 * element or one of its children. The value corresponding to each style is
	 * the reference to the element that is using the style
	 */
	public Map<String, List<ANode>> getUsedStyles() {
		return new HashMap<>();
	}

	/**
	 * Set a style on this element, the default implementation is empty but in
	 * the subclasses that support styles it should set the style on the jr
	 * element
	 * 
	 * @param style the style to set, could be null to remove it
	 */
	public void setStyle(JRStyle style) {

	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
