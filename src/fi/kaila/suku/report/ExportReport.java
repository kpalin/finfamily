package fi.kaila.suku.report;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.JFrame;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;

/**
 * <h1>Descendant report creator</h1>
 * 
 * The descendant report structure is creted here.
 * 
 * @author Kalle
 */
public class ExportReport extends CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

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
	public ExportReport(ReportWorkerDialog caller, SukuTypesTable typesTable,
			ReportInterface repoWriter) {
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

		int pidArray[];
		if (caller.getPid() > 0) {
			pidArray = new int[1];
			pidArray[0] = caller.getPid();
		} else {
			pidArray = new int[caller.getSukuParent().getDatabaseRowCount()];

			for (int idx = 0; idx < pidArray.length; idx++) {
				pidArray[idx] = caller.getSukuParent().getDatbasePerson(idx)
						.getPid();
			}
		}
		logger.info("Lista repo");

		textReferences = new HashMap<String, PersonInTables>();

		for (int idx = 0; idx < pidArray.length; idx++) {
			repoWriter.createReport();
			createExportTable(idx, pidArray);
			repoWriter.closeReport(pidArray[idx]);
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

}
