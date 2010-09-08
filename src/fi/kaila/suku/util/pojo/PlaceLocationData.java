package fi.kaila.suku.util.pojo;

import java.io.Serializable;

/**
 * Used by Map task to contain Place name and location data.
 * 
 * @author Kalle
 */
public class PlaceLocationData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String placeName = null;
	private String countryCode = null;
	private int counter = 0;
	private double latitude = 0;
	private double longitude = 0;

	/**
	 * Instantiates a new place location data.
	 * 
	 * @param place
	 *            the place
	 * @param countryCode
	 *            the country code
	 */
	public PlaceLocationData(String place, String countryCode) {
		this.placeName = place;
		this.countryCode = countryCode;
		this.counter = 1;

	}

	/**
	 * add one to number of occurrences.
	 */
	public void increment() {
		this.counter++;
	}

	/**
	 * Gets the name.
	 * 
	 * @return placename
	 */
	public String getName() {
		return this.placeName;
	}

	/**
	 * Gets the country code.
	 * 
	 * @return country code for place
	 */
	public String getCountryCode() {
		return this.countryCode;
	}

	/**
	 * Gets the count.
	 * 
	 * @return count of places
	 */
	public int getCount() {
		return this.counter;
	}

	/**
	 * Sets the latitude.
	 * 
	 * @param latitude
	 *            the new latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Sets the longitude.
	 * 
	 * @param longitude
	 *            the new longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the latitude.
	 * 
	 * @return latitude
	 */
	public double getLatitude() {
		return this.latitude;
	}

	/**
	 * Gets the longitude.
	 * 
	 * @return longitude
	 */
	public double getLongitude() {
		return this.longitude;
	}

}
