package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.util.Resurses;

/**
 * Dialog for connect to database.
 * 
 * @author FIKAAKAIL
 */
public class ConnectDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String OK = "OK";
	private static final String CANCEL = "CANCEL";

	private JTextField host = null;
	private JComboBox dbname = null;
	private JTextField userid = null;
	private JTextField password = null;
	private JCheckBox rememberPwd = null;
	private boolean isRemote;
	private boolean okPressed = false;
	private boolean rememberDatabase = false;
	private SukuKontroller kontroller = null;

	/**
	 * Constructor for dialog.
	 * 
	 * @param owner
	 *            the owner
	 * @param kontroller
	 *            the kontroller
	 * @param isRemote
	 *            the is remote
	 */

	public ConnectDialog(JFrame owner, SukuKontroller kontroller,
			boolean isRemote) {
		super(owner, Resurses.getString("LOGIN_CONNECT"), true);
		this.isRemote = isRemote;
		this.kontroller = kontroller;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		JLabel lbl;

		setLayout(null);
		int y = 20;
		if (!isRemote) {
			lbl = new JLabel(Resurses.getString("LOGIN_HOST"));
			getContentPane().add(lbl);
			lbl.setBounds(20, y, 100, 20);

			host = new JTextField();
			getContentPane().add(host);
			host.setBounds(120, y, 200, 20);
			y += 24;
			lbl = new JLabel(Resurses.getString("LOGIN_DBNAME"));
			getContentPane().add(lbl);
			lbl.setBounds(20, y, 100, 20);

			dbname = new JComboBox();
			dbname.setEditable(true);
			getContentPane().add(dbname);
			dbname.setBounds(120, y, 200, 20);
			y += 24;

		}
		lbl = new JLabel(Resurses.getString("LOGIN_USERID"));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		userid = new JTextField();
		getContentPane().add(userid);
		userid.setBounds(120, y, 200, 20);
		y += 24;
		lbl = new JLabel(Resurses.getString("LOGIN_PASSWORD"));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		password = new JPasswordField();
		getContentPane().add(password);
		password.setBounds(120, y, 200, 20);

		y += 24;

		rememberPwd = new JCheckBox(Resurses.getString("LOGIN_REMEMBER"));
		getContentPane().add(rememberPwd);
		rememberPwd.setBounds(120, y, 200, 20);
		// y += 24;
		//
		// isRemote = new JCheckBox(Resurses.instance().getString("ISREMOTE"));
		// getContentPane().add(isRemote);
		// isRemote.setBounds(120,y,200,20);

		y += 40;
		JButton ok = new JButton(Resurses.getString(OK));
		getContentPane().add(ok);
		ok.setBounds(100, y, 100, 24);
		ok.setActionCommand(OK);
		ok.addActionListener(this);
		ok.setDefaultCapable(true);
		getRootPane().setDefaultButton(ok);

		JButton cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(cancel);
		cancel.setBounds(220, y, 100, 24);
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);
		this.okPressed = false;
		String aux;
		if (isRemote) {
			aux = this.kontroller.getPref(this, "WUSERID", "");
			userid.setText(aux);
			userid.setSelectionStart(aux.length());
			userid.setSelectionEnd(aux.length());

			aux = this.kontroller.getPref(this, "WPASSWORD", "");
			password.setText(aux);
			password.setSelectionStart(aux.length());
			password.setSelectionEnd(aux.length());
		} else {
			aux = this.kontroller.getPref(this, "HOST", "localhost");
			host.setText(aux);
			host.setSelectionStart(aux.length());
			host.setSelectionEnd(aux.length());

			aux = this.kontroller.getPref(this, "DBNAMES", "");
			if (aux != null) {
				String[] names = aux.split(";");
				for (int i = 0; i < names.length; i++) {

					dbname.addItem(names[i]);

				}

				// dbname.setText(aux);
				// dbname.setSelectionStart(aux.length());
				// dbname.setSelectionEnd(aux.length());
			}
			aux = this.kontroller.getPref(this, "USERID", "");
			userid.setText(aux);
			userid.setSelectionStart(aux.length());
			userid.setSelectionEnd(aux.length());

			aux = this.kontroller.getPref(this, "PASSWORD", "");
			password.setText(aux);
			password.setSelectionStart(aux.length());
			password.setSelectionEnd(aux.length());

			aux = this.kontroller.getPref(this, "REMEMBER", "false");
			rememberPwd.setSelected("true".equals(aux));
			aux = this.kontroller.getPref(this, "REMEMBER_DB", "true");
			rememberDatabase = "true".equals(aux) ? true : false;

		}
		setBounds(d.width / 2 - 200, d.height / 2 - 100, 380, y + 80);
		setResizable(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals(CANCEL)) {
			this.okPressed = false;
			setVisible(false);
			return;
		}
		if (cmd.equals(OK)) {
			this.okPressed = true;
			rememberDatabase = true;
			String aux;
			if (this.isRemote) {
				aux = userid.getText();
				this.kontroller.putPref(this, "WUSERID", aux);

				aux = password.getText();
				this.kontroller.putPref(this, "WPASSWORD", aux);

			} else {
				aux = host.getText();
				this.kontroller.putPref(this, "HOST", aux);

				aux = userid.getText();
				this.kontroller.putPref(this, "USERID", aux);

				if (rememberPwd.isSelected()) {

					this.kontroller.putPref(this, "REMEMBER", "true");

				} else {
					this.kontroller.putPref(this, "REMEMBER", "false");

				}
				aux = password.getText();
				if (rememberPwd.isSelected()) {
					this.kontroller.putPref(this, "PASSWORD", aux);
				} else {
					this.kontroller.putPref(this, "PASSWORD", "");
				}

				this.kontroller.putPref(this, "REMEMBER_DB", "true");

			}
			setVisible(false);
		}
	}

	/**
	 * Was ok.
	 * 
	 * @return true if ok was pressed
	 */
	public boolean wasOk() {
		return this.okPressed;
	}

	/**
	 * Gets the host.
	 * 
	 * @return host
	 */
	public String getHost() {
		if (this.host == null)
			return null;
		return this.host.getText();
	}

	/**
	 * Gets the db name.
	 * 
	 * @return database name
	 */
	public String getDbName() {
		if (this.dbname == null)
			return null;

		return (String) this.dbname.getSelectedItem();
	}

	/**
	 * Gets the user id.
	 * 
	 * @return userid
	 */
	public String getUserId() {
		return this.userid.getText();
	}

	/**
	 * Gets the password.
	 * 
	 * @return database password
	 */
	public String getPassword() {
		String pwd = this.password.getText();
		if (pwd.isEmpty())
			return null;
		return pwd;
	}

	/**
	 * Remember database.
	 * 
	 * @param value
	 *            the value
	 */
	public void rememberDatabase(boolean value) {
		rememberDatabase = value;
		this.kontroller.putPref(this, "REMEMBER_DB", rememberDatabase ? "true"
				: "false");
		// this.kontroller.putPref(this, "PASSWORD", "");
	}

	/**
	 * Checks for database.
	 * 
	 * @return true is database is activated
	 */
	public boolean hasDatabase() {
		return rememberDatabase;
	}

}
