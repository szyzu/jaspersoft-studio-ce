/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.gef.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import com.jaspersoft.studio.background.MBackgrounImage;
import com.jaspersoft.studio.callout.MCallout;
import com.jaspersoft.studio.callout.pin.MPinConnection;
import com.jaspersoft.studio.editor.gef.figures.APageFigure;
import com.jaspersoft.studio.editor.gef.figures.ReportPageFigure;
import com.jaspersoft.studio.editor.gef.parts.band.NotMovablePartDragTracker;
import com.jaspersoft.studio.editor.gef.rulers.ReportRuler;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.IGraphicElement;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.model.band.MBand;
import com.jaspersoft.studio.preferences.DesignerPreferencePage;
import com.jaspersoft.studio.property.dataset.dialog.IDatasetDialogSupport;
import com.jaspersoft.studio.utils.ModelUtils;

/*
 * The Class PageEditPart.
 * 
 * @author Chicu Veaceslav
 */
public class ReportPageEditPart extends PageEditPart implements PropertyChangeListener, IDatasetDialogSupport {
	
	protected APageFigure newPageFigure() {
		return new ReportPageFigure(getJasperDesign(), true, this);
	}

	@Override
	public DragTracker getDragTracker(Request request) {
		return new NotMovablePartDragTracker(this);
	}

	/**
	 * Setup page figure.
	 * 
	 * @param jd
	 *          the jasper design
	 * @param figure2
	 *          the figure2
	 */
	protected void setupPageFigure(APageFigure figure2) {
		JasperDesign jd = getJasperDesign();
		List<JRBand> bands = ModelUtils.getAllBands(jd);
		int dh = ModelUtils.getDesignHeight(bands);
		int designHeight = dh + jd.getTopMargin() + jd.getBottomMargin();

		int w = jd.getPageWidth() + 20;
		int h = designHeight + 10;

		ReportPageFigure rpFig = (ReportPageFigure) figure2;
		rpFig.setBandsHeight(designHeight);

		setupPagePreferences(figure2);
		figure2.setSize(w, h);
	}

	@Override
	protected void setupPagePreferences(APageFigure figure2) {
		String mcolor = jConfig.getProperty(DesignerPreferencePage.P_PAGE_MARGIN_COLOR,
				DesignerPreferencePage.DEFAULT_MARGINCOLOR);
		RGB rgb = StringConverter.asRGB(mcolor);
		((ReportPageFigure) figure2).setPrintMarginColor(new java.awt.Color(rgb.red, rgb.green, rgb.blue));
		super.setupPagePreferences(figure2);
	}

	public void updateRullers() {
		JasperDesign jd = getJasperDesign();

		List<JRBand> bands = ModelUtils.getAllBands(jd);
		int dh = ModelUtils.getDesignHeight(bands);
		int tx = jd.getLeftMargin() + ReportPageFigure.PAGE_BORDER.left;
		int ty = jd.getTopMargin() + ReportPageFigure.PAGE_BORDER.top;

		getViewer().setProperty(ReportRuler.PROPERTY_HOFFSET, tx);
		getViewer().setProperty(ReportRuler.PROPERTY_VOFFSET, ty);
		getViewer().setProperty(ReportRuler.PROPERTY_HEND, jd.getPageWidth() - jd.getLeftMargin() - jd.getRightMargin());
		getViewer().setProperty(ReportRuler.PROPERTY_VEND, dh);

		getViewer().setProperty(SnapToGrid.PROPERTY_GRID_ORIGIN,
				new Point(tx, ReportPageFigure.PAGE_BORDER.top + jd.getTopMargin()));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List<Object> getModelChildren() {
		List<Object> list = new ArrayList<Object>();
		List<Object> sList = new ArrayList<Object>();
		for (INode node : getPage().getChildren()) {
			//The background element is always the first one
			if (node instanceof MBackgrounImage) {
				list.add(0, node);
			} else if (node instanceof MCallout) {
				sList.add(node);
				for (INode n : node.getChildren()){
					//the connection must not be returned, since their edit part 
					//must not be created trough the edit part factory but from the createConnection
					//method of the Pin/Callout edit part
					if (!(n instanceof MPinConnection)) {
						sList.add(n);
					}
				}
			} else if (node instanceof IGraphicElement && node.getValue() != null) {
				if (node instanceof MBand) {
					MBand band = (MBand) node;
					list.add(band);
					getNodeChildren(node, sList);
					continue;
				}
				sList.add(node);
			}
		}
		list.addAll(sList);
		return list;
	}

	/**
	 * Gets the node children that need to be shown
	 * 
	 * @param node
	 *          the node
	 * @param list
	 *          the list
	 * @return the node children
	 */
	private void getNodeChildren(INode node, List<Object> list) {
		if (node.showChildren()){
			for (INode nod : node.getChildren()) {
				if (nod instanceof IGraphicElement)
					list.add(nod);
				getNodeChildren(nod, list);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getSource() instanceof MReport) {
			ANode model = (ANode) getModel();
			if (model.getChildren() != null)
				for (Object node : getModelChildren()) {
					if (node instanceof INode) {
						EditPart ep = (EditPart) getViewer().getEditPartRegistry().get(node);
						if (ep instanceof PropertyChangeListener)
							((PropertyChangeListener) ep).propertyChange(arg0);
					}
				}
		}
		super.propertyChange(arg0);
	}

}
