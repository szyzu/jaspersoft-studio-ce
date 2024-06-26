/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.translation.wizard;

import java.io.File;
import java.util.List;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.translation.ExtendedTranslationInformation;
import com.jaspersoft.studio.translation.FragmentCreationUtil;
import com.jaspersoft.studio.translation.ImageLocale;
import com.jaspersoft.studio.wizards.CongratulationsWizardPage;
import com.jaspersoft.studio.wizards.JSSWizard;

/**
 *
 * This wizard provide the steps to export a Translation project like fragments
 * that can be used to add a translation to jaspersoft studio
 *
 * @author Orlandin Marco
 *
 */
public class GenerateFragmentWizard extends JSSWizard {

	/**
	 * In this step the user can select which plugins among 
	 * the ones in the translation project should be exported
	 */
	private ExportedResourcesWizardPage step1;
	
	
	private LocalesTranslationWizardPage step2;
	
	/**
	 * Congratulations final page
	 */
	private CongratulationsWizardPage step3;
	
	/**
	 * List of the resources inside the translation project
	 */
	private List<ExtendedTranslationInformation> sourceList;
	
	/**
	 * Create an instance of the class
	 * 
	 * @param sourceList List of the resources inside the translation project that the user want to export
	 */
	public GenerateFragmentWizard(List<ExtendedTranslationInformation> sourceList){
		super();
		this.sourceList = sourceList;
	}
	
	/**
	 * Can finish only if the user has reached the last step
	 */
	@Override
	public boolean canFinish() {
		 List<ExtendedTranslationInformation> selectedResources = step1.getSelectedResources();
		 if (selectedResources.isEmpty()) return false;
		 String destinationPath = step2.getDestinationPath();
		 if (destinationPath.isEmpty() || !(new File(destinationPath).exists())) return false;
		 List<ImageLocale> localesList = step2.getSelectedLanguages();
		 if (localesList.isEmpty()) return false;
		 return true;
	}
	
	@Override
	public void addPages() {
		step1 = new ExportedResourcesWizardPage(sourceList);
		addPage(step1);

		step2 = new LocalesTranslationWizardPage();
		addPage(step2);
		
		step3 = new CongratulationsWizardPage(Messages.GenerateFragmentWizard_congratsText1, Messages.GenerateFragmentWizard_congratsText2, Messages.GenerateFragmentWizard_congratsText3, Messages.GenerateFragmentWizard_congratsText4, Messages.GenerateFragmentWizard_congratsText5);
		addPage(step3);
	}
	
	public List<ExtendedTranslationInformation> getSelectedResources(){
		return step1.getSelectedResources();
	}
	
	@Override
	public boolean performFinish() {
		 List<ExtendedTranslationInformation> selectedResources = step1.getSelectedResources();
		 String destinationPath = step2.getDestinationPath();
		 List<ImageLocale> localesList = step2.getSelectedLanguages();
		 FragmentCreationUtil.createFragment(destinationPath, selectedResources, localesList);
		 return super.performFinish();
	}

}
