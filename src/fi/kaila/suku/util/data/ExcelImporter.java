package fi.kaila.suku.util.data;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.logging.Logger;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;
import jxl.*;

/**
 * Class to import data from excel file
 * 
 * @author Kalle
 *
 */
public class ExcelImporter {

	private static Logger logger= Logger.getLogger(ExcelImporter.class.getName());
	
	
	/**
	 * Import the types data
	 * @param con
	 * @param path
	 * @return types and texts
	 * @throws SukuException
	 */
	public SukuData importTypes(Connection con, String path) throws SukuException {
		
		SukuData suk = new SukuData();
		try {
			Workbook workbook = Workbook.getWorkbook(new File(path));
			
			Sheet sheet = workbook.getSheet("Types");
			
			int rivi;
			int col;
			int colCount = sheet.getColumns();		
			int rowCount = sheet.getRows();
			
			String header[] = new String[colCount];
			int text_col[] = new int[colCount];
			for (col=0; col < colCount; col++){
				Cell x0 = sheet.getCell(col,0);
				header[col]= null;
				if (x0 != null){
					header[col] = x0.getContents();
					
					
					
					
				}
			}
			
			for (col = 0; col < colCount; col++) {
				text_col[col]=-1;
				if (header[col].length()==2 ){
					
					for (int j = col+1; j < colCount; j++) {
						if (header[j] != null && header[j].equals("text_"+header[col])){
							text_col[col] = j;
							break;
						}
					}
					
				}
			}
			
			
			String INSERT_TYPES = "insert into Types (TagType,Tag,Rule,LangCode,Name,ReportName) " +
					" values (?,?,?,?,?,?)";

	        String DELETE_TYPES = "delete from Types";
	        
	        PreparedStatement pst;
	        
	        
			try {
				
				pst = con.prepareStatement(DELETE_TYPES);
				pst.executeUpdate();
				
				pst = con.prepareStatement(INSERT_TYPES);
			
				
			} catch (SQLException e) {
				
				e.printStackTrace();
				throw new SukuException(e);
			}
		
			
			for (rivi = 1;rivi < rowCount; rivi++){
			
				Cell ac1 = sheet.getCell(0,rivi);
				Cell bc1 = sheet.getCell(1,rivi);
				Cell cc1 = sheet.getCell(2,rivi);
				
				String a1 = ac1.getContents();
				String b1 = bc1.getContents();
				String c1 = cc1.getContents();
				
				for (col = 3; col < colCount; col++){
					Cell xc1 = sheet.getCell(col,rivi);
					String x1=null;
					if (xc1 != null) {
						 x1 = xc1.getContents();
						 if ("".equals(x1) ) {
							x1=null;
						 }
					}
					String y1 = null;
					if ( header[col].length()==2){
							
						pst.setString(1, a1);
						pst.setString(2, b1);
						if ("".equals(c1)){
							c1 = null;
						}
						pst.setString(3, c1);
						pst.setString(4, header[col]);
						pst.setString(5, x1);
						if (text_col[col] > 0) {
							Cell yc1 = sheet.getCell(text_col[col],rivi);
							if (yc1 != null ) {
								y1 = yc1.getContents();
								if ("".equals(y1)){
									y1=null;
								}
							}							
						}
						pst.setString(6, y1);
						pst.executeUpdate();
					}
//					}
				}
			}
			

			
//			workbook = Workbook.getWorkbook(new File(path));
			
			sheet = workbook.getSheet("Texts");
			
		
			 colCount = sheet.getColumns();		
			 rowCount = sheet.getRows();
			
			header = new String[colCount];
			
			for (col=0; col < colCount; col++){
				Cell x0 = sheet.getCell(col,0);
				header[col]= null;
				if (x0 != null){
					header[col] = x0.getContents();
					
					
					
					
				}
			}
			
			
			
			String INSERT_TEXTS = "insert into Texts (TagType,Tag,LangCode,Name) " +
					" values (?,?,?,?)";

	        String DELETE_TEXTS = "delete from Texts";
	        
	       
	        
	        
			try {
				
				pst = con.prepareStatement(DELETE_TEXTS);
				pst.executeUpdate();
				
				pst = con.prepareStatement(INSERT_TEXTS);
			
				
			} catch (SQLException e) {
				
				e.printStackTrace();
				throw new SukuException(e);
			}
		
			
			for (rivi = 1;rivi < rowCount; rivi++){
			
				Cell ac1 = sheet.getCell(0,rivi);
				Cell bc1 = sheet.getCell(1,rivi);
				
				
				String a1 = ac1.getContents();
				String b1 = bc1.getContents();
				
				
				for (col = 2; col < colCount; col++){
					Cell xc1 = sheet.getCell(col,rivi);
					String x1=null;
					if (xc1 != null) {
						 x1 = xc1.getContents();
						 if ("".equals(x1) ) {
							x1=null;
						 }
					}
					
					if ( header[col].length()==2){
							
						pst.setString(1, a1);
						pst.setString(2, b1);
						pst.setString(3, header[col]);
						pst.setString(4, x1);
						pst.executeUpdate();
					}
//					}
				}
			}

			workbook.close();
			
		} catch (Exception e) {
			suk.resu = e.getMessage();
			e.printStackTrace();
		} 
		
		return suk;
		
	}

	/**
	 * Import the coordinates data
	 * @param con
	 * @param path
	 * @return coordinates reuqested
	 * @throws SukuException
	 */
	public SukuData importCoordinates(Connection con, String path) throws SukuException {

		SukuData suk = new SukuData();
		
        String INSERT_PLACELOC = "insert into PlaceLocations (PlaceName,Location) values (?,point(?,?))";
        String INSERT_PLACEOTHER = "insert into PlaceOtherNames (OtherName,PlaceName) values (?,?)";

        String DELETE_PLACELOC = "delete from PlaceLocations";
        String DELETE_PLACEOTHER = "delete from PlaceOtherNames";
        
        PreparedStatement pst;
        
        
        Workbook workbook;
		try {
			workbook = Workbook.getWorkbook(new File(path));
			Sheet sheet = workbook.getSheet("Coordinates");
			
			int rivi;
			int col;
			int colCount = sheet.getColumns();		
			int rowCount = sheet.getRows();
			
			String header[] = new String[colCount];
			
			for (col=0; col < colCount; col++){
				Cell x0 = sheet.getCell(col,0);
				header[col]= null;
				if (x0 != null){
					header[col] = x0.getContents();				
				}
			}
			
			if (!"place".equalsIgnoreCase(header[0]) ||
					!"latitude".equalsIgnoreCase(header[1]) ||
					!"longitude".equalsIgnoreCase(header[2]) ) {
				throw new SukuException("Incorrect columns in coordinates page");
			}
			
		
			
			pst = con.prepareStatement(DELETE_PLACEOTHER);
			pst.executeUpdate();
			pst = con.prepareStatement(DELETE_PLACELOC);
			pst.executeUpdate();
			pst = con.prepareStatement(INSERT_PLACELOC);
			
				
		
				
			String placeName;
			double placeLatitude;
			double placeLongitude;
			int laskuri=0;
			
			for (rivi = 1;rivi < rowCount; rivi++){
				
				Cell ac1 = sheet.getCell(0,rivi);
				Cell bc1 = sheet.getCell(1,rivi);
				Cell cc1 = sheet.getCell(2,rivi);
				
				placeName = ac1.getContents();
				
				String b1 = bc1.getContents();
				String c1 = cc1.getContents();
				
				if (placeName != null && b1 != null && c1 != null) {
					
					String b2 = b1.replace(',', '.');
					placeLongitude = Double.parseDouble(b2);
					
					String c2 = c1.replace(',', '.');
					placeLatitude  = Double.parseDouble(c2);
					
					
					try {	
						pst.setString(1, placeName.toUpperCase());
						pst.setDouble(2, placeLatitude);
						pst.setDouble(3, placeLongitude);
						pst.executeUpdate();
						laskuri++;
						
					} catch (SQLException e) {
						logger.info("failed to insert " + placeName + " at [" + placeLongitude + ";" + placeLatitude + "] " + e.getMessage());
						e.printStackTrace();
					}
					
					
				}
			
			}
			logger.info("inserted " + laskuri + " places with locations");
			
			sheet = workbook.getSheet("MuutNimet");
			
			
			 colCount = sheet.getColumns();		
			 rowCount = sheet.getRows();
			
			
			 header= new String[colCount];
			
			for (col=0; col < colCount; col++){
				Cell x0 = sheet.getCell(col,0);
				header[col]= null;
				if (x0 != null){
					header[col] = x0.getContents();				
				}
			}
			
			if (!"othername".equalsIgnoreCase(header[0]) ||
					!"placename".equalsIgnoreCase(header[1])) {
				throw new SukuException("Incorrect columns in muutnimet page");
			}
				
			pst = con.prepareStatement(INSERT_PLACEOTHER);
			
			String otherName;
			
			laskuri=0;
			
			for (rivi = 1;rivi < rowCount; rivi++){
				
				Cell ac1 = sheet.getCell(0,rivi);
				Cell bc1 = sheet.getCell(1,rivi);
				
				
				placeName = ac1.getContents();
				otherName = bc1.getContents();
				
				if (placeName != null && otherName != null ) {
					
					try {	
						
						pst.setString(1, placeName.toUpperCase());
						pst.setString(2, otherName.toUpperCase());
						
						pst.executeUpdate();
						laskuri++;
						
					} catch (SQLException e) {
						logger.info("failed to insert " + otherName + " for [" + placeName + "] " + e.getMessage());
						e.printStackTrace();
					}
					
					
				}
			
			}
			logger.info("inserted " + laskuri + " othernames for places");
		
		
		} catch (Exception e1) {
			suk.resu = e1.getMessage();
			e1.printStackTrace();
		} 

				
				


		return suk;
	}
	
	
}
