package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fi.kaila.suku.swing.Suku;
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

	private JTextArea txtArea=null;
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
		save.addActionListener(this);
		save.setActionCommand("SAVE_AS");
		mFile.add(save);
		JMenuItem print = new JMenuItem(Resurses.getString("FILE_PRINT"));
		print.addActionListener(this);
		print.setActionCommand("PRINT");
		mFile.add(print);
		JMenuItem cl = new JMenuItem(Resurses.getString("CLOSE"));
		cl.addActionListener(this);
		cl.setActionCommand("CLOSE");
		mFile.add(cl);

		txtArea = new JTextArea(text);
		JScrollPane sc = new JScrollPane(txtArea);
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
		} else if (cmd.equals("PRINT")){
			try {
				txtArea.print();
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(this, Resurses
						.getString("IMPORT_GEDCOM")
						+ ":" + e.getMessage());
			}
		} else if (cmd.equals("SAVE_AS")){
			boolean isFile = Suku.kontroller.createLocalFile("txt");
			if (isFile) {
				OutputStream fos = Suku.kontroller.getOutputStream();
			
				String tekst;
				if (java.io.File.pathSeparatorChar == ';') {
					tekst = txtArea.getText().replaceAll("\n", "\r\n");
				} else {
					tekst = txtArea.getText();
				}
				byte[] buffi = tekst.getBytes();
				try {
					fos.write(buffi);
					fos.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, Resurses
							.getString("IMPORT_GEDCOM")
							+ ":" + e.getMessage());
				}
			
			}
			
			
		}
	}

}
