package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * This tool orders the children in the database for all persons or all persons
 * in selected view
 * 
 * If person has children without birth year that person is inserted into result
 * view
 * 
 * @author kalle
 * 
 */
public class OrderChildren extends JDialog implements ActionListener,
		PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Suku owner = null;
	private static OrderChildren runner = null;

	private static final String SORT = "NOTICES.SORT";
	private static final String CANCEL = "CANCEL";

	private JCheckBox orderAll = null;
	private JComboBox viewList = null;
	private int[] viewIds = null;

	private final JButton ok;

	private final JLabel textContent;
	private final JProgressBar progressBar;
	private Task task;

	public OrderChildren(Suku owner) throws SukuException {
		super(owner, Resurses.getString("MENU_CHILDREN_ORDER"), true);
		this.owner = owner;
		runner = this;
		int y = 0;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setBounds(d.width / 2 - 150, d.height / 2 - 130, 300, 260);
		setResizable(false);
		setLayout(null);

		y += 50;

		JLabel lbl;

		orderAll = new JCheckBox(Resurses.getString("ORDER_ALL_CHILDREN"));
		add(orderAll);
		orderAll.setBounds(10, y, 260, 20);
		y += 24;
		// lbl = new JLabel();
		// add(lbl);
		// lbl.setBounds(10, y, 260, 20);
		// y += 20;

		lbl = new JLabel(Resurses.getString("STORE_NOT_SORTED"));
		add(lbl);
		lbl.setBounds(10, y, 260, 20);
		y += 20;
		SukuData vlist = Suku.kontroller.getSukuData("cmd=viewlist");

		String[] lista = vlist.generalArray;
		this.viewList = new JComboBox();
		add(this.viewList);
		viewList.setBounds(10, y, 260, 20);
		viewList.addItem("");
		viewIds = new int[lista.length + 1];
		viewIds[0] = 0;
		for (int i = 0; i < lista.length; i++) {
			String[] pp = lista[i].split(";");
			if (pp.length > 1) {
				int vid = 0;
				try {
					vid = Integer.parseInt(pp[0]);
					viewIds[i + 1] = vid;
				} catch (NumberFormatException ne) {
					viewIds[i + 1] = 0;
				}
				viewList.addItem(pp[1]);
			}
		}

		y += 30;
		textContent = new JLabel(Resurses.getString("DIALOG_SORTINFO"));
		add(textContent);
		this.textContent.setBounds(10, y, 300, 40);
		y += 30;

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(10, y, 260, 20);
		add(this.progressBar);

		y += 40;
		this.ok = new JButton(Resurses.getString(SORT));
		add(this.ok);
		this.ok.setBounds(10, y, 100, 24);
		this.ok.setActionCommand(SORT);
		this.ok.addActionListener(this);
		getRootPane().setDefaultButton(this.ok);

		JButton cancel = new JButton(Resurses.getString(CANCEL));
		add(cancel);
		cancel.setBounds(140, y, 100, 24);
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);

		this.task = null;

	}

	public static OrderChildren getRunner() {
		return runner;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		if (cmd.equals(CANCEL)) {
			if (task == null) {
				setVisible(false);
				return;
			} else {
				task.stopMeNow = true;
			}
		}

		if (cmd.equals(SORT)) {

			int ii = viewList.getSelectedIndex();
			if (ii <= 0) {
				JOptionPane.showMessageDialog(owner,
						Resurses.getString("STORE_VIEW_MISSING"),
						Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
				return;

			}

			task = new Task();
			task.orderAll = orderAll.isSelected();
			task.viewId = viewIds[ii];
			task.addPropertyChangeListener(this);
			task.execute();

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
			progressBar.setIndeterminate(false);
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

		int viewId = 0;
		boolean stopMeNow = false;
		boolean orderAll = false;
		int notOrdered = 0;

		@Override
		protected Void doInBackground() throws Exception {

			setProgress(0);
			setRunnerValue("0;" + Resurses.getString("MENU_CHILDREN_ORDER"));

			int dbcount = owner.getDatabaseRowCount();
			Vector<Integer> pidsnot = new Vector<Integer>();
			int prosent;
			for (int dbi = 0; dbi < dbcount; dbi++) {
				if (stopMeNow) {
					break;
				}
				prosent = (dbi * 100) / dbcount;
				PersonShortData sho = owner.getDatbasePerson(dbi);
				String tmp = "" + prosent + "; " + sho.getAlfaName();

				setRunnerValue(tmp);

				if (sho.getChildCount() > 1) {

					SukuData plong;
					try {
						plong = Suku.kontroller.getSukuData("cmd=sort", "all="
								+ orderAll, "pid=" + sho.getPid());
						if (plong.resuCount > 0) {
							pidsnot.add(sho.getPid());
						}
					} catch (SukuException e) {

						JOptionPane.showMessageDialog(owner, e.toString(),
								Resurses.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);

						break;

					}

				}
			}

			if (pidsnot.size() > 0) {
				notOrdered = pidsnot.size();
				SukuData request = new SukuData();
				request.pidArray = new int[pidsnot.size()];
				for (int i = 0; i < pidsnot.size(); i++) {
					request.pidArray[i] = pidsnot.get(i);
				}
				Suku.kontroller.getSukuData(request, "cmd=view", "action=add",
						"key=pidarray", "viewid=" + viewId, "empty=true");

			}

			return null;
		}

		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setVisible(false);
			if (notOrdered > 0) {
				JOptionPane.showMessageDialog(owner,
						Resurses.getString("STORE_VIEW_SIZE") + " "
								+ notOrdered,
						Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

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
