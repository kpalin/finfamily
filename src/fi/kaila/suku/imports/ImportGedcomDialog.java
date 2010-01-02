package fi.kaila.suku.imports;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * <h1>Import gedom-file</h1>
 * 
 * 
 * @author Kalle
 * 
 */

public class ImportGedcomDialog extends JDialog implements ActionListener,
		PropertyChangeListener {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	private static final String OK = "OK";
	private static final String CANCEL = "CANCEL";

	private JLabel textContent;
	private JButton ok;
	private JButton cancel;
	private JTextField fileName;

	private String selectedOldLang = null;

	private SukuKontroller kontroller = null;

	private JProgressBar progressBar;
	private Task task = null;

	/**
	 * @return the dialog handle used for the progresBar
	 */
	public static ImportGedcomDialog getRunner() {
		return runner;
	}

	private Suku owner = null;
	private static ImportGedcomDialog runner = null;

	private SukuData gedcomResult = null;

	/**
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

	/**
	 * 
	 * Constructor takes {@link fi.kaila.suku.swing.Suku main program} and
	 * 
	 * @param owner
	 * @param dbName
	 * @throws SukuException
	 */
	public ImportGedcomDialog(Suku owner, String dbName) throws SukuException {
		super(owner, Resurses.getString("IMPORT"), true);
		this.owner = owner;
		runner = this;
		this.kontroller = Suku.kontroller;

		setLayout(null);
		int y = 20;

		JLabel lbl = new JLabel(Resurses.getString("GEDCOM_FILE"));
		getContentPane().add(lbl);
		lbl.setBounds(30, y, 340, 20);

		y += 20;

		fileName = new JTextField(dbName);
		fileName.setEditable(false);
		getContentPane().add(fileName);
		fileName.setBounds(30, y, 340, 20);
		y += 30;

		textContent = new JLabel("");
		getContentPane().add(textContent);
		this.textContent.setBounds(30, y, 340, 20);

		y += 30;

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(30, y, 340, 20);
		getContentPane().add(this.progressBar);

		y += 40;
		this.ok = new JButton(Resurses.getString(OK));
		getContentPane().add(this.ok);
		this.ok.setBounds(80, y, 100, 24);
		this.ok.setActionCommand(OK);
		this.ok.addActionListener(this);
		this.ok.setDefaultCapable(true);
		getRootPane().setDefaultButton(this.ok);

		this.cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(200, y, 100, 24);
		this.cancel.setActionCommand(CANCEL);
		this.cancel.addActionListener(this);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 200, d.height / 2 - 100, 400, y + 100);

		SukuData resp;

		resp = Suku.kontroller.getSukuData("cmd=unitCount");

		if (resp.resuCount > 0) {

			int answer = JOptionPane.showConfirmDialog(this, Resurses
					.getString("DATABASE_NOT_EMPTY")
					+ " "
					+ resp.resuCount
					+ " "
					+ Resurses.getString("DELETE_DATA_OK"), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			if (answer == 1) {
				throw new SukuException(Resurses
						.getString("DATABASE_NOT_EMPTY"));

			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(OK)) {

			this.ok.setEnabled(false);

			// we create new instances as needed.
			task = new Task();
			task.lang = this.selectedOldLang;
			task.addPropertyChangeListener(this);
			task.execute();

			// setVisible(false);
		}
		if (cmd.equals(CANCEL)) {

			this.cancel.setEnabled(false);
			isCancelled = true;

		}

	}

	class Task extends SwingWorker<Void, Void> {

		String lang = null;

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {

			// Initialize progress property.
			setProgress(0);
			setRunnerValue("Luodaan tietokanta");

			try {

				kontroller.getSukuData("cmd=initdb");

				setRunnerValue(Resurses.getString("IMPORT_PAIKAT"));
				kontroller.getSukuData("cmd=excel",
						"path=resources/excel/PaikatExcel.xls",
						"page=coordinates");
				setRunnerValue(Resurses.getString("IMPORT_TYPES"));
				kontroller.getSukuData("cmd=excel",
						"path=resources/excel/TypesExcel.xls", "page=types");
				kontroller.getSukuData("cmd=excel",
						"path=resources/excel/TextsExcel.xls", "page=texts");

				if (fileName.getText().length() > 0) {
					SukuData resp = kontroller.getSukuData("cmd=importGedcom",
							"db=" + fileName.getText());
					gedcomResult = resp;
					if (resp.resu != null) {
						JOptionPane.showMessageDialog(owner, Resurses
								.getString("IMPORT_GEDCOM")
								+ ":" + resp.resu);
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
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			// setVisible(false);

			// startButton.setEnabled(true);
			// setCursor(null); //turn off the wait cursor
			// taskOutput.append("Done!\n");
		}
	}

	private String errorMessage = null;
	private boolean isCancelled = false;

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
	 * 
	 * @param juttu
	 * @return true if cancel command has been issued
	 */
	public boolean setRunnerValue(String juttu) {
		String[] kaksi = juttu.split(";");
		if (kaksi.length >= 2) {
			int progress = 0;
			try {
				progress = Integer.parseInt(kaksi[0]);
			} catch (NumberFormatException ne) {
				textContent.setText(juttu);
				progressBar.setIndeterminate(true);
				progressBar.setValue(0);
				return isCancelled;
			}
			progressBar.setIndeterminate(false);
			progressBar.setValue(progress);
			textContent.setText(kaksi[1]);

		} else {
			textContent.setText(juttu);

			progressBar.setIndeterminate(true);
			progressBar.setValue(0);
		}
		return isCancelled;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
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
}
