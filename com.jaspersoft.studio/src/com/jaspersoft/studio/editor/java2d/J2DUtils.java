/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.java2d;

import java.awt.BasicStroke;
import java.awt.Stroke;
/*
 * The Class J2DUtils.
 */
public class J2DUtils {

	  /**
		 * This function provides a way to cancel the effects of the zoom on a particular Stroke. All the stroke values (as
		 * line width, dashes, etc...) are divided by the zoom factor. This allow to have essentially a fixed Stroke
		 * independent by the zoom. The returned Stroke is a copy. Remember to restore the right stroke in the graphics when
		 * done.
		 * 
		 * It works only with instances of BasicStroke
		 * 
		 * zoom is the zoom factor.
		 * 
		 * @param stroke
		 *          the stroke
		 * @param zoom
		 *          the zoom
		 * @return the inverted zoomed stroke
		 */
	  public static Stroke getInvertedZoomedStroke(Stroke stroke, double zoom)
	  {
	            if (stroke == null || !(stroke instanceof BasicStroke )) return stroke;
	            
	            BasicStroke bs = (BasicStroke)stroke;
	            float[] dashArray = bs.getDashArray();
	            
	            float[] newDashArray = null;
	            if (dashArray != null)
	            {
	                newDashArray = new float[dashArray.length];
	                for (int i=0; i<newDashArray.length; ++i)
	                {
	                    newDashArray[i] = (float)(dashArray[i] / zoom);
	                }
	            }
	            
	            BasicStroke newStroke = new BasicStroke(       
	                            (float)(bs.getLineWidth() / zoom),
	                            bs.getEndCap(),
	                            bs.getLineJoin(),
	                            bs.getMiterLimit(),
	                            //(float)(bs.getMiterLimit() / zoom),
	                            newDashArray,
	                            (float)(bs.getDashPhase() / zoom)
	                    );
	            return newStroke;
	  }
}
