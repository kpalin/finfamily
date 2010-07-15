package fi.kaila.suku.swing;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PlaceLocationData;

/**
 * 
 * Shows world map from Google Maps service with locations of relatives
 * 
 * @author halonmi
 * 
 */
public class WorldMap extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(WorldMap.class.getName());
	private ISuku parent;
	private WorldMap me;

	private JLabel map;
	private Image mapImage;

	JComboBox zoom;
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

		setLayout(null);
		setLocation(200, 10);

		this.map = new JLabel("Map");
		this.map.setBounds(10, 10, 400, 600);
		this.getContentPane().add(this.map);

		JLabel lblZ = new JLabel(Resurses.getString("Zoom"));
		lblZ.setBounds(420, 0, 200, 20);
		this.getContentPane().add(lblZ);

		zoom = new JComboBox();
		zoom.addItem(makeObj("0"));
		zoom.addItem(makeObj("1"));
		zoom.addItem(makeObj("2"));
		zoom.addItem(makeObj("3"));
		zoom.addItem(makeObj("4"));
		zoom.addItem(makeObj("5"));
		zoom.addItem(makeObj("6"));
		zoom.addItem(makeObj("7"));
		zoom.addItem(makeObj("8"));
		zoom.addItem(makeObj("9"));
		zoom.addItem(makeObj("10"));
		zoom.addItem(makeObj("11"));
		zoom.addItem(makeObj("12"));
		zoom.addItem(makeObj("14"));
		zoom.addItem(makeObj("15"));
		zoom.addItem(makeObj("16"));

		zoom.setSelectedIndex(4);

		zoom.setBounds(420, 20, 160, 20);
		this.getContentPane().add(zoom);

		JLabel lbl = new JLabel(Resurses.getString(Resurses.PLACECURRENT));
		lbl.setBounds(420, 50, 200, 20);
		this.getContentPane().add(lbl);

		currentPlaces = new JComboBox();
		currentPlaces.setBounds(420, 70, 160, 20);
		currentPlaces.setActionCommand(Resurses.SHOWGRID);
		currentPlaces.addActionListener(this);
		this.getContentPane().add(currentPlaces);

		lbl = new JLabel(Resurses.getString(Resurses.PLACEMISSING));
		lbl.setBounds(420, 100, 200, 20);
		this.getContentPane().add(lbl);

		this.missingPlacesList = new JTextArea();
		this.missingPlacesList.setEditable(false);
		JScrollPane js = new JScrollPane(this.missingPlacesList);
		js.setBounds(420, 120, 160, 480);
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

		GoogleMaps gMap = new GoogleMaps("");
		StringBuffer markers = new StringBuffer("&markers=color:blue");

		int xy = 0;
		int x = this.currentPlaces.getSelectedIndex() - 1;

		try {
			for (int xx = 0; xx < places.length; xx++) {
				if (places[xx].getLatitude() > 0) {
					if (x == -1) {
						markers.append("|" + places[xx].getLatitude() + ","
								+ places[xx].getLongitude());

					} else {
						if (xy == x) {
							markers.append("|" + places[xx].getLatitude() + ","
									+ places[xx].getLongitude());
							xx = places.length + 10;
						}
					}
					xy++;
				}
			}
			this.mapImage = gMap.retrieveStaticImage(400, 600,
					this.zoom.getSelectedIndex(), "png32", markers.toString());
			this.map.setIcon(new ImageIcon(this.mapImage));
		} catch (IOException e1) {
			e1.printStackTrace();
			logger.log(Level.WARNING, "Cannot read map from Google", e1);
			this.mapImage = null;
		}

		map.updateUI();
	}

}
