package fi.kaila.suku.swing;

import javax.swing.JFrame;

/**
 * The Interface ISuku.
 * 
 * @author Kaarle Kaila
 * 
 *         The interface allows the called frame to call back at the caller when
 *         it closes
 */
public interface ISuku {

	/**
	 * used by map form.
	 * 
	 * @param me
	 *            the me
	 */
	void SukuFormClosing(JFrame me);

	/**
	 * used by database admin frame.
	 * 
	 * @param me
	 *            the me
	 */
	void AdminFormClosing(JFrame me);

	/**
	 * used by hiski form.
	 */
	void HiskiFormClosing();

	/**
	 * used by group window form.
	 */
	void GroupWindowClosing();
}
