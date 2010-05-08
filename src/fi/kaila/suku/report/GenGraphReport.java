package fi.kaila.suku.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * <h1>GenGraph Report</h1>
 * 
 */
public class GenGraphReport extends CommonReport {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor for GenGraphReport
	 * 
	 * @param caller
	 * @param typesTable
	 * @param repoWriter
	 */
	public GenGraphReport(ReportWorkerDialog caller, SukuTypesTable typesTable,
			ReportInterface repoWriter) {
		super(caller, typesTable, repoWriter);
	}

	/**
	 * execute the report.
	 */
	public void executeReport() {
		SukuData vlist = null;
		// this will create the output file stream
		if (!Suku.kontroller.createLocalFile("xls")) {
			return;
		}

		int genAnc = caller.getOtherPane().getAncestors();
		int genDesc = caller.getOtherPane().getDescendants();
		int youngFrom = caller.getOtherPane().getYoungFrom();

		boolean adopted = caller.getOtherPane().isAdopted();
		boolean parents = caller.getOtherPane().isParents();
		boolean spouses = caller.getOtherPane().isSpouses();
		boolean givenName = caller.getOtherPane().isGivenname();
		boolean surname = caller.getOtherPane().isSurname();
		boolean occu = caller.getOtherPane().isOccupation();
		boolean lived = caller.getOtherPane().isLived();
		boolean place = caller.getOtherPane().isPlace();
		boolean married = caller.getOtherPane().isMarried();

		logger.info("asked for " + genAnc + " ancestors, " + genDesc
				+ " descendants and " + youngFrom + " backwards generations");

		try {
			vlist = caller.getKontroller().getSukuData("cmd=gengraph",
					"anc=" + genAnc, "desc=" + genDesc, "back=" + youngFrom,
					"pid=" + caller.getPid());
		} catch (SukuException e) {
			logger.log(Level.INFO, Resurses.getString(Resurses.CREATE_REPORT),
					e);
			JOptionPane.showMessageDialog(caller, Resurses
					.getString(Resurses.CREATE_REPORT)
					+ ":" + e.getMessage());
			return;
		}

		logger.info("GenGraphReport");

		try {

			BufferedOutputStream bstr = new BufferedOutputStream(
					Suku.kontroller.getOutputStream());
			WritableWorkbook workbook = Workbook.createWorkbook(bstr);
			// WritableWorkbook workbook = Workbook.createWorkbook(new
			// File("output.xls"));

			WritableSheet sheet = workbook.createSheet("DescLista", 0);

			// Create a cell format for Times 16, bold and italic
			WritableFont arial10italic = new WritableFont(WritableFont.ARIAL,
					10, WritableFont.NO_BOLD, true);
			WritableCellFormat italic10format = new WritableCellFormat(
					arial10italic);

			WritableFont arial10bold = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.BOLD, false);
			WritableCellFormat italic10bold = new WritableCellFormat(
					arial10bold);

			Label label = new Label(0, 0, "Tulos");
			sheet.addCell(label);
			int row = 2;
			Number number;
			for (int i = 0; i < vlist.pers.length; i++) {
				PersonShortData pp = vlist.pers[i];

				String bdate = pp.getBirtDate() == null ? null : pp
						.getBirtDate().substring(0, 4);
				String ddate = pp.getDeatDate() == null ? null : pp
						.getDeatDate().substring(0, 4);

				StringBuilder sb = new StringBuilder();
				sb.append(pp.getAlfaName());
				if (bdate != null) {
					sb.append(" ");
					sb.append(bdate);
				}
				if (ddate != null) {
					sb.append("-");
					sb.append(ddate);
				}
				row++;
				label = new Label(1, row, sb.toString());

				sheet.addCell(label);
			}

			// All sheets and cells added. Now write out the workbook
			workbook.write();
			workbook.close();
			bstr.close();

			String report = Suku.kontroller.getFilePath();
			Utils.openExternalFile(report);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void setVisible(boolean b) {
		// not implemented here

	}
}
