package fi.kaila.suku.util;

import java.util.Comparator;

/**
 * Comparator for suku dates. Suku date is text string in one of following formats
 * 
 * <ul>
 * <li>YYYYMMDD</li>
 * <li>YYYYMM</li>
 * <li>YYYY</li>
 * </ul>
 * 
 *
 * @author Kalle
 *
 */
@SuppressWarnings("unchecked")
public class SukuDateComparator implements Comparator {

	@Override
	public int compare(Object arg0, Object arg1) {
		if (arg0 == null || arg1 == null) return 0;

		
		
		try {
			String a = Utils.dbDate(arg0.toString());
			String b = Utils.dbDate(arg1.toString());
			
			return a.compareTo(b);
		} catch (SukuDateException e) {
			return 0;
		}
		
	}

}
