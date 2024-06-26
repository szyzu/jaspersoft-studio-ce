/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.tools.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.palette.JDPaletteFactory;
import com.jaspersoft.studio.editor.tools.CompositeElementManager;
import com.jaspersoft.studio.editor.tools.MCompositeElement;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.plugin.ExtensionManager;
import com.jaspersoft.studio.plugin.PaletteGroup;
import com.jaspersoft.studio.preferences.util.PreferencesUtils;
import com.jaspersoft.studio.utils.ImageUtils;
import com.jaspersoft.studio.wizards.ContextHelpIDs;
import com.jaspersoft.studio.wizards.JSSHelpWizardPage;

/**
 * Wizard page to provide the metadata of a composite element
 * 
 * @author Orlandin Marco
 *
 */
public class CompositeElementDefinitionWizardPage extends JSSHelpWizardPage {

	protected static final String DEFAULT_PALETTE = "defaultCustomElementsPalette";
	
	/**
	 * The unique name of the new composite element
	 */
	private String name = ""; //$NON-NLS-1$
	
	/**
	 * The description of the new composite element
	 */
	private String elementDescription = ""; //$NON-NLS-1$
	
	/**
	 * The path to the icon of the new composite element
	 */
	private String iconPath = ""; //$NON-NLS-1$
	
	/**
	 * The palette group ID where the composite element will be placed
	 */
	private String groupID = "";
	
	/**
	 * Text area where the name of the composite element is provided
	 */
	protected Text nameText;
	
	/**
	 * Text area where the description of the composite element is provided
	 */
	protected Text descriptionText;
	
	/**
	 * Combo where the user can select the palette where the composite element will be placed
	 */
	protected Combo palettePosition;
	
	/**
	 * Text area where the icon path of the composite element is provided
	 */
	protected Text iconPathText;
	
	/**
	 * The available palette group where this element can be placed
	 */
	protected List<PaletteGroup> groups = getAvailableGroups();
	
	/**
	 * Text area where the currently loaded icon is shown
	 */
	private Label iconPreview;
	
	/**
	 * The last loaded image as icon for the composite element. It is keep a reference
	 * to dispose it when it is no more necessary
	 */
	private Image lastLoadedImage = null;

	/**
	 * Modify listener called when one of the textual control changes, update
	 * the field and eventually if the icon path is changed then it reload the image
	 */
	protected ModifyListener widgetsModfied = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			updateWidgets();
		}
	};
	
	/**
	 * Create the wizard page
	 */
	protected CompositeElementDefinitionWizardPage() {
		super("elementdefinitionwizard"); //$NON-NLS-1$
		setTitle(Messages.ToolDefinitionWizardPage_title);
		setDescription(Messages.ToolDefinitionWizardPage_description);
	}

	/**
	 * Read the values from the widgets and store them in the appropriate
	 * fields.
	 */
	protected void updateWidgets(){
		name = nameText.getText();
		elementDescription = descriptionText.getText();
		
		PaletteGroup selectedGroup = groups.get(palettePosition.getSelectionIndex());
		groupID = selectedGroup.getId();
		PreferencesUtils.getJaspersoftStudioPrefStore().setValue(DEFAULT_PALETTE, groupID);
		
		if (!iconPath.equals(iconPathText.getText())){
			iconPath = iconPathText.getText();
			try{ 
				loadIconSample();
			} catch (Exception ex){
				//if something goes wrong hide the preview area
				iconPreview.setImage(null);
				GridData iconLabelData = new GridData();
				iconLabelData.exclude = true;
				iconPreview.setLayoutData(iconLabelData);
				iconPreview.getParent().layout(true, true);
			}
		}
		getWizard().getContainer().updateButtons();
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible){
			updateWidgets();
		}
	}
	
	/**
	 * Create the area used to handle the icon image
	 * 
	 * @param container the container where control are created, it is a grid layout
	 * with 2 columns
	 */
	protected void createIconArea(Composite container){
			//Create the controls for the image
			Composite imageContainer = new Composite(container, SWT.NONE);
			imageContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout imageContainerLayout = new GridLayout(3, false);
			imageContainerLayout.marginHeight = 0;
			imageContainerLayout.marginWidth = 0;
			imageContainer.setLayout(imageContainerLayout);
			iconPathText = new Text(imageContainer, SWT.BORDER);
			iconPathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			iconPreview = new Label(imageContainer, SWT.NONE);
			GridData previewData = new GridData();
			previewData.exclude = true;
			iconPreview.setLayoutData(previewData);
			
			Button selectImageButton = new Button(imageContainer, SWT.PUSH);
			selectImageButton.setText(Messages.common_browse);
			selectImageButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
				   	FileDialog fd = new FileDialog(UIUtils.getShell(), SWT.OPEN);
		        fd.setText(Messages.common_open);
		        fd.setFilterExtensions(getFileExtensions());
		        fd.setFilterNames(getFileExtensionsNames());
		        String selected = fd.open();
		        if (selected != null){
		        	iconPathText.setText(selected);
		        }
				}
			});
	}
	
	/**
	 * Return the names of the image extensions
	 * 
	 * @return a not null array of extension human readable names
	 */
	protected String[] getFileExtensionsNames() {
		return new String[] {"All Images", "PNG", "JPEG", "GIF", "SVG", "ICO"};
	}

	/**
	 * Return the extensions of the images
	 * 
	 * @return a not null array of extensions
	 */
	protected String[] getFileExtensions() {
		return new String[] {"*.png;*.jpeg;*.jpg;*.gif;*.svg;*.ico", "*.png", "*.jpeg; *.jpg", "*.gif", ".svg", "*.ico" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	/**
	 * Get the group of the palette where this element can be place
	 * 
	 * @return a not null and not empty list of available palette group
	 */
	protected List<PaletteGroup> getAvailableGroups(){
		List<PaletteGroup> result = new ArrayList<PaletteGroup>();
		result.add(JDPaletteFactory.getBaseElementsGroup());
		result.add(JDPaletteFactory.getOtherElementsGroup());
		ExtensionManager m = JaspersoftStudioPlugin.getExtensionManager();
		result.addAll(m.getPaletteGroups());
		return result;
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		
		//Create the name and description text area
		new Label(container, SWT.NONE).setText(Messages.ToolDefinitionWizardPage_titleLabel);
		nameText = new Text(container, SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameText.setText(getFirstAvailableName());
		
		new Label(container, SWT.NONE).setText(Messages.ToolDefinitionWizardPage_descriptionLabel);
		descriptionText = new Text(container, SWT.BORDER);
		descriptionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(container, SWT.NONE).setText(Messages.ToolDefinitionWizardPage_iconLabel);
		
		createIconArea(container);
		
		//create the palette area
		new Label(container, SWT.NONE).setText("Position in Palette");
		palettePosition = new Combo(container, SWT.READ_ONLY);
		palettePosition.setData(groups);
		List<String> entries = new ArrayList<String>();
		for(PaletteGroup group : groups){
			entries.add(group.getName());
		}
		palettePosition.setItems(entries.toArray(new String[entries.size()]));
		new File("fdsdfswd").getAbsolutePath();
		//Search the default selection for the palette area
		String defaultPalette = PreferencesUtils.getJaspersoftStudioPrefStore().getString(DEFAULT_PALETTE);
		int initialIndex = 1;
		if (defaultPalette != null){
			for(int i = 0; i<groups.size(); i++){
				PaletteGroup group = groups.get(i);
				if (group.getId().equals(defaultPalette)){
					initialIndex = i;
					break;
				}
			}
		}
		
		groupID = groups.get(initialIndex).getId();
		palettePosition.select(initialIndex);
		
		setControl(container);
		addListeners();
	}
	
	/**
	 * Add the modify and the selection listeners to the controls
	 */
	protected void addListeners(){
		//Add the listeners
		nameText.addModifyListener(widgetsModfied);
		descriptionText.addModifyListener(widgetsModfied);
		iconPathText.addModifyListener(widgetsModfied);
		palettePosition.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateWidgets();
			}
		});
	}
	
	/**
	 * Check if the page is complete, the page is completed 
	 * if name, description and icon are not empty and if the name is 
	 * unique
	 */
	@Override
	public boolean isPageComplete() {
		if (name.trim().isEmpty()){
			setErrorMessage(Messages.ToolDefinitionWizardPage_errorNameEmpry);	
		} else if (!FileUtils.isValidFilename(name)){
			setErrorMessage(Messages.ToolDefinitionWizardPage_invalidFileName);
		} else if (CompositeElementManager.INSTANCE.isNameAlreadyUsed(name.trim())){
			setErrorMessage(Messages.ToolDefinitionWizardPage_errorNameUsed);
		} else {
			setErrorMessage(null);
			setDescription(Messages.ToolDefinitionWizardPage_description);
		}
		return getErrorMessage() == null;
	}
	
	/**
	 * Search the first available name for a composite element
	 * 
	 * @return a not null and unique name for a composite element
	 */
	private String getFirstAvailableName(){
		String baseName= Messages.ToolDefinitionWizardPage_defaultName;
		String name = baseName;
		int counter = 1;
		while(CompositeElementManager.INSTANCE.isNameAlreadyUsed(name)){
			name = baseName + " " + counter;  //$NON-NLS-1$
			counter ++;
		}
		return name;
	}
	
	/**
	 * Load the selected file as icon and show it the appropriate area
	 * 
	 * @throws FileNotFoundException throw if the icon file can not be found
	 */
	private void loadIconSample() throws FileNotFoundException{
		FileInputStream stream = null;
		//dispose first the last loaded image if available
		if (lastLoadedImage != null) lastLoadedImage.dispose();
		try{
			stream = new FileInputStream(new File(iconPath));
			Image loadedImage = new Image(null, stream);
			ImageData resized = ImageUtils.resizeKeepingRatio(16, loadedImage);
			loadedImage.dispose();
			FileUtils.closeStream(stream);
			lastLoadedImage = new Image(null, resized);
			iconPreview.setImage(lastLoadedImage);
			
			GridData iconLabelData = new GridData();
			iconLabelData.widthHint = resized.width;
			iconPreview.setLayoutData(iconLabelData);
	
			iconPreview.getParent().layout(true, true);
		} catch(FileNotFoundException ex){
			FileUtils.closeStream(stream);
			throw ex;
		}
	}
	
	/**
	 * When disposed it dispose also the last loaded image
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (lastLoadedImage != null){
			lastLoadedImage.dispose();
			lastLoadedImage = null;
		}
	}

	@Override
	protected String getContextName() {
		return ContextHelpIDs.WIZARD_COMPOSITE_ELEMENTS;
	}

	/**
	 * Return the name of the composite element
	 * 
	 * @return a not null and not empty string
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the description of the composite element
	 * 
	 * @return a not null and not empty string
	 */
	public String getElementDescription() {
		return elementDescription;
	}

	/**
	 * Return the absolute icon path of the composite element
	 * 
	 * @return a not null and valid absolute path
	 */
	public String getIconPath() {
		return iconPath;
	}

	/**
	 * Return the id of the palette group where the tool should be placed
	 * 
	 * @return a not null string
	 */
	public String getGroupID(){
		return groupID;
	}
	
	/**
	 * Search in the  composite element model an image path, first search for the big image,
	 * if it can`t be found the look for the small. If one of these is found the path is returned
	 * otherwise it return null
	 * 
	 * @param element the element, must be not null
	 * @return the path to the icon of the element if any of them can be found, otherwise null
	 */
	public static String getFirstAvailableImagePath(MCompositeElement element){
		String imagePath = element.getIconPathBig();
		if (imagePath == null) {
			imagePath = element.getIconPathSmall();
		}
		return imagePath;
	}
}
