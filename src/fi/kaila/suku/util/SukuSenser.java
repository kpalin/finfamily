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
 * @author Kalle A singleton class that displays an intellisens window below a
 *         SukuTextField
 */
public class SukuSenser implements MouseListener {

	private JWindow sens;
	private JScrollPane scroller;
	private JList lista;
	private Vector<String> model;

	private SukuSenser() {
		sens = new JWindow();
		model = new Vector<String>();

		lista = new JList(model);
		scroller = new JScrollPane(lista);

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

	/**
	 * Initialize the places
	 * 
	 * @param places
	 */
	public void setPaikat(String[] places) {
		this.paikat = places;
	}

	/**
	 * @param tag
	 * @param fld
	 * @param parent
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
	 * Show intellisens
	 * 
	 * @param parent
	 * @param tag
	 * @param fld
	 * 
	 * @param txt
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
			model.removeAllElements();
			for (int i = 0; i < paikat.length; i++) {
				if (paikat[i].toLowerCase().startsWith(txt.toLowerCase())) {
					model.add(paikat[i]);
				}
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
	 * return top element from list
	 * 
	 * @param parent
	 */
	public void getSens(SukuTextField parent) {
		if (model.size() > 0) {
			parent.setText(model.get(0));
			model.removeAllElements();
		}
		sens.setVisible(false);
	}

	/**
	 * 
	 */
	public void hide() {
		sens.setVisible(false);
	}

	private int listIndex = 0;

	/**
	 * move selection in sens-list forward or backward
	 * 
	 * @param direction
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
		if (listIndex >= model.size()) {
			listIndex = model.size() - 1;
		}

		if (listIndex >= 0) {
			lista.setSelectedIndex(listIndex);
			lista.ensureIndexIsVisible(listIndex);

		}
	}

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

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

}
