/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.wizard.fragments.data;

import java.text.MessageFormat;
import java.util.List;

import net.sf.jasperreports.charts.JRTimeSeries;
import net.sf.jasperreports.charts.design.JRDesignTimeSeries;
import net.sf.jasperreports.charts.design.JRDesignTimeSeriesDataset;
import net.sf.jasperreports.charts.type.TimePeriodEnum;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;
import net.sf.jasperreports.engine.design.JRDesignHyperlink;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.components.chart.wizard.HyperlinkPage;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.dialog.SeriesDialog;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.series.TimeSerie;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.widget.DatasetSeriesWidget;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.jasper.JSSDrawVisitor;
import com.jaspersoft.studio.model.MHyperLink;
import com.jaspersoft.studio.property.dataset.ExpressionWidget;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.utils.EnumHelper;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class DSTimeSeries extends ADSComponent {

	private JRDesignTimeSeriesDataset dataset;
	private ExpressionWidget valueWidget;
	private ExpressionWidget timePeriod;
	private ExpressionWidget labelWidget;
	private Combo seriesCombo;
	private Combo timePeriodCombo;
	private Button hyperlinkBtn;
	
	public DSTimeSeries(Composite composite, DatasetSeriesWidget dsWidget) {
		super(composite, dsWidget);
	}

	@Override
	public String getName() {
		return Messages.DSTimeSeries_0; 
	}

	@Override
	public void setData(JSSDrawVisitor drawVisitor, JRDesignElement jrChart,
			JRDesignElementDataset eDataset,
			JasperReportsConfiguration jrContext) {
		Assert.isTrue(eDataset instanceof JRDesignTimeSeriesDataset);
		super.setData(drawVisitor, jrChart, eDataset, jrContext);
		dataset = (JRDesignTimeSeriesDataset) eDataset;
		setSeries(0);

		TimePeriodEnum tpe = TimePeriodEnum.getByValue(dataset.getTimePeriod());
		if (tpe == null) {
			timePeriodCombo.select(5);
		} else {
			TimePeriodEnum[] tpevalues = TimePeriodEnum.values();
			for (int i = 0; i < tpevalues.length; i++) {
				if (tpe.equals(tpevalues[i])) {
					timePeriodCombo.select(i + 1);
					break;
				}
			}
		}
	}

	private void setSeries(int selection) {
		List<JRTimeSeries> seriesList = dataset.getSeriesList();
		if (!seriesList.isEmpty()) {
			String[] srnames = new String[seriesList.size()];
			for (int i = 0; i < seriesList.size(); i++) {
				JRTimeSeries cs = seriesList.get(i);
				JRExpression se = cs.getSeriesExpression();
				srnames[i] = se != null && se.getText() != null ? se.getText()
						: ""; //$NON-NLS-1$
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
		JRTimeSeries serie = null;
		if (selection >= 0 && selection < dataset.getSeriesList().size())
			serie = dataset.getSeriesList().get(selection);

		valueWidget.bindObject(serie, "ValueExpression"); //$NON-NLS-1$
		timePeriod.bindObject(serie, "TimePeriodExpression"); //$NON-NLS-1$
		labelWidget.bindObject(serie, "LabelExpression"); //$NON-NLS-1$
		hyperlinkBtn.setText(MessageFormat.format(Messages.DSCategory_defineHyperlinkButtton,seriesCombo.getText()));
		
		valueWidget.setEnabled(serie != null);
		timePeriod.setEnabled(serie != null);
		labelWidget.setEnabled(serie != null);
		hyperlinkBtn.setEnabled(serie != null);
	}

	protected Control createChartTop(Composite composite) {
		Composite yCompo = new Composite(composite, SWT.NONE);
		yCompo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		yCompo.setLayout(new GridLayout(10, false));

		Label lbl = new Label(yCompo, SWT.NONE);
		lbl.setText(Messages.DSCategory_seriesLabel);

		seriesCombo = new Combo(yCompo, SWT.READ_ONLY | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		seriesCombo.setLayoutData(gd);
		seriesCombo.setItems(new String[] { "series 1" }); //$NON-NLS-1$
		seriesCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				handleSelectSeries(seriesCombo.getSelectionIndex());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btn = new Button(yCompo, SWT.PUSH | SWT.FLAT);
		btn.setText("..."); //$NON-NLS-1$
		btn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				TimeSerie serie = new TimeSerie();
				SeriesDialog<JRTimeSeries> dlg = new SeriesDialog<JRTimeSeries>(
						btn.getShell(), serie);
				dlg.setExpressionContext(expContext);
				List<JRTimeSeries> oldList = dataset.getSeriesList();
				int oldsel = seriesCombo.getSelectionIndex();
				JRTimeSeries selected = null;
				if (oldsel >= 0)
					selected = oldList.get(oldsel);
				serie.setList(oldList);
				if (dlg.open() == Window.OK) {
					List<JRTimeSeries> newlist = serie.getList();
					for (JRTimeSeries item : dataset.getSeries())
						dataset.removeTimeSeries(item);
					for (JRTimeSeries item : serie.getList())
						dataset.addTimeSeries(item);

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
				JRDesignTimeSeries serie = null;
				if (selection >= 0 && selection < dataset.getSeriesList().size())
					serie = (JRDesignTimeSeries) dataset.getSeriesList().get(selection);
				if (serie != null){
					MHyperLink hyperLinkElement = null;
					JRHyperlink hyperlink = serie.getItemHyperlink();
					if (hyperlink != null){
						hyperLinkElement = createHyperlinkModel((JRHyperlink)hyperlink.clone());
					} else {
						hyperLinkElement = createHyperlinkModel(new JRDesignHyperlink());
					}
					HyperlinkPage dlg = new HyperlinkPage(hyperlinkBtn.getShell(), hyperLinkElement, seriesCombo.getText(), serie.getItemHyperlink() != null);
					int operationResult = dlg.open();
					if (operationResult == Window.OK) {
						serie.setItemHyperlink((JRHyperlink)dlg.getElement().getValue());
					} else if (operationResult == IDialogConstants.ABORT_ID){
						serie.setItemHyperlink(null);
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

		valueWidget = new ExpressionWidget(yCompo, Messages.DSTimeSeries_valueLabel);
		return yCompo;
	}

	@Override
	protected Control createChartBottom(Composite parent) {
		Composite yCompo = new Composite(parent, SWT.NONE);
		yCompo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		yCompo.setLayout(new GridLayout(10, false));

		timePeriod = new ExpressionWidget(yCompo, Messages.DSTimeSeries_timePeriodExpression);

		Label lbl = new Label(yCompo, SWT.NONE);
		lbl.setText(Messages.DSTimeSeries_timePeriodLabel);

		timePeriodCombo = new Combo(yCompo, SWT.SINGLE | SWT.READ_ONLY
				| SWT.BORDER);
		timePeriodCombo.setItems(EnumHelper.getEnumNames(
				TimePeriodEnum.values(), NullEnum.NULL));
		timePeriodCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (timePeriodCombo.getSelectionIndex() == 0)
					dataset.setTimePeriod(null);
				else {
					TimePeriodEnum tpe = TimePeriodEnum.values()[timePeriodCombo
							.getSelectionIndex() - 1];
					dataset.setTimePeriod(tpe.getTimePeriod());
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		return yCompo;
	}

	@Override
	protected Control createChartRight(Composite parent) {
		Composite yCompo = new Composite(parent, SWT.NONE);
		yCompo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		yCompo.setLayout(new GridLayout(3, false));

		labelWidget = new ExpressionWidget(yCompo, Messages.DSTimeSeries_labelExpression);
		return yCompo;
	}

	@Override
	public void setExpressionContext(ExpressionContext expContext) {
		super.setExpressionContext(expContext);
		this.labelWidget.setExpressionContext(expContext);
		this.timePeriod.setExpressionContext(expContext);
		this.valueWidget.setExpressionContext(expContext);
	}

}
