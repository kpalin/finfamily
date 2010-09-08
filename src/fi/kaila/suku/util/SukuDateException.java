package fi.kaila.suku.util;

/**
 * Date exception is used to check for incorrect date format.
 * 
 * @author Kalle
 */
public class SukuDateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mess = null;

	/**
	 * Instantiates a new suku date exception.
	 * 
	 * @param text
	 *            the text
	 */
	public SukuDateException(String text) {
		mess = text;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {

		return mess;

	}

}
