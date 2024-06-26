/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.text;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.DefaultValue;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.checkbox.CheckBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.combo.FontNamePropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.combo.FontSizeButtonPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.combo.RWComboBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.combo.RWFontSizePropertyDescriptor;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.SPBooleanToggle;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.design.JRDesignStyle;

public class MFont extends APropertyNode {
	
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String FONT_INCREMENT = "FONT_INCREMENT";
	
	private static IPropertyDescriptor[] descriptors;
	
	public MFont(JRFont value) {
		super();
		setValue(value);
	}

	public ImageDescriptor getImagePath() {
		return null;
	}

	public String getDisplayText() {
		return null;
	}

	@Override
	protected void postDescriptors(IPropertyDescriptor[] descriptors) {
		super.postDescriptors(descriptors);
	}

	@Override
	public HashMap<String, Object> getStylesDescriptors() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		if (getValue() == null)
			return result;
		JRFont element = (JRFont) getValue();
		result.put(JRDesignStyle.PROPERTY_FONT_NAME, element.getOwnFontName());
		result.put(JRDesignStyle.PROPERTY_FONT_SIZE, element.getOwnFontsize());
		result.put(JRDesignStyle.PROPERTY_PDF_ENCODING, element.getOwnPdfEncoding());
		result.put(JRDesignStyle.PROPERTY_PDF_FONT_NAME, element.getOwnPdfFontName());
		result.put(JRDesignStyle.PROPERTY_BOLD, element.isOwnBold());
		result.put(JRDesignStyle.PROPERTY_ITALIC, element.isOwnItalic());
		result.put(JRDesignStyle.PROPERTY_UNDERLINE, element.isOwnUnderline());
		result.put(JRDesignStyle.PROPERTY_PDF_EMBEDDED, element.isOwnPdfEmbedded());
		result.put(JRDesignStyle.PROPERTY_STRIKE_THROUGH, element.isOwnStrikeThrough());
		return result;
	}

	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		FontNamePropertyDescriptor fontNameD = new FontNamePropertyDescriptor(JRBaseFont.PROPERTY_FONT_NAME,
				Messages.common_font_name, getJasperConfiguration().getFontList(), NullEnum.INHERITED);
		fontNameD.setDescription(Messages.MFont_font_name_description);
		desc.add(fontNameD);

		RWFontSizePropertyDescriptor fontSizeD = new RWFontSizePropertyDescriptor(JRBaseFont.PROPERTY_FONT_SIZE, Messages.common_font_size, ModelUtils.FONT_SIZES, NullEnum.INHERITED);
		fontSizeD.setDescription(Messages.MFont_font_size_description);
		desc.add(fontSizeD);

		FontSizeButtonPropertyDescriptor fontIncrement = new FontSizeButtonPropertyDescriptor(FONT_INCREMENT, this);
		desc.add(fontIncrement);

		RWComboBoxPropertyDescriptor pdfFontNameD = new RWComboBoxPropertyDescriptor(JRBaseFont.PROPERTY_PDF_FONT_NAME,
				Messages.MFont_pdf_font_name, ModelUtils.getPDFFontNames(), NullEnum.INHERITED);
		pdfFontNameD.setDescription(Messages.MFont_pdf_font_name_description);
		pdfFontNameD.setCategory(Messages.MFont_pdfCategory);
		desc.add(pdfFontNameD);

		RWComboBoxPropertyDescriptor pdfEncodingD = new RWComboBoxPropertyDescriptor(JRBaseFont.PROPERTY_PDF_ENCODING,
				Messages.MFont_pdf_encoding, ModelUtils.getPDFEncodings(), NullEnum.INHERITED);
		pdfEncodingD.setDescription(Messages.MFont_pdf_encoding_description);
		pdfEncodingD.setCategory(Messages.MFont_pdfCategory);
		desc.add(pdfEncodingD);

		CheckBoxPropertyDescriptor boldD = new CheckBoxPropertyDescriptor(JRBaseFont.PROPERTY_BOLD, Messages.common_bold,
				NullEnum.INHERITED) {
			@Override
			public ASPropertyWidget<CheckBoxPropertyDescriptor> createWidget(Composite parent, AbstractSection section) {
				return new SPBooleanToggle(parent, section, this,
						JaspersoftStudioPlugin.getInstance().getImage("icons/resources/edit-bold.png"));
			}
		};
		boldD.setDescription(Messages.MFont_bold_description);
		desc.add(boldD);

		CheckBoxPropertyDescriptor italicD = new CheckBoxPropertyDescriptor(JRBaseFont.PROPERTY_ITALIC,
				Messages.common_italic, NullEnum.INHERITED) {
			@Override
			public ASPropertyWidget<CheckBoxPropertyDescriptor> createWidget(Composite parent, AbstractSection section) {
				return new SPBooleanToggle(parent, section, this,
						JaspersoftStudioPlugin.getInstance().getImage("icons/resources/edit-italic.png"));
			}
		};
		italicD.setDescription(Messages.MFont_italic_description);
		desc.add(italicD);

		CheckBoxPropertyDescriptor underlineD = new CheckBoxPropertyDescriptor(JRBaseFont.PROPERTY_UNDERLINE,
				Messages.common_underline, NullEnum.INHERITED) {
			@Override
			public ASPropertyWidget<CheckBoxPropertyDescriptor> createWidget(Composite parent, AbstractSection section) {
				return new SPBooleanToggle(parent, section, this,
						JaspersoftStudioPlugin.getInstance().getImage("icons/resources/edit-underline.png"));
			}
		};
		underlineD.setDescription(Messages.MFont_underline_description);
		desc.add(underlineD);

		CheckBoxPropertyDescriptor strikeTroughD = new CheckBoxPropertyDescriptor(JRBaseFont.PROPERTY_STRIKE_THROUGH,
				Messages.common_strike_trough, NullEnum.INHERITED) {
			@Override
			public ASPropertyWidget<CheckBoxPropertyDescriptor> createWidget(Composite parent, AbstractSection section) {
				return new SPBooleanToggle(parent, section, this,
						JaspersoftStudioPlugin.getInstance().getImage("icons/resources/edit-strike.png"));
			}
		};
		strikeTroughD.setDescription(Messages.MFont_strike_trough_description);
		desc.add(strikeTroughD);

		CheckBoxPropertyDescriptor pdfEmbedD = new CheckBoxPropertyDescriptor(JRBaseFont.PROPERTY_PDF_EMBEDDED,
				Messages.MFont_pdf_embedded, NullEnum.INHERITED);
		pdfEmbedD.setDescription(Messages.MFont_pdf_embedded_description);
		pdfEmbedD.setCategory(Messages.MFont_pdfCategory);
		desc.add(pdfEmbedD);

		fontNameD.setCategory(Messages.common_font);
		fontSizeD.setCategory(Messages.common_font);
		boldD.setCategory(Messages.common_font);
		italicD.setCategory(Messages.common_font);
		underlineD.setCategory(Messages.common_font);
		strikeTroughD.setCategory(Messages.common_font);

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#font");
	}
	
	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();
		
		defaultsMap.put(JRBaseFont.PROPERTY_FONT_NAME, new DefaultValue("SansSerif", true)); //$NON-NLS-1$
		defaultsMap.put(JRBaseFont.PROPERTY_FONT_SIZE, new DefaultValue(10f, true)); 
		defaultsMap.put(JRBaseFont.PROPERTY_STRIKE_THROUGH, new DefaultValue(null, true));
		defaultsMap.put(JRBaseFont.PROPERTY_UNDERLINE, new DefaultValue(null, true));
		defaultsMap.put(JRBaseFont.PROPERTY_ITALIC, new DefaultValue(null, true));
		defaultsMap.put(JRBaseFont.PROPERTY_BOLD, new DefaultValue(null, true));
		
		return defaultsMap;
	}


	@Override
	public IPropertyDescriptor[] getDescriptors() {
		return descriptors;
	}

	@Override
	public void setDescriptors(IPropertyDescriptor[] descriptors1) {
		descriptors = descriptors1;
	}

	@Override
	public JRFont getValue() {
		return (JRFont) super.getValue();
	}

	public Object getPropertyActualValue(Object id) {
		JRFont jrElement = getValue();
		if (id.equals(JRBaseFont.PROPERTY_BOLD))
			return jrElement.isBold();
		if (id.equals(JRBaseFont.PROPERTY_UNDERLINE))
			return jrElement.isUnderline();
		if (id.equals(JRBaseFont.PROPERTY_ITALIC))
			return jrElement.isItalic();
		if (id.equals(JRBaseFont.PROPERTY_STRIKE_THROUGH))
			return jrElement.isStrikeThrough();
		if (id.equals(JRBaseFont.PROPERTY_PDF_EMBEDDED))
			return jrElement.isPdfEmbedded();
		if (id.equals(JRBaseFont.PROPERTY_FONT_NAME))
			return jrElement.getFontName();
		if (id.equals(JRBaseFont.PROPERTY_PDF_FONT_NAME))
			return jrElement.getPdfFontName();
		if (id.equals(JRBaseFont.PROPERTY_PDF_ENCODING))
			return ModelUtils.getKey4PDFEncoding(jrElement.getPdfEncoding());
		if (id.equals(JRBaseFont.PROPERTY_FONT_SIZE))
			return jrElement.getFontsize(); // $NON-NLS-1$
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		JRFont jrElement = getValue();
		if (id.equals(JRBaseFont.PROPERTY_BOLD))
			return jrElement.isOwnBold();
		if (id.equals(JRBaseFont.PROPERTY_UNDERLINE))
			return jrElement.isOwnUnderline();
		if (id.equals(JRBaseFont.PROPERTY_ITALIC))
			return jrElement.isOwnItalic();
		if (id.equals(JRBaseFont.PROPERTY_STRIKE_THROUGH))
			return jrElement.isOwnStrikeThrough();
		if (id.equals(JRBaseFont.PROPERTY_PDF_EMBEDDED))
			return jrElement.isOwnPdfEmbedded();
		if (id.equals(JRBaseFont.PROPERTY_FONT_NAME))
			return jrElement.getOwnFontName();
		if (id.equals(JRBaseFont.PROPERTY_PDF_FONT_NAME))
			return jrElement.getOwnPdfFontName();
		if (id.equals(JRBaseFont.PROPERTY_PDF_ENCODING))
			return ModelUtils.getKey4PDFEncoding(jrElement.getOwnPdfEncoding());
		if (id.equals(JRBaseFont.PROPERTY_FONT_SIZE))
			return jrElement.getOwnFontsize(); //$NON-NLS-1$
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		JRFont jrElement = getValue();
		if (id.equals(JRBaseFont.PROPERTY_BOLD))
			jrElement.setBold((Boolean) value);
		else if (id.equals(JRBaseFont.PROPERTY_UNDERLINE))
			jrElement.setUnderline((Boolean) value);
		else if (id.equals(JRBaseFont.PROPERTY_ITALIC))
			jrElement.setItalic((Boolean) value);
		else if (id.equals(JRBaseFont.PROPERTY_STRIKE_THROUGH))
			jrElement.setStrikeThrough((Boolean) value);
		else if (id.equals(JRBaseFont.PROPERTY_PDF_EMBEDDED))
			jrElement.setPdfEmbedded((Boolean) value);
		else if (id.equals(JRBaseFont.PROPERTY_FONT_NAME)) {
			if (value instanceof String) {
				if (((String) value).isEmpty())
					value = null;
				jrElement.setFontName((String) value);
			} else if (value == null) {
				jrElement.setFontName((String) value);
			}
		} else if (id.equals(JRBaseFont.PROPERTY_FONT_SIZE)){
			jrElement.setFontSize((Float)value);
		} else if (id.equals(JRBaseFont.PROPERTY_PDF_FONT_NAME))
			jrElement.setPdfFontName((String) value);
		else if (id.equals(JRBaseFont.PROPERTY_PDF_ENCODING))
			jrElement.setPdfEncoding(ModelUtils.getPDFEncoding2key((String) value));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		if (name.equals(JRBaseFont.PROPERTY_BOLD) || name.equals(JRBaseFont.PROPERTY_UNDERLINE)
				|| name.equals(JRBaseFont.PROPERTY_ITALIC) || name.equals(JRBaseFont.PROPERTY_STRIKE_THROUGH)
				|| name.equals(JRBaseFont.PROPERTY_PDF_EMBEDDED) || name.equals(JRBaseFont.PROPERTY_FONT_NAME)
				|| name.equals(JRBaseFont.PROPERTY_FONT_SIZE) || name.equals(JRBaseFont.PROPERTY_PDF_FONT_NAME)
				|| name.equals(JRBaseFont.PROPERTY_PDF_ENCODING))
			super.propertyChange(evt);

	}
}
