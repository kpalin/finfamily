package fi.kaila.suku.swing.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import fi.kaila.suku.swing.util.SukuPopupMenu;
import fi.kaila.suku.swing.util.SukuPopupMenu.MenuSource;
import fi.kaila.suku.util.FamilyParentRelationIndex;
import fi.kaila.suku.util.ImageSelection;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.TableShortData;

/**
 * 
 * FamilyPanel shows a simple graph of the subjects family, parents and
 * grandparents
 * 
 * @author Kalle
 * 
 */
public class FamilyPanel extends JPanel implements MouseListener,
		MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector<TableShortData> tabs = new Vector<TableShortData>();

	private Vector<FamilyParentRelationIndex> pareRels = new Vector<FamilyParentRelationIndex>();
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private PersonView parent;

	/**
	 * @param parent
	 */
	public FamilyPanel(PersonView parent) {
		this.parent = parent;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void copyToClipAsImage() {
		// Create a BufferedImage

		Dimension dd = getPreferredSize();

		BufferedImage image = new BufferedImage(dd.width, dd.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		Graphics2D graphics = (Graphics2D) g;
		this.paint(graphics);
		ImageSelection imgSel = new ImageSelection(image);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel,
				null);

		try {
			ImageIO.write(image, "jpg", new File("component.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// JAI allows Bitmap,
		// others only GIF,PNG,JSPEG - JPEG:correction

	}

	@Override
	public void paintComponent(Graphics g) {

		// }
		// public void paint(Graphics g) {

		Rectangle d = this.getBounds();
		g.setColor(Color.white);
		g.fillRect(0, 0, d.width, d.height);

		g.setColor(Color.black);

		for (int i = 0; i < tabs.size(); i++) {
			TableShortData t = tabs.get(i);
			t.drawMe(g);
		}

		for (int i = 0; i < pareRels.size(); i++) {

			FamilyParentRelationIndex rel = pareRels.get(i);

			TableShortData child = tabs.get(rel.getChildIdx());
			TableShortData parent = tabs.get(rel.getParentIdx());

			Point cp = child.getLocation();
			Point pp = parent.getLocation();
			Dimension dd = child.getSize(g);
			Dimension dp = parent.getSize(g);

			g.drawLine(cp.x + dd.width / 2, cp.y, pp.x + dp.width / 2, pp.y
					+ dp.height);

		}

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

	/**
	 * empty the graph
	 */
	public void resetTable() {
		tabs.removeAllElements();
		pareRels.removeAllElements();
	}

	/**
	 * add a table to the graph
	 * 
	 * @param data
	 */
	public void addTable(TableShortData data) {
		tabs.add(data);

		updateUI();

	}

	/**
	 * @return table size = number of tables in list
	 */
	public int getTabSize() {
		return this.tabs.size();
	}

	/**
	 * 
	 * Add parent relation
	 * 
	 * @param relIdx
	 */
	public void addRels(FamilyParentRelationIndex relIdx) {

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
								logger.log(Level.WARNING, "failed", e1);

							}
						}
					}
				}
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		presTab = null;
		// System.out.println("ENT: " + e.toString());
	}

	@Override
	public void mouseExited(MouseEvent e) {

		if (e.getButton() == 1 && presTab != null) {
			// System.out.println("EXT: " + e.toString());
			presTab = null;
		}
	}

	TableShortData presTab = null;
	Point presFrom = null;

	@Override
	public void mousePressed(MouseEvent e) {
		presTab = null;
		if (e.getButton() == 1) {
			Point presPoint = e.getPoint();
			for (int i = 0; i < tabs.size(); i++) {
				TableShortData t = tabs.get(i);
				Rectangle rec = t.getArea();
				if (rec.contains(presPoint)) {
					presTab = t;
					presFrom = new Point(presPoint.x - t.getArea().x,
							presPoint.y - t.getArea().y);
					System.out.println("PRS: " + t.toString());
					break;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == 1 && presTab != null) {
			// System.out.println("DROP IT HERE: " + e.toString());
			Point p = e.getPoint();
			p.x -= presFrom.x;
			p.y -= presFrom.y;
			presTab.setLocation(p);
			this.updateUI();
			presTab = null;
			presFrom = null;
		}
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
						pop.show(e, pp.x, pp.y, MenuSource.familyView);
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

	@Override
	public void mouseDragged(MouseEvent e) {
		if (presTab != null) {
			Point p = e.getPoint();
			p.x -= presFrom.x;
			p.y -= presFrom.y;
			presTab.setLocation(p);
			this.updateUI();
			// System.out.println("DRG: " + e.toString());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// System.out.println("MOV: " + e.toString());

	}

	// 
}
