/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.crosstab.model.crosstab.command.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.components.Activator;
import com.jaspersoft.studio.components.crosstab.messages.Messages;
import com.jaspersoft.studio.model.field.MField;
import com.jaspersoft.studio.model.parameter.MParameter;
import com.jaspersoft.studio.model.variable.MVariable;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.combo.EditableComboBoxCellEditor;
import com.jaspersoft.studio.utils.EnumHelper;
import com.jaspersoft.studio.wizards.JSSWizard;
import com.jaspersoft.studio.wizards.fields.StaticWizardFieldsPage;

import net.sf.jasperreports.crosstabs.JRCrosstabBucket;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabBucket;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.type.CrosstabTotalPositionEnum;
import net.sf.jasperreports.engine.analytics.dataset.BucketOrder;
import net.sf.jasperreports.engine.type.SortOrderEnum;

public class CrosstabWizardColumnPage extends StaticWizardFieldsPage {

	private static final String F_CALCULATION = "CALCULATION";
	private static final String F_TOTALPOSITION = "TOTALPOSITION";
	private static final String F_ORDER = "ORDER";
	private static final String F_NAME = "NAME";

	protected static final String[] bucketOrderNames = EnumHelper.getEnumNames(BucketOrder.values(), NullEnum.NOTNULL);
	protected static final String[] crosstabTotalPositionEnumNames = EnumHelper
			.getEnumNames(CrosstabTotalPositionEnum.values(), NullEnum.NOTNULL);

	private final class TColumnLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			Wrapper w = (Wrapper) element;
			String oldExpText = w.getBaseObjectExpression();
			switch (columnIndex) {
			case 0:
				if (oldExpText.startsWith("$F{")) //$NON-NLS-1$
					return JaspersoftStudioPlugin.getInstance().getImage(MField.getIconDescriptor().getIcon16());
				if (oldExpText.startsWith("$P{")) //$NON-NLS-1$
					return JaspersoftStudioPlugin.getInstance().getImage(MParameter.getIconDescriptor().getIcon16());
				if (oldExpText.startsWith("$V{")) //$NON-NLS-1$
					return JaspersoftStudioPlugin.getInstance().getImage(MVariable.getIconDescriptor().getIcon16());
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			Wrapper w = (Wrapper) element;
			JRDesignCrosstabColumnGroup m = (JRDesignCrosstabColumnGroup) w.getValue();
			JRCrosstabBucket bucket = m.getBucket();

			switch (columnIndex) {
			case 0:
				return w.getLabel();
			case 1:
				return bucket.getOrder().getName();
			case 2:
				return m.getTotalPositionValue().getName();
			case 3:
				return w.getCalculation() != null ? w.getCalculation().getName() : "";
			}
			return ""; //$NON-NLS-1$
		}
	}

	protected CrosstabWizardColumnPage() {
		this("crosstabcolumnpage"); //$NON-NLS-1$
	}

	protected CrosstabWizardColumnPage(String pagename) {
		super(pagename);
		setTitle(Messages.CrosstabWizardColumnPage_columns);
		setImageDescriptor(Activator.getDefault().getImageDescriptor("icons/wizard_columns.png"));//$NON-NLS-1$
		setDescription(Messages.CrosstabWizardColumnPage_description);
		setPageComplete(false);
	}

	@Override
	public void setAvailableFields(List<?> inFields) {
		List<Wrapper> nlist = new ArrayList<Wrapper>();
		if (inFields != null) {
			for (Object obj : inFields)
				nlist.add(new Wrapper(obj));
		}
		super.setAvailableFields(nlist);
	}

	@Override
	public List<Object> getAvailableFields() {
		List<Object> wlist = super.getAvailableFields();
		if (wlist != null) {
			List<Object> out = new ArrayList<Object>(wlist.size());
			for (Object w : wlist)
				out.add(((Wrapper) w).getValue());
			return out;
		}
		return null;
	}

	@Override
	public List<Object> getSelectedFields() {
		List<Object> wlist = super.getSelectedFields();
		if (wlist != null) {
			List<Object> out = new ArrayList<Object>(wlist.size());
			for (Object w : wlist)
				out.add(((Wrapper) w).getValue());
			return out;
		}
		return null;
	}
	
	public List<Wrapper> getSelectedFieldWrappers() {
		List<Object> wlist = super.getSelectedFields();
		if (wlist != null) {
			List<Wrapper> out = new ArrayList<Wrapper>(wlist.size());
			for (Object w : wlist)
				out.add((Wrapper) w);
			return out;
		}
		return null;
	}

	@Override
	protected void setLabelProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new TColumnLabelProvider());
	}

	@Override
	protected void createColumns() {
		TableColumn[] col = new TableColumn[4];
		col[0] = new TableColumn(rightTable, SWT.NONE);
		col[0].setText(Messages.common_fields);
		col[0].pack();

		col[1] = new TableColumn(rightTable, SWT.NONE);
		col[1].setText(Messages.common_order);
		col[1].pack();

		col[2] = new TableColumn(rightTable, SWT.NONE);
		col[2].setText(Messages.common_total_position);
		col[2].pack();

		col[3] = new TableColumn(rightTable, SWT.NONE);
		col[3].setText(Messages.common_calculation);
		col[3].pack();

		TableLayout tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(30, false));
		tlayout.addColumnData(new ColumnWeightData(20, false));
		tlayout.addColumnData(new ColumnWeightData(20, false));
		tlayout.addColumnData(new ColumnWeightData(30, false));
		rightTable.setLayout(tlayout);
	}

	@Override
	protected void attachCellEditors(final TableViewer viewer, Composite parent) {
		viewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				if (property.equals(F_ORDER)) // $NON-NLS-1$
					return true;
				if (property.equals(F_TOTALPOSITION)) // $NON-NLS-1$
					return true;
				if (property.equals(F_CALCULATION)) { // $NON-NLS-1$
					return AgregationFunctionEnum.isEditable(((Wrapper)element).getBaseObjectType());
				}
				return false;
			}

			public Object getValue(Object element, String property) {
				Wrapper w = (Wrapper) element;
				JRDesignCrosstabColumnGroup prop = (JRDesignCrosstabColumnGroup) w.getValue();
				if (F_NAME.equals(property)) // $NON-NLS-1$
					return ((TColumnLabelProvider) viewer.getLabelProvider()).getColumnText(element, 1);

				if (F_ORDER.equals(property)) {// $NON-NLS-1$
					return EnumHelper.getEnumIndexByTranslatedName(bucketOrderNames, prop.getBucket().getOrder());
				}
				if (F_TOTALPOSITION.equals(property)) {// $NON-NLS-1$
					return EnumHelper.getEnumIndexByTranslatedName(crosstabTotalPositionEnumNames,
							prop.getTotalPositionValue());
				}
				if (F_CALCULATION.equals(property)) // $NON-NLS-1$
					return w.getCalculation() != null ? w.getCalculation() : "";

				return ""; //$NON-NLS-1$
			}

			public void modify(Object element, String property, Object value) {
				TableItem tableItem = (TableItem) element;
				setErrorMessage(null);
				setMessage(getDescription());
				Wrapper w = (Wrapper) tableItem.getData();
				JRDesignCrosstabColumnGroup data = (JRDesignCrosstabColumnGroup) w.getValue();
				JRDesignCrosstabBucket bucket = (JRDesignCrosstabBucket) data.getBucket();
				if (F_ORDER.equals(property)) { // $NON-NLS-1$
					bucket.setOrder(EnumHelper.getEnumByObjectValue(BucketOrder.values(), value));
				} else if (F_TOTALPOSITION.equals(property)) { // $NON-NLS-1$
					data.setTotalPosition(EnumHelper.getEnumByObjectValue(CrosstabTotalPositionEnum.values(), value));
				} else if (F_CALCULATION.equals(property)) { // $NON-NLS-1$
					AgregationFunctionEnum function = (AgregationFunctionEnum)value;
					w.setCalculation(function);
					CrosstabWizard.setBucketExpression(bucket, w.getBaseObjectExpression(), function);
				}
				viewer.update(element, new String[] { property });
				viewer.refresh();
			}
		});
		
		//create the cell editors
		TextCellEditor nameEditor = new TextCellEditor(parent);
		ComboBoxCellEditor sortEditor = new ComboBoxCellEditor(parent, EnumHelper.getEnumNames(SortOrderEnum.values(), NullEnum.NOTNULL), SWT.READ_ONLY);
		ComboBoxCellEditor totalEditor = new ComboBoxCellEditor(parent, EnumHelper.getEnumNames(CrosstabTotalPositionEnum.values(), NullEnum.NOTNULL), SWT.READ_ONLY);
		EditableComboBoxCellEditor aggregationEditor = new EditableComboBoxCellEditor(parent, new String[]{}, SWT.READ_ONLY) {
			
			protected void doSetValue(Object value) {
				StructuredSelection sel = (StructuredSelection)rightTView.getSelection();
				if (!sel.isEmpty()) {
					Wrapper selected = (Wrapper)sel.getFirstElement();
					String className = selected.getBaseObjectType();
					AgregationFunctionEnum[] values = AgregationFunctionEnum.getStringValues(className);
					String[] names = AgregationFunctionEnum.getValuesNames(values);
					comboBox.setItems(names);
					comboBox.setData(values);
				}
				AgregationFunctionEnum[] values = (AgregationFunctionEnum[])comboBox.getData();
				int index = Arrays.asList(values).indexOf(value);
				if (index >= 0 && index < comboBox.getItemCount()) super.doSetValue(index);
				else doSetValue(0);
			};
			
			@Override
			protected Object doGetValue() {
				int index = (Integer)super.doGetValue();
				AgregationFunctionEnum[] values = (AgregationFunctionEnum[]) comboBox.getData();
				return values[index];
			}
			
		};
		
		viewer.setCellEditors(new CellEditor[] { nameEditor, sortEditor, totalEditor, aggregationEditor });
		viewer.setColumnProperties(new String[] { F_NAME, F_ORDER, F_TOTALPOSITION, F_CALCULATION });
	}

	/**
	 * This procedure initialize the dialog page with the list of available
	 * objects. This implementation looks for object set in the map as
	 * DISCOVERED_FIELDS.
	 * 
	 */
	public void loadSettings() {

		if (getSettings() == null)
			return;

		if (getWizard() instanceof CrosstabWizard) {
			setAvailableFields(((CrosstabWizard) getWizard()).getAvailableColumnGroups());
		} else {
			setAvailableFields(null);
		}
	}

	/**
	 * Every time a new selection occurs, the selected fields are stored in the
	 * settings map with the key WizardDataSourcePage.DATASET_FIELDS
	 */
	public void storeSettings() {
		if (getWizard() instanceof JSSWizard && getWizard() != null) {
			Map<String, Object> settings = ((JSSWizard) getWizard()).getSettings();

			if (settings == null)
				return;

			settings.put(CrosstabWizard.CROSSTAB_COLUMNS, getSelectedFields());
			getContainer().updateButtons();
		}

	}

	@Override
	public boolean isPageComplete() {
		if (getWizard() instanceof JSSWizard && getWizard() != null) {
			Map<String, Object> settings = ((JSSWizard) getWizard()).getSettings();
			if (settings != null && settings.get(CrosstabWizard.CROSSTAB_COLUMNS) != null){
				List<?> fields = (List<?>)settings.get(CrosstabWizard.CROSSTAB_COLUMNS);
				return !fields.isEmpty();
			}
			return false;
		}
		return super.isPageComplete();
	}
	
	/**
	 * This function checks if a particular right element is in the provided
	 * list, and which is the matching element in that list.
	 * 
	 * This implementation is based on the string value returned by left and
	 * right getText label providers on column 0
	 * 
	 * @param object
	 * @param fields
	 * @return
	 */
	protected Object findElement(Object object, List<?> fields) {

		String objName = ((TColumnLabelProvider) rightTView.getLabelProvider()).getColumnText(object, 0);
		for (Object obj : fields) {
			if (((TColumnLabelProvider) leftTView.getLabelProvider()).getColumnText(obj, 0).equals(objName)) {
				return obj;
			}
		}
		return null;
	}

}
