/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.doc.handlers;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.jaspersoft.studio.doc.messages.Messages;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.MQuery;
import com.jaspersoft.studio.model.dataset.MDataset;
import com.jaspersoft.studio.property.dataset.dialog.DatasetAction;
import com.jaspersoft.studio.property.dataset.dialog.DatasetDialog;
import com.jaspersoft.studio.property.descriptor.pattern.dialog.PatternEditor;
import com.jaspersoft.studio.utils.SelectionHelper;

/**
 * Open the edit query dialog of a report or a message dialog if the query
 * dialog isn't available for the opened element (for example a style reference
 * file).
 * 
 * @author Orlandin Marco
 * 
 */
public class OpenQueryHandler extends DatasetAction {

	public OpenQueryHandler() {
		super(SelectionHelper.getActiveJRXMLEditor());
	}

	@Override
	public void run() {
		APropertyNode reportRoot = HandlersUtil.getRootElement();
		if (reportRoot != null) {
			MDataset mdataset = (MDataset) reportRoot.getPropertyValue(JasperDesign.PROPERTY_MAIN_DATASET);
			MQuery mquery = (MQuery) mdataset.getPropertyValue(JRDesignDataset.PROPERTY_QUERY);
			PatternEditor wizard = new PatternEditor();
			wizard.setValue(mquery.getPropertyValue(JRDesignQuery.PROPERTY_TEXT).toString());
			new DatasetDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), mdataset, mquery.getJasperConfiguration(), getCommandStack()).open();
		} else
			MessageDialog.openWarning(UIUtils.getShell(), Messages.OpenQueryHandler_message_title, Messages.OpenQueryHandler_message_text);
	}

}
