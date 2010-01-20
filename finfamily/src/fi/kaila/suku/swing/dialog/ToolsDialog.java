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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * This dialog is now defining notice order possible other tasks will be done
 * here later
 * 
 * @author Kalle
 * 
 */
public class ToolsDialog extends JDialog implements ActionListener,
		PropertyChangeListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger
			.getLogger(ToolsDialog.class.getName());
	private static final String OK = "OK";
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

	/**
	 * @return handle to this instance
	 */
	public static ToolsDialog getRunner() {
		return runner;
	}

	HashMap<String, String> kokoMap = new HashMap<String, String>();
	HashMap<String, String> settiMap = new HashMap<String, String>();

	Vector<String> kokoTags = new Vector<String>();
	Vector<String> kokoLista = new Vector<String>();

	Vector<String> settiTags = new Vector<String>();
	Vector<String> settiLista = new Vector<String>();

	boolean hasListaChanged = false;

	private Suku owner = null;
	private static ToolsDialog runner = null;

	/**
	 * @param owner
	 */
	public ToolsDialog(Suku owner) {
		super(owner, Resurses.getString("DIALOG_SORT_NOTICES"), true);
		this.owner = owner;
		runner = this;

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 300, d.height / 2 - 200, 600, 400);
		setLayout(null);

		int y = 10;
		SukuData reposet; // types
		String[] notorder = null;
		try {
			reposet = Suku.kontroller.getSukuData("cmd=gettypes", "lang="
					+ Resurses.getLanguage());

			SukuData resp = Suku.kontroller.getSukuData("cmd=getsettings",
					"type=order", "name=notice");
			notorder = resp.generalArray;

			for (int i = 0; i < reposet.vvTypes.size(); i++) {
				String tag = reposet.vvTypes.get(i)[0];
				String value = reposet.vvTypes.get(i)[1];
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

			for (int i = 0; i < reposet.vvTypes.size(); i++) {

				String tag = reposet.vvTypes.get(i)[0];

				String value = settiMap.get(tag);
				if (value == null) {
					kokoTags.add(tag);

					value = reposet.vvTypes.get(i)[1];
					kokoLista.add(value);
				}
			}

			JLabel lbl = new JLabel(Resurses.getString("DIALOG_NONSORT"));
			getContentPane().add(lbl);
			lbl.setBounds(300, y, 120, 20);

			lbl = new JLabel(Resurses.getString("DIALOG_YESSORT"));
			getContentPane().add(lbl);
			lbl.setBounds(460, y, 120, 20);

			y += 20;
			koko = new JList(kokoLista);
			koko.addMouseListener(this);
			kokoScroll = new JScrollPane(koko);
			getContentPane().add(kokoScroll);
			kokoScroll.setBounds(300, y, 120, 200);

			setti = new JList(settiLista);
			setti.addMouseListener(this);
			settiScroll = new JScrollPane(setti);
			getContentPane().add(settiScroll);
			settiScroll.setBounds(460, y, 120, 200);
			y += 206;
			lbl = new JLabel(Resurses.getString("DIALOG_CLICKINFO"));
			getContentPane().add(lbl);
			lbl.setBounds(300, y, 300, 20);
			y += 18;
			lbl = new JLabel(Resurses.getString("DIALOG_STATICNAME"));
			getContentPane().add(lbl);
			lbl.setBounds(300, y, 300, 20);
			y += 18;

		} catch (SukuException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

		}

		y = 260;

		textContent = new JLabel(Resurses.getString("DIALOG_SORTINFO"));

		getContentPane().add(textContent);
		this.textContent.setBounds(30, y, 340, 40);
		y += 30;

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(30, y, 340, 20);
		getContentPane().add(this.progressBar);

		y += 40;
		this.ok = new JButton(Resurses.getString(OK));
		getContentPane().add(this.ok);
		this.ok.setBounds(360, y, 100, 24);
		this.ok.setActionCommand(OK);
		this.ok.addActionListener(this);
		this.ok.setDefaultCapable(true);
		getRootPane().setDefaultButton(this.ok);

		this.cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(480, y, 100, 24);
		this.cancel.setActionCommand(CANCEL);
		this.cancel.addActionListener(this);

		this.task = null;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(OK)) {

			SukuData request = new SukuData();
			request.generalArray = settiTags.toArray(new String[0]);

			try {
				Suku.kontroller.getSukuData(request, "cmd=updatesettings",
						"type=order", "name=notice");

			} catch (SukuException ee) {
				JOptionPane.showMessageDialog(this, ee.getMessage(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				ee.printStackTrace();
			}

			this.ok.setEnabled(false);
			// we create new instances as needed.
			task = new Task();
			task.wn = settiTags;
			task.addPropertyChangeListener(this);
			task.execute();

		}
		if (cmd.equals(CANCEL)) {
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

	class Task extends SwingWorker<Void, Void> {

		Vector<String> wn = null;

		/*
		 * Main task. Executed in background thread.
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
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setVisible(false);

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

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

}
