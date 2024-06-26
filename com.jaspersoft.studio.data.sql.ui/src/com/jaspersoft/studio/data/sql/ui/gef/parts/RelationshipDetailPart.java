/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.ui.gef.parts;

import java.util.List;

import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.swt.SWT;

import com.jaspersoft.studio.data.sql.QueryWriter;
import com.jaspersoft.studio.data.sql.SQLQueryDesigner;
import com.jaspersoft.studio.data.sql.action.table.EditTableJoin;
import com.jaspersoft.studio.data.sql.model.query.AMKeyword;
import com.jaspersoft.studio.data.sql.model.query.IQueryString;
import com.jaspersoft.studio.data.sql.model.query.expression.MExpression;
import com.jaspersoft.studio.data.sql.model.query.expression.MExpressionGroup;
import com.jaspersoft.studio.data.sql.model.query.from.MFromTableJoin;
import com.jaspersoft.studio.data.sql.model.query.from.TableJoinDetail;
import com.jaspersoft.studio.data.sql.model.query.operand.AOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.FieldOperand;
import com.jaspersoft.studio.data.sql.model.query.subquery.MQueryTable;
import com.jaspersoft.studio.data.sql.ui.gef.SQLQueryDiagram;
import com.jaspersoft.studio.data.sql.ui.gef.anchors.LateralAnchor;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.util.ModelVisitor;

public class RelationshipDetailPart extends AbstractConnectionEditPart {

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
	}

	@Override
	public TableJoinDetail getModel() {
		return (TableJoinDetail) super.getModel();
	};

	@Override
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())) {
			SQLQueryDesigner designer = (SQLQueryDesigner) getViewer().getProperty(SQLQueryDiagram.SQLQUERYDIAGRAM);
			EditTableJoin ct = designer.getOutline().getAfactory().getAction(EditTableJoin.class);
			MFromTableJoin tJoin = getModel().getMFromTableJoin();
			if (ct.calculateEnabled(new Object[] { tJoin }))
				ct.run();
		}
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		PolylineConnection conn = (PolylineConnection) super.createFigure();
		conn.setConnectionRouter(new BendpointConnectionRouter());
		conn.setForegroundColor(UIUtils.getSystemColor(SWT.COLOR_BLACK));
		conn.setBackgroundColor(UIUtils.getSystemColor(SWT.COLOR_BLACK));
		return conn;
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		TableJoinDetail m = getModel();
		MFromTableJoin tJoin = m.getMFromTableJoin();
		PolylineConnection f = (PolylineConnection) getFigure();
		setupToolTip(tJoin, f);
		if (tJoin.getJoin().equals(AMKeyword.INNER_JOIN)) {
			f.setTargetDecoration(getInnerDecoration());
			f.setSourceDecoration(getInnerDecoration());
		} else if (tJoin.getJoin().equals(AMKeyword.LEFT_OUTER_JOIN)) {
			f.setTargetDecoration(getOuterDecoration());
			f.setSourceDecoration(getInnerDecoration());
		} else if (tJoin.getJoin().equals(AMKeyword.RIGHT_OUTER_JOIN)) {
			f.setTargetDecoration(getInnerDecoration());
			f.setSourceDecoration(getOuterDecoration());
		} else if (tJoin.getJoin().equals(AMKeyword.FULL_OUTER_JOIN)) {
			f.setTargetDecoration(getOuterDecoration());
			f.setSourceDecoration(getOuterDecoration());
		} else if (tJoin.getJoin().equals(AMKeyword.CROSS_JOIN)) {
			f.setTargetDecoration(getCrossDecoration());
			f.setSourceDecoration(getCrossDecoration());
		}
		if (m.getExpr() != null) {
			List<AOperand> ops = m.getExpr().getOperands();
			IFigure fcol = getColumnFigure(getSource(), ops);
			if (fcol != null)
				f.setSourceAnchor(new LateralAnchor(fcol));
			fcol = getColumnFigure(getTarget(), ops);
			if (fcol != null)
				f.setTargetAnchor(new LateralAnchor(fcol));
		} else {

		}
	}

	private void setupToolTip(MFromTableJoin tJoin, PolylineConnection f) {
		final StringBuffer tt = new StringBuffer();
		if (tJoin != null && tJoin.getValue() instanceof MQueryTable)
			tt.append(tJoin.toSQLString() + QueryWriter.writeQuery(tJoin));
		else {
			tt.append(tJoin.getToolTip());
			new ModelVisitor<Object>(tJoin) {

				@Override
				public boolean visit(INode n) {
					if (n instanceof MExpression || n instanceof MExpressionGroup) {
						tt.append(((IQueryString) n).toSQLString());
						return true;
					}
					return false;
				}
			};
		}
		f.setToolTip(new Label(tt.toString().trim()));
	}

	private IFigure getColumnFigure(EditPart ep, List<AOperand> ops) {
		if (ep instanceof TableEditPart) {
			TableEditPart tep = (TableEditPart) ep;
			for (Object p : tep.getChildren())
				if (p instanceof ColumnEditPart) {
					ColumnEditPart fo = (ColumnEditPart) p;
					for (AOperand o : ops)
						if (o instanceof FieldOperand && ((FieldOperand) o).getMColumn().equals(fo.getModel())) {
							return fo.getFigure();
						}
				}
		}
		return null;
	}

	public static RotatableDecoration getInnerDecoration() {
		CircleDecoration srcpd = new CircleDecoration();
		srcpd.setBackgroundColor(ColorConstants.black);
		return srcpd;
	}

	public static RotatableDecoration getOuterDecoration() {
		CircleDecoration srcpd = new CircleDecoration();
		srcpd.setBackgroundColor(ColorConstants.white);
		return srcpd;
	}

	public static RotatableDecoration getCrossDecoration() {
		CircleDecoration srcpd = new CircleDecoration();
		srcpd.setBackgroundColor(ColorConstants.lightGray);
		return srcpd;
	}

	public static class CircleDecoration extends Ellipse implements RotatableDecoration {
		private static final int RADIUS = 3;
		private Point location = new Point();

		public CircleDecoration() {
			super();
		}

		@Override
		public void setLocation(Point p) {
			location = p;
			int d = RADIUS * 2;
			Rectangle bounds = new Rectangle(location.x - RADIUS, location.y - RADIUS, d, d);
			setBounds(bounds);
		}

		@Override
		public void setReferencePoint(Point p) {
			double d = Math.sqrt(Math.pow((location.x - p.x), 2) + Math.pow(location.y - p.y, 2));
			if (d < RADIUS)
				return;

			double k = (d - RADIUS) / d;
			int x = (int) (k * Math.abs(p.x - location.x));
			int y = (int) (k * Math.abs(p.y - location.y));
			int rx = location.x < p.x ? p.x - x : p.x + x;
			int ry = location.y > p.y ? p.y + y : p.y - y;
			setBounds(new Rectangle(rx - RADIUS, ry - RADIUS, (int) (RADIUS * 2.5), (int) (RADIUS * 2.5)));
		}
	}
}
