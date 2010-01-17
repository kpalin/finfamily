package fi.kaila.suku.report;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fi.kaila.suku.swing.worker.ReportWorkerDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * <h1>Ancestor report creator</h1>
 * 
 * The descendant report structure is creted here
 * 
 * @author Kalle
 * 
 */
public class AncestorReport extends CommonReport {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor for Ancestor report
	 * 
	 * @param caller
	 * @param repoWriter
	 */
	public AncestorReport(ReportWorkerDialog caller, ReportInterface repoWriter) {
		super(caller, repoWriter);

	}

	/**
	 * execute the ancestor report
	 * 
	 * @throws SukuException
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
			JOptionPane.showMessageDialog(caller, Resurses
					.getString(Resurses.CREATE_REPORT)
					+ ":" + e.getMessage());
		}
		if (vlist.resu != null) {
			JOptionPane.showMessageDialog(caller, Resurses
					.getString(Resurses.CREATE_REPORT)
					+ " [" + vlist.resu + "]");
			return;
		}
		tables = vlist.tables;

		for (int i = 0; i < tables.size(); i++) {
			ReportUnit cu = tables.get(i);
			logger.info("anc:" + cu);
		}

		if (tables.size() > 0) {
			personReferences = Utils.getDescendantToistot(tables);

			repoWriter.createReport();
			if (order.equals("ESPOLIN")) {
				createEspolinReport();
			} else {
				createStradoReport();
			}
			repoWriter.closeReport();

		}

	}

	private void createStradoReport() {
		textReferences = new HashMap<String, PersonInTables>();
		long tabno = 0;
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

			createAncestorTable(i, ftab, mtab);
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
		for (int i = 0; i < tables.size(); i++) {

			ReportUnit tab = tables.get(i);
			createAncestorTable(i, tab, null);
			// createDescendantTable(i, tab);
		}

		caller.setRunnerValue("100;OK");

	}

	/**
	 * Used to close / hide the report writer
	 * 
	 * @param b
	 */
	@Override
	public void setVisible(boolean b) {
		if (repoWriter instanceof JFrame) {
			JFrame ff = (JFrame) repoWriter;
			ff.setVisible(b);
		}

	}

}
