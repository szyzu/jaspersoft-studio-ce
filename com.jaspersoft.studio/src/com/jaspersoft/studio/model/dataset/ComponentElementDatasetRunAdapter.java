/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.dataset;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRDatasetParameter;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;

/**
 * Adapter for the dataset run used in a generic component element (inside the dataset attribute). This adapter can be
 * used when dealing with generic dialogs/wizards/forms that modify a "generic" dataset run (instance of
 * {@link JRDesignDatasetRun}). This class can be extended by specific components (Maps, Charts, Widgets, etc.): however
 * in most case scenarios there should be no need to add further additional methods.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 * @see JRDesignDatasetRun
 * @see IEditableDataset
 * @see IEditableDatasetRun
 */
public class ComponentElementDatasetRunAdapter implements IEditableDatasetRun {

	/* The wrapped JRDatasetRun instance for the dataset of the generic component */
	protected JRDesignDatasetRun datasetRun;

	/* A reference to the "editable version" of the dataset holding the dataset run information */
	protected IEditableDataset editableDataset;

	public ComponentElementDatasetRunAdapter(IEditableDataset editableDataset) {
		this.editableDataset = editableDataset;
		if (editableDataset.getJRElementDataset() != null)
			this.datasetRun = (JRDesignDatasetRun) editableDataset.getJRElementDataset().getDatasetRun();
	}

	public void setDatasetName(String newDatasetName) {
		if (!useReportMainDataset()) {
			datasetRun.setDatasetName(newDatasetName);
		}
	}

	public void setParametersMapExpression(JRExpression newParametersMapExp) {
		if (!useReportMainDataset()) {
			datasetRun.setParametersMapExpression(newParametersMapExp);
		}
	}

	public void setParameters(JRDatasetParameter[] newParameters) {
		if (!useReportMainDataset()) {
			// Remove all existing parameters
			JRDatasetParameter[] oldParameters = datasetRun.getParameters();
			for (JRDatasetParameter p : oldParameters) {
				datasetRun.removeParameter(p);
			}
			// Add the new ones
			for (JRDatasetParameter p : newParameters) {
				try {
					datasetRun.addParameter(p);
				} catch (JRException e) {
					UIUtils.showError(e);
				}
			}
		}
	}

	public void addParameter(JRDatasetParameter newParameter) {
		if (!useReportMainDataset()) {
			try {
				datasetRun.addParameter(newParameter);
			} catch (JRException e) {
				UIUtils.showError(e);
			}
		}
	}

	public void removeParameter(JRDatasetParameter oldParameter) {
		if (!useReportMainDataset()) {
			datasetRun.removeParameter(oldParameter);
		}
	}

	public void setConnectionExpression(JRExpression newConnectionExp) {
		if (!useReportMainDataset()) {
			datasetRun.setConnectionExpression(newConnectionExp);
		}
	}

	public void setDataSourceExpression(JRExpression newDataSourceExp) {
		if (!useReportMainDataset()) {
			datasetRun.setDataSourceExpression(newDataSourceExp);
		}
	}

	public IEditableDataset getEditableDataset() {
		return this.editableDataset;
	}

	public JRDatasetRun getJRDatasetRun() {
		return this.datasetRun;
	}

	public boolean useReportMainDataset() {
		if (this.datasetRun == null)
			return true;
		return false;
	}

	public void resetDatasetRun(boolean nullableFlag) {
		if (nullableFlag) {
			this.datasetRun = null;
			this.editableDataset.setDatasetRun(null);
		} else {
			this.datasetRun = new JRDesignDatasetRun();
			this.editableDataset.setDatasetRun(datasetRun);
		}
	}

	public void setDatasetRun(JRDesignDatasetRun datasetRun) {
		this.datasetRun = datasetRun;
		this.editableDataset.setDatasetRun(datasetRun);
	}

}
