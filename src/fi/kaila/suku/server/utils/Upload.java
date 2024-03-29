package fi.kaila.suku.server.utils;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Logger;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * upload of various data from server.
 * 
 * @author Kalle
 */
public class Upload {

	private static Logger logger = Logger.getLogger(Upload.class.getName());

	/**
	 * Used to upload hiski family to database.
	 * 
	 * @param con
	 *            the con
	 * @param families
	 *            the families
	 * @return null
	 * @throws SQLException
	 *             the sQL exception
	 * @throws SukuException
	 */
	public static SukuData uploadFamilies(Connection con, SukuData families)
			throws SQLException, SukuException {

		SukuData respons = new SukuData();

		respons.pidArray = new int[families.persons.length];
		for (int i = 0; i < respons.pidArray.length; i++) {
			respons.pidArray[i] = 0;
		}

		if (families.relations != null) {

			for (int i = 0; i < families.persons.length; i++) {

				PersonLongData pers = families.persons[i];
				if (pers != null) {
					int curPid = pers.getPid();

					if (curPid > 0) {
						respons.pidArray[i] = curPid;
					} else if (curPid < 0) {
						respons.pidArray[i] = nextSeq(con, "unitseq");
					}

					if (curPid < 0) {

						for (int j = 0; j < families.relations.length; j++) {
							Relation rel = families.relations[j];

							if (rel.getPid() == curPid) {
								rel.setPid(respons.pidArray[i]);
							}
							if (rel.getRelative() == curPid) {
								rel.setRelative(respons.pidArray[i]);
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < respons.pidArray.length; i++) {
			if (families.persons[i].getPid() == 0) {
				// TODO:
			} else if (families.persons[i].getPid() <= 0
					&& respons.pidArray[i] <= 0) {
				respons.pidArray[i] = nextSeq(con, "unitseq");
			} else if (families.persons[i].getPid() > 0) {
				respons.pidArray[i] = families.persons[i].getPid();
			}
		}

		// SukuData resp = Suku.kontroller.getSukuData("cmd=getsettings",
		// "type=order",
		// "name=notice");

		PersonUtil pu = new PersonUtil(con);
		SukuData orderdata = pu.getSettings(null, "order", "notice");
		String orders[] = new String[orderdata.generalArray.length + 1];
		orders[0] = "NAME";
		for (int i = 1; i < orders.length; i++) {
			orders[i] = orderdata.generalArray[i - 1];
		}
		String sql = "insert into Unit (pid,tag,privacy,groupid,sex,sourcetext,privatetext,userrefn) "
				+ "values (?,?,?,?, ?,?,?,?)";
		String sqlnotice = "insert into unitnotice (pid,pnid,surety,noticerow,tag,noticetype,description,dateprefix,fromdate,"
				+ "place,village,farm,notetext,prefix,surname,givenname,patronym,postfix,sourcetext,RefNames) "
				+ "values (?,?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?) ";

		PreparedStatement pstm = con.prepareStatement(sql);
		PreparedStatement pstmn = con.prepareStatement(sqlnotice);

		for (int i = 0; i < families.persons.length; i++) {
			int surety = 80;
			int rowno = 1;

			int hiskipnid = 0;
			String hiskiText = null;
			PersonLongData person = families.persons[i];

			if (person != null && person.getNotices() != null) {
				if (person.getPid() <= 0) {
					surety = 80;
					person.setPid(respons.pidArray[i]);
					pstm.setInt(1, person.getPid());
					pstm.setString(2, person.getTag());
					pstm.setString(3, person.getPrivacy());
					pstm.setString(4, person.getGroupId());
					pstm.setString(5, person.getSex());
					pstm.setString(6, person.getSource());
					pstm.setString(7, person.getPrivateText());
					pstm.setString(8, person.getRefn());

					int lukuri = pstm.executeUpdate();
					if (lukuri != 1) {
						logger.warning("Update of Unit " + person.getPid()
								+ " result " + lukuri + " rows");
					}
				} else {
					surety = 60;
					String sqlrow = "select max(noticerow) from unitnotice where pid = ?";
					PreparedStatement pstmr = con.prepareStatement(sqlrow);
					pstmr.setInt(1, person.getPid());
					ResultSet rs = pstmr.executeQuery();
					if (rs.next()) {
						rowno = rs.getInt(1) + 1;
					}
					rs.close();
					pstmr.close();

				}

				String sqlrow = "select pnid,noticerow,notetext from unitnotice "
						+ "where pid = ? and tag='HISKI' order by noticerow desc limit 1";
				PreparedStatement pstmr = con.prepareStatement(sqlrow);
				pstmr.setInt(1, person.getPid());
				ResultSet rs = pstmr.executeQuery();
				if (rs.next()) {
					hiskipnid = rs.getInt(1);
					// hiskiRow = rs.getInt(2);
					hiskiText = rs.getString(3);
				}
				rs.close();
				pstmr.close();
				String hupdate = "update unitnotice set notetext = ? where pnid = ?";

				UnitNotice[] nots = person.getNotices();

				for (int j = 0; j < nots.length; j++) {
					UnitNotice n = nots[j];
					if (n.getTag().equals("HISKI") && hiskipnid > 0) {
						if (hiskiText != null) {
							hiskiText += "\n\n" + n.getSource();
							hiskiText += "\n" + n.getNoteText();
						} else {
							hiskiText = n.getSource();
							hiskiText += "\n" + n.getNoteText();
						}
						PreparedStatement pstmh = con.prepareStatement(hupdate);
						pstmh.setString(1, hiskiText);
						pstmh.setInt(2, hiskipnid);
						pstmh.executeUpdate();

					} else {
						pstmn.setInt(1, person.getPid());
						int unitpnid = nextSeq(con, "unitnoticeseq");
						if (n.getTag().equals("HISKI")) {
							hiskipnid = unitpnid;
							hiskiText = n.getNoteText();
						}
						pstmn.setInt(2, unitpnid);
						pstmn.setInt(3, surety);
						pstmn.setInt(4, j + rowno);
						pstmn.setString(5, n.getTag());
						pstmn.setString(6, n.getNoticeType());
						pstmn.setString(7, n.getDescription());
						pstmn.setString(8, n.getDatePrefix());
						pstmn.setString(9, n.getFromDate());
						pstmn.setString(10, n.getPlace());
						pstmn.setString(11, n.getVillage());
						pstmn.setString(12, n.getFarm());
						pstmn.setString(13, n.getNoteText());
						pstmn.setString(14, n.getPrefix());
						pstmn.setString(15, n.getSurname());
						pstmn.setString(16, n.getGivenname());
						pstmn.setString(17, n.getPatronym());
						pstmn.setString(18, n.getPostfix());
						pstmn.setString(19, n.getSource());

						if (n.getRefNames() == null) {
							pstmn.setNull(20, Types.ARRAY);
						} else {

							Array xx = con.createArrayOf("varchar",
									n.getRefNames());
							pstmn.setArray(20, xx);

						}

						int lukuri = pstmn.executeUpdate();
						if (lukuri != 1) {
							logger.warning("Update of UnitNotice "
									+ person.getPid() + " result " + lukuri
									+ " rows");
						}
					}
				}

				/** put notices in correct order still */

				PersonUtil u = new PersonUtil(con);
				SukuData fam = u.getFullPerson(person.getPid(), null);

				ArrayList<UnitNotice> nns = new ArrayList<UnitNotice>();
				StringBuilder sb = new StringBuilder();
				for (int k = 0; k < orders.length; k++) {
					for (UnitNotice n : fam.persLong.getNotices()) {
						if (n.getTag().equals(orders[k])) {
							nns.add(n);
							sb.append("|" + orders[k]);
						}
					}
				}

				// now the ones that should be orderer are ordered
				// now add the rest to the end

				for (UnitNotice n : fam.persLong.getNotices()) {
					if (sb.toString().indexOf(n.getTag()) < 0) {
						nns.add(n);
					}
				}
				fam.persLong.setNotices(nns.toArray(new UnitNotice[0]));
				pu.updateNoticesOrder(fam.persLong);
			}
		}

		if (families.relations == null)
			return respons;

		String sqlrn = "insert into relationnotice (rnid,rid,noticerow,tag,relationtype,"
				+ "description,fromdate,place,notetext,sourcetext) values (?,?,?,?,?, ?,?,?,?,?) ";

		pstmn = con.prepareStatement(sqlrn);

		sql = "insert into relation (rid,pid,surety,tag,relationrow) values (?,?,80,?,?) ";

		String relaQuery = "select a.rid from relation a inner join relation b on a.rid=b.rid "
				+ "where a.pid=? and a.tag=? and b.pid=? and b.tag=?  ";

		PreparedStatement relaSt = con.prepareStatement(relaQuery);
		pstm = con.prepareStatement(sql);
		int rid;
		int foundrid = 0;
		for (int i = 0; i < families.relations.length; i++) {
			Relation rel = families.relations[i];

			if (rel.getTag().equals("WIFE")) {
				relaSt.setInt(1, rel.getPid());
				relaSt.setString(2, "WIFE");
				relaSt.setInt(3, rel.getRelative());
				relaSt.setString(4, "HUSB");
			} else {
				if (rel.getTag().equals("FATH")) {
					relaSt.setString(2, "FATH");
				} else {
					relaSt.setString(2, "MOTH");
				}
				relaSt.setInt(1, rel.getPid());
				relaSt.setInt(3, rel.getRelative());
				relaSt.setString(4, "CHIL");
			}
			ResultSet rrs = relaSt.executeQuery();

			if (rrs.next()) {
				foundrid = rrs.getInt(1);

			}
			rrs.close();

			if (foundrid == 0) {
				rid = nextSeq(con, "relationseq");

				pstm.setInt(1, rid);
				pstm.setInt(2, rel.getPid());
				if ("FATH".equals(rel.getTag()) || "MOTH".equals(rel.getTag())) {
					pstm.setString(3, rel.getTag());
				} else if ("HUSB".equals(rel.getTag())
						|| "WIFE".equals(rel.getTag())) {
					pstm.setString(3, rel.getTag());
				}
				pstm.setInt(4, 1);
				int luk = pstm.executeUpdate();
				if (luk != 1) {
					logger.warning("Update of Relation " + rel.getPid()
							+ " result " + luk + " rows");
				}

				pstm.setInt(1, rid);
				pstm.setInt(2, rel.getRelative());
				if ("FATH".equals(rel.getTag()) || "MOTH".equals(rel.getTag())) {
					pstm.setString(3, "CHIL");
				} else if ("HUSB".equals(rel.getTag())) {
					pstm.setString(3, "WIFE");
				} else if ("WIFE".equals(rel.getTag())) {
					pstm.setString(3, "HUSB");
				}
				pstm.setInt(4, 1);
				luk = pstm.executeUpdate();
				if (luk != 1) {
					logger.warning("Update of Relation " + rel.getRelative()
							+ " result " + luk + " rows");
				}

				int rnid = nextSeq(con, "relationnoticeseq");

				RelationNotice[] noti = rel.getNotices();
				if (noti != null) {
					for (int j = 0; j < noti.length; j++) {
						pstmn.setInt(1, rnid);
						pstmn.setInt(2, rid);
						pstmn.setInt(3, j + 1);
						pstmn.setString(4, noti[j].getTag());
						pstmn.setString(5, noti[j].getType());
						pstmn.setString(6, noti[j].getDescription());
						pstmn.setString(7, noti[j].getFromDate());
						pstmn.setString(8, noti[j].getPlace());
						pstmn.setString(9, noti[j].getNoteText());
						pstmn.setString(10, noti[j].getSource());
						luk = pstmn.executeUpdate();
						if (luk != 1) {
							logger.warning("Insert to RelationNotice "
									+ rel.getRelative() + " result " + luk
									+ " rows");
						}
					}
				}
			} else { // relation already exists. check relation notice still
				// we're only interested in MARR

				boolean addMarr = false;
				if (rel.getNotices() != null) {
					for (RelationNotice rn : rel.getNotices()) {
						if (rn.getTag().equals("MARR")) {
							addMarr = true;
						}
					}
					if (addMarr) {
						String sqlrela = "select count(*) from relationnotice where rid = ? and tag = 'MARR'";
						PreparedStatement relas = con.prepareStatement(sqlrela);
						relas.setInt(1, foundrid);
						ResultSet rrela = relas.executeQuery();
						boolean isMarried = false;
						if (rrela.next()) {
							if (rrela.getInt(1) > 0) {
								isMarried = true;
							}
						}
						rrela.close();
						relas.close();
						if (!isMarried) {
							int rnid = nextSeq(con, "relationnoticeseq");

							RelationNotice[] noti = rel.getNotices();
							if (noti != null) {
								for (int j = 0; j < noti.length; j++) {
									pstmn.setInt(1, rnid);
									pstmn.setInt(2, foundrid);
									pstmn.setInt(3, j + 1);
									pstmn.setString(4, noti[j].getTag());
									pstmn.setString(5, noti[j].getType());
									pstmn.setString(6, noti[j].getDescription());
									pstmn.setString(7, noti[j].getFromDate());
									pstmn.setString(8, noti[j].getPlace());
									pstmn.setString(9, noti[j].getNoteText());
									pstmn.setString(10, noti[j].getSource());
									int luk = pstmn.executeUpdate();
									if (luk != 1) {
										logger.warning("Insert to RelationNotice "
												+ rel.getRelative()
												+ " result " + luk + " rows");
									}
								}
							}

						}
					}
				}
			}

		}
		pstm.close();
		pstmn.close();
		relaSt.close();
		return respons;

	}

	private static int nextSeq(Connection con, String seqName)
			throws SQLException {
		Statement stm = con.createStatement();

		ResultSet rs = stm.executeQuery("select nextval('" + seqName + "')");
		int pnid = 0;
		if (rs.next()) {
			pnid = rs.getInt(1);
		}
		rs.close();

		return pnid;
	}

	/**
	 * get database version and do a vacuum.
	 * 
	 * @param con
	 *            the con
	 * @return [0] = version, [1] = time to vacuum
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static String[] getServerVersion(Connection con) throws SQLException {
		Statement stm = con.createStatement();

		ResultSet rs = stm.executeQuery("select version()");
		String vers[] = new String[2];
		if (rs.next()) {
			vers[0] = rs.getString(1);
		}
		rs.close();

		long starttime = System.currentTimeMillis();
		stm.executeUpdate("vacuum");
		long endtime = System.currentTimeMillis();
		vers[1] = "Vacuum in [" + (endtime - starttime) / 1000 + "] secs";

		return vers;
	}

	/**
	 * request list of report languages.
	 * 
	 * @param con
	 *            the con
	 * @return list of report languages in format lancode;langname
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static String[] getReportLanguages(Connection con)
			throws SQLException {
		Statement stm = con.createStatement();
		ArrayList<String> ll = new ArrayList<String>();
		ResultSet rs = stm
				.executeQuery("select langcode||';'||name from texts where tag='LANGUAGE'");
		String tmp;
		while (rs.next()) {
			tmp = rs.getString(1);
			ll.add(tmp);
		}
		rs.close();

		return ll.toArray(new String[0]);
	}

}
