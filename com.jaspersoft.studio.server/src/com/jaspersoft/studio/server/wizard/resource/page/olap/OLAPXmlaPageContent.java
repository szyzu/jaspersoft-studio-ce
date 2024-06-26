/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.wizard.resource.page.olap;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceProperty;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.model.datasource.MROlapXmlaConnection;
import com.jaspersoft.studio.server.utils.ResourceDescriptorUtil;
import com.jaspersoft.studio.server.wizard.resource.APageContent;
import com.jaspersoft.studio.utils.UIUtil;

public class OLAPXmlaPageContent extends APageContent {

	private Text tdatasource;
	private Text tuser;
	private Text tpass;
	private Text tcatalog;
	private Text turi;

	public OLAPXmlaPageContent(ANode parent, AMResource resource, DataBindingContext bindingContext) {
		super(parent, resource, bindingContext);
	}

	public OLAPXmlaPageContent(ANode parent, AMResource resource) {
		super(parent, resource);
	}

	@Override
	public String getPageName() {
		return "com.jaspersoft.studio.server.wizard.resource.page.olap.xmla"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return MROlapXmlaConnection.getIconDescriptor().getTitle();
	}

	public Control createContent(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		UIUtil.createLabel(composite, Messages.OLAPXmlaPageContent_uti);

		turi = new Text(composite, SWT.BORDER);
		turi.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtil.createLabel(composite, Messages.OLAPXmlaPageContent_catalog);

		tcatalog = new Text(composite, SWT.BORDER);
		tcatalog.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtil.createLabel(composite, Messages.OLAPXmlaPageContent_datasource);

		tdatasource = new Text(composite, SWT.BORDER);
		tdatasource.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtil.createLabel(composite, Messages.OLAPXmlaPageContent_username);

		tuser = new Text(composite, SWT.BORDER);
		tuser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// if (res.getValue().getIsNew()) {
		UIUtil.createLabel(composite, Messages.OLAPXmlaPageContent_pass);

		tpass = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		tpass.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// }
		rebind();
		return composite;
	}

	@Override
	protected void rebind() {
		List<ResourceProperty> props = res.getValue().getProperties();
		ResourceProperty resprop = ResourceDescriptorUtil.getProperty(MROlapXmlaConnection.PROP_XMLA_URI, props);

		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(turi),
				PojoProperties.value("value").observe(resprop)); //$NON-NLS-1$

		resprop = ResourceDescriptorUtil.getProperty(MROlapXmlaConnection.PROP_XMLA_CATALOG, props);

		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(tcatalog),
				PojoProperties.value("value").observe(resprop)); //$NON-NLS-1$

		resprop = ResourceDescriptorUtil.getProperty(MROlapXmlaConnection.PROP_XMLA_DATASOURCE, props);

		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(tdatasource),
				PojoProperties.value("value").observe(resprop)); //$NON-NLS-1$

		resprop = ResourceDescriptorUtil.getProperty(MROlapXmlaConnection.PROP_XMLA_USERNAME, props);

		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(tuser),
				PojoProperties.value("value").observe(resprop)); //$NON-NLS-1$

		resprop = ResourceDescriptorUtil.getProperty(MROlapXmlaConnection.PROP_XMLA_PASSWORD, props);

		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(tpass),
				PojoProperties.value("value").observe(resprop)); //$NON-NLS-1$
	}

	@Override
	public String getHelpContext() {
		return "com.jaspersoft.studio.doc.adapter_xmla";
	}
}
