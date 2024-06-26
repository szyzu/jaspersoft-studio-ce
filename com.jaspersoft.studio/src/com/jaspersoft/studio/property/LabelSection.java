/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.properties.view.AbstractPropertySection;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.properties.view.validation.ValidationError;

/*
 * The Class LabelSection.
 */
public class LabelSection extends AbstractPropertySection {

	/** The label text. */
	private Text labelText;

	/** The listener. */
	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent arg0) {

		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormLayout layout = new FormLayout();
		labelText = getWidgetFactory().createText(composite, "ura"); //$NON-NLS-1$
		labelText.setLayoutData(layout);
		labelText.addModifyListener(listener);

		CLabel labelLabel = getWidgetFactory().createCLabel(composite, "Label:"); //$NON-NLS-1$
		labelLabel.setLayoutData(layout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		// Object input = ((IStructuredSelection) selection).getFirstElement();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	public void refresh() {
		labelText.removeModifyListener(listener);
		labelText.setText("fdsakfjkasjfla");
		labelText.addModifyListener(listener);
	}

	@Override
	public void resetErrors() {

	}

	@Override
	public void showErrors(List<ValidationError> errors) {

	}
}
