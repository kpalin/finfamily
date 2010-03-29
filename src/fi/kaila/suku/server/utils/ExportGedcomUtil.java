package fi.kaila.suku.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import fi.kaila.suku.ant.AntVersion;
import fi.kaila.suku.exports.ExportGedcomDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

public class ExportGedcomUtil {

	private Connection con;

	private ExportGedcomDialog runner = null;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private int viewId = 0;
	private int surety = 100;

	// private String[] charsetNames = { "","Ascii", "Ansel", "UTF-8", "UTF-16"
	// };

	private boolean includeImages = true;

	private LinkedHashMap<Integer, MinimumIndividual> units = null;
	private LinkedHashMap<String, MinimumFamily> families = null;

	private Vector<MinimumImage> images = null;
	private String zipPath = "nemo";

	private enum GedSet {
		Set_None, Set_Ascii, Set_Ansel, Set_Utf8, Set_Utf16
	}

	private GedSet thisSet = GedSet.Set_None;
	private int imageCounter = 0;

	/**
	 * Constructor with connection
	 * 
	 * @param con
	 */
	public ExportGedcomUtil(Connection con) {
		this.con = con;
		this.runner = ExportGedcomDialog.getRunner();
	}

	public SukuData exportGedcom(String db, String path, String langCode,
			int viewId, int surety, int charsetId, boolean includeImages) {

		this.viewId = viewId;
		this.surety = surety;
		switch (charsetId) {
		case 1:
			thisSet = GedSet.Set_Ascii;
			break;
		case 2:
			thisSet = GedSet.Set_Ansel;
			break;
		case 3:
			thisSet = GedSet.Set_Utf8;
			break;
		case 4:
			thisSet = GedSet.Set_Utf16;
			break;
		default:
			thisSet = GedSet.Set_None;
		}

		this.includeImages = includeImages;
		images = new Vector<MinimumImage>();
		SukuData result = new SukuData();
		if (path == null || path.lastIndexOf(".") < 1) {
			result.resu = "output filename missing";
			return result;
		}
		try {
			collectIndividuals();

			collectFamilies();

			zipPath = path.substring(0, path.lastIndexOf("."));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ZipOutputStream zip = new ZipOutputStream(bos);
			String fileName = zipPath + "/" + db + ".ged";

			ZipEntry entry = new ZipEntry(fileName);

			zip.putNextEntry(entry);
			writeBom(zip);
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
				PersonShortData shortie = new PersonShortData(fam.persLong);
				writeIndi(zip, fam.persLong);

				double prose = (curreCount * 100) / allCount;
				int intprose = (int) prose;
				StringBuilder sbb = new StringBuilder();
				sbb.append("" + intprose + ";" + shortie.getAlfaName());
				if (this.runner.setRunnerValue(sbb.toString())) {
					throw new SukuException(Resurses
							.getString("GEDCOM_CANCELLED"));
				}

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

			zip.write(gedBytes("0 TRLR\r\n"));
			zip.closeEntry();

			for (int i = 0; i < images.size(); i++) {
				entry = new ZipEntry(zipPath + "/" + images.get(i).getPath());
				zip.putNextEntry(entry);
				zip.write(images.get(i).imageData);
				zip.closeEntry();
			}

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

	private void writeBom(ZipOutputStream zip) {
		byte[] bom8 = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		byte[] bom16 = { (byte) 0xFE, (byte) 0xFF };
		try {
			switch (thisSet) {
			case Set_Utf8:
				zip.write(bom8);
				return;
			case Set_Utf16:
				zip.write(bom16);
				return;
			}
		} catch (IOException e) {
			logger.warning("Wrining bom: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void writeIndi(ZipOutputStream zip, PersonLongData persLong)
			throws IOException {
		MinimumIndividual indi = units.get(persLong.getPid());
		StringBuilder sb = new StringBuilder();
		sb.append("0 @I" + indi.gid + "@ INDI\r\n");
		sb.append("1 SEX " + indi.sex + "\r\n");
		UnitNotice[] notices = persLong.getNotices();
		for (int i = 0; i < notices.length; i++) {
			if (notices[i].getTag().equals("NAME")
					&& surety >= notices[i].getSurety()) {
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
				if (notices[i].getSource() != null) {
					sb.append(getNoteStructure(2, "SOUR", notices[i]
							.getSource()));

				}
				if (notices[i].getNoticeType() != null) {
					sb.append("2 TYPE " + notices[i].getNoticeType() + "\r\n");
				}
				if (notices[i].getDescription() != null) {
					sb.append(getNoteStructure(2, "NOTE", notices[i]
							.getDescription()));

				}
			}
		}

		for (int i = 0; i < notices.length; i++) {
			if (!notices[i].getTag().equals("NAME")
					&& surety >= notices[i].getSurety()) {
				UnitNotice notice = notices[i];
				StringBuilder nm = new StringBuilder();
				String gedTag = notice.getTag();
				nm.append("1 " + gedTag + "\r\n");
				if (notice.getNoticeType() != null) {
					nm.append("2 TYPE " + notice.getNoticeType() + "\r\n");
				}
				if (notice.getDescription() != null) {
					nm.append("2 NOTE " + notice.getDescription() + "\r\n");
				}
				if (notice.getFromDate() != null) {
					nm.append("2 DATE ");
					if (notice.getDatePrefix() != null) {
						nm.append(notice.getDatePrefix() + " ");
					}
					nm.append(gedDate(notice.getFromDate()));
					if (notice.getDatePrefix() != null
							&& notice.getToDate() != null) {
						if (notice.getDatePrefix().equals("BET")) {
							nm.append(" AND ");
							nm.append(gedDate(notice.getToDate()));
						} else if (notice.getDatePrefix().equals("FROM")) {
							nm.append(" TO ");
							nm.append(gedDate(notice.getToDate()));
						}
					}
					nm.append("\r\n");

				}
				if (notice.getPlace() != null) {
					nm.append("2 PLAC " + notice.getPlace() + "\r\n");
				}
				if (notice.getNoteText() != null) {
					nm
							.append(getNoteStructure(2, "NOTE", notice
									.getNoteText()));
				}

				if (notice.getAddress() != null
						|| notice.getPostOffice() != null) {
					if (notice.getAddress() != null) {
						if (notice.getState() == null) {

							nm.append(getNoteStructure(2, "ADDR", notice
									.getAddress(), 1));
							if (notice.getPostOffice() != null) {
								if (notice.getPostalCode() != null
										&& notice.getPostOffice() != null) {
									nm.append("3 CONT "
											+ notice.getPostalCode() + " "
											+ notice.getPostOffice() + "\r\n");
								} else {
									nm.append("3 CONT "
											+ notice.getPostOffice() + "\r\n");
								}

							}
						} else {
							nm.append(getNoteStructure(2, "ADDR", notice
									.getAddress(), 1));
							if (notice.getPostOffice() != null) {
								nm.append("3 CONT " + notice.getPostOffice()
										+ "\r\n");
							}
							if (notice.getPostalCode() != null) {
								nm.append("3 CONT " + notice.getState() + " "
										+ notice.getPostOffice() + "\r\n");
							} else {
								nm.append("3 CONT " + notice.getState()
										+ "\r\n");
							}

						}

					}
					if (notice.getCountry() != null) {
						nm.append("3 CONT " + notice.getCountry() + "\r\n");
					}
				} else if (notice.getCountry() != null
						|| notice.getState() != null) {
					if (notice.getState() != null) {
						nm.append("2 ADDR " + notice.getState() + "\r\n");
						if (notice.getCountry() != null) {
							nm.append("3 CONT " + notice.getCountry() + "\r\n");
						}
					} else {
						nm.append("2 ADDR " + notice.getCountry() + "\r\n");
					}
				}
				if (notice.getEmail() != null) {
					nm.append("2 EMAIL " + notice.getEmail() + "\r\n");
				}
				if (notice.getSource() != null) {
					nm.append(getNoteStructure(2, "SOUR", notice.getSource()));
				}

				if (includeImages) {
					if (notice.getMediaFilename() != null
							&& notice.getMediaData() != null) {
						MinimumImage minimg = new MinimumImage(indi.gid, notice
								.getMediaFilename(), notice.getMediaData());
						nm.append("2 OBJE\r\n");
						if (notice.getMediaFilename().toLowerCase().endsWith(
								".jpg")) {
							nm.append("3 FORM jpeg\r\n");
						}
						if (notice.getMediaTitle() != null) {
							nm.append("3 TITL " + notice.getMediaTitle()
									+ "\r\n");
						}
						nm.append("3 FILE " + minimg.getPath() + "\r\n");

						images.add(minimg);
					}
				}
				sb.append(nm.toString());

			}
		}

		for (int i = 0; i < indi.fams.size(); i++) {
			sb.append("1 FAMS F" + indi.fams.get(i) + "\r\n");
		}
		for (int i = 0; i < indi.famc.size(); i++) {
			sb.append("1 FAMC F" + indi.famc.get(i) + "\r\n");
		}
		zip.write(gedBytes(sb.toString()));
	}

	private Object gedDate(String dbDate) {
		String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL",
				"AUG", "SEP", "OCT", "NOV", "DEC" };
		String mon = "";
		if (dbDate.length() >= 6) {

			try {
				int m = Integer.parseInt(dbDate.substring(4, 6));
				if (m > 0 && m <= 12) {
					mon = months[m - 1] + " ";
				}
			} catch (NumberFormatException ne) {

			}
		}
		if (dbDate.length() == 8) {
			return dbDate.substring(6) + " " + mon + dbDate.substring(0, 4);
		}
		return mon + dbDate.substring(0, 4);

	}

	private String getNoteStructure(int level, String tag, String text) {
		return getNoteStructure(level, tag, text, 2);
	}

	private String getNoteStructure(int level, String tag, String text,
			int emptyMax) {
		Vector<String> ss = new Vector<String>();

		int linelen = 73;

		if (text == null)
			return null;
		StringBuilder sb = new StringBuilder();
		char prevc = 0;
		int emptyCount = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch (c) {
			case '\r':
				break;
			case '\n':
				emptyCount++;
				sb.append(" ");
				break;
			default:
				if (emptyCount >= emptyMax) {
					if (sb.length() > 0) {
						ss.add(sb.toString());
						sb = new StringBuilder();
					}
				} else if (emptyCount == 1) {
					if (prevc != ' ') {
						sb.append(" ");
					}
				}
				emptyCount = 0;
				sb.append(c);

			}
			prevc = c;
		}
		ss.add(sb.toString());
		sb = new StringBuilder();
		String currTag = tag;
		int currLevel = level;

		for (int i = 0; i < ss.size(); i++) {
			String chap = ss.get(i);
			if (i > 0) {
				currTag = "CONT";
			}
			while (chap.length() > 0) {
				if (chap.length() < linelen) {
					sb.append("" + currLevel + " " + currTag + " " + chap
							+ "\r\n");
					chap = "";
				} else {
					sb.append("" + currLevel + " " + currTag + " "
							+ chap.substring(0, linelen) + "\r\n");
					chap = chap.substring(linelen);
					currLevel = level + 1;
					currTag = "CONC";
				}
			}
		}

		return sb.toString();

	}

	private void writeFam(ZipOutputStream zip, MinimumFamily fam)
			throws IOException {

		StringBuilder sb = new StringBuilder();
		sb.append("0 @F" + fam.id + "@ FAM\r\n");
		if (fam.dad > 0) {
			sb.append("1 HUSB @I" + fam.getDada() + "@\r\n");
		}
		if (fam.mom > 0) {
			sb.append("1 WIFE @I" + fam.getMama() + "@\r\n");
		}
		for (int i = 0; i < fam.chils.size(); i++) {
			sb.append("1 CHIL @I" + fam.getChild(i) + "@\r\n");
		}

		zip.write(gedBytes(sb.toString()));

	}

	private void writeHead(ZipOutputStream zip) throws IOException {

		StringBuilder sb = new StringBuilder();
		sb.append("0 HEAD\r\n");
		sb.append("1 SOUR FinFamily\r\n");
		sb
				.append("2 VERS " + AntVersion.antVersion
						+ " UNDER CONSTRUCTION\r\n");
		sb.append("2 NAME FinFamily\r\n");
		sb.append("2 CORP KK-Software\r\n");
		sb.append("3 ADDR http://www.sukuohjelmisto.fi\r\n");
		sb.append("1 SUBM @U1@\r\n");
		sb.append("1 GEDC\r\n");
		sb.append("2 VERS 5.5\r\n");
		sb.append("2 FORM LINEAGE-LINKED\r\n");

		switch (thisSet) {
		case Set_Ascii:
			sb.append("1 CHAR ASCII\r\n");
			break;
		case Set_Ansel:
			sb.append("1 CHAR ANSEL\r\n");
			break;
		case Set_Utf8:
		case Set_Utf16:
			sb.append("1 CHAR UNICODE\r\n");
			break;
		default:
			sb.append("1 CHAR ANSI\r\n");
		}

		sb.append("0 @U1@ SUBM\r\n");
		sb.append("1 NAME Test user\r\n"); // TODO add user data

		// zip.write(gedBytes("0 HEAD\r\n"));
		//		
		// zip
		// .write(gedBytes("1 NOTE FinFamily Gedcom Export is under construction\r\n"));
		zip.write(gedBytes(sb.toString()));
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

	private byte[] gedBytes(String text) {
		if (text == null)
			return null;
		try {
			switch (thisSet) {
			case Set_Ascii:
				return text.getBytes("US_ASCII");
			case Set_None:
				return text.getBytes("ISO-8859-1");
			case Set_Utf8:
				return text.getBytes("UTF-8");
			case Set_Utf16:
				return text.getBytes("UTF-16");
			case Set_Ansel:
				return toAnsel(text);
			}

		} catch (UnsupportedEncodingException e) {
			logger.warning("Writing " + thisSet.name() + ": " + e.getMessage());
			e.printStackTrace();
		}

		return text.getBytes();
	}

	private byte[] toAnsel(String text) {

		char toAnsel[] = {

		225, 'A', 226, 'A', 227, 'A', 228, 'A', 232, 'A', 234, 'A', 165, 0,
				240, 'C', 225, 'E', 226, 'E', 227, 'E', 232, 'E', 225, 'I',
				226, 'I', 227, 'I', 232, 'I', 163, 0, 228, 'N', 225, 'O', 226,
				'O', 227, 'O', 228, 'O', 232, 'O', 0, 0, 162, 0, 225, 'U', 226,
				'U', 227, 'U', 232, 'U', 226, 'Y', 164, 0, 207, 0,

				225, 'a', 226, 'a', 227, 'a', 228, 'a', 232, 'a', 234, 'a',
				182, 0, 240, 'c', 225, 'e', 226, 'e', 227, 'e', 232, 'e', 225,
				'i', 226, 'i', 227, 'i', 232, 'i', 186, 0, 228, 'n', 225, 'o',
				226, 'o', 227, 'o', 228, 'o', 232, 'o', 0, 0, 178, 0, 225, 'u',
				226, 'u', 227, 'u', 232, 'u', 226, 'y', 180, 0, 232, 'y' };

		StringBuilder st = new StringBuilder();

		int iInLen = text.length();
		int iNow = 0;

		int iIndex;
		// TCHAR uCurr,u0,u1;
		char uCurr, u0, u1;
		// LPTSTR ps = sTemp.GetBuffer(iInLen*2);
		while (iNow < iInLen) {
			// uCurr = sIn.GetAt(iNow);
			uCurr = text.charAt(iNow);
			iNow++;
			if ((uCurr & 0x80) == 0) {
				st.append(uCurr);
				// ps[iNew++]=uCurr;
			} else {
				if ((uCurr & 0xc0) != 0xc0) {
					switch (uCurr) {

					case 0x8c:
						st.append((char) 166);
						break;
					case 0x9c:
						st.append((char) 182);
						break;
					case 0xa1:
						st.append((char) 198);
						break;
					case 0xa3:
						st.append((char) 185);
						break;
					case 0xa9:
						st.append((char) 195);
						break;
					case 0xbf:
						st.append((char) 207);
						break;
					default:
						st.append('?');
						break;
					}
				} else {
					iIndex = uCurr - 0xc0;
					u0 = toAnsel[iIndex * 2];
					u1 = toAnsel[iIndex * 2 + 1];
					if (u0 == 0)
						st.append('?');
					else
						st.append(u0);
					if (u1 != 0)
						st.append(u1);
				}
			}
		}

		try {
			return st.toString().getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			logger.warning("Writing ansel: " + e.getMessage());
			e.printStackTrace();
		}
		return text.getBytes();

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

		int getChild(int idx) {
			int cid = chils.get(idx);
			MinimumIndividual mm = units.get(cid);
			if (mm == null) {
				logger.warning("child for " + cid + "does not exist");
			}
			return mm.gid;
		}
	}

	class MinimumImage {
		int indiGid = 0;
		String imgName = null;
		int counter = 0;
		byte[] imageData = null;

		MinimumImage(int gid, String name, byte[] data) {
			this.indiGid = gid;
			this.imgName = name;
			this.imageData = data;
			this.counter = ++imageCounter;
		}

		String getPath() {
			StringBuilder sb = new StringBuilder();
			sb.append("images/" + counter + "_" + imgName);
			return sb.toString();
		}

	}

}
