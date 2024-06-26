/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.color.chooser;

import org.eclipse.swt.graphics.RGB;

/**
 * Governor for the ColorSelectorWidget where the user clicking the rectangle
 * slider area control the brightness of the color, and on the square area are shown
 * the color variations of hue and saturation with the selected brightness
 * 
 * @author Orlandin Marco
 *
 */
public class BrightnessBasedSelector implements IWidgetGovernor{

	@Override
	public int getSliderMax() {
		return 100;
	}

	@Override
	public int getSliderMin() {
		return 0;
	}

	@Override
	public int getPadMinX() {
		return 0;
	}

	@Override
	public int getPadMinY() {
		return 0;
	}

	@Override
	public int getPadMaxX() {
		return 360;
	}

	@Override
	public int getPadMaxY() {
		return 100;
	}
	

	@Override
	public RGB getPadColor(int x, int y, int sliderPosition) {
		float fBright = (float)Math.abs(100-sliderPosition)/100;
		float fSat = (float) Math.abs(100-y) / 100;
		return new RGB((float)Math.abs(360-x), fSat, fBright);
	}
	
	@Override
	public RGB getSliderColor(int x, int y, int sliderPosition) {
		return getPadColor(x, y, sliderPosition);
	}
	
	@Override
	public int[] getXYSlider(Object color) {
		float[] hsb = null;
		if (color instanceof RGB){
			hsb = ((RGB)color).getHSB();
		} else if (color instanceof float[]){
			hsb = (float[])color;
		}
		if (hsb == null) return new int[]{0,0,0};
		return new int[]{Math.round(Math.abs(360-hsb[0])), Math.abs(Math.round(100-hsb[1]*100)), Math.abs(Math.round(100-hsb[2]*100))};
	}

}
