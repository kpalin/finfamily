package fi.kaila.suku.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PlaceLocationData;

/**
 * Class used by SuomiMap view.
 * 
 * @author Kalle
 */
public class SuomiPlacesResolver {

	/**
	 * Server class to fetch addresses to request list.
	 * 
	 * @param con
	 *            the con
	 * @param request
	 *            the request
	 * @return array of places with coordinates
	 * @throws SukuException
	 *             the suku exception
	 */
	public static PlaceLocationData[] resolveSuomiPlaces(Connection con,
			PlaceLocationData[] request) throws SukuException {

		if (request == null)
			return request;

		int idx;

		PlaceLocationData[] response = request;

		StringBuilder sql = new StringBuilder();
		sql.append("select location[0],location[1],countrycode from placelocations where placename || ';' || countrycode  in ( ");
		sql.append("select placename || ';' || countrycode from placeothernames where othername = ?) ");
		sql.append("union ");
		sql.append("select location[0],location[1],countrycode from placelocations where placename = ? ");

		PreparedStatement pstm;
		String countryCode = null;
		try {
			pstm = con.prepareStatement(sql.toString());

			for (idx = 0; idx < response.length; idx++) {

				pstm.setString(1, response[idx].getName().toUpperCase());
				pstm.setString(2, response[idx].getName().toUpperCase());

				ResultSet rs = pstm.executeQuery();
				while (rs.next()) {
					countryCode = rs.getString(3);
					if (countryCode.equalsIgnoreCase(response[idx]
							.getCountryCode())
							|| response[idx].getCountryCode() == null) {
						response[idx].setLongitude(rs.getDouble(1));
						response[idx].setLatitude(rs.getDouble(2));
					}

				}
				rs.close();

			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SukuException("Placelocations error " + e.getMessage());
		}
		return response;

	}

}
