/*
 * JasperReports - Free Java Reporting Library. Copyright (C) 2001 - 2012 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program is part of JasperReports.
 * 
 * JasperReports is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * JasperReports is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with JasperReports. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.server.wizard.resource.page;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.server.model.MDataType;
import com.jaspersoft.studio.utils.UIUtils;

public class RDDataTypePage extends AResourcePage {

	public RDDataTypePage(ANode parent, MDataType resource) {
		super("rddatatype", parent, resource);
		setTitle("Data Type");
		setDescription("Data Type");
	}

	@Override
	protected void createTabs(TabFolder tabFolder) {
		super.createTabs(tabFolder);
		createReferenceTab(tabFolder);
	}

	protected void createReferenceTab(TabFolder tabFolder) {
		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Data Type");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		item.setControl(composite);

		UIUtils.createLabel(composite, "Data Type");

		CCombo ttype = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
		ttype.setItems(new String[] { "Text", "Number", "Date", "Date/time" });

		UIUtils.createLabel(composite, "Pattern");

		Text tpattern = new Text(composite, SWT.BORDER);
		tpattern.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtils.createLabel(composite, "Min Value");

		Text tmin = new Text(composite, SWT.BORDER);
		tmin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtils.createLabel(composite, "");

		Button bmin = new Button(composite, SWT.CHECK);
		bmin.setText("Is Strict Minimum");
		bmin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtils.createLabel(composite, "Max Value");

		Text tmax = new Text(composite, SWT.BORDER);
		tmax.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtils.createLabel(composite, "");

		Button bmax = new Button(composite, SWT.CHECK);
		bmax.setText("Is Strict Maximum");
		bmax.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		bindingContext.bindValue(
				SWTObservables.observeText(tpattern, SWT.Modify),
				PojoObservables.observeValue(res.getValue(), "pattern"));
		bindingContext.bindValue(SWTObservables.observeText(tmin, SWT.Modify),
				PojoObservables.observeValue(res.getValue(), "minValue"));
		bindingContext.bindValue(SWTObservables.observeText(tmax, SWT.Modify),
				PojoObservables.observeValue(res.getValue(), "maxValue"));
		bindingContext.bindValue(SWTObservables.observeSelection(bmin),
				PojoObservables.observeValue(res.getValue(), "strictMin"));
		bindingContext.bindValue(SWTObservables.observeSelection(bmax),
				PojoObservables.observeValue(res.getValue(), "strictMax"));

		bindingContext.bindValue(SWTObservables
				.observeSingleSelectionIndex(ttype), PojoObservables
				.observeValue(getProxy(res.getValue()), "dataType"));
	}

	private ShiftProxy getProxy(ResourceDescriptor rd) {
		proxy.setResourceDescriptor(rd);
		return proxy;
	}

	private ShiftProxy proxy = new ShiftProxy();

	class ShiftProxy {
		private ResourceDescriptor rd;
		private final int shift = 1;

		public void setResourceDescriptor(ResourceDescriptor rd) {
			this.rd = rd;
		}

		public void setDataType(int type) {
			rd.setDataType((byte) (type + shift));
		}

		public int getDataType() {
			return rd.getDataType() - shift;
		}
	}

}
