/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.dialogs;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.jaspersoft.studio.data.sql.model.query.expression.MExpressionPNot;
import com.jaspersoft.studio.messages.Messages;

import net.sf.jasperreports.eclipse.ui.ATitledDialog;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignParameter;

public class EditPNotExpressionDialog extends ATitledDialog {
	private MExpressionPNot value;
	private JRDesignParameter prm;

	public EditPNotExpressionDialog(Shell parentShell) {
		super(parentShell);
		setTitle("Expression ${X} Dialog");
		setDescription("$P!{} will be replaced in the query as is.");
	}

	public void setValue(MExpressionPNot value) {
		this.value = value;
		prm = value.getValue();
	}

	public JRDesignParameter getValue() {
		return prm;
	}

	public String getPrm() {
		return prm.getName();
	}

	public void setPrm(String name) {
		JRDesignDataset ds = value.getRoot().getValue();
		prm = (JRDesignParameter) ds.getParametersMap().get(name);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cmp = (Composite) super.createDialogArea(parent);
		cmp.setLayout(new GridLayout(5, false));

		DataBindingContext bindingContext = new DataBindingContext();

		new Label(cmp, SWT.NONE).setText(Messages.CreateParameterCommand_parameter_name);

		Combo operator = new Combo(cmp, SWT.READ_ONLY);
		JRDesignDataset ds = value.getRoot().getValue();
		List<JRParameter> prms = ds.getParametersList();
		String[] items = new String[prms.size()];
		for (int i = 0; i < items.length; i++)
			items[i] = prms.get(i).getName();
		operator.setItems(items);
		operator.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(operator),
				PojoProperties.value("prm").observe(this)); //$NON-NLS-1$
		return cmp;
	}
}
