package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
	private static Logger logger = Logger.getLogger(SettingsDialog.class
			.getName());
	private JComboBox loca = null;
	private JComboBox repolang = null;
	private JComboBox dateFormat = null;
	private JCheckBox useOpenStreetMap = null;
	private JComboBox defaultCountryCode = null;
	private JComboBox imageScaling = null;
	private final JComboBox lookAndFeel;
	private JTextField dbFontSize = null;
	private final JButton graphVizSetup;
	private final JTextField graphVizPath;
	private final JButton imageMagickSetup;
	private final JTextField imageMagickPath;
	private final JComboBox serverUrl;
	private final String originUrl;
	private final String originLanguage;
	private String ccodes[] = null;
	private String selectedCc = "FI";
	private final String[] lfNames;
	private final UIManager.LookAndFeelInfo[] lafInfo;
	private String[] locas = null;
	private String[] dateCodes = null;
	private Suku owner = null;

	private Vector<String> urlvec = null;

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

		String[] locatexts = ExcelBundle.getLangNames();
		locas = ExcelBundle.getLangCodes();

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
		originLanguage = prevloca;
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

		if (Suku.kontroller.isWebStart()) {
			useOpenStreetMap.setEnabled(false);
		}

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
		SukuData countdata = null;
		try {
			countdata = Suku.kontroller
					.getSukuData("cmd=get", "type=countries");
			selectedCc = Resurses.getDefaultCountry();
		} catch (SukuException e) {
			countdata = new SukuData();
		}
		if (countdata.generalArray == null) {
			// JOptionPane.showMessageDialog(this,
			// Resurses.getString("COUNTRY_ERROR"),
			// Resurses.getString(Resurses.SUKU),
			// JOptionPane.ERROR_MESSAGE);
			ccodes = new String[1];
			ccodes[0] = "FI";
			String nulcountries[] = { "Finland" };

			defaultCountryCode = new JComboBox(nulcountries);
			getContentPane().add(defaultCountryCode);
			defaultCountryCode.setBounds(x + 210, y, 200, 20);

			defaultCountryCode.setSelectedIndex(0);

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
		}
		lbl = new JLabel(Resurses.getString("IMAGE_SCALE_LABEL"));
		getContentPane().add(lbl);
		lbl.setBounds(x + 210, y + 60, 200, 20);

		String scales[] = new String[5];
		scales[0] = Resurses.getString("IMAGE_SCALE_NO");
		scales[1] = Resurses.getString("IMAGE_SCALE_JAVA");
		scales[2] = Resurses.getString("IMAGE_SCALE_GT") + " 2";
		scales[3] = Resurses.getString("IMAGE_SCALE_GT") + " 3";
		scales[4] = Resurses.getString("IMAGE_SCALE_GT") + " 4";
		imageScaling = new JComboBox(scales);
		getContentPane().add(imageScaling);
		imageScaling.setBounds(x + 210, y + 84, 250, 20);
		if (scaleImageIndex >= 0) {
			imageScaling.setSelectedIndex(scaleImageIndex);
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

		y += 20;
		lbl = new JLabel(Resurses.getString("LOOK_AND_FEEL"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 200, 20);

		y += 24;

		String lfdef = Suku.kontroller.getPref(owner, "LOOK_AND_FEEL", "");

		lafInfo = UIManager.getInstalledLookAndFeels();
		int defLf = 0;
		lfNames = new String[lafInfo.length + 1];
		lfNames[0] = "";
		for (int i = 0; i < lafInfo.length; i++) {
			lfNames[i + 1] = lafInfo[i].getName();
			if (lfNames[i + 1].equalsIgnoreCase(lfdef)) {
				defLf = i + 1;
			}
		}

		lookAndFeel = new JComboBox(lfNames);
		getContentPane().add(lookAndFeel);
		lookAndFeel.setBounds(x, y, 200, 20);
		if (defLf > 0) {
			lookAndFeel.setSelectedIndex(defLf);
		}
		y += 24;
		lbl = new JLabel(Resurses.getString("SETTINGS_GRAPHVIZ"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 400, 20);
		y += 20;

		String grpath = Suku.kontroller.getPref(owner, "GRAPHVIZ", "");
		graphVizPath = new JTextField(grpath);
		graphVizPath.setBounds(x, y, 440, 20);
		graphVizPath.setEditable(false);
		getContentPane().add(graphVizPath);
		graphVizSetup = new JButton("...");
		graphVizSetup.setBounds(x + 440, y, 20, 20);
		graphVizSetup.setActionCommand("GRAPHVIZ");
		graphVizSetup.addActionListener(this);
		getContentPane().add(graphVizSetup);

		y += 24;
		lbl = new JLabel(Resurses.getString("SETTINGS_IMAGEMAGICK"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 400, 20);
		y += 20;

		String impath = Suku.kontroller.getPref(owner, "IMAGEMAGICK", "");
		imageMagickPath = new JTextField(impath);
		imageMagickPath.setBounds(x, y, 440, 20);
		imageMagickPath.setEditable(false);
		getContentPane().add(imageMagickPath);
		imageMagickSetup = new JButton("...");
		imageMagickSetup.setBounds(x + 440, y, 20, 20);
		imageMagickSetup.setActionCommand("IMAGEMAGICK");
		imageMagickSetup.addActionListener(this);
		getContentPane().add(imageMagickSetup);

		if (Suku.kontroller.isWebStart()) {
			graphVizSetup.setEnabled(false);
			imageMagickSetup.setEnabled(false);
		}

		y += 24;
		lbl = new JLabel(Resurses.getString("SETTINGS_SERVERURL"));
		getContentPane().add(lbl);
		lbl.setBounds(x, y, 400, 20);
		y += 20;

		urlvec = new Vector<String>();
		urlvec.add("");
		// urlvec.add(DEMO_URL);

		String curre = Suku.kontroller.getPref(owner, "SERVERURL", "");
		String old = Suku.kontroller.getPref(owner, "SERVEROLD", "");
		String prev = Suku.kontroller.getPref(owner, "SERVERPREV", "");
		originUrl = curre;
		if (curre.equals(old)) {
			old = prev;
			Suku.kontroller.putPref(owner, "SERVEROLD", old);
		}

		int selectedUrl = 0;
		if (!curre.isEmpty()) {
			Suku.kontroller.putPref(owner, "SERVERURL", curre);
			Suku.kontroller.putPref(owner, "SERVERPREV", curre);
			urlvec.add(curre);
			selectedUrl = urlvec.size() - 1;
		}
		if (!prev.isEmpty() && !prev.equals(curre)) {

			urlvec.add(prev);
		}

		if (!old.isEmpty() && !old.equals(curre) && !old.equals(prev)) {
			urlvec.add(old);
		}

		serverUrl = new JComboBox(urlvec);
		// serverUrl = new JTextField(url);
		serverUrl.setBounds(x, y, 440, 20);
		serverUrl.setEditable(true);
		serverUrl.setSelectedIndex(selectedUrl);
		getContentPane().add(serverUrl);
		// serverUrlSetup = new JButton("...");
		// serverUrlSetup.setBounds(x + 440, y, 20, 20);
		// serverUrlSetup.setActionCommand("SERVERURL");
		// serverUrlSetup.addActionListener(this);
		// getContentPane().add(serverUrlSetup);

		if (Suku.kontroller.isWebStart()) {
			graphVizSetup.setEnabled(false);
		}
		JButton ok = new JButton(Resurses.OK);
		// this.ok.setDefaultCapable(true);
		y += 36;
		getContentPane().add(ok);
		ok.setActionCommand(Resurses.OK);
		ok.addActionListener(this);
		ok.setBounds(330, y, 100, 24);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width / 2 - 300, d.height / 2 - 200, 540, 400);
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
		boolean askRestart = false;
		if (cmd == null)
			return;
		if (cmd.equals("GRAPHVIZ")) {

			JFileChooser chooser = new JFileChooser();

			chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter("exe"));
			chooser.setDialogTitle("Open exe file");

			if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
				Suku.kontroller.putPref(owner, "GRAPHVIZ", "");
				graphVizPath.setText("");
				owner.mToolsAuxGraphviz.setEnabled(false);
				return;
			}
			File f = chooser.getSelectedFile();
			if (f == null) {
				return;
			}
			String filename = f.getAbsolutePath();

			if (filename == null || filename.isEmpty()) {
				Suku.kontroller.putPref(owner, "GRAPHVIZ", "");
				graphVizPath.setText("");
				owner.mToolsAuxGraphviz.setEnabled(false);
				return;
			}

			Suku.kontroller.putPref(owner, "GRAPHVIZ", filename);
			graphVizPath.setText(filename);
			owner.mToolsAuxGraphviz.setEnabled(true);

		}
		if (cmd.equals("IMAGEMAGICK")) {

			JFileChooser chooser = new JFileChooser();

			chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter("exe"));
			chooser.setDialogTitle("Open exe file");

			if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
				Suku.kontroller.putPref(owner, "IMAGEMAGICK", "");
				imageMagickPath.setText("");
				return;
			}
			File f = chooser.getSelectedFile();
			if (f == null) {

				return;
			}
			String filename = f.getAbsolutePath();

			if (filename == null || filename.isEmpty()) {
				Suku.kontroller.putPref(owner, "IMAGEMAGICK", "");
				imageMagickPath.setText("");

				return;
			}

			Suku.kontroller.putPref(owner, "IMAGEMAGICK", filename);
			imageMagickPath.setText(filename);

		}
		if (cmd.equals(Resurses.OK)) {

			String input = (String) serverUrl.getSelectedItem();
			if (input == null) {
				input = "";
			}

			if (input.isEmpty()) {
				Suku.kontroller.putPref(owner, "SERVERURL", "");
				if (!originUrl.isEmpty()) {
					askRestart = true;
				}
			} else {

				URL url;
				String resp = null;
				try {
					url = new URL(input + "SukuServlet");
					HttpURLConnection uc = (HttpURLConnection) url
							.openConnection();

					int resu = uc.getResponseCode();

					if (resu == 200) {
						byte buff[] = new byte[1024];
						InputStream in = uc.getInputStream();
						int len = in.read(buff);
						resp = new String(buff, 0, len);
						uc.disconnect();

					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (resp != null && resp.toLowerCase().startsWith("finfamily")) {
					Suku.kontroller.putPref(owner, "SERVERURL", input);
					if (!input.equals(originUrl)) {
						askRestart = true;
					}
					// serverUrl.setText(input);
				} else {
					JOptionPane.showMessageDialog(this,
							Resurses.getString("SERVER_ERROR"),
							Resurses.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);
				}
			}
			int newLoca = loca.getSelectedIndex();
			Suku.kontroller.putPref(owner, Resurses.LOCALE, locas[newLoca]);
			if (!originLanguage.equals(locas[newLoca])) {
				askRestart = true;
			}

			int newLang = repolang.getSelectedIndex();
			if (newLang >= 0) {
				Suku.kontroller.putPref(owner, Resurses.REPOLANG,
						Suku.getRepoLanguage(newLang, true));
				Resurses.setLanguage(Suku.getRepoLanguage(newLang, true));
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
				owner.setStatus(e1.getMessage());
				// JOptionPane.showMessageDialog(this, e1.getMessage(),
				// Resurses.getString(Resurses.SUKU),
				// JOptionPane.ERROR_MESSAGE);
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

			String lf = lfNames[lookAndFeel.getSelectedIndex()];
			Suku.kontroller.putPref(owner, "LOOK_AND_FEEL", lf);

			int lfIdx = -1;
			for (int i = 0; i < lafInfo.length; i++) {
				if (lafInfo[i].getName().equalsIgnoreCase(lf)) {
					lfIdx = i;
					break;
				}
			}
			try {
				if (lfIdx < 0) {

					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} else {
					UIManager.setLookAndFeel(lafInfo[lfIdx].getClassName());
				}
				SwingUtilities.updateComponentTreeUI(owner);

			} catch (Exception e1) {
				logger.log(Level.WARNING, "look_and_feel", e1);

			}

			setVisible(false);
			if (askRestart) {
				JOptionPane.showMessageDialog(this,
						Resurses.getString("RESTART_FINFAMILY"),
						Resurses.getString(Resurses.SUKU),
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}
