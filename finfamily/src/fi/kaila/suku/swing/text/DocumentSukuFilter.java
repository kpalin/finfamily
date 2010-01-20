package fi.kaila.suku.swing.text;

import java.io.Serializable;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Aux class for java type text
 * 
 * @author Kalle
 * 
 */
public class DocumentSukuFilter extends DocumentFilter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean DEBUG = false;

	/**
	 * 
	 */
	public DocumentSukuFilter() {

	}

	public void insertString(FilterBypass fb, int offs, String str,
			AttributeSet a) throws BadLocationException {
		if (DEBUG) {
			System.out.println("in DocumentSizeFilter's insertString [" + offs
					+ "]:" + str);
		}

		// This rejects the entire insertion if it would make
		// the contents too long. Another option would be
		// to truncate the inserted string so the contents
		// would be exactly maxCharacters in length.
		// if ((fb.getDocument().getLength() + str.length()) <= maxCharacters)
		super.insertString(fb, offs, str, a);
		// else
		// Toolkit.getDefaultToolkit().beep();
	}

	public void replace(FilterBypass fb, int offs, int length, String str,
			AttributeSet a) throws BadLocationException {
		if (DEBUG) {
			System.out.println("in DocumentSizeFilter's replace [" + offs + ";"
					+ length + "]:" + str);
		}
		// This rejects the entire replacement if it would make
		// the contents too long. Another option would be
		// to truncate the replacement string so the contents
		// would be exactly maxCharacters in length.
		// if ((fb.getDocument().getLength() + str.length()
		// - length) <= maxCharacters)
		super.replace(fb, offs, length, str, a);
		// else
		// Toolkit.getDefaultToolkit().beep();
	}

}
