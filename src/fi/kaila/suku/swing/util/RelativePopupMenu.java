package fi.kaila.suku.swing.util;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PersonShortData;

public class RelativePopupMenu {

	private static RelativePopupMenu me = null;

	private JPopupMenu pMenu = null;

	private JMenuItem pPerson;
	private JMenuItem pShowPerson;
	private JMenuItem pShowFamily;
	private JMenuItem pPaste;
	private JMenuItem pPasteBefore;
	private JMenuItem pPasteAfter;
	private JMenu pParent;
	private JMenuItem[] pAddParent;

	private void addActionListener(ActionListener l) {

		pPerson.addActionListener(l);
		pShowFamily.addActionListener(l);
		pShowPerson.addActionListener(l);
		pPaste.addActionListener(l);
		pPasteBefore.addActionListener(l);
		pPasteAfter.addActionListener(l);
		for (int i = 0; i < pAddParent.length; i++) {
			pAddParent[i].addActionListener(l);
		}
	}

	public int getParentCount() {
		if (pAddParent == null)
			return 0;
		return pAddParent.length;
	}

	public void setChildName(PersonShortData child) {
		pParent.setText(Resurses.getString("MENU_OTHER") + " "
				+ child.getAlfaName());
	}

	public void showParent(int idx, boolean value) {
		pPerson.setVisible(true);
		if (idx == 0) {
			pParent.setVisible(value);
		}
		pAddParent[idx].setVisible(value);
	}

	public void setParentName(int idx, PersonShortData person) {
		pAddParent[idx].setText(person.getAlfaName());
	}

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
		// pPerson.setEnabled(false);

		pShowPerson = new JMenuItem(Resurses
				.getString(Resurses.TAB_PERSON_TEXT));
		// pShowPerson.addActionListener(popupListener);
		pShowPerson.setActionCommand(Resurses.TAB_PERSON_TEXT);
		pMenu.add(pShowPerson);

		pShowFamily = new JMenuItem(Resurses.getString(Resurses.TAB_FAMILY));
		// pShowFamily.addActionListener(popupListener);
		pShowFamily.setActionCommand(Resurses.TAB_FAMILY);
		pMenu.add(pShowFamily);

		pPasteBefore = new JMenuItem(Resurses.getString(Resurses.MENU_PASTE));
		pPasteBefore.setActionCommand(Resurses.MENU_PASTE_BEFORE);
		pMenu.add(pPasteBefore);

		pPaste = new JMenuItem(Resurses.getString(Resurses.MENU_PASTE));
		pPaste.setActionCommand(Resurses.MENU_PASTE);
		pMenu.add(pPaste);

		pPasteAfter = new JMenuItem(Resurses.getString(Resurses.MENU_PASTE));
		pPasteAfter.setActionCommand(Resurses.MENU_PASTE_AFTER);
		pMenu.add(pPasteAfter);

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

	PersonShortData currentPerson = null;

	public void setPerson(PersonShortData person, String text) {
		this.currentPerson = person;

		// pPaste.setVisible(person != null);
		if (person == null) {

			return;
		}

		if (text != null) {
			pPaste.setText(Resurses.getString(Resurses.MENU_PASTE) + " "
					+ person.getAlfaName() + " " + text);
			pPasteAfter.setText(Resurses.getString(Resurses.MENU_PASTE) + " "
					+ text + " [" + person.getAlfaName() + "] "
					+ Resurses.getString(Resurses.MENU_PASTE_AFTER));
			pPasteBefore.setText(Resurses.getString(Resurses.MENU_PASTE) + " "
					+ text + " [" + person.getAlfaName() + "] "
					+ Resurses.getString(Resurses.MENU_PASTE_BEFORE));
		}
	}

	public PersonShortData getPerson() {
		return currentPerson;
	}

	/**
	 * This class is a semi-singleton.
	 * 
	 * @return the menu
	 */
	public static RelativePopupMenu getInstance(ActionListener l) {
		// FIXME: This method contains an unsynchronized lazy initialization of
		// a static field. After the field is set, the object stored into that
		// location is further updated or accessed. The setting of the field is
		// visible to other threads as soon as it is set. If the futher accesses
		// in the method that set the field serve to initialize the object, then
		// you have a very serious multithreading bug, unless something else
		// prevents any other thread from accessing the stored object until it
		// is fully initialized.

		// Even if you feel confident that the method is never called by
		// multiple threads, it might be better to not set the static field
		// until the value you are setting it to is fully populated/initialized.

		if (me == null) {
			me = new RelativePopupMenu();
			me.addActionListener(l);
		}
		return me;

	}

	public void setPasteAtRow(int pasteAtRow) {

		pPerson.setVisible(pasteAtRow >= 0);
		String name = "";
		if (mousePerson != null) {
			name = mousePerson.getAlfaName();
		}
		pPerson.setText(Resurses.getString(Resurses.TAB_PERSON) + " " + name);

		if (currentPerson == null) {
			pPaste.setVisible(false);
			pPasteAfter.setVisible(false);
			pPasteBefore.setVisible(false);
			return;
		}

		if (pasteAtRow < 0) {
			pPaste.setVisible(true);
			pPasteAfter.setVisible(false);
			pPasteBefore.setVisible(false);
		} else {

			pPaste.setVisible(true);
			pPasteAfter.setVisible(true);
			pPasteBefore.setVisible(true);
		}

	}

	private PersonShortData mousePerson = null;

	public void setMousePerson(PersonShortData mousePerson) {
		this.mousePerson = mousePerson;
	}

	public PersonShortData getMousePerson() {
		return mousePerson;
	}

}
