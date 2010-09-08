package fi.kaila.suku.util;

/**
 * Exception container.
 * 
 * @author FIKAAKAIL 25.7.2007
 */
public class SukuException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new suku exception.
	 * 
	 * @param text
	 *            the text
	 */
	public SukuException(String text) {
		super(text);

	}

	/**
	 * Instantiates a new suku exception.
	 * 
	 * @param text
	 *            the text
	 * @param t
	 *            the t
	 */
	public SukuException(String text, Throwable t) {
		super(text, t);

	}

	/**
	 * Instantiates a new suku exception.
	 * 
	 * @param t
	 *            the t
	 */
	public SukuException(Throwable t) {
		super(t);

	}
}
