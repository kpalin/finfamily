package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import fi.kaila.suku.ant.AntVersion;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class AboutDialog.
 * 
 * @author FIKAAKAIL
 * 
 *         About box for Suku
 */
public class AboutDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String OK = "OK";
	private static Logger logger = null;
	private JTextArea aboutArea;

	/**
	 * Instantiates a new about dialog.
	 * 
	 * @param owner
	 *            the owner
	 */
	public AboutDialog(JFrame owner) {
		super(owner, Resurses.getString("ABOUT"), true);
		logger = Logger.getLogger(this.getClass().getName());
		setLayout(null);

		aboutArea = new JTextArea();
		aboutArea.setEditable(false);
		aboutArea.setLineWrap(true);

		add(aboutArea);
		aboutArea.setBounds(20, 20, 560, 200);

		StringBuilder about = new StringBuilder();

		String aux = "Java Version: " + System.getProperty("java.version")
				+ " from " + System.getProperty("java.vendor");
		logger.info(aux);
		about.append("- " + aux + "\n");
		about.append("- os.name: " + System.getProperty("os.name") + "\n");
		aux = Resurses.getString(Resurses.ABOUT_SUKU_VERSION) + " = "
				+ AntVersion.antVersion;
		logger.info(aux);
		about.append("- " + aux);
		about.append("\n");

		if (Suku.serverVersion != null) {
			aux = Resurses.getString(Resurses.ABOUT_SERVER_VERSION) + " = "
					+ Suku.serverVersion;
			about.append("- " + aux);
			about.append("\n");
			logger.info(aux);
		}

		if (Suku.postServerVersion != null) {

			aux = Resurses.getString(Resurses.ABOUT_DB_VERSION) + " = "
					+ Suku.postServerVersion;
			about.append("- " + aux);
			about.append("\n");
			logger.info(aux);
		}
		if (Suku.serverVersion != null) {
			aux = Resurses.getString(Resurses.ABOUT_SERVER_VERSION) + " = "
					+ Suku.serverVersion;
			about.append("- " + aux);
			about.append("\n");
		}
		SukuData stats = null;
		try {
			stats = Suku.kontroller.getSukuData("cmd=dbstats");
		} catch (Exception e) {
			stats = new SukuData();
			if (e.getMessage() != null) {
				stats.resu = e.getMessage();
			} else {
				stats.resu = "no connection";
			}
		}
		logger.info(stats.resu);
		about.append(Resurses.getString("DBSTATS") + ": " + stats.resu);
		aboutArea.setText(about.toString());

		JButton ok = new JButton(OK);
		getContentPane().add(ok);
		ok.setBounds(480, 235, 100, 24);
		ok.addActionListener(this);
		ok.setDefaultCapable(true);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width / 2 - 200, d.height / 2 - 200, 600, 300);
		setResizable(false);

		getRootPane().setDefaultButton(ok);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		this.setVisible(false);

	}

}
