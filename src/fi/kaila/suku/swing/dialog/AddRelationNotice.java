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

/**
 * Dialog to add relation notice.
 * 
 * @author Kalle
 */
public class AddRelationNotice extends JDialog implements ActionListener,
		MouseListener {

	private static final long serialVersionUID = 1L;

	/** The koko map. */
	HashMap<String, String> kokoMap = new HashMap<String, String>();

	private JScrollPane kokoScroll;
	private JList koko;

	/** The koko lista. */
	String[] kokoLista = null;

	/** The koko tags. */
	String[] kokoTags = null;

	private String selectedTag = null;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner
	 * @param isMarriage
	 *            the is marriage
	 * @throws SukuException
	 *             the suku exception
	 */
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
			kokoLista = new String[4];
			kokoTags = new String[4];
			kokoLista[0] = Resurses.getString("RELA_TAG_MARR");
			kokoLista[1] = Resurses.getString("RELA_TAG_DIV");
			kokoLista[2] = Resurses.getString("RELA_TAG_SOUR");
			kokoLista[3] = Resurses.getString("RELA_TAG_NOTE");
			kokoTags[0] = "MARR";
			kokoTags[1] = "DIV";
			kokoTags[2] = "SOUR";
			kokoTags[3] = "NOTE";
		} else {
			kokoLista = new String[3];
			kokoTags = new String[3];
			kokoLista[0] = Resurses.getString("RELA_TAG_ADOP");
			kokoLista[1] = Resurses.getString("RELA_TAG_SOUR");
			kokoLista[2] = Resurses.getString("RELA_TAG_NOTE");
			kokoTags[0] = "ADOP";
			kokoTags[1] = "SOUR";
			kokoTags[2] = "NOTE";

		}

		koko = new JList(kokoLista);
		koko.addMouseListener(this);
		kokoScroll = new JScrollPane(koko);
		getContentPane().add(kokoScroll);
		kokoScroll.setBounds(10, 10, 120, 80);

	}

	/**
	 * Gets the selected tag.
	 * 
	 * @return tag to be added
	 */
	public String getSelectedTag() {
		return selectedTag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		int idx = koko.getSelectedIndex();
		selectedTag = kokoTags[idx];
		setVisible(false);

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
