package fi.kaila.suku.util;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * The Class SukuTextField.
 * 
 * @author Kalle
 * 
 *         This will become a textfield with some intellisens feature
 */
public class SukuTextField extends JTextField implements FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Enum for the field types that will be recognized.
	 */
	public enum Field {

		/** Field type givenname. */
		Fld_Givenname,

		/** Field type Patronyme. */
		Fld_Patronyme,

		/** Field type Surname. */
		Fld_Surname,

		/** Field type place. */
		Fld_Place,

		/** Field type country. */
		Fld_Country,

		/** Field type = type. */
		Fld_Type,

		/** Field type description. */
		Fld_Description,

		/** Field group. */
		Fld_Group,

		/** No field. */
		Fld_Null
	}

	/** The senser. */
	SukuSenser senser = null;
	private String tag = null;
	private Field type = Field.Fld_Null;

	/**
	 * Instantiates a new suku text field.
	 * 
	 * @param tag
	 *            the tag
	 * @param type
	 *            the type
	 */
	public SukuTextField(String tag, Field type) {
		addFocusListener(this);
		// addKeyListener(this);
		this.tag = tag;
		this.type = type;
		senser = SukuSenser.getInstance();

	}

	/** The has focus. */
	boolean hasFocus = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#processKeyEvent(java.awt.event.KeyEvent)
	 */
	@Override
	protected void processKeyEvent(KeyEvent e) {

		int cmd = e.getKeyCode();
		if (cmd == 40 || cmd == 38 || cmd == 10) {

			if (e.getID() == KeyEvent.KEY_RELEASED) {
				senser.selectList(cmd);
			}
			if (cmd != 10) {
				return;
			}

		}
		if (cmd == 10) {
			if (senser.isVisible()) {
				senser.hide();
				return;
			}
		}
		super.processKeyEvent(e);
		if (e.getID() == KeyEvent.KEY_RELEASED && cmd != 10) {
			senser.showSens(this, tag, type);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent arg0) {
		hasFocus = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent arg0) {
		hasFocus = false;
		senser.hide();
	}

}
