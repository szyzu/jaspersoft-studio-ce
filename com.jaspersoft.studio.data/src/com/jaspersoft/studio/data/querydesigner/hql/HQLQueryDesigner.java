/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.querydesigner.hql;

import com.jaspersoft.studio.data.querydesigner.sql.SimpleSQLQueryDesigner;
import com.jaspersoft.studio.data.querydesigner.sql.text.SQLLineStyler;

/**
 * Simple query designer for HQL (hibernate) that provides syntax highlighting.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class HQLQueryDesigner extends SimpleSQLQueryDesigner {
	
	@Override
	protected SQLLineStyler getSQLBasedLineStyler() {
		return new HQLLineStyler();
	}

}
