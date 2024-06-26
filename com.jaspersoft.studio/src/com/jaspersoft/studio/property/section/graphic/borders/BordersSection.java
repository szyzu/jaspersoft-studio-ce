/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.section.graphic.borders;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.java2d.J2DLightweightSystem;
import com.jaspersoft.studio.help.HelpSystem;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.MGraphicElementLineBox;
import com.jaspersoft.studio.model.MLineBox;
import com.jaspersoft.studio.model.MLinePen;
import com.jaspersoft.studio.properties.internal.IHighlightPropertyWidget;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.graphic.ASHighlightControl;
import com.jaspersoft.studio.property.section.graphic.borders.LineBoxDrawer.Location;
import com.jaspersoft.studio.property.section.widgets.BackgroundHighlight;
import com.jaspersoft.studio.property.section.widgets.BorderHightLight;
import com.jaspersoft.studio.property.section.widgets.SPLineStyleEnum;
import com.jaspersoft.studio.swt.widgets.ColorStyledText;
import com.jaspersoft.studio.swt.widgets.NullableSpinner;
import com.jaspersoft.studio.utils.AlfaRGB;
import com.jaspersoft.studio.utils.Colors;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.base.JRBasePen;
import net.sf.jasperreports.engine.base.JRBoxPen;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.util.StyleResolver;

/**
 * The location section on the location tab.
 * 
 * @author Chicu Veaceslav & Orlandin Marco
 */
public class BordersSection extends AbstractSection {

	/**
	 * Toolbutton to set all the borders
	 */
	private ToolItem allBorder;

	/**
	 * Toolbutton to remove all the borders
	 */
	private ToolItem noneBorder;

	/**
	 * Toolbutton to set the up and bottom borders
	 */
	private ToolItem upDownBorder;

	/**
	 * Toolbutton to set the left and right borders
	 */
	private ToolItem leftRightBorder;

	/**
	 * Drawer for the borders linebox
	 */
	private LineBoxDrawer bd;

	/**
	 * The filed used to set\show the lineWidth
	 */
	private NullableSpinner lineWidth;
	
	private CLabel lineWidthLabel;

	/**
	 * The Combo popup used to set\show the linestyle
	 */
	private SPLineStyleEnum lineStyle;
	
	private CLabel lineStyleLabel;

	/**
	 * Checkbox to know if all the padding are at the same value
	 */
	private Button checkBoxPadding;

	/**
	 * Spinner for the left padding or for the general padding when the checkbox is selected
	 */
	private NullableSpinner paddingLeft;
	
	private CLabel leftPaddingLabel;

	/**
	 * Spinner for the right padding
	 */
	private NullableSpinner paddingRight;
	
	private CLabel rightPaddingLabel;

	/**
	 * Spinner for the bottom padding
	 */
	private NullableSpinner paddingBottom;
	
	private CLabel bottomPaddingLabel;

	/**
	 * Spinner for the top padding
	 */
	private NullableSpinner paddingTop;
	
	private CLabel topPaddingLabel;

	/**
	 * Border figure rectangle
	 */
	private RectangleFigure borderPreview;

	/**
	 * Border figure canvas
	 */
	private Canvas square;

	/**
	 * Group section where the controls regarding the border are placed
	 */
	private Group rightPanel;

	/**
	 * The widget used to set\show the line color
	 */
	private ColorStyledText lineColor;
	
	private CLabel lineColorLabel;

	private static final String PREFIX = "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1";

	private static final String PADDING_GENERAL = PREFIX.concat("#box_padding");

	private static final String PADDING_TOP = PREFIX.concat("#box_topPadding");

	private static final String PADDING_BOTTOM = PREFIX.concat("#box_bottomPadding");

	private static final String PADDING_LEFT = PREFIX.concat("#box_leftPadding");

	private static final String PADDING_RIGHT = PREFIX.concat("#box_rightPadding");

	private static final String LINE_COLOR = PREFIX.concat("#pen_lineColor");

	private static final String LINE_WIDTH = PREFIX.concat("#pen_lineWidth");

	private static final String LINE_STYLE = PREFIX.concat("#pen_lineStyle");

	private static final String BOX = PREFIX.concat("#box");

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite mainLayout = new Composite(parent, SWT.NONE);
		mainLayout.setLayout(new GridLayout(1, true));
		mainLayout.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		createPaddingPanel(mainLayout);

		rightPanel = new Group(mainLayout, SWT.NONE);
		rightPanel.setText(Messages.common_borders);
		rightPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		rightPanel.setLayout(new GridLayout(2, false));

		createBorderPreview(rightPanel);

		createStyle(rightPanel, JRBaseLineBox.PROPERTY_PADDING);

		Composite toolBarLayout = new Composite(rightPanel, SWT.NONE);
		toolBarLayout.setLayout(new GridLayout(1, false));
		GridData toolBardGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		toolBardGridData.horizontalSpan = 2;
		toolBardGridData.widthHint = 200;
		toolBardGridData.horizontalIndent = 5;
		toolBarLayout.setLayoutData(toolBardGridData);
		Label textLabel = new Label(toolBarLayout, SWT.NONE);
		textLabel.setText(Messages.BordersSection_Default_Label);
		ToolBar toolBar = new ToolBar(toolBarLayout, SWT.FLAT | SWT.WRAP);
		createButtons(toolBar);

		allBorder.setSelection(false);
		noneBorder.setSelection(false);
		leftRightBorder.setSelection(false);
		upDownBorder.setSelection(false);
	}

	/**
	 * Create the canvas box where the borders will be represented and could be edited
	 * 
	 * @param composite
	 *          the composite where the canvas will be placed
	 */
	private void createBorderPreview(Composite composite) {
		square = new Canvas(composite, SWT.BORDER | SWT.NO_REDRAW_RESIZE);
		square.setBackground(ColorConstants.white);
		HelpSystem.setHelp(square, BOX);

		GridData gd = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		gd.widthHint = 120;
		gd.heightHint = 120;
		square.setLayoutData(gd);

		LightweightSystem lws = new J2DLightweightSystem();
		lws.setControl(square);
		bd = new LineBoxDrawer(jasperReportsContext, square);
		bd.addBorderSelectionListener(new BorderSelectionListener() {
			
			@Override
			public void borderSelected(BorderSelectionEvent event) {;
				/*Float beforeSelectionWidth = getLineWidth(event.getClickedBorder());
				if (beforeSelectionWidth == null || beforeSelectionWidth.equals(0f)){
					changeProperty(JRBasePen.PROPERTY_LINE_WIDTH, 1f);	
				}*/
				updateRightPanel();
			}
		});
		borderPreview = new LineBoxRectangle(bd, this);
		square.setToolTipText(Messages.BordersSection_preview_ToolTip);
		lws.setContents(borderPreview);
	}

	/**
	 * Enable or disable the spinner for the directional padding when the checkbox to set the same value in every spinner
	 * is selected
	 */
	private void checkBoxValueChange() {
		if (checkBoxPadding.getSelection()) {
			paddingRight.setEnabled(false);
			rightPaddingLabel.setEnabled(false);
			paddingTop.setEnabled(false);
			topPaddingLabel.setEnabled(false);
			paddingBottom.setEnabled(false);
			bottomPaddingLabel.setEnabled(false);
			paddingRight.setDefaultValue(null);
			paddingTop.setDefaultValue(null);
			paddingBottom.setDefaultValue(null);
			paddingRight.setValue(null);
			paddingTop.setValue(null);
			paddingBottom.setValue(null);
		} else {
			paddingRight.setDefaultValue(0);
			paddingTop.setDefaultValue(0);
			paddingBottom.setDefaultValue(0);
			paddingRight.setEnabled(true);
			rightPaddingLabel.setEnabled(true);
			paddingTop.setEnabled(true);
			topPaddingLabel.setEnabled(true);
			paddingBottom.setEnabled(true);
			bottomPaddingLabel.setEnabled(true);
		}
	}

	/**
	 * When the checkbox is selected or deselected this method perform immediately an action to set the general padding to
	 * a value or null respectively
	 */
	private void uniformAfterCheck() {
		JSSCompoundCommand cc = new JSSCompoundCommand("Change padding", null); //$NON-NLS-1$
		if (checkBoxPadding.getSelection()) {
			// I've selected to use the same value in all the padding area
			// so i generate immediately the command
			for (APropertyNode m : getElements()) {
				cc.setReferenceNodeIfNull(m);
				Command c = null;
				MLineBox lb = (MLineBox) m.getPropertyValue(MGraphicElementLineBox.LINE_BOX);
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_PADDING, new Integer(paddingLeft.getValueAsInteger()), lb);
				if (c != null) cc.add(c);
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_BOTTOM_PADDING, null, lb);
				if (c != null) cc.add(c);
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_TOP_PADDING, null, lb);
				if (c != null) cc.add(c);
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_LEFT_PADDING, null, lb);
				if (c != null) cc.add(c);
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_RIGHT_PADDING, null, lb);
				if (c != null) cc.add(c);
			}
		} else {
			// The box was deselected so i need to set immediately the padding for all to null
			for (APropertyNode m : getElements()) {
				cc.setReferenceNodeIfNull(m);
				Command c = null;
				MLineBox lb = (MLineBox) m.getPropertyValue(MGraphicElementLineBox.LINE_BOX);
				Integer oldValue = getPaddingValue(lb.getPropertyValue(JRBaseLineBox.PROPERTY_PADDING));
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_PADDING, null, lb);
				if (c != null) cc.add(c);
				
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_BOTTOM_PADDING, oldValue, lb);
				if (c != null) cc.add(c);
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_TOP_PADDING, oldValue, lb);
				if (c != null) cc.add(c);
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_RIGHT_PADDING, oldValue, lb);
				if (c != null) cc.add(c);
				c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_LEFT_PADDING, oldValue, lb);
				if (c != null) cc.add(c);
			}
		}
		CommandStack cs = getEditDomain().getCommandStack();
		cs.execute(cc);
		refresh();
		paddingLeft.forceFocus();
	}

	/**
	 * Create the padding group and the control in it
	 * 
	 * @param parent
	 *          the composite where the group will be placed
	 */
	private void createPaddingPanel(Composite parent) {
		Group composite = new Group(parent, SWT.NONE);
		composite.setText(Messages.BordersSection_Padding_Box_Title);

		GridLayout layout = new GridLayout(4, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridData gd = new GridData();
		gd.horizontalSpan = 4;

		checkBoxPadding = getWidgetFactory().createButton(composite, Messages.BordersSection_Same_Padding_Value_Check,
				SWT.CHECK);
		checkBoxPadding.setLayoutData(gd);
		HelpSystem.setHelp(checkBoxPadding, PADDING_GENERAL);
		checkBoxPadding.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkBoxValueChange();
				uniformAfterCheck();
			}
		});

		leftPaddingLabel = getWidgetFactory().createCLabel(composite, Messages.BordersSection_Left_Label, SWT.RIGHT);

		paddingLeft = new NullableSpinner(composite, SWT.BORDER);
		paddingLeft.setDefaultValue(0);
		paddingLeft.setValues(0, 0, 999);
		paddingLeft.setToolTipText(Messages.BordersSection_padding_tool_tip);
		HelpSystem.setHelp(paddingLeft, PADDING_LEFT);
		paddingLeft.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changePropertyPadding(JRBaseLineBox.PROPERTY_LEFT_PADDING, paddingLeft.getValueAsInteger());
				setControlTooltipInherithed(paddingLeft.getValueAsInteger() == null, paddingLeft, leftPaddingLabel, Messages.BordersSection_padding_tool_tip);
			}
		});
		createPaddingContextualMenu(paddingLeft, JRBaseLineBox.PROPERTY_LEFT_PADDING);

		rightPaddingLabel = getWidgetFactory().createCLabel(composite, Messages.common_right, SWT.RIGHT);

		paddingRight = new NullableSpinner(composite, SWT.BORDER);
		paddingRight.setDefaultValue(0);
		paddingRight.setValues(0, 0, 999);
		paddingRight.setToolTipText(Messages.BordersSection_padding_tool_tip);
		HelpSystem.setHelp(paddingRight, PADDING_RIGHT);
		paddingRight.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changePropertyPadding(JRBaseLineBox.PROPERTY_RIGHT_PADDING, paddingRight.getValueAsInteger());
				setControlTooltipInherithed(paddingRight.getValueAsInteger() == null, paddingRight, rightPaddingLabel, Messages.BordersSection_padding_tool_tip);
			}
		});
		createPaddingContextualMenu(paddingRight, JRBaseLineBox.PROPERTY_RIGHT_PADDING);

		topPaddingLabel = getWidgetFactory().createCLabel(composite, Messages.BordersSection_Top_Label, SWT.RIGHT);

		paddingTop = new NullableSpinner(composite, SWT.BORDER);
		paddingTop.setDefaultValue(0);
		paddingTop.setValues(0, 0, 999);
		paddingTop.setToolTipText(Messages.BordersSection_padding_tool_tip);
		HelpSystem.setHelp(paddingTop, PADDING_TOP);
		paddingTop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changePropertyPadding(JRBaseLineBox.PROPERTY_TOP_PADDING, paddingTop.getValueAsInteger());
				setControlTooltipInherithed(paddingTop.getValueAsInteger() == null, paddingTop, topPaddingLabel, Messages.BordersSection_padding_tool_tip);
			}
		});
		createPaddingContextualMenu(paddingTop, JRBaseLineBox.PROPERTY_TOP_PADDING);

		bottomPaddingLabel = getWidgetFactory().createCLabel(composite, Messages.common_bottom, SWT.RIGHT);

		paddingBottom = new NullableSpinner(composite, SWT.BORDER);
		paddingBottom.setDefaultValue(0);
		paddingBottom.setValues(0, 0, 999);
		paddingBottom.setToolTipText(Messages.BordersSection_padding_tool_tip);
		HelpSystem.setHelp(paddingBottom, PADDING_BOTTOM);
		paddingBottom.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changePropertyPadding(JRBaseLineBox.PROPERTY_BOTTOM_PADDING, paddingBottom.getValueAsInteger());
				setControlTooltipInherithed(paddingBottom.getValueAsInteger() == null, paddingBottom, bottomPaddingLabel, Messages.BordersSection_padding_tool_tip);
			}
		});
		createPaddingContextualMenu(paddingBottom, JRBaseLineBox.PROPERTY_BOTTOM_PADDING);
	}

	private Control createStyle(Composite parent, final String property) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		lineColorLabel = getWidgetFactory().createCLabel(composite, Messages.common_pen_color + ":", SWT.RIGHT); //$NON-NLS-1$
		lineColor = new ColorStyledText(composite);
		lineColor.setColor(AlfaRGB.getFullyOpaque(new RGB(0, 0, 0)));
		lineColor.setHelp(LINE_COLOR);
		lineColor.addListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				changeProperty(JRBasePen.PROPERTY_LINE_COLOR, lineColor.getColor());
				refresh();
			}
		});
		createLineContextualMenu(lineColor, JRBasePen.PROPERTY_LINE_COLOR);

		lineStyleLabel = getWidgetFactory().createCLabel(composite, Messages.common_pen_style + ":"); //$NON-NLS-1$

		createLineStyle(property, composite);

		lineWidthLabel = getWidgetFactory().createCLabel(composite, Messages.common_pen_width + ":", SWT.RIGHT); //$NON-NLS-1$

		lineWidth = new NullableSpinner(composite, SWT.BORDER, 2, 6);
		lineWidth.setDefaultValue(0f);
		lineWidth.setIncrement(0.25f);
		GridData lineWidthData = new GridData();
		lineWidthData.widthHint = 50 ;
		lineWidth.setLayoutData(lineWidthData);
		lineWidth.setValues(null, 0, 999);
		lineWidth.setToolTipText(Messages.BordersSection_width_tool_tip);
		HelpSystem.setHelp(lineWidth, LINE_WIDTH);
		lineWidth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changeProperty(JRBasePen.PROPERTY_LINE_WIDTH, lineWidth.getValueAsFloat());
				boolean isInheirted = lineWidth.getValueAsFloat() == null;
				setControlTooltipInherithed(isInheirted, lineWidth, lineWidthLabel, Messages.BordersSection_width_tool_tip);
			}
		});
		createLineContextualMenu(lineWidth, JRBasePen.PROPERTY_LINE_WIDTH);

		return composite;
	}

	/**
	 * Create the line style combo popup
	 * 
	 * @param prop
	 *          properties associated to the combo popup
	 * @param composite
	 *          parent where the combo will be placed
	 */
	private void createLineStyle(final String prop, final Composite composite) {
		lineStyle = new SPLineStyleEnum(composite, this, prop) {
			@Override
			public void propertyChange(AbstractSection section, String property, Object value) {
				((BordersSection) section).changeProperty(property, value);
				setInhterited(false);
				Float beforeSelectionWidth = lineWidth.getValueAsFloat();
				if (beforeSelectionWidth == null || beforeSelectionWidth.equals(0f)){
					changeProperty(JRBasePen.PROPERTY_LINE_WIDTH, 1f);	
					refresh();
				}
			}
		};
		lineStyle.setData(1);
		lineStyle.setHelp(LINE_STYLE);
		//createLineContextualMenu(lineStyle.getControl(), JRBasePen.PROPERTY_LINE_COLOR);
	}

	/**
	 * Print the toolbar button and add the listener to them
	 * 
	 * @param toolBar
	 */
	private void createButtons(ToolBar toolBar) {

		noneBorder = new ToolItem(toolBar, SWT.PUSH);
		noneBorder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AlfaRGB beforeSelectionColor = lineColor.getColor();// lastColorSelected;
				Float selection = lineWidth.getValueAsFloat();
				Object beforeSelectionStyle = lineStyle.getSelectedValue();
				bd.setBorderSelected(Location.LEFT);
				bd.setBorderSelected(Location.RIGHT);
				bd.setBorderSelected(Location.TOP);
				bd.setBorderSelected(Location.BOTTOM);
				AlfaRGB color = AlfaRGB.getFullyOpaque(new RGB(0, 0, 0));
				Float newValue = new Float(0);
				changeProperty(JRBasePen.PROPERTY_LINE_STYLE, 1);
				changeProperty(JRBasePen.PROPERTY_LINE_COLOR, color);
				changeProperty(JRBasePen.PROPERTY_LINE_WIDTH, newValue);
				bd.unselectAll();
				// The selection action change the displayed values, so i need to restore them after the unselect
				lineStyle.setData((LineStyleEnum) beforeSelectionStyle);
				lineWidth.setValue(selection);
				lineColor.setColor(beforeSelectionColor);
			}
		});
		noneBorder.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/border.png")); //$NON-NLS-1$
		noneBorder.setToolTipText(Messages.BordersSection_No_Borders);

		allBorder = new ToolItem(toolBar, SWT.PUSH);
		allBorder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Float beforeSelectionWidth = lineWidth.getValueAsFloat();
				if (beforeSelectionWidth == null || beforeSelectionWidth.equals(0f)){
					beforeSelectionWidth = 1f;
				}
				bd.setBorderSelected(Location.LEFT);
				bd.setBorderSelected(Location.RIGHT);
				bd.setBorderSelected(Location.TOP);
				bd.setBorderSelected(Location.BOTTOM);
				AlfaRGB color = lineColor.getColor();
				Object style = lineStyle.getSelectedValue();
				changeProperty(JRBasePen.PROPERTY_LINE_STYLE, style);
				changeProperty(JRBasePen.PROPERTY_LINE_COLOR, color);
				changeProperty(JRBasePen.PROPERTY_LINE_WIDTH, beforeSelectionWidth);
				lineWidth.setValue(beforeSelectionWidth);
				bd.unselectAll();
			}
		});
		allBorder.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/border-outside.png")); //$NON-NLS-1$
		allBorder.setToolTipText(Messages.BordersSection_all_borders_tool_tip);

		leftRightBorder = new ToolItem(toolBar, SWT.PUSH);
		leftRightBorder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AlfaRGB beforeSelectionColor = lineColor.getColor();
				Float beforeSelectionWidth = lineWidth.getValueAsFloat();
				Object beforeSelectionStyle = lineStyle.getSelectedValue();
				if (beforeSelectionWidth == null || beforeSelectionWidth.equals(0f)){
					beforeSelectionWidth = 1f;
				}
				bd.setBorderSelected(Location.LEFT, false);
				bd.setBorderSelected(Location.RIGHT, false);
				bd.setBorderSelected(Location.TOP);
				bd.setBorderSelected(Location.BOTTOM);
				Float newValue = new Float(0);
				changeProperty(JRBasePen.PROPERTY_LINE_STYLE, 1);
				changeProperty(JRBasePen.PROPERTY_LINE_COLOR, new AlfaRGB(new RGB(0, 0, 0), 255));
				changeProperty(JRBasePen.PROPERTY_LINE_WIDTH, newValue);
				bd.setBorderSelected(Location.LEFT);
				bd.setBorderSelected(Location.RIGHT);
				bd.setBorderSelected(Location.TOP, false);
				bd.setBorderSelected(Location.BOTTOM, false);
				changeProperty(JRBasePen.PROPERTY_LINE_STYLE, beforeSelectionStyle);
				changeProperty(JRBasePen.PROPERTY_LINE_COLOR, beforeSelectionColor);
				changeProperty(JRBasePen.PROPERTY_LINE_WIDTH, beforeSelectionWidth);
				bd.unselectAll();
				lineColor.setColor(beforeSelectionColor);
				lineWidth.setValue(beforeSelectionWidth);
				lineStyle.setData((LineStyleEnum) beforeSelectionStyle);
			}
		});
		leftRightBorder.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/border-right-left.png")); //$NON-NLS-1$
		leftRightBorder.setToolTipText(Messages.BordersSection_Left_Right_Borders);

		upDownBorder = new ToolItem(toolBar, SWT.PUSH);
		upDownBorder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AlfaRGB beforeSelectionColor = lineColor.getColor();
				Object beforeSelectionStyle = lineStyle.getSelectedValue();
				Float beforeSelectionWidth = lineWidth.getValueAsFloat();
				if (beforeSelectionWidth == null || beforeSelectionWidth.equals(0f)){
					beforeSelectionWidth = 1f;
				}
				bd.setBorderSelected(Location.LEFT);
				bd.setBorderSelected(Location.RIGHT);
				bd.setBorderSelected(Location.TOP, false);
				bd.setBorderSelected(Location.BOTTOM, false);
				Float newValue = new Float(0);
				changeProperty(JRBasePen.PROPERTY_LINE_STYLE, 1);
				changeProperty(JRBasePen.PROPERTY_LINE_COLOR, new AlfaRGB(new RGB(0, 0, 0), 255));
				changeProperty(JRBasePen.PROPERTY_LINE_WIDTH, newValue);
				bd.setBorderSelected(Location.LEFT, false);
				bd.setBorderSelected(Location.RIGHT, false);
				bd.setBorderSelected(Location.TOP);
				bd.setBorderSelected(Location.BOTTOM);
				
				changeProperty(JRBasePen.PROPERTY_LINE_STYLE, beforeSelectionStyle);
				changeProperty(JRBasePen.PROPERTY_LINE_COLOR, beforeSelectionColor);
				changeProperty(JRBasePen.PROPERTY_LINE_WIDTH, beforeSelectionWidth);
				bd.unselectAll();
				lineColor.setColor(beforeSelectionColor);
				lineWidth.setValue(beforeSelectionWidth);
				lineStyle.setData((LineStyleEnum) beforeSelectionStyle);
			}
		});
		upDownBorder.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/resources/border-top-bottom.png")); //$NON-NLS-1$
		upDownBorder.setToolTipText(Messages.BordersSection_Top_Bottom_Borders);
	}

	/**
	 * Change the padding property of a linebox
	 * 
	 * @param property
	 *          the property name
	 * @param newValue
	 *          the new value
	 */
	public void changePropertyPadding(String property, Object newValue) {
		if (!isRefreshing()) {
			JSSCompoundCommand cc = new JSSCompoundCommand("Change padding", null); //$NON-NLS-1$
			for (APropertyNode m : getElements()) {
				cc.setReferenceNodeIfNull(m);
				Command c = null;
				MLineBox lb = (MLineBox) m.getPropertyValue(MGraphicElementLineBox.LINE_BOX);
				// If the checkbox is set i need to set the general padding, otherwise it must be null
				if (checkBoxPadding.getSelection()) {
					c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_PADDING, newValue, lb);
				} else {
					c = getChangePropertyCommand(JRBaseLineBox.PROPERTY_PADDING, null, lb);
					if (c != null)
						cc.add(c);
					c = getChangePropertyCommand(property, newValue, lb);
				}
				if (c != null)
					cc.add(c);
			}
			CommandStack cs = getEditDomain().getCommandStack();
			cs.execute(cc);
		}
	}

	/**
	 * Change the property of a linepen
	 * 
	 * @param property
	 *          the property name
	 * @param newValue
	 *          the new value
	 */
	public void changeProperty(String property, Object newValue) {
		if (!isRefreshing()) {
			JSSCompoundCommand cc = new JSSCompoundCommand("Change border", null); //$NON-NLS-1$
			for (APropertyNode m : getElements()) {
				cc.setReferenceNodeIfNull(m);
				Command c = null;
				MLineBox lb = (MLineBox) m.getPropertyValue(MGraphicElementLineBox.LINE_BOX);
				// it's a change of a border attribute
				boolean areAllUnselected = bd.areAllUnslected();
				if (areAllUnselected){
					MLinePen lp = (MLinePen) lb.getPropertyValue(MLineBox.LINE_PEN);
					//Since the global LINE_PEN is overridden by the single segment, first I need to
					//reset them, NOTE: this behavior was disabled to make the widget compliant with JR
					//if nothing is select only the general value is set
					//getLinePenClearCommand(cc, MLineBox.LINE_PEN_BOTTOM, lb);
					//getLinePenClearCommand(cc, MLineBox.LINE_PEN_TOP, lb);
					//getLinePenClearCommand(cc, MLineBox.LINE_PEN_LEFT, lb);
					//getLinePenClearCommand(cc, MLineBox.LINE_PEN_RIGHT, lb);
					c = getChangePropertyCommand(property, newValue, lp);
					if (c != null)
						cc.add(c);
				} else {
					if (bd.isBottomSelected()) {
						MLinePen lp = (MLinePen) lb.getPropertyValue(MLineBox.LINE_PEN_BOTTOM);
						c = getChangePropertyCommand(property, newValue, lp);
						if (c != null)
							cc.add(c);
					}
					if (bd.isTopSelected()) {
						MLinePen lp = (MLinePen) lb.getPropertyValue(MLineBox.LINE_PEN_TOP);
						c = getChangePropertyCommand(property, newValue, lp);
						if (c != null)
							cc.add(c);
					} 
					if (bd.isLeftSelected()) {
						MLinePen lp = (MLinePen) lb.getPropertyValue(MLineBox.LINE_PEN_LEFT);
						c = getChangePropertyCommand(property, newValue, lp);
						if (c != null)
							cc.add(c);
					} 
					if (bd.isRightSelected()) {
						MLinePen lp = (MLinePen) lb.getPropertyValue(MLineBox.LINE_PEN_RIGHT);
						c = getChangePropertyCommand(property, newValue, lp);
						if (c != null)
							cc.add(c);
					}
				}
			}
			CommandStack cs = getEditDomain().getCommandStack();
			cs.execute(cc);
			bd.refresh();
		}
	}
	
	/**
	 * Generate the commands to clear the properties of color, width and style of 
	 * a specific linepen
	 *  
	 * @param cc the {@link CompoundCommand} where the generated command are added
	 * @param linePenProperty property ID of the linepen to reset
	 * @param lb linebox where the linepen is placed
	 */
	protected void getLinePenClearCommand(CompoundCommand cc, String linePenProperty, MLineBox lb){
		MLinePen lp = (MLinePen) lb.getPropertyValue(linePenProperty);
		Command c = getChangePropertyCommand(JRBasePen.PROPERTY_LINE_WIDTH, null, lp);
		if (c != null)
			cc.add(c);
		c = getChangePropertyCommand(JRBasePen.PROPERTY_LINE_STYLE, null, lp);
		if (c != null)
			cc.add(c);
		c = getChangePropertyCommand(JRBasePen.PROPERTY_LINE_COLOR, null, lp);
		if (c != null)
			cc.add(c);
	}

	/**
	 * Convert a border location to the corresponding line pen location
	 * 
	 * @param loc
	 *          location of a border
	 * @return a line pen location
	 */
	private String locationToLine(Location loc) {
		if (loc == Location.TOP)
			return MLineBox.LINE_PEN_TOP;
		else if (loc == Location.BOTTOM)
			return MLineBox.LINE_PEN_BOTTOM;
		else if (loc == Location.RIGHT)
			return MLineBox.LINE_PEN_RIGHT;
		else
			return MLineBox.LINE_PEN_LEFT;
	}

	/**
	 * Update the value on the right panel with the selected line data
	 */
	private void updateRightPanel() {
		APropertyNode m = getElement();
		if (m != null) {
			MLineBox lb = (MLineBox) m.getPropertyActualValue(MGraphicElementLineBox.LINE_BOX);
			if (bd.getLastSelected() != null && bd.getLastSelected().isSelected()) {
				refreshLinePen(lb, locationToLine(bd.getLastSelected().getLocation()));
			} else if (bd.isTopSelected()) {
				refreshLinePen(lb, MLineBox.LINE_PEN_TOP);
			} else if (bd.isBottomSelected()) {
				refreshLinePen(lb, MLineBox.LINE_PEN_BOTTOM);
			} else if (bd.isLeftSelected()) {
				refreshLinePen(lb, MLineBox.LINE_PEN_LEFT);
			} else if (bd.isRightSelected()) {
				refreshLinePen(lb, MLineBox.LINE_PEN_RIGHT);
			} else {
				// No border is selected, use the main linepen
				refreshLinePen(lb, MLineBox.LINE_PEN);
			}
		}
	}

	/**
	 * Used when no border is selected to fined an intersection of the value of the borders
	 * 
	 * @param lb
	 *          the linebox
	 * @return a pen with the intersection values
	 */
	private JRBoxPen getIntesectionValues(MLineBox lb) {
		if (lb != null && lb.getValue() != null) {
			JRLineBox jrLineBox = (JRLineBox) lb.getValue();
			List<JRBoxPen> edgePen = new ArrayList<JRBoxPen>();
			edgePen.add(jrLineBox.getLeftPen());
			edgePen.add(jrLineBox.getRightPen());
			edgePen.add(jrLineBox.getTopPen());
			edgePen.add(jrLineBox.getBottomPen());
			Color intersectionLineColor = null;
			LineStyleEnum intersectionLineStyle = null;
			Float intersectionLineWidth = null;
			JRBoxPen resultPen = null;
			for (JRBoxPen pen : edgePen) {
				if (pen != null) {
					resultPen = pen;
					if (intersectionLineColor != null && !intersectionLineColor.equals(pen.getLineColor())) {
						resultPen = null;
						break;
					}
					if (intersectionLineStyle != null && !intersectionLineStyle.equals(pen.getLineStyleValue())) {
						resultPen = null;
						break;
					}
					if (intersectionLineWidth != null && !intersectionLineWidth.equals(pen.getLineWidth())) {
						resultPen = null;
						break;
					}
					intersectionLineColor = pen.getLineColor();
					intersectionLineStyle = pen.getLineStyleValue();
					intersectionLineWidth = pen.getLineWidth();
				}
			}
			if (resultPen == null && jrLineBox.getPen() != null)
				return jrLineBox.getPen();
			return resultPen;
		}
		return null;
	}

	/**
	 * Refresh the right panel with the values of the selected border. If more than one border is selected will be shown
	 * the values of one of them, choose as follow: -The last border selected -If the last border selected was deselected
	 * the first from the other SELECTED borders will be choose. The border examination order is TOP,BOTTOM,LEFT,RIGHT -If
	 * no border is selected will default values will be used
	 */
	public void refresh() {
		setRefreshing(true);
		APropertyNode m = getElement();
		if (m != null) {
			MLineBox lb = (MLineBox) m.getPropertyActualValue(MGraphicElementLineBox.LINE_BOX);
			bd.unselectAll();
			updateRightPanel();
			if (!m.isEditable()){ 
				enableControls(false);
			} else {
				enableControls(true);
				refreshPadding(lb);
			}
		}
		if (square != null)
			square.redraw();
		setRefreshing(false);
	}

	private void enableControls(boolean enable) {
		lineColor.setEnabled(enable);
		lineStyle.getControl().setEnabled(enable);
		lineWidth.setEnabled(enable);
		paddingRight.setEnabled(enable);
		paddingLeft.setEnabled(enable);
		paddingTop.setEnabled(enable);
		paddingBottom.setEnabled(enable);
		checkBoxPadding.setEnabled(enable);
		allBorder.setEnabled(enable);
		noneBorder.setEnabled(enable);
		leftRightBorder.setEnabled(enable);
		upDownBorder.setEnabled(enable);
	}

	/**
	 * Return the padding value
	 * 
	 * @param padding
	 *          and object representing the padding value, could be null
	 * @return and integer version of the padding value if it isn't null, otherwise false
	 */
	private Integer getPaddingValue(Object padding) {
		return (padding != null ? (Integer) padding : 0);
	}
	
	/**
	 * Create a contextual menu for a padding control. This contextual menu
	 *  will contain the action to set the value to null.
	 * 
	 * @param control the padding control to which the menu will be assigned
	 * @param propertyID the ID of the property that the selection on the menu will change
	 */
	protected void createPaddingContextualMenu(final Control control, final String propertyID){
		if (control != null && !control.isDisposed() && control.getMenu() == null){
			Menu controlMenu = new Menu(control);
			MenuItem nullItem = new MenuItem(controlMenu, SWT.NONE);
			nullItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					changePropertyPadding(propertyID, null);
					refresh();
					control.setFocus();
				}
			});
	    nullItem.setText(Messages.ASPropertyWidget_1);
			control.setMenu(controlMenu);
		}
	}

	/**
	 * Create a contextual menu for a border control. This contextual menu
	 *  will contain the action to set the value to null.
	 * 
	 * @param control the border control to which the menu will be assigned
	 * @param propertyID the ID of the property that the selection on the menu will change
	 */
	protected void createLineContextualMenu(final Control control, final String propertyID){
		if (control != null && !control.isDisposed() && control.getMenu() == null){
			Menu controlMenu = new Menu(control);
			MenuItem nullItem = new MenuItem(controlMenu, SWT.NONE);
			nullItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					changeProperty(propertyID, null);
					refresh();
					control.setFocus();
				}
			});
	    nullItem.setText(Messages.ASPropertyWidget_1);
			control.setMenu(controlMenu);
		}
	}
	
	/**
	 * Refresh the padding information, actually it's not used
	 */
	public void refreshPadding(MLineBox lb) {
		if (lb != null) {
			paddingBottom.setValue(null);
			paddingLeft.setValue(null);
			paddingRight.setValue(null);
			paddingTop.setValue(null);
			Object propertyPadding = lb.getPropertyValue(JRBaseLineBox.PROPERTY_PADDING);
			if (propertyPadding != null) {
				checkBoxPadding.setSelection(true);
				Integer value = (Integer) propertyPadding;
				//paddingTop.setValue(value);
				//paddingBottom.setValue(value);
				paddingLeft.setValue(value);
				//paddingRight.setValue(value);
			} else {
				checkBoxPadding.setSelection(false);
				paddingTop.setValue(getPaddingValue(lb.getPropertyActualValue(JRBaseLineBox.PROPERTY_TOP_PADDING)));
				paddingBottom.setValue(getPaddingValue(lb.getPropertyActualValue(JRBaseLineBox.PROPERTY_BOTTOM_PADDING)));
				paddingLeft.setValue(getPaddingValue(lb.getPropertyActualValue(JRBaseLineBox.PROPERTY_LEFT_PADDING)));
				paddingRight.setValue(getPaddingValue(lb.getPropertyActualValue(JRBaseLineBox.PROPERTY_RIGHT_PADDING)));
				
				setControlTooltipInherithed(lb.getPropertyValue(JRBaseLineBox.PROPERTY_TOP_PADDING) == null, paddingTop, topPaddingLabel, Messages.BordersSection_padding_tool_tip);
				setControlTooltipInherithed(lb.getPropertyValue(JRBaseLineBox.PROPERTY_BOTTOM_PADDING) == null, paddingBottom, bottomPaddingLabel, Messages.BordersSection_padding_tool_tip);
				setControlTooltipInherithed(lb.getPropertyValue(JRBaseLineBox.PROPERTY_LEFT_PADDING) == null, paddingLeft, leftPaddingLabel, Messages.BordersSection_padding_tool_tip);
				setControlTooltipInherithed(lb.getPropertyValue(JRBaseLineBox.PROPERTY_RIGHT_PADDING) == null, paddingRight, rightPaddingLabel, Messages.BordersSection_padding_tool_tip);
			}
			checkBoxValueChange();
		}
	}
	
	protected void setControlTooltipInherithed(boolean isInherithed, NullableSpinner paddingControl, CLabel paddingLabel, String tooltipText) {
		paddingControl.setInherited(isInherithed);
		if (isInherithed) {
			paddingControl.setToolTipText(Messages.common_inherited_attribute + tooltipText);
			paddingLabel.setToolTipText(Messages.common_inherited_attribute + tooltipText);
			paddingLabel.setForeground(UIUtils.INHERITED_COLOR);
		} else {
			paddingControl.setToolTipText(tooltipText);
			paddingLabel.setToolTipText(tooltipText);
			paddingLabel.setForeground(ColorConstants.black);
		}
	}
	
	private LineStyleEnum getLineStyleValue(JRBoxPen boxPen, StyleResolver sr) {
		LineStyleEnum ownLineStyle = boxPen.getOwnLineStyleValue();
		if (ownLineStyle != null) {
			return ownLineStyle;
		}
		LineStyleEnum penLineStyle = boxPen.getBox().getPen().getOwnLineStyleValue();
		if (penLineStyle != null) {
			return penLineStyle;
		}
		JRStyle baseStyle = sr.getBaseStyle(boxPen.getPenContainer());
		if (baseStyle != null) {
			LineStyleEnum lineStyle = boxPen.getPen(baseStyle.getLineBox()).getLineStyleValue();
			if (lineStyle != null) {
				return lineStyle;
			}
		}
		return null;
	}
	
	private Float getLineWidthValue(JRBoxPen boxPen, StyleResolver sr) {
		Float ownLineWidth = boxPen.getOwnLineWidth();
		if (ownLineWidth != null) {
			return ownLineWidth;
		}
		Float penLineWidth = boxPen.getBox().getPen().getOwnLineWidth();
		if (penLineWidth != null) {
			return penLineWidth;
		}
		JRStyle baseStyle = sr.getBaseStyle(boxPen.getPenContainer());
		if (baseStyle != null) {
			Float lineWidth = boxPen.getPen(baseStyle.getLineBox()).getLineWidth();
			if (lineWidth != null) {
				return lineWidth;
			}
		}
		return null;
	}
	
	private Color getLineColorValue(JRBoxPen boxPen, StyleResolver sr) {
		Color ownLineColor = boxPen.getOwnLineColor();
		if (ownLineColor != null) {
			return ownLineColor;
		}
		Color penLineColor = boxPen.getBox().getPen().getOwnLineColor();
		if (penLineColor != null) {
			return penLineColor;
		}
		JRStyle baseStyle = sr.getBaseStyle(boxPen.getPenContainer());
		if (baseStyle != null) {
			Color lineColor = boxPen.getPen(baseStyle.getLineBox()).getLineColor();
			if (lineColor != null) {
				return lineColor;
			}
		}
		return null;
	}


	/**
	 * Update the right panel with the value of a linepen, but only if it's visible
	 * 
	 * @param lb
	 * @param property
	 */
	public void refreshLinePen(MLineBox lb, String property) {
		if (lb != null) {
			StyleResolver sr = new StyleResolver(lb.getJasperConfiguration());
			MLinePen lp = (MLinePen) lb.getPropertyActualValue(property);
			JRBoxPen pen = (JRBoxPen) lp.getValue();
			LineStyleEnum currentStyle = getLineStyleValue(pen, sr);
			Color currentColor = getLineColorValue(pen, sr);
			Float currentWidth = getLineWidthValue(pen, sr);
			LineStyleEnum ownStyle = pen.getOwnLineStyleValue();
			Color ownColor = pen.getOwnLineColor();
			Float ownWidth = pen.getOwnLineWidth();
			AlfaRGB backcolor = null;
			/*MLinePen globalPen = null;
			if (property != MLineBox.LINE_PEN) {
				globalPen = (MLinePen)lb.getPropertyValue(MLineBox.LINE_PEN);
				JRBoxPen globalPenValue = (JRBoxPen) globalPen.getValue();
				if (currentStyle == null) {
					//if the current style is not defined in the edge or a style check in the global box
					currentStyle = getLineStyleValue(globalPenValue, sr);	
				}
			}*/
			if (currentStyle == null) {
				//the style is not in the global box or on the edge, get the default value
				currentStyle = LineStyleEnum.values()[((Integer)lp.getPropertyActualValue(JRBasePen.PROPERTY_LINE_STYLE)) - 1];
			}
			if (currentWidth == null) {
				currentWidth = (Float) lp.getPropertyActualValue(JRBasePen.PROPERTY_LINE_WIDTH);
			}
			if (currentColor == null) {
				backcolor = (AlfaRGB) lp.getPropertyActualValue(JRBasePen.PROPERTY_LINE_COLOR);
			} else {
				backcolor = new AlfaRGB(new RGB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue()), currentColor.getAlpha());
			}
			//Float inheritedPropertyValue = (Float) lp.getPropertyValue(JRBasePen.PROPERTY_LINE_WIDTH);
			// Set the border data only if it is visible
			if (lineWidth != null && !lineWidth.isDisposed()) {
				lineWidth.setValue(ownWidth);	
				lineWidth.setDefaultValue(currentWidth);
				setControlTooltipInherithed(ownWidth == null, lineWidth, lineWidthLabel, Messages.BordersSection_width_tool_tip);
			}

			if (lineStyle != null && !isDisposed()) {
				boolean isInherited = ownStyle == null;
				lineStyle.setData(currentStyle, isInherited);
				if (isInherited) {
					lineStyle.setToolTipText(Messages.common_inherited_attribute + "Style");
					lineStyleLabel.setToolTipText(Messages.common_inherited_attribute + "Style");
					lineStyleLabel.setForeground(UIUtils.INHERITED_COLOR);
				} else {
					lineStyle.setToolTipText("Style");
					lineStyleLabel.setToolTipText("Style");
					lineStyleLabel.setForeground(ColorConstants.black);
				}
			}

			if (lineColor != null) {
				if (backcolor != null) lineColor.setColor(backcolor);
				else lineColor.setColor(AlfaRGB.getFullyOpaque(ColorConstants.black.getRGB()));
				boolean isInherited = ownColor == null;
				lineColor.setInhterited(isInherited);
				if (isInherited) {
					lineColor.setToolTipText(Messages.common_inherited_attribute + "Color");
					lineColorLabel.setToolTipText(Messages.common_inherited_attribute + "Color");
					lineColorLabel.setForeground(UIUtils.INHERITED_COLOR);
				} else {
					lineColor.setToolTipText("Color");
					lineColorLabel.setToolTipText("Color");
					lineColorLabel.setForeground(ColorConstants.black);
				}
			}
		}
	}

	/**
	 * Get the width of a specific line
	 * 
	 * @param location the line location
	 * @return the width of the line, can be null
	 */
	protected Float getLineWidth(Location location){
		MLineBox lb = (MLineBox) getElement().getPropertyValue(MGraphicElementLineBox.LINE_BOX);
		if (location == Location.BOTTOM) {
			MLinePen lp = (MLinePen) lb.getPropertyValue(MLineBox.LINE_PEN_BOTTOM);
			return (Float)lp.getPropertyActualValue(JRBasePen.PROPERTY_LINE_WIDTH);
		} else if (location == Location.TOP) {
			MLinePen lp = (MLinePen) lb.getPropertyValue(MLineBox.LINE_PEN_TOP);
			return (Float)lp.getPropertyActualValue(JRBasePen.PROPERTY_LINE_WIDTH);
		} else if (location == Location.LEFT) {
			MLinePen lp = (MLinePen) lb.getPropertyValue(MLineBox.LINE_PEN_LEFT);
			return (Float)lp.getPropertyActualValue(JRBasePen.PROPERTY_LINE_WIDTH);
		} else if (location == Location.RIGHT) {
			MLinePen lp = (MLinePen) lb.getPropertyValue(MLineBox.LINE_PEN_RIGHT);
			return (Float)lp.getPropertyActualValue(JRBasePen.PROPERTY_LINE_WIDTH);
		}
		return null;
	}
	
	public void refreshLinePen(JRBoxPen pen) {
		if (pen != null) {
			Float width = pen.getLineWidth();
			// Set the border data only if it is visible
			if (lineWidth != null && !lineWidth.isDisposed()) {
				if (width == null) {
					lineWidth.setValues(null, 0, 999);
				} else {
					lineWidth.setValue(width);
				}
				lineWidth.setInherited(pen.getOwnLineWidth() == null);
			}

			if (lineStyle != null && !isDisposed()) {
				if (pen.getLineStyleValue() == null) {
					lineStyle.setData(1);
				} else {
					int ls = lineStyle.getIndexByType(pen.getLineStyleValue());
					lineStyle.setData(ls);
				}
			}

			if (lineColor != null && !lineColor.isDisposed()) {
				if (pen.getLineColor() == null) {
					lineColor.setColor(AlfaRGB.getFullyOpaque(new RGB(0, 0, 0)));
				} else {
					AlfaRGB backcolor = Colors.getSWTRGB4AWTGBColor(pen.getLineColor());
					lineColor.setColor(backcolor);
				}
				lineColor.setInhterited(pen.getOwnLineColor() == null);
			}
		}
	}

	@Override
	protected void initializeProvidedProperties() {
		super.initializeProvidedProperties();
		addProvidedProperties(JRBaseLineBox.PROPERTY_PADDING, Messages.BordersSection_Padding_Box_Title);
		addProvidedProperties(JRBaseLineBox.PROPERTY_LEFT_PADDING, Messages.BordersSection_Left_Label);
		addProvidedProperties(JRBaseLineBox.PROPERTY_RIGHT_PADDING, Messages.common_right);
		addProvidedProperties(JRBaseLineBox.PROPERTY_TOP_PADDING, Messages.BordersSection_Top_Label);
		addProvidedProperties(JRBaseLineBox.PROPERTY_BOTTOM_PADDING, Messages.common_bottom);
		addProvidedProperties(JRBasePen.PROPERTY_LINE_COLOR, Messages.common_pen_color);
		addProvidedProperties(JRBasePen.PROPERTY_LINE_WIDTH, Messages.common_pen_width);
		addProvidedProperties(JRBasePen.PROPERTY_LINE_STYLE, Messages.common_pen_style);
		addProvidedProperties("BordersDefinitionGroup", Messages.common_borders);
	}

	public IHighlightPropertyWidget getWidgetForProperty(Object propertyId) {
		if (propertyId.equals(JRBaseLineBox.PROPERTY_PADDING))
			return new ASHighlightControl(checkBoxPadding.getParent(),
					new BorderHightLight(checkBoxPadding.getParent(), Composite.class));
		else if (propertyId.equals(JRBaseLineBox.PROPERTY_LEFT_PADDING))
			return new ASHighlightControl(paddingLeft, new BackgroundHighlight(paddingLeft));
		else if (propertyId.equals(JRBaseLineBox.PROPERTY_RIGHT_PADDING))
			return new ASHighlightControl(paddingRight, new BackgroundHighlight(paddingRight));
		else if (propertyId.equals(JRBaseLineBox.PROPERTY_TOP_PADDING))
			return new ASHighlightControl(paddingTop, new BackgroundHighlight(paddingTop));
		else if (propertyId.equals(JRBaseLineBox.PROPERTY_BOTTOM_PADDING))
			return new ASHighlightControl(paddingBottom, new BackgroundHighlight(paddingBottom));
		else if (propertyId.equals(JRBasePen.PROPERTY_LINE_COLOR))
			return new ASHighlightControl(lineColor, new BackgroundHighlight(lineColor));
		else if (propertyId.equals(JRBasePen.PROPERTY_LINE_WIDTH))
			return new ASHighlightControl(lineWidth, new BackgroundHighlight(lineWidth));
		else if (propertyId.equals(JRBasePen.PROPERTY_LINE_STYLE))
			return new ASHighlightControl(lineStyle.getControl(), new BackgroundHighlight(lineStyle.getControl()));
		else if (propertyId.equals("BordersDefinitionGroup"))
			return new ASHighlightControl(rightPanel, new BorderHightLight(rightPanel, Composite.class));
		return null;
	}

}
