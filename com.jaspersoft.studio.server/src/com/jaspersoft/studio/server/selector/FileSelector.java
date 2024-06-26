/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.selector;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.dto.resources.ClientFile.FileType;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.studio.jface.IFileSelection;
import com.jaspersoft.studio.jface.dialogs.FilePreviewSelectionDialog;
import com.jaspersoft.studio.jface.dialogs.FileSelectionDialog;
import com.jaspersoft.studio.jface.dialogs.ImageSelectionDialog;
import com.jaspersoft.studio.jface.dialogs.StyleTemplateSelectionDialog;
import com.jaspersoft.studio.jface.dialogs.SubreportSelectionDialog;
import com.jaspersoft.studio.server.ServerManager;
import com.jaspersoft.studio.server.export.AExporter;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.model.server.MServerProfile;
import com.jaspersoft.studio.server.properties.dialog.RepositoryDialog;
import com.jaspersoft.studio.server.protocol.Feature;
import com.jaspersoft.studio.server.wizard.find.FindResourceJob;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.design.JasperDesign;

public class FileSelector implements IFileSelection {
	private Composite cmpExpr;
	private FileSelectionDialog dialog;
	private Text txtURL;
	private JasperDesign jd;
	private ModifyListener listener = e -> dialog.setFileExpressionText(txtURL.getText());

	@Override
	public void createRadioButton(Composite parent, FileSelectionDialog d, JasperDesign jd) {
		this.dialog = d;
		this.jd = jd;
		Button btnExpression = new Button(parent, SWT.RADIO);
		btnExpression.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnExpression.getSelection()) {
					dialog.changeFileSelectionMode(cmpExpr);
					d.setAllowValidation(false);
				}
			}
		});
		btnExpression.setText(Messages.FileSelector_0);
	}

	@Override
	public void changeSelectionMode(Control newTopControl) {
		txtURL.setText(""); //$NON-NLS-1$
	}

	@Override
	public void createFileSelectionContainer(Composite parent) {
		cmpExpr = new Composite(parent, SWT.NONE);
		cmpExpr.setLayout(new GridLayout(2, false));

		Label lbl = new Label(cmpExpr, SWT.NONE);
		lbl.setText(Messages.FileSelector_0);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		lbl.setLayoutData(gd);

		txtURL = new Text(cmpExpr, SWT.BORDER);
		txtURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtURL.addModifyListener(listener);

		Button btn = new Button(cmpExpr, SWT.PUSH);
		btn.setText("..."); //$NON-NLS-1$
		btn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String servURL = jd.getProperty(AExporter.PROP_SERVERURL);
				String servUser = jd.getProperty(AExporter.PROP_USER);

				MServerProfile msp = ServerManager.getServerByUrl(servURL, servUser);
				if (msp == null) {
					SelectServerWizard wizard = new SelectServerWizard();
					WizardDialog w = new WizardDialog(UIUtils.getShell(), wizard);
					if (w.open() == Dialog.OK) {
						msp = wizard.getValue();
						try {
							jd.setProperty(AExporter.PROP_SERVERURL, msp.getValue().getUrl());
							jd.setProperty(AExporter.PROP_USER, AExporter.encodeUsr(msp.getValue()));
						} catch (MalformedURLException | URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
				if (msp != null)
					showFindDialog(msp);
			}

			protected void showFindDialog(MServerProfile msp) {
				if (msp.isSupported(Feature.SEARCHREPOSITORY)) {
					boolean t = msp.getWsClient().getServerInfo().getVersion().compareTo("5.5") >= 0; //$NON-NLS-1$
					String[] incl = null;
					if (dialog instanceof SubreportSelectionDialog) {
						incl = new String[] { t ? FileType.jrxml.name() : ResourceMediaType.FILE_CLIENT_TYPE };
					}
					else if (dialog instanceof ImageSelectionDialog) {
						incl = new String[] { t ? FileType.img.name() : ResourceMediaType.FILE_CLIENT_TYPE };
					}
					else if (dialog instanceof StyleTemplateSelectionDialog) {
						incl = new String[] { t ? FileType.jrtx.name() : ResourceMediaType.FILE_CLIENT_TYPE };
					}
					else if (dialog instanceof IFileSelectorServerTypes) {
						incl = ((IFileSelectorServerTypes) dialog).getServerSupportedTypes();
					}
					ResourceDescriptor rd = FindResourceJob.doFindResource(msp, incl, null, true);
					if (rd != null) {
						dialog.setFileExpressionText("repo:" + rd.getUriString()); //$NON-NLS-1$
						txtURL.removeModifyListener(listener);
						txtURL.setText(rd.getUriString());
						txtURL.addModifyListener(listener);
						if (dialog instanceof FilePreviewSelectionDialog)
							((FilePreviewSelectionDialog) dialog).loadImagePreview();
					}
				} else {
					RepositoryDialog rd = new RepositoryDialog(UIUtils.getShell(), msp) {

						@Override
						public boolean isResourceCompatible(AMResource r) {
							if (dialog instanceof SubreportSelectionDialog) {
								return r.getValue().getWsType().equals(ResourceDescriptor.TYPE_JRXML);
							}
							else if (dialog instanceof ImageSelectionDialog) {
								return r.getValue().getWsType().equals(ResourceDescriptor.TYPE_IMAGE);
							}
							else if (dialog instanceof StyleTemplateSelectionDialog) {
								return r.getValue().getWsType().equals(ResourceDescriptor.TYPE_STYLE_TEMPLATE);
							}
							else if (dialog instanceof IFileSelectorServerTypes) {
								return ((IFileSelectorServerTypes) dialog).isResourceCompatible(r.getValue().getWsType());
							}
							return true;
						}
					};
					if (rd.open() == Dialog.OK) {
						AMResource rs = rd.getResource();
						if (rs != null) {
							dialog.setFileExpressionText("repo:" + rs.getValue().getUriString()); //$NON-NLS-1$
							txtURL.removeModifyListener(listener);
							txtURL.setText(rs.getValue().getUriString());
							txtURL.addModifyListener(listener);
							if (dialog instanceof FilePreviewSelectionDialog)
								((FilePreviewSelectionDialog) dialog).loadImagePreview();
						}
					}
				}
			}
		});

	}

}
