/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.jface.dialogs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.jface.IFileSelection;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.swt.widgets.WTextExpression;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.ui.util.PersistentLocationDialog;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.eclipse.util.StringUtils;
import net.sf.jasperreports.engine.design.JRDesignExpression;

/**
 * Dialog proposed when an image needs to be selected.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public class FileSelectionDialog extends PersistentLocationDialog implements ISupportedTypes{
	
	public static final String ALL_FILES_TYPE = "*.*"; //$NON-NLS-1$

	// Expression that will be associated to the image element
	protected String fileExpressionText;
	// All widgets stuff
	protected Text txtResourcePath;
	protected Text txtFilesystemPath;
	protected Text txtURL;
	protected Button btnWorkspaceResource;
	protected Button btnAbsolutePath;
	protected Button btnNoFile;
	protected Button btnUrlRemote;
	protected Button btnCustomExpression;
	protected StackLayout grpOptionsLayout;
	protected Composite cmpWorkspaceResourceSelection;
	protected Composite cmpFilesystemResourceSelection;
	protected Composite cmpNoFile;
	protected Composite cmpCustomExpression;
	protected Composite cmpURL;
	protected Group grpOptions;
	protected WTextExpression customExpression;
	protected JRDesignExpression jrFileExpression;
	protected JasperReportsConfiguration jConfig;
	protected boolean allowValidation = true;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public FileSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(getDialogTitle());
	}

	/**
	 * @return the title for the dialog
	 */
	protected String getDialogTitle() {
		return Messages.FileSelectionDialog_0;
	}

	/**
	 * Returns an array of strings containing the title for the modes section, plus
	 * the title of every mode.
	 * <p>
	 * 
	 * Default implementation would return 6 strings, including 1 title and the
	 * following 5 modes:
	 * <ol>
	 * <li>workspace resource;</li>
	 * <li>absolute path in filesystem;</li>
	 * <li>URL;</li>
	 * <li>no image;</li>
	 * <li>custom expression</li>
	 * </ol>
	 * 
	 * @return the title and labels for the group of modes
	 */
	protected String[] getImageModesAndHeaderTitles() {
		return new String[] { Messages.FileSelectionDialog_1, Messages.FileSelectionDialog_2,
				Messages.FileSelectionDialog_3, Messages.FileSelectionDialog_4, Messages.FileSelectionDialog_5,
				Messages.FileSelectionDialog_6 };
	}

	private List<IFileSelection> fselectors;
	protected Composite container;

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		String[] fileModesAndHeaderTitles = getImageModesAndHeaderTitles();

		Group grpFileSelectionMode = new Group(container, SWT.NONE);
		grpFileSelectionMode.setText(fileModesAndHeaderTitles[0]);
		grpFileSelectionMode.setLayout(new GridLayout(1, false));
		grpFileSelectionMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		btnWorkspaceResource = new Button(grpFileSelectionMode, SWT.RADIO);
		btnWorkspaceResource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnWorkspaceResource.getSelection()) {
					changeFileSelectionMode(cmpWorkspaceResourceSelection);
					allowValidation = true;
				}
			}
		});
		btnWorkspaceResource.setText(fileModesAndHeaderTitles[1]);

		btnAbsolutePath = new Button(grpFileSelectionMode, SWT.RADIO);
		btnAbsolutePath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnAbsolutePath.getSelection()) {
					changeFileSelectionMode(cmpFilesystemResourceSelection);
					allowValidation = true;
				}
			}
		});
		btnAbsolutePath.setText(fileModesAndHeaderTitles[2]);

		btnUrlRemote = new Button(grpFileSelectionMode, SWT.RADIO);
		btnUrlRemote.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnUrlRemote.getSelection()) {
					changeFileSelectionMode(cmpURL);
					allowValidation = true;
				}
			}
		});
		btnUrlRemote.setText(fileModesAndHeaderTitles[3]);

		fselectors = JaspersoftStudioPlugin.getExtensionManager().getFileSelectors();
		for (IFileSelection fs : fselectors) {
			fs.createRadioButton(grpFileSelectionMode, this, jConfig.getJasperDesign());
		}

		btnCustomExpression = new Button(grpFileSelectionMode, SWT.RADIO);
		btnCustomExpression.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnCustomExpression.getSelection()) {
					changeFileSelectionMode(cmpCustomExpression);
					allowValidation = false;
				}
			}
		});
		btnCustomExpression.setText(fileModesAndHeaderTitles[5]);

		if (allowNoFileOption()) {
			btnNoFile = new Button(grpFileSelectionMode, SWT.RADIO);
			btnNoFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(btnNoFile.getSelection()) {
						changeFileSelectionMode(cmpNoFile);
						allowValidation = false;
					}
				}
			});
			btnNoFile.setText(fileModesAndHeaderTitles[4]);

			createOptionsPanel(container);

			// As default no image radio button selected
			btnNoFile.setSelection(true);
			btnNoFile.setFocus();
			allowValidation = false;

			changeFileSelectionMode(cmpNoFile);
		} else {
			createOptionsPanel(container);
			// As default no image radio button selected
			btnWorkspaceResource.setSelection(true);
			allowValidation = true;
			btnWorkspaceResource.setFocus();

			changeFileSelectionMode(cmpWorkspaceResourceSelection);
		}

		return area;
	}

	/**
	 * Return if the selected path type allow a validation of the resource
	 * 
	 * @return true if it allow a validation of the resource, false of otherwise
	 */
	public boolean isValidationAllowed() {
		return allowValidation;
	}

	/**
	 * Set if the selected path type allow a validation of the resource
	 * 
	 * @param value
	 *            true if it allow a validation of the resource, false of otherwise
	 */
	public void setAllowValidation(boolean value) {
		allowValidation = value;
	}

	/*
	 * Creates the panel with the different options container. A stack layout will
	 * be used.
	 */
	private void createOptionsPanel(Composite container) {
		grpOptions = new Group(container, SWT.NONE);
		grpOptions.setText(Messages.ImageSelectionDialog_OptionsGroupTitle);
		grpOptionsLayout = new StackLayout();
		grpOptions.setLayout(grpOptionsLayout);
		grpOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		createWSSelectionContainer();
		createFSSelectionContainer();
		createNoFileContainer();
		createCustomExprContainer();
		createURLOptionsContainer();
		for (IFileSelection fs : fselectors)
			fs.createFileSelectionContainer(grpOptions);
	}

	/*
	 * Creates the composite container for the workspace image selection.
	 */
	private void createWSSelectionContainer() {
		cmpWorkspaceResourceSelection = new Composite(grpOptions, SWT.NONE);
		cmpWorkspaceResourceSelection.setLayout(new GridLayout(2, false));

		Label lblSelectFileFromWS = new Label(cmpWorkspaceResourceSelection, SWT.NONE);
		lblSelectFileFromWS.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblSelectFileFromWS.setText(Messages.ImageSelectionDialog_SelectImgFromWS);

		txtResourcePath = new Text(cmpWorkspaceResourceSelection, SWT.BORDER);
		txtResourcePath.setEnabled(false);
		txtResourcePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnSelectWsRes = new Button(cmpWorkspaceResourceSelection, SWT.NONE);
		btnSelectWsRes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectFileFromWorkspace();
			}
		});
		btnSelectWsRes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSelectWsRes.setText(Messages.ImageSelectionDialog_Browse);
	}

	/*
	 * Creates the composite container for the filesystem image selection.
	 */
	private void createFSSelectionContainer() {
		cmpFilesystemResourceSelection = new Composite(grpOptions, SWT.NONE);
		cmpFilesystemResourceSelection.setLayout(new GridLayout(2, false));

		Label lblSelectFileFromFilesystem = new Label(cmpFilesystemResourceSelection, SWT.NONE);
		lblSelectFileFromFilesystem.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblSelectFileFromFilesystem.setText(Messages.ImageSelectionDialog_SelectImgFromFS);

		txtFilesystemPath = new Text(cmpFilesystemResourceSelection, SWT.BORDER);
		txtFilesystemPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtFilesystemPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				handleTxtFilesystemPathChange();
			}
		});

		Button btnSelectFilesystemRes = new Button(cmpFilesystemResourceSelection, SWT.NONE);
		btnSelectFilesystemRes.setText(Messages.ImageSelectionDialog_Browse);
		btnSelectFilesystemRes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectFileFromFilesystem();
			}
		});
	}

	public void handleTxtFilesystemPathChange() {
		fileExpressionText = getRootRelativePath(txtFilesystemPath.getText())
				.replace(System.getProperty("file.separator").charAt(0), '/'); //$NON-NLS-1$
	}

	/*
	 * Creates the empty composite for no image selection.
	 */
	private void createNoFileContainer() {
		cmpNoFile = new Composite(grpOptions, SWT.NONE);
	}

	/*
	 * Creates the composite container for the custom expression editing.
	 */
	private void createCustomExprContainer() {
		cmpCustomExpression = new Composite(grpOptions, SWT.NONE);
		GridLayout cmpCustomExpressionlayout = new GridLayout();
		cmpCustomExpression.setLayout(cmpCustomExpressionlayout);

		customExpression = new WTextExpression(cmpCustomExpression, SWT.NONE,
				Messages.ImageSelectionDialog_EnterExpression, WTextExpression.LABEL_ON_TOP) {
			@Override
			public void setExpression(JRDesignExpression exp) {
				super.setExpression(exp);
				// Keep in synch the expression modification in the widget
				// with the final image expression.
				jrFileExpression = exp;
			}
		};
		customExpression.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	/*
	 * Create the composite container for the URL image selection.
	 */
	private void createURLOptionsContainer() {
		cmpURL = new Composite(grpOptions, SWT.NONE);
		cmpURL.setLayout(new GridLayout(1, false));

		Label lblNewLabel = new Label(cmpURL, SWT.NONE);
		lblNewLabel.setText(Messages.ImageSelectionDialog_EnterURL);

		txtURL = new Text(cmpURL, SWT.BORDER);
		txtURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtURL.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				handleTxtUrlChange();
			}
		});
	}

	public void handleTxtUrlChange() {
		String imageURLText = txtURL.getText();
		fileExpressionText = imageURLText;
	}

	/**
	 * Show the custom expression area with an expression preinitialized inside it
	 * 
	 * @param expression
	 *            the expression to show, must be not null
	 */
	protected void showCustomExpression(String expression) {
		changeFileSelectionMode(cmpCustomExpression);
		btnWorkspaceResource.setSelection(false);
		btnAbsolutePath.setSelection(false);
		btnNoFile.setSelection(false);
		btnUrlRemote.setSelection(false);
		btnCustomExpression.setSelection(true);
		customExpression.setExpression(new JRDesignExpression(expression));
	}

	/*
	 * When a new image selection mode is selected, shows the dedicated options
	 * panel and hide the image preview one.
	 */
	public void changeFileSelectionMode(Control newTopControl) {
		// Resets previous info on the image expression
		fileExpressionText = null;
		// Resets widgets
		txtResourcePath.setText(""); //$NON-NLS-1$
		txtFilesystemPath.setText(""); //$NON-NLS-1$
		txtURL.setText(""); //$NON-NLS-1$
		customExpression.setExpression(null);

		// Change the top control for the options panel
		grpOptionsLayout.topControl = newTopControl;
		grpOptions.layout();

		for (IFileSelection fs : fselectors)
			fs.changeSelectionMode(newTopControl);
	}

	/*
	 * Popup the dialog to select the image from workspace.
	 */
	protected IFile selectFileFromWorkspace() {
		IFile file = null;
		FilteredResourcesSelectionDialog fd = new FilteredResourcesSelectionDialog(
				UIUtils.getShell(), false, ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
		fd.setInitialPattern(getDefaultResourcesPattern());// $NON-NLS-1$
		boolean isok = fd.open() == Dialog.OK;
		if (isok) {
			file = (IFile) fd.getFirstResult();
			String filepath = getRootRelativePath(file.getLocation().toOSString());
			txtResourcePath.setText(filepath);

			// Change the standard separator with an universal one
			fileExpressionText = StringUtils.replaceBackslashWithDoubleBackslash(filepath); // $NON-NLS-1$
		} else {
			// no image selected
			txtResourcePath.setText(""); //$NON-NLS-1$
		}
		return file;
	}

	private String getRootRelativePath(String file) {
		if (!Misc.isNullOrEmpty(file)) {
			Path cpath = Paths.get(file);
			IFile f = (IFile) jConfig.get(FileUtils.KEY_FILE);
			Path fpath = Paths.get(f.getLocation().toOSString());
			if (fpath.getParent().equals(cpath.getParent()))
				return cpath.getFileName().toString();
			for (String p : jConfig.getEditorContext().getRepositoryRoots()) {
				Path rpath = Paths.get(p);
				if (cpath.startsWith(rpath)) {
					String path = rpath.relativize(cpath).toString();
					path = FilenameUtils.separatorsToUnix(path);
					if (!path.startsWith("/")) //$NON-NLS-1$
						path = "/" + path; //$NON-NLS-1$
					return path;
				}
			}
		}
		return file;
	}

	/**
	 * Returns the default pattern that will be used for resources selection 
	 * in some kind of situations (i.e workspace items selection).
	 * 
	 * @return default pattern
	 */
	protected String getDefaultResourcesPattern() {
		return ALL_FILES_TYPE; //$NON-NLS-1$
	}
	
	
	@Override
	public String[] getSupportedTypes() {
		return new String[] { ALL_FILES_TYPE };
	}
	
	@Override
	public String getSupportedTypeName(String type) {
		return ALL_FILES_TYPE.equals(type) ? Messages.FileSelectionDialog_AllFilesText : type;
	}
	
	private String[] getFileExtensionsNames() {
		String[] types = getSupportedTypes();
		String[] result = new String[types.length];
		for(int i=0;i<types.length;i++) {
			result[i] = getSupportedTypeName(types[i]);
		}
		return result;
	}
	
	/*
	 * Popup the dialog to select the image from the filesystem.
	 */
	private void selectFileFromFilesystem() {
		// Creating temporary shell to address JIRA #JSS-2712 and Eclipse Bug#558015
		Shell tmpShell = new Shell(UIUtils.getDisplay());
		FileDialog fd = new FileDialog(tmpShell);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		fd.setFilterPath(root.getLocation().toOSString());
		String[] extensions = getSupportedTypes();
		String[] extensionNames = getFileExtensionsNames();
		fd.setFilterExtensions(extensions);
		if (extensions.length == extensionNames.length) {
			fd.setFilterNames(extensionNames);
		}
		String selection = fd.open();
		if (selection != null) {
			// After the text modification the image preview job will be invoked...
			txtFilesystemPath.setText(selection);
			handleTxtFilesystemPathChange();
		}
		tmpShell.dispose();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Configure required information for the correct dialog functioning.
	 * 
	 * @param jConfig
	 */
	public void configureDialog(JasperReportsConfiguration jConfig) {
		this.jConfig = jConfig;
	}

	@Override
	public int open() {
		if (jConfig == null) {
			throw new RuntimeException(Messages.ImageSelectionDialog_Error);
		}
		return super.open();
	}

	@Override
	protected void okPressed() {
		// Updates the expression that will be associated to the image element.
		// Covers all cases except the custom expression one because
		// it is already kept in synch.
		if (!btnCustomExpression.getSelection()) {
			if (fileExpressionText != null) {
				jrFileExpression = new JRDesignExpression();
				jrFileExpression.setText("\"" + fileExpressionText + "\"");//$NON-NLS-1$ //$NON-NLS-2$
			} else {
				jrFileExpression = null;
			}
		}

		super.okPressed();
	}

	public void setFileExpressionText(String imageExpressionText) {
		this.fileExpressionText = imageExpressionText;
	}

	public JRDesignExpression getFileExpression() {
		return jrFileExpression;
	}

	/**
	 * Override this to change if the no file option should be shown or not
	 * 
	 * @return true if the no file option should be shown, false otherwise
	 */
	protected boolean allowNoFileOption() {
		return true;
	}

}
