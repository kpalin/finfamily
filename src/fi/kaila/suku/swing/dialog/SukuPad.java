package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fi.kaila.suku.util.Resurses;

/**
 * 
 * simple notepad dialog
 * 
 * @author Kalle
 * 
 */
public class SukuPad extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param owner
	 * @param text
	 */
	public SukuPad(JFrame owner, String text) {

		super(owner, Resurses.getString("SUKUOHJELMISTO"));

		JMenuBar menubar = new JMenuBar();

		setJMenuBar(menubar);
		JMenu mFile = new JMenu(Resurses.getString(Resurses.FILE));
		menubar.add(mFile);

		JMenuItem save = new JMenuItem(Resurses.getString("FILE_SAVE_AS"));
		mFile.add(save);
		JMenuItem print = new JMenuItem(Resurses.getString("FILE_PRINT"));

		mFile.add(print);
		JMenuItem cl = new JMenuItem(Resurses.getString("CLOSE"));
		cl.addActionListener(this);
		cl.setActionCommand("CLOSE");
		mFile.add(cl);

		JTextArea txt = new JTextArea(text);
		JScrollPane sc = new JScrollPane(txt);
		getContentPane().add(sc);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width / 2 - 300, d.height / 2 - 200, 600, 400);
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		String cmd = a.getActionCommand();

		if (cmd == null)
			return;
		if (cmd.equals("CLOSE")) {
			setVisible(false);
		}
	}

}
