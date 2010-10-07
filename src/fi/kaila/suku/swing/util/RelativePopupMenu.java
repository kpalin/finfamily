package fi.kaila.suku.swing.util;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PersonShortData;

/**
 * The popup menu for relatives pane.
 * 
 * @author Kalle
 */
public class RelativePopupMenu {

	private static RelativePopupMenu me = null;

	private JPopupMenu pMenu = null;

	private final JMenuItem pPerson;
	private final JMenuItem pRelatives;
	private final JMenuItem pShowPerson;
	private final JMenuItem pShowFamily;
	private final JMenuItem pShowReport;
	private final JMenuItem pDeletePerson;
	// private JMenuItem pPaste;
	// private JMenuItem pPasteBefore;
	// private JMenuItem pPasteAfter;
	private final JMenu pParent;
	private final JMenuItem[] pAddParent;

	private void addActionListener(ActionListener l) {

		pPerson.addActionListener(l);
		pRelatives.addActionListener(l);
		pShowFamily.addActionListener(l);
		pShowPerson.addActionListener(l);
		pShowReport.addActionListener(l);
		pDeletePerson.addActionListener(l);
		// pPaste.addActionListener(l);
		// pPasteBefore.addActionListener(l);
		// pPasteAfter.addActionListener(l);
		for (int i = 0; i < pAddParent.length; i++) {
			pAddParent[i].addActionListener(l);
		}
	}

	/**
	 * Gets the parent count.
	 * 
	 * @return no pf parents
	 */
	public int getParentCount() {
		if (pAddParent == null)
			return 0;
		return pAddParent.length;
	}

	/**
	 * Sets the child name.
	 * 
	 * @param child
	 *            name to other parent menu cmd
	 */
	public void setChildName(PersonShortData child) {
		pParent.setText(Resurses.getString("MENU_OTHER") + " "
				+ child.getAlfaName());
	}

	/**
	 * set parent at idx to show/not show.
	 * 
	 * @param idx
	 *            the idx
	 * @param value
	 *            the value
	 */
	public void showParent(int idx, boolean value) {
		pPerson.setVisible(true);
		if (idx == 0) {
			pParent.setVisible(value);
			pRelatives.setVisible(value);
		}
		pAddParent[idx].setVisible(value);
	}

	/**
	 * set parent name at idx.
	 * 
	 * @param idx
	 *            the idx
	 * @param person
	 *            the person
	 */
	public void setParentName(int idx, PersonShortData person) {
		pAddParent[idx].setText(person.getAlfaName());
	}

	/**
	 * show menu at location.
	 * 
	 * @param e
	 *            the e
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void show(MouseEvent e, int x, int y) {
		pMenu.show(e.getComponent(), x, y);
	}

	private RelativePopupMenu() {
		// so it can only be initited from within

		pMenu = new JPopupMenu();

		pPerson = new JMenuItem(Resurses.getString(Resurses.TAB_PERSON));
		// pShowFamily.addActionListener(popupListener);
		pPerson.setActionCommand(Resurses.TAB_PERSON);
		pMenu.add(pPerson);

		pRelatives = new JMenuItem(Resurses.getString(Resurses.TAB_RELATIVES));
		// pShowFamily.addActionListener(popupListener);
		pRelatives.setActionCommand(Resurses.TAB_RELATIVES);
		pMenu.add(pRelatives);
		// pPerson.setEnabled(false);

		pShowPerson = new JMenuItem(
				Resurses.getString(Resurses.TAB_PERSON_TEXT));
		// pShowPerson.addActionListener(popupListener);
		pShowPerson.setActionCommand(Resurses.TAB_PERSON_TEXT);
		pMenu.add(pShowPerson);

		pShowFamily = new JMenuItem(Resurses.getString(Resurses.TAB_FAMILY));
		// pShowFamily.addActionListener(popupListener);
		pShowFamily.setActionCommand(Resurses.TAB_FAMILY);
		pMenu.add(pShowFamily);
		pShowReport = new JMenuItem(Resurses.getString(Resurses.CREATE_REPORT));
		// pShowFamily.addActionListener(popupListener);
		pShowReport.setActionCommand(Resurses.CREATE_REPORT);
		pMenu.add(pShowReport);

		pDeletePerson = new JMenuItem(
				Resurses.getString(Resurses.CREATE_REPORT));
		// pShowFamily.addActionListener(popupListener);
		pDeletePerson.setActionCommand(Resurses.CREATE_REPORT);
		pMenu.add(pDeletePerson);

		pMenu.addSeparator();

		// pPasteBefore = new
		// JMenuItem(Resurses.getString(Resurses.MENU_PASTE));
		// pPasteBefore.setActionCommand(Resurses.MENU_PASTE_BEFORE);
		// pMenu.add(pPasteBefore);
		//
		// pPaste = new JMenuItem(Resurses.getString(Resurses.MENU_PASTE));
		// pPaste.setActionCommand(Resurses.MENU_PASTE);
		// pMenu.add(pPaste);
		//
		// pPasteAfter = new JMenuItem(Resurses.getString(Resurses.MENU_PASTE));
		// pPasteAfter.setActionCommand(Resurses.MENU_PASTE_AFTER);
		// pMenu.add(pPasteAfter);

		pParent = new JMenu(Resurses.getString("MENU_OTHER"));
		pMenu.add(pParent);
		pParent.setVisible(false);
		pAddParent = new JMenuItem[30];
		for (int i = 0; i < pAddParent.length; i++) {
			pAddParent[i] = new JMenuItem("LISÄÄ" + i);
			pAddParent[i].setActionCommand("PARE " + i);

			pParent.add(pAddParent[i]);
		}

	}

	/** The current person. */
	PersonShortData currentPerson = null;

	/**
	 * set person name to menu commands.
	 * 
	 * @param person
	 *            the person
	 * @param text
	 *            the text
	 */
	public void setPerson(PersonShortData person, String text) {
		this.currentPerson = person;

		// pPaste.setVisible(person != null);
		if (person == null) {

			return;
		}

		// if (text != null) {
		// pPaste.setText(Resurses.getString(Resurses.MENU_PASTE) + " "
		// + person.getAlfaName() + " " + text);
		// pPasteAfter.setText(Resurses.getString(Resurses.MENU_PASTE) + " "
		// + text + " [" + person.getAlfaName() + "] "
		// + Resurses.getString(Resurses.MENU_PASTE_AFTER));
		// pPasteBefore.setText(Resurses.getString(Resurses.MENU_PASTE) + " "
		// + text + " [" + person.getAlfaName() + "] "
		// + Resurses.getString(Resurses.MENU_PASTE_BEFORE));
		// }
	}

	/**
	 * Gets the person.
	 * 
	 * @return the current person
	 */
	public PersonShortData getPerson() {
		return currentPerson;
	}

	/**
	 * This class is a semi-singleton.
	 * 
	 * @param l
	 *            the l
	 * @return the menu
	 */
	public static synchronized RelativePopupMenu getInstance(ActionListener l) {

		if (me == null) {

			me = new RelativePopupMenu();
			me.addActionListener(l);
		}
		return me;

	}

	/**
	 * used to setup the menu to paste person copy/paste probably not so much
	 * used after drag-and-drop was introduced.
	 * 
	 * @param pasteAtRow
	 *            the new paste at row
	 */
	public void setPasteAtRow(int pasteAtRow) {

		pPerson.setVisible(pasteAtRow >= 0);
		pRelatives.setVisible(pasteAtRow >= 0);
		String name = "";
		if (mousePerson != null) {
			name = mousePerson.getAlfaName();
		}
		pPerson.setText(Resurses.getString("" + name));
		// if (currentPerson == null) {
		// pPaste.setVisible(false);
		// pPasteAfter.setVisible(false);
		// pPasteBefore.setVisible(false);
		// return;
		// }
		//
		// if (pasteAtRow < 0) {
		// pPaste.setVisible(true);
		// pPasteAfter.setVisible(false);
		// pPasteBefore.setVisible(false);
		// } else {
		//
		// pPaste.setVisible(true);
		// pPasteAfter.setVisible(true);
		// pPasteBefore.setVisible(true);
		// }

	}

	private PersonShortData mousePerson = null;

	/**
	 * Sets the mouse person.
	 * 
	 * @param mousePerson
	 *            i.e. the person that paste should show
	 */
	public void setMousePerson(PersonShortData mousePerson) {
		this.mousePerson = mousePerson;
	}

	/**
	 * Gets the mouse person.
	 * 
	 * @return mouse person i.e. the person that paste should show
	 */
	public PersonShortData getMousePerson() {
		return mousePerson;
	}

}
