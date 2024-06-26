/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.wizard.resource.page;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.protocol.ProxyConnection;
import com.jaspersoft.studio.server.wizard.resource.APageContent;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.UIUtil;

import net.sf.jasperreports.eclipse.ui.validator.EmptyStringValidator;
import net.sf.jasperreports.eclipse.util.Misc;

public class QueryPageContent extends APageContent {

	public static final String[] LANGUAGES = new String[] { "sql", "hql", "domain", "sl", "HiveQL", "MongoDbQuery" };

	public QueryPageContent(ANode parent, AMResource resource, DataBindingContext bindingContext) {
		super(parent, resource, bindingContext);
	}

	public QueryPageContent(ANode parent, AMResource resource) {
		super(parent, resource);
	}

	private boolean showLangs = true;
	private Text tsql;
	private Combo clang;
	private QProxy proxy;

	public QueryPageContent(ANode parent, AMResource resource, boolean showLangs) {
		super(parent, resource);
		this.showLangs = showLangs;
	}

	@Override
	public String getPageName() {
		return "com.jaspersoft.studio.server.page.query";
	}

	@Override
	public String getName() {
		return Messages.RDQueryPage_textquery;
	}

	public Control createContent(Composite parent) {
		Control createContentComposite = createContentComposite(parent, bindingContext, res.getValue(), res, showLangs);
		rebind();
		return createContentComposite;
	}

	public Control createContentComposite(Composite parent, DataBindingContext bindingContext, ResourceDescriptor r,
			AMResource res, boolean showLangs) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		if (showLangs) {
			UIUtil.createLabel(composite, Messages.RDQueryPage_language);

			clang = new Combo(composite, SWT.BORDER);
			clang.setItems(LANGUAGES);
			if (res.getWsClient().getServerInfo().getVersion().compareTo(ProxyConnection.ONYX1) > 0)
				clang.add("jasperQL");

			// clang.setItems(ModelUtils.getQueryLanguages(res.getJasperConfiguration()));

		}
		UIUtil.createLabel(composite, Messages.RDQueryPage_query);

		tsql = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumHeight = 100;
		gd.widthHint = 400;
		tsql.setLayoutData(gd);

		return composite;
	}

	@Override
	protected void rebind() {
		ResourceDescriptor r = res.getValue();
		if (clang != null && !clang.isDisposed()) {
			bindingContext.bindValue(
					WidgetProperties.text().observe(clang),
					PojoProperties.value("language").observe(getProxy(r))); //$NON-NLS-1$
		}
		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(tsql),
				PojoProperties.value("sql").observe(r), //$NON-NLS-1$
				new UpdateValueStrategy().setAfterConvertValidator(new EmptyStringValidator()), null);
	}

	@Override
	public boolean isPageComplete() {
		if (tsql == null || tsql.isDisposed() || tsql.getText() == null || Misc.isNullOrEmpty(tsql.getText().trim()))
			return false;
		return super.isPageComplete();
	}

	private QProxy getProxy(ResourceDescriptor rd) {
		if (proxy == null)
			proxy = new QProxy();
		proxy.setResourceDescriptor(rd);
		return proxy;
	}

	public static class QProxy {
		private ResourceDescriptor rd;

		public void setResourceDescriptor(ResourceDescriptor rd) {
			this.rd = rd;
		}

		public void setLanguage(String lang) {
			lang = ModelUtils.getLanguage(lang);
			rd.setResourceProperty(ResourceDescriptor.PROP_QUERY_LANGUAGE, lang);
		}

		public String getLanguage() {
			return rd.getResourcePropertyValue(ResourceDescriptor.PROP_QUERY_LANGUAGE);
		}
	}
}
