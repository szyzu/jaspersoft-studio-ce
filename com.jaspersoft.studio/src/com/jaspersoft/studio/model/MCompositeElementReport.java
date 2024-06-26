/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.FormatFactory;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.help.HelpReferenceBuilder;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.checkbox.CheckBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.classname.ImportDeclarationPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.classname.NClassTypePropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.combo.RWComboBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.properties.JPropertiesPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.IntegerPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.JSSTextPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.NamedEnumPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.PixelPropertyDescriptor;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.report.util.PHolderUtil;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.SPToolBarEnum;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

/**
 * Define the report node used for the composite elements report. This node hide some properties of the standard report,
 * like the dataset stuff
 * 
 * @author Orlandin Marco
 *
 */
public class MCompositeElementReport extends MReport {

	private static final long serialVersionUID = -3601244815409007973L;

	private IPropertyDescriptor[] descriptors;

	public MCompositeElementReport(ANode parent, JasperReportsConfiguration jConfig) {
		super(parent, jConfig);
	}

	@Override
	public IPropertyDescriptor[] getDescriptors() {
		return descriptors;
	}

	@Override
	public void setDescriptors(IPropertyDescriptor[] descriptors1) {
		descriptors = descriptors1;
	}

	/**
	 * Essentially it is the same of the standard MReport, but it doesn't include the dataset section
	 */
	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {

		ImportDeclarationPropertyDescriptor importsD = new ImportDeclarationPropertyDescriptor(
				JasperDesign.PROPERTY_IMPORTS, Messages.MReport_imports);
		importsD.setDescription(Messages.MReport_imports_description);
		desc.add(importsD);
		importsD.setHelpRefBuilder(
				new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#import")); //$NON-NLS-1$

		JSSTextPropertyDescriptor nameD = new JSSTextPropertyDescriptor(JasperDesign.PROPERTY_NAME,
				Messages.MReport_report_name);
		nameD.setDescription(Messages.MReport_report_name_description);
		nameD.setCategory(Messages.common_report);
		desc.add(nameD);

		NClassTypePropertyDescriptor formatFactoryClassD = new NClassTypePropertyDescriptor(
				JasperDesign.PROPERTY_FORMAT_FACTORY_CLASS, Messages.MReport_format_factory_class);
		formatFactoryClassD.setDescription(Messages.MReport_format_factory_class_description);
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(FormatFactory.class);
		formatFactoryClassD.setClasses(classes);
		desc.add(formatFactoryClassD);

		// -------------------
		PixelPropertyDescriptor heightD = new PixelPropertyDescriptor(JasperDesign.PROPERTY_PAGE_HEIGHT,
				Messages.MReport_page_height);
		heightD.setDescription(Messages.MReport_page_height_description);
		heightD.setCategory(Messages.MReport_report_page_category);
		desc.add(heightD);

		IntegerPropertyDescriptor widthD = new IntegerPropertyDescriptor(JasperDesign.PROPERTY_PAGE_WIDTH,
				Messages.MReport_page_width);
		widthD.setDescription(Messages.MReport_page_width_description);
		widthD.setCategory(Messages.MReport_report_page_category);
		desc.add(widthD);

		IntegerPropertyDescriptor rightMarginD = new IntegerPropertyDescriptor(JasperDesign.PROPERTY_RIGHT_MARGIN,
				Messages.MReport_right_margin);
		rightMarginD.setDescription(Messages.MReport_right_margin_description);
		rightMarginD.setCategory(Messages.MReport_report_page_category);
		desc.add(rightMarginD);

		IntegerPropertyDescriptor leftMarginD = new IntegerPropertyDescriptor(JasperDesign.PROPERTY_LEFT_MARGIN,
				Messages.MReport_left_margin);
		leftMarginD.setDescription(Messages.MReport_left_margin_description);
		leftMarginD.setCategory(Messages.MReport_report_page_category);
		desc.add(leftMarginD);

		IntegerPropertyDescriptor topMarginD = new IntegerPropertyDescriptor(JasperDesign.PROPERTY_TOP_MARGIN,
				Messages.MReport_top_margin);
		topMarginD.setDescription(Messages.MReport_top_margin_description);
		topMarginD.setCategory(Messages.MReport_report_page_category);
		desc.add(topMarginD);

		IntegerPropertyDescriptor bottomMarginD = new IntegerPropertyDescriptor(JasperDesign.PROPERTY_BOTTOM_MARGIN,
				Messages.MReport_bottom_margin);
		bottomMarginD.setDescription(Messages.MReport_bottom_margin_description);
		bottomMarginD.setCategory(Messages.MReport_report_page_category);
		desc.add(bottomMarginD);

		IntegerPropertyDescriptor columnCountD = new IntegerPropertyDescriptor(JasperDesign.PROPERTY_COLUMN_COUNT,
				Messages.MReport_column_count);
		columnCountD.setDescription(Messages.MReport_column_count_description);
		columnCountD.setCategory(Messages.MReport_columns_category);
		desc.add(columnCountD);

		IntegerPropertyDescriptor columnWidthD = new IntegerPropertyDescriptor(JasperDesign.PROPERTY_COLUMN_WIDTH,
				Messages.MReport_column_width);
		columnWidthD.setDescription(Messages.MReport_column_width_description);
		columnWidthD.setCategory(Messages.MReport_columns_category);
		desc.add(columnWidthD);

		IntegerPropertyDescriptor columnSpaceD = new IntegerPropertyDescriptor(JasperDesign.PROPERTY_COLUMN_SPACING,
				Messages.MReport_column_space);
		columnSpaceD.setDescription(Messages.MReport_column_space_description);
		columnSpaceD.setCategory(Messages.MReport_columns_category);
		desc.add(columnSpaceD);

		RWComboBoxPropertyDescriptor languageD = new RWComboBoxPropertyDescriptor(JasperDesign.PROPERTY_LANGUAGE,
				Messages.common_language, ModelUtils.getDefaultReportLanguages(), NullEnum.NOTNULL, false);
		languageD.setDescription(Messages.MReport_language_description);
		languageD.setCategory(Messages.common_report);
		desc.add(languageD);

		NamedEnumPropertyDescriptor<OrientationEnum> orientationD = new NamedEnumPropertyDescriptor<OrientationEnum>(
				JasperDesign.PROPERTY_ORIENTATION, Messages.MReport_page_orientation, OrientationEnum.LANDSCAPE,
				NullEnum.NOTNULL) {
			@Override
			public ASPropertyWidget<NamedEnumPropertyDescriptor<OrientationEnum>> createWidget(Composite parent,
					AbstractSection section) {
				Image[] images = new Image[] { JaspersoftStudioPlugin.getInstance().getImage("icons/resources/portrait16.png"), //$NON-NLS-1$
						JaspersoftStudioPlugin.getInstance().getImage("icons/resources/landscape16.png") }; //$NON-NLS-1$
				return new SPToolBarEnum<NamedEnumPropertyDescriptor<OrientationEnum>>(parent, section, this, images);
			}
		};
		orientationD.setDescription(Messages.MReport_page_orientation_description);
		orientationD.setCategory(Messages.MReport_report_page_category);
		desc.add(orientationD);

		NamedEnumPropertyDescriptor<PrintOrderEnum> printOrderD = new NamedEnumPropertyDescriptor<PrintOrderEnum>(
				JasperDesign.PROPERTY_PRINT_ORDER, Messages.MReport_print_order, PrintOrderEnum.HORIZONTAL, NullEnum.NULL);
		printOrderD.setDescription(Messages.MReport_print_order_description);
		printOrderD.setCategory(Messages.MReport_columns_category);
		desc.add(printOrderD);

		NamedEnumPropertyDescriptor<WhenNoDataTypeEnum> whenNoDataD = new NamedEnumPropertyDescriptor<WhenNoDataTypeEnum>(
				JasperDesign.PROPERTY_WHEN_NO_DATA_TYPE, Messages.MReport_when_no_data_type,
				WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL, NullEnum.NULL);
		whenNoDataD.setDescription(Messages.MReport_when_no_data_type_description);
		whenNoDataD.setCategory(Messages.common_report);
		desc.add(whenNoDataD);

		// checkboxes
		CheckBoxPropertyDescriptor titleNewPageD = new CheckBoxPropertyDescriptor(JasperDesign.PROPERTY_TITLE_NEW_PAGE,
				Messages.MReport_title_on_a_new_page);
		titleNewPageD.setDescription(Messages.MReport_title_on_a_new_page_description);
		desc.add(titleNewPageD);

		CheckBoxPropertyDescriptor summaryNewPageD = new CheckBoxPropertyDescriptor(JasperDesign.PROPERTY_SUMMARY_NEW_PAGE,
				Messages.MReport_summary_on_a_new_page);
		summaryNewPageD.setDescription(Messages.MReport_summary_on_a_new_page_description);
		desc.add(summaryNewPageD);

		CheckBoxPropertyDescriptor summaryWHFD = new CheckBoxPropertyDescriptor(
				JasperDesign.PROPERTY_SUMMARY_WITH_PAGE_HEADER_AND_FOOTER,
				Messages.MReport_summary_with_page_header_and_footer);
		summaryWHFD.setDescription(Messages.MReport_summary_with_page_header_and_footer_description);
		desc.add(summaryWHFD);

		CheckBoxPropertyDescriptor floatColumnFooterD = new CheckBoxPropertyDescriptor(
				JasperDesign.PROPERTY_FLOAT_COLUMN_FOOTER, Messages.MReport_float_column_footer);
		floatColumnFooterD.setDescription(Messages.MReport_float_column_footer_description);
		desc.add(floatColumnFooterD);

		CheckBoxPropertyDescriptor ignorePaginationD = new CheckBoxPropertyDescriptor(
				JasperDesign.PROPERTY_IGNORE_PAGINATION, Messages.MReport_ignore_pagination);
		ignorePaginationD.setDescription(Messages.MReport_ignore_pagination_description);
		desc.add(ignorePaginationD);

		JPropertiesPropertyDescriptor propertiesMapD = new JPropertiesPropertyDescriptor(MGraphicElement.PROPERTY_MAP,
				Messages.common_properties, getJasperConfiguration(), getValue());
		propertiesMapD.setDescription(Messages.common_properties);
		desc.add(propertiesMapD);

		CheckBoxPropertyDescriptor createBookmarks = new CheckBoxPropertyDescriptor(PROPERY_CREATE_BOOKMARKS,
				Messages.MReport_createBookmarksTitle);
		titleNewPageD.setDescription(Messages.MReport_createBookmarksDescription);
		desc.add(createBookmarks);
		
		JSSTextPropertyDescriptor descD = new JSSTextPropertyDescriptor(
				PHolderUtil.COM_JASPERSOFT_STUDIO_REPORT_DESCRIPTION, Messages.common_description);
		descD.setCategory(Messages.common_report);
		descD.setDescription(Messages.common_description);
		desc.add(descD);

		titleNewPageD.setCategory(Messages.MReport_pagination);
		ignorePaginationD.setCategory(Messages.MReport_pagination);
		summaryNewPageD.setCategory(Messages.MReport_pagination);
		floatColumnFooterD.setCategory(Messages.MReport_pagination);
		summaryWHFD.setCategory(Messages.MReport_pagination);

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#jasperReport"); //$NON-NLS-1$
	}

	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();

		defaultsMap.put(JasperDesign.PROPERTY_PAGE_WIDTH, new DefaultValue(new Integer(595), false));
		defaultsMap.put(JasperDesign.PROPERTY_PAGE_HEIGHT, new DefaultValue(new Integer(842), false));
		defaultsMap.put(JasperDesign.PROPERTY_TOP_MARGIN, new DefaultValue(new Integer(30), false));
		defaultsMap.put(JasperDesign.PROPERTY_BOTTOM_MARGIN, new DefaultValue(new Integer(30), false));
		defaultsMap.put(JasperDesign.PROPERTY_LEFT_MARGIN, new DefaultValue(new Integer(20), false));
		defaultsMap.put(JasperDesign.PROPERTY_RIGHT_MARGIN, new DefaultValue(new Integer(20), false));
		defaultsMap.put(JasperDesign.PROPERTY_LANGUAGE, new DefaultValue("Java", false)); //$NON-NLS-1$
		defaultsMap.put(JasperDesign.PROPERTY_COLUMN_COUNT, new DefaultValue(new Integer(1), false));
		defaultsMap.put(JasperDesign.PROPERTY_COLUMN_WIDTH, new DefaultValue(new Integer(555), false));
		defaultsMap.put(JasperDesign.PROPERTY_COLUMN_SPACING, new DefaultValue(new Integer(0), false));
		defaultsMap.put(JasperDesign.PROPERTY_TITLE_NEW_PAGE, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JasperDesign.PROPERTY_SUMMARY_NEW_PAGE, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JasperDesign.PROPERTY_SUMMARY_WITH_PAGE_HEADER_AND_FOOTER, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JasperDesign.PROPERTY_FLOAT_COLUMN_FOOTER, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JasperDesign.PROPERTY_IGNORE_PAGINATION, new DefaultValue(Boolean.FALSE, false));

		int orientationValue = NamedEnumPropertyDescriptor.getIntValue(OrientationEnum.PORTRAIT, NullEnum.NOTNULL,
				OrientationEnum.PORTRAIT);
		defaultsMap.put(JasperDesign.PROPERTY_ORIENTATION, new DefaultValue(orientationValue, false));

		int printOrderValue = NamedEnumPropertyDescriptor.getIntValue(PrintOrderEnum.VERTICAL, NullEnum.NULL,
				PrintOrderEnum.VERTICAL);
		defaultsMap.put(JasperDesign.PROPERTY_PRINT_ORDER, new DefaultValue(printOrderValue, true));

		int whenNoDataValue = NamedEnumPropertyDescriptor.getIntValue(WhenNoDataTypeEnum.NO_PAGES, NullEnum.NULL,
				WhenNoDataTypeEnum.NO_PAGES);
		defaultsMap.put(JasperDesign.PROPERTY_WHEN_NO_DATA_TYPE, new DefaultValue(whenNoDataValue, true));

		return defaultsMap;
	}
}
