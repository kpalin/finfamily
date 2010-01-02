package fi.kaila.suku.report;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.report.style.ChildHeaderText;
import fi.kaila.suku.report.style.ChildListText;
import fi.kaila.suku.report.style.ImageText;
import fi.kaila.suku.report.style.MainPersonText;
import fi.kaila.suku.report.style.SubPersonText;
import fi.kaila.suku.report.style.TableHeaderText;
import fi.kaila.suku.report.style.TableSubHeaderText;
import fi.kaila.suku.swing.worker.ReportWorkerDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Roman;
import fi.kaila.suku.util.SukuException;
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
public class CommonReport {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	protected ReportWorkerDialog caller;

	protected Vector<ReportUnit> tables = new Vector<ReportUnit>();

	protected ReportInterface repoWriter;

	protected HashMap<Integer, PersonInTables> personReferences = null;

	protected HashMap<String, PersonInTables> textReferences = null;
	private int referencePid = 0;

	/**
	 * @return vector of tables
	 */
	public Vector<ReportUnit> getTables() {
		return tables;
	}

	/**
	 * @return hashmap with references
	 */
	public Vector<PersonInTables> getPersonReferences() {

		Vector<PersonInTables> vv = new Vector<PersonInTables>();

		Set<Map.Entry<Integer, PersonInTables>> entriesx = personReferences
				.entrySet();
		Iterator<Map.Entry<Integer, PersonInTables>> eex = entriesx.iterator();
		while (eex.hasNext()) {
			Map.Entry<Integer, PersonInTables> entrx = (Map.Entry<Integer, PersonInTables>) eex
					.next();

			PersonInTables pit = entrx.getValue();
			vv.add(pit);
		}

		Set<Map.Entry<String, PersonInTables>> entriesy = textReferences
				.entrySet();
		Iterator<Map.Entry<String, PersonInTables>> eey = entriesy.iterator();
		while (eey.hasNext()) {
			Map.Entry<String, PersonInTables> entry = (Map.Entry<String, PersonInTables>) eey
					.next();

			PersonInTables pit = entry.getValue();
			vv.add(pit);
		}

		return vv;
	}

	/**
	 * access to report writer
	 * 
	 * @return the report writer
	 */
	public ReportInterface getWriter() {
		return repoWriter;
	}

	protected CommonReport(ReportWorkerDialog caller, ReportInterface repoWriter) {
		this.caller = caller;
		this.repoWriter = repoWriter;
	}

	/**
	 * Taulun rakentaminen tulosteeseen
	 * 
	 * tab sisältää henkilön ReportTableMember[0] puolisoiden
	 * ReportTableMember[1...n] lasten ReportTableMember[0..n-1]
	 * 
	 * persLong sisältää FullPerson katso
	 * fi.kaila.suku.server.SukuServerImpl.getFullPerson
	 * 
	 * personReferences sisältää kaikkien henkilöiden esiintymiset
	 * taulurakenteessa
	 * 
	 * @param idx
	 * @param tab
	 */
	protected void createTable(int idx, ReportUnit tab) {

		BodyText bt = null;
		ReportTableMember subjectmember = tab.getParent().get(0);
		SukuData pdata = null;
		StringBuffer tabOwner = new StringBuffer();
		try {
			pdata = caller.getKontroller().getSukuData("cmd=person",
					"pid=" + subjectmember.getPid(),
					"lang=" + Resurses.getLanguage());
		} catch (SukuException e1) {
			logger.log(Level.WARNING, "background reporting", e1);
			JOptionPane.showMessageDialog(caller, e1.getMessage());
			return;
		}
		if (pdata.persLong == null)
			return;

		UnitNotice[] xnotices = pdata.persLong.getNotices();

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

		UnitNotice[] tableNotices = new UnitNotice[tableCount];
		UnitNotice[] famtNotices = new UnitNotice[famtCount];

		int xn = 0;
		int xtable = 0;
		int xfamt = 0;

		for (int i = 0; i < xnotices.length; i++) {
			if (xnotices[i].getTag().equals("FAMT")) {
				famtNotices[xfamt++] = xnotices[i];
			} else if (xnotices[i].getTag().equals("TABLE")) {
				tableNotices[xtable++] = xnotices[i];
			} else {
				notices[xn++] = xnotices[i];
			}
		}

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			if (nn.getTag().equals("NAME")) {
				tabOwner.append(nn.getSurname());
				if (tabOwner.length() > 0)
					tabOwner.append(" ");
				tabOwner.append(nn.getGivenname());
				break;
			}
		}
		float prose = (idx * 100f) / tables.size();
		caller.setRunnerValue("" + (int) prose + ";" + tab.getTableNo() + ":"
				+ tabOwner);

		String genText = "";
		if (tab.getGen() > 0) {
			genText = Roman.int2roman(tab.getGen());
		}
		bt = new TableHeaderText();

		bt.addText(caller.getTypeText("TABLE"));
		bt.addText(" " + tab.getTableNo());
		repoWriter.addText(bt);

		bt = new TableSubHeaderText();
		PersonInTables ref;
		String fromTable = "";
		String fromSubTable = "";
		ref = personReferences.get(tab.getPid());
		if (ref != null) {
			fromTable = ref.getReferences(tab.getTableNo(), false, true, false);
		}

		if (fromTable.length() > 0) {
			bt.addText(caller.getTextValue("FROMTABLE") + " " + fromTable);
		}

		repoWriter.addText(bt);

		if (tableNotices.length > 0) {
			bt = new BodyText();
			printNotices(bt, tableNotices, 2, tab.getTableNo());
			repoWriter.addText(bt);
		}
		bt = new MainPersonText();
		if (genText.length() > 0) {
			bt.addText(genText);
			bt.addText(". ");
		}

		printName(bt, notices, 2);
		printNotices(bt, notices, 2, tab.getTableNo());

		fromTable = "";
		ref = personReferences.get(tab.getPid());
		if (ref != null) {
			fromTable = ref.getReferences(tab.getTableNo(), true, false, false);
		}
		if (fromTable.length() == 0) {

			fromTable = ref.getReferences(tab.getTableNo(), false, false, true);
		}
		if (fromTable.length() > 0) {
			bt.addText(caller.getTextValue("ALSO") + " " + fromTable + ". ",
					true, false);
		}

		if (bt.getCount() > 0) {
			repoWriter.addText(bt);

		}
		//
		// spouse list
		// 

		SukuData sdata;
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
				String spouType = caller.getTextValue(tmp);
				RelationNotice rnn[] = null;
				if (sdata.relations != null) {

					for (int i = 0; i < sdata.relations.length; i++) {
						if (sdata.relations[i].getRelative() == tab.getPid()) {
							if (sdata.relations[i].getNotices() != null) {
								rnn = sdata.relations[i].getNotices();

								RelationNotice rn = rnn[0];
								spouType = printRelationNotice(rn, spouType,
										spouNum);

							}

						}

					}
				}

				bt.addText("- ");
				bt.addText(spouType);
				bt.addText(" ");

				fromTable = "";
				int typesColumn = 2;
				ref = personReferences.get(spouseMember.getPid());
				if (ref != null) {
					typesColumn = ref.getTypesColumn(tab.getTableNo(), true,
							true, false);
				}

				notices = sdata.persLong.getNotices();
				printName(bt, notices, typesColumn);
				printNotices(bt, notices, typesColumn, tab.getTableNo());

				if (rnn != null && rnn.length > 1) {
					for (int i = 1; i < rnn.length; i++) {
						RelationNotice rn = rnn[i];
						spouType = printRelationNotice(rn, null, 0);
						if (spouType.length() > 0) {
							bt.addText(" ");
							bt.addText(spouType);
							bt.addText(".");
						}
					}
				}

				if (ref != null) {
					fromTable = ref.getReferences(tab.getTableNo(), true, true,
							false);
					if (fromTable.length() > 0) {
						bt.addText(caller.getTextValue("ALSO") + " "
								+ fromTable + ". ", true, false);
					}
				}

				if (bt.getCount() > 0) {
					repoWriter.addText(bt);

				}

				for (int i = 0; i < spouseMember.getSubCount(); i++) {
					bt = new SubPersonText();
					bt.addText(spouseMember.getSubDadMom(i) + " ");
					SukuData sub = caller.getKontroller().getSukuData(
							"cmd=person", "pid=" + spouseMember.getSubPid(i));
					notices = sub.persLong.getNotices();
					printName(bt, notices, 4);
					printNotices(bt, notices, 4, tab.getTableNo());

					fromTable = "";
					ref = personReferences.get(spouseMember.getSubPid(i));
					if (ref != null) {
						fromTable = ref.getReferences(tab.getTableNo(), true,
								true, true);
						if (fromTable.length() > 0) {
							bt.addText(caller.getTextValue("ALSO") + " "
									+ fromTable + ". ", true, false);
						}
					}

					repoWriter.addText(bt);

				}

			} catch (SukuException e1) {
				logger.log(Level.WARNING, "background reporting", e1);

			}
		}

		if (tab.getChild().size() > 0) {
			bt = new ChildHeaderText();
			genText = Roman.int2roman(tab.getGen() + 1);
			bt.addText(genText + ". ");
			bt.addText(caller.getTextValue("Chil"));
			repoWriter.addText(bt);

		}
		//
		// child list
		//
		SukuData cdata;
		ReportTableMember childMember;
		ReportTableMember childSpouseMember;
		String toTable = "";
		boolean hasOwnTable = false;
		for (int ichil = 0; ichil < tab.getChild().size(); ichil++) {
			bt = new ChildListText();
			childMember = tab.getChild().get(ichil);

			try {
				cdata = caller.getKontroller().getSukuData("cmd=person",
						"pid=" + childMember.getPid());
				ref = personReferences.get(childMember.getPid());
				toTable = "";
				hasOwnTable = false;
				if (ref != null) {
					toTable = ref.getReferences(0, true, false, false);
					if (!toTable.equals("") && ref.asOwner > 0) {
						hasOwnTable = true;
					}

				}

				//
				// let's try to find mother here
				//
				String paretag = "MOTH";
				String ownerTag = "FATH";
				int bid = 0;
				if (pdata.persLong.getSex().equals("F")) {
					paretag = "FATH";
					ownerTag = "MOTH";
				}
				for (int i = 0; i < cdata.relations.length; i++) {
					if (paretag.equals(cdata.relations[i].getTag())) {
						bid = cdata.relations[i].getRelative();
						break;

					}
				}
				String pareTxt = "";
				if (bid > 0) {
					ReportTableMember pareMember;
					for (int isp = 1; isp < tab.getParent().size(); isp++) {
						pareMember = tab.getParent().get(isp);
						if (pareMember.getPid() == bid) {
							if (tab.getParent().size() <= 2) {
								pareTxt = "";
							} else {
								pareTxt = "" + isp + ": ";
							}
							break;
						}

					}
				}
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
				if (!pareTxt.equals("")) {
					bt.addText(pareTxt);
				}
				if (adopTag != null) {
					bt.addText("(" + caller.getTextValue(adopTag) + ") ");
				}
				printName(bt, notices, (toTable.equals("") ? 2 : 3));
				printNotices(bt, notices, (toTable.equals("") ? 2 : 3), tab
						.getTableNo());

				if (childMember.getSubCount() > 0) {
					repoWriter.addText(bt);

					for (int i = 0; i < childMember.getSubCount(); i++) {
						bt = new SubPersonText();
						bt.addText(childMember.getSubDadMom(i) + " ");
						SukuData sub = caller.getKontroller()
								.getSukuData("cmd=person",
										"pid=" + childMember.getSubPid(i));
						notices = sub.persLong.getNotices();
						printName(bt, notices, 4);
						printNotices(bt, notices, 4, tab.getTableNo());

						fromSubTable = "";
						ref = personReferences.get(childMember.getSubPid(i));
						if (ref != null) {
							fromSubTable = ref.getReferences(tab.getTableNo(),
									true, true, true);
							if (fromSubTable.length() > 0) {
								bt.addText(caller.getTextValue("ALSO") + " "
										+ fromSubTable + ". ", true, false);
							}
						}

						repoWriter.addText(bt);

					}
				}

				if (!toTable.equals("")) {
					if (hasOwnTable) {
						bt.addText(caller.getTextValue("TABLE") + " " + toTable
								+ ". ", true, false);
					} else {
						bt.addText(caller.getTextValue("ALSO") + " " + toTable
								+ ". ", true, false);
					}
					repoWriter.addText(bt);

				}

				// else {
				if (childMember.getSpouses() != null
						&& childMember.getSpouses().length > 0) {

					for (int j = 0; j < childMember.getSpouses().length; j++) {
						childSpouseMember = childMember.getSpouses()[j];
						SukuData child = caller.getKontroller().getSukuData(
								"cmd=person",
								"pid=" + childSpouseMember.getPid());
						String tmp;
						if ("M".equals(child.persLong.getSex())) {
							tmp = "HUSB";
						} else {
							tmp = "WIFE";
						}
						String spouType = caller.getTextValue(tmp);

						int spouNum = 0;
						if (childMember.getSpouses().length > 1) {
							spouNum = j + 1;

						}

						RelationNotice rnn[] = null;
						if (child.relations != null) {

							for (int i = 0; i < child.relations.length; i++) {
								if (child.relations[i].getRelative() == childMember
										.getPid()) {
									if (child.relations[i].getNotices() != null) {
										rnn = child.relations[i].getNotices();
										for (int jj = 0; jj < rnn.length; jj++) {
											RelationNotice rn = rnn[jj];
											spouType = printRelationNotice(rn,
													spouType, spouNum);
										}
									}
								}

							}
						}

						bt.addText("- ");
						bt.addText(spouType);
						bt.addText(" ");

						notices = child.persLong.getNotices();
						int typesColumn = 2;
						ref = personReferences.get(childSpouseMember.getPid());
						if (ref != null) {
							typesColumn = ref.getTypesColumn(tab.getTableNo(),
									true, true, false);
						}

						printName(bt, notices, typesColumn);
						printNotices(bt, notices, typesColumn, tab.getTableNo());

						if (rnn != null && rnn.length > 1) {
							for (int i = 1; i < rnn.length; i++) {
								RelationNotice rn = rnn[i];

								// spouType = printRelationNotice(rn,
								// spouType, spouNum);
								//									

								spouType = printRelationNotice(rn, null, 0);
								if (spouType.length() > 0) {
									bt.addText(" ");
									bt.addText(spouType);
									bt.addText(".");
								}
							}
						}

						fromTable = "";
						// ref =
						// personReferences.get(child.persLong.getPid()) ;
						if (ref != null) {
							fromTable = ref.getReferences(tab.getTableNo(),
									true, false, false);
							if (fromTable.equals("")) {
								fromTable = ref.getReferences(tab.getTableNo(),
										false, true, false);
							}
							if (fromTable.equals("")) {
								fromTable = ref.getReferences(tab.getTableNo(),
										false, false, true);
							}
							if (fromTable.length() > 0) {
								bt.addText(caller.getTextValue("ALSO") + " "
										+ fromTable + ". ", true, false);
							}
						}
						if (bt.getCount() > 0) {
							repoWriter.addText(bt);

						}

						if (childSpouseMember.getSubCount() > 0) {

							for (int i = 0; i < childSpouseMember.getSubCount(); i++) {
								bt = new SubPersonText();
								bt.addText(childSpouseMember.getSubDadMom(i)
										+ " ");
								SukuData sub = caller.getKontroller()
										.getSukuData(
												"cmd=person",
												"pid="
														+ childSpouseMember
																.getSubPid(i));
								notices = sub.persLong.getNotices();
								printName(bt, notices, 4);
								printNotices(bt, notices, 4, tab.getTableNo());

								fromTable = "";
								ref = personReferences.get(childSpouseMember
										.getSubPid(i));
								if (ref != null) {
									fromTable = ref.getReferences(tab
											.getTableNo(), true, true, true);
									if (fromTable.length() > 0) {
										bt.addText(caller.getTextValue("ALSO")
												+ " " + fromTable + ". ", true,
												false);
									}
								}

								repoWriter.addText(bt);

							}
						}
					}
				}
				// }

				if (bt.getCount() > 0) {
					repoWriter.addText(bt);

				}
			} catch (SukuException e1) {
				logger.log(Level.WARNING, "SukuException", e1);
			}
		}
		if (famtNotices.length > 0) {
			bt = new BodyText();
			printNotices(bt, famtNotices, 2, tab.getTableNo());
			repoWriter.addText(bt);
		}
	}

	private String printRelationNotice(RelationNotice rn, String defType,
			int spouseNum) {

		String showType = caller.getSpouseData().getSelection()
				.getActionCommand();
		if (showType == null) {
			showType = ReportWorkerDialog.SET_SPOUSE_NONE;
		}

		StringBuffer sb = new StringBuffer();
		boolean addSpace = false;

		if (rn.getType() != null) {
			sb.append(rn.getType());
		} else if (defType != null) {
			sb.append(defType);
		}
		if (sb.length() == 0 && rn.getTag() != null) {
			sb.append(caller.getTextValue(rn.getTag()));
		}

		if (sb.length() > 0) {
			if (spouseNum > 0) {
				sb.append(" " + spouseNum + ":o");
			}
			addSpace = true;
		}

		if (showType.equals(ReportWorkerDialog.SET_SPOUSE_NONE)) {
			return sb.toString();
		}
		if (showType.equals(ReportWorkerDialog.SET_SPOUSE_YEAR)) {

			String yr = rn.getFromDate();
			if (yr != null && yr.length() >= 4) {
				if (addSpace) {
					sb.append(" ");
				}
				sb.append(yr.substring(0, 4));
			}

			return sb.toString();
		}

		if (showType.equals(ReportWorkerDialog.SET_SPOUSE_DATE)) {
			String yr = rn.getFromDate();
			if (yr != null && yr.length() >= 4) {

				String date = printDate(rn.getDatePrefix(), rn.getFromDate(),
						rn.getToDate());
				if (date.length() > 0) {
					if (addSpace) {
						sb.append(" ");
					}
					sb.append(date);
				}
			}

			return sb.toString();
		}

		String date = printDate(rn.getDatePrefix(), rn.getFromDate(), rn
				.getToDate());
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
			sb.append(rn.getNoteText());
			addSpace = true;
		}

		return sb.toString();
	}

	private void printNotices(BodyText bt, UnitNotice[] notices, int colType,
			long tableNo) {
		boolean addSpace = false;
		boolean addDot = false;
		String tag;

		if (notices.length > 0 && caller.showOnSeparateLines()) {

			repoWriter.addText(bt);
		}

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			addSpace = false;
			addDot = false;
			tag = nn.getTag();
			if (!tag.equals("NAME")) {
				if (nn.getPrivacy() == null) {
					if ((caller.isType(tag, colType))) {

						if (caller.isType(tag, 1)) {

							String noType = nn.getNoticeType();
							if (noType != null) {
								bt.addText(noType);
							} else {
								bt.addText(caller.getTypeText(tag));
							}
							addSpace = true;
							addDot = true;
						}
						if (nn.getDescription() != null) {
							if (addSpace)
								bt.addText(" ");
							bt.addText(nn.getDescription());
							addSpace = true;
							addDot = true;
						}

						String dd = printDate(nn.getDatePrefix(), nn
								.getFromDate(), nn.getToDate());
						if (dd.length() > 0) {
							if (addSpace) {
								bt.addText(" ");
								bt.addText(dd);
							}
							addSpace = true;
							addDot = true;
						}
						if (nn.getPlace() != null) {
							if (addSpace)
								bt.addText(" ");
							bt.addText(convertPlace(tag, nn.getPlace()));
							addSpace = true;
							addDot = true;
						}
						if (nn.getState() != null) {
							if (addSpace) {
								bt.addText(",");
								addSpace = true;
								addDot = true;
							}
							bt.addText(nn.getState());
						}
						if (nn.getCountry() != null) {
							if (addSpace) {
								bt.addText(",");
								addSpace = true;
								addDot = true;
							}
							bt.addText(nn.getCountry());
						}

						if (nn.getMediaFilename() != null
								&& caller.showImages()) {
							ImageText imagetx = new ImageText();
							BufferedImage img = nn.getMediaImage();
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

								Image imgs = img.getScaledInstance((int) neww,
										(int) newh, Image.SCALE_DEFAULT);

								imagetx.setImage(imgs, nn.getMediaData(), img
										.getWidth(), img.getHeight(), nn
										.getMediaFilename(),
										nn.getMediaTitle(), nn.getTag());
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

						if (nn.getNoteText() != null) {

							if (addSpace) {
								if (nn.getNoteText().charAt(0) != ','
										&& nn.getNoteText().charAt(0) != '.') {
									bt.addText(" ");
								}
							}
							int tlen = printText(bt, nn.getNoteText());
							if (tlen > 0) {
								addSpace = true;
								addDot = true;
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
										ppText.shortPerson = new PersonShortData(
												refpid, parts[1], null, null,
												parts[0], null, null, null);
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
								String parts[] = nn.getAddress().replaceAll(
										"\\r", "").split("\n");
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

						if (addDot) {
							bt.addText(". ");
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

	/**
	 * Set 1st paragraph bold (header) if it begins with *
	 * 
	 * @param bt
	 * @param text
	 * @return
	 */
	private int printText(BodyText bt, String text) {
		if (text == null)
			return 0;

		StringBuffer sb = new StringBuffer();
		Vector<String> v = new Vector<String>();
		boolean wasWhite = false;
		boolean wasNl = false;
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
			if (i == 0 && aux.charAt(0) == '*') {
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

	private String convertPlace(String tag, String place) {

		// TODO convert here
		return place;
	}

	private String printDate(String datePrefix, String dateFrom, String dateTo) {
		String mellan = "";
		String framme = "";
		if (dateFrom == null)
			return "";
		StringBuffer sb = new StringBuffer();

		if (datePrefix != null) {
			framme = caller.getTextValue(datePrefix);
			if (datePrefix.equals("FROM") && dateTo != null) {
				mellan = caller.getTextValue("TO");
			}
			if (datePrefix.equals("BET") && dateTo != null) {
				mellan = caller.getTextValue("AND");
			}
		}
		if (!framme.equals("")) {
			sb.append(framme);
			sb.append(" ");
		}
		sb.append(toRepoDate(dateFrom));
		if (!mellan.equals("") && dateTo != null) {
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

		if (date.length() == 4) {
			return date;
		} else if (date.length() >= 6) {
			yy = Integer.parseInt(date.substring(0, 4));
			mm = Integer.parseInt(date.substring(4, 6));
		}
		if (date.length() == 8) {
			dd = Integer.parseInt(date.substring(6, 8));
		}
		if (df.equals("SE")) {
			if (dd == 0) {
				return "" + date.substring(0, 4) + "-" + date.substring(4, 6);
			} else {
				return "" + date.substring(0, 4) + "-" + date.substring(4, 6)
						+ "-" + date.substring(6, 8);
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
	private void printName(BodyText bt, UnitNotice[] notices, int colType) {
		int nameCount = 0;
		String prevGivenname = "";

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			if (nn.getTag().equals("NAME")) {
				if (nn.getPrivacy() == null) {
					if ((caller.isType("NAME", colType) || nameCount == 0)) {

						if (nameCount > 0) {
							bt.addText(". ");
							if (caller.isType("NAME", 1)) {
								bt.addText(caller.getTypeText("NAME"));
								bt.addText(" ");
							}
						}
						if (!prevGivenname.equals(nv(nn.getGivenname()))) {
							prevGivenname = nv(nn.getGivenname());

							printGivenname(bt, prevGivenname);

						}
						boolean wasName = false;
						if (!prevGivenname.equals("")
								&& !nv(nn.getPrefix()).equals("")) {
							bt.addText(" ", caller.showBoldNames(), false);
							bt.addText(nn.getPrefix(), caller.showBoldNames(),
									false);
							wasName = true;
						}
						if (wasName && !nv(nn.getPatronym()).equals("")) {
							bt.addText(" ", caller.showBoldNames(), false);
						}

						if (!nv(nn.getPatronym()).equals("")) {
							bt.addText(nn.getPatronym(),
									caller.showBoldNames(), false);
							wasName = true;
						}
						if (wasName && !nv(nn.getSurname()).equals("")) {
							bt.addText(" ", caller.showBoldNames(), false);
						}
						if (!nv(nn.getSurname()).equals("")) {
							bt.addText(nn.getSurname(), caller.showBoldNames(),
									false);
							wasName = true;
						}
						if (wasName && !nv(nn.getPostfix()).equals("")) {
							bt.addText(" ", caller.showBoldNames(), false);
						}
						if (!nv(nn.getPostfix()).equals("")) {
							bt.addText(nn.getPostfix(), caller.showBoldNames(),
									false);
						}

					}

					nameCount++;
				}
			}
		}

		bt.addText(". ");
	}

	private void printGivenname(BodyText bt, String prevGivenname) {
		String[] nameParts = prevGivenname.split(" ");
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
			if (!startChar.equals("")) {
				bt.addText(startChar, caller.showBoldNames(), false);
			}
			String[] subParts = namePart.split("-");

			int astidx = namePart.indexOf("*");
			int bstidx = namePart.indexOf("**");
			if (bstidx > 0 || subParts.length == 1) {
				if (astidx > 0) {

					if (astidx == namePart.length() - 1) {
						bt.addText(namePart.substring(0, astidx), caller
								.showBoldNames(), caller.showUnderlineNames());
					} else {

						if (bstidx > 0) {
							if (bstidx == namePart.length() - 2) {

								bt.addText(namePart.substring(0, bstidx),
										caller.showBoldNames(), caller
												.showUnderlineNames());
							}
						}
					}
				} else {
					bt.addText(namePart, caller.showBoldNames(), false);
				}
			} else {

				for (int kk = 0; kk < subParts.length; kk++) {
					String subPart = subParts[kk];
					int cstidx = subPart.indexOf("*");
					if (kk > 0) {
						bt.addText("-", caller.showBoldNames(), false);
					}
					if (cstidx == subPart.length() - 1) {

						bt.addText(subPart.substring(0, cstidx), caller
								.showBoldNames(), caller.showUnderlineNames());

					} else {
						bt.addText(subPart, caller.showBoldNames(), false);
					}
				}
			}

			if (!endChar.equals("")) {
				bt.addText(endChar, caller.showBoldNames(), false);
			}
			bt.addText(" ", caller.showBoldNames(), false);
		}
	}

	protected String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

}
