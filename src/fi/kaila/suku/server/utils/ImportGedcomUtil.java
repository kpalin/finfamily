package fi.kaila.suku.server.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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

	LinkedHashMap<String, GedcomRecord> gedMap = null;
	Vector<String> unknownLine = new Vector<String>();

	/**
	 * @param file
	 * @param db
	 * @return result in SukuData
	 * @throws SukuException
	 */
	public SukuData importGedcom(String file, String db) throws SukuException {
		SukuData resp = new SukuData();
		gedMap = new LinkedHashMap<String, GedcomRecord>();
		GedcomRecord record = null;
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
			fileIndex = 4;
			if (dataLen < 10) {
				resp.resu = Resurses.getString("GEDCOM_BAD_FORMAT");
				return resp;
			}
			char c;
			int data0 = bis.read();
			int data1 = bis.read();
			int data2 = bis.read();
			int data3 = bis.read();

			// int lineNumber = 0;
			StringBuffer line = new StringBuffer();
			if (data0 == 255 && data1 == 254) {
				thisSet = GedSet.Set_Utf16le;
				c = (char) (data3 * 256 + data2);
				line.append(c);
			} else if (data1 == 255 && data0 == 254) {
				thisSet = GedSet.Set_Utf16be;
				c = (char) (data2 * 256 + data3);
				line.append(c);
			} else if (data0 == 239 && data1 == 187 && data2 == 191) {
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

			while ((data = bis.read()) >= 0) {

				fileIndex++;

				c = cnvToChar(data, bis);

				if (c != '\n' && c != '\r') {
					if (line.length() > 0 || c != ' ') {
						line.append(c);
					}
				}
				if (c == '\n' && line.length() > 0) {

					// System.out.println(line.toString());
					// now we have next line
					// split into parts
					String linex = line.toString();
					int i1 = linex.indexOf(' ');
					int i2 = 0;
					int i3 = 0;
					int ix = 0;

					GedcomLine lineg = null;
					String aux;
					if (i1 > 0 && i1 < linex.length()) {
						try {
							ix = Integer.parseInt(linex.substring(0, i1));

						} catch (NumberFormatException ne) {
							ix = -1;
						}
						lineg = new GedcomLine(ix);
						if (ix == 0) {
							if (record != null) {
								String key = record.getKey();
								gedMap.put(key, record);
								consumeGedcomRecord(record);
							}
							record = new GedcomRecord();

						}

						i2 = linex.indexOf(' ', i1 + 1);
						if (i2 > 0 && i2 < linex.length()) {
							aux = linex.substring(i1 + 1, i2);
							i3 = i2;
							if (aux.charAt(0) == '@'
									&& aux.charAt(aux.length() - 1) == '@') {
								lineg.id = aux;
								i3 = linex.indexOf(' ', i2 + 1);
								if (i3 > 0) {
									lineg.tag = linex.substring(i2 + 1, i3);
								} else {
									lineg.tag = linex.substring(i2 + 1);
								}
							} else {
								lineg.tag = aux;
							}
							if (i3 > 0) {
								lineg.lineValue = linex.substring(i3 + 1);
							}
						} else {
							lineg.tag = linex.substring(i1 + 1);
						}
						// System.out.println("'" + lineg.level + "'" + lineg.id
						// + "'" + lineg.tag + "'" + lineg.lineValue);
					}
					if (lineg.tag == null)
						continue;

					if (lineg.lineValue != null && lineg.level == 1
							&& lineg.tag.equals("CHAR")) {
						if (thisSet == GedSet.Set_None) {
							if (lineg.lineValue.equalsIgnoreCase("UNICODE")
									|| lineg.lineValue
											.equalsIgnoreCase("UTF-8")
									|| lineg.lineValue.equalsIgnoreCase("UTF8")) {
								thisSet = GedSet.Set_Utf8;
							} else if (lineg.lineValue
									.equalsIgnoreCase("ANSEL")) {
								thisSet = GedSet.Set_Ansel;
							}
						}
					} else {
						record.add(lineg);
					}
					line = new StringBuffer();

					// lineNumber++;
					// if (lineg.tag != null) { // at beginning nothing is known
					//
					// unknown.add("[" + lineNumber + "] " + linex);
					// }

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

					double dluku = fileIndex;

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

			String key = record.getKey();
			gedMap.put(key, record);

			Set<Map.Entry<String, GedcomRecord>> entries = gedMap.entrySet();
			Iterator<Map.Entry<String, GedcomRecord>> ee = entries.iterator();
			Vector<String> recs = new Vector<String>();
			while (ee.hasNext()) {
				Map.Entry<String, GedcomRecord> entry = (Map.Entry<String, GedcomRecord>) ee
						.next();
				recs.add(entry.getValue().toString());
			}

			resp.generalArray = recs.toArray(new String[0]);

			bis.close();
		} catch (Exception e) {
			throw new SukuException(e);
		}
		return resp;
		// SukuUtility data = SukuUtility.instance();
		// data.createSukuDb(this.con, "/sql/finfamily.sql");

		// logger.fine("database created for " + path);

	}

	int recordCount = 0;
	String submitterId = null;

	private void consumeGedcomRecord(GedcomRecord record) {
		recordCount++;
		if (recordCount == 1) { // expected HEAD record
			if (record.lines.get(0).tag.equals("HEAD")) {
				consumeGedcomHead(record);
			}
		}
		if (record.lines.get(0).tag.equals("SUBM")) {
			cosumeGedcomSubmitter(record);
		}

	}

	private void cosumeGedcomSubmitter(GedcomRecord record) {
		// TODO Auto-generated method stub

	}

	private void consumeGedcomHead(GedcomRecord record) {
		for (int i = 1; i < record.lines.size(); i++) {
			GedcomLine line = record.lines.get(i);
			if (line.level == 1) {
				if (line.tag.equals("SOUR")) {
				} else if (line.tag.equals("SUBM")) {
					submitterId = line.lineValue;
				} else if (line.tag.equals("GEDC")) {
				}
			} else if (line.level > 1) {
				if (line.tag.equals("VERS") || line.tag.equals("NAME")
						|| line.tag.equals("CORP") || line.tag.equals("ADDR")
						|| line.tag.equals("VERS") || line.tag.equals("FORM")
						|| line.tag.equals("CONT") || line.tag.equals("CONC"))
					continue;
			} else {
				this.unknownLine.add(line.toString());
			}

		}

	}

	private long fileIndex = 0;

	private char cnvToChar(int data, InputStream bis) throws IOException {
		char c;
		int datax;
		int datay;
		int dataz;
		switch (thisSet) {
		case Set_Utf16le:
			datax = bis.read();
			fileIndex++;
			c = (char) (datax * 256 + data);

			break;
		case Set_Utf16be:
			datax = bis.read();
			fileIndex++;
			c = (char) (data * 256 + datax);

			break;
		case Set_Utf8:
			if ((data & 0x80) == 0) {
				c = (char) data;
				break;
			}
			datax = bis.read();
			fileIndex++;
			if ((data & 0x20) == 0) {
				datax &= 0x3F;
				data &= 0x1F;
				data = data << 6;
				c = (char) (data | datax);
				break;
			}
			datay = bis.read();
			fileIndex++;
			if ((data & 0x10) == 0) {
				datay &= 0x3F;
				datax &= 0x3F;
				data &= 0x0F;
				datax = datax << 6;
				data = data << 10;
				c = (char) (data | datax | datay);
			}
			dataz = bis.read();
			fileIndex++;
			dataz &= 0x3F;
			datay &= 0x3F;
			datax &= 0x3F;
			data &= 0x07;
			datay = datay << 6;
			datax = datax << 12;
			data = data << 18;
			c = (char) (data | datax | datay | dataz);
			break;

		case Set_Ansel:

			if ((data & 0x80) == 0) {
				c = (char) data;
				break;
			}
			if (data < 0xE0) {
				if (data == 0xA4)
					c = 'Þ';
				else if (data == 0xA2)
					c = 'Ø';
				else if (data == 0xA5)
					c = 'Æ';
				else if (data == 0xA6)
					c = 'Œ';
				else if (data == 0xAA)
					c = '®';

				else if (data == 0xAB)
					c = '±';
				else if (data == 0xB2)
					c = 'ø';
				else if (data == 0xB4)
					c = 'þ';
				else if (data == 0xB5)
					c = 'æ';
				else if (data == 0xB6)
					c = 'œ';
				else if (data == 0xB9)
					c = '£';
				else if (data == 0xBA)
					c = 'ð';
				else if (data == 0xC3)
					c = '©';
				else if (data == 0xC5)
					c = '¿';
				else if (data == 0xC6)
					c = '¡';
				else if (data == 0xCF)
					c = 'ß';
				else
					c = '?';

			} else {
				datax = bis.read();
				fileIndex++;
				switch (data) {
				case 0xE1: // grave accent
					if (datax == 'a')
						c = 'à';
					else if (datax == 'A')
						c = 'À';
					else if (datax == 'e')
						c = 'è';
					else if (datax == 'E')
						c = 'È';
					else if (datax == 'i')
						c = 'ì';
					else if (datax == 'I')
						c = 'ì';
					else if (datax == 'o')
						c = 'ò';
					else if (datax == 'O')
						c = 'Ò';
					else if (datax == 'u')
						c = 'ù';
					else if (datax == 'U')
						c = 'Ù';
					else
						c = (char) datax;
					break;
				case 0xE2: // acute accent
					if (datax == 'a')
						c = 'á';
					else if (datax == 'A')
						c = 'Á';
					else if (datax == 'e')
						c = 'é';
					else if (datax == 'E')
						c = 'É';
					else if (datax == 'i')
						c = 'í';
					else if (datax == 'I')
						c = 'Í';
					else if (datax == 'o')
						c = 'ó';
					else if (datax == 'O')
						c = 'Ó';
					else if (datax == 'u')
						c = 'ú';
					else if (datax == 'U')
						c = 'Ú';
					else
						c = (char) datax;
					break;
				case 0xE3: // circumflex accent
					if (datax == 'a')
						c = 'â';
					else if (datax == 'A')
						c = 'Â';
					else if (datax == 'e')
						c = 'ê';
					else if (datax == 'E')
						c = 'Ê';
					else if (datax == 'i')
						c = 'î';
					else if (datax == 'I')
						c = 'Î';
					else if (datax == 'o')
						c = 'ô';
					else if (datax == 'O')
						c = 'Ô';
					else if (datax == 'u')
						c = 'û';
					else if (datax == 'U')
						c = 'Û';
					else
						c = (char) datax;
					break;
				case 0xE4: // tilde
					if (datax == 'a')
						c = 'ã';
					else if (datax == 'A')
						c = 'Ã';
					else if (datax == 'n')
						c = 'ñ';
					else if (datax == 'N')
						c = 'Ñ';
					else if (datax == 'o')
						c = 'õ';
					else if (datax == 'O')
						c = 'Õ';
					else
						c = (char) datax;
					break;
				case 0xF0: // cedilla
					if (datax == 'c')
						c = 'ç';
					else if (datax == 'C')
						c = 'Ç';
					else if (datax == 'n')
						c = 'ñ';
					else if (datax == 'N')
						c = 'Ñ';
					else if (datax == 'o')
						c = 'õ';
					else if (datax == 'O')
						c = 'Õ';
					else
						c = (char) datax;
					break;

				case 0xE8: // umlaut
					if (datax == 'a')
						c = 'ä';
					else if (datax == 'A')
						c = 'Ä';
					else if (datax == 'o')
						c = 'ö';
					else if (datax == 'O')
						c = 'Ö';
					else if (datax == 'u')
						c = 'ü';
					else if (datax == 'U')
						c = 'Ü';
					else if (datax == 'e')
						c = 'ë';
					else if (datax == 'E')
						c = 'Ë';
					else if (datax == 'i')
						c = 'ï';
					else if (datax == 'I')
						c = 'Ï';
					else
						c = (char) datax;
					break;
				case 0xEA: // ringabove
					if (datax == 'a')
						c = 'å';
					else if (datax == 'A')
						c = 'Å';
					else
						c = (char) datax;
					break;

				default:
					c = (char) datax;
					break;
				}
			} // end of ansel
			break;
		default:
			c = (char) data;
			break;
		}
		return c;

	}

	int keyCounter = 0;

	class GedcomRecord {
		Vector<GedcomLine> lines = new Vector<GedcomLine>();
		String key = null;

		String getKey() {

			GedcomLine line = lines.get(0);
			if (line.id != null) {
				return line.id;
			}

			if (key != null) {
				return key;
			}
			key = "X" + keyCounter + "X";
			return key;

		}

		void add(GedcomLine line) {

			lines.add(line);
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < lines.size(); i++) {
				sb.append(lines.get(i));
				sb.append("\n");
			}
			return sb.toString();

		}

	}

	class GedcomLine {
		int level = -1;
		String tag = null;
		String id = null;
		String lineValue = null;

		GedcomLine(int level) {
			this.level = level;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			// sb.append("                          ".substring(0, level*2));
			sb.append(level);
			sb.append(" ");
			if (id != null) {
				sb.append(id);
				sb.append(" ");
			}
			sb.append(tag);
			if (lineValue != null) {
				sb.append(" ");
				sb.append(lineValue);
			}
			return sb.toString();
		}
	}

}
