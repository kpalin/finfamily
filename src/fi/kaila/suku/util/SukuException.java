package fi.kaila.suku.util;

/**
 * Exception container
 * 
 * @author FIKAAKAIL 25.7.2007
 * 
 */
public class SukuException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param text
	 */
	public SukuException(String text) {
		super(text);

	}

	/**
	 * @param text
	 * @param t
	 */
	public SukuException(String text, Throwable t) {
		super(text, t);

	}

	/**
	 * @param t
	 */
	public SukuException(Throwable t) {
		super(t);

	}
}
