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
package com.jaspersoft.studio.components.map.model.marker;

import java.util.List;

import net.sf.jasperreports.components.map.Marker;

import com.jaspersoft.studio.model.ANode;

public class MarkersDTO {
	private List<Marker> marker;
	private ANode pnode;

	public MarkersDTO(List<Marker> propExpressions, ANode pnode) {
		super();
		this.marker = propExpressions;
		this.pnode = pnode;
	}

	public List<Marker> getMarkers() {
		return marker;
	}

	public void setMarkers(List<Marker> propExpressions) {
		this.marker = propExpressions;
	}

	public ANode getPnode() {
		return pnode;
	}

	public void setPnode(ANode pnode) {
		this.pnode = pnode;
	}

}
