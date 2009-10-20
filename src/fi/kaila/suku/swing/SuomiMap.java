package fi.kaila.suku.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PlaceLocationData;

public class SuomiMap extends JFrame implements ActionListener {

	private static Logger logger = Logger.getLogger(SuomiMap.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ISuku parent;
	private SuomiMap me;

	private KarttaPanel map;
	private BufferedImage kartta;

	JMenuBar menubar;
	JMenu mFile;
	JMenuItem mGrid;

	JTextArea currentPlace;
	JTextArea missingPlaceList;

	private Rectangle suomiSize = new Rectangle();

	private static final double suomiLeft = 18.44; // 18.55;
	private static final double suomiBottom = 59.65; // 59.735;
	private static final double suomiRight = 33.4; // 33.5;
	@SuppressWarnings("unused")
	private static final double suomiTop = 70.3;

	public SuomiMap(ISuku parent) {
		this.parent = parent;
		this.me = this;
		initMe();
	}

	private void initMe() {

		this.menubar = new JMenuBar();
		setJMenuBar(this.menubar);
		this.mFile = new JMenu(Resurses.getString(Resurses.FILE));
		this.menubar.add(this.mFile);

		this.mGrid = new JMenuItem(Resurses.getString(Resurses.SHOWGRID));
		this.mFile.add(this.mGrid);
		this.mGrid.setActionCommand(Resurses.SHOWGRID);
		this.mGrid.addActionListener(this);

		setLayout(null);
		setLocation(200, 10);

		File f = new File("resources/images/suomikartta.jpg");
		try {
			this.kartta = ImageIO.read(f);

			suomiSize = new Rectangle(0, 0, this.kartta.getWidth(), this.kartta
					.getHeight());
		} catch (IOException e1) {
			e1.printStackTrace();
			logger.log(Level.WARNING, "Cannot read suomikartta", e1);
			this.kartta = null;
		}

		this.map = new KarttaPanel();

		this.map.setBounds(10, 10, kartta.getWidth(), kartta.getHeight());
		this.getContentPane().add(this.map);

		// Font fnt = new Font(Font.SANS_SERIF,Font.PLAIN,10);

		JLabel lbl = new JLabel(Resurses.getString(Resurses.PLACECURRENT));
		lbl.setBounds(20 + kartta.getWidth(), 0, 200, 20);
		this.getContentPane().add(lbl);

		this.currentPlace = new JTextArea("");
		// this.currentPlace.setFont(fnt);
		this.currentPlace.setEditable(false);
		JScrollPane jsc = new JScrollPane(this.currentPlace);
		jsc.setBounds(20 + kartta.getWidth(), 20, 160, 100);
		this.getContentPane().add(jsc);

		lbl = new JLabel(Resurses.getString(Resurses.PLACEMISSING));
		lbl.setBounds(20 + kartta.getWidth(), 120, 200, 20);
		this.getContentPane().add(lbl);

		this.missingPlaceList = new JTextArea();
		this.missingPlaceList.setEditable(false);
		JScrollPane js = new JScrollPane(this.missingPlaceList);
		js
				.setBounds(20 + kartta.getWidth(), 140, 160,
						kartta.getHeight() - 140);
		this.getContentPane().add(js);

		setSize(new Dimension(250 + kartta.getWidth(), 80 + kartta.getHeight()));
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (parent != null) {
					parent.SukuFormClosing(me);

				}
				e.getClass();

			}
		});

	}

	private int lukuri = 0;
	private PlaceLocationData[] places = null;

	public void displaySuomiMap(PlaceLocationData[] places) {
		lukuri++;
		this.places = places;
		// int paikkddoja = 0;
		// if (places != null){
		// paikkddoja = places.length;
		//			
		// }
		StringBuilder sb = new StringBuilder();

		for (int xx = 0; xx < places.length; xx++) {
			if (places[xx].getLatitude() == 0) {
				sb.append(places[xx].getName() + "(" + places[xx].getCount()
						+ ")\n");
			}
		}
		missingPlaceList.setText(sb.toString());

		// JOptionPane.showMessageDialog(this, "Paikkoja [" + paikkoja +
		// "]. Käynti no " + lukuri);
	}

	class KarttaPanel extends JPanel implements MouseListener,
			MouseMotionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		KarttaPanel() {
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}

		public void paintComponent(Graphics g) {

			// Rectangle suomiSize = new
			// Rectangle(0,0,kartta.getWidth(),kartta.getHeight());

			g.drawImage(kartta, 0, 0, null);

			if (showGrid) {

				Font redFont = new Font("SansSerif", Font.PLAIN, 9);
				FontMetrics fm = g.getFontMetrics(redFont);

				g.setColor(Color.red);
				g.setFont(redFont);
				Graphics2D gg = (Graphics2D) g;
				for (float yy = 60f; yy < 71f; yy += 1f) {
					PointD pWest = convertToPointD(19, yy);
					PointD pEast = convertToPointD(33, yy);
					PointD pTxt = convertToPointD(suomiLeft + 0.1, yy + 0.25);
					g.drawLine((int) pWest.x, (int) pWest.y, (int) pEast.x,
							(int) pEast.y);
					gg.drawString("" + (int) yy, (float) pTxt.x,
							(float) (pTxt.y + fm.getHeight()));
				}

				// gg.drawLine(19,59,33,59);
				// gg.drawString("KALLEN KOE", 19, 59);

				for (float xx = 19f; xx < 33.5f; xx += 1f) {
					PointD pSouth = convertToPointD(xx, 60);
					PointD pNorth = convertToPointD(xx, 70);
					PointD pTxt = convertToPointD(xx - 0.3, 60);
					g.drawLine((int) pSouth.x, (int) pSouth.y, (int) pNorth.x,
							(int) pNorth.y);
					gg.drawString("" + (int) xx, (float) pTxt.x,
							(float) (pTxt.y + fm.getHeight()));
				}
			}

			g.setColor(Color.blue);

			int idx;
			PlaceLocationData place;
			for (idx = 0; idx < places.length; idx++) {

				place = places[idx];

				if (place.getLatitude() > 0) {

					Rectangle rec = convertToRectangle(place.getLongitude(),
							place.getLatitude(), place.getCount());

					// if (debWrite)
					// {
					// Debug.WriteLine(place.ToString() + " to " +
					// rec.ToString());
					// }
					// g.drawOval(rec.x,rec.y-100,50,50);

					g.drawOval(rec.x, rec.y, rec.width, rec.height);

				}

			}
		}

		private Rectangle convertToRectangle(double xtude, double ytude,
				int count) {
			// PointD xyz1 = convertToPointD(18.44, 59.65); // left bottom
			// PointD xyz2 = convertToPointD(18.44, 70.3); // left bottom
			// PointD xyz3 = convertToPointD(33.4, 59.65); // left bottom
			// PointD xyz4 = convertToPointD(33.4, 70.3); // left bottom

			PointD pp = convertToPointD(xtude, ytude);

			int displayCount = count;
			if (count > 20)
				displayCount = 20;
			return new Rectangle((int) (pp.x - displayCount * 2 / 2),
					(int) (pp.y - displayCount * 2 / 2), displayCount * 2,
					displayCount * 2);
		}

		private PointD convertToPointD(double xtude, double ytude) {
			double xoffset = xtude - suomiLeft;

			xoffset = xoffset / (suomiRight - suomiLeft);

			double suomix = suomiSize.getWidth();
			double paikkaX = suomix * xoffset;

			double YY = ytude - suomiBottom;

			double y = 0.03 * YY * YY + 1.7 * YY;
			double yoffset = y / 21.9; // (suomiTop - suomiLong);
			double suomiy = suomiSize.getHeight();
			double paikkaY = suomiy * yoffset;

			return new PointD(paikkaX, (suomiy - paikkaY));

		}

		boolean mouseIsHere = false;

		protected void processMouseEvent(MouseEvent e) {
			if (e.getID() == MouseEvent.MOUSE_ENTERED) {
				mouseIsHere = true;
			} else if (e.getID() == MouseEvent.MOUSE_EXITED) {
				mouseIsHere = false;
			}

			// System.out.println("Mouse: " + e.toString());
		}

		protected void processMouseMotionEvent(MouseEvent e) {
			if (mouseIsHere) {
				int idx;
				PlaceLocationData place;
				int x = e.getX();
				int y = e.getY();

				for (idx = 0; idx < places.length; idx++) {

					place = places[idx];

					if (place.getLatitude() > 0) {
						Rectangle rec = convertToRectangle(
								place.getLongitude(), place.getLatitude(),
								place.getCount());

						if (rec.contains(x, y)) {
							String nytPaikka = place.getName() + "("
									+ place.getCount() + ")";
							String olde = currentPlace.getText();

							String[] rivit = olde.split("\n");

							StringBuffer sb = new StringBuffer();
							sb.append(nytPaikka);
							sb.append("\n");
							int rc = 8;
							if (rivit.length < 8) {
								rc = rivit.length;
							}
							for (int i = 0; i < rc; i++) {
								if (!nytPaikka.equals(rivit[i])) {
									sb.append(rivit[i]);
									sb.append("\n");
								}
							}
							currentPlace.setText(sb.toString());
						}
					}
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			//
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			//		
		}

		@Override
		public void mouseExited(MouseEvent e) {
			//
		}

		@Override
		public void mousePressed(MouseEvent e) {
			//
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			//
		}

		@Override
		public void mouseMoved(MouseEvent arg) {
			//
		}

	}

	class PointD {

		double x;
		double y;

		PointD(double x, double y) {
			this.x = x;
			this.y = y;
		}

	}

	private boolean showGrid = false;

	@Override
	public void actionPerformed(ActionEvent e) {

		this.showGrid = !this.showGrid;
		map.updateUI();
		// JOptionPane.showMessageDialog(this, "Näytä ruudukko");
	}

}
