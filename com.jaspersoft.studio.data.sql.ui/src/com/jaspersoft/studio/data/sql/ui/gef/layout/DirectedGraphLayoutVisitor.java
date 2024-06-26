/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.ui.gef.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class DirectedGraphLayoutVisitor {
	private static final Insets PADDING = new Insets(15, 15, 15, 15);
	private Map<AbstractGraphicalEditPart, Object> partToNodesMap;
	private DirectedGraph graph;

	public void layoutDiagram(AbstractGraphicalEditPart diagram) {
		partToNodesMap = new HashMap<AbstractGraphicalEditPart, Object>();
		graph = new DirectedGraph();
		graph.setDirection(PositionConstants.EAST);
		addNodes(diagram);
		Rectangle r = diagram.getFigure().getBounds();
		if (r.x < -1000 || r.y < -1000)
			return;
		if (graph.nodes.size() > 0) {
			addEdges(diagram);
			new NodeJoiningDirectedGraphLayout().visit(graph);
			applyChildrenResults(diagram);
		}
	}

	protected void addNodes(AbstractGraphicalEditPart diagram) {
		for (Object obj : diagram.getChildren()) {
			AbstractGraphicalEditPart part = (AbstractGraphicalEditPart) obj;
			Node n = new Node(part);
			Dimension psize = part.getFigure().getPreferredSize(400, 300);
			n.width = psize.width;
			n.height = psize.height;
			n.setPadding(PADDING);
			partToNodesMap.put(part, n);
			graph.nodes.add(n);
		}
	}

	protected void addEdges(AbstractGraphicalEditPart diagram) {
		for (Object obj : diagram.getChildren()) {
			AbstractGraphicalEditPart part = (AbstractGraphicalEditPart) obj;
			for (Object item : part.getSourceConnections()) {
				AbstractConnectionEditPart rpart = (AbstractConnectionEditPart) item;
				Node source = (Node) partToNodesMap.get(rpart.getSource());
				Node target = (Node) partToNodesMap.get(rpart.getTarget());
				if (source != null && target != null) {
					Edge e = new Edge(rpart, source, target);
					e.weight = 2;
					graph.edges.add(e);
					partToNodesMap.put(rpart, e);
				}
			}
		}
	}

	protected void applyChildrenResults(AbstractGraphicalEditPart diagram) {
		List<AbstractGraphicalEditPart> children = diagram.getChildren();
		Rectangle r = diagram.getFigure().getBounds();
		for (AbstractGraphicalEditPart tp : children) {
			Node n = (Node) partToNodesMap.get(tp);
			IFigure f = tp.getFigure();

			Dimension psize = f.getPreferredSize();
			f.setBounds(new Rectangle(r.x + n.x, r.y + n.y, psize.width,
					psize.height));

			List<AbstractConnectionEditPart> sc = tp.getSourceConnections();
			for (AbstractConnectionEditPart rp : sc)
				applyResults(rp);
		}
	}

	protected void applyResults(AbstractConnectionEditPart relationshipPart) {
		Edge e = (Edge) partToNodesMap.get(relationshipPart);
		if (e == null)
			return;
		NodeList nodes = e.vNodes;

		PolylineConnection conn = (PolylineConnection) relationshipPart
				.getConnectionFigure();
		if (nodes != null) {
			List<AbsoluteBendpoint> bends = new ArrayList<AbsoluteBendpoint>();
			for (int i = 0; i < nodes.size(); i++) {
				Node vn = nodes.getNode(i);
				int x = vn.x;
				int y = vn.y;
				if (e.isFeedback()) {
					bends.add(new AbsoluteBendpoint(x, y + vn.height));
					bends.add(new AbsoluteBendpoint(x, y));
				} else {
					bends.add(new AbsoluteBendpoint(x, y));
					bends.add(new AbsoluteBendpoint(x, y + vn.height));
				}
			}
			conn.setRoutingConstraint(bends);
		} else {
			conn.setRoutingConstraint(Collections.EMPTY_LIST);
		}

	}
}
