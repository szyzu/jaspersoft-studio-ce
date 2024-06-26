/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section.widgets;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.property.descriptor.combo.RWComboBoxPropertyDescriptor;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.UIUtil;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.Misc;

public class SPRWCombo<T extends IPropertyDescriptor> extends ASPropertyWidget<T> {

	protected CCombo combo;

	protected APropertyNode pnode;

	public SPRWCombo(Composite parent, AbstractSection section, T pDescriptor) {
		super(parent, section, pDescriptor);
	}

	@Override
	public Control getControl() {
		return combo;
	}

	protected boolean refresh = false;

	protected void createComponent(Composite parent) {
		combo = new CCombo(parent, SWT.FLAT | SWT.BORDER) {
			
			@Override
			protected void checkSubclass() {
			}
			
			/**
			 * Assuring that the width has an hint in case of grid layout, doing this will force the
			 * combo to not grow too much depending on the text content 
			 */
			@Override
			public void setLayoutData(Object layoutData) {
				Object newData = layoutData;
				if (newData instanceof GridData) {
					GridData newGridData = (GridData)newData;
					if (newGridData.grabExcessHorizontalSpace && newGridData.horizontalAlignment == SWT.FILL && newGridData.widthHint == SWT.DEFAULT) {
						int w = getCharWidth(this) * 15;
						if (w > 50) w = 50;
						newGridData.widthHint = w;
					}
				}
				super.setLayoutData(newData);
			}
			
		};
		if (parent.getLayout() instanceof GridLayout) {
			GridData gd = new GridData();
			gd.minimumWidth = 100;
			combo.setLayoutData(gd);
		}
		setNewItems((RWComboBoxPropertyDescriptor) pDescriptor);
		combo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (refresh)
					return;
				if (combo.getSelectionIndex() >= 0) {
					section.changeProperty(pDescriptor.getId(), combo.getItem(combo.getSelectionIndex()));
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		combo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (refresh)
					return;
				section.changeProperty(pDescriptor.getId(), combo.getText());
			}
		});
		combo.setToolTipText(pDescriptor.getDescription());
	}
	
	@Override
	public void setData(APropertyNode pnode, Object resolvedValue, Object elementValue) {
		if (elementValue == null) {
			combo.setForeground(UIUtils.INHERITED_COLOR);
			combo.setToolTipText(Messages.common_inherited_attribute + pDescriptor.getDescription());
			if (getLabel() != null) {
				getLabel().setToolTipText(Messages.common_inherited_attribute + pDescriptor.getDescription());
				getLabel().setForeground(UIUtils.INHERITED_COLOR);
			}
		} else {
			combo.setForeground(UIUtil.getColor(JFacePreferences.INFORMATION_FOREGROUND_COLOR));
			combo.setToolTipText(pDescriptor.getDescription());
			if (getLabel() != null) {
				getLabel().setToolTipText(pDescriptor.getDescription());
				getLabel().setForeground(UIUtil.getColor(JFacePreferences.INFORMATION_FOREGROUND_COLOR));
			}
		}
		setData(pnode, resolvedValue);
	}

	public void setData(APropertyNode pnode, Object b) {
		createContextualMenu(pnode);
		refresh = true;
		this.pnode = pnode;
		final RWComboBoxPropertyDescriptor pd = (RWComboBoxPropertyDescriptor) pDescriptor;
		String str = (String) b;
		setComboSelection(str, pd.isCaseSensitive());
		combo.setEnabled(pnode.isEditable());
		refresh = false;
	}

	protected void setComboSelection(String str, boolean isCaseSensitive) {
		Point oldSel = combo.getSelection();
		int oldLenght = combo.getText().length();
		String[] items = combo.getItems();
		int selection = -1;
		for (int i = 0; i < items.length; i++) {
			if (Misc.compare(items[i], str, isCaseSensitive)) {
				selection = i;
				break;
			}
		}
		if (selection != -1){
			combo.select(selection);
			combo.setText(items[selection]);
		}else
			combo.setText(Misc.nvl(str));
		String t = combo.getText();
		// if (oldSel.x != oldSel.y) {
		if (oldSel.y - oldSel.x == t.length()) {
			int stringLength = t.length();
			combo.setSelection(new Point(stringLength, stringLength));
		} else {
			int stringLength = Math.min(t.length() - (oldLenght - oldSel.y), t.length());
			combo.setSelection(new Point(stringLength, stringLength));
		}
		combo.getParent().layout(true);
	}

	/**
	 * Return the items inside the combo control
	 * 
	 * @return a list of string
	 */
	public String[] getItems() {
		return combo.getItems();
	}

	public void setNewItems(final RWComboBoxPropertyDescriptor pd) {
		// Block the update and reset the previously selected item
		refresh = true;
		String[] newItems = pd.getItems();
		if (!ModelUtils.safeEquals(newItems, combo.getItems())) {
			String oldSelection = combo.getText();
			combo.setItems(newItems);
			setComboSelection(oldSelection, pd.isCaseSensitive());
		}	
		refresh = false;
	}
}
