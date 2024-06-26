/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.scriptlet.command;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignScriptlet;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.scriptlet.MScriptlet;
import com.jaspersoft.studio.model.scriptlet.MScriptlets;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.SelectionHelper;
/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class CreateScriptletCommand extends Command {

	/** The jr scriptlet. */
	protected JRDesignScriptlet jrScriptlet;

	/** The jr dataset. */
	private JRDesignDataset jrDataset;

	/** The index. */
	private int index;

	/**
	 * Instantiates a new creates the scriptlet command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 * @param index
	 *          the index
	 */
	public CreateScriptletCommand(MScriptlets destNode, MScriptlet srcNode, int index) {
		super();
		this.jrDataset = (JRDesignDataset) destNode.getValue();
		this.index = index;
		if (srcNode != null && srcNode.getValue() != null)
			this.jrScriptlet = (JRDesignScriptlet) srcNode.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (jrScriptlet == null) {
			this.jrScriptlet = MScriptlet.createJRScriptlet(jrDataset);
		}
		if (jrScriptlet != null) {
			try {
				if (index >= 0 && index < jrDataset.getScriptletsList().size())
					jrDataset.addScriptlet(index, jrScriptlet);
				else
					jrDataset.addScriptlet(jrScriptlet);
				SelectionHelper.setOutlineSelection(jrScriptlet);
			} catch (JRException e) {
				e.printStackTrace();
				if (e.getMessage().startsWith("Duplicate declaration")) { //$NON-NLS-1$
					String defaultName = ModelUtils.getDefaultName(jrDataset.getScriptletsMap(), "CopyOFScriptlet_"); //$NON-NLS-1$
					InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
							Messages.CreateScriptletCommand_scriptlet_name,
							Messages.CreateScriptletCommand_scriptlet_name_dialog_text, defaultName, null);
					if (dlg.open() == InputDialog.OK) {
						jrScriptlet.setName(dlg.getValue());
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
		jrDataset.removeScriptlet(jrScriptlet);
	}
}
