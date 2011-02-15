package fi.kaila.suku.util.data;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Class to import data from excel file.
 * 
 * @author Kalle
 */
public class ExcelImporter {

	private static Logger logger = Logger.getLogger(ExcelImporter.class
			.getName());

	/**
	 * Import the types data.
	 * 
	 * @param con
	 *            the con
	 * @param path
	 *            the path
	 * @return types
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData importTypes(Connection con, InputStream fis)
			throws SukuException {
		WorkbookSettings ws = new WorkbookSettings();
		ws.setEncoding("ISO-8859-1");
		ws.setCharacterSet(0);
		SukuData suk = new SukuData();
		suk.resu = Resurses.getString("UNKNOWN_EXCEL_TYPE");
		try {
			Workbook workbook = Workbook.getWorkbook(fis, ws);

			String[] names = workbook.getSheetNames();

			Sheet sheet = workbook.getSheet("Types");
			int colCount;
			int rowCount;
			String[] header;
			int rivi;
			int col;
			PreparedStatement pst;
			if (sheet != null) {
				suk.resu = null;
				colCount = sheet.getColumns();
				rowCount = sheet.getRows();

				header = new String[colCount];
				int text_col[] = new int[colCount];
				for (col = 0; col < colCount; col++) {
					Cell x0 = sheet.getCell(col, 0);
					header[col] = null;
					if (x0 != null) {
						header[col] = x0.getContents();

					}
				}

				for (col = 0; col < colCount; col++) {
					text_col[col] = -1;
					if (header[col].length() == 2) {

						for (int j = col + 1; j < colCount; j++) {
							if (header[j] != null
									&& header[j].equals("text_" + header[col])) {
								text_col[col] = j;
								break;
							}
						}

					}
				}

				String INSERT_TYPES = "insert into Types (TagType,Tag,Rule,LangCode,Name,ReportName) "
						+ " values (?,?,?,?,?,?)";
				String DELETE_TYPES = "delete from Types";
				String UPDATE_SETTINGS = "update SukuSettings "
						+ "set settingvalue = substring(settingvalue for 5)  "
						+ "where settingtype = 'reporttypes' ";
				try {

					pst = con.prepareStatement(DELETE_TYPES);
					pst.executeUpdate();
					pst = con.prepareStatement(UPDATE_SETTINGS);
					pst.executeUpdate();
					pst = con.prepareStatement(INSERT_TYPES);

				} catch (SQLException e) {

					e.printStackTrace();
					throw new SukuException(e);
				}

				for (rivi = 1; rivi < rowCount; rivi++) {

					Cell ac1 = sheet.getCell(0, rivi);
					Cell bc1 = sheet.getCell(1, rivi);
					Cell cc1 = sheet.getCell(2, rivi);

					String a1 = ac1.getContents();
					String b1 = bc1.getContents();
					String c1 = cc1.getContents();
					if ("Notice".equalsIgnoreCase(a1)) {
						for (col = 3; col < colCount; col++) {
							Cell xc1 = sheet.getCell(col, rivi);
							String x1 = null;
							if (xc1 != null) {
								x1 = xc1.getContents();
								if (x1.length() == 0) {
									x1 = null;
								}
							}
							String y1 = null;
							if (header[col].length() == 2) {

								pst.setString(1, a1);
								pst.setString(2, b1);
								if (c1 != null && c1.length() == 0) {
									c1 = null;
								}
								pst.setString(3, c1);
								pst.setString(4, header[col]);
								pst.setString(5, x1);
								if (text_col[col] > 0) {
									Cell yc1 = sheet.getCell(text_col[col],
											rivi);
									if (yc1 != null) {
										y1 = yc1.getContents();
										if (y1.length() == 0) {
											y1 = null;
										}
									}
								}
								pst.setString(6, y1);
								pst.executeUpdate();
							}
						}
					}
				}
			}

			for (int i = 0; i < names.length; i++) {
				if (names[i].length() == 2) {
					// let's check if it's a conversion sheet
					sheet = workbook.getSheet(names[i]);
					if (sheet != null) {

						colCount = sheet.getColumns();
						rowCount = sheet.getRows();

						header = new String[colCount];

						int placeCol = -1;
						int inCol = -1;
						int toCol = -1;
						int fromCol = -1;

						for (col = 0; col < colCount; col++) {
							Cell x0 = sheet.getCell(col, 0);
							header[col] = null;
							if (x0 != null) {
								header[col] = x0.getContents();
								if ("place".equalsIgnoreCase(header[col])) {
									placeCol = col;
								} else if ("in".equalsIgnoreCase(header[col])) {
									inCol = col;
								} else if ("to".equalsIgnoreCase(header[col])) {
									toCol = col;
								} else if ("from".equalsIgnoreCase(header[col])) {
									fromCol = col;
								}

							}
						}
						if (placeCol >= 0 && inCol >= 0 && toCol >= 0
								&& fromCol >= 0) {
							suk.resu = null;

							//
							String INSERT_CONVERSIONS = "insert into Conversions (FromText,LangCode,Rule,ToText) "
									+ " values (?,?,?,?)";

							String DELETE_CONVERSIONS = "delete from Conversions where fromtext = ? ";
							PreparedStatement pstdel = null;
							try {

								pstdel = con
										.prepareStatement(DELETE_CONVERSIONS);
								// pst.executeUpdate();

								pst = con.prepareStatement(INSERT_CONVERSIONS);

							} catch (SQLException e) {

								e.printStackTrace();
								throw new SukuException(e);
							}

							for (rivi = 1; rivi < rowCount; rivi++) {

								Cell ac1 = sheet.getCell(placeCol, rivi);
								Cell bc1 = sheet.getCell(inCol, rivi);
								Cell cc1 = sheet.getCell(toCol, rivi);
								Cell dc1 = sheet.getCell(fromCol, rivi);
								String a1 = ac1.getContents();
								String b1 = bc1.getContents();
								String c1 = cc1.getContents();
								String d1 = dc1.getContents();

								if (a1 != null && !a1.isEmpty()) {

									pstdel.setString(1, a1);
									pstdel.executeUpdate();

									if (b1 != null && !b1.isEmpty()
											&& !b1.equals("XXX")) {

										pst.setString(1, a1);
										pst.setString(2, names[i].toLowerCase());
										pst.setString(3, "IN");
										pst.setString(4, b1);
										pst.executeUpdate();
									}

									if (c1 != null && !c1.isEmpty()
											&& !c1.equals("XXX")) {
										pst.setString(1, a1);
										pst.setString(2, names[i].toLowerCase());
										pst.setString(3, "TO");
										pst.setString(4, c1);
										pst.executeUpdate();
									}
									if (d1 != null && !d1.isEmpty()
											&& !d1.equals("XXX")) {
										pst.setString(1, a1);
										pst.setString(2, names[i].toLowerCase());
										pst.setString(3, "FROM");
										pst.setString(4, d1);
										pst.executeUpdate();
									}
								}
							}
						}
					}
				}
			}

			workbook.close();

		} catch (Throwable e) {
			suk.resu = e.getMessage();
			logger.log(Level.WARNING, "Excel import", e);

		}

		return suk;

	}

	/**
	 * Import the coordinates data.
	 * 
	 * @param con
	 *            the con
	 * @param path
	 *            the path
	 * @return coordinates reuqested
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData importCoordinates(Connection con, InputStream fis)
			throws SukuException {

		SukuData suk = new SukuData();

		Vector<String> errvec = new Vector<String>();

		String INSERT_PLACELOC = "insert into PlaceLocations (PlaceName,CountryCode,Location) values (?,?,point(?,?))";
		String INSERT_PLACEOTHER = "insert into PlaceOtherNames (OtherName,CountryCode,PlaceName) values (?,?,?)";

		String DELETE_PLACELOC = "delete from PlaceLocations";
		String DELETE_PLACEOTHER = "delete from PlaceOtherNames";

		PreparedStatement pst;
		PreparedStatement pstOther;
		Workbook workbook;
		try {
			WorkbookSettings ws = new WorkbookSettings();
			ws.setEncoding("ISO-8859-1");
			ws.setCharacterSet(0);

			workbook = Workbook.getWorkbook(fis, ws);
			int sheetCount = workbook.getNumberOfSheets();

			pst = con.prepareStatement(DELETE_PLACEOTHER);
			pst.executeUpdate();
			pst = con.prepareStatement(DELETE_PLACELOC);
			pst.executeUpdate();

			for (int sheetIdx = 0; sheetIdx < sheetCount; sheetIdx++) {

				Sheet sheet = workbook.getSheet(sheetIdx);

				String sheetName = sheet.getName();

				int rivi;
				int col;
				int colCount = sheet.getColumns();
				int rowCount = sheet.getRows();

				String header[] = new String[colCount];

				for (col = 0; col < colCount; col++) {
					Cell x0 = sheet.getCell(col, 0);
					header[col] = null;
					if (x0 != null) {
						header[col] = x0.getContents();
					}
				}

				if (!"place".equalsIgnoreCase(header[0])
						|| !"latitude".equalsIgnoreCase(header[1])
						|| !"longitude".equalsIgnoreCase(header[2])) {
					throw new SukuException(
							"Incorrect columns in coordinates page");
				}

				pst = con.prepareStatement(INSERT_PLACELOC);
				pstOther = con.prepareStatement(INSERT_PLACEOTHER);

				String placeName;
				double placeLatitude;
				double placeLongitude;
				int laskuri1 = 0;

				for (rivi = 1; rivi < rowCount; rivi++) {

					Cell ac1 = sheet.getCell(0, rivi);
					Cell bc1 = sheet.getCell(1, rivi);
					Cell cc1 = sheet.getCell(2, rivi);
					// LabelCell ll = (LabelCell) ac1;
					placeName = ac1.getContents();
					// String llname = ll.getString();
					// String utname = toUtf(placeName);
					// placeName = utname;
					String b1 = bc1.getContents();
					String c1 = cc1.getContents();

					if (placeName != null && b1 != null && c1 != null
							&& !b1.isEmpty() && !c1.isEmpty()) {

						String b2 = b1.replace(',', '.');
						placeLongitude = Double.parseDouble(b2);

						String c2 = c1.replace(',', '.');
						placeLatitude = Double.parseDouble(c2);

						try {
							pst.setString(1, placeName.toUpperCase());
							pst.setString(2, sheetName.toUpperCase());
							pst.setDouble(3, placeLatitude);
							pst.setDouble(4, placeLongitude);
							pst.executeUpdate();
							laskuri1++;

						} catch (SQLException e) {
							String error = "Failed to insert " + placeName
									+ " at [" + placeLongitude + ";"
									+ placeLatitude + "] \n" + e.getMessage()
									+ "\n";
							logger.info(error);
							errvec.add(error);
							// e.printStackTrace();
						}

					}

				}

				String otherName;

				int laskuri2 = 0;

				for (rivi = 1; rivi < rowCount; rivi++) {

					Cell ac1 = sheet.getCell(0, rivi);
					placeName = ac1.getContents();

					int colo = 0;
					for (colo = 3; colo < colCount; colo++) {
						Cell bc1 = sheet.getCell(colo, rivi);
						if (bc1 != null) {
							otherName = bc1.getContents();
							if (otherName != null && !otherName.isEmpty()) {
								try {
									pstOther.setString(1,
											otherName.toUpperCase());
									pstOther.setString(2,
											sheetName.toUpperCase());
									pstOther.setString(3,
											placeName.toUpperCase());

									pstOther.executeUpdate();
									laskuri2++;
								} catch (SQLException e) {
									String error = "failed to insert "
											+ otherName + " for [" + placeName
											+ "] \n" + e.getMessage() + "\n";
									logger.info(error);
									errvec.add(error);
									// e.printStackTrace();
								}
							}

						}

					}

				}
				pst.close();
				pstOther.close();

				logger.info("inserted " + laskuri1
						+ " places with locations and " + laskuri2
						+ " othernames in [" + sheetName.toUpperCase() + "]");

			}

		} catch (Throwable e1) {
			suk.resu = e1.getMessage();

		} finally {
			suk.generalArray = errvec.toArray(new String[0]);
		}

		return suk;
	}

	/**
	 * Export coordinates.
	 * 
	 * @param con
	 *            the con
	 * @param path
	 *            the path
	 * @param langCode
	 *            the lang code
	 * @param doAll
	 *            the do all
	 * @return SukuData for response status
	 */
	public SukuData exportCoordinates(Connection con, String path,
			String langCode, boolean doAll) {
		SukuData resp = new SukuData();
		String sql;
		if (doAll) {
			sql = "select coalesce(u.place,c.fromtext)as place,c.rule,coalesce(c.totext,'XXX') as totext,count(*) "
					+ "from unitnotice as u "
					+ "inner join types as t on u.tag=t.tag and t.rule is not null and t.langcode = '"
					+ langCode
					+ "' "
					+ "right join conversions as c on u.place=c.fromtext and c.rule = t.rule "
					+ "group by place,c.fromtext,c.rule,c.totext order by place ";
		} else {
			sql = "select u.place,t.rule,coalesce(c.totext,'XXX') as totext,count(*) "
					+ "from unitnotice as u "
					+ "inner join types as t on u.tag=t.tag and t.rule is not null and t.langcode = '"
					+ langCode
					+ "' "
					+ "left join conversions as c on u.place=c.fromtext and c.rule = t.rule "
					+ "where u.place is not null group by u.place,t.rule,c.totext order by u.place";
		}

		try {

			BufferedOutputStream bstr = new BufferedOutputStream(
					Suku.kontroller.getOutputStream());
			WritableWorkbook workbook = Workbook.createWorkbook(bstr);

			WritableFont arial10bold = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.BOLD, true);
			WritableFont arial10 = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD, false);
			WritableCellFormat arial0bold = new WritableCellFormat(arial10bold);
			WritableCellFormat arial0 = new WritableCellFormat(arial10);
			WritableSheet sheet = workbook.createSheet(langCode, 0);

			Label label = new Label(0, 0, "Place", arial0bold);
			sheet.addCell(label);
			label = new Label(1, 0, "Count", arial0bold);
			sheet.addCell(label);
			label = new Label(2, 0, "IN", arial0bold);
			sheet.addCell(label);
			label = new Label(3, 0, "TO", arial0bold);
			sheet.addCell(label);
			label = new Label(4, 0, "FROM", arial0bold);
			sheet.addCell(label);

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(sql);

			int row = 1;
			String currPlace = "";
			int lukuri = 0;
			while (rs.next()) {
				String place = rs.getString(1);
				String rule = rs.getString(2);
				String toText = rs.getString(3);
				int counter = rs.getInt(4);
				if (!place.equals(currPlace)) {
					if (!currPlace.isEmpty()) {
						label = new Label(1, row, "" + lukuri, arial0);
						sheet.addCell(label);
						lukuri = 0;
					}
					row++;
					label = new Label(0, row, place, arial0);
					sheet.addCell(label);
					currPlace = place;
				}
				lukuri += counter;
				if ("IN".equals(rule)) {
					label = new Label(2, row, toText, arial0);
					sheet.addCell(label);
				} else if ("TO".equals(rule)) {
					label = new Label(3, row, toText, arial0);
					sheet.addCell(label);
				} else if ("FROM".equals(rule)) {
					label = new Label(4, row, toText, arial0);
					sheet.addCell(label);
				}

			}
			label = new Label(1, row, "" + lukuri, arial0);
			sheet.addCell(label);
			rs.close();
			stm.close();

			workbook.write();
			workbook.close();
			bstr.close();

		} catch (Throwable e) {
			resp.resu = e.getMessage();
			logger.log(Level.WARNING, "Exception in background thread", e);
		}

		// ///////////////////////////////////////////////////////
		return resp;
	}

}
