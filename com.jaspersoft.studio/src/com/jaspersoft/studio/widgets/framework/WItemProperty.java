/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.widgets.framework;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.ExpressionEditorSupportUtil;
import com.jaspersoft.studio.editor.expression.IExpressionContextSetter;
import com.jaspersoft.studio.property.descriptor.expression.dialog.JRExpressionEditor;
import com.jaspersoft.studio.property.itemproperty.desc.ItemPropertyBaseLabelProvider;
import com.jaspersoft.studio.swt.events.ExpressionModifiedListener;
import com.jaspersoft.studio.utils.ImageUtils;
import com.jaspersoft.studio.widgets.framework.events.ItemPropertyModifiedEvent;
import com.jaspersoft.studio.widgets.framework.events.ItemPropertyModifiedListener;
import com.jaspersoft.studio.widgets.framework.manager.ItemPropertyLayout;
import com.jaspersoft.studio.widgets.framework.manager.ItemPropertyLayoutData;
import com.jaspersoft.studio.widgets.framework.manager.LazyExpressionLabel;
import com.jaspersoft.studio.widgets.framework.model.WidgetPropertyDescriptor;
import com.jaspersoft.studio.widgets.framework.ui.IDialogProvider;
import com.jaspersoft.studio.widgets.framework.ui.ItemPropertyDescription;
import com.jaspersoft.studio.widgets.framework.ui.dialog.ItemPropertyElementDialog;
import com.jaspersoft.studio.widgets.framework.ui.menu.IMenuProvider;
import com.jaspersoft.studio.widgets.framework.ui.menu.StandardContextualMenu;
import com.jaspersoft.studio.widgets.framework.ui.providers.BaseLabelProvider;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;

/**
 * Main component of the widgets framework. It will contains the widget for the
 * editing of the value, both expression and static value, and the button to
 * open the edit dialog. It can have also a label set before the widget and it
 * will show the button/label to open the expression editor when the widget is
 * in expression mode. It will use internally a custom layout to align the
 * element to provide better performances. Be carefull into changing it
 * 
 * @author Orlandin Marco
 *
 */
public class WItemProperty extends Composite implements IExpressionContextSetter, IWItemProperty {

	/**
	 * Style bit: for only the expression mode on the advance dialog
	 */
	public static final int FORCE_EXPRESSION_DIALOG = 1 << 1;

	/**
	 * Style bit: for only the simple mode on the advance dialog
	 */
	public static final int FORCE_SIMPLE_MODE = 1 << 2;

	/**
	 * Suffix for properties requiring a custom simple mode handling
	 */
	public static final String CUSTOM_SIMPLE_MODE_SUFFIX = "_customSimpleMode";

	/**
	 * Icon used in the button to open the edit dialog
	 */
	public static final String BUTTON_ICON_PATH = "icons/resources/expressionedit-16.png"; //$NON-NLS-1$

	/**
	 * The context for the expression
	 */
	private ExpressionContext expContext;

	/**
	 * The control used to edit the value
	 */
	private Control editorControl;

	/**
	 * The button to open the dialog to switch between the static value or
	 * expression
	 */
	private Button btnEditExpression;

	/**
	 * Label provider, not used internally but can be used from outside to
	 * resolve the string
	 */
	private BaseLabelProvider lprovider = null;

	/**
	 * The label that can be clicked to open the expression editor
	 */
	private LazyExpressionLabel expressionEditLabel;

	/**
	 * Flag typically set when the widget are writing the value into the model
	 * element
	 */
	private boolean isRefresh = false;

	/**
	 * Flag set when the widget are updating their value, by reading if from the
	 * model element
	 */
	private boolean isUpdating = false;

	/**
	 * {@link ItemPropertyDescription} from where the editor control is build
	 */
	private ItemPropertyDescription<?> ipDesc;

	/**
	 * {@link IPropertyEditor} used to store the value into the element
	 */
	private IPropertyEditor editor;

	/**
	 * Optional label that can be show before the control
	 */
	private Label titleLabel = null;

	/**
	 * The layout data used to dispose the content
	 */
	private ItemPropertyLayoutData contentLayoutData = new ItemPropertyLayoutData();

	/**
	 * Expression modify listeners
	 */
	private List<ItemPropertyModifiedListener> listeners = new ArrayList<>();

	private Image resizedImageForExprBtn;
	private Image disabledImageForExprBtn;

	/**
	 * Create the widget without a label
	 * 
	 * @param parent the parent of the widget
	 * @param style the style of the main composite for this element
	 * @param widgetDescriptor the descriptor of the value control
	 * @param editor the editor used to write and read the values from the
	 * target object, must be not null
	 */
	public WItemProperty(Composite parent, int style, ItemPropertyDescription<?> widgetDescriptor,
			IPropertyEditor editor) {
		this(parent, style, null, widgetDescriptor, editor);
	}

	/**
	 * Create the widget with a label
	 * 
	 * @param parent the parent of the widget
	 * @param style the style of the main composite for this element
	 * @param descriptor the descriptor used to create the label. If null no
	 * label is created
	 * @param widgetDescriptor the descriptor of the value control
	 * @param editor the editor used to write and read the values from the
	 * target object, must be not null
	 */
	public WItemProperty(Composite parent, int style, WidgetPropertyDescriptor descriptor,
			ItemPropertyDescription<?> widgetDescriptor, IPropertyEditor editor) {
		super(parent, style);
		Assert.isNotNull(editor);
		this.ipDesc = widgetDescriptor;
		this.editor = editor;

		if (descriptor != null) {
			titleLabel = new Label(this, SWT.NONE);
			titleLabel.setText(descriptor.getLabel());
			titleLabel.setToolTipText(descriptor.getDescription());
		}

		// Create the expression label
		expressionEditLabel = new LazyExpressionLabel(this);
		expressionEditLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (!ExpressionEditorSupportUtil.isExpressionEditorDialogOpen()) {
					JRExpressionEditor wizard = new JRExpressionEditor();
					wizard.setValue(
							(JRDesignExpression) getPropertyEditor().getPropertyValueExpression(ipDesc.getName()));
					wizard.setExpressionContext(expContext);
					WizardDialog dialog = ExpressionEditorSupportUtil
							.getExpressionEditorWizardDialog(Display.getDefault().getActiveShell(), wizard);
					if (dialog.open() == Dialog.OK) {
						JRDesignExpression exprTmp = wizard.getValue();
						setValue(null, exprTmp);
					}
				}
			}

		});

		// Create the simple control
		editorControl = ipDesc.createControl(this, this);

		// Create the edit expression button
		btnEditExpression = new Button(this, SWT.FLAT);
		if (contentLayoutData != null) {
			btnEditExpression.setImage(getResizedImageForExprButton());
		} else {
			btnEditExpression.setImage(getDisabledImageForExprButton());
		}
		btnEditExpression.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEditButton();
			}

		});
		if (widgetDescriptor != null)
			UIUtils.getDisplay().asyncExec(() -> {
				if (!editorControl.isDisposed()) {
					String tmp = getToolTip();
					String tt = tmp != null ? tmp : widgetDescriptor.getToolTip();
					expressionEditLabel.setToolTipText(tt);
					editorControl.setToolTipText(tt);
					btnEditExpression.setToolTipText(tt);
				}
			});
		
		this.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// Remove modify listeners
				Object[] listenersArray = listeners.toArray();
				for (Object l : listenersArray) {
					removeModifyListener((ItemPropertyModifiedListener) l);
				}
				listeners.clear();
				listeners = null;
				if(resizedImageForExprBtn!=null) {
					resizedImageForExprBtn.dispose();
					resizedImageForExprBtn=null;
				}
				if(disabledImageForExprBtn!=null) {
					disabledImageForExprBtn.dispose();
					disabledImageForExprBtn=null;
				}				
			}
		});

		setLayout(new ItemPropertyLayout(this, titleLabel, expressionEditLabel, editorControl, btnEditExpression));
	}

	private Image getResizedImageForExprButton() {
		if(resizedImageForExprBtn!=null) {
			resizedImageForExprBtn.dispose();
		}
		Point buttonSize = contentLayoutData.getButtonSize();
		Image loadedImage = JaspersoftStudioPlugin.getInstance().getImage(BUTTON_ICON_PATH);
		resizedImageForExprBtn = ImageUtils.resize(loadedImage, buttonSize.x / 2, buttonSize.y / 2);
		return resizedImageForExprBtn;
	}
	
	private Image getDisabledImageForExprButton() {
		if(disabledImageForExprBtn==null) {
			Image loadedImage = JaspersoftStudioPlugin.getInstance().getImage(BUTTON_ICON_PATH);
			disabledImageForExprBtn = new Image(loadedImage.getDevice(), loadedImage.getImageData());
		}
		return disabledImageForExprBtn;
	}

	@Override
	public void setRefresh(boolean refreshing) {
		this.isRefresh = refreshing;
	}

	/**
	 * The section is refreshing when a setValue or an update method are called
	 */
	@Override
	public boolean isRefresh() {
		return isRefresh || isUpdating;
	}

	/**
	 * Sets the layout data information for the custom widget controls.
	 */
	@SuppressWarnings("unused")
	private void configureWidgetsLayoutData() {
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				layout(true, true);
			}
		});
	}

	/**
	 * Set a value both on the widget and on the element
	 * 
	 * @param staticValue the new static value
	 * @param expressionValue the new expression value
	 */
	public void setValue(String staticValue, JRExpression expressionValue) {
		setRefresh(true);
		try {
			getPropertyEditor().createUpdateProperty(ipDesc.getName(), staticValue, expressionValue);
			updateWidget();

			// Notifies the listeners of the new expression
			fireModifyEvent(staticValue, expressionValue);
		} finally {
			setRefresh(false);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.editorControl.setEnabled(enabled);
		this.btnEditExpression.setEnabled(enabled);
	}

	/**
	 * Return the current label provider
	 * 
	 * @return a not null label provider, if no one is set it return a defaul
	 * {@link ItemPropertyBaseLabelProvider}
	 */
	public BaseLabelProvider getLabelProvider() {
		if (lprovider == null) {
			return BaseLabelProvider.INSTANCE;
		}
		return lprovider;
	}

	/**
	 * Set the label provider for the element
	 * 
	 * @param lprovider the new label provider, it can be null
	 */
	public void setLabelProvider(BaseLabelProvider lprovider) {
		this.lprovider = lprovider;
	}

	/**
	 * Set the expression context for the expression editor
	 */
	public void setExpressionContext(ExpressionContext expContext) {
		this.expContext = expContext;
	}

	/**
	 * Return the expression context
	 * 
	 * @return the expression context currently set, <code>null</code> if none
	 */
	public ExpressionContext getExpressionContext() {
		return this.expContext;
	}

	/**
	 * Adds a new listener that will be notified of any expression
	 * change/notification.
	 * 
	 * @param ml the new {@link ExpressionModifiedListener} to add
	 */
	public void addModifyListener(ItemPropertyModifiedListener ml) {
		listeners.add(ml);
	}

	/**
	 * Removes an {@link ExpressionModifiedListener} instance.
	 * 
	 * @param ml the {@link ExpressionModifiedListener} instance to be removed
	 */
	public void removeModifyListener(ItemPropertyModifiedListener ml) {
		listeners.remove(ml);
	}

	/**
	 * Return the control widget
	 */
	public Control getControl() {
		return editorControl;
	}

	/**
	 * Notifies the listeners of the expression change.
	 */
	protected void fireModifyEvent(String staticValue, JRExpression expressionValue) {
		ItemPropertyModifiedEvent event = new ItemPropertyModifiedEvent(this);
		event.staticValue = staticValue;
		event.expressionValue = expressionValue;
		event.propertyName = ipDesc.getName();
		for (ItemPropertyModifiedListener ml : listeners)
			ml.itemModified(event);
	}

	/**
	 * Open the dialog to switch between expression and static value
	 */
	protected void handleEditButton() {
		ItemPropertyElementDialog dialog = null;
		// if the property description is a dialog provider use the dialog
		// provided by it
		if (ipDesc instanceof IDialogProvider) {
			dialog = ((IDialogProvider) ipDesc).getDialog(this);
		} else {
			dialog = new ItemPropertyElementDialog(UIUtils.getShell(), ipDesc, this);
			dialog.setHelpAvailable(false);
			dialog.setForceExpressionMode(hasForcedExpression());
		}
		if (dialog.open() == Dialog.OK) {
			setValue(dialog.getStaticValue(), dialog.getExpressionValue());
		}
	}

	@Override
	public String getStaticValue() {
		return getPropertyEditor().getPropertyValue(ipDesc.getName());
	}

	@Override
	public JRExpression getExpressionValue() {
		return getPropertyEditor().getPropertyValueExpression(ipDesc.getName());
	}

	/**
	 * Return the name of the property handled by this {@link WItemProperty}
	 */
	@Override
	public String getPropertyName() {
		return ipDesc.getName();
	}

	public String getToolTip() {
		return null;
	}

	/**
	 * Return if the current property is in expression mode, by default a
	 * property is in expression mode if there is an expression defined for it.
	 * It can be overridden to provide a different behavior
	 */
	@Override
	public boolean isExpressionMode() {
		return !hasForcedSimpleMode() && getPropertyEditor().getPropertyValueExpression(ipDesc.getName()) != null;
	}

	/**
	 * Return an instance of the standard menu provider, that handle the action
	 * "set to default" and reset to null"
	 */
	@Override
	public IMenuProvider getContextualMenuProvider() {
		return StandardContextualMenu.INSTANCE;
	}

	@Override
	public void updateWidget(boolean refreshLayout) {
		isUpdating = true;
		try {
			ipDesc.update(editorControl, this);
			// show or hide the expression label
			if (isExpressionMode()) {
				expressionEditLabel.setImage(JaspersoftStudioPlugin.getInstance().getImage("icons/functions_icon.png"));
				layout(true, true);
			} else {
				expressionEditLabel.setImage(null);
				if (refreshLayout)
					layout(true, true);
			}
		} finally {
			isUpdating = false;
		}
	}

	/**
	 * Update the widget, avoid to re-trigger the set value trough the
	 * isUpdating flag This will also re-layout the widget
	 */
	@Override
	public void updateWidget() {
		updateWidget(true);
	}

	@Override
	public IPropertyEditor getPropertyEditor() {
		return editor;
	}

	@Override
	public void setPropertyEditor(IPropertyEditor editor) {
		Assert.isNotNull(editor);
		this.editor = editor;
	}

	@Override
	public Object getFallbackValue() {
		return ipDesc.getFallbackValue();
	}

	/**
	 * Return the label of the current property
	 */
	public String getPropertyLabel() {
		return ipDesc.getLabel();
	}

	/**
	 * Validate the current value looking is it is mandatory but still empty.
	 * Can be overridden to provide a complex behavior
	 * 
	 * @return a list of string, empty if there are no validation errors or with
	 * the list of error messages if there are validation errors
	 */
	public List<String> isValueValid() {
		List<String> result = new ArrayList<>();
		if (ipDesc != null && editor != null && ipDesc.isMandatory()) {
			String staticValue = getStaticValue();
			boolean hasStaticValue = true;
			if (staticValue == null || staticValue.isEmpty()) {
				hasStaticValue = false;
			}
			JRExpression expValue = getExpressionValue();
			boolean hasExpValue = true;
			if (expValue == null) {
				hasExpValue = false;
			}
			if (!(hasStaticValue || hasExpValue)) {
				String message = "Property {0} is mandatory";
				result.add(MessageFormat.format(message, new Object[] { getPropertyLabel() }));
			}
		}
		return result;
	}

	/**
	 * Set the visibility of the control and re-layout it
	 */
	@Override
	public void setVisible(boolean value) {
		super.setVisible(value);
		layout(true, true);
	}

	/**
	 * The visibility of this controls depends only from it and not from the
	 * parent
	 */
	@Override
	public boolean isVisible() {
		if (!isDisposed())
			return getVisible();
		return false;
	}
	
	/**
	 * Set the layout for the content of this {@link WItemProperty}, after the
	 * set operation a layout of this container is triggered
	 * 
	 * @param data a not null {@link ItemPropertyLayoutData}
	 */
	public void setContentLayoutData(ItemPropertyLayoutData data) {
		Assert.isNotNull(data);
		this.contentLayoutData = data;
		Image oldImage = btnEditExpression.getImage();
		if (oldImage != null && !oldImage.isDisposed()) {
			oldImage.dispose();
		}
		btnEditExpression.setImage(getResizedImageForExprButton());
		layout();
	}

	/**
	 * Return the current layout data for this container
	 * 
	 * @return a not null {@link ItemPropertyLayoutData}
	 */
	public ItemPropertyLayoutData getContentLayoutData() {
		return contentLayoutData;
	}

	/**
	 * Check if the elements has the flag to force only the expression editing
	 * in the dialog
	 * 
	 * @return true if the {@link WItemProperty} was created with the force
	 * expression dialog stylebit false otherwise
	 */
	public boolean hasForcedExpression() {
		return (getStyle() & FORCE_EXPRESSION_DIALOG) == FORCE_EXPRESSION_DIALOG;
	}

	/**
	 * Check if the elements has the flag to force only the simple mode editing
	 * 
	 * @return true if the {@link WItemProperty} was created with the force
	 * simple mode dialog stylebit false otherwise
	 */
	public boolean hasForcedSimpleMode() {
		return (getStyle() & FORCE_SIMPLE_MODE) == FORCE_SIMPLE_MODE;
	}
}
