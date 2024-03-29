package fi.kaila.suku.util;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import fi.kaila.suku.util.SukuTextField.Field;

/**
 * The Class SukuSenser.
 * 
 * @author Kalle A singleton class that displays an intellisens window below a
 *         SukuTextField
 */
public class SukuSenser implements MouseListener {

	private final JWindow sens;
	private final JList lista;
	private final Vector<String> model;

	private SukuSenser() {
		sens = new JWindow();
		model = new Vector<String>();

		lista = new JList(model);
		JScrollPane scroller = new JScrollPane(lista);

		// lista.addListSelectionListener(this);
		lista.addMouseListener(this);
		sens.add(scroller, BorderLayout.CENTER);
	}

	private static SukuSenser single = null;

	// private String tag = null;
	// private SukuTextField.Field fld = Field.Fld_Null;
	// private SukuTextField parent;
	private String[] paikat = { "Helsinki", "Espoo", "Tampere", "Porvoo",
			"Bromarf", "Hangö", "Tyrvää", "Tammela", "Peuramaa", "Borgå",
			"Heinola", "Hämeenlinna" };

	private String[] givennames = null;
	private String[] patronymes = null;
	private String[] surnames = null;
	private String[] descriptions = null;
	private String[] noticeTypes = null;
	private String[] groups = null;

	/**
	 * Initialize the places.
	 * 
	 * @param places
	 *            the new places
	 */
	public void setPlaces(String[] places) {
		this.paikat = places;
	}

	/**
	 * Initialize the givennames.
	 * 
	 * @param givennames
	 *            the new givennames
	 */
	public void setGivennames(String[] givennames) {
		this.givennames = givennames;
	}

	/**
	 * Initialize the patronymes.
	 * 
	 * @param patronymes
	 *            the new patronymes
	 */
	public void setPatronymes(String[] patronymes) {
		this.patronymes = patronymes;
	}

	/**
	 * Initialize the surnames.
	 * 
	 * @param surnames
	 *            the new surnames
	 */
	public void setSurnames(String[] surnames) {
		this.surnames = surnames;
	}

	/**
	 * Initialize the groups.
	 * 
	 * @param groups
	 *            the new groups
	 */
	public void setGroups(String[] groups) {
		this.groups = groups;
	}

	/**
	 * initialize the descriptions.
	 * 
	 * @param descriptions
	 *            the new descriptions
	 */
	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	/**
	 * initialize the noticetypes.
	 * 
	 * @param noticeTypes
	 *            the new notice types
	 */
	public void setNoticeTypes(String[] noticeTypes) {
		this.noticeTypes = noticeTypes;
	}

	/**
	 * Gets the single instance of SukuSenser.
	 * 
	 * @return the handle
	 */
	public static SukuSenser getInstance() {
		if (single == null) {
			single = new SukuSenser();
		}

		single.sens.setVisible(false);

		return single;
	}

	private SukuTextField parent;

	/**
	 * Show intellisens.
	 * 
	 * @param parent
	 *            the parent
	 * @param tag
	 *            the tag
	 * @param fld
	 *            the fld
	 */
	public void showSens(SukuTextField parent, String tag, Field fld) {
		Rectangle rt = parent.getBounds();
		Point pt = new Point(rt.x, rt.y);
		SwingUtilities.convertPointToScreen(pt, parent.getParent());
		Rectangle rs = new Rectangle();
		rs.x = pt.x;
		rs.y = pt.y + rt.height;
		rs.width = rt.width;
		rs.height = 100;
		this.parent = parent;
		sens.setBounds(rs);
		String txt = parent.getText();
		if (txt.length() > 0) {
			model.clear();
			switch (fld) {
			case Fld_Place:
				for (int i = 0; i < paikat.length; i++) {
					if (paikat[i].toLowerCase().startsWith(txt.toLowerCase())) {
						model.add(paikat[i]);
					}
				}
				break;
			case Fld_Givenname:
				for (int i = 0; i < givennames.length; i++) {
					if (givennames[i].toLowerCase().startsWith(
							txt.toLowerCase())) {
						model.add(givennames[i]);
					}
				}
				break;
			case Fld_Patronyme:
				for (int i = 0; i < patronymes.length; i++) {
					if (patronymes[i].toLowerCase().startsWith(
							txt.toLowerCase())) {
						model.add(patronymes[i]);
					}
				}
				break;
			case Fld_Surname:
				for (int i = 0; i < surnames.length; i++) {
					if (surnames[i].toLowerCase().startsWith(txt.toLowerCase())) {
						model.add(surnames[i]);
					}
				}
				break;
			case Fld_Type:
				for (int i = 0; i < noticeTypes.length; i++) {
					if (noticeTypes[i].toLowerCase().startsWith(
							txt.toLowerCase())) {
						model.add(noticeTypes[i]);
					}
				}
				break;
			case Fld_Description:
				if (tag != null) {
					for (int i = 0; i < descriptions.length; i++) {
						int iix = descriptions[i].indexOf(';');
						if (iix > 0) {
							String myTag = descriptions[i].substring(0, iix);
							String myText = descriptions[i].substring(iix + 1);
							if (myTag.equals(tag)) {

								if (myText.toLowerCase().startsWith(
										txt.toLowerCase())) {
									model.add(myText);
								}
							}
						}
					}
				}
				break;
			case Fld_Group:
				for (int i = 0; i < groups.length; i++) {
					if (groups[i].toLowerCase().startsWith(txt.toLowerCase())) {
						model.add(groups[i]);
					}
				}
				break;
			case Fld_Country:
				// TODO: Fld_Country ?
				break;
			case Fld_Null:
				// TODO: Fld_Null ?
				break;
			}
			lista.updateUI();
			listIndex = 0;
			lista.setSelectedIndex(listIndex);
			// System.out.println("reset to -1");
			sens.setVisible(model.size() > 0);
		} else {
			sens.setVisible(false);
		}
	}

	/**
	 * return top element from list.
	 * 
	 * @param parent
	 *            the parent
	 */
	public void getSens(SukuTextField parent) {
		if (model.size() > 0) {
			parent.setText(model.get(0));
			model.clear();
		}
		sens.setVisible(false);
	}

	/**
	 * Hide.
	 */
	public void hide() {
		sens.setVisible(false);
	}

	/**
	 * Checks if is visible.
	 * 
	 * @return visible state of senser
	 */
	public boolean isVisible() {
		return sens.isVisible();
	}

	private int listIndex = 0;

	/**
	 * move selection in sens-list forward or backward.
	 * 
	 * @param direction
	 *            the direction
	 */
	public void selectList(int direction) {
		// System.out.println("d:" + direction);
		if (direction == 40) {
			listIndex++;
		} else if (direction == 38) {
			listIndex--;
		} else if (direction == 10) {
			int indexi = lista.getSelectedIndex();
			if (indexi >= 0 && indexi < model.size()) {
				String aux = (String) lista.getSelectedValue();
				if (parent != null) {
					parent.setText(aux);
				}

				parent = null;
				return;
			}
			sens.setVisible(false);

		}
		if (listIndex < 0) {
			listIndex = 0;
		}
		if (listIndex >= model.size()) {
			listIndex = model.size() - 1;
		}

		if (listIndex >= 0) {
			lista.setSelectedIndex(listIndex);
			lista.ensureIndexIsVisible(listIndex);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent m) {
		int indexi = lista.getSelectedIndex();
		if (indexi >= 0 && indexi < model.size()) {
			String aux = (String) lista.getSelectedValue();
			if (parent != null) {
				parent.setText(aux);
			}
			sens.setVisible(false);
			parent = null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Not used
	}

}
