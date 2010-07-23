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
import javax.swing.GroupLayout;
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
	private Waypoint centerWaypoint;

	JComboBox currentPlaces;
	JTextArea missingPlacesList;

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

		map = new JXMapKit();
		map.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
		map.setZoom(12);

		JLabel lblC = new JLabel(Resurses.getString(Resurses.PLACECURRENT));

		currentPlaces = new JComboBox();
		currentPlaces.setActionCommand(Resurses.SHOWGRID);
		currentPlaces.addActionListener(this);

		JLabel lblM = new JLabel(Resurses.getString(Resurses.PLACEMISSING));

		this.missingPlacesList = new JTextArea();
		this.missingPlacesList.setEditable(false);
		JScrollPane js = new JScrollPane(this.missingPlacesList);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING).addComponent(
								map, 0, GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(lblC, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(currentPlaces, 250, 250,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblM, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(js, 250, 250,
										GroupLayout.PREFERRED_SIZE)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(map, 0, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addGroup(
										layout.createSequentialGroup()
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
																.addComponent(
																		lblC))
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.LEADING)
																.addComponent(
																		currentPlaces,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.LEADING)
																.addComponent(
																		lblM))
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.LEADING)
																.addComponent(
																		js)))));

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
		int x = this.currentPlaces.getSelectedIndex();

		Set<Waypoint> waypoints = new HashSet<Waypoint>();

		for (int xx = 0; xx < places.length; xx++) {
			if (places[xx].getLatitude() > 0) {
				waypoints.add(new SpecialWaypoint(places[xx].getLatitude(),
						places[xx].getLongitude(), places[xx].getCount()));
				if (xy == x) {
					map.setCenterPosition(new GeoPosition(places[xx]
							.getLatitude(), places[xx].getLongitude()));
					centerWaypoint = new SpecialWaypoint(
							places[xx].getLatitude(),
							places[xx].getLongitude(), places[xx].getCount());
				}
				xy++;
			}
		}

		// Create a WaypointPainter to draw the points
		WaypointPainter<JXMapViewer> painter = new WaypointPainter<JXMapViewer>();
		painter.setWaypoints(waypoints);

		// Use own waypoint renderer
		painter.setRenderer(new SpecialWaypointRenderer());

		map.getMainMap().setOverlayPainter(painter);
	}

	private class SpecialWaypointRenderer implements WaypointRenderer {

		public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint wp) {
			SpecialWaypoint swp = (SpecialWaypoint) wp;
			if (swp.getPosition() != null) {
				if (swp.getPosition().equals(centerWaypoint.getPosition())) {
					g.setColor(Color.RED);
					g.drawLine(-6, -6, +6, +6);
					g.drawLine(-6, +6, +6, -6);
				} else {
					g.setColor(Color.BLUE);
					g.drawLine(-3, -3, +3, +3);
					g.drawLine(-3, +3, +3, -3);
				}

				g.setColor(Color.BLACK);
				int x = swp.getCount() + 5;
				if (x < 105) {
					int y = -1 * (x / 5);
					int z = 2 * (x / 5);
					g.drawOval(y, y, z, z);
				} else {
					g.drawOval(-20, -20, 40, 40);
				}

				return false;
			}
			return true;
		}

	}

}
