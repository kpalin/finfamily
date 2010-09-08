package fi.kaila.suku.webstart;

/**
 * The Class Suku.
 * 
 * @author kalle
 * 
 *         Suku program for webstart. This calls
 *         {@link fi.kaila.suku.swing.Suku} using parameter "web"
 */
public class Suku {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		String wargs[] = { "web" };
		fi.kaila.suku.swing.Suku.main(wargs);

	}

}
