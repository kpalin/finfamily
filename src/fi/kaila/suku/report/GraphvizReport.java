package fi.kaila.suku.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.SukuData;

public class GraphvizReport extends CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public GraphvizReport(ReportWorkerDialog caller, SukuTypesTable typesTable) {
		super(caller, typesTable, null);

	}

	LinkedHashMap<String, PersonShortData> identMap = null;
	LinkedHashMap<String, String> relaMap = null;

	@Override
	public void executeReport() throws SukuException {
		int descgen = caller.getDescendantPane().getGenerations();
		boolean includeAdopted = caller.getDescendantPane().getAdopted();
		identMap = new LinkedHashMap<String, PersonShortData>();
		relaMap = new LinkedHashMap<String, String>();
		try {
			if (caller.getPid() > 0) {
				caller.getDescendantPane().getGenerations();
				SukuData pdata = caller.getKontroller().getSukuData(
						"cmd=person", "pid=" + caller.getPid(),
						"lang=" + Resurses.getLanguage());
				GraphData subj = new GraphData(pdata);
				// PersonShortData subj = new PersonShortData(pdata.persLong);
				identMap.put("I" + subj.getPid(), subj);

				addRelatives(subj, descgen - 1, includeAdopted);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();

				// System.out.println("graph G {");
				bos.write("graph G {".getBytes("UTF-8"));
				Set<Map.Entry<String, PersonShortData>> setti = identMap
						.entrySet();

				Iterator<Map.Entry<String, PersonShortData>> it = setti
						.iterator();

				while (it.hasNext()) {
					Map.Entry<String, PersonShortData> entry = it.next();
					StringBuilder sb = new StringBuilder();
					PersonShortData pp = entry.getValue();
					sb.append("I" + pp.getPid());
					sb.append(" [shape=");
					if (pp.getSex().equals("M")) {
						sb.append("box,color=red");
					} else {
						sb.append("ellipse,color=blue");
					}
					sb.append(",style=bold,label=\"");
					if (pp.getGivenname() != null) {
						String parts[] = pp.getGivenname().split(" ");
						String etunimi = parts[0];
						for (int n = 0; n < parts.length; n++) {
							if (parts[n].endsWith("*")) {
								etunimi = parts[n].substring(0,
										parts[n].length() - 1);
								break;
							}
						}
						sb.append(etunimi);
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
					if (pp.getBirtDate() != null || pp.getBirtPlace() != null) {
						sb.append("\\n");

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
					if (pp.getDeatDate() != null || pp.getDeatPlace() != null) {
						sb.append("\\n");

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
					if (pp.getOccupation() != null) {
						sb.append("\\n");
						sb.append(pp.getOccupation());
					}

					sb.append("\"];");

					bos.write(sb.toString().getBytes("UTF-8"));

					// System.out.println(sb.toString());

				}
				Set<Map.Entry<String, String>> relat = relaMap.entrySet();
				Iterator<Map.Entry<String, String>> itr = relat.iterator();

				while (itr.hasNext()) {
					Map.Entry<String, String> entry = itr.next();
					bos.write(entry.getValue().getBytes("UTF-8"));

					// System.out.println(entry.getValue());

				}
				bos.write("}".getBytes("UTF-8"));
				byte[] buffi = bos.toByteArray();

				ByteArrayInputStream bin = new ByteArrayInputStream(buffi);
				Suku.kontroller.saveFile("txt", bin);

				// System.out.println("}");

			} else {
				JOptionPane.showMessageDialog(caller, "dblista");
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "creating graphviz file", e);

		}
	}

	private void addRelatives(PersonShortData pers, int generation,
			boolean includeAdopted) throws SukuException {
		SukuData pdata = caller.getKontroller().getSukuData("cmd=person",
				"pid=" + pers.getPid(), "lang=" + Resurses.getLanguage());
		GraphData gdata = new GraphData(pdata);
		for (int i = 0; i < gdata.relations.length; i++) {
			Relation spo = gdata.relations[i];
			if (spo.getTag().equals("WIFE") || spo.getTag().equals("HUSB")) {
				PersonShortData spouse = gdata.rels.get(spo.getRelative());
				identMap.put("I" + spouse.getPid(), spouse);
				StringBuilder sb = new StringBuilder();
				sb.append(" ");
				sb.append("I" + pers.getPid());
				sb.append(" -- ");
				sb.append("I" + spouse.getPid());
				sb.append("  [style=bold,color=green]; ");
				relaMap.put("I" + pers.getPid() + "I" + spouse.getPid(),
						sb.toString());

			}
		}

		if (generation > 0) {
			for (int i = 0; i < gdata.relations.length; i++) {
				Relation chi = gdata.relations[i];
				if (chi.getTag().equals("CHIL")) {
					PersonShortData chil = gdata.rels.get(chi.getRelative());
					PersonShortData prev = identMap.put("I" + chil.getPid(),
							chil);

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
									.get(gdata.relations[moth].getRelative());

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
								relaMap.put(
										"I" + mother.getPid() + "I"
												+ chil.getPid(), sbb.toString());
							}

						}
					}

					if (prev == null) {
						addRelatives(chil, generation - 1, includeAdopted);
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

		public GraphData(SukuData data) {
			super(data.persLong);
			relations = data.relations;
			for (int i = 0; i < data.pers.length; i++) {
				rels.put(data.pers[i].getPid(), data.pers[i]);
			}
		}
	}

}
