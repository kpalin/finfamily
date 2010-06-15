package fi.kaila.suku.imports;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

public class ImportOtherDialog extends JDialog implements ActionListener,
		ListSelectionListener {

	/** */
	private static final long serialVersionUID = 1L;

	private JList scList = null;
	private JList scViews = null;
	private JButton ok;
	private JButton cancel;
	private String selectedSchema = null;
	private String[] schemaList = null;
	private Vector<String> viewList = null;
	private JScrollPane viewScroll;
	private int[] viewIds;
	private int selectedView = -1;
	private boolean wasOk = false;

	/**
	 * Constructor
	 * 
	 * @param owner
	 * @throws SukuException
	 */
	public ImportOtherDialog(JFrame owner) throws SukuException {
		super(owner, Resurses.getString(Resurses.IMPORT_OTHER), true);
		// this.owner = owner;

		constructMe(true);

	}

	private void constructMe(boolean allowNew) throws SukuException {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 150, d.height / 2 - 200, 300, 400);
		setLayout(null);
		int y = 10;

		JLabel lbl = new JLabel(Resurses.getString("SCHEMA_SELECT"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 200, 20);
		y += 20;

		SukuData dat = Suku.kontroller.getSukuData("cmd=schema", "type=get");
		String schema = dat.generalArray.length == 1 ? dat.generalArray[0]
				: null;
		SukuData schemas = Suku.kontroller.getSukuData("cmd=schema",
				"type=count");

		Vector<String> v = new Vector<String>();
		for (int i = 0; i < schemas.generalArray.length; i++) {
			if (!schemas.generalArray[i].equals(schema)) {
				v.add(schemas.generalArray[i]);
			}
		}

		schemaList = v.toArray(new String[0]);

		scList = new JList(schemaList);
		scList.addListSelectionListener(this);
		JScrollPane scroll = new JScrollPane(scList);
		getContentPane().add(scroll);
		scroll.setBounds(10, y, 260, 100);

		y += 110;

		lbl = new JLabel(Resurses.getString("SELECT_VIEW"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 200, 20);
		y += 20;

		viewList = new Vector<String>();
		scViews = new JList(viewList);
		scList.addListSelectionListener(this);
		viewScroll = new JScrollPane(scViews);
		getContentPane().add(viewScroll);
		viewScroll.setBounds(10, y, 260, 100);
		y += 110;

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
		y += 60;
		setBounds(d.width / 2 - 150, d.height / 2 - 200, 300, y);

		this.setVisible(true);

	}

	public String getSchema() {
		if (wasOk) {
			return selectedSchema;
		} else {
			return null;
		}
	}

	public int getViewId() {
		if (wasOk) {
			return selectedView;
		}
		return -1;
	}

	@Override
	public void actionPerformed(ActionEvent arg) {

		Object activator = arg.getSource();
		if (activator == null)
			return;
		if (activator == this.ok) {
			selectedView = scViews.getSelectedIndex();
			wasOk = true;
			setVisible(false);
			return;
		}
		if (activator == this.cancel) {
			wasOk = false;
			setVisible(false);
			return;
		}

	}

	@Override
	public void valueChanged(ListSelectionEvent arg) {

		String schema = schemaList[scList.getSelectedIndex()];
		if (schema.equals(selectedSchema))
			return;
		selectedSchema = schema;

		scViews.setSelectedIndices(new int[0]);
		try {
			SukuData views = Suku.kontroller.getSukuData("cmd=viewlist",
					"schema=" + selectedSchema);
			viewIds = new int[views.generalArray.length];
			viewList.removeAllElements();
			for (int i = 0; i < views.generalArray.length; i++) {
				String[] parts = views.generalArray[i].split(";");
				if (parts.length == 2) {
					viewIds[i] = Integer.parseInt(parts[0]);
					viewList.add(parts[1]);
				}
			}
			this.scViews.updateUI();
			this.viewScroll.updateUI();
		} catch (SukuException e) {

			e.printStackTrace();
		}

	}

}
