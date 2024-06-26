/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.editor.text;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.internal.preferences.EclipsePreferences;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.jaspersoft.studio.messages.Messages;

import net.sf.jasperreports.eclipse.util.Misc;

public class TextFieldEditor extends FieldEditor {

	/**
	 * Validation strategy constant (value <code>0</code>) indicating that the
	 * editor should perform validation after every key stroke.
	 * 
	 * @see #setValidateStrategy
	 */
	public static final int VALIDATE_ON_KEY_STROKE = 0;

	/**
	 * Validation strategy constant (value <code>1</code>) indicating that the
	 * editor should perform validation only when the text widget loses focus.
	 * 
	 * @see #setValidateStrategy
	 */
	public static final int VALIDATE_ON_FOCUS_LOST = 1;

	/**
	 * Text limit constant (value <code>-1</code>) indicating unlimited text limit
	 * and width.
	 */
	public static int UNLIMITED = -1;

	/**
	 * Cached valid state.
	 */
	private boolean isValid;

	/**
	 * Old text value.
	 * 
	 * @since 3.4 this field is protected.
	 */
	protected String oldValue;

	/**
	 * The text field, or <code>null</code> if none.
	 */
	private Text textField;

	/**
	 * Width of text field in characters; initially unlimited.
	 */
	// private int widthInChars = UNLIMITED;

	/**
	 * Text limit of text field in characters; initially unlimited.
	 */
	private int textLimit = UNLIMITED;

	/**
	 * The error message, or <code>null</code> if none.
	 */
	private String errorMessage;

	/**
	 * Indicates whether the empty string is legal; <code>true</code> by default.
	 */
	private boolean emptyStringAllowed = true;
	private boolean isNullAllowed = false;

	/**
	 * The validation strategy; <code>VALIDATE_ON_KEY_STROKE</code> by default.
	 */
	private int validateStrategy = VALIDATE_ON_KEY_STROKE;

	private Button bIsNull;

	/**
	 * Creates a string field editor. Use the method <code>setTextLimit</code> to
	 * limit the text.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param width
	 *            the width of the text input field in characters, or
	 *            <code>UNLIMITED</code> for no limit
	 * @param strategy
	 *            either <code>VALIDATE_ON_KEY_STROKE</code> to perform on the fly
	 *            checking (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to
	 *            perform validation only after the text has been typed in
	 * @param parent
	 *            the parent of the field editor's control
	 * @since 2.0
	 */
	public TextFieldEditor(String name, String labelText, int width, int strategy, boolean isNullAllowed,
			Composite parent) {
		init(name, labelText);
		this.isNullAllowed = isNullAllowed;
		// widthInChars = width;
		setValidateStrategy(strategy);
		isValid = false;
		errorMessage = "Field contains an invalid value";//$NON-NLS-1$
		createControl(parent);
	}

	/**
	 * Creates a string field editor. Use the method <code>setTextLimit</code> to
	 * limit the text.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param width
	 *            the width of the text input field in characters, or
	 *            <code>UNLIMITED</code> for no limit
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public TextFieldEditor(String name, String labelText, int width, Composite parent) {
		this(name, labelText, width, VALIDATE_ON_KEY_STROKE, false, parent);
	}

	/**
	 * Creates a string field editor of unlimited width. Use the method
	 * <code>setTextLimit</code> to limit the text.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public TextFieldEditor(String name, String labelText, Composite parent) {
		this(name, labelText, UNLIMITED, parent);
	}

	/**
	 * Creates a string field editor of unlimited width. Use the method
	 * <code>setTextLimit</code> to limit the text.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public TextFieldEditor(String name, String labelText, boolean isNullAllowed, Composite parent) {
		this(name, labelText, UNLIMITED, VALIDATE_ON_KEY_STROKE, isNullAllowed, parent);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns) {
		// GridData gd = (GridData) textField.getLayoutData();
		// gd.horizontalSpan = numColumns - 1;
		// // We only grab excess space if we have to
		// // If another field editor has more columns then
		// // we assume it is setting the width.
		// gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	/**
	 * Checks whether the text input field contains a valid value or not.
	 * 
	 * @return <code>true</code> if the field value is valid, and <code>false</code>
	 *         if invalid
	 */
	protected boolean checkState() {
		boolean result = false;
		if (isNullAllowedAndSet())
			result = true;
		else {
			if (emptyStringAllowed) {
				result = true;
			}

			if (textField == null) {
				result = false;
			}

			String txt = textField.getText();

			result = (txt.trim().length() > 0) || emptyStringAllowed;

			// call hook for subclasses
			result = result && doCheckState();
		}
		if (result) {
			clearErrorMessage();
		} else {
			showErrorMessage(errorMessage);
		}

		return result;
	}

	/**
	 * Hook for subclasses to do specific state checks.
	 * <p>
	 * The default implementation of this framework method does nothing and returns
	 * <code>true</code>. Subclasses should override this method to specific state
	 * checks.
	 * </p>
	 * 
	 * @return <code>true</code> if the field value is valid, and <code>false</code>
	 *         if invalid
	 */
	protected boolean doCheckState() {
		return true;
	}

	/**
	 * Fills this field editor's basic controls into the given parent.
	 * <p>
	 * The string field implementation of this <code>FieldEditor</code> framework
	 * method contributes the text field. Subclasses may override but must call
	 * <code>super.doFillIntoGrid</code>.
	 * </p>
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		getLabelControl(parent);

		textField = getTextControl(parent);
		GridData gd = new GridData(GridData.FILL_BOTH);
		if (isNullAllowed)
			gd.horizontalSpan = 2;
		textField.setLayoutData(gd);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad() {
		if (textField != null) {
			IPreferenceStore pstore = getPreferenceStore();
			String value = pstore.getString(getPreferenceName());
			if (isNullAllowed && bIsNull != null) {
				if (pstore instanceof ScopedPreferenceStore) {
					try {
						Method m = getPrivateInternalGet(pstore.getClass());
						if (m != null)
							value = (String) m.invoke(pstore, getPreferenceName());
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}

				bIsNull.setSelection(value == null);
				textField.setEnabled(value != null);
			}
			textField.setText(Misc.nvl(value));
			oldValue = value;
		}
	}

	private Method getPrivateInternalGet(Class<?> clazz) {
		if (clazz == null)
			return null;
		try {
			Method m = clazz.getDeclaredMethod("internalGet", String.class); //$NON-NLS-1$
			if (m != null) {
				m.setAccessible(true);
				return m;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			return getPrivateInternalGet(clazz.getSuperclass());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault() {
		if (textField != null) {
			String value = getPreferenceStore().getDefaultString(getPreferenceName());
			if (isNullAllowed && bIsNull != null) {
				bIsNull.setSelection(value == null);
				textField.setEnabled(value != null);
			}
			textField.setText(Misc.nvl(value));
		}
		valueChanged();
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore() {
		IPreferenceStore pstore = getPreferenceStore();
		if (isNullAllowedAndSet()) {
			if (pstore instanceof EclipsePreferences)
				((EclipsePreferences) pstore).remove(getPreferenceName());
			else if (pstore instanceof ScopedPreferenceStore) {
				// try {
				for (IEclipsePreferences ep : ((ScopedPreferenceStore) pstore).getPreferenceNodes(true))
					ep.remove(getPreferenceName());

				// Method m = pstore.getClass().getDeclaredMethod("getStorePreferences");
				// m.setAccessible(true);
				// if (m != null) {
				// IEclipsePreferences ep = (IEclipsePreferences) m.invoke(pstore);
				// ep.remove(getPreferenceName());
				// }
				// } catch (SecurityException e) {
				// e.printStackTrace();
				// } catch (NoSuchMethodException e) {
				// e.printStackTrace();
				// } catch (IllegalArgumentException e) {
				// e.printStackTrace();
				// } catch (IllegalAccessException e) {
				// e.printStackTrace();
				// } catch (InvocationTargetException e) {
				// e.printStackTrace();
				// }

			} else
				pstore.setValue(getPreferenceName(), null);
		} else
			pstore.setValue(getPreferenceName(), textField.getText());
	}

	/**
	 * Returns the error message that will be displayed when and if an error occurs.
	 * 
	 * @return the error message, or <code>null</code> if none
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls() {
		if (isNullAllowed)
			return 2;
		return 1;
	}

	/**
	 * Returns the field editor's value.
	 * 
	 * @return the current value
	 */
	public String getStringValue() {
		if (isNullAllowedAndSet())
			return null;
		if (textField != null)
			return textField.getText();
		return getPreferenceStore().getString(getPreferenceName());
	}

	protected boolean isNullAllowedAndSet() {
		return isNullAllowed && bIsNull != null && bIsNull.getSelection();
	}

	/**
	 * Returns this field editor's text control.
	 * 
	 * @return the text control, or <code>null</code> if no text field is created
	 *         yet
	 */
	public Text getTextControl() {
		return textField;
	}

	/**
	 * Returns this field editor's text control.
	 * <p>
	 * The control is created if it does not yet exist
	 * </p>
	 * 
	 * @param parent
	 *            the parent
	 * @return the text control
	 */
	public Text getTextControl(Composite parent) {
		if (textField == null) {
			if (isNullAllowed) {
				bIsNull = new Button(parent, SWT.CHECK);
				bIsNull.setText(Messages.TextFieldEditor_setToNullCheckbox);
				bIsNull.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						textField.setEnabled(!bIsNull.getSelection());
						textField.setText(""); //$NON-NLS-1$
					}
				});
			}

			textField = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			textField.setFont(parent.getFont());
			switch (validateStrategy) {
			case VALIDATE_ON_KEY_STROKE:
				textField.addKeyListener(new KeyAdapter() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.
					 * KeyEvent)
					 */
					public void keyReleased(KeyEvent e) {
						valueChanged();
					}
				});
				textField.addFocusListener(new FocusAdapter() {
					// Ensure that the value is checked on focus loss in case we
					// missed a keyRelease or user hasn't released key.
					// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=214716
					public void focusLost(FocusEvent e) {
						valueChanged();
					}
				});

				break;
			case VALIDATE_ON_FOCUS_LOST:
				textField.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						clearErrorMessage();
					}
				});
				textField.addFocusListener(new FocusAdapter() {
					public void focusGained(FocusEvent e) {
						refreshValidState();
					}

					public void focusLost(FocusEvent e) {
						valueChanged();
						clearErrorMessage();
					}
				});
				break;
			default:
				Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
			}
			textField.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					textField = null;
				}
			});
			if (textLimit > 0) {// Only set limits above 0 - see SWT spec
				textField.setTextLimit(textLimit);
			}
		} else {
			checkParent(textField, parent);
		}
		return textField;
	}

	/**
	 * Returns whether an empty string is a valid value.
	 * 
	 * @return <code>true</code> if an empty string is a valid value, and
	 *         <code>false</code> if an empty string is invalid
	 * @see #setEmptyStringAllowed
	 */
	public boolean isEmptyStringAllowed() {
		return emptyStringAllowed;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public boolean isValid() {
		return isValid;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void refreshValidState() {
		isValid = checkState();
	}

	/**
	 * Sets whether the empty string is a valid value or not.
	 * 
	 * @param b
	 *            <code>true</code> if the empty string is allowed, and
	 *            <code>false</code> if it is considered invalid
	 */
	public void setEmptyStringAllowed(boolean b) {
		emptyStringAllowed = b;
	}

	/**
	 * Sets the error message that will be displayed when and if an error occurs.
	 * 
	 * @param message
	 *            the error message
	 */
	public void setErrorMessage(String message) {
		errorMessage = message;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus() {
		if (textField != null) {
			textField.setFocus();
		}
	}

	/**
	 * Sets this field editor's value.
	 * 
	 * @param value
	 *            the new value, or <code>null</code> meaning the empty string
	 */
	public void setStringValue(String value) {
		if (textField != null) {
			if (value == null) {
				value = "";//$NON-NLS-1$
			}
			oldValue = textField.getText();
			if (!oldValue.equals(value)) {
				textField.setText(value);
				valueChanged();
			}
		}
	}

	/**
	 * Sets this text field's text limit.
	 * 
	 * @param limit
	 *            the limit on the number of character in the text input field, or
	 *            <code>UNLIMITED</code> for no limit
	 */
	public void setTextLimit(int limit) {
		textLimit = limit;
		if (textField != null) {
			textField.setTextLimit(limit);
		}
	}

	/**
	 * Sets the strategy for validating the text.
	 * <p>
	 * Calling this method has no effect after <code>createPartControl</code> is
	 * called. Thus this method is really only useful for subclasses to call in
	 * their constructor. However, it has public visibility for backward
	 * compatibility.
	 * </p>
	 * 
	 * @param value
	 *            either <code>VALIDATE_ON_KEY_STROKE</code> to perform on the fly
	 *            checking (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to
	 *            perform validation only after the text has been typed in
	 */
	public void setValidateStrategy(int value) {
		Assert.isTrue(value == VALIDATE_ON_FOCUS_LOST || value == VALIDATE_ON_KEY_STROKE);
		validateStrategy = value;
	}

	/**
	 * Shows the error message set via <code>setErrorMessage</code>.
	 */
	public void showErrorMessage() {
		showErrorMessage(errorMessage);
	}

	/**
	 * Informs this field editor's listener, if it has one, about a change to the
	 * value (<code>VALUE</code> property) provided that the old and new values are
	 * different.
	 * <p>
	 * This hook is <em>not</em> called when the text is initialized (or reset to
	 * the default value) from the preference store.
	 * </p>
	 */
	protected void valueChanged() {
		setPresentsDefaultValue(false);
		boolean oldState = isValid;
		refreshValidState();

		if (isValid != oldState) {
			fireStateChanged(IS_VALID, oldState, isValid);
		}

		String newValue = textField.getText();
		if (!newValue.equals(oldValue)) {
			fireValueChanged(VALUE, oldValue, newValue);
			oldValue = newValue;
		}
	}

	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getTextControl(parent).setEnabled(enabled);
		bIsNull.setEnabled(enabled);
	}
}
