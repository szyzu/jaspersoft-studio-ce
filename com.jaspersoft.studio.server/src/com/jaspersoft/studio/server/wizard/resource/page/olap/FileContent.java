/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.wizard.resource.page.olap;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.server.model.MContentResource;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.wizard.resource.APageContent;
import com.jaspersoft.studio.server.wizard.resource.page.selector.SelectorFile;

public class FileContent extends APageContent {
	private SelectorFile scompo;

	public FileContent(ANode parent, AMResource resource, DataBindingContext bindingContext) {
		super(parent, resource, bindingContext);
	}

	private String title;

	public FileContent(ANode parent, AMResource resource, String title) {
		super(parent, resource);
		this.title = title;
	}

	@Override
	public String getName() {
		if (title != null)
			return title;
		return MContentResource.getIconDescriptor().getTitle();
	}

	@Override
	public String getPageName() {
		return "com.jaspersoft.studio.server.page.file";
	}

	@Override
	public String getHelpContext() {
		return "com.jaspersoft.studio.doc.editFile";
	}

	@Override
	public Control createContent(Composite parent) {
		scompo = new SelectorFile();
		scompo.addPageCompleteListener(this);
		rebind();
		return scompo.createControls(parent, pnode, res);
	}

	@Override
	protected void rebind() {
	}

	@Override
	public boolean isPageComplete() {
		return scompo != null && scompo.isPageComplete();
	}

}
