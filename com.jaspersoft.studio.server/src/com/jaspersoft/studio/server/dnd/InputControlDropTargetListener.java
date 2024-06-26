/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.TreeItem;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.dnd.NodeTransfer;
import com.jaspersoft.studio.dnd.NodeTreeDropAdapter;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.server.ServerManager;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.model.IInputControlsContainer;
import com.jaspersoft.studio.server.model.MInputControl;
import com.jaspersoft.studio.server.model.MReportUnit;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

/**
 * A target drop listener that creates a generic file resource element when
 * something is dropped on the JRS repository tree.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public class InputControlDropTargetListener extends NodeTreeDropAdapter implements TransferDropTargetListener {
	// private ControlDecoration decorator;

	public InputControlDropTargetListener(TreeViewer treeViewer) {
		super(treeViewer);
		// decorator = new ControlDecoration(treeViewer.getTree(), SWT.TOP |
		// SWT.LEFT);
	}

	@Override
	public boolean validateDrop(Object target, int op, TransferData type) {
		// String tgt = getCurrentTarget().toString();
		// if (target instanceof ANode)
		// tgt = ((ANode) target).getDisplayText();
		// decorator.showHoverText("drop? " + tgt + "\n" + op + "\n" + type +
		// "\n" + type.type + "\n" + type.data + "\n"
		// + getCurrentLocation());
		return super.validateDrop(target, op, type);
	}

	@Override
	public boolean performDrop(Object data) {

		if (data == null)
			return false;
		final List<MInputControl> mc = new ArrayList<MInputControl>();
		if (data.getClass().isArray()) {
			Object[] ar = (Object[]) data;
			for (Object obj : ar)
				if (obj instanceof MInputControl)
					mc.add((MInputControl) obj);
		} else if (data instanceof MInputControl)
			mc.add((MInputControl) data);

		Job job = new Job(com.jaspersoft.studio.messages.Messages.common_reorder_elements) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IStatus status = Status.OK_STATUS;
				monitor.beginTask(com.jaspersoft.studio.messages.Messages.common_reorder_elements,
						IProgressMonitor.UNKNOWN);
				try {
					Object target = getCurrentTarget();
					if (target instanceof ANode && ((ANode) target).getParent() instanceof MReportUnit)
						status = doRun((ANode) target, mc, monitor);
					else if (target instanceof ANode
							&& InputControlDragSourceListener.isDragable(((ANode) target).getParent()))
						status = doRun((ANode) target, mc, monitor);
				} finally {
					monitor.done();
				}
				return status;
			}
		};
		job.setPriority(Job.LONG);
		job.setUser(true);
		job.schedule();
		return true;
	}

	protected IStatus doRun(ANode target, List<MInputControl> toMove, IProgressMonitor monitor) {
		AMResource container = null;
		if (target instanceof IInputControlsContainer)
			container = (AMResource) target;
		else if (target.getParent() instanceof IInputControlsContainer)
			container = (AMResource) ((ANode) target).getParent();

		int indx = container.getChildren().indexOf(target);
		if (getCurrentLocation() == LOCATION_AFTER)
			indx++;

		List<MInputControl> tm = new ArrayList<MInputControl>();
		for (INode n : container.getChildren()) {
			if (n instanceof MInputControl) {
				String uri = ((MInputControl) n).getValue().getUriString();
				for (MInputControl mc : toMove) {
					if (mc.getValue().getUriString().equals(uri)) {
						tm.add((MInputControl) n);
						break;
					}
				}
			}
		}
		if (!tm.isEmpty()) {
			// move elements here
			container.removeChildren(tm);

			for (int i = 0; i < tm.size(); i++)
				container.addChild(tm.get(i), i + indx);
		} else {
			for (int i = 0; i < toMove.size(); i++)
				container.addChild(toMove.get(i), i + indx);
		}
		String uriString = container.getValue().getUriString();
		try {
			container.getWsClient().reorderInputControls(uriString, doBuildICResourceDescriptorList(container),
					monitor);
		} catch (Exception e) {
			UIUtils.showError(e);
		}
		// }
		ServerManager.selectIfExists(monitor, container);
		return Status.OK_STATUS;
	}

	protected List<ResourceDescriptor> doBuildICResourceDescriptorList(AMResource mrunit) {
		List<ResourceDescriptor> ics = new ArrayList<ResourceDescriptor>();
		for (INode n : mrunit.getChildren())
			if (n instanceof MInputControl)
				ics.add(((MInputControl) n).getValue());
		return ics;
	}

	@Override
	public boolean isEnabled(DropTargetEvent event) {
		if (event.item instanceof TreeItem) {
			TreeItem item = (TreeItem) event.item;
			Object d = item.getData();
			if (d instanceof MInputControl
					&& InputControlDragSourceListener.isDragable(((MInputControl) d).getParent()))
				return true;
		}
		return false;
	}

	@Override
	public Transfer getTransfer() {
		return NodeTransfer.getInstance();
	}

}
