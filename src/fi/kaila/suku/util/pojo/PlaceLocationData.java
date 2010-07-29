package fi.kaila.suku.util.pojo;

import java.io.Serializable;

/**
 * Used by Map task to contain Place name and location data
 * 
 * @author Kalle
 * 
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
	 * @param place
	 */
	public PlaceLocationData(String place, String countryCode) {
		this.placeName = place;
		this.countryCode = countryCode;
		this.counter = 1;

	}

	/**
	 * add one to number of occurrences
	 */
	public void increment() {
		this.counter++;
	}

	/**
	 * @return placename
	 */
	public String getName() {
		return this.placeName;
	}

	/**
	 * 
	 * @return country code for place
	 */
	public String getCountryCode() {
		return this.countryCode;
	}

	/**
	 * @return count of places
	 */
	public int getCount() {
		return this.counter;
	}

	/**
	 * @param latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @param longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return latitude
	 */
	public double getLatitude() {
		return this.latitude;
	}

	/**
	 * @return longitude
	 */
	public double getLongitude() {
		return this.longitude;
	}

}
