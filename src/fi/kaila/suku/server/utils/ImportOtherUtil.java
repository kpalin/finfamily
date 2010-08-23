package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.imports.ImportOtherDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Server class to import from another database
 * 
 * @author kalle
 * 
 */
public class ImportOtherUtil {
	private Connection con;

	private ImportOtherDialog runner = null;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private String schema = null;
	private int viewId = -1;
	private String viewName = null;

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

	public SukuData importOther(String schema, int viewId, String viewName)
			throws SukuException {
		SukuData result = new SukuData();
		logger.info("Starting to import from " + schema
				+ ((viewId >= 0) ? " the view " + viewId : ""));
		this.schema = schema;
		this.viewId = viewId;
		this.viewName = viewName;

		try {
			result = collectIndividuals();

			collectRelations();

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

				sql = "select max(rid) from relation ";

				rs = stm.executeQuery(sql);
				int maxrid = 0;
				if (rs.next()) {
					maxrid = rs.getInt(1);
				}
				rs.close();

				if (maxrid > nextrid) {
					nextrid = maxpnid;
				}
				sql = "SELECT setval('relationseq'," + nextrid + ")";

				rs = stm.executeQuery(sql);
				rs.close();

				sql = "select max(rnid) from relationnotice ";

				rs = stm.executeQuery(sql);
				int maxrnid = 0;
				if (rs.next()) {
					maxrnid = rs.getInt(1);
				}
				rs.close();

				if (maxrnid > nextnrid) {
					nextnrid = maxrnid;
				}
				sql = "SELECT setval('relationnoticeseq'," + nextnrid + ")";

				rs = stm.executeQuery(sql);
				rs.close();
				stm.close();

			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Import from Other database failed "
						+ "to update unitnoticeseq", e);
				e.printStackTrace();
				throw new SukuException(e);
			}
		}

		// result.resu = "Under construction";
		return result;

	}

	private int nextrid = 0;
	private int nextnrid = 0;

	private void collectRelations() throws SQLException, SukuException {
		StringBuilder sq = new StringBuilder();
		PreparedStatement pst;
		LinkedHashMap<String, Integer> ridpidmap = new LinkedHashMap<String, Integer>();
		LinkedHashMap<Integer, Integer> ridmap = new LinkedHashMap<Integer, Integer>();
		sq.append("select a.rid,a.pid from " + schema
				+ ".relation as a inner join " + schema
				+ ".relation as b on a.rid=b.rid and a.pid <> b.pid ");
		if (viewId > 0) {
			sq.append("and a.pid in (select pid from " + schema
					+ ".viewunits where vid=" + viewId + ") "
					+ "and b.pid in (select pid from " + schema
					+ ".viewunits where vid=" + viewId + ")  ");
		}

		Statement stm = con.createStatement();
		ResultSet rs = stm.executeQuery("select nextval('relationseq')");

		if (rs.next()) {
			nextrid = rs.getInt(1);

		} else {
			throw new SQLException("Sequence relationseq error");
		}

		rs = stm.executeQuery("select nextval('relationnoticeseq')");

		if (rs.next()) {
			nextnrid = rs.getInt(1);

		} else {
			throw new SQLException("Sequence relationnoticeseq error");
		}

		rs.close();

		rs = stm.executeQuery(sq.toString());

		while (rs.next()) {
			int orid = rs.getInt(1);
			int opid = rs.getInt(2);
			String thekey = "" + orid + ";" + opid;
			Integer nrid = ridpidmap.get(thekey);
			if (nrid == null) {
				Integer newRid = ridmap.get(orid);
				if (newRid == null) {
					ridmap.put(orid, nextrid);
					ridpidmap.put(thekey, nextrid);
					nextrid++;
				} else {
					ridpidmap.put(thekey, newRid);
				}
			}
		}
		rs.close();

		String sql = "insert into relation (rid,pid,surety,tag,relationrow,modified,createdate) "
				+ "select ?,?,surety,tag,relationrow,modified,createdate from "
				+ schema + ".relation where rid=? and pid = ?";

		//FIXME: Method may fail to close database resource
		pst = con.prepareStatement(sql);

		int counter = 0;
		int relativesInserted = 0;
		Set<Map.Entry<String, Integer>> entriesx = ridpidmap.entrySet();
		Iterator<Map.Entry<String, Integer>> eex = entriesx.iterator();
		while (eex.hasNext()) {
			Map.Entry<String, Integer> entrx = (Map.Entry<String, Integer>) eex
					.next();
			String key = entrx.getKey();

			Integer newrid = entrx.getValue();

			String[] parts = key.split(";");
			if (parts.length != 2)
				continue;
			int orid = Integer.parseInt(parts[0]);
			int opid = Integer.parseInt(parts[1]);
			int npid = pidmap.get(opid);

			if (npid == 0)
				continue;

			pst.setInt(1, newrid);
			pst.setInt(2, npid);
			pst.setInt(3, orid);
			pst.setInt(4, opid);
			int relCount = pst.executeUpdate();
			relativesInserted += relCount;
			double dbSize = ridpidmap.size();

			double prossa = counter++ / dbSize;
			int prose = (int) (prossa * 100);

			if (prose > 100)
				prose = 100;
			prose = prose / 2;
			if (this.runner.setRunnerValue("" + prose + ";relation")) {

				throw new SukuException(Resurses.getString("IMPORT_CANCELLED"));
			}

		}

		String sqln = "insert into relationnotice (rid,rnid,surety,noticerow,tag,description,relationtype,"
				+ "dateprefix,fromdate,todate,place,notetext,sourcetext,privatetext,modified,createdate) "
				+ "select ?,?,surety,noticerow,tag,description,relationtype,dateprefix,fromdate,todate,"
				+ "place,notetext,sourcetext,privatetext,modified,createdate from "
				+ schema + ".relationnotice where rnid = ?";

		pst = con.prepareStatement(sqln);

		String sqll = "insert into relationlanguage (rid,rnid,langcode,relationtype,description,"
				+ "place,notetext,modified,createdate) "
				+ "select ?,?,langcode,relationtype,description,"
				+ "place,notetext,modified,createdate from "
				+ schema
				+ ".relationlanguage where rnid = ?";

		PreparedStatement pstl = con.prepareStatement(sqll);

		sql = "select rid,rnid from " + schema + ".relationnotice ";

		rs = stm.executeQuery(sql);
		counter = 0;
		while (rs.next()) {
			int nrid = rs.getInt(1);
			int nrnid = rs.getInt(2);
			Integer torid = ridmap.get(nrid);
			if (torid != null) {

				pst.setInt(1, torid);
				pst.setInt(2, nextnrid);
				pst.setInt(3, nrnid);
				pst.executeUpdate();

				pstl.setInt(1, torid);
				pstl.setInt(2, nextnrid);
				pstl.setInt(3, nrnid);
				pstl.executeUpdate();

				nextnrid++;

				double dbSize = ridmap.size();

				double prossa = counter++ / dbSize;
				int prose = (int) (prossa * 100);

				if (prose > 100)
					prose = 100;
				prose = 50 + (prose / 2);
				if (this.runner.setRunnerValue("" + prose + ";relationnotice")) {

					throw new SukuException(Resurses
							.getString("IMPORT_CANCELLED"));
				}
			}

			this.runner.setRunnerValue("100;relationnotice");

		}
		pst.close();
		pstl.close();
		rs.close();
		stm.close();

		logger.info("Copied " + relativesInserted + " relatives ");

	}

	private int nextnpid = 0;

	private SukuData collectIndividuals() throws SQLException, SukuException {
		SukuData resu = new SukuData();
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

		//FIXME: Method may fail to close database resource
		stm = con.createStatement();
		rs = stm.executeQuery("select nextval('unitnoticeseq')");

		if (rs.next()) {
			nextnpid = rs.getInt(1);

		} else {
			throw new SQLException("Sequence unitnoticeseq error");
		}
		rs.close();

		if (viewId < 0) {
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

		String sqll = "insert into unitlanguage (pnid,pid,tag,langcode,noticetype,description,place,"
				+ "notetext,mediatitle,modified,createdate) "
				+ "select ?,?,tag,langcode,noticetype,description,place,"
				+ "notetext,mediatitle,modified,createdate from "
				+ schema
				+ ".unitlanguage where pnid=? ";

		PreparedStatement pnsl = con.prepareStatement(sqll);

		String sqln2 = "select pnid from " + schema
				+ ".unitnotice where pid = ?";

		//FIXME: Method may fail to close database resource
		PreparedStatement pnst2 = con.prepareStatement(sqln2);

		double counter = 0;
		int languageCount = 0;
		int noticeCount = 0;
		Set<Map.Entry<Integer, Integer>> entriesx = pidmap.entrySet();
		Iterator<Map.Entry<Integer, Integer>> eex = entriesx.iterator();
		int pidIdx = 0;
		int newpids[] = new int[pidmap.size()];

		while (eex.hasNext()) {
			Map.Entry<Integer, Integer> entrx = (Map.Entry<Integer, Integer>) eex
					.next();
			Integer newpid = entrx.getValue(); // here is pid in this db
			Integer prevpid = entrx.getKey(); // and this is import db

			pst.setInt(1, newpid);
			pst.setInt(2, prevpid);
			int luku = pst.executeUpdate();
			double dbSize = pidmap.size();
			newpids[pidIdx++] = newpid;
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

				int ii = pnst.executeUpdate();
				noticeCount += ii;

				pnsl.setInt(1, nextnpid);
				pnsl.setInt(2, newpid);
				pnsl.setInt(3, oldpnid);

				int lanCount = pnsl.executeUpdate();
				languageCount += lanCount;
				nextnpid++;
			}
			rs.close();

			counter += luku;

		}

		ViewUtil vu = new ViewUtil(con);

		rs = stm.executeQuery("select max(vid) from views");
		int maxvid = 0;
		while (rs.next()) {
			maxvid = rs.getInt(1);
		}
		rs.close();
		maxvid++;
		String viewName = Resurses.getString("IMPORTED_VIEW") + " "
				+ this.viewName + " [" + maxvid + "]";
		SukuData oth = vu.addView(viewName);
		vu.addViewUnits(oth.resultPid, newpids, false);
		resu.resultPid = oth.resultPid;
		// (newpids,oth.resultPid);

		resu.generalText = viewName;
		logger.info("Copied " + ((int) counter) + " units with " + noticeCount
				+ " notices, " + " with " + languageCount
				+ " unitlanguage records");
		this.runner.setRunnerValue("100;unit");
		resu.resuCount = (int) counter;

		return resu;
	}

	public SukuData comparePersons(HashMap<String, String> map)
			throws SukuException {
		SukuData result = new SukuData();

		String schema = map.get("schema");
		int viewId = -1;
		String view = map.get("view");

		if (view != null) {
			viewId = Integer.parseInt(view);
		}
		StringBuilder sql = new StringBuilder();
		String sqlPrefix = null;
		String sqlPostfix = "";
		int subCount = 0;
		if (schema != null) {
			sqlPrefix = "pid in (select  a.pid from unitnotice as a, " + schema
					+ ".unitnotice as b where ";
			if (viewId > 0) {
				sqlPostfix = " and b.pid in (select pid from " + schema
						+ ".viewunits where vid = " + viewId + ") ";
			}
		} else {
			sqlPrefix = "pid in (select  a.pid from unitnotice as a, "
					+ "unitnotice as b where  a.pid <> b.pid and ";
			if (viewId > 0) {
				sqlPostfix = " and b.pid in (select pid from viewunits where vid = "
						+ viewId + ") and a.pid <> b.pid ";
			}
		}

		sql.append("select pid from unit where ");

		if (map.get("dates") != null) {
			subCount++;
			sql.append(sqlPrefix);
			sql
					.append("( coalesce(a.fromdate,'') = coalesce(b.fromdate,'') and a.tag='BIRT' and b.tag='BIRT')"
							+ sqlPostfix + " )");
		}

		if (map.get("surname") != null) {
			subCount++;
			if (subCount > 1) {
				sql.append("and ");
			}
			sql.append(sqlPrefix);
			sql
					.append("( coalesce(a.surname,'') ilike coalesce(b.surname,'') and a.tag='NAME' and b.tag='NAME')"
							+ sqlPostfix + ")  ");

		}
		if (map.get("patronym") != null) {
			subCount++;
			if (subCount > 1) {
				sql.append("and ");
			}
			sql.append(sqlPrefix);
			sql
					.append("( coalesce(a.patronym,'') ilike coalesce(b.patronym,'') and a.tag='NAME' and b.tag='NAME')"
							+ sqlPostfix + ")  ");

		}

		subCount++;
		if (subCount > 1) {
			sql.append("and ");
		}

		if (map.get("firstname") == null) {

			sql.append(sqlPrefix);
			sql
					.append("( coalesce(a.givenname,'') ilike coalesce(b.givenname,'') "
							+ " and a.tag='NAME' and b.tag='NAME') "
							+ sqlPostfix + " )");
		} else {
			sql.append(sqlPrefix);
			sql
					.append("( substring(coalesce(a.givenname,'') from '[\\\\w]+') "
							+ " ilike substring(coalesce(b.givenname,'') from '[\\\\w]+') "
							+ "   and a.tag='NAME' and b.tag='NAME') "
							+ sqlPostfix + ") ");
		}
		logger.info(sql.toString());

		try {
			Vector<Integer> vv = new Vector<Integer>();
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery("select max(vid) from views");
			int maxvid = 0;
			while (rs.next()) {
				maxvid = rs.getInt(1);
			}
			rs.close();
			maxvid++;
			String newView = Resurses.getString("COMPARE_VIEW") + " " + " ["
					+ maxvid + "]";

			rs = stm.executeQuery(sql.toString());
			int pid = 0;
			while (rs.next()) {
				pid = rs.getInt(1);
				vv.add(pid);
			}
			rs.close();
			if (vv.size() > 0) {
				ViewUtil vu = new ViewUtil(con);
				SukuData oth = vu.addView(newView);

				int pids[] = new int[vv.size()];
				for (int i = 0; i < pids.length; i++) {
					pids[i] = vv.get(i);
				}
				vu.addViewUnits(oth.resultPid, pids, false);
				result.generalText = newView;
				result.resultPid = oth.resultPid;
			}
			stm.close();

		} catch (SQLException e) {

			e.printStackTrace();
			throw new SukuException(e);
		}

		return result;
	}

}
