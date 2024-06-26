/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.view.report.html;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener;

import net.sf.jasperreports.eclipse.util.Misc;

public class URLContributionItem extends ContributionItem implements PropertyChangeListener, Listener {

	private Text txt;
	private ToolItem toolitem;
	private IPartListener partListener;
	private String url;

	public URLContributionItem(String url) {
		super("urlitem");
		this.url = url;
	}

	public void setUrl(String url) {
		this.url = url;
		refresh();
	}

	/**
	 * Computes the width required by control
	 * 
	 * @param control
	 *          The control to compute width
	 * @return int The width required
	 */
	protected int computeWidth(Control control) {
		return control.computeSize(500, SWT.DEFAULT, true).x;
	}

	/**
	 * Creates and returns the control for this contribution item under the given parent composite.
	 * 
	 * @param parent
	 *          the parent composite
	 * @return the new control
	 */
	protected Control createControl(final Composite parent) {
		txt = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		txt.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				handleWidgetDefaultSelected(e);
			}
		});
		txt.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				// do nothing
			}

			public void focusLost(FocusEvent e) {
				refresh();
			}
		});
		txt.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				txt.setSelection(0, txt.getText().length());
			}
			
		});
		
		//This listener dynamically set the size of the text item when 
		//the toolbar is resized
		final ControlAdapter resizeListener = new ControlAdapter() {
			
			@Override
			public void controlResized(ControlEvent e) {
				if (!parent.isDisposed() && !txt.isDisposed()) {
					refresh();
					toolitem.setWidth(computeWidth(txt));
					txt.pack();
				}
			}
			
		};
		
		//This will remove the resize listener from the toolbar
		//when it is disposed
		DisposeListener disposeListener = new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				parent.removeControlListener(resizeListener);
			}
		};
		parent.addDisposeListener(disposeListener);
		txt.addDisposeListener(disposeListener);
		parent.addControlListener(resizeListener);
		
		refresh();
		toolitem.setWidth(computeWidth(txt));
		txt.pack();
		return txt;
	}

	/**
	 * @see org.eclipse.jface.action.ContributionItem#dispose()
	 */
	@Override
	public void dispose() {
		if (partListener == null)
			return;

		txt = null;
		partListener = null;
	}

	/**
	 * The control item implementation of this <code>IContributionItem</code> method calls the <code>createControl</code>
	 * framework method. Subclasses must implement <code>createControl</code> rather than overriding this method.
	 * 
	 * @param parent
	 *          The parent of the control to fill
	 */
	@Override
	public final void fill(Composite parent) {
		createControl(parent);
	}

	/**
	 * The control item implementation of this <code>IContributionItem</code> method throws an exception since controls
	 * cannot be added to menus.
	 * 
	 * @param parent
	 *          The menu
	 * @param index
	 *          Menu index
	 */
	@Override
	public final void fill(Menu parent, int index) {
		Assert.isTrue(false, "Can't add a control to a menu");//$NON-NLS-1$
	}

	/**
	 * The control item implementation of this <code>IContributionItem</code> method calls the <code>createControl</code>
	 * framework method to create a control under the given parent, and then creates a new tool item to hold it.
	 * Subclasses must implement <code>createControl</code> rather than overriding this method.
	 * 
	 * @param parent
	 *          The ToolBar to add the new control to
	 * @param index
	 *          Index
	 */
	@Override
	public void fill(ToolBar parent, int index) {
		if (toolitem == null || txt == null || txt.isDisposed()) {
			toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
			Control control = createControl(parent);
			toolitem.setControl(control);
		} else
			toolitem.setWidth(computeWidth(txt));
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
	 */
	private void handleWidgetDefaultSelected(SelectionEvent event) {
		refresh();
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
	 */
	private void handleWidgetSelected(SelectionEvent event) {
		// forceSetText = true;
		handleWidgetDefaultSelected(event);
		// forceSetText = false;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.FocusIn:
			refresh();
			break;
		case SWT.Selection:
		case SWT.DefaultSelection:
			// onSelection();
			break;
		}
	}

	public void refresh() {
		if (txt == null || txt.isDisposed())
			return;
		// $TODO GTK workaround
		try {
			String strurl = Misc.nvl(url);
			txt.setText(strurl);
			txt.setToolTipText(strurl);
		} catch (SWTException exception) {
			if (!SWT.getPlatform().equals("gtk")) //$NON-NLS-1$
				throw exception;
		}
	}

	public void setEnabled(boolean enabled) {
		txt.setEnabled(enabled);
	}

}
