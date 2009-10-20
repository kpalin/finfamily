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
	private int counter = 0;
	private double latitude = 0;
	private double longitude = 0;

	public PlaceLocationData(String place) {
		this.placeName = place;
		this.counter = 1;

	}

	/**
	 * add one to number of occurrences
	 */
	public void increment() {
		this.counter++;
	}

	public String getName() {
		return this.placeName;
	}

	public int getCount() {
		return this.counter;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

}
