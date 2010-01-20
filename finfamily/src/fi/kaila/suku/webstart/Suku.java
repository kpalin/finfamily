package fi.kaila.suku.webstart;

/**
 * @author kalle
 * 
 *         Suku program for webstart. This calls
 *         {@link fi.kaila.suku.swing.Suku} using paramater "web"
 */
public class Suku {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String wargs[] = { "web" };
		fi.kaila.suku.swing.Suku.main(wargs);

	}

}
