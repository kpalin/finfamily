package fi.kaila.suku.server.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fi.kaila.suku.imports.ImportGedcomDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.ExcelBundle;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateException;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * Gedcom import class.
 * 
 * @author Kalle
 */
public class ImportGedcomUtil {

	private final Connection con;

	private ImportGedcomDialog runner = null;
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private enum GedSet {
		Set_None, Set_Ascii, Set_Ansel, Set_Utf8, Set_Utf16le, Set_Utf16be
	}

	private GedSet thisSet = GedSet.Set_None;

	/**
	 * Constructor with connection.
	 * 
	 * @param con
	 *            the con
	 */
	public ImportGedcomUtil(Connection con) {
		this.con = con;
		this.runner = ImportGedcomDialog.getRunner();
	}

	/** The ged pid. */
	LinkedHashMap<String, GedcomPidEle> gedPid = null;

	/** The ged map. */
	LinkedHashMap<String, GedcomLine> gedMap = null;

	/** The ged source. */
	LinkedHashMap<String, GedcomLine> gedSource = null;

	/** The ged fam map. */
	LinkedHashMap<String, GedcomLine> gedFamMap = null;

	/** The unknown line. */
	Vector<String> unknownLine = new Vector<String>();

	/** The ged fams. */
	LinkedHashMap<String, GedcomFams> gedFams = null;

	/** The ged adopt. */
	LinkedHashMap<String, GedcomLine> gedAdopt = null;

	/** The repo texts. */
	ExcelBundle repoTexts = null;
	// HashMap<String, String> texts = null;
	/** The images. */
	LinkedHashMap<String, String> images = null;

	/** The is zip file. */
	boolean isZipFile = false;

	/** The base folder. */
	String baseFolder = "";

	/**
	 * Import gedcom.
	 * 
	 * @param lang
	 *            the lang
	 * @return result in SukuData
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData importGedcom(String path, String lang) throws SukuException {
		SukuData resp = new SukuData();
		gedMap = new LinkedHashMap<String, GedcomLine>();
		gedSource = new LinkedHashMap<String, GedcomLine>();
		gedFamMap = new LinkedHashMap<String, GedcomLine>();
		gedPid = new LinkedHashMap<String, GedcomPidEle>();
		gedFams = new LinkedHashMap<String, GedcomFams>();
		gedAdopt = new LinkedHashMap<String, GedcomLine>();
		images = new LinkedHashMap<String, String>();
		seenTrlr = false;
		double indiCount = 0;
		double famCount = 0;
		GedcomLine record = null;
		Statement stm;
		logger.fine("importGedcom entered from " + path);
		if (path == null) {
			throw new SukuException("FILE MISSING");
		}
		repoTexts = new ExcelBundle();
		Locale locRepo = new Locale(lang);
		repoTexts.importBundle(Suku.getFinFamilyXls(), "Report", locRepo);
		logger.fine("importGedcom repoTexts + con=" + con);

		try {
			int unitCount = 0;
			stm = con.createStatement();
			ResultSet rs = stm.executeQuery("select count(*) from unit");

			if (rs.next()) {
				unitCount = rs.getInt(1);

			}

			logger.fine("gedcom existing count " + unitCount);
			rs.close();
			stm.close();
			if (unitCount > 0) {
				resp.resu = Resurses.getString("DATABASE_NOT_EMPTY");
				return resp;
			}
			logger.fine("gedcom check filename next for count " + unitCount);
			String fileName = path;
			logger.fine("gedcom input from " + fileName);
			ZipInputStream zipIn = null;
			ZipEntry zipEntry = null;
			String entryName = null;
			BufferedInputStream bis;
			File f = new File(fileName);
			FileInputStream ff = new FileInputStream(f);
			boolean foundGedcom = false;
			if (fileName.toLowerCase().endsWith(".zip")) {
				isZipFile = true;
				// this is a zip-file. let's first find the gedcom file from
				// there

				zipIn = new ZipInputStream(ff);
				bis = new BufferedInputStream(zipIn);
				while ((zipEntry = zipIn.getNextEntry()) != null) {
					entryName = zipEntry.getName();
					if (entryName.toLowerCase().endsWith(".ged")) {
						foundGedcom = true;
						int li = entryName.replace('\\', '/').lastIndexOf('/');
						if (li > 0) {
							baseFolder = entryName.substring(0, li + 1);
						}
						setRunnerValue("+" + entryName);
						break;
					} else {
						copyImageToTempfile(zipIn, entryName);
					}
				}
			} else if (fileName.toLowerCase().endsWith(".ged")) {
				foundGedcom = true;
				int li = fileName.replace('\\', '/').lastIndexOf('/');
				if (li > 0) {
					baseFolder = fileName.substring(0, li + 1);
				}
				bis = new BufferedInputStream(ff);
			} else {
				resp.resu = Resurses.getString("GEDCOM_BAD_FORMAT");
				return resp;
			}

			long dataLen = f.length();
			double dLen = dataLen;
			int data = 0;
			fileIndex = 4;
			if (dataLen < 10 || !foundGedcom) {
				resp.resu = Resurses.getString("GEDCOM_BAD_FORMAT");
				return resp;
			}
			char c;
			int data0 = bis.read();
			int data1 = bis.read();
			int data2 = bis.read();
			int data3 = bis.read();

			// int lineNumber = 0;
			StringBuilder line = new StringBuilder();
			if (data0 == 255 && data1 == 254) {
				thisSet = GedSet.Set_Utf16le;
				c = (char) (data3 * 256 + data2);
				line.append(c);
			} else if (data1 == 0 && data0 == 48) {
				thisSet = GedSet.Set_Utf16le;
				c = (char) (data0);
				line.append(c);
				c = (char) (data2);
				line.append(c);
			} else if (data0 == 255 && data1 == 254) {
				thisSet = GedSet.Set_Utf16be;
				c = (char) (data2 * 256 + data3);
				line.append(c);
			} else if (data0 == 0 && data1 == 48) {
				thisSet = GedSet.Set_Utf16be;
				c = (char) (data1);
				line.append(c);
				c = (char) (data3);
				line.append(c);
			} else if (data0 == 239 && data1 == 187 && data2 == 191) {
				thisSet = GedSet.Set_Utf8;
				c = (char) data3;
				line.append(c);
			} else {
				c = (char) data0;
				line.append(c);

				c = (char) data1;
				line.append(c);
				c = (char) data2;
				line.append(c);
				c = (char) data3;
				line.append(c);

			}

			// while ((data = bis.read()) >= 0) {
			while (true) {
				data = bis.read();

				fileIndex++;
				if (data < 0) {
					c = '\n';
				} else {
					c = cnvToChar(data, bis);
				}
				if (c != '\n' && c != '\r') {
					if (line.length() > 0 || c != ' ') {
						line.append(c);
					}
				}
				if ((c == '\n' || c == '\r') && line.length() > 0) {

					// System.out.println(line.toString());
					// now we have next line
					// split into parts
					String linex = line.toString();
					int i1 = linex.indexOf(' ');
					int i2 = 0;
					int i3 = 0;
					int ix = 0;

					GedcomLine lineg = null;
					String aux;
					if (i1 > 0 && i1 < linex.length()) {
						try {
							ix = Integer.parseInt(linex.substring(0, i1));

						} catch (NumberFormatException ne) {
							ix = -1;
						}
						lineg = new GedcomLine(ix);
						if (ix == 0) {
							if (record != null) {
								String key = record.getKey();
								gedMap.put(key, record);
								if (record.tag.equals("INDI")) {
									indiCount++;
								} else if (record.tag.equals("FAM")) {
									famCount++;
								}
								// consumeGedcomRecord(record);
								if (this.runner != null) {
									StringBuilder sb = new StringBuilder();

									double dluku = fileIndex;

									double prose = (dluku * 100) / dLen;
									int intprose = (int) prose;
									sb.append(intprose);
									sb.append(";");
									sb.append(record.toString(false));
									setRunnerValue(sb.toString());

								}
							}
							record = lineg;

						}

						i2 = linex.indexOf(' ', i1 + 1);
						if (i2 > 0 && i2 < linex.length()) {
							aux = linex.substring(i1 + 1, i2);
							i3 = i2;
							if (aux.charAt(0) == '@'
									&& aux.charAt(aux.length() - 1) == '@') {
								lineg.id = aux;
								i3 = linex.indexOf(' ', i2 + 1);
								if (i3 > 0) {
									lineg.tag = linex.substring(i2 + 1, i3);
								} else {
									lineg.tag = linex.substring(i2 + 1);
								}
							} else {
								lineg.tag = aux;
							}
							if (i3 > 0) {
								lineg.lineValue = linex.substring(i3 + 1);
							}
						} else {
							lineg.tag = linex.substring(i1 + 1);
						}
						// System.out.println("'" + lineg.level + "'" + lineg.id
						// + "'" + lineg.tag + "'" + lineg.lineValue);
					}
					if (lineg == null || lineg.tag == null)
						continue;

					if (lineg.lineValue != null && lineg.level == 1
							&& lineg.tag.equals("CHAR")) {
						if (thisSet == GedSet.Set_None) {
							if (lineg.lineValue.equalsIgnoreCase("UNICODE")
									|| lineg.lineValue
											.equalsIgnoreCase("UTF-8")
									|| lineg.lineValue.equalsIgnoreCase("UTF8")) {
								thisSet = GedSet.Set_Utf8;
							} else if (lineg.lineValue
									.equalsIgnoreCase("ANSEL")) {
								thisSet = GedSet.Set_Ansel;
							}
						}
					} else {
						if (lineg.level > 0) {
							record.add(lineg);

						}
					}
					line = new StringBuilder();

				}
				if (data < 0)
					break;
			}
			if (record == null) {
				throw new SukuException(Resurses.getString("GEDCOM_MISSING"));
			}
			String key = record.getKey();
			gedMap.put(key, record);

			if (zipIn != null && isZipFile) {
				zipIn.closeEntry();

				ZipEntry entry = null;
				while ((entry = zipIn.getNextEntry()) != null) {

					String imgName = entry.getName();
					copyImageToTempfile(zipIn, imgName);

				}
				zipIn.close();

			}

			logger.info("Starting to do INDI");
			// consumeGedcomRecord(record);
			Set<Map.Entry<String, GedcomLine>> lineSet = gedMap.entrySet();
			Iterator<Map.Entry<String, GedcomLine>> ite = lineSet.iterator();
			double indiIndex = 0;
			while (ite.hasNext()) {
				Map.Entry<String, GedcomLine> entry = ite.next();
				GedcomLine rec = entry.getValue();
				consumeGedcomRecord(rec);
				StringBuilder sb = new StringBuilder();

				double prose = (indiIndex * 100) / indiCount;
				int intprose = (int) prose;
				sb.append(intprose);
				sb.append(";");
				sb.append(rec.toString(false));
				setRunnerValue(sb.toString());
				indiIndex++;
			}

			if (!seenTrlr) {
				unknownLine.add(Resurses.getString("GEDCOM_NO_TRLR") + "\r\n");

			}
			logger.info("Starting to do FAM");
			setRunnerValue(Resurses.getString("GEDCOM_FINALIZE"));
			Set<Map.Entry<String, GedcomLine>> entries = gedFamMap.entrySet();
			Iterator<Map.Entry<String, GedcomLine>> ee = entries.iterator();
			double famIndex = 0;
			while (ee.hasNext()) {
				Map.Entry<String, GedcomLine> entry = ee.next();
				GedcomLine rec = entry.getValue();
				consumeGedcomFam(rec);
				StringBuilder sb = new StringBuilder();
				double prose = (famIndex * 100) / famCount;
				int intprose = (int) prose;
				sb.append(intprose);
				sb.append(";");
				sb.append(rec.toString(false));
				setRunnerValue(sb.toString());
				famIndex++;
			}
			logger.info("Mostly done now");
			// this.runner.setRunnerValue(Resurses.getString("GEDCOM_FINALIZE"));
			// consumeFams();
			// resp.generalArray = recs.toArray(new String[0]);

			bis.close();
		} catch (Throwable e) {
			unknownLine.add(e.getMessage() + "\r\n");
			logger.log(Level.WARNING, "gedcom input", e);
		} finally {
			Set<Map.Entry<String, String>> imgSet = images.entrySet();
			Iterator<Map.Entry<String, String>> iti = imgSet.iterator();

			while (iti.hasNext()) {
				Map.Entry<String, String> entryi = iti.next();
				String fn = entryi.getKey();

				unknownLine.add(Resurses.getString("IMPORT_ZIP_NOT_USED")
						+ ": " + fn + "\r\n");
			}

			resp.generalArray = unknownLine.toArray(new String[0]);
			gedMap.clear();
			gedSource.clear();
			gedFamMap.clear();
			gedPid.clear();
			gedFams.clear();
			gedAdopt.clear();
			logger.info("maps cleared");
		}
		return resp;
		// SukuUtility data = SukuUtility.instance();
		// data.createSukuDb(this.con, "/sql/finfamily.sql");

		// logger.fine("database created for " + path);

	}

	private void copyImageToTempfile(ZipInputStream zipIn, String imgName)
			throws IOException, FileNotFoundException, SukuException {
		int ldot = imgName.lastIndexOf(".");
		String imgSuffix = null;
		if (ldot > 0 && ldot > (imgName.length() - 6)) {
			imgSuffix = imgName.substring(ldot);
		}

		if (baseFolder.length() > 0) {
			if (imgName.substring(0, baseFolder.length()).equalsIgnoreCase(
					baseFolder)) {
				imgName = imgName.substring(baseFolder.length());
			}
		}
		setRunnerValue(imgName);
		File tf = File.createTempFile("finFam", imgSuffix);
		BufferedOutputStream fos = new BufferedOutputStream(
				new FileOutputStream(tf));
		int dd = 0;
		while ((dd = zipIn.read()) >= 0) {
			fos.write(dd);
		}
		tf.deleteOnExit();
		fos.close();

		images.put(imgName.replace('\\', '/'), tf.getPath());
		zipIn.closeEntry();
	}

	/**
	 * The FAMS lines from INDI records have been stored in the gedFams map We
	 * use those to put spouses in correct order
	 * 
	 * 
	 */

	private void consumeGedcomFam(GedcomLine record) {

		ArrayList<Relation> rels = new ArrayList<Relation>();
		Relation rel = null;
		Relation crel;
		int ownerPid = 0;

		GedcomLine lineHusb = null;
		GedcomLine lineWife = null;
		GedcomLine lineChil = null;

		GedcomPidEle aid = null;
		GedcomPidEle bid = null;
		GedcomPidEle cid = null;

		ArrayList<RelationNotice> relNotice = new ArrayList<RelationNotice>();
		for (int i = 0; i < record.lines.size(); i++) {
			if (record.lines.get(i).tag.equals("HUSB")) {
				lineHusb = record.lines.get(i);
			} else if (record.lines.get(i).tag.equals("WIFE")) {
				lineWife = record.lines.get(i);
			}
		}
		if (lineHusb != null && lineWife != null) {
			aid = gedPid.get(lineHusb.lineValue);
			bid = gedPid.get(lineWife.lineValue);
			if (aid != null && bid != null) {
				if (ownerPid == 0) {
					ownerPid = aid.pid;
				}

				rel = new Relation(0, aid.pid, bid.pid, "WIFE", 100, null,
						null, null, null);
				rels.add(rel);
			}
		}
		if (lineHusb != null) {
			aid = gedPid.get(lineHusb.lineValue);
			if (aid != null) {
				ownerPid = aid.pid;
			}
		} else if (lineWife != null) {
			bid = gedPid.get(lineWife.lineValue);
			if (bid != null) {
				ownerPid = bid.pid;
			}
		}

		RelationNotice rnmarr = null;
		RelationNotice rn = null;
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine line = record.lines.get(i);
			if ("|MARR|DIV|ANUL|CENS|DIVF|ENGA|MARB|MARC|MARL|MARS|SOUR|NOTE|"
					.indexOf(line.tag) > 0) {
				String sukuTag = null;
				if ("|DIV|ANUL|CENS|DIVF|".indexOf(line.tag) > 0) {
					sukuTag = "DIV";
				} else if ("|MARR|ENGA|MARB|MARC|MARL|MARS|".indexOf(line.tag) > 0) {
					sukuTag = "MARR";
				} else {
					sukuTag = line.tag;
				}
				if (sukuTag.equals("MARR") || sukuTag.equals("DIV")) {
					rnmarr = new RelationNotice(sukuTag);
					rnmarr.setSurety(100);
					relNotice.add(rnmarr);
					rn = rnmarr;
					if (line.tag.equals("ENGA")) {
						rn.setType(Resurses.getReportString("ENGA"));
					}

				} else {
					rn = new RelationNotice(sukuTag);
					rn.setSurety(100);
					relNotice.add(rn);
					if (line.tag.equals("SOUR")) {
						rn.setSource(line.lineValue);
					} else if (line.tag.equals("NOTE")) {
						rn.setNoteText(line.lineValue);
					}
				}
				for (int j = 0; j < line.lines.size(); j++) {
					GedcomLine detail = line.lines.get(j);

					if (detail.tag.equals("TYPE")) {
						rn.setType(detail.lineValue);
					} else if (detail.tag.equals("CAUS")) {
						rn.setDescription(detail.lineValue);
					} else if (detail.tag.equals("NOTE")) {
						rn.setNoteText(detail.lineValue);
					} else if (detail.tag.equals("PLAC")) {
						rn.setPlace(detail.lineValue);
					} else if (detail.tag.equals("SOUR")) {
						rn.setSource(detail.lineValue);
					} else if (detail.tag.equals("DATE")) {
						String[] dparts = consumeGedcomDate(detail.lineValue);
						if (dparts != null && dparts.length > 3) {
							rn.setDatePrefix(dparts[0]);
							rn.setFromDate(dparts[1]);
							rn.setToDate(dparts[2]);
							if (dparts[3] != null) {
								if (rn.getDescription() != null) {
									rn.setDescription(Utils.nv(rn
											.getDescription())
											+ " "
											+ dparts[3]);
								} else {
									rn.setDescription(dparts[3]);
								}
							}
						}
					} else if (detail.tag.equals("CHAN")) {
						// TODO:
					} else if (detail.tag.equals("CREA")) {
						// TODO:
					} else {
						unknownLine.add(detail.toString());
					}

				}

			} else if (line.tag.equals("HUSB") || line.tag.equals("WIFE")) {
				// TODO:
			} else if (line.tag.equals("NOTE")) {
				if (rnmarr != null) {
					StringBuilder sb = new StringBuilder();
					if (rnmarr.getNoteText() != null) {
						sb.append(rnmarr.getNoteText());
						sb.append(" ");
					}
					sb.append(line.lineValue);
					rnmarr.setNoteText(sb.toString());
				}

			} else if (line.tag.equals("CHIL")) {
				lineChil = record.lines.get(i);
				cid = gedPid.get(lineChil.lineValue);

				if (cid != null) {

					GedcomLine adopt = gedAdopt.get(lineChil.lineValue);

					if (aid != null) {
						crel = new Relation(0, cid.pid, aid.pid, "FATH", 100,
								null, null, null, null);
						rels.add(crel);
						if (adopt != null) {
							for (int j = 0; j < adopt.lines.size(); j++) {
								GedcomLine detail = adopt.lines.get(j);
								if (detail.lineValue != null
										&& record.id.equals(detail.lineValue)) {
									boolean adoptedByFather = true; // as
									// default
									for (int k = 0; k < detail.lines.size(); k++) {
										GedcomLine sub = detail.lines.get(k);
										if (sub.tag.equals("ADOP")) {
											if ("MOTH".equals(sub.lineValue)) {
												adoptedByFather = false;
											}
										}

									}
									if (adoptedByFather) {
										RelationNotice[] ados = new RelationNotice[1];
										ados[0] = new RelationNotice("ADOP");

										crel.setNotices(ados);

									}
								}
							}
						}
					}
					if (bid != null) {
						crel = new Relation(0, cid.pid, bid.pid, "MOTH", 100,
								null, null, null, null);
						rels.add(crel);
						if (adopt != null) {
							for (int j = 0; j < adopt.lines.size(); j++) {
								GedcomLine detail = adopt.lines.get(j);
								if (detail.lineValue != null
										&& record.id.equals(detail.lineValue)) {
									boolean adoptedByMother = true; // as
									// default
									for (int k = 0; k < detail.lines.size(); k++) {
										GedcomLine sub = detail.lines.get(k);
										if (sub.tag.equals("ADOP")) {
											if ("FATH".equals(sub.lineValue)) {
												adoptedByMother = false;
											}
										}

									}
									if (adoptedByMother) {
										RelationNotice[] ados = new RelationNotice[1];
										ados[0] = new RelationNotice("ADOP");
										crel.setNotices(ados);
									}
								}
							}
						}
					}
				}
			} else if (line.tag.equals("CHAN")) {
				// TODO:
			} else if (line.tag.equals("CREA")) {
				// TODO:
			} else {
				unknownLine.add(line.toString());
			}
		}
		if (rel != null) {
			rel.setNotices(relNotice.toArray(new RelationNotice[0]));
		}

		//
		// pick up the position of both husband and wife from FAMS lines in INDI
		// records
		// to set row number of spouse
		//

		int husbandNumber = 0;
		int wifeNumber = 0;
		if (aid != null && bid != null) {

			GedcomFams fms = gedFams.get(aid.id);
			wifeNumber = -1;
			for (int ffi = 0; ffi < fms.fams.size(); ffi++) {
				GedcomLine fam = fms.fams.get(ffi);
				GedcomLine faw = gedFamMap.get(fam.lineValue);
				for (int ffj = 0; ffj < faw.lines.size(); ffj++) {
					GedcomLine fax = faw.lines.get(ffj);
					if (fax.lineValue != null && fax.lineValue.equals(bid.id)) {
						wifeNumber = ffi;
						break;
					}
				}
				if (wifeNumber >= 0) {
					break;
				}
			}
			if (wifeNumber <= 0) {
				wifeNumber = 0;
			}

			fms = gedFams.get(bid.id);
			husbandNumber = -1;
			for (int ffi = 0; ffi < fms.fams.size(); ffi++) {
				GedcomLine fam = fms.fams.get(ffi);
				GedcomLine faw = gedFamMap.get(fam.lineValue);
				for (int ffj = 0; ffj < faw.lines.size(); ffj++) {
					GedcomLine fax = faw.lines.get(ffj);
					if (fax.lineValue != null && fax.lineValue.equals(aid.id)) {
						husbandNumber = ffi;
						break;
					}
				}
				if (husbandNumber >= 0) {
					break;
				}
			}
			if (husbandNumber <= 0) {
				husbandNumber = 0;
			}

		} else if (aid != null && cid != null) {

			GedcomFams fms = gedFams.get(aid.id);
			wifeNumber = -1;
			for (int ffi = 0; ffi < fms.fams.size(); ffi++) {
				GedcomLine fam = fms.fams.get(ffi);
				GedcomLine faw = gedFamMap.get(fam.lineValue);
				for (int ffj = 0; ffj < faw.lines.size(); ffj++) {
					GedcomLine fax = faw.lines.get(ffj);

					if (cid != null && fax != null && fax.lineValue != null
							&& fax.lineValue.equals(cid.id)) {
						wifeNumber = ffi;
						break;
					}
				}
				if (wifeNumber >= 0) {
					break;
				}
			}
			if (wifeNumber <= 0) {
				wifeNumber = 0;
			}
		} else if (bid != null && cid != null) {

			GedcomFams fms = gedFams.get(bid.id);
			wifeNumber = -1;
			for (int ffi = 0; ffi < fms.fams.size(); ffi++) {
				GedcomLine fam = fms.fams.get(ffi);
				GedcomLine faw = gedFamMap.get(fam.lineValue);
				for (int ffj = 0; ffj < faw.lines.size(); ffj++) {
					GedcomLine fax = faw.lines.get(ffj);

					if (fax != null && fax.lineValue != null
							&& fax.lineValue.equals(cid.id)) {
						husbandNumber = ffi;
						break;
					}
				}
				if (husbandNumber >= 0) {
					break;
				}
			}
			if (husbandNumber <= 0) {
				husbandNumber = 0;
			}
		}

		PersonUtil u = new PersonUtil(con);
		String resu = u.insertGedcomRelations(husbandNumber, wifeNumber,
				rels.toArray(new Relation[0]));
		if (resu != null) {
			unknownLine.add(record.toString() + ":" + resu);
		}
		// SukuData resp = u.updatePerson(req);

	}

	/** The record count. */
	int recordCount = 0;

	/** The owner info. */
	String ownerInfo = null;

	/** The seen trlr. */
	boolean seenTrlr = false;

	private void consumeGedcomRecord(GedcomLine record) throws SQLException,
			SukuException {
		recordCount++;
		if (seenTrlr) {
			unknownLine.add(record.toString());
			return;
		}
		if (recordCount == 1) { // expected HEAD record
			if (!record.tag.equals("HEAD")) {
				unknownLine.add(Resurses.getString("GEDCOM_MISSING_HEAD"));
			} else {
				consumeGedcomHead(record);
			}
		} else if (record.tag.equals("SUBM")) {
			consumeGedcomSubmitter(record);
		} else if (record.tag.equals("INDI")) {
			consumeGedcomIndi(record);
		} else if (record.tag.equals("SOUR") || record.tag.equals("NOTE")) {
			gedSource.put(record.id, record);
		} else if (record.tag.equals("FAM")) {
			gedFamMap.put(record.id, record);
		} else if (record.tag.equals("TRLR")) {
			seenTrlr = true;
		} else {
			unknownLine.add(record.toString());
		}

	}

	private String extractGedcomSource(GedcomLine record) {
		StringBuilder sb = new StringBuilder();
		sb.append(record.lineValue);
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine line = record.lines.get(i);
			if (line.tag.equals("TEXT") || line.tag.equals("NOTE")) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(line.lineValue);
			}
		}
		if (sb.length() == 0)
			return null;
		return sb.toString();
	}

	private void consumeGedcomIndi(GedcomLine record) throws SukuException {
		PersonLongData pers = new PersonLongData(0, "INDI", "U");
		ArrayList<UnitNotice> notices = new ArrayList<UnitNotice>();
		GedcomFams f = new GedcomFams();
		f.id = record.id;
		pers.setUserRefn(record.id);
		String previousGivenName = null;
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine noti = record.lines.get(i);
			if (noti.tag.equals("SEX")) {
				pers.setSex(noti.lineValue);
			} else if (noti.tag.equals("REFN")) {
				pers.setUserRefn(noti.lineValue);
			} else if (noti.tag.equals("ALIA") && sourceSystem != null
					&& sourceSystem.toLowerCase().indexOf("sukujutut") >= 0
					&& noti.lineValue != null) {

				if (noti.lineValue.indexOf(" ") < 0
						&& noti.lineValue.indexOf(",") < 0) {
					UnitNotice notice = new UnitNotice("NAME");
					notices.add(notice);
					notice.setGivenname(previousGivenName);
					notice.setSurname(noti.lineValue);
				} else {
					UnitNotice notice = new UnitNotice("ALIA");
					notices.add(notice);
					notice.setDescription(noti.lineValue);
				}
			} else if (noti.tag.equals("NAME")) {

				if (noti.lineValue != null) {
					StringBuilder privSource = new StringBuilder();
					UnitNotice notice = new UnitNotice("NAME");
					notices.add(notice);
					previousGivenName = null;
					String parts[] = noti.lineValue.split("/");
					if (parts.length > 0) {
						if (parts[0].length() > 0) {
							previousGivenName = parts[0];
							notice.setGivenname(Utils.extractPatronyme(
									parts[0], false));
							notice.setPatronym(Utils.extractPatronyme(parts[0],
									true));
						}
						if (parts.length > 1 && parts[1].length() > 0) {

							int vonIndex = Utils.isKnownPrefix(parts[1]);
							if (vonIndex > 0) {
								notice.setPrefix(parts[1]
										.substring(0, vonIndex));
								notice.setSurname(parts[1]
										.substring(vonIndex + 1));
							} else {

								notice.setSurname(parts[1]);
							}
						}
						if (parts.length > 2 && parts[2].length() > 0) {
							notice.setPostfix(parts[2]);
						}
					}

					for (int j = 0; j < noti.lines.size(); j++) {
						GedcomLine detail = noti.lines.get(j);
						if (detail.tag.equals("NSFX")) {
							if ((notice.getPatronym() != null && !notice
									.getPatronym().equals(detail.lineValue))) {
								StringBuilder sb = new StringBuilder();
								if (notice.getGivenname() != null) {
									sb.append(notice.getGivenname());
									sb.append(" ");
								}
								sb.append(notice.getPatronym());
								notice.setGivenname(sb.toString());
							}
							notice.setPatronym(detail.lineValue);
						} else if (detail.tag.equals("SPFX")
								|| detail.tag.equals("NPFX")) {
							if (notice.getPrefix() == null) {
								notice.setPrefix(detail.lineValue);
							} else {
								notice.setPrefix(notice.getPrefix() + " "
										+ detail.lineValue);
							}
						} else if (detail.tag.equals("NICK")) {
							if (notice.getPatronym() == null
									&& detail.lineValue != null) {
								String patro = Utils.extractPatronyme(
										detail.lineValue, true);
								if (patro != null) {
									notice.setPatronym(patro);
								} else {
									if (privSource.length() > 0) {
										privSource.append(";");
									}
									privSource.append(detail.lineValue);
									unknownLine.add(detail.toString());
								}

							}
						} else if (detail.tag.equals("GIVN")) {

							if (notice.getGivenname() != null
									&& !notice.getGivenname().equals(
											detail.lineValue)) {
								if (privSource.length() > 0) {
									privSource.append(";");
								}
								privSource.append(detail.lineValue);
								unknownLine.add(detail.toString());
							}
						} else if (detail.tag.equals("SURN")) {
							if (notice.getSurname() != null
									&& !notice.getSurname().equals(
											detail.lineValue)) {
								if (privSource.length() > 0) {
									privSource.append(";");
								}
								privSource.append(detail.lineValue);
								unknownLine.add(detail.toString());
							}
						} else if (detail.tag.equals("NOTE")) {
							if (notice.getDescription() == null) {
								notice.setDescription(detail.lineValue);
							} else {
								notice.setDescription(notice.getDescription()
										+ " " + detail.lineValue);
							}
						} else if (detail.tag.equals("SOUR")) {
							if (notice.getSource() == null) {
								notice.setSource(detail.lineValue);
							} else {
								notice.setSource(notice.getSource() + " "
										+ detail.lineValue);
							}
						} else if (detail.tag.equals("TYPE")) {
							notice.setNoticeType(detail.lineValue);
						} else if (detail.tag.equals("CREA")) {
							// TODO:
						} else if (detail.tag.equals("CHAN")) {
							// TODO:
						} else {
							unknownLine.add(detail.toString());
						}
					}
					if (privSource.length() > 0) {

						notice.setPrivateText(privSource.toString());
					}
				}
			} else if (noti.tag.equals("NOTE")) {
				if (noti.lineValue != null) {
					UnitNotice notice = new UnitNotice("NOTE");
					notices.add(notice);
					if (noti.lineValue.startsWith("@")
							&& noti.lineValue.indexOf('@', 1) > 1) {
						GedcomLine notirec = gedMap.get(noti.lineValue);
						if (notirec != null) {
							notice.setNoteText(notirec.lineValue);
						}
					} else {
						notice.setNoteText(noti.lineValue);
					}
				}
			} else if (noti.tag.equals("ADDR")) {
				UnitNotice notice = new UnitNotice("RESI");
				notices.add(notice);
				extractAddressData(notice, noti);
			} else if (noti.tag.equals("OBJE")) {
				int topIdx = notices.size() - 1;
				UnitNotice top = notices.get(topIdx);
				if (sourceSystem.equals("FinFamily")
						&& top.getMediaData() == null
						&& top.getMediaFilename() == null
						&& top.getMediaTitle() == null) {
					extractMultimedia(top, noti);
				} else {
					UnitNotice notice = new UnitNotice("PHOT");
					notices.add(notice);
					extractMultimedia(notice, noti);
				}
			} else if (noti.tag.equals("FAMC")) {
				// TODO:
			} else if (noti.tag.equals("FAMS")) {
				f.fams.add(noti);
			} else if (noti.tag.equals("SOUR")) {
				if (noti.lineValue.startsWith("@")
						&& noti.lineValue.indexOf('@', 1) > 1) {
					GedcomLine dets = gedMap.get(noti.lineValue);

					pers.setSource(extractSourceText(dets));
				} else {
					String src = extractGedcomSource(noti);
					pers.setSource(src);
				}
			} else if (noti.tag.equals("CHAN")) {
				// TODO:
			} else if (noti.tag.equals("CREA")) {
				// TODO:
			} else if (noti.tag.equals("ADOP")) {

				noti.lineValue = record.id;
				if (noti.lineValue != null) {
					gedAdopt.put(noti.lineValue, noti);
				}
			} else if (Resurses.gedcomTags.indexOf(noti.tag) > 0

			|| noti.tag.startsWith("_")) {

				String notiTag = noti.tag;

				if (notiTag.equals("BAPM") || notiTag.equals("BARM")
						|| notiTag.equals("BASM") || notiTag.equals("BLES")) {
					notiTag = "CHR";
				}
				if (notiTag.startsWith("_"))
					notiTag = noti.tag.substring(1);
				UnitNotice notice = new UnitNotice(notiTag);
				notices.add(notice);
				if (noti.lineValue != null && !noti.lineValue.equals("Y")) {
					notice.setDescription(noti.lineValue);
				}
				for (int j = 0; j < noti.lines.size(); j++) {
					GedcomLine detail = noti.lines.get(j);
					if (detail.tag.equals("TYPE")) {
						notice.setNoticeType(detail.lineValue);
					} else if (detail.tag.equals("CAUS")) {
						notice.setDescription(detail.lineValue);
					} else if (detail.tag.equals("FAMC")) {
						// TODO:
					} else if (detail.tag.equals("PLAC")) {
						notice.setPlace(detail.lineValue);
					} else if (detail.tag.equals("ADDR")) {
						extractAddressData(notice, detail);
					} else if (detail.tag.equals("EMAIL")) {
						notice.setEmail(detail.lineValue);
					} else if (detail.tag.equals("_VILLAGE")) {
						notice.setVillage(detail.lineValue);
					} else if (detail.tag.equals("_FARM")) {
						notice.setFarm(detail.lineValue);
					} else if (detail.tag.equals("_CROFT")) {
						notice.setCroft(detail.lineValue);
					} else if (detail.tag.equals("PHON")) {
						notice.setPrivateText(notice.getPrivateText() == null ? detail.lineValue
								: notice.getPrivateText() + ", Tel:"
										+ detail.lineValue);
					} else if (detail.tag.equals("WWW")) {
						notice.setPrivateText(notice.getPrivateText() == null ? detail.lineValue
								: notice.getPrivateText() + ", www:"
										+ detail.lineValue);
					} else if (detail.tag.equals("FAX")) {
						notice.setPrivateText(notice.getPrivateText() == null ? detail.lineValue
								: notice.getPrivateText() + ", Fax:"
										+ detail.lineValue);
					} else if (detail.tag.equals("NOTE")) {
						if (detail.lineValue.startsWith("@")
								&& detail.lineValue.indexOf('@', 1) > 1) {
							GedcomLine notirec = gedMap.get(detail.lineValue);
							if (notirec != null) {
								notice.setNoteText(notirec.lineValue);
							}
						} else {
							if (notice.getDescription() == null) {
								notice.setDescription(detail.lineValue);
							} else {
								notice.setNoteText(detail.lineValue);
							}
						}
					} else if (detail.tag.equals("SOUR")) {
						if (detail.lineValue.startsWith("@")
								&& detail.lineValue.indexOf('@', 1) > 1) {
							GedcomLine dets = gedMap.get(detail.lineValue);
							notice.setSource(extractSourceText(dets));
							notice.setSurety(extractGedcomSurety(dets));
						} else {

							String src = extractGedcomSource(detail);
							notice.setSource(src);
							notice.setSurety(extractGedcomSurety(detail));
						}
					} else if (detail.tag.equals("DATE")) {
						String[] dateParts = consumeGedcomDate(detail.lineValue);
						if (dateParts[0] != null) {
							notice.setDatePrefix(dateParts[0]);
						}
						if (dateParts[1] != null) {
							notice.setFromDate(dateParts[1]);
						}
						if (dateParts[2] != null) {
							notice.setToDate(dateParts[2]);
						}
						if (dateParts[3] != null) {
							if (notice.getDescription() == null) {
								notice.setDescription(dateParts[3]);
							} else {
								notice.setDescription(notice.getDescription()
										+ " " + dateParts[3]);
							}
						}
					} else if (detail.tag.equals("OBJE")) {

						extractMultimedia(notice, detail);
					} else if (detail.tag.equals("CHAN")) {
						// TODO:
					} else if (detail.tag.equals("CREA")) {
						// TODO:
					} else {
						unknownLine.add(detail.toString());
					}
				}

			} else {
				unknownLine.add(noti.toString());
			}

		}
		pers.setNotices(notices.toArray(new UnitNotice[0]));

		SukuData request = new SukuData();
		request.persLong = pers;
		PersonUtil u = new PersonUtil(con);

		SukuData resp = u.updatePerson(null, request);
		if (resp.resu != null) {
			throw new SukuException(resp.resu);
		}
		if (resp.resultPid > 0) {
			GedcomPidEle pide = new GedcomPidEle();
			pide.pid = resp.resultPid;
			// pide.sex = pers.getSex();
			pide.id = record.id;

			gedPid.put(record.id, pide);
			f.pid = pide.pid;
			gedFams.put(f.id, f);
		}

	}

	private void extractAddressData(UnitNotice notice, GedcomLine noti) {
		GedcomAddress address = splitAddress(noti.lineValue);
		notice.setAddress(address.address);
		notice.setPostalCode(address.postalCode);
		notice.setPostOffice(address.postOffice);
		notice.setCountry(address.country);
		notice.setEmail(address.email);
		String adr1 = null;
		String adr2 = null;
		String adr3 = null;

		for (int j = 0; j < noti.lines.size(); j++) {
			GedcomLine detail = noti.lines.get(j);
			if (detail.tag.equals("CITY")) {
				notice.setPostOffice(detail.lineValue);
			} else if (detail.tag.equals("STAE")) {
				notice.setState(detail.lineValue);
			} else if (detail.tag.equals("POST")) {
				notice.setPostalCode(detail.lineValue);
			} else if (detail.tag.equals("CTRY")) {
				notice.setCountry(detail.lineValue);
			} else if (detail.tag.equals("ADR1")) {
				adr1 = detail.lineValue;
			} else if (detail.tag.equals("ADR2")) {
				adr2 = detail.lineValue;
			} else if (detail.tag.equals("ADR3")) {
				adr3 = detail.lineValue;

			} else {
				unknownLine.add(detail.toString());
			}
		}
		StringBuilder sb = new StringBuilder();
		if (adr1 != null) {
			sb.append(adr1);
		}
		if (adr2 != null) {
			sb.append("\n");
			sb.append(adr2);
		}
		if (adr3 != null) {
			sb.append("\n");
			sb.append(adr3);
		}
		if (sb.length() > 0) {
			notice.setAddress(sb.toString());
		}

	}

	private String extractSourceText(GedcomLine dets) {
		StringBuffer sb = new StringBuffer();
		if (dets.lineValue != null) {
			sb.append(dets.lineValue);
		}
		extractSourceChild(dets, sb);
		return sb.toString();
	}

	private void extractSourceChild(GedcomLine dets, StringBuffer sb) {
		for (int i = 0; i < dets.lines.size(); i++) {
			GedcomLine detl = dets.lines.get(i);
			// String name = texts.get("REF_" + detl.tag);
			String name = repoTexts.getString("REF_" + detl.tag);
			if (name != null) {
				sb.append(name);
				sb.append(" ");

				if (detl.lineValue != null) {
					sb.append(detl.lineValue);
					sb.append(", ");
				}
			}
			extractSourceChild(detl, sb);
		}

	}

	/**
	 * @param notice
	 * @param detail
	 */
	private void extractMultimedia(UnitNotice notice, GedcomLine detail) {
		for (int k = 0; k < detail.lines.size(); k++) {
			GedcomLine item = detail.lines.get(k);

			if (item.tag.equals("FILE") && item.lineValue != null) {
				InputStream ins = null;
				if (this.isZipFile) {
					String tempFile = images.get(item.lineValue.replace('\\',
							'/'));
					if (tempFile != null) {

						try {
							ins = new FileInputStream(tempFile);
						} catch (FileNotFoundException e) {
							ins = null;
							e.printStackTrace();
						}
						if (ins != null) {
							images.remove(item.lineValue.replace('\\', '/'));
						}
					}
				} else {
					String filePath;
					if (baseFolder != null) {
						filePath = baseFolder + item.lineValue;
					} else {
						filePath = item.lineValue;
					}

					File f1 = new File(filePath);
					try {
						ins = new FileInputStream(f1);
					} catch (FileNotFoundException e) {
						ins = null;
					}
				}
				if (ins != null) {
					BufferedInputStream bstr = null;
					// System.out.println("OPEN: " +
					// openedImage);

					ByteArrayOutputStream bos = null;
					int imgSize = 0;
					try {
						bstr = new BufferedInputStream(ins);
						bos = new ByteArrayOutputStream();
						byte[] buff = new byte[2048];
						while (true) {
							int rdbytes;
							try {
								rdbytes = bstr.read(buff);
							} catch (IOException e) {
								imgSize = -1;
								break;
							}
							imgSize += rdbytes;
							if (rdbytes < 0)
								break;
							bos.write(buff, 0, rdbytes);

						}
					} finally {
						if (bstr != null) {
							try {
								bstr.close();
							} catch (IOException ignored) {
								// IOException ignored
							}
						}
					}
					int lastdir = item.lineValue.replace('\\', '/')
							.lastIndexOf('/');
					if (lastdir > 0) {
						notice.setMediaFilename(Utils
								.tidyFileName(item.lineValue
										.substring(lastdir + 1)));
					}

					if (imgSize > 0) {
						notice.setMediaData(bos.toByteArray());
					}
				} else {
					unknownLine.add(item.toString());
				}

				// else {
				//
				// if (item.lineValue != null) {
				//
				// notice.setMediaFilename(item.lineValue);
				// // MinimumImage image = new MinimumImage(item.lineValue,
				// // null);
				// // images.add(image);
				// } else {
				// unknownLine.add(item.toString());
				// }
				// }
				// try {
				// int luettu = bstr.read(buffer);
				// if (luettu == filesize) {
				// notice.setMediaData(buffer);
				// } else {
				// logger.warning("Filesize expected " +
				// filesize
				// + " read " + luettu);
				// }
				// bstr.close();
				//
				//
			} else if (item.tag.equals("FORM")) {
				// TODO:
			} else if (item.tag.equals("TITL")) {
				notice.setMediaTitle(item.lineValue);
			} else {

				unknownLine.add(item.toString());
			}
		}
	}

	private int extractGedcomSurety(GedcomLine record) {
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine line = record.lines.get(i);
			if (line.tag.equals("QUAY")) {
				if (line.lineValue == null)
					return 100;
				if (line.lineValue.equals("0"))
					return 40;
				if (line.lineValue.equals("1"))
					return 60;
				if (line.lineValue.equals("2"))
					return 80;
				if (line.lineValue.equals("3"))
					return 100;

			}
		}
		return 100;
	}

	private String[] consumeGedcomDate(String lineValue) {
		if (lineValue == null)
			return null;
		String[] dateparts = new String[4];

		int spIdx = lineValue.indexOf(" ");
		if (spIdx > 0) {
			String fp = lineValue.substring(0, spIdx);

			if (fp.equalsIgnoreCase("FROM") || fp.equalsIgnoreCase("ABT")
					|| fp.equalsIgnoreCase("CAL") || fp.equalsIgnoreCase("EST")
					|| fp.equalsIgnoreCase("TO") || fp.equalsIgnoreCase("BEF")
					|| fp.equalsIgnoreCase("AFT") || fp.equalsIgnoreCase("BET")) {
				dateparts[0] = fp;

				int i1 = 0;
				int i2 = 0;
				if (fp.equalsIgnoreCase("FROM")) {
					i1 = lineValue.indexOf("TO");
					i2 = lineValue.indexOf(" ", i1 + 2);
				} else if (fp.equalsIgnoreCase("BET")) {
					i1 = lineValue.indexOf("AND");
					i2 = lineValue.indexOf(" ", i1 + 2);
				}

				String aux = null;
				if (i1 > 0) {
					aux = toSukuDate(lineValue.substring(spIdx, i1));
				} else {
					aux = toSukuDate(lineValue.substring(spIdx));
				}
				if (aux != null) {
					dateparts[1] = aux;
				} else {
					dateparts[3] = lineValue;
				}
				if (i2 > 0) {
					aux = toSukuDate(lineValue.substring(i2));
					if (aux != null) {
						dateparts[2] = aux;
					} else {
						dateparts[3] = lineValue;
					}
				}

			} else { // assume it's date format only
				String aux = toSukuDate(lineValue);
				if (aux != null) {
					dateparts[1] = aux;
				} else {
					dateparts[3] = lineValue;
				}
			}
		} else {
			String aux = toSukuDate(lineValue);
			if (aux != null) {
				dateparts[1] = aux;
			} else {
				dateparts[3] = lineValue;
			}
		}
		return dateparts;
	}

	private String toSukuDate(String gedcomDate) {
		String[] parts = gedcomDate.split(" ");
		int dl = parts.length - 1;
		if (dl > 3 || dl < 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int year;
		try {
			year = Integer.parseInt(parts[dl]);

			if (year <= 0 || year >= 3000) {
				return null;
			}
			String aux = "0000" + year;
			sb.append(aux.substring(aux.length() - 4, aux.length()));
			dl--;
			String mm = null;
			if (dl >= 0) {
				int kk = "|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|"
						.indexOf(four(parts[dl].toUpperCase()));
				if (kk > 0) {
					kk--;
					kk /= 2;
					mm = "010203040506070809101112".substring(kk, kk + 2);
				} else {
					kk = "|TAM|HEL|MAA|HUH|TOU|KES|HEI|ELO|SYY|LOK|MAR|JOU|"
							.indexOf(four(parts[dl].toUpperCase()));
					if (kk > 0) {
						kk--;
						kk /= 2;
						mm = "010203040506070809101112".substring(kk, kk + 2);
					}

				}
			}
			if (mm != null) {
				sb.append(mm);
			} else {
				return sb.toString();
			}
			dl--;
			String dat = null;
			if (dl >= 0) {
				int dd = 0;

				dd = Integer.parseInt(parts[dl]);

				if (dd > 0 && dd <= 31) {
					dat = "000" + dd;
					dat = dat.substring(dat.length() - 2, dat.length());
				}
			}
			if (dat != null) {
				sb.append(dat);
			}
			return sb.toString();
		} catch (NumberFormatException ne) {
			// something was bad. lets try standard date conversion

			try {
				return Utils.dbDate(gedcomDate);

			} catch (SukuDateException e) {
				return null;
			}

		}
	}

	private String four(String text) {
		if (text == null)
			return "";
		if (text.length() < 4) {
			return text;
		}
		return text.substring(0, 3);
	}

	/** The submitter done. */
	boolean submitterDone = false;

	private void consumeGedcomSubmitter(GedcomLine record) throws SQLException {
		String name = null;
		GedcomAddress address = null;

		if (submitter == null || !submitter.equals(record.id) || submitterDone) {
			unknownLine.add(record.toString());
			return;
		}
		submitterDone = true;
		StringBuilder note = new StringBuilder();
		if (ownerInfo != null) {
			note.append(ownerInfo);
		}

		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine noti = record.lines.get(i);
			if (noti.tag.equals("NAME")) {
				if (noti.lineValue != null) {
					name = noti.lineValue.trim();
				}
			} else if (noti.tag.equals("ADDR")) {
				address = splitAddress(noti.lineValue);
			} else if (noti.tag.equals("WWW")) {
				// FIXME: address can be null in here
				address.www = noti.lineValue;
			} else if (noti.tag.equals("EMAIL")) {
				// FIXME: address can be null in here
				address.email = noti.lineValue;
			} else if (noti.tag.equals("NOTE")) {
				note.append(noti.lineValue);

			}
		}

		String sql = "insert into sukuvariables (owner_name,owner_info, "
				+ "owner_address,owner_postalcode,owner_postoffice,"
				+ "owner_country,owner_email,owner_webaddress,user_id) values (?,?,?,?,?,?,?,?,user) ";

		PreparedStatement pst = null;
		int lukuri;
		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, name);
			pst.setString(2, note.toString());
			if (address != null) {
				pst.setString(3, address.address);
				pst.setString(4, address.postalCode);
				pst.setString(5, address.postOffice);
				pst.setString(6, address.country);
				pst.setString(7, address.email);
				pst.setString(8, address.www);
			} else {
				pst.setString(3, null);
				pst.setString(4, null);
				pst.setString(5, null);
				pst.setString(6, null);
				pst.setString(7, null);
				pst.setString(8, null);
			}
			lukuri = pst.executeUpdate();

			if (createdDate != null) {
				String swedate = createdDate.substring(0, 4) + "-"
						+ createdDate.substring(4, 6) + "-"
						+ createdDate.substring(6);
				sql = "update sukuvariables set createdate = '" + swedate + "'";
				pst = con.prepareStatement(sql);
				lukuri = pst.executeUpdate();
				pst.close();
			}

		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ignored) {
					// SQLException ignored
				}
			}
		}

		logger.info("Sukuvariables updated " + lukuri + " lines");
	}

	private GedcomAddress splitAddress(String lineValue) {
		StringBuilder address = new StringBuilder();
		StringBuilder country = new StringBuilder();
		GedcomAddress addr = new GedcomAddress();
		if (lineValue != null) {
			String parts[] = lineValue.split("\n");
			boolean wasPo = false;
			for (int j = 0; j < parts.length; j++) {
				if (j == 0) {
					address.append(parts[j].trim());
				} else {
					if (parts[j].indexOf('@') > 0) { // possibly email
						addr.email = parts[j];
					} else if (!wasPo) {
						String posts[] = parts[j].split(" ");
						if (posts.length > 1) {
							int ponum = -1;
							try {
								ponum = Integer.parseInt(posts[0]);
							} catch (NumberFormatException ne) {
								// NumberFormatException ignored
							}
							if (ponum > 1) { // now assume beginning is
								// postalcode
								addr.postalCode = posts[0];
								addr.postOffice = parts[j].substring(posts[0]
										.length() + 1);
								wasPo = true;
							}
						}
						if (!wasPo) {
							if (address.length() > 0) {
								address.append("\n");
							}
							address.append(parts[j]);

						}
					} else {
						if (country.length() > 0) {
							country.append("\n");
						}
						country.append(parts[j]);
					}

				}
			}

			if (address.length() > 0) {
				addr.address = address.toString();
			}
			if (country.length() > 0) {
				addr.country = country.toString();
			}
		}
		return addr;
	}

	/** The submitter. */
	String submitter = null;

	/** The created date. */
	String createdDate = null;
	private String sourceSystem = null;

	private void consumeGedcomHead(GedcomLine record) {
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine notice1 = record.lines.get(i);
			if (notice1.tag.equals("SOUR")) {
				sourceSystem = notice1.lineValue;
				continue;
			} else if (notice1.tag.equals("DEST")) {
				continue;
			} else if (notice1.tag.equals("FILE")) {
				continue;
			} else if (notice1.tag.equals("SUBM")) {
				submitter = notice1.lineValue;
				continue;
			} else if (notice1.tag.equals("GEDC")) {
				continue;
			} else if (notice1.tag.equals("DATE")) {
				String[] dateParts = consumeGedcomDate(notice1.lineValue);
				if (dateParts.length > 1 && dateParts[1].length() == 8) {
					createdDate = dateParts[1];
				} else {
					unknownLine.add(notice1.toString());
				}
			} else if (notice1.tag.equals("NOTE")) {
				ownerInfo = notice1.lineValue;
			} else {
				unknownLine.add(notice1.toString());

			}

		}
	}

	private long fileIndex = 0;

	private char cnvToChar(int data, InputStream bis) throws IOException {
		char c;
		int datax;
		int datay;
		int dataz;
		switch (thisSet) {
		case Set_Utf16le:
			datax = bis.read();
			fileIndex++;
			c = (char) (datax * 256 + data);

			break;
		case Set_Utf16be:
			datax = bis.read();
			fileIndex++;
			c = (char) (data * 256 + datax);

			break;
		case Set_Utf8:
			if ((data & 0x80) == 0) {
				c = (char) data;
				break;
			}
			datax = bis.read();
			fileIndex++;
			if ((data & 0x20) == 0) {
				datax &= 0x3F;
				data &= 0x1F;
				data = data << 6;
				c = (char) (data | datax);
				break;
			}
			datay = bis.read();
			fileIndex++;
			if ((data & 0x10) == 0) {
				datay &= 0x3F;
				datax &= 0x3F;
				data &= 0x0F;
				datax = datax << 6;
				data = data << 10;
				c = (char) (data | datax | datay);
			}
			dataz = bis.read();
			fileIndex++;
			dataz &= 0x3F;
			datay &= 0x3F;
			datax &= 0x3F;
			data &= 0x07;
			datay = datay << 6;
			datax = datax << 12;
			data = data << 18;
			c = (char) (data | datax | datay | dataz);
			break;

		case Set_Ansel:

			if ((data & 0x80) == 0) {
				c = (char) data;
				break;
			}
			if (data < 0xE0) {
				if (data == 0xA4)
					c = 'Þ';
				else if (data == 0xA2)
					c = 'Ø';
				else if (data == 0xA5)
					c = 'Æ';
				else if (data == 0xA6)
					c = 'Œ';
				else if (data == 0xAA)
					c = '®';

				else if (data == 0xAB)
					c = '±';
				else if (data == 0xB2)
					c = 'ø';
				else if (data == 0xB4)
					c = 'þ';
				else if (data == 0xB5)
					c = 'æ';
				else if (data == 0xB6)
					c = 'œ';
				else if (data == 0xB9)
					c = '£';
				else if (data == 0xBA)
					c = 'ð';
				else if (data == 0xC3)
					c = '©';
				else if (data == 0xC5)
					c = '¿';
				else if (data == 0xC6)
					c = '¡';
				else if (data == 0xCF)
					c = 'ß';
				else
					c = '?';

			} else {
				datax = bis.read();
				fileIndex++;
				switch (data) {
				case 0xE1: // grave accent
					if (datax == 'a')
						c = 'à';
					else if (datax == 'A')
						c = 'À';
					else if (datax == 'e')
						c = 'è';
					else if (datax == 'E')
						c = 'È';
					else if (datax == 'i')
						c = 'ì';
					else if (datax == 'I')
						c = 'ì';
					else if (datax == 'o')
						c = 'ò';
					else if (datax == 'O')
						c = 'Ò';
					else if (datax == 'u')
						c = 'ù';
					else if (datax == 'U')
						c = 'Ù';
					else
						c = (char) datax;
					break;
				case 0xE2: // acute accent
					if (datax == 'a')
						c = 'á';
					else if (datax == 'A')
						c = 'Á';
					else if (datax == 'e')
						c = 'é';
					else if (datax == 'E')
						c = 'É';
					else if (datax == 'i')
						c = 'í';
					else if (datax == 'I')
						c = 'Í';
					else if (datax == 'o')
						c = 'ó';
					else if (datax == 'O')
						c = 'Ó';
					else if (datax == 'u')
						c = 'ú';
					else if (datax == 'U')
						c = 'Ú';
					else
						c = (char) datax;
					break;
				case 0xE3: // circumflex accent
					if (datax == 'a')
						c = 'â';
					else if (datax == 'A')
						c = 'Â';
					else if (datax == 'e')
						c = 'ê';
					else if (datax == 'E')
						c = 'Ê';
					else if (datax == 'i')
						c = 'î';
					else if (datax == 'I')
						c = 'Î';
					else if (datax == 'o')
						c = 'ô';
					else if (datax == 'O')
						c = 'Ô';
					else if (datax == 'u')
						c = 'û';
					else if (datax == 'U')
						c = 'Û';
					else
						c = (char) datax;
					break;
				case 0xE4: // tilde
					if (datax == 'a')
						c = 'ã';
					else if (datax == 'A')
						c = 'Ã';
					else if (datax == 'n')
						c = 'ñ';
					else if (datax == 'N')
						c = 'Ñ';
					else if (datax == 'o')
						c = 'õ';
					else if (datax == 'O')
						c = 'Õ';
					else
						c = (char) datax;
					break;
				case 0xF0: // cedilla
					if (datax == 'c')
						c = 'ç';
					else if (datax == 'C')
						c = 'Ç';
					else if (datax == 'n')
						c = 'ñ';
					else if (datax == 'N')
						c = 'Ñ';
					else if (datax == 'o')
						c = 'õ';
					else if (datax == 'O')
						c = 'Õ';
					else
						c = (char) datax;
					break;

				case 0xE8: // umlaut
					if (datax == 'a')
						c = 'ä';
					else if (datax == 'A')
						c = 'Ä';
					else if (datax == 'o')
						c = 'ö';
					else if (datax == 'O')
						c = 'Ö';
					else if (datax == 'u')
						c = 'ü';
					else if (datax == 'U')
						c = 'Ü';
					else if (datax == 'e')
						c = 'ë';
					else if (datax == 'E')
						c = 'Ë';
					else if (datax == 'i')
						c = 'ï';
					else if (datax == 'I')
						c = 'Ï';
					else
						c = (char) datax;
					break;
				case 0xEA: // ringabove
					if (datax == 'a')
						c = 'å';
					else if (datax == 'A')
						c = 'Å';
					else
						c = (char) datax;
					break;

				default:
					c = (char) datax;
					break;
				}
			} // end of ansel
			break;
		default:
			c = (char) data;
			break;
		}
		return c;

	}

	/** The key counter. */
	int keyCounter = 0;

	// class GedcomRecord {
	// Vector<GedcomLine> lines = new Vector<GedcomLine>();
	//
	// void add(GedcomLine line) {
	//
	// lines.add(line);
	// }
	//
	// public String toString() {
	// StringBuilder sb = new StringBuilder();
	// for (int i = 0; i < lines.size(); i++) {
	// sb.append(lines.get(i));
	// sb.append("\n");
	// }
	// return sb.toString();
	//
	// }
	//
	// }

	private void setRunnerValue(String juttu) throws SukuException {
		if (runner != null) {
			if (this.runner.setRunnerValue(juttu)) {
				throw new SukuException(
						Resurses.getString("EXECUTION_CANCELLED"));
			}
		}
	}

	/**
	 * The Class GedcomLine.
	 */
	class GedcomLine {

		/** The level. */
		int level = -1;

		/** The tag. */
		String tag = null;

		/** The id. */
		String id = null;

		/** The line value. */
		String lineValue = "";

		/** The parent. */
		GedcomLine parent = null;

		/** The lines. */
		Vector<GedcomLine> lines = new Vector<GedcomLine>();

		/**
		 * Adds the.
		 * 
		 * @param line
		 *            the line
		 */
		void add(GedcomLine line) {
			if (line.level == level + 1) {
				if (line.tag.equals("CONT")) {
					lineValue += "\n" + line.lineValue;
				} else if (line.tag.equals("CONC")) {
					lineValue += line.lineValue;
				} else {
					lines.add(line);
					line.parent = this;
				}
			} else {
				int last = lines.size() - 1;
				if (last < 0) {
					unknownLine.add(line.toString());
				} else {
					GedcomLine subline = lines.get(last);
					subline.add(line);
					line.parent = subline;

				}
			}
		}

		/** The key. */
		String key = null;

		/**
		 * Gets the key.
		 * 
		 * @return the key
		 */
		String getKey() {
			if (id != null) {
				return id;
			}
			if (key != null) {
				return key;
			}
			if (tag.equals("HEAD") || tag.equals("TRLR")) {
				key = tag;
				return key;
			}
			keyCounter++;
			key = "X" + keyCounter + "X";
			return key;
		}

		/**
		 * Instantiates a new gedcom line.
		 * 
		 * @param level
		 *            the level
		 */
		GedcomLine(int level) {
			this.level = level;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return toString(true);
		}

		/**
		 * To string.
		 * 
		 * @param withLevels
		 *            the with levels
		 * @return the string
		 */
		public String toString(boolean withLevels) {
			StringBuilder sb = new StringBuilder();
			if (level > 0) {
				ArrayList<String> stack = new ArrayList<String>();
				GedcomLine pareLine = this.parent;
				while (pareLine != null) {
					if (pareLine.id != null) {
						stack.add("" + pareLine.level + " " + pareLine.id + " "
								+ pareLine.tag);
					} else {
						if (pareLine.level > 0) {
							stack.add(pareLine.tag);
						} else {
							stack.add("" + pareLine.level + " " + pareLine.tag);
						}
					}

					pareLine = pareLine.parent;

				}
				for (int i = stack.size() - 1; i >= 0; i--) {
					sb.append(stack.get(i));
					sb.append("|");
				}
			}
			if (level == 0) {
				sb.append(level);
				sb.append(" ");
			}
			if (id != null) {
				sb.append(id);
				sb.append(" ");
			}
			sb.append(tag);
			if (lineValue != null) {
				sb.append(" ");
				sb.append(lineValue);
			}
			sb.append("\n");
			if (withLevels) {
				for (int i = 0; i < lines.size(); i++) {
					sb.append(lines.get(i).toString());
				}
			}
			return sb.toString();
		}
	}

	/**
	 * The Class GedcomAddress.
	 */
	class GedcomAddress {

		/** The address. */
		String address = null;

		/** The postal code. */
		String postalCode = null;

		/** The post office. */
		String postOffice = null;

		/** The country. */
		String country = null;

		/** The email. */
		String email = null;

		/** The www. */
		String www = null;
	}

	/**
	 * The Class GedcomPidEle.
	 */
	class GedcomPidEle {

		/** The id. */
		public String id;

		/** The pid. */
		int pid = 0;

	}

	/**
	 * The Class GedcomFams.
	 */
	class GedcomFams {

		/** The pid. */
		int pid;

		/** The id. */
		String id;

		/** The fams. */
		Vector<GedcomLine> fams = new Vector<GedcomLine>();
	}

}
