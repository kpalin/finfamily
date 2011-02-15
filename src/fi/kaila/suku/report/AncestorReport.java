package fi.kaila.suku.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.report.style.ChildHeaderText;
import fi.kaila.suku.report.style.ChildListText;
import fi.kaila.suku.report.style.MainPersonText;
import fi.kaila.suku.report.style.SubPersonText;
import fi.kaila.suku.report.style.TableHeaderText;
import fi.kaila.suku.report.style.TableSubHeaderText;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Roman;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * <h1>Ancestor report creator</h1>
 * 
 * The ancestor report structure is created here.
 * 
 * @author Kalle
 */
public class AncestorReport extends CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor for Ancestor report.
	 * 
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param repoWriter
	 *            the repo writer
	 */
	public AncestorReport(ReportWorkerDialog caller, SukuTypesTable typesTable,
			ReportInterface repoWriter) {
		super(caller, typesTable, repoWriter);

	}

	/**
	 * execute the ancestor report.
	 * 
	 * @throws SukuException
	 *             the suku exception
	 */
	@Override
	public void executeReport() throws SukuException {
		SukuData vlist = null;
		String order = caller.getAncestorPane().getNumberingFormat()
				.getSelection().getActionCommand();

		boolean showFamily = caller.getAncestorPane().getShowfamily();
		int generations = caller.getAncestorPane().getGenerations();

		logger.info("Ancestor report for " + caller.getPid() + ", order="
				+ order + ", include family = [" + showFamily + "] with ["
				+ generations + "] generations");

		try {
			vlist = caller.getKontroller().getSukuData(
					"cmd=" + Resurses.CMD_CREATE_TABLES,
					"type=" + Resurses.CMD_ANC_TYPE,
					"generations=" + generations, "order=" + order,
					"family=" + showFamily, "pid=" + caller.getPid());
		} catch (SukuException e) {
			logger.log(Level.INFO, Resurses.getString(Resurses.CREATE_REPORT),
					e);
			JOptionPane.showMessageDialog(
					caller,
					Resurses.getString(Resurses.CREATE_REPORT) + ":"
							+ e.getMessage());
		}

		if (vlist != null && vlist.resu != null) {
			JOptionPane.showMessageDialog(caller,
					Resurses.getString(Resurses.CREATE_REPORT) + " ["
							+ vlist.resu + "]");
			return;
		}
		tables = vlist.tables;

		if (tables.size() > 0) {
			personReferences = Utils.getDescendantToistot(tables);
			initPersonTables();
			repoWriter.createReport();
			if (order.equals("ESPOLIN")) {
				createEspolinReport();
			} else {

				if (caller.getAncestorPane().getAllBranches()) {
					createFullStradoReport(vlist.reportUnits);
				} else {
					createStradoReport();
				}
			}
			try {
				printNameIndex();
			} catch (SukuException e) {
				logger.log(Level.WARNING, "NameIndex", e);
				JOptionPane.showMessageDialog(
						caller,
						Resurses.getString(Resurses.CREATE_REPORT) + ":"
								+ e.getMessage());
			}
			repoWriter.closeReport();

		}

	}

	private void createFullStradoReport(HashMap<Integer, ReportUnit> reportUnits)
			throws SukuException {
		textReferences = new HashMap<String, PersonInTables>();

		ReportUnitAll ftab = null;
		ReportUnitAll mtab = null;
		if (tables.size() == 0) {
			return;
		}

		Vector<ReportUnitAll> genlistNext = null;

		Vector<ReportUnitAll> genlist = new Vector<ReportUnitAll>();
		ReportUnitAll ra = new ReportUnitAll();
		ra.r = tables.get(0);
		ra.gene = 1;
		ra.tabNo = 1;
		genlist.add(ra);
		boolean firstTable = true;
		while (true) {
			genlistNext = new Vector<ReportUnitAll>();
			for (ReportUnitAll ua : genlist) {
				if (firstTable) {
					ftab = ua;
					mtab = new ReportUnitAll();
					mtab.r = null;
				} else {
					ftab = new ReportUnitAll();
					ftab.r = reportUnits.get(ua.r.getFatherPid());
					if (ftab.r != null) {
						ftab.tabNo = ua.tabNo * 2;
						ftab.gene = ua.gene + 1;
						ftab.r.setTableNo(ftab.tabNo);
						ftab.r.setGen(ftab.gene);
					}
					mtab = new ReportUnitAll();
					mtab.r = reportUnits.get(ua.r.getMotherPid());
					if (mtab.r != null) {
						mtab.tabNo = ua.tabNo * 2 + 1;
						mtab.gene = ua.gene + 1;
						mtab.r.setTableNo(mtab.tabNo);
						mtab.r.setGen(mtab.gene);
					}
				}

				if (ua.r.getFatherPid() > 0 || ua.r.getMotherPid() > 0) {

					createAncestorTable(0, ftab.r, mtab.r,
							(ftab.r != null ? ftab.tabNo : mtab.tabNo));

					if (ftab.r != null) {
						genlistNext.add(ftab);
					}
					if (mtab.r != null) {
						genlistNext.add(mtab);
					}
				}
				// if (!firstTable && ua.r.getMotherPid() > 0) {
				// createAncestorTable(0, ftab.r, mtab.r, tabNo);
				// if (mtab.r != null) {
				// genlistNext.add(mtab);
				// }
				// }
				firstTable = false;
			}
			if (genlistNext.size() > 0) {
				genlist = genlistNext;
			} else {
				break;
			}
		}

		caller.setRunnerValue("100;OK");

	}

	private void createStradoReport() throws SukuException {
		textReferences = new HashMap<String, PersonInTables>();

		ReportUnit ftab;
		ReportUnit mtab;
		int i = 0;

		while (i < tables.size()) {

			ReportUnit tab = tables.get(i);
			ftab = null;
			mtab = null;

			if (i == 0) {
				ftab = tab;
			} else {

				if (i > 0 && tab.getTableNo() % 2 == 0) {
					ftab = tab;
					if (i < tables.size() - 1
							&& (tab.getTableNo() + 1 == tables.get(i + 1)
									.getTableNo())) {
						mtab = tables.get(i + 1);
					}

				} else {
					mtab = tab;
				}
			}

			createAncestorTable(i, ftab, mtab, tab.getTableNo());
			// (ftab != null) ? ftab.getTableNo() : mtab.getTableNo());
			// tabno = tab.getTableNo();
			// System.out.println("TAB: " + tabno);
			i++;
			if (ftab != null && mtab != null) {
				i++;
			}
		}

		caller.setRunnerValue("100;OK");

	}

	private void createEspolinReport() throws SukuException {
		textReferences = new HashMap<String, PersonInTables>();
		long currTab = 0;
		for (int i = 0; i < tables.size(); i++) {

			ReportUnit tab = tables.get(i);

			createAncestorTable(i, tab, null, (currTab == tab.getTableNo()) ? 0
					: tab.getTableNo());
			// createDescendantTable(i, tab);
			currTab = tab.getTableNo();
		}

		caller.setRunnerValue("100;OK");

	}

	/**
	 * Ancestor report family table is created here.
	 * 
	 * @param idx
	 *            the idx
	 * @param ftab
	 *            the ftab
	 * @param mtab
	 *            the mtab
	 * @param tableNum
	 *            the table num
	 * @throws SukuException
	 */
	protected void createAncestorTable(int idx, ReportUnit ftab,
			ReportUnit mtab, long tableNum) throws SukuException {
		BodyText bt = null;
		ReportTableMember subjectmember;
		SukuData pappadata = null;
		SukuData mammadata = null;
		ReportUnit mainTab = null;

		// long fatherTable = 0;
		// long motherTable = 0;
		//
		// long currTabNo = 0;
		boolean fams = caller.getAncestorPane().getShowfamily();
		String order = caller.getAncestorPane().getNumberingFormat()
				.getSelection().getActionCommand();
		if (!order.equals("ESPOLIN")
				&& caller.getAncestorPane().getAllBranches()) {
			// currTabNo = tableNum;
		}
		PersonLongData xdata = null;
		// UnitNotice[] xnotices = null;
		StringBuilder tabOwner = new StringBuilder();
		if (ftab != null) {
			// fatherTable = ftab.getTableNo();
			mainTab = ftab;
			subjectmember = ftab.getParent().get(0);

			try {
				pappadata = caller.getKontroller().getSukuData("cmd=person",
						"pid=" + subjectmember.getPid(),
						"lang=" + Resurses.getLanguage());
			} catch (SukuException e1) {
				logger.log(Level.WARNING, "background reporting", e1);
				JOptionPane.showMessageDialog(caller, e1.getMessage());
				return;
			}
			xdata = pappadata.persLong;

		}
		if (mtab != null) {
			// motherTable = mtab.getTableNo();
			subjectmember = mtab.getParent().get(0);

			try {
				mammadata = caller.getKontroller().getSukuData("cmd=person",
						"pid=" + subjectmember.getPid(),
						"lang=" + Resurses.getLanguage());
			} catch (SukuException e1) {
				logger.log(Level.WARNING, "background reporting", e1);
				JOptionPane.showMessageDialog(caller, e1.getMessage());
				return;
			}
			if (pappadata == null) {
				mainTab = mtab;
				xdata = mammadata.persLong;

			}

		}

		for (int i = 0; i < mainTab.getMemberCount(); i++) {
			ReportTableMember mem = mainTab.getMember(i);
			PersonInTables pt = personReferences.get(mem.getPid());
			if (pt != null) {
				if (mainTab.getTableNo() > 0) {
					pt.addOwner(mainTab.getTableNo());
				}
			}
		}

		for (int j = 0; j < xdata.getNotices().length; j++) {
			UnitNotice nn = xdata.getNotices()[j];
			if (nn.getTag().equals("NAME")) {
				tabOwner.append(nn.getSurname());
				if (tabOwner.length() > 0)
					tabOwner.append(" ");
				tabOwner.append(nn.getGivenname());
				break;
			}
		}

		float prose = (idx * 100f) / tables.size();
		caller.setRunnerValue("" + (int) prose + ";" + tableNum + ":"
				+ tabOwner.toString());

		bt = new TableHeaderText();
		String genText = "";
		if (mainTab.getGen() > 0) {
			genText = Roman.int2roman(mainTab.getGen());
		}

		if (mtab != null && ftab != null) {
			bt.addText(typesTable.getTextValue("TABLES") + " ");
			if (!genText.isEmpty()) {
				bt.addText(genText + ".");
			}
			bt.addAnchor("" + (ftab.getTableNo()));
			bt.addAnchor("" + (mtab.getTableNo()));
			bt.addText(" " + toPrintTable(ftab.getTableNo()));

			bt.addText(", " + toPrintTable(mtab.getTableNo()));
		} else {
			if (tableNum > 0) {
				bt.addText(typesTable.getTextValue("TABLE") + "\u00A0");
				if (!genText.isEmpty()) {
					bt.addText(genText + ".");
				}
				bt.addAnchor("" + (tableNum));
				bt.addText(" " + toPrintTable(tableNum));
			}
		}

		if (bt.getCount() > 0) {
			repoWriter.addText(bt);
		}
		ReportUnit tab;
		if (!order.equals("ESPOLIN")) {
			bt = new TableSubHeaderText();
			if (ftab != null) {
				tab = ftab;
			} else {
				tab = mtab;
			}

			HashMap<Long, Long> mp = new HashMap<Long, Long>();
			for (int j = 0; j < tab.getChild().size(); j++) {
				ReportTableMember mom = tab.getChild().get(j);

				long mytab = 0;

				ReportUnit ru = personTables.get(mom.getPid());
				if (ru != null) {
					mytab = ru.getTableNo();
					if (mytab > 0) {
						if (mp.get(mytab) == null) {
							mp.put(mytab, mytab);
						}
					}
				}

			}
			if (mp.size() > 0) {
				bt.addText("(");
				bt.addText(typesTable.getTextValue((mp.size() == 1 ? "FROMTABLE"
						: "FROMTABLES")));
				bt.addText(" ");

				Iterator<Long> ki = mp.keySet().iterator();
				boolean setComma = false;
				while (ki.hasNext()) {
					if (setComma) {
						bt.addText(",");
					}
					setComma = true;
					String tt = toPrintTable(ki.next(), true);
					bt.addLink(" " + tt, true, false, false, "" + tt);
					// bt.addText(toPrintTable(ki.next(), true));
				}

				bt.addText(")");
			}

			if (bt.getCount() > 0) {
				repoWriter.addText(bt);
			}
		}
		UnitNotice[] notices = null;
		int fid = 0;
		int mid = 0;

		if (ftab != null) {
			tab = ftab;
			fid = tab.getPid();
			notices = getInternalNotices(pappadata.persLong.getNotices());

			bt = new MainPersonText();
			if (genText.length() > 0) {
				bt.addText(genText);
				bt.addText(". ");
			}
			if (caller.showRefn() && xdata.getRefn() != null) {

				bt.addText(xdata.getRefn());
				bt.addText(" ");
			}
			if (caller.showGroup() && xdata.getGroupId() != null) {

				bt.addText(xdata.getGroupId());
				bt.addText(" ");
			}
			if (pappadata.persLong.getPrivacy() == null) {
				printName(bt, pappadata.persLong, 2);
				if (!order.equals("ESPOLIN")) {
					addParentReference(tab, bt);
				}
				printNotices(bt, pappadata.persLong, getTypeColumn(fid),
						ftab.getTableNo());
			} else {
				printNameNn(bt);
			}
		}
		if (bt.getCount() > 0) {
			repoWriter.addText(bt);

		}
		if (mtab != null) {
			tab = mtab;
			mid = mtab.getPid();
			if (ftab != null) {
				// now let's look for marriage info

				Relation marr = null;
				if (mammadata != null && mammadata.relations != null) {
					for (int i = 0; i < mammadata.relations.length; i++) {
						Relation rr = mammadata.relations[i];
						if (rr.getTag().equals("HUSB")
								&& fid == rr.getRelative()
								&& mid == rr.getPid()) {
							marr = rr;
							break;
						}
					}
				}
				if (marr != null) {
					if (marr.getNotices() != null) {

						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < marr.getNotices().length; i++) {
							RelationNotice rn = marr.getNotices()[i];

							String spouType = printRelationNotice(rn, "", 0,
									false);
							if (!spouType.isEmpty()) {
								sb.append(spouType);
								sb.append(". ");
							}
						}
						bt.addText("\n");
						bt.addText(sb.toString(), false, false, true);
						bt.addText("\n");

						repoWriter.addText(bt);

					}

				}

			}

			notices = getInternalNotices(mammadata.persLong.getNotices());

			bt = new MainPersonText();
			if (genText.length() > 0) {
				bt.addText(genText);
				bt.addText(". ");
			}
			if (caller.showRefn() && mammadata.persLong.getRefn() != null) {

				bt.addText(mammadata.persLong.getRefn());
				bt.addText(" ");
			}
			if (caller.showGroup() && mammadata.persLong.getGroupId() != null) {

				bt.addText(mammadata.persLong.getGroupId());
				bt.addText(" ");
			}

			if (mammadata.persLong.getPrivacy() == null) {
				printName(bt, mammadata.persLong, 2);
				if (!order.equals("ESPOLIN")) {
					addParentReference(tab, bt);
				}
				printNotices(bt, mammadata.persLong,
						getTypeColumn(mammadata.persLong.getPid()),
						mtab.getTableNo());
			} else {
				printNameNn(bt);
			}
		}

		if (bt.getCount() > 0) {
			repoWriter.addText(bt);

		}

		//
		// spouse list
		//
		if (order.equals("ESPOLIN")) {
			SukuData sdata;
			String fromTable;
			PersonInTables ref;
			tab = ftab;
			ReportTableMember spouseMember;
			for (int ispou = 1; ispou < tab.getParent().size(); ispou++) {
				bt = new MainPersonText();
				spouseMember = tab.getParent().get(ispou);
				int spouNum = 0;
				if (tab.getParent().size() > 2) {
					spouNum = ispou;

				}

				try {
					sdata = caller.getKontroller().getSukuData("cmd=person",
							"pid=" + spouseMember.getPid());
					String tmp;
					if ("M".equals(sdata.persLong.getSex())) {
						tmp = "HUSB";
					} else {
						tmp = "WIFE";
					}
					String spouType = typesTable.getTextValue(tmp);
					RelationNotice rnn[] = null;
					if (sdata.relations != null) {

						for (int i = 0; i < sdata.relations.length; i++) {
							if (sdata.relations[i].getRelative() == tab
									.getPid()) {
								if (sdata.relations[i].getNotices() != null) {
									rnn = sdata.relations[i].getNotices();

									RelationNotice rn = rnn[0];
									spouType = printRelationNotice(rn,
											spouType, spouNum, true);

								}

							}

						}
					}

					bt.addText("- ");
					bt.addText(spouType);
					bt.addText(" ");

					fromTable = "";
					int typesColumn = getTypeColumn(spouseMember.getPid());
					ref = personReferences.get(spouseMember.getPid());
					// if (ref != null) {
					// typesColumn = ref.getTypesColumn(tableNum, true, true,
					// false);
					// }

					notices = sdata.persLong.getNotices();
					if (sdata.persLong.getPrivacy() == null) {
						printName(bt, sdata.persLong, typesColumn);
						printNotices(bt, sdata.persLong, typesColumn, tableNum);
					} else {
						printNameNn(bt);
					}
					if (rnn != null && rnn.length > 1) {
						for (int i = 1; i < rnn.length; i++) {
							RelationNotice rn = rnn[i];
							spouType = printRelationNotice(rn, null, 0, false);
							if (spouType.length() > 0) {
								bt.addText(" ");
								bt.addText(spouType);
								bt.addText(".");
							}
						}
					}

					if (ref != null) {
						fromTable = "";
						if (ref.getOwnerArray().length > 0) {
							fromTable = "" + ref.getOwnerString(0);
						}
						// fromTable = ref.getReferences(tab.getTableNo(), true,
						// true, false);
						if (fromTable.length() > 0) {
							bt.addLink(typesTable.getTextValue("TABLE")
									+ "\u00A0" + fromTable + ". ", true, false,
									false, "" + fromTable);
							// bt.addText(typesTable.getTextValue("TABLE")
							// + "\u00A0" + fromTable + ". ", true, false);
						}
					}

					if (bt.getCount() > 0) {
						repoWriter.addText(bt);

					}

					for (int i = 0; i < spouseMember.getSubCount(); i++) {
						bt = new SubPersonText();
						bt.addText(spouseMember.getSubDadMom(i) + " ");
						SukuData sub = caller.getKontroller().getSukuData(
								"cmd=person",
								"pid=" + spouseMember.getSubPid(i));
						notices = sub.persLong.getNotices();
						if (sub.persLong.getPrivacy() == null) {
							printName(bt, sub.persLong, 4);
							printNotices(bt, sub.persLong, 4, tableNum);
						} else {
							printNameNn(bt);
						}
						fromTable = "";
						ref = personReferences.get(spouseMember.getSubPid(i));
						if (ref != null) {
							fromTable = ref.getReferences(tableNum, true, true,
									true, 0);
							if (fromTable.length() > 0) {
								// bt.addText(typesTable.getTextValue("ALSO")
								// + "\u00A0" + fromTable + ". ", true,
								// false);
								bt.addLink(typesTable.getTextValue("ALSO")
										+ "\u00A0" + fromTable + ". ", true,
										false, false, "" + fromTable);
							}
						}

						repoWriter.addText(bt);

					}

				} catch (SukuException e1) {
					logger.log(Level.WARNING, "background reporting", e1);

				}
			}
		}

		if (fams) {
			// ensin vanhempien yhteiset lapset
			// tai ainoan vanhemman lapset

			tab = ftab;
			if (tab == null) {
				tab = mtab;
			}
			ArrayList<ReportTableMember> full = new ArrayList<ReportTableMember>();

			for (int i = 0; i < tab.getChild().size(); i++) {
				ReportTableMember mem = tab.getChild().get(i);
				boolean addMe = false;
				if (ftab != null && mtab != null) {
					for (int j = 0; j < mtab.getChild().size(); j++) {
						ReportTableMember mom = mtab.getChild().get(j);
						if (mem.getPid() == mom.getPid()) {
							addMe = true;
							break;
						}
					}
				} else {
					addMe = true;
				}
				if (addMe) {
					full.add(mem);
				}
			}

			SukuData cdata;
			ReportTableMember childMember;

			String toTable = "";

			for (int ichil = 0; ichil < full.size(); ichil++) {
				if (ichil == 0) {
					bt = new ChildHeaderText();
					if (full.size() > 1) {
						bt.addText(typesTable.getTextValue("CHILDREN"));
					} else {
						bt.addText(typesTable.getTextValue("CHIL"));
					}
					bt.addText(":");
					repoWriter.addText(bt);

				}
				bt = new ChildListText();
				// childMember = tab.getChild().get(ichil);
				childMember = full.get(ichil);
				try {
					cdata = caller.getKontroller().getSukuData("cmd=person",
							"pid=" + childMember.getPid());

					String ownerTag = "FATH";

					// check if child is adopted
					String adopTag = null;
					for (int i = 0; i < cdata.relations.length; i++) {
						if (ownerTag.equals(cdata.relations[i].getTag())) {
							if (cdata.relations[i].getNotices() != null) {
								adopTag = cdata.relations[i].getNotices()[0]
										.getTag();

							}
							break;

						}
					}

					notices = cdata.persLong.getNotices();
					// if (!pareTxt.isEmpty()) {
					// bt.addText(pareTxt);
					// }
					if (adopTag != null) {
						bt.addText("(" + typesTable.getTextValue(adopTag)
								+ ") ");
					}
					if (cdata.persLong.getPrivacy() == null) {

						printName(bt, cdata.persLong, (toTable.isEmpty() ? 2
								: 3));

						addChildReference(ftab, mtab, cdata.persLong.getPid(),
								typesTable.getTextValue("TABLE"), bt);

						printNotices(bt, cdata.persLong,
								getTypeColumn(cdata.persLong.getPid()),
								tab.getTableNo());
					} else {
						printNameNn(bt);
					}
					// else {

					// }

					if (bt.getCount() > 0) {
						repoWriter.addText(bt);

					}
				} catch (SukuException e1) {
					logger.log(Level.WARNING, "SukuException", e1);
				}
			}
			//
			// now children for man and woman only
			//
			boolean isMale = true;
			String parentText;

			if (ftab != null && mtab != null) {
				while (true) {
					if (isMale) {
						parentText = typesTable.getTextValue("FATHERS");
						tab = ftab;
						isMale = false;
					} else {
						parentText = typesTable.getTextValue("MOTHERS");
						tab = mtab;
						isMale = true;
					}

					ArrayList<ReportTableMember> other = new ArrayList<ReportTableMember>();

					for (int i = 0; i < tab.getParent().size(); i++) {
						ReportTableMember mem = tab.getParent().get(i);

						boolean addMe = true;

						if (mem.getPid() == ftab.getPid()
								|| mem.getPid() == mtab.getPid()) {
							addMe = false;
						}

						if (addMe) {
							other.add(mem);
						}
					}

					for (int ichil = 0; ichil < other.size(); ichil++) {
						if (ichil == 0) {
							bt = new ChildHeaderText();
							bt.addText(parentText);
							bt.addText(" ");
							if (other.size() > 1) {
								bt.addText(typesTable.getTextValue("SPOUSES")
										.toLowerCase());
							} else {
								bt.addText(typesTable.getTextValue(
										(isMale) ? "WIFE" : "HUSB")
										.toLowerCase());
							}
							bt.addText(":");
							repoWriter.addText(bt);

						}
						bt = new ChildListText();
						childMember = other.get(ichil);

						try {
							cdata = caller.getKontroller()
									.getSukuData("cmd=person",
											"pid=" + childMember.getPid());

							notices = cdata.persLong.getNotices();
							if (cdata.persLong.getPrivacy() == null) {
								printName(bt, cdata.persLong,
										(toTable.isEmpty() ? 2 : 3));
								addChildReference(ftab, mtab, tab.getPid(),
										typesTable.getTextValue("TABLE"), bt);
								printNotices(bt, cdata.persLong, tab.getPid(),
										tab.getTableNo());
							} else {
								printNameNn(bt);
							}
							if (bt.getCount() > 0) {
								repoWriter.addText(bt);

							}
						} catch (SukuException e1) {
							logger.log(Level.WARNING, "SukuException", e1);
						}
					}

					other = new ArrayList<ReportTableMember>();

					for (int i = 0; i < tab.getChild().size(); i++) {
						ReportTableMember mem = tab.getChild().get(i);

						boolean addMe = true;

						for (int j = 0; j < full.size(); j++) {
							ReportTableMember mom = full.get(j);
							if (mem.getPid() == mom.getPid()) {
								addMe = false;
								break;
							}
						}
						if (addMe) {
							other.add(mem);
						}
					}

					for (int ichil = 0; ichil < other.size(); ichil++) {
						if (ichil == 0) {
							bt = new ChildHeaderText();
							bt.addText(parentText);
							bt.addText(" ");
							if (other.size() > 1) {
								bt.addText(typesTable.getTextValue("CHILDREN")
										.toLowerCase());
							} else {
								bt.addText(typesTable.getTextValue("CHIL")
										.toLowerCase());
							}
							bt.addText(":");
							repoWriter.addText(bt);

						}
						bt = new ChildListText();
						childMember = other.get(ichil);
						// childMember = tab.getChild().get(ichil);

						try {
							cdata = caller.getKontroller()
									.getSukuData("cmd=person",
											"pid=" + childMember.getPid());

							String ownerTag = "FATH";

							// check if child is adopted
							String adopTag = null;
							for (int i = 0; i < cdata.relations.length; i++) {
								if (ownerTag
										.equals(cdata.relations[i].getTag())) {
									if (cdata.relations[i].getNotices() != null) {
										adopTag = cdata.relations[i]
												.getNotices()[0].getTag();

									}
									break;

								}
							}

							notices = cdata.persLong.getNotices();

							if (adopTag != null) {
								bt.addText("("
										+ typesTable.getTextValue(adopTag)
										+ ") ");
							}
							if (cdata.persLong.getPrivacy() == null) {
								printName(bt, cdata.persLong,
										(toTable.isEmpty() ? 2 : 3));
								addChildReference(ftab, mtab, tab.getPid(),
										typesTable.getTextValue("TABLE"), bt);
								printNotices(bt, cdata.persLong, tab.getPid(),
										tab.getTableNo());
							} else {
								printNameNn(bt);
							}
							if (bt.getCount() > 0) {
								repoWriter.addText(bt);

							}
						} catch (SukuException e1) {
							logger.log(Level.WARNING, "SukuException", e1);
						}
					}

					if (isMale) {
						break;
					}

				}
			}

			repoWriter.addText(bt);
		}
	}

	/**
	 * Used to close / hide the report writer.
	 * 
	 * @param b
	 *            the new visible
	 */
	@Override
	public void setVisible(boolean b) {
		if (repoWriter instanceof JFrame) {
			JFrame ff = (JFrame) repoWriter;
			ff.setVisible(b);
		}

	}

	/**
	 * The Class ReportUnitAll.
	 */
	class ReportUnitAll {

		/** The tab no. */
		long tabNo = 0;

		/** The gene. */
		int gene = 0;

		/** The r. */
		ReportUnit r = null;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("tab(" + tabNo + ") ;");
			sb.append("gen(" + gene + ") ;");
			if (r != null) {
				sb.append("pid(" + r.getPid() + ") ;");
				sb.append("father(" + r.getFatherPid() + ") ;");
				sb.append("mother(" + r.getMotherPid() + ").");
			}
			return sb.toString();
		}
	}

}
