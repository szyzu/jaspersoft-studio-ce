/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.widgets.framework.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.property.section.report.util.Unit;
import com.jaspersoft.studio.property.section.report.util.Unit.PixelConversionException;
import com.jaspersoft.studio.property.section.widgets.SPPixel;
import com.jaspersoft.studio.property.section.widgets.SPPixel.MeasureUnit;
import com.jaspersoft.studio.utils.UIUtil;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.widgets.framework.IWItemProperty;
import com.jaspersoft.studio.widgets.framework.WItemProperty;
import com.jaspersoft.studio.widgets.framework.manager.DoubleControlComposite;
import com.jaspersoft.studio.widgets.framework.model.WidgetPropertyDescriptor;
import com.jaspersoft.studio.widgets.framework.model.WidgetsDescriptor;
import com.jaspersoft.studio.widgets.framework.ui.dialog.ItemPropertyElementDialog;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

/**
 * Widget used to insert values in different measure units
 * 
 * @author Orlandin Marco
 *
 */
public class MeasureUnitPropertyDescription extends AbstractMeasurePropertyDescription<Long> {

	public MeasureUnitPropertyDescription() {
	}

	public MeasureUnitPropertyDescription(String name, String label, String description, boolean mandatory,
			Long defaultValue, long min, long max) {
		super(name, label, description, mandatory, defaultValue, min, max);
	}

	public MeasureUnitPropertyDescription(String name, String label, String description, boolean mandatory, long min,
			long max) {
		super(name, label, description, mandatory, min, max);
	}

	@Override
	public Control createControl(final IWItemProperty wiProp, Composite parent) {
		DoubleControlComposite cmp = new DoubleControlComposite(parent, SWT.NONE);
		cmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		lazyCreateExpressionControl(wiProp, cmp);

		final Text simpleControl = new Text(cmp.getSecondContainer(), SWT.BORDER); // $NON-NLS-1$
		// Flag used to overcome the problem of focus events in Mac OS X
		// - JSS Bugzilla 42999
		// - Eclipse Bug 383750
		// It makes sense only on E4 platform and Mac OS X operating systems.
		FocusListener focusListener = new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if (UIUtil.isMacAndEclipse4()) {
					if (((Text) e.getSource()).isDisposed())
						return;
					wiProp.updateWidget();
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				handleEdit(simpleControl, wiProp);
			}
		};
		simpleControl.addFocusListener(focusListener);
		// Store inside the control the focus listener, so it can be removed and
		// added
		// another time in some case
		simpleControl.setData(FOCUS_KEY, focusListener);

		simpleControl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR)
					handleEdit(simpleControl, wiProp);
			}
		});
		simpleControl.addMouseListener(new MouseClickListener(simpleControl, wiProp));

		// add the autocomplete only if there are more then one entry
		if (SPPixel.autocompleteValues.length > 1)
			new AutoCompleteField(simpleControl, new AutoCompleteMeasure(simpleControl), SPPixel.autocompleteValues);

		defaultBackgroundColor = simpleControl.getBackground();
		cmp.getSecondContainer().setData(simpleControl);
		cmp.setSimpleControlToHighlight(simpleControl);
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.verticalAlignment = SWT.CENTER;
		textData.grabExcessVerticalSpace = true;
		simpleControl.setLayoutData(textData);
		setupContextMenu(simpleControl, wiProp);
		cmp.switchToFirstContainer();
		return cmp;
	}

	/**
	 * Set the value inside the correct control, if the editor is in expression
	 * mode or not
	 */
	@Override
	public void update(Control c, IWItemProperty wip) {
		DoubleControlComposite cmp = (DoubleControlComposite) wip.getControl();
		if (wip.isExpressionMode()) {
			lazyCreateExpressionControl(wip, cmp);
			Text expressionControl = (Text) cmp.getFirstContainer().getData();
			super.update(expressionControl, wip);
			cmp.switchToFirstContainer();
			expressionControl.setToolTipText(getToolTip(wip, expressionControl.getText()));
		} else {
			boolean isFallback = false;
			Text simpleControl = (Text) cmp.getSecondContainer().getData();
			String v = wip.getStaticValue();
			if (v == null && wip.getFallbackValue() != null) {
				v = wip.getFallbackValue().toString();
				isFallback = true;
			}
			Number n = v != null ? Long.parseLong(v) : null;
			setDataNumber(n, simpleControl, wip);
			// String errorMessage = getErrorMessages();
			// setErrorStatus(errorMessage, simpleControl);

			changeFallbackForeground(isFallback, simpleControl);
			cmp.switchToSecondContainer();
			simpleControl.setToolTipText(getToolTip(wip, simpleControl.getText()));
		}
	}

	/**
	 * Receive a number and set it in the text widget
	 * 
	 * @param f the number
	 * @param insertField the text widget, must be not null
	 * @param wItemProp the {@link IWItemProperty} used to read the current
	 * measure unit from the model
	 */
	public void setDataNumber(Number f, Text insertField, IWItemProperty wItemProp) {
		if (f != null) {
			int oldpos = insertField.getCaretPosition();
			setPixels(f.toString(), insertField, wItemProp);
			if (insertField.getText().length() >= oldpos)
				insertField.setSelection(oldpos, oldpos);
		} else
			insertField.setText(""); //$NON-NLS-1$
	}

	@Override
	public String getToolTip(IWItemProperty wip, String value) {
		return super.getToolTip(wip, value) + "\n" + getToolTip();
	}

	@Override
	public String getToolTip() {
		String tt = super.getToolTip();
		if (getMin() != null)
			tt += "\nmin: " + getMin();
		if (getMax() != null)
			tt += "\nmax: " + getMax();
		return tt;
	}

	/**
	 * Create the popup menu
	 * 
	 * @param insertField the Text widget where the menu is set, must be not
	 * null
	 * @param wItemProp the {@link IWItemProperty} used to apply the action when
	 * an entry of the menu is selected, must be not null
	 */
	@Override
	protected Menu createPopupMenu(Text insertField, IWItemProperty wItemProp) {
		final Menu popUpMenu = new Menu(insertField);
		insertField.setData(POPUP_KEY, popUpMenu);
		insertField.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				popUpMenu.dispose();
			}
		});
		// Add the new elements
		for (int i = 0; i < SPPixel.units.length; i++) {
			MeasureUnit key = SPPixel.units[i];
			MenuItem item = new MenuItem(popUpMenu, SWT.PUSH);
			item.setText(key.getUnitName());
			item.addSelectionListener(new MenuAction(key.getKeyName(), key.getUnitName(), insertField, wItemProp));
		}
		return popUpMenu;
	}

	/**
	 * Return the value in the text widget, it's returned as pixel
	 * 
	 * @param insertField the text widget, must be not null
	 * @return the value in the textfield as pixel
	 */
	protected String getPixels(Text insertField) {
		String text = insertField.getText().trim().toLowerCase();
		String key = getMeasureUnitFromText(text);
		MeasureUnit unit = SPPixel.unitsMap.get(Unit.getKeyFromAlias(key));
		if (unit != null) {
			String value = text.substring(0, text.indexOf(key));
			if (value.trim().isEmpty())
				return null;
			Unit uunit = new Unit(Double.parseDouble(value), unit.getKeyName());
			Double dValue = uunit.getValue(Unit.PX);
			return String.valueOf(dValue.longValue());
		}
		return null;
	}

	/**
	 * Set the value into the text widget, it's converted from pixel to the
	 * default measure unit
	 * 
	 * @param value the value to set, must be in pixel
	 * @param insertField the text widget, must be not null
	 * @param wItemProp the {@link IWItemProperty} used to read the current
	 * preferred measure unit from the widget
	 */
	protected void setPixels(String value, Text insertField, IWItemProperty wItemProp) {
		MeasureUnit defaultMeasure = getDefaultMeasure(insertField, wItemProp);
		Unit uunit = new Unit(Double.parseDouble(value), Unit.PX);
		Double dValue = uunit.getValue(defaultMeasure.getKeyName());

		insertField.setBackground(null);
		if (Unit.PX.equals(defaultMeasure.getKeyName())) {
			insertField.setText(String.valueOf(dValue.intValue()).concat(" ".concat(defaultMeasure.getUnitName()))); //$NON-NLS-1$
		} else {
			insertField.setText(String.valueOf(dValue).concat(" ".concat(defaultMeasure.getUnitName()))); //$NON-NLS-1$
		}
	}

	/**
	 * Return the default measure unit, that can be a local value if it's
	 * present or the global default value
	 * 
	 * @return
	 */
	protected MeasureUnit getDefaultMeasure(Text insertField, IWItemProperty wItemProp) {
		MeasureUnit mu = null;
		MeasureDefinition localValue = getMeasureUnit(wItemProp);
		if (localValue != null && SPPixel.unitsMap.containsKey(localValue.getKey())) {
			mu = SPPixel.unitsMap.get(localValue);
		} else
			mu = SPPixel.unitsMap.get(Unit.PX);
		if (mu == null)
			mu = SPPixel.units[0];
		return mu;
	}

	@Override
	public void handleEdit(Control txt, IWItemProperty wiProp) {
		if (wiProp == null)
			return;
		if (!wiProp.isExpressionMode() && txt instanceof Text) {
			Text insertField = (Text) txt;
			String text = insertField.getText().trim().toLowerCase();
			if (!text.isEmpty()) {
				String unitName = getMeasureUnitFromText(text);
				String value;
				MeasureUnit unit;
				if (unitName == null) {
					// A unit is not specified, so use the element or default
					// one
					unit = getDefaultMeasure(insertField, wiProp);
					value = text;
				} else {
					unit = SPPixel.unitsMap.get(Unit.getKeyFromAlias(unitName));
					value = text.substring(0, text.indexOf(unitName));
				}
				if (unit != null) {
					try {
						setMeasureUnit(unit.getKeyName(), unitName, wiProp);
						// Convert the value into pixel, internally JR work
						// always with pixels
						String convertedValue = unit.doConversionFromThis(SPPixel.unitsMap.get(Unit.PX), value);
						if (convertedValue != null) {
							long pixelCount = Double.valueOf(convertedValue).longValue();
							wiProp.setValue(String.valueOf(pixelCount), null);
						} else {
							wiProp.setValue(null, null);
						}
						setErrorStatus(null, insertField);
					} catch (NumberFormatException ex) {
						// The value can not be converted into a number
						setErrorStatus(Messages.common_this_is_not_an_integer_number, insertField);
					} catch (PixelConversionException ex) {
						// The value can be converted into a number but not into
						// an integer
						setErrorStatus(ex.getMessage(), insertField);
					}
				} else {
					// Measure unit not found
					setErrorStatus(Messages.SPPixel_errorMeasureUnit, insertField);
				}
			} else {
				wiProp.setValue(null, null);
				// remove the measure unit property
				removeMeasureUnit(wiProp);
			}
		} else
			super.handleEdit(txt, wiProp);
	}

	/**
	 * This property description provide a custom dialog to allow to use also
	 * the property to handle the measure unit
	 */
	@Override
	public ItemPropertyElementDialog getDialog(final WItemProperty wItemProp) {
		ItemPropertyElementDialog result = new ItemPropertyElementDialog(UIUtils.getShell(), this, wItemProp) {

			@Override
			public boolean close() {
				if (getReturnCode() == Dialog.OK) {
					// when the dialog is closed force to lose focus to trigger
					// the focus listener
					// otherwise the text is destroyed before the focus lost
					Control focusedControl = getShell().getDisplay().getFocusControl();
					if (focusedControl != null) {
						// disabling a control force the focus lost on SWT
						focusedControl.setEnabled(false);
					}
					// Write the current measure unit in the model
					String propertyName = wItemProp.getPropertyName();
					String measureUnit = customPropertiesMap.get(propertyName + CURRENT_MEASURE_KEY);
					if (measureUnit != null) {
						MeasureDefinition currentDef = decode(measureUnit);
						setMeasureUnit(currentDef.getKey(), currentDef.getName(), wItemProp);
					}
				}
				return super.close();
			}

			@Override
			protected Control createDialogArea(Composite parent) {
				// On create write the measure unit property in the additional
				// properties map
				MeasureDefinition currentMeasureDef = getMeasureUnit(wItemProp);
				String propertyName = wItemProp.getPropertyName();
				customPropertiesMap.put(propertyName + CURRENT_MEASURE_KEY, encode(currentMeasureDef));
				return super.createDialogArea(parent);
			}
		};
		result.setHelpAvailable(false);
		result.setForceExpressionMode(wItemProp.hasForcedExpression());
		return result;
	}

	@Override
	public ItemPropertyDescription<?> getInstance(WidgetsDescriptor cd, WidgetPropertyDescriptor cpd,
			JasperReportsConfiguration jConfig) {
		Long def = null;
		Long fallBack = null;

		Long min = createMin(cpd);
		Long max = createMax(cpd);

		// Setup the default value
		if (cpd.getDefaultValue() != null && !cpd.getDefaultValue().isEmpty()) {
			def = new Long(cpd.getDefaultValue());
		}

		// Setup the fallback value
		if (cpd.getFallbackValue() != null && !cpd.getFallbackValue().isEmpty()) {
			fallBack = new Long(cpd.getFallbackValue());
		}
		MeasureUnitPropertyDescription intDesc = new MeasureUnitPropertyDescription(cpd.getName(),
				cd.getLocalizedString(cpd.getLabel()), cd.getLocalizedString(cpd.getDescription()), cpd.isMandatory(),
				def, min, max);
		intDesc.setReadOnly(cpd.isReadOnly());
		intDesc.setFallbackValue(fallBack);
		intDesc.setjConfig(jConfig);
		return intDesc;
	}

	@Override
	public ItemPropertyDescription<Long> clone() {
		MeasureUnitPropertyDescription result = new MeasureUnitPropertyDescription();
		result.defaultValue = defaultValue;
		result.description = description;
		result.jConfig = jConfig;
		result.label = label;
		result.mandatory = mandatory;
		result.name = name;
		result.readOnly = readOnly;
		result.min = min;
		result.max = max;
		result.fallbackValue = fallbackValue;
		return result;
	}

	// ADDITIONAL CLASSES

	/**
	 * Listener that handle the double click on the Text, made the contextual
	 * menu appears
	 * 
	 * @author Orlandin Marco
	 * 
	 */
	private class MouseClickListener extends MouseAdapter {

		/**
		 * The text widget
		 */
		private Text insertField;

		/**
		 * The {@link IWItemProperty} used to execute the change of value
		 */
		private IWItemProperty wItemProp;

		public MouseClickListener(Text insertField, IWItemProperty wItemProp) {
			this.insertField = insertField;
			this.wItemProp = wItemProp;
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			String measureUnitAlias = insertField.getSelectionText().trim().toLowerCase();
			String measureUnitName = Unit.getKeyFromAlias(measureUnitAlias);
			if (measureUnitName != null) {
				openPopupMenu(insertField, wItemProp);
			}
		}

	}

	/**
	 * Action to execute when a new measure unit is selected from a popup combo
	 * 
	 * @author Orlandin Marco
	 * 
	 */
	private class MenuAction extends SelectionAdapter {

		/**
		 * Key of the unit represented by this listener
		 */
		private String measureKey;

		/**
		 * Name of the unit represented by this listener
		 */
		private String measureName;

		/**
		 * The text widget
		 */
		private Text insertField;

		/**
		 * The {@link IWItemProperty} used to execute the change of value
		 */
		private IWItemProperty wItemProperty;

		public MenuAction(String measureKey, String measureName, Text insertField, IWItemProperty wItemProperty) {
			this.measureKey = measureKey;
			this.measureName = measureName;
			this.insertField = insertField;
			this.wItemProperty = wItemProperty;
		}

		/**
		 * When a new measure unit is selected a new local is set and the
		 * conversion is done
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			String pixelValue = getPixels(insertField);
			setMeasureUnit(measureKey, measureName, wItemProperty);
			setPixels(pixelValue, insertField, wItemProperty);
		}

	}
}
