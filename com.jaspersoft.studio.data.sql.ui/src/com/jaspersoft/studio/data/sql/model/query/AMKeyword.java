/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.model.query;

import net.sf.jasperreports.engine.JRConstants;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.RGB;

import com.jaspersoft.studio.data.sql.model.MDBObjects;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.preferences.fonts.utils.BoldStyler;
import com.jaspersoft.studio.preferences.fonts.utils.FontUtils;
import com.jaspersoft.studio.utils.UIUtil;

public class AMKeyword extends MDBObjects implements IQueryString {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String SELECT_KEYWORD = "SELECT";
	public static final String SELECT_DISTINCT_KEYWORD = "SELECT DISTINCT";

	public static final String SET_OPERATOR_UNION = "UNION";
	public static final String SET_OPERATOR_UNION_ALL = "UNION ALL";
	public static final String SET_OPERATOR_INTERSECT = "INTERSECT";
	public static final String SET_OPERATOR_MINUS = "MINUS";
	public static final String SET_OPERATOR_EXCEPT = "EXCEPT";

	public static final String AND_OPERATOR = "AND";
	public static final String OR_OPERATOR = "OR";
	public static final String[] CONDITIONS = new String[] { AND_OPERATOR, OR_OPERATOR };

	public static final String ALIAS_KEYWORD = "AS";
	public static final String ASCENDING_KEYWORD = " ASC";
	public static final String DESCENDING_KEYWORD = " DESC";
	public static final String[] ALIAS_KEYWORDS = new String[] { ALIAS_KEYWORD, " " };

	public static final String INNER_JOIN = "INNER JOIN";
	public static final String LEFT_OUTER_JOIN = "LEFT OUTER JOIN";
	public static final String CROSS_JOIN = "CROSS JOIN";
	public static final String FULL_OUTER_JOIN = "FULL OUTER JOIN";
	public static final String RIGHT_OUTER_JOIN = "RIGHT OUTER JOIN";
	public static final String[] JOIN_KEYWORDS = new String[] { INNER_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN,
			FULL_OUTER_JOIN, CROSS_JOIN };

	public AMKeyword(ANode parent, String value, String image) {
		super(parent, value, image);
	}

	public AMKeyword(ANode parent, String value, String image, int index) {
		super(parent, value, image, index);
	}

	@Override
	public StyledString getStyledDisplayText() {
		return new StyledString(getDisplayText(), FontUtils.getKeywordStyler());
	}

	protected boolean noSqlIfEmpty = false;

	@Override
	public String toSQLString() {
		if (noSqlIfEmpty && getChildren().isEmpty())
			return "";
		return "\n" + getValue() + " ";
	}

}
