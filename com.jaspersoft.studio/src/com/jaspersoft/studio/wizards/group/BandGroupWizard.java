/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.wizards.group;

import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.jface.wizard.Wizard;

import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.IExpressionContextSetter;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.group.MGroup;

public class BandGroupWizard extends Wizard implements IExpressionContextSetter {
	private MGroup group;
	private ExpressionContext expContext;

	public boolean isAddHeader() {
		return step2.isAddHeader();
	}

	public boolean isAddFooter() {
		return step2.isAddFooter();
	}

	private WizardBandGroupPage step1;
	private WizardBandGroupLayoutPage step2;

	public BandGroupWizard() {
		super();
		setWindowTitle(Messages.BandGroupWizard_group_band);
		setNeedsProgressMonitor(false);
	}

	@Override
	public void addPages() {
		this.group = new MGroup();
		group.setValue(MGroup.createJRGroup(jasperDesign.getMainDesignDataset()));

		step1 = new WizardBandGroupPage(jasperDesign);
		addPage(step1);
		step1.setGroup(group);
		if (expContext != null) {
			step1.setExpressionContext(expContext);
		}

		step2 = new WizardBandGroupLayoutPage();
		addPage(step2);
	}

	public MGroup getGroup() {
		return group;
	}

	@Override
	public boolean performFinish() {
		JRDesignGroup gr = (JRDesignGroup) group.getValue();
		if (jasperDesign.getMainDesignDataset().getGroupsMap().get(gr.getName()) != null)
			return false;
		return true;
	}

	private JasperDesign jasperDesign;

	public void init(JasperDesign jd) {
		this.jasperDesign = jd;
	}

	public void setExpressionContext(ExpressionContext expContext) {
		this.expContext = expContext;
		if (step1 != null) {
			step1.setExpressionContext(expContext);
		}
	}

}
