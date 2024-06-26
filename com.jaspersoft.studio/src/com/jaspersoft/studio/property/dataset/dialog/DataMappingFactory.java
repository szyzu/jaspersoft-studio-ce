/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.dataset.dialog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import com.jaspersoft.studio.data.IFieldSetter;
import com.jaspersoft.studio.data.IMappingTool;

public class DataMappingFactory {
	private Map<Class<? extends IMappingTool>, IMappingTool> classmap = new HashMap<Class<? extends IMappingTool>, IMappingTool>();

	public DataMappingFactory(CTabFolder tabFolder, IFieldSetter fsetter, DataQueryAdapters dqa) {

		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
				"com.jaspersoft.studio", "mappingTool"); //$NON-NLS-1$ //$NON-NLS-2$
		for (IConfigurationElement e : config) {
			try {
				IMappingTool qd = (IMappingTool) e.createExecutableExtension("MappingToolClass"); //$NON-NLS-1$
				qd.setParentContainer(dqa);
				CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
				bptab.setText(qd.getName());

				addDesigner(qd);

				bptab.setControl(qd.createControl(tabFolder));
				qd.setFields(fsetter);
			} catch (CoreException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public void dispose() {
		for (IMappingTool mt : classmap.values())
			mt.dispose();
	}

	public Collection<IMappingTool> getMappingTools() {
		return classmap.values();
	}

	private IMappingTool addDesigner(IMappingTool qd) {
		IMappingTool iqd = classmap.get(qd.getClass());
		if (iqd == null) {
			iqd = qd;

			classmap.put(qd.getClass(), iqd);
		}
		return iqd;
	}
}
