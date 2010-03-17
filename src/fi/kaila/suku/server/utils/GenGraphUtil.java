package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.SukuData;

public class GenGraphUtil {

	private static Logger logger = Logger.getLogger(ReportUtil.class.getName());

	private Connection con = null;

	ReportWorkerDialog runner = null;

	/**
	 * Constructor
	 * 
	 * @param con
	 *            connection instance to the PostgreSQL database
	 */
	public GenGraphUtil(Connection con) {
		this.con = con;
		this.runner = ReportWorkerDialog.getRunner();
	}

	public SukuData getGengraphData(int pid) throws SukuException {
		SukuData fam = new SukuData();
		Vector<PersonShortData> persons = new Vector<PersonShortData>();
		Vector<Relation> relas = new Vector<Relation>();
		PersonShortData psp = new PersonShortData(con, pid);
		persons.add(psp);
		Relation rela;

		String sql = "select rid,bid,tag from parent where aid = ?";

		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, pid);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int rid = rs.getInt(1);
				int bid = rs.getInt(2);
				String tag = rs.getString(3);
				rela = new Relation(rid, bid, pid, tag, 100, null, null);
				relas.add(rela);
				psp = new PersonShortData(con, bid);
				persons.add(psp);

			}
			rs.close();
			pst.close();

			fam.relations = relas.toArray(new Relation[0]);

			fam.pers = persons.toArray(new PersonShortData[0]);

		} catch (SQLException e) {
			throw new SukuException("GenGraph sql error", e);
		}

		logger.fine("GenGraphUtil repo");

		this.runner.setRunnerValue(Resurses.getString("REPORT_DESC_COUNTING"));
		return fam;

	}

}
