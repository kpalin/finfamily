package fi.kaila.suku.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fi.kaila.suku.exports.ExportGedcomDialog;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

public class ExportGedcomUtil {

	private Connection con;

	private ExportGedcomDialog runner = null;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private String path = null;
	private String langCode = null;
	private int viewId = 0;
	private int surety = 100;
	private boolean includeImages = true;

	private LinkedHashMap<Integer, MinimumIndividual> units = null;
	private LinkedHashMap<String, MinimumFamily> families = null;

	private enum GedSet {
		Set_None, Set_Ascii, Set_Ansel, Set_Utf8, Set_Utf16le, Set_Utf16be
	}

	private GedSet thisSet = GedSet.Set_None;

	/**
	 * Constructor with connection
	 * 
	 * @param con
	 */
	public ExportGedcomUtil(Connection con) {
		this.con = con;
		this.runner = ExportGedcomDialog.getRunner();
	}

	public SukuData exportGedcom(String path, String langCode, int viewId,
			int surety, boolean includeImages) {
		this.path = path;
		this.langCode = langCode;
		this.viewId = viewId;
		this.surety = surety;
		this.includeImages = includeImages;

		SukuData result = new SukuData();
		if (path == null || path.lastIndexOf(".") < 1) {
			result.resu = "output filename missing";
			return result;
		}
		try {
			collectIndividuals();

			collectFamilies();

			String simple = path.substring(0, path.lastIndexOf("."));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ZipOutputStream zip = new ZipOutputStream(bos);
			String fileName = simple + "/" + simple + ".ged";

			ZipEntry entry = new ZipEntry(fileName);

			zip.putNextEntry(entry);

			// insert first the gedcom file here
			writeHead(zip);
			int allCount = units.size();
			int curreCount = 0;
			Set<Map.Entry<Integer, MinimumIndividual>> unitss = units
					.entrySet();
			Iterator<Map.Entry<Integer, MinimumIndividual>> eex = unitss
					.iterator();
			while (eex.hasNext()) {
				Map.Entry<Integer, MinimumIndividual> unitx = (Map.Entry<Integer, MinimumIndividual>) eex
						.next();
				MinimumIndividual pit = unitx.getValue();
				curreCount++;

				PersonUtil u = new PersonUtil(con);
				SukuData fam = u.getFullPerson(pit.pid, langCode);

				writeIndi(zip, fam.persLong);

			}

			// private LinkedHashMap<ParentPair, MinimumFamily> families = null;
			Set<Map.Entry<String, MinimumFamily>> fss = families.entrySet();

			Iterator<Map.Entry<String, MinimumFamily>> ffx = fss.iterator();
			while (ffx.hasNext()) {
				Map.Entry<String, MinimumFamily> fx = (Map.Entry<String, MinimumFamily>) ffx
						.next();
				MinimumFamily fix = fx.getValue();

				writeFam(zip, fix);

			}

			zip.write("0 TRLR\r\n".getBytes());
			zip.closeEntry();
			zip.close();

			result.buffer = bos.toByteArray();

		} catch (IOException e) {
			result.resu = e.getMessage();
			e.printStackTrace();
		} catch (SQLException e) {
			result.resu = e.getMessage();
			e.printStackTrace();
		} catch (SukuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private void writeIndi(ZipOutputStream zip, PersonLongData persLong)
			throws IOException {
		MinimumIndividual indi = units.get(persLong.getPid());
		StringBuilder sb = new StringBuilder();
		sb.append("0 @I" + indi.gid + "@ INDI\r\n");
		sb.append("1 SEX " + indi.sex + "\r\n");
		UnitNotice[] notices = persLong.getNotices();
		for (int i = 0; i < notices.length; i++) {
			if (notices[i].getTag().equals("NAME")) {
				StringBuilder nm = new StringBuilder();
				if (notices[i].getGivenname() != null) {
					nm.append(notices[i].getGivenname());
				}
				if (notices[i].getPatronym() != null) {
					if (nm.length() > 0) {
						nm.append(" ");
					}
					nm.append(notices[i].getPatronym());
				}
				nm.append("/");
				if (notices[i].getPrefix() != null) {
					nm.append(notices[i].getPrefix());
					nm.append(" ");
				}
				if (notices[i].getSurname() != null) {
					nm.append(notices[i].getSurname());
				}
				if (notices[i].getPostfix() != null) {
					nm.append("/");
					nm.append(notices[i].getPostfix());
				}

				sb.append("1 NAME " + nm.toString() + "\r\n");
			}
		}
		for (int i = 0; i < indi.fams.size(); i++) {
			sb.append("1 FAMS F" + indi.fams.get(i) + "\r\n");
		}
		for (int i = 0; i < indi.famc.size(); i++) {
			sb.append("1 FAMC F" + indi.famc.get(i) + "\r\n");
		}
		zip.write(sb.toString().getBytes());
	}

	private void writeIndi(ZipOutputStream zip, int pid) throws IOException {
		MinimumIndividual indi = units.get(pid);
		StringBuilder sb = new StringBuilder();
		sb.append("0 @I" + indi.gid + "@ INDI\r\n");
		sb.append("1 SEX " + indi.sex + "\r\n");
		sb.append("1 NAME " + indi.pid + "\r\n");

		for (int i = 0; i < indi.fams.size(); i++) {
			sb.append("1 FAMS F" + indi.fams.get(i) + "\r\n");
		}
		for (int i = 0; i < indi.famc.size(); i++) {
			sb.append("1 FAMC F" + indi.famc.get(i) + "\r\n");
		}
		zip.write(sb.toString().getBytes());

	}

	private void writeFam(ZipOutputStream zip, MinimumFamily fam)
			throws IOException {

		StringBuilder sb = new StringBuilder();
		sb.append("0 @F" + fam.id + "@ FAM\r\n");
		if (fam.dad > 0) {
			sb.append("1 HUSB I" + fam.getDada() + "\r\n");
		}
		if (fam.mom > 0) {
			sb.append("1 WIFE I" + fam.getMama() + "\r\n");
		}
		for (int i = 0; i < fam.chils.size(); i++) {
			sb.append("1 CHIL I" + fam.chils.get(i) + "\r\n");
		}

		zip.write(sb.toString().getBytes());

	}

	private void writeHead(ZipOutputStream zip) throws IOException {
		zip.write("0 HEAD\r\n".getBytes());
		zip.write("1 NOTE FinFamily Gedcom Export is under construction\r\n"
				.getBytes());

	}

	private void collectIndividuals() throws SQLException {
		units = new LinkedHashMap<Integer, MinimumIndividual>();
		String sql = null;
		PreparedStatement pst;

		if (viewId == 0) {
			sql = "select pid,sex from unit order by pid";
			pst = con.prepareStatement(sql);
		} else {
			sql = "select pid,sex from unit where pid in (select pid from viewunits where vid = ?) order by pid";
			pst = con.prepareStatement(sql);
			pst.setInt(1, viewId);
		}

		ResultSet rs = pst.executeQuery();
		int gid = 0;
		while (rs.next()) {

			gid++;
			int pid = rs.getInt(1);
			String sex = rs.getString(2);
			units.put(pid, new MinimumIndividual(pid, sex, gid));

		}
		rs.close();
		pst.close();

	}

	private void collectFamilies() throws SQLException {
		families = new LinkedHashMap<String, MinimumFamily>();

		StringBuilder sql = new StringBuilder();
		;
		PreparedStatement pst;

		sql
				.append("select a.pid,a.tag,a.relationrow,b.pid,b.tag,b.relationrow "
						+ "from relation as a inner join relation as b on a.rid=b.rid ");
		if (viewId > 0) {
			sql.append("and a.pid in (select pid from viewunits where vid="
					+ viewId + ") "
					+ "and b.pid in (select pid from viewunits where vid="
					+ viewId + ") ");
		}
		if (surety != 100) {
			sql.append("and a.surety >= " + surety + " ");
		}
		sql.append("and a.tag='WIFE' and b.tag='HUSB' "
				+ "order by a.pid,a.relationrow");

		pst = con.prepareStatement(sql.toString());

		ResultSet rs = pst.executeQuery();

		while (rs.next()) {
			int dada = rs.getInt(1);
			int mama = rs.getInt(4);

			ParentPair pp = new ParentPair(dada, mama);

			MinimumFamily mf = new MinimumFamily(dada, mama);
			families.put(pp.toString(), mf);

			MinimumIndividual mi = units.get(dada);
			mi.addFams(mf.id);
			mi = units.get(mama);
			mi.addFams(mf.id);

		}
		rs.close();

		sql = new StringBuilder();
		// select a.pid,b.pid,b.tag
		// from relation as a inner join relation as b on a.rid=b.rid and
		// a.tag='CHIL' and b.tag != 'CHIL'

		// select a.pid,a.tag,a.relationrow,b.pid,b.tag,b.relationrow
		// from relation as a inner join relation as b on a.rid=b.rid and
		// a.tag='CHIL' and b.tag != 'CHIL'
		// order by b.pid,a.relationrow

		sql.append("select a.pid,b.pid,b.tag from ");
		sql.append("relation as a inner join relation as b on a.rid=b.rid ");
		sql.append("and a.tag='CHIL' and b.tag != 'CHIL' ");
		if (viewId > 0) {
			sql.append("and a.pid in (select pid from viewunits where vid="
					+ viewId + ") "
					+ "and b.pid in (select pid from viewunits where vid="
					+ viewId + ") ");
		}
		if (surety != 100) {
			sql.append("and a.surety >= " + surety + " ");
		}

		sql.append("order by b.pid,a.relationrow ");

		pst = con.prepareStatement(sql.toString());
		Vector<MinimumIndividual> p = new Vector<MinimumIndividual>();
		int previd = 0;
		rs = pst.executeQuery();
		while (rs.next()) {
			int pare = rs.getInt(1);
			int chil = rs.getInt(2);
			String tag = rs.getString(3);

			if (chil != previd) {
				//
				// now let's find correct family
				//
				MinimumFamily fm;
				boolean foundFam = false;
				for (int i = 0; i < p.size() - 1; i++) {
					for (int j = i + 1; j < p.size(); j++) {
						MinimumIndividual pi = p.get(i);
						MinimumIndividual pj = p.get(j);

						int dada = pi.pid;
						int mama = pj.pid;

						ParentPair pp = new ParentPair(dada, mama);
						fm = families.get(pp.toString());
						if (fm == null) {
							pp = new ParentPair(mama, dada);
							fm = families.get(pp.toString());
						}
						if (fm != null) {

							fm.addChil(previd);
							pi = units.get(previd);
							pi.addFamc(fm.id);

							foundFam = true;
							break;
						}
					}
					if (foundFam)
						break;
				}
				if (!foundFam && previd > 0) { // this means single parent
					for (int i = 0; i < p.size(); i++) {
						MinimumIndividual pi = p.get(i);
						ParentPair pp;
						if (pi.sex.equals("M")) {
							pp = new ParentPair(pi.pid, 0);
						} else {
							pp = new ParentPair(0, pi.pid);
						}
						fm = families.get(pp.toString());
						if (fm != null) {
							fm.addChil(previd);
						} else {
							if (pi.sex.equals("M")) {
								fm = new MinimumFamily(pi.pid, 0);
							} else {
								fm = new MinimumFamily(0, pi.pid);
							}
							families.put(pp.toString(), fm);
							fm.addChil(previd);
							pi = units.get(previd);
							pi.addFamc(fm.id);

						}

					}

				}

				p = new Vector<MinimumIndividual>();

			}
			MinimumIndividual pi = units.get(pare);
			p.add(pi);
			previd = chil;

		}

	}

	private class MinimumIndividual {
		int pid = 0; // person id
		int gid = 0; // gedcom id
		String sex = null;
		Vector<Integer> fams = new Vector<Integer>();
		Vector<Integer> famc = new Vector<Integer>();

		MinimumIndividual(int pid, String sex, int gid) {

			this.pid = pid;
			this.gid = gid;
			this.sex = sex;

		}

		void addFams(int id) {
			this.fams.add(id);

		}

		void addFamc(int id) {
			this.famc.add(id);

		}
	}

	private class ParentPair {
		int dada = 0;
		int mama = 0;

		ParentPair(int dad, int mom) {

			this.dada = dad;
			this.mama = mom;

		}

		@Override
		public String toString() {

			return "" + dada + "_" + mama;

		}

	}

	private static int nextFamilyId = 0;

	private class MinimumFamily {
		int dad = 0;
		int mom = 0;
		int id = 0;
		Vector<Integer> chils = new Vector<Integer>();

		MinimumFamily(int dad, int mom) {
			this.dad = dad;
			this.mom = mom;
			id = ++nextFamilyId;

		}

		int getDada() {
			if (dad == 0)
				return 0;
			MinimumIndividual mm = units.get(dad);
			if (mm == null) {
				logger.warning("person for " + dad + "does not exist");
			}
			return mm.gid;
		}

		int getMama() {
			if (mom == 0)
				return 0;
			MinimumIndividual mm = units.get(mom);
			if (mm == null) {
				logger.warning("person for " + mom + "does not exist");
			}
			return mm.gid;
		}

		void addChil(int chi) {
			chils.add(chi);
		}
	}

}
