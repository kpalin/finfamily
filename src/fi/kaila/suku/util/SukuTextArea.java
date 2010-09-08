package fi.kaila.suku.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextArea;

/**
 * 
 * SukuTextArea extends the JTextArea. It adds context a pop up menu to the text
 * area
 * 
 * @author Kaarle Kaila
 * 
 */
public class SukuTextArea extends JTextArea implements ActionListener,
		ClipboardOwner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private Logger logger = Logger.getLogger(this.getClass().getName());
	/** The popup listener. */
	PopupListener popupListener = null;

	private SukuTextArea me = null;

	/**
	 * Textarea with context sensitive menu.
	 */
	public SukuTextArea() {
		me = this;
		popupListener = new PopupListener();
		// TextPopupMenu pop = TextPopupMenu.getInstance(this);
		this.addMouseListener(popupListener);
	}

	/**
	 * The listener interface for receiving popup events. The class that is
	 * interested in processing a popup event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addPopupListener<code> method. When
	 * the popup event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see PopupEvent
	 */
	class PopupListener implements MouseListener, ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			//
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			//
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			//

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			//

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);

		}

		private void maybeShowPopup(MouseEvent e) {

			String aux = me.getText();
			int hiskiIdx = aux.indexOf("Hiski");
			hiskiNumero = 0;
			boolean maybeHiski = false;
			String selected = me.getSelectedText();
			if (selected == null) {
				selected = "";
			}
			if (selected.length() > 0) {

				try {
					hiskiNumero = Long.parseLong(selected);
				} catch (NumberFormatException ne) {
					// NumberFormatException ignored
				}

				int alk = me.getSelectionStart();
				int lop = me.getSelectionEnd();

				if (hiskiNumero > 0 && alk > 0 && lop > 0 && alk < lop
						&& lop < aux.length() && lop == alk + selected.length()) {

					char aca = aux.charAt(alk - 1);
					char lca = aux.charAt(lop);
					if (aca == '[' && lca == ']') {
						if (hiskiIdx >= 0 && hiskiIdx < alk) {
							maybeHiski = true;
						}
					}
				}
			}

			if (!maybeHiski && hiskiIdx >= 0) {
				int icb = aux.indexOf('[', hiskiIdx);
				int ice = aux.indexOf(']', hiskiIdx);
				if (icb > hiskiIdx && ice > icb + 1) {
					try {
						hiskiNumero = Long.parseLong(aux
								.substring(icb + 1, ice));
						maybeHiski = true;
					} catch (NumberFormatException ne) {
						// NumberFormatException ignored
					}
				}

			}

			if (e.isPopupTrigger()) {

				// Point clickPoint = e.getPoint();

				TextPopupMenu pop = TextPopupMenu.getInstance(me);
				pop.enableHiskiMenu(maybeHiski);
				pop.enableCopyMenu(selected.length() > 0);
				pop.show(e, e.getX(), e.getY());
			}
		}

	}

	private static long hiskiNumero = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;

		if (cmd.equals("HISKI_OPEN")) {
			Utils.openExternalFile("http://hiski.genealogia.fi/hiski?fi+t"
					+ hiskiNumero);

		} else if (cmd.equals(Resurses.MENU_COPY)) {
			StringSelection stringSelection = new StringSelection(
					me.getSelectedText());
			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			clipboard.setContents(stringSelection, this);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer
	 * .Clipboard, java.awt.datatransfer.Transferable)
	 */
	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// do nothing

	}

}
