package fi.kaila.suku.util;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

/**
 * @author Kalle
 * 
 *         This will become a textfield with some intellisens feature
 * 
 */
public class SukuTextField extends JTextField implements FocusListener,
		KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Enum for the field types that will be recognized
	 * 
	 */
	public enum Field {
		/**
		 * Field type givenname
		 */
		Fld_Givenname,
		/**
		 * Field type Patronyme
		 */
		Fld_Patronyme,

		/**
		 * Field type Surname
		 */
		Fld_Surname,
		/**
		 * Field type place
		 */
		Fld_Place,
		/**
		 * Field type country
		 */
		Fld_Country,

		/**
		 * Field type = type
		 */
		Fld_Type,

		/**
		 * Field type description
		 */
		Fld_Description,
		/**
		 * No field
		 */
		Fld_Null
	};

	SukuSenser senser = null;

	/**
	 * @param tag
	 * @param type
	 */
	public SukuTextField(String tag, Field type) {
		addFocusListener(this);
		addKeyListener(this);

		senser = SukuSenser.getInstance();
	}

	boolean hasFocus = false;

	@Override
	public void focusGained(FocusEvent arg0) {
		hasFocus = true;
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		hasFocus = false;
		senser.hide();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent k) {
		char c = k.getKeyChar();

		if (c == '\n') {
			senser.getSens(this);
			return;
		}
		senser.showSens(this, null, Field.Fld_Place);

	}

	@Override
	public void keyTyped(KeyEvent k) {

		// char c = k.getKeyChar();
		//
		// if (c == '\n') {
		// senser.getSens(this);
		// return;
		// }

		// int i = k.getKeyCode();
		// int m1 = k.getModifiers();
		// int m2 = k.getModifiersEx();
		//
		// int ii = c;
		// // if (k.getKeyChar() == '\n') {
		// System.out.println("K:" + ii + "/" + i + "/" + m1 + "/" + m2);
		// // System.out.println("k:" + k.toString());
		// // }
	}

}
