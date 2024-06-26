/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.widgets.scalar;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.data.designer.AQueryDesigner;
import com.jaspersoft.studio.data.sql.model.query.operand.ScalarOperand;

public class NumberWidget extends AScalarWidget {
	private Text txt;

	public NumberWidget(Composite parent, ScalarOperand<Number> operand, AQueryDesigner designer) {
		super(parent, SWT.NONE, operand, designer);

	}

	@Override
	protected void createWidget(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 2;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		setLayout(layout);

		txt = new Text(this, SWT.BORDER);
		txt.setText(getValue().toSQLString());
		txt.setToolTipText(getValue().toSQLString());
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.widthHint = 100;
		txt.setLayoutData(gd);

		DataBindingContext bindingContext = new DataBindingContext();

		IValidator numberValidator = new IValidator() {

			@Override
			public IStatus validate(Object value) {
				String s = String.valueOf(value);
				boolean matches = s
						.matches("^(-?0[.]\\d+)$|^(-?[1-9]+\\d*([.]\\d+)?)$|^0$");
				if (matches) {
					return ValidationStatus.ok();
				}
				return ValidationStatus.error("Only Number permitted");
			}
		};
		UpdateValueStrategy targetToModel = new UpdateValueStrategy();
		targetToModel.setBeforeSetValidator(numberValidator);
		Binding bindValue = bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(txt),
				PojoProperties.value("value").observe(getValue()),
				targetToModel, null);

		ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);
	}
}
