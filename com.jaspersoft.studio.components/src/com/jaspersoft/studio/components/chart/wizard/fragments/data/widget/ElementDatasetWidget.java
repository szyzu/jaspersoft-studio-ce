/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.wizard.fragments.data.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.ExpressionEditorSupportUtil;
import com.jaspersoft.studio.editor.expression.IExpressionContextSetter;
import com.jaspersoft.studio.model.dataset.MDatasetRun;
import com.jaspersoft.studio.model.dataset.descriptor.DatasetRunRVPropertyEditor;
import com.jaspersoft.studio.property.dataset.DatasetRunSelectionListener;
import com.jaspersoft.studio.property.dataset.DatasetRunWidget;
import com.jaspersoft.studio.property.descriptor.expression.dialog.JRExpressionEditor;
import com.jaspersoft.studio.property.descriptor.parameter.dialog.ComboParameterEditor;
import com.jaspersoft.studio.property.descriptor.parameter.dialog.GenericJSSParameter;
import com.jaspersoft.studio.property.descriptor.returnvalue.RVPropertyPage;
import com.jaspersoft.studio.utils.EnumHelper;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetParameter;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.DatasetResetTypeEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;

public class ElementDatasetWidget implements IExpressionContextSetter {
	private static final String GROUPPREFIX = "[Group] "; //$NON-NLS-1$
	private JRDesignElementDataset eDataset;
	private JasperDesign jrDesign;
	private Combo dsCombo;
	private Combo cbIncrement;
	private Combo cbReset;
	private Button btnIncrement;
	private ToolItem prmItem;
	private ToolItem prmMapItem;
	private ToolItem returnValue;
	private DatasetRunWidget dsRun;
	private ExpressionContext expContext;
	private List<DatasetRunSelectionListener> dsRunSelectionListeners;

	/**
	 * Map to store the dataset run created when switching the combo. This allow to
	 * avoid to reset the values of a dataset run when switching to another one and
	 * then go back
	 */
	private HashMap<String, JRDesignDatasetRun> datasetRunMap = new HashMap<>();

	public CTabFolder ctFolder;

	public ElementDatasetWidget(Composite parent) {
		this.dsRunSelectionListeners = new ArrayList<>();
		createDataset(parent);
		bindData();
	}

	public void setDataset(JRDesignElementDataset eDataset, JasperDesign jrDesign) {
		this.eDataset = eDataset;
		this.jrDesign = jrDesign;
		fillData();
	}

	private void fillData() {
		final String[] ds = ModelUtils.getDataSets(jrDesign, true);
		dsCombo.setItems(ds);
		dsCombo.select(0);
		if (eDataset != null && eDataset.getDatasetRun() != null) {
			for (int i = 0; i < ds.length; i++) {
				if (ds[i].equals(eDataset.getDatasetRun().getDatasetName())) {
					dsCombo.select(i);
					break;
				}
			}
			dsRun.setData((JRDesignDatasetRun) eDataset.getDatasetRun());
			dsRun.setExpressionContext(this.expContext);
		}

		enableMainDatasetRun();
		fillIncrement();
		fillResetGroup();
		dsCombo.getParent().layout(true);
	}

	private void fillIncrement() {
		List<String> lsIncs = new ArrayList<>();
		lsIncs.add(IncrementTypeEnum.REPORT.getName());
		lsIncs.add(IncrementTypeEnum.PAGE.getName());
		lsIncs.add(IncrementTypeEnum.COLUMN.getName());
		JRDataset jrds = getJRdataset(eDataset);
		for (JRGroup gr : jrds.getGroups())
			lsIncs.add(GROUPPREFIX + gr.getName());
		lsIncs.add(IncrementTypeEnum.NONE.getName());

		cbIncrement.setItems(lsIncs.toArray(new String[lsIncs.size()]));

		IncrementTypeEnum rst = eDataset.getIncrementTypeValue();
		String grname = eDataset.getIncrementGroup() != null ? eDataset.getIncrementGroup().getName() : null;
		for (int i = 0; i < lsIncs.size(); i++) {
			String rsttype = lsIncs.get(i);
			if (rst.equals(IncrementTypeEnum.GROUP)) {
				if (rsttype.startsWith(GROUPPREFIX) && grname.equals(rsttype.substring(GROUPPREFIX.length()))) {
					cbIncrement.select(i);
					break;
				}
			} else if (rsttype.equals(rst.getName())) {
				cbIncrement.select(i);
				break;
			}
		}
	}

	private void fillResetGroup() {
		JRDataset jrds = getJRdataset(eDataset);
		List<String> lsRsts = new ArrayList<>();
		lsRsts.add(ResetTypeEnum.REPORT.getName());
		lsRsts.add(ResetTypeEnum.COLUMN.getName());
		lsRsts.add(ResetTypeEnum.PAGE.getName());

		for (JRGroup gr : jrds.getGroups()) {
			lsRsts.add(GROUPPREFIX + gr.getName());
		}
		lsRsts.add(ResetTypeEnum.NONE.getName());
		cbReset.setItems(lsRsts.toArray(new String[lsRsts.size()]));

		DatasetResetTypeEnum rst = eDataset.getDatasetResetType();
		String grname = eDataset.getResetGroup() != null ? eDataset.getResetGroup().getName() : null;
		for (int i = 0; i < lsRsts.size(); i++) {
			String rsttype = lsRsts.get(i);
			if (rst.equals(DatasetResetTypeEnum.GROUP)) {
				if (rsttype.startsWith(GROUPPREFIX) && grname.equals(rsttype.substring(GROUPPREFIX.length()))) {
					cbReset.select(i);
					break;
				}
			} else if (rsttype.equals(rst.getName())) {
				cbReset.select(i);
				break;
			}
		}
	}

	private void bindData() {
		dsCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (eDataset.getIncrementTypeValue().equals(IncrementTypeEnum.GROUP)) {
					eDataset.setIncrementType(IncrementTypeEnum.REPORT);
					eDataset.setIncrementGroup(null);
					cbIncrement.select(0);
				}
				if (eDataset.getDatasetResetType().equals(DatasetResetTypeEnum.GROUP)) {
					eDataset.setResetType(DatasetResetTypeEnum.REPORT);
					eDataset.setResetGroup(null);
					cbReset.select(0);
				}
				if (dsCombo.getSelectionIndex() == 0) {
					eDataset.setDatasetRun(null);
				} else {

					JRDesignDatasetRun datasetRun = datasetRunMap.get(dsCombo.getText());
					if (datasetRun == null) {
						datasetRun = new JRDesignDatasetRun();
						datasetRun.setDatasetName(dsCombo.getText());
						datasetRunMap.put(dsCombo.getText(), datasetRun);
					}

					eDataset.setDatasetRun(datasetRun);
				}
				dsRun.setData((JRDesignDatasetRun) eDataset.getDatasetRun());
				enableMainDatasetRun();
				fillResetGroup();
				fillIncrement();
				notifyDatasetRunSelectionChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		bindIncrementGroup();
		bindResetGroup();

		returnValue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MDatasetRun datasetModel = new MDatasetRun(eDataset.getDatasetRun(), jrDesign);
				DatasetRunRVPropertyEditor wizard = new DatasetRunRVPropertyEditor(datasetModel);
				WizardDialog dialog = new WizardDialog(returnValue.getParent().getShell(), wizard);
				dialog.create();
				UIUtils.resizeAndCenterShell(dialog.getShell(), RVPropertyPage.WIDTH_HINT, -1);
				if (dialog.open() == Dialog.OK) {
					datasetModel.setPropertyValue(JRDesignDatasetRun.PROPERTY_RETURN_VALUES, wizard.getValue());
				}
			}
		});

		prmItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JRDesignDatasetRun datasetRun = (JRDesignDatasetRun) eDataset.getDatasetRun();
				ComboParameterEditor wizard = new ComboParameterEditor(
						new MDatasetRun(eDataset.getDatasetRun(), jrDesign));
				wizard.setValue(GenericJSSParameter.convertFrom(datasetRun.getParameters()));
				wizard.setExpressionContext(expContext);
				WizardDialog dialog = new WizardDialog(btnIncrement.getShell(), wizard);
				dialog.create();
				if (dialog.open() == Dialog.OK) {
					JRDatasetParameter[] params = GenericJSSParameter.convertToDataset(wizard.getValue());

					for (JRDatasetParameter prm : datasetRun.getParameters()) {
						datasetRun.removeParameter(prm);
					}

					for (JRDatasetParameter param : params) {
						try {
							datasetRun.addParameter(param);
						} catch (JRException er) {
							er.printStackTrace();
						}
					}
				}
			}
		});

		prmMapItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!ExpressionEditorSupportUtil.isExpressionEditorDialogOpen()) {
					JRExpressionEditor wizard = new JRExpressionEditor();
					wizard.setValue((JRDesignExpression) eDataset.getDatasetRun().getParametersMapExpression());
					wizard.setExpressionContext(expContext);
					WizardDialog dialog = ExpressionEditorSupportUtil
							.getExpressionEditorWizardDialog(btnIncrement.getShell(), wizard);
					if (dialog.open() == Dialog.OK) {
						((JRDesignDatasetRun) eDataset.getDatasetRun()).setParametersMapExpression(wizard.getValue());
					}
				}
			}
		});
	}

	private void enableMainDatasetRun() {
		boolean en = dsCombo.getSelectionIndex() != 0;
		prmItem.setEnabled(en);
		prmMapItem.setEnabled(en);
		returnValue.setEnabled(en);
		dsRun.setEnabled(en);
	}

	private void bindResetGroup() {
		cbReset.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				String newval = cbReset.getText();
				DatasetResetTypeEnum val = EnumHelper.getEnumByObjectValue(DatasetResetTypeEnum.values(), newval);
				if (val != null) {
					eDataset.setResetType(val);
				} else {
					eDataset.setResetType(DatasetResetTypeEnum.GROUP);
					JRDataset jrds = getJRdataset(eDataset);
					for (JRGroup gr : jrds.getGroups()) {
						if (gr.getName().equals(newval.substring(GROUPPREFIX.length()))) {
							eDataset.setResetGroup(gr);
							break;
						}

					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private void bindIncrementGroup() {
		cbIncrement.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				String newval = cbIncrement.getText();
				IncrementTypeEnum val = EnumHelper.getEnumByObjectValue(IncrementTypeEnum.values(), newval);
				if (val != null) {
					eDataset.setIncrementType(val);
				} else {
					eDataset.setIncrementType(IncrementTypeEnum.GROUP);
					JRDataset jrds = getJRdataset(eDataset);
					for (JRGroup gr : jrds.getGroups()) {
						if (gr.getName().equals(newval.substring(GROUPPREFIX.length()))) {
							eDataset.setIncrementGroup(gr);
							break;
						}
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		btnIncrement.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (!ExpressionEditorSupportUtil.isExpressionEditorDialogOpen()) {
					JRExpressionEditor wizard = new JRExpressionEditor();
					wizard.setValue((JRDesignExpression) eDataset.getIncrementWhenExpression());
					// Increment when expression should rely on the dataset run
					// information.
					JRDatasetRun datasetRun = eDataset.getDatasetRun();
					JRDesignDataset dds = jrDesign.getMainDesignDataset();
					if (datasetRun != null && datasetRun.getDatasetName() != null) {
						dds = ModelUtils.getDesignDatasetByName(jrDesign, datasetRun.getDatasetName());
					}
					ExpressionContext ec = new ExpressionContext(dds, expContext.getJasperReportsConfiguration());
					wizard.setExpressionContext(ec);
					WizardDialog dialog = ExpressionEditorSupportUtil
							.getExpressionEditorWizardDialog(btnIncrement.getShell(), wizard);
					if (dialog.open() == Dialog.OK) {
						eDataset.setIncrementWhenExpression(wizard.getValue());
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private JRDataset getJRdataset(final JRDesignElementDataset jrDataset) {
		if (jrDataset != null && jrDataset.getDatasetRun() != null) {
			String dsname = jrDataset.getDatasetRun().getDatasetName();
			if (jrDesign.getDatasetMap().containsKey(dsname)) {
				return jrDesign.getDatasetMap().get(dsname);
			}
		}
		// Fallback on the main dataset
		return jrDesign.getMainDataset();
	}

	public void createDataset(Composite composite) {
		Composite grDataset = new Composite(composite, SWT.NONE);
		grDataset.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		grDataset.setLayout(new GridLayout(1, false));

		ctFolder = new CTabFolder(grDataset, SWT.TOP);
		ctFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		createParametersMap(ctFolder);
		createConnection(ctFolder);
		GridData folderData = new GridData(SWT.FILL, SWT.TOP, true, false);
		folderData.heightHint = 90;
		folderData.minimumHeight = 90;
		ctFolder.setLayoutData(folderData);
		ctFolder.setSelection(0);
	}

	private void createConnection(CTabFolder tabFolder) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(Messages.ElementDatasetWidget_tabTitle);

		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));

		Composite leftComposite = new Composite(composite, SWT.NONE);
		leftComposite.setLayout(new GridLayout(3, false));
		leftComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));

		new Label(leftComposite, SWT.NONE).setText(Messages.ElementDatasetWidget_incrementOnLabel);
		cbIncrement = new Combo(leftComposite, SWT.BORDER | SWT.READ_ONLY | SWT.SINGLE);
		cbIncrement.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbIncrement.setToolTipText(
				"Define the moment during the execution of the report when the chart will read the current and use its values to populate itslef. None means each record, report only at the end, page and column each page or column or group when the group changes");
		btnIncrement = new Button(leftComposite, SWT.PUSH);
		btnIncrement.setText("..."); //$NON-NLS-1$
		btnIncrement.setToolTipText(Messages.ElementDatasetWidget_buttonTooltip);

		new Label(leftComposite, SWT.NONE).setText(Messages.ElementDatasetWidget_resetOnLabel);
		cbReset = new Combo(leftComposite, SWT.BORDER | SWT.READ_ONLY | SWT.SINGLE);
		cbReset.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbReset.setToolTipText(
				"Define the moment during the execution when the chart delete the acquired data from the previous record and start from the current record. This can be used when the chart is printed on more paged and it need to show only a subset of the dataset record in the moment it is printed");

		new Label(leftComposite, SWT.NONE);

		dsRun = new DatasetRunWidget(composite);
		dsRun.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		bptab.setControl(composite);
	}

	private void createParametersMap(CTabFolder ctfolder) {
		Composite composite = new Composite(ctfolder, SWT.NONE);
		GridLayout layout = new GridLayout(10, false);
		layout.verticalSpacing = 1;
		layout.marginWidth = 1;
		layout.marginTop = 1;
		layout.marginBottom = 1;
		composite.setLayout(layout);

		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(Messages.ElementDatasetWidget_datasetLabel);

		dsCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY | SWT.SINGLE);
		dsCombo.setItems(new String[] { "main dataset" }); //$NON-NLS-1$

		ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP | SWT.RIGHT);

		returnValue = new ToolItem(toolBar, SWT.PUSH);
		returnValue.setText(com.jaspersoft.studio.messages.Messages.common_return_values);

		prmItem = new ToolItem(toolBar, SWT.PUSH);
		prmItem.setText(Messages.ElementDatasetWidget_parametersLabel);

		prmMapItem = new ToolItem(toolBar, SWT.PUSH);
		prmMapItem.setText(Messages.ElementDatasetWidget_parametersMapLabel);

		int tabHeight = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		tabHeight = Math.max(tabHeight, ctfolder.getTabHeight());
		ctfolder.setTabHeight(tabHeight);

		ctfolder.setTopRight(composite);
	}

	public void setExpressionContext(ExpressionContext expContext) {
		this.expContext = expContext;
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
}
