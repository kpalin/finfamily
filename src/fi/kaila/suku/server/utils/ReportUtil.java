package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.report.PersonInTables;
import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.RelationShortData;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Server side class for report creator
 * 
 * @author Kalle
 * 
 */
public class ReportUtil {

	private static Logger logger = Logger.getLogger(ReportUtil.class.getName());

	private Connection con = null;

	ReportWorkerDialog runner = null;

	/**
	 * Constructor
	 * 
	 * @param con
	 *            connection instance to the PostgreSQL database
	 */
	public ReportUtil(Connection con) {
		this.con = con;
		this.runner = ReportWorkerDialog.getRunner();
	}

	private HashMap<Integer, ReportUnit> unitMap = new HashMap<Integer, ReportUnit>();

	//
	// collect here females for whom table must not be builot
	//
	private HashMap<Integer, ReportTableMember> females = null;

	private HashMap<Integer, ReportTableMember> pidmap = new HashMap<Integer, ReportTableMember>();

	HashMap<Integer, PersonInTables> personReferences = null;

	Vector<ReportUnit> regs1 = null;
	Vector<ReportUnit> regs2 = null;

	/**
	 * 
	 * Create descendant table structure
	 * 
	 * @param pid
	 * @param generations
	 * @param spouGen
	 * @param chilGen
	 * @param order
	 * @param adopted
	 * @return result of process
	 * @throws SQLException
	 */
	public SukuData createDescendantStructure(int pid, int generations,
			int spouGen, int chilGen, String order, boolean adopted)
			throws SQLException {
		SukuData fam = new SukuData();
		unitMap = new HashMap<Integer, ReportUnit>();

		females = null;
		if (order.equals(ReportWorkerDialog.SET_ORDER_FIRSTMALE)) {
			females = new HashMap<Integer, ReportTableMember>();
		}

		ReportTableMember chi = new ReportTableMember();
		chi.setPid(pid);

		@SuppressWarnings("unused")
		int lasttab = createDescendantTables(0, 0, chi, 1, generations, order,
				adopted, 0);

		if (females != null) {
			int round = females.size();
			if (round > 0) {
				unitMap = new HashMap<Integer, ReportUnit>();
				createDescendantTables(0, 0, chi, 1, generations,
						ReportWorkerDialog.SET_ORDER_NEWMALE, adopted, round);
			}
		}

		logger.fine("Descendant repo");

		this.runner.setRunnerValue(Resurses.getString("REPORT_DESC_COUNTING"));
		tableNo = 1;
		int gen = 0;
		ReportUnit unit = unitMap.get(pid);
		unit.setTableNo(tableNo);
		unit.setGen(gen);
		tables.add(unit);

		if (order.equals(ReportWorkerDialog.SET_ORDER_REG)) {
			regs1 = new Vector<ReportUnit>();

			regs1.add(unit);
			calculateRegistryTableNumbers(regs1, unitMap);
		} else {
			calculateDescendantTableNumbers(unit, unitMap);
		}

		// logUnits("laskettu taulut");

		this.runner.setRunnerValue(Resurses.getString("REPORT_DESC_ALSO_IN"));

		personReferences = Utils.getDescendantToistot(tables);
		logUnits("laskettu toistot");

		// System.out.println("SPOU/CHIL = " + spouGen + "/" + chilGen);
		if (spouGen > 0) {
			addSpouseAncestors(spouGen);

		}
		if (chilGen > 0) {
			addChildAncestors(chilGen);
		}
		logUnits("laskettu muutkin");
		fam.tables = tables;

		fam.reportUnits = unitMap;
		return fam;

	}

	private void addChildAncestors(int gen) throws SQLException {

		for (int i = 0; i < tables.size(); i++) {
			ReportUnit unit = tables.get(i);
			for (int j = 0; j < unit.getChild().size(); j++) {
				ReportTableMember member = unit.getChild().get(j);
				// HashMap<Integer,PersonInTables> personReferences
				// long strado=2;
				// if (unit.getMember(0).getSex().equals("M")){
				// strado=3;
				// }

				addAncestorsToMember(member, 1, gen);
				// spouse not a relative

			}
		}

	}

	private void addSpouseAncestors(int gen) throws SQLException {
		String fromTable;
		for (int i = 0; i < tables.size(); i++) {
			ReportUnit unit = tables.get(i);
			int startIdx = 1;
			if (unit.getGen() == 0) {
				startIdx = 0;
			}
			for (int j = startIdx; j < unit.getParent().size(); j++) {
				ReportTableMember member = unit.getParent().get(j);
				// HashMap<Integer,PersonInTables> personReferences
				PersonInTables ref = personReferences.get(member.getPid());
				fromTable = "";
				if (ref != null) {
					fromTable = ref.getReferences(unit.getTableNo(), true,
							true, false);
				}
				if (fromTable.isEmpty()) {
					addAncestorsToMember(member, 1, gen);
					// spouse not a relative
				}
			}
		}
		for (int i = 0; i < tables.size(); i++) {
			ReportUnit unit = tables.get(i);
			for (int j = 0; j < unit.getChild().size(); j++) {
				ReportTableMember member = unit.getChild().get(j);

				ReportTableMember spouses[] = member.getSpouses();
				if (spouses != null) {
					for (int k = 0; k < spouses.length; k++) {
						PersonInTables ref = personReferences.get(spouses[k]
								.getPid());
						fromTable = "";
						if (ref != null) {
							fromTable = ref.getReferences(unit.getTableNo(),
									true, true, false);
						}
						if (fromTable.isEmpty()) {
							addAncestorsToMember(spouses[k], 1, gen);
							// spouse not a relative
						}
					}
				}
			}
		}
	}

	private void addAncestorsToMember(ReportTableMember member, long strado,
			int gen) throws SQLException {

		int pid = member.getPid();
		String sex = member.getSex();

		addMemberParents(member, pid, strado, sex, 0, gen);

		member.sortSubs();

	}

	private void addMemberParents(ReportTableMember member, int pid,
			long strado, String sex, int gen, int maxGen) throws SQLException {

		String sql = "select p.aid,p.bid,p.tag "
				+ "from parent as p left join relationnotice as r on p.rid=r.rid "
				+ "where aid=? and r.tag is null";

		int fatherPid = 0;
		int motherPid = 0;
		String tag;
		PreparedStatement stm = con.prepareStatement(sql);
		stm.setInt(1, pid);

		ResultSet rs = stm.executeQuery();

		while (rs.next()) {
			tag = rs.getString(3);
			if (tag.equals("FATH")) {
				fatherPid = rs.getInt(2);
			} else {
				motherPid = rs.getInt(2);
			}
		}
		rs.close();
		stm.close();
		PersonInTables ref;
		if (gen < 2) {

			ref = personReferences.get(member.getPid());
			String fromTable = "";
			if (ref != null) {
				fromTable = ref.getReferences(0, true, true, false);
			}
			if (fromTable.isEmpty()) {
				//FIXME:???
			}
		}

		if (fatherPid > 0) {
			ref = null;
			if (gen < 2) {
				ref = personReferences.get(fatherPid);
			}

			if (ref == null) {
				member.addSub(fatherPid, "M", strado * 2);
			} else {
				fatherPid = 0;
			}
		}
		if (motherPid > 0) {
			ref = null;
			if (gen < 2) {
				ref = personReferences.get(motherPid);
			}

			if (ref == null) {
				member.addSub(motherPid, "F", strado * 2 + 1);
			} else {
				motherPid = 0;
			}

		}

		if (fatherPid > 0 && gen < maxGen - 1) {
			addMemberParents(member, fatherPid, strado * 2, "M", gen + 1,
					maxGen);
		}
		if (motherPid > 0 && gen < maxGen - 1) {
			addMemberParents(member, motherPid, strado * 2 + 1, "F", gen + 1,
					maxGen);
		}

	}

	/**
	 * 
	 * @param parentTableNo
	 *            table # of the parent
	 * @param nexttab
	 *            table # of next table if none is created
	 * @param chi
	 *            child for whom table is top be created
	 * @param gen
	 *            current generation
	 * @param generations
	 *            max generations
	 * @param order
	 *            # from report settings 0 = normal order, 1 = men order, 2 =
	 *            women order 3 = men first order, 4 = register order
	 * @param adopted
	 *            true if also adopted children are to be included
	 * @return
	 * @throws SQLException
	 */
	private int createDescendantTables(long parentTableNo, int nexttab,
			ReportTableMember chi, int gen, int generations, String order,
			boolean adopted, int round) throws SQLException {

		ReportUnit pidTable = unitMap.get(chi.getPid());

		if (pidTable != null) {
			return nexttab;
		}

		if (gen > generations) {
			return nexttab;
		}
		// create a table for the child
		ReportUnit unit = createOneTable(nexttab, chi, gen, adopted, true,
				false);

		if (nexttab == 0 && unit.getChild().size() == 0) {
			unitMap.put(unit.getPid(), unit);
			return 0;
		}

		//
		// if child has no children then copy spouses to child at parent
		//
		if (unit.getChild().size() == 0) {
			if (unit.getParent().size() > 1) {
				int idx = 0;
				ReportTableMember[] spouses = new ReportTableMember[unit
						.getParent().size() - 1];
				for (int i = 0; i < unit.getParent().size(); i++) {
					if (unit.getPid() != unit.getParent().get(i).getPid()) {
						spouses[idx++] = unit.getParent().get(i);
					}
				}
				chi.setSpouses(spouses);
			}
			return nexttab; // and continue at nexttab

		}

		//
		// check possible table of spouses
		//
		for (int i = 0; i < unit.getParent().size(); i++) {
			ReportUnit pareUnits = unitMap
					.get(unit.getParent().get(i).getPid());
			//
			// got table of spouse (if such exists)
			//
			if (pareUnits != null) {

				if (unit.getChild().size() != pareUnits.getChild().size()) {
					break;
				}
				int j = 0;
				int jlen = unit.getChild().size();
				for (j = 0; j < jlen; j++) {
					if (unit.getChild().get(j).getPid() != pareUnits.getChild()
							.get(j).getPid()) {
						break;
					}
				}
				if (j < jlen)
					break;
				if (order.equals(ReportWorkerDialog.SET_ORDER_FIRSTMALE)
						&& chi.getSex().equals("M")) {
					// females.put(unit.getPid(),chi);
					females.put(pareUnits.getPid(), pareUnits.getMember(0));
				}
				//
				// if spouse has table then if spouse and subject childlist
				// match
				// then spouse does not get own table
				//
				return nexttab;
			}

		}

		if (females != null
				&& ReportWorkerDialog.SET_ORDER_NEWMALE.equals(order)
				&& females.get(chi.getPid()) != null) {
			return nexttab;
		}

		int tableNo = nexttab + 1;
		if (round > 0) {
			this.runner.setRunnerValue(Resurses
					.getString("REPORT_TABSTRUCT_INITMALE")
					+ " [" + round + "]: " + tableNo);
		} else {
			this.runner.setRunnerValue(Resurses
					.getString("REPORT_TABSTRUCT_INIT")
					+ " " + tableNo);
		}
		unitMap.put(unit.getPid(), unit);

		ReportTableMember mymem = new ReportTableMember();
		mymem.setPid(unit.getPid());
		pidmap.put(unit.getPid(), mymem);

		for (int i = 0; i < unit.getMemberCount(); i++) {
			mymem = unit.getMember(i);
			pidmap.put(mymem.getPid(), mymem);
		}

		int nxttab = 0;
		for (int rowno = 0; rowno < unit.getChild().size(); rowno++) {
			ReportTableMember chix = unit.getChild().get(rowno);

			if (order.equals(ReportWorkerDialog.SET_ORDER_TAB)
					|| order.equals(ReportWorkerDialog.SET_ORDER_FIRSTMALE)
					|| order.equals(ReportWorkerDialog.SET_ORDER_NEWMALE)
					|| order.equals(ReportWorkerDialog.SET_ORDER_REG)
					|| (order.equals(ReportWorkerDialog.SET_ORDER_MALE)
							&& "M".equals(chix.getSex()) || (order
							.equals(ReportWorkerDialog.SET_ORDER_FEMALE) && "F"
							.equals(chix.getSex())))) {

				nxttab = createDescendantTables(tableNo, tableNo, chix,
						gen + 1, generations, order, adopted, round);
				if (nxttab > tableNo) {
					tableNo = nxttab;
				}
			}
		}

		return tableNo;

	}

	private int createAncestorTables(int nexttab, ReportTableMember chi,
			int gen, int generations, boolean family, String order)
			throws SQLException {

		ReportUnit pidTable = unitMap.get(chi.getPid());

		if (pidTable != null) {
			return nexttab;
		}

		if (gen > generations) {
			return nexttab;
		}
		// create a table for the child
		ReportUnit unit = createOneTable(nexttab, chi, gen, true, family, true);

		unitMap.put(unit.getPid(), unit);

		if (unit.getFatherPid() > 0) {
			ReportTableMember chif = new ReportTableMember();
			chif.setPid(unit.getFatherPid());
			createAncestorTables(nexttab * 2, chif, gen + 1, generations,
					family, order);
		}

		if (unit.getMotherPid() > 0) {
			ReportTableMember chim = new ReportTableMember();
			chim.setPid(unit.getMotherPid());
			createAncestorTables(nexttab * 2 + 1, chim, gen + 1, generations,
					family, order);
		}

		return 0;

	}

	private ReportUnit createOneTable(int tabno, ReportTableMember chi,
			int gen, boolean adopted, boolean includeFamily,
			boolean includeParents) throws SQLException {

		String sql;

		PreparedStatement stm;
		int pid = chi.getPid();

		ResultSet rs;

		String sex = null;
		sql = "select sex from unit where pid = ?";

		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);

		rs = stm.executeQuery();
		while (rs.next()) {
			sex = rs.getString("sex");
		}
		rs.close();
		stm.close();

		ReportUnit unit = new ReportUnit();
		unit.setPid(pid);

		ReportTableMember member = new ReportTableMember();

		member.setPid(pid);
		member.setRowNo(0);
		member.setSex(sex);
		unit.addParent(member);

		if (includeParents) {
			sql = "select bid,p.tag,r.tag from parent as p left join relationnotice as r on p.rid=r.rid where aid=?";
			stm = con.prepareStatement(sql);
			stm.setInt(1, pid);

			rs = stm.executeQuery();
			while (rs.next()) {
				int parep = rs.getInt(1);
				String pare = rs.getString(2);
				String adop = rs.getString(3);
				if (adop == null) {
					if ("FATH".equals(pare)) {
						unit.setFatherPid(parep);
					} else {
						unit.setMotherPid(parep);
					}
				}
			}
			rs.close();
			stm.close();
		}

		if (!includeFamily) {
			return unit;
		}
		sql = "select bid,s.tag,u.sex "
				+ "from spouse as s inner join unit as u on s.bid=u.pid "
				+ "where s.aid=? order by relationrow";

		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);
		int spousenum = 0;
		rs = stm.executeQuery();
		while (rs.next()) {
			int bid = rs.getInt("bid");
			String stag = rs.getString("tag");
			String ssex = rs.getString("sex");
			spousenum++;

			member = new ReportTableMember();

			member.setPid(bid);
			member.setRowNo(spousenum);
			member.setSex(ssex);
			member.setTag(stag);
			unit.addParent(member);

		}
		rs.close();
		stm.close();

		int childno = 0;
		PreparedStatement adopstm = con
				.prepareStatement("select count(*) from relationnotice where tag='ADOP' and rid = ?");
		String adoptext = "";
		if (!adopted) {
			adoptext = "and r.tag is null";
		}

		sql = "select c.bid,u.sex,c.rid "
				+ "from (child as c inner join unit as u on c.bid=u.pid ) "
				+ "where aid=? order by relationrow";

		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);

		rs = stm.executeQuery();

		while (rs.next()) {
			int bid = rs.getInt("bid");
			String csex = rs.getString("sex");

			int rid = rs.getInt(3);
			boolean isAdopted = isChildRelationAdopted(adopstm, rid);
			member = new ReportTableMember();
			member.setPid(bid);
			member.setSex(csex);
			member.setRowNo(spousenum + childno);
			member.setTag("CHIL");
			if (isAdopted) {
				member.setRelTag("ADOP");
			}
			unit.addChild(member);
			childno++;

		}

		rs.close();
		stm.close();

		return unit;

	}

	private boolean isChildRelationAdopted(PreparedStatement pst, int rid)
			throws SQLException {

		pst.setInt(1, rid);

		int counter = 0;
		ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			counter = rs.getInt(1);

		}
		rs.close();
		return counter > 0;

	}

	private int tableNo;
	Vector<ReportUnit> tables = new Vector<ReportUnit>();

	private void calculateDescendantTableNumbers(ReportUnit tab,
			HashMap<Integer, ReportUnit> unitMap) {

		for (int i = 0; i < tab.getChild().size(); i++) {
			ReportTableMember asChi = tab.getChild().get(i);

			if (asChi.getMyTable() == 0) {
				ReportUnit asOwner = unitMap.get(asChi.getPid());
				if (asOwner != null) {
					if (asOwner.getTableNo() == 0) {

						tableNo++;
						asOwner.setTableNo(tableNo);
						asOwner.setGen(tab.getGen() + 1);
						asChi.setMyTable(tableNo);
						tables.add(asOwner);
						calculateDescendantTableNumbers(asOwner, unitMap);
					}
					asOwner.setParentTable(asChi.getMyTable());
				}
			} else {
				asChi.addAsChild(tab.getTableNo());
			}
		}

	}

	private void calculateRegistryTableNumbers(Vector<ReportUnit> regs,
			HashMap<Integer, ReportUnit> unitMap) {
		regs2 = new Vector<ReportUnit>();

		for (int j = 0; j < regs.size(); j++) {
			ReportUnit tab = regs.get(j);
			if (j == 0) {
				System.out.println("generation for " + tab.getPid() + "("
						+ regs.size() + ") = " + tab.getGen());
			}

			for (int i = 0; i < tab.getChild().size(); i++) {
				ReportTableMember asChi = tab.getChild().get(i);

				if (asChi.getMyTable() == 0) {
					ReportUnit asOwner = unitMap.get(asChi.getPid());
					if (asOwner != null) {
						if (asOwner.getTableNo() == 0) {

							tableNo++;
							asOwner.setTableNo(tableNo);
							asOwner.setGen(tab.getGen() + 1);
							asChi.setMyTable(tableNo);
							tables.add(asOwner);
							regs2.add(asOwner);
						}
						asOwner.setParentTable(asChi.getMyTable());
					}
				} else {
					asChi.addAsChild(tab.getTableNo());
				}
			}

		}
		if (regs2.size() > 0) {
			regs1 = regs2;
			calculateRegistryTableNumbers(regs1, unitMap);
		}
	}

	private void logUnits(String text) {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer(text);
			for (int i = 0; i < tables.size(); i++) {
				ReportUnit tab = tables.get(i);
				logger.finer(tab.toString());
			}
		}
	}

	/**
	 * 
	 * Create data for the descendant list (Excel report)
	 * 
	 * @param pid
	 * @return result in Sukudata pers = list of persons, generalArray 0 tag,
	 *         pidarray = generation of corresponding persons
	 * @throws SukuException
	 * @throws SQLException
	 */
	public SukuData createDescendantLista(int pid) throws SukuException,
			SQLException {
		descListaPersons = new Vector<PersonShortData>();
		descListaText = new Vector<String>();
		descListaGen = new Vector<Integer>();
		multicheck = new HashMap<Integer, PersonShortData>();
		// descListaRelations= new Vector<RelationShortData> ();
		PersonShortData p = new PersonShortData(this.con, pid, true);
		multicheck.put(p.getPid(), p);
		descListaPersons.add(p);
		descListaGen.add(0);
		descListaText.add("SUBJ");
		descListaCounter = 0;
		insertIntoDescendantLista(pid, 0);
		SukuData ddd = new SukuData();

		ddd.pers = descListaPersons.toArray(new PersonShortData[0]);
		ddd.generalArray = descListaText.toArray(new String[0]);
		ddd.pidArray = new int[descListaGen.size()];
		for (int i = 0; i < ddd.pidArray.length; i++) {
			ddd.pidArray[i] = descListaGen.get(i);
		}
		return ddd;

	}

	Vector<PersonShortData> descListaPersons = null;
	Vector<String> descListaText = null;
	Vector<Integer> descListaGen = null;
	int descListaCounter = 0;
	HashMap<Integer, PersonShortData> multicheck = null;

	private void insertIntoDescendantLista(int pid, int gen)
			throws SQLException, SukuException {
		Vector<RelationShortData> rr = new Vector<RelationShortData>();
		descListaCounter++;
		this.runner.setRunnerValue(Resurses.getString("REPORT.LISTA.DESCLISTA")
				+ " [" + descListaCounter + "/" + gen + "] ");
		String sql;
		PreparedStatement stm;
		ResultSet rs;

		sql = "select bid,relationrow,tag,r.surety from spouse where aid=? order by relationrow";
		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);
		rs = stm.executeQuery();
		while (rs.next()) {
			RelationShortData rel = new RelationShortData(pid, rs.getInt(1), rs
					.getInt(2), rs.getString(3), rs.getInt(4));
			rr.add(rel);
		}
		rs.close();
		stm.close();

		sql = "select bid,relationrow,tag,r.surety from child where aid=? order by relationrow";
		stm = con.prepareStatement(sql);
		stm.setInt(1, pid);
		rs = stm.executeQuery();
		while (rs.next()) {
			RelationShortData rel = new RelationShortData(pid, rs.getInt(1), rs
					.getInt(2), rs.getString(3), rs.getInt(4));
			rr.add(rel);
		}
		stm.close();

		for (int i = 0; i < rr.size(); i++) {
			RelationShortData rel = rr.get(i);

			PersonShortData p = new PersonShortData(this.con, rel
					.getRelationPid(), true);
			descListaPersons.add(p);
			int mygen = gen;
			if (rel.getTag().equals("CHIL")) {
				mygen++;
			}
			descListaGen.add(mygen);
			descListaText.add(rel.getTag());
			if (rel.getTag().equals("CHIL")) {
				if (multicheck.put(p.getPid(), p) == null) {
					insertIntoDescendantLista(p.getPid(), gen + 1);
				}
			}
		}

	}

	/**
	 * @param pid
	 * @param generations
	 * @param family
	 * @param order
	 * @return ancestor structure
	 * @throws SQLException
	 */
	public SukuData createAncestorStructure(int pid, int generations,
			boolean family, String order) throws SQLException {
		SukuData fam = new SukuData();
		unitMap = new HashMap<Integer, ReportUnit>();

		ReportTableMember chi = new ReportTableMember();
		chi.setPid(pid);

		createAncestorTables(1, chi, 1, generations, family, order);
		Vector<ReportUnit> curvec = new Vector<ReportUnit>();
		Vector<ReportUnit> nxtvec = null;
		if (order.equals(ReportWorkerDialog.SET_ANC_ESPOLIN)) {

			ReportUnit cu = unitMap.get(pid);
			cu.setTableNo(0);
			cu.setGen(0);
			curvec.add(cu);
			long epsotab = 0;
			while (curvec.size() > 0) {
				nxtvec = new Vector<ReportUnit>();
				for (int i = 0; i < curvec.size(); i++) {
					ReportUnit cux = curvec.get(i);
					if (cux.getTableNo() == 0) {
						if (cux.getFatherPid() == 0 && cux.getMotherPid() == 0) {
							continue;
						}
						epsotab++;
						cux.setTableNo(epsotab);
						tables.add(cux);

						ReportUnit cuxx = cux;
						ReportUnit moxx;
						while (cuxx.getFatherPid() > 0) {
							// int prepid=cuxx.getPid();
							int pregen = cuxx.getGen();
							int mopid = cuxx.getMotherPid();
							int fapid = cuxx.getFatherPid();
							cuxx = unitMap.get(fapid);
							if (cuxx.getTableNo() == 0) {
								cuxx.setTableNo(cux.getTableNo());
								cuxx.setGen(pregen + 1);
								cuxx.setTableNo(epsotab);
								tables.add(cuxx);
							}
							if (mopid > 0) {
								moxx = unitMap.get(mopid);
								moxx.setGen(pregen + 1);
								nxtvec.add(moxx);
							}
						}
					}
				}
				curvec = nxtvec;
			}

		} else {

			ReportUnit cu = unitMap.get(pid);
			cu.setTableNo(1);
			cu.setGen(0);
			curvec.add(cu);

			while (curvec.size() > 0) {
				nxtvec = new Vector<ReportUnit>();
				for (int i = 0; i < curvec.size(); i++) {

					ReportUnit cux = curvec.get(i);
					if (cux != null) {
						tables.add(cux);
						int fid = cux.getFatherPid();
						if (fid > 0) {
							ReportUnit cuf = unitMap.get(fid);
							if (cuf != null && cuf.getTableNo() == 0) {
								cuf.setTableNo(cux.getTableNo() * 2);
								cuf.setGen(cux.getGen() + 1);
								// tables.add(cuf);
								nxtvec.add(cuf);
							}

						}

						int mid = cux.getMotherPid();
						if (mid > 0) {
							ReportUnit cum = unitMap.get(mid);
							if (cum != null && cum.getTableNo() == 0) {
								cum.setTableNo(cux.getTableNo() * 2 + 1);
								cum.setGen(cux.getGen() + 1);
								// tables.add(cum);
								nxtvec.add(cum);
							}

						}
					}
				}
				curvec = nxtvec;

			}

		}

		fam.reportUnits = unitMap;
		fam.tables = tables;
		return fam;
	}

}
