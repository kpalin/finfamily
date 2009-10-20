package fi.kaila.suku.util;

import java.util.Comparator;

/**
 * comparator for numeric strings
 * 
 * @author Kalle
 * 
 */
@SuppressWarnings("unchecked")
public class SukuNumStringComparator implements Comparator {

	@Override
	public int compare(Object arg0, Object arg1) {
		int a, b;

		try {

			if (arg0 == null || arg0.equals("")) {
				a = 0;
			} else {
				a = Integer.parseInt((String) arg0);
				// a = ((Integer)arg0).intValue();
			}
			if (arg1 == null || arg1.equals("")) {
				b = 0;
			} else {
				b = Integer.parseInt((String) arg1);
				// b = ((Integer)arg1).intValue();
			}
			if (a < b)
				return -1;
			if (a > b)
				return 1;
			return 0;
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			return 0;
		}
	}

}
