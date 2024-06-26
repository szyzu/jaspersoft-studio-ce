/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.java2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
/*
 * The Class GenericImageRenderer.
 */
public final class GenericImageRenderer implements ImageRenderer
{
  
  /** The Constant PALETTE_DATA. */
  private static final PaletteData PALETTE_DATA = new PaletteData(16711680, 65280, 255);

  /* (non-Javadoc)
   * @see com.jaspersoft.studio.editor.java2d.ImageRenderer#render(org.eclipse.swt.widgets.Display, org.eclipse.swt.graphics.GC, int[], int, int, int, int, int, int, int, int)
   */
  public final void render(Display paramDisplay, GC paramGC, int[] data, int xSrc, int ySrc, int width, int height, int xDest, int yDest, int imgWidth, int imgHeight)
  {
	  	ImageData imageData = new ImageData(width, height, 32, PALETTE_DATA);
    	imageData.setPixels(0, 0, data.length, data, 0);
    	Image image = new Image(paramDisplay, imageData);
	    try {
	      paramGC.drawImage(image, 0, 0, width, height, xDest, yDest, imgWidth, imgHeight);
	    }
	    finally
	    {
	    	image.dispose();
	    }
  }
}
