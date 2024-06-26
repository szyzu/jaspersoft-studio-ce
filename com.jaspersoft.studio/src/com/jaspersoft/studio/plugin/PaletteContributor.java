/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef.palette.PaletteEntry;

import com.jaspersoft.studio.editor.palette.JDPaletteFactory;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.util.IIconDescriptor;

public class PaletteContributor implements IPaletteContributor {
	private Map<String, List<PaletteEntry>> map = new TreeMap<String, List<PaletteEntry>>();

	public Map<String, List<PaletteEntry>> getPaletteEntries() {
		return map;
	}

	public void add(String id, Class<? extends ANode> value) {
		List<PaletteEntry> lst = map.get(id);
		if (lst == null) {
			lst = new ArrayList<PaletteEntry>();
			map.put(id, lst);
		}
		try {
			IIconDescriptor idesc = (IIconDescriptor) value.getDeclaredMethod("getIconDescriptor", new Class[0]).invoke( //$NON-NLS-1$
					value, new Object[0]);
			lst.add(JDPaletteFactory.createJDEntry(idesc, value));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	public void add(Class<? extends ANode> value) {
		add("", value);
	}

}
