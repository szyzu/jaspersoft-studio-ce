/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.jface.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.IExpressionContextSetter;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.dataset.IEditableDataset;
import com.jaspersoft.studio.model.dataset.IEditableDatasetRun;
import com.jaspersoft.studio.property.dataset.DatasetRunSelectionListener;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.swt.widgets.WTextExpression;
import com.jaspersoft.studio.utils.EnumHelper;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.DatasetResetTypeEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;

/**
 * This generic composite can be reused in dialogs/wizards when there is the
 * need to edit the dataset information of a report element.<br>
 * A {@link IEditableDataset} instance is used to modify all the dataset
 * information plus the dataset run one.
 * 
 * @author mrabbi
 * 
 * @see IEditableDataset
 * @see DatasetRunBaseComposite
 * 
 */
public abstract class EditableDatasetBaseComposite extends Composite implements IExpressionContextSetter {

	private IEditableDataset datasetInstance;
	private Combo comboResetType;
	private Combo comboResetGroup;
	private Combo comboIncrementType;
	private Combo comboIncrementGroup;
	private WTextExpression filterExpression;
	private List<DatasetRunSelectionListener> dsRunSelectionListeners;
	private DatasetRunBaseComposite datasetRunContent;

	public void setDatasetInstance(IEditableDataset datasetInstance) {
		this.datasetInstance = datasetInstance;
	}

	public EditableDatasetBaseComposite(IEditableDataset datasetInst, Composite parent, int style) {
		super(parent, SWT.NONE);
		this.dsRunSelectionListeners = new ArrayList<>();
		this.datasetInstance = datasetInst;
		this.setLayout(new GridLayout(2, true));

		// Dataset composite content
		Label lblResetType = new Label(this, SWT.NONE);
		lblResetType.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblResetType.setText(Messages.EditableDatasetBaseComposite_ResetTypeLbl);

		Label lblResetGroup = new Label(this, SWT.NONE);
		lblResetGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		lblResetGroup.setBounds(0, 0, 59, 14);
		lblResetGroup.setText(Messages.EditableDatasetBaseComposite_ResetGroupLbl);

		comboResetType = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		comboResetType.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		comboResetType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selIndex = ((Combo) e.widget).getSelectionIndex();
				updateResetTypeInformation(selIndex);
			}

		});

		comboResetGroup = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		comboResetGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		comboResetGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selIndex = ((Combo) e.widget).getSelectionIndex();
				updateResetGroupInformation(selIndex);
			}

		});

		Label lblIncrementType = new Label(this, SWT.NONE);
		lblIncrementType.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		lblIncrementType.setText(Messages.EditableDatasetBaseComposite_IncrementTypeLbl);

		Label lblIncrementGroup = new Label(this, SWT.NONE);
		lblIncrementGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		lblIncrementGroup.setText(Messages.EditableDatasetBaseComposite_IncrementGroupLbl);

		comboIncrementType = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		comboIncrementType.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		comboIncrementType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selIndex = ((Combo) e.widget).getSelectionIndex();
				updateIncrementTypeInformation(selIndex);
			}

		});

		comboIncrementGroup = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		comboIncrementGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		comboIncrementGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selIndex = ((Combo) e.widget).getSelectionIndex();
				updateIncrementGroupInformation(selIndex);
			}

		});

		filterExpression = new WTextExpression(this, SWT.NONE,
				Messages.EditableDatasetBaseComposite_FilterExprWidgetLbl, WTextExpression.LABEL_ON_TOP) {
			@Override
			public void setExpression(JRDesignExpression exp) {
				super.setExpression(exp);
				datasetInstance.setIncrementWhenExpression(exp);
			}
		};
		GridData gdFilterExpression = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		filterExpression.setLayoutData(gdFilterExpression);

		// Dataset run information
		Group grpDatasetRun = new Group(this, SWT.NONE);
		grpDatasetRun.setText(Messages.EditableDatasetBaseComposite_DatasetRunGroupTitle);
		grpDatasetRun.setLayout(new GridLayout(1, true));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		grpDatasetRun.setLayoutData(gd);

		datasetRunContent = new DatasetRunBaseComposite(getEditableDatesetRun(), grpDatasetRun, SWT.NONE);
		datasetRunContent.addDatasetRunSelectionListener(this::notifyDatasetRunSelectionChanged);

		initWidgets();
	}

	/*
	 * Inits all the components inside the composite mask.
	 */
	private void initWidgets() {
		if (datasetInstance.getJRElementDataset() == null)
			return;
		// Select the reset type value
		comboResetType.setItems(EnumHelper.getEnumNames(ResetTypeEnum.values(), NullEnum.NOTNULL));
		comboResetType.select(this.datasetInstance.getJRElementDataset().getDatasetResetType().ordinal());
		// Select the reset type group, if needed
		comboResetGroup.setItems(new String[0]);
		comboResetGroup.setEnabled(false);
		if (this.datasetInstance.getJRElementDataset().getDatasetResetType() == DatasetResetTypeEnum.GROUP) {
			fillGroupCombo(comboResetGroup);
			JRGroup resetGroup = this.datasetInstance.getJRElementDataset().getResetGroup();
			if (resetGroup != null) {
				String currentGroupName = resetGroup.getName();
				for (int i = 0; i < comboResetGroup.getItemCount(); i++) {
					if (comboResetGroup.getItem(i).equals(currentGroupName)) {
						comboResetGroup.select(i);
						break;
					}
				}
			}
		}
		// Select the increment type value
		comboIncrementType.setItems(EnumHelper.getEnumNames(IncrementTypeEnum.values(), NullEnum.NOTNULL));
		comboIncrementType.select(this.datasetInstance.getJRElementDataset().getIncrementTypeValue().ordinal());
		// Select the increment group, if needed
		comboIncrementGroup.setItems(new String[0]);
		comboIncrementGroup.setEnabled(false);
		if (this.datasetInstance.getJRElementDataset().getIncrementTypeValue() == IncrementTypeEnum.GROUP) {
			fillGroupCombo(comboIncrementGroup);
			JRGroup incrementGroup = this.datasetInstance.getJRElementDataset().getIncrementGroup();
			if (incrementGroup != null) {
				String currentGroupName = incrementGroup.getName();
				for (int i = 0; i < comboIncrementGroup.getItemCount(); i++) {
					if (comboIncrementGroup.getItem(i).equals(currentGroupName)) {
						comboIncrementGroup.select(i);
						break;
					}
				}
			}
		}
		// Filter (increment when) expression
		filterExpression.setExpression(
				(JRDesignExpression) this.datasetInstance.getJRElementDataset().getIncrementWhenExpression());
	}

	/**
	 * Gets the {@link IEditableDatasetRun} instance for the dataset currently
	 * being edited.
	 * 
	 * @return the editable dataset run instance
	 */
	protected abstract IEditableDatasetRun getEditableDatesetRun();

	/**
	 * Gets the {@link IEditableDataset} instance currently being edited inside
	 * this composite.
	 * 
	 * @return the editable dataset instance
	 */
	protected IEditableDataset getEditableDataset() {
		return this.datasetInstance;
	}

	protected List<JRGroup> getCurrentGroupsList() {
		JasperDesign jd = datasetInstance.getJasperDesign();
		JRDataset currentDataset = jd.getMainDataset();
		JRDatasetRun darasetRun = datasetInstance.getJRElementDataset().getDatasetRun();
		if (darasetRun != null && jd.getDatasetMap().containsKey(darasetRun.getDatasetName())) {
			currentDataset = jd.getDatasetMap().get(darasetRun.getDatasetName());
		}
		return ((JRDesignDataset) currentDataset).getGroupsList();
	}

	/*
	 * Fills the a group combo box. Returns true if the combo filling operation
	 * was successful, false otherwise.
	 */
	private boolean fillGroupCombo(Combo widget) {
		List<JRGroup> groupsList = getCurrentGroupsList();
		if (groupsList == null || groupsList.isEmpty()) {
			MessageDialog.openWarning(getShell(), Messages.EditableDatasetBaseComposite_NoGroupsErrTitle,
					Messages.EditableDatasetBaseComposite_NoGroupsErrMsg);
			widget.setItems(new String[0]);
			widget.setEnabled(false);
			return false;
		} else {
			List<String> groupLst = new ArrayList<>();
			for (JRGroup g : groupsList) {
				groupLst.add(g.getName());
			}
			widget.setEnabled(true);
			widget.setItems(groupLst.toArray(new String[] {}));
			return true;
		}
	}

	/*
	 * Updates the dataset information with the reset group data.
	 */
	private void updateResetGroupInformation(int selIndex) {
		JRGroup jrGroup = getCurrentGroupsList().get(selIndex);
		datasetInstance.setResetGroup(jrGroup);
	}

	/*
	 * Updates the dataset information with the reset type data.
	 */
	private void updateResetTypeInformation(int selIndex) {
		DatasetResetTypeEnum selectedResType = EnumHelper.getEnumByObjectValue(DatasetResetTypeEnum.values(),
				comboResetType.getText());
		if (selectedResType == DatasetResetTypeEnum.GROUP) {
			if (fillGroupCombo(comboResetGroup)) {
				// force the selection of the first group
				comboResetGroup.select(0);
				updateResetGroupInformation(0);
			}
		} else {
			datasetInstance.setResetGroup(null);
			this.comboResetGroup.setEnabled(false);
			this.comboResetGroup.setItems(new String[0]);
		}
		datasetInstance.setResetType(selectedResType);
	}

	/*
	 * Updates the dataset information with the increment group data.
	 */
	private void updateIncrementGroupInformation(int selIndex) {
		JRGroup jrGroup = getCurrentGroupsList().get(selIndex);
		datasetInstance.setIncrementGroup(jrGroup);
	}

	/*
	 * Updates the dataset information with the increment type data.
	 */
	private void updateIncrementTypeInformation(int selIndex) {
		IncrementTypeEnum selectedIncrType = EnumHelper.getEnumByObjectValue(IncrementTypeEnum.values(),
				comboIncrementType.getText());
		if (selectedIncrType == IncrementTypeEnum.GROUP) {
			if (fillGroupCombo(comboIncrementGroup)) {
				// force the selection of the first group
				comboIncrementGroup.select(0);
				updateIncrementGroupInformation(0);
			}
		} else {
			datasetInstance.setResetGroup(null);
			this.comboIncrementGroup.setEnabled(false);
			this.comboIncrementGroup.setItems(new String[0]);
		}
		datasetInstance.setIncrementType(selectedIncrType);
	}

	public void addDatasetRunSelectionListener(DatasetRunSelectionListener listener) {
		dsRunSelectionListeners.add(listener);
	}

	public void removeDatasetRunSelectionListener(DatasetRunSelectionListener listener) {
		dsRunSelectionListeners.remove(listener);
	}

	private void notifyDatasetRunSelectionChanged() {
		for (DatasetRunSelectionListener l : dsRunSelectionListeners) {
			l.selectionChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.editor.expression.IExpressionContextSetter#
	 * setExpressionContext(com.jaspersoft.studio.editor
	 * .expression.ExpressionContext)
	 */
	public void setExpressionContext(ExpressionContext expContext) {
		// Expression context from dataset run information
		this.filterExpression.setExpressionContext(expContext);
	}

	/**
	 * Sets the default expression context needed for the dataset run composite
	 * content.
	 * 
	 * @param expContext the expression context to set
	 */
	public void setDefaultExpressionContext(ExpressionContext expContext) {
		this.datasetRunContent.setExpressionContext(expContext);
	}
}
