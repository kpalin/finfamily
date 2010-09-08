package fi.kaila.suku.util.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fi.kaila.suku.util.SukuException;

/**
 * Class used to read excel 2003 xml data.
 * 
 * @author Kalle
 * @deprecated
 */
public class ImportExcelData {

	private Connection con;

	private static Logger logger = Logger.getLogger(ImportExcelData.class
			.getName());
	private Document doc;

	/**
	 * Database connection and excel workbook file is set in constructor.
	 * 
	 * @param con
	 *            database as destination
	 * @param path
	 *            excel file as source
	 * @throws SukuException
	 *             the suku exception
	 */
	public ImportExcelData(Connection con, String path) throws SukuException {
		this.con = con;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		try {
			DocumentBuilder bld = factory.newDocumentBuilder();

			File excel = new File(path);

			doc = bld.parse(excel);

		} catch (ParserConfigurationException e) {

			e.printStackTrace();
			throw new SukuException(e);
		}

		catch (SAXException e) {

			e.printStackTrace();
			throw new SukuException(e);
		} catch (IOException e) {

			e.printStackTrace();
			throw new SukuException(e);
		}
	}

	/**
	 * import coordinates from excel xml.
	 * 
	 * @throws SukuException
	 *             the suku exception
	 */
	public void importCoordinates() throws SukuException {

		int idx;
		int colIdx;
		int rowIdx;
		Element docEle = doc.getDocumentElement();

		NodeList docl = docEle.getElementsByTagName("Worksheet");
		Element sheet;
		String sheetName;
		String aux;
		NodeList rows;
		NodeList cells;
		NodeList nl;
		Element row;
		Element cell;

		Element ele;
		int nameIdx = -1;
		int latiIdx = -1;
		int longIdx = -1;

		String INSERT_PLACELOC = "insert into PlaceLocations (PlaceName,Location) values (?,point(?,?))";
		String INSERT_PLACEOTHER = "insert into PlaceOtherNames (OtherName,PlaceName) values (?,?)";

		String DELETE_PLACELOC = "delete from PlaceLocations";
		String DELETE_PLACEOTHER = "delete from PlaceOtherNames";

		PreparedStatement pst;

		PreparedStatement pstOther;

		try {

			pst = con.prepareStatement(DELETE_PLACEOTHER);
			pst.executeUpdate();
			pst = con.prepareStatement(DELETE_PLACELOC);
			pst.executeUpdate();
			pst = con.prepareStatement(INSERT_PLACELOC);
			pstOther = con.prepareStatement(INSERT_PLACEOTHER);

		} catch (SQLException e) {

			e.printStackTrace();
			throw new SukuException(e);
		}

		for (idx = 0; idx < docl.getLength(); idx++) {
			sheet = (Element) docl.item(idx);
			sheetName = sheet.getAttribute("ss:Name");
			// System.out.println("attr:" + sheetName);

			if ("Coordinates".equalsIgnoreCase(sheetName)) {

				String placeName;
				double placeLatitude;
				double placeLongitude;
				int laskuri = 0;

				rows = sheet.getElementsByTagName("Row");

				row = (Element) rows.item(0); // get header row

				cells = row.getElementsByTagName("Cell");

				for (colIdx = 0; colIdx < cells.getLength(); colIdx++) {
					cell = (Element) cells.item(colIdx);

					nl = cell.getElementsByTagName("Data");
					if (nl.getLength() > 0) {
						ele = (Element) nl.item(0);
						aux = ele.getTextContent();
						if (aux != null) {
							if (aux.equalsIgnoreCase("place")) {
								nameIdx = colIdx;
							} else if (aux.equalsIgnoreCase("longitude")) {
								longIdx = colIdx;
							} else if (aux.equalsIgnoreCase("latitude")) {
								latiIdx = colIdx;
							}
						}
					}
				}
				if (nameIdx < 0 || latiIdx < 0 || longIdx < 0) {
					throw new SukuException(
							"Incorrect columns in coordinates page");
				}

				for (rowIdx = 1; rowIdx < rows.getLength(); rowIdx++) {
					row = (Element) rows.item(rowIdx); // get data row
					cells = row.getElementsByTagName("Cell");

					cell = (Element) cells.item(nameIdx);
					nl = cell.getElementsByTagName("Data");
					if (nl.getLength() > 0) {
						ele = (Element) nl.item(0);
						placeName = ele.getTextContent();
					} else {
						placeName = "";
					}

					if (placeName.length() > 0) {

						cell = (Element) cells.item(latiIdx);
						nl = cell.getElementsByTagName("Data");
						if (nl.getLength() > 0) {
							ele = (Element) nl.item(0);
							aux = ele.getTextContent();
						} else {
							aux = "";
						}
						try {
							placeLatitude = Double.parseDouble(aux);
						} catch (NumberFormatException ne) {
							placeLatitude = 0;
						}
						cell = (Element) cells.item(longIdx);
						nl = cell.getElementsByTagName("Data");
						if (nl.getLength() > 0) {
							ele = (Element) nl.item(0);
							aux = ele.getTextContent();
						} else {
							aux = "";
						}
						try {
							placeLongitude = Double.parseDouble(aux);
						} catch (NumberFormatException ne) {
							placeLongitude = 0;
						}

						try {
							pst.setString(1, placeName.toUpperCase());
							pst.setDouble(2, placeLongitude);
							pst.setDouble(3, placeLatitude);
							pst.executeUpdate();
							laskuri++;

						} catch (SQLException e) {
							logger.info("failed to insert " + placeName
									+ " at [" + placeLongitude + ";"
									+ placeLatitude + "] " + e.getMessage());
							e.printStackTrace();
							// throw new SukuException(e);
						}

						// System.out.println("Siis: " + placeName + " [" +
						// placeLongitude + ";" + placeLatitude + "]");

					}

				}

				logger.fine("inserted to  PlaceLocations " + laskuri
						+ " locations");

			} else if ("MuutNimet".equalsIgnoreCase(sheetName)) {

				String otherName;
				String placeName;
				int otherIdx = -1;
				int placeIdx = -1;
				int laskuri = 0;
				rows = sheet.getElementsByTagName("Row");

				row = (Element) rows.item(0); // get header row

				cells = row.getElementsByTagName("Cell");

				for (colIdx = 0; colIdx < cells.getLength(); colIdx++) {
					cell = (Element) cells.item(colIdx);

					nl = cell.getElementsByTagName("Data");
					if (nl.getLength() > 0) {
						ele = (Element) nl.item(0);
						aux = ele.getTextContent();
						if (aux != null) {
							if (aux.equalsIgnoreCase("othername")) {
								otherIdx = colIdx;
							} else if (aux.equalsIgnoreCase("placename")) {
								placeIdx = colIdx;
							}
						}
					}
				}
				if (otherIdx < 0 || placeIdx < 0) {
					throw new SukuException(
							"Incorrect columns in muutname page");
				}

				for (rowIdx = 1; rowIdx < rows.getLength(); rowIdx++) {
					row = (Element) rows.item(rowIdx); // get data row
					cells = row.getElementsByTagName("Cell");

					cell = (Element) cells.item(otherIdx);
					nl = cell.getElementsByTagName("Data");
					if (nl.getLength() > 0) {
						ele = (Element) nl.item(0);
						otherName = ele.getTextContent();
					} else {
						otherName = null;
					}

					if (otherName != null) {
						placeName = null;
						cell = (Element) cells.item(placeIdx);
						nl = cell.getElementsByTagName("Data");
						if (nl.getLength() > 0) {
							ele = (Element) nl.item(0);
							placeName = ele.getTextContent();
						}

						if (placeName != null) {

							try {
								pstOther.setString(1, otherName.toUpperCase());
								pstOther.setString(2, placeName.toUpperCase());

								pstOther.executeUpdate();
								laskuri++;
							} catch (SQLException e) {
								logger.info("failed to insert " + otherName
										+ " for [" + placeName + "] "
										+ e.getMessage());
								e.printStackTrace();
								// throw new SukuException(e);
							}
						}
						// System.out.println("Siis: " + placeName + " [" +
						// placeLongitude + ";" + placeLatitude + "]");

					}

				}
				logger.fine("inserted to  PlaceOtherNames " + laskuri + " rows");

			}
		}

	}

}
