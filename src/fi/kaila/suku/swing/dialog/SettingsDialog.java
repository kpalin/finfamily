package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.ExcelBundle;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * various settings will be done here.
 * 
 * @author Kalle
 */
public class SettingsDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox loca = null;
	private JComboBox repolang = null;
	private JComboBox dateFormat = null;
	private JCheckBox useOpenStreetMap = null;
	private JComboBox defaultCountryCode = null;
	private JComboBox imageScaling = null;
	private JTextField dbFontSize = null;
	private String ccodes[] = null;
	private String selectedCc = "FI";

	private String[] locas = null;
	private String[] dateCodes = null;
	private Suku owner = null;

	/**
	 * Instantiates a new settings dialog.
	 * 
	 * @param owner
	 *            the owner
	 * @throws SukuException
	 *             the suku exception
	 */
	public SettingsDialog(Suku owner) throws SukuException {
		super(owner, Resurses.getString("SETTINGS"), true);
		this.owner = owner;
		setLayout(null);
		int x = 20;
		int y = 20;
		int scaleImageIndex = 0;
		// SukuData exresp = Suku.kontroller.getSukuData("cmd=excel",
		// "page=languages");

		// ExcelBundle.getBundle("excel/FinFamily", "Report", new Locale("fi"));
		String[] locatexts = ExcelBundle.getLangNames();
		locas = ExcelBundle.getLangCodes();
		// Resurses.getString("LOCALIZAT_TEXTS").split(";");
		// locas = Resurses.getString("LOCALIZAT_CODES").split(";");
		String[] dateFormats = Resurses.getString("LOCALIZAT_DATEFORMATS")
				.split(";");
		dateCodes = Resurses.getString("LOCALIZAT_DATECODES").split(";");
		boolean openStreetMap = false;
		if (Suku.kontroller.getPref(owner, "USE_OPEN_STREETMAP", "false")
				.equals("true")) {
			openStreetMap = true;
		}

		String databaseViewFontSize = Suku.kontroller.getPref(owner,
				"DB_VIEW_FONTSIZE", "11");

		String scaleImageText = Suku.kontroller.getPref(owner, "SCALE_IMAGE",
				"0");
		if (scaleImageText != null) {
			scaleImageIndex = Integer.parseInt(scaleImageText);
		}

		JLabel lbl = new JLabel(Resurses.getString("SETTING_LOCALE"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 200, 20);

		y += 20;
		loca = new JComboBox(locatexts);
		getContentPane().add(loca);
		loca.setBounds(x, y, 200, 20);

		String prevloca = Suku.kontroller.getPref(owner, Resurses.LOCALE, "fi");
		int locaIndex = 0;
		for (int i = 0; i < locas.length; i++) {
			if (prevloca.equals(locas[i])) {
				locaIndex = i;
				break;
			}
		}
		loca.setSelectedIndex(locaIndex);

		useOpenStreetMap = new JCheckBox(
				Resurses.getString("USE_OPEN_STREETMAP"), openStreetMap);
		getContentPane().add(useOpenStreetMap);

		useOpenStreetMap.setBounds(x + 210, y, 200, 20);

		y += 20;
		lbl = new JLabel(Resurses.getString("SETTING_REPOLANG"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 200, 20);

		lbl = new JLabel(Resurses.getString("COUNTRY_DEFAULT"));
		getContentPane().add(lbl);
		lbl.setBounds(x + 210, y, 200, 20);

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

		SukuData countdata = Suku.kontroller.getSukuData("cmd=get",
				"type=countries");
		selectedCc = Resurses.getDefaultCountry();

		if (countdata.generalArray == null) {
			JOptionPane.showMessageDialog(this,
					Resurses.getString("COUNTRY_ERROR"),
					Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
		} else {
			int seleId = -1;
			ccodes = new String[countdata.generalArray.length];
			String countries[] = new String[countdata.generalArray.length];
			for (int i = 0; i < countdata.generalArray.length; i++) {
				String parts[] = countdata.generalArray[i].split(";");
				ccodes[i] = parts[0];
				if (ccodes[i].equals(selectedCc)) {
					seleId = i;
				}
				if (!parts[2].equals("null")) {
					countries[i] = parts[1] + " - " + parts[2];
				} else {
					countries[i] = parts[1];
				}
			}
			defaultCountryCode = new JComboBox(countries);
			getContentPane().add(defaultCountryCode);
			defaultCountryCode.setBounds(x + 210, y, 200, 20);
			if (seleId >= 0) {
				defaultCountryCode.setSelectedIndex(seleId);
			}

			lbl = new JLabel(Resurses.getString("IMAGE_SCALE_LABEL"));
			getContentPane().add(lbl);
			lbl.setBounds(x + 210, y + 60, 200, 20);

			String scales[] = new String[2];
			scales[0] = Resurses.getString("IMAGE_SCALE_NO");
			scales[1] = Resurses.getString("IMAGE_SCALE_JAVA");

			imageScaling = new JComboBox(scales);
			getContentPane().add(imageScaling);
			imageScaling.setBounds(x + 210, y + 84, 250, 20);
			if (scaleImageIndex >= 0) {
				imageScaling.setSelectedIndex(scaleImageIndex);
			}

		}
		y += 20;

		lbl = new JLabel(Resurses.getString("SETTING_DATEFORMAT"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 200, 20);
		lbl = new JLabel(Resurses.getString("SETTING_FONTSIZE"));
		getContentPane().add(lbl);
		lbl.setBounds(x + 210, y, 200, 20);

		y += 20;
		dateFormat = new JComboBox(dateFormats);
		getContentPane().add(dateFormat);
		dateFormat.setBounds(x, y, 200, 20);

		dbFontSize = new JTextField(databaseViewFontSize);
		getContentPane().add(dbFontSize);
		dbFontSize.setBounds(x + 210, y, 80, 20);

		prevloca = Suku.kontroller.getPref(owner, Resurses.DATEFORMAT, "FI");
		int dateIndex = 0;
		for (int i = 0; i < dateFormats.length; i++) {
			if (prevloca.equals(dateCodes[i])) {
				dateIndex = i;
			}
		}

		dateFormat.setSelectedIndex(dateIndex);

		JButton ok = new JButton(Resurses.OK);
		// this.ok.setDefaultCapable(true);
		getContentPane().add(ok);
		ok.setActionCommand(Resurses.OK);
		ok.addActionListener(this);
		ok.setBounds(330, 220, 100, 24);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width / 2 - 300, d.height / 2 - 150, 540, 300);
		setResizable(false);
		getRootPane().setDefaultButton(ok);

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

		if (cmd.equals(Resurses.OK)) {

			int newLoca = loca.getSelectedIndex();
			Suku.kontroller.putPref(owner, Resurses.LOCALE, locas[newLoca]);
			Resurses.setLanguage(locas[newLoca]);

			int newLang = repolang.getSelectedIndex();
			if (newLang >= 0) {
				Suku.kontroller.putPref(owner, Resurses.REPOLANG,
						Suku.getRepoLanguage(newLang, true));

			}

			int imageScaler = imageScaling.getSelectedIndex();
			Suku.kontroller.putPref(owner, "SCALE_IMAGE", "" + imageScaler);
			owner.setImageScalerIndex(imageScaler);
			int seleId = defaultCountryCode.getSelectedIndex();
			if (seleId >= 0) {
				selectedCc = ccodes[seleId];
			}

			try {
				Resurses.setDefaultCountry(selectedCc);
			} catch (SukuException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(),
						Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
			}

			int newDateIndex = dateFormat.getSelectedIndex();
			Suku.kontroller.putPref(owner, Resurses.DATEFORMAT,
					dateCodes[newDateIndex]);
			Resurses.setDateFormat(dateCodes[newDateIndex]);
			Utils.resetSukuModel();

			boolean openStreetMap = useOpenStreetMap.isSelected();
			Suku.kontroller.putPref(owner, "USE_OPEN_STREETMAP", ""
					+ openStreetMap);

			String fntSize = dbFontSize.getText();
			Suku.kontroller.putPref(owner, "DB_VIEW_FONTSIZE", fntSize);

			setVisible(false);
		}
	}

}
