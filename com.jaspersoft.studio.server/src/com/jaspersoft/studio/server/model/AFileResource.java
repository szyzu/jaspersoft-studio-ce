/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.model;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRConstants;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.model.ANode;

public abstract class AFileResource extends AMResource {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public AFileResource(ANode parent, ResourceDescriptor rd, int index) {
		super(parent, rd, index);
	}

	public abstract String getDefaultFileExtension();

	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		if (file != null)
			try {
				getValue().setFile(file);
				getValue().setData(Base64.encodeBase64(net.sf.jasperreports.eclipse.util.FileUtils.getBytes(file)));
				getValue().setHasData(true);
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		getValue().setData(null);
		getValue().setHasData(false);
	}

	public String getHFFileSize() {
		if (file != null && file.exists())
			return FileUtils.byteCountToDisplaySize(file.length());
		return "";
	}

	public String getFileName() {
		if (file != null)
			try {
				return file.getCanonicalPath();
			} catch (IOException e) {
				UIUtils.showError(e);
			}
		return "";
	}

	@Override
	public String getJRSUrl() throws UnsupportedEncodingException {
		return "flow.html?_flowId=addFileResourceFlow&selectedResource="
				+ URLEncoder.encode(getValue().getUriString(), "ISO-8859-1") + "&ParentFolderUri="
				+ URLEncoder.encode(getValue().getParentFolder(), "ISO-8859-1");
	}
}
