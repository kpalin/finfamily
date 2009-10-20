package fi.kaila.suku.swing.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JPanel;

import fi.kaila.suku.swing.util.SukuPopupMenu;
import fi.kaila.suku.util.FamilyParentRelationIndex;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.TableShortData;

public class FamilyPanel extends JPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private TableShortData subjectTable=null;
	private Vector<TableShortData> tabs = new Vector<TableShortData>();

	private Vector<FamilyParentRelationIndex> pareRels = new Vector<FamilyParentRelationIndex>();
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private PersonView parent;

	public FamilyPanel(PersonView parent) {
		this.parent = parent;
		this.addMouseListener(this);
	}

	public void paint(Graphics g) {

		// for (int i = 0; i < tabs.size(); i++) {
		// logger.fine("BEG[" + i + "]=" + tabs.get(i));
		// }

		Rectangle d = this.getBounds();
		g.setColor(Color.white);
		g.fillRect(0, 0, d.width, d.height);

		g.setColor(Color.black);

		for (int i = 0; i < tabs.size(); i++) {
			TableShortData t = tabs.get(i);
			t.DrawMe(g);
		}

		for (int i = 0; i < pareRels.size(); i++) {

			FamilyParentRelationIndex rel = pareRels.get(i);

			// System.out.println("rel:" + rel.getChildIdx() + "/" +
			// rel.getParentIdx());

			TableShortData child = tabs.get(rel.getChildIdx());
			TableShortData parent = tabs.get(rel.getParentIdx());

			Point cp = child.getLocation();
			Point pp = parent.getLocation();
			Dimension dd = child.getSize(g);
			Dimension dp = parent.getSize(g);
			// cp.x += dd.width/2;

			g.drawLine(cp.x + dd.width / 2, cp.y, pp.x + dp.width / 2, pp.y
					+ dp.height);

		}

		// if (subjectTable != null) {
		// subjectTable.DrawMe(g);
		// }

		for (int x = 0; x < d.width; x += 100) {
			g.drawLine(x, 0, x, 20);
			g.drawString("" + x, x, 10);

		}
		Dimension prefd = new Dimension();
		for (int i = 0; i < tabs.size(); i++) {
			TableShortData t = tabs.get(i);
			int x = t.getLocation().x + t.getSize(g).width;
			int y = t.getLocation().y + t.getSize(g).height;
			if (prefd.width < x)
				prefd.width = x;
			if (prefd.height < y)
				prefd.height = y;

			logger.finer("END[" + i + "]=" + tabs.get(i));
		}
		prefd.width += 20;
		prefd.height += 20;
		logger.finer("PREFD[" + prefd.width + "," + prefd.height + "]");
		setPreferredSize(prefd);

	}

	public void resetTable() {
		tabs.removeAllElements();
		pareRels.removeAllElements();
	}

	public void addTable(TableShortData data) {
		tabs.add(data);

		updateUI();

	}

	public int getTabSize() {
		return this.tabs.size();
	}

	public void addRels(FamilyParentRelationIndex relIdx) {
		// System.out.println("addrel: " + relIdx.getChildIdx() + "/" +
		// relIdx.getParentIdx());

		pareRels.add(relIdx);
		updateUI();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

			Point pp = e.getPoint();
			for (int i = 0; i < tabs.size(); i++) {
				TableShortData subjectTable = tabs.get(i);

				Rectangle dd = subjectTable.getArea();
				if (dd != null) {
					if (dd.contains(pp)) {
						Point point = new Point(pp.x - dd.x, pp.y - dd.y);
						PersonShortData person = subjectTable
								.getPersonAtPoint(point);
						if (person != null) {
							try {
								parent.setSubjectForFamily(person);
							} catch (SukuException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {

			Point pp = e.getPoint();
			for (int i = 0; i < tabs.size(); i++) {
				TableShortData subjectTable = tabs.get(i);

				Rectangle dd = subjectTable.getArea();

				if (dd.contains(pp)) {
					// System.out.println("Osui: ");// +
					// subjectTable.getSubject().getTextName());
					Point point = new Point(pp.x - dd.x, pp.y - dd.y);

					PersonShortData person = subjectTable
							.getPersonAtPoint(point);
					if (person != null) {
						SukuPopupMenu pop = SukuPopupMenu.getInstance();
						pop.setPerson(person);
						pop.show(e, pp.x, pp.y);
						//					
						// parent.getSuku().pShowPerson.setText(person.getAlfaName());
						// parent.getSuku().pMenu.show(e.getComponent(),pp.x,pp.y);

						// pMenu.show(e.getComponent(),
						// e.getX(), e.getY());

						// System.out.println("Henkilöön: " +
						// person.getTextName());
					} else {
						// System.out.println("Tyhjään");
					}

				}

			}

		}
	}

}
