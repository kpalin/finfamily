package fi.kaila.suku.server.utils;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

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
				+ "SourceText=?,PrivateText=?,Modified=now() "
				+ "where pnid=? ";

		String insSql = "insert into unitnotice  ("
				+ "surety,Privacy,NoticeType,Description,"
				+ "DatePrefix,FromDate,ToDate,Place,"
				+ "Village,Farm,Croft,Address,"
				+ "PostalCode,PostOffice,State,Country,Email,"
				+ "NoteText,MediaFilename,MediaTitle,Prefix,"
				+ "Surname,Givenname,Patronym,PostFix,"
				+ "SourceText,PrivateText,pnid,pid,tag) values ("
				+ "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?) ";

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

			for (int i = 0; i < nn.length; i++) {
				UnitNotice n = nn[i];
				int pnid = 0;
				if (n.isToBeDeleted()) {
					pstDelLang.setInt(1, n.getPnid());
					int landelcnt = pstDelLang.executeUpdate();
					pstDel.setInt(1, n.getPnid());
					int delcnt = pstDel.executeUpdate();
					String text = "Poistettiin " + delcnt + " riviä ["
							+ landelcnt + "] kieliversiota pid = " + n.getPid()
							+ " tag=" + n.getTag();
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
					}
					if (n.getPnid() > 0) {
						pst.setInt(28, n.getPnid());
						int luku = pst.executeUpdate();
						// System.out.println("Päivitettiin " + luku +
						// " tietuetta");
						logger.fine("Päivitettiin " + luku
								+ " tietuetta pnid=[" + n.getPnid() + "]");
					} else {
						pst.setInt(28, pnid);
						pst.setInt(29, pid);
						pst.setString(30, n.getTag());
						int luku = pst.executeUpdate();
						// System.out.println("Luotiin " + luku +
						// " uusi tietue");
						logger.fine("Luotiin " + luku + " tietue pnid=[" + pnid
								+ "]");
					}

					if (n.getMediaData() == null) {
						String sql = "update unitnotice set mediadata = null where pnid = ?";
						pst = con.prepareStatement(sql);
						pst.setInt(1, pnid);
						int lukuri = pst.executeUpdate();
						if (lukuri != 1) {
							logger.warning("media deleted for pnid "
									+ n.getPnid() + " gave result " + lukuri);
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
									logger.warning("language deleted for pnid "
											+ n.getPnid() + " ["
											+ ll.getLangCode()
											+ "] gave result " + lukuri);
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
									logger.warning("language added for pnid "
											+ n.getPnid() + " ["
											+ ll.getLangCode()
											+ "] gave result " + lukuri);
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
				+ "NoteText=?,Modified=now()" +
				// "SourceText=?,PrivateText=?,Modified=now() " +
				"where rnid=? ";

		String insSql = "insert into relationnotice  "
				+ "(surety,RelationType,Description,DatePrefix,FromDate,ToDate,"
				+ "Place,NoteText,rnid,rid,tag,noticerow)"
				+ " values (?,?,?,?,?,?,?,?,?,?,?,0) ";

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

		SukuData res = new SukuData();

		try {
			// PreparedStatement updNoti = con.prepareStatement(updSql);

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
					logger.info("deleted relation [" + r.getTag() + "" + rid
							+ " between " + r.getPid() + "/" + r.getRelative()
							+ " result [" + laskRel + "/" + laskNoti + "/"
							+ laskLang);
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
						}
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
								pst.setInt(9, rnid);
								if (rn.getRnid() > 0) {
									int rer = pst.executeUpdate();
									System.out.println("update rn for " + rnid
											+ "[" + rer + "]");
								} else {
									pst.setInt(10, rid);
									pst.setString(11, rn.getTag());
									int rer = pst.executeUpdate();
									System.out.println("insert rn for " + rnid
											+ "[" + rer + "]");

								}
							}

							rorder.setInt(1, j);
							rorder.setInt(2, rnid);
							int orderit = rorder.executeUpdate();
							if (false)
								System.out.println("RN order lkm = " + orderit);
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
											System.out
													.println("insert rl rnid: "
															+ rnid + "/"
															+ rl.getLangCode()
															+ "count:[" + rier
															+ "]");

										} else if (rl.isToBeDeleted()) {
											PreparedStatement updLang = con
													.prepareStatement(delLangSql);
											updLang.setInt(1, rnid);
											updLang.setString(2, rl
													.getLangCode());
											int rder = updLang.executeUpdate();
											System.out
													.println("delete rl rnid: "
															+ rnid + "/"
															+ rl.getLangCode()
															+ "count:[" + rder
															+ "]");

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
											System.out.println("update rl for "
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

			String updRowSql = "update relation set relationrow = ? where rid = ? and pid = ?";
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
					if (false)
						System.out.println("RELAROW # " + lukuri);

				}
			}

		} catch (SQLException e) {
			// e.printStackTrace();
			logger.log(Level.WARNING, "Relation update", e);
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

}
