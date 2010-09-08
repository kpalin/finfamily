package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesModel;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * This dialog is now defining notice order possible other tasks will be done
 * here later.
 * 
 * @author Kalle
 */
public class ToolsDialog extends JDialog implements ActionListener,
		PropertyChangeListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger
			.getLogger(ToolsDialog.class.getName());
	private static final String SORT = "NOTICES.SORT";
	private static final String CANCEL = "CANCEL";

	private JLabel textContent;
	private JButton ok;
	private JButton cancel;

	private JScrollPane kokoScroll;
	private JList koko;

	private JScrollPane settiScroll;
	private JList setti;

	private JProgressBar progressBar;
	private Task task;

	private JTextField name;
	private JTextField address;
	private JTextField postalCode;
	private JTextField postOffice;
	private JTextField state;
	private JTextField country;
	private JTextField email;
	private JTextField web;
	private JTextArea text;
	private JButton update;

	/**
	 * Gets the runner.
	 * 
	 * @return handle to this instance
	 */
	public static ToolsDialog getRunner() {
		return runner;
	}

	/** The koko map. */
	HashMap<String, String> kokoMap = new HashMap<String, String>();

	/** The setti map. */
	HashMap<String, String> settiMap = new HashMap<String, String>();

	/** The koko tags. */
	Vector<String> kokoTags = new Vector<String>();

	/** The koko lista. */
	Vector<String> kokoLista = new Vector<String>();

	/** The setti tags. */
	Vector<String> settiTags = new Vector<String>();

	/** The setti lista. */
	Vector<String> settiLista = new Vector<String>();

	/** The has lista changed. */
	boolean hasListaChanged = false;

	private Suku owner = null;
	private static ToolsDialog runner = null;

	/**
	 * Instantiates a new tools dialog.
	 * 
	 * @param owner
	 *            the owner
	 */
	public ToolsDialog(Suku owner) {
		super(owner, Resurses.getString("DIALOG_PROPERTIES"), true);
		this.owner = owner;
		runner = this;

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 350, d.height / 2 - 250, 700, 500);
		setLayout(null);

		int y = 30;
		JPanel nsort = new JPanel();
		JPanel props = new JPanel();
		String[] notorder = null;
		try {
			SukuTypesModel types = Utils.typeInstance();

			SukuData resp = Suku.kontroller.getSukuData("cmd=getsettings",
					"type=order", "name=notice");
			notorder = resp.generalArray;

			for (int i = 0; i < types.getTypesTagsCount(); i++) {
				String tag = types.getTypesTag(i);
				String value = types.getTypesName(i);
				kokoMap.put(tag, value);
			}

			settiTags.add("NAME");
			settiLista.add(kokoMap.get("NAME"));
			settiMap.put("NAME", kokoMap.get("NAME"));

			for (int i = 0; i < notorder.length; i++) {
				String tag = notorder[i];
				if (!tag.equals("NAME")) {
					settiTags.add(tag);
					String value = kokoMap.get(tag);
					settiLista.add(value);
					settiMap.put(tag, value);
				}

			}

			for (int i = 0; i < types.getTypesTagsCount(); i++) {
				String tag = types.getTypesTag(i);
				String value = settiMap.get(tag);
				if (value == null) {
					kokoTags.add(tag);
					value = types.getTypesName(i);
					kokoLista.add(value);
				}
			}

			nsort.setLayout(null);
			nsort.setBounds(400, 0, 280, 450);

			nsort.setBorder(BorderFactory.createTitledBorder(Resurses
					.getString("DIALOG_SORT_NOTICES")));
			getContentPane().add(nsort);
			props.setLayout(null);
			props.setBounds(10, 0, 380, 450);

			props.setBorder(BorderFactory.createTitledBorder(Resurses
					.getString("DIALOG_OWNER")));

			getContentPane().add(props);
			JLabel lbl = new JLabel(Resurses.getString("DIALOG_NONSORT"));
			nsort.add(lbl);
			lbl.setBounds(10, y, 120, 20);

			lbl = new JLabel(Resurses.getString("DIALOG_YESSORT"));
			nsort.add(lbl);
			lbl.setBounds(150, y, 120, 20);

			y += 20;
			koko = new JList(kokoLista);
			koko.addMouseListener(this);
			kokoScroll = new JScrollPane(koko);
			nsort.add(kokoScroll);
			kokoScroll.setBounds(10, y, 120, 200);

			setti = new JList(settiLista);
			setti.addMouseListener(this);
			settiScroll = new JScrollPane(setti);
			nsort.add(settiScroll);
			settiScroll.setBounds(150, y, 120, 200);
			y += 206;
			lbl = new JLabel(Resurses.getString("DIALOG_CLICKINFO"));
			nsort.add(lbl);
			lbl.setBounds(10, y, 300, 20);
			y += 18;
			lbl = new JLabel(Resurses.getString("DIALOG_STATICNAME"));
			nsort.add(lbl);
			lbl.setBounds(10, y, 300, 20);
			y += 18;

		} catch (SukuException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(),
					Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);

		}

		y += 30;
		textContent = new JLabel(Resurses.getString("DIALOG_SORTINFO"));

		nsort.add(textContent);
		this.textContent.setBounds(10, y, 300, 40);
		y += 30;

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(10, y, 260, 20);
		nsort.add(this.progressBar);

		y += 40;
		this.ok = new JButton(Resurses.getString(SORT));
		nsort.add(this.ok);
		this.ok.setBounds(10, y, 100, 24);
		this.ok.setActionCommand(SORT);
		this.ok.addActionListener(this);
		// this.ok.setDefaultCapable(true);
		// getRootPane().setDefaultButton(this.ok);

		this.cancel = new JButton(Resurses.getString(CANCEL));
		nsort.add(this.cancel);
		this.cancel.setBounds(140, y, 100, 24);
		this.cancel.setActionCommand(CANCEL);
		this.cancel.addActionListener(this);

		this.task = null;

		SukuData resp = new SukuData();
		try {
			resp = Suku.kontroller.getSukuData("cmd=variables", "type=get");
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(
					this,
					Resurses.getString("DIALOG_UPDATE_DB") + ":"
							+ e.getMessage(),
					Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
			resp.generalArray = new String[10];

		}
		notorder = resp.generalArray;

		y = 20;

		JLabel lbl = new JLabel(Resurses.getString("DATA_NAME"));
		props.add(lbl);
		lbl.setBounds(10, y, 80, 20);

		name = new JTextField(Utils.nv(resp.generalArray[0]));
		props.add(name);
		name.setBounds(90, y, 280, 20);
		y += 24;
		lbl = new JLabel(Resurses.getString("DATA_ADDRESS"));
		props.add(lbl);
		lbl.setBounds(10, y, 80, 20);

		address = new JTextField(Utils.nv(resp.generalArray[1]));
		props.add(address);
		address.setBounds(90, y, 280, 20);

		y += 24;
		lbl = new JLabel(Resurses.getString("DATA_POSTCODE"));
		props.add(lbl);
		lbl.setBounds(90, y, 80, 20);
		lbl = new JLabel(Resurses.getString("DATA_POSTOFF"));
		props.add(lbl);
		lbl.setBounds(190, y, 80, 20);
		y += 20;
		postalCode = new JTextField(Utils.nv(resp.generalArray[2]));
		props.add(postalCode);
		postalCode.setBounds(90, y, 85, 20);
		postOffice = new JTextField(Utils.nv(resp.generalArray[3]));
		props.add(postOffice);
		postOffice.setBounds(180, y, 190, 20);

		y += 24;

		lbl = new JLabel(Resurses.getString("DATA_STATE"));
		props.add(lbl);
		lbl.setBounds(90, y, 80, 20);
		lbl = new JLabel(Resurses.getString("DATA_COUNTRY"));
		props.add(lbl);
		lbl.setBounds(190, y, 80, 20);
		y += 20;
		state = new JTextField(Utils.nv(resp.generalArray[4]));
		props.add(state);
		state.setBounds(90, y, 85, 20);
		country = new JTextField(Utils.nv(resp.generalArray[5]));
		props.add(country);
		country.setBounds(180, y, 190, 20);

		y += 24;
		lbl = new JLabel(Resurses.getString("DATA_EMAIL"));
		props.add(lbl);
		lbl.setBounds(10, y, 80, 20);

		email = new JTextField(Utils.nv(resp.generalArray[6]));
		props.add(email);
		email.setBounds(90, y, 280, 20);

		y += 24;
		lbl = new JLabel(Resurses.getString("DIALOG_WWW"));
		props.add(lbl);
		lbl.setBounds(10, y, 80, 20);

		web = new JTextField(Utils.nv(resp.generalArray[7]));
		props.add(web);
		web.setBounds(90, y, 280, 20);
		y += 24;
		lbl = new JLabel(Resurses.getString("DATA_NOTE"));
		props.add(lbl);
		lbl.setBounds(10, y, 80, 20);
		text = new JTextArea(Utils.nv(resp.generalArray[8]));
		text.setLineWrap(true);
		JScrollPane scrollPrivate = new JScrollPane(text,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		props.add(scrollPrivate);
		scrollPrivate.setBounds(90, y, 280, 150);
		y += 160;
		update = new JButton(Resurses.getString("DIALOG_UPDATE"));
		props.add(update);
		update.setBounds(90, y, 160, 24);
		update.setActionCommand("DIALOG_UPDATE");
		update.addActionListener(this);
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
		if (cmd.equals(SORT)) {

			SukuData request = new SukuData();
			request.generalArray = settiTags.toArray(new String[0]);

			try {
				Suku.kontroller.getSukuData(request, "cmd=updatesettings",
						"type=order", "name=notice");

			} catch (SukuException ee) {
				JOptionPane.showMessageDialog(this, ee.getMessage(),
						Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
				ee.printStackTrace();
			}

			this.ok.setEnabled(false);
			// we create new instances as needed.
			task = new Task();
			task.wn = settiTags;
			task.addPropertyChangeListener(this);
			task.execute();

		} else if (cmd.equals("DIALOG_UPDATE")) {

			try {

				SukuData req = new SukuData();
				req.generalArray = new String[9];
				req.generalArray[0] = Utils.vn(name.getText());
				req.generalArray[1] = Utils.vn(address.getText());
				req.generalArray[2] = Utils.vn(postalCode.getText());
				req.generalArray[3] = Utils.vn(postOffice.getText());
				req.generalArray[4] = Utils.vn(state.getText());
				req.generalArray[5] = Utils.vn(country.getText());
				req.generalArray[6] = Utils.vn(email.getText());
				req.generalArray[7] = Utils.vn(web.getText());
				req.generalArray[8] = Utils.vn(text.getText());

				SukuData resp = Suku.kontroller.getSukuData(req,
						"cmd=variables", "type=update");
				setVisible(false);
			} catch (SukuException ee) {
				JOptionPane.showMessageDialog(this, ee.getMessage(),
						Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (cmd.equals(CANCEL)) {
			if (this.task == null) {

				setVisible(false);
			} else {
				this.task.cancel(true);
			}
		}

	}

	/**
	 * progressbar text is split with ; before ; is number 0-100 to show on
	 * progressbar. After ; is shown in text field if no ; exists then text is
	 * shown in textfiels
	 * 
	 * @param juttu
	 *            the new runner value
	 */
	public void setRunnerValue(String juttu) {
		String[] kaksi = juttu.split(";");
		if (kaksi.length >= 2) {
			int progress = Integer.parseInt(kaksi[0]);

			progressBar.setValue(progress);
			textContent.setText(kaksi[1]);

		} else {
			textContent.setText(juttu);

			progressBar.setIndeterminate(true);
			progressBar.setValue(0);
		}
	}

	/**
	 * The Class Task.
	 */
	class Task extends SwingWorker<Void, Void> {

		/** The wn. */
		Vector<String> wn = null;

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
			setRunnerValue("0;" + Resurses.getString(""));

			int dbcount = owner.getDatabaseRowCount();
			int updateCount = 0;
			int prosent;
			for (int dbi = 0; dbi < dbcount; dbi++) {

				prosent = (dbi * 100) / dbcount;
				PersonShortData sho = owner.getDatbasePerson(dbi);
				String tmp = "" + prosent + "; " + sho.getAlfaName();
				// System.out.println(tmp);
				setRunnerValue(tmp);

				SukuData plong;
				try {
					plong = Suku.kontroller.getSukuData("cmd=person", "pid="
							+ sho.getPid());
				} catch (SukuException e) {

					e.printStackTrace();
					return null;
				}
				PersonLongData persLong = plong.persLong;

				Vector<UnitNotice> un = new Vector<UnitNotice>();
				for (int j = 0; j < persLong.getNotices().length; j++) {
					un.add(persLong.getNotices()[j]);
				}

				int lastCheckedIndex = -1;

				boolean hasSorted = false;
				for (int tagIdx = 0; tagIdx < wn.size(); tagIdx++) {
					String tag = wn.get(tagIdx);

					for (int k = 0; k < un.size(); k++) {
						UnitNotice uun = un.get(k);
						if (uun.getTag().equals(tag)) {
							if (k > lastCheckedIndex) {
								lastCheckedIndex++;
								if (k > lastCheckedIndex) {
									UnitNotice t = un.remove(k);
									un.insertElementAt(t, lastCheckedIndex);
									hasSorted = true;
								}
							} else {
								lastCheckedIndex = k;
							}
						}
					}
				}

				if (hasSorted) {
					updateCount++;
					logger.fine("sorted notices for [" + sho.getPid() + "]: "
							+ sho.getAlfaName());
					plong.persLong.setNotices(un.toArray(new UnitNotice[0]));

					try {
						Suku.kontroller.getSukuData(plong, "cmd=update",
								"type=person");
						logger.fine("person updated pid[" + persLong.getPid()
								+ "]");
						// System.out.println("päivitys : " + resp.resu);
					} catch (SukuException e) {
						logger.log(Level.WARNING, "person update failed", e);
						JOptionPane.showMessageDialog(null, e.getMessage(),
								Resurses.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);

					}
				}

			}

			logger.info("Sorted notices for [" + updateCount
					+ "] persons from [" + dbcount + "] in set");

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
			setVisible(false);

		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == 1) {

			if (e.getSource() == koko) {
				int rivi = koko.getSelectedIndex();
				settiTags.add(kokoTags.get(rivi));
				settiLista.add(kokoLista.get(rivi));
				kokoTags.remove(rivi);
				kokoLista.remove(rivi);
				koko.updateUI();
				setti.updateUI();
				hasListaChanged = true;

			} else if (e.getSource() == setti) {
				int rivi = setti.getSelectedIndex();
				if (rivi > 0) {
					kokoTags.add(settiTags.get(rivi));
					kokoLista.add(settiLista.get(rivi));
					settiTags.remove(rivi);
					settiLista.remove(rivi);
					koko.updateUI();
					setti.updateUI();
					hasListaChanged = true;
				}

			} else {
				System.out.println("ME:" + e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

}
