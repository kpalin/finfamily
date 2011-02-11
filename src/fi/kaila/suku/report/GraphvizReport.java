package fi.kaila.suku.report;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.SukuData;

public class GraphvizReport extends CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private int reportType = 0;

	private String filePath = null;
	private static String FOLDER_NAME = "Graphviz_images";
	private final String imageMagickPath;

	public GraphvizReport(ReportWorkerDialog caller, SukuTypesTable typesTable,
			int reportType) throws SukuException {
		super(caller, typesTable, null);
		imageMagickPath = Suku.kontroller.getPref(caller.getSukuParent(),
				"IMAGEMAGICK", "");
		this.reportType = reportType;

		if (!Suku.kontroller.createLocalFile("gv")) {
			throw new SukuException(
					Resurses.getString("WARN_REPORT_NOT_SELECTED"));
		}

		String path = Suku.kontroller.getFilePath();

		int pIdx = path.lastIndexOf("/");
		filePath = path.substring(0, pIdx);

	}

	LinkedHashMap<String, PersonShortData> identMap = null;
	LinkedHashMap<String, String> relaMap = null;

	@Override
	public void executeReport() throws SukuException {
		int descgen = caller.getDescendantPane().getGenerations();
		int ancgen = caller.getAncestorPane().getGenerations();
		boolean includeFamily = caller.getAncestorPane().getShowfamily();
		boolean includeAdopted = caller.getDescendantPane().getAdopted();
		boolean underlineName = caller.showUnderlineNames();
		Dimension maxPersonImageSize = caller.getPersonImageMaxSize();
		if (maxPersonImageSize.width == 0) {
			maxPersonImageSize = new Dimension(100, 150);
		} else {
			if (maxPersonImageSize.height == 0) {
				maxPersonImageSize.height = maxPersonImageSize.width * 3 / 2;
			}
		}
		boolean nameShow = typesTable.isType("NAME", 2);
		boolean birtShow = typesTable.isType("BIRT", 2);
		boolean deatShow = typesTable.isType("DEAT", 2);
		boolean occuShow = typesTable.isType("OCCU", 2);
		boolean pictShow = typesTable.isType("PHOT", 2);
		if (!caller.showImages()) {
			pictShow = false;
		}
		if (pictShow) {
			File d = new File(filePath + "/" + FOLDER_NAME);
			if (d.exists()) {
				// if (d.isDirectory()) {
				// int resu = JOptionPane.showConfirmDialog(caller,
				// Resurses.getString("CONFIRM_REPLACE_REPORTDIR"),
				// Resurses.getString(Resurses.SUKU),
				// JOptionPane.YES_NO_OPTION,
				// JOptionPane.QUESTION_MESSAGE);
				// if (resu == JOptionPane.YES_OPTION) {
				if (d.isDirectory()) {
					String[] files = d.list();
					for (int i = 0; i < files.length; i++) {
						File df = new File(filePath + "/" + files[i]);
						df.delete();
					}
				}
				// }
				// }
			}
			d.mkdirs();
		}

		identMap = new LinkedHashMap<String, PersonShortData>();
		relaMap = new LinkedHashMap<String, String>();

		// repoWriter.createReport();

		try {
			if (caller.getPid() > 0) {
				// caller.getDescendantPane().getGenerations();
				SukuData pdata = caller.getKontroller().getSukuData(
						"cmd=person", "pid=" + caller.getPid(),
						"lang=" + Resurses.getLanguage());
				GraphData subj = new GraphData(pdata);

				identMap.put("I" + subj.getPid(), subj);
				if (reportType == 0) {
					addDescendantRelatives(subj, descgen - 1, includeAdopted);
				} else if (reportType == 1) {
					addAncestorRelatives(subj, ancgen - 1, includeFamily);
				} else if (reportType == 2) {
					addRelations(subj);
				} else {
					throw new SukuException("BAD REPORT TYPE");
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();

				bos.write("graph G {\n".getBytes("UTF-8"));
				Set<Map.Entry<String, PersonShortData>> setti = identMap
						.entrySet();

				Iterator<Map.Entry<String, PersonShortData>> it = setti
						.iterator();

				while (it.hasNext()) {
					int lineCount = 1;
					Map.Entry<String, PersonShortData> entry = it.next();
					StringBuilder sb = new StringBuilder();
					PersonShortData pp = entry.getValue();
					sb.append("I" + pp.getPid());
					sb.append(" [shape=");
					if (pp.getSex().equals("M")) {
						sb.append("box,color=blue");
					} else {
						sb.append("ellipse,color=red");
					}
					sb.append(",style=bold,label=\"");
					if (nameShow) {
						if (pp.getGivenname() != null) {
							if (!underlineName) {
								StringBuilder sbx = new StringBuilder();
								for (int l = 0; l < pp.getGivenname().length(); l++) {
									char c = pp.getGivenname().charAt(l);
									if (c != '*') {
										sbx.append(c);
									}
								}
								sb.append(sbx.toString());

							} else {
								String parts[] = pp.getGivenname().split(" ");
								String etunimi = parts[0];
								for (int n = 0; n < parts.length; n++) {
									if (parts[n].indexOf("*") > 0) {
										etunimi = parts[n];
										break;
									}
								}
								StringBuilder sbs = new StringBuilder();
								for (int m = 0; m < etunimi.length(); m++) {
									char c = etunimi.charAt(m);
									if (c != '*') {
										sbs.append(c);
									}
								}
								sb.append(sbs.toString());
							}
						}
						if (pp.getPatronym() != null) {
							sb.append(" ");
							sb.append(pp.getPatronym());
						}

						if (pp.getPrefix() != null) {
							sb.append(" ");
							sb.append(pp.getPrefix());

						}
						if (pp.getSurname() != null) {
							sb.append(" ");
							sb.append(pp.getSurname());
						}
						if (pp.getPostfix() != null) {
							sb.append(" ");
							sb.append(pp.getPostfix());
						}
					} else {
						// don't show name. Show initials only
						if (pp.getGivenname() != null) {
							String parts[] = pp.getGivenname().split(" ");
							String firstpart = parts[0];
							for (int i1 = 0; i1 < parts.length; i1++) {
								if (parts[i1].indexOf("*") > 0) {
									firstpart = parts[i1];
									break;
								}
							}
							sb.append(firstpart.charAt(0));

						}
						if (pp.getPrefix() != null) {
							sb.append(pp.getPrefix().charAt(0));
						}
						if (pp.getSurname() != null) {
							sb.append(pp.getSurname().charAt(0));
						}
					}
					if (birtShow) {
						if (pp.getBirtDate() != null
								|| pp.getBirtPlace() != null) {
							sb.append("\\n");
							lineCount++;
							sb.append(typesTable.getTextValue("ANC_BORN"));
							if (pp.getBirtDate() != null) {
								sb.append(" ");
								sb.append(Utils.textDate(pp.getBirtDate(), true));
							}
							if (pp.getBirtPlace() != null) {
								sb.append(" ");
								sb.append(pp.getBirtPlace());
							}
							if (pp.getBirthCountry() != null) {
								sb.append(" ");
								sb.append(pp.getBirthCountry());
							}

						}
					}
					if (deatShow) {
						if (pp.getDeatDate() != null
								|| pp.getDeatPlace() != null) {
							sb.append("\\n");
							lineCount++;
							sb.append(typesTable.getTextValue("ANC_DIED"));
							if (pp.getDeatDate() != null) {
								sb.append(" ");
								sb.append(Utils.textDate(pp.getDeatDate(), true));
							}
							if (pp.getDeatPlace() != null) {
								sb.append(" ");
								sb.append(pp.getDeatPlace());
							}
							if (pp.getDeatCountry() != null) {
								sb.append(" ");
								sb.append(pp.getDeatCountry());
							}

						}
					}
					if (occuShow) {
						if (pp.getOccupation() != null) {
							sb.append("\\n");
							lineCount++;
							sb.append(pp.getOccupation());
						}
					}
					sb.append("\"");
					if (pictShow) {
						if (pp.getMediaData() != null) {
							if (pp.getMediaFilename() != null) {

								BufferedImage img = pp.getImage();
								BufferedImage imgStamp = Utils.scaleImage(
										imageMagickPath, img,
										maxPersonImageSize.width,
										maxPersonImageSize.height,
										lineCount * 25);

								// O P E N
								// converting to bytes : copy-paste from
								// http://mindprod.com/jgloss/imageio.html#TOBYTES
								ByteArrayOutputStream baos = new ByteArrayOutputStream(
										1000);
								byte[] resultImageAsRawBytes = null;
								// W R I T E
								try {
									ImageIO.write(imgStamp, "jpeg", baos);
									// C L O S E
									baos.flush();
									img.flush();
									resultImageAsRawBytes = baos.toByteArray();

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								String imgName = pp.getMediaFilename();

								File ff = new File(filePath + "/" + FOLDER_NAME
										+ "/" + imgName);

								FileOutputStream fos;
								try {

									fos = new FileOutputStream(ff);
									// fos.write(pp.getMediaData());
									fos.write(resultImageAsRawBytes);
									fos.close();

								} catch (FileNotFoundException e) {
									logger.log(Level.WARNING, "Image", e);
								} catch (IOException e) {
									logger.log(Level.WARNING, "Image", e);
								}
							}

							sb.append(",image=\"");
							sb.append(FOLDER_NAME);
							sb.append("/");
							sb.append(pp.getMediaFilename());
							sb.append("\"");
							sb.append(",labelloc=b");
						}
					}

					sb.append("];");

					bos.write(sb.toString().getBytes("UTF-8"));
					bos.write('\n');

				}
				Set<Map.Entry<String, String>> relat = relaMap.entrySet();
				Iterator<Map.Entry<String, String>> itr = relat.iterator();

				while (itr.hasNext()) {
					Map.Entry<String, String> entry = itr.next();
					bos.write(entry.getValue().getBytes("UTF-8"));
					bos.write('\n');
				}
				bos.write("}".getBytes("UTF-8"));
				byte[] buffi = bos.toByteArray();
				FileOutputStream fos = new FileOutputStream(
						Suku.kontroller.getFilePath());
				fos.write(buffi);
				fos.close();
				String infile = Suku.kontroller.getFilePath();
				String jpgfile = null;
				if (infile.toLowerCase().endsWith(".gv")) {
					jpgfile = infile.substring(0, infile.length() - 2) + "jpg";
				}
				String exeTask = Suku.kontroller.getPref(
						caller.getSukuParent(), "GRAPHVIZ", "");
				if (!exeTask.equals("")) {
					Utils.graphvizDo(exeTask, infile, jpgfile);
				}
			} else {
				JOptionPane.showMessageDialog(caller, "dblista");
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "creating graphviz file", e);

		}
	}

	HashMap<Integer, PersonShortData> ancs = new HashMap<Integer, PersonShortData>();
	Vector<PersonShortData> commons = new Vector<PersonShortData>();

	private void addRelations(GraphData subj) throws SukuException {
		PersonLongData pers = caller.getSukuParent().getSubject();

		addParents(subj.getPid(), 0);
		addParents(pers.getPid(), 0);

		// if (commons.size() > 0) {
		for (int i = 0; i < commons.size(); i++) {
			PersonShortData cp = commons.get(i);
			drawRelation(cp);
		}
	}

	private void drawRelation(PersonShortData cp) throws SukuException {

		identMap.put("I" + cp.getPid(), cp);
		Vector<Integer> relas = cp.getRelapath();
		if (relas != null) {
			for (int i = 0; i < relas.size(); i++) {

				PersonShortData p = ancs.get(relas.get(i));
				if (p != null) {
					// SukuData xdata =
					// Suku.kontroller.getSukuData("cmd=person",
					// "pid=" + relas.get(i), "mode=short");
					// PersonShortData p = xdata.pers[0];
					identMap.put("I" + p.getPid(), p);
					String color = "blue";
					if (!cp.getSex().equals("M")) {
						color = "red";
					}
					StringBuilder sb = new StringBuilder();
					sb.append(" ");
					sb.append("I" + cp.getPid());
					sb.append(" -- ");
					sb.append("I" + p.getPid());

					sb.append("  [style=bold,color=" + color + "]; ");

					relaMap.put("I" + cp.getPid() + "I" + p.getPid(),
							sb.toString());
					drawRelation(p);
				}
			}
		}
	}

	private void addParents(int pid, int fromPid) throws SukuException {

		SukuData xdata = Suku.kontroller.getSukuData("cmd=person",
				"pid=" + pid, "mode=short");
		PersonShortData p = xdata.pers[0];

		caller.setRunnerValue(p.getName(true, false));
		p.addToRelapath(fromPid);
		PersonShortData me = ancs.get(p.getPid());
		if (me != null) {
			me.addToRelapath(fromPid);
			commons.add(me);
			return;
		}

		if (p.getPid() > 0) {

			ancs.put(pid, p);
			if (p.getFatherPid() > 0) {
				addParents(p.getFatherPid(), pid);
			}
			if (p.getMotherPid() > 0) {
				addParents(p.getMotherPid(), pid);
			}
		}

	}

	// private void addParentsForX(int pid) throws SukuException {
	// SukuData xdata = Suku.kontroller.getSukuData("cmd=person",
	// "pid=" + pid, "mode=short");
	// PersonShortData p = xdata.pers[0];
	// if (p.getPid() > 0) {
	// ancs.put(pid, p);
	// if (p.getFatherPid()>0) {
	// addParentsForX(p.getFatherPid());
	// }
	// if (p.getMotherPid()>0) {
	// addParentsForX(p.getMotherPid());
	// }
	// }
	// }

	HashMap<Integer, Integer> remover = new HashMap<Integer, Integer>();

	private void addAncestorRelatives(PersonShortData pers, int generation,
			boolean includeFamily) throws SukuException {
		SukuData pdata = caller.getKontroller().getSukuData("cmd=person",
				"pid=" + pers.getPid(), "lang=" + Resurses.getLanguage());
		GraphData gdata = new GraphData(pdata);

		caller.setRunnerValue(gdata.getName(true, false));

		int fatherPid = 0;
		int motherPid = 0;
		for (int i = 0; i < gdata.relations.length; i++) {
			Relation spo = gdata.relations[i];

			if (spo.getTag().equals("FATH")) {
				fatherPid = spo.getRelative();
				remover.put(spo.getRid(), 1);
			} else if (spo.getTag().equals("MOTH")) {
				motherPid = spo.getRelative();
				remover.put(spo.getRid(), 1);
			}

		}
		if (includeFamily) {
			String spouseTag = "HUSB";
			if (pers.getSex().equals("M")) {
				spouseTag = "WIFE";
			}
			for (int i = 0; i < gdata.relations.length; i++) {
				Relation rela = gdata.relations[i];
				Integer wasAlready = remover.put(rela.getRid(), 1);
				if (wasAlready == null && rela.getTag().equals(spouseTag)) {
					PersonShortData spou = gdata.rels.get(rela.getRelative());
					identMap.put("I" + spou.getPid(), spou);

					StringBuilder sb = new StringBuilder();
					sb.append(" ");
					sb.append("I" + pers.getPid());
					sb.append(" -- ");
					sb.append("I" + spou.getPid());

					sb.append("  [style=bold,color=violet]; ");

					relaMap.put("I" + pers.getPid() + "I" + spou.getPid(),
							sb.toString());

				}
				if (wasAlready == null && rela.getTag().equals("CHIL")) {

					PersonShortData chil = gdata.rels.get(rela.getRelative());
					identMap.put("I" + chil.getPid(), chil);

					StringBuilder sb = new StringBuilder();
					sb.append(" ");
					sb.append("I" + pers.getPid());
					sb.append(" -- ");
					sb.append("I" + chil.getPid());

					sb.append("  [style=bold,color=orange]; ");

					relaMap.put("I" + pers.getPid() + "I" + chil.getPid(),
							sb.toString());

					int otherPid = 0;
					if (spouseTag.equals("HUSB")) {
						otherPid = chil.getFatherPid();
					} else {
						otherPid = chil.getMotherPid();
					}

					for (int j = 0; j < gdata.relations.length; j++) {
						Relation othe = gdata.relations[j];
						if (othe.getRelative() == otherPid) {
							StringBuilder ssb = new StringBuilder();
							ssb.append(" ");
							ssb.append("I" + otherPid);
							ssb.append(" -- ");
							ssb.append("I" + chil.getPid());

							ssb.append("  [style=bold,color=orange]; ");

							relaMap.put("I" + otherPid + "I" + chil.getPid(),
									ssb.toString());
						}
					}

				}

			}
		}
		if (fatherPid > 0) {
			addParentData(pers, gdata, fatherPid, generation, includeFamily);
		}
		if (motherPid > 0) {
			addParentData(pers, gdata, motherPid, generation, includeFamily);
		}

	}

	/**
	 * @param pers
	 *            of original person
	 * @param gdata
	 * @param fatherPid
	 * @throws SukuException
	 */
	public void addParentData(PersonShortData pers, GraphData gdata,
			int parePid, int generation, boolean includeFamily)
			throws SukuException {

		if (generation > 0) {
			PersonShortData pare = gdata.rels.get(parePid);

			identMap.put("I" + pare.getPid(), pare);
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			sb.append("I" + pare.getPid());
			sb.append(" -- ");
			sb.append("I" + pers.getPid());
			if (pare.getSex().equals("M")) {
				sb.append("  [style=bold,color=blue]; ");
			} else {
				sb.append("  [style=bold,color=red]; ");
			}
			relaMap.put("I" + pers.getPid() + "I" + pare.getPid(),
					sb.toString());

			addAncestorRelatives(pare, generation - 1, includeFamily);
		}
	}

	private void addDescendantRelatives(PersonShortData pers, int generation,
			boolean includeAdopted) throws SukuException {
		SukuData pdata = caller.getKontroller().getSukuData("cmd=person",
				"pid=" + pers.getPid(), "lang=" + Resurses.getLanguage());
		GraphData gdata = new GraphData(pdata);

		caller.setRunnerValue(gdata.getName(true, false));

		for (int i = 0; i < gdata.relations.length; i++) {
			Relation spo = gdata.relations[i];
			if (spo.getTag().equals("WIFE") || spo.getTag().equals("HUSB")) {
				PersonShortData spouse = gdata.rels.get(spo.getRelative());

				SukuData sdata = caller.getKontroller().getSukuData(
						"cmd=person", "pid=" + spouse.getPid(),
						"lang=" + Resurses.getLanguage());
				GraphData xdata = new GraphData(sdata); // full version of
														// spouse

				identMap.put("I" + spouse.getPid(), spouse);
				StringBuilder sb = new StringBuilder();
				sb.append(" ");
				sb.append("I" + pers.getPid());
				sb.append(" -- ");
				sb.append("I" + spouse.getPid());
				sb.append("  [style=bold,color=green]; ");
				relaMap.put("I" + pers.getPid() + "I" + spouse.getPid(),
						sb.toString());
				int spogen = caller.getDescendantPane().getSpouseAncestors();
				if (spogen > 0) {

					int spoFatherPid = 0;
					int spoMotherPid = 0;
					for (int ii = 0; ii < xdata.relations.length; ii++) {
						Relation spox = xdata.relations[ii];
						if (spox.getTag().equals("FATH")) {
							spoFatherPid = spox.getRelative();
						} else if (spox.getTag().equals("MOTH")) {
							spoMotherPid = spox.getRelative();
						}
					}
					if (spoFatherPid > 0) {
						PersonShortData fat = xdata.rels.get(spoFatherPid);
						identMap.put("I" + spoFatherPid, fat);

						StringBuilder sbb = new StringBuilder();
						sbb.append(" ");
						sbb.append("I" + fat.getPid());
						sbb.append(" -- ");
						sbb.append("I" + spouse.getPid());
						sbb.append("  [style=bold,color=blue]; ");
						relaMap.put("I" + fat.getPid() + "I" + spouse.getPid(),
								sbb.toString());

					}
					if (spoMotherPid > 0) {
						PersonShortData fat = xdata.rels.get(spoMotherPid);
						identMap.put("I" + spoMotherPid, fat);
						StringBuilder sbb = new StringBuilder();
						sbb.append(" ");
						sbb.append("I" + fat.getPid());
						sbb.append(" -- ");
						sbb.append("I" + spouse.getPid());
						sbb.append("  [style=bold,color=red]; ");

						relaMap.put("I" + fat.getPid() + "I" + spouse.getPid(),
								sbb.toString());
					}

				}

			}
		}

		if (generation > 0) {
			for (int i = 0; i < gdata.relations.length; i++) {
				Relation chi = gdata.relations[i];
				if (chi.getTag().equals("CHIL")) {
					boolean notAdopted = true;
					if (chi.getNotices() != null) {
						for (int adop = 0; adop < chi.getNotices().length; adop++) {
							if (chi.getNotices()[adop].getTag().equals("ADOP")) {
								notAdopted = false;
								break;
							}
						}
					}
					if (notAdopted || includeAdopted) {

						PersonShortData chil = gdata.rels
								.get(chi.getRelative());
						PersonShortData prev = identMap.put(
								"I" + chil.getPid(), chil);

						StringBuilder sb = new StringBuilder();
						sb.append(" ");
						sb.append("I" + pers.getPid());
						sb.append(" -- ");
						sb.append("I" + chil.getPid());
						if (pers.getSex().equals("M")) {
							sb.append("  [style=bold,color=blue]; ");
						} else {
							sb.append("  [style=bold,color=red]; ");
						}
						relaMap.put("I" + pers.getPid() + "I" + chil.getPid(),
								sb.toString());

						for (int moth = 1; moth < gdata.relations.length; moth++) {
							if (gdata.relations[moth].getRelative() == chil
									.getMotherPid()
									|| gdata.relations[moth].getRelative() == chil
											.getFatherPid()) {
								PersonShortData mother = gdata.rels
										.get(gdata.relations[moth]
												.getRelative());

								if (mother != null) {
									StringBuilder sbb = new StringBuilder();
									sbb.append(" ");
									sbb.append("I" + mother.getPid());
									sbb.append(" -- ");
									sbb.append("I" + chil.getPid());
									if (mother.getSex().equals("M")) {
										sbb.append("  [style=bold,color=blue]; ");
									} else {
										sbb.append("  [style=bold,color=red]; ");
									}
									relaMap.put("I" + mother.getPid() + "I"
											+ chil.getPid(), sbb.toString());
								}
							}
						}

						if (prev == null) {
							addDescendantRelatives(chil, generation - 1,
									includeAdopted);
						}
					}
				}
			}
		}
	}

	@Override
	public void setVisible(boolean b) {

	}

	class GraphData extends PersonShortData {
		/**  */
		private static final long serialVersionUID = 1L;
		Relation[] relations;
		HashMap<Integer, PersonShortData> rels = new HashMap<Integer, PersonShortData>();

		@Override
		public int getFatherPid() {
			String tag = "FATH";
			return getParent(tag);
		}

		/**
		 * Here get the most probable parent not adopted rather than adopted
		 * First relation of greatest surety is selected
		 * 
		 * @param tag
		 * @return
		 */
		private int getParent(String tag) {

			boolean isAdopted = true;
			Relation rr = null;

			if (relations != null) {
				for (int i = 0; i < relations.length; i++) {
					Relation r = relations[i];
					if (r.getTag().equals(tag)) {
						if (rr == null) {
							rr = r;
						}
						if (r.getNotice("ADOP") == null) {
							// not adopted
							if (isAdopted) {
								rr = r;
								isAdopted = false;
							}
							if (r.getSurety() > rr.getSurety()) {
								rr = r;
							}
						} else {
							// is adopted
							if (isAdopted) {
								// continue only if adopted
								if (r.getSurety() > rr.getSurety()) {
									rr = r;
								}
							}
						}

					}
				}
				return rr.getRelative();
			}
			return 0;
		}

		@Override
		public int getMotherPid() {
			String tag = "MOTH";

			return getParent(tag);
		}

		public GraphData(SukuData data) {
			super(data.persLong);
			relations = data.relations;
			for (int i = 0; i < data.pers.length; i++) {
				rels.put(data.pers[i].getPid(), data.pers[i]);
			}
		}
	}

	class RelationData extends PersonShortData {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Vector<Integer> relPath = new Vector<Integer>();

	}

}
