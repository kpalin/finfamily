package fi.kaila.suku.swing;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;

/**
 * @author halonmi
 * 
 * Extended Waypoint for WorldMap
 *
 */
public class SpecialWaypoint extends Waypoint {
	private int count;

	/**
	 * @param count
	 */
	public SpecialWaypoint(int count) {
		super();
		this.count = count;
	}

	/**
	 * @param latitude
	 * @param longitude
	 * @param count
	 */
	public SpecialWaypoint(double latitude, double longitude, int count) {
		super(latitude, longitude);
		this.count = count;
	}

	/**
	 * @param coord
	 * @param count
	 */
	public SpecialWaypoint(GeoPosition coord, int count) {
		super(coord);
		this.count = count;
	}

	/**
	 * @return
	 */
	public int getCount() {
		return count;
	}

}
