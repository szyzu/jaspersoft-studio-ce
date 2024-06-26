/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.customvisualization.properties;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.jaspersoft.studio.components.customvisualization.messages.Messages;
import com.jaspersoft.studio.components.customvisualization.model.CVItemDataDescriptor;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.swt.widgets.NumberedLabelProvider;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.components.items.ItemData;
import net.sf.jasperreports.customvisualization.design.CVDesignComponent;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;

/**
 * Widget to modify the {@link CVDesignComponent#PROPERTY_ITEM_DATA} property in
 * the dedicated Property section.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public class SPCVItemDataList extends ASPropertyWidget<CVItemDataDescriptor> {

	private TableViewer itemDataTV;
	private Button btnAddItemData;
	private Button btnModifyItemData;
	private Button btnRemoveItemData;
	private Group itemDataGrp;
	private List<ItemData> itemDataElements;
	private NumberedLabelProvider lblProv;

	public SPCVItemDataList(Composite parent, AbstractSection section,
			CVItemDataDescriptor pDescriptor) {
		super(parent, section, pDescriptor);
	}

	@Override
	protected void createComponent(Composite parent) {
		itemDataGrp = new Group(parent, SWT.NONE);
		itemDataGrp.setLayout(new GridLayout(2, false));
		itemDataGrp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		itemDataTV = createItemDataTable(itemDataGrp);
		itemDataTV.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				modifyItemDataBtnPressed();
			}
		});

		btnAddItemData = new Button(itemDataGrp, SWT.PUSH);
		btnAddItemData.setText(Messages.SPCVItemDataList_Add);
		btnAddItemData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false));
		btnAddItemData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addNewItemDataBtnPressed();
			}
		});

		btnModifyItemData = new Button(itemDataGrp, SWT.PUSH);
		btnModifyItemData.setText(Messages.SPCVItemDataList_Edit);
		btnModifyItemData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false));
		btnModifyItemData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				modifyItemDataBtnPressed();
			}
		});

		btnRemoveItemData = new Button(itemDataGrp, SWT.PUSH);
		btnRemoveItemData.setText(Messages.SPCVItemDataList_Remove);
		btnRemoveItemData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false));
		btnRemoveItemData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeItemDataBtnPressed();
			}
		});

	}

	private TableViewer createItemDataTable(Group parent) {
		TableViewer itemDataTV = new TableViewer(parent, SWT.BORDER
				| SWT.V_SCROLL | SWT.SINGLE);
		itemDataTV.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		lblProv = new NumberedLabelProvider(
				Messages.SPCVItemDataList_LblProviderPrefix);
		itemDataTV.setLabelProvider(lblProv);
		itemDataTV.setContentProvider(new ArrayContentProvider());
		return itemDataTV;
	}

	private void addNewItemDataBtnPressed() {
		CVItemDataDialog dialog = new CVItemDataDialog(UIUtils.getShell(),
				null, section.getElement().getJasperConfiguration());
		dialog.setDefaultExpressionContext(getExpressionContext());
		if (dialog.open() == Window.OK) {
			ItemData CVItemData = dialog.getCVItemData();
			itemDataElements.add(CVItemData);
			section.changeProperty(CVDesignComponent.PROPERTY_ITEM_DATA,
					itemDataElements);
		}
	}

	private void modifyItemDataBtnPressed() {
		ItemData i = getCurrentSelectedItemData();
		if (i != null) {
			ItemData clonedItemData = (ItemData) i.clone();
			CVItemDataDialog dialog = new CVItemDataDialog(UIUtils.getShell(),
					clonedItemData, section.getElement()
							.getJasperConfiguration());
			dialog.setDefaultExpressionContext(getExpressionContext());
			if (dialog.open() == Window.OK) {
				ItemData CVItemData = dialog.getCVItemData();
				int indexOf = itemDataElements.indexOf(i);
				itemDataElements.remove(indexOf);
				itemDataElements.add(indexOf, CVItemData);
				section.changeProperty(CVDesignComponent.PROPERTY_ITEM_DATA,
						itemDataElements);
			}
		}
	}

	private void removeItemDataBtnPressed() {
		ItemData i = getCurrentSelectedItemData();
		if (i != null) {
			itemDataElements.remove(i);
			section.changeProperty(CVDesignComponent.PROPERTY_ITEM_DATA,
					itemDataElements);
		}
	}

	private ItemData getCurrentSelectedItemData() {
		Object selEl = ((IStructuredSelection) itemDataTV.getSelection())
				.getFirstElement();
		if (selEl instanceof ItemData) {
			return (ItemData) selEl;
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setData(APropertyNode pnode, Object value) {
		itemDataElements = (List<ItemData>) value;
		itemDataTV.setInput(itemDataElements);
		lblProv.resetIndex();
	}

	@Override
	public Control getControl() {
		return itemDataGrp;
	}

	private ExpressionContext getExpressionContext() {
		return ModelUtils.getElementExpressionContext(null,
				section.getElement());
	}

}
