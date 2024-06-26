/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.pdf;


import org.eclipse.ui.IWorkbenchPart;

public class PdfActionTableDetail extends PdfActionAbstract {
	
	/** Id of the actions */
	public static final String ID_TableDetail_Full = "PdfAction_TableDetail_Full"; //$NON-NLS-1$
	public static final String ID_TableDetail_Start = "PdfAction_TableDetail_Start"; //$NON-NLS-1$
	public static final String ID_TableDetail_End = "PdfAction_TableDetail_End"; //$NON-NLS-1$
	public static final String ID_TableDetail_None = "PdfAction_TableDetail_None"; //$NON-NLS-1$
	
	
	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *          The part for this action
	 * @param action_position
	 * 					Identify The position of the label
	 */
	public PdfActionTableDetail(IWorkbenchPart part,Position action_position) {
		super(part, action_position, ID_TableDetail_Full, ID_TableDetail_Start, ID_TableDetail_End, ID_TableDetail_None);
	}

	/**
	 * method to return the property name 
	 * @return Property for which one the value must be changed
	 */
	protected String getPropertyName(){
		return "net.sf.jasperreports.export.pdf.tag.td";
	}

}
