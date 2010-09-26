package fi.kaila.suku.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Vector;
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
import fi.kaila.suku.util.SukuTypesModel;
import fi.kaila.suku.util.SukuTypesTable;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * <h1>Descendant List</h1>
 * 
 * <p>
 * The descendant list creates an excel report of the subjects descendans in a
 * compresssed format to give user a view of the database.
 * </p>
 * 
 * <p>
 * The report includes persons generation, birthyear, name and information on
 * selected notices that person has.
 * </p>
 * 
 * @author Kalle
 * 
 */
public class DescendantLista extends CommonReport {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Construcor for Descendant report.
	 * 
	 * @param caller
	 *            the caller
	 * @param typesTable
	 *            the types table
	 * @param repoWriter
	 *            the repo writer
	 */
	public DescendantLista(ReportWorkerDialog caller,
			SukuTypesTable typesTable, ReportInterface repoWriter) {
		super(caller, typesTable, repoWriter);
	}

	/**
	 * execute the report.
	 */
	@Override
	public void executeReport() {
		SukuData vlist = null;

		if (!Suku.kontroller.createLocalFile("xls")) {
			return;
		}
		try {
			vlist = caller.getKontroller().getSukuData("cmd=crlista",
					"type=" + Resurses.CMD_DESC_TYPE, "pid=" + caller.getPid());
		} catch (SukuException e) {
			logger.log(Level.INFO, Resurses.getString(Resurses.CREATE_REPORT),
					e);
			JOptionPane.showMessageDialog(
					caller,
					Resurses.getString(Resurses.CREATE_REPORT) + ":"
							+ e.getMessage());
			return;
		}
		SukuTypesModel types = Utils.typeInstance();
		int alltags = types.getTypesTagsCount();
		Vector<String> tname = new Vector<String>();
		Vector<String> ttag = new Vector<String>();

		for (int i = 0; i < alltags; i++) {
			String tag = typesTable.getTypesTag(i);
			if (typesTable.isType(tag, 2)) {
				ttag.add(tag);
				tname.add(typesTable.getTagName(tag));
			}
		}

		logger.info("Descendant lista for [" + caller.getPid() + "]");

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

			Label label = new Label(0, 0, "Num");
			sheet.addCell(label);
			label = new Label(1, 0, "Gen");
			sheet.addCell(label);
			label = new Label(2, 0, "Birt");
			sheet.addCell(label);
			label = new Label(3, 0, "Group");
			sheet.addCell(label);
			label = new Label(4, 0, "Refn");
			sheet.addCell(label);
			WritableSheet wsh = sheet;

			int col = 0;
			int tagcol = 5;
			for (col = 0; col < tname.size() + 32; col++) {
				wsh.setColumnView(col, 5);
			}
			for (col = 0; col < tname.size(); col++) {
				label = new Label(tagcol + col, 0, tname.get(col));
				sheet.addCell(label);
			}

			col += tagcol;
			int genpids[] = new int[64];
			int genspids[] = new int[64];
			String gensex[] = new String[64];
			for (int i = 0; i < genpids.length; i++) {
				genpids[i] = 0;
				genspids[i] = 0;
				gensex[i] = "U";
			}
			Vector<ListPerson> lpp = new Vector<ListPerson>();
			Vector<ListPerson> lspouses = new Vector<ListPerson>();
			for (int i = 0; i < vlist.pidArray.length; i++) {
				ListPerson lp = new ListPerson(vlist.pers[i],
						vlist.pidArray[i], vlist.generalArray[i]);
				if (lp.tag.equals("WIFE") || lp.tag.equals("HUSB")) {
					lspouses.add(lp);
				} else {
					int mymp = -1;
					if (lp.gene > 0) {

						for (mymp = 0; mymp < lspouses.size(); mymp++) {
							ListPerson ppp = lspouses.get(mymp);
							int morsa = lp.ps.getFatherPid();
							if (gensex[lp.gene - 1].equals("M")) {
								morsa = lp.ps.getMotherPid();
							}
							if (morsa == ppp.ps.getPid()) {
								// this is my mother
								break;
							}
						}
						if (mymp >= 0 && mymp < lspouses.size()) {
							// first flush all other
							// while (lspouses.size() > mymp) {
							//
							// lpp.add(lspouses.get(mymp));
							// lspouses.remove(mymp);
							// }
							ListPerson lps = lspouses.get(mymp);
							lpp.add(lps);
							lspouses.remove(mymp);
							genspids[lps.gene] = lps.ps.getPid();
						}

					}

					// first flush all spouses for the generation
					for (mymp = 0; mymp < lspouses.size(); mymp++) {
						ListPerson ppp = lspouses.get(mymp);
						if (ppp.gene >= lp.gene)
							break;
					}
					while (lspouses.size() > mymp) {

						lpp.add(lspouses.get(mymp));
						lspouses.remove(mymp);
					}

					lpp.add(lp);
					genpids[lp.gene] = lp.ps.getPid();
					gensex[lp.gene] = lp.ps.getSex();
					if (lp.gene > 0
							&& lp.ps.getFatherPid() != genspids[lp.gene - 1]
							&& lp.ps.getMotherPid() != genspids[lp.gene - 1]) {
						lp.noParent = true;
					}
				}
			}

			Number number;
			for (int i = 0; i < lpp.size(); i++) {
				int gen = lpp.get(i).gene;
				PersonShortData pp = lpp.get(i).ps;
				String text = lpp.get(i).tag;
				boolean noPare = lpp.get(i).noParent;
				number = new Number(0, i + 1, i);
				sheet.addCell(number);

				number = new Number(1, i + 1, gen);
				sheet.addCell(number);
				String grp = pp.getGroup();
				if (grp != null) {
					label = new Label(3, i + 1, grp);
					sheet.addCell(label);
				}
				String refn = pp.getRefn();
				if (refn != null) {
					label = new Label(4, i + 1, refn);
					sheet.addCell(label);
				}
				int byear = pp.getBirtYear();
				if (byear > 0) {
					number = new Number(2, i + 1, byear);
					sheet.addCell(number);
				}
				// label = new Label(2, i+1, text);
				// sheet.addCell(label);
				int coln = col + gen;
				String bdate = pp.getBirtDate() == null ? null : pp
						.getBirtDate().substring(0, 4);
				// label = new Label(col, i+1,date );
				// sheet.addCell(label);
				// col++;
				String ddate = pp.getDeatDate() == null ? null : pp
						.getDeatDate().substring(0, 4);
				// label = new Label(col, i+1, date);
				// sheet.addCell(label);
				//
				// col++;

				for (int jj = 0; jj < ttag.size(); jj++) {
					if (pp.existsTag(ttag.get(jj))) {
						label = new Label(jj + tagcol, i + 1, "XX");
						sheet.addCell(label);
					}
				}
				if (noPare) {
					label = new Label(coln - 1, i + 1, "?", italic10bold);
					sheet.addCell(label);
				}
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
				if (text.equals("CHIL")) {
					label = new Label(coln, i + 1, sb.toString(), italic10bold);
				} else {
					label = new Label(coln, i + 1, sb.toString(),
							italic10format);
				}

				sheet.addCell(label);
			}

			// Label label = new Label(0, 2, "A label record");
			// sheet.addCell(label);
			//
			// Number number = new Number(3, 4, 3.1459);
			// sheet.addCell(number);
			//

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

		// repoWriter.createReport();
		//
		// repoWriter.closeReport();
		//
		//

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
	 * Temp Storage for one person info
	 * 
	 * @author kalle
	 * 
	 */
	class ListPerson {
		PersonShortData ps = null;
		int gene = 0;
		String tag = null;
		boolean noParent = false;

		ListPerson(PersonShortData ps, int gene, String tag) {
			this.ps = ps;
			this.gene = gene;
			this.tag = tag;
		}

	}

}
