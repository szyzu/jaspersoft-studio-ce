/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.editor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.ide.IDEEncoding;
import org.eclipse.ui.ide.dialogs.AbstractEncodingFieldEditor;

public class CEncodingFieldEditor extends AbstractEncodingFieldEditor {

	/**
	 * Creates a new encoding field editor with the given preference name, label and parent.
	 * 
	 * @param name
	 *          the name of the preference this field editor works on
	 * @param labelText
	 *          the label text of the field editor
	 * @param groupTitle
	 *          the title for the field editor's control. If groupTitle is <code>null</code> the control will be
	 *          unlabelled (by default a {@link Composite} instead of a {@link Group}.
	 * @param parent
	 *          the parent of the field editor's control
	 * @see AbstractEncodingFieldEditor#setGroupTitle(String)
	 * @since 3.3
	 */
	public CEncodingFieldEditor(String name, String labelText, String groupTitle, Composite parent) {
		super();
		init(name, labelText);
		setGroupTitle(groupTitle);
		createControl(parent);
	}

	/**
	 * Create a new instance of the receiver on the preference called name with a label of labelText.
	 * 
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public CEncodingFieldEditor(String name, String labelText, Composite parent) {
		super();
		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected Composite createEncodingGroup(Composite parent, int numColumns) {
		topControl = super.createEncodingGroup(parent, numColumns);
		return topControl;
	}

	private Composite topControl;

	public Control[] getControls() {
		if (topControl != null)
			return topControl.getChildren();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.ide.dialogs.AbstractEncodingFieldEditor#getStoredValue()
	 */
	protected String getStoredValue() {
		return getPreferenceStore().getString(getPreferenceName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore() {
		String encoding = getSelectedEncoding();

		if (hasSameEncoding(encoding)) {
			return;
		}

		IDEEncoding.addIDEEncoding(encoding);

		if (encoding.equals(getDefaultEnc())) {
			getPreferenceStore().setToDefault(getPreferenceName());
		} else {
			getPreferenceStore().setValue(getPreferenceName(), encoding);
		}
	}

	public void setPreferenceStore(IPreferenceStore store) {
		if (store != null)
			super.setPreferenceStore(store);
	}

}
