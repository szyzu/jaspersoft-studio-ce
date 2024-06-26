/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.wizard.fragments.data;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

import net.sf.jasperreports.charts.JRPieSeries;
import net.sf.jasperreports.charts.design.JRDesignPieDataset;
import net.sf.jasperreports.charts.design.JRDesignPieSeries;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignHyperlink;

import org.apache.commons.validator.routines.FloatValidator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.components.chart.wizard.HyperlinkPage;
import com.jaspersoft.studio.components.chart.wizard.OtherSectionPage;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.dialog.SeriesDialog;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.series.PieSerie;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.widget.DatasetSeriesWidget;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.jasper.JSSDrawVisitor;
import com.jaspersoft.studio.model.MHyperLink;
import com.jaspersoft.studio.property.dataset.ExpressionWidget;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class DSPie extends ADSComponent {
	private JRDesignPieDataset dataset;
	private ExpressionWidget valueWidget;
	private ExpressionWidget labelWidget;
	private ExpressionWidget key;
	private Combo seriesCombo;
	private Text minSlice;
	private Spinner maxSlice;
	private Button obtn;
	private Button hyperlinkBtn;
	
	public DSPie(Composite composite, DatasetSeriesWidget dsWidget) {
		super(composite, dsWidget);
	}

	@Override
	public String getName() {
		return "Pie Dataset"; //$NON-NLS-1$
	}

	@Override
	public void setData(JSSDrawVisitor drawVisitor, JRDesignElement jrChart,
			JRDesignElementDataset eDataset,
			JasperReportsConfiguration jrContext) {
		Assert.isTrue(eDataset instanceof JRDesignPieDataset);
		super.setData(drawVisitor, jrChart, eDataset, jrContext);
		dataset = (JRDesignPieDataset) eDataset;
		setSeries(0);

		if (dataset.getMinPercentage() != null)
			minSlice.setText(NumberFormat.getInstance().format(
					dataset.getMinPercentage()));
		else
			minSlice.setText(""); //$NON-NLS-1$
		maxSlice.setSelection(dataset.getMaxCount() != null ? dataset
				.getMaxCount().intValue() : 0);
	}

	private void setSeries(int selection) {
		List<JRPieSeries> seriesList = dataset.getSeriesList();
		if (!seriesList.isEmpty()) {
			String[] srnames = new String[seriesList.size()];
			for (int i = 0; i < seriesList.size(); i++) {
				srnames[i] = String.format("Pie series (%d)", i); //$NON-NLS-1$
			}
			seriesCombo.setItems(srnames);
			seriesCombo.select(selection);
			hyperlinkBtn.setEnabled(true);
			handleSelectSeries(selection);
		} else {
			seriesCombo.setItems(new String[0]);
			hyperlinkBtn.setEnabled(false);
			hyperlinkBtn.setText(Messages.DSCategory_hyperlinkButtonDisabled);
			handleSelectSeries(-1);
		}
	}

	private void handleSelectSeries(int selection) {
		JRPieSeries serie = null;
		if (selection >= 0 && selection < dataset.getSeriesList().size())
			serie = dataset.getSeriesList().get(selection);

		valueWidget.bindObject(serie, "ValueExpression"); //$NON-NLS-1$
		key.bindObject(serie, "KeyExpression"); //$NON-NLS-1$
		labelWidget.bindObject(serie, "LabelExpression"); //$NON-NLS-1$
		hyperlinkBtn.setText(MessageFormat.format(Messages.DSCategory_defineHyperlinkButtton,seriesCombo.getText()));
		
		valueWidget.setEnabled(serie != null);
		key.setEnabled(serie != null);
		labelWidget.setEnabled(serie != null);
		hyperlinkBtn.setEnabled(serie != null);
	}

	protected Control createChartTop(Composite composite) {
		Composite yCompo = new Composite(composite, SWT.NONE);
		yCompo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		yCompo.setLayout(new GridLayout(10, false));

		Label lbl = new Label(yCompo, SWT.NONE);
		lbl.setText(Messages.DSCategory_seriesLabel);
		lbl.setToolTipText(Messages.DSPie_seriesTooltip);
		seriesCombo = new Combo(yCompo, SWT.READ_ONLY | SWT.BORDER);
		seriesCombo.setToolTipText(Messages.DSPie_seriesTooltip);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		seriesCombo.setLayoutData(gd);
		seriesCombo.setItems(new String[] { "series 1" }); //$NON-NLS-1$
		seriesCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				handleSelectSeries(seriesCombo.getSelectionIndex());
				obtn.setSelection(false);
				valueWidget.setEnabled(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btn = new Button(yCompo, SWT.PUSH | SWT.FLAT);
		btn.setText("..."); //$NON-NLS-1$
		btn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				PieSerie serie = new PieSerie();
				SeriesDialog<JRPieSeries> dlg = new SeriesDialog<JRPieSeries>(
						btn.getShell(), serie);
				dlg.setExpressionContext(expContext);
				List<JRPieSeries> oldList = dataset.getSeriesList();
				int oldsel = seriesCombo.getSelectionIndex();
				JRPieSeries selected = null;
				if (oldsel >= 0)
					selected = oldList.get(oldsel);
				serie.setList(oldList);
				if (dlg.open() == Window.OK) {
					List<JRPieSeries> newlist = serie.getList();
					for (JRPieSeries item : dataset.getSeries())
						dataset.removePieSeries(item);
					for (JRPieSeries item : serie.getList())
						dataset.addPieSeries(item);

					int sel = selected != null
							&& newlist.indexOf(selected) >= 0 ? newlist
							.indexOf(selected) : 0;
					setSeries(sel);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		hyperlinkBtn = new Button(yCompo, SWT.PUSH | SWT.FLAT);
		hyperlinkBtn.setSelection(false);
		hyperlinkBtn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				int selection = seriesCombo.getSelectionIndex();
				JRDesignPieSeries serie = null;
				if (selection >= 0 && selection < dataset.getSeriesList().size())
					serie = (JRDesignPieSeries) dataset.getSeriesList().get(selection);
				if (serie != null){
					MHyperLink hyperLinkElement = null;
					JRHyperlink hyperlink = serie.getSectionHyperlink();
					if (hyperlink != null){
						hyperLinkElement = createHyperlinkModel((JRHyperlink)hyperlink.clone());
					} else {
						hyperLinkElement = createHyperlinkModel(new JRDesignHyperlink());
					}
					HyperlinkPage dlg = new HyperlinkPage(hyperlinkBtn.getShell(), hyperLinkElement, seriesCombo.getText(), serie.getSectionHyperlink() != null);
					int operationResult = dlg.open();
					if (operationResult == Window.OK) {
						serie.setSectionHyperlink((JRHyperlink)dlg.getElement().getValue());
					} else if (operationResult == IDialogConstants.ABORT_ID){
						serie.setSectionHyperlink(null);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		return yCompo;
	}

	@Override
	protected Control createChartLeft(Composite parent) {
		Composite yCompo = new Composite(parent, SWT.NONE);
		yCompo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		yCompo.setLayout(new GridLayout(3, false));

		valueWidget = new ExpressionWidget(yCompo, Messages.DSCategory_valueLabel);
		valueWidget.setToolTipText(Messages.DSPie_valueTooltip);
		labelWidget = new ExpressionWidget(yCompo, Messages.DSCategory_labelLabel);
		labelWidget.setToolTipText(Messages.DSPie_labelTooltip);
		return yCompo;
	}

	@Override
	protected Control createChartBottom(Composite parent) {
		Composite yCompo = new Composite(parent, SWT.NONE);
		yCompo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		yCompo.setLayout(new GridLayout(10, false));

		key = new ExpressionWidget(yCompo, "Key"); //$NON-NLS-1$
		key.setToolTipText(Messages.DSPie_keyTooltip);
		return yCompo;
	}

	@Override
	protected Control createChartRight(Composite parent) {
		Composite yCompo = new Composite(parent, SWT.NONE);
		yCompo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		yCompo.setLayout(new GridLayout(2, false));

		new Label(yCompo, SWT.NONE).setText(Messages.DSPie_minSlicePercentage);
		minSlice = new Text(yCompo, SWT.BORDER);
		minSlice.addListener(SWT.Verify, new Listener() {

			public void handleEvent(Event e) {
				try {
					String number = e.text;
					String oldText = ((Text) e.widget).getText();
					if (e.start == 0)
						number = e.text + oldText;
					else
						number = oldText.substring(0, e.start) + e.text;
					if (oldText.length() - 1 > e.start + 1)
						number += oldText.substring(e.start + 1);

					if (number.equals("-")) //$NON-NLS-1$
						number = "-0";//$NON-NLS-1$
					if (number.equals(".")) //$NON-NLS-1$
						number = "0.";//$NON-NLS-1$

					e.doit = FloatValidator.getInstance().isValid(number);
				} catch (NumberFormatException ne) {
					e.doit = false;
				}
			}
		});
		minSlice.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				try {
					dataset.setMinPercentage(new Float(minSlice.getText()));
				} catch (NumberFormatException ne) {

				}
			}
		});

		new Label(yCompo, SWT.NONE).setText(Messages.DSPie_maxSlices);
		maxSlice = new Spinner(yCompo, SWT.BORDER);
		maxSlice.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				Integer intValue = maxSlice.getSelection();
				if (intValue == 0) intValue = null;
				dataset.setMaxCount(intValue);
			}
		});
		
		obtn = new Button(yCompo, SWT.PUSH);
		obtn.setText(Messages.DSPie_otherSectionButton);
		obtn.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		obtn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				MHyperLink hyperLinkElement = null;
				JRHyperlink hyperlink = dataset.getOtherSectionHyperlink();
				if (hyperlink != null){
					hyperLinkElement = new MHyperLink((JRHyperlink)hyperlink.clone());
				} else {
					hyperLinkElement = new MHyperLink(null);
				}
				JRDesignExpression otherKeyExp = getCopyExpression(dataset.getOtherKeyExpression());
				JRDesignExpression otherLabelExp = getCopyExpression(dataset.getOtherLabelExpression());
				OtherSectionPage dlg = new OtherSectionPage(hyperlinkBtn.getShell(), hyperLinkElement, otherKeyExp, otherLabelExp);
				int operationResult = dlg.open();
				if (operationResult == Window.OK) {
					dataset.setOtherSectionHyperlink(dlg.getHyperlink());
					dataset.setOtherKeyExpression(dlg.getKeyExpression());
					dataset.setOtherLabelExpression(dlg.getLabelExpression());
				} 
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		return yCompo;
	}
	
	private JRDesignExpression getCopyExpression(JRExpression baseExp){
		if (baseExp == null || !(baseExp instanceof JRDesignExpression)) return new JRDesignExpression();
		else return ((JRDesignExpression)baseExp.clone());
	}

	@Override
	public void setExpressionContext(ExpressionContext expContext) {
		super.setExpressionContext(expContext);
		this.key.setExpressionContext(expContext);
		this.labelWidget.setExpressionContext(expContext);
		this.valueWidget.setExpressionContext(expContext);
	}

}
