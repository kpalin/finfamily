package fi.kaila.suku.server;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.server.utils.GroupUtil;
import fi.kaila.suku.server.utils.PersonUtil;
import fi.kaila.suku.server.utils.ReportUtil;
import fi.kaila.suku.server.utils.Upload;
import fi.kaila.suku.server.utils.ViewUtil;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.data.ExcelImporter;
import fi.kaila.suku.util.data.SukuUtility;
import fi.kaila.suku.util.local.LocalDatabaseUtility;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationLanguage;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.RelationShortData;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitLanguage;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * Serverpään mm tietokantaa käsittelevä
 * 
 * @author FIKAAKAIL
 * 
 */
public class SukuServerImpl implements SukuServer {

	private String dbDriver = "org.postgresql.Driver";
	private String dbConne = "jdbc:postgresql://localhost/sukuproto?user=kalle&password=kalle";
	private Connection con = null;
	private String schema = null;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private String openFile = null;
	private String toDoTagName = null;

	/**
	 * Constructor
	 * 
	 * @throws SukuException
	 */
	public SukuServerImpl() throws SukuException {
		this.schema = "public";

		try {
			Class.forName(this.dbDriver);
		} catch (ClassNotFoundException e) {
			throw new SukuException(e);
		}
		this.toDoTagName = Resurses.getString(Resurses.COLUMN_T_TODO);

	}

	/**
	 * used by tomcat from webstart idea is that database uses different scema
	 * for each user
	 * 
	 * @param schema
	 * @throws SukuException
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

	@Override
	public void getConnection(String host, String dbname, String userid,
			String passwd) throws SukuException {
		this.dbConne = "jdbc:postgresql://" + host + "/" + dbname + "?user="
				+ userid;
		logger.fine("Connection: " + this.dbConne + ";schema: " + this.schema);
		if (passwd != null && !passwd.equals("")) {

			this.dbConne += "&password=" + passwd;
		}

		try {
			this.con = DriverManager.getConnection(this.dbConne);
			Statement stm = this.con.createStatement();
			stm.executeUpdate("set search_path to " + this.schema);
			// boolean needsState = false;
			// try {
			// ResultSet rs = stm
			// .executeQuery("select state from unitnotice limit 1");
			// } catch (SQLException e) {
			// needsState = true;
			// }
			// if (needsState) {
			// stm
			// .executeUpdate("ALTER TABLE unitnotice ADD COLUMN state varchar");
			// }

			stm.close();

		} catch (SQLException e) {
			e.printStackTrace();

			throw new SukuException(e.getMessage());
		}

	}

	private void import2004Data(String path, String oldCode)
			throws SukuException {
		SukuUtility data = SukuUtility.instance();
		data.createSukuDb(this.con, "/sql/finfamily.sql");

		logger.fine("database created for " + path);

		try {
			data.import2004Data(this.con, path, oldCode);
		} catch (Exception e) {
			throw new SukuException(e);
		}

	}

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

	private SukuData getViewList() throws SukuException {
		SukuData vlist = new SukuData();
		String sql = "select vid,name from views order by name";

		Vector<String> v = new Vector<String>();
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

		String sql = "select s.bid,r.relationrow,r.tag,rn.fromdate,rd.fromdate "
				+ "from ((spouse as s inner join relation as r on s.rid = r.rid "
				+ "and s.tag=r.tag) left join relationNotice as rn on r.rid = rn.rid  "
				+ "and rn.tag='MARR') left join relationNotice as rd on r.rid = rd.rid  "
				+ "and rd.tag='DIV' where s.aid = ? order by r.relationrow ";

		// String sql = "select s.bid,r.relationrow,r.tag,rn.fromdate " +
		// "from (spouse as s inner join relation as r on s.rid = r.rid and s.tag=r.tag) "
		// +
		// "left join relationNotice as rn on r.rid = rn.rid  " +
		// "where s.aid = ? order by r.relationrow";

		// System.out.println( sql);
		Vector<RelationShortData> v = new Vector<RelationShortData>();
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
				// System.out.println("XX: " + tag + "/" + relPid + "/" +
				// relOrder + "/" + aux + "/" + div);

				RelationShortData rel = new RelationShortData(pid, relPid,
						relOrder, tag);

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

			sql = "select c.bid,r.relationrow,r.tag "
					+ "from (child as c inner join relation as r on c.rid = r.rid and c.tag=r.tag) "
					+ "where c.aid=? " + "order by r.relationrow";

			pstm = this.con.prepareStatement(sql);

			pstm.setInt(1, pid);

			rs = pstm.executeQuery();
			while (rs.next()) {
				RelationShortData rel = new RelationShortData(pid,
						rs.getInt(1), rs.getInt(2), rs.getString(3));
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
					sql = "select bid,tag from parent where aid =? ";
					Vector<Integer> parents = new Vector<Integer>();
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

			sql = "select c.bid,r.relationrow,r.tag "
					+ "from (parent as c inner join relation as r on c.rid = r.rid "
					+ "and c.tag=r.tag) where c.aid=?  order by r.relationrow";

			pstm = this.con.prepareStatement(sql);

			pstm.setInt(1, pid);

			rs = pstm.executeQuery();
			while (rs.next()) {
				RelationShortData rel = new RelationShortData(pid,
						rs.getInt(1), rs.getInt(2), rs.getString(3));
				v.add(rel);

			}

			RelationShortData[] dt = new RelationShortData[0];
			fam.rels = v.toArray(dt);
			Vector<PersonShortData> pv = new Vector<PersonShortData>();

			PersonShortData p = new PersonShortData(this.con, pid);
			pv.add(p);

			for (int i = 0; i < fam.rels.length; i++) {
				int ipid = fam.rels[i].getRelationPid();
				// String memType=fam.rels[i].getTag();
				if (ipid != pid) {
					// if ("CHIL".equals(fam.rels[i].getTag())){
					// memType=PersonShortData.FAM_TYPE_CHILD;
					// }
					//					
					p = new PersonShortData(this.con, ipid);
					pv.add(p);
				}
			}
			PersonShortData[] dp = new PersonShortData[0];
			fam.pers = pv.toArray(dp);

			pstm.close();

			return fam;

		} catch (SQLException e) {
			throw new SukuException(e);
		}

	}

	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException {
		String auxes[];

		int j;
		StringBuffer sb = new StringBuffer();
		for (j = 0; j < params.length; j++) {
			if (j > 0)
				sb.append(";");
			sb.append(params[j]);
		}
		logger.fine(sb.toString());

		HashMap<String, String> map = new HashMap<String, String>();
		int pid;
		String tmp;
		String aux;
		for (int i = 0; i < params.length; i++) {
			auxes = params[i].split("=");
			if (auxes.length == 2) {
				map.put(auxes[0], auxes[1]);
			}
		}
		String cmd = map.get("cmd");
		String file;
		String lang;
		if (cmd == null)
			return null;
		SukuData fam = new SukuData();
		;
		if (cmd.equals("plist")) {
			fam = queryDatabase(params);
			// fam = getShortPersonList(params);
		} else if (cmd.equals("family")) {
			tmp = map.get("pid");
			aux = map.get("parents");
			if (tmp != null) {
				pid = Integer.parseInt(tmp);
				fam = getShortFamily(pid, aux == null ? false : true);
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_PID");
			}
		} else if (cmd.equals("person")) {
			tmp = map.get("pid");
			if (tmp != null) {
				pid = Integer.parseInt(tmp);
				lang = map.get("lang");
				fam = getFullPerson(pid, lang);
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_PID");
			}
		} else if (cmd.equals("delete")) {
			tmp = map.get("pid");
			if (tmp != null) {
				pid = Integer.parseInt(tmp);

				fam = deletePerson(pid);
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_PID");
			}
		} else if (cmd.equals("relatives")) {
			tmp = map.get("pid");
			if (tmp != null) {
				pid = Integer.parseInt(tmp);
				String tag = map.get("tag");
				// throw new SukuException("parents has not been implemented");
				fam = getRelatives(pid, tag);
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_PID");
			}
		} else if (cmd.equals("upload")) {
			fam = uploadFamily(request);
		} else if (cmd.equals("dbversion")) {
			fam = getDbVersion();
		} else if (cmd.equals("dbstats")) {
			fam = getDbStatistics();
		} else if (cmd.equals("repolanguages")) {
			fam = getReportLanguages();
			// } else if (cmd.equals("report")) {
			// fam = createReport(map.get("type"),map.get("pid"));
		} else if (cmd.equals("crtables")) {
			if ("desc".equals(map.get("type"))) {
				fam = createDescTables(map.get("order"),
						map.get("generations"), map.get("spougen"), map
								.get("chilgen"), map.get("adopted"), map
								.get("pid"));
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_REPORT_TYPE");
			}
		} else if (cmd.equals("crlista")) {
			if ("desc".equals(map.get("type"))) {
				fam = createDescendantLista(map.get("pid"));
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_REPORT_TYPE");
			}
		} else if (cmd.equals("logout")) {
			try {
				con.close();
			} catch (SQLException e) {
				con = null;
			}
			con = null;

		} else if (cmd.equals("initdb")) {
			String path = map.get("path");
			if (path == null) {
				path = "/sql/finfamily.sql";
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_SQL_FILE");
				return fam;
			}
			SukuUtility data = SukuUtility.instance();
			data.createSukuDb(this.con, path);

		} else if (cmd.equals("viewlist")) {
			fam = getViewList();
		} else if (cmd.equals("gettypes")) {
			fam = getTypes(map.get("lang"));
			SukuData txts = getTexts(map.get("lang"));
			fam.vvTexts = txts.vvTexts;
		} else if (cmd.equals("getsettings")) {

			fam = getSettings(map.get("index"), map.get("type"), map
					.get("name"));

		} else if (cmd.equals("updatesettings")) {
			fam = updateSettings(request, map.get("index"), map.get("type"),
					map.get("name"));
		} else if (cmd.equals("places")) {

			if (request != null) {
				fam.places = SuomiPlacesResolver.resolveSuomiPlaces(this.con,
						request.places);
			} else {
				fam.resu = Resurses.getString("GETSUKU_BAD_PLACES");
			}
		} else if (cmd.equals("dblista") && "public".equals(this.schema)) {
			fam.generalArray = LocalDatabaseUtility.getListOfDatabases(con);
		} else if (cmd.equals("import2004")) {
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
						|| file.toLowerCase().endsWith("xml")) {
					import2004Data(file, lang);
				}
			}
		} else if (cmd.equals("excel")) {
			String page = map.get("page");
			String path = map.get("path");
			if (path == null) {
				path = this.openFile;
				if (path == null) {
					fam.resu = Resurses.getString("GETSUKU_BAD_COORDINATES");
					return fam;
				}
			}
			importExcelData(path, page);

		} else if (cmd.equals("saverepo")) {
			fam = saveReportSettings(map);

		} else if (cmd.equals("update")) {
			String type = map.get("type");
			if (type == null) {
				fam.resu = Resurses.getString("GETSUKU_BAD_UPDATE_TYPE");
				return fam;
			}
			if (type.equals("person")) {
				fam = updatePerson(request);
				if (fam.resultPid > 0) {
					PersonShortData psp = new PersonShortData(this.con,
							fam.resultPid);
					fam.pers = new PersonShortData[1];
					fam.pers[0] = psp;
				}
			} else {

				fam.resu = "Wrong type [" + type + "] in update command";
				return fam;
			}
		} else if (cmd.equals("group")) {
			String action = map.get("action");
			String key = map.get("key");
			String view = map.get("view");
			String group = map.get("group");
			String pidg = map.get("pid");
			String gen = map.get("gen");
			if (action == null)
				return fam;
			GroupUtil grp = new GroupUtil(con);
			fam = new SukuData();
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
						fam = grp.addDescendantsToGroup(pidb, group, gen, (!key
								.equals("DESC")));
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

		} else if (cmd.equals("view")) {

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
						fam.resu = Resurses
								.getString("GETSUKU_BAD_VIEW_COMMAND");
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
						fam = vv.addViewUnits(viewId, request.pidArray, (empty
								.equalsIgnoreCase("true")));

					} else if (key.toLowerCase().startsWith("desc")) {
						fam = vv.addViewDesc(viewId, Integer.parseInt(pidg),
								gen,
								(key.toUpperCase().equals("DESC_SPOUSES")),
								(empty.equalsIgnoreCase("true")));
					} else if (key.toLowerCase().equals("anc")) {
						fam = vv.addViewAnc(viewId, Integer.parseInt(pidg),
								gen, (empty.equalsIgnoreCase("true")));
					} else {
						fam.resu = Resurses
								.getString("GETSUKU_BAD_VIEW_COMMAND");
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

		}

		else {
			logger.warning("unknown server request, cmd = " + cmd);
		}

		if (fam != null) {
			fam.cmd = map.get("cmd");
		}

		return fam;
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
		StringBuffer sb = new StringBuffer();
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

		// String sql = "select settingvalue " +
		// "from sukusettings where settingtype = '"+type+"' " +
		// "and settingname = '"+name+"' " +
		// "order by settingindex ";
		SukuData resp = new SukuData();
		String sql = "delete from sukusettings where  settingtype = '" + type
				+ "' " + "and settingname = '" + name + "' ";

		String insSql = "insert into sukusettings "
				+ "(settingtype,settingindex,settingname,settingvalue) values (?,?,?,?)";
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			int lukuri = pst.executeUpdate();
			pst.close();

			logger.fine("deleted [" + lukuri + "] settings where type =" + type
					+ " and name = " + name);
			pst = con.prepareStatement(insSql);
			for (int i = 0; i < request.generalArray.length; i++) {
				pst.setString(1, type);
				pst.setInt(2, i);
				pst.setString(3, name);
				pst.setString(4, request.generalArray[i]);
				pst.executeUpdate();
			}
			pst.close();
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

		Vector<Integer> vxv = new Vector<Integer>();

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
			dat = x.createTableStructure(Integer.parseInt(spid), geners, spgen,
					chgen, order, adop);
		} catch (Exception e) {
			throw new SukuException(e);
		}
		return dat;
	}

	private SukuData getSettings(String index, String type, String name) {
		SukuData res = new SukuData();
		try {
			if (name == null && index != null) {

				String sql = "select settingindex,settingvalue "
						+ "from sukusettings where settingtype = 'report' and settingname = 'name' "
						+ "order by settingindex ";
				String[] vv = new String[12];

				Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				while (rs.next()) {
					int idx = rs.getInt(1);
					String nam = rs.getString(2);

					if (idx >= 0 && idx < 12) {
						vv[idx] = nam;
					}

				}
				rs.close();
				res.generalArray = vv;
				String vx[] = new String[2];
				res.vvTypes = new Vector<String[]>();

				sql = "select settingname,settingvalue from SukuSettings "
						+ "where settingtype = 'report' and settingindex = "
						+ index;

				rs = stm.executeQuery(sql);
				while (rs.next()) {
					vx = new String[2];
					vx[0] = rs.getString(1);
					vx[1] = rs.getString(2);
					res.vvTypes.add(vx);
				}
				rs.close();
				stm.close();

			} else {

				String sql = "select settingvalue "
						+ "from sukusettings where settingtype = '" + type
						+ "' " + "and settingname = '" + name + "' "
						+ "order by settingindex ";

				Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				Vector<String> setv = new Vector<String>();
				while (rs.next()) {

					String val = rs.getString(1);
					setv.add(val);

				}
				rs.close();
				stm.close();
				res.generalArray = setv.toArray(new String[0]);
			}
		} catch (SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}
		return res;
	}

	private SukuData getTypes(String langu) {
		SukuData res = new SukuData();
		String vx[];
		res.vvTypes = new Vector<String[]>();
		try {
			String sql = "select tag,name,reportname,tagtype from types where langCode = '"
					+ langu + "' order by tagtype,tag";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				vx = new String[4];
				vx[0] = rs.getString(1);
				vx[1] = rs.getString(2);
				vx[2] = rs.getString(3);
				vx[3] = rs.getString(4);
				res.vvTypes.add(vx);
			}
			rs.close();

			sql = "select distinct tag from unitnotice where tag not in  (select tag from types)";
			rs = stm.executeQuery(sql);

			while (rs.next()) {
				vx = new String[4];
				vx[0] = rs.getString(1);
				vx[1] = vx[0];
				vx[2] = vx[0];
				vx[3] = "Notices";
				res.vvTypes.add(vx);
			}
			stm.close();

		} catch (SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}

		return res;

	}

	private SukuData getTexts(String langu) {
		SukuData res = new SukuData();
		String vx[];
		res.vvTexts = new Vector<String[]>();
		try {
			String sql = "select tag,name,tagtype from texts where langCode = '"
					+ langu + "' ";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				vx = new String[4];
				vx[0] = rs.getString(1);
				vx[1] = rs.getString(2);
				vx[2] = rs.getString(3);
				res.vvTexts.add(vx);
			}
			stm.close();

		} catch (SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}

		return res;

	}

	private SukuData saveReportSettings(HashMap<String, String> map)
			throws SukuException {
		SukuData res = new SukuData();
		int index;

		String sql;
		String tmp = map.get("index");
		if (tmp == null) {
			throw new SukuException("Setting index missing");
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
		sql = "select count(*) from SukuSettings where settingIndex = ? ";
		try {
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, index);
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
			// if (luku < 0) {
			// sql = "create table SukuSettings " +
			// "(SettingType varchar not null,	" +
			// "SettingIndex integer not null," +
			// "SettingName varchar not null," +
			// "SettingValue varchar) with oids";
			//				
			// pstm = con.prepareStatement(sql);
			// pstm.executeUpdate();
			//				
			// } else if (luku > 0) {
			sql = "delete from SukuSettings where SettingIndex = ?";
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, index);
			pstm.executeUpdate();
			pstm.close();

			// }

			sql = "insert into SukuSettings (SettingType,SettingIndex,SettingName,SettingValue) "
					+ "values ('report'," + index + ",?,?)";

			pstm = con.prepareStatement(sql);

			Set<Map.Entry<String, String>> entries = map.entrySet();
			Iterator<Map.Entry<String, String>> ee = entries.iterator();

			while (ee.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) ee
						.next();
				if (!entry.getKey().equals("index")
						&& !entry.getKey().equals("cmd")) {
					pstm.setString(1, entry.getKey().toString());
					pstm.setString(2, entry.getValue().toString());
					pstm.executeUpdate();
				}

			}
			pstm.close();

			res.resu = Resurses.OK;

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

	private SukuData getReportLanguages() {
		SukuData resdat = new SukuData();

		try {
			resdat.generalArray = Upload.getReportLanguages(con);
		} catch (SQLException e) {
			resdat.resu = e.getMessage();
		}

		return resdat;
	}

	private SukuData uploadFamily(SukuData family) {

		SukuData resdat = new SukuData();

		String resu = null;
		try {
			resu = Upload.uploadFamilies(con, family);
		} catch (SQLException e) {
			resu = e.getMessage();
			logger.log(Level.WARNING, "uplaoding family", e);

		}

		resdat.resu = resu;

		return resdat;
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
	 * @return
	 * @throws SukuException
	 */

	private SukuData getFullPerson(int pid, String lang) throws SukuException {
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

				sql = "select a.rid,a.pid,b.pid,a.tag,a.surety,a.modified,a.createdate,"
						+ "rn.surety,rn.tag,rn.relationtype,rn.description,rn.dateprefix,"
						+ "rn.fromdate,rn.todate,rn.place,rn.notetext,"
						+ "rn.sourcetext,rn.privatetext,rn.modified,rn.createdate,rn.rnid "
						+ "from relation a inner join relation b on a.rid=b.rid "
						+ "left join relationnotice rn on a.rid=rn.rid "
						+ "where a.pid <> b.pid and a.pid=? "
						+ "order by a.tag,a.relationrow,rn.noticerow ,a.rid,b.pid";
				pstm = con.prepareStatement(sql);
				pstm.setInt(1, pid);
				rs = pstm.executeQuery();
				int prevRid = -1;
				int rid;
				int bid;
				String tag;
				Relation rel = null;
				RelationNotice rnote = null;
				Vector<Integer> relpids = new Vector<Integer>();
				while (rs.next()) {
					rid = rs.getInt(1);
					bid = rs.getInt(3);
					tag = rs.getString(4);
					relpids.add(bid);

					if (rid != prevRid) {
						if (rel != null && relNotices != null
								&& relNotices.size() > 0) {
							rel.setNotices(relNotices
									.toArray(new RelationNotice[0]));
						}
						relNotices = new Vector<RelationNotice>();
						prevRid = rid;
						rel = new Relation(rid, rs.getInt(2), bid, tag, rs
								.getInt(5), rs.getTimestamp(6), rs
								.getTimestamp(7));
						rels.add(rel);

					}
					String rtag = rs.getString(9);
					if (rtag != null) {
						rnote = new RelationNotice(rs.getInt(21), rid, rs
								.getInt(8), rtag, rs.getString(10), rs
								.getString(11), rs.getString(12), rs
								.getString(13), rs.getString(14), rs
								.getString(15), rs.getString(16), rs
								.getString(17), rs.getString(18), rs
								.getTimestamp(19), rs.getTimestamp(20));

						if (relNotices == null) {
							relNotices = new Vector<RelationNotice>();
						}
						relNotices.add(rnote);
					}

				}
				if (rel != null && relNotices != null && relNotices.size() > 0) {
					rel.setNotices(relNotices.toArray(new RelationNotice[0]));
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
			// ImportExcelData ex = new ImportExcelData(this.con,path);
			// ex.importCoordinates();
			// ExcelImporter ex = new ExcelImporter();
			// return ex.importCoordinates(this.con,path);
		} else if ("types".equals(page)) {
			ExcelImporter ex = new ExcelImporter();
			return ex.importTypes(this.con, path);
		}

		return new SukuData();
	}

	// private SukuData getShortParents(int pid) throws SukuException {
	// Vector<PersonShortData> v = new Vector<PersonShortData>();
	//		
	// try {
	// String sql = "select aid,bid,tag from parent where aid in (?,?) ";
	// // String sql =
	// "select u.pid,u.sex,n.tag,n.MainItem,n.givenname,n.prefix," +
	// // "n.surname,n.postfix,n.fromdate,n.LangCode " +
	// // "from unit as u left join unitnotice as n " +
	// // "on u.pid = n.pid and n.tag in ('BIRT','DEAT','NAME') " +
	// // "where u.pid in (select bid from parent where aid=?) order by u.pid ";
	//
	// // PersonShortData dao;
	// PreparedStatement stm = this.con.prepareStatement(sql);
	// stm.setInt(1, pid);
	// stm.setInt(2, pid);
	// // String tag,sex=null;
	// //
	// // String givenname=null;
	// // String prefix=null,surname=null,postfix=null;
	// // String bdate=null,ddate=null;
	// // boolean isMainItem=false;
	// ResultSet rs = stm.executeQuery();
	// int bid;
	//			
	// String tag;
	// String sex;
	// String famType;
	// int mo = 0;
	// int fa = 0;
	// int aid=0;
	// while (rs.next()){
	// aid = rs.getInt(1);
	// bid = rs.getInt(2);
	// tag = rs.getString(3);
	// if (tag != null){
	// famType= "1"; // generation
	// if (tag.equals("FATH")){
	// sex = "M";
	// fa=bid;
	// } else {
	// sex = "F";
	// mo = bid;
	// }
	//				
	//					
	// PersonShortData dao = new PersonShortData(bid,null,sex,null,null,
	// null,null,null,null,null,null,null,null,null,null,famType,0,0,0,null);
	// v.add(dao);
	// }
	// }
	// rs.close();
	// if (fa != 0 || mo != 0) {
	// if (fa != 0 && mo != 0){
	// stm.setInt(1, fa);
	// stm.setInt(2, mo);
	// } else if (fa != 0){
	// stm.setInt(1, fa);
	// stm.setInt(2, fa);
	// } else {
	// stm.setInt(1, mo);
	// stm.setInt(2, mo);
	// }
	//			
	// rs = stm.executeQuery();
	// famType="2"; // generation
	// while (rs.next()){
	// aid = rs.getInt(1);
	// bid = rs.getInt(2);
	// tag = rs.getString(3);
	// if (aid == fa){
	// if (tag.equals("FATH")){
	// sex = "M";
	//							
	// } else {
	// sex = "F";
	//							
	// }
	// } else {
	// if (tag.equals("FATH")){
	// sex = "M";
	//							
	// } else {
	// sex = "F";
	//							
	// }
	// }
	// PersonShortData dao = new PersonShortData(bid,null,sex,null,null,
	// null,null,null,null,null,null,null,null,null,null,famType,0,0,0,null);
	// v.add(dao);
	// }
	// }
	//			
	// } catch (SQLException e) {
	//			
	// throw new SukuException(e);
	// }
	// PersonShortData[] dt = new PersonShortData[0];
	// SukuData fam = new SukuData();
	// fam.pers =v.toArray(dt);
	//		
	// return fam;
	//
	// }
	@Override
	public void setOpenFile(String f) {
		this.openFile = f;

	}

	private SukuData queryDatabase(String... params) throws SukuException {
		String[] pari;
		String decod;
		Vector<PersonShortData> personList = new Vector<PersonShortData>();
		persMap = new HashMap<Integer, PersonShortData>();
		int idx;

		boolean needsRelativeInfo = false;

		try {
			StringBuffer seleSQL = new StringBuffer();

			seleSQL
					.append("select u.pid,u.sex,u.userrefn,u.groupid,u.tag,n.tag,n.givenname,");
			seleSQL.append("n.patronym,n.prefix,n.surname,n.postfix,");
			seleSQL
					.append("n.fromdate,n.Place,n.Description,n.pnid,n.mediafilename,n.mediatitle ");
			seleSQL
					.append("from unit as u left join unitnotice as n on u.pid = n.pid ");
			seleSQL
					.append("and n.tag in ('BIRT','DEAT','CHR','BURI','NAME','PHOT','OCCU'");
			if (this.toDoTagName != null) {
				seleSQL.append(",'TODO'");
			}
			seleSQL.append(") and n.surety >= 80 ");

			StringBuffer fromSQL = new StringBuffer();
			StringBuffer sbn = new StringBuffer();

			if (params.length > 1) {
				boolean isFirstCriteria = true;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_RELATIVE_INFO)) {
						needsRelativeInfo = true;
					} else {

						if (pari[0].equals(Resurses.CRITERIA_GIVENNAME)) {
							if (sbn.length() > 0)
								sbn.append("and ");
							sbn.append("givenname ilike '" + decod + "%' ");
						} else if (pari[0].equals(Resurses.CRITERIA_SURNAME)) {
							if (sbn.length() > 0)
								sbn.append("and ");
							sbn.append("surname ilike '" + decod + "%' ");
						} else if (pari[0].equals(Resurses.CRITERIA_PATRONYME)) {
							if (sbn.length() > 0)
								sbn.append("and ");
							sbn.append("patronym ilike '" + decod + "%' ");
						}
					}
				}
				if (sbn.length() > 0) {
					fromSQL
							.append("where u.pid in (select pid from unitnotice where ");
					fromSQL.append(sbn.toString());
					fromSQL.append("and tag='NAME') ");
					isFirstCriteria = false;
				}

				String begdate = null;
				String todate = null;
				String place = null;
				String group = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");
					if (pari[0].equals(Resurses.CRITERIA_GROUP)) {
						group = decod;
					}
				}
				if (group != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					fromSQL.append("u.groupid = '" + group + "' ");
				}

				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_BIRT_FROM)) {
						begdate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_BIRT_TO)) {
						todate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_BIRT_PLACE)) {
						place = decod;
					}
				}

				if (begdate != null || todate != null || place != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					fromSQL
							.append("u.pid in (select pid from unitnotice where ");
					if (begdate != null && todate == null) {
						fromSQL.append("fromdate >= '" + begdate + "' ");
					} else if (begdate == null && todate != null) {
						fromSQL.append("fromdate <= '" + todate + "9999' ");
					} else if (begdate != null && todate != null) {
						fromSQL.append("fromdate between '" + begdate
								+ "' and '" + todate + "9999' ");
					}
					if (begdate == null && todate == null && place != null) {
						fromSQL.append("place ilike '" + place + "%' ");
					} else if (place != null) {
						fromSQL.append("and place ilike '" + place + "%' ");
					}
					fromSQL.append("and tag='BIRT') ");
				}

				begdate = null;
				todate = null;
				place = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_DEAT_FROM)) {
						begdate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_DEAT_TO)) {
						todate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_DEAT_PLACE)) {
						place = decod;
					}
				}

				if (begdate != null || todate != null || place != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					fromSQL
							.append("u.pid in (select pid from unitnotice where ");
					if (begdate != null && todate == null) {
						fromSQL.append("fromdate >= '" + begdate + "' ");
					} else if (begdate == null && todate != null) {
						fromSQL.append("fromdate <= '" + todate + "9999' ");
					} else if (begdate != null && todate != null) {
						fromSQL.append("fromdate between '" + begdate
								+ "' and '" + todate + "9999' ");
					}
					if (begdate == null && todate == null && place != null) {
						fromSQL.append("place ilike '" + place + "%' ");
					} else if (place != null) {
						fromSQL.append("and place ilike '" + place + "%' ");
					}
					fromSQL.append("and tag='DEAT') ");
				}
				begdate = null;
				todate = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_CREATED_FROM)) {
						begdate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_CREATED_TO)) {
						todate = decod;
					}
				}
				if (begdate != null || todate != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					if (todate == null) {
						fromSQL
								.append("u.pid in (select pid from unitnotice where createdate >= '"
										+ begdate + "' )");
					} else if (begdate == null) {
						fromSQL
								.append("u.pid in (select pid from unitnotice where createdate >= '"
										+ todate + "' )");
					} else {
						fromSQL
								.append("u.pid in (select pid from unitnotice where createdate between '"
										+ begdate + "' and '" + todate + "' ) ");
					}
				}
				String viewIdTxt = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_VIEW)) {
						viewIdTxt = decod;
					}
				}
				if (viewIdTxt != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;

					fromSQL
							.append("u.pid in (select pid from viewunits where vid = "
									+ viewIdTxt + ") ");

				}
			}

			StringBuffer sql = new StringBuffer();

			sql.append(seleSQL);
			sql.append(fromSQL);

			sql.append("order by u.pid,n.noticerow ");

			logger.fine(sql.toString());

			PersonShortData perso = null;

			// PersonShortData dao;

			Statement stm = this.con.createStatement();
			ResultSet rs = stm.executeQuery(sql.toString());
			// persMap = new HashMap<Integer, PersonShortData>();
			int currentPid = 0;
			int pid;
			// String dbutag; //5
			String dbntag; // 6
			String dbgivenname; // 7
			String dbpatronym; // 8
			String dbprefix; // 9
			String dbsurname; // 10
			String dbpostfix; // 11
			String dbfromdate; // 12
			String dbplace; // 13
			String dbdescription; // 14
			int dbnpid; // 15
			String dbmediafilename; // 16
			String dbmediatitle; // 17

			String rtag;
			int marriages;
			int children;
			int parents;

			while (rs.next()) {
				pid = rs.getInt(1);
				// dbutag = rs.getString(5);
				dbntag = rs.getString(6);
				dbgivenname = rs.getString(7);
				dbpatronym = rs.getString(8);
				dbprefix = rs.getString(9);
				dbsurname = rs.getString(10);
				dbpostfix = rs.getString(11);
				dbfromdate = rs.getString(12);
				dbplace = rs.getString(13);
				dbdescription = rs.getString(14);
				dbnpid = rs.getInt(15);
				dbmediafilename = rs.getString(16);
				dbmediatitle = rs.getString(17);

				if (pid != currentPid && currentPid != 0) {
					personList.add(perso);

					persMap.put(perso.getPid(), perso);
					perso = null;
				}
				currentPid = pid;
				if (perso == null) {
					perso = new PersonShortData();
					perso.setPid(pid);
					persMap.put(perso.getPid(), perso);
					perso.setSex(rs.getString(2));
					perso.setRefn(rs.getString(3));
					perso.setGroup(rs.getString(4));

				}
				if (dbntag != null) {
					if (dbntag.equals("NAME") && perso.getNameTag() == null) {
						perso.setNameTag(dbntag);
						perso.setGivenname(dbgivenname);
						perso.setPatronym(dbpatronym);
						perso.setPrefix(dbprefix);
						perso.setSurname(dbsurname);
						perso.setPostfix(dbpostfix);

					} else if (dbntag.equals("NAME")
							&& perso.getNameTag() != null) {

						StringBuffer sb = new StringBuffer();
						if (perso.getMorenames() != null) {
							sb.append(perso.getMorenames());
							sb.append(";");
						}
						if (dbprefix != null) {
							sb.append(dbprefix);
							sb.append(" ");
						}
						if (dbsurname != null) {
							sb.append(dbsurname);
						}
						perso.setMorenames(sb.toString());
					}

					if (dbntag.equals("OCCU")) {
						perso.setOccupation(dbdescription);
					}
					if (dbntag.equals("PHOT")) {
						perso.setMediaDataNotice(dbnpid);
						perso.setMediaTitle(dbmediatitle);
						perso.setMediaFilename(dbmediafilename);

					}

					if (dbntag.equals("BIRT") || dbntag.equals("CHR")) // &&

					{

						if (perso.getBirtTag() == null || dbntag.equals("BIRT")) {
							perso.setBirtTag(dbntag);
							perso.setBirtDate(dbfromdate);
							perso.setBirtPlace(dbplace);
						}
					}

					if (dbntag.equals("DEAT") || dbntag.equals("BURI")) // &&

					{

						if (perso.getDeatTag() == null || dbntag.equals("DEAT")) {
							perso.setDeatTag(dbntag);
							perso.setDeatDate(dbfromdate);
							perso.setDeatPlace(dbplace);
						}
					}
				}
			}
			if (perso != null) {
				// personDict.Add(perso.Pid, perso);
				personList.add(perso);
				persMap.put(perso.getPid(), perso);
				perso = null;
			}
			if (needsRelativeInfo) {

				StringBuffer relSQL = new StringBuffer();
				relSQL
						.append("select tag,pid,count(*) from relation where pid in "
								+ "(select pid  from unit u ");
				relSQL.append(fromSQL);
				relSQL.append(") group by pid,tag order by pid");
				logger.fine("Relative sql: " + relSQL.toString());
				PreparedStatement pstm = this.con.prepareStatement(relSQL
						.toString());
				ResultSet prs = pstm.executeQuery();
				while (prs.next()) {
					rtag = prs.getString(1);
					int rpid = prs.getInt(2);
					PersonShortData rp = persMap.get(rpid);
					if (rp != null) {

						if (rtag.equals("HUSB") || rtag.equals("WIFE")) {
							marriages = prs.getInt(3);
							marriages += rp.getMarrCount();
							rp.setMarrCount(marriages);
						} else if (rtag.equals("CHIL")) {
							children = prs.getInt(3);
							rp.setChildCount(children);
						} else if (rtag.equals("MOTH") || rtag.equals("FATH")) {
							parents = prs.getInt(3);
							parents += rp.getPareCount();
							rp.setPareCount(parents);
						}
					}
				}
				prs.close();

			}

		} catch (Exception e) {

			throw new SukuException(e);
		}

		PersonShortData[] dt = new PersonShortData[0];
		SukuData qlist = new SukuData();
		qlist.pers = personList.toArray(dt);
		return qlist;
	}

	HashMap<Integer, PersonShortData> persMap = null;

	@Override
	public SukuData getSukuData(String... params) throws SukuException {
		return getSukuData(null, params);
	}

}
