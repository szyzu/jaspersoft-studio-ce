/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;

import com.jaspersoft.studio.editor.AEditPartFactory;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MRoot;
import com.jaspersoft.studio.model.style.MStyle;
import com.jaspersoft.studio.model.style.MStyleTemplateReference;
import com.jaspersoft.studio.model.style.MStylesTemplate;
import com.jaspersoft.studio.model.util.ModelVisitor;

/*
 * A factory for creating JasperDesignEditPart objects.
 * 
 * @author Chicu Veaceslav
 */
public class StyleEditPartFactory extends AEditPartFactory {

	@Override
	protected EditPart createEditPart(Object model) {
		EditPart editPart = null;
		if (model instanceof MRoot || model instanceof MStylesTemplate)
			editPart = new StylesTemplateEditPart() {
				protected List<Object> getModelChildren() {
					final List<Object> list = new ArrayList<Object>();
					new ModelVisitor<INode>((INode) getModel()) {

						@Override
						public boolean visit(INode n) {
							if (n instanceof MStyle && n.getValue() != null)
								list.add(n);
							else if (n instanceof MStyleTemplateReference)
								return false;
							return true;
						}
					};
					return list;
				}
			};
		else if (model instanceof MStyle)
			editPart = new StyleEditPart();

		return editPart;
	}
}
