/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.descriptors;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.SPText;

/**
 * This class can handle a textual property, but differently from other properties it also offer the methods to validate
 * the input and if the input is not validated any changes done in the text area are reverted.
 * 
 * @author Orlandin Marco
 * 
 */
public class JSSValidatedTextPropertyDescriptor extends JSSTextPropertyDescriptor {

	/**
	 * class that extends the standard text control widget to override the text changed method. This is method is called
	 * when a value was inserted in the text area and need to be send to the related node. It differs from the original
	 * because of the check with a validator before to modify the node. If the validator return an error then this is
	 * displayed and the value in the textarea is reset to the actual value of the node. If the validator is null the node
	 * is not changed
	 * 
	 * @author Orlandin Marco
	 * 
	 */
	private class SPValidatedText<T extends IPropertyDescriptor> extends SPText<T> {

		public SPValidatedText(Composite parent, AbstractSection section, T pDescriptor) {
			super(parent, section, pDescriptor);
		}

		@Override
		protected void handleTextChanged(AbstractSection section, Object property, String text) {
			ICellEditorValidator validator = getValidator();
			if (validator != null) {
				String validateResult = validator.isValid(text);
				if (validateResult == null)
					super.handleTextChanged(section, property, text);
				else {
					MessageDialog.openWarning(UIUtils.getShell(), Messages.JSSValidatedTextPropertyDescriptor_unableToEditTitle,
							validateResult);
					setData(section.getElement(), section.getElement().getPropertyActualValue(getId()));
				}
			}
		}
		
		protected int getStyle() {
			return SWT.BORDER;
		}

	}

	/**
	 * Create an instance of the class with a validator. Note that the validator can be null without error, but every edit
	 * done using this widget is discarded in that case
	 * 
	 * @param validator
	 *          validator used to check if the value inserted in the textarea is valid for the property node. As validator
	 *          is suggested an instanceof AbstractJSSCellEditorValidator, since this update the target of the validator
	 *          when the widget is created. This is done because many property descriptors are created statically and for
	 *          this reason in some cases it isn't possible keep an instance of the target inside the descriptor, since
	 *          the descriptor is shared from more elements
	 */
	public JSSValidatedTextPropertyDescriptor(Object id, String displayName, ICellEditorValidator validator) {
		super(id, displayName);
		setValidator(validator);
	}

	/**
	 * Create an instance of the class with a validator. Note that the validator can be null without error, but every edit
	 * done using this widget is discarded in that case
	 * 
	 * @param validator
	 *          validator used to check if the value inserted in the textarea is valid for the property node. As validator
	 *          is suggested an instanceof AbstractJSSCellEditorValidator, since this update the target of the validator
	 *          when the widget is created. This is done because many property descriptors are created statically and for
	 *          this reason in somecases it isn't possible keep an instance of the target inside the descriptor, since the
	 *          descriptor is shared from more elements
	 */
	public JSSValidatedTextPropertyDescriptor(Object id, String displayName, int style, ICellEditorValidator validator) {
		super(id, displayName, style);
		setValidator(validator);
	}

	/**
	 * Return the text area widget and if the validator is set and it is an instance of AbstractJSSCellEditorValidator,
	 * the target of the validator is set to the same element edited by the widget
	 */
	@Override
	public ASPropertyWidget<JSSValidatedTextPropertyDescriptor> createWidget(Composite parent, AbstractSection section) {
		ICellEditorValidator validator = getValidator();
		if (validator instanceof AbstractJSSCellEditorValidator) {
			AbstractJSSCellEditorValidator extendedValidator = (AbstractJSSCellEditorValidator) validator;
			extendedValidator.setTargetNode(section.getElement());
		}
		ASPropertyWidget<JSSValidatedTextPropertyDescriptor> textWidget = new SPValidatedText<JSSValidatedTextPropertyDescriptor>(parent, section, this);
		textWidget.setReadOnly(readOnly);
		return textWidget;
	}

}
