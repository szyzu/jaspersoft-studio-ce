/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved. http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.jasper;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.components.ComponentsManager;
import net.sf.jasperreports.engine.component.ComponentManager;
import net.sf.jasperreports.engine.component.ComponentsBundle;
import net.sf.jasperreports.engine.component.DefaultComponentsBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class ComponentConverterManager {
	public void init() {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
				JaspersoftStudioPlugin.PLUGIN_ID, "componentConverter"); //$NON-NLS-1$  
		for (IConfigurationElement e : config) {
			try {
				Object o = e.createExecutableExtension("ClassFactory"); //$NON-NLS-1$
				if (o instanceof AComponentDesignConverter)
					nodeFactory.add((AComponentDesignConverter) o);
			} catch (CoreException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	private List<AComponentDesignConverter> nodeFactory = new ArrayList<AComponentDesignConverter>();

	public void setupComponentConvertor(ComponentsBundle cb) {
		if (cb instanceof DefaultComponentsBundle) {
			JasperReportsConfiguration jrConfig = JasperReportsConfiguration.getDefaultInstance();
			for (AComponentDesignConverter f : nodeFactory) {
				try {
					ComponentManager cm = cb.getComponentManager(f.getComponentName());
					if (cm != null && cm instanceof ComponentsManager) {
						ComponentsManager newcb = new ComponentsManager();
						newcb.setDesignConverter(f);
						newcb.setComponentCompiler(cm.getComponentCompiler(jrConfig));
						newcb.setComponentFillFactory(cm.getComponentFillFactory(jrConfig));
						((DefaultComponentsBundle) cb).getComponentManagers().put(f.getComponentName(), newcb);
					}
				} catch (Exception e) {
				}
			}
		}
	}
}
