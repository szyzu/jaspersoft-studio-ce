/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.gef.decorator.json;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.editor.action.json.JSONPathDataAction;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.text.MStaticText;
import com.jaspersoft.studio.model.text.MTextElement;
import com.jaspersoft.studio.model.text.MTextField;
import com.jaspersoft.studio.swt.widgets.WTextExpression;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.eclipse.ui.ATitledDialog;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;

/**
 * A simple dialog to ask a string value to the user
 * 
 * @author Veaceslav Chicu
 * 
 */
public class PathAndDataDialog extends ATitledDialog {

	private MTextElement mtext;
	/**
	 * Contain the value of inserted in the textfield after the button "ok" is
	 * pressed. This field is used because after the button ok is pressed then
	 * all the widget in the dialog are automatically disposed, and so the
	 * content of the text field need to be saved here
	 */
	private String path;
	private JRExpression data;
	private boolean repeat = false;
	private WTextExpression wtExp;
	private Button bRepeat;

	/**
	 * Build the dialog with a title and an initial value for the text field
	 * 
	 * @param parentShell
	 * @param mtext
	 */
	public PathAndDataDialog(Shell parentShell, MTextElement mtext) {
		super(parentShell);
		this.mtext = mtext;
		setTitle(Messages.PathAndDataDialog_0);
		setDescription(Messages.PathAndDataDialog_1);
		JRDesignElement v = mtext.getValue();
		path = v.getPropertiesMap().getProperty(JSONPathDataAction.JSON_EXPORTER_PATH_PROPERTY);
		if (path == null) {
			if (mtext instanceof MTextField) {
				JRDesignTextField tf = (JRDesignTextField) v;
				if (tf.getExpression() != null) {
					path = tf.getExpression().getText().trim();
					if (path.startsWith("$F{") || path.startsWith("$P{") || path.startsWith("$V{")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						int ind = path.indexOf("}"); //$NON-NLS-1$
						path = ind > 0 ? path.substring(3, ind) : path.substring(3);
					} else
						path = "json_path"; //$NON-NLS-1$
				}
			} else if (mtext instanceof MStaticText) {
				JRDesignStaticText tf = (JRDesignStaticText) v;
				path = tf.getText();
			}
		}
		repeat = Misc.nvl(
				Boolean.valueOf(
						v.getPropertiesMap().getProperty(JSONPathDataAction.JSON_EXPORTER_REPEAT_VALUE_PROPERTY)),
				false);
		JRPropertyExpression[] pExpr = v.getPropertyExpressions();
		if (pExpr != null)
			for (JRPropertyExpression pe : pExpr) {
				if (pe.getName().equals(JSONPathDataAction.JSON_EXPORTER_DATA_PROPERTY)) {
					if (pe.getValueExpression() != null)
						data = pe.getValueExpression();
					break;
				}
			}
	}

	/**
	 * Return the value in the text field after the button ok is pressed, or the
	 * value used to initialize the textfield before the button ok is pressed
	 */
	public String getName() {
		if (Misc.isNullOrEmpty(path))
			return null;
		return path;
	}

	public JRExpression getData() {
		if (Misc.isNullOrEmpty(path))
			return null;
		return data;
	}

	public boolean isRepeat() {
		return repeat;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite cmp = new Composite(area, SWT.NONE);
		cmp.setLayout(new GridLayout(2, false));
		cmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(cmp, SWT.NONE).setText(Messages.PathAndDataDialog_7);

		Text tPath = new Text(cmp, SWT.BORDER);
		tPath.setText(Misc.nvl(path));
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd.widthHint = 200;
		gd.horizontalSpan = 1;
		tPath.setLayoutData(gd);
		tPath.addModifyListener(e -> {
			path = tPath.getText();
			validate(path);
			setEnabledWidgets(path);
		});

		new Label(cmp, SWT.NONE);
		Label lbl = new Label(cmp, SWT.NONE);
		lbl.setText(Messages.PathAndDataDialog_8);

		bRepeat = new Button(cmp, SWT.CHECK);
		bRepeat.setText(Messages.PathAndDataDialog_9);
		bRepeat.setSelection(repeat);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		bRepeat.setLayoutData(gd);
		bRepeat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				repeat = bRepeat.getSelection();
			}
		});

		bUseExp = new Button(cmp, SWT.CHECK);
		bUseExp.setText(Messages.PathAndDataDialog_10);
		bRepeat.setSelection(repeat);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		bUseExp.setLayoutData(gd);
		bUseExp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				wtExp.setEnabled(bUseExp.getSelection());
				if (!bUseExp.getSelection()) {
					data = null;
					wtExp.setExpression(null);
				}
			}
		});
		bUseExp.setSelection(data != null);

		wtExp = new WTextExpression(cmp, SWT.NONE, Messages.PathAndDataDialog_11, WTextExpression.LABEL_ON_LEFT, 3);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		wtExp.setLayoutData(gd);
		Object expContextAdapter = mtext.getAdapter(ExpressionContext.class);
		if (expContextAdapter != null)
			wtExp.setExpressionContext((ExpressionContext) expContextAdapter);
		else
			wtExp.setExpressionContext(ModelUtils.getElementExpressionContext(mtext.getValue(), mtext));
		wtExp.setExpression((JRDesignExpression) data);
		wtExp.addModifyListener(event -> data = wtExp.getExpression());

		setEnabledWidgets(path);
		UIUtils.getDisplay().asyncExec(() -> validate(path));

		return area;
	}

	private List<JRDesignElement> tels;
	private Button bUseExp;

	private void validate(String path) {
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		setError(""); //$NON-NLS-1$
		if (Misc.isNullOrEmpty(path)) {
			JRPropertiesMap map = mtext.getValue().getPropertiesMap();
			if (map != null && map.getProperty(JSONPathDataAction.JSON_EXPORTER_PATH_PROPERTY) != null)
				setError(Messages.PathAndDataDialog_13);
			else {
				setError(Messages.PathAndDataDialog_14);
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
			return;
		}
		if (tels == null) {
			tels = new ArrayList<>();
			List<JRDesignElement> els = ModelUtils.getAllGElements(mtext.getJasperDesign());
			for (JRDesignElement de : els)
				if (de instanceof JRTextElement && de != mtext.getValue())
					tels.add(de);
		}
		for (JRDesignElement te : tels) {
			JRPropertiesMap map = te.getPropertiesMap();
			if (map != null) {
				String name = map.getProperty(JSONPathDataAction.JSON_EXPORTER_PATH_PROPERTY);
				if (name != null && name.equals(path)) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setError(Messages.PathAndDataDialog_15);
					break;
				}
			}
		}
	}

	private void setEnabledWidgets(String path) {
		bRepeat.setEnabled(!Misc.isNullOrEmpty(path));
		wtExp.setEnabled(!Misc.isNullOrEmpty(path) && bUseExp.getSelection());
		bUseExp.setEnabled(!Misc.isNullOrEmpty(path));
	}
}
