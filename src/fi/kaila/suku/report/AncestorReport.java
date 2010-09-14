package fi.kaila.suku.report;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;

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
					"family=true", "pid=" + caller.getPid());
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
			repoWriter.closeReport();

		}

	}

	private void createFullStradoReport(HashMap<Integer, ReportUnit> reportUnits) {
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

	private void createStradoReport() {
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

	private void createEspolinReport() {
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
