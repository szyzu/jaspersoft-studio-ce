/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.wizards.datasource;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.jaspersoft.studio.data.AWizardDataEditorComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterManager;
import com.jaspersoft.studio.data.IWizardDataEditorProvider;
import com.jaspersoft.studio.data.actions.CreateDataAdapterAction;
import com.jaspersoft.studio.data.empty.EmptyDataAdapterDescriptor;
import com.jaspersoft.studio.data.storage.ADataAdapterStorage;
import com.jaspersoft.studio.data.ui.SimpleQueryWizardDataEditorComposite;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.datasource.MDatasources;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.utils.jobs.ProgressMonitorCheckerThread;
import com.jaspersoft.studio.wizards.ContextHelpIDs;
import com.jaspersoft.studio.wizards.JSSWizard;
import com.jaspersoft.studio.wizards.JSSWizardRunnablePage;
import com.jaspersoft.studio.wizards.fields.StaticWizardFieldsPage;
import com.jaspersoft.studio.wizards.group.ReportWizardFieldsGroupByDynamicPage;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;

/**
 * 
 * This data source wizard page allows to select a data adapter and, if
 * supported by the specific data adapter, edit a query to get the fields. The
 * fields are retrieved with the support of the
 * {@link com.jaspersoft.studio.wizards.JSSWizardRunnablePage
 * JSSWizardRunnablePage} abstract class.</br>
 * </br>
 * The result of the elaboration must be checked by the wizard, which will be in
 * charge to decide or not to display other steps, like Fields selection and
 * Group By fields selection.</br>
 * </br>
 * After the elaboration, this page puts inside the wizard settings map an List
 * of JRDesignField with the key WizardDataSourcePage.DISCOVERED_FIELDS.</br>
 * Any interested step can use this List.</br>
 * The WizardFieldsPage will use this key to populate the list of available
 * fields.</br>
 * 
 * @see StaticWizardFieldsPage
 * @see ReportWizardFieldsGroupByDynamicPage
 * 
 * 
 * 
 * @author gtoffoli
 * 
 */
public class StaticWizardDataSourcePage extends JSSWizardRunnablePage {

	public static final String DISCOVERED_FIELDS = "discovered_fields"; //$NON-NLS-1$
	public static final String DISCOVERED_PARAMETERS = "discovered_parameters"; //$NON-NLS-1$
	public static final String DATASET_FIELDS = "dataset_fields"; //$NON-NLS-1$
	public static final String GROUP_FIELDS = "group_fields"; //$NON-NLS-1$
	public static final String DATASET_QUERY_LANGUAGE = "query_language"; //$NON-NLS-1$
	public static final String DATASET_PROPERTIES = "dataset_properties";
	public static final String DATASET_QUERY_TEXT = "query_text"; //$NON-NLS-1$
	public static final String EXTRA_PARAMETERS = "extra_parameters"; //$NON-NLS-1$
	public static final String ORDER_GROUP = "create_sort_fields"; //$NON-NLS-1$

	private Composite composite_editor = null;
	private AWizardDataEditorComposite activeEditor = null;
	private Label lblEmptyEditor = null;
	private Map<DataAdapterDescriptor, AWizardDataEditorComposite> editors = new HashMap<>();

	/**
	 * This variable is used to initialize this page with all the defaults as it
	 * gets visible. If this is not the first load, the loadSettings method will
	 * probably do nothing.
	 * 
	 */
	boolean firstLoad = true;

	// Global UI elements;
	protected Combo dataAdaptersCombo = null;
	List<DataAdapterDescriptor> dataAdapterDescriptors = new ArrayList<>();

	private DataAdapterDescriptor selectedDataAdapterDescriptor = null;

	public StaticWizardDataSourcePage() {
		super("datasourcepage"); //$NON-NLS-1$
		setTitle(Messages.WizardDataSourcePage_datasource);
		setImageDescriptor(MDatasources.getIconDescriptor().getIcon32());
		setDescription(Messages.WizardDataSourcePage_description);
	}

	/**
	 * Return the context name for the help of this page
	 */
	@Override
	protected String getContextName() {
		return ContextHelpIDs.WIZARD_SELECT_DATASET;
	}

	/**
	 * Return the selected data adapter...
	 * 
	 * @return
	 */
	public DataAdapterDescriptor getDataAdapter() {
		return selectedDataAdapterDescriptor;
	}

	public void createControl(Composite parent) {
		Composite composite_container = new Composite(parent, SWT.NONE);
		setControl(composite_container);

		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		composite_container.setLayout(layout);

		Label lblDataAdapter = new Label(composite_container, SWT.NONE);
		lblDataAdapter.setText(Messages.WizardDataSourcePage_lblNewLabel_text);
		dataAdaptersCombo = new Combo(composite_container, SWT.READ_ONLY);
		dataAdaptersCombo.addDisposeListener(e -> cleanDataAdapterStorageListeners());
		dataAdaptersCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleDataAdapterSelectionEvent(e);
			}
		});
		dataAdaptersCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNew = new Button(composite_container, SWT.NONE);
		btnNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				CreateDataAdapterAction cda = new CreateDataAdapterAction();
				cda.run();

				if (cda.getNewDataAdapter() != null) {
					// Force a reload of the settings..
					firstLoad = true;
					loadSettings();
					int index = dataAdapterDescriptors.indexOf(cda.getNewDataAdapter());
					if (index >= 0) {
						dataAdaptersCombo.select(index);
						handleDataAdapterSelectionEvent(null);
					}
				}

			}
		});
		btnNew.setText(Messages.WizardDataSourcePage_btnNew_text);

		composite_editor = new Composite(composite_container, SWT.NONE);
		composite_editor.setLayout(new StackLayout());
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		gd.horizontalSpan = 3;
		composite_editor.setLayoutData(gd);

		lblEmptyEditor = new Label(composite_editor, SWT.BORDER);
		lblEmptyEditor.setText(Messages.WizardDataSourcePage_lblThisDataAdapter_text);
	}

	/**
	 * This procedure initialize the dialog page with the list of data adapters
	 * only if this is for real just the first time the page is shown.
	 * 
	 */
	public void loadSettings() {
		if (!firstLoad)
			return;
		firstLoad = false;

		dataAdapterDescriptors.clear();
		dataAdaptersCombo.removeAll();

		// Look if there is a specific project in which we are working...
		// In other words we assume that the user may have choose a project
		// directory
		// in a previous step, and this directory has been stored in the
		// settings with the key "new_file_path"...
		IProject selectedProject = null;
		JasperReportsConfiguration jConfig = null;
		if (getSettings() != null) {
			jConfig = (JasperReportsConfiguration) getSettings().get(JSSWizard.JASPERREPORTS_CONFIGURATION);
			IFile f = (IFile) jConfig.get(FileUtils.KEY_FILE);
			if (getSettings().containsKey(JSSWizard.FILE_PATH)) {
				IResource resource = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(((IPath) getSettings().get(JSSWizard.FILE_PATH)).toOSString());
				if (resource != null && resource.getProject() != null)
					selectedProject = resource.getProject();
			} else if (getSettings().get(JSSWizard.JASPERREPORTS_CONFIGURATION) != null && f != null)
				selectedProject = f.getProject();
		}
		cleanDataAdapterStorageListeners();

		storages = new ArrayList<>();
		// Load all the data adapters...
		// Attention, we are not loading all the possible data storages, but
		// only the
		// ones we know
		// which are preferences and project. In the future we may have other
		// data
		// storages
		storages.add(DataAdapterManager.getPreferencesStorage());

		if (selectedProject != null) {
			storages.add(DataAdapterManager.getProjectStorage(selectedProject));
		}

		for (ADataAdapterStorage storage : storages) {
			if (jConfig != null && !jConfig.getEditorContext().isDataAdapterStorage(storage))
				continue;

			List<DataAdapterDescriptor> das = new ArrayList<>(storage.getDataAdapterDescriptors());
			// Sort data adapters
			Collections.sort(das, new Comparator<DataAdapterDescriptor>() {
				@Override
				public int compare(DataAdapterDescriptor o1, DataAdapterDescriptor o2) {
					return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
				}
			});
			for (DataAdapterDescriptor d : das) {
				// Since we are not showing icons, we append the data adapter
				// type to the name
				dataAdaptersCombo.add(storage.getLabel(d));
				dataAdapterDescriptors.add(d);
			}
			storage.addPropertyChangeListener(dsListner);
		}

		if (!dataAdapterDescriptors.isEmpty()) {
			if (!selectDataAdapter(EmptyDataAdapterDescriptor.EMPTY_ADAPTER_NAME) && !selectDataAdapter("Sample DB"))
				dataAdaptersCombo.select(0);
			// update the editor control state
			handleDataAdapterSelectionEvent(null);
		}
	}

	private boolean selectDataAdapter(String name) {
		for (int i = 0; i < dataAdaptersCombo.getItemCount(); i++) {
			if (dataAdapterDescriptors.get(i).getName().equals(name)) {
				dataAdaptersCombo.select(i);
				return true;
			}
		}
		return false;
	}

	private void cleanDataAdapterStorageListeners() {
		if (storages != null) {
			for (ADataAdapterStorage s : storages)
				s.removePropertyChangeListener(dsListner);
		}
	}

	private PropertyChangeListener dsListner = arg0 -> loadSettings();
	private List<ADataAdapterStorage> storages;

	/**
	 * Invoked when a data adapter is selected from the combo box...
	 */
	public void handleDataAdapterSelectionEvent(SelectionEvent event) {
		setErrorMessage(null);
		selectedDataAdapterDescriptor = null;

		if (dataAdaptersCombo.getSelectionIndex() >= 0) {
			DataAdapterDescriptor da = dataAdapterDescriptors.get(dataAdaptersCombo.getSelectionIndex());

			selectedDataAdapterDescriptor = da;

			AWizardDataEditorComposite editor = null;

			if (editors.containsKey(da)) {
				editor = editors.get(da);
			} else {
				if (da instanceof IWizardDataEditorProvider) {
					editor = ((IWizardDataEditorProvider) da).createDataEditorComposite(composite_editor, this);
					editors.put(da, editor);
				}
			}

			if (editor != null) {
				if (editor != activeEditor) {
					activeEditor = editor;
					((StackLayout) composite_editor.getLayout()).topControl = editor;
					composite_editor.layout();
				}
			} else {
				activeEditor = null;
				// show default component...
				((StackLayout) composite_editor.getLayout()).topControl = lblEmptyEditor;
				composite_editor.layout();
			}
		}

		fireChangeEvent();
	}

	/**
	 * We use the setVisible(true) entry point to load the UI with
	 * loadSettings()...
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			loadSettings();
			// FIX for JSS-3181: in the latest platform it appears with Mac OS X 
			// and additional redraw invocation is required to prevent UI glitches
			// in some corner cases
			composite_editor.redraw();
		}
	}

	@Override
	public void run(final IProgressMonitor monitor) throws Exception {
		if (activeEditor != null) {
			getSettings().remove(DISCOVERED_FIELDS);
			String lang = activeEditor.getQueryLanguage();
			if (lang == null) {
				String[] langs = selectedDataAdapterDescriptor.getLanguages();
				if (!Misc.isNullOrEmpty(langs))
					lang = langs[0];
			}
			getSettings().put(DATASET_QUERY_LANGUAGE, lang);
			getSettings().put(DATASET_QUERY_TEXT, activeEditor.getQueryString());
			if (activeEditor instanceof SimpleQueryWizardDataEditorComposite)
				getSettings().put(DATASET_PROPERTIES,
						((SimpleQueryWizardDataEditorComposite) activeEditor).getDataset().getPropertiesMap());

			UIUtils.getDisplay().asyncExec(() -> monitor.setTaskName("Getting fields..."));

			ProgressMonitorCheckerThread checker = new ProgressMonitorCheckerThread(monitor);
			checker.addListener(activeEditor);
			checker.start();

			setupParameters();

			List<JRDesignField> fields = activeEditor.readFields();

			if (fields != null && !fields.isEmpty() && getSettings() != null)
				getSettings().put(DISCOVERED_FIELDS, fields);
		} else {
			getSettings().remove(DATASET_QUERY_LANGUAGE);
			getSettings().remove(DATASET_QUERY_TEXT);
			getSettings().remove(DATASET_PROPERTIES);
		}
	}

	public void setupParameters() throws Exception {
		if (activeEditor != null) {
			List<JRDesignParameter> prms = activeEditor.readParameters();
			if (prms != null && !prms.isEmpty() && getSettings() != null)
				getSettings().put(DISCOVERED_PARAMETERS, prms);
		}
	}

	/**
	 * We don't want to start an elaboration if there is not a suitable editor
	 * active...
	 * 
	 * @return boolean true if an elaboration is required, or false if the
	 * current status of the page does not require to trigger an elaboration
	 */
	@Override
	public boolean requireElaboration() {
		return activeEditor != null;
	}
}
