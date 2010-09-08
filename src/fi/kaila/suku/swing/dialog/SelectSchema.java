package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.MouseInputListener;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Select existing or create new schema.
 * 
 * @author kalle
 */
public class SelectSchema extends JDialog implements ActionListener,
		MouseInputListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private JFrame owner = null;

	private JTextField schema = null;
	private boolean okSelected = false;
	private JList scList = null;
	private JButton ok;
	private JButton cancel;
	private String postDb = "Koedatabase";
	private String[] schemaList = null;

	/**
	 * Constructor that always shows the schema textfield.
	 * 
	 * @param owner
	 *            the owner
	 * @param db
	 *            the db
	 * @throws SukuException
	 *             the suku exception
	 */
	public SelectSchema(JFrame owner, String db) throws SukuException {
		super(owner, Resurses.getString("SCHEMA"), true);
		// this.owner = owner;
		this.postDb = db;
		constructMe(true);

	}

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner
	 * @param db
	 *            the db
	 * @param allowNew
	 *            if false then only list is shown
	 * @throws SukuException
	 *             the suku exception
	 */
	public SelectSchema(JFrame owner, String db, boolean allowNew)
			throws SukuException {
		super(owner, Resurses.getString("SCHEMA"), true);
		// this.owner = owner;
		this.postDb = db;
		constructMe(allowNew);

	}

	private void constructMe(boolean allowNew) throws SukuException {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 120, d.height / 2 - 140, 240, 240);
		setLayout(null);
		int y = 10;
		if (postDb != null) {
			setTitle(postDb);
		}
		String labelValue = "SCHEMA_SELECT";
		if (allowNew)
			labelValue = "SCHEMA_SELECTNEW";

		JLabel lbl = new JLabel(Resurses.getString(labelValue));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 200, 20);
		y += 20;
		schema = new JTextField();
		getContentPane().add(schema);
		schema.setBounds(10, y, 200, 20);
		schema.setVisible(allowNew);
		SukuData schemas = Suku.kontroller.getSukuData("cmd=schema",
				"type=count");
		schemaList = schemas.generalArray;
		if (!allowNew) {
			if (schemaList.length == 1) {
				okSelected = true;
				schema.setText(schemaList[0]);
			}
		}
		scList = new JList(schemaList);

		scList.addMouseListener(this);

		JScrollPane scroll = new JScrollPane(scList);
		getContentPane().add(scroll);
		scroll.setBounds(10, y + 20, 200, 100);

		y += 140;
		this.ok = new JButton(Resurses.getString("OK"));
		getContentPane().add(this.ok);
		this.ok.setBounds(30, y, 80, 24);
		this.ok.setActionCommand("OK");
		this.ok.addActionListener(this);
		this.ok.setDefaultCapable(true);

		getRootPane().setDefaultButton(this.ok);

		this.cancel = new JButton(Resurses.getString("CANCEL"));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(120, y, 80, 24);
		this.cancel.setActionCommand("CANCEL");
		this.cancel.addActionListener(this);
		getRootPane().setDefaultButton(ok);
	}

	/**
	 * Gets the schema.
	 * 
	 * @return schema selected
	 */
	public String getSchema() {
		if (okSelected) {
			return schema.getText();
		}
		return null;

	}

	/**
	 * Checks if is existing schema.
	 * 
	 * @return true if selected schema already existed
	 */
	public boolean isExistingSchema() {

		String aux = getSchema();
		if (aux != null) {

			for (int i = 0; i < schemaList.length; i++) {
				if (aux.equalsIgnoreCase(schemaList[i])) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		if (cmd.equals("OK")) {
			okSelected = true;

		}
		setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		int clickCount = e.getClickCount();
		int idx = scList.getSelectedIndex();
		if (idx >= 0) {
			schema.setText(schemaList[idx]);
			// if (schema.isVisible()) {
			// scList.setSelectedIndices(new int[0]);
			// }
			if (clickCount > 1) {
				okSelected = true;
				setVisible(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(java.awt.event.MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(java.awt.event.MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(java.awt.event.MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(java.awt.event.MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {

	}

}
