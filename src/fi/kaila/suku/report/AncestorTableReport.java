package fi.kaila.suku.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
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
		HashMap<Long, ReportUnit> tabMap = new HashMap<Long, ReportUnit>();

		for (int j = 0; j < tables.size(); j++) {
			ReportUnit ru = tables.get(j);
			tabMap.put(ru.getTableNo(), ru);
		}
		Vector<ReportUnit> tabNext = new Vector<ReportUnit>();

		try {

			BufferedOutputStream bstr = new BufferedOutputStream(
					Suku.kontroller.getOutputStream());
			WritableWorkbook workbook = Workbook.createWorkbook(bstr);

			ReportUnit[] page = new ReportUnit[32];

			WritableCellFormat wrall = new WritableCellFormat();
			wrall.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
			wrall.setVerticalAlignment(VerticalAlignment.TOP);
			wrall.setWrap(true);

			int col = 0;
			int row = 0;
			int lrow = 0;
			int lcol = 0;

			int xpage = 0;

			ReportUnit rpux = tabMap.get(1L);
			tabNext.add(rpux);

			while (tabNext.size() > 0) {
				rpux = tabNext.remove(0);
				long currTab = rpux.getTableNo();

				for (int j = 0; j < 32; j++) {
					page[j] = null;
				}

				ReportUnit rpu = tabMap.get(currTab);

				page[1] = rpu;

				for (int i = 1; i <= 15; i++) {
					rpu = page[i];
					if (rpu != null) {
						currTab = rpu.getTableNo();
						if (rpu.getFatherPid() > 0) {
							rpux = tabMap.get(currTab * 2);
							page[i * 2] = rpux;
							if (i > 7
									&& rpux != null
									&& rpux.getFatherPid()
											+ rpux.getMotherPid() > 0) {
								tabNext.add(rpux);
							}
						}

						if (rpu.getMotherPid() > 0) {
							rpux = tabMap.get(currTab * 2 + 1);
							page[i * 2 + 1] = rpux;
							if (i > 7
									&& rpux != null
									&& rpux.getFatherPid()
											+ rpux.getMotherPid() > 0) {
								tabNext.add(rpux);
							}
						}
					}
				}

				ReportUnit uu;
				long tabIdx = 0;
				long tabno;

				xpage++;
				int sheetCount = workbook.getNumberOfSheets();
				WritableSheet sheet = workbook.createSheet("P " + xpage,
						sheetCount + 1);

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
				for (int i = 1; i < 32; i++) {

					StringBuilder sb = new StringBuilder();
					uu = page[i];

					tabno = tabIdx + i;

					SukuData pappadata = null;
					String bdate = null;
					String ddate = null;
					long tableNumber = 0;
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
						tableNumber = uu.getTableNo();
						bdate = pp.getBirtDate() == null ? null : Utils
								.textDate(pp.getBirtDate(), false);
						if (bdate != null && pp.getBirtPlace() != null) {
							bdate += " " + pp.getBirtPlace();
						} else if (pp.getBirtPlace() != null) {
							bdate = pp.getBirtPlace();
						}
						ddate = pp.getDeatDate() == null ? null : Utils
								.textDate(pp.getDeatDate(), false);
						if (ddate != null && pp.getDeatPlace() != null) {
							ddate += " " + pp.getDeatPlace();
						} else if (pp.getDeatPlace() != null) {
							ddate = pp.getDeatPlace();
						}
						pname = pp.getAlfaName(true);
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
					sb.append(" ");
					if (tableNumber > 0) {
						sb.append("(" + tableNumber + ")");
						sb.append("\n");
					}

					if (bdate != null) {
						sb.append(typesTable.getTextValue("ANC_BORN") + " ");
						sb.append(bdate);

					}

					sb.append("\n");
					if (ddate != null) {
						sb.append(typesTable.getTextValue("ANC_DIED") + " ");
						sb.append(ddate);

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
				// break;
			}
			// for (int i = 0; i < tabNext.size(); i++) {
			// System.out.println("TN:" + tabNext.get(i).getTableNo());
			// }
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
