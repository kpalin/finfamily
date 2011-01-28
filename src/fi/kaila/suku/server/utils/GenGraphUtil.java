package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class GenGraphUtil.
 */
public class GenGraphUtil {

	private static Logger logger = Logger.getLogger(ReportUtil.class.getName());

	private Connection con = null;

	/** The runner. */
	ReportWorkerDialog runner = null;

	/**
	 * Constructor.
	 * 
	 * @param con
	 *            connection instance to the PostgreSQL database
	 */
	public GenGraphUtil(Connection con) {
		this.con = con;
		this.runner = ReportWorkerDialog.getRunner();
	}

	/**
	 * Gets the gengraph data.
	 * 
	 * @param pid
	 *            the pid
	 * @param lang
	 *            the lang
	 * @return the gengraph data
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData getGengraphData(int pid, String lang) throws SukuException {
		SukuData fam = new SukuData();
		Relation rela;
		ArrayList<PersonShortData> persons = new ArrayList<PersonShortData>();
		ArrayList<Relation> relas = new ArrayList<Relation>();
		// LinkedHashMap<Integer,ReportUnit> ru = new
		// LinkedHashMap<Integer,ReportUnit>();
		// LinkedHashMap<Integer, PersonShortData> pu = new
		// LinkedHashMap<Integer, PersonShortData>();
		Vector<RelationNotice> relaNotices = null;
		PersonShortData psp = new PersonShortData(con, pid);
		persons.add(psp);

		try {

			String sql = "select a.rid,b.pid,a.tag,a.surety,c.tag "
					+ "from relation as a inner join relation as b on a.rid=b.rid and b.pid <> a.pid	"
					+ "left join relationnotice as c on a.rid=c.rid "
					+ "where a.pid=? order by a.tag,a.relationrow";

			String sqln = "select tag,fromdate from relationnotice where rid=? order by noticerow";
			PreparedStatement pst = con.prepareStatement(sql);

			pst.setInt(1, pid);
			ResultSet rs = pst.executeQuery();

			int defRid = 0;
			while (rs.next()) {
				int rid = rs.getInt(1);
				if (defRid != rid) {
					defRid = rid;
					int bid = rs.getInt(2);
					String tag = rs.getString(3);
					int surety = rs.getInt(4);
					String noteTag = rs.getString(5);
					rela = new Relation(rid, pid, bid, tag, surety, null, null,
							null, null);
					relas.add(rela);

					if (noteTag != null) {
						relaNotices = new Vector<RelationNotice>();
						PreparedStatement pstn = con.prepareStatement(sqln);
						pstn.setInt(1, rid);
						ResultSet rsn = pstn.executeQuery();
						while (rsn.next()) {

							String xtag = rsn.getString(1);
							String xdate = rsn.getString(2);

							RelationNotice rn = new RelationNotice(xtag);
							if (xdate != null) {
								rn.setFromDate(xdate);
							}
							relaNotices.add(rn);
						}
						rsn.close();
						pstn.close();
						rela.setNotices(relaNotices
								.toArray(new RelationNotice[0]));
					}

				}
			}
			rs.close();
			pst.close();
			fam.relations = relas.toArray(new Relation[0]);

			fam.pers = persons.toArray(new PersonShortData[0]);

		} catch (SQLException e) {
			throw new SukuException("GenGraph sql error", e);
		}

		logger.fine("GenGraphUtil repo");

		this.runner.setRunnerValue(psp.getAlfaName(true));
		return fam;

	}

}
