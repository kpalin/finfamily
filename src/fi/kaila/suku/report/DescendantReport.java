package fi.kaila.suku.report;

import java.util.HashMap;
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
 * <h1>Descendant report creator</h1>
 * 
 * The descendant report structure is creted here.
 * 
 * @author Kalle
 */
public class DescendantReport extends CommonReport {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor for Descendant report.
	 * 
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param repoWriter
	 *            the repo writer
	 */
	public DescendantReport(ReportWorkerDialog caller,
			SukuTypesTable typesTable, ReportInterface repoWriter) {
		super(caller, typesTable, repoWriter);

	}

	/**
	 * execute the report.
	 * 
	 * @throws SukuException
	 *             the suku exception
	 */
	@Override
	public void executeReport() throws SukuException {
		SukuData vlist = null;

		if (caller.getDescendantPane().getTableOrder().getSelection() == null) {
			logger.log(Level.INFO, Resurses.getString(Resurses.CREATE_REPORT)
					+ Resurses.getString("REPORT_ERROR_ORDERMISSING"));
			JOptionPane.showMessageDialog(caller,
					Resurses.getString(Resurses.CREATE_REPORT) + ": "
							+ Resurses.getString("REPORT_ERROR_ORDERMISSING"));
			return;
		}
		String order = caller.getDescendantPane().getTableOrder()
				.getSelection().getActionCommand();

		try {
			vlist = caller.getKontroller()
					.getSukuData(
							"cmd=" + Resurses.CMD_CREATE_TABLES,
							"type=" + Resurses.CMD_DESC_TYPE,
							"order=" + order,
							"adopted="
									+ caller.getDescendantPane().getAdopted(),
							"generations="
									+ caller.getDescendantPane()
											.getGenerations(),
							"spougen="
									+ caller.getDescendantPane()
											.getSpouseAncestors(),
							"chilgen="
									+ caller.getDescendantPane()
											.getChildAncestors(),
							"pid=" + caller.getPid());
		} catch (SukuException e) {
			logger.log(Level.INFO, Resurses.getString(Resurses.CREATE_REPORT),
					e);
			JOptionPane.showMessageDialog(
					caller,
					Resurses.getString(Resurses.CREATE_REPORT) + ":"
							+ e.getMessage());
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
		textReferences = new HashMap<String, PersonInTables>();
		for (int i = 0; i < tables.size(); i++) {

			ReportUnit tab = tables.get(i);
			createDescendantTable(i, tab);

		}

		printImages();

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

}
