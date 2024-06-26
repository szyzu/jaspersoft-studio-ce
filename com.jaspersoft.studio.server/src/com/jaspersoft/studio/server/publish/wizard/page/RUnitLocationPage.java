/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.publish.wizard.page;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MDummy;
import com.jaspersoft.studio.outline.ReportTreeContetProvider;
import com.jaspersoft.studio.outline.ReportTreeLabelProvider;
import com.jaspersoft.studio.server.ContextHelpIDs;
import com.jaspersoft.studio.server.ResourceFactory;
import com.jaspersoft.studio.server.ServerProvider;
import com.jaspersoft.studio.server.WSClientHelper;
import com.jaspersoft.studio.server.action.resource.RefreshResourcesAction;
import com.jaspersoft.studio.server.export.AExporter;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.model.AMJrxmlContainer;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.model.IInputControlsContainer;
import com.jaspersoft.studio.server.model.MContentResource;
import com.jaspersoft.studio.server.model.MFolder;
import com.jaspersoft.studio.server.model.MJrxml;
import com.jaspersoft.studio.server.model.MReportUnit;
import com.jaspersoft.studio.server.model.server.MServerProfile;
import com.jaspersoft.studio.server.protocol.Feature;
import com.jaspersoft.studio.server.publish.FindResources;
import com.jaspersoft.studio.server.publish.PublishUtil;
import com.jaspersoft.studio.server.utils.ResourceDescriptorUtil;
import com.jaspersoft.studio.server.utils.ValidationUtils;
import com.jaspersoft.studio.server.wizard.resource.page.selector.SelectorDatasource;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.wizards.JSSHelpWizardPage;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.ui.validator.IDStringValidator;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.design.JasperDesign;

public class RUnitLocationPage extends JSSHelpWizardPage {
	private JasperDesign jDesign;
	private TreeViewer treeViewer;
	private Button bnRunit;
	private Text ruLabel;

	private ANode n;
	private RefreshResourcesAction refreshAction;
	private JasperReportsConfiguration jConfig;
	private Text ruID;
	private Text ruDescription;

	private boolean isFillingInput;
	private boolean canSuggestID;

	public RUnitLocationPage(JasperReportsConfiguration jConfig, JasperDesign jDesign, ANode n) {
		super("serverpublish"); //$NON-NLS-1$
		setTitle(Messages.RUnitLocationPage_title);
		setDescription(Messages.RUnitLocationPage_description);
		this.jDesign = jDesign;
		this.n = n;
		this.jConfig = jConfig;
	}

	public void setValue(JasperDesign jDesign, ANode n) {
		this.jDesign = jDesign;
		this.n = n;
		fillInput();
	}

	public AMJrxmlContainer getSelectedNode() {
		TreeSelection ts = (TreeSelection) treeViewer.getSelection();
		Object obj = ts.getFirstElement();
		if (obj != null) {
			if (obj instanceof MFolder)
				return reportUnit;
			return (AMJrxmlContainer) obj;
		}
		if (n instanceof AMJrxmlContainer)
			return (AMJrxmlContainer) n;
		reportUnit.setJasperConfiguration(jConfig);
		return reportUnit;
	}

	/**
	 * Return the context name for the help of this page
	 */
	@Override
	protected String getContextName() {
		return ContextHelpIDs.WIZARD_SELECT_SERVER;
	}

	@Override
	public boolean isPageComplete() {
		boolean isC = super.isPageComplete() && getErrorMessage() == null;
		if (isC)
			isC = isPageCompleteLogic();
		return isC;
	}

	protected boolean isPageCompleteLogic() {
		boolean isC;
		TreeSelection ts = (TreeSelection) treeViewer.getSelection();
		Object firstElement = ts.getFirstElement();
		isC = firstElement instanceof MJrxml || firstElement instanceof MFolder || firstElement instanceof MReportUnit;
		AMJrxmlContainer runit = getReportUnit();
		if (isC && firstElement instanceof MFolder) {
			isC = runit instanceof AMJrxmlContainer && runit.getParent() != null;
			if (!(firstElement instanceof MFolder))
				isC = bnRunit.getSelection();
		}
		if (isC && (firstElement instanceof MFolder || firstElement instanceof MServerProfile)) {
			String nm = runit.getValue().getName();
			String t = runit.getValue().getWsType();
			for (INode n : ((ANode) firstElement).getChildren()) {
				ResourceDescriptor rd = (ResourceDescriptor) n.getValue();
				if (n == newjrxml || n == newrunit || rd == null)
					continue;
				if (rd.getName().equals(nm) && !rd.getWsType().equals(t)) {
					super.setErrorMessage("A resource of different type already exist for the same name");
					return false;
				}
			}
		}
		return isC;
	}

	@Override
	public void setErrorMessage(String newMessage) {
		super.setErrorMessage(newMessage);
		setPageComplete(isPageComplete());
	}

	/*
	 * Perform validation checks and eventually set the error message.
	 */
	private void performPageChecks() {
		String errorMsg = null;
		errorMsg = ValidationUtils.validateName(ruID.getText());
		if (errorMsg == null)
			errorMsg = ValidationUtils.validateLabel(ruLabel.getText());
		if (errorMsg == null)
			errorMsg = ValidationUtils.validateDesc(ruDescription.getText());
		setErrorMessage(errorMsg);
		setPageComplete(errorMsg == null);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		treeViewer = new TreeViewer(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 300;
		gd.heightHint = 400;
		gd.horizontalSpan = 2;
		treeViewer.getTree().setLayoutData(gd);
		treeViewer.setContentProvider(new ReportTreeContetProvider() {
			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof AMResource) {
					AMResource mres = (AMResource) parentElement;
					if (mres instanceof MReportUnit || (mres.isSupported(Feature.INPUTCONTROLS_ORDERING)
							&& (mres instanceof IInputControlsContainer))) {
						if (mres.getChildren() != null && !mres.getChildren().isEmpty()) {
							List<INode> children = new ArrayList<>();
							if (mres.getChildren().get(0) instanceof MDummy)
								try {
									return mres.getChildren().toArray();
								} catch (Exception e) {
									UIUtils.showError(e);
								}
							for (INode n1 : mres.getChildren())
								if (n1 instanceof AMResource
										&& !SelectorDatasource.isDatasource(((AMResource) n1).getValue()))
									children.add(n1);
							return children.toArray();
						}
					} else if (mres instanceof MFolder && newrunit.getValue().getIsNew()) {
						MFolder node = (MFolder) mres;
						if (node.getChildren() != null && !node.getChildren().isEmpty()) {
							List<INode> children = new ArrayList<>();
							for (INode n1 : node.getChildren())
								if (n1 != newrunit && n1 != newjrxml)
									children.add(n1);
							return children.toArray();
						}
					}
				}
				return super.getChildren(parentElement);
			}
		});
		treeViewer.setLabelProvider(new ReportTreeLabelProvider());
		ColumnViewerToolTipSupport.enableFor(treeViewer);

		bnRunit = new Button(composite, SWT.CHECK);
		bnRunit.setText(Messages.RUnitLocationPage_addreportunit_button);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		bnRunit.setLayoutData(gd);
		bnRunit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = bnRunit.getSelection();
				// Enable/Disable the detail textboxes
				ruLabel.setEnabled(selected);
				ruID.setEnabled(selected);
				ruDescription.setEnabled(selected);

				reportUnit = selected ? getNewRunit() : getNewJrxml();
				if (reportUnit.getParent() == null) {
					TreeSelection ts = (TreeSelection) treeViewer.getSelection();
					Object obj = ts.getFirstElement();
					if (obj instanceof ANode)
						reportUnit.setParent((ANode) obj, -1);
				}
				performPageChecks();
				setPageComplete(isPageComplete());
			}
		});
		bnRunit.setSelection(true);

		// Report Unit shown label (resource descriptor label)
		Label lblRepoUnitName = new Label(composite, SWT.NONE);
		lblRepoUnitName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		lblRepoUnitName.setText(Messages.RUnitLocationPage_reportunitlabel);
		ruLabel = new Text(composite, SWT.BORDER);
		ruLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ruLabel.addModifyListener(e -> {
			if (isRefresh)
				return;
			isRefresh = true;
			MReportUnit mru = getNewRunit();
			String rtext = ruLabel.getText();
			String id = IDStringValidator.safeChar(rtext);
			String validationError = ValidationUtils.validateLabel(rtext);
			if (validationError == null) {
				ANode p = mru.getParent();
				if (p != null)
					for (INode n1 : p.getChildren()) {
						if (n1 instanceof AMResource && n1 != mru) {
							ResourceDescriptor amr = ((AMResource) n1).getValue();
							if (amr.getIsNew())
								continue;
							if (amr.getName().equals(id))
								validationError = "This id is already used in this folder";
							else if (amr.getLabel().equals(rtext))
								validationError = "This label is already used in this folder";
						}
					}
			}
			if (canSuggestID)
				ruID.setText(rtext);
			setErrorMessage(validationError);
			if (validationError == null) {
				ResourceDescriptor ru = mru.getValue();
				ru.setLabel(rtext);
				// suggest the ID
				if (canSuggestID) {
					ru.setName(IDStringValidator.safeChar(rtext));
					ru.setUriString(ru.getParentFolder() + "/" + ru.getName());
				}
				setPageComplete(true);
			}
			isRefresh = false;
		});

		// Report Unit ID (resource descriptor name)
		Label lblRepoUnitID = new Label(composite, SWT.NONE);
		lblRepoUnitID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		lblRepoUnitID.setText(Messages.RUnitLocationPage_lblreportunit);
		ruID = new Text(composite, SWT.BORDER);
		ruID.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ruID.addModifyListener(e -> {
			if (isRefresh)
				return;
			isRefresh = true;
			MReportUnit mru = getNewRunit();
			String rtext = ruID.getText();
			String validationError = ValidationUtils.validateName(rtext);
			if (validationError == null) {
				ANode p = mru.getParent();
				if (p != null)
					for (INode n1 : p.getChildren())
						if (n1 instanceof AMResource && n1 != mru && n1 != newjrxml
								&& ((AMResource) n1).getValue().getName().equals(rtext))
							validationError = "This id is already used in this folder";
			}
			setErrorMessage(validationError);
			if (validationError == null) {
				ResourceDescriptor ru = mru.getValue();
				ru.setName(rtext);
				ru.setUriString(ru.getParentFolder() + "/" + ru.getName());
			}
			if (!isFillingInput && validationError == null) {
				canSuggestID = false;
			} else {
				canSuggestID = true;
			}
			isRefresh = false;
		});
		// sanitize the text for the id attribute (name)
		// of the repository resource
		ruID.addVerifyListener(e -> e.text = IDStringValidator.safeChar(e.text));

		// Report Unit description
		Label lblRepoUnitDescription = new Label(composite, SWT.NONE);
		GridData descLblGD = new GridData(SWT.FILL, SWT.TOP, false, false);
		lblRepoUnitDescription.setLayoutData(descLblGD);
		lblRepoUnitDescription.setText(Messages.RUnitLocationPage_reportunitdesc_label);
		ruDescription = new Text(composite, SWT.BORDER | SWT.MULTI);
		GridData descGD = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		descGD.heightHint = 50;
		ruDescription.setLayoutData(descGD);
		ruDescription.setText(""); //$NON-NLS-1$
		ruDescription.addModifyListener(e -> {
			if (isRefresh)
				return;
			String rtext = ruDescription.getText();
			ResourceDescriptor ru = getNewRunit().getValue();
			ru.setDescription(rtext);
			setErrorMessage(ValidationUtils.validateDesc(rtext));
		});

		treeViewer.addSelectionChangedListener(event -> {
			TreeSelection ts = (TreeSelection) event.getSelection();
			Object obj = ts.getFirstElement();
			handleSelectionChanged(obj);
		});
		treeViewer.addDoubleClickListener(event -> {
			TreeSelection ts = (TreeSelection) treeViewer.getSelection();
			Object el = ts.getFirstElement();
			if (el instanceof MFolder || el instanceof MServerProfile) {
				if (treeViewer.getExpandedState(el))
					treeViewer.collapseToLevel(el, 1);
				else {
					if (refreshAction == null)
						refreshAction = new RefreshResourcesAction(treeViewer);
					if (refreshAction.isEnabled())
						refreshAction.run();
					treeViewer.expandToLevel(el, 1);
				}
			}
		});
		treeViewer.addTreeListener(new ITreeViewerListener() {
			private ServerProvider serverProvider;

			public void treeExpanded(final TreeExpansionEvent event) {
				if (!skipEvents) {
					UIUtils.getDisplay().asyncExec(() -> {
						try {
							getContainer().run(true, true, new IRunnableWithProgress() {

								public void run(IProgressMonitor monitor)
										throws InvocationTargetException, InterruptedException {
									monitor.beginTask(Messages.Publish2ServerWizard_MonitorName,
											IProgressMonitor.UNKNOWN);
									try {
										if (serverProvider == null)
											serverProvider = new ServerProvider();
										Object element = event.getElement();
										boolean be = reportUnit.getParent() == element;
										serverProvider.handleTreeEvent(event, monitor);
										if (be) {
											MFolder f = (MFolder) element;
											String nm = reportUnit.getValue().getName();
											boolean isnew = true;
											for (INode n1 : f.getChildren())
												if (n1 instanceof AMJrxmlContainer
														&& ((AMJrxmlContainer) n1).getValue().getName().equals(nm)) {
													reportUnit = (AMJrxmlContainer) n1;
													isnew = false;
													break;
												}
											if (isnew)
												reportUnit.setParent(f, -1);
										}
									} catch (Exception e) {
										UIUtils.showError(e);
									} finally {
										monitor.done();
									}
								}
							});
						} catch (InvocationTargetException e) {
							UIUtils.showError(e.getCause());
						} catch (InterruptedException e) {
							UIUtils.showError(e.getCause());
						}
					});

				}
			}

			public void treeCollapsed(TreeExpansionEvent event) {
				// nothing to do
			}
		});
		fillInput();
	}

	private AMJrxmlContainer reportUnit;
	private MReportUnit newrunit;
	private MJrxml newjrxml;

	private MReportUnit getNewRunit() {
		if (newrunit == null) {
			ResourceDescriptor rd = MReportUnit.createDescriptor(null);
			rd.setName(null);
			rd.setResourceProperty(ResourceDescriptor.PROP_RU_ALWAYS_PROPMT_CONTROLS, true);
			newrunit = new MReportUnit(null, rd, -1);
		}
		PublishUtil.initRUnitName(newrunit, jDesign, jConfig);
		return newrunit;
	}

	private MJrxml getNewJrxml() {
		if (newjrxml == null) {
			ResourceDescriptor rd = MJrxml.createDescriptor(null);
			rd.setName(null);
			newjrxml = new MJrxml(null, rd, -1);
		}
		PublishUtil.initRUnitName(newjrxml, jDesign, jConfig);
		return newjrxml;
	}

	private AMJrxmlContainer getReportUnit() {
		PublishUtil.initRUnitName(reportUnit, jDesign, jConfig);
		return reportUnit;
	}

	private boolean isRefresh = false;

	protected void handleSelectionChanged(Object obj) {
		if (isRefresh)
			return;
		isRefresh = true;
		boolean isFolder = obj instanceof MFolder;
		ruLabel.setEnabled(bnRunit.getSelection() && isFolder);
		ruID.setEnabled(bnRunit.getSelection() && isFolder);
		ruDescription.setEnabled(bnRunit.getSelection() && isFolder);

		reportUnit = getNewRunit();

		if (obj instanceof MContentResource) {
			try {
				WSClientHelper.refreshResource((MContentResource) obj, new NullProgressMonitor());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (obj instanceof MReportUnit) {
			reportUnit = getNewRunit();
			reportUnit = (MReportUnit) obj;
			ruLabel.setText(Misc.nvl(reportUnit.getValue().getLabel()));
			ruID.setText(Misc.nvl(reportUnit.getValue().getName()));
			ruDescription.setText(Misc.nvl(reportUnit.getValue().getDescription()));
		} else if (obj instanceof MFolder) {
			newrunit = getNewRunit();
			newrunit.setParent((ANode) obj, -1);

			newjrxml = getNewJrxml();
			newjrxml.setParent((ANode) obj, -1);

			if (bnRunit.getSelection())
				reportUnit = newrunit;
			else
				reportUnit = newjrxml;

			ResourceDescriptor nrd = reportUnit.getValue();
			nrd.setName(ruID.getText());
			nrd.setLabel(ruLabel.getText());
			nrd.setDescription(ruDescription.getText());
			String uri = ((MFolder) obj).getValue().getUriString();
			nrd.setParentFolder(uri);
			nrd.setUriString(uri + "/" + nrd.getName()); //$NON-NLS-1$
		} else if (obj instanceof MJrxml) {
			reportUnit = getNewJrxml();
			reportUnit = (MJrxml) obj;
			ruLabel.setText(Misc.nvl(reportUnit.getValue().getLabel()));
			ruID.setText(Misc.nvl(reportUnit.getValue().getName()));
			ruDescription.setText(Misc.nvl(reportUnit.getValue().getDescription()));
		} else if (obj instanceof AMResource) {
			ANode mparent = ((AMResource) obj).getParent();
			treeViewer.setSelection(new StructuredSelection(mparent), true);
			handleSelectionChanged(mparent);
		} else
			setPageComplete(false);
		performPageChecks();
		isRefresh = false;
	}

	private boolean skipEvents = false;

	public void fillInput() {
		UIUtils.getDisplay().asyncExec(() -> {
			isFillingInput = true;
			initIDLabel();
			if (n instanceof MServerProfile)
				look4SelectedUnit((MServerProfile) n);
			setSelectedNode();
			isFillingInput = false;
		});
	}

	private void initIDLabel() {
		if (jDesign != null) {
			String rUnitNAme = PublishUtil.getRUnitNAme(jDesign, jConfig);
			ruID.setText(rUnitNAme.replace(" ", "")); //$NON-NLS-1$ //$NON-NLS-2$
			ruLabel.setText(rUnitNAme);
			ruDescription.setText(newrunit != null ? Misc.nvl(newrunit.getValue().getDescription()) : "");
		}
	}

	private void setSelectedNode() {
		if (n == null || treeViewer.getTree().isDisposed())
			return;
		Display.getDefault().asyncExec(() -> {
			INode root = n.getRoot();
			if (root instanceof MServerProfile)
				root = ((ANode) root.getParent()).getRoot();
			treeViewer.setInput(root);
			skipEvents = true;
			treeViewer.refresh();
			treeViewer.setSelection(new StructuredSelection(n), true);
			setPageComplete(isPageCompleteLogic());
			skipEvents = false;
			handleSelectionChanged(n);
		});
	}

	private void look4SelectedUnit(final MServerProfile mres) {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					IFile file = (IFile) jConfig.get(FileUtils.KEY_FILE);
					ANode node = FindResources.findReportUnit(mres, monitor, jDesign, file);
					if (monitor.isCanceled())
						return;
					if (n != mres)
						return;
					n = node;
					try {
						if (n instanceof MReportUnit && !ResourceDescriptorUtil.isReportMain(file)) {
							MReportUnit mReportUnit = (MReportUnit) n;
							String res = jDesign.getProperty(AExporter.PROP_REPORTRESOURCE);
							if (!Misc.isNullOrEmpty(res)) {
								mReportUnit.setValue(WSClientHelper.getResource(monitor, n, mReportUnit.getValue()));
								List<ResourceDescriptor> children = mReportUnit.getValue().getChildren();
								ResourceDescriptor rd = null;
								for (ResourceDescriptor c : children) {
									if (c.getWsType().equals(ResourceDescriptor.TYPE_JRXML)
											&& c.getUriString().equals(res)) {
										rd = c;
										break;
									}
								}
								if (rd != null) {
									n.removeChildren();
									ANode tmpn = null;
									for (ResourceDescriptor c : children) {
										AMResource mr = ResourceFactory.getResource(n, c, -1);
										if (c == rd)
											tmpn = mr;
									}
									n = tmpn;
								}
							}
						}
					} catch (Exception ce) {
						ce.printStackTrace();
					}
				}
			});
		} catch (InvocationTargetException e) {
			UIUtils.showError(e.getCause());
		} catch (InterruptedException e) {
			UIUtils.showError(e.getCause());
		}
	}
}
