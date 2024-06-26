/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.csv;

import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.export.JRCsvMetadataExporter;
import net.sf.jasperreports.export.CsvExporterConfiguration;
import net.sf.jasperreports.export.CsvMetadataReportConfiguration;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.editor.action.CustomSelectionAction;
import com.jaspersoft.studio.editor.action.pdf.PropertiesList;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.model.MRoot;
import com.jaspersoft.studio.property.SetValueCommand;

/**
 * Base action to set the CSV attributes that can be represented with a true or false value
 * 
 * @author Orlandin Marco
 * 
 */
public class CSVAction extends CustomSelectionAction {

	/** Embedded attributes ids */
	public static String COL_NAME = JRCsvMetadataExporter.PROPERTY_COLUMN_NAME;

	public static String COL_DATA = JRCsvMetadataExporter.PROPERTY_DATA;

	public static String FIELD_DELIMITER = CsvExporterConfiguration.PROPERTY_FIELD_DELIMITER;

	public static String RECORD_DELIMITER = CsvExporterConfiguration.PROPERTY_RECORD_DELIMITER;

	public static String COL_NAMES = CsvMetadataReportConfiguration.PROPERTY_COLUMN_NAMES_PREFIX;

	public static String WRITE_HEADER = CsvMetadataReportConfiguration.PROPERTY_WRITE_HEADER;

	private String[] attributeToRemove;

	public CSVAction(IWorkbenchPart part, String actionId, String actionName) {
		super(part, IAction.AS_CHECK_BOX);
		setId(actionId);
		// the property need to be registered
		PropertiesList.addItem(actionId);
		setText(actionName);
		attributeToRemove = null;
	}

	/**
	 * Uses the attribute to remove parameter to define the attribute that should be removed when the attributeId is set.
	 * This is done to define attribute mutually exclusive with the others
	 */
	public CSVAction(IWorkbenchPart part, String actionId, String actionName, String[] attributeToRemove) {
		this(part, actionId, actionName);
		this.attributeToRemove = attributeToRemove;
	}

	/**
	 * Take the selected nodes and use the first of them to reach the root of the report. The root could be and MReport if
	 * we are in the main editor or an MRoot if we are editing an element in its own editor (table, list and crosstab). In
	 * this last case we need to reach the MPage of the subeditor and from there it is possible read and write the
	 * properties of the root.
	 * 
	 * @return the root of the report (typically an MReport or a MPage), or null is the root is unreachable
	 */
	protected APropertyNode getRoot() {
		List<Object> nodes = editor.getSelectionCache().getSelectionModelForType(APropertyNode.class);
		return getRoot(nodes);
	}

	/**
	 * Take the passed list of nodes and use the first of them to reach the root. The root could be and MReport if we are
	 * in the main editor or an MRoot if we are editing an element in its own editor (table, list and crosstab). In this
	 * last case we need to reach the MPage of the subeditor and from there it is possible read and write the properties
	 * of the root.
	 * 
	 * @return the root of the report (typically an MReport or a MPage), or null is the root is unreachable
	 */
	protected APropertyNode getRoot(List<Object> nodes) {
		if (nodes.isEmpty()) {
			return null;
		}
		APropertyNode columnValue = (APropertyNode) nodes.get(0);
		if (columnValue.getRoot() instanceof MRoot) {
			INode rootChildren = columnValue.getRoot().getChildren().get(0);
			// Return the MPage
			if (rootChildren instanceof APropertyNode)
				return (APropertyNode) rootChildren;
		} else {
			return (APropertyNode) columnValue.getRoot();
		}
		return null;
	}

	/**
	 * Check if the attribute associated to the id of the action has value true and then return true, otherwise (so if
	 * even if the attribute is undefined) return null.
	 */
	public boolean isChecked() {
		if (!freshChecked) {
			freshChecked = true;
			ischecked = true;
			APropertyNode model = getRoot();
			if (model == null)
				return false;
			if (model instanceof MReport) {
				JRPropertiesMap v = ((MReport) model).getValue().getPropertiesMap();
				if (v == null)
					ischecked = false;
				else {
					Object oldValue = v.getProperty(getId());
					if (oldValue == null || oldValue.equals("false")) //$NON-NLS-1$
						ischecked = false;
				}
			} else {
				if (!(model instanceof MGraphicElement)) {
					ischecked = false;
				} else {
					JRPropertiesMap v = (JRPropertiesMap) ((MGraphicElement) model).getPropertiesMap();
					if (v == null)
						ischecked = false;
					else {
						Object oldValue = v.getProperty(getId());
						if (oldValue == null || oldValue.equals("false")) //$NON-NLS-1$
							ischecked = false;
					}
				}
			}
		}
		return ischecked;
	}

	/**
	 * Remove from the property map all the attributes in the attributeToRemove array
	 * 
	 * @param map
	 *          location from where the attributes are removed
	 */
	protected void removeAttributes(JRPropertiesMap map) {
		if (attributeToRemove != null) {
			for (String attributeName : attributeToRemove)
				map.removeProperty(attributeName);
		}
	}

	/**
	 * Create the command for the selected action. Try to set the specified attribute id with the value true on the root
	 * element. If the attirbute it is already present with the value true then it is removed
	 * 
	 * @param model
	 *          Model of the selected item
	 * @return the command to execute
	 */
	public Command createCommand(APropertyNode model) {
		SetValueCommand cmd = new SetValueCommand();
		cmd.setTarget(model);
		cmd.setPropertyId(MGraphicElement.PROPERTY_MAP);
		JRPropertiesMap v = (JRPropertiesMap) model.getPropertyValue(MGraphicElement.PROPERTY_MAP);
		if (v == null)
			v = new JRPropertiesMap();
		if (v.containsProperty(getId()))
			v.removeProperty(getId());
		else {
			v.setProperty(getId(), "true"); //$NON-NLS-1$
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

	/**
	 * Returns a container with the command for the editing of the action id attribute. See {@createCommand} method to
	 * have more information
	 * 
	 * @return a stack of commands that contain only the command to change a single attribute on the root of the document.
	 *         If the root is not found the stack will be empty
	 */
	protected JSSCompoundCommand createCompoundCommand() {
		ANode root = getRoot();
		return root != null ? new JSSCompoundCommand(root) : null;
	}

	@Override
	protected Command createCommand() {
		JSSCompoundCommand command = createCompoundCommand();
		if (command != null) {
			command.setDebugLabel(getText());
			APropertyNode model = getRoot();
			if (model != null)
				command.add(createCommand(model));
			else
				return null;
		}
		freshChecked = false;
		return command;
	}

}
