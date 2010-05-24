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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * Select existing or create new schema
 * 
 * @author kalle
 * 
 */
public class SelectSchema extends JDialog implements ListSelectionListener,
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame owner = null;

	private JTextField schema = null;
	private boolean okSelected = false;
	private JList scList = null;
	private JButton ok;
	private JButton cancel;

	private String[] schemaList = null;

	public SelectSchema(JFrame owner) throws SukuException {
		super(owner, Resurses.getString("SCHEMA_SELECT"), true);
		this.owner = owner;

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 120, d.height / 2 - 140, 240, 280);
		setLayout(null);
		int y = 10;

		JLabel lbl = new JLabel(Resurses.getString("SELECTED_SCHEMA"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 100, 20);
		y += 20;
		schema = new JTextField();
		getContentPane().add(schema);
		schema.setBounds(10, y, 200, 20);

		SukuData schemas = Suku.kontroller.getSukuData("cmd=schema",
				"type=count");
		schemaList = schemas.generalArray;
		scList = new JList(schemaList);
		scList.addListSelectionListener(this);

		JScrollPane scroll = new JScrollPane(scList);
		getContentPane().add(scroll);
		scroll.setBounds(10, y + 20, 200, 100);

		y += 140;
		this.ok = new JButton(Resurses.getString("OK"));
		getContentPane().add(this.ok);
		this.ok.setBounds(10, y, 100, 24);
		this.ok.setActionCommand("OK");
		this.ok.addActionListener(this);
		this.ok.setDefaultCapable(true);
		getRootPane().setDefaultButton(this.ok);

		this.cancel = new JButton(Resurses.getString("CANCEL"));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(130, y, 100, 24);
		this.cancel.setActionCommand("CANCEL");
		this.cancel.addActionListener(this);

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		System.out.println("e" + e.toString());

		int idx = scList.getSelectedIndex();
		if (idx >= 0) {
			schema.setText(schemaList[idx]);
			scList.setSelectedIndices(new int[0]);
		}

	}

	/**
	 * 
	 * @return schema selected
	 */
	public String getSchema() {
		if (okSelected) {
			return schema.getText();
		}
		return null;

	}

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

}
