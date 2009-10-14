package fi.kaila.suku.util;

import java.util.Comparator;

/**
 * 
 * Standard String comparator for databasetable
 *  
 * @author FIKAAKAIL
 *
 */
@SuppressWarnings("unchecked")
public class SukuStringComparator implements Comparator {

	public int compare(Object arg0, Object arg1) {
		if (arg0 == null || arg1 == null) return 0;
		String a = arg0.toString();
		String b = arg1.toString();
		
//		String a = (String)arg0;
//		String b = (String)arg1;
		
		return a.compareTo(b);
	}

}
