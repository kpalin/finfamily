package fi.kaila.suku.swing.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.pojo.UnitLanguage;

/**
 * Language dialog to add secondary languages to person notices.
 * 
 * @author Kalle
 */
public class LanguageDialog extends JDialog implements ActionListener,
		ComponentListener {

	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private String tag = null;
	private JTextField noticeType = null;

	private JTextField description = null;
	private JTextField place = null;
	private JTextArea noteText = null;

	private JLabel typeLbl = null;
	private JLabel descLbl = null;
	private JLabel placeLbl = null;
	private JLabel noteLbl = null;
	private JLabel mediaLbl = null;

	/** The scroll area. */
	JScrollPane scrollArea = null;
	private JTextField mediaTitle = null;

	private static int ytype = 40;
	// private static int ydesc = 64;
	private static int yplace = 88;
	private static int ytitle = 110;
	// private static int ynote = 132;

	private JButton ok;

	/** The created lbl. */
	JLabel createdLbl = null;

	/** The created. */
	JTextField created = null;

	/** The modified lbl. */
	JLabel modifiedLbl = null;

	/** The modified. */
	JTextField modified = null;

	// public String [] texts = null;
	private UnitLanguage[] languages = null;

	/** The langxx. */
	JRadioButton[] langxx;

	/**
	 * Instantiates a new language dialog.
	 * 
	 * @param owner
	 *            the owner
	 */
	public LanguageDialog(JFrame owner) {
		super(owner, Resurses.getString("DATA_LANG_PAGE"), true);
		setLayout(null);

		JLabel lbll = new JLabel(Resurses.getString("DATA_LANG_PAGE"));
		add(lbll);
		lbll.setBounds(10, 5, 75, 20);
		ButtonGroup languageGroup = new ButtonGroup();

		int lcnt = Suku.getRepoLanguageCount();
		langxx = new JRadioButton[lcnt];
		languages = new UnitLanguage[lcnt];
		for (int i = 0; i < lcnt; i++) {

			langxx[i] = new JRadioButton(Suku.getRepoLanguage(i, false));
			langxx[i].setActionCommand(Suku.getRepoLanguage(i, true));

			langxx[i].setBounds(100 + (i * 80), 5, 80, 20);
			langxx[i].addActionListener(this);
			languageGroup.add(langxx[i]);
			add(langxx[i]);
			languages[i] = new UnitLanguage(Suku.getRepoLanguage(i, true));

		}
		// langxx[0].setSelected(true);

		typeLbl = new JLabel(Resurses.getString("DATA_TYPE"));
		add(typeLbl);

		noticeType = new JTextField();
		add(noticeType);
		descLbl = new JLabel(Resurses.getString("DATA_DESCRIPTION"));
		add(descLbl);

		description = new JTextField();
		add(description);
		placeLbl = new JLabel(Resurses.getString("DATA_PLACE"));
		add(placeLbl);

		place = new JTextField();
		add(place);

		noteLbl = new JLabel(Resurses.getString("DATA_NOTE"));
		add(noteLbl);

		noteText = new JTextArea();
		noteText.setLineWrap(true);
		noteText.setWrapStyleWord(true);
		scrollArea = new JScrollPane(noteText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollArea);

		mediaLbl = new JLabel(Resurses.getString("DATA_MEDIA_TITLE"));
		add(mediaLbl);

		mediaTitle = new JTextField();
		add(mediaTitle);

		enableFields(false);
		addComponentListener(this);

		ok = new JButton(Resurses.getString("CLOSE"));
		ok.setActionCommand("OK");
		ok.addActionListener(this);
		getRootPane().setDefaultButton(ok);
		add(ok);

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

	}

	/**
	 * Gets the languages.
	 * 
	 * @return list of UnitLanguage notices
	 */
	public UnitLanguage[] getLanguages() {

		for (int i = 0; i < languages.length; i++) {
			UnitLanguage u = languages[i];
			if (u.getNoticeType() == null && u.getMediaTitle() == null
					&& u.getDescription() == null && u.getPlace() == null
					&& u.getNoteText() == null) {
				u.setToBeDeleted(true);
			}
		}

		return languages;
	}

	/**
	 * Sets the languages.
	 * 
	 * @param tag
	 *            the tag
	 * @param languages
	 *            to be initialized
	 */
	public void setLanguages(String tag, UnitLanguage[] languages) {
		this.tag = tag;
		if (languages != null) {
			for (int i = 0; i < languages.length; i++) {
				int idx = Suku.getRepoLanguageIndex(languages[i].getLangCode());
				if (idx >= 0) {
					this.languages[idx] = languages[i];
					if (languages[i].getPid() > 0
							|| languages[i].isToBeUpdated())
						langxx[idx].setForeground(Color.RED);
				} else {
					logger.warning("language code not known ["
							+ languages[i].getLangCode() + "]");
				}
			}

			for (int i = 0; i < Suku.getRepoLanguageCount(); i++) {
				if (this.languages[i].getPnid() > 0) {
					langxx[i].setSelected(true);
					langTextToPojo(this.languages[i].getLangCode());
					enableFields(true);
					break;
				}
			}

			// this.languages = languages;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JDialog#createRootPane()
	 */
	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();

		rootPane.registerKeyboardAction(this, "OK", stroke,
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		langTextToPojo(cmd);
		if (cmd.equals("OK")) {
			setVisible(false);
		} else {
			enableFields(true);
		}
	}

	private String previousLang = null;

	/**
	 * Copy data from dialog to the pojo.
	 * 
	 * @param cmd
	 *            = langcode
	 */
	public void langTextToPojo(String cmd) {
		// if (previousLang == null) {
		// previousLang = languages[0].getLangCode();
		// }

		if (previousLang != null) {
			int langIdx = 0;
			for (int i = 0; i < languages.length; i++) {
				if (previousLang.equals(languages[i].getLangCode())) {
					langIdx = i;
					break;
				}
			}

			String tmp;
			tmp = noticeType.getText();
			languages[langIdx].setNoticeType(tmp);

			tmp = description.getText();
			languages[langIdx].setDescription(tmp);

			tmp = place.getText();
			languages[langIdx].setPlace(tmp);

			tmp = noteText.getText();
			languages[langIdx].setNoteText(tmp);

			tmp = mediaTitle.getText();
			languages[langIdx].setMediaTitle(tmp);

		}

		if (cmd != null) {

			int langIdx = -1;
			for (int i = 0; i < languages.length; i++) {
				if (cmd.equals(languages[i].getLangCode())) {
					langIdx = i;
					break;
				}
			}
			if (langIdx >= 0) {
				previousLang = cmd;
				String tmp = "";
				if (languages[langIdx].getCreated() != null) {
					tmp = languages[langIdx].getCreated().toString();
				}
				created.setText(tmp);
				tmp = "";
				if (languages[langIdx].getModified() != null) {
					tmp = languages[langIdx].getModified().toString();
				}
				modified.setText(tmp);
				noticeType.setText(languages[langIdx].getNoticeType());
				description.setText(languages[langIdx].getDescription());
				place.setText(languages[langIdx].getPlace());
				noteText.setText(languages[langIdx].getNoteText());
				mediaTitle.setText(languages[langIdx].getMediaTitle());
			}
		}
	}

	private void enableFields(boolean value) {
		noticeType.setEnabled(value);
		description.setEnabled(value);
		place.setEnabled(value);
		noteText.setEnabled(value);
		mediaTitle.setEnabled(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentMoved(ComponentEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent e) {

		for (int i = 0; i < langxx.length; i++) {
			int j = 0;
			for (j = 0; j < languages.length; j++) {
				if (Suku.getRepoLanguage(i, true).equals(
						languages[j].getLangCode())) {
					break;
				}
			}
		}

		Dimension currSize = getSize();
		int leftWidth = currSize.width - 260;
		int rightColumn = 85 + leftWidth;

		int ry = ytype;
		ok.setBounds(rightColumn, ry, 80, 20);
		ry += 22;

		createdLbl.setBounds(rightColumn, ry, 100, 20);
		ry += 22;
		created.setBounds(rightColumn, ry, 150, 20);
		ry += 22;
		modifiedLbl.setBounds(rightColumn, ry, 100, 20);
		ry += 22;
		modified.setBounds(rightColumn, ry, 150, 20);
		int yl = ytype;
		if (!tag.equals("NOTE")) {
			typeLbl.setBounds(5, yl, 70, 20);
			noticeType.setBounds(80, yl, leftWidth, 20);
			yl += 24;
			descLbl.setBounds(5, yl, 70, 20);
			description.setBounds(80, yl, leftWidth, 20);
			yl += 24;
			if (!tag.equals("NAME")) {
				placeLbl.setBounds(5, yplace, 70, 20);
				place.setBounds(80, yl, leftWidth, 20);
				yl += 24;
				mediaLbl.setBounds(5, ytitle, 70, 20);
				mediaTitle.setBounds(80, yl, leftWidth, 20);
				yl += 24;
			} else {

				place.setVisible(false);
				mediaTitle.setVisible(false);
				scrollArea.setVisible(false);
				placeLbl.setVisible(false);
				mediaLbl.setVisible(false);
				noteLbl.setVisible(false);
			}

		} else {
			typeLbl.setVisible(false);
			descLbl.setVisible(false);
			placeLbl.setVisible(false);
			mediaLbl.setVisible(false);
			noticeType.setVisible(false);
			description.setVisible(false);
			place.setVisible(false);
			mediaTitle.setVisible(false);
		}
		noteLbl.setBounds(5, yl, 70, 20);
		scrollArea.setBounds(80, yl, leftWidth, currSize.height - yl - 40);
		scrollArea.updateUI();
		// if (firstEnabledLangu != null) {
		// langTextToPojo(firstEnabledLangu);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentShown(ComponentEvent e) {

	}

}
