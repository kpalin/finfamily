package fi.kaila.suku.swing;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;

/**
 * The Class SpecialWaypoint.
 * 
 * @author halonmi
 * 
 *         Extended Waypoint for WorldMap
 */
public class SpecialWaypoint extends Waypoint {
	private int count;

	/**
	 * Instantiates a new special waypoint.
	 * 
	 * @param count
	 *            the count
	 */
	public SpecialWaypoint(int count) {
		super();
		this.count = count;
	}

	/**
	 * Instantiates a new special waypoint.
	 * 
	 * @param latitude
	 *            the latitude
	 * @param longitude
	 *            the longitude
	 * @param count
	 *            the count
	 */
	public SpecialWaypoint(double latitude, double longitude, int count) {
		super(latitude, longitude);
		this.count = count;
	}

	/**
	 * Instantiates a new special waypoint.
	 * 
	 * @param coord
	 *            the coord
	 * @param count
	 *            the count
	 */
	public SpecialWaypoint(GeoPosition coord, int count) {
		super(coord);
		this.count = count;
	}

	/**
	 * Gets the count.
	 * 
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

}
