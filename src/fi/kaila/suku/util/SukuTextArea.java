package fi.kaila.suku.util;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextArea;

/**
 * 
 * SukuTextArea extends the JTextArea. It adds context a pop up menu to the text
 * area
 * 
 * @author Kaarle Kaila
 * 
 */
public class SukuTextArea extends JTextArea implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	PopupListener popupListener = null;

	private SukuTextArea me = null;

	public SukuTextArea() {
		me = this;
		popupListener = new PopupListener();
		TextPopupMenu pop = TextPopupMenu.getInstance(this);
		this.addMouseListener(popupListener);
	}

	class PopupListener implements MouseListener, ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);

		}

		private void maybeShowPopup(MouseEvent e) {

			String selected = me.getSelectedText();
			if (selected == null || selected.length() == 0) {
				return;
			}

			try {
				hiskiNumero = Long.parseLong(selected);
			} catch (NumberFormatException ne) {
				hiskiNumero = 0;
			}

			int alk = me.getSelectionStart();
			int lop = me.getSelectionEnd();
			boolean maybeHiski = false;
			String aux = me.getText();
			if (hiskiNumero > 0 && alk > 0 && lop > 0 && alk < lop
					&& lop < aux.length() && lop == alk + selected.length()) {

				char aca = aux.charAt(alk - 1);
				char lca = aux.charAt(lop);
				if (aca == '[' && lca == ']') {
					int hiskiIdx = aux.indexOf("Hiski");
					if (hiskiIdx >= 0 && hiskiIdx < alk) {
						maybeHiski = true;
					}
				}
			}

			if (e.isPopupTrigger()) {

				Point clickPoint = e.getPoint();

				TextPopupMenu pop = TextPopupMenu.getInstance(me);
				pop.enableHiskiMenu(maybeHiski);
				pop.enableCopyMenu(selected.length() > 0);
				pop.show(e, e.getX(), e.getY());
			}
		}

	}

	private long hiskiNumero = 0;

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;

		if (cmd.equals("HISKI_OPEN")) {
			try {
				String[] cmds = { "rundll32", "url.dll,FileProtocolHandler", "" };
				cmds[2] = "http://hiski.genealogia.fi/hiski?fi+t" + hiskiNumero;
				Process p = Runtime.getRuntime().exec(cmds);
				p.waitFor();

				//
				// this should work on mac
				//
				// String [] macs = {"open",report);
				// macs[1] = report;
				// Process p = Runtime.getRuntime().exec(macs);

			} catch (Throwable t) {
				logger.log(Level.INFO, "rundll32", t);

			}

		} else if (cmd.equals(Resurses.MENU_COPY)) {
			StringSelection stringSelection = new StringSelection(me
					.getSelectedText());
			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}

	}

}
