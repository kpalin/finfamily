package fi.kaila.suku.report.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.PersonShortData;

public class JoinDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final PersonShortData main;
	private final PersonShortData sub;
	private final Suku parent;

	public JoinDialog(Suku owner, PersonShortData main, PersonShortData sub) {
		super(owner, Resurses.getString("JOIN_MENU"), true);

		this.parent = owner;

		this.main = main;
		this.sub = sub;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				// UIManager.put("swing.boldMetal", Boolean.FALSE);
				initMe();
			}

		});
	}

	private void initMe() {

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension sz = new Dimension(d.width - 200, d.height - 150);
		sz = new Dimension(1000, 600);
		int footery = sz.height - 125;
		setBounds((d.width - sz.width) / 2, (d.height - sz.height) / 2,
				sz.width, sz.height);
		setLayout(null);

		int x1 = 20;
		int x2 = sz.width / 2 + 20;
		int y1 = 20;
		int y2 = 20;

		JLabel lbl = new JLabel(main.getName(true, true));
		lbl.setBounds(x1, y1, 200, 20);
		add(lbl);

		lbl = new JLabel(sub.getName(true, true));
		lbl.setBounds(x2, y2, 200, 20);
		add(lbl);

		JButton dome = new JButton(Resurses.getString("JOIN_US"));
		dome.setBounds(x2, footery, 100, 22);
		dome.addActionListener(this);
		dome.setActionCommand("JOIN_US");
		add(dome);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null) {
			return;
		}
		if (cmd.equals("JOIN_US")) {
			setVisible(false);
		}

	}

}
