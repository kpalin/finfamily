package fi.kaila.suku.swing;

import fi.kaila.suku.util.pojo.PlaceLocationData;

/**
 * The Interface SukuMapInterface.
 */
public interface SukuMapInterface {

	/**
	 * Display map.
	 * 
	 * @param places
	 *            the places
	 */
	public void displayMap(PlaceLocationData[] places);
}
