/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.book.gallery.commands;

import net.sf.jasperreports.engine.design.JRDesignPart;
import net.sf.jasperreports.engine.design.JRDesignSection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.jaspersoft.studio.book.gallery.controls.GalleryComposite;
import com.jaspersoft.studio.book.gallery.interfaces.IGalleryElement;
import com.jaspersoft.studio.book.model.MReportPart;

/**
 * Move an element of the gallery after another element
 * 
 * @author Orlandin Marco
 *
 */
public class MoveElementAfter extends AbstractOperation{

	/**
	 * Element that will be to the left of the moved element when the command will be executed
	 */
	private IGalleryElement previousItem;
	
	/**
	 * Element to move
	 */
	private IGalleryElement elementMoved;
	
	/**
	 * Gallery source
	 */
	private GalleryComposite source;
	
	/**
	 * Gallery target
	 */
	private GalleryComposite target;
	
	/**
	 * Element that was to the left of the moved element before the command was be executed
	 */
	private IGalleryElement oldPreviousItem = null;
	
	private JRDesignPart part;
	
	/**
	 * Create the command 
	 * 
	 * @param previousItem Element that will be to the left of the moved element when the command will be executed or null if
	 * the moved element should be moved on the first position
	 * @param elementMoved Element to move
	 * @param container the gallery that contains the elements
	 */
	public MoveElementAfter(IGalleryElement previousItem, IGalleryElement elementMoved, GalleryComposite source, GalleryComposite target){
		super("Move Item");
		this.source = source;
		this.target = target;
		this.elementMoved = elementMoved;
		MReportPart mpart = (MReportPart) elementMoved.getData();
		this.part = mpart.getValue();
		this.previousItem = previousItem;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		int oldIndex = source.getIndexOf(elementMoved);
		//Store the old item
		if (oldIndex == 0) {
			oldPreviousItem = null;
		} else {
			oldPreviousItem = source.getElementAt(oldIndex-1);
		}
		
		//Delete from the old position
		source.removeItem(oldIndex);
		JRDesignSection jrsectionSource = source.getPartsContainer().getSection();
		if(jrsectionSource!=null){
			jrsectionSource.removePart(oldIndex);
		}
		
		//create on the new position
		int newIndex = 0;
		if (previousItem != null){
			newIndex = target.getIndexOf(previousItem)+1;
		}
		JRDesignSection jrsectionTarget = target.getPartsContainer().getSection();
		if(jrsectionTarget!=null){
			jrsectionTarget.addPart(newIndex,part);
		}
		target.createItem(elementMoved, newIndex);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		int oldIndex = target.getIndexOf(elementMoved);
		
		//Delete from the new position
		target.removeItem(oldIndex);
		
		//create on the old position
		int newIndex = 0;
		if (oldPreviousItem != null){
			newIndex = source.getIndexOf(oldPreviousItem)+1;
		}
		source.createItem(elementMoved, newIndex);
		return Status.OK_STATUS;
	}
}
