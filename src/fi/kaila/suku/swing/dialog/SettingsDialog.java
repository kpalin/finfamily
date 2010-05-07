package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.ExcelBundle;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * various settings will be done here
 * 
 * @author Kalle
 * 
 */
public class SettingsDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(this.getName());

	private JComboBox loca = null;
	private JComboBox repolang = null;
	private JComboBox dateFormat = null;
	private JButton ok;

	private String[] locatexts = null;
	private String[] locas = null;
	private String[] dateFormats = null;
	private String[] dateCodes = null;
	private int dateIndex = 0;
	private int locaIndex = 0;
	private JFrame owner = null;

	/**
	 * @param owner
	 * @throws SukuException
	 */
	public SettingsDialog(JFrame owner) throws SukuException {
		this.owner = owner;
		setLayout(null);
		int x = 20;
		int y = 20;

		SukuData exresp = Suku.kontroller.getSukuData("cmd=excel",
				"page=languages");

		// ExcelBundle.getBundle("excel/FinFamily", "Report", new Locale("fi"));
		locatexts = ExcelBundle.getLangNames();
		locas = ExcelBundle.getLangCodes();
		// Resurses.getString("LOCALIZAT_TEXTS").split(";");
		// locas = Resurses.getString("LOCALIZAT_CODES").split(";");
		dateFormats = Resurses.getString("LOCALIZAT_DATEFORMATS").split(";");
		dateCodes = Resurses.getString("LOCALIZAT_DATECODES").split(";");

		JLabel lbl = new JLabel(Resurses.getString("SETTING_LOCALE"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 200, 20);
		y += 20;
		loca = new JComboBox(locatexts);
		getContentPane().add(loca);
		loca.setBounds(x, y, 200, 20);

		String prevloca = Suku.kontroller.getPref(owner, Resurses.LOCALE, "fi");
		locaIndex = 0;
		for (int i = 0; i < locas.length; i++) {
			if (prevloca.equals(locas[i])) {
				locaIndex = i;
			}
		}
		loca.setSelectedIndex(locaIndex);

		y += 20;
		lbl = new JLabel(Resurses.getString("SETTING_REPOLANG"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 200, 20);

		y += 20;

		String[] langnames = new String[Suku.getRepoLanguageCount()];
		String[] langcodes = new String[Suku.getRepoLanguageCount()];
		for (int i = 0; i < langnames.length; i++) {
			langnames[i] = Suku.getRepoLanguage(i, false);
			langcodes[i] = Suku.getRepoLanguage(i, true);
		}

		repolang = new JComboBox(langnames);
		getContentPane().add(repolang);
		repolang.setBounds(x, y, 200, 20);

		prevloca = Suku.kontroller.getPref(owner, Resurses.REPOLANG, "fi");

		locaIndex = 0;
		for (int i = 0; i < langcodes.length; i++) {
			if (prevloca.equals(langcodes[i])) {
				locaIndex = i;
			}
		}

		if (locaIndex < repolang.getItemCount()) {
			repolang.setSelectedIndex(locaIndex);
		}

		y += 20;

		lbl = new JLabel(Resurses.getString("SETTING_DATEFORMAT"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 200, 20);

		y += 20;
		dateFormat = new JComboBox(dateFormats);
		getContentPane().add(dateFormat);
		dateFormat.setBounds(x, y, 200, 20);

		prevloca = Suku.kontroller.getPref(owner, Resurses.DATEFORMAT, "FI");
		dateIndex = 0;
		for (int i = 0; i < dateFormats.length; i++) {
			if (prevloca.equals(dateCodes[i])) {
				dateIndex = i;
			}
		}

		dateFormat.setSelectedIndex(dateIndex);

		this.ok = new JButton(Resurses.OK);
		// this.ok.setDefaultCapable(true);
		getContentPane().add(this.ok);
		this.ok.setActionCommand(Resurses.OK);
		this.ok.addActionListener(this);
		this.ok.setBounds(120, 220, 100, 24);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width / 2 - 200, d.height / 2 - 150, 400, 300);
		getRootPane().setDefaultButton(this.ok);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		if (cmd == null)
			return;

		if (cmd.equals(Resurses.OK)) {

			int newLoca = loca.getSelectedIndex();
			Suku.kontroller.putPref(owner, Resurses.LOCALE, locas[newLoca]);
			Resurses.setLanguage(locas[newLoca]);

			int newLang = repolang.getSelectedIndex();
			if (newLang >= 0) {
				Suku.kontroller.putPref(owner, Resurses.REPOLANG, Suku
						.getRepoLanguage(newLang, true));

			}
			int newDateIndex = dateFormat.getSelectedIndex();
			Suku.kontroller.putPref(owner, Resurses.DATEFORMAT,
					dateCodes[newDateIndex]);
			Resurses.setDateFormat(dateCodes[newDateIndex]);
			Utils.resetSukuModel();
			setVisible(false);
		}
	}

}
