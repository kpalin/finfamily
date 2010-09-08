package fi.kaila.suku.util.local;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.util.SukuException;

/**
 * Help for login routine.
 * 
 * @author Kalle
 */
public class LocalDatabaseUtility {

	private static Logger logger = Logger.getLogger(LocalDatabaseUtility.class
			.getName());

	/**
	 * Gets the list of databases.
	 * 
	 * @param con
	 *            the con
	 * @return list of available databases
	 * @throws SukuException
	 *             the suku exception
	 */
	public static String[] getListOfDatabases(Connection con)
			throws SukuException {

		String sql = "select datname from pg_database where datname not in ('postgres','template1','template0') order by datname ";
		StringBuilder sb = new StringBuilder();
		try {
			Statement stm = con.createStatement();

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				if (sb.length() > 0) {
					sb.append(";");
				}
				sb.append(rs.getString(1));
			}
			rs.close();

			return sb.toString().split(";");

		} catch (SQLException e) {
			logger.log(Level.WARNING, "databasenames list", e);

			throw new SukuException(e);
		}

	}

	/**
	 * Gets the list of users.
	 * 
	 * @param con
	 *            the con
	 * @return list of available users
	 * @throws SukuException
	 *             the suku exception
	 */
	// cmd.CommandText =
	// "select rolname from pg_roles where rolname != 'postgres' ";
	public static String[] getListOfUsers(Connection con) throws SukuException {

		String sql = "select rolname from pg_roles where rolname != 'postgres' order by rolname ";
		StringBuilder sb = new StringBuilder();
		try {
			Statement stm = con.createStatement();

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				if (sb.length() > 0) {
					sb.append(";");
				}
				sb.append(rs.getString(1));
			}
			rs.close();

			return sb.toString().split(";");

		} catch (SQLException e) {
			logger.log(Level.WARNING, "usernames list", e);

			throw new SukuException(e);
		}

	}

	/**
	 * Gets the list of schemas.
	 * 
	 * @param con
	 *            the con
	 * @return the list of schemas
	 * @throws SukuException
	 *             the suku exception
	 */
	public static String[] getListOfSchemas(Connection con)
			throws SukuException {
		String sql = "select * from pg_namespace where nspname not like 'pg%' and nspname <> 'information_schema' ";
		StringBuilder sb = new StringBuilder();
		try {
			Statement stm = con.createStatement();

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				if (sb.length() > 0) {
					sb.append(";");
				}
				sb.append(rs.getString(1));
			}
			rs.close();

			return sb.toString().split(";");

		} catch (SQLException e) {
			logger.log(Level.WARNING, "schemas list", e);

			throw new SukuException(e);
		}

	}

	/**
	 * Sets the schema.
	 * 
	 * @param con
	 *            the con
	 * @param schema
	 *            the schema
	 * @return the string
	 */
	public static String setSchema(Connection con, String schema) {
		Statement stm;
		String resu = null;
		try {
			stm = con.createStatement();
			stm.executeUpdate("set search_path to " + schema);
			stm.close();
		} catch (SQLException e) {
			resu = e.getMessage();
			e.printStackTrace();
		}
		return resu;
	}

	/**
	 * Creates the new schema.
	 * 
	 * @param con
	 *            the con
	 * @param schema
	 *            the schema
	 * @return the string
	 */
	public static String createNewSchema(Connection con, String schema) {
		String resu = null;
		Statement stm;
		try {
			stm = con.createStatement();
			stm.executeUpdate("create schema " + schema);
			stm.close();
		} catch (SQLException e) {
			resu = e.getMessage();
			e.printStackTrace();
		}
		return resu;
	}

	/**
	 * Drop named schema from current database.
	 * 
	 * @param con
	 *            the con
	 * @param name
	 *            the name
	 * @return possible error string
	 */
	public static String dropSchema(Connection con, String name) {
		String resu = null;
		Statement stm;
		// if (name.equals("public")) {
		// return Resurses.getString("SCHEMA_PUBLIC_NOT_DROPPED");
		// }
		try {
			stm = con.createStatement();
			stm.executeUpdate("drop schema " + name + " cascade");
			stm.executeUpdate("set search_path to public ");
			stm.close();
		} catch (SQLException e) {
			resu = e.getMessage();
			e.printStackTrace();
		}
		return resu;
	}

}
