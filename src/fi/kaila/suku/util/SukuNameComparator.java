package fi.kaila.suku.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * The Class SukuNameComparator.
 * 
 * @author FIKAAKAIL
 * 
 *         Comparator for names
 */
public class SukuNameComparator implements Comparator {

	/** The colli. */
	Collator colli;

	/**
	 * Constructor with locale.
	 * 
	 * @param langu
	 *            the langu
	 */
	public SukuNameComparator(String langu) {

		Locale ll = new Locale(langu);
		this.colli = Collator.getInstance(ll);

		adels = Resurses.getString("NAME_VON").split(";");
	}

	private String adels[] = null;

	private String noAdel(String nime) {
		int ll;
		for (int i = 0; i < adels.length; i++) {
			ll = adels[i].length();
			if (nime.length() > ll) {
				if (adels[i].equalsIgnoreCase(nime.substring(0, ll))) {
					return nime.substring(ll + 1);
				}
			}
		}
		return nime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		String uno = (String) arg0;
		String duo = (String) arg1;
		if (uno == null || duo == null)
			return 0;

		String nuno = noAdel(uno.trim()).replace(' ', '!');
		String nduo = noAdel(duo.trim()).replace(' ', '!');

		return this.colli.compare(nuno, nduo);

	}

}
