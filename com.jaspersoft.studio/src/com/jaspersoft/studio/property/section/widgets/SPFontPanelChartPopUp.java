/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.wb.swt.ResourceManager;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.jface.IntegerCellEditorValidator;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.text.MFont;
import com.jaspersoft.studio.preferences.fonts.FontsPreferencePage;
import com.jaspersoft.studio.property.combomenu.ComboItem;
import com.jaspersoft.studio.property.combomenu.ComboItemAction;
import com.jaspersoft.studio.property.combomenu.ComboItemSeparator;
import com.jaspersoft.studio.property.combomenu.ComboMenuViewer;
import com.jaspersoft.studio.property.descriptor.combo.FontNamePropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.combo.RWComboBoxPropertyDescriptor;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.swt.widgets.NumericCombo;
import com.jaspersoft.studio.utils.ImageUtils;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.design.JRDesignFont;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.util.StyleResolver;

/**
 * This class implement the subsection into the chart property tab using the
 * font combo for the font name
 * 
 * @author Chicu Veaceslav & Orlandin Marco
 *
 */
public class SPFontPanelChartPopUp extends ASPropertyWidget<IPropertyDescriptor> {
	private final class PreferenceListener implements IPropertyChangeListener {

		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			if (event.getProperty().equals(FontsPreferencePage.FPP_FONT_LIST)) {
				if (parentNode != null) {
					List<String[]> fonts = ModelUtils.getFontNames(parentNode.getJasperConfiguration());
					fontName.setItems(stringToItems(fonts));
				}
			}
		}
	}

	private PreferenceListener preferenceListener;

	/**
	 * The combo popup with the font names
	 */
	private ComboMenuViewer fontName;

	/**
	 * The combo with the font size
	 */
	private NumericCombo fontSize;

	/**
	 * Buttom for the attribute bold
	 */
	private ToolItem boldButton;

	/**
	 * Button for the attribute italic
	 */
	private ToolItem italicButton;

	/**
	 * Button for the attribute underline
	 */
	private ToolItem underlineButton;

	/**
	 * Button for the attribute striketrought
	 */
	private ToolItem strikeTroughtButton;

	/**
	 * Flag to check if the font name was already been inserted into the combo
	 * popup
	 */
	private boolean itemsSetted;

	/**
	 * Font model
	 */
	private MFont mfont;

	/**
	 * Node represented
	 */
	private APropertyNode parentNode;

	/**
	 * Composite where the control will be placed
	 */
	private Composite group;

	public SPFontPanelChartPopUp(Composite parent, AbstractSection section, IPropertyDescriptor pDescriptor) {
		super(parent, section, pDescriptor);
		preferenceListener = new PreferenceListener();
		JaspersoftStudioPlugin.getInstance()
				.addPreferenceListener(preferenceListener,
				(IResource) section.getJasperReportsContext().get(FileUtils.KEY_FILE));
		itemsSetted = false;
	}

	@Override
	public Control getControl() {
		return group.getParent();
	}

	/**
	 * Convert a list of array of string into a List of ComboItem, ready to be
	 * inserted into a combo popup
	 * 
	 * @param fontsList List of array of fonts, between every array will be
	 * inserted a separator
	 * @return List of combo item
	 */
	private List<ComboItem> stringToItems(List<String[]> fontsList) {
		int i = 0;
		List<ComboItem> itemsList = new ArrayList<ComboItem>();
		FontUtil util = FontUtil.getInstance(parentNode.getJasperConfiguration());
		for (int index = 0; index < fontsList.size(); index++) {
			String[] fonts = fontsList.get(index);
			for (String element : fonts) {
				Image resolvedImage = ResourceManager.getImage(element);
				if (resolvedImage == null) {
					resolvedImage = new Image(null,
							ImageUtils.convertToSWT(SPFontNamePopUp.createFontImage(element, util)));
					ResourceManager.addImage(element, resolvedImage);
				}
				itemsList.add(new ComboItem(element, true, resolvedImage, i, element, element));
				i++;
			}
			if (index + 1 != fontsList.size() && fonts.length > 0) {
				itemsList.add(new ComboItemSeparator(i));
				i++;
			}
		}
		return itemsList;
	}

	/**
	 * Property change action
	 * 
	 * @param section
	 * @param property
	 * @param value
	 * @param pd
	 */
	public void propertyChange(AbstractSection section, String property, String value, FontNamePropertyDescriptor pd) {
		changeProperty(section, pDescriptor.getId(), pd.getId(), value);
	}

	/**
	 * The increment\decrement font size button, adapted to the chart font
	 * structure
	 * 
	 * @author Orlandin Marco
	 *
	 */
	private class SPChartButtom<T extends IPropertyDescriptor> extends SPFontSize<T> {

		/**
		 * The type of font represented (title, legend, subtitle)
		 */
		private String fontNameProperty;

		public SPChartButtom(Composite parent, AbstractSection section, T pDescriptor, APropertyNode fontValue,
				String fontNameProperty) {
			super(parent, section, pDescriptor, fontValue);
			this.fontNameProperty = fontNameProperty;
		}

		/**
		 * The ovverrided version first change the font into a temp. mfont item,
		 * the use this object to replace the font size inside the chart model
		 */
		@Override
		protected void createCommand(boolean increment) {
			Float fontSizeValue = fontSize.getValueAsFloat();
			Float newValue = 2f;
			if (fontSizeValue != null) {
				newValue = fontSizeValue;
				Integer plus = null;
				if (increment)
					plus = Math.round((new Float(newValue) / 100) * SPFontSize.factor) + 1;
				else
					plus = Math.round((new Float(newValue) / 100) * -SPFontSize.factor) - 1;
				if ((newValue + plus) > 99f)
					newValue = 99f;
				else if ((newValue + plus) > 0f)
					newValue += plus;
				section.changePropertyOn(JRBaseFont.PROPERTY_FONT_SIZE, newValue, mfont);
				section.changePropertyOn(fontNameProperty, new MFont((JRFont) mfont.getValue()), parentNode);
			}
		}
	}

	protected void createComponent(Composite parent) {
		mfont = new MFont(new JRDesignFont(null));
		group = section.getWidgetFactory().createSection(parent, pDescriptor.getDisplayName(), true, 3);

		final FontNamePropertyDescriptor pd = (FontNamePropertyDescriptor) mfont
				.getPropertyDescriptor(JRBaseStyle.PROPERTY_FONT_NAME);
		fontName = new ComboMenuViewer(group, ComboMenuViewer.NO_IMAGE, "SampleSampleSample");
		fontName.setToolTipText(pd.getDescription());
		fontName.addSelectionListener(new ComboItemAction() {
			@Override
			public void exec() {
				propertyChange(section, JRBaseFont.PROPERTY_FONT_NAME,
						fontName.getSelectionValue() != null ? fontName.getSelectionValue().toString() : null, pd);
			}
		});
		fontName.getControl().addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				JaspersoftStudioPlugin.getInstance().removePreferenceListener(preferenceListener);
			}
		});

		final RWComboBoxPropertyDescriptor pd1 = (RWComboBoxPropertyDescriptor) mfont
				.getPropertyDescriptor(JRBaseStyle.PROPERTY_FONT_SIZE);

		Composite fontSizeLayout = new Composite(group, SWT.NONE);
		GridData fontSizeData = new GridData();
		fontSizeData.widthHint = 65;
		fontSizeData.minimumWidth = 65;
		fontSizeLayout.setLayout(new GridLayout(1, false));
		fontSizeLayout.setLayoutData(fontSizeData);
		fontSize = new NumericCombo(fontSizeLayout, SWT.FLAT, 0, 6);
		fontSize.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fontSize.setItems(pd1.getItems());
		fontSize.addSelectionListener(new SelectionAdapter() {

			private int time = 0;

			public void widgetSelected(SelectionEvent e) {
				if (e.time - time > 100) {
					String value = fontSize.getText();
					if (IntegerCellEditorValidator.instance().isValid(value) == null)
						changeProperty(section, pDescriptor.getId(), pd1.getId(), value);
				}
				time = e.time;
			}
		});
		fontSize.setToolTipText(pd1.getDescription());

		/*
		 * Button to increment\decrment the font size
		 */
		new SPChartButtom<IPropertyDescriptor>(group, section, pd1, mfont, pDescriptor.getId().toString());

		ToolBar toolBar = new ToolBar(group, SWT.FLAT | SWT.WRAP | SWT.LEFT);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		toolBar.setLayoutData(gd);

		boldButton = createItem(toolBar, JRBaseStyle.PROPERTY_BOLD, "icons/resources/edit-bold.png");

		italicButton = createItem(toolBar, JRBaseStyle.PROPERTY_ITALIC, "icons/resources/edit-italic.png");

		underlineButton = createItem(toolBar, JRBaseStyle.PROPERTY_UNDERLINE, "icons/resources/edit-underline.png");

		strikeTroughtButton = createItem(toolBar, JRBaseStyle.PROPERTY_STRIKE_THROUGH,
				"icons/resources/edit-strike.png");
	}

	/**
	 * Create a tool bar button
	 * 
	 * @param toolBar the parent tool bar
	 * @param id the id of the property changed by the button press
	 * @param image the image of the tool button
	 * @return the created tool button
	 */
	private ToolItem createItem(ToolBar toolBar, Object id, String image) {
		final IPropertyDescriptor ipd = mfont.getPropertyDescriptor(id);

		final ToolItem item = new ToolItem(toolBar, SWT.CHECK);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changeProperty(section, pDescriptor.getId(), ipd.getId(), new Boolean(item.getSelection()));
			}
		});
		item.setImage(JaspersoftStudioPlugin.getInstance().getImage(image)); // $NON-NLS-1$
		item.setToolTipText(ipd.getDescription());
		return item;
	}

	private void changeProperty(AbstractSection section, Object property, Object prop, Object value) {
		section.changePropertyOn(prop, value, mfont);
		if (property != null && parentNode != null)
			section.changePropertyOn(property, new MFont((JRFont) mfont.getValue()), parentNode);
	}

	/**
	 * Set the font size value on the widget, setting also the information if it
	 * is inherited or not
	 * 
	 * @param resolvedNumber the font size value resolved
	 * @param ownNumber the font size value of the element selected
	 */
	public void setFontSizeNumber(Number resolvedNumber, Number ownNumber) {
		if (resolvedNumber != null) {
			int oldpos = fontSize.getCaretPosition();
			if (ownNumber == null) {
				fontSize.setDefaultValue(resolvedNumber);
			}
			fontSize.setValue(ownNumber);
			if (fontSize.getText().length() >= oldpos) {
				fontSize.setSelection(new Point(oldpos, oldpos));
			}
		} else if (ownNumber != null) {
			int oldpos = fontSize.getCaretPosition();
			fontSize.setValue(ownNumber);
			if (fontSize.getText().length() >= oldpos) {
				fontSize.setSelection(new Point(oldpos, oldpos));
			}
		} else {
			fontSize.setValue(null);
		}
	}

	/**
	 * Set the font name, the font size and the font attribute in the respective
	 * controls
	 */
	public void setData(APropertyNode pnode, Object value) {
		this.parentNode = pnode;
		this.mfont = (MFont) value;
		if (mfont != null) {

			JRFont fontValue = (JRFont) mfont.getValue();

			if (!itemsSetted) {
				fontName.setItems(stringToItems(ModelUtils.getFontNames(parentNode.getJasperConfiguration())));
				itemsSetted = true;
			}
			StyleResolver sr = new StyleResolver(mfont.getJasperConfiguration());
			String strfontname = sr.getFontName(fontValue);
			fontName.setText(strfontname);

			setFontSizeNumber(fontValue.getFontsize(), fontValue.getOwnFontsize());

			Boolean b = sr.isBold(fontValue);
			boldButton.setSelection(b != null ? b.booleanValue() : false);
			b = sr.isItalic(fontValue);
			italicButton.setSelection(b != null ? b.booleanValue() : false);
			b = sr.isUnderline(fontValue);
			underlineButton.setSelection(b != null ? b.booleanValue() : false);
			b = sr.isStrikeThrough(fontValue);
			strikeTroughtButton.setSelection(b != null ? b.booleanValue() : false);
		}
	}
}
