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

public class LanguageDialog extends JDialog implements ActionListener,
		ComponentListener {

	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private JTextField noticeType = null;

	private JTextField description = null;
	private JTextField place = null;
	private JTextArea noteText = null;
	JScrollPane scrollArea = null;
	private JTextField mediaTitle = null;

	private static int ytype = 40;
	private static int ydesc = 64;
	private static int yplace = 88;
	private static int ytitle = 110;
	private static int ynote = 132;

	private JButton ok;
	JLabel createdLbl = null;
	JTextField created = null;
	JLabel modifiedLbl = null;
	JTextField modified = null;

	// public String [] texts = null;
	private UnitLanguage[] languages = null;

	private ButtonGroup languageGroup = null;

	JRadioButton[] langxx;

	public LanguageDialog(JFrame owner) {
		super(owner, Resurses.getString("DATA_LANG_PAGE"), true);
		setLayout(null);

		JLabel lbl = new JLabel(Resurses.getString("DATA_LANG_PAGE"));
		add(lbl);
		lbl.setBounds(10, 5, 75, 20);
		languageGroup = new ButtonGroup();

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
		langxx[0].setSelected(true);

		lbl = new JLabel(Resurses.getString("DATA_TYPE"));
		add(lbl);
		lbl.setBounds(10, ytype, 80, 20);
		noticeType = new JTextField();
		add(noticeType);
		lbl = new JLabel(Resurses.getString("DATA_DESCRIPTION"));
		add(lbl);
		lbl.setBounds(10, ydesc, 80, 20);
		description = new JTextField();
		add(description);
		lbl = new JLabel(Resurses.getString("DATA_PLACE"));
		add(lbl);
		lbl.setBounds(10, yplace, 80, 20);
		place = new JTextField();
		add(place);

		lbl = new JLabel(Resurses.getString("DATA_NOTE"));
		add(lbl);
		lbl.setBounds(10, ynote, 80, 20);

		noteText = new JTextArea();
		noteText.setLineWrap(true);
		scrollArea = new JScrollPane(noteText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollArea);

		lbl = new JLabel(Resurses.getString("DATA_MEDIA_TITLE"));
		add(lbl);
		lbl.setBounds(10, ytitle, 80, 20);
		mediaTitle = new JTextField();
		add(mediaTitle);

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

	public UnitLanguage[] getLanguages() {

		for (int i = 0; i < languages.length; i++) {
			UnitLanguage u = languages[i];
			if (u.getNoticeType() == null && u.getMediaTitle() == null
					&& u.getDescription() == null && u.getPlace() == null) {
				u.setToBeDeleted(true);
			}
		}

		return languages;
	}

	public void setLanguages(UnitLanguage[] languages) {

		if (languages != null) {
			for (int i = 0; i < languages.length; i++) {
				int idx = Suku.getRepoLanguageIndex(languages[i].getLangCode());
				if (idx >= 0) {
					this.languages[idx] = languages[i];
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
					break;
				}
			}

			// this.languages = languages;
		}
	}

	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();

		rootPane.registerKeyboardAction(this, "OK", stroke,
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		langTextToPojo(cmd);
		if (cmd.equals("OK")) {
			setVisible(false);
		}
	}

	private String previousLang = null;

	public void langTextToPojo(String cmd) {

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

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentResized(ComponentEvent e) {

		// String firstEnabledLangu=null;

		for (int i = 0; i < langxx.length; i++) {
			int j = 0;
			for (j = 0; j < languages.length; j++) {
				if (Suku.getRepoLanguage(i, true).equals(
						languages[j].getLangCode())) {
					break;
				}
			}
			// boolean enableThis = j != languages.length;

			// if (firstEnabledLangu==null && enableThis) {
			// firstEnabledLangu = Suku.getRepoLanguage(i,true);
			// // langxx[i].setSelected(true);
			//				
			// }

		}

		Dimension currSize = getSize();
		int leftWidth = currSize.width - 320;
		int rightColumn = 120 + leftWidth;

		int ry = ytype;
		ok.setBounds(rightColumn, ry, 80, 20);
		ry += 22;
		// ok.setBounds(rightColumn,ry,80,20);
		// ry += 22;
		createdLbl.setBounds(rightColumn, ry, 100, 20);
		ry += 22;
		created.setBounds(rightColumn, ry, 150, 20);
		ry += 22;
		modifiedLbl.setBounds(rightColumn, ry, 100, 20);
		ry += 22;
		modified.setBounds(rightColumn, ry, 150, 20);

		noticeType.setBounds(100, ytype, leftWidth, 20);
		description.setBounds(100, ydesc, leftWidth, 20);
		place.setBounds(100, yplace, leftWidth, 20);
		mediaTitle.setBounds(100, ytitle, leftWidth, 20);

		scrollArea.setBounds(100, ynote, leftWidth, currSize.height - ynote
				- 40);
		scrollArea.updateUI();

		// if (firstEnabledLangu != null) {
		// langTextToPojo(firstEnabledLangu);
		// }
	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	private String nv(String text) {
		if (text == null)
			return "";
		return text;
	}
}
