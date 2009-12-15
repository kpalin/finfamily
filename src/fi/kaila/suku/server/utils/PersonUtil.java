package fi.kaila.suku.server.utils;

import java.awt.Dimension;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationLanguage;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitLanguage;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * Server class for update,insert and delete of person and relation data
 * 
 * @author Kalle
 * 
 */
public class PersonUtil {

	private static Logger logger = Logger.getLogger(PersonUtil.class.getName());

	private Connection con = null;

	/**
	 * Initlaize with database conmnection
	 * 
	 * @param con
	 */
	public PersonUtil(Connection con) {
		this.con = con;

	}

	/**
	 * Update the person/relation data
	 * 
	 * @param req
	 * @return result in resu field if failed
	 */
	public SukuData updatePerson(SukuData req) {

		String insPers = "insert into unit (pid,tag,privacy,groupid,sex,"
				+ "sourcetext,privatetext,userrefn) "
				+ "values (?,?,?,?,?,?,?,?)";

		String updPers = "update unit set privacy=?,sex=?,"
				+ "sourcetext=?,privatetext=?,userrefn=?,Modified=now() where pid = ?";

		String updSql = "update unitnotice set "
				+ "surety=?,Privacy=?,NoticeType=?,Description=?,"
				+ "DatePrefix=?,FromDate=?,ToDate=?,Place=?,"
				+ "Village=?,Farm=?,Croft=?,Address=?,"
				+ "PostalCode=?,PostOffice=?,State=?,Country=?,Email=?,"
				+ "NoteText=?,MediaFilename=?,MediaTitle=?,Prefix=?,"
				+ "Surname=?,Givenname=?,Patronym=?,PostFix=?,"
				+ "SourceText=?,PrivateText=?,RefNames=?,RefPlaces=?,Modified=now() "
				+ "where pnid=? ";

		String insSql = "insert into unitnotice  ("
				+ "surety,Privacy,NoticeType,Description,"
				+ "DatePrefix,FromDate,ToDate,Place,"
				+ "Village,Farm,Croft,Address,"
				+ "PostalCode,PostOffice,State,Country,Email,"
				+ "NoteText,MediaFilename,MediaTitle,Prefix,"
				+ "Surname,Givenname,Patronym,PostFix,"
				+ "SourceText,PrivateText,RefNames,Refplaces,pnid,pid,tag) values ("
				+ "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?) ";

		String updLangSql = "update unitlanguage set "
				+ "NoticeType=?,Description=?," + "Place=?,"
				+ "NoteText=?,MediaTitle=?,Modified=now() "
				+ "where pnid=? and langCode = ?";

		String insLangSql = "insert into unitlanguage (pnid,pid,tag,langcode,"
				+ "NoticeType,Description,Place,"
				+ "NoteText,MediaTitle) values (?,?,?,?,?,?,?,?,?)";
		String delOneLangSql = "delete from unitlanguage where pnid = ? and langcode = ? ";
		String updRowSql = "update unitnotice set noticerow = ? where pnid = ? ";

		String delSql = "delete from unitnotice where pnid = ? ";
		String delAllLangSql = "delete from Unitlanguage where pnid = ? ";

		SukuData res = new SukuData();
		UnitNotice[] nn = req.persLong.getNotices();
		int pid = 0;
		try {
			Statement stm;
			PreparedStatement pst;

			if (req.persLong.getPid() > 0) { // insert new person

				res.resultPid = req.persLong.getPid();
				pid = req.persLong.getPid();

				if (req.persLong.isMainModified()) {

					// String updPers =
					// "update unit set privacy=?,groupid=?,sex=?," +
					// "sourcetext=?,privatetext=?,userrefn=?,Modified=now() ";
					pst = con.prepareStatement(updPers);

					pst.setString(1, req.persLong.getPrivacy());
					pst.setString(2, req.persLong.getSex());
					pst.setString(3, req.persLong.getSource());
					pst.setString(4, req.persLong.getPrivateText());
					pst.setString(5, req.persLong.getRefn());
					pst.setInt(6, req.persLong.getPid());
					int lukuri = pst.executeUpdate();
					if (lukuri != 1) {
						logger.warning("Person updated for pid " + pid
								+ "  gave result " + lukuri);
					}
				}

			} else {
				stm = con.createStatement();
				ResultSet rs = stm.executeQuery("select nextval('unitseq')");

				if (rs.next()) {
					pid = rs.getInt(1);
					res.resultPid = pid;
				} else {
					throw new SQLException("Sequence unitseq error");
				}
				rs.close();
				pst = con.prepareStatement(insPers);
				pst.setInt(1, pid);
				pst.setString(2, req.persLong.getTag());
				pst.setString(3, req.persLong.getPrivacy());
				pst.setString(4, req.persLong.getGroupId());
				pst.setString(5, req.persLong.getSex());
				pst.setString(6, req.persLong.getSource());
				pst.setString(7, req.persLong.getPrivateText());
				pst.setString(8, req.persLong.getRefn());
				int lukuri = pst.executeUpdate();
				if (lukuri != 1) {
					logger.warning("Person created for pid " + pid
							+ "  gave result " + lukuri);
				}
			}

			PreparedStatement pstDel = con.prepareStatement(delSql);
			PreparedStatement pstDelLang = con.prepareStatement(delAllLangSql);
			PreparedStatement pstUpdRow = con.prepareStatement(updRowSql);
			if (nn != null) {
				for (int i = 0; i < nn.length; i++) {
					UnitNotice n = nn[i];
					int pnid = 0;
					if (n.isToBeDeleted()) {
						pstDelLang.setInt(1, n.getPnid());
						int landelcnt = pstDelLang.executeUpdate();
						pstDel.setInt(1, n.getPnid());
						int delcnt = pstDel.executeUpdate();
						String text = "Poistettiin " + delcnt + " riviä ["
								+ landelcnt + "] kieliversiota pid = "
								+ n.getPid() + " tag=" + n.getTag();
						// System.out.println(text);
						logger.fine(text);
					} else if (n.getPnid() == 0 || n.isToBeUpdated()) {

						if (n.getPnid() == 0) {// is this new i.e. insert

							stm = con.createStatement();
							ResultSet rs = stm
									.executeQuery("select nextval('unitnoticeseq')");

							if (rs.next()) {
								pnid = rs.getInt(1);
							} else {
								throw new SQLException(
										"Sequence unitnoticeseq error");
							}
							rs.close();
							pst = con.prepareStatement(insSql);
						} else {
							pst = con.prepareStatement(updSql);
							pnid = n.getPnid();
						}

						if (n.isToBeUpdated() || n.getPnid() == 0) {

							pst.setInt(1, n.getSurety());
							pst.setString(2, n.getPrivacy());
							pst.setString(3, n.getNoticeType());
							pst.setString(4, n.getDescription());
							pst.setString(5, n.getDatePrefix());
							pst.setString(6, n.getFromDate());
							pst.setString(7, n.getToDate());
							pst.setString(8, n.getPlace());
							pst.setString(9, n.getVillage());
							pst.setString(10, n.getFarm());
							pst.setString(11, n.getCroft());
							pst.setString(12, n.getAddress());
							pst.setString(13, n.getPostalCode());
							pst.setString(14, n.getPostOffice());
							pst.setString(15, n.getState());
							pst.setString(16, n.getCountry());
							pst.setString(17, n.getEmail());
							pst.setString(18, n.getNoteText());
							pst.setString(19, n.getMediaFilename());
							pst.setString(20, n.getMediaTitle());
							pst.setString(21, n.getPrefix());
							pst.setString(22, n.getSurname());
							pst.setString(23, n.getGivenname());
							pst.setString(24, n.getPatronym());
							pst.setString(25, n.getPostfix());
							pst.setString(26, n.getSource());
							pst.setString(27, n.getPrivateText());
							if (n.getRefNames() == null) {
								pst.setNull(28, Types.ARRAY);
							} else {

								Array xx = con.createArrayOf("varchar", n
										.getRefNames());
								pst.setArray(28, xx);

							}
							if (n.getRefPlaces() == null) {
								pst.setNull(29, Types.ARRAY);
							} else {

								Array xx = con.createArrayOf("varchar", n
										.getRefPlaces());
								pst.setArray(29, xx);

							}
						}
						if (n.getPnid() > 0) {
							pst.setInt(30, n.getPnid());
							int luku = pst.executeUpdate();
							// System.out.println("Päivitettiin " + luku +
							// " tietuetta");
							logger.fine("Päivitettiin " + luku
									+ " tietuetta pnid=[" + n.getPnid() + "]");
						} else {
							pst.setInt(30, pnid);
							pst.setInt(31, pid);
							pst.setString(32, n.getTag());
							int luku = pst.executeUpdate();
							// System.out.println("Luotiin " + luku +
							// " uusi tietue");
							logger.fine("Luotiin " + luku + " tietue pnid=["
									+ pnid + "]");
						}

						if (n.getMediaData() == null) {
							String sql = "update unitnotice set mediadata = null where pnid = ?";
							pst = con.prepareStatement(sql);
							pst.setInt(1, pnid);
							int lukuri = pst.executeUpdate();
							if (lukuri != 1) {
								logger.warning("media deleted for pnid "
										+ n.getPnid() + " gave result "
										+ lukuri);
							}
						} else {
							String UPDATE_IMAGE_DATA = "update UnitNotice set MediaData = ?,"
									+ "mediaWidth = ?,mediaheight = ? where PNID = ? ";

							PreparedStatement ps = this.con
									.prepareStatement(UPDATE_IMAGE_DATA);

							ps.setBytes(1, n.getMediaData());
							Dimension d = n.getMediaSize();
							ps.setInt(2, d.width);
							ps.setInt(3, d.height);
							ps.setInt(4, pnid);
							ps.executeUpdate();
						}

					}

					if (n.getLanguages() != null) {

						for (int l = 0; l < n.getLanguages().length; l++) {
							UnitLanguage ll = n.getLanguages()[l];
							if (ll.isToBeDeleted()) {
								if (ll.getPnid() > 0) {
									pst = con.prepareStatement(delOneLangSql);
									pst.setInt(1, ll.getPnid());
									pst.setString(2, ll.getLangCode());
									int lukuri = pst.executeUpdate();
									if (lukuri != 1) {
										logger
												.warning("language deleted for pnid "
														+ n.getPnid()
														+ " ["
														+ ll.getLangCode()
														+ "] gave result "
														+ lukuri);
									}
								}
							}

							if (ll.isToBeUpdated()) {

								if (ll.getPnid() == 0) {
									//								
									pst = con.prepareStatement(insLangSql);
									pst.setInt(1, n.getPnid());
									pst.setInt(2, pid);
									pst.setString(3, n.getTag());
									pst.setString(4, ll.getLangCode());
									pst.setString(5, ll.getNoticeType());
									pst.setString(6, ll.getDescription());
									pst.setString(7, ll.getPlace());
									pst.setString(8, ll.getNoteText());
									pst.setString(9, ll.getMediaTitle());
									int lukuri = pst.executeUpdate();
									if (lukuri != 1) {
										logger
												.warning("language added for pnid "
														+ n.getPnid()
														+ " ["
														+ ll.getLangCode()
														+ "] gave result "
														+ lukuri);
									}

								} else {
									pst = con.prepareStatement(updLangSql);
									pst.setString(1, ll.getNoticeType());
									pst.setString(2, ll.getDescription());
									pst.setString(3, ll.getPlace());
									pst.setString(4, ll.getNoteText());
									pst.setString(5, ll.getMediaTitle());
									pst.setInt(6, ll.getPnid());
									pst.setString(7, ll.getLangCode());
									int lukuri = pst.executeUpdate();
									if (lukuri != 1) {
										logger.warning("language for pnid "
												+ ll.getPnid() + " ["
												+ ll.getLangCode()
												+ "] gave result " + lukuri);
									}
									pst.close();
								}
							}

						}

					}

					if (n.getPnid() > 0) {
						pnid = n.getPnid();
					}
					pstUpdRow.setInt(1, i + 1);
					pstUpdRow.setInt(2, pnid);
					pstUpdRow.executeUpdate();
				}
			}
		} catch (SQLException e) {
			// e.printStackTrace();
			logger.log(Level.WARNING, "person update", e);
			res.resu = e.getMessage();
			return res;

		}
		if (req.relations != null) {
			updateRelations(req);
		}
		return res;

	}

	private SukuData updateRelations(SukuData req) {
		String updSql = "update relationnotice set "
				+ "surety=?,RelationType=?,Description=?,"
				+ "DatePrefix=?,FromDate=?,ToDate=?,Place=?,"
				+ "NoteText=?,SourceText=?,PrivateText=?,Modified=now()" +
				// "SourceText=?,PrivateText=?,Modified=now() " +
				"where rnid=? ";

		String insSql = "insert into relationnotice  "
				+ "(surety,RelationType,Description,DatePrefix,FromDate,ToDate,"
				+ "Place,NoteText,sourcetext,privatetext,rnid,rid,tag,noticerow)"
				+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,0) ";

		String updLangSql = "update relationlanguage set "
				+ "RelationType=?,Description=?,Place=?,"
				+ "NoteText=?,Modified=now()" +
				// "SourceText=?,PrivateText=?,Modified=now() " +
				"where rnid=? and langcode = ?";

		String insLangSql = "insert into relationlanguage  "
				+ "(rnid,rid,langcode,RelationType,Description,Place,NoteText) "
				+ " values (?,?,?,?,?,?,?) ";

		String delLangSql = "delete from relationlanguage where rnid=? and langcode = ?";

		String insRelSql = "insert into relation (rid,pid,surety,tag,relationrow) values (?,?,?,?,?) ";
		String updRowSql = "update relation set relationrow = ? where rid = ? and pid = ?";

		SukuData res = new SukuData();
		SukuData ffmm = null;
		try {

			PreparedStatement pst;
			Statement stm;
			String delRel = "delete from relation where rid = ?";
			String delRelNoti = "delete from relationnotice where rid = ?";
			String delRelLangu = "delete from relationlanguage where rid = ?";

			for (int i = 0; i < req.relations.length; i++) {
				Relation r = req.relations[i];
				int rid = r.getRid();
				if (req.relations[i].isToBeDeleted() && rid > 0) {

					pst = con.prepareStatement(delRelLangu);
					pst.setInt(1, rid);
					int laskLang = pst.executeUpdate();
					pst = con.prepareStatement(delRelNoti);
					pst.setInt(1, rid);
					int laskNoti = pst.executeUpdate();
					pst = con.prepareStatement(delRel);
					pst.setInt(1, rid);
					int laskRel = pst.executeUpdate();
					logger.info("deleted relation [" + r.getTag() + "]" + rid
							+ " between " + r.getPid() + "/" + r.getRelative()
							+ " result [" + laskRel + "/" + laskNoti + "/"
							+ laskLang + "]");
				} else if (req.relations[i].isToBeUpdated()) {

					if (rid == 0) {
						stm = con.createStatement();
						ResultSet rs = stm
								.executeQuery("select nextval('relationseq')");

						if (rs.next()) {
							rid = rs.getInt(1);
						} else {
							throw new SQLException("Sequence relationseq error");
						}
						rs.close();
						r.setRid(rid);
						if (r.getPid() == req.persLong.getPid()) {

							pst = con.prepareStatement(insRelSql);

							pst.setInt(1, rid);
							pst.setInt(2, r.getPid());
							pst.setInt(3, r.getSurety());
							pst.setString(4, r.getTag());
							pst.setInt(5, 10);
							// TODO the rownumber
							int lukuri = pst.executeUpdate();
							if (lukuri != 1) {
								logger.warning("relation for rid " + rid
										+ "  gave result " + lukuri);
							}

							String tag;
							if (r.getTag().equals("CHIL")) {
								if (req.persLong.getSex().equals("M")) {
									tag = "FATH";
								} else {
									tag = "MOTH"; // or mother
								}
							} else if (r.getTag().equals("FATH")
									|| r.getTag().equals("MOTH")) {
								tag = "CHIL";
							} else if (r.getTag().equals("HUSB")) {
								tag = "WIFE";
							} else {
								tag = "HUSB";
							}
							pst.setInt(1, rid);
							pst.setInt(2, r.getRelative());
							pst.setInt(3, r.getSurety());

							pst.setString(4, tag);
							pst.setInt(5, 10);
							// TODO the rownumber
							lukuri = pst.executeUpdate();
							if (lukuri != 1) {
								logger.warning("relation for rid " + rid
										+ "  gave result " + lukuri);
							}
						} else {
							//
							// here for the special MOTH/FATH relations
							//

							if (r.getTag().equals("FATH")
									|| r.getTag().equals("MOTH")) {
								pst = con.prepareStatement(insRelSql);
								pst.setInt(1, rid);
								pst.setInt(2, r.getPid());
								pst.setInt(3, r.getSurety());
								pst.setString(4, r.getTag());
								pst.setInt(5, 10);
								// TODO the rownumber
								int lukuri = pst.executeUpdate();
								if (lukuri != 1) {
									logger.warning("other relation for rid "
											+ rid + " [" + r.getTag()
											+ "]  gave result " + lukuri);
								}

								pst.setInt(1, rid);
								pst.setInt(2, r.getRelative());
								pst.setInt(3, r.getSurety());

								pst.setString(4, "CHIL");
								pst.setInt(5, 10);
								// TODO the rownumber
								lukuri = pst.executeUpdate();
								if (lukuri != 1) {
									logger.warning("other relation for rid "
											+ rid + " [CHIL]  gave result "
											+ lukuri);
								}
							}

							ffmm = getFullPerson(r.getRelative(), null);
							Vector<Relation> ffvec = new Vector<Relation>();
							Relation newrel = null;
							for (int j = 0; j < ffmm.relations.length; j++) {
								Relation rfm = ffmm.relations[j];
								if (rfm.getTag().equals("CHIL")) {
									for (int k = 0; k < ffmm.pers.length; k++) {
										PersonShortData pfm = ffmm.pers[k];
										if (pfm.getPid() == rfm.getRelative()) {
											rfm.setShortPerson(pfm);
										}
									}
									if (rfm.getRid() == rid) {
										newrel = rfm;
									} else {
										ffvec.add(rfm);
									}
								}
							}
							if (newrel == null
									|| newrel.getShortPerson() == null
									|| newrel.getShortPerson().getBirtDate() == null
									|| newrel.getShortPerson().getBirtDate()
											.equals("")) {
								newrel = null;
							} else {
								for (int j = 0; j < ffvec.size(); j++) {
									Relation rfm = ffvec.get(j);
									if (rfm.getShortPerson() == null
											|| rfm.getShortPerson()
													.getBirtDate() == null
											|| rfm.getShortPerson()
													.getBirtDate().equals("")) {
										ffvec.insertElementAt(newrel, j);
										newrel = null;
										break;
									} else {
										if (newrel.getShortPerson()
												.getBirtDate().compareTo(
														rfm.getShortPerson()
																.getBirtDate()) < 0) {
											ffvec.insertElementAt(newrel, j);
											newrel = null;
											break;
										}
									}
								}
								if (newrel != null) {
									ffvec.add(newrel);
								}

								// order childnotices for father or mother

								for (int rivi0 = 0; rivi0 < ffvec.size(); rivi0++) {
									Relation rr = ffvec.get(rivi0);

									pst = con.prepareStatement(updRowSql);
									pst.setInt(1, rivi0 + 1);
									pst.setInt(2, rr.getRid());
									pst.setInt(3, rr.getPid());
									int lukuri = pst.executeUpdate();
									logger.finest("RELAFFMMROW # " + lukuri);

								}

							}

						}
					} else {
						String updSureSql = "update relation set surety = ?,Modified=now() where rid = ?";

						PreparedStatement updLang = con
								.prepareStatement(updSureSql);
						updLang.setInt(1, r.getSurety());

						updLang.setInt(2, r.getRid());

						int rner = updLang.executeUpdate();
						logger.fine("Surety set to " + r.getSurety()
								+ " for rid " + r.getRid() + " cnt " + rner);
					}
				}
				if (req.relations[i].getNotices() != null) {

					String updnorder = "update relationNotice set noticerow = ? where rnid = ?";
					PreparedStatement rorder = con.prepareStatement(updnorder);

					for (int j = 0; j < req.relations[i].getNotices().length; j++) {
						RelationNotice rn = req.relations[i].getNotices()[j];
						int rnid = rn.getRnid();
						if (rn.isToBeDeleted() && rnid > 0) {

							String sqlNoti = "delete from relationnotice where rnid = ?";
							String sqlRelLangu = "delete from relationlanguage where rnid = ?";

							pst = con.prepareStatement(sqlRelLangu);
							pst.setInt(1, rnid);
							int laskLang = pst.executeUpdate();
							pst = con.prepareStatement(sqlNoti);
							pst.setInt(1, rnid);
							int laskNoti = pst.executeUpdate();

							logger.info("deleted relationnotice [" + r.getTag()
									+ "" + rid + " between " + r.getPid() + "/"
									+ r.getRelative() + " result [" + laskNoti
									+ "/" + laskLang);
						} else {
							if (rn.isToBeUpdated() || rnid == 0) {

								if (rn.getRnid() == 0) {
									stm = con.createStatement();
									ResultSet rs = stm
											.executeQuery("select nextval('RelationNoticeSeq')");

									if (rs.next()) {
										rnid = rs.getInt(1);
									} else {
										throw new SQLException(
												"Sequence relationseq error");
									}
									rs.close();

									pst = con.prepareStatement(insSql);

								} else {
									pst = con.prepareStatement(updSql);
								}

								pst.setInt(1, rn.getSurety());
								pst.setString(2, rn.getType());
								pst.setString(3, rn.getDescription());
								pst.setString(4, rn.getDatePrefix());
								pst.setString(5, rn.getFromDate());
								pst.setString(6, rn.getToDate());
								pst.setString(7, rn.getPlace());
								pst.setString(8, rn.getNoteText());
								pst.setString(9, rn.getSource());
								pst.setString(10, rn.getPrivateText());

								if (rn.getRnid() > 0) {
									pst.setInt(11, rnid);
									int rer = pst.executeUpdate();
									logger.fine("update rn for " + rnid + "["
											+ rer + "]");
								} else {

									pst.setInt(11, rnid);
									pst.setInt(12, rid);
									pst.setString(13, rn.getTag());
									int rer = pst.executeUpdate();
									logger.fine("insert rn for " + rnid + "["
											+ rer + "]");

								}
							}

							rorder.setInt(1, j);
							rorder.setInt(2, rnid);
							int orderit = rorder.executeUpdate();

							logger.finest("RN order lkm = " + orderit);
							if (rn.getLanguages() != null) {

								for (int k = 0; k < rn.getLanguages().length; k++) {
									RelationLanguage rl = rn.getLanguages()[k];
									if (rl.isToBeUpdated()) {
										if (rl.getRnid() == 0) {
											PreparedStatement updLang = con
													.prepareStatement(insLangSql);
											// "(rnid,rid,langcode,RelationType,Description,Place,NoteText) "
											// +
											updLang.setInt(1, rnid);
											updLang.setInt(2, rid);
											updLang.setString(3, rl
													.getLangCode());
											updLang.setString(4, rl
													.getRelationType());
											updLang.setString(5, rl
													.getDescription());
											updLang.setString(6, rl.getPlace());
											updLang.setString(7, rl
													.getNoteText());

											int rier = updLang.executeUpdate();
											logger.fine("insert rl rnid: "
													+ rnid + "/"
													+ rl.getLangCode()
													+ "count:[" + rier + "]");

										} else if (rl.isToBeDeleted()) {
											PreparedStatement updLang = con
													.prepareStatement(delLangSql);
											updLang.setInt(1, rnid);
											updLang.setString(2, rl
													.getLangCode());
											int rder = updLang.executeUpdate();
											logger.fine("delete rl rnid: "
													+ rnid + "/"
													+ rl.getLangCode()
													+ "count:[" + rder + "]");

										} else {

											PreparedStatement updLang = con
													.prepareStatement(updLangSql);
											updLang.setString(1, rl
													.getRelationType());
											updLang.setString(2, rl
													.getDescription());
											updLang.setString(3, rl.getPlace());
											updLang.setString(4, rl
													.getNoteText());
											updLang.setInt(5, rl.getRnid());
											updLang.setString(6, rl
													.getLangCode());
											int rner = updLang.executeUpdate();
											logger.fine("update rl for "
													+ rl.getRnid() + "/"
													+ rl.getLangCode() + "["
													+ rner + "]");
										}
									}
								}
							}
						}
					}

					// some ordering still

				}
			}

			//
			// set the order still
			// 
			int childRow = 0;
			int parentRow = 0;
			int spouseRow = 0;
			int thisRow = 0;

			for (int i = 0; i < req.relations.length; i++) {
				Relation r = req.relations[i];
				if (r.getPid() == req.persLong.getPid()) {
					if (r.getTag().equals("CHIL")) {
						thisRow = ++childRow;
					} else if (r.getTag().equals("HUSB")
							|| r.getTag().equals("WIFE")) {
						thisRow = ++spouseRow;
					} else {
						thisRow = ++parentRow;
					}

					pst = con.prepareStatement(updRowSql);
					pst.setInt(1, thisRow);
					pst.setInt(2, r.getRid());
					pst.setInt(3, r.getPid());
					int lukuri = pst.executeUpdate();

					logger.finest("RELAROW # " + lukuri);

				}
			}

		} catch (SQLException e) {
			// e.printStackTrace();
			logger.log(Level.WARNING, "Relation update", e);
			res.resu = e.getMessage();
			return res;

		} catch (SukuException e) {
			logger.log(Level.WARNING, "Person load", e);
			res.resu = e.getMessage();
			return res;
		}

		return res;

	}

	/**
	 * delete all data for the person
	 * 
	 * @param pid
	 * @return status of delete operation in resu field if error
	 */
	public SukuData deletePerson(int pid) {
		SukuData res = new SukuData();

		// first delete relations

		String sqlrlang = "delete from relationlanguage where rid in "
				+ "(select rid from relation where pid = ?)";

		String sqlrnoti = "delete from relationnotice where rid in "
				+ "(select rid from relation where pid = ?)";

		String sqlr = "delete from relation where rid in "
				+ "(select rid from relation where pid = ?)";

		String sqlul = "delete from unitlanguage where pid = ?";

		String sqlun = "delete from unitnotice where pid = ? ";

		String sqlu = "delete from unit where pid=?";

		try {
			PreparedStatement pst = con.prepareStatement(sqlrlang);
			pst.setInt(1, pid);
			int lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] relationlanguage count:"
					+ lukuri);
			pst.close();

			pst = con.prepareStatement(sqlrnoti);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] relationnotice count:" + lukuri);
			pst.close();

			pst = con.prepareStatement(sqlr);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] relation count:" + lukuri);
			pst.close();

			pst = con.prepareStatement(sqlul);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] unitlanguage count:" + lukuri);
			pst.close();

			pst = con.prepareStatement(sqlun);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] unitnotice count:" + lukuri);
			pst.close();

			pst = con.prepareStatement(sqlu);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] unit count:" + lukuri);
			pst.close();

		} catch (SQLException e) {
			res.resu = e.getMessage();
			logger.log(Level.WARNING, "Deleting person " + pid, e);
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * Full person sisältää
	 * 
	 * SukuData siirto-oliossa
	 * 
	 * henkilön tiedot: persLong tietojaksot: persLong.notices[]
	 * sukulaisuussuheet: relations[] sukujaksot: rellations[i].notices[]
	 * 
	 * @param pid
	 * @param lang
	 * @return SukuData result
	 * @throws SukuException
	 */

	public SukuData getFullPerson(int pid, String lang) throws SukuException {
		SukuData pers = new SukuData();
		Vector<UnitNotice> nvec = new Vector<UnitNotice>();

		Vector<Relation> rels = new Vector<Relation>();
		Vector<RelationNotice> relNotices = null;
		try {
			String sql = "select * from unit where pid = ? ";
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, pid);

			ResultSet rs = pstm.executeQuery();

			if (rs.next()) {
				pers.persLong = new PersonLongData(rs);
			}
			rs.close();
			pstm.close();
			if (pers.persLong != null) {
				UnitNotice notice;
				if (lang == null) {
					sql = "select * from unitnotice where pid = ? order by noticerow ";
				} else {
					sql = "select * from unitnotice_" + lang
							+ " where pid = ? order by noticerow ";
				}
				pstm = con.prepareStatement(sql);
				pstm.setInt(1, pid);
				rs = pstm.executeQuery();
				while (rs.next()) {
					notice = new UnitNotice(rs);
					nvec.add(notice);
				}
				rs.close();
				pstm.close();
				pers.persLong.setNotices(nvec.toArray(new UnitNotice[0]));

				if (lang == null) {
					Vector<UnitLanguage> lvec = new Vector<UnitLanguage>();
					UnitLanguage langnotice;
					sql = "select * from unitlanguage where pid = ? order by pnid,langcode";

					pstm = con.prepareStatement(sql);
					pstm.setInt(1, pid);
					rs = pstm.executeQuery();
					while (rs.next()) {
						langnotice = new UnitLanguage(rs);
						lvec.add(langnotice);
					}
					rs.close();
					pstm.close();

					Vector<UnitLanguage> llvec = null;

					for (int i = 0; i < pers.persLong.getNotices().length; i++) {
						UnitNotice noti = pers.persLong.getNotices()[i];
						llvec = new Vector<UnitLanguage>();
						for (int j = 0; j < lvec.size(); j++) {
							UnitLanguage ul = lvec.get(j);
							if (noti.getPnid() == ul.getPnid()) {
								llvec.add(ul);
							}
						}
						if (llvec.size() > 0) {
							noti.setLanguages(llvec
									.toArray(new UnitLanguage[0]));
						}

					}

					// pers.persLong.setLanguages(lvec.toArray(new
					// UnitLanguage[0]));
				}

				
				sql = "select a.rid,a.pid,b.pid,a.tag,a.surety,a.modified,a.createdate  " +
						"from relation a inner join relation b on a.rid=b.rid " +
						"where a.pid <> b.pid and a.pid=? order by a.tag,a.relationrow";
				pstm = con.prepareStatement(sql);
				pstm.setInt(1, pid);
				rs = pstm.executeQuery();
				int rid;
				int aid;
				int bid;
				String tag;
				Relation rel = null;
				
			
				Vector<Integer> relpids = new Vector<Integer>();
				LinkedHashMap<Integer,Relation> relmap = new LinkedHashMap<Integer,Relation>(); 
				while (rs.next()) {
					rid = rs.getInt(1);
					aid = rs.getInt(2);
					bid = rs.getInt(3);
					tag = rs.getString(4);
					relpids.add(bid);
					
					rel = new Relation(rid, aid, bid, tag, rs
							.getInt(5), rs.getTimestamp(6), rs
							.getTimestamp(7));
					rels.add(rel);
					relmap.put(rid, rel);

				}
				rs.close();
				
				sql = "select * from relationnotice " 
						+ "where rid in (select rid from relation where pid=?) order by rid,noticerow";
				pstm = con.prepareStatement(sql);
				pstm.setInt(1, pid);
				rs = pstm.executeQuery();
				int curid=0;
				rid=0;
				RelationNotice rnote = null;
				rs = pstm.executeQuery();
				relNotices = new Vector<RelationNotice>();
				while (rs.next()){
					rid=rs.getInt("rid");
					if (rid != curid) {
						rel = relmap.get(Integer.valueOf(curid));
						if (rel != null  && relNotices.size()>0){							
							rel.setNotices(relNotices.toArray(new RelationNotice[0]));							
						}
						relNotices=null;
						curid=rid;
						relNotices = new Vector<RelationNotice>();
					} 
						
					rnote = new RelationNotice(rs.getInt("rnid"), rid, rs
							.getInt("surety"), rs.getString("tag"), rs.getString("relationtype"), rs
							.getString("description"), rs.getString("dateprefix"), rs
							.getString("fromdate"), rs.getString("todate"), rs
							.getString("place"), rs.getString("notetext"), rs
							.getString("sourcetext"), rs.getString("privatetext"), rs
							.getTimestamp("modified"), rs.getTimestamp("createdate"));						
					relNotices.add(rnote);
				}
				
				if ( relNotices.size()>0){	
					rel = relmap.get(Integer.valueOf(curid));
					if (rel != null ){							
						rel.setNotices(relNotices.toArray(new RelationNotice[0]));
					}
				}
				rs.close();
				pstm.close();
				

				if (rels.size() > 0) {
					pers.relations = rels.toArray(new Relation[0]);
				} else {
					pers.relations = new Relation[0];
				}

				//
				// lets still pick up the language variants
				//

				for (int i = 0; i < pers.relations.length; i++) {
					if (pers.relations[i].getNotices() != null) {
						for (int j = 0; j < pers.relations[i].getNotices().length; j++) {
							RelationNotice rn = pers.relations[i].getNotices()[j];
							Vector<RelationLanguage> rl = new Vector<RelationLanguage>();

							sql = "select rnid,rid,langcode,relationtype,description,place,notetext,modified,createdate "
									+ "from relationLanguage where rnid = ?";

							pstm = con.prepareStatement(sql);
							pstm.setInt(1, rn.getRnid());
							rs = pstm.executeQuery();
							while (rs.next()) {
								RelationLanguage rrl = new RelationLanguage(rs);
								rl.add(rrl);
							}
							rs.close();
							pstm.close();

							if (rl.size() > 0) {
								rn.setLanguages(rl
										.toArray(new RelationLanguage[0]));
							}

							// if
							// (pers.relations[i].getNotices()[j].getLanguages()
							// != null){
							//								
							//								
							//								
							// }
						}
					}
				}

				if (lang == null) {
					Vector<PersonShortData> pv = new Vector<PersonShortData>();
					HashMap<Integer, Integer> testPid = new HashMap<Integer, Integer>();
					for (int i = 0; i < relpids.size(); i++) {
						Integer test = relpids.get(i);

						if (testPid.put(test, test) == null) {
							// System.out.println("kalleko:" + test.intValue());
							PersonShortData p = new PersonShortData(this.con,
									test.intValue());
							pv.add(p);
						}
					}
					pers.pers = pv.toArray(new PersonShortData[0]);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new SukuException(e);
		}
		return pers;
	}

	public String insertGedcomRelations(int husbandNumber,int wifeNumber,Relation [] relations) {

		String insSql = "insert into relationnotice  "
			+ "(surety,RelationType,Description,DatePrefix,FromDate,ToDate,"
			+ "Place,NoteText,sourcetext,privatetext,rnid,rid,tag,noticerow)"
			+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		String insRelSql = "insert into relation (rid,pid,surety,tag,relationrow) values (?,?,?,?,?) ";

		ResultSet rs;
		try {

			PreparedStatement pst;
			Statement stm;
			
			int childForFatherRow=husbandNumber*50;
			int childForMotherRow=wifeNumber*50;
			
			int aid=0;
			int bid=0;
			
			for (int i = 0; i < relations.length; i++) {
				Relation r = relations[i];
				int rid = r.getRid();

				stm = con.createStatement();
				rs = stm
				.executeQuery("select nextval('relationseq')");

				if (rs.next()) {
					rid = rs.getInt(1);
				} else {
					throw new SQLException("Sequence relationseq error");
				}
				rs.close();
				r.setRid(rid);

				pst = con.prepareStatement(insRelSql);

				pst.setInt(1, rid);
				pst.setInt(2, r.getPid());
				pst.setInt(3, r.getSurety());
				pst.setString(4, r.getTag());
				if (r.getTag().equals("WIFE")) {
					pst.setInt(5, wifeNumber);
					aid=r.getPid();
				} else  {
					pst.setInt(5,1);
				}
//				pst.setInt(5, childRow);
				int lukuri = pst.executeUpdate();
				if (lukuri != 1) {
					logger.warning("relation for rid " + rid
							+ "  gave result " + lukuri);
				}

				String tag;
				if (r.getTag().equals("FATH")
						|| r.getTag().equals("MOTH")) {
					tag = "CHIL";
				} else {
					tag = "HUSB";
					bid=r.getRelative();
				}
				pst.setInt(1, rid);
				pst.setInt(2, r.getRelative());
				pst.setInt(3, r.getSurety());
				pst.setString(4, tag);
				if (tag.equals("HUSB")){
					pst.setInt(5, husbandNumber);
				} else {
					if (r.getTag().equals("FATH")){
						pst.setInt(5, childForMotherRow++);
					} else {
						pst.setInt(5, childForFatherRow++);
					}
				}
				lukuri = pst.executeUpdate();
				if (lukuri != 1) {
					logger.warning("relation for rid " + rid
							+ "  gave result " + lukuri);
				}
				
				if (relations[i].getNotices() != null) {

		
					for (int j = 0; j < relations[i].getNotices().length; j++) {
						RelationNotice rn = relations[i].getNotices()[j];
						int rnid = rn.getRnid();

						stm = con.createStatement();
						rs = stm
						.executeQuery("select nextval('RelationNoticeSeq')");

						if (rs.next()) {
							rnid = rs.getInt(1);
						} else {
							throw new SQLException(
									"Sequence relationseq error");
						}
						rs.close();

						pst = con.prepareStatement(insSql);
						pst.setInt(1, rn.getSurety());
						pst.setString(2, rn.getType());
						pst.setString(3, rn.getDescription());
						pst.setString(4, rn.getDatePrefix());
						pst.setString(5, rn.getFromDate());
						pst.setString(6, rn.getToDate());
						pst.setString(7, rn.getPlace());
						pst.setString(8, rn.getNoteText());
						pst.setString(9, rn.getSource());
						pst.setString(10, rn.getPrivateText());	
						pst.setInt(11, rnid);
						pst.setInt(12, rid);
						pst.setString(13, rn.getTag());
						pst.setInt(14,j+1);
						int rer = pst.executeUpdate();
						
						logger.fine("insert rn for " + rnid + "["
								+ rer + "]");

					}
				}
			}

		} catch (SQLException e) {
			// e.printStackTrace();
			logger.log(Level.WARNING, "Relation update", e);
			return e.getMessage();
		}

		return null;

	}
	
}
