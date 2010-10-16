package fi.kaila.suku.swing.dialog;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesModel;
import fi.kaila.suku.util.Utils;

/**
 * Dialog to request for notice to be added.
 * 
 * @author Kalle
 */
public class AddNotice extends JDialog implements MouseListener {

	private static final long serialVersionUID = 1L;

	/** The koko map. */
	HashMap<String, String> kokoMap = new HashMap<String, String>();

	private JList koko;

	/** The koko tags. */
	Vector<String> kokoTags = new Vector<String>();

	/** The koko lista. */
	Vector<String> kokoLista = new Vector<String>();

	private String selectedTag = null;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner
	 * @throws SukuException
	 *             the suku exception
	 */
	public AddNotice(Suku owner) throws SukuException {
		super(owner, Resurses.getString("DIALOG_ADD_NOTICE"), true);

		// Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		//
		// setBounds(d.width/2-300,d.height/2-200,300,400);
		setLayout(null);
		SukuTypesModel types = Utils.typeInstance();

		for (int i = 0; i < types.getTypesTagsCount(); i++) {
			String tag = types.getTypesTags(i);
			String value = types.getTypesName(i);
			kokoMap.put(tag, value);
		}

		for (int i = 0; i < types.getTypesTagsCount(); i++) {
			String tag = types.getTypesTags(i);
			kokoTags.add(tag);

			String value = types.getTypesName(i);
			kokoLista.add(value);

		}

		koko = new JList(kokoLista);
		koko.addMouseListener(this);
		JScrollPane kokoScroll = new JScrollPane(koko);
		getContentPane().add(kokoScroll);
		kokoScroll.setBounds(10, 10, 120, 340);

	}

	/**
	 * Gets the selected tag.
	 * 
	 * @return the tag selected to be added
	 */
	public String getSelectedTag() {
		return selectedTag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		int idx = koko.getSelectedIndex();
		selectedTag = kokoTags.get(idx);
		setVisible(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// Not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Not used
	}

}
