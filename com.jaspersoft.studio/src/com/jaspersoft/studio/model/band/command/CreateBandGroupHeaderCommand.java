/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.band.command;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.model.band.MBandGroupHeader;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
/*
 * creates a band in a Group.
 * 
 * @author Chicu Veaceslav
 */
public class CreateBandGroupHeaderCommand extends Command {

	/** The jr band. */
	private JRDesignBand jrBand;

	/** The jr design section. */
	private JRDesignSection jrDesignSection;

	/** The index. */
	private int index = -1;

	public CreateBandGroupHeaderCommand() {
		super();
	}

	/**
	 * Instantiates a new creates the band group header command.
	 * 
	 * @param destNode
	 *          the dest node
	 */
	public CreateBandGroupHeaderCommand(MBandGroupHeader destNode) {
		super();
		this.jrDesignSection = (JRDesignSection) (destNode.getJrGroup()).getGroupHeaderSection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (jrBand == null) {
			jrBand = MBandGroupHeader.createJRBand();
		}
		if (jrBand != null && jrDesignSection != null) {
			if (index < 0 || index > jrDesignSection.getBandsList().size())
				jrDesignSection.addBand(jrBand);
			else
				jrDesignSection.addBand(index, jrBand);
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
		if (jrBand != null && jrDesignSection != null) {
			index = jrDesignSection.getBandsList().indexOf(jrBand);
			jrDesignSection.removeBand(jrBand);
		}
	}

	/**
	 * Return the element created on the execution of the command
	 * 
	 * @return the jr element created when the command is executed or null
	 */
	public JRChild getCreatedElement(){
		return jrBand;
	}
}
