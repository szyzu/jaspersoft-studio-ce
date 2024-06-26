/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.wizard.resource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.server.ResourceFactory;
import com.jaspersoft.studio.server.ServerManager;
import com.jaspersoft.studio.server.WSClientHelper;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.model.MReference;
import com.jaspersoft.studio.server.model.MReportUnit;
import com.jaspersoft.studio.server.model.server.MServerProfile;
import com.jaspersoft.studio.server.wizard.resource.page.AddResourcePage;
import com.jaspersoft.studio.server.wizard.resource.page.ResourceDescriptorPage;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

public class AddResourceWizard extends Wizard {
	private AddResourcePage page0;

	public AddResourceWizard(ANode parent, boolean nested) {
		this(parent);
		setNested(nested);
	}

	public AddResourceWizard(ANode parent) {
		super();
		setWindowTitle(Messages.AddResourceWizard_windowtitle);
		setNeedsProgressMonitor(true);
		this.parent = parent;
	}

	private boolean skipFirstPage = false;
	private boolean nested = false;

	public void setNested(boolean nested) {
		this.nested = nested;
	}

	public void setSkipFirstPage(boolean skipFirstPage) {
		this.skipFirstPage = skipFirstPage;
	}

	@Override
	public IWizardPage getStartingPage() {
		if (skipFirstPage && page0 != null)
			return getNextPage(page0);
		return super.getStartingPage();
	}

	private boolean dsonly = false;

	public void setOnlyDatasource(boolean dsonly) {
		this.dsonly = dsonly;
	}

	private boolean ruOnly = false;

	public void setOnlyReportUnit(boolean ruOnly) {
		this.ruOnly = ruOnly;
	}

	private boolean monOnly = false;

	public void setMondrianOnly(boolean monOnly) {
		this.monOnly = monOnly;
	}

	private boolean olapOnly = false;

	public void setOlapOnly(boolean olapOnly) {
		this.olapOnly = olapOnly;
	}

	@Override
	public void addPages() {
		page0 = new AddResourcePage(parent);
		page0.setOnlyDatasource(dsonly);
		page0.setOnlyReportUnit(ruOnly);
		page0.setMondrianOnly(monOnly);
		page0.setOlapOnly(olapOnly);
		addPage(page0);

		addPage(new ResourceDescriptorPage());
	}

	private ResourceFactory rfactory = new ResourceFactory();
	private Map<Class<? extends AMResource>, IWizardPage[]> pagemap = new HashMap<>();

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == page0) {
			AMResource r = page0.getResource();
			if (r != null) {
				int size = getPageCount();
				try {
					Field f = Wizard.class.getDeclaredField("pages"); //$NON-NLS-1$
					f.setAccessible(true); // FIXME, REALLY UGLY :( BUT IT'S FASTER
					List<IWizardPage> wpages = (List<IWizardPage>) f.get(this);
					for (int i = 1; i < size; i++)
						wpages.remove(1);
				} catch (SecurityException | NoSuchFieldException | IllegalArgumentException
						| IllegalAccessException e) {
					e.printStackTrace();
				}
				IWizardPage[] rpage = pagemap.get(r.getClass());
				if (rpage == null) {
					rpage = rfactory.getResourcePage(parent, r);
					if (rpage != null)
						pagemap.put(r.getClass(), rpage);
				}
				if (rpage != null) {
					IWizardPage firstpage = null;
					for (IWizardPage p : rpage) {
						if (getPage(p.getName()) == null) {
							addPage(p);
							if (firstpage == null)
								firstpage = p;
						}
					}
					return firstpage;
				}
				return null;
			}
		}
		return super.getNextPage(page);
	}

	@Override
	public void dispose() {
		super.dispose();
		for (IWizardPage[] pages : pagemap.values())
			for (IWizardPage p : pages)
				p.dispose();
	}

	private ANode parent;

	public AMResource getResource() {
		return page0.getResource();
	}

	private boolean canFinish = true;;

	@Override
	public boolean performFinish() {
		canFinish = true;
		if (nested)
			return true;
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(Messages.AddResourceWizard_1, IProgressMonitor.UNKNOWN);

					try {
						AMResource resource = getResource();
						ResourceDescriptor res = resource.getValue();
						if (res.getIsNew()) {
							res.setUriString(res.getParentFolder() + "/" //$NON-NLS-1$
									+ res.getName());
							INode r = parent.getRoot();
							if (r instanceof MServerProfile) {
								MServerProfile msp = (MServerProfile) r;
								msp = ServerManager.getMServerProfileCopy(msp);
								if (WSClientHelper.findSelected(monitor, resource.getValue(), msp) != null && !UIUtils
										.showConfirmation(Messages.AddResourceWizard_3, Messages.AddResourceWizard_4)) {
									canFinish = false;
									return;
								}
							}
						}
						if (parent instanceof MReportUnit
								&& (resource instanceof MReference || resource.getValue().getIsReference())) {
							MReportUnit mrunit = (MReportUnit) parent;
							WSClientHelper.refreshResource(mrunit, monitor);

							ResourceDescriptor runit = mrunit.getValue();
							mrunit.setValue(runit);
							runit.getChildren().add(resource.getValue());
							WSClientHelper.saveResource(mrunit, monitor);
						} else {
							resource.setParent(parent, -1);
							WSClientHelper.saveResource(resource, monitor);
						}
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			UIUtils.showError(e.getCause());
			return false;
		} catch (InterruptedException e) {
			UIUtils.showError(e);
			return false;
		}
		return canFinish;
	}

	@Override
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == page0)
			return false;
		return super.canFinish();
	}

}
