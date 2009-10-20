/**
 * 
 */
package fi.kaila.suku.imports;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;

/**
 * 
 * <h1>Importing a Sukuohjelmisto 2004 backup file</h1>
 * 
 * <p>
 * The Suku 2004 backup file is an xml-file that contains all relevant
 * information in the Suku2004 database. The xml-file is usually packed in
 * gzipped format
 * </p>
 * 
 * <p>
 * This is a Dialog window that is located in UI side. It runs a Worker thread
 * to display progress of the import. The import is executed in server side by
 * {@link fi.kaila.suku.imports.Read2004XML }. Setting up the import is done
 * here.
 * </p>
 * 
 * @author Kalle
 * 
 */
public class Import2004Dialog extends JDialog implements ActionListener,
		PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String OK = "OK";
	private static final String CANCEL = "CANCEL";
	private JComboBox lista;
	private JLabel textContent;
	private JButton ok;
	private JButton cancel;

	private String langOk[];
	private String langS[];

	private String selectedLang = null;
	private String selectedOldLang = null;

	private SukuKontroller kontroller = null;

	private JProgressBar progressBar;
	private Task task;

	public static Import2004Dialog getRunner() {
		return runner;
	}

	private JFrame owner = null;
	private static Import2004Dialog runner = null;

	/**
	 * 
	 * Constructor takes {@link fi.kaila.suku.swing.Suku main program} and
	 * {@link fi.kaila.suku.kontroller.SukuKontroller kontroller interface} as
	 * parameters
	 * 
	 * @param owner
	 * @param kontroller
	 */
	public Import2004Dialog(JFrame owner, SukuKontroller kontroller) {
		super(owner, Resurses.getString("IMPORT"), true);
		this.owner = owner;
		runner = this;
		this.kontroller = kontroller;

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 200, d.height / 2 - 100, 400, 200);
		setLayout(null);
		int y = 20;

		JLabel lbl = new JLabel(Resurses.getString("IMPORT_LANGUAGE"));
		getContentPane().add(lbl);
		lbl.setBounds(20, y, 100, 20);

		// String deflan = Resurses.instance().getString("DEFAULT_LANGUAGE");
		String lanlist = Resurses.getString("IMPORT_LANGS");

		String apu[] = lanlist.split(";");
		this.langOk = new String[apu.length / 3];
		this.langS = new String[apu.length / 3];
		String langNames[] = new String[apu.length / 3];
		int j = 0;
		for (int i = 0; i < apu.length / 3; i++) {
			this.langOk[i] = apu[j++];
			this.langS[i] = apu[j++];
			langNames[i] = apu[j++];
		}

		this.lista = new JComboBox(langNames);
		getContentPane().add(this.lista);
		this.lista.setBounds(120, y, 200, 20);

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

		y += 30;
		this.ok = new JButton(Resurses.getString(OK));
		getContentPane().add(this.ok);
		this.ok.setBounds(60, y, 100, 30);
		this.ok.setActionCommand(OK);
		this.ok.addActionListener(this);
		this.ok.setDefaultCapable(true);
		getRootPane().setDefaultButton(this.ok);

		this.cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(200, y, 100, 30);
		this.cancel.setActionCommand(CANCEL);
		this.cancel.addActionListener(this);

		this.task = null;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(OK)) {
			int indx = this.lista.getSelectedIndex();

			this.selectedLang = this.langOk[indx];
			this.selectedOldLang = this.langS[indx];

			this.ok.setEnabled(false);

			// we create new instances as needed.
			task = new Task();
			task.lang = this.selectedOldLang;
			task.addPropertyChangeListener(this);
			task.execute();

			// setVisible(false);
		}
		if (cmd.equals(CANCEL)) {
			if (this.task == null) {
				this.selectedLang = null;
				setVisible(false);
			} else {
				this.task.cancel(true);
			}
		}

	}

	// /**
	// * @return true if language selected
	// */
	// public boolean isOK(){
	// if (this.selectedLang != null){
	// return true;
	// }
	// return false;
	// }

	// /**
	// * Suku 2004 data may consist of special characters for defining text
	// * e.g. in different languages. In Suku11 database the different languages
	// are
	// * stored in separate language pages. The user must choose the main
	// language
	// * during import to be stored with the main data. Other languages will be
	// * stored in the language pages
	// *
	// * @return selected language
	// */
	// public String getSelectedLang(){
	// return this.selectedLang;
	// }
	//
	//	
	// /**
	// * @return selected old language
	// */
	// public String getSelected2004Lang(){
	// return this.selectedOldLang;
	// }

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
				kontroller.getSukuData("cmd=import2004", "lang=" + lang);
			} catch (SukuException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(owner, Resurses
						.getString(Resurses.IMPORT_SUKU)
						+ ":" + e.getMessage());

				return null;
			}

			// Random random = new Random();
			// int progress = 0;
			// //Initialize progress property.
			// setProgress(0);
			// while (progress < 100) {
			// //Sleep for up to one second.
			// try {
			// Thread.sleep(random.nextInt(1000));
			// } catch (InterruptedException ignore) {}
			// //Make random progress.
			// progress += random.nextInt(10);
			// setProgress(Math.min(progress, 100));
			// firePropertyChange("progress", "old", "" + progress + ";prgii[" +
			// progress + "]");
			// }
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setVisible(false);
			// startButton.setEnabled(true);
			// setCursor(null); //turn off the wait cursor
			// taskOutput.append("Done!\n");
		}
	}

	/**
	 * The runner is the progressbar on the import dialog. Set new values to the
	 * progress bar using this command
	 * 
	 * the text may be split in two parts seperated by ";"
	 * 
	 * if the text is divided then part before ; must be an integer number
	 * between 0-100 for the progress bar. Text behind ; or if ; does not exist
	 * is diplayed above the progress bar
	 * 
	 * 
	 * @param juttu
	 */
	public void setRunnerValue(String juttu) {
		String[] kaksi = juttu.split(";");
		if (kaksi.length >= 2) {
			int progress = 0;
			try {
				progress = Integer.parseInt(kaksi[0]);
			} catch (NumberFormatException ne) {
				textContent.setText(juttu);
				progressBar.setIndeterminate(true);
				progressBar.setValue(0);
				return;
			}

			progressBar.setValue(progress);
			textContent.setText(kaksi[1]);

		} else {
			textContent.setText(juttu);
			// int progre = progressBar.getValue();
			// if (progre > 95) {
			// progre=0;
			//	        		
			// } else {
			// progre++;
			// }
			progressBar.setIndeterminate(true);
			progressBar.setValue(0);
		}
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
