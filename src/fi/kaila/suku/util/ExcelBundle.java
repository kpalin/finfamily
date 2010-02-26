package fi.kaila.suku.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

/**
 * 
 * A FinFamily specific resource bundle class using Excel file for input
 * 
 * @author Kalle
 * 
 */
public class ExcelBundle {

	HashMap<String, String> bundle = null;

	private static Logger logger = Logger
			.getLogger(ExcelBundle.class.getName());

	private ExcelBundle() {

	}

	/**
	 * @param path
	 * @param currentLocale
	 * @return an instance of this;
	 */
	public static ExcelBundle getBundle(String path, Locale currentLocale) {
		ExcelBundle me = new ExcelBundle();
		me.importBundle(path, currentLocale);
		return me;
	}

	/**
	 * @param name
	 * @return value of resource
	 */
	public String getString(String name) {
		if (bundle == null) {
			return name;
		}
		String value = bundle.get(name);
		if (value == null) {
			return name;
		}
		return value;
	}

	private void importBundle(String path, Locale locale) {
		WorkbookSettings ws = new WorkbookSettings();
		ws.setCharacterSet(0);
		ws.setEncoding("ISO-8859-1");
		// ws.setEncoding("UTF-8");

		ws.setCharacterSet(0);
		String myLocale = null;
		bundle = new HashMap<String, String>();
		if (locale != null) {
			myLocale = locale.getLanguage();
		}
		InputStream in = null;

		try {

			in = this.getClass().getResourceAsStream("/" + path + ".xls");

			Workbook workbook = Workbook.getWorkbook(in, ws);
			// Workbook workbook = Workbook.getWorkbook(new File("resources/"
			// + path + ".xls"), ws);

			Sheet sheet = workbook.getSheet("FinFamily");
			int colCount;
			int rowCount;
			String[] header;
			int rivi;
			int col;
			int defCol = 1;
			if (sheet != null) {
				colCount = sheet.getColumns();
				rowCount = sheet.getRows();
				header = new String[colCount];
				for (col = 0; col < colCount; col++) {
					Cell x0 = sheet.getCell(col, 0);
					header[col] = null;
					if (x0 != null) {
						header[col] = x0.getContents();
						if (myLocale != null && myLocale.equals(header[col])) {
							defCol = col;
						}
					}
				}

				for (rivi = 0; rivi < rowCount; rivi++) {

					Cell ac1 = sheet.getCell(0, rivi);

					String a1 = ac1.getContents();

					if (a1 != null && !a1.isEmpty()) {
						Cell xc1 = sheet.getCell(defCol, rivi);
						String x1 = xc1.getContents();
						if (x1 == null || x1.isEmpty()) {
							xc1 = sheet.getCell(1, rivi);
							x1 = xc1.getContents();
						}

						bundle.put(a1, x1);
					}
				}

			}

			workbook.close();

		} catch (Throwable e) {

			logger.log(Level.WARNING, "Excel bundle", e);

		}

	}

}
