/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.dialogs;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

import com.jaspersoft.studio.data.sql.QueryWriter;
import com.jaspersoft.studio.data.sql.messages.Messages;
import com.jaspersoft.studio.data.sql.model.query.AMKeyword;
import com.jaspersoft.studio.data.sql.model.query.from.MFrom;
import com.jaspersoft.studio.data.sql.model.query.from.MFromTable;
import com.jaspersoft.studio.data.sql.model.query.subquery.MQueryTable;
import com.jaspersoft.studio.data.sql.text2model.ConvertUtil;
import com.jaspersoft.studio.data.sql.validator.TableAliasStringValidator;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.util.ModelVisitor;
import com.jaspersoft.studio.utils.UIUtil;

import net.sf.jasperreports.eclipse.ui.ATitledDialog;
import net.sf.jasperreports.eclipse.ui.validator.ValidatorUtil;

public class EditFromTableDialog extends ATitledDialog {
	private MFromTable mFromTable;
	private String alias;
	private String aliasKeyword;
	private Text talias;
	private Combo keyword;

	public EditFromTableDialog(Shell parentShell) {
		super(parentShell);
		setTitle(Messages.EditFromTableDialog_0);
	}

	public void setValue(MFromTable value) {
		this.mFromTable = value;
		setAlias(value.getAlias());
		setAliasKeyword(value.getAliasKeyword());

		if (value.getValue() instanceof MQueryTable)
			setTitle(Messages.EditFromTableDialog_2);
		else
			setTitle(Messages.EditFromTableDialog_0);
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

		if (mFromTable.getValue() instanceof MQueryTable) {
			Label lbl = new Label(cmp, SWT.READ_ONLY);
			UIUtil.setBold(lbl);
			lbl.setText(Messages.EditFromTableDialog_3);
			lbl.setToolTipText(QueryWriter.writeSubQuery(mFromTable));
		} else {
			Text lbl = new Text(cmp, SWT.BORDER | SWT.READ_ONLY);
			lbl.setText(ConvertUtil.cleanDbNameFull(mFromTable.getValue()
					.toSQLString()));
			lbl.setToolTipText(lbl.getText());
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.widthHint = 200;
			lbl.setLayoutData(gd);
		}

		keyword = new Combo(cmp, SWT.READ_ONLY);
		keyword.setItems(AMKeyword.ALIAS_KEYWORDS);

		talias = new Text(cmp, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 200;
		gd.horizontalIndent = 8;
		talias.setLayoutData(gd);

		return cmp;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control createButtonBar = super.createButtonBar(parent);

		DataBindingContext bindingContext = new DataBindingContext();
		Binding b = bindingContext.bindValue(
				WidgetProperties.text(SWT.Modify).observe(talias),				
				PojoProperties.value("alias").observe(this), //$NON-NLS-1$
				new UpdateValueStrategy().setAfterConvertValidator(new TableAliasStringValidator() {
					@Override
					public IStatus validate(final Object value) {
						IStatus status = super.validate(value);
						if (status.equals(Status.OK_STATUS) && value != null && !((String) value).isEmpty()) {
							ModelVisitor<Boolean> mv = new ModelVisitor<Boolean>(mFromTable.getRoot()) {
								@Override
								public boolean visit(INode n) {
									if (n instanceof MFrom || n instanceof MFromTable) {
										if (n instanceof MFromTable && n != mFromTable) {
											String al = ((MFromTable) n).getAlias();
											if (al != null && al.equals(value)) {
												setObject(Boolean.TRUE);
												return false;
											}
										}
										return true;
									}
									return false;
								}
							};
							if (mv.getObject() != null && mv.getObject() == true)
								return ValidationStatus.error(Messages.EditFromTableDialog_1);
						}
						return status;
					}
				}), null);
		bindingContext.bindValue(
				WidgetProperties.widgetSelection().observe(keyword),
				PojoProperties.value("aliasKeyword").observe(this)); //$NON-NLS-1$

		ValidatorUtil.controlDecorator(b, getButton(IDialogConstants.OK_ID));
		return createButtonBar;
	}
}
