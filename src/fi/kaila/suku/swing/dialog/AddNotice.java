package fi.kaila.suku.swing.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import fi.kaila.suku.util.pojo.SukuData;

public class AddNotice extends JDialog implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	HashMap<String, String> kokoMap = new HashMap<String, String>();

	private JScrollPane kokoScroll;
	private JList koko;
	Vector<String> kokoTags = new Vector<String>();
	Vector<String> kokoLista = new Vector<String>();

	private Suku owner = null;

	private String selectedTag = null;

	public AddNotice(Suku owner) throws SukuException {
		super(owner, Resurses.getString("DIALOG_ADD_NOTICE"), true);
		this.owner = owner;

		// Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		//		
		// setBounds(d.width/2-300,d.height/2-200,300,400);
		setLayout(null);
		// setUndecorated(true);
		int x = 20;
		int y = 20;
		SukuData reposet = Suku.kontroller.getSukuData("cmd=gettypes", "lang="
				+ Resurses.getLanguage());

		for (int i = 0; i < reposet.vvTypes.size(); i++) {
			String tag = reposet.vvTypes.get(i)[0];
			String value = reposet.vvTypes.get(i)[1];
			kokoMap.put(tag, value);
		}

		for (int i = 0; i < reposet.vvTypes.size(); i++) {

			String tag = reposet.vvTypes.get(i)[0];

			kokoTags.add(tag);

			String value = reposet.vvTypes.get(i)[1];
			kokoLista.add(value);

		}

		y += 20;
		koko = new JList(kokoLista);
		koko.addMouseListener(this);
		kokoScroll = new JScrollPane(koko);
		getContentPane().add(kokoScroll);
		kokoScroll.setBounds(10, 10, 120, 340);

	}

	public String getSelectedTag() {
		return selectedTag;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		int idx = koko.getSelectedIndex();
		selectedTag = kokoTags.get(idx);
		setVisible(false);

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
