package fi.kaila.suku.report;

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
 * <h1>Descendant report creator</h1>
 * 
 * The descendant report structure is creted here
 * 
 * @author Kalle
 * 
 */
public class DescendantReport extends CommonReport {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Construcor for Descendant report
	 * 
	 * @param caller
	 * @param repoWriter
	 */
	public DescendantReport(ReportWorkerDialog caller,
			ReportInterface repoWriter) {
		super(caller, repoWriter);

	}

	/**
	 * execute the report.
	 * 
	 * @throws SukuException
	 */
	public void executeReport() throws SukuException {
		SukuData vlist = null;

		if (caller.getDescendantPanel().getTableOrder().getSelection() == null) {
			logger.log(Level.INFO, Resurses.getString(Resurses.CREATE_REPORT)
					+ Resurses.getString("REPORT_ERROR_ORDERMISSING"));
			JOptionPane.showMessageDialog(caller, Resurses
					.getString(Resurses.CREATE_REPORT)
					+ ": " + Resurses.getString("REPORT_ERROR_ORDERMISSING"));
			return;
		}
		String order = caller.getDescendantPanel().getTableOrder()
				.getSelection().getActionCommand();

		try {
			vlist = caller.getKontroller().getSukuData(
					"cmd=" + Resurses.CMD_CREATE_TABLES,
					"type=" + Resurses.CMD_DESC_TYPE,
					"order=" + order,
					"adopted=" + caller.getDescendantPanel().getAdopted(),
					"generations="
							+ caller.getDescendantPanel().getGenerations(),
					"spougen="
							+ caller.getDescendantPanel().getSpouseAncestors(),
					"chilgen="
							+ caller.getDescendantPanel().getChildAncestors(),
					"pid=" + caller.getPid());
		} catch (SukuException e) {
			logger.log(Level.INFO, Resurses.getString(Resurses.CREATE_REPORT),
					e);
			JOptionPane.showMessageDialog(caller, Resurses
					.getString(Resurses.CREATE_REPORT)
					+ ":" + e.getMessage());
		}

		if (vlist != null) {
			logger.info("Descendant repo");
			tables = vlist.tables;

			personReferences = Utils.getDescendantToistot(tables);

			if (tables.size() > 0) {

				repoWriter.createReport();
				createReport();
				repoWriter.closeReport();

			}

		}
	}

	private void createReport() {
		for (int i = 0; i < tables.size(); i++) {

			ReportUnit tab = tables.get(i);
			createTable(i, tab);
		}

		caller.setRunnerValue("100;OK");

	}

	/**
	 * Used to close / hide the report writer
	 * 
	 * @param b
	 */
	public void setVisible(boolean b) {
		if (repoWriter instanceof JFrame) {
			JFrame ff = (JFrame) repoWriter;
			ff.setVisible(b);
		}

	}

}
