/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.properties.view;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Represents a section of properties for a given input.
 * <p>
 * The lifecycle of an ISection is as follows:
 * <ul>
 * <li><code>ISection.createControls()</code></li>
 * <li><code>ISection.setInput()</code></li>
 * <li><code>ISection.aboutToBeShown()</code></li>
 * <li><code>ISection.refresh()</code></li>
 * <li><code>ISection.aboutToBeHidden()</code></li>
 * <li><code>ISection.dispose()</code></li>
 * </ul>
 * </p>
 * <p>
 * Implementors of this class should be aware that a section instance might be
 * reused for different input objects (as long as they are valid section
 * inputs). It means that <code>ISection.setInput</code> can be called at any
 * time between <code>ISection.createControls</code> and
 * <code>ISection.dispose</code>.
 * </p>
 * <p>
 * When an input change event occurs, such as a tab selection or a workbench
 * selection change, an ISection is sent:
 * <ul>
 * <li><code>ISection.setInput()</code></li>
 * <li><code>ISection.refresh()</code></li>
 * </ul>
 * </p>
 * <p>
 * When an part activation event occurs, such as the contributor part activation
 * event, an ISection is sent:
 * <ul>
 * <li><code>ISection.setInput()</code></li>
 * <li><code>ISection.aboutToBeShown()</code></li>
 * <li><code>ISection.refresh()</code></li>
 * <li><code>ISection.setInput()</code></li>
 * <li><code>ISection.refresh()</code></li>
 * </ul>
 * This is because both a tab selection event and an input selection event have
 * occurred.
 * </p>
 * <p>
 * This interface should not be extended or implemented. New section instances
 * should be created using <code>AbstractPropertySection</code>.
 * </p>
 * 
 * @see org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage
 * 
 * @author Anthony Hunter
 */
public interface ISection {

	/**
	 * Creates the controls for the section.
	 * <p>
	 * Clients should take advantage of the widget factory provided by the
	 * framework to achieve a common look between property sections.
	 * </p>
	 * @see org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage#getWidgetFactory()
	 * 
	 * @param parent
	 *            the parent composite for the section.
	 * @param tabbedPropertySheetPage
	 *            the tabbed property sheet page.
	 */
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage);

	/**
	 * Notifies the section that the workbench selection has changed. 
	 * @param part The active workench part.
	 * @param selection The active selection in the workbench part.
	 */
	public void setInput(IWorkbenchPart part, ISelection selection);

	/**
	 * Notifies the section that its controls are about to be shown. It is
	 * expected that sections enable domain related functions in this method,
	 * most commonly add listeners.
	 * <p>
	 * Since the controls are not visible, the section should wait for the
	 * refresh() before updating the section controls.
	 * </p>
	 */
	public void aboutToBeShown();

	/**
	 * Notifies the section that its controls are about to be hidden. It is
	 * expected that sections disable domain related functions in this method,
	 * most commonly remove listeners.
	 */
	public void aboutToBeHidden();

	/**
	 * Dispose this section.
	 */
	public void dispose();

	/**
	 * Returns the minimum height needed by this section. A return value of
	 * <code>SWT.DEFAULT</code> indicates that no minimum height is defined.
	 * 
	 * @return the minimum height needed by this section.
	 */
	public int getMinimumHeight();

	/**
	 * Determine whether this section would like extra height space in case
	 * there is some left. Normally this is true when the section is the last to
	 * be displayed on a tab or is the only section on a tab.
	 * @return <code>true</code> if this section would like extra height space.
	 */
	public boolean shouldUseExtraSpace();
	
	/**
	 * Return if the current section provide dynamic content. This
	 * will disable some optimization since even if the showed section
	 * is the same, since the content is dynamic the size of the page must 
	 * be recalculated
	 * 
	 * @return true if the content shown in the section can change after its creation
	 * false otherwise
	 */
	public boolean hasDynamicContent();

	/**
	 * Refresh the contents of the controls displayed in this section.
	 */
	public void refresh();

	/**
	 * Return  the current element selected in the section
	 * 
	 * @return the selected element, can be null
	 */
	public Object getElement();
}
