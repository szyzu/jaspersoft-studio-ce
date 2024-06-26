/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.components.chart.wizard.fragments.data;

import net.sf.jasperreports.charts.design.JRDesignHighLowDataset;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.jaspersoft.studio.components.chart.messages.Messages;
import com.jaspersoft.studio.components.chart.wizard.HyperlinkPage;
import com.jaspersoft.studio.components.chart.wizard.fragments.data.widget.DatasetSeriesWidget;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.jasper.JSSDrawVisitor;
import com.jaspersoft.studio.model.MHyperLink;
import com.jaspersoft.studio.property.dataset.ExpressionWidget;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class DSHighLow extends ADSComponent {
	private JRDesignHighLowDataset dataset;
	private ExpressionWidget series;
	private ExpressionWidget volume;
	private ExpressionWidget date;
	private ExpressionWidget close;
	private ExpressionWidget open;
	private ExpressionWidget high;
	private ExpressionWidget low;
	private Button hyperlinkBtn;

	public DSHighLow(Composite composite, DatasetSeriesWidget dsWidget) {
		super(composite, dsWidget);
	}

	@Override
	public String getName() {
		return "High Low Dataset"; //$NON-NLS-1$
	}

	@Override
	public void setData(JSSDrawVisitor drawVisitor, JRDesignElement jrChart,
			JRDesignElementDataset eDataset,
			JasperReportsConfiguration jrContext) {
		Assert.isTrue(eDataset instanceof JRDesignHighLowDataset);
		super.setData(drawVisitor, jrChart, eDataset, jrContext);
		dataset = (JRDesignHighLowDataset) eDataset;

		series.bindObject(dataset, "SeriesExpression"); //$NON-NLS-1$
		volume.bindObject(dataset, "VolumeExpression"); //$NON-NLS-1$
		date.bindObject(dataset, "DateExpression"); //$NON-NLS-1$
		close.bindObject(dataset, "CloseExpression"); //$NON-NLS-1$
		open.bindObject(dataset, "OpenExpression"); //$NON-NLS-1$
		high.bindObject(dataset, "HighExpression"); //$NON-NLS-1$
		low.bindObject(dataset, "LowExpression"); //$NON-NLS-1$
	}

	protected Control createChartTop(Composite composite) {
		Composite yCompo = new Composite(composite, SWT.NONE);
		yCompo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		yCompo.setLayout(new GridLayout(10, false));

		return yCompo;
	}

	@Override
	protected Control createChartLeft(Composite parent) {
		Composite xCompo = new Composite(parent, SWT.NONE);
		xCompo.setLayout(new GridLayout(3, false));
		xCompo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		high = new ExpressionWidget(xCompo, Messages.DSHighLow_highExpression);

		close = new ExpressionWidget(xCompo, Messages.DSHighLow_closeExpression);

		open = new ExpressionWidget(xCompo, Messages.DSHighLow_openExpression);

		low = new ExpressionWidget(xCompo, Messages.DSHighLow_lowExpression);


		return xCompo;
	}

	@Override
	protected Control createChartBottom(Composite parent) {
		Composite xCompo = new Composite(parent, SWT.NONE);
		xCompo.setLayout(new GridLayout(3, false));
		xCompo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		series = new ExpressionWidget(xCompo, Messages.DSHighLow_seriesExpression);

		return xCompo;
	}

	@Override
	protected Control createChartRight(Composite parent) {
		Composite xCompo = new Composite(parent, SWT.NONE);
		xCompo.setLayout(new GridLayout(3, false));
		xCompo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		volume = new ExpressionWidget(xCompo, Messages.DSHighLow_volumeExpression);
		date = new ExpressionWidget(xCompo, Messages.DSHighLow_dateExpression);
		
		hyperlinkBtn = new Button(xCompo, SWT.PUSH | SWT.FLAT);
		hyperlinkBtn.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		hyperlinkBtn.setText(Messages.DSHighLow_hypertextButton);
		hyperlinkBtn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				MHyperLink hyperLinkElement = null;
				JRHyperlink hyperlink = dataset.getItemHyperlink();
				if (hyperlink != null){
					hyperLinkElement = createHyperlinkModel((JRHyperlink)hyperlink.clone());
				} else {
					hyperLinkElement = createHyperlinkModel(new JRDesignHyperlink());
				}
				
				//The highlow chart has the hyperlink on the dataset and not on the series, so it 
				//need to show a custom message
				HyperlinkPage dlg = new HyperlinkPage(hyperlinkBtn.getShell(), hyperLinkElement, null, dataset.getItemHyperlink() != null){
					
					@Override
					protected String getDialogTitle() {
						return  Messages.DSHighLow_hypertextDialogTitle;
					};
				
					@Override
					protected String getDeleteMessage() {
						return Messages.DSHighLow_deleteHyperlinkConfirmation;
					};
				};
				int operationResult = dlg.open();
				if (operationResult == Window.OK) {
					dataset.setItemHyperlink((JRHyperlink)dlg.getElement().getValue());
				} else if (operationResult == IDialogConstants.ABORT_ID){
					dataset.setItemHyperlink(null);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		return xCompo;
	}

	@Override
	public void setExpressionContext(ExpressionContext expContext) {
		super.setExpressionContext(expContext);
		this.date.setExpressionContext(expContext);
		this.high.setExpressionContext(expContext);
		this.low.setExpressionContext(expContext);
		this.open.setExpressionContext(expContext);
		this.series.setExpressionContext(expContext);
		this.volume.setExpressionContext(expContext);
	}
	
}
