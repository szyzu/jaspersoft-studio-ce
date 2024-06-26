/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.band;

import java.util.List;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignFrame;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.IGraphicElement;
import com.jaspersoft.studio.model.IGraphicElementContainer;
import com.jaspersoft.studio.model.IGroupElement;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MElementGroup;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.model.MPage;
import com.jaspersoft.studio.property.SetValueCommand;
import com.jaspersoft.studio.utils.ModelUtils;

public class StretchToContentAction extends SelectionAction {

	/** The Constant ID. */
	public static final String ID = "stretch2content"; //$NON-NLS-1$

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *            The part for this action
	 */
	public StretchToContentAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
		setText(Messages.StretchToContentAction_name);
		setToolTipText(Messages.StretchToContentAction_tooltip);
		setId(ID);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/transparent_icon.png"));
		// setImageDescriptor(JaspersoftStudioPlugin.getImageDescriptor(layout.getIcon()));
		// setDisabledImageDescriptor(JaspersoftStudioPlugin.getImageDescriptor(layout.getIcon()));
		setEnabled(false);
	}

	/**
	 * Returns <code>true</code> if the selected objects can be created. Returns
	 * <code>false</code> if there are no objects selected or the selected objects
	 * are not {@link EditPart}s.
	 * 
	 * @return if the command should be enabled
	 */
	protected boolean calculateEnabled() {
		Command cmd = createReorderCommand(getSelectedObjects());
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}

	/**
	 * Create a command to create the selected objects.
	 * 
	 * @param objects
	 *            The objects to be deleted.
	 * @return The command to remove the selected objects.
	 */
	public Command createReorderCommand(List<?> objects) {
		if (objects == null || objects.isEmpty())
			return null;
		Object obj = objects.get(0);
		if (obj instanceof EditPart) {
			Object model = ((EditPart) obj).getModel();
			if (model instanceof ANode) {
				ANode n = (ANode) model;
				if (n instanceof MPage) {
					for (INode c : n.getChildren()) {
						if (c instanceof MGraphicElement) {
							n = (ANode) c;
							break;
						}
					}
				}
				if (!(n instanceof IGraphicElement))
					return null;

				JRElementGroup container = getContainer(n);
				if (container == null)
					return null;

				APropertyNode mcontainer = getContainerNode(n);
				JSSCompoundCommand cc = new JSSCompoundCommand(getText(), mcontainer);
				Dimension size = new Dimension(0, 0);
				if (container instanceof JRDesignFrame) {
					size = ModelUtils.getContainerSize(container.getChildren(), size);
					if (size.height > 0 && size.width > 0) {
						SetValueCommand cmd = new SetValueCommand();
						cmd.setTarget(mcontainer);
						cmd.setPropertyId(JRDesignFrame.PROPERTY_WIDTH);
						cmd.setPropertyValue(size.width);
						cc.add(cmd);

						cmd = new SetValueCommand();
						cmd.setTarget(mcontainer);
						cmd.setPropertyId(JRDesignFrame.PROPERTY_HEIGHT);
						cmd.setPropertyValue(size.height);
						cc.add(cmd);
					}
				} else if (container instanceof JRDesignBand) {
					int bandHeight = ModelUtils.getBandHeight((JRBand) container);
					if (bandHeight > 0) {
						SetValueCommand cmd = new SetValueCommand();
						cmd.setTarget(mcontainer);
						cmd.setPropertyId(JRDesignBand.PROPERTY_HEIGHT);
						cmd.setPropertyValue(bandHeight);
						cc.add(cmd);
					}
				} else if (n instanceof IGraphicElementContainer) {
					Command c = JaspersoftStudioPlugin.getExtensionManager().getStretchToContent(n);
					if (c != null)
						cc.add(c);
				} else if (n.getParent() instanceof IGraphicElementContainer) {
					Command c = JaspersoftStudioPlugin.getExtensionManager().getStretchToContent(n.getParent());
					if (c != null)
						cc.add(c);
				}
				return cc;
			}
		}
		return null;
	}

	private JRElementGroup getContainer(ANode n) {
		if (n != null) {
			Object val = n.getValue();
			if (n instanceof IGroupElement)
				return ((IGroupElement) n).getJRElementGroup();
			if (val instanceof JRElementGroup)
				return (JRElementGroup) val;
			if (val instanceof JRDesignElement)
				return getContainer(n.getParent());
		}
		return null;
	}

	private APropertyNode getContainerNode(ANode n) {
		Object val = n.getValue();
		if (n instanceof IGroupElement)
			return (APropertyNode) n;
		if (val instanceof JRElementGroup) {
			if (n instanceof MElementGroup) {
				return getContainerNode(n.getParent());
			} else {
				return (APropertyNode) n;
			}
		}
		if (val instanceof JRDesignElement)
			return getContainerNode(n.getParent());
		return null;
	}

	/**
	 * Performs the create action on the selected objects.
	 */
	public void run() {
		execute(createReorderCommand(getSelectedObjects()));
	}

}
