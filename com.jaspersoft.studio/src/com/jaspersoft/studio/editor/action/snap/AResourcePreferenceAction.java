/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.snap;

import net.sf.jasperreports.eclipse.util.FileUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.preferences.util.FieldEditorOverlayPage;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public abstract class AResourcePreferenceAction extends Action {

	protected JasperReportsConfiguration jrConfig;
	protected final static String pageID = JaspersoftStudioPlugin.getUniqueIdentifier();

	/**
	 * Constructor
	 * 
	 * @param diagramViewer
	 *          the GraphicalViewer whose grid enablement and visibility properties are to be toggled
	 */
	public AResourcePreferenceAction(JasperReportsConfiguration jrConfig) {
		super();
		this.jrConfig = jrConfig;
		getStore();
	}

	public AResourcePreferenceAction(String text, JasperReportsConfiguration jrConfig, int style) {
		super(text, style);
		this.jrConfig = jrConfig;
		getStore();
	}

	protected ScopedPreferenceStore getStore() {
		return jrConfig.getPrefStore();
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		try {
			IFile file = (IFile) jrConfig.get(FileUtils.KEY_FILE);
			file.setPersistentProperty(new QualifiedName(pageID, FieldEditorOverlayPage.USERESOURCESETTINGS),
					FieldEditorOverlayPage.RESOURCE);
			doRun();

			getStore().save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void doRun() throws Exception;
}
