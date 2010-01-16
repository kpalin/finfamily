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
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.worker.ReportWorkerDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
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

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Construcor for Descendant report
	 * 
	 * @param caller
	 * @param repoWriter
	 */
	public DescendantLista(ReportWorkerDialog caller, ReportInterface repoWriter) {
		super(caller, repoWriter);

	}

	/**
	 * execute the report.
	 */
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
			JOptionPane.showMessageDialog(caller, Resurses
					.getString(Resurses.CREATE_REPORT)
					+ ":" + e.getMessage());
			return;
		}

		int alltags = caller.getTypesTagsCount();
		Vector<String> tname = new Vector<String>();
		Vector<String> ttag = new Vector<String>();

		for (int i = 0; i < alltags; i++) {
			String tag = caller.getTypesTag(i);
			if (caller.isType(tag, 2)) {
				ttag.add(tag);
				tname.add(caller.getTagName(tag));
			}
		}

		logger.info("Descendant lista");

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
			int col = 0;
			int tagcol = 5;
			for (col = 0; col < tname.size(); col++) {
				label = new Label(tagcol + col, 0, tname.get(col));
				sheet.addCell(label);
			}
			col += tagcol;
			Number number;
			for (int i = 0; i < vlist.pidArray.length; i++) {
				int gen = vlist.pidArray[i];
				PersonShortData pp = vlist.pers[i];
				String text = vlist.generalArray[i];

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

				StringBuffer sb = new StringBuffer();
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

	@Override
	public void setVisible(boolean b) {
		// not implemented here

	}
}
