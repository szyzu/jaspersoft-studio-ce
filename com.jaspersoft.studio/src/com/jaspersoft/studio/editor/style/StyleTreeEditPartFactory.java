/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.style;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;

import com.jaspersoft.studio.editor.style.command.CreateStyleCommand;
import com.jaspersoft.studio.editor.style.command.CreateStyleTemplateReferenceCommand;
import com.jaspersoft.studio.editor.style.command.DeleteStyleCommand;
import com.jaspersoft.studio.editor.style.command.DeleteStyleTemplateCommand;
import com.jaspersoft.studio.editor.style.tree.OpenableStyleTreeEditPart;
import com.jaspersoft.studio.editor.style.tree.StyleContainerTreeEditPart;
import com.jaspersoft.studio.editor.style.tree.StyleTreeEditPart;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.IContainerEditPart;
import com.jaspersoft.studio.model.style.MConditionalStyle;
import com.jaspersoft.studio.model.style.MStyle;
import com.jaspersoft.studio.model.style.MStyleTemplateReference;
import com.jaspersoft.studio.model.style.MStylesTemplate;
import com.jaspersoft.studio.model.style.command.CreateConditionalStyleCommand;
import com.jaspersoft.studio.model.style.command.DeleteConditionalStyleCommand;

import net.sf.jasperreports.engine.design.JRDesignStyle;
/*
 * A factory for creating OutlineTreeEditPart objects.
 */
public class StyleTreeEditPartFactory implements EditPartFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart editPart = null;
		if (model instanceof IContainerEditPart) {
			editPart = new StyleContainerTreeEditPart();
		} else if (model instanceof MStyleTemplateReference) {
			editPart = new OpenableStyleTreeEditPart();
		} else editPart = new StyleTreeEditPart();
		if (editPart != null)
			editPart.setModel(model);
		return editPart;
	}

	/**
	 * Gets the delete command.
	 * 
	 * @param parent
	 *          the parent
	 * @param child
	 *          the child
	 * @return the delete command
	 */
	public static Command getDeleteCommand(ANode parent, ANode child) {
		if (parent instanceof MStyle && child instanceof MConditionalStyle) {
			return new DeleteConditionalStyleCommand((MStyle) parent, (MConditionalStyle) child);
		}
		if (parent instanceof MStylesTemplate) {
			if (child instanceof MStyleTemplateReference) {
				return new DeleteStyleTemplateCommand((MStylesTemplate) parent, (MStyleTemplateReference) child);
			}
			if (child instanceof MConditionalStyle) {
				return new DeleteConditionalStyleCommand((MStyle) child.getParent(), (MConditionalStyle) child);
			}
			if (child instanceof MStyle) {
				return new DeleteStyleCommand((MStylesTemplate) parent, (MStyle) child);
			}
		}
		return null;
	}

	/**
	 * Gets the reorder command.
	 * 
	 * @param child
	 *          the child
	 * @param parent
	 *          the parent
	 * @param newIndex
	 *          the new index
	 * @return the reorder command
	 */
	public static Command getReorderCommand(ANode child, ANode parent, int newIndex) {
		// if (child instanceof MStyle) {
		// if (parent instanceof MStyles) {
		// return new ReorderStyleCommand((MStyle) child, (MStyles) parent, newIndex);
		// }
		// }
		return null;
	}

	/**
	 * Gets the creates the command.
	 * 
	 * @param parent
	 *          the parent
	 * @param child
	 *          the child
	 * @param location
	 *          the location
	 * @param newIndex
	 *          the new index
	 * @return the creates the command
	 */
	public static Command getCreateCommand(ANode parent, ANode child, Rectangle location, int newIndex) {
		if (parent instanceof MStylesTemplate) {
			if (child instanceof MStyle)
				return new CreateStyleCommand((MStylesTemplate) parent, (MStyle) child, newIndex);
			if (child instanceof MStyleTemplateReference)
				return new CreateStyleTemplateReferenceCommand((MStylesTemplate) parent, (MStyleTemplateReference) child,
						newIndex);
		} else if (child instanceof MConditionalStyle) {
			if (parent instanceof MStyle && parent.getValue() instanceof JRDesignStyle)
				return new CreateConditionalStyleCommand((MStyle) parent, (MConditionalStyle) child, newIndex);
		} 
		else if (parent.getParent() instanceof MStylesTemplate) {
			if (child instanceof MStyle && !(child instanceof MConditionalStyle))
				return new CreateStyleCommand((MStylesTemplate) parent.getParent(), (MStyle) child, newIndex);
			if (child instanceof MStyleTemplateReference)
				return new CreateStyleTemplateReferenceCommand((MStylesTemplate) parent.getParent(),
						(MStyleTemplateReference) child, newIndex);
		}
		return null;
	}

	/**
	 * Gets the orphan command.
	 * 
	 * @param parent
	 *          the parent
	 * @param child
	 *          the child
	 * @return the orphan command
	 */
	public static Command getOrphanCommand(ANode parent, ANode child) {
		return UnexecutableCommand.INSTANCE;
	}
}
