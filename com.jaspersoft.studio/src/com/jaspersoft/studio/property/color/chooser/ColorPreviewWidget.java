/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.color.chooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;

import com.jaspersoft.studio.messages.Messages;

/**
 * Composite where an old color is compared with a new one. If the old color is
 * null then the composite will show only the new color
 * 
 * @author Orlandin Marco
 */
public class ColorPreviewWidget extends Composite {

	/**
	 * Color compared with the new one. If it is null it is not shown
	 */
	private RGB oldColor = null;

	/**
	 * Alpha of the color compared with the new one.
	 */
	private int alphaOldColor = 255;

	/**
	 * Color compared with the old one
	 */
	private RGB newColor = null;

	/**
	 * Alpha of the color compared with the old one.
	 */
	private int alphaNewColor = 255;

	/**
	 * Composite where the new and, eventually, the old color are painted
	 */
	private Composite colorComposite;

	/**
	 * composite where additional components can be placed right to the color
	 * preview
	 */
	private Composite additionalComponents;

	/**
	 * Label to identify the old color, a reference is keep to hide it easily
	 * when there is not an old color
	 */
	private Label oldColorLabel;

	/**
	 * Create the preview composite
	 * 
	 * @param parent
	 * @param style
	 */
	public ColorPreviewWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Composite left = new Composite(this, SWT.NONE);
		left.setLayout(new GridLayout(1, false));
		left.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		Composite right = new Composite(this, SWT.NONE);
		right.setLayout(new GridLayout(1, false));
		right.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		Label newColorLabel = new Label(left, SWT.NONE);
		newColorLabel.setText(Messages.ColorDialog_newColorLabel);
		newColorLabel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false));

		colorComposite = new Composite(left, SWT.BORDER);
		colorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridData previewData = new GridData();
		previewData.widthHint = 120;
		previewData.heightHint = 50;
		previewData.verticalAlignment = SWT.TOP;
		colorComposite.setLayoutData(previewData);

		new Label(right, SWT.NONE);
		additionalComponents = new Composite(right, SWT.NONE);
		GridLayout additionalComponentLayout = new GridLayout(1, false);
		additionalComponentLayout.horizontalSpacing = 0;
		additionalComponentLayout.verticalSpacing = 0;
		additionalComponentLayout.marginHeight = 0;
		additionalComponentLayout.marginWidth = 0;
		additionalComponents.setLayout(additionalComponentLayout);
		additionalComponents.setLayoutData(new GridData(GridData.FILL_BOTH));

		oldColorLabel = new Label(left, SWT.NONE);
		oldColorLabel.setText(Messages.ColorDialog_oldColorLabel);
		GridData oldColorLabelData = new GridData(SWT.CENTER, SWT.TOP, false, false);
		oldColorLabelData.exclude = true;
		oldColorLabel.setLayoutData(oldColorLabelData);
		oldColorLabel.setVisible(false);

		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				updatePreview();
			}
		});
		colorComposite.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				Rectangle rect = colorComposite.getClientArea();
				int width = rect.width;
				int height = rect.height;
				gc.setAntialias(SWT.ON);
				// If there is an old color set paint also it into the preview area to
				// compare it with the new one,
				// otherwise print only the new one
				if (oldColor != null) {
					gc.setAlpha(255);
					gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
					gc.fillRectangle(0, 0, width, height / 2);
					gc.setAlpha(alphaNewColor);
					gc.setBackground(ResourceManager.getColor(newColor));
					gc.fillRectangle(0, 0, width, height / 2);
					gc.setAlpha(255);
					gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
					gc.fillRectangle(0, height / 2, width, height);
					gc.setAlpha(alphaOldColor);
					gc.setBackground(ResourceManager.getColor(oldColor));
					gc.fillRectangle(0, height / 2, width, height);
				} else {
					gc.setAlpha(255);
					gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
					gc.fillRectangle(0, 0, width, height);
					gc.setAlpha(alphaNewColor);
					gc.setBackground(ResourceManager.getColor(newColor));
					gc.fillRectangle(0, 0, width, height);
				}
				gc.dispose();
				
			}
		});
	}

	/**
	 * Return the composite where additional components can be placed
	 * 
	 * @return a composite right to the color preview with a grid layout with
	 * only a column and without margins. if fill the container in both
	 * directions
	 */
	public Composite getAdditionalComponentarea() {
		return additionalComponents;
	}

	/**
	 * Set the new color
	 * 
	 * @param color rgb of the new color
	 * @param alphaNewColor alpha of the new color
	 */
	public void setNewColor(RGB color, int alphaNewColor) {
		newColor = color;
		this.alphaNewColor = alphaNewColor;
		updatePreview();
	}

	/**
	 * Set the old color
	 * 
	 * @param color rgb of the old color
	 * @param alphaNewColor alpha of the old color
	 */
	public void setOldColor(RGB color, int alphaOldColor) {
		oldColor = color;
		this.alphaOldColor = alphaOldColor;
		boolean labelVisible = oldColor != null;
		((GridData) oldColorLabel.getLayoutData()).exclude = !labelVisible;
		oldColorLabel.setVisible(labelVisible);
		updatePreview();
	}

	/**
	 * update the preview area with the set old and new color
	 */
	public void updatePreview() {
		if (newColor == null)
			return;
		colorComposite.redraw();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
