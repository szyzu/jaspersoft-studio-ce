/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.preferences.fonts.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.wizards.ContextHelpIDs;
import com.jaspersoft.studio.wizards.JSSHelpWizardPage;

import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.SimpleFontFace;
import net.sf.jasperreports.engine.fonts.SimpleFontFamily;

public class FontFamilyPage extends JSSHelpWizardPage {
	private static final String BOLDITALIC = "BOLDITALIC"; //$NON-NLS-1$
	private static final String ITALIC = "ITALIC"; //$NON-NLS-1$
	private static final String BOLD = "BOLD"; //$NON-NLS-1$
	private static final String NORMAL = "normal"; //$NON-NLS-1$
	private SimpleFontFamily fontFamily;
	private Text dsname;
	private Button embedepdf;
	private Combo pdfenc;
	private Button bIsVisible;

	public FontFamilyPage(FontFamily fontFamily) {
		super("fontfamilypage"); //$NON-NLS-1$
		setTitle(Messages.FontFamilyPage_dialogTitle);
		setDescription(Messages.FontFamilyPage_dialogSubtitle);
		this.fontFamily = (SimpleFontFamily) fontFamily;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		setControl(composite);

		new Label(composite, SWT.NONE).setText(Messages.FontFamilyPage_familyNameLabel);

		dsname = new Text(composite, SWT.BORDER);
		dsname.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String dstext = dsname.getText();
				if (dstext == null || dstext.trim().equals("")) {//$NON-NLS-1$
					setErrorMessage(Messages.WizardDatasetNewPage_validation_not_null);
					setPageComplete(false);
				} else {
					setPageComplete(true);
					setErrorMessage(null);
					setMessage(getDescription());
					fontFamily.setName(dstext);
				}
			}
		});
		dsname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		bIsVisible = new Button(composite, SWT.CHECK);
		bIsVisible.setText(Messages.FontFamilyPage_0);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		bIsVisible.setLayoutData(gd);
		bIsVisible.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fontFamily.setVisible(!bIsVisible.getSelection());
			}
		});

		CTabFolder tabFolder = new CTabFolder(composite, SWT.FLAT | SWT.TOP | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		tabFolder.setLayoutData(gd);
		tabFolder.setLayout(new GridLayout(1, false));

		createFileField(tabFolder, Messages.FontFamilyPage_normalLabel, NORMAL);
		tabFolder.setSelection(0);

		createFileField(tabFolder, Messages.FontFamilyPage_boldLabel, BOLD);
		createFileField(tabFolder, Messages.FontFamilyPage_italicLabel, ITALIC);
		createFileField(tabFolder, Messages.FontFamilyPage_boldItalicLabel, BOLDITALIC);

		Group gr = new Group(composite, SWT.NONE);
		gr.setText(Messages.FontFamilyPage_pdfGroup);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gr.setLayoutData(gd);
		gr.setLayout(new GridLayout(2, false));

		Label label = new Label(gr, SWT.NONE);
		label.setText(Messages.FontFamilyPage_pdfHintText);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		new Label(gr, SWT.NONE).setText(Messages.FontFamilyPage_pdfEncodingLabel);
		pdfenc = new Combo(gr, SWT.SINGLE | SWT.BORDER);
		pdfenc.setItems(ModelUtils.getPDFEncodings());
		pdfenc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = pdfenc.getSelectionIndex();
				if (index > -1 && index < pdfenc.getItemCount()) {
					String pdfencod = ModelUtils.getPDFEncoding2key(pdfenc.getItem(index));
					fontFamily.setPdfEncoding(pdfencod.isEmpty() ? null : pdfencod);
				}
			}

		});
		pdfenc.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				String pdfencod = pdfenc.getText();
				if (pdfencod.isEmpty())
					pdfencod = null;
				else
					pdfencod = ModelUtils.getPDFEncoding2key(pdfencod);
				fontFamily.setPdfEncoding(pdfencod);
			}
		});

		embedepdf = new Button(gr, SWT.CHECK);
		embedepdf.setText(Messages.FontFamilyPage_pdfEmbeddedLabel);
		embedepdf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fontFamily.setPdfEmbedded(embedepdf.getSelection());
			}

		});
		fillWidgets();
	}

	private FontFaceFragment createFileField(CTabFolder tabFolder, String name, final String type) {
		CTabItem bptab = new CTabItem(tabFolder, SWT.NONE);
		bptab.setText(name);

		SimpleFontFace fontFace = new SimpleFontFace(JasperReportsConfiguration.getDefaultInstance());
		if (type.equals(NORMAL)) {
			fontFace = Misc.nvl((SimpleFontFace) fontFamily.getNormalFace(), fontFace);
			fontFamily.setNormalFace(fontFace);
		} else if (type.equals(BOLD)) {
			fontFace = Misc.nvl((SimpleFontFace) fontFamily.getBoldFace(), fontFace);
			fontFamily.setBoldFace(fontFace);
		} else if (type.equals(ITALIC)) {
			fontFace = Misc.nvl((SimpleFontFace) fontFamily.getItalicFace(), fontFace);
			fontFamily.setItalicFace(fontFace);
		} else if (type.equals(BOLDITALIC)) {
			fontFace = Misc.nvl((SimpleFontFace) fontFamily.getBoldItalicFace(), fontFace);
			fontFamily.setBoldItalicFace(fontFace);
		}
		FontFaceFragment fontFaceDialog = new FontFaceFragment(fontFace);
		Composite cmp = fontFaceDialog.createDialogArea(tabFolder);

		bptab.setControl(cmp);
		return fontFaceDialog;
	}

	private void fillWidgets() {
		dsname.setText(fontFamily.getName());
		setPageComplete(fontFamily.getName() != null);
		bIsVisible.setSelection(!fontFamily.isVisible());

		String pdfEncoding = fontFamily.getPdfEncoding();
		int pdfEncodingIndex = ModelUtils.getPDFEncodingIndex(ModelUtils.getKey4PDFEncoding(pdfEncoding));
		pdfenc.select(pdfEncodingIndex >= 0 ? pdfEncodingIndex : 0);
		if (pdfEncodingIndex < 0 && pdfEncoding != null)
			pdfenc.setText(pdfEncoding);

		if (fontFamily.isPdfEmbedded() != null)
			embedepdf.setSelection(fontFamily.isPdfEmbedded());
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	@Override
	protected String getContextName() {
		return ContextHelpIDs.WIZARD_FONT_EXTENSION;
	}
}
