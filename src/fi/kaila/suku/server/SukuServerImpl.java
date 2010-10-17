package fi.kaila.suku.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.server.utils.ExportBackupUtil;
import fi.kaila.suku.server.utils.ExportGedcomUtil;
import fi.kaila.suku.server.utils.GenGraphUtil;
import fi.kaila.suku.server.utils.GroupUtil;
import fi.kaila.suku.server.utils.ImportGedcomUtil;
import fi.kaila.suku.server.utils.ImportOtherUtil;
import fi.kaila.suku.server.utils.PersonUtil;
import fi.kaila.suku.server.utils.QueryUtil;
import fi.kaila.suku.server.utils.ReportUtil;
import fi.kaila.suku.server.utils.Upload;
import fi.kaila.suku.server.utils.ViewUtil;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.data.ExcelImporter;
import fi.kaila.suku.util.data.SukuUtility;
import fi.kaila.suku.util.local.LocalDatabaseUtility;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.RelationShortData;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * Serverpään mm tietokantaa käsittelevä.
 * 
 * @author FIKAAKAIL
 */
public class SukuServerImpl implements SukuServer {

	private final String dbDriver = "org.postgresql.Driver";
	private Connection con = null;
	private String schema = null;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private String openFile = null;

	// /**
	// * Constructor
	// *
	// * @throws SukuException
	// */
	// public SukuServerImpl() throws SukuException {
	// this.schema = "public";
	//
	// try {
	// Class.forName(this.dbDriver);
	// } catch (ClassNotFoundException e) {
	// throw new SukuException(e);
	// }
	//
	// }

	/**
	 * used by tomcat from webstart idea is that database uses different scema
	 * for each user.
	 * 
	 * @param schema
	 *            the schema
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuServerImpl(String schema) throws SukuException {
		this.schema = schema;
		logger = Logger.getLogger(this.getClass().getName());
		try {
			Class.forName(this.dbDriver);
		} catch (ClassNotFoundException e) {
			throw new SukuException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.server.SukuServer#getConnection(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void getConnection(String host, String dbname, String userid,
			String passwd) throws SukuException {
		String dbConne = "jdbc:postgresql://localhost/sukuproto?user=kalle&password=kalle";
		dbConne = "jdbc:postgresql://" + host + "/" + dbname + "?user="
				+ userid;
		logger.info("Connection: " + dbConne + ";schema: " + this.schema);
		if (passwd != null && !passwd.isEmpty()) {

			dbConne += "&password=" + passwd;
		}
		Statement stm = null;
		try {
			this.con = DriverManager.getConnection(dbConne);
			stm = this.con.createStatement();
			stm.executeUpdate("set search_path to " + this.schema);
			stm.close();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Connection: " + dbConne + ";schema: "
					+ this.schema, e);

			if (stm == null) {
				throw new SukuException("db=" + dbname + ": " + e.getMessage());
			} else {
				try {
					stm.executeUpdate("set search_path to public");
					stm.close();
				} catch (SQLException e1) {
					throw new SukuException("db=" + dbname + ": "
							+ e.getMessage());
				}
			}
		}
		// finally {
		// try {
		// stm.close();
		// } catch (SQLException e) {
		// throw new SukuException("db=" + dbname + ": " + e.getMessage());
		// }
		// }

	}

	private SukuData import2004Data(String path, String oldCode)
			throws SukuException {
		SukuUtility data = SukuUtility.instance();
		data.createSukuDb(this.con, "/sql/finfamily.sql");

		logger.fine("database created for " + path);

		try {
			SukuData resp = data.import2004Data(this.con, path, oldCode);
			return resp;
		} catch (Exception e) {
			throw new SukuException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.server.SukuServer#resetConnection()
	 */
	@Override
	public void resetConnection() {
		if (this.con != null) {
			try {
				this.con.close();

			} catch (SQLException e) {
				// throw new SukuException(e);
			}
			this.con = null;
		}

	}

	private SukuData getViewList(String schema) throws SukuException {
		SukuData vlist = new SukuData();
		String sql = "select vid,name from views order by name";

		if (schema != null) {
			sql = "select vid,name from " + schema + ".views order by name";
		}

		ArrayList<String> v = new ArrayList<String>();
		try {
			PreparedStatement pstm = this.con.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {

				int vid = rs.getInt(1);
				String aux = rs.getString(2);

				v.add("" + vid + ";" + aux);
			}
			rs.close();
			pstm.close();
			String[] vl = new String[0];
			vlist.generalArray = v.toArray(vl);
			return vlist;
		} catch (SQLException e) {
			throw new SukuException(e);
		}
	}

	private SukuData getShortFamily(int pid, boolean bothParents)
			throws SukuException {

		SukuData fam = new SukuData();

		String sql = "select s.bid,s.relationrow,s.tag,rn.fromdate,rd.fromdate,rn.tag,s.surety "
				+ "from (spouse_all as s left join relationNotice as rn on s.rid = rn.rid  "
				+ "and rn.tag='MARR') left join relationNotice as rd on s.rid = rd.rid  "
				+ "and rd.tag='DIV' where s.aid = ? order by s.relationrow ";

		ArrayList<RelationShortData> v = new ArrayList<RelationShortData>();
		try {
			PreparedStatement pstm = this.con.prepareStatement(sql);

			pstm.setInt(1, pid);

			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				String tag = rs.getString(3);
				int relPid = rs.getInt(1);
				int relOrder = rs.getInt(2);
				String aux = rs.getString(4);
				String div = rs.getString(5);
				// String stag = rs.getString(6);
				int surety = rs.getInt(7);
				// System.out.println("XX: " + tag + "/" + relPid + "/" +
				// relOrder + "/" + aux + "/" + div);

				RelationShortData rel = new RelationShortData(pid, relPid,
						relOrder, tag, surety);

				if (aux != null) {
					rel.setMarrDate(aux);
				}

				if (div != null) {
					rel.setDivDate(div);
				}

				v.add(rel);

			}
			pstm.close();

			int begChil = v.size();

			sql = "select c.bid,c.relationrow,c.tag,rn.tag,c.surety "
					+ "from child_all as c left join relationNotice as rn on c.rid = rn.rid  "
					+ "and rn.tag='ADOP' where c.aid = ? order by c.relationrow ";

			// sql = "select c.bid,c.relationrow,c.tag,c.surety "
			// + "from child_all as c  "
			// + "where c.aid=? order by c.relationrow";

			pstm = this.con.prepareStatement(sql);

			pstm.setInt(1, pid);

			rs = pstm.executeQuery();
			while (rs.next()) {
				RelationShortData rel = new RelationShortData(pid,
						rs.getInt(1), rs.getInt(2), rs.getString(3),
						rs.getInt(5));
				String adop = rs.getString(4);
				if (adop != null) {
					rel.setAdopted(adop);
				}

				// String aux = rs.getString(4);
				// if (aux != null) {
				// rel.setMarrDate(aux);
				// }
				v.add(rel);

			}
			rs.close();
			pstm.close();
			if (bothParents) {
				int endChil = v.size();

				for (int i = begChil; i < endChil; i++) {
					sql = "select bid,tag from parent_all where aid =? ";
					ArrayList<Integer> parents = new ArrayList<Integer>();
					pstm = this.con.prepareStatement(sql);
					RelationShortData relsho = v.get(i);
					pstm.setInt(1, relsho.getRelationPid());

					rs = pstm.executeQuery();
					while (rs.next()) {
						parents.add(Integer.valueOf(rs.getInt(1)));
					}
					rs.close();
					pstm.close();

					int[] apu = new int[parents.size()];
					for (int j = 0; j < parents.size(); j++) {
						apu[j] = parents.get(j).intValue();
					}

					relsho.setParentArray(apu);
				}
			}

			sql = "select p.bid,p.relationrow,p.tag,rn.tag,p.surety "
					+ "from parent_all as p left join relationNotice as rn on p.rid = rn.rid  "
					+ "and rn.tag='ADOP' where p.aid = ? order by p.relationrow ";

			pstm = this.con.prepareStatement(sql);

			pstm.setInt(1, pid);

			rs = pstm.executeQuery();
			while (rs.next()) {
				RelationShortData rel = new RelationShortData(pid,
						rs.getInt(1), rs.getInt(2), rs.getString(3),
						rs.getInt(5));
				String adop = rs.getString(4);
				if (adop != null) {
					rel.setAdopted(adop);
				}
				v.add(rel);

			}
			rs.close();
			pstm.close();

			RelationShortData[] dt = new RelationShortData[0];
			fam.rels = v.toArray(dt);
			ArrayList<PersonShortData> pv = new ArrayList<PersonShortData>();

			PersonShortData p = new PersonShortData(this.con, pid);
			pv.add(p);

			// sql =
			// "select tag,count(*) from relation where pid=? group by tag";

			sql = "select a.tag,b.pid "
					+ "from relation as a inner join relation as b on a.rid=b.rid "
					+ "where a.pid=? ";

			pstm = this.con.prepareStatement(sql);

			for (int i = 0; i < fam.rels.length; i++) {
				int ipid = fam.rels[i].getRelationPid();
				// String memType=fam.rels[i].getTag();
				if (ipid != pid) {
					// if ("CHIL".equals(fam.rels[i].getTag())){
					// memType=PersonShortData.FAM_TYPE_CHILD;
					// }
					//
					p = new PersonShortData(this.con, ipid);

					pstm.setInt(1, ipid);
					rs = pstm.executeQuery();
					int cc = 0;
					int pc = 0;
					int fid = 0;
					int mid = 0;
					while (rs.next()) {
						String tag = rs.getString(1);

						if (tag.equals("CHIL")) {
							cc++;

						} else if (tag.equals("FATH")) {
							pc++;

							if (fid == 0) {
								fid = rs.getInt(2);
							}
						} else if (tag.equals("MOTH")) {
							pc++;

							if (mid == 0) {
								mid = rs.getInt(2);
							}
						}
					}
					rs.close();
					p.setChildCount(cc);
					p.setPareCount(pc);
					p.setFatherPid(fid);
					p.setMotherPid(mid);

					pv.add(p);
				}
			}

			fam.pers = pv.toArray(new PersonShortData[0]);

			pstm.close();

			return fam;

		} catch (SQLException e) {
			throw new SukuException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.server.SukuServer#getSukuData(fi.kaila.suku.util.pojo.SukuData
	 * , java.lang.String[])
	 */
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException {
		String auxes[];

		int j;
		StringBuilder sb = new StringBuilder();
		for (j = 0; j < params.length; j++) {
			if (j > 0)
				sb.append(";");
			sb.append(params[j]);
		}
		logger.fine(sb.toString());

		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < params.length; i++) {
			auxes = params[i].split("=");
			if (auxes.length == 2) {
				map.put(auxes[0], auxes[1]);
			}
		}
		String cmd = map.get("cmd");

		if (cmd == null)
			return null;
		SukuData fam = new SukuData();
		if (cmd.equals("compare")) {
			ImportOtherUtil inoth = new ImportOtherUtil(con);
			fam = inoth.comparePersons(map);
		} else if (cmd.equals("create")) {
			fam = executeCmdCreate(map, fam);
		} else if (cmd.equals("crlista")) {
			fam = executeCmdCrlista(map, fam);
		} else if (cmd.equals("crtables")) {
			fam = executeCmdCrtables(map, fam);
		} else if (cmd.equals("dblista")) {
			fam.generalArray = LocalDatabaseUtility.getListOfDatabases(con);
		} else if (cmd.equals("dbversion")) {
			fam = getDbVersion();
		} else if (cmd.equals("dbstats")) {
			fam = getDbStatistics();
		} else if (cmd.equals("delete")) {
			fam = executeCmdDelete(map, fam);
		} else if (cmd.equals("excel")) {
			fam = executeCmdExcel(map, fam);
		} else if (cmd.equals("family")) {
			fam = executeCmdFamily(map, fam);
		} else if (cmd.equals("get")) {
			fam = executeCmdGet(map, cmd, fam);
		} else if (cmd.equals("getsettings")) {
			PersonUtil pu = new PersonUtil(con);
			fam = pu.getSettings(map.get("index"), map.get("type"),
					map.get("name"));
		} else if (cmd.equals("group")) {
			fam = executeCmdGroup(request, map);
		} else if (cmd.equals("import")) {
			fam = executeCmdImport(map, fam);
		} else if (cmd.equals("initdb")) {
			executeCmdInitdb(map);
		} else if (cmd.equals("intelli")) {
			fam = getIntelliSensData();
		} else if (cmd.equals("logout")) {
			executeCmdLogout();
		} else if (cmd.equals("person")) {
			fam = executeCmdPerson(map, fam);
		} else if (cmd.equals("places")) {
			executeCmdPlaces(request, fam);
		} else if (cmd.equals("plist")) {
			QueryUtil q = new QueryUtil(con);
			fam = q.queryDatabase(params);
		} else if (cmd.equals("relatives")) {
			fam = executeCmdRelatives(map, fam);
		} else if (cmd.equals("savesettings")) {
			fam = saveReportSettings(map);
		} else if (cmd.equals("schema")) {
			executeCmdSchema(map, fam);
		} else if (cmd.equals("sql")) {
			fam = executeCmdSql(request, map, fam);
		} else if (cmd.equals("unitCount")) {
			fam = getUnitCount();
		} else if (cmd.equals("update")) {
			fam = executeCmdUpdate(request, map, fam);
		} else if (cmd.equals("updatesettings")) {
			fam = executeCmdUpdatesettings(request, map);
		} else if (cmd.equals("upload")) {
			fam = uploadFamily(request);
		} else if (cmd.equals("viewlist")) {
			fam = getViewList(map.get("schema"));
		} else if (cmd.equals("variables")) {
			fam = executeCmdVariables(request, map, fam);
		} else if (cmd.equals("view")) {
			fam = executeCmdView(request, map, fam);
		} else if (cmd.equals("virtual")) {
			fam = executeCmdVirtual(map, fam);
		} else {
			fam.resu = "Unknown server request, cmd = " + cmd;
			logger.warning(fam.resu);
		}

		if (fam != null) {
			fam.cmd = map.get("cmd");
		} else {
			throw new SukuException(Resurses.getString("SERVER_RESULT_NULL"));
		}
		if (fam.resu != null) {
			throw new SukuException(fam.resu);
		}
		return fam;
	}

	private SukuData executeCmdSql(SukuData request,
			HashMap<String, String> map, SukuData fam) {
		String type = map.get("type");
		String vidt = map.get("vid");
		String sql = null;
		int vid = -1;
		SukuData resp = new SukuData();

		if (request == null || request.generalText == null) {
			resp.resu = "sql command missing";
			return resp;
		}
		if (vidt != null) {
			try {
				vid = Integer.parseInt(vidt);
			} catch (NumberFormatException ne) {
				resp.resu = ne.getMessage();
				return resp;
			}
		}

		sql = request.generalText;

		if (type == null || !type.equals("select")) {
			resp.resu = Resurses.getString("SQL_ILLEGAL_COMMAND");
		} else {
			if (!sql.toLowerCase().startsWith("select ")) {
				resp.resu = Resurses.getString("SQL_ILLEGAL_COMMAND");

			} else {

				Statement stm;

				try {
					stm = con.createStatement();
					ResultSet rs = stm.executeQuery(sql);

					ResultSetMetaData rsMetaData = rs.getMetaData();

					if (vid > 0 && rsMetaData.getColumnCount() == 1) {
						String empty = map.get("empty");
						if (empty != null && empty.equals("true")) {
							stm.executeUpdate("delete from viewunits where vid = "
									+ vid);

						}

						String sqlIns = "insert into viewunits (vid,pid)  select "
								+ vid + "," + sql.substring(7);
						stm.executeUpdate(sqlIns);
					} else {
						resp.vvTexts = new Vector<String[]>();

						int numberOfColumns = rsMetaData.getColumnCount();
						String[] hdrs = new String[numberOfColumns];
						for (int i = 0; i < numberOfColumns; i++) {
							hdrs[i] = rsMetaData.getColumnName(i + 1);
						}
						resp.vvTexts.add(hdrs);
						while (rs.next()) {
							String[] cols = new String[numberOfColumns];
							for (int i = 0; i < numberOfColumns; i++) {
								cols[i] = rs.getString(i + 1);
							}
							resp.vvTexts.add(cols);
						}

					}
					rs.close();
					stm.close();
				} catch (SQLException e) {
					resp.resu = e.getMessage();
				}
			}
		}
		return resp;
	}

	private SukuData executeCmdUpdatesettings(SukuData request,
			HashMap<String, String> map) {

		return updateSettings(request, map.get("index"), map.get("type"),
				map.get("name"));
	}

	private SukuData executeCmdVirtual(HashMap<String, String> map, SukuData fam) {
		int pid;
		String tmp;
		String type = map.get("type");
		tmp = map.get("pid");
		if (tmp != null) {
			pid = Integer.parseInt(tmp);
			if (type == null || type.equals("counts")) {
				fam = getVirtualPerson(pid);
			} else if (type.equals("relatives")) {
				fam = getVirtualRelatives(pid);
			}
		}
		return fam;
	}

	private SukuData executeCmdView(SukuData request,
			HashMap<String, String> map, SukuData fam) throws SukuException {
		ViewUtil vv = new ViewUtil(con);
		String action = map.get("action");

		String viewno = map.get("viewid");
		String viewname = map.get("viewname");
		String pidg = map.get("pid");
		String key = map.get("key");
		String gen = map.get("gen");
		String empty = map.get("empty");
		if ("removeview".equals(action)) {
			if (viewno != null) {

				try {
					int viewId = Integer.parseInt(viewno);
					fam = vv.removeView(viewId);
				} catch (NumberFormatException ne) {

					fam.resu = ne.getMessage();
					logger.log(Level.WARNING, "Bad view number", ne);
				}
			}
		} else if ("remove".equals(action) && viewno != null) {
			int viewId = Integer.parseInt(viewno);
			if (key != null) {
				if (key.equals("all")) {
					fam = vv.emptyView(viewId);
				} else if (key.equals("pidarray")) {
					fam = vv.removeViewUnits(viewId, request.pidArray);
				} else {
					fam.resu = Resurses.getString("GETSUKU_BAD_VIEW_COMMAND");
				}
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_VIEW_COMMAND");
			}
		} else if ("addview".equals(action)) {
			if (viewname != null) {

				try {
					fam = vv.addView(viewname);
				} catch (NumberFormatException ne) {
					fam.resu = ne.getMessage();
					logger.log(Level.WARNING, "Bad view number", ne);
				}
			}
		} else if ("add".equals(action)) {
			if (viewno != null && key != null) {
				int viewId = Integer.parseInt(viewno);
				if (key.equals("pidarray")) {
					// add pids to view
					fam = vv.addViewUnits(viewId, request.pidArray,
							(empty.equalsIgnoreCase("true")));

				} else if (key.toLowerCase().startsWith("desc")) {
					fam = vv.addViewDesc(viewId, Integer.parseInt(pidg), gen,
							(key.toUpperCase().equals("DESC_SPOUSES")),
							(empty.equalsIgnoreCase("true")));
				} else if (key.toLowerCase().equals("anc")) {
					fam = vv.addViewAnc(viewId, Integer.parseInt(pidg), gen,
							(empty.equalsIgnoreCase("true")));
				} else {
					fam.resu = Resurses.getString("GETSUKU_BAD_VIEW_COMMAND");
				}

			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_VIEW_COMMAND");
			}

		} else if ("get".equals(action) && pidg != null) {
			try {

				fam = vv.getViews(Integer.parseInt(pidg));
			} catch (NumberFormatException ne) {
				fam.resu = ne.getMessage();
				logger.log(Level.WARNING, "Bad view number", ne);
			}
		}
		return fam;
	}

	private SukuData executeCmdVariables(SukuData request,
			HashMap<String, String> map, SukuData fam) throws SukuException {
		String type = map.get("type");
		if ("get".equals(type)) {
			fam = getSukuInfo();
		} else if ("update".equals(type)) {
			fam = setSukuInfo(request);
		}
		return fam;
	}

	private SukuData executeCmdUpdate(SukuData request,
			HashMap<String, String> map, SukuData fam) throws SukuException {
		String type = map.get("type");
		if (type == null) {
			fam.resu = Resurses.getString("GETSUKU_BAD_UPDATE_TYPE");

		} else if (type.equals("person")) {
			fam = updatePerson(request);
			if (fam.resultPid > 0) {
				PersonShortData psp = new PersonShortData(this.con,
						fam.resultPid);
				if (request != null && request.persLong != null
						&& request.persLong.getNotices() != null) {
					boolean addname = false;
					for (int i = 0; i < request.persLong.getNotices().length; i++) {
						UnitNotice n = request.persLong.getNotices()[i];
						if (n.getTag().equals("NAME")) {
							if (addname) {
								psp.addName(n.getSurname(), n.getPatronym(),
										n.getPrefix(), n.getSurname(),
										n.getPostfix());
							} else {
								addname = true;
							}
						}
					}
				}

				fam.pers = new PersonShortData[1];
				fam.pers[0] = psp;
			}
		} else {
			fam.resu = "Wrong type [" + type + "] in update command";

		}
		return fam;
	}

	private void executeCmdSchema(HashMap<String, String> map, SukuData fam)
			throws SukuException {
		String type = map.get("type");
		if (type != null) {
			if (type.equals("get")) {
				fam.generalArray = new String[1];
				fam.generalArray[0] = this.schema;
			} else if (type.equals("count")) {
				fam.generalArray = LocalDatabaseUtility.getListOfSchemas(con);
			} else if (type.equals("set")) {
				String name = map.get("name");
				if (name != null) {
					this.schema = name;
					fam.resu = LocalDatabaseUtility.setSchema(con, this.schema);
				}
			} else if (type.equals("create")) {
				String name = map.get("name");
				if (name != null) {
					fam.resu = LocalDatabaseUtility.createNewSchema(con, name);
				}
			} else if (type.equals("drop")) {
				String name = map.get("name");
				if (name != null) {
					fam.resu = LocalDatabaseUtility.dropSchema(con, name);
					this.schema = "public";
				}
			} else {
				fam.resu = "Bad schema command";
			}
		}
	}

	private SukuData executeCmdRelatives(HashMap<String, String> map,
			SukuData fam) {
		int pid;
		String tmp;
		tmp = map.get("pid");
		if (tmp != null) {
			pid = Integer.parseInt(tmp);
			String tag = map.get("tag");
			// throw new SukuException("parents has not been implemented");
			fam = getRelatives(pid, tag);
		} else {
			fam.resu = Resurses.getString("GETSUKU_BAD_PID");
		}
		return fam;
	}

	private void executeCmdLogout() {
		try {
			con.close();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "logout", e);
			con = null;
		}
		con = null;
	}

	private void executeCmdPlaces(SukuData request, SukuData fam)
			throws SukuException {
		if (request != null) {
			fam.places = SuomiPlacesResolver.resolveSuomiPlaces(this.con,
					request.places);
		} else {
			fam.resu = Resurses.getString("GETSUKU_BAD_PLACES");
		}
	}

	private SukuData executeCmdPerson(HashMap<String, String> map, SukuData fam)
			throws SukuException {
		int pid;
		String tmp;
		String lang;
		tmp = map.get("pid");
		if (tmp != null) {
			pid = Integer.parseInt(tmp);
			String mode = map.get("mode");
			if ("short".equals(mode)) {
				PersonShortData psp = new PersonShortData(this.con, pid);
				fam.pers = new PersonShortData[1];
				fam.pers[0] = psp;

			} else if ("relations".equals(mode)) {
				tmp = map.get("pid");

				lang = map.get("lang");
				if (tmp != null) {
					pid = Integer.parseInt(tmp);

					GenGraphUtil gutil = new GenGraphUtil(con);
					fam = gutil.getGengraphData(pid, lang);

				}
			} else {

				lang = map.get("lang");
				PersonUtil u = new PersonUtil(con);
				fam = u.getFullPerson(pid, lang);
			}
		} else {
			fam.resu = Resurses.getString("GETSUKU_BAD_PID");
		}
		return fam;
	}

	private void executeCmdInitdb(HashMap<String, String> map)
			throws SukuException {
		String path = map.get("path");
		if (path == null) {
			path = "/sql/finfamily.sql";
		}

		SukuUtility data = SukuUtility.instance();
		data.createSukuDb(this.con, path);
	}

	private SukuData executeCmdImport(HashMap<String, String> map, SukuData fam)
			throws SukuException {
		String file;
		String lang;
		String type = map.get("type");
		if (type != null) {
			if (type.equals("backup")) {
				// this.createSukuDb();
				file = this.openFile;
				if (file == null) {
					file = map.get("filename");
				} else {
					fam.resu = Resurses.getString("GETSUKU_BAD_FILEMISSING");
				}
				lang = map.get("lang");
				logger.info("Suku 2004 FILE: " + file);
				if (file != null) {
					if (file.toLowerCase().endsWith("xml.gz")
							|| file.toLowerCase().endsWith("xml")
							|| file.toLowerCase().endsWith("zip")) {
						fam = import2004Data(file, lang);
					}
				}
			} else if (type.equals("gedcom")) {
				lang = map.get("lang");
				if (lang == null) {
					lang = Resurses.getLanguage();
				}

				ImportGedcomUtil inged = new ImportGedcomUtil(con);
				fam = inged.importGedcom(lang);
				inged = null;
			} else if (type.equals("other")) {
				ImportOtherUtil inoth = new ImportOtherUtil(con);
				String schema = map.get("schema");
				int viewId = -1;
				String view = map.get("view");
				String viewName = null;
				if (view != null) {
					viewId = Integer.parseInt(view);
					viewName = map.get("viewName");
				}
				fam = inoth.importOther(schema, viewId, viewName);

			}
		}
		return fam;
	}

	private SukuData executeCmdGroup(SukuData request,
			HashMap<String, String> map) throws SukuException {
		SukuData fam;
		String action = map.get("action");

		String key = map.get("key");
		String view = map.get("view");
		String group = map.get("group");
		String pidg = map.get("pid");
		String gen = map.get("gen");

		GroupUtil grp = new GroupUtil(con);
		fam = new SukuData();
		if (action == null)
			return null;
		fam.resu = Resurses.getString("DIALOG_GROUP_SERVER_BADCMD")
				+ " action=" + action;
		if (action.equals("remove")) {

			if (view != null) {

				int vid = Integer.parseInt(view);
				fam = grp.removeViewGroups(vid);

			} else if (key != null) {
				if (key.equals("all")) {
					fam = grp.removeAllGroups();
				} else if (key.equals("pidarray")) {
					fam = grp.removeSelectedGroups(request.pidArray);
				}
			} else if (group != null) {
				fam = grp.removeGroup(group);
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_GROUP_COMMAND");

			}

		} else if (action.equals("add")) {
			if (key != null) {
				if (key.equals("pidarray")) {
					fam = grp.addSelectedGroups(request.pidArray, group);
				} else if (key.startsWith("DESC") && pidg != null) {
					int pidb = Integer.parseInt(pidg);
					fam = grp.addDescendantsToGroup(pidb, group, gen,
							(!key.equals("DESC")));
				} else if (key.equals("ANC") && pidg != null) {
					int pidb = Integer.parseInt(pidg);
					fam = grp.addAncestorsToGroup(pidb, group, gen);
				} else {
					fam.resu = "key=" + key + " not supported";
				}
			} else if (view != null) {
				int vid = Integer.parseInt(view);
				fam = grp.addViewGroups(vid, group);
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_GROUP_COMMAND");
			}
		} else {
			fam.resu = "action=" + action + " not supported";
		}
		return fam;
	}

	private SukuData executeCmdCrlista(HashMap<String, String> map, SukuData fam)
			throws SukuException {
		if ("desc".equals(map.get("type"))) {
			fam = createDescendantLista(map.get("pid"));
		} else {
			fam.resu = Resurses.getString("GETSUKU_BAD_REPORT_TYPE");
		}
		return fam;
	}

	private SukuData executeCmdGet(HashMap<String, String> map, String cmd,
			SukuData fam) throws SukuException {
		String type = map.get("type");
		if (type == null) {
			fam.resu = Resurses.getString("ERR_TYPE_MISSING");
		} else if (type.endsWith("types")) {
			fam = getTypes(map.get("lang"));
			// SukuData txts = getTexts(map.get("lang"));
			// fam.vvTexts = txts.vvTexts;
		} else if (type.endsWith("conversions")) {
			fam = getConversions(map.get("lang"));
		} else if (type.endsWith("dbstatistics")) {
			String user = map.get("user");
			String password = map.get("password");
			String host = map.get("host");
			fam = getDbLista(host, user, password);
		} else if (cmd.equals("dblista")) {
			fam.generalArray = LocalDatabaseUtility.getListOfDatabases(con);
		} else if (type.endsWith("countries")) {
			fam = getCountryList();
		} else if (type.endsWith("ccodes")) {
			fam = getCountryCodes();
		} else {
			fam.resu = Resurses.getString("ERR_TYPE_INVALID");
		}
		return fam;
	}

	private SukuData executeCmdFamily(HashMap<String, String> map, SukuData fam)
			throws SukuException {
		int pid;
		String tmp;
		String aux;
		tmp = map.get("pid");
		aux = map.get("parents");
		if (tmp != null) {
			pid = Integer.parseInt(tmp);
			fam = getShortFamily(pid, aux == null ? false : true);
		} else {
			fam.resu = Resurses.getString("GETSUKU_BAD_PID");
		}
		return fam;
	}

	private SukuData executeCmdExcel(HashMap<String, String> map, SukuData fam)
			throws SukuException {
		String lang;
		String page = map.get("page");
		String path = map.get("path");
		String type = map.get("type");
		lang = map.get("lang");
		String all = map.get("all");
		if (lang == null) {
			lang = Resurses.getLanguage();
		}
		if (path == null) {
			path = this.openFile;
			if (path == null) {
				path = "resources/excel/TypesExcel.xls";
				// fam.resu = Resurses.getString("INVALID_FILE");

			}
		}
		if (type == null || type.equals("import")) {
			fam = importExcelData(path, page);
		} else if (type.equals("export")) {
			fam = exportExcelData(path, page, lang,
					(all != null && all.equals("true")) ? true : false);
		} else {
			fam.resu = Resurses.getString("BAD_COMMAND_TYPE");
		}
		return fam;
	}

	private SukuData executeCmdDelete(HashMap<String, String> map, SukuData fam) {
		int pid;
		String tmp;
		tmp = map.get("pid");
		if (tmp != null) {
			pid = Integer.parseInt(tmp);
			fam = deletePerson(pid);
		} else {
			fam.resu = Resurses.getString("GETSUKU_BAD_PID");
		}
		return fam;
	}

	private SukuData executeCmdCrtables(HashMap<String, String> map,
			SukuData fam) throws SukuException {
		if ("desc".equals(map.get("type"))) {
			fam = createDescTables(map.get("order"), map.get("generations"),
					map.get("spougen"), map.get("chilgen"), map.get("adopted"),
					map.get("pid"));
		} else if ("anc".equals(map.get("type"))) {

			fam = createAncTables(map.get("order"), map.get("generations"),
					map.get("family"), map.get("pid"));

		} else {
			fam.resu = Resurses.getString("GETSUKU_BAD_REPORT_TYPE");
		}
		return fam;
	}

	private SukuData executeCmdCreate(HashMap<String, String> map, SukuData fam) {
		String tmp;
		String lang;
		String type = map.get("type");
		if ("gedcom".equals(type)) {
			lang = map.get("lang");
			String path = map.get("file");
			String db = map.get("db");
			tmp = map.get("viewId");
			int viewId = 0;
			if (tmp != null) {
				viewId = Integer.parseInt(tmp);
			}
			int surety = 100;
			tmp = map.get("surety");

			if (tmp != null) {
				surety = Integer.parseInt(tmp);
			}
			tmp = map.get("charid");
			int charid = Integer.parseInt(tmp);
			boolean incImages = map.get("images") != null ? true : false;

			ExportGedcomUtil exgen = new ExportGedcomUtil(con);
			fam = exgen.exportGedcom(db, path, lang, viewId, surety, charid,
					incImages);
		} else if ("backup".equals(type)) {
			String path = map.get("file");
			String db = map.get("db");
			ExportBackupUtil exb = new ExportBackupUtil(con);

			fam = exb.exportBackup(path, db);
		}
		return fam;
	}

	private SukuData getCountryList() {
		SukuData res = new SukuData();
		ArrayList<String> v = new ArrayList<String>();

		try {
			String sql = "select a.countrycode,a.placename,b.othername"
					+ " from placelocations as a left join "
					+ "placeothernames as b on a.placename=b.placename "
					+ "and a.countrycode = b.countrycode "
					+ "where location[0] = 0 and location[1] = 0";

			String prev = "";

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {

				String cc = rs.getString(1);
				String nm = rs.getString(2);
				String ot = rs.getString(3);

				if (!cc.equals(prev)) {
					v.add(cc + ";" + nm + ";" + ot);
				}
				prev = cc;

			}
			rs.close();
			stm.close();
			res.generalArray = v.toArray(new String[0]);

		} catch (SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}
		return res;
	}

	private SukuData getCountryCodes() {
		SukuData res = new SukuData();
		ArrayList<String> v = new ArrayList<String>();

		try {
			String sql = "select a.countrycode,a.placename,b.othername"
					+ " from placelocations as a left join "
					+ "placeothernames as b on a.placename=b.placename "
					+ "and a.countrycode = b.countrycode "
					+ "where location[0] = 0 and location[1] = 0";

			String prev = "";

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {

				String cc = rs.getString(1);
				String nm = rs.getString(2);
				String ot = rs.getString(3);

				if (!cc.equals(prev)) {
					v.add(nm + ";" + cc);
					if (ot != null) {
						v.add(ot + ";" + cc);
					}
				} else {
					v.add(ot + ";" + cc);
				}
				prev = cc;

			}
			rs.close();
			stm.close();
			res.generalArray = v.toArray(new String[0]);

		} catch (SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}
		return res;
	}

	private SukuData setSukuInfo(SukuData request) throws SukuException {
		SukuData response = new SukuData();
		String sql = "update sukuvariables set owner_name=?,owner_address=?,owner_postalcode=?,owner_postoffice=?,"
				+ "owner_state=?,owner_country=?,owner_email=?,owner_webaddress=?,owner_info=? ";

		try {

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, request.generalArray[0]);
			pst.setString(2, request.generalArray[1]);
			pst.setString(3, request.generalArray[2]);
			pst.setString(4, request.generalArray[3]);
			pst.setString(5, request.generalArray[4]);
			pst.setString(6, request.generalArray[5]);
			pst.setString(7, request.generalArray[6]);
			pst.setString(8, request.generalArray[7]);
			pst.setString(9, request.generalArray[8]);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SukuException("setSukuInfo " + e.getMessage());

		}

		return response;
	}

	private SukuData getSukuInfo() throws SukuException {

		SukuData response = new SukuData();
		String sql = "select * from sukuvariables";

		Statement stm;
		try {
			stm = con.createStatement();

			ResultSet rs = stm.executeQuery(sql);
			response.generalArray = new String[9];
			if (rs.next()) {
				response.generalArray[0] = rs.getString("owner_name");
				response.generalArray[1] = rs.getString("owner_address");
				response.generalArray[2] = rs.getString("owner_postalcode");
				response.generalArray[3] = rs.getString("owner_postoffice");
				response.generalArray[4] = rs.getString("owner_state");
				response.generalArray[5] = rs.getString("owner_country");
				response.generalArray[6] = rs.getString("owner_email");
				response.generalArray[7] = rs.getString("owner_webaddress");
				response.generalArray[8] = rs.getString("owner_info");

			}

			rs.close();
			stm.close();

		} catch (SQLException e) {
			throw new SukuException("SukuVariables: " + e.getMessage());
		}

		return response;
	}

	private SukuData getDbLista(String host, String user, String password)
			throws SukuException {
		SukuData response = new SukuData();
		String sql = "select datname from pg_database where datname not in ('postgres','template1','template0') order by datname ";

		ArrayList<String> lista = new ArrayList<String>();
		Statement stm = null;
		try {
			stm = con.createStatement();

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				lista.add(rs.getString(1));
			}
			rs.close();

			// return sb.toString().split(";");

		} catch (SQLException e) {
			logger.log(Level.WARNING, "databasenames list", e);

			throw new SukuException(e);
		} finally {
			if (stm != null) {
				try {
					stm.close();
				} catch (SQLException ignored) {
					// SQLException ignored
				}
			}
		}

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < lista.size(); i++) {

			String constring = "jdbc:postgresql://" + host + "/" + lista.get(i)
					+ "?user=" + user;
			logger.fine("Connection: " + constring);
			if (password != null && !password.isEmpty()) {

				constring += "&password=" + password;
			}
			ArrayList<String> sv = new ArrayList<String>();
			Connection mycon = null;
			try {
				mycon = DriverManager.getConnection(constring);
				result.add("==========================");
				String sqls = "select * from pg_namespace where nspname not like 'pg%' and nspname <> 'information_schema' ";

				stm = mycon.createStatement();
				ResultSet rss = stm.executeQuery(sqls);
				while (rss.next()) {
					sv.add(rss.getString(1));
				}
				rss.close();
				for (int j = 0; j < sv.size(); j++) {

					try {
						String schema = sv.get(j);
						result.add(lista.get(i) + ":" + schema);
						sql = "select * from " + schema + ".sukuvariables";

						ResultSet rs = stm.executeQuery(sql);
						while (rs.next()) {
							result.add("    " + Resurses.getString("DB_OWNER")
									+ " [" + rs.getString("owner_name") + "]");

							Timestamp ts = rs.getTimestamp("createdate");

							result.add("    "
									+ Resurses.getString("DB_CREATED") + " ["
									+ ts.toString() + "]");
						}
						rs.close();

						sql = "select count(*) from " + schema + ".unit";

						stm = mycon.createStatement();
						rs = stm.executeQuery(sql);
						while (rs.next()) {
							result.add("    "
									+ Resurses.getString("DB_UNIT_COUNT")
									+ " [" + rs.getInt(1) + "]");
						}
						rs.close();

						sql = "select max(coalesce(modified,createdate)) as maxi from "
								+ schema + ".unitnotice";

						stm = mycon.createStatement();
						rs = stm.executeQuery(sql);
						while (rs.next()) {
							Timestamp ts = rs.getTimestamp("maxi");
							if (ts != null) {
								result.add("    "
										+ Resurses
												.getString("DB_UNIT_LATESTCHANGE")
										+ " [" + ts.toString() + "]");
							}
						}
						rs.close();

						// rs =
						// stm.executeQuery("select count(*) from unitnotice");
						// while (rs.next()) {
						// sb.append("unitnotice [");
						// sb.append(rs.getInt(1));
						// sb.append("]; ");
						// }
						// rs.close();
						//
						// rs =
						// stm.executeQuery("select count(*) from unitlanguage");
						// while (rs.next()) {
						// sb.append("unitlanguage [");
						// sb.append(rs.getInt(1));
						// sb.append("]; ");
						// }
						// rs.close();
						//
						// rs =
						// stm.executeQuery("select count(*) from relation");
						// while (rs.next()) {
						// sb.append("relation [");
						// sb.append(rs.getInt(1));
						// sb.append("]; ");
						// }
						// rs.close();
						//
						// rs =
						// stm.executeQuery("select count(*) from relationnotice");
						// while (rs.next()) {
						// sb.append("relationnotice [");
						// sb.append(rs.getInt(1));
						// sb.append("]; ");
						// }
						// rs.close();
						//
						// rs =
						// stm.executeQuery("select count(*) from relationlanguage");
						// while (rs.next()) {
						// sb.append("relationlanguage [");
						// sb.append(rs.getInt(1));
						// sb.append("]; ");
						// }
						// rs.close();
						//
						// rs =
						// stm.executeQuery("select count(*) from conversions");
						// while (rs.next()) {
						// sb.append("conversions [");
						// sb.append(rs.getInt(1));
						// sb.append("]; ");
						// }
						// rs.close();
						//
						// rs = stm.executeQuery("select count(*) from views");
						// while (rs.next()) {
						// sb.append("views [");
						// sb.append(rs.getInt(1));
						// sb.append("]; ");
						// }
						// rs.close();

						// ///////////////////////
					} catch (SQLException ee) {
						result.add(ee.getMessage());
						ee.printStackTrace();
					}
				}

			} catch (SQLException e) {
				result.add(e.getMessage());
				e.printStackTrace();

			} finally {
				if (stm != null) {
					try {
						stm.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (mycon != null) {
					try {
						mycon.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

		}
		response.generalArray = result.toArray(new String[0]);

		return response;
	}

	private SukuData getConversions(String langu) {
		SukuData res = new SukuData();

		res.vvTexts = new Vector<String[]>();
		try {
			String sql = "select fromtext,rule,totext from conversions where langCode = '"
					+ langu + "' order by fromtext,rule";

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				String[] auxx = new String[3];
				auxx[0] = rs.getString(1);
				auxx[1] = rs.getString(2);
				auxx[2] = rs.getString(3);
				res.vvTexts.add(auxx);
			}
			rs.close();
			stm.close();
		} catch (SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}
		return res;
	}

	private SukuData exportExcelData(String path, String page, String langCode,
			boolean doAll) throws SukuException {
		SukuData resu = new SukuData();
		if ("conversions".equals(page)) {
			ExcelImporter ex = new ExcelImporter();
			return ex.exportCoordinates(this.con, path, langCode, doAll);
		}
		return resu;
	}

	private SukuData createAncTables(String order, String generations,
			String withFamily, String spid) throws SukuException {
		SukuData dat;
		try {
			int geners = Integer.parseInt(generations);
			boolean family = Boolean.parseBoolean(withFamily);
			ReportUtil x = new ReportUtil(con);
			dat = x.createAncestorStructure(Integer.parseInt(spid), geners,
					family, order);
		} catch (Exception e) {
			throw new SukuException(e);
		}
		return dat;
	}

	private SukuData getUnitCount() throws SukuException {
		SukuData resp = new SukuData();
		Statement stm;
		try {

			stm = con.createStatement();
			boolean unitExists = false;
			ResultSet rs = stm
					.executeQuery("select tablename from pg_tables where tablename = 'unit' and schemaname = '"
							+ this.schema + "'");

			if (rs.next()) {
				unitExists = true;
			} else {
				resp.resuCount = -1;
			}
			rs.close();
			if (unitExists) {
				rs = stm.executeQuery("select count(*) from unit");
				if (rs.next()) {
					resp.resuCount = rs.getInt(1);
				}
				rs.close();
			}
			stm.close();

		} catch (SQLException e) {
			throw new SukuException(e);

		}
		return resp;
	}

	private SukuData getIntelliSensData() {
		SukuData res = new SukuData();
		res.vvTexts = new Vector<String[]>();
		ArrayList<String> vPlaces = new ArrayList<String>();
		ArrayList<String> vGivennames = new ArrayList<String>();
		ArrayList<String> vPatronymes = new ArrayList<String>();
		ArrayList<String> vSurnames = new ArrayList<String>();
		ArrayList<String> vDescriptions = new ArrayList<String>();
		ArrayList<String> vTypes = new ArrayList<String>();
		// Vector<String> vGroupd = new Vector<String>();
		try {
			String sql = "select place,count(*) from unitnotice "
					+ "where place is not null group by place  "
					+ "order by 2 desc limit 256";
			// having count(*) > 3
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				vPlaces.add(rs.getString(1));
			}
			rs.close();

			res.vvTexts.add(vPlaces.toArray(new String[0]));

			// sql = "select givenname,count(*) from unitnotice "
			// + "where givenname is not null group by givenname "
			// + "order by 2 desc limit 256";

			sql = "select split_part(givenname,' ',1),count(*) "
					+ "from unitnotice where givenname is not null "
					+ "group by split_part(givenname,' ',1)"
					+ "order by 2 desc limit 256";

			rs = stm.executeQuery(sql);

			while (rs.next()) {
				vGivennames.add(rs.getString(1));
			}
			rs.close();
			res.vvTexts.add(vGivennames.toArray(new String[0]));

			sql = "select patronym,count(*) from unitnotice "
					+ "where patronym is not null group by patronym "
					+ "order by 2 desc limit 256";

			rs = stm.executeQuery(sql);

			while (rs.next()) {
				vPatronymes.add(rs.getString(1));
			}
			rs.close();

			res.vvTexts.add(vPatronymes.toArray(new String[0]));

			sql = "select surname,count(*) from unitnotice "
					+ "where surname is not null group by surname "
					+ "order by 2 desc limit 256";

			rs = stm.executeQuery(sql);

			while (rs.next()) {
				vSurnames.add(rs.getString(1));
			}
			rs.close();
			res.vvTexts.add(vSurnames.toArray(new String[0]));

			sql = "select tag || ';' || description,count(*) from unitnotice "
					+ "where description is not null group by tag,description "
					+ "order by 2 desc limit 1024";

			rs = stm.executeQuery(sql);

			while (rs.next()) {
				vDescriptions.add(rs.getString(1));
			}
			rs.close();
			res.vvTexts.add(vDescriptions.toArray(new String[0]));

			sql = "select noticetype,count(*) from unitnotice "
					+ "where noticetype is not null group by noticetype "
					+ "order by 2 desc limit 256";

			rs = stm.executeQuery(sql);

			while (rs.next()) {
				vTypes.add(rs.getString(1));
			}
			rs.close();
			res.vvTexts.add(vTypes.toArray(new String[0]));
			sql = "select groupid,count(*) from unit "
					+ "where groupid is not null group by groupid "
					+ "order by 2 desc limit 256";

			rs = stm.executeQuery(sql);

			while (rs.next()) {
				vTypes.add(rs.getString(1));
			}
			rs.close();
			res.vvTexts.add(vTypes.toArray(new String[0]));
			stm.close();

		} catch (SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}

		return res;

	}

	private SukuData getVirtualPerson(int pid) {
		SukuData resp = new SukuData();
		resp.pidArray = new int[3];
		resp.pidArray[0] = resp.pidArray[1] = resp.pidArray[2] = 0;

		String sql = "select tag,count(*) from relation where pid = ? "
				+ " group by tag";

		PreparedStatement pstm;
		try {
			pstm = this.con.prepareStatement(sql);

			pstm.setInt(1, pid);
			ResultSet prs = pstm.executeQuery();
			while (prs.next()) {
				String rtag = prs.getString(1);

				if (rtag.equals("HUSB") || rtag.equals("WIFE")) {
					resp.pidArray[1] += prs.getInt(2);

				} else if (rtag.equals("CHIL")) {
					resp.pidArray[0] += prs.getInt(2);

				} else if (rtag.equals("MOTH") || rtag.equals("FATH")) {
					resp.pidArray[2] += prs.getInt(2);

				}

			}
			prs.close();
			pstm.close();
		} catch (SQLException e) {
			resp.resu = e.getMessage();
		}
		return resp;
	}

	private SukuData getVirtualRelatives(int pid) {
		SukuData resp = new SukuData();
		ArrayList<Integer> relas = new ArrayList<Integer>();

		String sql = "select b.pid from relation as a inner join relation as b on a.rid=b.rid where  b.pid<>a.pid and a.pid=? ";
		PreparedStatement pstm;
		try {
			pstm = this.con.prepareStatement(sql);

			pstm.setInt(1, pid);
			ResultSet prs = pstm.executeQuery();
			while (prs.next()) {
				int bpid = prs.getInt(1);
				relas.add(bpid);
			}
			prs.close();
			pstm.close();
			resp.pidArray = new int[relas.size()];
			for (int i = 0; i < relas.size(); i++) {
				resp.pidArray[i] = relas.get(i);
			}

		} catch (SQLException e) {
			resp.resu = e.getMessage();
		}
		return resp;
	}

	private SukuData createDescendantLista(String spid) throws SukuException {
		SukuData dat;
		try {
			ReportUtil x = new ReportUtil(con);
			dat = x.createDescendantLista(Integer.parseInt(spid));
		} catch (Exception e) {
			throw new SukuException(e);
		}
		return dat;
	}

	private SukuData getDbStatistics() {
		SukuData response = new SukuData();
		String sql = "select count(*) from unit";
		Statement stm;
		StringBuilder sb = new StringBuilder();
		try {
			stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				sb.append("unit [");
				sb.append(rs.getInt(1));
				sb.append("]; ");
			}
			rs.close();

			rs = stm.executeQuery("select count(*) from unitnotice");
			while (rs.next()) {
				sb.append("unitnotice [");
				sb.append(rs.getInt(1));
				sb.append("]; ");
			}
			rs.close();

			rs = stm.executeQuery("select count(*) from unitlanguage");
			while (rs.next()) {
				sb.append("unitlanguage [");
				sb.append(rs.getInt(1));
				sb.append("]; ");
			}
			rs.close();

			rs = stm.executeQuery("select count(*) from relation");
			while (rs.next()) {
				sb.append("relation [");
				sb.append(rs.getInt(1));
				sb.append("]; ");
			}
			rs.close();

			rs = stm.executeQuery("select count(*) from relationnotice");
			while (rs.next()) {
				sb.append("relationnotice [");
				sb.append(rs.getInt(1));
				sb.append("]; ");
			}
			rs.close();

			rs = stm.executeQuery("select count(*) from relationlanguage");
			while (rs.next()) {
				sb.append("relationlanguage [");
				sb.append(rs.getInt(1));
				sb.append("]; ");
			}
			rs.close();

			rs = stm.executeQuery("select count(*) from conversions");
			while (rs.next()) {
				sb.append("conversions [");
				sb.append(rs.getInt(1));
				sb.append("]; ");
			}
			rs.close();

			rs = stm.executeQuery("select count(*) from views");
			while (rs.next()) {
				sb.append("views [");
				sb.append(rs.getInt(1));
				sb.append("]; ");
			}
			rs.close();
			stm.close();

			response.resu = sb.toString();
		} catch (SQLException e) {
			response.resu = e.getMessage();
		}

		return response;
	}

	private SukuData deletePerson(int pid) {
		PersonUtil u = new PersonUtil(con);

		return u.deletePerson(pid);
	}

	private SukuData updateSettings(SukuData request, String index,
			String type, String name) {

		SukuData resp = new SukuData();

		try {
			String sql = "delete from sukusettings where  settingtype = ? and settingname = ? ";

			String insSql = "insert into sukusettings "
					+ "(settingtype,settingindex,settingname,settingvalue) values (?,?,?,?)";
			if (name != null) {

				PreparedStatement pst = con.prepareStatement(sql);
				pst.setString(1, type);
				pst.setString(2, name);
				int lukuri = pst.executeUpdate();
				pst.close();

				logger.fine("deleted [" + lukuri + "] settings where type ="
						+ type + " and name = " + name);
				pst = con.prepareStatement(insSql);
				for (int i = 0; i < request.generalArray.length; i++) {
					pst.setString(1, type);
					pst.setInt(2, i);
					pst.setString(3, name);
					pst.setString(4, request.generalArray[i]);
					pst.executeUpdate();
				}
				pst.close();
			} else {
				sql = "delete from sukusettings where settingtype = ?";
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setString(1, type);
				pst.executeUpdate();
				pst.close();

				pst = con.prepareStatement(insSql);

				for (int i = 0; i < request.generalArray.length; i++) {
					String[] parts = request.generalArray[i].split("=");
					if (parts.length == 2) {
						pst.setString(1, type);
						pst.setInt(2, i);
						pst.setString(3, parts[0]);
						pst.setString(4, parts[1]);
						pst.executeUpdate();
					}
				}
				pst.close();

			}
		} catch (SQLException e) {
			resp.resu = e.getMessage();
			e.printStackTrace();
			return resp;
		}

		return resp;
	}

	private SukuData getRelatives(int pid, String tag) {
		SukuData res = new SukuData();

		String sql;

		if (tag.equals("FATH")) {
			sql = "select bid from father where aid = " + pid;
		} else if (tag.equals("MOTH")) {
			sql = "select bid from mother where aid = " + pid;
		} else if (tag.equals("WIFE")) {
			sql = "select bid from wife where aid = " + pid;
		} else if (tag.equals("HUSB")) {
			sql = "select bid from husband where aid = " + pid;
		} else {
			sql = "select bid from child where aid = " + pid;
		}

		ArrayList<Integer> vxv = new ArrayList<Integer>();

		try {
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				int idx = rs.getInt(1);
				vxv.add(idx);

			}
			rs.close();
			stm.close();

			res.pidArray = new int[vxv.size()];
			for (int i = 0; i < vxv.size(); i++) {
				res.pidArray[i] = vxv.get(i);
			}

		} catch (SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}

		return res;
	}

	private SukuData updatePerson(SukuData request) {
		PersonUtil u = new PersonUtil(con);

		return u.updatePerson(request);

	}

	private SukuData createDescTables(String order, String generations,
			String spouGen, String chilGen, String adopted, String spid)
			throws SukuException {
		SukuData dat;
		try {
			int geners = Integer.parseInt(generations);
			int spgen = Integer.parseInt(spouGen);
			int chgen = Integer.parseInt(chilGen);
			boolean adop = false;
			if (adopted != null && adopted.equals("true")) {
				adop = true;
			}

			ReportUtil x = new ReportUtil(con);
			dat = x.createDescendantStructure(Integer.parseInt(spid), geners,
					spgen, chgen, order, adop);
		} catch (Exception e) {
			throw new SukuException(e);
		}
		return dat;
	}

	private SukuData getTypes(String langu) {
		SukuData res = new SukuData();
		String vx[];
		res.vvTypes = new Vector<String[]>();
		try {
			String sql = "select tag,name,reportname,tagtype,rule from types where langCode = '"
					+ langu + "' order by tagtype,typeid";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				vx = new String[5];
				vx[0] = rs.getString(1);
				vx[1] = rs.getString(2);
				vx[2] = rs.getString(3);
				vx[3] = rs.getString(4);
				vx[4] = rs.getString(5);
				res.vvTypes.add(vx);
			}
			rs.close();

			sql = "select distinct tag from unitnotice where tag not in  (select tag from types)";
			rs = stm.executeQuery(sql);

			while (rs.next()) {
				vx = new String[5];
				vx[0] = rs.getString(1);
				vx[1] = vx[0];
				vx[2] = vx[0];
				vx[3] = "Notices";
				vx[4] = null;
				res.vvTypes.add(vx);
			}
			stm.close();

		} catch (SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}

		return res;

	}

	// private SukuData getTexts(String langu) {
	// SukuData res = new SukuData();
	// String vx[];
	// res.vvTexts = new Vector<String[]>();
	// try {
	// String sql = "select tag,name,tagtype from texts where langCode = '"
	// + langu + "' ";
	// Statement stm = con.createStatement();
	// ResultSet rs = stm.executeQuery(sql);
	//
	// while (rs.next()) {
	// vx = new String[4];
	// vx[0] = rs.getString(1);
	// vx[1] = rs.getString(2);
	// vx[2] = rs.getString(3);
	// res.vvTexts.add(vx);
	// }
	// stm.close();
	//
	// } catch (SQLException e) {
	// res.resu = e.getMessage();
	// e.printStackTrace();
	// }
	//
	// return res;
	//
	// }

	private SukuData saveReportSettings(HashMap<String, String> map)
			throws SukuException {
		SukuData res = new SukuData();
		int index;

		String sql;
		String tmp = map.get("index");
		if (tmp == null) {
			throw new SukuException("Setting index missing");
		}
		String type = map.get("type");
		if (type == null) {
			throw new SukuException("Setting type missing");
		}
		try {
			index = Integer.parseInt(tmp);
		} catch (NumberFormatException ne) {
			index = -1;
		}
		if (index > 11 || index < 0)
			index = 0;

		int luku = -1;
		PreparedStatement pstm;
		ResultSet rs;
		sql = "select count(*) from SukuSettings where settingIndex = ? and settingtype = ?";
		try {
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, index);
			pstm.setString(2, type);
			rs = pstm.executeQuery();

			if (rs.next()) {
				luku = rs.getInt(1);
			}
			rs.close();
			pstm.close();
		} catch (SQLException se) {
			logger.log(Level.WARNING, "Saving report settings [" + luku + "]",
					se);

		}
		try {

			sql = "delete from SukuSettings where SettingIndex = ? and settingtype = ?";
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, index);
			pstm.setString(2, type);
			pstm.executeUpdate();
			pstm.close();

			// }

			sql = "insert into SukuSettings (SettingType,SettingIndex,SettingName,SettingValue) "
					+ "values (?,?,?,?)";

			pstm = con.prepareStatement(sql);

			Set<Map.Entry<String, String>> entries = map.entrySet();
			Iterator<Map.Entry<String, String>> ee = entries.iterator();

			while (ee.hasNext()) {
				Map.Entry<String, String> entry = ee.next();
				if (!entry.getKey().equals("index")
						&& !entry.getKey().equals("cmd")
						&& !entry.getKey().equals("type")) {
					pstm.setString(1, type);
					pstm.setInt(2, index);
					pstm.setString(3, entry.getKey().toString());
					pstm.setString(4, entry.getValue().toString());
					pstm.executeUpdate();
				}

			}
			pstm.close();

		} catch (SQLException e) {
			logger.log(Level.WARNING, "report settings", e);

			res.resu = e.getMessage();
		}
		return res;
	}

	private SukuData getDbVersion() {
		SukuData resdat = new SukuData();

		try {
			resdat.generalArray = Upload.getServerVersion(con);
		} catch (SQLException e) {
			resdat.resu = e.getMessage();
		}

		return resdat;
	}

	/**
	 * Upload hiski family to database
	 * 
	 * @param family
	 * @return
	 * @throws SukuException
	 */
	private SukuData uploadFamily(SukuData family) throws SukuException {

		SukuData resdat = new SukuData();

		try {
			resdat = Upload.uploadFamilies(con, family);
			resdat.pers = new PersonShortData[resdat.pidArray.length];
			for (int i = 0; i < resdat.pers.length; i++) {

				resdat.pers[i] = new PersonShortData(this.con,
						resdat.pidArray[i]);

			}
		} catch (SQLException e) {
			resdat.resu = e.getMessage();
			logger.log(Level.WARNING, "uploading family", e);

		}

		// resdat.resu = resda;

		return resdat;
	}

	/**
	 * Importing data from excel repository to database
	 * 
	 * @param path
	 * @param page
	 * @return
	 * @throws SukuException
	 */
	private SukuData importExcelData(String path, String page)
			throws SukuException {

		if ("coordinates".equals(page)) {
			ExcelImporter ex = new ExcelImporter();
			return ex.importCoordinates(this.con, path);

		} else {
			ExcelImporter ex = new ExcelImporter();
			return ex.importTypes(this.con, path);

		}

	}

	/**
	 * sets the opened file name.
	 * 
	 * @param f
	 *            the new open file
	 */
	@Override
	public void setOpenFile(String f) {
		this.openFile = f;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.server.SukuServer#getSukuData(java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(String... params) throws SukuException {
		return getSukuData(null, params);
	}

}
