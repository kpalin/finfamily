package fi.kaila.suku.util;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * 
 * popupmenu for Suku Text area
 * 
 * @author Kaarle Kaila
 * 
 */
public class TextPopupMenu {

	private static TextPopupMenu me = null;

	private JPopupMenu pMenu = null;
	private JMenuItem pOpenHiskiPage;

	private JMenuItem pCopy;

	/**
	 * add all action listeners for menu commands
	 * 
	 * @param l
	 */
	private void addActionListener(ActionListener l) {
		pOpenHiskiPage.addActionListener(l);
		pCopy.addActionListener(l);

	}

	/**
	 * show menu at location
	 * 
	 * @param e
	 * @param x
	 * @param y
	 */
	public void show(MouseEvent e, int x, int y) {
		pMenu.show(e.getComponent(), x, y);
	}

	public void enableHiskiMenu(boolean value) {
		pOpenHiskiPage.setVisible(value);
	}

	public void enableCopyMenu(boolean value) {
		pCopy.setEnabled(value);
	}

	private TextPopupMenu() {
		// private so it can only be initiated from within

		pMenu = new JPopupMenu();

		pOpenHiskiPage = new JMenuItem(Resurses.getString("HISKI_OPEN"));
		// pShowPerson.addActionListener(popupListener);
		pOpenHiskiPage.setActionCommand("HISKI_OPEN");
		pMenu.add(pOpenHiskiPage);

		pMenu.addSeparator();
		pCopy = new JMenuItem(Resurses.getString(Resurses.MENU_COPY));
		// pShowFamily.addActionListener(popupListener);
		// pCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// Event.CTRL_MASK ));
		pCopy.setActionCommand(Resurses.MENU_COPY);
		pMenu.add(pCopy);

	}

	/**
	 * This is a singleton class.
	 * 
	 * @return the menu
	 */
	public static TextPopupMenu getInstance(ActionListener l) {
		if (me == null) {
			me = new TextPopupMenu();
			me.addActionListener(l);
		}
		return me;

	}

}
