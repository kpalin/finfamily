package fi.kaila.suku.util.local;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;

/**
 * 
 * Dialog for usermanagement as admin
 * 
 * @author Kaarle Kaila
 * 
 */
public class LocalUserAdmin extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String OK = "OK";
	private static final String CANCEL = "CANCEL";

	private JTextField userid = null;
	private JTextField password = null;
	private JTextField verifyPassword = null;
	private JButton ok;
	private JButton cancel;

	/**
	 * Constructor for dialog
	 * 
	 * @param owner
	 */
	@SuppressWarnings("unqualified-field-access")
	public LocalUserAdmin(JFrame owner) {
		super(owner, Resurses.getString("ADMIN"), true);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		JLabel lbl;

		setLayout(null);
		int y = 20;

		lbl = new JLabel(Resurses.getString("USERID"));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		userid = new JTextField();
		getContentPane().add(userid);
		userid.setBounds(120, y, 200, 20);

		y += 30;

		lbl = new JLabel(Resurses.getString(Resurses.PASSWORD));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		password = new JPasswordField();
		getContentPane().add(password);
		password.setBounds(120, y, 200, 20);

		y += 30;

		lbl = new JLabel(Resurses.getString(Resurses.VERIFYPWD));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		verifyPassword = new JPasswordField();
		getContentPane().add(verifyPassword);
		verifyPassword.setBounds(120, y, 200, 20);

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
	@SuppressWarnings("unqualified-field-access")
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals(CANCEL)) {
			setVisible(false);
			return;
		}
		if (cmd.equals(OK)) {

			if (password.getText().equals(verifyPassword.getText())
					&& password.getText().length() > 2) {
				setVisible(false);
			} else {
				JOptionPane.showMessageDialog(this, Resurses
						.getString(Resurses.PASSWORDNOTVERIFY));
			}

		}
	}

	boolean isOldUser = false;

	public void setUserid(String userid) {
		isOldUser = true;

		this.userid.setText(userid);
		this.userid.setEditable(false);

	}

	public String getUserid() {
		if (isOldUser)
			return null;
		String usr = this.userid.getText();
		if (usr != null && usr.length() > 2) {
			return usr;
		}
		return null;
	}

	/**
	 * @return database password
	 */
	public String getPassword() {
		String pwd1 = this.password.getText();
		String pwd2 = this.verifyPassword.getText();

		if (pwd1 != null && pwd2 != null && pwd1.equals(pwd2)
				&& pwd1.length() > 2) {

			return pwd1;
		}
		return null;
	}

}
