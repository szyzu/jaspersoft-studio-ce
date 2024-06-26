/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.model.theme.paintproviders;

import java.util.Collection;
import java.util.List;

import net.sf.jasperreports.chartthemes.simple.PaintProvider;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;

public class SPPaintProviders extends ASPropertyWidget<IPropertyDescriptor> {
	protected Composite composite;

	public SPPaintProviders(Composite parent, AbstractSection section, IPropertyDescriptor pDescriptor) {
		super(parent, section, pDescriptor);
	}

	@Override
	public Control getControl() {
		return composite;
	}

	protected void createComponent(Composite parent) {
		composite = section.getWidgetFactory().createComposite(parent, SWT.READ_ONLY);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 1;
		layout.marginRight = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ftext = section.getWidgetFactory().createText(composite, "", SWT.LEFT | SWT.READ_ONLY);
		ftext.setToolTipText(pDescriptor.getDescription());
		setWidth(composite, 20);

		Button btn = section.getWidgetFactory().createButton(composite, "...", SWT.PUSH);
		btn.setToolTipText(pDescriptor.getDescription());
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PaintProvidersDialog dialog = new PaintProvidersDialog(composite.getShell());
				dialog.setValue(series);
				if (dialog.open() == Dialog.OK) {
					handleTextChanged(section, pDescriptor.getId(), dialog.getValue());
				}
			}
		});
	}

	protected void setWidth(Composite parent, int chars) {
		int w = getCharWidth(parent) * chars;
		if (parent.getLayout() instanceof RowLayout) {
			RowData rd = new RowData();
			rd.width = w;
			ftext.setLayoutData(rd);
		} else if (parent.getLayout() instanceof GridLayout) {
			GridData rd = new GridData(GridData.FILL_HORIZONTAL);
			rd.minimumWidth = w;
			ftext.setLayoutData(rd);
		}
	}

	protected void handleTextChanged(final AbstractSection section, final Object property, Collection<?> text) {
		section.changeProperty(property, text);
	}

	private List<PaintProvider> series;
	private Text ftext;

	public void setData(APropertyNode pnode, Object b) {
		series = (List<PaintProvider>) b;
		ftext.setText(pDescriptor.getLabelProvider().getText(b));
	}

}
