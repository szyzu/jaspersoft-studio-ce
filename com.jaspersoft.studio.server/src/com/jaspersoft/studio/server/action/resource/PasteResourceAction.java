/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.action.resource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.ICopyable;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.util.ModelVisitor;
import com.jaspersoft.studio.server.ResourceFactory;
import com.jaspersoft.studio.server.WSClientHelper;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.model.IInputControlsContainer;
import com.jaspersoft.studio.server.model.MFolder;
import com.jaspersoft.studio.server.model.MInputControl;
import com.jaspersoft.studio.server.model.MRQuery;
import com.jaspersoft.studio.server.model.MReference;
import com.jaspersoft.studio.server.model.MReportUnit;
import com.jaspersoft.studio.server.model.datasource.AMRDatasource;
import com.jaspersoft.studio.server.model.server.MServerProfile;
import com.jaspersoft.studio.server.protocol.IConnection;

import net.sf.jasperreports.eclipse.ui.ATitledDialog;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.ui.validator.IDStringValidator;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Misc;

public class PasteResourceAction extends Action {
	protected TreeViewer treeViewer;
	private TreeSelection s;
	protected Object contents;

	public PasteResourceAction(TreeViewer treeViewer) {
		super();
		setId(ActionFactory.PASTE.getId());
		setText(Messages.common_paste);
		setToolTipText(Messages.common_paste);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		this.treeViewer = treeViewer;
	}

	@Override
	public boolean isEnabled() {
		boolean res = super.isEnabled();
		contents = Clipboard.getDefault().getContents();
		ANode parent = getSelected();
		Object firstElement = ((TreeSelection) treeViewer.getSelection()).getFirstElement();
		if (firstElement instanceof AMResource && AddResourceAction.isSpecialFolder((AMResource) firstElement))
			return false;
		if (res && contents != null && contents instanceof List<?>) {
			List<?> list = (List<?>) contents;
			res = false;
			for (Object obj : list) {
				if (obj instanceof AMResource && AddResourceAction.isSpecialFolder((AMResource) obj))
					return false;
				if (obj instanceof AMResource && obj instanceof ICopyable) {
					if (!isSameServer(parent, (AMResource) obj) && (obj instanceof IInputControlsContainer
							|| obj instanceof MFolder || obj instanceof AMRDatasource || obj instanceof MReference
							|| obj instanceof MRQuery || obj instanceof MInputControl
							|| obj.getClass().getName().contains("Dashboard") //$NON-NLS-1$
							|| obj.getClass().getName().contains("Olap") //$NON-NLS-1$
							|| obj.getClass().getName().contains("Mondrian"))) { //$NON-NLS-1$
						return false;
					}
					ICopyable c = (ICopyable) obj;
					if (c.isCopyable2(parent) == ICopyable.RESULT.COPYABLE) {
						res = true;
						break;
					} else
						return false;
				}
			}
			return true;
		}
		if (contents == null)
			return false;
		if (res) {
			res = firstElement != null;
			if (res) {
				if (firstElement instanceof AMResource) {
					AMResource mres = (AMResource) firstElement;
					if (!isSameServer(parent, (AMResource) mres)
							&& (mres instanceof IInputControlsContainer || mres instanceof MFolder)) {
						return false;
					}
					int pmask = mres.getValue().getPermissionMask(mres.getWsClient());
					res = res && (pmask == 1 || (pmask & 4) == 4);
					if (AddResourceAction.isOrganizations(mres))
						return false;
				}
			}
		}
		return res;
	}

	protected ANode getSelected() {
		s = (TreeSelection) treeViewer.getSelection();
		TreePath[] p = s.getPaths();
		for (int i = 0; i < p.length; i++) {
			Object obj = p[i].getLastSegment();
			if (obj instanceof AMResource || obj instanceof MServerProfile)
				return (ANode) obj;
		}
		return null;
	}

	@Override
	public void run() {
		final ANode parent = getSelected();
		final List<?> list = (List<?>) Clipboard.getDefault().getContents();
		if (list == null)
			return;
		ProgressMonitorDialog pm = new ProgressMonitorDialog(UIUtils.getShell());
		try {
			pm.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						INode root = parent.getRoot();
						final String puri = parent instanceof AMResource
								? ((AMResource) parent).getValue().getUriString()
								: ""; //$NON-NLS-1$
						doWork(monitor, parent, list);
						ANode p = parent;
						if (parent instanceof AMResource)
							p = new ModelVisitor<ANode>(root) {

								@Override
								public boolean visit(INode n) {
									if (n instanceof AMResource) {
										AMResource mres = (AMResource) n;
										if (mres.getValue() != null && mres.getValue().getUriString().equals(puri)) {
											setObject(mres);
											stop();
										}
									}
									return true;
								}
							}.getObject();
						if (!p.getChildren().isEmpty())
							p = (ANode) p.getChildren().get(0);
						s = new TreeSelection(new TreePath(new Object[] { p }));

						UIUtils.getDisplay().asyncExec(() -> {
							treeViewer.refresh(true);
							treeViewer.setSelection(s);
						});
					} catch (Throwable e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}

			});
		} catch (InvocationTargetException e) {
			UIUtils.showError(e.getCause());
		} catch (InterruptedException e) {
			UIUtils.showError(e);
		}
	}

	private boolean existsAll = false;
	private boolean exists = false;
	private PasteDialog d;

	private void doWork(final IProgressMonitor monitor, ANode parent, List<?> list) throws Exception {
		existsAll = false;
		MServerProfile sp = (MServerProfile) parent.getRoot();
		String dURI = ((AMResource) parent).getValue().getUriString();
		IConnection ws = sp.getWsClient(monitor);
		Set<ANode> toRefresh = new HashSet<>();

		monitor.beginTask(Messages.PasteResourceAction_1 + dURI, list.size());
		if (parent instanceof MReportUnit)
			parent.setValue(ws.get(monitor, ((AMResource) parent).getValue(), null));
		boolean copy = false;
		boolean replace = false;
		confirmNames = true;
		firstConfirm = false;
		for (Object obj : list) {
			if (obj instanceof AMResource && obj instanceof ICopyable) {
				if (monitor.isCanceled())
					return;
				final AMResource m = (AMResource) obj;
				if (m.isCopyable2(parent) == ICopyable.RESULT.COPYABLE) {
					ResourceDescriptor origin = m.getValue();
					monitor.subTask(Messages.PasteResourceAction_4);
					exists = false;
					ResourceDescriptor rd = new ResourceDescriptor();
					try {
						rd.setName(origin.getName());
						rd.setUriString(dURI + "/" + origin.getName()); //$NON-NLS-1$
						rd.setWsType(origin.getWsType());
						if (parent instanceof MReportUnit) {
							rd.setUriString(rd.getUriString() + "_files"); //$NON-NLS-1$
							boolean ex = false;
							for (INode n : parent.getChildren()) {
								if (n.getValue() instanceof ResourceDescriptor) {
									ResourceDescriptor tmp = (ResourceDescriptor) n.getValue();
									if (tmp.getWsType().equals(rd.getWsType())
											&& tmp.getUriString().equals(rd.getUriString())) {
										rd = tmp;
										ex = true;
										break;
									}
								}
							}
							if (!ex)
								rd = null;
						} else
							rd = ws.get(monitor, rd, null);
						if (rd != null)
							exists = true;
					} catch (Exception e) {
					}
					if (!existsAll) {
						copy = false;
						replace = false;
					}
					if (exists && !existsAll) {
						UIUtils.getDisplay().syncExec(() -> {
							d = new PasteDialog(UIUtils.getShell(), m);
							if (d.open() != Dialog.OK) {
								monitor.setCanceled(true);
								return;
							}
						});
						existsAll = d.getForAll();
						switch (d.getChoise()) {
						case PasteDialog.REPLACE:
							replace = true;
							break;
						case PasteDialog.SKIP:
							continue;
						case PasteDialog.COPY:
							copy = true;
							break;
						}
					}
					monitor.subTask(Messages.PasteResourceAction_7);
					if (origin.isMainReport())
						m.setCut(false);
					if (m.isCut())
						toRefresh.add(m.getParent());
					if (!isSameServer(parent, m)) {
						IConnection mc = m.getWsClient();
						File file = FileUtils.createTempFile("tmp", "file"); //$NON-NLS-1$ //$NON-NLS-2$
						try {
							rd = mc.get(monitor, origin, file);
							rd.setData(Base64.encodeBase64(net.sf.jasperreports.eclipse.util.FileUtils.getBytes(file)));
							rd.setHasData(origin.getData() != null);
						} catch (Throwable e) {
							file = null;
						}
						if (parent instanceof MFolder) {
							String suf = copy ? getCopyName(parent, rd.getName(), monitor) : origin.getName();
							if (monitor.isCanceled())
								return;
							rd.setName(IDStringValidator.safeChar(suf));
							rd.setUriString(dURI + "/" + suf); //$NON-NLS-1$ //$NON-NLS-2$
							rd.setLabel(suf); // $NON-NLS-1$
							fixUris(rd, monitor, mc);
							ws.addOrModifyResource(monitor, rd, file);
						} else if (parent instanceof MReportUnit) {
							saveToReportUnit(monitor, (AMResource) parent, ws, origin, true);
							if (parent instanceof MReportUnit)
								parent.setValue(ws.get(monitor, ((MReportUnit) parent).getValue(), null));
						}
					} else if (parent instanceof MFolder) {
						if (copy) {
							IConnection mc = m.getWsClient();
							File file = FileUtils.createTempFile("tmp", "file"); //$NON-NLS-1$ //$NON-NLS-2$
							try {
								rd = mc.get(monitor, origin, file);
								rd.setData(Base64
										.encodeBase64(net.sf.jasperreports.eclipse.util.FileUtils.getBytes(file)));
								rd.setHasData(origin.getData() != null);
							} catch (Throwable e) {
								file = null;
							}

							rd.setIsNew(true);
							String suf = getCopyName(parent, rd.getName(), monitor);
							if (monitor.isCanceled())
								return;
							rd.setName(IDStringValidator.safeChar(suf));
							rd.setUriString(dURI + "/" + rd.getName()); //$NON-NLS-1$
							rd.setLabel(suf);
							fixUris(rd, monitor, mc);
							if (monitor.isCanceled())
								return;
							ws.addOrModifyResource(monitor, rd, null);
						} else if (replace) {
							IConnection mc = m.getWsClient();
							File file = FileUtils.createTempFile("tmp", "file"); //$NON-NLS-1$ //$NON-NLS-2$
							try {
								rd = mc.get(monitor, origin, file);
								rd.setData(Base64
										.encodeBase64(net.sf.jasperreports.eclipse.util.FileUtils.getBytes(file)));
								rd.setHasData(origin.getData() != null);
							} catch (Throwable e) {
								file = null;
							}
							rd.setParentFolder(dURI);
							rd.setUriString(dURI + "/" + rd.getName()); //$NON-NLS-1$
							fixUris(rd, monitor, mc);
							if (monitor.isCanceled())
								return;
							ws.addOrModifyResource(monitor, rd, null);
						} else {
							if (m.isCut()) {
								ws.move(monitor, origin, dURI);
								m.setCut(false);
							} else
								ws.copy(monitor, origin, dURI);
						}
					} else if (parent instanceof MReportUnit) {
						if (copy) {
							String suf = getCopyName(parent, origin.getName(), monitor);
							if (monitor.isCanceled())
								return;
							origin.setName(IDStringValidator.safeChar(suf));
							origin.setLabel(suf);
							origin.setUriString(origin.getUriString() + "/" + rd.getName()); //$NON-NLS-1$
						}
						if (!(m.getParent() instanceof MFolder) && m.getParent() instanceof AMResource) {
							if (origin.getParentFolder() != null && !origin.getParentFolder().endsWith("_files")) //$NON-NLS-1$
								origin.setIsReference(true);
						}
						saveToReportUnit(monitor, (MReportUnit) parent, ws, origin, false);
						// if (parent instanceof MReportUnit)
						// parent.setValue(ws.get(monitor, ((MReportUnit) parent).getValue(), null));
					}
				}
				deleteIfCut(monitor, m);
			}

			monitor.worked(1);
			if (monitor.isCanceled())
				break;
		}
		if (monitor.isCanceled())
			return;
		if (parent instanceof MReportUnit)
			ws.addOrModifyResource(monitor, ((MReportUnit) parent).getValue(), null);
		toRefresh.add(parent);
		for (ANode n : toRefresh)
			refreshNode(n, monitor);
	}

	private String copyName;
	private boolean confirmNames = true;
	private boolean firstConfirm = false;

	private String getCopyName(final ANode parent, final String name, final IProgressMonitor monitor) {
		String extension = FilenameUtils.getExtension(name);
		final String ext = extension.isEmpty() ? "" : "." + extension; //$NON-NLS-1$ //$NON-NLS-2$
		final String sname = extension.isEmpty() ? name : name.substring(0, name.length() - ext.length());

		copyName = "_COPY"; //$NON-NLS-1$
		for (int i = 0, j = 0; i < parent.getChildren().size(); i++) {
			INode n = parent.getChildren().get(i);
			if (n instanceof AMResource && ((AMResource) n).getValue().getName().equals(sname + copyName + ext)) {
				i = 0;
				j++;
				copyName = "_COPY" + j; //$NON-NLS-1$
			}
		}
		if (confirmNames) {
			UIUtils.getDisplay().syncExec(() -> {
				ResourceNameDialog d1 = new ResourceNameDialog(UIUtils.getShell(), name, sname + copyName + ext,
						parent);
				if (d1.open() == Dialog.OK)
					copyName = d1.getValue();
				else
					monitor.setCanceled(true);
			});
			firstConfirm = true;
		} else
			copyName = sname + copyName + ext;

		return copyName;
	}

	class ResourceNameDialog extends ATitledDialog {
		private ANode p;
		private String name;

		public ResourceNameDialog(Shell shell, String name, String value, ANode p) {
			super(shell);
			setTitle(Messages.PasteResourceAction_12);
			this.name = name;
			this.value = value;
			this.p = p;
		}

		@Override
		protected boolean isResizable() {
			return false;
		}

		private String value;

		public String getValue() {
			return value;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite c = (Composite) super.createDialogArea(parent);

			new Label(c, SWT.NONE).setText(Messages.PasteResourceAction_13 + name + "'"); // $NON-NLS-2$ //$NON-NLS-1$

			final Text txt = new Text(c, SWT.BORDER);
			txt.setText(Misc.nvl(value));
			txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			txt.addVerifyListener(new VerifyListener() {

				@Override
				public void verifyText(VerifyEvent e) {
					String oldText = txt.getText();
					String leftText = oldText.substring(0, e.start);
					String rightText = oldText.substring(e.end, oldText.length());
					String nm = leftText + e.text + rightText;
					if (nm.isEmpty()) {
						error(Messages.PasteResourceAction_6);
						return;
					}
					for (INode n : p.getChildren()) {
						if (n instanceof AMResource && ((AMResource) n).getValue().getName().equals(nm)) {
							error(Messages.PasteResourceAction_16);
							return;
						}
					}
					error(null);
				}

				private void error(String msg) {
					setError(msg);
					getButton(IDialogConstants.OK_ID).setEnabled(msg == null);
				}
			});
			txt.addModifyListener(e -> value = txt.getText());

			final Button bAuto = new Button(c, SWT.CHECK);
			bAuto.setText(Messages.PasteResourceAction_8);
			bAuto.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					confirmNames = !bAuto.getSelection();
				}
			});
			bAuto.setSelection(!firstConfirm);
			confirmNames = !bAuto.getSelection();
			return c;
		}
	}

	protected void deleteIfCut(IProgressMonitor monitor, AMResource m) throws Exception {
		if (m.isCut()) {
			m.setCut(false);
			WSClientHelper.deleteResource(monitor, m);
		}
	}

	protected void saveToReportUnit(IProgressMonitor monitor, AMResource parent, IConnection ws,
			ResourceDescriptor origin, boolean doSave) throws Exception {
		ResourceDescriptor prd = putIntoReportUnit(monitor, parent, ws, origin);
		if (doSave)
			ws.addOrModifyResource(monitor, prd, null);
	}

	public static ResourceDescriptor putIntoReportUnit(IProgressMonitor monitor, AMResource parent, IConnection ws,
			ResourceDescriptor origin) throws Exception {
		ResourceDescriptor prd = parent.getValue();
		ResourceDescriptor rd = null;
		File file = null;
		if (origin.getIsReference()) {
			rd = new ResourceDescriptor();
			rd.setName(origin.getName());
			rd.setLabel(origin.getLabel());
			rd.setDescription(origin.getDescription());
			rd.setIsNew(true);
			rd.setIsReference(true);
			rd.setReferenceUri(origin.getUriString());
			rd.setParentFolder(prd.getParentFolder() + "/" + prd.getName() + "_files"); //$NON-NLS-1$ //$NON-NLS-2$
			if (ResourceFactory.isFileResourceType(origin))
				rd.setWsType(ResourceDescriptor.TYPE_REFERENCE);
			else
				rd.setWsType(origin.getWsType());
			rd.setUriString(prd.getParentFolder() + "/" + prd.getName() + "_files/" + prd.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			file = FileUtils.createTempFile("tmp", "file"); //$NON-NLS-1$ //$NON-NLS-2$

			try {
				rd = ws.get(monitor, origin, file);
				rd.setData(Base64.encodeBase64(net.sf.jasperreports.eclipse.util.FileUtils.getBytes(file)));
				rd.setHasData(rd.getData() != null);

				rd = doPasteIntoReportUnit(prd, rd);
			} catch (Throwable e) {
				file = null;
			}
		}
		prd.getChildren().add(rd);
		return prd;
	}

	public static boolean isSameServer(ANode parent, AMResource m) {
		IConnection mc = m.getWsClient();
		IConnection pc = null;
		if (parent instanceof AMResource)
			pc = ((AMResource) parent).getWsClient();
		else if (parent instanceof MServerProfile)
			pc = ((MServerProfile) parent).getWsClient();
		if (pc != null && mc != null)
			try {
				return mc.getServerProfile().getUrl().equals(pc.getServerProfile().getUrl());
			} catch (MalformedURLException e) {
				UIUtils.showError(e);
			} catch (URISyntaxException e) {
				UIUtils.showError(e);
			}

		return true;
	}

	protected static ResourceDescriptor doPasteIntoReportUnit(ResourceDescriptor prd, ResourceDescriptor origin) {
		String ruuri = prd.getUriString();
		origin.setParentFolder(ruuri + "_files"); //$NON-NLS-1$
		origin.setIsNew(true);
		origin.setName(getRName(origin.getName(), prd.getChildren()));
		origin.setUriString(origin.getParentFolder() + "/" + origin.getName()); //$NON-NLS-1$
		origin.setLabel(origin.getLabel());
		origin.setMainReport(false);

		for (ResourceDescriptor rd : origin.getChildren()) {
			if (!rd.getIsReference()) {
				rd.setParentFolder(origin.getUriString() + "_files"); //$NON-NLS-1$
				rd.setUriString(rd.getParentFolder() + "/" + rd.getName()); //$NON-NLS-1$
			}
		}
		return origin;
	}

	private static String getRName(String name, List<?> children) {
		String n = name;
		int j = 0;
		for (int i = 0; i < children.size(); i++) {
			ResourceDescriptor r = (ResourceDescriptor) children.get(i);
			if (n.equals(r.getName())) {
				n = name + "_" + j; //$NON-NLS-1$
				i = 0;
				j++;
			}
		}
		return n;
	}

	private void fixUris(ResourceDescriptor rd, IProgressMonitor monitor, IConnection mc) {
		for (ResourceDescriptor r : rd.getChildren()) {
			r.setIsNew(true);
			if (!r.getIsReference() && r.getParentFolder().endsWith("_files")) { //$NON-NLS-1$
				File file;
				try {
					file = FileUtils.createTempFile("tmp", "file"); //$NON-NLS-1$ //$NON-NLS-2$
					try {
						mc.get(monitor, r, file);
						r.setData(Base64.encodeBase64(net.sf.jasperreports.eclipse.util.FileUtils.getBytes(file)));
						r.setHasData(r.getData() != null);
					} catch (Throwable e) {
						file = null;
					}
					r.setParentFolder(rd.getUriString() + "_files"); //$NON-NLS-1$
					r.setUriString(r.getParentFolder() + "/" + r.getName()); //$NON-NLS-1$

					// r.setUriString(r.getUriString().replaceFirst(r.getParentFolder(),
					// rd.getUriString() + "_files"));
					// r.setParentFolder(rd.getUriString()+"_files");
					fixUris(r, monitor, mc);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (monitor.isCanceled())
				break;
		}
	}

	public static void refreshNode(INode p, IProgressMonitor monitor) throws Exception {
		if (p instanceof AMResource)
			WSClientHelper.refreshResource((AMResource) p, monitor);
		else if (p instanceof MServerProfile) {
			WSClientHelper.listFolder(((MServerProfile) p), ((MServerProfile) p).getWsClient(monitor),
					Messages.PasteResourceAction_15, monitor, 0);
		}
	}
}
