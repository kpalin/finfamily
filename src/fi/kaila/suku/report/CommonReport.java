package fi.kaila.suku.report;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.report.style.ImageText;
import fi.kaila.suku.report.style.MainPersonText;
import fi.kaila.suku.report.style.NameIndexText;
import fi.kaila.suku.report.style.SubPersonText;
import fi.kaila.suku.report.style.TableHeaderText;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Roman;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * <h1>Report creator</h1>
 * 
 * <p>
 * Reports architecture consist of
 * </p>
 * 
 * <ul>
 * <li>Report logical creator. Example the {@link DescendantReport}</li>
 * <li>Report Interface {@link ReportInterface}</li>
 * <li>Actual output creator. Example {@link JavaReport}</li>
 * </ul>
 * 
 * 
 * Common routines used by report generators
 * 
 * @author Kalle
 * 
 */
public abstract class CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/** The caller. */
	protected ReportWorkerDialog caller;

	/** The types table. */
	protected SukuTypesTable typesTable;

	/** The tables. */
	protected Vector<ReportUnit> tables = new Vector<ReportUnit>();
	/** The tables in a hashmap. */
	protected HashMap<Long, ReportUnit> tabMap = new HashMap<Long, ReportUnit>();
	/** The repo writer. */
	protected ReportInterface repoWriter;

	/** The person references. */
	protected HashMap<Integer, PersonInTables> personReferences = null;
	/** The person tables. */
	protected HashMap<Integer, ReportUnit> personTables = null;

	/** The text references. */
	protected HashMap<String, PersonInTables> textReferences = null;

	private final HashMap<Integer, Integer> mapper = new HashMap<Integer, Integer>();

	private final HashMap<String, PlaceInTables> places = new HashMap<String, PlaceInTables>();

	private final LinkedHashMap<String, Integer> refs = new LinkedHashMap<String, Integer>();

	private final Vector<ImageNotice> imgNotices = new Vector<ImageNotice>();
	private int referencePid = 0;

	private int imageNumber = 0;

	/**
	 * Gets the tables.
	 * 
	 * @return vector of tables
	 */
	public Vector<ReportUnit> getTables() {
		return tables;
	}

	/**
	 * Execute the report Implemented by the derived class.
	 * 
	 * @throws SukuException
	 *             the suku exception
	 */
	public abstract void executeReport() throws SukuException;

	/**
	 * Gets the person references.
	 * 
	 * @return Vector with references
	 */
	public Vector<PersonInTables> getPersonReferences() {

		Vector<PersonInTables> vv = new Vector<PersonInTables>();

		Set<Map.Entry<Integer, PersonInTables>> entriesx = personReferences
				.entrySet();
		Iterator<Map.Entry<Integer, PersonInTables>> eex = entriesx.iterator();
		while (eex.hasNext()) {
			Map.Entry<Integer, PersonInTables> entrx = eex.next();
			PersonInTables pit = entrx.getValue();
			vv.add(pit);
		}

		Set<Map.Entry<String, PersonInTables>> entriesy = textReferences
				.entrySet();
		Iterator<Map.Entry<String, PersonInTables>> eey = entriesy.iterator();
		while (eey.hasNext()) {
			Map.Entry<String, PersonInTables> entry = eey.next();
			PersonInTables pit = entry.getValue();

			String[] parts = entry.getKey().split(",");

			if (parts.length == 2) {
				pit.givenName = parts[1];
				pit.surName = parts[0];
				vv.add(pit);
			} else if (parts.length == 1) {
				pit.surName = parts[0];
				vv.add(pit);
			}
		}

		return vv;
	}

	/**
	 * finds the tables by their owners into personTables
	 */
	public void initPersonTables() {
		personTables = new HashMap<Integer, ReportUnit>();

		for (ReportUnit ru : tables) {
			personTables.put(ru.getPid(), ru);
		}

	}

	/**
	 * Gets the place references.
	 * 
	 * @return the place references
	 */
	public PlaceInTables[] getPlaceReferences() {

		ArrayList<PlaceInTables> vv = new ArrayList<PlaceInTables>();

		Set<Map.Entry<String, PlaceInTables>> entriesx = places.entrySet();
		Iterator<Map.Entry<String, PlaceInTables>> eex = entriesx.iterator();
		while (eex.hasNext()) {
			Map.Entry<String, PlaceInTables> entrx = eex.next();
			PlaceInTables pit = entrx.getValue();
			vv.add(pit);
		}

		PlaceInTables[] pits = vv.toArray(new PlaceInTables[0]);
		Arrays.sort(pits);

		return pits;

	}

	/**
	 * Gets the source list.
	 * 
	 * @return the source list
	 */
	public String[] getSourceList() {

		ArrayList<String> vv = new ArrayList<String>();

		Set<Map.Entry<String, Integer>> entriesx = refs.entrySet();
		Iterator<Map.Entry<String, Integer>> eex = entriesx.iterator();
		while (eex.hasNext()) {
			Map.Entry<String, Integer> entrx = eex.next();
			String src = entrx.getKey();

			vv.add(src);
		}

		return vv.toArray(new String[0]);

	}

	/**
	 * access to report writer.
	 * 
	 * @return the report writer
	 */
	public ReportInterface getWriter() {
		return repoWriter;
	}

	/**
	 * Prints the images.
	 * 
	 * @throws SukuException
	 */
	public void printImages() throws SukuException {
		if (imgNotices.size() > 0) {
			BodyText bt = new TableHeaderText();
			bt.addText("\n");
			repoWriter.addText(bt);
			bt.addText(Resurses.getReportString("INDEX_IMAGES"));
			bt.addText("\n");
			repoWriter.addText(bt);
			bt = new MainPersonText();
			for (int i = 0; i < imgNotices.size(); i++) {
				// for (ImageNotice inoti : imgNotices) {
				ImageNotice inoti = imgNotices.get(i);
				UnitNotice nn = inoti.nn;

				float prose = (i * 100f) / imgNotices.size();
				caller.setRunnerValue("" + (int) prose + ";"
						+ nn.getMediaFilename());

				if (caller.showImages()) {
					ImageText imagetx = new ImageText();
					BufferedImage img = null;
					try {
						img = nn.getMediaImage();
					} catch (IOException e) {
						logger.log(Level.WARNING, "printImages", e);

					}

					if (img != null) {
						double imh = img.getHeight();
						double imw = img.getWidth();
						double newh = 300;
						double neww = 300;

						if (imh <= newh) {
							if (imw <= neww) {
								newh = imh;
								neww = imw;

							} else {
								newh = imh * (neww / imw);
								neww = imw;
							}
						} else {
							neww = imw * (newh / imh);
						}

						/*
						 * String imgTitle = ""; if (caller.isNumberingImages())
						 * { imgTitle = Resurses .getReportString("INDEX_IMAGE")
						 * + " " + imageNumber + ". "; } if (nn.getMediaTitle()
						 * != null) { imgTitle += nn.getMediaTitle(); } String
						 * xxx = "0000" + imageNumber; String imgNamePrefix =
						 * xxx.substring(xxx .length() - 4) + "_";
						 * imagetx.setImage( imgs, nn.getMediaData(),
						 * img.getWidth(), img.getHeight(), imgNamePrefix +
						 * nn.getMediaFilename(), imgTitle, nn.getTag());
						 */

						String imgTitle = "";

						if (nn.getMediaTitle() != null) {
							String titl = trim(nn.getMediaTitle());
							imgTitle += titl + ".";

						}
						String xxx = "0000" + inoti.imgNumber;
						String imgNamePrefix = xxx.substring(xxx.length() - 4)
								+ "_";

						StringBuilder sm = new StringBuilder();
						sm.append(Resurses.getReportString("INDEX_IMAGE"));
						sm.append(" ");
						sm.append(inoti.imgNumber);
						sm.append(" (");
						sm.append(Resurses.getReportString("TABLE")
								.toLowerCase());
						sm.append(" ");
						sm.append(inoti.tabNo);
						sm.append("). ");
						if (nn.getMediaTitle() != null) {
							sm.append(imgTitle);
						}

						imagetx.setImage(img, nn.getMediaData(),
								img.getWidth(), img.getHeight(), imgNamePrefix
										+ nn.getMediaFilename(), sm.toString(),
								nn.getTag());
						imagetx.addText("");

					}

					repoWriter.addText(imagetx);
					if (nn.getNoteText() != null) {
						printText(bt, nn.getNoteText(), null);
					}

				} else {
					bt.addText(Resurses.getReportString("INDEX_IMAGE"), true,
							false);
					bt.addText(" " + inoti.imgNumber + " ", true, false);
					bt.addText(Resurses.getReportString("TABLE").toLowerCase(),
							true, false);
					bt.addText(" " + inoti.tabNo, true, false);
					bt.addText(". ", true, false);
					if (nn.getMediaTitle() != null) {
						bt.addText(nn.getMediaTitle(), true, false);
						bt.addText(". ", true, false);
					}

					repoWriter.addText(bt);
					if (nn.getNoteText() != null) {
						printText(bt, nn.getNoteText(), null);
					}
				}
				// if (img != null) {
				//
				// imagetx.setImage(img, nn.getMediaData(), img.getWidth(),
				// img.getHeight(), nn.getMediaFilename(), nn
				// .getMediaTitle(), nn.getTag());
				// imagetx.addText("");
				// }

				repoWriter.addText(bt);

			}
		}
	}

	/**
	 * Print name index in word document (or html)
	 * 
	 * @throws SukuException
	 */
	public void printNameIndex() throws SukuException {

		if (caller.isCreateNameIndexSet()) {
			int tableOffset = caller.getDescendantPane().getStartTable();
			if (tableOffset > 0) {
				tableOffset--;
			}
			Vector<PersonInTables> vv = getPersonReferences();

			float runnervalue = 0;
			float mapsize = vv.size();
			HashMap<String, String> nms = new HashMap<String, String>();
			Vector<Integer> pidlist = new Vector<Integer>();
			for (int j = 0; j < mapsize; j++) {
				PersonInTables pit = vv.get(j);
				// vv.add(pit);

				if (pit.shortPerson == null) {

					SukuData resp = Suku.kontroller.getSukuData("cmd=person",
							"mode=short", "pid=" + pit.pid);

					if (resp.pers != null) {

						pit.shortPerson = resp.pers[0];
						if (pit.shortPerson.getPrivacy() == null) {
							pidlist.add(pit.pid);
							nms.clear();
							String testName = nv(pit.shortPerson.getPrefix())
									+ "|" + nv(pit.shortPerson.getSurname())
									+ "|" + nv(pit.shortPerson.getGivenname())
									+ "|" + nv(pit.shortPerson.getPatronym())
									+ "|" + nv(pit.shortPerson.getPostfix());
							nms.put(testName, "1");
							for (int i = 1; i < pit.shortPerson.getNameCount(); i++) {

								PersonInTables pitt = new PersonInTables(
										pit.shortPerson.getPid());
								pitt.asChildren = pit.asChildren;
								pitt.references = pit.references;
								Long[] aa = pit.getOwnerArray();
								for (Long a : aa) {
									pitt.addOwner(a);
								}

								pitt.asParents = pit.asParents;
								PersonShortData p = pit.shortPerson;
								PersonShortData alias = new PersonShortData(
										p.getPid(), p.getGivenname(i),
										p.getPatronym(i), p.getPrefix(i),
										p.getSurname(i), p.getPostfix(i),
										p.getBirtDate(), p.getDeatDate());
								pitt.shortPerson = alias;
								testName = nv(pitt.shortPerson.getPrefix())
										+ "|"
										+ nv(pitt.shortPerson.getSurname())
										+ "|"
										+ nv(pitt.shortPerson.getGivenname())
										+ "|"
										+ nv(pitt.shortPerson.getPatronym())
										+ "|"
										+ nv(pitt.shortPerson.getPostfix());

								String oldName = nms.put(testName, "1");
								if (oldName == null) {

									vv.add(pitt);
								}
							}
							nms.clear();
						}
					}
					float prose = (runnervalue * 100f) / mapsize;
					if (prose > 100)
						prose = 100;
					caller.setRunnerValue("" + (int) prose + ";"
							+ pit.shortPerson.getAlfaName());
					runnervalue++;
				}
			}
			if (caller.getStorageVid() >= 0) {
				SukuData request = new SukuData();
				request.pidArray = new int[pidlist.size()];
				for (int i = 0; i < pidlist.size(); i++) {
					request.pidArray[i] = pidlist.get(i);
				}
				Suku.kontroller.getSukuData(request, "cmd=view", "action=add",
						"key=pidarray", "viewid=" + caller.getStorageVid(),
						"empty=" + caller.emptyStorageView());

			}

			PersonInTables[] pits = vv.toArray(new PersonInTables[0]);
			Arrays.sort(pits);
			BodyText bt = new TableHeaderText();
			bt.addText("\n");
			repoWriter.addText(bt);
			bt.addText(Resurses.getReportString("INDEX_NAMEINDEX"));
			bt.addText("\n");
			repoWriter.addText(bt);
			bt = new NameIndexText();

			String previousSurname = null;

			for (int i = 0; i < pits.length; i++) {

				PersonInTables pit = pits[i];
				if (pit.shortPerson.getPrivacy() != null) {
					if (pit.shortPerson.getPrivacy().equals("F")) {
						PersonShortData nn = new PersonShortData(
								pit.shortPerson.getPid(),
								typesTable.getTextValue("REPORT_NOMEN_NESCIO"),
								null, null, null, null, null, null);
						pit.shortPerson = nn;
					} else {
						continue;
					}
				}
				String mefe = pit.getReferences(0, false, false, true,
						tableOffset);

				StringBuilder tstr = new StringBuilder();
				if (pit.shortPerson.getPrefix() != null) {
					tstr.append(pit.shortPerson.getPrefix());
					tstr.append(" ");
				}
				if (pit.shortPerson.getSurname() != null) {
					tstr.append(pit.shortPerson.getSurname());
				}
				String surname = tstr.toString();
				if (!Utils.nv(previousSurname).equalsIgnoreCase(surname)) {
					bt.addText(surname, true, false);
					repoWriter.addText(bt);
					previousSurname = surname;
				}

				StringBuilder tstg = new StringBuilder();
				if (pit.shortPerson.getGivenname() != null) {
					tstg.append(pit.shortPerson.getGivenname());
				}
				if (pit.shortPerson.getPatronym() != null) {
					if (tstg.length() > 0) {
						tstg.append(" ");
					}
					tstg.append(pit.shortPerson.getPatronym());
				}

				if (pit.shortPerson.getPostfix() != null) {
					tstg.append(" ");
					tstg.append(pit.shortPerson.getPostfix());
				}
				bt.addText("  ");
				if (tstg.length() == 0) {
					bt.addText("  ");
				} else {
					printGivenname(bt, tstg.toString(), false);
				}
				//
				// let's add living years here
				//
				if (caller.showIndexYears()) {
					StringBuilder yrs = new StringBuilder();
					if (pit.shortPerson.getBirtYear() > 0
							|| pit.shortPerson.getDeatYear() > 0) {
						yrs.append(" (");
						if (pit.shortPerson.getBirtYear() > 0) {
							yrs.append(pit.shortPerson.getBirtYear());
						}
						if (pit.shortPerson.getDeatYear() > 0) {
							yrs.append("-");
							yrs.append(pit.shortPerson.getDeatYear());
						}

						yrs.append(")");
						bt.addText(yrs.toString());
					}
				}

				String kefe = pit.getReferences(0, true, false, false,
						tableOffset);
				String cefe = pit.getReferences(0, false, true, false,
						tableOffset);
				String refe = kefe;

				if (pit.getOwnerString(tableOffset).isEmpty()) {

					if (refe.isEmpty()) {
						refe = cefe;
					} else {
						if (!cefe.isEmpty()) {
							refe += "," + cefe;
						}
					}
				}

				if (!mefe.isEmpty()) {
					if (!refe.isEmpty()) {
						refe += "," + mefe;
					} else {
						refe = mefe;
					}
				}

				StringBuilder tt = new StringBuilder();
				if (!kefe.isEmpty()) {
					tt.append(kefe);
				} else if (!pit.getOwnerString(tableOffset).isEmpty()) {
					tt.append(pit.getOwnerString(tableOffset));
				}
				if (tt.length() == 0 && !cefe.isEmpty()) {
					tt.append(cefe);
				}
				if (!mefe.isEmpty()) {
					if (tt.length() > 0) {
						tt.append(", ");
					}
					tt.append(mefe);
				}
				if (tt.length() > 0) {
					bt.addText("\t");

					bt.addLink(tt.toString(), false, false, false,
							tt.toString());
					repoWriter.addText(bt);
					// bt.addText(tt.toString());
				} else {

					repoWriter.addText(bt);
				}
			}

		}
		printPlaceIndex();
		printSourceReference();
	}

	private void printPlaceIndex() throws SukuException {

		if (caller.isCreatePlaceIndexSet()) {
			int tableOffset = caller.getDescendantPane().getStartTable();
			if (tableOffset > 0) {
				tableOffset--;
			}
			PlaceInTables[] places = getPlaceReferences();
			BodyText bt = null;
			if (places.length > 0) {
				bt = new TableHeaderText();
				bt.addText("\n");
				repoWriter.addText(bt);
				bt.addText(Resurses.getReportString("INDEX_PLACES"));
				bt.addText("\n");
				repoWriter.addText(bt);
				bt = new NameIndexText();

				for (int i = 0; i < places.length; i++) {
					PlaceInTables pit = places[i];
					bt.addText(pit.getPlace());
					bt.addText("\t");
					bt.addText(pit.toString());
					repoWriter.addText(bt);

				}
			}
		}
	}

	private void printSourceReference() throws SukuException {
		if (caller.getSourceFormat().equals(ReportWorkerDialog.SET_AFT)) {
			int tableOffset = caller.getDescendantPane().getStartTable();
			if (tableOffset > 0) {
				tableOffset--;
			}
			BodyText bt = null;
			String[] refs = getSourceList();
			if (refs.length > 0) {
				bt = new TableHeaderText();
				bt.addText("\n");
				repoWriter.addText(bt);
				bt.addText(Resurses.getReportString("SOURCE_INDEXES"));
				bt.addText("\n");
				repoWriter.addText(bt);
				bt = new NameIndexText();
				int row = 1;
				for (int i = 0; i < refs.length; i++) {
					bt.addText("" + row);
					bt.addText("\t");
					bt.addText(refs[i]);

					repoWriter.addText(bt);
					row++;
				}
			}

		}
	}

	/**
	 * Instantiates a new common report.
	 * 
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param repoWriter
	 *            the repo writer
	 */
	protected CommonReport(ReportWorkerDialog caller,
			SukuTypesTable typesTable, ReportInterface repoWriter) {
		this.caller = caller;
		this.typesTable = typesTable;
		this.repoWriter = repoWriter;

	}

	/**
	 * @param tabMember
	 * @param bt
	 * @param spouseMember
	 * @param spouNum
	 * @return
	 * @throws SukuException
	 */
	protected void printSpouse(long tabNo, int memberPid, BodyText bt,
			ReportTableMember spouseMember, int spouNum, int tableOffset)
			throws SukuException {
		UnitNotice[] notices;
		String fromTable;
		SukuData sdata;
		PersonInTables refs;
		sdata = caller.getKontroller().getSukuData("cmd=person",
				"pid=" + spouseMember.getPid());
		String tmp;
		if ("M".equals(sdata.persLong.getSex())) {
			tmp = "HUSB";
		} else {
			tmp = "WIFE";
		}

		if (sdata.persLong.getPrivacy() != null
				&& sdata.persLong.getPrivacy().equals("P")) {
			return;
		}

		String spouType = typesTable.getTextValue(tmp);
		RelationNotice rnn[] = null;
		if (sdata.relations != null) {
			RelationNotice rnMarr = null;

			for (int i = 0; i < sdata.relations.length; i++) {
				if (sdata.relations[i].getRelative() == memberPid) {
					if (sdata.persLong.getPrivacy() == null) {
						if (sdata.relations[i].getNotices() != null) {
							rnn = sdata.relations[i].getNotices();

							for (RelationNotice rr : rnn) {
								if (rr.getTag().equals("MARR")) {
									rnMarr = rr;
									break;
								}
							}
							if (rnMarr != null) {
								spouType = printRelationNotice(rnMarr,
										spouType, spouNum, true);
								break;
							}

						}

					}
				}
			}
			if (rnMarr == null) {
				if (spouNum > 0) {
					spouType += " " + spouNum + ":o";
				}
			}

			bt.addText("- ");
			bt.addText(spouType);
			bt.addText(" ");

			fromTable = "";
			int typesColumn = 2;
			boolean isRelated = false;
			refs = personReferences.get(spouseMember.getPid());
			if (refs != null) {

				typesColumn = refs.getTypesColumn(tabNo, true, true, false);

				String asChild = refs.getReferences(tabNo, false, true, false,
						tableOffset);
				if (!asChild.isEmpty()) {
					isRelated = true;
					if (refs.getMyTable() > 0) {
						typesColumn = 3;
					}
					fromTable = refs.getReferences(tabNo, true, false, false,
							tableOffset);
					if (fromTable.isEmpty()) {
						fromTable = "" + asChild;
					}

				} else {
					fromTable = refs.getReferences(tabNo, true, false, false,
							tableOffset);
				}

			}

			notices = sdata.persLong.getNotices();
			if (sdata.persLong.getPrivacy() == null) {

				printName(bt, sdata.persLong, typesColumn);
				printNotices(bt, notices, typesColumn, tabNo);
			} else {
				printNameNn(bt);
			}
			if (rnn != null) {
				boolean skipMarr = true;
				for (int i = 0; i < rnn.length; i++) {
					RelationNotice rn = rnn[i];
					if (skipMarr && rn.getTag().equals("MARR")) {
						skipMarr = false;
					} else {
						spouType = printRelationNotice(rn, null, 0, false);
						if (spouType.length() > 0) {
							// bt.addText(" ");
							bt.addText(spouType);
							bt.addText(". ");
						}
					}
				}
			}

			if (refs != null) {

				if (fromTable.length() > 0) {
					bt.addLink(
							typesTable.getTextValue(isRelated ? "ISFAMILY"
									: "ALSO") + " " + fromTable + ". ", true,
							false, false, "" + fromTable);
				}
			}
		}
		if (bt.getCount() > 0) {
			repoWriter.addText(bt);
		}

		for (int i = 0; i < spouseMember.getSubCount(); i++) {
			bt = new SubPersonText();
			String subDad = spouseMember.getSubDadMom(i);
			bt.addText(subDad + " ");
			SukuData sub = caller.getKontroller().getSukuData("cmd=person",
					"pid=" + spouseMember.getSubPid(i));
			notices = sub.persLong.getNotices();
			StringBuilder fromsTable = new StringBuilder();
			boolean referenceFoundEarlier = false;
			refs = personReferences.get(spouseMember.getSubPid(i));
			if (refs != null) {
				fromTable = refs.getReferences(tabNo, true, true, true,
						tableOffset);

				String[] froms = fromTable.split(",");

				for (int j = 0; j < froms.length; j++) {
					long refTab = 0;
					try {
						refTab = Long.parseLong(froms[j]);
					} catch (NumberFormatException ne) {
						refTab = 0;
					}
					if (refTab > 0 && refTab < tabNo) {
						referenceFoundEarlier = true;
					}

					if (j > 0) {
						fromsTable.append(",");
					}
					fromsTable.append(froms[j]);
				}
			}

			if (sub.persLong.getPrivacy() == null) {

				printName(bt, sub.persLong, 4);
				int noticeCol = 4;
				if (referenceFoundEarlier) {
					noticeCol = 3;
				}
				printNotices(bt, notices, noticeCol, tabNo);
			} else {
				printNameNn(bt);
			}
			fromTable = "";

			if (fromsTable.length() > 0) {
				bt.addLink(
						typesTable.getTextValue("ALSO") + " "
								+ fromsTable.toString() + ". ", true, false,
						false, fromsTable.toString());
			}

			repoWriter.addText(bt);

		}

	}

	protected int getTypeColumn(int pid) {
		Integer foundMe = mapper.get(pid);
		if (foundMe != null)
			return 3;
		mapper.put(Integer.valueOf(pid), Integer.valueOf(pid));
		return 2;
	}

	/**
	 * @param bt
	 * @param ppid
	 * @return the result in a SukuData object
	 * @throws SukuException
	 */
	public SukuData printParentReference(BodyText bt, int ppid)
			throws SukuException {
		SukuData ppdata = caller.getKontroller().getSukuData("cmd=person",
				"pid=" + ppid, "mode=short", "lang=" + Resurses.getLanguage());

		if (ppdata.pers != null && ppdata.pers.length > 0) {

			if (ppdata.pers[0].getPrivacy() != null) {
				ppdata.pers[0] = new PersonShortData(ppdata.pers[0].getPid(),
						typesTable.getTextValue("REPORT_NOMEN_NESCIO"), null,
						null, null, null, null, null);

			}
			if (ppdata.pers[0].getGivenname() != null) {
				printGivenname(bt, ppdata.pers[0].getGivenname(), false);
				if (ppdata.pers[0].getPrefix() != null
						|| ppdata.pers[0].getSurname() != null
						|| ppdata.pers[0].getPostfix() != null) {
					bt.addText(" ");
				}
				if (ppdata.pers[0].getPrefix() != null) {
					bt.addText(ppdata.pers[0].getPrefix() + " ");
				}
				if (ppdata.pers[0].getSurname() != null) {
					bt.addText(ppdata.pers[0].getSurname());
				}
				if (ppdata.pers[0].getPostfix() != null) {
					bt.addText(" " + ppdata.pers[0].getPostfix());
				}

			}
		}
		return ppdata;
	}

	protected void printNameNn(BodyText bt) {

		UnitNotice[] notices = new UnitNotice[1];
		notices[0] = new UnitNotice("NAME");
		notices[0].setGivenname(typesTable.getTextValue("REPORT_NOMEN_NESCIO"));

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];

			if (nn.getTag().equals("NAME")) {

				printGivenname(bt, nn.getGivenname(), true);

			}

		}

		bt.addText(". ");
	}

	protected String toPrintTable(long tableNo) {
		return toPrintTable(tableNo, false);
	}

	protected String toPrintTable(long tableNo, boolean showGeneration) {
		String order = caller.getAncestorPane().getNumberingFormat()
				.getSelection().getActionCommand();

		long hagerSize = 1;
		int gen = 0;
		while (tableNo >= hagerSize) {
			gen++;
			hagerSize *= 2;
		}
		hagerSize /= 2;
		gen--;

		if (!order.equals("HAGER")) {

			return "" + tableNo;

		}
		if (tableNo == 1) {
			return "1";
		}
		if (!showGeneration) {
			return "" + (1 + tableNo - hagerSize);
		}

		return Roman.int2roman(gen) + "." + (1 + tableNo - hagerSize);

	}

	/**
	 * @param ftab
	 * @param bt
	 * @return
	 */
	protected void addParentReference(ReportUnit ftab, BodyText bt) {
		PersonInTables ref;
		ref = personReferences.get(ftab.getPid());
		if (ref != null) {
			StringBuilder sb = new StringBuilder();
			int pareCount = 0;
			if (ref.asChildren.size() > 0) {
				if (sb.length() > 0) {
					sb.append(",");
				}

			}
			for (int i = 0; i < ref.asChildren.size(); i++) {
				if (i > 0) {
					sb.append(",");
				}
				pareCount++;
				long pareTab = ref.asChildren.get(i);
				String partext = typesTable
						.getTextValue((pareTab % 2 == 0) ? "Father" : "Mother");
				if (caller.getAncestorPane().getAllBranches()) {
					long fftab = ftab.getTableNo() * 2;
					if (pareTab % 2 != 0) {
						fftab++;
					}
					sb.append(partext + " " + toPrintTable(fftab, true));
				} else {
					sb.append(partext + " "
							+ toPrintTable(ref.asChildren.get(i), true));
				}
			}
			if (sb.length() > 0) {
				bt.addText("(" + sb.toString() + "). ");
			}
		}

	}

	/**
	 * @param ftab
	 * @param bt
	 * @return
	 */
	protected boolean addChildReference(ReportUnit pop, ReportUnit mom,
			int pid, String text, BodyText bt) {

		long momTable = 0;
		long dadTable = 0;

		if (pop != null) {
			dadTable = pop.getTableNo();
		}
		if (mom != null) {
			momTable = mom.getTableNo();
		}

		ReportUnit cu = personTables.get(pid);
		// if has own table then refer only there
		if (cu != null && momTable != cu.getTableNo()
				&& dadTable != cu.getTableNo()) {

			// bt.addText("(" + text + " " + cu.getTableNo() + "). ");
			bt.addLink("(" + text + " " + cu.getTableNo() + "). ", false,
					false, false, " " + cu.getTableNo());

			return true;

		}

		PersonInTables ref = personReferences.get(pid);
		if (ref == null) {
			return false;
		}

		StringBuilder sb = new StringBuilder();

		Long ownerArray[] = ref.getOwnerArray();
		if (ownerArray.length > 0) {

			boolean addComma = false;
			for (Long pif : ownerArray) {
				if (pif != momTable && pif != dadTable) {
					if (addComma) {
						sb.append(",");
					}
					addComma = true;
					sb.append(toPrintTable(pif, true));
				}
			}
		}

		if (sb.length() > 0) {

			bt.addLink("(" + typesTable.getTextValue("ALSO") + " " + text + " "
					+ sb.toString() + "). ", false, false, false, sb.toString());

			return true;
		}

		return false;
	}

	protected UnitNotice[] getInternalNotices(UnitNotice[] xnotices) {

		int tableCount = 0;
		int famtCount = 0;

		for (int i = 0; i < xnotices.length; i++) {
			if (xnotices[i].getTag().equals("TABLE"))
				tableCount++;
			if (xnotices[i].getTag().equals("FAMT"))
				famtCount++;
		}

		UnitNotice[] notices = new UnitNotice[xnotices.length - tableCount
				- famtCount];

		int xn = 0;

		for (int i = 0; i < xnotices.length; i++) {
			if (!xnotices[i].getTag().equals("FAMT")
					&& !xnotices[i].getTag().equals("TABLE")) {
				notices[xn++] = xnotices[i];
			}
		}
		return notices;
	}

	protected String printRelationNotice(RelationNotice rn, String defType,
			int spouseNum, boolean isBefore) {

		String showType = caller.getSpouseData().getSelection()
				.getActionCommand();
		if (showType == null) {
			showType = ReportWorkerDialog.SET_SPOUSE_NONE;
		}

		StringBuilder sb = new StringBuilder();
		boolean addSpace = false;
		if (isBefore) {
			if (rn.getType() != null) {
				if (rn.getTag().equals("MARR")) {

					sb.append(rn.getType());
					addSpace = true;

				}
			} else if (defType != null) {
				sb.append(defType);
			}
		} else {

			if (showType.equals(ReportWorkerDialog.SET_SPOUSE_NONE)) {
				return "";
			}
			if (rn.getType() != null) {
				sb.append(rn.getType());
			} else {
				sb.append(typesTable.getTextValue(Resurses.getReportString(rn
						.getTag())));
			}
			addSpace = true;

		}

		if (isBefore) {
			if (sb.length() > 0) {
				if (spouseNum > 0) {
					sb.append(" " + spouseNum + ":o");
				}
				addSpace = true;
			}
		}

		if (showType.equals(ReportWorkerDialog.SET_SPOUSE_NONE)) {
			return sb.toString();
		}

		if (showType.equals(ReportWorkerDialog.SET_SPOUSE_YEAR)
				|| showType.equals(ReportWorkerDialog.SET_SPOUSE_DATE)) {
			if ((isBefore && rn.getTag().equals("MARR")) || (!isBefore)) {
				String yr = rn.getFromDate();
				if (showType.equals(ReportWorkerDialog.SET_SPOUSE_YEAR)) {
					if (yr != null && yr.length() >= 4) {
						if (addSpace) {
							sb.append(" ");
						}
						sb.append(yr.substring(0, 4));
					}
				} else {
					String date = printDate(rn.getDatePrefix(),
							rn.getFromDate(), rn.getToDate());
					if (date.length() > 0) {
						if (addSpace) {
							sb.append(" ");
						}
						sb.append(date);
					}
				}
			}
			return sb.toString();
		}

		if (isBefore && !rn.getTag().equals("MARR")) {
			return "";
		}

		if (rn.getDescription() != null) {

			if (addSpace) {
				sb.append(" ");
			}
			sb.append("(");
			sb.append(rn.getDescription());
			sb.append(")");
			addSpace = true;

		}

		String date = printDate(rn.getDatePrefix(), rn.getFromDate(),
				rn.getToDate());
		if (date.length() > 0) {
			if (addSpace) {
				sb.append(" ");
			}
			sb.append(date);
			addSpace = true;
		}

		if (rn.getPlace() != null) {
			if (addSpace) {
				sb.append(" ");
			}
			sb.append(rn.getPlace());
			addSpace = true;
		}
		if (rn.getNoteText() != null) {
			if (addSpace) {
				if (rn.getNoteText().charAt(0) != ','
						&& rn.getNoteText().charAt(0) != '.') {
					sb.append(" ");
				}
			}

			sb.append("(");
			sb.append(trim(rn.getNoteText()));
			sb.append(")");

			addSpace = true;
		}
		String srcFormat = caller.getSourceFormat();
		if (!ReportWorkerDialog.SET_NO.equals(srcFormat)) {

			String src = trim(rn.getSource());

			String srcText = addSource(false, srcFormat, src);
			sb.append(srcText);
		}

		return sb.toString();
	}

	protected void printNotices(BodyText bt, UnitNotice[] notices, int colType,
			long tableNo) {
		boolean addSpace = false;
		boolean addDot = false;
		String tag;
		String occuTypes = "|OCCU|EDUC|TITL|";
		int minSurety = caller.showMinSurety();
		if (notices.length > 0 && caller.showOnSeparateLines()) {

			repoWriter.addText(bt);
		}

		boolean forceOccuUpperCase = true;
		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			addSpace = false;
			addDot = false;
			tag = nn.getTag();
			if (!tag.equals("NAME")) {
				if ((nn.getPrivacy() == null || nn.getPrivacy().equals(
						Resurses.PRIVACY_TEXT))
						&& nn.getSurety() >= minSurety) {
					if ((typesTable.isType(tag, colType))) {

						if (typesTable.isType(tag, 1)) {

							String noType = nn.getNoticeType();
							if (noType != null) {
								bt.addText(noType);
							} else {

								bt.addText(typesTable.getTypeText(tag));
							}
							addSpace = true;
							addDot = true;
						}

						if (nn.getDescription() != null) {

							if (nn.getTag().equals("TABLE")
									&& bt.getCount() == 0) {
								// for TABLE notice the description contains a
								// header if this is the first text piece for
								// the notice

								BodyText btt = bt;
								bt = new TableHeaderText();

								bt.addText(nn.getDescription(), true, false);
								repoWriter.addText(bt);
								bt = btt;
								repoWriter.addText(bt);
							} else {
								if (addSpace) {
									bt.addText(" ");
								}
								String desc = nn.getDescription();
								if (occuTypes.indexOf(nn.getTag()) > 0) {
									if (forceOccuUpperCase) {
										if (desc.length() > 1) {
											desc = desc.substring(0, 1)
													.toUpperCase()
													+ desc.substring(1);
										}
									}
								}
								bt.addText(desc);
								addSpace = true;
								addDot = true;
							}
						}
						String dd = printDate(nn.getDatePrefix(),
								nn.getFromDate(), nn.getToDate());
						if (dd.length() > 0) {
							if (addSpace) {
								if (typesTable.getTypeText(tag).length() > 1) {
									bt.addText(" ");
								} else {
									bt.addText("\u00A0");
								}
								bt.addText(dd);
							}
							addSpace = true;
							addDot = true;
						}

						if (nn.getPlace() != null
								|| (nn.getTag().equals("RESI") && nn
										.getPostOffice() != null)) {
							if (addSpace)
								bt.addText(" ");
							bt.addText(convertPlace(nn));

							addSpace = true;
							addDot = true;
						}

						if (caller.isCreatePlaceIndexSet()
								&& typesTable.isType(nn.getTag(), 5)) {

							String place = nn.getPlace();
							if (place != null && nn.getTag().equals("RESI")
									&& nn.getPostOffice() != null) {
								place = nn.getPostOffice();
							}
							int idx = -1;
							while (idx > -2) {

								if (idx >= 0) {
									if (nn.getRefPlaces() != null
											&& idx < nn.getRefPlaces().length) {
										place = nn.getRefPlaces()[idx];

									} else {
										idx = -9999;
									}
								}

								if (place != null) {

									PlaceInTables pit = places.get(place
											.toUpperCase());
									if (pit == null) {
										pit = new PlaceInTables(place);
										places.put(place.toUpperCase(), pit);
									}
									pit.addTable(tableNo);
								}
								idx++;
							}
						}

						if ((caller.isShowVillageFarm() && (nn.getVillage() != null
								|| nn.getFarm() != null || nn.getCroft() != null))
								|| nn.getState() != null
								|| nn.getCountry() != null) {
							if (addSpace) {
								bt.addText(" ");
								addSpace = false;
							}
							bt.addText("(");
							if (caller.isShowVillageFarm()
									&& nn.getVillage() != null) {
								bt.addText(nn.getVillage());
								addSpace = true;
							}
							if (caller.isShowVillageFarm()
									&& nn.getFarm() != null) {
								if (addSpace) {
									bt.addText(" ");
									addSpace = true;
								}
								bt.addText(nn.getFarm());
								addSpace = true;
							}
							if (caller.isShowVillageFarm()
									&& nn.getCroft() != null) {
								if (addSpace) {
									bt.addText(" ");
									addSpace = true;
								}
								bt.addText(nn.getCroft());

							}

							if (nn.getState() != null) {
								if (addSpace) {
									bt.addText(" ");
								}
								addSpace = true;
								bt.addText(nn.getState());
							}
							if (nn.getCountry() != null) {
								if (addSpace) {
									bt.addText(", ");
								}
								bt.addText(nn.getCountry());
							}
							addSpace = true;
							addDot = true;
							bt.addText(")");
						}
						if (tag.startsWith("PHOT") && caller.isSeparateImages()) {
							if (addSpace) {
								addSpace = true;
							}
							imageNumber++;
							bt.addText(Resurses.getReportString("INDEX_IMAGE")
									+ " " + imageNumber + ". ", true, false);

							ImageNotice inoti = new ImageNotice(nn,
									imageNumber, tableNo);
							imgNotices.add(inoti);
						} else {

							if (caller.showImages()) {
								ImageText imagetx = new ImageText();
								BufferedImage img;
								try {
									img = nn.getMediaImage();
								} catch (IOException e) {
									logger.log(Level.WARNING, "getMedia", e);

									img = null;
								}
								if (img != null) {
									double imh = img.getHeight();
									double imw = img.getWidth();
									double newh = 300;
									double neww = 300;

									// Dimension xx = caller.getImageMaxSize();

									if (imh <= newh) {
										if (imw <= neww) {
											newh = imh;
											neww = imw;

										} else {
											newh = imh * (neww / imw);
											neww = imw;
										}
									} else {
										neww = imw * (newh / imh);
									}

									imageNumber++;
									String imgTitle = "";
									if (caller.isNumberingImages()) {
										imgTitle = Resurses
												.getReportString("INDEX_IMAGE")
												+ "\u00A0" + imageNumber + ". ";
									}
									if (nn.getMediaTitle() != null) {
										String titl = trim(nn.getMediaTitle());
										imgTitle += titl + ".";
									}
									String xxx = "0000" + imageNumber;
									String imgNamePrefix = xxx.substring(xxx
											.length() - 4) + "_";
									imagetx.setImage(
											img,
											nn.getMediaData(),
											img.getWidth(),
											img.getHeight(),
											imgNamePrefix
													+ nn.getMediaFilename(),
											imgTitle, nn.getTag());
									imagetx.addText("");
								}
								// if (nn.getMediaTitle() != null) {
								// imagetx.addText(nn.getMediaTitle());
								// }
								if (imagetx.getCount() > 0) {
									repoWriter.addText(bt);
									repoWriter.addText(imagetx);

								}
							}
							// }

							if (nn.getNoteText() != null) {
								String trimmed = trim(nn.getNoteText());
								if (addSpace) {

									if (trimmed != null && !trimmed.isEmpty()) {
										if (trimmed.charAt(0) != ','
												&& trimmed.charAt(0) != '.') {
											bt.addText(" ");
										}
									}
								}
								int tlen = printText(bt, trimmed,
										nn.getRefNames());
								if (tlen > 0) {
									addSpace = true;
									addDot = true;
								}
							}
						}
						if (nn.getRefNames() != null) {

							for (int i = 0; i < nn.getRefNames().length; i++) {
								String txtName = nn.getRefNames()[i];
								PersonInTables ppText = textReferences
										.get(txtName);
								if (ppText == null) {
									int refpid = --referencePid;
									ppText = new PersonInTables(refpid);
									String[] parts = txtName.split(",");
									if (parts.length == 2) {
										//
										// check the von
										//
										String vonPart = null;
										String surPart = null;
										int vonIndex = Utils
												.isKnownPrefix(parts[0]);
										if (vonIndex > 0
												&& vonIndex < parts[0].length()) {
											vonPart = parts[0].substring(0,
													vonIndex);
											surPart = parts[0]
													.substring(vonIndex + 1);
										} else {
											surPart = parts[0];
										}

										ppText.shortPerson = new PersonShortData(
												refpid, parts[1], null,
												vonPart, surPart, null, null,
												null);
										textReferences.put(txtName, ppText);
									} else if (parts.length == 1) {
										ppText.shortPerson = new PersonShortData(
												refpid, parts[0], null, null,
												null, null, null, null);
										textReferences.put(txtName, ppText);
									}
								}
								ppText.references.add(tableNo);

							}

						}

						if (caller.showAddress()) {

							if (nn.getAddress() != null) {
								if (addSpace)
									bt.addText(" ");
								int tlen = 0;
								String parts[] = nn.getAddress()
										.replaceAll("\\r", "").split("\n");
								for (int i = 0; i < parts.length; i++) {

									if (i > 0) {
										bt.addText(",");
									}
									bt.addText(parts[i]);
									tlen = i + 1;
								}
								if (nn.getPostalCode() != null
										|| nn.getPostOffice() != null) {
									if (tlen++ > 0) {
										bt.addText(",");
									}
									if (nn.getPostalCode() != null) {
										bt.addText(nn.getPostalCode());
										if (nn.getPostOffice() != null) {
											bt.addText(" ");
											bt.addText(nn.getPostOffice());
											tlen++;
										}
									} else {
										bt.addText(nn.getPostOffice());
										tlen++;
									}

								}

								if (nn.getEmail() != null) {
									if (tlen++ > 0) {
										bt.addText(",");
									}
									bt.addText("[" + nn.getEmail() + "]");
								}

								if (tlen > 0) {
									addSpace = true;
									addDot = true;
								}
							}
						}
						String srcFormat = caller.getSourceFormat();
						if (!ReportWorkerDialog.SET_NO.equals(srcFormat)) {

							String src = trim(nn.getSource());

							String text = addSource(addDot, srcFormat, src);
							bt.addText(text);
							if (!text.isEmpty()) {
								addDot = true;
							}
						}
						if (addDot) {

							if (occuTypes.indexOf(tag) > 0) {
								String nxttag = null;
								if (j < notices.length - 1) {
									nxttag = notices[j + 1].getTag();
								}
								if (nxttag != null) {
									if (occuTypes.indexOf(nxttag) > 0) {
										bt.addText(", ");
										forceOccuUpperCase = false;
									} else {
										if (!bt.endsWithText(".")) {

											bt.addText(". ");
										} else {
											bt.addText(" ");
										}
										forceOccuUpperCase = true;
									}
								} else {
									if (!bt.endsWithText(".")) {
										bt.addText(". ");
									} else {
										bt.addText(" ");
									}
									forceOccuUpperCase = true;
								}

							} else {
								if (!bt.endsWithText(".")) {
									bt.addText(". ");
								} else {
									bt.addText(" ");
								}
								forceOccuUpperCase = true;
							}
							if (caller.showOnSeparateLines()) {

								repoWriter.addText(bt);
							}
						}

					}
				}
			}
		}

		// if (caller.showOnSeparateLines()){
		// repoWriter.addText(bt);
		// }

	}

	protected String addSource(boolean addDot, String srcFormat, String src) {
		StringBuilder sb = new StringBuilder();
		if (src != null && !src.isEmpty()) {

			if (srcFormat.equals(ReportWorkerDialog.SET_TX1)) {
				if (addDot) {
					sb.append(". ");

				}
				sb.append(" ");
				sb.append(src);

			} else if (srcFormat.equals(ReportWorkerDialog.SET_TX2)) {
				sb.append(" [");
				sb.append(src);
				sb.append("]");

			} else if (srcFormat.equals(ReportWorkerDialog.SET_AFT)) {
				Integer srcId = refs.get(src);
				if (srcId == null) {
					srcId = refs.size() + 1;
					refs.put(src, srcId);
				}
				sb.append(" [");
				sb.append(srcId.toString());
				sb.append("]");
				addDot = true;
			}

		}
		return sb.toString();
	}

	/**
	 * Set 1st paragraph bold (header) if it begins with *
	 * 
	 * @param bt
	 * @param text
	 * @param strings
	 * @return
	 */
	private int printText(BodyText bt, String text, String[] namesin) {
		if (text == null)
			return 0;

		if (namesin != null && caller.showBoldNames()) {
			String names[] = new String[namesin.length];
			for (int i = 0; i < names.length; i++) {
				names[i] = namesin[i];
			}

			int firstName = -1;
			int nameIdx = -1;
			String nameTxt = null;

			TextNamePart txtPart = null;
			for (int i = 0; i < names.length; i++) {
				if (names[i] != null) {
					txtPart = locateName(text, names[i]);

					if (txtPart != null && txtPart.location >= 0) {
						if (firstName < 0 || txtPart.location < firstName) {
							firstName = txtPart.location;
							nameTxt = txtPart.nameText;
							nameIdx = i;
						}
					}
				}
			}
			String nxtText;
			int fullLength = 0;
			if (firstName > 0) {
				nxtText = text.substring(firstName + nameTxt.length());
				int len1 = printText(bt, text.substring(0, firstName), null);
				fullLength += len1;
			} else if (firstName == 0) {
				nxtText = text;
			} else {
				printText(bt, text, null);
				return fullLength + text.length();
			}
			bt.addText(nameTxt, true, false);
			if (nameTxt.equals(text)) {
				return fullLength + nameTxt.length();
			}
			names[nameIdx] = null;
			int len2 = printText(bt, nxtText, names);
			return fullLength + len2;

		}

		StringBuilder sb = new StringBuilder();
		ArrayList<String> v = new ArrayList<String>();
		boolean wasWhite = false;
		boolean wasNl = true;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == ' ' || c == '\r' || c == '\t') {
				if (!wasWhite) {
					sb.append(' ');
					wasWhite = true;
				}
			} else {
				if (c == '\n') {
					if (wasNl) {
						v.add(sb.toString());
						sb.delete(0, sb.length());
						wasNl = false;
					} else {
						sb.append(' ');
						wasNl = true;
					}
				} else {
					wasNl = false;
					wasWhite = false;
					sb.append(c);
				}
			}
		}
		if (sb.length() > 0) {
			v.add(sb.toString());
		}
		for (int i = 0; i < v.size(); i++) {
			String aux = v.get(i);
			if (i == 0 && aux.length() > 0 && aux.charAt(0) == '*') {
				repoWriter.addText(bt);
				int fontSize = bt.getFontSize();
				if (aux.length() > 2 && aux.substring(0, 2).equals("**")) {
					bt.setFontSize(fontSize + 4);
					bt.addText(aux.substring(2), true, false);
				} else {
					bt.setFontSize(fontSize + 2);
					bt.addText(aux.substring(1), true, false);

				}
				// repoWriter.addText(bt);
				bt.setFontSize(fontSize);
			} else if (i > 0) {
				repoWriter.addText(bt);
				bt.addText(aux);
			} else {
				bt.addText(aux);
			}
		}

		return text.length();
	}

	private TextNamePart locateName(String text, String name) {
		TextNamePart tnp = new TextNamePart();
		int minusEnding = 0;
		String parts[] = name.split(",");
		if (parts.length == 2) {

			if (parts[0].isEmpty()) {
				tnp.location = text.indexOf(parts[1]);
				tnp.nameText = parts[1];
			} else if (parts[1].isEmpty()) {
				tnp.location = text.indexOf(parts[0]);
				tnp.nameText = parts[0];
			} else {
				while (minusEnding < 4) {
					String compaName = parts[1] + " " + parts[0];
					if (compaName.length() > 10) {
						compaName = compaName.substring(0, compaName.length()
								- minusEnding);
					}
					tnp.location = text.indexOf(compaName);

					if (tnp.location >= 0 || compaName.length() < 10) {
						tnp.nameText = compaName;
						break;
					}
					minusEnding++;
				}
			}
		}
		if (tnp.location < 0) {
			tnp.location = text.indexOf(name);
			tnp.nameText = name;
		}

		if (tnp.location >= 0) {
			int j = 0;
			String origName = tnp.nameText;
			for (j = 0; j < 5 + minusEnding; j++) {
				if (text.length() == tnp.location + tnp.nameText.length()) {
					break;
				}
				char c = text.charAt(tnp.location + tnp.nameText.length());
				if (c < 'A') {
					break;
				}
				tnp.nameText += c;
			}
			if (j == 5) {
				tnp.nameText = origName;
			}

		}

		return tnp;
	}

	private String printDate(String datePrefix, String dateFrom, String dateTo) {
		String mellan = "";
		String framme = "";
		if (dateFrom == null)
			return "";
		StringBuilder sb = new StringBuilder();

		if (datePrefix != null) {
			framme = typesTable.getTextValue(datePrefix);
			if (datePrefix.equals("FROM") && dateTo != null) {
				mellan = typesTable.getTextValue("TO");
			}
			if (datePrefix.equals("BET") && dateTo != null) {
				mellan = typesTable.getTextValue("AND");
			}
		}
		if (!framme.isEmpty()) {
			sb.append(framme);
			sb.append(" ");
		}
		sb.append(toRepoDate(dateFrom));
		if (!mellan.isEmpty() && dateTo != null) {
			sb.append(" ");
			sb.append(mellan);
			sb.append(" ");
			sb.append(toRepoDate(dateTo));
		}
		return sb.toString();

	}

	private String toRepoDate(String date) {
		if (date == null)
			return null;
		String df = caller.getDateFormat();

		int dd = 0;
		int mm = 0;
		int yy = 0;
		try {
			if (date.length() == 4) {
				return date;
			} else if (date.length() >= 6) {
				yy = Integer.parseInt(date.substring(0, 4).trim());
				mm = Integer.parseInt(date.substring(4, 6).trim());
			}
			if (date.length() == 8) {

				dd = Integer.parseInt(date.substring(6, 8).trim());

			}
			if (df.equals("SE")) {
				if (dd == 0) {
					return "" + date.substring(0, 4) + "-"
							+ date.substring(4, 6);
				} else {
					return "" + date.substring(0, 4) + "-"
							+ date.substring(4, 6) + "-" + date.substring(6, 8);
				}
			} else if (df.equals("UK")) {
				if (dd == 0) {
					return "" + mm + "/" + yy;
				} else {
					return "" + dd + "/" + mm + "/" + yy;
				}
			} else if (df.equals("US")) {
				if (dd == 0) {
					return "" + mm + "/" + yy;
				} else {
					return "" + mm + "/" + dd + "/" + yy;
				}
			} else {
				if (dd == 0) {
					return "" + mm + "." + yy;
				} else {
					return "" + dd + "." + mm + "." + yy;
				}
			}
		} catch (NumberFormatException ne) {
			logger.log(Level.WARNING, "toRepoDate", ne);
			return "00.00.00";
		}

	}

	/**
	 * print name to report. First name is always printed regardless of types
	 * settings unless set as privacy = true
	 * 
	 * TODO special underline such as "Anna*-Liisa" (Juha-Pekka*) TODO underline
	 * Anna-Liisa** or Per Erik** to result in two names underlined
	 * 
	 * @param bt
	 * @param notices
	 * @param isMain
	 */
	protected void printName(BodyText bt, PersonLongData persLong, int colType) {
		int nameCount = 0;
		String prevGivenname = "";
		String prevPatronyme = "";
		String prevPostfix = "";
		UnitNotice[] notices = persLong.getNotices();
		int minSurety = caller.showMinSurety();
		boolean isDead = false;
		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			if (nn.getTag().equals("DEAT") || nn.getTag().equals("BURI")) {
				isDead = true;
			}
			if (nn.getTag().equals("BIRT")) {
				String bd = nn.getFromDate();
				if (bd != null && bd.length() >= 4) {

					try {
						int by = Integer.parseInt(bd.substring(0, 4));
						Calendar rightNow = Calendar.getInstance();
						int cy = rightNow.get(Calendar.YEAR);
						if (cy > by + 115) {
							isDead = true;
						}
					} catch (NumberFormatException ne) {
						// NumberFormatException ignored
					}

				}
			}
		}

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			if (nn.getTag().equals("NAME")) {
				if ((nn.getPrivacy() == null || nn.getPrivacy().equals(
						Resurses.PRIVACY_TEXT))
						&& nn.getSurety() >= minSurety) {
					if ((typesTable.isType("NAME", colType) || nameCount == 0)) {

						if (nameCount > 0 && nn.getNoticeType() == null) {
							bt.addText(", ");
							if (typesTable.isType("NAME", 1)) {
								String[] parts = typesTable.getTypeText("NAME")
										.split(";");
								String part = null;
								if (parts != null) {
									if (parts.length > 1) {
										if (j < notices.length - 1) {
											if (notices[j + 1].getTag().equals(
													"NAME")) {
												part = parts[0];
											}
										}
										if (part == null) {
											if (parts.length < 3
													|| isDead == false) {
												part = parts[1];
											} else {
												part = parts[2];
											}
										}
										bt.addText(part);
										bt.addText(" ");
									} else if (parts.length == 1) {
										bt.addText(parts[0]);
										bt.addText(" ");
									}
								}
							}
						} else if (nn.getNoticeType() != null) {
							if (nameCount > 0) {
								if (!nn.getNoticeType().equals("/")
										&& !nn.getNoticeType().equals("(")) {
									bt.addText(", ");
									bt.addText(nn.getNoticeType());
									bt.addText(" ");
								} else {
									if (nn.getNoticeType().equals("(")) {
										bt.addText(" ");
									}
									bt.addText(nn.getNoticeType());

								}
							}

						}
						if (nn.getDescription() != null) {
							bt.addText(nn.getDescription());
							bt.addText(" ");
						}
						boolean wasName = false;
						if (!prevGivenname.equals(nv(nn.getGivenname()))) {
							prevGivenname = nv(nn.getGivenname());

							printGivenname(bt, prevGivenname, true);
							if (!prevGivenname.isEmpty()) {
								wasName = true;
							}
						}

						if (!prevGivenname.isEmpty()
								&& !nv(nn.getPrefix()).isEmpty()) {
							if (wasName) {
								bt.addText(" ", caller.showBoldNames(), false);
							}
							bt.addText(nn.getPrefix(), caller.showBoldNames(),
									false);
							wasName = true;
						}

						if (!prevPatronyme.equals(nv(nn.getPatronym()))) {
							prevPatronyme = nv(nn.getPatronym());
							if (wasName && !nv(nn.getPatronym()).isEmpty()) {
								bt.addText(" ", caller.showBoldNames(), false);
							}

							if (!nv(nn.getPatronym()).isEmpty()) {
								bt.addText(nn.getPatronym(),
										caller.showBoldNames(), false);
								wasName = true;
							}
						}
						if (wasName && !nv(nn.getSurname()).isEmpty()) {
							bt.addText(" ", caller.showBoldNames(), false);
						}
						if (!nv(nn.getSurname()).isEmpty()) {
							bt.addText(nn.getSurname(), caller.showBoldNames(),
									false);
							wasName = true;
						}
						if (!prevPostfix.equals(nv(nn.getPostfix()))) {
							prevPostfix = nv(nn.getPostfix());
							if (wasName && !nv(nn.getPostfix()).isEmpty()) {
								bt.addText(" ", caller.showBoldNames(), false);
							}

							if (!nv(nn.getPostfix()).isEmpty()) {
								bt.addText(nn.getPostfix(),
										caller.showBoldNames(), false);
							}
						}

						if (nn.getNoticeType() != null
								&& nn.getNoticeType().equals("(")) {
							bt.addText(")");
						}
					}
					nameCount++;
				}
			}
		}
		String srcFormat = caller.getSourceFormat();

		if (!ReportWorkerDialog.SET_NO.equals(srcFormat)) {
			String src = persLong.getSource();
			String text = addSource(true, srcFormat, src);
			bt.addText(text);
		}

		bt.addText(". ");
	}

	private void printGivenname(BodyText bt, String prevGivenname,
			boolean useBoldInfo) {
		String[] nameParts = prevGivenname.split(" ");
		boolean doBold = caller.showBoldNames();
		if (!useBoldInfo) {
			doBold = false;
		}

		for (int k = 0; k < nameParts.length; k++) {
			String namePart = nameParts[k];
			String startChar = "";
			String endChar = "";
			if (namePart.length() > 2
					&& ((namePart.charAt(0) == '(' && namePart.charAt(namePart
							.length() - 1) == ')') || (namePart.charAt(0) == '\"' && namePart
							.charAt(namePart.length() - 1) == '\"'))) {
				char[] c = new char[1];
				c[0] = namePart.charAt(0);
				startChar = new String(c);
				c[0] = namePart.charAt(namePart.length() - 1);
				endChar = new String(c);
				namePart = namePart.substring(1, namePart.length() - 1);

			}
			if (!startChar.isEmpty()) {
				bt.addText(startChar, doBold, false);
			}
			String[] subParts = namePart.split("-");

			int astidx = namePart.indexOf("*");
			int bstidx = namePart.indexOf("**");
			if (bstidx > 0 || subParts.length == 1) {
				if (astidx > 0) {

					if (astidx == namePart.length() - 1) {
						bt.addText(namePart.substring(0, astidx), doBold,
								caller.showUnderlineNames());
					} else {

						if (bstidx > 0) {
							if (bstidx == namePart.length() - 2) {

								bt.addText(namePart.substring(0, bstidx),
										doBold, caller.showUnderlineNames());
							}
						}
					}
				} else {
					bt.addText(namePart, doBold, false);
				}
			} else {

				for (int kk = 0; kk < subParts.length; kk++) {
					String subPart = subParts[kk];
					int cstidx = subPart.indexOf("*");
					if (kk > 0) {
						bt.addText("-", doBold, false);
					}
					if (cstidx >= 0 && cstidx == subPart.length() - 1) {

						bt.addText(subPart.substring(0, cstidx), doBold,
								caller.showUnderlineNames());

					} else {
						bt.addText(subPart, doBold, false);
					}
				}
			}

			if (!endChar.isEmpty()) {
				bt.addText(endChar, doBold, false);
			}
			if (k != nameParts.length - 1) {
				bt.addText(" ", doBold, false);
			}
		}
	}

	private HashMap<String, String> bendMap = null;

	/**
	 * Convert place.
	 * 
	 * @param notice
	 *            the notice
	 * @return the string
	 */
	protected String convertPlace(UnitNotice notice) {
		String place = null;
		if (notice.getPlace() != null) {
			place = notice.getPlace();
		} else if (notice.getTag().equals("RESI")
				&& notice.getPostOffice() != null) {
			place = toProper(notice.getPostOffice());
		} else {
			return null;
		}

		if (caller.isBendPlaces()) {
			if (bendMap == null) {
				bendMap = new HashMap<String, String>();
				try {
					SukuData resp = Suku.kontroller.getSukuData("cmd=get",
							"type=conversions",
							"lang=" + Resurses.getLanguage());
					for (int i = 0; i < resp.vvTexts.size(); i++) {
						String[] cnvx = resp.vvTexts.get(i);
						String key = cnvx[1] + "|" + cnvx[0];
						bendMap.put(key.toLowerCase(), cnvx[2]);
					}
				} catch (SukuException e) {
					JOptionPane.showMessageDialog(caller, e.getMessage());
				}
			}
			String tag = notice.getTag();
			String rule = typesTable.getTypeRule(tag);
			if (rule != null) {
				String key = rule + "|" + place;
				String tmp = bendMap.get(key.toLowerCase());
				if (tmp != null) {
					place = tmp;
				} else {
					tmp = typesTable.getTextValue("CNV_" + rule);
					if (tmp != null && !tmp.isEmpty() && !tmp.equals("x")) {
						place = tmp + " " + place;
					}
				}
			}
		}
		return place;
	}

	private String toProper(String postOffice) {
		if (postOffice == null) {
			return null;
		}
		if (postOffice.indexOf(" ") < 0) {
			String tmp = postOffice.toLowerCase();
			if (tmp.length() < 2) {
				return postOffice;
			}
			String A = tmp.substring(0, 1).toUpperCase();
			return A + tmp.substring(1);
		}
		return postOffice;

	}

	private String trim(String text) {
		if (text == null)
			return null;

		String tek = spaceTrim(text);

		if (tek.equals(".")) {
			return "";
		}
		if (tek.endsWith(".")) {
			tek = tek.substring(0, tek.length() - 1);
		}
		return spaceTrim(tek);
	}

	private String spaceTrim(String text) {
		StringBuilder sb = new StringBuilder();
		int lastSpace = -1;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c != ' ' || sb.length() > 0) {
				sb.append(c);
			}
			if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
				if (lastSpace < 0) {
					lastSpace = sb.length() - 1;
				}
			} else {
				lastSpace = -1;
			}
		}

		if (lastSpace > 0) {
			return sb.toString().substring(0, lastSpace);
		} else if (lastSpace == 0) {
			return "";
		} else {
			return sb.toString();
		}

	}

	/**
	 * Nv.
	 * 
	 * @param text
	 *            the text
	 * @return the string
	 */
	protected String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

	class TextNamePart {
		int location = -1;
		int nameIdx = -1;
		String nameText = null;
	}

	/**
	 * Implemented by derived class.
	 * 
	 * @param b
	 *            the new visible
	 */
	public abstract void setVisible(boolean b);

	/**
	 * Images may be stored in a Vector<ImageNotice> for later print.
	 */
	class ImageNotice {

		/** The nn. */
		UnitNotice nn = null;

		/** The img number. */
		int imgNumber = 0;

		/** The tab no. */
		long tabNo = 0;

		/**
		 * Instantiates a new image notice.
		 * 
		 * @param nn
		 *            the nn
		 * @param imgNumber
		 *            the img number
		 * @param tabNo
		 *            the tab no
		 */
		ImageNotice(UnitNotice nn, int imgNumber, long tabNo) {
			this.nn = nn;
			this.tabNo = tabNo;
			this.imgNumber = imgNumber;
		}
	}

}
