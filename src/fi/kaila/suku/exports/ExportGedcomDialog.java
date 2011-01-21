package fi.kaila.suku.exports;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.util.SukuSuretyField;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Export as a Gedcom file.
 * 
 * @author Kalle
 */
public class ExportGedcomDialog extends JDialog implements ActionListener,
		PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String OK = "OK";
	private static final String CANCEL = "CANCEL";

	private final JLabel textContent;
	private final JButton ok;
	private final JButton cancel;
	private final JTextField fileName;
	private final JLabel timeEstimate;
	private final JComboBox viewList;
	private String[] viewArray = null;
	private String viewName = null;
	private final JComboBox langList;
	private final JComboBox charsetList;
	private final JCheckBox includeImages;
	private final SukuSuretyField surety;
	private String zipName = "nemo";
	private String langCode = null;
	private String langName = null;
	private String[] langCodes = null;
	private String[] langNames = null;
	private final String[] charsetNames = { "", "Ascii", "Ansel", "UTF-8",
			"UTF-16" };
	private final JProgressBar progressBar;
	private Task task = null;

	/**
	 * Gets the runner.
	 * 
	 * @return the dialog handle used for the progresBar
	 */
	public static ExportGedcomDialog getRunner() {
		return runner;
	}

	private Suku owner = null;
	private static ExportGedcomDialog runner = null;
	private final SukuData gedcomResult = null;
	private String dbName = null;

	/**
	 * Constructor takes {@link fi.kaila.suku.swing.Suku main program} and
	 * 
	 * @param owner
	 *            the owner
	 * @param dbName
	 *            the db name
	 * @param zipName
	 *            the zip name
	 * @throws SukuException
	 *             the suku exception
	 */
	public ExportGedcomDialog(Suku owner, String dbName, String zipName)
			throws SukuException {
		super(owner, Resurses.getString("EXPORT"), true);
		this.owner = owner;
		runner = this;
		this.dbName = dbName;
		this.zipName = zipName;
		setLayout(null);
		int y = 20;

		JLabel lbl = new JLabel(Resurses.getString("EXPORT_ZIPFILE"));

		getContentPane().add(lbl);
		lbl.setBounds(30, y, 340, 20);
		lbl = new JLabel(Resurses.getString("EXPORT_LANG"));
		getContentPane().add(lbl);
		lbl.setBounds(400, y, 160, 20);
		y += 20;

		fileName = new JTextField(zipName);
		fileName.setEditable(false);
		getContentPane().add(fileName);
		fileName.setBounds(30, y, 340, 20);

		langNames = new String[Suku.getRepoLanguageCount() + 1];
		langCodes = new String[Suku.getRepoLanguageCount() + 1];
		langNames[0] = Resurses.getString("EXPORT_DEFAULT");
		for (int i = 1; i < langCodes.length; i++) {
			langCodes[i] = Suku.getRepoLanguage(i - 1, true);
			langNames[i] = Suku.getRepoLanguage(i - 1, false);

		}

		langList = new JComboBox(langNames);
		getContentPane().add(langList);
		langList.setBounds(400, y, 160, 20);

		y += 20;
		lbl = new JLabel(Resurses.getString("EXPORT_VIEW"));
		getContentPane().add(lbl);
		lbl.setBounds(30, y, 340, 20);

		lbl = new JLabel(Resurses.getString("EXPORT_CHARSET"));
		getContentPane().add(lbl);
		lbl.setBounds(400, y, 160, 20);

		y += 20;
		SukuData vlist = Suku.kontroller.getSukuData("cmd=viewlist");
		String[] lista = vlist.generalArray;
		this.viewList = new JComboBox();
		getContentPane().add(this.viewList);
		this.viewList.setBounds(30, y, 340, 20);

		charsetList = new JComboBox(charsetNames);
		charsetList.setSelectedIndex(3);
		getContentPane().add(charsetList);
		charsetList.setBounds(400, y, 160, 20);

		viewArray = lista;
		viewList.addItem(Resurses.getString("EXPORT_ALL"));
		for (int i = 0; i < viewArray.length; i++) {
			String[] pp = viewArray[i].split(";");
			if (pp.length > 1) {
				viewList.addItem(pp[1]);
			}
		}
		y += 20;

		textContent = new JLabel("");
		getContentPane().add(textContent);
		this.textContent.setBounds(30, y, 340, 20);
		lbl = new JLabel(Resurses.getString("EXPORT_SURETY"));
		getContentPane().add(lbl);
		lbl.setBounds(400, y, 340, 20);

		y += 20;
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(30, y, 340, 20);
		getContentPane().add(this.progressBar);

		surety = new SukuSuretyField();
		this.surety.setBounds(400, y, 160, 20);
		getContentPane().add(this.surety);

		y += 30;
		timeEstimate = new JLabel("");
		getContentPane().add(timeEstimate);
		timeEstimate.setBounds(30, y, 340, 20);

		includeImages = new JCheckBox(Resurses.getString("EXPORT_IMAGES"));
		getContentPane().add(includeImages);
		includeImages.setBounds(400, y, 340, 20);

		y += 40;
		this.ok = new JButton(Resurses.getString(OK));
		getContentPane().add(this.ok);
		this.ok.setBounds(120, y, 100, 24);
		this.ok.setActionCommand(OK);
		this.ok.addActionListener(this);
		this.ok.setDefaultCapable(true);
		getRootPane().setDefaultButton(this.ok);

		this.cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(240, y, 100, 24);
		this.cancel.setActionCommand(CANCEL);
		this.cancel.addActionListener(this);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 320, d.height / 2 - 100, 640, y + 100);

	}

	/**
	 * Gets the result.
	 * 
	 * @return failed gedcom lines
	 */
	public String[] getResult() {
		String[] resu;

		if (gedcomResult == null) {
			if (errorMessage != null) {
				resu = new String[1];
				resu[0] = errorMessage;
			} else {
				resu = new String[0];
			}
			return resu;
		}
		if (gedcomResult.generalArray == null) {
			if (errorMessage != null) {
				resu = new String[1];
				resu[0] = errorMessage;
			} else {
				resu = new String[0];
				resu[0] = "generalArray missing";
			}
			return resu;
		}
		if (errorMessage != null) {
			resu = new String[gedcomResult.generalArray.length + 1];
			resu[0] = errorMessage;
			for (int i = 0; i < gedcomResult.generalArray.length; i++) {
				resu[i + 1] = gedcomResult.generalArray[i];
			}
			return resu;
		}
		return gedcomResult.generalArray;
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
		if (cmd.equals(OK)) {

			this.ok.setEnabled(false);

			// we create new instances as needed.
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();

			// setVisible(false);
		}
		if (cmd.equals(CANCEL)) {
			if (task != null) {
				this.cancel.setEnabled(false);
			} else {
				setVisible(false);
			}
			isCancelled = true;

		}

	}

	/**
	 * The Class Task.
	 */
	class Task extends SwingWorker<Void, Void> {

		/*
		 * Main task. Executed in background thread.
		 */
		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		public Void doInBackground() {

			// Initialize progress property.
			setProgress(0);
			setRunnerValue(Resurses.getString("EXPORT_INITIALIZING"));

			try {

				if (fileName.getText().length() > 0) {
					Vector<String> v = new Vector<String>();
					v.add("cmd=create");
					v.add("type=gedcom");
					v.add("file=" + zipName);
					v.add("db=" + dbName);
					int listIdx = viewList.getSelectedIndex();

					if (listIdx > 0) {
						String parts[] = viewArray[listIdx - 1].split(";");
						int viewId = Integer.parseInt(parts[0]);
						if (parts.length > 1) {
							viewName = parts[1];
						}
						v.add("viewId=" + viewId);
					}

					if (langList.getSelectedIndex() > 0) {
						langCode = langCodes[langList.getSelectedIndex()];
						langName = langNames[langList.getSelectedIndex()];
					}
					if (langCode != null) {
						v.add("lang=" + langCode);
					}

					int suretylevel = surety.getSurety();
					v.add("surety=" + suretylevel);
					int charidx = charsetList.getSelectedIndex();

					if (charidx < 0) {
						charidx = 0;
					}
					if (includeImages.isSelected()) {
						v.add("images=true");
					}
					v.add("charid=" + charidx);
					String[] auxes = v.toArray(new String[0]);
					SukuData resp = Suku.kontroller.getSukuData(auxes);

					String tekst = "GEDCOM EXPORT";

					byte[] buffi = null;
					if (resp.buffer != null) {
						buffi = resp.buffer;
					} else {
						buffi = tekst.getBytes();
					}
					if (Suku.kontroller.isWebStart()) {
						ByteArrayInputStream in = new ByteArrayInputStream(
								buffi);

						Suku.kontroller.saveFile("zip", in);
					} else {
						try {
							OutputStream fos = Suku.kontroller
									.getOutputStream();
							fos.write(buffi);
							fos.close();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null,
									Resurses.getString("EXPORT_GEDCOM") + ":"
											+ e.getMessage());
						}
						if (resp.resu != null) {
							JOptionPane.showMessageDialog(owner,
									Resurses.getString("EXPORT_GEDCOM") + ":"
											+ resp.resu);
						}
					}
				}
			} catch (SukuException e) {
				e.printStackTrace();
				errorMessage = e.getMessage();
			}
			setVisible(false);
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();

		}
	}

	/**
	 * The runner is the progress bar on the import dialog. Set new values to
	 * the progress bar using this command
	 * 
	 * the text may be split in two parts separated by ";"
	 * 
	 * if the text is divided then part before ; must be an integer number
	 * between 0-100 for the progress bar. Text behind ; or if ; does not exist
	 * is displayed above the progress bar
	 * 
	 * @param juttu
	 *            the juttu
	 * @return true if cancel command has been issued
	 */
	public boolean setRunnerValue(String juttu) {
		String[] kaksi = juttu.split(";");
		if (kaksi.length >= 2) {
			int progress = 0;
			try {
				progress = Integer.parseInt(kaksi[0]);

				if (progress == 0) {
					startTime = System.currentTimeMillis();
					timerText = Resurses.getString("EXPORT_TIME_LEFT");
					showCounter = 10;
				}

			} catch (NumberFormatException ne) {
				textContent.setText(juttu);
				progressBar.setIndeterminate(true);
				progressBar.setValue(0);
				timeEstimate.setText("Cancelled");
				return isCancelled;
			}
			progressBar.setIndeterminate(false);
			progressBar.setValue(progress);
			textContent.setText(kaksi[1]);
			showCounter--;
			if (progress > 0 && showCounter < 0 && timerText != null) {
				showCounter = 10;
				long nowTime = System.currentTimeMillis();
				long usedTime = nowTime - startTime;
				long estimatedDuration = (usedTime / progress) * 100;
				long restShow = estimatedDuration - usedTime;
				// long restShow = usedTime * (100 - progress);
				restShow = restShow / 1000;
				String timeType = " s";
				if (restShow > 180) {
					timeType = " min";
					restShow = restShow / 60;
				}
				String showTime = timerText + " :" + restShow + timeType;
				if (!timeEstimate.getText().equals(showTime)) {
					timeEstimate.setText(showTime);
				}
			}
		} else {
			textContent.setText(juttu);

			progressBar.setIndeterminate(true);
			progressBar.setValue(0);
			timeEstimate.setText("");
		}
		return isCancelled;
	}

	private String errorMessage = null;
	private boolean isCancelled = false;
	private long startTime = 0;
	private String timerText = null;
	private int showCounter = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			String juttu = evt.getNewValue().toString();
			String[] kaksi = juttu.split(";");
			if (kaksi.length >= 2) {
				int progress = Integer.parseInt(kaksi[0]);
				progressBar.setIndeterminate(false);
				progressBar.setValue(progress);
				textContent.setText(kaksi[1]);
			} else {

				textContent.setText(juttu);
				int progre = progressBar.getValue();
				if (progre > 95) {
					progre = 0;

				} else {
					progre++;
				}
				progressBar.setIndeterminate(true);
				progressBar.setValue(progre);
			}
		}
	}

	/**
	 * Gets the lang.
	 * 
	 * @param asCode
	 *            to return language code. false = language name
	 * @return the langCode
	 */
	public String getLang(boolean asCode) {
		if (asCode) {
			return langCode;
		}
		return langName;

	}

	/**
	 * Gets the view name.
	 * 
	 * @return the selected view name
	 */
	public String getViewName() {
		return viewName;

	}

}
