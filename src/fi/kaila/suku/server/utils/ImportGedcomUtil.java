package fi.kaila.suku.server.utils;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import fi.kaila.suku.imports.ImportGedcomDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * Gedcom import class
 * 
 * @author Kalle
 * 
 */
public class ImportGedcomUtil {

	private Connection con;

	private ImportGedcomDialog runner = null;

	private enum GedSet {
		Set_None, Set_Ascii, Set_Ansel, Set_Utf8, Set_Utf16le, Set_Utf16be
	}

	private GedSet thisSet = GedSet.Set_None;

	/**
	 * Constructor with connection
	 * 
	 * @param con
	 */
	public ImportGedcomUtil(Connection con) {
		this.con = con;
		this.runner = ImportGedcomDialog.getRunner();
	}

	/**
	 * @param file
	 * @param db
	 * @return result in SukuData
	 * @throws SukuException
	 */
	public SukuData importGedcom(String file, String db) throws SukuException {
		SukuData resp = new SukuData();

		Statement stm;
		try {
			int unitCount = 0;
			stm = con.createStatement();
			ResultSet rs = stm.executeQuery("select count(*) from unit");

			if (rs.next()) {
				unitCount = rs.getInt(1);

			}
			rs.close();
			stm.close();
			if (unitCount > 0) {
				resp.resu = Resurses.getString("DATABASE_NOT_EMPTY");
				return resp;
			}

			BufferedInputStream bis = new BufferedInputStream(Suku.kontroller
					.getInputStream());

			long dataLen = Suku.kontroller.getFileLength();
			double dLen = dataLen;
			int data = 0;
			long lukuri = 4;
			if (dataLen < 10) {
				resp.resu = Resurses.getString("GEDCOM_BAD_FORMAT");
				return resp;
			}
			char c;
			int data0 = bis.read();
			int data1 = bis.read();
			int data2 = bis.read();
			int data3 = bis.read();
			StringBuffer line = new StringBuffer();
			if (data0 == 255 && data1 == 254) {
				thisSet = GedSet.Set_Utf16le;
				c = (char) (data3 * 256 + data2);
				line.append(c);
			}
			if (data1 == 255 && data0 == 254) {
				thisSet = GedSet.Set_Utf16be;
				c = (char) (data2 * 256 + data3);
				line.append(c);
			}
			if (data0 == 239 && data1 == 187 && data2 == 191) {
				thisSet = GedSet.Set_Utf8;
				c = (char) data3;
				line.append(c);
			} else {
				c = (char) data0;
				line.append(c);

				c = (char) data1;
				line.append(c);
				c = (char) data2;
				line.append(c);
				c = (char) data3;
				line.append(c);

			}

			System.out.println("GEDC" + data0 + "/" + data1 + "/" + data2 + "/"
					+ data3);
			int datax;
			int level = 0;
			String tag;
			String refId;
			String lineValue;

			while ((data = bis.read()) >= 0) {

				lukuri++;

				switch (thisSet) {
				case Set_Utf16le:
					datax = bis.read();
					c = (char) (datax * 256 + datax);

					break;
				case Set_Utf16be:
					datax = bis.read();
					c = (char) (data * 256 + datax);

					break;
				default:
					c = (char) data;

				}
				if (c != '\n' && c != '\r') {
					if (line.length() > 0 || c != ' ') {
						line.append(c);
					}
				}
				if (c == '\n' && line.length() > 0) {
					System.out.println(line.toString());
					// now we have next line
					// split into parts
					String linex = line.toString();
					int i1 = linex.indexOf(' ');
					int i2 = 0;
					int i3 = 0;
					tag = null;
					refId = null;
					lineValue = null;
					String aux;
					if (i1 > 0 && i1 < linex.length()) {
						try {
							level = Integer.parseInt(linex.substring(0, i1));
						} catch (NumberFormatException ne) {

						}
						i2 = linex.indexOf(' ', i1 + 1);
						if (i2 > 0 && i2 < linex.length()) {
							aux = linex.substring(i1 + 1, i2);
							i3 = i2;
							if (aux.charAt(0) == '@'
									&& aux.charAt(aux.length() - 1) == '@') {
								refId = aux.substring(1, aux.length() - 1);
								i3 = linex.indexOf(' ', i2 + 1);
								if (i3 > 0) {
									tag = linex.substring(i2 + 1, i3);
								} else {
									tag = linex.substring(i2 + 1);
								}
							} else {
								tag = aux;
							}
							if (i3 > 0) {
								lineValue = linex.substring(i3 + 1);
							}
						} else {
							tag = linex.substring(i1 + 1);
						}
						System.out.println("'" + level + "'" + refId + "'"
								+ tag + "'" + lineValue);
					}

					line = new StringBuffer();
				}

				if (this.runner != null) {
					StringBuffer sb = new StringBuffer();

					// sb.append(this.unitId + ":  ");
					// sb.append(this.unitGivenName);
					// if (this.unitPrefix != null) {
					// sb.append(" ");
					// sb.append(this.unitPrefix);
					// }
					// if (this.unitSurName != null) {
					// sb.append(" ");
					// sb.append(this.unitSurName);
					// }
					// if (this.unitPostfix != null) {
					// sb.append(" ");
					// sb.append(this.unitPostfix);
					// }

					double dluku = lukuri;

					double prose = (dluku * 100) / dLen;
					int intprose = (int) prose;
					sb.append("" + intprose + ";Kalle koetta");
					this.runner.setRunnerValue(sb.toString());

					// try {
					// Thread.sleep(1);
					//
					// } catch (InterruptedException ie) {
					// }

				}

				// if (unitCount > 0) {
				// throw new SukuException(Resurses
				// .getString("DATABASE_NOT_EMPTY"));
				//
				// }
			}
			bis.close();
		} catch (Exception e) {
			throw new SukuException(e);
		}
		return resp;
		// SukuUtility data = SukuUtility.instance();
		// data.createSukuDb(this.con, "/sql/finfamily.sql");

		// logger.fine("database created for " + path);

	}

}
