package fi.kaila.suku.exports;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class ExportFamilyDatabaseDialog.
 */
public class ExportFamilyDatabaseDialog extends JDialog implements
		ActionListener, PropertyChangeListener {

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

	private String dbName = null;
	private String zipName = null;
	private Suku owner = null;
	private static ExportFamilyDatabaseDialog runner = null;
	private final JLabel timeEstimate;
	private final JProgressBar progressBar;

	private Task task = null;

	/**
	 * Instantiates a new export family database dialog.
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
	public ExportFamilyDatabaseDialog(Suku owner, String dbName, String zipName)
			throws SukuException {
		super(owner, Resurses.getString("EXPORT"), true);
		this.owner = owner;
		runner = this;
		this.dbName = dbName;
		this.zipName = zipName;
		setLayout(null);
		int y = 20;

		fileName = new JTextField(zipName);
		fileName.setEditable(false);
		getContentPane().add(fileName);
		fileName.setBounds(30, y, 340, 20);

		y += 20;

		textContent = new JLabel("");
		getContentPane().add(textContent);
		this.textContent.setBounds(30, y, 340, 20);

		y += 20;
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(30, y, 340, 20);
		getContentPane().add(this.progressBar);
		y += 30;
		timeEstimate = new JLabel("");
		getContentPane().add(timeEstimate);
		timeEstimate.setBounds(30, y, 340, 20);
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

		setBounds(d.width / 2 - 300, d.height / 2 - 100, 600, y + 100);

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
	 * The Class Task.
	 */
	class Task extends SwingWorker<Void, Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Void doInBackground() throws Exception {
			// Initialize progress property.
			setProgress(0);
			setRunnerValue(Resurses.getString("EXPORT_INITIALIZING"));

			try {

				if (fileName.getText().length() > 0) {
					Vector<String> v = new Vector<String>();
					v.add("cmd=create");
					v.add("type=backup");
					v.add("file=" + zipName);
					v.add("db=" + dbName);

					String[] auxes = v.toArray(new String[0]);
					SukuData resp = Suku.kontroller.getSukuData(auxes);

					String tekst = "EXPORT_BACKUP";

					byte[] buffi = null;
					if (resp.buffer != null) {
						buffi = resp.buffer;
					} else {
						buffi = tekst.getBytes();
					}
					ByteArrayInputStream in = new ByteArrayInputStream(buffi);

					Suku.kontroller.saveFile("zip", in);

				}
			} catch (SukuException e) {
				e.printStackTrace();
				errorMessage = e.getMessage();
				JOptionPane.showMessageDialog(owner,
						Resurses.getString("EXPORT_BACKUP") + ":"
								+ errorMessage);

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
	 * Gets the runner.
	 * 
	 * @return the runner
	 */
	public static ExportFamilyDatabaseDialog getRunner() {
		return runner;
	}

}
