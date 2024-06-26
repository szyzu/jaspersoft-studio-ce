/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.ui.metadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.eclipse.util.Misc;

public class SchemaUtil {
	public static String[] getSchemaPath(Connection c) {
		if (c == null)
			return null;
		try {
			Method m = c.getClass().getMethod("getSchema", new Class<?>[0]);
			if (m != null) {
				String schema = (String) m.invoke(c, new Object[0]);
				if (!Misc.isNullOrEmpty(schema))
					return new String[] { schema };
			}
		} catch (Throwable e) {
		}
		String[] res = null;
		try {
			String dbproduct = c.getMetaData().getDatabaseProductName();
			System.out.println(dbproduct);
			if (dbproduct.equalsIgnoreCase("Oracle")) {
				try {
					Method m = c.getClass().getMethod("setIncludeSynonyms", boolean.class);
					if (m != null) {
						m.setAccessible(true);
						m.invoke(c, true);
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
				return runSchemaQuery(c, "select sys_context('USERENV', 'CURRENT_SCHEMA') from dual");
				// else if (dbproduct.equalsIgnoreCase("SQL Anywhere"))
				// return runSchemaQuery(c, "select db_name()");
			} else if (dbproduct.equalsIgnoreCase("PostgreSQL")) {
				Statement stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SHOW search_path");
				while (rs.next()) {
					String str = rs.getString(1);
					res = str.split(",");
				}
				rs.close();
				stmt.close();
			} else if (dbproduct.equalsIgnoreCase("Apache Hive") || dbproduct.equalsIgnoreCase("ApacheHive")) {
				return runSchemaQuery(c, "SELECT current_database()");
			} else if (dbproduct.equalsIgnoreCase("Impala") || dbproduct.equalsIgnoreCase("Spark SQL")) {
				List<String> schemas = new ArrayList<>();
				ResultSet rs = c.getMetaData().getSchemas();
				try {
					while (rs.next())
						schemas.add(rs.getString("TABLE_SCHEM"));
					res = schemas.toArray(new String[schemas.size()]);
				} finally {
					SchemaUtil.close(rs);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public static String getMetadataAllSymbol(DatabaseMetaData dbmeta) throws SQLException {
		String dbproduct = dbmeta.getDatabaseProductName();
		if (dbproduct.equalsIgnoreCase("Apache Hive") || dbproduct.equalsIgnoreCase("Impala")
				|| dbproduct.equalsIgnoreCase("ApacheHive"))
			return null;
		return "%";
	}

	protected static String[] runSchemaQuery(Connection c, String query) throws SQLException {
		List<String> paths = new ArrayList<>();
		try (Statement stmt = c.createStatement()) {
			try (ResultSet rs = stmt.executeQuery(query)) {
				while (rs.next()) {
					String str = rs.getString(1);
					paths.add(str);
					System.out.println(str);
				}
			}
		}
		if (!paths.isEmpty())
			return paths.toArray(new String[paths.size()]);
		return null;
	}

	public static void close(Connection c) {
		if (c != null)
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public static void close(ResultSet rs) {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
