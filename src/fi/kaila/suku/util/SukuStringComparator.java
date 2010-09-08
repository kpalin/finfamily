package fi.kaila.suku.util;

import java.util.Comparator;

/**
 * Standard String comparator for database table.
 * 
 * @author FIKAAKAIL
 */
@SuppressWarnings("unchecked")
public class SukuStringComparator implements Comparator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		if (arg0 == null || arg1 == null)
			return 0;
		String a = arg0.toString();
		String b = arg1.toString();

		// String a = (String)arg0;
		// String b = (String)arg1;

		return a.compareTo(b);
	}

}
