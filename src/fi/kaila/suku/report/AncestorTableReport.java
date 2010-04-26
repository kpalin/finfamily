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
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * <h1>GenGraph Report</h1>
 * 
 */
public class AncestorTableReport extends CommonReport {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor for GenGraphReport
	 * 
	 * @param caller
	 * @param typesTable
	 * @param repoWriter
	 */
	public AncestorTableReport(ReportWorkerDialog caller,
			SukuTypesTable typesTable, ReportInterface repoWriter) {
		super(caller, typesTable, repoWriter);
	}

	/**
	 * execute the report.
	 */
	public void executeReport() {
		SukuData vlist = null;

		int generations = caller.getAncestorPane().getGenerations();
		logger.info("Ancestor tables for " + caller.getPid() + ",generations="
				+ generations);

		if (!Suku.kontroller.createLocalFile("xls")) {
			return;
		}
		try {
			vlist = caller.getKontroller().getSukuData(
					"cmd=" + Resurses.CMD_CREATE_TABLES,
					"type=" + Resurses.CMD_ANC_TYPE,
					"generations=" + generations, "order=STRADONIZ",
					"family=false", "pid=" + caller.getPid());
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

		logger.info("AncestorTableReport");

		try {

			BufferedOutputStream bstr = new BufferedOutputStream(
					Suku.kontroller.getOutputStream());
			WritableWorkbook workbook = Workbook.createWorkbook(bstr);
			// WritableWorkbook workbook = Workbook.createWorkbook(new
			// File("output.xls"));

			WritableSheet sheet = workbook.createSheet("Table 1", 0);

			// Create a cell format for Times 16, bold and italic
			WritableFont arial10italic = new WritableFont(WritableFont.ARIAL,
					10, WritableFont.NO_BOLD, true);
			WritableCellFormat italic10format = new WritableCellFormat(
					arial10italic);

			WritableFont arial10bold = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.BOLD, false);
			WritableCellFormat italic10bold = new WritableCellFormat(
					arial10bold);

			Label label = new Label(0, 0, "Table 1");
			sheet.addCell(label);

			Number number;
			int col = 0;
			int row = 0;
			for (int i = 0; i < tables.size(); i++) {

				ReportUnit uu = tables.get(i);
				long tabno = uu.getTableNo();

				ReportTableMember rtu = uu.getMember(0);
				int pid = rtu.getPid();
				SukuData pappadata = null;

				try {
					pappadata = caller.getKontroller().getSukuData(
							"cmd=person", "pid=" + pid,
							"lang=" + Resurses.getLanguage());
				} catch (SukuException e1) {
					logger.log(Level.WARNING, "background reporting", e1);
					JOptionPane.showMessageDialog(caller, e1.getMessage());
					return;
				}

				PersonShortData pp = new PersonShortData(pappadata.persLong);

				String bdate = pp.getBirtDate() == null ? null : pp
						.getBirtDate();
				String ddate = pp.getDeatDate() == null ? null : pp
						.getDeatDate();

				String pname = pp.getAlfaName();
				String occu = pp.getOccupation();

				String who = null;
				int tabcol = 1;
				int ax;
				switch ((int) tabno) {
				case 1:
					row = 22;
					col = 0;
					tabcol = 2;
					who = typesTable.getTextValue("ANC_SUBJECT");
					break;
				case 2:
					row = 10;
					col = 1;
					tabcol = 2;
					who = typesTable.getTextValue("ANC_FATHER");
					break;
				case 3:
					row = 33;
					col = 1;
					tabcol = 2;
					who = typesTable.getTextValue("ANC_MOTHER");
					break;
				case 4:
					row = 4;
					col = 2;
					tabcol = 2;
					who = typesTable.getTextValue("ANC_FF");
					break;
				case 5:
					row = 16;
					col = 2;
					tabcol = 2;
					who = typesTable.getTextValue("ANC_FM");
					break;
				case 6:
					row = 28;
					col = 2;
					tabcol = 2;
					who = typesTable.getTextValue("ANC_MF");
					break;
				case 7:
					row = 39;
					col = 2;
					tabcol = 2;
					who = typesTable.getTextValue("ANC_MM");
					break;
				default:
					if (tabno < 16) {
						ax = (int) (tabno - 8);
						col = 5;
						row = 1 + (ax * 7);
					} else {
						col = 7;
						ax = (int) (tabno - 16);
						if ((ax & 1) == 0) {
							row = 1 + ((ax / 2) * 7);
						} else {
							row = 1 + ((ax / 2) * 7) + 4;
						}
					}

				}
				int lrow = row;
				if (who != null) {

					label = new Label(col, lrow++, who);
					sheet.addCell(label);
				}
				label = new Label(col, lrow++, pname);
				sheet.addCell(label);
				if (bdate != null) {
					label = new Label(col, lrow, bdate);
					sheet.addCell(label);
				}
				lrow++;
				if (ddate != null) {
					label = new Label(col, lrow, ddate);
					sheet.addCell(label);
				}
				lrow++;
				if (occu != null) {
					label = new Label(col, lrow, occu);
					sheet.addCell(label);
				}
				number = new Number(col + tabcol, row, tabno);
				sheet.addCell(number);

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
