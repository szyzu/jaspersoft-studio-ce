/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.json;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.editor.gef.decorator.json.SchemaDialog;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.property.SetValueCommand;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.export.JsonMetadataReportConfiguration;

/**
 * 
 * @author Veaceslav Chicu
 * 
 */
public class JSONSchemaAction extends JSONAction {

	private String path;

	public JSONSchemaAction(IWorkbenchPart part) {
		super(part, JsonMetadataReportConfiguration.JSON_EXPORTER_JSON_SCHEMA, Messages.JSONSchemaAction_0);
	}

	@Override
	public void run() {
		MReport element = getSelectedElement();
		if (element == null)
			return;
		SchemaDialog dialog = new SchemaDialog(UIUtils.getShell(), element);
		if (dialog.open() == Window.OK) {
			path = dialog.getName();
			execute(createCommand());
		}
	}

	private MReport getSelectedElement() {
		List<Object> textElements = editor.getSelectionCache().getSelectionModelForType(MReport.class);
		if (textElements.isEmpty() || textElements.size() != getSelectedObjects().size())
			return null;
		return (MReport) textElements.get(0);
	}

	@Override
	public boolean isChecked() {
		MReport model = getSelectedElement();
		if (model == null)
			return false;

		JRPropertiesMap colDataMap = model.getValue().getPropertiesMap();
		return colDataMap.containsProperty(JsonMetadataReportConfiguration.JSON_EXPORTER_JSON_SCHEMA);
	}

	@Override
	protected boolean calculateEnabled() {
		return !editor.getSelectionCache().getSelectionModelForType(MReport.class).isEmpty();
	}

	/**
	 * Create the commands necessary to transform a textual element into a JSON
	 * column or to remove it is it is already a JSON column
	 * 
	 */
	@Override
	protected Command createCommand() {
		MReport n = getSelectedElement();
		if (n == null)
			return null;

		JRPropertiesMap map = (JRPropertiesMap) n.getPropertyValue(APropertyNode.PROPERTY_MAP);
		if (map == null)
			map = new JRPropertiesMap();

		if (path == null)
			map.removeProperty(JsonMetadataReportConfiguration.JSON_EXPORTER_JSON_SCHEMA);
		else
			map.setProperty(JsonMetadataReportConfiguration.JSON_EXPORTER_JSON_SCHEMA, path);

		SetValueCommand cmd = new SetValueCommand();
		cmd.setTarget(n);
		cmd.setPropertyId(APropertyNode.PROPERTY_MAP);
		cmd.setPropertyValue(map);
		cmd.setDebugLabel(getText());

		return cmd;
	}

}
