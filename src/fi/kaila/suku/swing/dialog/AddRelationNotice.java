package fi.kaila.suku.swing.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;

public class AddRelationNotice extends JDialog implements ActionListener,
		MouseListener {

	private static final long serialVersionUID = 1L;
	HashMap<String, String> kokoMap = new HashMap<String, String>();

	private JScrollPane kokoScroll;
	private JList koko;
	String[] kokoLista = null;
	String[] kokoTags = null;

	private String selectedTag = null;

	public AddRelationNotice(Suku owner, boolean isMarriage)
			throws SukuException {
		super(owner, Resurses.getString("DIALOG_ADD_NOTICE"), true);

		// Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		//		
		// setBounds(d.width/2-300,d.height/2-200,300,400);
		setLayout(null);
		// setUndecorated(true);

		// RELA_TAG_MARR=Avioliitto
		// RELA_TAG_DIV=Eronnut
		// RELA_TAG_ADOP=Adoptoitu

		if (isMarriage) {
			kokoLista = new String[2];
			kokoTags = new String[2];
			kokoLista[0] = Resurses.getString("RELA_TAG_MARR");
			kokoLista[1] = Resurses.getString("RELA_TAG_DIV");
			kokoTags[0] = "MARR";
			kokoTags[1] = "DIV";

		} else {
			kokoLista = new String[1];
			kokoTags = new String[1];
			kokoLista[0] = Resurses.getString("RELA_TAG_ADOP");
			kokoTags[0] = "ADOP";
		}

		koko = new JList(kokoLista);
		koko.addMouseListener(this);
		kokoScroll = new JScrollPane(koko);
		getContentPane().add(kokoScroll);
		kokoScroll.setBounds(10, 10, 120, 80);

	}

	public String getSelectedTag() {
		return selectedTag;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		int idx = koko.getSelectedIndex();
		selectedTag = kokoTags[idx];
		setVisible(false);

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
