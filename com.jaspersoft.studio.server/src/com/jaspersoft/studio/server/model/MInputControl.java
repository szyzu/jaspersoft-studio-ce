/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sf.jasperreports.engine.JRConstants;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.server.ServerIconDescriptor;

public class MInputControl extends AMResource {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MInputControl(ANode parent, ResourceDescriptor rd, int index) {
		super(parent, rd, index);
	}

	private static IIconDescriptor iconDescriptor;

	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new ServerIconDescriptor("inputcontrol"); //$NON-NLS-1$
		return iconDescriptor;
	}

	@Override
	public IIconDescriptor getThisIconDescriptor() {
		return getIconDescriptor();
	}

	public static ResourceDescriptor createDescriptor(ANode parent) {
		ResourceDescriptor rd = AMResource.createDescriptor(parent);
		rd.setWsType(ResourceDescriptor.TYPE_INPUT_CONTROL);
		rd.setControlType(ResourceDescriptor.IC_TYPE_BOOLEAN);
		return rd;
	}

	@Override
	public String getJRSUrl() throws UnsupportedEncodingException {
		return "flow.html?_flowId=addInputControlFlow&isEdit=true&resource="
				+ URLEncoder.encode(getValue().getUriString(), "ISO-8859-1")
				+ "&ParentFolderUri="
				+ URLEncoder.encode(getValue().getParentFolder(), "ISO-8859-1");
	}
}
