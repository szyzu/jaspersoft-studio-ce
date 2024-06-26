/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview;

import java.util.Map;

import com.jaspersoft.studio.editor.preview.actions.RunStopAction;

/**
 * This interface should be implemented by whose clients who want to contribute
 * to the extension-point <code>com.jaspersoft.studio.previewModeInfo</code>.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public interface PreviewModeDetails {

	/** Extension point id information */
	String EXTENSION_POINT_ID = "previewModeInfo";

	/** Constant for Local Preview Mode */
	String PREVIEW_MODE_LOCAL = RunStopAction.MODERUN_LOCAL;


	/**
	 * @return the id of the preview mode to which this details apply to
	 */
	String getPreviewModeID();

	/**
	 * @return a map of properties that should be set in the specified preview mode
	 *         ({@link #getPreviewModeID()}).
	 */
	Map<String, String> getPreviewModeProperties();

}
