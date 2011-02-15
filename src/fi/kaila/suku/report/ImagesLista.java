package fi.kaila.suku.report;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.report.style.MainPersonText;
import fi.kaila.suku.report.style.TableHeaderText;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * <h1>Descendant report creator</h1>
 * 
 * The descendant report structure is creted here.
 * 
 * @author Kalle
 */
public class ImagesLista extends CommonReport {

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
	public ImagesLista(ReportWorkerDialog caller, SukuTypesTable typesTable,
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

		int pidArray[] = new int[caller.getSukuParent().getDatabaseRowCount()];

		for (int idx = 0; idx < pidArray.length; idx++) {
			pidArray[idx] = caller.getSukuParent().getDatbasePerson(idx)
					.getPid();
		}

		logger.info("Lista repo");

		textReferences = new HashMap<String, PersonInTables>();
		repoWriter.createReport();
		for (int idx = 0; idx < pidArray.length; idx++) {
			// repoWriter.createReport();
			createPidTable(idx, pidArray);
			// repoWriter.closeReport(pidArray[idx]);
		}
		caller.setRunnerValue("100;OK");
		repoWriter.closeReport();

	}

	protected void createPidTable(int idx, int[] pidCount) throws SukuException {
		BodyText bt = new TableHeaderText();
		bt.addText(Resurses.getReportString("REPORT.LISTA.TABLE") + " Pid = "
				+ pidCount[idx]);
		repoWriter.addText(bt);

		SukuData pdata = null;
		StringBuilder tabOwner = new StringBuilder();

		try {
			pdata = caller.getKontroller().getSukuData("cmd=person",
					"pid=" + pidCount[idx], "lang=" + Resurses.getLanguage());
		} catch (SukuException e1) {
			logger.log(Level.WARNING, "background reporting", e1);
			JOptionPane.showMessageDialog(caller, e1.getMessage());
			return;
		}

		UnitNotice[] notices = pdata.persLong.getNotices();

		for (int j = 0; j < notices.length; j++) {
			UnitNotice nn = notices[j];
			if (nn.getTag().equals("NAME")) {
				tabOwner.append(nn.getSurname());
				if (tabOwner.length() > 0)
					tabOwner.append(" ");
				tabOwner.append(nn.getGivenname());
				// break;
			}

			String xxx = nn.getMediaTitle();
			if (xxx == null) {
				xxx = "";
			}

		}
		float prose = (idx * 100f) / pidCount.length;
		caller.setRunnerValue("" + (int) prose + ";" + tabOwner);

		bt = new MainPersonText();
		printName(bt, pdata.persLong, 2);
		repoWriter.addText(bt);

		printNotices(bt, pdata.persLong, 2, 0);
		// bt = new BodyText();
		// bt.addText("");
		repoWriter.addText(bt);
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
