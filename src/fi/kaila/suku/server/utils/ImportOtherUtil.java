package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.imports.ImportOtherDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

public class ImportOtherUtil {
	private Connection con;

	private ImportOtherDialog runner = null;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private String schema = null;
	private int viewId = -1;

	private LinkedHashMap<Integer, Integer> pidmap = new LinkedHashMap<Integer, Integer>();

	/**
	 * Constructor with connection
	 * 
	 * @param con
	 */
	public ImportOtherUtil(Connection con) {
		this.con = con;
		this.runner = ImportOtherDialog.getRunner();

	}

	public SukuData importOther(String schema, int viewId) throws SukuException {
		SukuData result = new SukuData();
		logger.info("Starting to import from " + schema
				+ ((viewId >= 0) ? " the view " + viewId : ""));
		this.schema = schema;
		this.viewId = viewId;

		try {
			result.resuCount = collectIndividuals();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Import from Other database failed", e);
			throw new SukuException(e);
		} finally {
			String sql = "select max(pnid) from unitnotice ";
			Statement stm;
			try {
				stm = con.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				int maxpnid = 0;
				if (rs.next()) {
					maxpnid = rs.getInt(1);
				}
				rs.close();

				if (maxpnid > nextnpid) {
					nextnpid = maxpnid;
				}
				sql = "SELECT setval('unitnoticeseq'," + nextnpid + ")";

				rs = stm.executeQuery(sql);
				rs.close();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Import from Other database failed "
						+ "to update unitnoticeseq", e);
				e.printStackTrace();
				throw new SukuException(e);
			}
		}

		result.resu = "Under construction";
		return result;

	}

	private int nextnpid = 0;

	private int collectIndividuals() throws SQLException, SukuException {
		String sql = null;
		PreparedStatement pst;

		Statement stm = con.createStatement();
		ResultSet rs = stm.executeQuery("select nextval('unitseq')");
		int npid = 0;
		if (rs.next()) {
			npid = rs.getInt(1);

		} else {
			throw new SQLException("Sequence unitseq error");
		}
		rs.close();

		stm = con.createStatement();
		rs = stm.executeQuery("select nextval('unitnoticeseq')");

		if (rs.next()) {
			nextnpid = rs.getInt(1);

		} else {
			throw new SQLException("Sequence unitnoticeseq error");
		}
		rs.close();

		if (viewId <= 0) {
			sql = "select pid from " + schema + ".unit order by pid";
			pst = con.prepareStatement(sql);
		} else {
			sql = "select pid from " + schema
					+ ".unit where pid in (select pid from " + schema
					+ ".viewunits where vid = ?) order by pid";
			pst = con.prepareStatement(sql);
			pst.setInt(1, viewId);
		}

		rs = pst.executeQuery();

		while (rs.next()) {
			int pid = rs.getInt("pid");
			pidmap.put(pid, npid);

			npid++;

		}
		rs.close();
		pst.close();

		sql = "SELECT setval('unitseq'," + npid + ")";
		rs = stm.executeQuery(sql);
		rs.close();

		sql = "insert into unit (pid,tag,privacy,groupid,sex,sourcetext,privatetext,modified,createdate) "
				+ "select ?,tag,privacy,groupid,sex,sourcetext,privatetext,modified,createdate from "
				+ schema + ".unit where pid=?";

		pst = con.prepareStatement(sql);

		String sqln = "insert into unitnotice (pnid,pid,surety,noticerow,tag,privacy,noticetype,description,"
				+ "dateprefix,fromdate,todate,place,village,farm,croft,address,postoffice,postalcode,state,country,"
				+ "email,notetext,mediafilename,mediadata,mediatitle,prefix,surname,givenname,patronym,postfix,"
				+ "refnames,refplaces,sourcetext,privatetext,modified,createdate) "
				+ "select ?,?,surety,noticerow,tag,privacy,noticetype,description,"
				+ "dateprefix,fromdate,todate,place,village,farm,croft,address,postoffice,postalcode,state,country,"
				+ "email,notetext,mediafilename,mediadata,mediatitle,prefix,surname,givenname,patronym,postfix,"
				+ "refnames,refplaces,sourcetext,privatetext,modified,createdate from "
				+ schema + ".unitnotice where pnid=?";

		PreparedStatement pnst = con.prepareStatement(sqln);

		String sqln2 = "select pnid from " + schema
				+ ".unitnotice where pid = ?";

		PreparedStatement pnst2 = con.prepareStatement(sqln2);

		double counter = 0;
		Set<Map.Entry<Integer, Integer>> entriesx = pidmap.entrySet();
		Iterator<Map.Entry<Integer, Integer>> eex = entriesx.iterator();
		while (eex.hasNext()) {
			Map.Entry<Integer, Integer> entrx = (Map.Entry<Integer, Integer>) eex
					.next();
			Integer newpid = entrx.getValue(); // here is pid in this db
			Integer prevpid = entrx.getKey(); // and this is import db

			pst.setInt(1, newpid);
			pst.setInt(2, prevpid);
			int luku = pst.executeUpdate();
			double dbSize = pidmap.size();

			double prossa = counter / dbSize;
			int prose = (int) (prossa * 100);
			if (prose > 100)
				prose = 100;
			if (this.runner.setRunnerValue("" + prose + ";unit")) {

				throw new SukuException(Resurses.getString("IMPORT_CANCELLED"));
			}

			pnst2.setInt(1, prevpid);

			rs = pnst2.executeQuery();
			while (rs.next()) {
				int oldpnid = rs.getInt(1);

				pnst.setInt(1, nextnpid);
				pnst.setInt(2, newpid);
				pnst.setInt(3, oldpnid);

				pnst.executeUpdate();
				nextnpid++;
			}
			rs.close();

			counter += luku;

		}
		this.runner.setRunnerValue("100;unit");
		return (int) counter;
	}

}
