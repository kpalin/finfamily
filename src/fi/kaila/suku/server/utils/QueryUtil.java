package fi.kaila.suku.server.utils;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class QueryUtil.
 * 
 * @author Kalle
 * 
 *         Utility class to do queries to the database
 */
public class QueryUtil {

	private static Logger logger = Logger.getLogger(QueryUtil.class.getName());

	private Connection con = null;

	// private String toDoTagName = null;

	/**
	 * Initialize with database connection.
	 * 
	 * @param con
	 *            the con
	 */
	public QueryUtil(Connection con) {
		this.con = con;
		// this.toDoTagName = Resurses.getString(Resurses.COLUMN_T_TODO);

	}

	/**
	 * Query database.
	 * 
	 * @param params
	 *            the params
	 * @return the query result
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData queryDatabase(String... params) throws SukuException {

		String[] pari;
		String decod;
		Vector<PersonShortData> personList = new Vector<PersonShortData>();
		HashMap<Integer, PersonShortData> persMap = new HashMap<Integer, PersonShortData>();
		int idx;

		boolean needsRelativeInfo = false;

		try {
			StringBuilder seleSQL = new StringBuilder();

			seleSQL.append("select u.pid,u.sex,u.userrefn,"
					+ "u.groupid,u.tag,n.tag,n.givenname,");
			seleSQL.append("n.patronym,n.prefix,n.surname,n.postfix,");
			seleSQL.append("n.fromdate,n.Place,n.Description,"
					+ "n.pnid,n.mediafilename,n.mediatitle,n.Country ");
			seleSQL.append("from unit as u left join unitnotice "
					+ "as n on u.pid = n.pid ");

			StringBuilder fromSQL = new StringBuilder();
			StringBuilder sbn = new StringBuilder();
			StringBuilder free = new StringBuilder();

			String searchPlace = null;
			String searchNoticeTag = null;
			boolean searchNoNotice = false;
			String searchSex = null;
			int searchMaxSurety = 100;
			String searchFullText = null;
			if (params.length > 1) {

				boolean isFirstCriteria = true;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_RELATIVE_INFO)) {
						needsRelativeInfo = true;
					} else {

						if (pari[0].equals(Resurses.CRITERIA_GIVENNAME)) {
							if (sbn.length() > 0)
								sbn.append("and ");
							sbn.append("givenname ilike '" + toQuery(decod)
									+ "%' ");
						} else if (pari[0].equals(Resurses.CRITERIA_SURNAME)) {
							if (sbn.length() > 0)
								sbn.append("and ");

							sbn.append("surname ilike '" + toQuery(decod)
									+ "%' ");
						} else if (pari[0].equals(Resurses.CRITERIA_PATRONYME)) {
							if (sbn.length() > 0)
								sbn.append("and ");
							sbn.append("patronym ilike '" + toQuery(decod)
									+ "%' ");
						} else if (pari[0].equals(Resurses.CRITERIA_PLACE)) {
							searchPlace = decod;
						} else if (pari[0].equals(Resurses.CRITERIA_NOTICE)) {
							searchNoticeTag = decod;
						} else if (pari[0]
								.equals(Resurses.CRITERIA_NOTICE_EXISTS)) {
							searchNoNotice = true;
						} else if (pari[0].equals(Resurses.CRITERIA_SEX)) {
							searchSex = decod;
						} else if (pari[0].equals(Resurses.CRITERIA_SURETY)) {
							try {
								searchMaxSurety = Integer.parseInt(decod);
							} catch (NumberFormatException ne) {
								searchMaxSurety = 100;
							}
						} else if (pari[0].equals(Resurses.CRITERIA_FULL_TEXT)) {
							searchFullText = decod;
						}
					}
				}
				if (searchSex != null) {
					seleSQL.append(" where u.sex = '" + searchSex + "' ");
					isFirstCriteria = false;
				}
				if (sbn.length() > 0) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					fromSQL.append("u.pid in (select pid from unitnotice where ");
					fromSQL.append(sbn.toString());
					fromSQL.append("and tag='NAME') ");
					isFirstCriteria = false;
				}

				String begdate = null;
				String todate = null;
				String place = null;
				String group = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");
					if (pari[0].equals(Resurses.CRITERIA_GROUP)) {
						group = decod;
					}
				}
				if (group != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					fromSQL.append("u.groupid ilike '" + toQuery(group) + "%' ");
				}

				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_BIRT_FROM)) {
						begdate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_BIRT_TO)) {
						todate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_BIRT_PLACE)) {
						place = decod;
					}
				}

				if (begdate != null || todate != null || place != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					fromSQL.append("u.pid in (select pid from unitnotice where ");
					if (begdate != null && todate == null) {
						fromSQL.append("fromdate >= '" + begdate + "' ");
					} else if (begdate == null && todate != null) {
						fromSQL.append("fromdate <= '" + todate + "9999' ");
					} else if (begdate != null && todate != null) {
						fromSQL.append("fromdate between '" + begdate
								+ "' and '" + todate + "9999' ");
					}
					if (begdate == null && todate == null && place != null) {
						fromSQL.append("place ilike '" + toQuery(place) + "%' ");
					} else if (place != null) {
						fromSQL.append("and place ilike '" + toQuery(place)
								+ "' ");
					}
					fromSQL.append("and tag='BIRT') ");
				}

				begdate = null;
				todate = null;
				place = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_DEAT_FROM)) {
						begdate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_DEAT_TO)) {
						todate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_DEAT_PLACE)) {
						place = decod;
					}
				}

				if (begdate != null || todate != null || place != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					fromSQL.append("u.pid in (select pid from unitnotice where ");
					if (begdate != null && todate == null) {
						fromSQL.append("fromdate >= '" + begdate + "' ");
					} else if (begdate == null && todate != null) {
						fromSQL.append("fromdate <= '" + todate + "9999' ");
					} else if (begdate != null && todate != null) {
						fromSQL.append("fromdate between '" + begdate
								+ "' and '" + todate + "9999' ");
					}
					if (begdate == null && todate == null && place != null) {
						fromSQL.append("place ilike '" + toQuery(place) + "' ");
					} else if (place != null) {
						fromSQL.append("and place ilike '" + place + "%' ");
					}
					fromSQL.append("and tag='DEAT') ");
				}
				begdate = null;
				todate = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_CREATED_FROM)) {
						begdate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_CREATED_TO)) {
						todate = decod;
					}
				}
				if (begdate != null || todate != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					if (todate == null) {
						fromSQL.append("u.pid in (select pid from unitnotice where createdate >= '"
								+ begdate + "' )");
					} else if (begdate == null) {
						fromSQL.append("u.pid in (select pid from unitnotice where createdate >= '"
								+ todate + "' )");
					} else {
						fromSQL.append("u.pid in (select pid from unitnotice where createdate between '"
								+ begdate + "' and '" + todate + "' ) ");
					}
				}
				String viewIdTxt = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_VIEW)) {
						viewIdTxt = decod;
					}
				}
				if (viewIdTxt != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;

					fromSQL.append("u.pid in (select pid from viewunits where vid = "
							+ viewIdTxt + ") ");

				}

				if (searchPlace != null || searchNoticeTag != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					if (searchPlace != null && searchNoticeTag == null) {

						fromSQL.append("u.pid in (select pid from unitnotice where place ilike '"
								+ toQuery(searchPlace) + "%') ");

					} else if (searchPlace != null && searchNoticeTag != null) {
						fromSQL.append("u.pid in (select pid from unitnotice where place ilike '"
								+ toQuery(searchPlace)
								+ "' and tag = '"
								+ searchNoticeTag + "') ");
					} else if (searchPlace == null && searchNoticeTag != null) {
						if (!searchNoNotice) {
							fromSQL.append("u.pid in (select pid from unitnotice where tag = '"
									+ searchNoticeTag + "') ");
						} else {
							fromSQL.append("u.pid not in (select pid from unitnotice where tag = '"
									+ searchNoticeTag + "') ");
						}
					}

				}
				if (searchMaxSurety < 100) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;

					fromSQL.append("u.pid in (select pid from unitnotice where surety <= "
							+ searchMaxSurety + ") ");
					fromSQL.append("or u.pid in (select pid from relation where surety <= "
							+ searchMaxSurety + ") ");

				}
				if (searchFullText != null) {
					if (isFirstCriteria) {
						free.append("where ");
					} else {
						free.append("and ");
					}
					isFirstCriteria = false;

					String[] parts = trimSpaces(searchFullText).split(" ");

					free.append("(");
					int valueAndOrNot = 0;
					for (int i = 0; i < parts.length; i++) {

						if (i > 0) {
							if (parts[i].equalsIgnoreCase(Resurses
									.getString("CRITERIA_AND"))) {
								valueAndOrNot = 0;
								continue;
							} else if (parts[i].equalsIgnoreCase(Resurses
									.getString("CRITERIA_OR"))) {
								valueAndOrNot = 1;
								continue;
							} else if (parts[i].equalsIgnoreCase(Resurses
									.getString("CRITERIA_NOT"))) {
								valueAndOrNot = 2;
								continue;
							}

							free.append(valueAndOrNot == 1 ? "or " : "and ");
						}
						free.append("u.pid "
								+ ((valueAndOrNot == 2) ? "not " : "")
								+ "in (select pid from fullTextView where fulltext ilike '%"
								+ toQuery(parts[i]) + "%') ");
					}
					free.append(")");

				}
				fromSQL.append(free.toString());

			}

			StringBuilder sql = new StringBuilder();

			sql.append(seleSQL);
			sql.append(fromSQL);

			sql.append("order by u.pid,n.noticerow ");

			logger.info(sql.toString());

			PersonShortData perso = null;

			Statement stm = this.con.createStatement();
			ResultSet rs = stm.executeQuery(sql.toString());
			int currentPid = 0;
			int pid;

			String dbntag; // 6
			String dbgivenname; // 7
			String dbpatronym; // 8
			String dbprefix; // 9
			String dbsurname; // 10
			String dbpostfix; // 11
			String dbfromdate; // 12
			String dbplace; // 13

			String dbdescription; // 14
			int dbnpid; // 15
			String dbmediafilename; // 16
			String dbmediatitle; // 17
			String dbCountry; // 18
			String rtag;
			int marriages = 0;
			int children = 0;
			int parents = 0;

			while (rs.next()) {
				pid = rs.getInt(1);
				// dbutag = rs.getString(5);
				dbntag = rs.getString(6);
				dbgivenname = rs.getString(7);
				dbpatronym = rs.getString(8);
				dbprefix = rs.getString(9);
				dbsurname = rs.getString(10);
				dbpostfix = rs.getString(11);
				dbfromdate = rs.getString(12);
				dbplace = rs.getString(13);
				dbdescription = rs.getString(14);
				dbnpid = rs.getInt(15);
				dbmediafilename = rs.getString(16);
				dbmediatitle = rs.getString(17);
				dbCountry = rs.getString(18);
				if (pid != currentPid && currentPid != 0) {
					personList.add(perso);

					persMap.put(perso.getPid(), perso);
					perso = null;
				}
				currentPid = pid;
				if (perso == null) {
					perso = new PersonShortData();
					perso.setPid(pid);
					persMap.put(perso.getPid(), perso);
					perso.setSex(rs.getString(2));
					perso.setRefn(rs.getString(3));
					perso.setGroup(rs.getString(4));

				}
				if (dbntag != null) {
					if (dbntag.equals("NAME")) {
						perso.addName(dbgivenname, dbpatronym, dbprefix,
								dbsurname, dbpostfix);
					}

					if (dbntag.equals("UNKN")) {
						perso.setUnkn(true);
					}
					if (dbntag.equals("OCCU")) {
						perso.setOccupation(dbdescription);
					}
					if (dbntag.equals("PHOT")) {
						perso.setMediaDataNotice(dbnpid);
						perso.setMediaTitle(dbmediatitle);
						perso.setMediaFilename(dbmediafilename);

					}

					if (dbntag.equals("BIRT") || dbntag.equals("CHR")) // &&

					{

						if (perso.getBirtTag() == null || dbntag.equals("BIRT")) {
							perso.setBirtTag(dbntag);
							perso.setBirtDate(dbfromdate);
							perso.setBirtPlace(dbplace);
							perso.setBirtCountry(dbCountry);
						}
					}

					if (dbntag.equals("DEAT") || dbntag.equals("BURI")) // &&

					{

						if (perso.getDeatTag() == null || dbntag.equals("DEAT")) {
							perso.setDeatTag(dbntag);
							perso.setDeatDate(dbfromdate);
							perso.setDeatPlace(dbplace);
							perso.setDeatCountry(dbCountry);
						}
					}
				}
			}
			if (perso != null) {
				// personDict.Add(perso.Pid, perso);
				personList.add(perso);
				persMap.put(perso.getPid(), perso);
				perso = null;
			}
			if (needsRelativeInfo) {

				StringBuilder relSQL = new StringBuilder();
				// relSQL.append("select tag,pid,count(*) from relation where pid in "
				// + "(select pid  from unit u ");
				// relSQL.append(fromSQL);
				// relSQL.append(") group by pid,tag order by pid");
				// select a.tag,a.pid aid,b.pid bid
				// from relation as a inner join relation as b on a.rid = b.rid
				// and a.pid <> b.pid
				// where a.pid in (select pid from unit u )
				relSQL.append("select u.tag,u.pid  aid,b.pid bid ");
				relSQL.append("from relation as u inner join relation as b on u.rid = b.rid and u.pid <> b.pid ");
				relSQL.append(fromSQL);
				relSQL.append("order by u.pid");
				logger.fine("Relative sql: " + relSQL.toString());
				PreparedStatement pstm = this.con.prepareStatement(relSQL
						.toString());
				ResultSet prs = pstm.executeQuery();

				while (prs.next()) {
					rtag = prs.getString(1);
					int aid = prs.getInt(2);
					int bid = prs.getInt(3);

					PersonShortData rp = persMap.get(aid);
					if (rp != null) {

						if (rtag.equals("HUSB") || rtag.equals("WIFE")) {

							rp.setMarrCount(rp.getMarrCount() + 1);
						} else if (rtag.equals("CHIL")) {

							rp.setChildCount(rp.getChildCount() + 1);
						} else if (rtag.equals("MOTH") || rtag.equals("FATH")) {
							if (rtag.equals("MOTH")) {
								if (rp.getMotherPid() == 0) {
									rp.setMotherPid(bid);
								}
							} else {
								if (rp.getFatherPid() == 0) {
									rp.setFatherPid(bid);
								}
							}
							rp.setPareCount(rp.getPareCount() + 1);
						}
					}
				}
				prs.close();

			}

		} catch (Exception e) {

			throw new SukuException(e);
		}

		PersonShortData[] dt = new PersonShortData[0];
		SukuData qlist = new SukuData();
		qlist.pers = personList.toArray(dt);
		return qlist;
	}

	private String trimSpaces(String searchFullText) {
		StringBuilder sb = new StringBuilder();
		boolean wasSpace = true;
		for (int i = 0; i < searchFullText.length(); i++) {
			char c = searchFullText.charAt(i);
			if (c == ' ') {
				if (wasSpace) {
					continue;
				} else {
					wasSpace = true;
				}
			} else {
				wasSpace = false;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Encode text for sql-queries e.f. O'Brien => O''Brien
	 * 
	 * @param text
	 * @return encoded text
	 */
	private String toQuery(String text) {
		if (text == null)
			return null;
		if (text.indexOf('\'') < 0) {
			return text;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch (c) {
			case '\'':
				sb.append("''");
				break;
			default:
				sb.append(c);
			}
		}

		return sb.toString();

	}
}
