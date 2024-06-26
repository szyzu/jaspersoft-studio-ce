/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.textfield;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.jface.resource.ImageDescriptor;

import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.text.MTextField;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.model.util.NodeIconDescriptor;

/*
 * /* The Class MTime.
 */
public class MTime extends MTextField {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new NodeIconDescriptor("time"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/**
	 * Instantiates a new m time.
	 */
	public MTime() {
		super();
	}

	/**
	 * Instantiates a new m time.
	 * 
	 * @param parent
	 *          the parent
	 * @param jrStaticText
	 *          the jr static text
	 * @param newIndex
	 *          the new index
	 */
	public MTime(ANode parent, JRTextField jrStaticText, int newIndex) {
		super(parent, jrStaticText, newIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jaspersoft.studio.model.textfield.MTextField#createJRElement(net.sf.jasperreports.engine.design.JasperDesign)
	 */
	@Override
	public JRDesignTextField createJRElement(JasperDesign jasperDesign) {
		JRDesignTextField jrDesignTextField = new JRDesignTextField(jasperDesign);
		JRDesignExpression expression = new JRDesignExpression();
		expression.setText("new java.util.Date()"); //$NON-NLS-1$
		jrDesignTextField.setExpression(expression);
		jrDesignTextField.setPattern("HH:mm");
		return jrDesignTextField;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.textfield.MTextField#getDisplayText()
	 */
	@Override
	public String getDisplayText() {
		if (getValue() != null) {
			JRTextField jrTextField = (JRTextField) getValue();
			if (jrTextField.getExpression() != null)
				return jrTextField.getExpression().getText();
		}
		return getIconDescriptor().getTitle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.textfield.MTextField#getImagePath()
	 */
	@Override
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.textfield.MTextField#getToolTip()
	 */
	@Override
	public String getToolTip() {
		return getIconDescriptor().getToolTip();
	}

}
