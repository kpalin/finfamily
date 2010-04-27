package fi.kaila.suku.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
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
	 * Constructor for AncestorTableReport
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

			ReportUnit[] page = new ReportUnit[31];

			WritableCellFormat wrall = new WritableCellFormat();
			wrall.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
			wrall.setVerticalAlignment(VerticalAlignment.TOP);
			wrall.setWrap(true);

			int col = 0;
			int row = 0;
			int lrow = 0;
			int lcol = 0;
			int idx = 0;
			int xpage = 0;
			while (idx < tables.size()) {
				long tabIdx = 0;

				ReportUnit uu = tables.get(idx);
				long tabno = uu.getTableNo();

				tabIdx = ((tabno - 1) & 0xffffffffffffffc0L);
				for (int j = 0; j < 31; j++) {
					page[j] = null;
				}
				page[(int) (tabno - tabIdx - 1)] = uu;
				idx++;
				uu = tables.get(idx);
				long curtab = uu.getTableNo();
				while (curtab <= tabIdx + 31) {
					page[(int) (curtab - tabIdx - 1)] = uu;
					idx++;
					if (idx < tables.size()) {
						uu = tables.get(idx);
						curtab = uu.getTableNo();
					} else {
						break;
					}
				}
				xpage++;

				WritableSheet sheet = workbook.createSheet("P " + xpage, 0);

				WritableSheet wsh = (WritableSheet) sheet;

				wsh.setColumnView(0, 2);
				wsh.setColumnView(1, 4);
				wsh.setColumnView(2, 25);
				wsh.setColumnView(3, 2);
				wsh.setColumnView(4, 2);
				wsh.setColumnView(5, 25);
				wsh.setColumnView(6, 2);
				wsh.setColumnView(7, 25);
				wsh.setColumnView(8, 4);
				wsh.setColumnView(9, 4);

				wsh.setColumnView(10, 1);
				Label label = new Label(0, 0, "P " + xpage);
				sheet.addCell(label);
				for (int i = 0; i < 31; i++) {

					StringBuilder sb = new StringBuilder();
					uu = page[i];

					tabno = tabIdx + i + 1;

					SukuData pappadata = null;
					String bdate = null;
					String ddate = null;

					String pname = null;
					String occu = null;
					if (uu != null) {
						ReportTableMember rtu = uu.getMember(0);
						int pid = rtu.getPid();

						try {
							pappadata = caller.getKontroller().getSukuData(
									"cmd=person", "pid=" + pid,
									"lang=" + Resurses.getLanguage());
						} catch (SukuException e1) {
							logger.log(Level.WARNING, "background reporting",
									e1);
							JOptionPane.showMessageDialog(caller, e1
									.getMessage());
							return;
						}

						PersonShortData pp = new PersonShortData(
								pappadata.persLong);

						bdate = pp.getBirtDate() == null ? null : pp
								.getBirtDate();
						ddate = pp.getDeatDate() == null ? null : pp
								.getDeatDate();

						pname = pp.getAlfaName();
						occu = pp.getOccupation();
					}
					String who = null;

					int ax;
					int bh = 5;
					switch ((int) tabno) {
					case 1:
						row = 22;
						lrow = row + bh;
						col = 0;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_SUBJECT");
						break;
					case 2:
						row = 10;
						lrow = row + bh;
						col = 1;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_FATHER");
						break;
					case 3:
						row = 34;
						lrow = row + bh;
						col = 1;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_MOTHER");
						break;
					case 4:
						row = 4;
						lrow = row + bh;
						col = 2;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_FF");
						break;
					case 5:
						row = 16;
						lrow = row + bh;
						col = 2;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_FM");
						break;
					case 6:
						row = 28;
						lrow = row + bh;
						col = 2;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_MF");
						break;
					case 7:
						row = 40;
						lrow = row + bh;
						col = 2;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_MM");
						break;
					default:
						if (tabno < 16) {
							ax = (int) (tabno - 8);
							col = 5;
							lcol = col + 1;
							row = 1 + (ax * 7);
							lrow = row + bh;
						} else {
							col = 7;
							lcol = col + 1;
							ax = (int) (tabno - 16);
							if ((ax & 1) == 0) {
								row = 1 + ((ax / 2) * 7);
								lrow = row + 3;
							} else {
								row = 1 + ((ax / 2) * 7) + 4;
								lrow = row + 2;
							}
						}

					}

					if (who != null) {
						sb.append(who + "\n");

					}
					if (pname != null) {
						sb.append(pname);
					}
					sb.append(" (" + tabno + ")");
					sb.append("\n");

					if (bdate != null) {
						sb.append(bdate);

					}

					sb.append("\n");
					if (ddate != null) {
						sb.append(bdate);

					}

					sb.append("\n");
					if (occu != null) {
						sb.append(occu);

					}

					label = new Label(col, row, sb.toString());
					sheet.addCell(label);

					// Range rng =
					wsh.mergeCells(col, row, lcol, lrow);
					wsh.getWritableCell(col, row).setCellFormat(wrall);

				}
				break;
			}

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
