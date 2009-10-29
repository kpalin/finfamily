package fi.kaila.suku.swing.util;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PersonShortData;

/**
 * 
 * popupmenu for database view
 * 
 * @author Kalle
 * 
 */
public class SukuPopupMenu {

	private static SukuPopupMenu me = null;

	private JPopupMenu pMenu = null;
	private JMenuItem pShowPerson;
	private JMenuItem pShowFamily;
	private JMenuItem pMainPerson;
	private JMenuItem pPersonView;
	private JMenuItem pCopy;
	private JMenuItem pPaste;
	private JMenu pHiskiConnect = null;
	private JMenuItem[] pHiskiPerson = null;

	private JMenuItem pReport = null;
	private PersonShortData currentPerson = null;

	/**
	 * enables the hiskpperson menu
	 * 
	 * @param idx
	 * @param b
	 */
	public void enableHiskiPerson(int idx, boolean b) {
		if (idx >= 0 && idx < pHiskiPerson.length) {
			pHiskiPerson[idx].setVisible(b);
		}
	}

	/**
	 * add all actionlisteners for menu commands
	 * 
	 * @param l
	 */
	public void addActionListener(ActionListener l) {
		pShowPerson.addActionListener(l);
		pMainPerson.addActionListener(l);
		pShowFamily.addActionListener(l);
		pPersonView.addActionListener(l);
		pCopy.addActionListener(l);
		pPaste.addActionListener(l);
		pReport.addActionListener(l);
		for (int i = 0; i < 30; i++) {
			pHiskiPerson[i].addActionListener(l);
		}
	}

	/**
	 * 
	 * sets person on whom menu is shown
	 * 
	 * @param person
	 */
	public void setPerson(PersonShortData person) {
		this.currentPerson = person;
		// pShowPerson.setText(person.getAlfaName());
		pMainPerson.setText(person.getAlfaName());
	}

	/**
	 * @return the person for the meny
	 */
	public PersonShortData getPerson() {
		return currentPerson;
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

	private SukuPopupMenu() {
		// so it can only be initited from within

		pMenu = new JPopupMenu();

		pMainPerson = new JMenuItem("A popup menu item");
		// pShowPerson.addActionListener(popupListener);
		pMainPerson.setActionCommand(Resurses.TAB_PERSON);
		pMenu.add(pMainPerson);
		pMenu.addSeparator();
		pPersonView = new JMenuItem(Resurses.getString(Resurses.TAB_PERSON));
		// pShowPerson.addActionListener(popupListener);
		pPersonView.setActionCommand(Resurses.TAB_PERSON);
		pMenu.add(pPersonView);

		pShowPerson = new JMenuItem(Resurses
				.getString(Resurses.TAB_PERSON_TEXT));
		// pShowPerson.addActionListener(popupListener);
		pShowPerson.setActionCommand(Resurses.TAB_PERSON_TEXT);
		pMenu.add(pShowPerson);

		pShowFamily = new JMenuItem(Resurses.getString(Resurses.TAB_FAMILY));
		// pShowFamily.addActionListener(popupListener);
		pShowFamily.setActionCommand(Resurses.TAB_FAMILY);
		pMenu.add(pShowFamily);

		pMenu.addSeparator();
		pCopy = new JMenuItem(Resurses.getString(Resurses.MENU_COPY));
		// pShowFamily.addActionListener(popupListener);
		// pCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// Event.CTRL_MASK ));
		pCopy.setActionCommand(Resurses.MENU_COPY);
		pMenu.add(pCopy);

		pPaste = new JMenuItem(Resurses.getString(Resurses.MENU_PASTE));
		// pShowFamily.addActionListener(popupListener);
		pPaste.setActionCommand(Resurses.MENU_PASTE);
		pPaste.setEnabled(false);
		pMenu.add(pPaste);
		pMenu.addSeparator();
		pHiskiPerson = new JMenuItem[30];
		pHiskiConnect = new JMenu(Resurses.getString("HISKI_CONNECT"));
		pMenu.add(pHiskiConnect);
		for (int i = 0; i < 30; i++) {
			pHiskiPerson[i] = new JMenuItem(Resurses.getString("HISKI_PERSON")
					+ " " + i);
			pHiskiPerson[i].setActionCommand("HISKI" + i);
			pHiskiConnect.add(pHiskiPerson[i]);
			pHiskiPerson[i].setVisible(false);
		}

		pReport = new JMenuItem(Resurses.getString(Resurses.CREATE_REPORT));
		// pReport.addActionListener(popupListener);
		pReport.setActionCommand(Resurses.CREATE_REPORT);
		pMenu.add(pReport);

	}

	/**
	 * This class is a semi-singleton.
	 * 
	 * @return the menu
	 */
	public static SukuPopupMenu getInstance() {
		if (me == null) {
			me = new SukuPopupMenu();
		}
		return me;

	}

}
