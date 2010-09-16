package fi.kaila.suku.server.utils;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;
import java.util.logging.Logger;

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
	 */
	public static SukuData uploadFamilies(Connection con, SukuData families)
			throws SQLException {

		SukuData respons = new SukuData();
		// first update pid values in LongPerson and Relation
		// if pid = 0 => get next pid. no relations
		// if pid < 0 => get next pid and use same pid both for unit and
		// relation

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
					} else {
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
			if (families.persons[i].getPid() <= 0 && respons.pidArray[i] <= 0) {
				respons.pidArray[i] = nextSeq(con, "unitseq");
			} else if (families.persons[i].getPid() > 0) {
				respons.pidArray[i] = families.persons[i].getPid();
			}
		}

		String sql = "insert into Unit (pid,tag,privacy,groupid,sex,sourcetext,privatetext,userrefn) "
				+ "values (?,?,?,?, ?,?,?,?)";
		String sqlnotice = "insert into unitnotice (pid,pnid,surety,noticerow,tag,noticetype,description,fromdate,"
				+ "place,village,farm,notetext,prefix,surname,givenname,patronym,postfix,sourcetext,RefNames) "
				+ "values (?,?,80,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?,?,?) ";

		PreparedStatement pstm = con.prepareStatement(sql);
		PreparedStatement pstmn = con.prepareStatement(sqlnotice);

		for (int i = 0; i < families.persons.length; i++) {

			PersonLongData person = families.persons[i];
			if (person != null) {
				if (person.getPid() <= 0) {

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
				}

				UnitNotice[] nots = person.getNotices();

				for (int j = 0; j < nots.length; j++) {
					UnitNotice n = nots[j];

					pstmn.setInt(1, person.getPid());

					pstmn.setInt(2, nextSeq(con, "unitnoticeseq"));
					pstmn.setInt(3, j + 1);
					pstmn.setString(4, n.getTag());
					pstmn.setString(5, n.getNoticeType());
					pstmn.setString(6, n.getDescription());
					pstmn.setString(7, n.getFromDate());
					pstmn.setString(8, n.getPlace());
					pstmn.setString(9, n.getVillage());
					pstmn.setString(10, n.getFarm());
					pstmn.setString(11, n.getNoteText());
					pstmn.setString(12, n.getPrefix());
					pstmn.setString(13, n.getSurname());
					pstmn.setString(14, n.getGivenname());
					pstmn.setString(15, n.getPatronym());
					pstmn.setString(16, n.getPostfix());
					pstmn.setString(17, n.getSource());

					if (n.getRefNames() == null) {
						pstmn.setNull(18, Types.ARRAY);
					} else {

						Array xx = con
								.createArrayOf("varchar", n.getRefNames());
						pstmn.setArray(18, xx);

					}

					int lukuri = pstmn.executeUpdate();
					if (lukuri != 1) {
						logger.warning("Update of UnitNotice "
								+ person.getPid() + " result " + lukuri
								+ " rows");
					}
				}
			}
		}

		if (families.relations == null)
			return respons;
		// create table Relation (
		// RID integer not null, -- Relation Id
		// PID integer not null references Unit(PID), -- Unit/Person Id
		// surety integer not null default 100, -- surety indicator
		// tag varchar, -- tag of relation
		// RelationRow integer, -- row of relation at person
		// Modified timestamp, -- timestamp modified
		// CreateDate timestamp not null default now() -- timestamp created
		// ) with oids;

		String sqlrn = "insert into relationnotice (rnid,rid,noticerow,tag,relationtype,"
				+ "description,fromdate,place,notetext,sourcetext) values (?,?,?,?,?, ?,?,?,?,?) ";

		pstmn = con.prepareStatement(sqlrn);

		sql = "insert into relation (rid,pid,surety,tag,relationrow) values (?,?,80,?,?) ";

		pstm = con.prepareStatement(sql);
		int rid;
		for (int i = 0; i < families.relations.length; i++) {
			Relation rel = families.relations[i];

			// System.out.println("rrr:" + rel.getPid() + "/" +
			// rel.getRelative()+ "/" + rel.getTag());
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

		}

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
		Vector<String> ll = new Vector<String>();
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
