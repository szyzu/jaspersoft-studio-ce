/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.customvisualization.model.command;

import java.util.List;

import com.jaspersoft.studio.components.customvisualization.ui.framework.CVCWidgetsDescriptor;
import com.jaspersoft.studio.wizards.JSSWizard;

public class CVCWizard extends JSSWizard {
	private List<CVCWidgetsDescriptor> modules;
	private CVCTypeWizardPage page0;

	public CVCWizard(List<CVCWidgetsDescriptor> modules) {
		super();
		setNeedsProgressMonitor(false);
		this.modules = modules;
	}

	@Override
	public void addPages() {
		page0 = new CVCTypeWizardPage(modules);
		addPage(page0);
	}

	public CVCWidgetsDescriptor getModule() {
		return page0.getModule();
	}
}
