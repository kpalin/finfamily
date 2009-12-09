package fi.kaila.suku.server.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Logger;

import fi.kaila.suku.imports.ImportGedcomDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

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
	private Logger logger = Logger.getLogger(this.getClass().getName());

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

	LinkedHashMap<String, GedcomLine> gedMap = null;
	Vector<String> unknownLine = new Vector<String>();

	/**
	 * @param file
	 * @param db
	 * @return result in SukuData
	 * @throws SukuException
	 */
	public SukuData importGedcom(String file, String db) throws SukuException {
		SukuData resp = new SukuData();
		gedMap = new LinkedHashMap<String, GedcomLine>();
		GedcomLine record = null;
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
							record = lineg;

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
						if (lineg.level > 0) {
							record.add(lineg);
						}
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
			consumeGedcomRecord(record);

			// Set<Map.Entry<String, GedcomLine>> entries = gedMap.entrySet();
			// Iterator<Map.Entry<String, GedcomLine>> ee = entries.iterator();
			// Vector<String> recs = new Vector<String>();
			// while (ee.hasNext()) {
			// Map.Entry<String, GedcomLine> entry = (Map.Entry<String,
			// GedcomLine>) ee
			// .next();
			// recs.add(entry.getValue().toString());
			// }
			//
			// resp.generalArray = recs.toArray(new String[0]);
			resp.generalArray = unknownLine.toArray(new String[0]);
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
	String ownerInfo = null;

	private void consumeGedcomRecord(GedcomLine record) throws SQLException {
		recordCount++;
		if (recordCount == 1) { // expected HEAD record
			if (!record.tag.equals("HEAD")) {
				unknownLine.add(Resurses.getString("GEDCOM_MISSING_HEAD"));
			} else {
				consumeGedcomHead(record);
			}
		} else if (record.tag.equals("SUBM")) {
			consumeGedcomSubmitter(record);
		} else if (record.tag.equals("INDI")) {
			consumeGedcomIndi(record);
		} else {
			unknownLine.add(record.toString());
		}

	}

	private void consumeGedcomIndi(GedcomLine record) {
		PersonLongData pers = new PersonLongData(0, "INDI", "U");
		Vector<UnitNotice> notices = new Vector<UnitNotice>();
		pers.setUserRefn(record.id);
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine noti = record.lines.get(i);
			if (noti.tag.equals("SEX")) {
				pers.setSex(noti.lineValue);
			} else if (noti.tag.equals("REFN")) {
				pers.setUserRefn(noti.lineValue);
			} else if (noti.tag.equals("NAME")) {
				if (noti.lineValue != null) {
					UnitNotice notice = new UnitNotice("NAME");
					notices.add(notice);
					String parts[] = noti.lineValue.split("/");
					if (parts.length > 0) {
						if (parts[0].length() > 0) {

							notice.setGivenname(Utils.extractPatronyme(
									parts[0], false));
							notice.setPatronym(Utils.extractPatronyme(parts[0],
									true));
						}
						if (parts.length > 1 && parts[1].length() > 0) {

							int vonIndex = Utils.isKnownPrefix(parts[1]);
							if (vonIndex > 0) {
								notice.setPrefix(parts[1]
										.substring(0, vonIndex));
								notice.setSurname(parts[1]
										.substring(vonIndex + 1));
							} else {

								notice.setSurname(parts[1]);
							}
						}
						if (parts.length > 2 && parts[2].length() > 0) {
							notice.setPostfix(parts[2]);
						}
					}
				}
			} else if (noti.tag.equals("OCCU") || noti.tag.equals("EDUC")
					|| noti.tag.equals("TITL")) {
				UnitNotice notice = new UnitNotice(noti.tag);
				notices.add(notice);
				notice.setDescription(noti.lineValue);
			} else if (noti.tag.equals("NOTE")) {
				if (noti.lineValue != null) {
					UnitNotice notice = new UnitNotice("NOTE");
					notices.add(notice);
					notice.setNoteText(noti.lineValue);
				}
			} else {
				unknownLine.add(noti.toString());
			}

		}
		pers.setNotices(notices.toArray(new UnitNotice[0]));

		SukuData request = new SukuData();
		request.persLong = pers;
		PersonUtil u = new PersonUtil(con);

		u.updatePerson(request);

	}

	private void consumeGedcomSubmitter(GedcomLine record) throws SQLException {
		String name = null;
		StringBuffer address = new StringBuffer();
		String postalcode = null;
		String postoffice = null;
		StringBuffer country = new StringBuffer();
		String email = null;
		StringBuffer note = new StringBuffer();
		if (ownerInfo != null) {
			note.append(ownerInfo);
		}

		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine noti = record.lines.get(i);
			if (noti.tag.equals("NAME")) {
				name = noti.lineValue;
			} else if (noti.tag.equals("ADDR")) {
				if (noti.lineValue != null) {
					String parts[] = noti.lineValue.split("\n");
					boolean wasPo = false;
					for (int j = 0; j < parts.length; j++) {
						if (j == 0) {
							address.append(parts[j]);
						} else {
							if (parts[j].indexOf('@') > 0) { // possibly email
								email = parts[j];
							} else if (!wasPo) {
								String posts[] = parts[j].split(" ");
								if (posts.length > 1) {
									int ponum = -1;
									try {
										ponum = Integer.parseInt(posts[0]);
									} catch (NumberFormatException ne) {

									}
									if (ponum > 1) { // now assume beginning is
										// postalcode
										postalcode = posts[0];
										postoffice = parts[j]
												.substring(posts[0].length() + 1);
										wasPo = true;
									}
								}
								if (!wasPo) {
									if (address.length() > 0) {
										address.append("\n");
									}
									address.append(parts[j]);

								}
							} else {
								if (country.length() > 0) {
									country.append("\n");
								}
								country.append(parts[j]);
							}

						}
					}

				}

			}
		}

		String sql = "insert into sukuvariables (owner_name,owner_info, "
				+ "owner_address,owner_postalcode,owner_postoffice,"
				+ "owner_country,owner_email,user_id) values (?,?,?,?,?,?,?,user) ";

		PreparedStatement pst = con.prepareStatement(sql);
		pst.setString(1, name);
		pst.setString(2, ownerInfo.toString());
		pst.setString(3, address.toString());
		pst.setString(4, postalcode);
		pst.setString(5, postoffice);
		pst.setString(6, country.toString());
		pst.setString(7, email);
		int lukuri = pst.executeUpdate();
		logger.info("Sukuvariables updated " + lukuri + " lines");
	}

	private void consumeGedcomHead(GedcomLine record) {
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine notice1 = record.lines.get(i);
			if (notice1.tag.equals("SOUR")) {
				continue;
			} else if (notice1.tag.equals("DEST")) {
				continue;
			} else if (notice1.tag.equals("SUBM")) {
				submitterId = notice1.lineValue;
				continue;
			} else if (notice1.tag.equals("GEDC")) {
				continue;
			} else if (notice1.tag.equals("NOTE")) {
				ownerInfo = notice1.lineValue;
			} else {
				unknownLine.add(notice1.toString());

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

	// class GedcomRecord {
	// Vector<GedcomLine> lines = new Vector<GedcomLine>();
	//
	// void add(GedcomLine line) {
	//
	// lines.add(line);
	// }
	//
	// public String toString() {
	// StringBuffer sb = new StringBuffer();
	// for (int i = 0; i < lines.size(); i++) {
	// sb.append(lines.get(i));
	// sb.append("\n");
	// }
	// return sb.toString();
	//
	// }
	//
	// }

	class GedcomLine {
		int level = -1;
		String tag = null;
		String id = null;
		String lineValue = null;
		Vector<GedcomLine> lines = new Vector<GedcomLine>();

		void add(GedcomLine line) {
			if (line.level == level + 1) {
				if (line.tag.equals("CONT")) {
					lineValue += "\n" + line.lineValue;
				} else if (line.tag.equals("CONC")) {
					lineValue += line.lineValue;
				} else {
					lines.add(line);
				}
			} else {
				int last = lines.size() - 1;
				if (last < 0) {
					unknownLine.add(line.toString());
				}
				GedcomLine subline = lines.get(last);
				subline.add(line);
			}
		}

		String key = null;

		String getKey() {
			if (id != null) {
				return id;
			}
			if (key != null) {
				return key;
			}
			keyCounter++;
			key = "X" + keyCounter + "X";
			return key;
		}

		GedcomLine(int level) {
			this.level = level;
		}

		public String toString() {
			return toString(false);
		}

		public String toString(boolean withLevels) {
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
			sb.append("\n");
			if (withLevels) {
				for (int i = 0; i < lines.size(); i++) {
					sb.append(lines.get(i).toString());
				}
			}
			return sb.toString();
		}
	}

}
