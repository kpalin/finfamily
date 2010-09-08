package fi.kaila.suku.util.local;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;

/**
 * Dialog for connect to admin database.
 * 
 * @author FIKAAKAIL
 */
public class AdminConnectDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String OK = "OK";
	private static final String CANCEL = "CANCEL";

	private JTextField password = null;

	private JButton ok;
	private JButton cancel;

	/**
	 * Constructor for dialog.
	 * 
	 * @param owner
	 *            the owner
	 */
	@SuppressWarnings("unqualified-field-access")
	public AdminConnectDialog(JFrame owner) {
		super(owner, Resurses.getString("ADMIN"), true);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		JLabel lbl;

		setLayout(null);
		int y = 20;

		lbl = new JLabel("Admin " + Resurses.getString("LOGIN_PASSWORD"));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		password = new JPasswordField();
		getContentPane().add(password);
		password.setBounds(120, y, 200, 20);

		y += 40;
		ok = new JButton(Resurses.getString(OK));
		getContentPane().add(ok);
		ok.setBounds(110, y, 100, 24);
		ok.setActionCommand(OK);
		ok.addActionListener(this);
		ok.setDefaultCapable(true);
		getRootPane().setDefaultButton(ok);

		cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(cancel);
		cancel.setBounds(230, y, 100, 24);
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);

		setBounds(d.width / 2 - 200, d.height / 2 - 100, 400, y + 70);
	}

	//
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("unqualified-field-access")
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals(CANCEL)) {

			setVisible(false);
			return;
		}
		if (cmd.equals(OK)) {
			// TODO:
		}
		setVisible(false);

	}

	/**
	 * Gets the password.
	 * 
	 * @return database password
	 */
	public String getPassword() {
		return this.password.getText();
	}

}
