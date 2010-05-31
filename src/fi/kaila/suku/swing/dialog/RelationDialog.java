package fi.kaila.suku.swing.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.util.SukuDateField;
import fi.kaila.suku.swing.util.SukuSuretyField;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.RelationLanguage;
import fi.kaila.suku.util.pojo.RelationNotice;

/**
 * relation dialog shows and updates details of a relation
 * 
 * @author Kalle
 * 
 */
public class RelationDialog extends JDialog implements ActionListener,
		ComponentListener, MenuListener {

	private static final long serialVersionUID = 1L;

	private JTextField relationType = null;

	private JTextField description = null;
	private JTextField place = null;
	private JTextArea noteText = null;
	JScrollPane scrollNote = null;

	private JTextArea sourceText = null;
	JScrollPane scrollSource = null;
	private JTextArea privateText = null;
	JScrollPane scrollPrivate = null;

	SukuDateField date;

	private JTextField relationTypeLang = null;

	private JTextField descriptionLang = null;
	private JLabel dateLbl = null;
	private JTextField placeLang = null;
	private JLabel placeLbl = null;
	private JTextArea noteTextLang = null;
	private JLabel noteLbl = null;

	private JPanel langPanel;
	private JScrollPane scrollNoteLang = null;
	private JLabel sourceLbl = null;
	private JLabel privateLbl = null;
	private JLabel langLbl = null;
	// private JLabel typeLangLbl = null;
	// private JLabel descLangLbl = null;
	private JLabel placeLangLbl = null;
	private JLabel noteLangLbl = null;
	private JButton ok;
	private JButton delete;
	private static int ytype = 10;
	private static int ydesc = 34;
	private static int ydate = 58;

	private static int ytypeLang = 24;
	private static int ydescLang = 48;
	private static int yplaceLang = 72;
	private static int ynoteLang = 96;

	private RelationNotice rela = null;

	private ButtonGroup languageGroup = null;

	// FIXME: This declares a volatile reference to an array, which might not be
	// what you want. With a volatile reference to an array, reads and writes of
	// the reference to the array are treated as volatile, but the array
	// elements are non-volatile. To get volatile array elements, you will need
	// to use one of the atomic array classes in java.util.concurrent (provided
	// in Java 5.0).

	// Jos tulet tänne taas niin selitä hieman
	volatile JRadioButton[] langxx;

	SukuSuretyField surety;
	JLabel suretyLbl;
	JTextField created;
	JLabel createdLbl;
	JTextField modified;
	JLabel modifiedLbl;

	/**
	 * @param owner
	 */
	public RelationDialog(JFrame owner) {
		super(owner, Resurses.getString("RELA_UPDATE_PAGE"), true);
		setLayout(null);

		langLbl = new JLabel(Resurses.getString("DATA_LANG_PAGE"));
		add(langLbl);

		languageGroup = new ButtonGroup();

		int lcnt = Suku.getRepoLanguageCount();
		langxx = new JRadioButton[lcnt];
		for (int i = 0; i < lcnt; i++) {

			langxx[i] = new JRadioButton(Suku.getRepoLanguage(i, false));
			langxx[i].setActionCommand(Suku.getRepoLanguage(i, true));

			langxx[i].addActionListener(this);
			languageGroup.add(langxx[i]);
			add(langxx[i]);
		}

		suretyLbl = new JLabel();
		suretyLbl = new JLabel(Resurses.getString("DATA_SURETY"));
		add(suretyLbl);
		surety = new SukuSuretyField();
		add(surety);

		ok = new JButton(Resurses.getString("CLOSE"));
		ok.setActionCommand("OK");
		ok.addActionListener(this);
		getRootPane().setDefaultButton(ok);
		add(ok);

		delete = new JButton(Resurses.getString("DATA_DELETE"));
		delete.setActionCommand("DEL");
		delete.addActionListener(this);
		getRootPane().setDefaultButton(delete);
		add(delete);

		createdLbl = new JLabel(Resurses.getString("DATA_CREATED"));
		add(createdLbl);
		created = new JTextField();
		created.setEditable(false);
		add(created);

		modifiedLbl = new JLabel(Resurses.getString("DATA_MODIFIED"));
		add(modifiedLbl);
		modified = new JTextField();
		modified.setEditable(false);
		add(modified);

		JLabel lbl = new JLabel(Resurses.getString("DATA_TYPE"));
		add(lbl);
		lbl.setBounds(10, ytype, 80, 20);
		relationType = new JTextField();
		add(relationType);
		lbl = new JLabel(Resurses.getString("DATA_DESCRIPTION"));
		add(lbl);
		lbl.setBounds(10, ydesc, 80, 20);
		description = new JTextField();
		add(description);
		dateLbl = new JLabel(Resurses.getString("DATA_DATE"));
		add(dateLbl);

		date = new SukuDateField();
		add(date);

		placeLbl = new JLabel(Resurses.getString("DATA_PLACE"));
		add(placeLbl);

		place = new JTextField();
		add(place);

		noteLbl = new JLabel(Resurses.getString("DATA_NOTE"));
		add(noteLbl);

		noteText = new JTextArea();
		noteText.setLineWrap(true);
		scrollNote = new JScrollPane(noteText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollNote);

		sourceText = new JTextArea();
		sourceText.setLineWrap(true);
		scrollSource = new JScrollPane(sourceText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollSource);

		privateText = new JTextArea();
		privateText.setLineWrap(true);
		scrollPrivate = new JScrollPane(privateText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPrivate);

		sourceLbl = new JLabel(Resurses.getString("DATA_SOURCE"));

		add(sourceLbl);

		privateLbl = new JLabel(Resurses.getString("DATA_PRIVATETEXT"));
		add(privateLbl);

		langPanel = new JPanel();
		add(langPanel);
		langPanel.setLayout(null);

		langPanel.setBorder(BorderFactory.createTitledBorder(""));
		JLabel llbl = new JLabel(Resurses.getString("DATA_TYPE"));
		langPanel.add(llbl);

		llbl.setBounds(5, ytypeLang, 80, 20);
		relationTypeLang = new JTextField();
		langPanel.add(relationTypeLang);
		llbl = new JLabel(Resurses.getString("DATA_DESCRIPTION"));
		langPanel.add(llbl);
		llbl.setBounds(5, ydescLang, 80, 20);
		descriptionLang = new JTextField();
		langPanel.add(descriptionLang);

		placeLangLbl = new JLabel(Resurses.getString("DATA_PLACE"));
		langPanel.add(placeLangLbl);

		placeLang = new JTextField();
		langPanel.add(placeLang);

		noteLangLbl = new JLabel(Resurses.getString("DATA_NOTE"));
		langPanel.add(noteLangLbl);

		noteTextLang = new JTextArea();
		noteTextLang.setLineWrap(true);
		scrollNoteLang = new JScrollPane(noteTextLang,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		langPanel.add(scrollNoteLang);

		addComponentListener(this);

	}

	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();

		rootPane.registerKeyboardAction(this, "OK", stroke,
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	/**
	 * Set pojo to display
	 * 
	 * @param rela
	 *            relation notice to dislpay
	 */
	public void setRelation(RelationNotice rela) {
		this.rela = rela;

		RelationLanguage[] rr = rela.getLanguages();
		RelationLanguage[] fixed = new RelationLanguage[Suku
				.getRepoLanguageCount()];
		for (int i = 0; i < fixed.length; i++) {
			String lan = Suku.getRepoLanguage(i, true);
			fixed[i] = new RelationLanguage(lan);
			if (rr != null) {
				for (int j = 0; j < rr.length; j++) {
					if (lan.equals(rr[j].getLangCode())) {
						if (rr[j].isToBeUpdated() || rr[j].getRid() > 0) {
							fixed[i] = rr[j];
							langxx[i].setForeground(Color.RED);
						}
						break;
					}
				}
			}
		}
		rela.setLanguages(fixed);
	}

	/**
	 * display the relationDialog
	 */
	public void showMe() {
		if (rela == null)
			return;
		if (rela.isToBeDeleted()) {
			delete.setText(Resurses.getString("DATA_DELETED"));
			delete.setEnabled(false);
		}

		if (rela.getCreated() == null) {
			created.setText("");
		} else {
			created.setText(rela.getCreated().toString());
		}
		if (rela.getModified() != null) {
			modified.setText(rela.getModified().toString());
		} else {
			modified.setText("");
		}
		relationType.setText(rela.getType());
		description.setText(rela.getDescription());
		if (rela.getTag().equals("MARR") || rela.getTag().equals("DIV")
				|| rela.getTag().equals("ADOP")) {
			date.setVisible(true);
			place.setVisible(true);
			scrollNoteLang.setVisible(true);
			dateLbl.setVisible(true);
			placeLbl.setVisible(true);
			noteLbl.setVisible(true);
			placeLangLbl.setVisible(true);
			noteLangLbl.setVisible(true);
		} else if (rela.getTag().equals("NOTE")) {
			date.setVisible(false);
			place.setVisible(false);
			scrollNoteLang.setVisible(true);
			dateLbl.setVisible(false);
			placeLbl.setVisible(false);
			noteLbl.setVisible(true);
			placeLangLbl.setVisible(true);
			noteLangLbl.setVisible(true);
		} else {
			date.setVisible(false);
			place.setVisible(false);
			scrollNoteLang.setVisible(false);
			dateLbl.setVisible(false);
			placeLbl.setVisible(false);
			noteLbl.setVisible(false);
			placeLangLbl.setVisible(false);
			noteLangLbl.setVisible(false);
		}
		date
				.setDate(rela.getDatePrefix(), rela.getFromDate(), rela
						.getToDate());
		place.setText(rela.getPlace());
		noteText.setText(rela.getNoteText());
		sourceText.setText(rela.getSource());
		privateText.setText(rela.getPrivateText());
		int firstIdx = -1;
		for (int i = 0; i < Suku.getRepoLanguageCount(); i++) {
			if (rela.getLanguages()[i].getRnid() > 0) {
				firstIdx = i;
				break;
			}
		}
		if (firstIdx >= 0) {
			langxx[firstIdx].setSelected(true);
			showLanguage(Suku.getRepoLanguage(firstIdx, true));
		} else {
			langPanel.setVisible(false);

		}

	}

	/**
	 * 
	 * @throws SukuDateException
	 */
	public void updateData() throws SukuDateException {
		if (rela == null)
			return;
		if (rela.isToBeDeleted())
			return;

		rela.setType(relationType.getText());

		rela.setDescription(description.getText());

		String tmp = date.getFromDate();
		String ttmp = date.getToDate();
		String pre = date.getDatePrefTag();

		if (!Utils.nv(pre).equals(Utils.nv(rela.getDatePrefix()))
				|| Utils.nv(tmp).equals(Utils.nv(rela.getFromDate()))
				|| Utils.nv(ttmp).equals(Utils.nv(rela.getToDate()))) {
			rela.setDatePrefix(pre);
			rela.setFromDate(tmp);
			rela.setToDate(ttmp);
		}

		date
				.setDate(rela.getDatePrefix(), rela.getFromDate(), rela
						.getToDate());

		rela.setPlace(place.getText());

		rela.setNoteText(noteText.getText());

		// noteText.setText(rela.getNoteText());
		rela.setSource(sourceText.getText());
		rela.setPrivateText(privateText.getText());
		return;
	}

	String oldLanguage = null;

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null) {
			// if (e.getSource() instanceof JRootPane){
			// System.out.println("E::" +e);
			// setVisible(false);
			// }
			return;
		}
		updateLanguage();
		String langcode = null;
		for (int i = 0; i < Suku.getRepoLanguageCount(); i++) {
			if (cmd.equals(Suku.getRepoLanguage(i, true))) {
				if (langxx[i].isSelected()) {
					langcode = cmd;
					break;
				}
			}
		}

		if (langcode != null && !langcode.equals(oldLanguage)) {

			showLanguage(langcode);
			// oldLanguage=cmd;

		}

		if (cmd.equals("OK")) {

			try {
				date.getFromDate();
				date.getToDate();
			} catch (SukuDateException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

				return;
			}

			setVisible(false);
		}
		if (cmd.equals("DEL")) {
			rela.setToBeDeleted(true);
			delete.setText(Resurses.getString("DATA_DELETED"));
			delete.setEnabled(false);
			setVisible(false);
		}
		if (cmd.equals(Resurses.CLOSE)) {
			System.out.println("Close akkuna");
			setVisible(false);
		}

	}

	private void showLanguage(String langcode) {
		boolean foundLangu = false;
		RelationLanguage rl = null;
		langPanel.setVisible(true);
		String langName = "";
		// descriptionLang.setVisible(true);
		for (int i = 0; i < rela.getLanguages().length; i++) {
			if (langcode.equals(rela.getLanguages()[i].getLangCode())) {
				rl = rela.getLanguages()[i];
				if (!foundLangu) {
					langxx[i].setSelected(true);

					langName = Suku.getRepoLanguage(i, false);

					foundLangu = true;
				}
				break;
			}
		}
		if (rl != null) {

			TitledBorder bb = (TitledBorder) langPanel.getBorder();
			bb.setTitle(langName);
			langPanel.updateUI();

			relationTypeLang.setText(rl.getRelationType());
			this.descriptionLang.setText(rl.getDescription());

			this.placeLang.setText(rl.getPlace());
			this.noteTextLang.setText(rl.getNoteText());
			if (rela.getTag().equals("MARR") || rela.getTag().equals("DIV")
					|| rela.getTag().equals("ADOP")) {
				placeLang.setVisible(true);
				scrollNoteLang.setVisible(true);
			} else {
				placeLang.setVisible(false);
				scrollNoteLang.setVisible(false);
			}
		}
		oldLanguage = langcode;
	}

	/**
	 * update language to pojo
	 */
	public void updateLanguage() {
		if (oldLanguage == null)
			return;
		RelationLanguage rl = null;
		boolean toBeDeleted = true;
		for (int i = 0; i < rela.getLanguages().length; i++) {
			if (oldLanguage.equals(rela.getLanguages()[i].getLangCode())) {
				rl = rela.getLanguages()[i];
				break;
			}
		}
		if (rl == null)
			return;
		String tmp = relationTypeLang.getText();
		rl.setRelationType(tmp);

		if (!tmp.isEmpty())
			toBeDeleted = false;

		tmp = descriptionLang.getText();
		rl.setDescription(tmp);
		if (!tmp.isEmpty())
			toBeDeleted = false;
		tmp = placeLang.getText();
		rl.setPlace(tmp);
		if (!tmp.isEmpty())
			toBeDeleted = false;
		tmp = noteTextLang.getText();
		rl.setNoteText(tmp);
		if (!tmp.isEmpty())
			toBeDeleted = false;
		rl.setToBeDeleted(toBeDeleted);
	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentResized(ComponentEvent e) {

		Dimension currSize = getSize();
		int leftWidth = currSize.width - 320;
		int rightColumn = 100 + leftWidth;
		int ry = ytype;
		ok.setBounds(rightColumn, ry, 80, 20);
		ry += 22;
		delete.setBounds(rightColumn, ry, 80, 20);
		ry += 22;
		suretyLbl.setBounds(rightColumn, ry, 100, 20);
		ry += 22;
		surety.setBounds(rightColumn, ry, 100, 20);
		ry += 22;
		createdLbl.setBounds(rightColumn, ry, 100, 20);
		ry += 22;
		created.setBounds(rightColumn, ry, 150, 20);
		ry += 22;
		modifiedLbl.setBounds(rightColumn, ry, 100, 20);
		ry += 22;
		modified.setBounds(rightColumn, ry, 150, 20);

		relationType.setBounds(80, ytype, leftWidth, 20);
		description.setBounds(80, ydesc, leftWidth, 20);
		int ly = ydate;
		if (rela.getTag().equals("MARR") || rela.getTag().equals("DIV")
				|| rela.getTag().equals("ADOP")) {

			date.setBounds(80, ly, 360, 20);
			dateLbl.setBounds(10, ly, 70, 20);

			ly += 24;
			place.setBounds(80, ly, leftWidth, 20);
			placeLbl.setBounds(10, ly, 70, 20);
			ly += 24;
			scrollNote.setBounds(80, ly, leftWidth, 60);
			noteLbl.setBounds(10, ly, 70, 20);
			ly += 64;
		} else if (rela.getTag().equals("NOTE")) {
			scrollNote.setBounds(80, ly, leftWidth, 120);
			noteLbl.setBounds(10, ly, 70, 20);
			ly += 124;

		}
		scrollSource.setBounds(80, ly, leftWidth, 60);
		sourceLbl.setBounds(10, ly, 70, 20);
		ly += 64;
		scrollPrivate.setBounds(80, ly, leftWidth, 60);
		privateLbl.setBounds(10, ly, 70, 20);
		ly += 80;

		int lcnt = Suku.getRepoLanguageCount();
		langLbl.setBounds(10, ly, 70, 20);
		for (int i = 0; i < lcnt; i++) {
			if (langxx[i] == null) {
				System.out.println("seonnull");
			}
			langxx[i].setBounds(80 + (i * 80), ly, 80, 20);
			langxx[i].updateUI();
		}
		ly += 24;
		langPanel.setBounds(5, ly, leftWidth + 100, 180);
		relationTypeLang.setBounds(80, ytypeLang, leftWidth, 20);
		// typeLangLbl.setBounds(10, ly, 70, 20);

		descriptionLang.setBounds(80, ydescLang, leftWidth, 20);
		// descLangLbl.setBounds(10, ly, 70, 20);

		placeLang.setBounds(80, yplaceLang, leftWidth, 20);
		placeLangLbl.setBounds(5, yplaceLang, 70, 20);

		scrollNoteLang.setBounds(80, ynoteLang, leftWidth, 60);
		noteLangLbl.setBounds(5, ynoteLang, 70, 20);
		langPanel.updateUI();

	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void menuCanceled(MenuEvent arg0) {

	}

	@Override
	public void menuDeselected(MenuEvent arg0) {

	}

	@Override
	public void menuSelected(MenuEvent arg0) {
		System.out.println("MENU");
		setVisible(false);

	}

}
