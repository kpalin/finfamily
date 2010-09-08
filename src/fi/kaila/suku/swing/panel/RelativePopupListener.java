/**
 * 
 */
package fi.kaila.suku.swing.panel;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;

class RelativePopupListener extends MouseAdapter implements ActionListener {

	/**
		 * 
		 */
	private RelativesPane relativesPane;

	/**
	 * @param relativesPane
	 */
	RelativePopupListener(RelativesPane relativesPane) {
		this.relativesPane = relativesPane;
	}

	static Logger logger = Logger.getLogger(RelativePopupListener.class
			.getName());

	private JTable showTable = null;

	private PersonShortData showNewPerson = null;
	private int pasteAtRow = -1;
	private static Relation showRela = null;

	@Override
	public void actionPerformed(ActionEvent a) {

		String cmd = a.getActionCommand();
		if (cmd == null)
			return;
		try {
			if (cmd.equals(Resurses.TAB_PERSON)) {
				doTabPerson(false);
			}
			if (cmd.equals(Resurses.TAB_RELATIVES)) {
				doTabPerson(true);
			}
			if (cmd.equals(Resurses.TAB_PERSON_TEXT)) {
				relativesPane.personView.setTextForPerson(relativesPane.pop
						.getMousePerson());
			}
			if (cmd.equals(Resurses.TAB_FAMILY)) {
				PersonShortData pp = relativesPane.pop.getMousePerson();
				relativesPane.personView.setSubjectForFamily(pp == null ? 0
						: pp.getPid());
			}
			if (cmd.equals(Resurses.CREATE_REPORT)) {
				relativesPane.personView.getSuku().createReport(
						relativesPane.pop.getMousePerson());

			}
			if (showTable == null) {
				return;
			}
			// if (cmd.startsWith(Resurses.MENU_PASTE)) {
			// doPaste(cmd);
			// }

			if (cmd.startsWith("PARE ")) {

				doPare(cmd);

			}
		} catch (SukuException e) {
			logger.log(Level.WARNING, "Opening person "
					+ relativesPane.pop.getMousePerson().getAlfaName(), e);
			JOptionPane.showMessageDialog(this.relativesPane.personView, e
					.getMessage(), Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void doPare(String cmd) throws SukuException {
		if (showRela != null) {
			int pareNo = -1;
			try {
				pareNo = Integer.parseInt(cmd.substring(5));
			} catch (NumberFormatException ne) {
				return;
			}

			PersonShortData mother = this.relativesPane.spouses.list
					.get(pareNo).getShortPerson();

			PersonShortData child = showRela.getShortPerson();
			child.setParentPid(mother.getPid());

			//
			// add Relations for update purposes
			//

			String tag = "MOTH";
			if (!this.relativesPane.longPers.getSex().equals("M")) {
				tag = "FATH";
			}
			Relation rpare = new Relation(0, child.getPid(), mother.getPid(),
					tag, 100, null, null);

			this.relativesPane.checkLocalRelation(child, rpare, mother);

			this.relativesPane.otherRelations.add(rpare);
			showTable.updateUI();
		}
	}

	private void doTabPerson(boolean showRelatives) throws SukuException {
		if (this.relativesPane.pop.getMousePerson() != null) {
			this.relativesPane.personView.closePersonPane(true);
			this.relativesPane.personView.displayPersonPane(relativesPane.pop
					.getMousePerson().getPid());
			if (showRelatives) {
				int midx = this.relativesPane.personView.getMainPaneIndex();
				if (midx >= 2) {
					this.relativesPane.personView.setSelectedIndex(midx + 1);
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		// maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		showTable = null;
		showRela = null;
		this.relativesPane.pop.setMousePerson(null);
		if (e.getButton() != 3)
			return;

		String asRelative = null;
		try {
			showNewPerson = (PersonShortData) Suku.sukuObject;

		} catch (ClassCastException cce) {
			showNewPerson = null;
		}

		if (showNewPerson != null) {
			if (this.relativesPane.personView.getSuku().getPerson(
					showNewPerson.getPid()) != null) {
				showNewPerson = this.relativesPane.personView.getSuku()
						.getPerson(showNewPerson.getPid());
			}
		}

		// popupListener = new PopupListener();

		if (e.getSource() == this.relativesPane.chilTab) {
			showTable = this.relativesPane.chilTab;

			asRelative = Resurses.getString("MENU_PASTE_ASCHILD");
			Point clickPoint = e.getPoint();
			pasteAtRow = showTable.rowAtPoint(clickPoint);

			if (pasteAtRow >= 0) {
				showRela = this.relativesPane.children.list.get(pasteAtRow);
			}
			if (showRela != null) {
				this.relativesPane.pop
						.setMousePerson(showRela.getShortPerson());
				int ppid = showRela.getShortPerson().getParentPid();
				if (ppid == 0) {
					this.relativesPane.pop.setChildName(showRela
							.getShortPerson());
					for (int i = 0; i < this.relativesPane.spouses.list.size(); i++) {
						this.relativesPane.pop.setParentName(i,
								this.relativesPane.spouses.list.get(i)
										.getShortPerson());
					}
				}
				for (int i = 0; i < this.relativesPane.pop.getParentCount(); i++) {
					this.relativesPane.pop.showParent(i, ppid == 0
							&& i < this.relativesPane.spouses.list.size());
				}
			}

		} else if (e.getSource() == this.relativesPane.pareTab) {
			pasteAtRow = -1;
			showTable = this.relativesPane.pareTab;

			Point clickPoint = e.getPoint();
			int rowAtPoint = showTable.rowAtPoint(clickPoint);
			if (rowAtPoint >= 0) {

				pasteAtRow = showTable.rowAtPoint(e.getPoint());
				showRela = this.relativesPane.parents.list.get(rowAtPoint);
				this.relativesPane.pop
						.setMousePerson(showRela.getShortPerson());
			} else {
				showRela = null;
			}
			asRelative = Resurses.getString("MENU_PASTE_ASPARENT");

			for (int i = 0; i < this.relativesPane.pop.getParentCount(); i++) {
				this.relativesPane.pop.showParent(i, false);
			}
		} else if (e.getSource() == this.relativesPane.spouTab) {
			pasteAtRow = -1;

			showTable = this.relativesPane.spouTab;
			if (showNewPerson != null) {
				if (showNewPerson.getSex().equals("M")) {
					asRelative = Resurses.getString("MENU_PASTE_ASHUSB");
				} else {
					asRelative = Resurses.getString("MENU_PASTE_ASWIFE");
				}
			}
			Point clickPoint = e.getPoint();
			pasteAtRow = showTable.rowAtPoint(clickPoint);
			if (pasteAtRow >= 0) {

				showRela = this.relativesPane.spouses.list.get(pasteAtRow);
				this.relativesPane.pop
						.setMousePerson(showRela.getShortPerson());
			} else {
				showRela = null;
			}

			for (int i = 0; i < this.relativesPane.pop.getParentCount(); i++) {
				this.relativesPane.pop.showParent(i, false);
			}
		} else if (e.getSource() == this.relativesPane.subject) {

			showNewPerson = null; // 
			asRelative = Resurses.getString("MENU_PASTE_ASSUBJ");
			this.relativesPane.pop.setMousePerson(new PersonShortData(
					relativesPane.longPers));
			this.relativesPane.pop.setPasteAtRow(0);
			this.relativesPane.pop.setPerson(showNewPerson, asRelative);
			this.relativesPane.pop.showParent(0, false);
			this.relativesPane.pop.setPasteAtRow(0);
			this.relativesPane.pop.show(e, e.getX(), e.getY());
			return;
		}
		if (showTable == null)
			return;
		if (e.isPopupTrigger()) {

			this.relativesPane.pop.setPerson(showNewPerson, asRelative);

			this.relativesPane.pop.setPasteAtRow(pasteAtRow);
			this.relativesPane.pop.show(e, e.getX(), e.getY());
		}
	}
}