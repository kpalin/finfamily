package fi.kaila.suku.swing;

import javax.swing.JFrame;

/**
 * 
 * @author Kaarle Kaila
 * 
 *         The interface allows the called frame to call back at the caller when
 *         it closes
 * 
 */
public interface ISuku {

	void SukuFormClosing(JFrame me);

	void AdminFormClosing(JFrame me);

	void HiskiFormClosing();

	void GroupWindowClosing();
}
