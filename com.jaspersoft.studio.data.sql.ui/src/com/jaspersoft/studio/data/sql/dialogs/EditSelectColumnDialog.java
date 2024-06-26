/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.dialogs;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.data.sql.model.query.AMKeyword;
import com.jaspersoft.studio.data.sql.model.query.select.MSelectColumn;
import com.jaspersoft.studio.data.sql.text2model.ConvertUtil;
import com.jaspersoft.studio.data.sql.validator.ColumnAliasStringValidator;
import com.jaspersoft.studio.utils.UIUtil;

import net.sf.jasperreports.eclipse.ui.ATitledDialog;
import net.sf.jasperreports.eclipse.ui.validator.ValidatorUtil;

public class EditSelectColumnDialog extends ATitledDialog {
	private MSelectColumn value;
	private String alias;
	private String aliasKeyword;
	private Text talias;
	private Combo keyword;

	public EditSelectColumnDialog(Shell parentShell) {
		super(parentShell);
		setTitle("Column Dialog");
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

	}

	public void setValue(MSelectColumn value) {
		this.value = value;
		setAlias(value.getAlias());
		setAliasKeyword(value.getAliasKeyword());
	}

	public void setAliasKeyword(String aliasKeyword) {
		this.aliasKeyword = aliasKeyword;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAliasKeyword() {
		return aliasKeyword;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cmp = (Composite) super.createDialogArea(parent);
		cmp.setLayout(new GridLayout(3, false));

		Label lbl = new Label(cmp, SWT.NONE);
		lbl.setText(ConvertUtil.cleanDbNameFull(value.getValue().toSQLString()));
		UIUtil.setBold(lbl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 300;
		lbl.setLayoutData(gd);

		keyword = new Combo(cmp, SWT.READ_ONLY);
		keyword.setItems(AMKeyword.ALIAS_KEYWORDS);

		talias = new Text(cmp, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 100;
		gd.horizontalIndent = 8;
		talias.setLayoutData(gd);

		DataBindingContext bindingContext = new DataBindingContext();
		bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(talias),
				PojoProperties.value("alias").observe(this)); //$NON-NLS-1$
		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(keyword),
				PojoProperties.value("aliasKeyword").observe(this), //$NON-NLS-1$
				new UpdateValueStrategy().setAfterConvertValidator(new ColumnAliasStringValidator()), null);
		return cmp;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control createButtonBar = super.createButtonBar(parent);

		DataBindingContext bindingContext = new DataBindingContext();
		Binding b = bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(talias),
				PojoProperties.value("alias").observe(this), //$NON-NLS-1$
				new UpdateValueStrategy().setAfterConvertValidator(new ColumnAliasStringValidator()), null);
		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(keyword),
				PojoProperties.value("aliasKeyword").observe(this)); //$NON-NLS-1$

		ValidatorUtil.controlDecorator(b, getButton(IDialogConstants.OK_ID));
		return createButtonBar;
	}
}
