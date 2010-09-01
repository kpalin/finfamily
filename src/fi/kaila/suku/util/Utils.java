package fi.kaila.suku.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.report.PersonInTables;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;

/**
 * general static utilities
 * 
 * @author FIKAAKAIL
 * 
 */
public class Utils {

	/**
	 * enumerator for source of person for drag-and-drop
	 * 
	 */
	public enum PersonSource {
		/**
		 * Database table
		 */
		DATABASE,
		/**
		 * parent table
		 */
		PARENT,
		/**
		 * spouse table
		 */
		SPOUSE,
		/**
		 * child table
		 */
		CHILD
	}

	private static Logger logger = Logger.getLogger(Utils.class.getName());

	/**
	 * Get boolean preference from local repository
	 * 
	 * @param o
	 * @param key
	 * @param def
	 * @return true or false
	 */
	public static boolean getBooleanPref(Object o, String key, boolean def) {
		String resu;
		String sdef = "false";
		if (def) {
			sdef = "true";
		}

		SukuKontroller kontroller = Suku.getKontroller();

		resu = kontroller.getPref(o, key, sdef);

		if (resu == null) {
			return def;
		}

		if (resu.equals("true")) {
			return true;
		}
		return false;

	}

	/**
	 * Put boolean preference into local repository
	 * 
	 * @param o
	 * @param key
	 * @param value
	 */
	public static void putBooleanPref(Object o, String key, boolean value) {
		String svalue = "false";
		if (value)
			svalue = "true";
		SukuKontroller kontroller = Suku.getKontroller();

		kontroller.putPref(o, key, svalue);
	}

	/**
	 * convert dbdate date to viewable textformat
	 * 
	 * @param dbDate
	 * @param trimDate
	 * @return in text format
	 */
	public static String textDate(String dbDate, boolean trimDate) {
		String df = Resurses.getDateFormat();
		if (dbDate == null)
			return null;
		if (dbDate.length() == 4) {
			return dbDate;
		}
		// int y=0;
		int m = 0;
		int d = 0;
		String yy = null;
		String mm = null;
		String dd = null;
		if (dbDate.length() == 6) {
			yy = dbDate.substring(0, 4);
			mm = dbDate.substring(4);
		} else if (dbDate.length() == 8) {
			yy = dbDate.substring(0, 4);
			mm = dbDate.substring(4, 6);
			dd = dbDate.substring(6);
		}
		try {
			// y = Integer.parseInt(yy);
			m = Integer.parseInt(mm);
			if (dd != null) {
				d = Integer.parseInt(dd);
			}
			if (!df.equals("SE") && trimDate) {
				mm = "" + m;
				if (dd != null) {
					dd = "" + d;
				}
			}
		} catch (NumberFormatException ne) {

		}

		if (dbDate.length() == 6) {

			if (df.equals("SE")) {
				return yy + "-" + mm;
			} else if (df.equals("FI")) {
				return mm + "." + yy;
			} else {
				return mm + "/" + yy;
			}
		} else if (dbDate.length() == 8) {
			if (df.equals("SE")) {
				return yy + "-" + mm + "-" + dd;
			} else if (df.equals("FI")) {
				return dd + "." + mm + "." + yy;
			} else if (df.equals("GB")) {
				return dd + "/" + mm + "/" + yy;
			} else {
				return mm + "/" + dd + "/" + yy;
			}

		}
		return dbDate;
	}

	/**
	 * @param textDate
	 * @return db date or null if wrong date
	 */
	public static String dbTryDate(String textDate) {
		try {
			return dbDate(textDate);
		} catch (SukuDateException e) {
			return null;
		}
	}

	/**
	 * convert viewable textdate to dbformat
	 * 
	 * @param textDate
	 * @return date in dbformat
	 * @throws SukuDateException
	 *             if bad dateformat
	 */
	public static String dbDate(String textDate) throws SukuDateException {
		if (textDate == null || textDate.isEmpty())
			return null;
		String df = Resurses.getDateFormat();

		StringBuilder sb = new StringBuilder();

		sb.append(Resurses.getString("ERROR_WRONGDATE"));
		sb.append(" ");
		sb.append(textDate);

		String parts[] = textDate.split("\\.|,|/|-");
		int parti[] = { -1, -1, -1 };
		if (parts.length > 3) {
			throw new SukuDateException(sb.toString());
		}
		for (int i = 0; i < parts.length; i++) {
			try {

				parti[i] = Integer.parseInt(parts[i].trim());
			} catch (NumberFormatException ne) {
				throw new SukuDateException(sb.toString());
			}
		}

		SimpleDateFormat dfor = new SimpleDateFormat("yyyyMMdd");
		String today = dfor.format(new java.util.Date());
		int nowy = Integer.parseInt(today.substring(0, 4));
		int y = -1;
		int m = -1;
		int d = -1;

		if (parts.length == 1) {
			y = parti[0];
		}
		if (parts.length == 2) {
			if (df.equals("SE")) {
				y = parti[0];
				m = parti[1];
			} else {
				y = parti[1];
				m = parti[2];
			}
		}
		if (parts.length == 3) {
			if (df.equals("SE")) {
				y = parti[0];
				m = parti[1];
				d = parti[2];

			} else if (df.equals("US")) {
				y = parti[2];
				m = parti[0];
				d = parti[1];

			} else {
				y = parti[2];
				m = parti[1];
				d = parti[0];
			}
		}

		int leap = y % 4;
		if (leap == 0) {
			leap = 29;
		} else {
			leap = 28;
		}
		if (y == 1712) {
			leap = 30;
		}
		if (y > nowy) {
			throw new SukuDateException(Resurses.getString("ERROR_FUTURE")
					+ " [" + textDate + "]");
		}

		if (m >= 0 && (m == 0 || m > 12)) {
			throw new SukuDateException(Resurses.getString("ERROR_MONTH")
					+ " [" + textDate + "]");
		}
		if (d >= 0) {
			if (d > 0 && d <= 31) {
				switch (m) {
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
					break;
				case 2:
					if (d > leap) {
						d = -1;
					}
					break;
				case 4:
				case 6:
				case 9:
				case 11:
					if (d == 31) {
						d = -1;

					}
					break;

				}
			} else {
				d = -1;
			}
			if (d < 0) {
				throw new SukuDateException(Resurses.getString("ERROR_DAY")
						+ " [" + textDate + "]");
			}
		}

		if (parts.length == 1) {
			return strl(parti[0], 4);
		}

		if (parts.length == 2) {
			if (df.equals("SE")) {
				return strl(parti[0], 4) + strl(parti[1], 2);
			} else {
				return strl(parti[1], 4) + strl(parti[0], 2);
			}
		}
		if (parts.length == 3) {
			if (df.equals("SE")) {
				return strl(parti[0], 4) + strl(parti[1], 2)
						+ strl(parti[2], 2);
			} else if (df.equals("US")) {
				return strl(parti[2], 4) + strl(parti[0], 2)
						+ strl(parti[1], 2);
			}
			return strl(parti[2], 4) + strl(parti[1], 2) + strl(parti[0], 2);

		}
		throw new SukuDateException(sb.toString());

	}

	/**
	 * Modify name to Proper name using capital latters as first character
	 * 
	 * @param names
	 * @return the proper name
	 */
	public static String toProper(String names) {
		if (names == null)
			return null;
		if (names.isEmpty())
			return "";
		String name[] = names.split(" ");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length; i++) {
			if (!name[i].isEmpty()) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				if (name[i].length() == 1) {
					sb.append(name[i].toUpperCase());
				} else {
					sb.append(name[i].substring(0, 1).toUpperCase());
					sb.append(name[i].substring(1));
				}
			}
		}
		return sb.toString();

	}

	private static String strl(int i, int len) {
		if (i == 0)
			return null;
		String text = "" + i;

		if (text.length() == len) {
			return text;
		}
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < len; j++) {
			sb.append("0");
		}
		sb.append(text);

		return sb.toString().substring(sb.length() - len);

	}

	/**
	 * 
	 * Create from report tables a HasMap of persons in the report
	 * 
	 * @param tables
	 *            Vector that contains all tables for report
	 * @return a HashMap containing a list of all persons in report with
	 *         information on tables where they exis
	 */
	public static HashMap<Integer, PersonInTables> getDescendantToistot(
			Vector<ReportUnit> tables) {
		HashMap<Integer, PersonInTables> personReferences = new HashMap<Integer, PersonInTables>();

		for (int i = 0; i < tables.size(); i++) {
			ReportUnit tab = tables.get(i);
			PersonInTables ref;
			ReportTableMember member;

			for (int j = 0; j < tab.getChild().size(); j++) {
				member = tab.getChild().get(j);
				ref = personReferences.get(member.getPid());
				if (ref == null) {
					ref = new PersonInTables(member.getPid());

					if (tab.getChild().size() > 0) {
						ref.asChildren.add(Long.valueOf(tab.getTableNo()));
					}
					personReferences.put(Integer.valueOf(member.getPid()), ref);
				} else {
					if (tab.getChild().size() > 0) {
						ref.asChildren.add(Long.valueOf(tab.getTableNo()));
					}
				}

				for (int m = 0; m < member.getSubCount(); m++) {
					ref = personReferences.get(member.getSubPid(m));
					if (ref == null) {
						ref = new PersonInTables(member.getSubPid(m));
						ref.references.add(Long.valueOf(tab.getTableNo()));
						personReferences.put(Integer.valueOf(member
								.getSubPid(m)), ref);
					} else {
						ref.references.add(tab.getTableNo());
					}
				}

				if (member.getSpouses() != null) {
					ReportTableMember[] spouseMembers = member.getSpouses();
					ReportTableMember spouseMember;
					for (int k = 0; k < spouseMembers.length; k++) {
						spouseMember = spouseMembers[k];
						ref = personReferences.get(spouseMember.getPid());
						if (ref == null) {
							ref = new PersonInTables(spouseMember.getPid());
							ref.asParents.add(Long.valueOf(tab.getTableNo()));
							personReferences.put(Integer.valueOf(spouseMember
									.getPid()), ref);
						} else {
							ref.asParents.add(tab.getTableNo());
						}
						for (int m = 0; m < spouseMember.getSubCount(); m++) {
							ref = personReferences.get(spouseMember
									.getSubPid(m));
							if (ref == null) {
								ref = new PersonInTables(spouseMember
										.getSubPid(m));
								ref.references.add(Long.valueOf(tab
										.getTableNo()));
								personReferences.put(Integer
										.valueOf(spouseMember.getSubPid(m)),
										ref);
							} else {
								ref.references.add(tab.getTableNo());
							}
						}
					}
				}

			}
			for (int j = 0; j < tab.getParent().size(); j++) {
				member = tab.getParent().get(j);
				if (tab.getPid() == member.getPid()) {
					// were the owner here
					ref = personReferences.get(member.getPid());
					if (ref == null) {
						ref = new PersonInTables(member.getPid());
						ref.addOwner(tab.getTableNo());
						personReferences.put(Integer.valueOf(member.getPid()),
								ref);
					} else {
						ref.addOwner(tab.getTableNo());

					}

				}

				ref = personReferences.get(member.getPid());
				if (ref == null) {
					ref = new PersonInTables(member.getPid());

					if (tab.getChild().size() > 0) {
						ref.asParents.add(Long.valueOf(tab.getTableNo()));
					}
					personReferences.put(Integer.valueOf(member.getPid()), ref);
				} else {
					if (tab.getChild().size() > 0) {
						ref.asParents.add(Long.valueOf(tab.getTableNo()));
					}
				}
				for (int m = 0; m < member.getSubCount(); m++) {
					ref = personReferences.get(member.getSubPid(m));
					if (ref == null) {
						ref = new PersonInTables(member.getSubPid(m));
						ref.references.add(Long.valueOf(tab.getTableNo()));
						personReferences.put(Integer.valueOf(member
								.getSubPid(m)), ref);
					} else {
						ref.references.add(tab.getTableNo());
					}
				}
			}
		}
		return personReferences;
	}

	/**
	 * 
	 * @param text
	 * @return empty string if null or text
	 */
	public static String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

	/**
	 * 
	 * @param text
	 * @return null if empty string or text
	 */
	public static String vn(String text) {
		if (text == null || text.isEmpty())
			return null;
		return text;
	}

	/**
	 * used primarily to display year only
	 * 
	 * @param text
	 * @return 4 first chars of string if exist
	 */
	public static String nv4(String text) {
		if (text == null)
			return "";
		if (text.length() < 4)
			return text;
		return text.substring(0, 4);
	}

	/**
	 * check if name begins with von part
	 * 
	 * @param name
	 *            to check
	 * @return parts
	 */
	public static int isKnownPrefix(String name) {
		String[] prexesu = Resurses.getString("NAME_VON").split(";");
		String[] prexes = new String[prexesu.length];
		int ii = prexes.length;
		int len = 0;
		// lets put them in order so that longest strings comes first
		while (ii > 0) {

			len++;
			for (int j = 0; j < prexesu.length; j++) {
				if (prexesu[j].length() == len) {
					ii--;
					prexes[ii] = prexesu[j];
				}
			}
		}

		for (int i = 0; i < prexes.length; i++) {
			if (name.equalsIgnoreCase(prexes[i])) {
				return prexes[i].length();
			}
			if (name.toLowerCase().startsWith(prexes[i] + " ")) {
				return prexes[i].length();
			}
		}
		return 0;
	}

	private static String[] patronymeEnds = null; // {"poika","son","dotter","tytär"};

	/**
	 * @param noticeGivenName
	 * @param patronymePart
	 * @return patronym for true, else givenname
	 */
	public static String extractPatronyme(String noticeGivenName,
			boolean patronymePart) {

		if (patronymeEnds == null) {

			String tmp = Resurses.getString("PATRONYM_ENDINGS");
			patronymeEnds = tmp.split(";");
		}
		if (noticeGivenName == null)
			return null;
		int j;
		String trimmedName = null;
		int nl = noticeGivenName.length();
		if (noticeGivenName.endsWith(".") && nl > 1) {
			trimmedName = noticeGivenName.substring(0, nl - 1);
		} else {
			trimmedName = noticeGivenName;
		}
		for (int i = 0; i < patronymeEnds.length; i++) {
			if (trimmedName.endsWith(patronymeEnds[i])) {
				j = trimmedName.lastIndexOf(' ');
				if (j > 0) {
					if (patronymePart)
						return noticeGivenName.substring(j + 1);
					return noticeGivenName.substring(0, j).trim();
				} else {
					if (patronymePart)
						return noticeGivenName;
					return noticeGivenName;
				}
			}
		}
		if (patronymePart)
			return null;

		return noticeGivenName.trim();
	}

	/**
	 * command to start the external application for the file
	 * 
	 * @param url
	 */
	public static void openExternalFile(String url) {
		try {

			String os = System.getProperties().getProperty("os.name");
			// System.out.println("OS:" + os);
			if (os.toLowerCase().indexOf("windows") >= 0) {

				String[] cmds = { "rundll32", "url.dll,FileProtocolHandler",
						url };
				Process p = Runtime.getRuntime().exec(cmds);
				p.waitFor();
			} else if (os.toLowerCase().indexOf("mac") >= 0) {

				// this should work on mac

				String[] macs = { "open", "" };
				macs[1] = url;
				Process p = Runtime.getRuntime().exec(macs);
			}

			// Properties props = System.getProperties();
			// Enumeration e = props.keys();
			//
			// while (e.hasMoreElements()) {
			// String key = (String) e.nextElement();
			// String value = props.getProperty(key);
			//
			// System.out.println(key + ":" + value);
			//
			// }

		} catch (Throwable t) {
			logger.log(Level.INFO, "rundll32", t);

		}
	}

	private static SukuTypesModel types = null;

	/**
	 * reset the model so next request reloads it
	 */
	public static void resetSukuModel() {
		types = null;
	}

	/**
	 * @return the types model
	 */
	public static SukuTypesModel typeInstance() {

		if (types == null) {
			types = new SukuTypesModel();
		}
		return types;
	}

}
