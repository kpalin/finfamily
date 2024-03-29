package fi.kaila.suku.util.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.imports.Read2004XML;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Utility to manage database.
 * 
 * @author FIKAAKAIL 25.7.2007
 */
public class SukuUtility {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private static SukuUtility sData = null;

	/**
	 * Singleton requestor of SukuData instance.
	 * 
	 * @return the singleton instance of SukuData
	 */
	public static synchronized SukuUtility instance() {
		if (sData == null) {
			sData = new SukuUtility();
		}
		return sData;

	}

	/**
	 * 
	 * Constructor for SukuData. Connection to database is created here
	 * 
	 * @throws SukuException
	 * 
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private SukuUtility() {
		//
	}

	/**
	 * Execute sql script.
	 * 
	 * @param con
	 *            the con
	 * @param sqlpath
	 *            the sqlpath
	 * @throws SukuException
	 *             the suku exception
	 */
	public void createSukuDb(Connection con, String sqlpath)
			throws SukuException {
		logger.fine("create db from " + sqlpath);
		InputStreamReader in = null;
		Statement stm;
		int lukuri = 0;
		try {
			in = new InputStreamReader(this.getClass().getResourceAsStream(
					sqlpath), "UTF-8");

			logger.fine("create script at " + in);
			// } catch (UnsupportedEncodingException e1) {
			//
			// logger.log(Level.WARNING, "create", e1);
			// throw new SukuException(e1);
			// }
			//
			//
			// try {
			stm = con.createStatement();

			boolean wasDash = false;
			boolean wasDashDash = false;
			StringBuilder sb = new StringBuilder();

			char c;
			int datal = 0;

			boolean isPastBOM = false;
			while (datal > -1) {
				while ((datal = in.read()) != -1) {
					if (!isPastBOM) {
						if (datal == 65279) {
							datal = in.read();
						}
					}
					isPastBOM = true;
					c = (char) datal;
					if (wasDashDash) {
						if (c == '\n') {
							wasDashDash = false;
							wasDash = false;
						}
					} else if (!wasDash && c == '-') {
						wasDash = true;

					} else if (wasDash && c == '-') {
						wasDashDash = true;
						wasDash = false;
					} else if (c == ';') {
						break;

					} else {
						if (wasDash) {
							sb.append('-');
						}
						wasDash = false;
						sb.append(c);
					}
				}

				String sql = sb.toString();

				try {
					stm.executeUpdate(sql);
					lukuri++;
				} catch (SQLException se) {
					logger.severe(se.getMessage());
				}
				sb = new StringBuilder();

			}
			logger.fine("creates script with  " + lukuri + " sql commands");
		} catch (Exception e) {
			logger.log(Level.WARNING, "create", e);
			throw new SukuException(e);
		} finally {
			try {
				in.close();
			} catch (IOException ignored) {
				// IOException ignored
			}
		}
	}

	/**
	 * import Suku 2004 backup file.
	 * 
	 * @param con
	 *            the con
	 * @param path
	 *            the path
	 * @param oldCode
	 *            the old code
	 * @return Read2004XML class
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData import2004Data(Connection con, String path, String oldCode)
			throws SukuException {
		Read2004XML x = new Read2004XML(con, oldCode);
		return x.importFile(path);
	}

}
