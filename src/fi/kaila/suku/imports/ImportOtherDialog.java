package fi.kaila.suku.imports;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class ImportOtherDialog.
 */
public class ImportOtherDialog extends JDialog implements ActionListener,
		ListSelectionListener, PropertyChangeListener {

	/** */
	private static final long serialVersionUID = 1L;

	private JList scList = null;
	private JList scViews = null;
	private JButton copyAndComp;
	private JButton cancel;
	private JButton compToSchema;
	private JButton compLocal;
	private JCheckBox dates;
	private JCheckBox firstname;
	private JCheckBox patronym;
	private JCheckBox surname;
	private String selectedSchema = null;
	private String[] schemaList = null;
	private Vector<String> viewList = null;
	private JScrollPane viewScroll;
	private int[] viewIds;
	private String[] viewNames;
	private int selectedView = -1;
	private boolean wasOk = false;
	private String createdView = null;
	private int createdViewId = 0;
	private JProgressBar progressBar;
	private JLabel timeEstimate;
	private JLabel textContent;
	private String errorMessage = null;

	private static ImportOtherDialog runner = null;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner
	 * @throws SukuException
	 *             the suku exception
	 */
	public ImportOtherDialog(JFrame owner) throws SukuException {
		super(owner, Resurses.getString(Resurses.IMPORT_OTHER), true);
		runner = this;

		constructMe(true);

	}

	/**
	 * Gets the runner.
	 * 
	 * @return the dialog handle used for the progresBar
	 */
	public static ImportOtherDialog getRunner() {
		return runner;
	}

	private void constructMe(boolean allowNew) throws SukuException {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		// setBounds(d.width / 2 - 300, d.height / 2 - 200, 600, 400);
		setLayout(null);
		int y = 20;
		JPanel pna = new JPanel();
		pna.setLayout(null);
		getContentPane().add(pna);
		pna.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("SCHEMA_SELECT")));
		JLabel lbl;// = new JLabel(Resurses.getString("SCHEMA_SELECT"));
		// pna.add(lbl);
		// lbl.setBounds(10, y, 200, 20);

		SukuData dat = Suku.kontroller.getSukuData("cmd=schema", "type=get");
		String schema = dat.generalArray.length == 1 ? dat.generalArray[0]
				: null;
		SukuData schemas = Suku.kontroller.getSukuData("cmd=schema",
				"type=count");

		ArrayList<String> v = new ArrayList<String>();
		for (int i = 0; i < schemas.generalArray.length; i++) {
			if (!schemas.generalArray[i].equals(schema)) {
				v.add(schemas.generalArray[i]);
			}
		}

		schemaList = v.toArray(new String[0]);

		scList = new JList(schemaList);
		scList.addListSelectionListener(this);
		JScrollPane scroll = new JScrollPane(scList);
		pna.add(scroll);
		scroll.setBounds(10, y, 260, 100);

		y += 110;

		lbl = new JLabel(Resurses.getString("SELECT_VIEW"));
		pna.add(lbl);
		lbl.setBounds(10, y, 200, 20);
		y += 20;

		viewList = new Vector<String>();
		scViews = new JList(viewList);
		scList.addListSelectionListener(this);
		viewScroll = new JScrollPane(scViews);
		pna.add(viewScroll);
		viewScroll.setBounds(10, y, 260, 100);
		y += 110;

		textContent = new JLabel("");
		pna.add(textContent);
		this.textContent.setBounds(30, y, 340, 20);

		y += 30;

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(10, y, 260, 20);
		pna.add(this.progressBar);

		y += 20;
		timeEstimate = new JLabel("");
		pna.add(timeEstimate);
		timeEstimate.setBounds(30, y, 340, 20);

		y += 30;
		int yy = 20;
		JPanel pnb = new JPanel();
		pnb.setLayout(null);
		getContentPane().add(pnb);
		pnb.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("SCHEMA_COMPARE")));

		dates = new JCheckBox(Resurses.getString("SCHEMA_COMP_BDATES"), true);
		pnb.add(dates);
		dates.setBounds(20, yy, 340, 20);

		yy += 24;

		patronym = new JCheckBox(Resurses.getString("SCHEMA_COMP_PATRONYM"),
				true);
		pnb.add(patronym);
		patronym.setBounds(20, yy, 340, 20);

		yy += 24;
		surname = new JCheckBox(Resurses.getString("SCHEMA_COMP_SURNAME"), true);
		pnb.add(surname);
		surname.setBounds(20, yy, 340, 20);
		yy += 24;
		firstname = new JCheckBox(Resurses.getString("SCHEMA_COMP_FIRSTNAME"),
				true);
		pnb.add(firstname);
		firstname.setBounds(20, yy, 340, 20);

		yy += 30;
		this.compToSchema = new JButton(Resurses.getString("SCHEMA_COMP"));
		pnb.add(this.compToSchema);
		this.compToSchema.setBounds(20, yy, 150, 24);
		this.compToSchema.setActionCommand("OK");
		this.compToSchema.addActionListener(this);

		if (yy > y)
			y = yy;
		this.copyAndComp = new JButton(Resurses.getString("SCHEMA_COPY"));
		pna.add(this.copyAndComp);
		this.copyAndComp.setBounds(10, y, 128, 24);
		this.copyAndComp.setActionCommand("OK");
		this.copyAndComp.addActionListener(this);
		this.copyAndComp.setDefaultCapable(true);

		getRootPane().setDefaultButton(this.copyAndComp);

		this.compLocal = new JButton(Resurses.getString("SCHEMA_COMP_ONLY"));
		pna.add(this.compLocal);
		this.compLocal.setBounds(142, y, 128, 24);
		this.compLocal.setActionCommand("OK");
		this.compLocal.addActionListener(this);

		this.cancel = new JButton(Resurses.getString("CANCEL"));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(310, y + 10, 80, 24);
		this.cancel.setActionCommand("CANCEL");
		this.cancel.addActionListener(this);

		pna.setBounds(10, 10, 280, y + 40);

		pnb.setBounds(300, 10, 280, yy + 40);
		y += 100;
		setBounds(d.width / 2 - 300, d.height / 2 - 200, 600, y);

		this.setVisible(true);

	}

	/**
	 * Gets the result.
	 * 
	 * @return possible error result
	 */
	public String getResult() {
		return this.errorMessage;
	}

	/**
	 * Gets the schema.
	 * 
	 * @return the schema
	 */
	public String getSchema() {
		if (wasOk) {
			return selectedSchema;
		} else {
			return null;
		}
	}

	/**
	 * Gets the view id.
	 * 
	 * @return the view id
	 */
	public int getViewId() {

		if (wasOk) {
			if (selectedView >= 0 && selectedView < viewIds.length)
				return viewIds[selectedView];
		}
		return -1;
	}

	/**
	 * Gets the view name.
	 * 
	 * @return the view name
	 */
	public String getViewName() {
		if (wasOk && selectedView >= 0) {
			return viewNames[selectedView];
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg) {

		Object activator = arg.getSource();
		if (activator == null)
			return;

		ArrayList<String> parms = new ArrayList<String>();
		parms.add("cmd=compare");
		if (this.dates.isSelected()) {
			parms.add("dates=true");
		}
		if (this.surname.isSelected()) {
			parms.add("surname=true");
		}
		if (this.patronym.isSelected()) {
			parms.add("patronym=true");
		}

		if (this.firstname.isSelected()) {
			parms.add("firstname=true");
		}

		if (activator == this.copyAndComp) {
			if (selectedSchema == null) {
				JOptionPane.showMessageDialog(this,
						Resurses.getString("SCHEMA_NOT_SELECTED"));
				return;
			}
			selectedView = scViews.getSelectedIndex();
			wasOk = true;

			this.copyAndComp.setEnabled(false);

			// we create new instances as needed.
			Task task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();

			try {
				parms.add("view=" + createdViewId);
				parms.add("viewName=" + createdView);
				SukuData resp = Suku.kontroller.getSukuData(parms
						.toArray(new String[0]));

				if (resp.generalText != null) {
					JOptionPane.showMessageDialog(this,
							Resurses.getString("COMPARE_RESULT") + " "
									+ resp.generalText);
				} else {
					JOptionPane.showMessageDialog(this,
							Resurses.getString("COMPARE_RESULT_NONE"));
				}

			} catch (SukuException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
			setVisible(false);
			return;
		} else if (activator == this.compLocal) {
			try {

				SukuData resp = Suku.kontroller.getSukuData(parms
						.toArray(new String[0]));

				if (resp.generalText != null) {
					JOptionPane.showMessageDialog(this,
							Resurses.getString("COMPARE_RESULT") + " "
									+ resp.generalText);
					setVisible(false);
				} else {
					JOptionPane.showMessageDialog(this,
							Resurses.getString("COMPARE_RESULT_NONE"));
				}

			} catch (SukuException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());

			}

		} else if (activator == this.compToSchema) {
			if (selectedSchema == null) {

				JOptionPane.showMessageDialog(this,
						Resurses.getString("SCHEMA_NOT_SELECTED"));
				return;
			}
			selectedView = scViews.getSelectedIndex();
			wasOk = true;
			try {

				parms.add("schema=" + selectedSchema);
				if (getViewId() >= 0) {
					parms.add("view=" + getViewId());
					parms.add("viewName=" + viewNames[selectedView]);
				}

				SukuData resp = Suku.kontroller.getSukuData(parms
						.toArray(new String[0]));

				if (resp.generalText != null) {
					JOptionPane.showMessageDialog(this,
							Resurses.getString("COMPARE_RESULT") + " "
									+ resp.generalText);
					setVisible(false);
				} else {
					JOptionPane.showMessageDialog(this,
							Resurses.getString("COMPARE_RESULT_NONE"));
				}

			} catch (SukuException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());

			}

		} else if (activator == this.cancel) {
			wasOk = false;
			setVisible(false);
			return;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
	 * .ListSelectionEvent)
	 */
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
			viewList.clear();
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

				SukuData resp = Suku.kontroller.getSukuData(parms
						.toArray(new String[0]));
				createdView = resp.generalText;
				createdViewId = resp.resultPid;
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

}
