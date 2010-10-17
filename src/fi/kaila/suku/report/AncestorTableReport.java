package fi.kaila.suku.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Roman;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.ReportTableMember;
import fi.kaila.suku.util.pojo.ReportUnit;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * <h1>GenGraph Report</h1>.
 */
public class AncestorTableReport extends CommonReport {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor for AncestorTableReport.
	 * 
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param repoWriter
	 *            the repo writer
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
			JOptionPane.showMessageDialog(
					caller,
					Resurses.getString(Resurses.CREATE_REPORT) + ":"
							+ e.getMessage());
		}
		if (vlist.resu != null) {
			JOptionPane.showMessageDialog(caller,
					Resurses.getString(Resurses.CREATE_REPORT) + " ["
							+ vlist.resu + "]");
			return;
		}
		tables = vlist.tables;

		logger.info("AncestorTableReport");
		HashMap<Long, ReportUnit> tabMap = new HashMap<Long, ReportUnit>();

		for (int j = 0; j < tables.size(); j++) {
			ReportUnit ru = tables.get(j);
			tabMap.put(ru.getTableNo(), ru);
		}
		ArrayList<ReportUnit> tabNext = new ArrayList<ReportUnit>();

		ArrayList<IndexPerson> ipers = new ArrayList<IndexPerson>();
		String proband = "ANC_SUBJECT";
		try {

			WritableFont arial10bold = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.BOLD, false);
			WritableCellFormat bold10 = new WritableCellFormat(arial10bold);
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
			int generation = 0;
			int xpage = 0;

			ReportUnit rpux = tabMap.get(1L);
			tabNext.add(rpux);

			while (tabNext.size() > 0) {
				rpux = tabNext.remove(0);
				long currTab = rpux.getTableNo();

				for (int j = 0; j < 32; j++) {
					page[j] = null;
				}
				int mySize = tabNext.size();
				ReportUnit rpu = tabMap.get(currTab);

				page[1] = rpu;

				for (int i = 1; i <= 15; i++) {
					rpu = page[i];
					if (rpu != null) {
						currTab = rpu.getTableNo();
						if (rpu.getFatherPid() > 0) {
							rpux = tabMap.get(currTab * 2);
							if (rpux != null) {
								page[i * 2] = rpux;

								if (i > 7
										&& rpux.getFatherPid()
												+ rpux.getMotherPid() > 0) {
									tabNext.add(rpux);
								}
							} else {
								page[i * 2] = new ReportUnit();
								page[i * 2].setPid(rpu.getFatherPid());

							}
						}
						if (rpu.getMotherPid() > 0) {
							rpux = tabMap.get(currTab * 2 + 1);
							if (rpux != null) {
								page[i * 2 + 1] = rpux;
								if (i > 7
										&& rpux.getFatherPid()
												+ rpux.getMotherPid() > 0) {
									tabNext.add(rpux);
								}
							} else {
								page[i * 2 + 1] = new ReportUnit();
								page[i * 2 + 1].setPid(rpu.getMotherPid());

							}
						}
					}
				}

				ReportUnit uu;

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
				Label label = new Label(0, 1, "P " + xpage);
				sheet.addCell(label);
				int addTable = 0;
				for (int i = 1; i < 32; i++) {

					StringBuilder sb = new StringBuilder();
					uu = page[i];

					tabno = i;

					SukuData pappadata = null;
					String bdate = null;
					String ddate = null;
					int pid = 0;
					long tableNumber = 0;
					String pname = null;
					String occu = null;
					String contPage = null;
					int curgen = 0;
					String nemo = null;
					if (uu != null) {

						if (uu.getMemberCount() == 0) {
							nemo = typesTable.getTextValue("ANC_SEEALSO");

							pid = uu.getPid();
							ReportUnit rru = vlist.reportUnits.get(pid);
							if (rru != null) {
								nemo += " (" + rru.getPageNo() + ","
										+ rru.getTableNo() + ")";
							}

						} else {
							ReportTableMember rtu = uu.getMember(0);
							pid = rtu.getPid();
						}
						try {
							pappadata = caller.getKontroller().getSukuData(
									"cmd=person", "pid=" + pid,
									"lang=" + Resurses.getLanguage());
						} catch (SukuException e1) {
							logger.log(Level.WARNING, "background reporting",
									e1);
							JOptionPane.showMessageDialog(caller,
									e1.getMessage());
							return;
						}

						PersonShortData pp = new PersonShortData(
								pappadata.persLong);
						tableNumber = uu.getTableNo();
						curgen = uu.getGen();
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
						uu.setPageNo(xpage);
						IndexPerson ixp = new IndexPerson(xpage, pp);
						ipers.add(ixp);

						caller.setRunnerValue("" + xpage);
						if (i > 15) {

							// lets check if we continue
							if (uu.getFatherPid() + uu.getMotherPid() > 0) {
								// yes we do
								addTable++;
								int qsize = mySize;
								qsize += addTable;
								qsize += xpage;
								contPage = "P " + qsize;

							}

						}

					}
					String who = null;

					int ax;
					int bh = 5;
					switch ((int) tabno) {
					case 1:
						generation = curgen;
						row = 25;
						lrow = row + bh;
						col = 0;
						lcol = col + 2;
						who = typesTable.getTextValue(proband);
						proband = "ANC_NEXT";
						break;
					case 2:
						row = 11;
						lrow = row + bh;
						col = 1;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_FATHER");
						break;
					case 3:
						row = 39;
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
						row = 18;
						lrow = row + bh;
						col = 2;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_FM");
						break;
					case 6:
						row = 32;
						lrow = row + bh;
						col = 2;
						lcol = col + 2;
						who = typesTable.getTextValue("ANC_MF");
						break;
					case 7:
						row = 46;
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
					if (nemo != null) {
						sb.append(" [");
						sb.append(nemo);
						sb.append("]");
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
					if (contPage != null) {
						label = new Label(col + 2, row, contPage);
						sheet.addCell(label);
					}

					// Range rng =
					wsh.mergeCells(col, row, lcol, lrow);
					wsh.getWritableCell(col, row).setCellFormat(wrall);
					if (tabno == 1) {
						ReportUnit rru = vlist.reportUnits.get(pid);
						if (rru != null && rru.getPageNo() > 0 && xpage > 1) {

							label = new Label(0, 2,
									typesTable.getTextValue("ANC_FROM") + " "
											+ rru.getPageNo());
							sheet.addCell(label);
						}
					}
				}

				label = new Label(1, 0, Roman.int2roman(generation + 1), bold10);
				sheet.addCell(label);

				label = new Label(2, 0, Roman.int2roman(generation + 2), bold10);
				sheet.addCell(label);

				label = new Label(5, 0, Roman.int2roman(generation + 3), bold10);
				sheet.addCell(label);

				label = new Label(7, 0, Roman.int2roman(generation + 4), bold10);
				sheet.addCell(label);

				// break;
			}
			// for (int i = 0; i < tabNext.size(); i++) {
			// System.out.println("TN:" + tabNext.get(i).getTableNo());
			// }
			int sheetCount = workbook.getNumberOfSheets();
			WritableSheet sheet = workbook.createSheet("Index", sheetCount + 1);

			IndexPerson[] ipx = ipers.toArray(new IndexPerson[0]);
			Arrays.sort(ipx);
			col = 0;

			Label lab = new Label(col++, 0, typesTable.getTextValue("ANC_PAGE"));
			sheet.addCell(lab);
			lab = new Label(col++, 0, "Pid");
			sheet.addCell(lab);
			lab = new Label(col++, 0, typesTable.getTextValue("ANC_SURNAME"));
			sheet.addCell(lab);
			lab = new Label(col++, 0, typesTable.getTextValue("ANC_GIVENNAME"));
			sheet.addCell(lab);
			lab = new Label(col++, 0, typesTable.getTextValue("ANC_PATRONYM"));
			sheet.addCell(lab);
			lab = new Label(col++, 0, typesTable.getTextValue("ANC_BIRT"));
			sheet.addCell(lab);
			lab = new Label(col++, 0, typesTable.getTextValue("ANC_DEAT"));
			sheet.addCell(lab);
			StringBuilder tabpage = new StringBuilder();
			IndexPerson ipprevious = new IndexPerson(0, new PersonShortData());
			int rowx = 1;
			for (int i = 1; i < ipx.length; i++) {
				IndexPerson ipp = ipx[i];

				if (ipprevious.pu.getPid() > 0) {
					if (i == ipx.length - 1
							|| ipp.pu.getPid() != ipprevious.pu.getPid()) {
						if (i == ipx.length - 1) {
							ipprevious = ipp;
						}

						col = 0;

						lab = new Label(col++, rowx, tabpage.toString());
						sheet.addCell(lab);
						tabpage = new StringBuilder();

						jxl.write.Number nume = new jxl.write.Number(col++,
								rowx, ipprevious.pu.getPid());
						sheet.addCell(nume);

						StringBuilder sb = new StringBuilder();
						if (ipprevious.pu.getPrefix() != null) {
							sb.append(ipprevious.pu.getPrefix());
							sb.append(" ");
						}
						sb.append(Utils.nv(ipprevious.pu.getSurname()));
						lab = new Label(col++, rowx, sb.toString());
						sheet.addCell(lab);
						sb = new StringBuilder();
						sb.append(Utils.nv(ipprevious.pu.getGivenname()));
						if (ipprevious.pu.getPostfix() != null) {
							sb.append(" ");
							sb.append(ipprevious.pu.getPostfix());
						}
						lab = new Label(col++, rowx, Utils.nv(sb.toString()));
						sheet.addCell(lab);
						lab = new Label(col++, rowx, Utils.nv(ipprevious.pu
								.getPatronym()));
						sheet.addCell(lab);
						lab = new Label(col++, rowx, Utils.textDate(
								ipprevious.pu.getBirtDate(), true));
						sheet.addCell(lab);
						lab = new Label(col++, rowx, Utils.textDate(
								ipprevious.pu.getDeatDate(), true));
						sheet.addCell(lab);
						rowx++;
					}
					if (ipp.pu.getPid() == ipprevious.pu.getPid()) {
						if (tabpage.length() > 0) {
							tabpage.append(";");

						}
					}

				}
				tabpage.append(ipp.page);
				ipprevious = ipp;
			}
			workbook.write();
			workbook.close();
			bstr.close();

			String report = Suku.kontroller.getFilePath();
			Utils.openExternalFile(report);
		} catch (IOException e) {
			logger.log(Level.WARNING, "IOEx", e);

		} catch (RowsExceededException e) {
			logger.log(Level.WARNING, "RowsExx", e);
		} catch (WriteException e) {
			logger.log(Level.WARNING, "WriteExx", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.report.CommonReport#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b) {
		// not implemented here

	}

	/**
	 * The Class IndexPerson.
	 */
	class IndexPerson implements Comparable<IndexPerson> {

		/** The pu. */
		PersonShortData pu = null;

		/** The page. */
		int page = 0;

		/**
		 * Instantiates a new index person.
		 * 
		 * @param page
		 *            the page
		 * @param pp
		 *            the pp
		 */
		IndexPerson(int page, PersonShortData pp) {
			// this.tableNo = tableNo;
			this.page = page;
			this.pu = pp;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(IndexPerson o) {
			int resu = Utils.nv(pu.getSurname()).compareToIgnoreCase(
					Utils.nv(o.pu.getSurname()));
			if (resu != 0)
				return resu;
			resu = Utils.nv(pu.getGivenname()).compareToIgnoreCase(
					Utils.nv(o.pu.getGivenname()));
			if (resu != 0)
				return resu;
			return Utils.nv(pu.getPatronym()).compareToIgnoreCase(
					Utils.nv(o.pu.getPatronym()));

		}

	}

}
