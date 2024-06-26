/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.gef.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.gef.decorator.error.ErrorDecorator;
import com.jaspersoft.studio.editor.gef.figures.ComponentFigure;
import com.jaspersoft.studio.editor.gef.figures.FigureFactory;
import com.jaspersoft.studio.editor.gef.figures.ReportPageFigure;
import com.jaspersoft.studio.editor.gef.figures.borders.CornerBorder;
import com.jaspersoft.studio.editor.gef.figures.borders.ElementLineBorder;
import com.jaspersoft.studio.editor.gef.parts.editPolicy.ElementEditPolicy;
import com.jaspersoft.studio.editor.gef.parts.editPolicy.FigurePageLayoutEditPolicy;
import com.jaspersoft.studio.editor.gef.parts.editPolicy.FigureSelectionEditPolicy;
import com.jaspersoft.studio.editor.gef.parts.editPolicy.SearchParentDragTracker;
import com.jaspersoft.studio.editor.gef.rulers.ReportRuler;
import com.jaspersoft.studio.jasper.JSSDrawVisitor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.IContainer;
import com.jaspersoft.studio.model.IGraphicElement;
import com.jaspersoft.studio.preferences.DesignerPreferencePage;
import com.jaspersoft.studio.properties.view.ErrorsDialog;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.engine.design.JRDesignElement;

/*
 * The Class FigureEditPart.
 */
public class FigureEditPart extends AJDEditPart implements PropertyChangeListener, IRulerUpdatable {

	private static final String RECTANGLE = "rectangle";
	protected JSSDrawVisitor drawVisitor;

	public JSSDrawVisitor getDrawVisitor() {
		return drawVisitor;
	}

	private PreferenceListener preferenceListener;

	private final class PreferenceListener implements IPropertyChangeListener {

		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			handlePreferenceChanged(event);
		}

	}

	protected void handlePreferenceChanged(org.eclipse.jface.util.PropertyChangeEvent event) {
		String p = event.getProperty();
		if (p.equals(DesignerPreferencePage.P_ELEMENT_DESIGN_BORDER_STYLE)
				|| p.equals(DesignerPreferencePage.P_ELEMENT_DESIGN_BORDER_COLOR)) {
			pref = null;
			setPrefsBorder(getFigure());
		} else
			refreshVisuals();
	}

	@Override
	public void activate() {
		super.activate();
		preferenceListener = new PreferenceListener();
		JaspersoftStudioPlugin.getInstance().addPreferenceListener(preferenceListener, getAssociatedFile());
		if (getModel() != null)
			getModel().getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		if (preferenceListener != null)
			JaspersoftStudioPlugin.getInstance().removePreferenceListener(preferenceListener);
		if (getModel() != null)
			getModel().getPropertyChangeSupport().removePropertyChangeListener(this);
		super.deactivate();
	}

	public void setDrawVisitor(JSSDrawVisitor drawVisitor) {
		this.drawVisitor = drawVisitor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		ANode model = getModel();
		IFigure rect = FigureFactory.createFigure(model);
		setPrefsBorder(rect);
		setupFigure(rect);
		return rect;
	}

	/**
	 * Instead of the default drag tracker an overridden one is returned, in
	 * this way we can control the edit part targeted from a drag & drop
	 * operation, and if the target is isn't an IContainer then it's parent is
	 * returned Change by Orlandin Marco
	 */
	@Override
	public org.eclipse.gef.DragTracker getDragTracker(org.eclipse.gef.Request request) {
		return new SearchParentDragTracker(this);
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ElementEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new FigureSelectionEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FigurePageLayoutEditPolicy());
	}

	@Override
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType()) && req instanceof SelectionRequest) {
			Point r = ((SelectionRequest) req).getLocation();

			Point tr = figure.getBounds().getTopRight();
			figure.translateToAbsolute(tr);
			if (figure instanceof ComponentFigure
					&& ((ComponentFigure) figure).getDecorator(ErrorDecorator.class) != null
					&& tr.getDistance(r) < 20) {
				ErrorDecorator dec = (ErrorDecorator) ((ComponentFigure) figure).getDecorator(ErrorDecorator.class);

				org.eclipse.swt.graphics.Point p = getViewer().getControl()
						.toDisplay(new org.eclipse.swt.graphics.Point(tr.x, tr.y));
				p.y = p.y - ErrorsDialog.hof;
				new ErrorsDialog().createDialog(null, p, dec.getErrorMessages());

				return;
			}
		}
		super.performRequest(req);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	public void refreshVisuals() {
		Shape rect = (Shape) getFigure();
		if (Display.getCurrent() != null) {
			setupFigure(rect);
			rect.invalidate();
			rect.repaint();
		}
	}

	protected JasperReportsConfiguration jConfig;

	public JasperReportsConfiguration getjConfig() {
		return jConfig;
	}

	private String pref;
	private Color fg;

	public void setPrefsBorder(IFigure rect) {
		if (pref == null) {
			if (jConfig == null)
				jConfig = getModel().getJasperConfiguration();
			pref = jConfig.getProperty(DesignerPreferencePage.P_ELEMENT_DESIGN_BORDER_STYLE, RECTANGLE);
			String mcolor = jConfig.getProperty(DesignerPreferencePage.P_ELEMENT_DESIGN_BORDER_COLOR,
					DesignerPreferencePage.DEFAULT_ELEMENT_DESIGN_BORDER_COLOR);
			fg = SWTResourceManager.getColor(StringConverter.asRGB(mcolor));
		}
		if (pref.equals(RECTANGLE)) // $NON-NLS-1$
			rect.setBorder(new ElementLineBorder(fg));
		else
			rect.setBorder(new CornerBorder(fg, 5));
	}

	/**
	 * Sets the up figure.
	 * 
	 * @param rect the new up figure
	 */
	protected void setupFigure(IFigure rect) {
		ANode model = getModel();
		rect.setToolTip(new Label(model.getToolTip()));
		if (model instanceof IGraphicElement && model.getValue() != null) {
			Rectangle bounds = ((IGraphicElement) model).getBounds();
			int x = bounds.x + ReportPageFigure.PAGE_BORDER.left;
			int y = bounds.y + ReportPageFigure.PAGE_BORDER.top;
			if (model.getValue() instanceof JRDesignElement) {
				JRDesignElement jrElement = (JRDesignElement) model.getValue();
				if (rect instanceof ComponentFigure && drawVisitor != null) {
					ComponentFigure f = (ComponentFigure) rect;
					f.setLocation(new Point(x, y));

					f.setJRElement(jrElement, drawVisitor);
				} else
					rect.setBounds(new Rectangle(x, y, jrElement.getWidth(), jrElement.getHeight()));
			} else {
				rect.setBounds(new Rectangle(x, y, bounds.width, bounds.height));
			}
		}
		if (rect instanceof ComponentFigure)
			JaspersoftStudioPlugin.getDecoratorManager().setupFigure((ComponentFigure) rect, this);
	}

	@Override
	public ANode getModel() {
		return (ANode) super.getModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// not necessary, the refresh of every node is done by the page when a
		// proprty changes
		// refresh();
		// refreshC(getModel());
		// refreshVisuals();
	}

	/**
	 * Refresh c.
	 * 
	 * @param n the n
	 */
	/*
	 * private void refreshC(ANode n) { if (n.getChildren() != null) for (INode
	 * node : n.getChildren()) { EditPart ep = (EditPart)
	 * getViewer().getEditPartRegistry().get(node); if (ep instanceof
	 * FigureEditPart) ((FigureEditPart) ep).refreshVisuals(); refreshC((ANode)
	 * node); } }
	 */

	public void updateRulers() {
		ANode model = getModel().getParent();
		if (model instanceof IGraphicElement && model.getValue() != null) {
			Rectangle bounds = ((IGraphicElement) model).getBounds();
			if (bounds != null) {
				int x = bounds.x + ReportPageFigure.PAGE_BORDER.left;
				int y = bounds.y + ReportPageFigure.PAGE_BORDER.top;

				getViewer().setProperty(ReportRuler.PROPERTY_HOFFSET, x);
				getViewer().setProperty(ReportRuler.PROPERTY_VOFFSET, y);
				getViewer().setProperty(ReportRuler.PROPERTY_HEND, bounds.width); // $NON-NLS-1$
				getViewer().setProperty(ReportRuler.PROPERTY_VEND, bounds.height);// $NON-NLS-1$

				getViewer().setProperty(SnapToGrid.PROPERTY_GRID_ORIGIN, new Point(x, y));
			}
		}
	}

	/**
	 * Return the edit part of the parent model of the parameter model
	 * 
	 * @param childEditPart not null edit part from where the model is read
	 * @return the editpart that contains the model of the parent of the passed
	 * model
	 */
	public static EditPart getParentEditPart(EditPart childEditPart) {
		if (childEditPart != null && childEditPart.getModel() != null) {
			ANode child = (ANode) childEditPart.getModel();
			ANode parentModel = child.getParent();
			// This use the model for the search because every EditPart in the
			// report has the same father.
			for (Object actualChild : childEditPart.getParent().getChildren()) {
				EditPart actualChildPart = (EditPart) actualChild;
				if (parentModel == actualChildPart.getModel())
					return actualChildPart;
			}
		}
		return null;
	}

	/**
	 * Return the editpart target of a drop operation when an element is
	 * released on this edit part some elements doesn't support direct drop
	 * (like table..) because the must be edited in a subeditor So the drop
	 * container is not always the real editpart where an element is dropped.
	 * This is mostly used to show the drop feedback since the generation of the
	 * commands with the correct container is handled from the EdtiPartFactory
	 * of the element where the drop was done
	 * 
	 * @return a not null edit part
	 */
	public EditPart getDropContainer() {
		if (!(this instanceof IContainer)) {
			EditPart parentEditPart = getParentEditPart(this);
			if (parentEditPart != null) {
				return parentEditPart;
			}
		}
		return this;
	}

}
