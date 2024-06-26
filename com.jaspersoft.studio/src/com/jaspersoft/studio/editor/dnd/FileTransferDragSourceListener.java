/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.dnd;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
/*/*
 * The listener interface for receiving fileTransferDragSource events. The class that is interested in processing a
 * fileTransferDragSource event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addFileTransferDragSourceListener<code> method. When
 * the fileTransferDragSource event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see FileTransferDragSourceEvent
 */
public class FileTransferDragSourceListener extends org.eclipse.gef.dnd.AbstractTransferDragSourceListener {

	/**
	 * Instantiates a new file transfer drag source listener.
	 * 
	 * @param viewer
	 *          the viewer
	 */
	public FileTransferDragSourceListener(EditPartViewer viewer) {
		super(viewer, TextTransfer.getInstance());
	}

	/**
	 * Instantiates a new file transfer drag source listener.
	 * 
	 * @param viewer
	 *          the viewer
	 * @param xfer
	 *          the xfer
	 */
	public FileTransferDragSourceListener(EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		event.data = "Some text"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) {
		// if (getViewer().getSelectedEditParts().get(0) instanceof
		// LogicLabelEditPart)
		// return;
		event.doit = false;
	}

}
