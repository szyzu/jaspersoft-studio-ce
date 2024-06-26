/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.dataset.dialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.IPropertySource;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.IDataPreviewInfoProvider;
import com.jaspersoft.studio.data.IFieldSetter;
import com.jaspersoft.studio.data.designer.AQueryDesignerContainer;
import com.jaspersoft.studio.data.designer.QueryStatus;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.MQuery;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.model.MRoot;
import com.jaspersoft.studio.model.dataset.MDataset;
import com.jaspersoft.studio.model.field.MField;
import com.jaspersoft.studio.model.field.command.CreateFieldCommand;
import com.jaspersoft.studio.model.field.command.DeleteFieldCommand;
import com.jaspersoft.studio.model.field.command.ReplaceAllFieldsCommand;
import com.jaspersoft.studio.model.parameter.MParameter;
import com.jaspersoft.studio.model.parameter.MParameterSystem;
import com.jaspersoft.studio.model.parameter.MParameters;
import com.jaspersoft.studio.model.parameter.command.CreateParameterCommand;
import com.jaspersoft.studio.model.parameter.command.DeleteParameterCommand;
import com.jaspersoft.studio.model.parameter.command.ReplaceAllParametersCommand;
import com.jaspersoft.studio.model.sortfield.command.ReplaceAllSortFieldsCommand;
import com.jaspersoft.studio.property.SetValueCommand;
import com.jaspersoft.studio.property.dataset.fields.FieldsTable;
import com.jaspersoft.studio.property.dataset.preview.DataPreviewTable;
import com.jaspersoft.studio.property.dataset.prm.ParametersTable;
import com.jaspersoft.studio.property.dataset.sort.SortFieldsTable;
import com.jaspersoft.studio.swt.events.ExpressionModifiedEvent;
import com.jaspersoft.studio.swt.events.ExpressionModifiedListener;
import com.jaspersoft.studio.swt.widgets.CSashForm;
import com.jaspersoft.studio.swt.widgets.CSashForm.ICustomSashFormListener;
import com.jaspersoft.studio.swt.widgets.WTextExpression;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.wizards.ContextHelpIDs;

import net.sf.jasperreports.eclipse.ui.util.PersistentLocationFormDialog;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.design.JasperDesign;

public class DatasetDialog extends PersistentLocationFormDialog implements IFieldSetter, IDataPreviewInfoProvider {

	/**
	 * Utility interface to manage the details of the JDBC Metadata Loading inside the Dataset&Query dialog
	 */
	public interface JDBC_METADATA_LOADING {
		public static final String DISABLED = "ALWAYS_DISABLED"; //$NON-NLS-1$
		public static final String DISABLED_ON_STARTUP = "DISABLED_ON_STARTUP"; //$NON-NLS-1$
		public static final String ENABLED = "ALWAYS_ENABLED"; //$NON-NLS-1$
		public static final String P_JDBC_METADATA_LOADING = "com.jaspersoft.studio.data.sql.prefs.JDBCMETADATALOADING"; //$NON-NLS-1$
	}
	
	private static boolean INITIAL_JDBCMETADATA_LOADING = true;
	
	private MDataset mdataset;
	private JasperReportsConfiguration jConfig;
	private Map<JRField, JRField> mapfields;
	private Map<JRParameter, JRParameter> mapparam;
	private CommandStack cmdStack;
	private JRDesignDataset newdataset;
	private FieldsTable ftable;
	private ParametersTable ptable;
	private SortFieldsTable sftable;
	private DataQueryAdapters dataquery;
	private WTextExpression filterExpression;
	private DataPreviewTable dataPreviewTable;
	private Composite body;
	private JSSCompoundCommand command;
	private Color background;

	public DatasetDialog(Shell shell, MDataset mdataset, JasperReportsConfiguration jConfig, CommandStack cmdStack) {
		super(shell);
		INITIAL_JDBCMETADATA_LOADING = true; // be sure to reset on each dialog opening
		this.cmdStack = cmdStack;
		this.mdataset = mdataset;
		this.jConfig = jConfig;
		newdataset = (JRDesignDataset) mdataset.getValue().clone();

		mapfields = new HashMap<>();
		List<JRField> newFieldsList = newdataset.getFieldsList();
		List<JRField> oldFieldsList = mdataset.getValue().getFieldsList();
		for (int i = 0; i < oldFieldsList.size(); i++)
			mapfields.put(oldFieldsList.get(i), newFieldsList.get(i));

		mapparam = new HashMap<>();
		List<JRParameter> newParamList = newdataset.getParametersList();
		List<JRParameter> oldParamList = mdataset.getValue().getParametersList();
		for (int i = 0; i < oldParamList.size(); i++)
			mapparam.put(oldParamList.get(i), newParamList.get(i));

	}
	
	/**
	 * Checks the flag indicating if the initial metadata loading (event) has still to occur.
	 */
	public static boolean isInitialJDBCMetadataLoading() {
		return INITIAL_JDBCMETADATA_LOADING;
	}
	
	/**
	 * Forces the information about the initial metadata loading as performed.
	 */
	public static void initialJDBCMetadataPerformed() {
		INITIAL_JDBCMETADATA_LOADING = false;
	}

	@Override
	protected boolean canHandleShellCloseEvent() {
		// Create the commands to check if there are changes and popup the
		// dialog
		// only if there commands to execute
		createCommand();
		// command is never null after createCommand, but if empty its
		// canExecute will return false
		if (!command.canExecute())
			return true;
		return UIUtils.showConfirmation(Messages.DatasetDialog_0, Messages.DatasetDialog_1);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.DatasetDialog_title);
	}

	@Override
	public boolean close() {
		if (getReturnCode() == OK) {
			createCommand();
			if (cmdStack != null) {
				cmdStack.execute(command);
			}
		}
		dataquery.dispose();
		ptable.getPropertyChangeSupport().removePropertyChangeListener(prmListener);
		return super.close();
	}

	/**
	 * Set the root control of the wizard, and also add a listener to do the
	 * perform help action and set the context of the top control.
	 */
	protected void setHelpControl(Control newControl) {
		newControl.addListener(SWT.Help, event -> performHelp());
	};

	/**
	 * Set and show the help data if a context, that bind this wizard with the
	 * data, is provided
	 */
	public void performHelp() {
		String child = dataquery.getContextHelpId();
		if (child == null)
			child = ContextHelpIDs.WIZARD_QUERY_DIALOG;

		PlatformUI.getWorkbench().getHelpSystem().setHelp(body, child);
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(child);
	};

	@Override
	protected void createFormContent(final IManagedForm mform) {
		FormToolkit toolkit = mform.getToolkit();
		body = mform.getForm().getBody();

		setHelpControl(body);
		body.setLayout(new GridLayout(1, true));
		background = body.getBackground();
		UIUtils.getDisplay().asyncExec(() -> {
			body.setBackground(sf.getBackground());
			body.getParent().update();
			body.getParent().layout(true);
		});

		dataquery = new DataQueryAdapters(body, jConfig, newdataset, background) {
			@Override
			protected void createStatusBar(Composite comp) {
				qStatus = new QueryStatus(comp);
			}

			@Override
			public void setFields(List<JRDesignField> fields) {
				DatasetDialog.this.setFields(fields);
			}

			@Override
			public List<JRDesignField> getCurrentFields() {
				return DatasetDialog.this.getCurrentFields();
			}

			@Override
			public void setParameters(List<JRParameter> params) {
				DatasetDialog.this.setParameters(params);
			}

			@Override
			public int getContainerType() {
				return AQueryDesignerContainer.CONTAINER_WITH_INFO_TABLES;
			}

		};

		dataquery.createToolbar(body);

		sf = new CSashForm(body, SWT.VERTICAL);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 600;
		gd.minimumHeight = 400;
		gd.widthHint = 800;
		sf.setLayoutData(gd);
		sf.setLayout(new GridLayout());

		dataquery.createTop(sf, this);

		// int tabHeight = c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		// tabHeight = Math.max(tabHeight, ctf.getTabHeight());
		// ctf.setTabHeight(tabHeight);
		//
		// ctf.setTopRight(c);

		createBottom(sf, toolkit);

		JasperDesign jd = mdataset.getJasperDesign();
		if (jd == null) {
			jd = mdataset.getMreport().getJasperDesign();
		}
		setDataset(jd, newdataset);
	}

	protected void initSashForm(CSashForm sashform, final JRDesignDataset jDataset) {
		final String SASH_W1 = getClass().getCanonicalName() + ".sash.w1";
		final String SASH_W2 = getClass().getCanonicalName() + ".sash.w2";
		int w1 = 450;
		int w2 = 250;
		if (jDataset != null) {
			try {
				String sw1 = jDataset.getPropertiesMap().getProperty(SASH_W1);
				if (sw1 != null)
					w1 = Integer.parseInt(sw1);
			} catch (NumberFormatException e) {
			}
			try {
				String sw2 = jDataset.getPropertiesMap().getProperty(SASH_W2);
				if (sw2 != null)
					w2 = Integer.parseInt(sw2);
			} catch (NumberFormatException e) {
			}
		}
		sashform.setWeights(new int[] { w1, w2 });
		sashform.addCustomSashFormListener(new ICustomSashFormListener() {

			@Override
			public void dividerMoved(int firstControlWeight, int secondControlWeight) {
				jDataset.getPropertiesMap().setProperty(SASH_W1, Integer.toString(firstControlWeight));
				jDataset.getPropertiesMap().setProperty(SASH_W2, Integer.toString(secondControlWeight));
			}
		});
	}

	public void setFields(List<JRDesignField> fields) {
		ftable.setFields(fields);
	}

	public void addFields(List<JRDesignField> fields) {
		List<JRDesignField> allFields = ftable.getFields();
		for (JRDesignField f : fields) {
			// Take care of having "valid" name for field
			String newName = ModelUtils.getNameForField(allFields, f.getName());
			f.setName(newName);
			allFields.add(f);
		}
		ftable.setFields(allFields);
	}

	public void clearFields() {
		ftable.setFields(new ArrayList<JRDesignField>(0));
	}

	public void setParameters(List<JRParameter> fields) {
		ptable.setFields(fields);
	}

	public List<JRDesignField> getCurrentFields() {
		return ftable.getFields();
	}

	private void createBottom(Composite parent, FormToolkit toolkit) {
		CTabFolder tabFolder = new CTabFolder(parent, SWT.BOTTOM | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 250;
		tabFolder.setLayoutData(gd);

		createFields(toolkit, tabFolder);
		createParameters(toolkit, tabFolder);
		createSortFields(toolkit, tabFolder);
		createFilterExpression(toolkit, tabFolder);
		createDataPreview(toolkit, tabFolder);

		tabFolder.setSelection(0);
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				sftable.refresh();
			}

		});
	}

	private void createDataPreview(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem dataPreviewtab = new CTabItem(tabFolder, SWT.NONE);
		dataPreviewtab.setText(Messages.DatasetDialog_DataPreviewTab);

		dataPreviewTable = new DataPreviewTable(dataquery, tabFolder, this, background);

		dataPreviewtab.setControl(dataPreviewTable.getControl());
	}

	private void createParameters(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(Messages.DatasetDialog_ParametersTab);

		ptable = new ParametersTable(tabFolder, newdataset, background, mdataset.isMainDataset(), mdataset);

		ptable.getPropertyChangeSupport().addPropertyChangeListener(prmListener);
		bptab.setControl(ptable.getControl());
	}

	private PropertyChangeListener prmListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			MRoot mroot = new MRoot(null, null);
			mroot.setJasperConfiguration(jConfig);
			MReport mrep = new MReport(mroot, jConfig);
			MDataset mdts = new MDataset(mrep, newdataset, -1);
			MParameters<?> mprms = new MParameters<>(mdts, newdataset, JRDesignDataset.PROPERTY_PARAMETERS);
			MParameter mprm = new MParameter(mprms, (JRDesignParameter) arg0.getSource(), -1);
			List<Command> cmds = JaspersoftStudioPlugin.getPostSetValueManager().postSetValue(mprm,
					JRDesignParameter.PROPERTY_NAME, arg0.getNewValue(), arg0.getOldValue());
			for (Command c : cmds)
				c.execute();
			dataquery.setDataset(jConfig.getJasperDesign(), newdataset);
		}
	};
	private CSashForm sf;

	private void createFields(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(Messages.DatasetDialog_fieldstab);

		ftable = new FieldsTable(tabFolder, newdataset, background, mdataset);

		bptab.setControl(ftable.getControl());
	}

	private void createSortFields(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(Messages.DatasetDialog_sortingtab);

		sftable = new SortFieldsTable(tabFolder, newdataset, background);

		bptab.setControl(sftable.getControl());
	}

	private void createFilterExpression(FormToolkit toolkit, CTabFolder tabFolder) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(Messages.DatasetDialog_filterexpression);

		Composite sectionClient = toolkit.createComposite(tabFolder);
		FillLayout fLayout = new FillLayout();
		fLayout.marginHeight = 5;
		fLayout.marginWidth = 5;
		sectionClient.setLayout(fLayout);

		filterExpression = new WTextExpression(sectionClient, SWT.NONE);
		filterExpression.setBackground(sectionClient.getBackground());
		filterExpression.setExpressionContext(new ExpressionContext(newdataset, mdataset.getJasperConfiguration()));
		filterExpression.setExpression((JRDesignExpression) newdataset.getFilterExpression());
		filterExpression.addModifyListener(new ExpressionModifiedListener() {

			@Override
			public void expressionModified(ExpressionModifiedEvent event) {
				newdataset.setFilterExpression(event.modifiedExpression);
			}
		});
		bptab.setControl(sectionClient);
	}

	public JSSCompoundCommand getCommand() {
		return command;
	}

	public void setDataset(JasperDesign jDesign, JRDesignDataset ds) {
		initSashForm(sf, ds);
		dataquery.setDataset(jDesign, ds);
	}

	public void createCommand() {
		JRDesignDataset ds = (mdataset.getParent() == null
				? mdataset.getJasperConfiguration().getJasperDesign().getMainDesignDataset()
				: mdataset.getValue());
		command = new JSSCompoundCommand(mdataset.getMreport());

		String lang = newdataset.getQuery().getLanguage();
		((JRDesignQuery) newdataset.getQuery()).setText(dataquery.getQuery());

		String qtext = newdataset.getQuery().getText();
		if (ds.getQuery() == null) {
			JRDesignQuery jrQuery = new JRDesignQuery();
			jrQuery.setLanguage(lang);
			jrQuery.setText(qtext);
			command.add(setValueCommand(JRDesignDataset.PROPERTY_QUERY, new MQuery(jrQuery, mdataset), mdataset));
		} else {
			IPropertySource mquery = (IPropertySource) mdataset.getPropertyValue(JRDesignDataset.PROPERTY_QUERY);
			String language = ds.getQuery().getLanguage();
			if (language == null || !language.equalsIgnoreCase(lang))
				command.add(setValueCommand(JRDesignQuery.PROPERTY_LANGUAGE, lang, mquery));
			if (!ds.getQuery().getText().equals(qtext))
				command.add(setValueCommand(JRDesignQuery.PROPERTY_TEXT, qtext, mquery));
		}
		String fexprtext = filterExpression.getText();
		if (fexprtext.trim().equals("")) //$NON-NLS-1$
			fexprtext = null;
		addSetValueCommand(command, JRDesignDataset.PROPERTY_FILTER_EXPRESSION, fexprtext, mdataset);
		addSetValueCommand(command, MDataset.PROPERTY_MAP, newdataset.getPropertiesMap(), mdataset);

		List<JRField> oldfields = ds.getFieldsList();
		List<JRDesignField> newfields = ftable.getFields();
		for (JRField f : oldfields) {
			Boolean canceled = null;
			for (JRDesignField newf : newfields) {
				if (newf.getName().equals(f.getName()) || mapfields.get(f) == newf) {
					canceled = Boolean.TRUE;
					break;
				}
			}
			if (canceled == null)
				command.add(new DeleteFieldCommand(jConfig, ds, (JRDesignField) f, canceled));
		}
		for (JRDesignField newf : newfields) {
			boolean notexists = true;
			for (JRField f : oldfields) {
				if (newf.getName().equals(f.getName())) {
					MField mfield = mdataset.getField(newf.getName());
					if (mfield != null) {
						addSetValueCommand(command, JRDesignField.PROPERTY_VALUE_CLASS_NAME, newf.getValueClassName(),
								mfield);
						addSetValueCommand(command, JRDesignField.PROPERTY_DESCRIPTION, newf.getDescription(), mfield);
						addSetValueCommand(command, JRDesignField.PROPERTY_PROPERTY_EXPRESSIONS,
								newf.getPropertyExpressionsList(), mfield);
						addSetValueCommand(command, MField.PROPERTY_MAP, newf.getPropertiesMap(), mfield);
					}
					notexists = false;
				} else if (mapfields.containsValue(newf)) {
					MField mfield = null;
					for (JRField p : mapfields.keySet()) {
						if (mapfields.get(p) == newf) {
							mfield = mdataset.getField(p.getName());
							break;
						}
					}
					if (mfield != null) {
						addSetValueCommand(command, JRDesignField.PROPERTY_NAME, newf.getName(), mfield);
						addSetValueCommand(command, JRDesignField.PROPERTY_VALUE_CLASS_NAME, newf.getValueClassName(),
								mfield);
						addSetValueCommand(command, JRDesignField.PROPERTY_DESCRIPTION, newf.getDescription(), mfield);
						addSetValueCommand(command, JRDesignField.PROPERTY_PROPERTY_EXPRESSIONS,
								newf.getPropertyExpressionsList(), mfield);
						addSetValueCommand(command, MField.PROPERTY_MAP, newf.getPropertiesMap(), mfield);
					}
					notexists = false;
				}
				if (!notexists)
					break;
			}
			if (notexists)
				command.add(new CreateFieldCommand(ds, newf, -1));
		}

		// checks ordering for fields
		if (oldfields.size() == newfields.size()) {
			for (int i = 0; i < newfields.size(); i++) {
				if (!ModelUtils.areFieldsEquals(oldfields.get(i), newfields.get(i))) {
					command.add(new ReplaceAllFieldsCommand(newfields, mdataset.getMFields()));
					break;
				}
			}
		} else {
			command.add(new ReplaceAllFieldsCommand(newfields, mdataset.getMFields()));
		}

		// handle sort fields
		List<JRSortField> oldSortFields = ds.getSortFieldsList();
		List<JRDesignSortField> newSortFields = sftable.getFields();
		if (oldSortFields.size() == newSortFields.size()) {
			for (int i = 0; i < newSortFields.size(); i++) {
				if (!ModelUtils.areSortFieldsEquals(oldSortFields.get(i), newSortFields.get(i))) {
					command.add(new ReplaceAllSortFieldsCommand(newSortFields, mdataset.getMSortFields()));
					break;
				}
			}
		} else {
			command.add(new ReplaceAllSortFieldsCommand(newSortFields, mdataset.getMSortFields()));
		}

		List<JRParameter> oldparams = ds.getParametersList();
		List<JRDesignParameter> newparams = ptable.getParameters();
		for (JRParameter f : oldparams) {
			if (f.isSystemDefined())
				continue;
			Boolean canceled = null;
			for (JRParameter newf : newparams)
				if (newf.getName().equals(f.getName()) || mapparam.get(f) == newf) {
					canceled = Boolean.TRUE;
					break;
				}
			if (canceled == null)
				command.add(new DeleteParameterCommand(jConfig, ds, f, canceled));
		}
		for (JRParameter newf : newparams) {
			if (newf.isSystemDefined())
				continue;
			boolean notexists = true;
			for (JRParameter f : oldparams) {
				JRDesignParameter prm = (JRDesignParameter) newdataset.getParametersMap().get(newf.getName());
				if (prm == null)
					break;
				if (newf.getName().equals(f.getName())) {
					MParameterSystem mparam = mdataset.getParamater(newf.getName());
					if (mparam != null) {
						addSetValueCommand(command, JRDesignParameter.PROPERTY_VALUE_CLASS_NAME,
								prm.getValueClassName(), mparam);
						addSetValueCommand(command, JRDesignParameter.PROPERTY_DESCRIPTION, prm.getDescription(),
								mparam);
						addSetValueCommand(command, JRDesignParameter.PROPERTY_DEFAULT_VALUE_EXPRESSION,
								prm.getDefaultValueExpression(), mparam);
						addSetValueCommand(command, JRDesignParameter.PROPERTY_FOR_PROMPTING, prm.isForPrompting(),
								mparam);
						addSetValueCommand(command, JRDesignParameter.PROPERTY_EVALUATION_TIME, prm.getEvaluationTime(),
								mparam);
						addSetValueCommand(command, MParameter.PROPERTY_MAP, prm.getPropertiesMap(), mparam);
					}
					notexists = false;
				} else if (mapparam.containsValue(newf)) {
					MParameterSystem mparam = null;
					for (JRParameter p : mapparam.keySet()) {
						if (mapparam.get(p) == newf) {
							mparam = mdataset.getParamater(p.getName());
							break;
						}
					}

					if (mparam != null) {
						addSetValueCommand(command, JRDesignParameter.PROPERTY_NAME, prm.getName(), mparam);
						addSetValueCommand(command, JRDesignParameter.PROPERTY_VALUE_CLASS_NAME,
								prm.getValueClassName(), mparam);
						addSetValueCommand(command, JRDesignParameter.PROPERTY_DESCRIPTION, prm.getDescription(),
								mparam);
						addSetValueCommand(command, JRDesignParameter.PROPERTY_DEFAULT_VALUE_EXPRESSION,
								prm.getDefaultValueExpression(), mparam);
						addSetValueCommand(command, JRDesignParameter.PROPERTY_EVALUATION_TIME, prm.getEvaluationTime(),
								mparam);
						addSetValueCommand(command, JRDesignParameter.PROPERTY_FOR_PROMPTING, prm.isForPrompting(),
								mparam);
						addSetValueCommand(command, MParameter.PROPERTY_MAP, prm.getPropertiesMap(), mparam);
					}
					notexists = false;
				}
				if (!notexists)
					break;
			}
			if (notexists)
				command.add(new CreateParameterCommand(ds, newf, jConfig, -1));
		}

		// handle parameters for a possible reorder
		if (oldparams.size() == newparams.size()) {
			for (int i = 0; i < newparams.size(); i++) {
				if (!ModelUtils.areParametersEquals(oldparams.get(i), newparams.get(i))) {
					command.add(new ReplaceAllParametersCommand(newparams, mdataset.getMParameters()));
					break;
				}
			}
		} else {
			command.add(new ReplaceAllParametersCommand(newparams, mdataset.getMParameters()));
		}
	}

	/**
	 * Create a new setvalue command, but only if the old value and the new
	 * value are different. If created the command is added to the compound
	 * command
	 * 
	 * @param cc the compound command
	 * @param property the property the set value command will set
	 * @param value the new value of the property
	 * @param target the target of the set
	 */
	private void addSetValueCommand(JSSCompoundCommand cc, String property, Object value, IPropertySource target) {
		if (!extendedEquals(value, target.getPropertyValue(property))) {
			SetValueCommand cmd = new SetValueCommand();
			cmd.setTarget(target);
			cmd.setPropertyId(property);
			cmd.setPropertyValue(value);
			cc.add(cmd);
		}
	}

	/**
	 * Used to compare two values. Since some JR Objects used inside here
	 * dosen't support the equals method, this one apply some special cases for
	 * that objects
	 * 
	 * @param value1 the first value to compare, can be null
	 * @param value2 the second value to compare, can be null
	 * @return true if the two objects are equals, false otherwise
	 */
	private boolean extendedEquals(Object value1, Object value2) {
		if (value1 instanceof JRExpression && value2 instanceof JRExpression) {
			JRExpression exp1 = (JRExpression) value1;
			JRExpression exp2 = (JRExpression) value2;
			return ModelUtils.safeEquals(exp1.getText(), exp2.getText());
		} else if (value1 instanceof JRPropertiesMap && value2 instanceof JRPropertiesMap) {
			JRPropertiesMap map1 = (JRPropertiesMap) value1;
			JRPropertiesMap map2 = (JRPropertiesMap) value2;
			return ModelUtils.jrPropertiesMapEquals(map1, map2);
		} else
			return ModelUtils.safeEquals(value1, value2);
	}

	private Command setValueCommand(String property, Object value, IPropertySource target) {
		SetValueCommand cmd = new SetValueCommand();
		cmd.setTarget(target);
		cmd.setPropertyId(property);
		cmd.setPropertyValue(value);
		return cmd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.IDataPreviewInfoProvider#
	 * getJasperReportsConfig()
	 */
	public JasperReportsConfiguration getJasperReportsConfig() {
		return this.jConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.data.IDataPreviewInfoProvider#
	 * getDataAdapterDescriptor()
	 */
	public DataAdapterDescriptor getDataAdapterDescriptor() {
		return this.dataquery.getDataAdapter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jaspersoft.studio.data.IDataPreviewInfoProvider#getDesignDataset()
	 */
	public JRDesignDataset getDesignDataset() {
		return this.newdataset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jaspersoft.studio.data.IDataPreviewInfoProvider#getFieldsForPreview()
	 */
	public List<JRDesignField> getFieldsForPreview() {
		return this.getCurrentFields();
	}

}
