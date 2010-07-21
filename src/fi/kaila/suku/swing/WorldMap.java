package fi.kaila.suku.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PlaceLocationData;

/**
 * 
 * Shows world map from OpenStreet maps service with locations of relatives
 * 
 * @author halonmi
 * 
 */
public class WorldMap extends JFrame implements ActionListener,
		SukuMapInterface {

	private static final long serialVersionUID = 1L;

	private ISuku parent;
	private WorldMap me;

	private JXMapKit map;

	JComboBox currentPlaces;
	JTextArea missingPlacesList;

	BufferedImage markerImg;

	/**
	 * Constructor
	 * 
	 * @param parent
	 */
	public WorldMap(ISuku parent) {
		this.parent = parent;
		this.me = this;
		initMe();
	}

	private void initMe() {

		setLayout(null);
		setLocation(200, 10);

		// Own waypoint renderer image
		// markerImg = createImageIcon("/images/jalkipolvi.gif");

		map = new JXMapKit();
		map.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
		map.setZoom(12);

		this.map.setBounds(10, 10, 400, 600);
		this.getContentPane().add(this.map);

		JLabel lbl = new JLabel(Resurses.getString(Resurses.PLACECURRENT));
		lbl.setBounds(420, 0, 200, 20);
		this.getContentPane().add(lbl);

		currentPlaces = new JComboBox();
		currentPlaces.setBounds(420, 20, 160, 20);
		currentPlaces.setActionCommand(Resurses.SHOWGRID);
		currentPlaces.addActionListener(this);
		this.getContentPane().add(currentPlaces);

		lbl = new JLabel(Resurses.getString(Resurses.PLACEMISSING));
		lbl.setBounds(420, 50, 200, 20);
		this.getContentPane().add(lbl);

		this.missingPlacesList = new JTextArea();
		this.missingPlacesList.setEditable(false);
		JScrollPane js = new JScrollPane(this.missingPlacesList);
		js.setBounds(420, 80, 160, 520);
		this.getContentPane().add(js);

		setSize(new Dimension(650, 680));
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

	/** Returns an BufferedImage, or null if the path was invalid. */
	protected BufferedImage createImageIcon(String path) {

		InputStream in = this.getClass().getResourceAsStream(path);
		BufferedImage icon;
		try {
			icon = ImageIO.read(in);
			return icon;

		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}

	}

	private PlaceLocationData[] places = null;

	/**
	 * display map with listed places
	 * 
	 * @param places
	 */
	public void displayMap(PlaceLocationData[] places) {
		this.places = places;
		StringBuilder sb = new StringBuilder();

		for (int xx = 0; xx < places.length; xx++) {
			if (places[xx].getLatitude() == 0) {
				sb.append(places[xx].getName() + "(" + places[xx].getCount()
						+ ")\n");
			}
		}
		missingPlacesList.setText(sb.toString());

		this.currentPlaces.addItem(makeObj(""));

		for (int xx = 0; xx < places.length; xx++) {
			if (places[xx].getLatitude() > 0) {
				this.currentPlaces.addItem(makeObj(places[xx].getName() + "("
						+ places[xx].getCount() + ")"));
			}
		}
	}

	private Object makeObj(final String item) {
		return new Object() {
			public String toString() {
				return item;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		int xy = 0;
		int x = this.currentPlaces.getSelectedIndex() - 1;

		Set<Waypoint> waypoints = new HashSet<Waypoint>();

		for (int xx = 0; xx < places.length; xx++) {
			if (places[xx].getLatitude() > 0) {
				if (x == -1) {
					waypoints.add(new Waypoint(places[xx].getLatitude(),
							places[xx].getLongitude()));
					if (xy == 0) {
						map.setCenterPosition(new GeoPosition(places[xx]
								.getLatitude(), places[xx].getLongitude()));
					}
				} else {
					if (xy == x) {
						waypoints.add(new Waypoint(places[xx].getLatitude(),
								places[xx].getLongitude()));
						map.setCenterPosition(new GeoPosition(places[xx]
								.getLatitude(), places[xx].getLongitude()));
						xx = places.length + 10;
					}
				}
				xy++;
			}
		}

		// Create a WaypointPainter to draw the points
		WaypointPainter<JXMapViewer> painter = new WaypointPainter<JXMapViewer>();
		painter.setWaypoints(waypoints);

		// Use own waypoint renderer
		// painter.setRenderer(new SpecialWaypointRenderer());

		map.getMainMap().setOverlayPainter(painter);
	}

	private class SpecialWaypointRenderer implements WaypointRenderer {

		public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint wp) {
			if (wp.getPosition() != null) {
				// Draw selected image
				// g.drawImage(markerImg, null, 0, 0);

				// Draw red X
				g.setColor(Color.RED);
				g.drawLine(-5, -5, +5, +5);
				g.drawLine(-5, +5, +5, -5);

				// Draw Oval with text
				// g.setColor(new Color(255, 255, 255, 175));
				// g.fillOval(-20, -20, 20, 20);
				// g.setColor(Color.BLACK);
				// g.drawString("20", -15, -8);

				return false;

			}
			return true;
		}

	}

}
