/*
 * Jaspersoft Open Studio - Eclipse-based JasperReports Designer. Copyright (C) 2005 - 2010 Jaspersoft Corporation. All
 * rights reserved. http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program is part of iReport.
 * 
 * iReport is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * iReport is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with iReport. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.crosstab.model.measure.command;

import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabMeasure;
import net.sf.jasperreports.engine.JRException;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;

import com.jaspersoft.studio.crosstab.model.measure.MMeasure;
import com.jaspersoft.studio.crosstab.model.measure.MMeasures;
import com.jaspersoft.studio.utils.ModelUtils;

/**
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class CreateMeasureCommand extends Command {

	/** The jr parameter. */
	private JRDesignCrosstabMeasure jrMeasure;

	/** The jr dataset. */
	private JRDesignCrosstab jrCrosstab;

	/** The index. */
	private int index;

	/**
	 * Instantiates a new creates the parameter command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 * @param position
	 *          the position
	 * @param index
	 *          the index
	 */
	public CreateMeasureCommand(MMeasures destNode, MMeasure srcNode, int index) {
		super();
		this.jrCrosstab = (JRDesignCrosstab) destNode.getValue();
		this.index = index;
		if (srcNode != null && srcNode.getValue() != null)
			this.jrMeasure = (JRDesignCrosstabMeasure) srcNode.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (jrMeasure == null) {
			this.jrMeasure = new JRDesignCrosstabMeasure();
			this.jrMeasure.setName(ModelUtils.getDefaultName(jrCrosstab.getMeasureIndicesMap(), Messages.CreateMeasureCommand_measure));
		}
		if (jrMeasure != null) {
			try {
				if (index < 0 || index > jrCrosstab.getMesuresList().size())
					jrCrosstab.addMeasure(jrMeasure);
				else
					jrCrosstab.addMeasure(jrMeasure);
				// jrCrosstab.addParameter(index, jrParameter);
			} catch (JRException e) {
				e.printStackTrace();
				if (e.getMessage().startsWith("Duplicate declaration")) { //$NON-NLS-1$
					String defaultName = ModelUtils.getDefaultName(jrCrosstab.getMeasureIndicesMap(), "CopyOFMeasure_"); //$NON-NLS-1$
					InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), Messages.CreateMeasureCommand_measure_name,
							Messages.CreateMeasureCommand_measure_text_dialog, defaultName, null);
					if (dlg.open() == InputDialog.OK) {
						jrMeasure.setName(dlg.getValue());
						execute();
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		jrCrosstab.removeMeasure(jrMeasure);
	}
}
