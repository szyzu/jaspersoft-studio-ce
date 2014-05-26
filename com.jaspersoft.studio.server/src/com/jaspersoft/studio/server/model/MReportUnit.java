/*******************************************************************************
 * Copyright (C) 2010 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, 
 * the following license terms apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jaspersoft Studio Team - initial API and implementation
 ******************************************************************************/
package com.jaspersoft.studio.server.model;

import net.sf.jasperreports.engine.JRConstants;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.server.ServerIconDescriptor;
import com.jaspersoft.studio.server.model.server.MServerProfile;

public class MReportUnit extends AMJrxmlContainer implements IInputControlsContainer {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MReportUnit(ANode parent, ResourceDescriptor rd, int index) {
		super(parent, rd, index);
	}

	public MReportUnit(ANode parent, ResourceDescriptor rd) {
		super(parent, rd, -1);
	}

	private static IIconDescriptor iconDescriptor;

	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new ServerIconDescriptor("reportunit"); //$NON-NLS-1$
		return iconDescriptor;
	}

	@Override
	public IIconDescriptor getThisIconDescriptor() {
		return getIconDescriptor();
	}

	@Override
	public boolean isCopyable2(Object parent) {
		if (parent instanceof MFolder || parent instanceof MServerProfile)
			return true;
		return false;
	}

	public static ResourceDescriptor createDescriptor(ANode parent) {
		ResourceDescriptor rd = MResource.createDescriptor(parent);
		rd.setWsType(ResourceDescriptor.TYPE_REPORTUNIT);
		return rd;
	}

	@Override
	public String getDefaultFileExtension() {
		return "";
	}
}
