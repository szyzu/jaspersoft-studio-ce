/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptor.box;

import java.awt.Color;

import net.sf.jasperreports.engine.JRPen;

import org.eclipse.jface.viewers.LabelProvider;

import com.jaspersoft.studio.model.MLinePen;
import com.jaspersoft.studio.property.descriptor.NullEnum;
/*
 * @author Chicu Veaceslav
 * 
 */
public class BoxLabelProvider extends LabelProvider {

	public BoxLabelProvider() {
		super();
	}

	@Override
	public String getText(Object element) {
		if (element != null && element instanceof MLinePen) {
			JRPen pen = (JRPen) ((MLinePen) element).getValue();
			String lineStyle = pen.getOwnLineStyleValue() != null ? pen.getOwnLineStyleValue().getName() : NullEnum.INHERITED
					.getName();
			String lineWidth = pen.getOwnLineWidth() != null ? pen.getOwnLineWidth().toString() : NullEnum.INHERITED
					.getName();
			Color rgb = pen.getOwnLineColor();
			String lineColor = NullEnum.INHERITED.getName();
			if (rgb != null)
				lineColor = "RGB (" + rgb.getRed() + "," + rgb.getGreen() + "," + rgb.getBlue() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return "[" + lineStyle + "," + lineWidth + "," + lineColor + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		return ""; //$NON-NLS-1$
	}

}
