/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.xls;

import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.export.XlsReportConfiguration;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.editor.action.CustomSelectionAction;
import com.jaspersoft.studio.editor.action.pdf.PropertiesList;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.property.SetValueCommand;

/**
 * Action used to set an XSL attribute
 * 
 * @author Orlandin Marco
 * 
 */
public class XLSAction extends CustomSelectionAction {

	/** Embedded attributes ids */
	public static String FIT_COL_ID = JRXlsAbstractExporter.PROPERTY_AUTO_FIT_COLUMN;

	public static String FIT_ROW_ID = JRXlsAbstractExporter.PROPERTY_AUTO_FIT_ROW;

	public static String BREAK_AFTER_ROW_ID = JRXlsAbstractExporter.PROPERTY_BREAK_AFTER_ROW;

	public static String BREAK_BEFORE_ROW_ID = JRXlsAbstractExporter.PROPERTY_BREAK_BEFORE_ROW;

	public static String CELL_HIDDEN_ID = XlsReportConfiguration.PROPERTY_CELL_HIDDEN;

	public static String CELL_LOCKED_ID = XlsReportConfiguration.PROPERTY_CELL_LOCKED;

	public static String AUTOFILTER_ID = JRXlsAbstractExporter.PROPERTY_AUTO_FILTER;

	public static String FREEZE_ROW_ID = JRXlsAbstractExporter.PROPERTY_FREEZE_ROW_EDGE;

	public static String FREEZE_COL_ID = JRXlsAbstractExporter.PROPERTY_FREEZE_COLUMN_EDGE;

	private String value;

	private String attributeId;

	private String[] attributeToRemove;

	public XLSAction(IWorkbenchPart part, String actionId, String value, String actionName) {
		this(part, actionId, actionId, value, actionName);
	}

	public XLSAction(IWorkbenchPart part, String actionId, String attributeId, String value, String actionName) {
		super(part, IAction.AS_CHECK_BOX);
		setId(actionId);
		this.attributeId = attributeId;
		this.value = value;
		// the property need to be registered
		PropertiesList.addItem(actionId);
		setText(actionName);
		attributeToRemove = null;
	}

	/**
	 * Uses the attribute to remove parameter to define the attribute that should be removed when the attributeId is set.
	 * This is done to define attribute mutually exclusives with the others
	 */
	public XLSAction(IWorkbenchPart part, String actionId, String attributeId, String value, String actionName,
			String[] attributeToRemove) {
		this(part, actionId, actionId, value, actionName);
		this.attributeToRemove = attributeToRemove;
	}

	public boolean isChecked() {
		List<Object> graphicalElements = editor.getSelectionCache().getSelectionModelForType(MGraphicElement.class);
		if (graphicalElements.isEmpty()) {
			return false;
		}
		for (Object element : graphicalElements) {
			MGraphicElement model = (MGraphicElement) element;
			JRPropertiesMap v = (JRPropertiesMap) model.getPropertiesMap();
			if (v == null)
				return false;
			else {
				Object oldValue = v.getProperty(attributeId);
				if (oldValue == null || !oldValue.equals(value))
					return false;
			}
		}
		return true;
	}

	/**
	 * Remove from the property map all the attributes in the attributeToRemove array
	 * 
	 * @param map
	 */
	private void removeAttributes(JRPropertiesMap map) {
		if (attributeToRemove != null) {
			for (String attributeName : attributeToRemove)
				map.removeProperty(attributeName);
		}
	}

	/**
	 * Create the command for the selected action
	 * 
	 * @param model
	 *          Model of the selected item
	 * @return the command to execute
	 */
	public Command createCommand(MGraphicElement model) {
		SetValueCommand cmd = new SetValueCommand();
		cmd.setTarget(model);
		cmd.setPropertyId(MGraphicElement.PROPERTY_MAP);
		JRPropertiesMap v = (JRPropertiesMap) model.getPropertyValue(MGraphicElement.PROPERTY_MAP);
		Object oldValue = null;
		if (v == null) {
			v = new JRPropertiesMap();
		} else {
			oldValue = v.getProperty(attributeId);
			v.removeProperty(attributeId);
		}
		if (value != null && !value.equals(oldValue)) {
			v.setProperty(attributeId, value);
			removeAttributes(v);
		}
		cmd.setPropertyValue(v);
		return cmd;
	}

	/**
	 * Performs the create action on the selected objects.
	 */
	@Override
	public void run() {
		execute(createCommand());
	}

	@Override
	protected Command createCommand() {
		List<Object> graphicalElements = editor.getSelectionCache().getSelectionModelForType(MGraphicElement.class);
		if (graphicalElements.isEmpty())
			return null;
		JSSCompoundCommand command = new JSSCompoundCommand(null);
		command.setDebugLabel(getText());
		for (Object element : graphicalElements) {
			MGraphicElement graphElement = (MGraphicElement) element;
			command.add(createCommand(graphElement));
			command.setReferenceNodeIfNull(graphElement);
		}
		freshChecked = false;
		return command;
	}

}
