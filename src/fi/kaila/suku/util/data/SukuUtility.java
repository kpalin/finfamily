package fi.kaila.suku.util.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import fi.kaila.suku.imports.Read2004XML;
import fi.kaila.suku.util.SukuException;

/**
 * Utility to manage database
 * 
 * @author FIKAAKAIL 25.7.2007
 * 
 * 
 */
public class SukuUtility {

	// private String dbDriver="org.postgresql.Driver";
	// private String
	// dbConne="jdbc:postgresql://localhost/sukuproto?user=kalle&password=kalle";
	//
	// Connection con=null;

	// public static BufferedImage womanxIcon=null;
	// public static BufferedImage manIcon;
	// public static BufferedImage unknownIcon;

	private static SukuUtility sData = null;

	/**
	 * Singleton requestor of SukuData instance
	 * 
	 * @return the sinlgeton instance of SukuData
	 * @throws SukuException
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
	 * Execute sql script
	 * 
	 * @param con
	 * @throws SukuException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void createSukuDb(Connection con) throws SukuException {

		InputStreamReader in = null;
		try {
			in = new InputStreamReader(this.getClass().getResourceAsStream(
					"/sql/finfamily.sql"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {

			e1.printStackTrace();
			throw new SukuException(e1);
		}

		Statement stm;
		try {
			stm = con.createStatement();

			boolean wasDash = false;
			boolean wasDashDash = false;
			StringBuffer sb = new StringBuffer();

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

				// System.out.println("sql:"+sql);
				stm.executeUpdate(sql);
				sb = new StringBuffer();

			}
		} catch (Exception e) {
			throw new SukuException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}

		}
	}

	/**
	 * import Suku 2004 backup file
	 * 
	 * @param con
	 * 
	 * @param path
	 * @param oldCode
	 * @return Read2004XML class
	 * @throws ParserConfigurationException
	 * @throws TransformerConfigurationException
	 * @throws SukuException
	 */
	public Read2004XML import2004Data(Connection con, String path,
			String oldCode) throws SukuException {
		Read2004XML x = new Read2004XML(path, con, oldCode);
		x.importFile();
		return x;
	}

}
