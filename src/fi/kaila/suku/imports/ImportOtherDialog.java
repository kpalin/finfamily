package fi.kaila.suku.imports;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

public class ImportOtherDialog extends JDialog implements ActionListener,
		ListSelectionListener, PropertyChangeListener {

	/** */
	private static final long serialVersionUID = 1L;

	private JList scList = null;
	private JList scViews = null;
	private JButton ok;
	private JButton cancel;
	private String selectedSchema = null;
	private String[] schemaList = null;
	private Vector<String> viewList = null;
	private JScrollPane viewScroll;
	private int[] viewIds;
	private String[] viewNames;
	private int selectedView = -1;
	private boolean wasOk = false;

	private JProgressBar progressBar;
	private Task task = null;
	private JLabel timeEstimate;
	private JLabel textContent;
	private String errorMessage = null;

	private static ImportOtherDialog runner = null;

	/**
	 * Constructor
	 * 
	 * @param owner
	 * @throws SukuException
	 */
	public ImportOtherDialog(JFrame owner) throws SukuException {
		super(owner, Resurses.getString(Resurses.IMPORT_OTHER), true);
		this.runner = this;

		constructMe(true);

	}

	/**
	 * @return the dialog handle used for the progresBar
	 */
	public static ImportOtherDialog getRunner() {
		return runner;
	}

	private void constructMe(boolean allowNew) throws SukuException {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 150, d.height / 2 - 200, 300, 400);
		setLayout(null);
		int y = 10;

		JLabel lbl = new JLabel(Resurses.getString("SCHEMA_SELECT"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 200, 20);
		y += 20;

		SukuData dat = Suku.kontroller.getSukuData("cmd=schema", "type=get");
		String schema = dat.generalArray.length == 1 ? dat.generalArray[0]
				: null;
		SukuData schemas = Suku.kontroller.getSukuData("cmd=schema",
				"type=count");

		Vector<String> v = new Vector<String>();
		for (int i = 0; i < schemas.generalArray.length; i++) {
			if (!schemas.generalArray[i].equals(schema)) {
				v.add(schemas.generalArray[i]);
			}
		}

		schemaList = v.toArray(new String[0]);

		scList = new JList(schemaList);
		scList.addListSelectionListener(this);
		JScrollPane scroll = new JScrollPane(scList);
		getContentPane().add(scroll);
		scroll.setBounds(10, y, 260, 100);

		y += 110;

		lbl = new JLabel(Resurses.getString("SELECT_VIEW"));
		getContentPane().add(lbl);
		lbl.setBounds(10, y, 200, 20);
		y += 20;

		viewList = new Vector<String>();
		scViews = new JList(viewList);
		scList.addListSelectionListener(this);
		viewScroll = new JScrollPane(scViews);
		getContentPane().add(viewScroll);
		viewScroll.setBounds(10, y, 260, 100);
		y += 110;

		textContent = new JLabel("");
		getContentPane().add(textContent);
		this.textContent.setBounds(30, y, 340, 20);

		y += 30;

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(10, y, 260, 20);
		getContentPane().add(this.progressBar);

		y += 20;
		timeEstimate = new JLabel("");
		getContentPane().add(timeEstimate);
		timeEstimate.setBounds(30, y, 340, 20);

		y += 30;

		this.ok = new JButton(Resurses.getString("OK"));
		getContentPane().add(this.ok);
		this.ok.setBounds(30, y, 80, 24);
		this.ok.setActionCommand("OK");
		this.ok.addActionListener(this);
		this.ok.setDefaultCapable(true);

		getRootPane().setDefaultButton(this.ok);

		this.cancel = new JButton(Resurses.getString("CANCEL"));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(120, y, 80, 24);
		this.cancel.setActionCommand("CANCEL");
		this.cancel.addActionListener(this);
		getRootPane().setDefaultButton(ok);
		y += 80;
		setBounds(d.width / 2 - 150, d.height / 2 - 200, 300, y);

		this.setVisible(true);

	}

	/**
	 * 
	 * @return possible error result
	 */
	public String getResult() {
		return this.errorMessage;
	}

	public String getSchema() {
		if (wasOk) {
			return selectedSchema;
		} else {
			return null;
		}
	}

	public int getViewId() {
		if (wasOk) {
			if (selectedView >= 0 && selectedView < viewIds.length)
				return viewIds[selectedView];
		}
		return -1;
	}

	public String getViewName() {
		if (wasOk && selectedView >= 0) {
			return viewNames[selectedView];
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent arg) {

		Object activator = arg.getSource();
		if (activator == null)
			return;
		if (activator == this.ok) {
			selectedView = scViews.getSelectedIndex();
			wasOk = true;

			this.ok.setEnabled(false);

			// we create new instances as needed.
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();

			return;
		}
		if (activator == this.cancel) {
			wasOk = false;
			setVisible(false);
			return;
		}

	}

	@Override
	public void valueChanged(ListSelectionEvent arg) {

		String schema = schemaList[scList.getSelectedIndex()];
		if (schema.equals(selectedSchema))
			return;
		selectedSchema = schema;

		scViews.setSelectedIndices(new int[0]);
		try {
			SukuData views = Suku.kontroller.getSukuData("cmd=viewlist",
					"schema=" + selectedSchema);
			viewIds = new int[views.generalArray.length];
			viewNames = new String[views.generalArray.length];
			viewList.removeAllElements();
			for (int i = 0; i < views.generalArray.length; i++) {
				String[] parts = views.generalArray[i].split(";");
				if (parts.length == 2) {
					viewIds[i] = Integer.parseInt(parts[0]);
					viewNames[i] = parts[1];
					viewList.add(parts[1]);
				}
			}
			this.scViews.updateUI();
			this.viewScroll.updateUI();
		} catch (SukuException e) {

			e.printStackTrace();
		}

	}

	class Task extends SwingWorker<Void, Void> {

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			System.out.println("Alkaa se");
			// Initialize progress property.
			setProgress(0);
			setRunnerValue("Aloitetaan tuonti");

			try {
				Vector<String> parms = new Vector<String>();
				parms.add("cmd=import");
				parms.add("type=other");
				parms.add("schema=" + selectedSchema);
				if (getViewId() >= 0) {
					parms.add("view=" + getViewId());
					parms.add("viewName=" + viewNames[selectedView]);
				}

				// SukuData resp =
				Suku.kontroller.getSukuData(parms.toArray(new String[0]));

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

	private boolean isCancelled = false;
	private long startTime = 0;
	private String timerText = null;
	private int showCounter = 0;

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

				if (progress == 0) {
					startTime = System.currentTimeMillis();
					timerText = Resurses.getString("IMPORT_TIME_LEFT");
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
