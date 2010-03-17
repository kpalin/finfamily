package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.util.logging.Logger;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
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

		PersonShortData psp = new PersonShortData(con, pid);
		fam.pers = new PersonShortData[1];
		fam.pers[0] = psp;

		logger.fine("GenGraphUtil repo");

		this.runner.setRunnerValue(Resurses.getString("REPORT_DESC_COUNTING"));
		return fam;

	}

}
