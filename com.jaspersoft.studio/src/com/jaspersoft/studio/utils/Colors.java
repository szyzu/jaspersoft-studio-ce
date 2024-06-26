/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wb.swt.SWTResourceManager;

import com.jaspersoft.studio.JaspersoftStudioPlugin;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

/*
 * /* The Class Colors.
 * 
 * @author Chicu Veaceslav
 */
public class Colors {
	
	/** Map to keep track of the HTML Color Names
	 	https://www.w3schools.com/colors/colors_names.asp  */
	private static final Properties htmlColorNames;
	/** Pattern for hexadecimal colors (i.e: #FFCA12) */
	public static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
	
	static {
		htmlColorNames=new Properties();
		InputStream in = null;
		try {
			in = Colors.class.getResourceAsStream("htmlColorNames.properties");
			htmlColorNames.load(in);
		}
		catch(IOException ex) {
			JaspersoftStudioPlugin.getInstance().logError(ex);
		}
		finally {
			if(in!=null){
				IOUtils.closeQuietly(in);
			}
		}
	}

	/**
	 * Gets the sW t4 awt color.
	 * 
	 * @param color
	 *          the color
	 * @return the sW t4 awt color
	 */
	public static Color getSWT4AWTColor(java.awt.Color color) {
		if (color != null)
			return SWTResourceManager.getColor(color.getRed(), color.getGreen(), color.getBlue());
		return null;
	}

	/**
	 * Gets the aW t4 swt color.
	 * 
	 * @param color
	 *          the color
	 * @return the aW t4 swt color
	 */
	public static java.awt.Color getAWT4SWTColor(Color color) {
		if (color != null)
			return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
		return null;
	}

	/**
	 * Gets the sWTRG b4 awtgb color.
	 * 
	 * @param color
	 *          the color
	 * @return the sWTRG b4 awtgb color
	 */
	public static AlfaRGB getSWTRGB4AWTGBColor(java.awt.Color color) {
		if (color != null)
			return new AlfaRGB(new RGB(color.getRed(), color.getGreen(), color.getBlue()), color.getAlpha());
		return null;
	}

	/**
	 * Gets the aW t4 swtrgb color.
	 * 
	 * @param color
	 *          the color
	 * @return the aW t4 swtrgb color
	 */
	public static java.awt.Color getAWT4SWTRGBColor(AlfaRGB color) {
		if (color != null && color.getRgb() != null)
			return new java.awt.Color(color.getRgb().red, color.getRgb().green, color.getRgb().blue, color.getAlfa());
		return null;
	}

	/**
	 * Gets the SWT RGB color derived from an input AWT color.
	 * 
	 * @param color
	 *          the AWT color to convert
	 * @return a converted {@link RGB} color instance, <code>null</code> otherwise
	 */
	public static RGB getRGB4AWTColor(java.awt.Color color) {
		if (color != null) {
			return new RGB(color.getRed(), color.getGreen(), color.getBlue());
		}
		return null;
	}

	/**
	 * Gets the encoded value of a {@link java.awt.Color} instance.<br>
	 * The output is the hexadecimal conversion (i.e: #00FFFF).
	 * 
	 * @param awtColor
	 *          the color to encode
	 * @return color value as encoded string
	 */
	public static String getHexEncodedAWTColor(java.awt.Color awtColor) {
		if (awtColor == null) {
			return ""; //$NON-NLS-1$
		}
		String nums = "0123456789ABCDEF"; //$NON-NLS-1$
		String s = "#"; //$NON-NLS-1$
		s += nums.charAt(awtColor.getRed() / 16);
		s += nums.charAt(awtColor.getRed() % 16);
		s += nums.charAt(awtColor.getGreen() / 16);
		s += nums.charAt(awtColor.getGreen() % 16);
		s += nums.charAt(awtColor.getBlue() / 16);
		s += nums.charAt(awtColor.getBlue() % 16);
		return s;
	}

	/**
	 * Gets the encoded value of a {@link Color} instance.<br>
	 * The output is the hexadecimal conversion (i.e: #00FFFF).
	 * 
	 * @param swtColor
	 *          the color to encode
	 * @return color value as encoded string
	 */
	public static String getHexEncodedSWTColor(Color swtColor) {
		if (swtColor == null) {
			return ""; //$NON-NLS-1$
		}
		return getHexEncodedAWTColor(getAWT4SWTColor(swtColor));
	}

	/**
	 * Gets the encoded value of a {@link RGB} instance.<br>
	 * The output is the hexadecimal conversion (i.e: #00FFFF).
	 * 
	 * @param rgbColor
	 *          the color to encode
	 * @return color value as encoded string
	 */
	public static String getHexEncodedRGBColor(RGB rgbColor) {
		if (rgbColor == null) {
			return ""; //$NON-NLS-1$
		}
		return getHexEncodedAWTColor(getAWT4SWTRGBColor(AlfaRGB.getFullyOpaque(rgbColor)));
	}

	/**
	 * Gets the encoded value of a {@link AlfaRGB} instance.<br>
	 * The output is the hexadecimal conversion (i.e: #00FFFF). The alpha channel is not considered.
	 * 
	 * @param alfaRgbColor
	 *          the color to encode
	 * @return color value as encoded string
	 */
	public static String getHexEncodedRGBColor(AlfaRGB alfaRgbColor) {
		if (alfaRgbColor == null) {
			return ""; //$NON-NLS-1$
		}
		return getHexEncodedAWTColor(getAWT4SWTRGBColor(alfaRgbColor));
	}

	public static String getRGBAEncodedRGBColor(AlfaRGB alfaRgbColor) {
		if (alfaRgbColor == null) {
			return ""; //$NON-NLS-1$
		}
		RGB rgb = alfaRgbColor.getRgb();
		return "rgba(" + rgb.red + "," + rgb.green + "," + rgb.blue + "," + alfaRgbColor.getAlfa() + ")";
	}

	/**
	 * Gets an SWT image representing a preview of the specified AWT color. The output image has size according to the
	 * specified width and height in pixels.
	 * <p>
	 * When no color is provided, a "empty" image with a grid-like pattern is returned.<br>
	 * 
	 * @param color
	 *          the AWT color instance
	 * @param width
	 *          width pixels
	 * @param height
	 *          height pixels
	 * @return an SWT image preview of the specified color
	 */
	public static Image getSWTColorPreview(java.awt.Color color, int width, int height) {
		RGB black = new RGB(0, 0, 0);
		RGB white = new RGB(255, 255, 255);

		ImageData data = null;
		if (color == null) {
			PaletteData dataPalette = new PaletteData(new RGB[] { black, black, white });
			data = new ImageData(width, height, 4, dataPalette);
			data.transparentPixel = 0;
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					if (y == 0 || y == data.height - 1 || x == 0 || x == data.width - 1) {
						// Always draw a black border
						data.setPixel(x, y, 1);
					} else if (y % 3 == 0 || x % 3 == 0) {
						data.setPixel(x, y, 1);
					} else {
						data.setPixel(x, y, 2);
					}
				}
			}
		} else {
			AlfaRGB rgb = getSWTRGB4AWTGBColor(color);
			PaletteData dataPalette = new PaletteData(new RGB[] { black, black, rgb.getRgb() });
			data = new ImageData(width, height, 4, dataPalette);
			data.transparentPixel = 0;
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					if (x == 0 || y == 0 || x == data.width - 1 || y == data.height - 1)
						data.setPixel(x, y, 1);
					else
						data.setPixel(x, y, 2);
				}
			}
		}

		Image image = new Image(UIUtils.getDisplay(), data);
		return image;
	}

	/**
	 * Gets the AWT color for the specified color string. The input string should be an integer number representing the
	 * rgb int value in 16 bit.
	 * 
	 * @param colorString
	 *          the rgb int value as string
	 * @return the AWT color instance converted, <code>null</code> if operation fails
	 */
	public static java.awt.Color decodeColor(String colorString) {
		java.awt.Color color = null;
		if (colorString.length() > 0) {
			try {
				color = new java.awt.Color(Integer.parseInt(colorString, 16));
			} catch (Exception ex) {
			}
		}
		return color;
	}

	/**
	 * Encodes the AWT color specified as HEX value (16 bit) representation. It does not include in the final output the #
	 * character commonly used in UI elements (i.e: text-boxes).
	 * 
	 * @param awtColor
	 *          the color instance to encode
	 * @return the encode string
	 */
	public static String getEncodedColor(java.awt.Color awtColor) {
		String s = getHexEncodedAWTColor(awtColor);
		if (s != null && !s.isEmpty())
			// remove the # char
			return s.substring(1);
		else
			return s;
	}

	/**
	 * Gets the AWT color for the specified color string. The input string should be an hexadecimal representation of an
	 * RGB. For example: #FAE123.
	 * 
	 * @param hexColorString
	 *          the hexadecimal string representing the color
	 * @return the converted AWT Color
	 */
	public static java.awt.Color decodeHexStringAsAWTColor(String hexColorString) {
		RGB decodedRGB = decodeHexStringAsSWTRGB(hexColorString);
		if (decodedRGB != null) {
			return getAWT4SWTRGBColor(AlfaRGB.getFullyOpaque(decodedRGB));
		} else {
			return null;
		}
	}

	/**
	 * Gets the SWT RGB for the specified color string. The input string should be an hexadecimal representation of an
	 * RGB. For example: #FAE123.
	 * 
	 * @param hexColorString
	 *          the hexadecimal string representing the color
	 * @return the converted {@link RGB}
	 */
	public static RGB decodeHexStringAsSWTRGB(String hexColorString) {
		RGB rgb = null;
		if (hexColorString != null && hexColorString.startsWith("#") && hexColorString.length() == 7) {
			int red = Integer.parseInt(hexColorString.substring(1, 3), 16);
			int green = Integer.parseInt(hexColorString.substring(3, 5), 16);
			int blue = Integer.parseInt(hexColorString.substring(5, 7), 16);
			rgb = new RGB(red, green, blue);
		}
		return rgb;
	}

	/**
	 * Decodes a text string that can contain a list of hex colors, as a more suitable array that can be manipulated.
	 * 
	 * @param hexColorsArray
	 *          an array-like string that contains multiple colors
	 * @return an array with all the hex colors extracted
	 */
	public static String[] decodeHexColorsArray(String hexColorsArray) {
		if (hexColorsArray == null)
			return new String[0];
		Matcher m = HEX_COLOR_PATTERN.matcher(hexColorsArray);
		List<String> listMatches = new ArrayList<String>();
		while (m.find()) {
			listMatches.add(m.group(0));
		}
		return listMatches.toArray(new String[listMatches.size()]);
	}

	/**
	 * Given a bunch of colors it produces an encoded string that can be used in different places as compact value
	 * representation.
	 * <p>
	 * <em>Sample return value</em>: <code>['#7cb5ec', '#434348', '#90ed7d', '#f7a35c', '#8085e9']</code>
	 * 
	 * @param colors
	 *          the array of colors
	 * @return the encoded string representing the array of colors
	 */
	public static String encodeHexColorsAsArray(String[] colors) {
		StringBuffer sb = new StringBuffer();
		if (colors != null && colors.length > 0) {
			String separator = "";
			sb.append("[");
			for (String col : colors) {
				sb.append(separator);
				separator = ",";
				sb.append("'").append(col).append("'");
			}
			sb.append("]");
		}
		return sb.toString();
	}
	
	/**
	 * Returns the HTML color name for the specified hex value if any.
	 * 
	 * @param hex the color hex value (i.e #FFAABB)
	 * @return the HTML color name found, <code>null</code> otherwise
	 */
	public static String getHtmlColorName(String hex) {
		if(!StringUtils.isEmpty(hex)){
			for(String p : htmlColorNames.stringPropertyNames()){
				if(htmlColorNames.getProperty(p).equalsIgnoreCase(hex)){
					return p;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the hex value for the specified HTML color name.
	 *  
	 * @param name the HTML color name
	 * @return the color hex value found (i.e #AABBCC), <code>null</code> otherwise
	 */
	public static String getHtmlColorHex(String name) {
		if(!StringUtils.isEmpty(name)){
			return htmlColorNames.getProperty(name.toLowerCase());
		}
		else {
			return null;
		}
	}
	
}
