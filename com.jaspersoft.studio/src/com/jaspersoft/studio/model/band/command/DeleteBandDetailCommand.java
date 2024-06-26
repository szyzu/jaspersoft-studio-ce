/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.band.command;

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.band.MBand;
/*/*
 * link nodes & together.
 * 
 * @author Chicu Veaceslav
 */
public class DeleteBandDetailCommand extends Command {
	
	/** The jr band. */
	private JRDesignBand jrBand;
	
	/** The jr design section. */
	private JRDesignSection jrDesignSection;
	
	/** The index. */
	private int index;

	/**
	 * Instantiates a new delete band detail command.
	 * 
	 * @param destNode
	 *          the dest node
	 * @param srcNode
	 *          the src node
	 */
	public DeleteBandDetailCommand(ANode destNode, MBand srcNode) {
		super();
		this.jrBand = (JRDesignBand) srcNode.getValue();
		this.jrDesignSection = (JRDesignSection) srcNode.getJasperDesign().getDetailSection();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		int bandIndex = jrDesignSection.getBandsList().indexOf(jrBand);
		index = bandIndex != -1 ? bandIndex : jrDesignSection.getBandsList().size();
		jrDesignSection.removeBand(jrBand);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		if (index > jrDesignSection.getBandsList().size())
			jrDesignSection.addBand(jrBand);
		else
			jrDesignSection.addBand(index, jrBand);
	}
}
