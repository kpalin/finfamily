package fi.kaila.suku.swing.util;

import java.awt.Point;

import fi.kaila.suku.util.pojo.PersonShortData;

/**
 * The grapic family window implements this. May not be required really Graphic
 * parts is still under construction
 * 
 * @author Kalle
 * 
 */
public interface ISukuGraphicalItem {

	/**
	 * Locate the person at the point from the graphical Item
	 * 
	 * @param point
	 * @return Person at location pointed at or null
	 */
	public PersonShortData getPersonAtPoint(Point point);

}
