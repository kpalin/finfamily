package fi.kaila.suku.swing.panel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateException;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTextArea;
import fi.kaila.suku.util.SukuTextField;
import fi.kaila.suku.util.SukuTextField.Field;
import fi.kaila.suku.util.SukuTypesModel;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationLanguage;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitLanguage;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * Main pane is the first of person windows panes with person main data.
 * 
 * @author Kalle
 */
public class PersonMainPane extends JPanel implements ActionListener,
		ComponentListener {

	private static final long serialVersionUID = 1L;

	/** The person view. */
	PersonView personView = null;
	private JButton close;
	private JButton update;
	private static Logger logger = Logger.getLogger(PersonMainPane.class
			.getName());

	private JCheckBox privacy;
	private String[] sextexts = { "Mies", "Nainen", "Tuntematon" };
	private final String[] sexes = { "M", "F", "U" };
	private ButtonGroup sexGroup = null;
	// private JComboBox sex;
	private JPanel sexr;
	private SukuTextField groupid;
	private JTextField refn;

	private JTextField created;
	private JTextField modified;

	private SukuTextField givenname;
	private SukuTextField patronym;
	// private JTextField prefix;
	private SukuTextField surname;
	private JTextField postfix;

	private JTextField birtDate;
	// private JTextField birtPlace;
	private SukuTextField birtPlace;
	private JTextField chrDate;
	private SukuTextField chrPlace;
	private JTextField deatDate;
	private SukuTextField deatPlace;
	private JTextField buriDate;
	private SukuTextField buriPlace;
	private SukuTextField occupation;

	private JTextArea notetext;
	private SukuTextArea source;
	private JTextArea privateText;

	private JScrollPane scrollNote;
	private JScrollPane scrollSource;
	private JScrollPane scrollPrivate;

	private JLabel groupLbl;
	private JLabel refnLbl;
	private JLabel createdLbl;
	private JLabel modifiedLbl;

	private final boolean listPanels = false;
	private int personPid = 0;

	/** The pers long. */
	PersonLongData persLong = null;

	/** The relas. */
	RelativesPane relas = null;

	/**
	 * Instantiates a new person main pane.
	 * 
	 * @param peronView
	 *            the peron view
	 * @param pid
	 *            the pid
	 * @throws SukuException
	 *             the suku exception
	 */
	public PersonMainPane(PersonView peronView, int pid) throws SukuException {
		this.personView = peronView;

		SukuTypesModel types = Utils.typeInstance();

		PersonView.types = new String[types.getTypesTagsCount()];
		PersonView.typesTexts.clear();
		addComponentListener(this);
		for (int i = 0; i < types.getTypesTagsCount(); i++) {

			String tag = types.getTypesTag(i);
			PersonView.types[i] = tag;
			String value = types.getTypesName(i);
			PersonView.typesTexts.put(tag, value);
		}

		initMe();

	}

	/**
	 * Gets the person pid.
	 * 
	 * @return the person pid
	 */
	int getPersonPid() {
		return personPid;
	}

	/**
	 * Update name.
	 */
	void updateName() {

		boolean firstName = true;
		boolean specialName = false;

		String fgiven = "";
		String fpatro = "";
		String fsurname = "";
		String prexi = "";
		String fposti = "";

		int noticeFirst = personView.getFirstNoticeIndex();
		int noticeCount = personView.getTabCount();
		givenname.setText("");
		patronym.setText("");
		surname.setText("");
		postfix.setText("");
		int privacyCount = 0;
		StringBuilder comboname = new StringBuilder();
		for (int i = noticeFirst; i < noticeCount; i++) {
			// Object oo = personView.getPane(i).pnl;
			NoticePane pane = (NoticePane) personView.getPane(i).pnl;
			if (pane.notice.getTag().equals("NAME")) {
				if (!pane.notice.isToBeDeleted()) {

					if (!pane.isPlain()) {
						privacyCount++;
					}

					prexi = pane.prefix.getText();

					if (firstName) {
						fgiven = pane.givenname.getText();
						fpatro = pane.patronym.getText();
						fposti = pane.postfix.getText();
						fsurname = toFullSurname(pane.prefix.getText(),
								pane.surname.getText());
						givenname.setText(fgiven);
						patronym.setText(fpatro);
						surname.setText(fsurname);

						if (!prexi.isEmpty()) {
							if (Utils.isKnownPrefix(prexi) == 0) {
								specialName = true;
							}
						}

						comboname.append(fsurname);

						postfix.setText(pane.postfix.getText());

						firstName = false;
					} else {
						if (!fposti.equals(pane.postfix.getText())) {
							specialName = true;
						}
						if (specialName) {

							break;
						}
						if (!fgiven.equals(pane.givenname.getText())) {
							specialName = true;
							break;
						}
						if (!fpatro.equals(pane.patronym.getText())) {
							specialName = true;
							break;
						}

						if (!prexi.isEmpty()) {

							if (Utils.isKnownPrefix(prexi) == 0) {
								specialName = true;
							}
						}
						String xsurname = toFullSurname(pane.prefix.getText(),
								pane.surname.getText());

						comboname.append(";" + xsurname);
					}
				}
			}
		}
		if (specialName) {
			privacyCount++;
		}
		givenname.setEnabled(privacyCount == 0);
		patronym.setEnabled(privacyCount == 0);
		// prefix.setEnabled(!specialName);
		surname.setEnabled(specialName == false || privacyCount == 0);
		postfix.setEnabled(privacyCount == 0);
		if (privacyCount == 0) {
			String tmp = comboname.toString();
			surname.setText(tmp);

		}

	}

	private String toFullSurname(String prefix, String surname) {
		if (prefix == null)
			return surname;
		if (prefix.isEmpty()) {
			return surname.trim();
		} else {

			if (surname.isEmpty()) {
				return prefix.trim();
			} else {
				return prefix.trim() + " " + surname.trim();
			}
		}
	}

	/**
	 * Update rest.
	 */
	void updateRest() {
		int birtCount = 0;
		int chrCount = 0;
		int deatCount = 0;
		int buriCount = 0;
		int occuCount = 0;
		int noteCount = 0;
		Vector<String> occuVec = new Vector<String>();
		int noticeFirst = personView.getFirstNoticeIndex();
		int noticeCount = personView.getTabCount();
		for (int i = noticeFirst; i < noticeCount; i++) {
			NoticePane pane = (NoticePane) personView.getPane(i).pnl;

			if (pane.notice.getTag().equals("BIRT")) {
				birtCount++;
				if (!pane.date.isPlain()) {
					birtCount++;
				}
				if (pane.notice.isToBeDeleted()) {
					birtDate.setText("");
					birtPlace.setText("");
				} else {

					birtDate.setText(pane.date.getTextFromDate());
					birtPlace.setText(pane.place.getText());
					if (!pane.isPlain())
						birtCount++;
				}
				if (birtDate.getText().equals("")
						&& birtPlace.getText().equals("")) {
					birtCount++;
				}
			}

			if (pane.notice.getTag().equals("CHR")) {
				chrCount++;
				if (!pane.date.isPlain()) {
					chrCount++;
				}
				if (pane.notice.isToBeDeleted()) {
					chrDate.setText("");
					chrPlace.setText("");
				} else {
					chrDate.setText(pane.date.getTextFromDate());
					chrPlace.setText(pane.place.getText());
					if (!pane.isPlain())
						chrCount++;
				}
				if (chrDate.getText().equals("")
						&& chrPlace.getText().equals("")) {
					chrCount++;
				}
			}

			if (pane.notice.getTag().equals("DEAT")) {
				deatCount++;
				if (!pane.date.isPlain()) {
					deatCount++;
				}
				if (pane.notice.isToBeDeleted()) {
					deatDate.setText("");
					deatPlace.setText("");
				} else {

					deatDate.setText(pane.date.getTextFromDate());
					deatPlace.setText(pane.place.getText());
					if (!pane.isPlain())
						deatCount++;
				}
				if (deatDate.getText().equals("")
						&& deatPlace.getText().equals("")) {
					deatCount++;
				}
			}

			if (pane.notice.getTag().equals("BURI")) {
				buriCount++;
				if (!pane.date.isPlain()) {
					buriCount++;
				}
				if (pane.notice.isToBeDeleted()) {
					buriDate.setText("");
					buriPlace.setText("");
				} else {
					buriDate.setText(pane.date.getTextFromDate());
					buriPlace.setText(pane.place.getText());
					if (!pane.isPlain())
						buriCount++;
				}
				if (buriDate.getText().equals("")
						&& buriPlace.getText().equals("")) {
					buriCount++;
				}
			}

			if (pane.notice.getTag().equals("OCCU")) {

				occuVec.add(pane.description.getText());

				occuCount++;

				if (!pane.isPlain())
					occuCount += 100;
			}
			if (pane.notice.getTag().equals("NOTE")) {
				noteCount++;
				if (noteCount == 1) {

					notetext.setText(pane.noteText.getText());

				}
				if (!pane.isPlain())
					noteCount++;
			}
		}

		StringBuilder oc = new StringBuilder();
		for (int i = 0; i < occuVec.size(); i++) {
			if (oc.length() > 0) {
				oc.append(";");
			}
			oc.append(occuVec.get(i));
		}
		occupation.setText(oc.toString());

		birtDate.setEnabled(birtCount <= 1);
		birtPlace.setEnabled(birtCount <= 1);
		chrDate.setEnabled(chrCount <= 1);
		chrPlace.setEnabled(chrCount <= 1);
		deatDate.setEnabled(deatCount <= 1);
		deatPlace.setEnabled(deatCount <= 1);
		buriDate.setEnabled(buriCount <= 1);
		buriPlace.setEnabled(buriCount <= 1);
		occupation.setEnabled(occuCount < 100);
		notetext.setEnabled(noteCount <= 1);
	}

	private final int lcol = 75;

	private final int rwidth = 70;
	// private int rcol = lwidth+lcol+10;

	private final int gnlen = 150;
	private final int postlen = 60;
	private final int surlen = 240;
	private final int datelen = 80;
	private final int colbet = 2;
	private final int placlen = 213;
	private final int rcol = lcol + datelen + placlen + colbet * 2; // 460; //
	private final int biglen = datelen + placlen + colbet * 2 + rwidth * 2;

	private void initMe() {

		setLayout(null);

		JLabel lbl;
		int rivi = 10;
		int rrivi = 30;

		privacy = new JCheckBox(Resurses.getString("DATA_PRIVACY"));
		privacy.setBounds(rcol, rrivi, 100, 20);
		add(privacy);

		rrivi += 24;

		groupLbl = new JLabel(Resurses.getString("DATA_GROUP"));
		add(groupLbl);
		groupLbl.setBounds(rcol, rrivi, 100, 20);
		rrivi += 20;
		groupid = new SukuTextField(null, Field.Fld_Group);
		add(groupid);
		groupid.setBounds(rcol, rrivi, rwidth * 2, 20);
		// groupid.setEditable(false);

		rrivi += 24;

		refnLbl = new JLabel(Resurses.getString("DATA_REFN"));
		add(refnLbl);
		refnLbl.setBounds(rcol, rrivi, 100, 20);
		rrivi += 20;
		refn = new JTextField();
		add(refn);
		refn.setBounds(rcol, rrivi, rwidth * 2, 20);

		rrivi += 20;
		createdLbl = new JLabel(Resurses.getString("DATA_PERSON_CREATED"));
		createdLbl.setBounds(rcol, rrivi, 200, 20);
		add(createdLbl);
		rrivi += 20;
		created = new JTextField();
		created.setBounds(rcol, rrivi, rwidth * 2, 20);
		created.setEditable(false);
		add(created);

		rrivi += 28;
		modifiedLbl = new JLabel(Resurses.getString("DATA_PERSON_MODIFIED"));
		modifiedLbl.setBounds(rcol, rrivi, 200, 20);
		add(modifiedLbl);
		rrivi += 20;
		modified = new JTextField();
		modified.setBounds(rcol, rrivi, rwidth * 2, 20);
		modified.setEditable(false);
		add(modified);

		rrivi += 22;

		close = new JButton(Resurses.getString(Resurses.CLOSE));

		add(this.close);
		close.setActionCommand(Resurses.CLOSE);
		close.addActionListener(this);
		close.setBounds(rcol, rrivi, rwidth, 24);

		update = new JButton(Resurses.getString(Resurses.UPDATE));

		add(this.update);
		update.setActionCommand(Resurses.UPDATE);
		update.addActionListener(this);
		update.setBounds(rcol + rwidth, rrivi, rwidth, 24);

		lbl = new JLabel(Resurses.getString("DATA_GIVENNAME"));
		add(lbl);
		lbl.setBounds(lcol, rivi, gnlen, 20);

		lbl = new JLabel(Resurses.getString("DATA_PATRONYM"));
		add(lbl);
		lbl.setBounds(lcol + gnlen, rivi, gnlen, 20);
		rivi += 20;
		lbl = new JLabel(Resurses.getString("DATA_NAME"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 20);

		givenname = new SukuTextField(null, Field.Fld_Givenname);
		add(givenname);
		givenname.setBounds(lcol, rivi, gnlen, 20);

		patronym = new SukuTextField(null, Field.Fld_Patronyme);
		add(patronym);
		patronym.setBounds(lcol + gnlen + colbet, rivi, gnlen - 5, 20);

		rivi += 24;
		// lbl = new JLabel(Resurses.getString("DATA_PREFIX"));
		// add(lbl);
		// lbl.setBounds(lcol, rivi, prelen, 20);
		lbl = new JLabel(Resurses.getString("DATA_SURNAME"));
		add(lbl);
		lbl.setBounds(lcol, rivi, 80, 20);
		lbl = new JLabel(Resurses.getString("DATA_POSTFIX"));
		add(lbl);
		lbl.setBounds(lcol + surlen + colbet, rivi, 80, 20);
		rivi += 20;
		// prefix = new JTextField();
		// add(prefix);
		// prefix.setBounds(lcol, rivi, prelen, 20);

		surname = new SukuTextField(null, Field.Fld_Surname);
		add(surname);
		surname.setBounds(lcol, rivi, surlen, 20);

		postfix = new JTextField();
		add(postfix);
		postfix.setBounds(lcol + surlen + colbet, rivi, postlen - 10, 20);

		rivi += 20;
		lbl = new JLabel(Resurses.getString("DATA_SEPARATE_SURNAMES"));
		add(lbl);
		lbl.setBounds(lcol, rivi, 300, 20);
		rivi += 20;

		lbl = new JLabel(Resurses.getString("DATA_SEX"));
		add(lbl);
		lbl.setBounds(10, rivi, 80, 20);
		sextexts = Resurses.getString("DATA_SEXES").split(";");
		// sex = new JComboBox(sextexts);
		// add(sex);
		// sex.setBounds(lcol + 300, rivi, 80, 20);

		sexr = new JPanel();
		add(sexr);
		sexr.setBounds(lcol, rivi - 8, 300, 24);

		sexr.setLayout(new FlowLayout(FlowLayout.LEFT));
		sexGroup = new ButtonGroup();
		for (int i = 0; i < sextexts.length; i++) {

			JRadioButton rb = new JRadioButton(sextexts[i]);
			sexr.add(rb);
			if (i == 0) {
				rb.setSelected(true);
			}
			rb.setActionCommand(sexes[i]);
			sexGroup.add(rb);

		}

		rivi += 24;
		lbl = new JLabel(Resurses.getString("DATA_DATE"));
		add(lbl);
		lbl.setBounds(lcol, rivi, 100, 20);
		lbl = new JLabel(Resurses.getString("DATA_PLACE"));
		add(lbl);
		lbl.setBounds(lcol + datelen + colbet, rivi, 100, 20);
		rivi += 20;
		lbl = new JLabel(Resurses.getString("DATA_BIRT"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 20);

		birtDate = new JTextField();
		add(birtDate);
		birtDate.setBounds(lcol, rivi, datelen, 20);
		birtPlace = new SukuTextField(null, Field.Fld_Place);

		add(birtPlace);
		birtPlace.setBounds(lcol + datelen + colbet, rivi, placlen, 20);

		rivi += 24;
		lbl = new JLabel(Resurses.getString("DATA_CHR"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 20);
		chrDate = new JTextField();
		add(chrDate);
		chrDate.setBounds(lcol, rivi, datelen, 20);
		chrPlace = new SukuTextField(null, Field.Fld_Place);
		add(chrPlace);
		chrPlace.setBounds(lcol + datelen + colbet, rivi, placlen, 20);

		rivi += 24;
		lbl = new JLabel(Resurses.getString("DATA_DEAT"));
		add(lbl);
		lbl.setBounds(10, rivi, datelen, 20);
		deatDate = new JTextField();
		add(deatDate);
		deatDate.setBounds(lcol, rivi, datelen, 20);
		deatPlace = new SukuTextField(null, Field.Fld_Place);
		add(deatPlace);
		deatPlace.setBounds(lcol + datelen + colbet, rivi, placlen, 20);

		rivi += 24;
		lbl = new JLabel(Resurses.getString("DATA_BURI"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 20);
		buriDate = new JTextField();
		add(buriDate);
		buriDate.setBounds(lcol, rivi, datelen, 20);
		buriPlace = new SukuTextField(null, Field.Fld_Place);
		add(buriPlace);
		buriPlace.setBounds(lcol + datelen + colbet, rivi, placlen, 20);

		rivi += 24;
		lbl = new JLabel(Resurses.getString("DATA_SEPARATE_OCCU"));
		add(lbl);
		lbl.setBounds(lcol, rivi, 300, 20);
		rivi += 20;
		lbl = new JLabel(Resurses.getString("DATA_OCCU"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 20);

		occupation = new SukuTextField("OCCU", Field.Fld_Description);
		add(occupation);
		occupation.setBounds(lcol, rivi, datelen + placlen + colbet, 20);

		rivi += 24;

		lbl = new JLabel(Resurses.getString("DATA_NOTE"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 20);
		notetext = new JTextArea();
		notetext.setLineWrap(true);
		notetext.setWrapStyleWord(true);
		scrollNote = new JScrollPane(notetext,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollNote);
		scrollNote.setBounds(lcol, rivi, biglen, 80);

		rivi += 85;

		lbl = new JLabel(Resurses.getString("DATA_SOURCE"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 20);
		source = new SukuTextArea();

		source.setLineWrap(true);
		source.setWrapStyleWord(true);
		scrollSource = new JScrollPane(source,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollSource);
		scrollSource.setBounds(lcol, rivi, biglen, 80);

		rivi += 85;

		lbl = new JLabel(Resurses.getString("DATA_PRIVATETEXT"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 40);
		privateText = new JTextArea();

		privateText.setLineWrap(true);
		privateText.setWrapStyleWord(true);
		scrollPrivate = new JScrollPane(privateText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollPrivate);
		scrollPrivate.setBounds(lcol, rivi, biglen, 80);

		rivi += 100;

		setPreferredSize(new Dimension(lcol + biglen + colbet, rivi + 30));

		// update = new JButton(Resurses.getString(Resurses.UPDATE));
		//
		// add(update);
		// update.setActionCommand(Resurses.UPDATE);
		// update.addActionListener(this);
		// update.setBounds(120, rivi, 100, 24);

	}

	private void setRadioButton(ButtonGroup g, String name) {
		Enumeration<AbstractButton> e = g.getElements();
		while (e.hasMoreElements()) {
			JRadioButton rr = (JRadioButton) e.nextElement();
			if (name.equals(rr.getActionCommand())) {
				rr.setSelected(true);
				break;
			}
		}
	}

	/**
	 * Close notices.
	 * 
	 * @return true, if successful
	 */
	boolean closeNotices() {

		personView.closePerson();
		persLong = null;
		relas = null;
		return true;

	}

	/**
	 * Open person notices.
	 * 
	 * @param pid
	 *            the pid
	 * @throws SukuException
	 *             the suku exception
	 */
	void openPersonNotices(int pid) throws SukuException {
		Relation[] relations = null;
		PersonShortData[] persons = null;
		personPid = pid;
		if (pid == 0) {
			persLong = new PersonLongData(0, "INDI", "M");
			persLong.setNotices(new UnitNotice[0]);
			relations = new Relation[0];
			persons = new PersonShortData[0];
		} else {
			SukuData plong = Suku.kontroller.getSukuData("cmd=person", "pid="
					+ pid);
			persLong = plong.persLong;
			relations = plong.relations;
			persons = plong.pers;

		}

		String currSex = persLong.getSex();
		if (currSex == null) {
			throw new SukuException(Resurses.getString("DATA_SEX_NULL"));
		}
		// int sexidx = 0;
		// for (int i = 0; i < sexes.length; i++) {
		// if (currSex.equals(sexes[i])) {
		// sexidx = i;
		// }
		// }
		// // sex.setSelectedIndex(sexidx);

		setRadioButton(sexGroup, currSex);

		privacy.setSelected(persLong.getPrivacy() != null);

		groupid.setText(persLong.getGroupId());

		// groupid.setEditable(persLong.getPid() == 0);
		refn.setText(persLong.getRefn());

		String tmp;
		if (persLong.getCreated() == null) {
			tmp = "";
		} else {
			tmp = persLong.getCreated().toString();
		}

		created.setText(tmp);
		if (persLong.getModified() == null) {
			tmp = "";
		} else {
			tmp = persLong.getModified().toString();
		}
		modified.setText(tmp);

		if (persLong.getSource() == null) {
			tmp = "";
		} else {
			tmp = persLong.getSource();
		}
		source.setText(tmp);

		if (persLong.getPrivateText() == null) {
			tmp = "";
		} else {
			tmp = persLong.getPrivateText();
		}
		privateText.setText(tmp);

		relas = RelativesPane.getInstance(personView, persLong, relations,
				persons);

		personView.addTab(new SukuTabPane("TAB_RELATIVES", relas));

		for (int i = 0; i < persLong.getNotices().length; i++) {
			NoticePane pane = new NoticePane(personView, persLong.getPid(),
					persLong.getNotices()[i]);
			String tag = persLong.getNotices()[i].getTag();
			String nimi = personView.getTypeValue(tag);
			if (nimi == null) {
				nimi = tag;
			}
			// String aputext = pane.noteText.getText();
			// System.out.println("NOTE on "+pane.notice.getTag()+"["+i+"]:"+aputext);
			personView.addTab(new SukuTabPane(nimi, pane, tag));

		}
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
		try {
			if (cmd.equals(Resurses.CLOSE)) {
				try {
					if (persLong == null)
						return;

					updateNameNotices();
					updateRestNotices();
					SukuData chnged = null;
					try {
						chnged = checkIfPersonStructureChanged();
						if (chnged == null)
							return;
					} catch (SukuDateException ee) {
						JOptionPane.showMessageDialog(this, ee.getMessage());
						return;
					}

					// return resp.resu == null ? false : true;

					// boolean hasChanged = hasPersonChanged();
					if (chnged.resu != null) {

						int askresu = JOptionPane.showConfirmDialog(this,
								Resurses.getString("ASK_SAVE_PERSON"),
								Resurses.getString(Resurses.SUKU),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (askresu == JOptionPane.YES_OPTION) {
							SukuData resp = updatePerson(false);
							logger.fine("Close response:" + resp.resu);
						}
					} else if (chnged.resuCount > 0) {
						SukuData resp = updatePerson(true);
						logger.fine("Order response:" + resp.resu);
					}

					personView.closeMainPane(false);

				} catch (SukuDateException e1) {
					JOptionPane.showMessageDialog(this, e1.getMessage(),
							Resurses.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);
					logger.log(Level.WARNING, "CLOSE:" + e1.getMessage());
					return;
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, e1.toString(),
							Resurses.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);
					logger.log(Level.WARNING, "Closing person", e1);

					personView.closeMainPane(false);
					return;
				}
			} else if (cmd.equals(Resurses.UPDATE)) {
				try {

					// SukuData chnged = null;
					// try {
					// chnged = checkIfPersonStructureChanged();
					// if (chnged == null)
					// return;
					// } catch (SukuDateException ee) {
					// JOptionPane.showMessageDialog(this, ee.getMessage());
					// return;
					// }

					updateNameNotices();
					updateRestNotices();

					listNoticePanels();

					SukuData resp = updatePerson(false);
					personView.closeMainPane(true);
					logger.fine("Close response:" + resp.resu);

					// if (cmd.equals(Resurses.UPDATE)){
					// personView.displayPersonPane(myPid);
					// }
					// if (resp.pers != null) {
					// personView.getSuku().updatePerson(resp.pers[0]);
					//
					// }
				} catch (SukuDateException e1) {
					JOptionPane.showMessageDialog(this, e1.getMessage(),
							Resurses.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);
					logger.log(Level.WARNING, "CLOSE:" + e1.getMessage());
					return;
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, e1.toString(),
							Resurses.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);
					logger.log(Level.WARNING, "Closing person", e1);

					personView.closeMainPane(false);
					return;
				}
			}
		} catch (Exception ex) {
			logger.log(Level.WARNING, "PersonMain action", e);
			JOptionPane.showMessageDialog(personView.getSuku(),
					"PersonMain action" + ":" + ex.getMessage());

		}
	}

	/**
	 * Update person data structure from pane fields and check if it has
	 * changed.
	 * 
	 * @return true is person data has changed
	 * @throws SukuDateException
	 *             the suku date exception
	 */
	SukuData checkIfPersonStructureChanged() throws SukuDateException {
		if (persLong == null)
			return null;
		// listNoticePanels();
		SukuData resp = null;
		// personView.updateNotices();
		listNoticePanels();
		int noticeFirst = personView.getFirstNoticeIndex();
		int tabCount = personView.getTabCount();

		boolean foundModification = false;
		boolean orderModification = false;
		// String newSex = sexes[sex.getSelectedIndex()];
		// persLong.setSex(newSex);

		ButtonModel model = sexGroup.getSelection();
		if (model != null) {
			persLong.setSex(model.getActionCommand());
		}

		String priva = privacy.isSelected() ? "P" : null;
		persLong.setPrivacy(priva);

		// if (persLong.getPid() == 0) {
		String grp = groupid.getText();
		persLong.setGroupId(grp);
		// }

		String rf = refn.getText();
		persLong.setUserRefn(rf);

		String sou = source.getText();
		persLong.setSource(sou);

		String prit = privateText.getText();
		persLong.setPrivateText(prit);

		if (persLong.getNotices().length != tabCount - noticeFirst) {
			persLong.setOrderModified();
			foundModification = true;
		} else {
			for (int i = noticeFirst; i < tabCount; i++) {
				NoticePane npane = (NoticePane) personView.getPane(i).pnl;
				if (persLong.getNotices()[i - noticeFirst].getPnid() != npane.notice
						.getPnid()) {
					persLong.setOrderModified();
					orderModification = true;
					break;
				}
			}
		}

		for (int i = noticeFirst; i < tabCount; i++) {
			NoticePane pane = (NoticePane) personView.getPane(i).pnl;
			pane.verifyUnitNotice();
		}

		Vector<UnitNotice> un = new Vector<UnitNotice>();

		for (int i = noticeFirst; i < tabCount; i++) {
			NoticePane pane = (NoticePane) personView.getPane(i).pnl;

			if (pane.notice.isToBeDeleted()) {
				foundModification = true;
			} else {
				pane.copyToUnitNotice();

				if (pane.notice.isToBeUpdated()) {
					foundModification = true;
				}
			}
			un.add(pane.notice);

		}

		verifyLocalPersonDates();
		SukuData req = new SukuData();

		if (relas != null) {
			Vector<Relation> rel = new Vector<Relation>();
			for (int i = 0; i < relas.parents.list.size(); i++) {
				Relation r = relas.parents.list.get(i);
				if (r.isToBeDeleted() || r.isToBeUpdated()) {
					foundModification = true;
				}
				rel.add(r);
			}
			for (int i = 0; i < relas.spouses.list.size(); i++) {
				Relation r = relas.spouses.list.get(i);
				if (r.isToBeDeleted() || r.isToBeUpdated()) {
					foundModification = true;
				}
				rel.add(r);
			}
			for (int i = 0; i < relas.children.list.size(); i++) {
				Relation r = relas.children.list.get(i);
				if (r.isToBeDeleted() || r.isToBeUpdated()) {
					foundModification = true;
				}
				rel.add(r);
			}
			for (int i = 0; i < relas.otherRelations.size(); i++) {
				Relation r = relas.otherRelations.get(i);
				if (r.isToBeDeleted() || r.isToBeUpdated()) {
					foundModification = true;
				}
				rel.add(r);
			}

			if (rel.size() > 0) {
				req.relations = rel.toArray(new Relation[0]);

			}
		}

		String[] notorder = null;
		try {
			resp = Suku.kontroller.getSukuData("cmd=getsettings", "type=order",
					"name=notice");
			notorder = resp.generalArray;
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
					Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
			logger.log(Level.WARNING, "get settings", e);
			return null;

		}

		String[] wn = new String[notorder.length + 1];
		wn[0] = "NAME";
		for (int i = 0; i < notorder.length; i++) {
			wn[i + 1] = notorder[i];
		}

		if (reorderNotices(un, wn)) {
			orderModification = true;
		}
		if (persLong.getPid() == 0) {
			foundModification = false;
			for (int i = noticeFirst; i < tabCount; i++) {
				NoticePane pane = (NoticePane) personView.getPane(i).pnl;
				if (pane.notice.isToBeUpdated()) {
					if (pane.notice.isToBeDeleted() == false) {
						foundModification = true;
					}
				}
			}
		}

		req.persLong = persLong;
		if (req.persLong.isMainModified()) {
			foundModification = true;
		}
		req.persLong.setNotices(un.toArray(new UnitNotice[0]));
		req.resu = foundModification ? "modified" : null;
		if (orderModification) {
			req.resuCount = 1; // this returns >0 if order has been modified
		}
		return req;
	}

	/**
	 * Update person.
	 * 
	 * @param force
	 *            the force
	 * @return the suku data
	 * @throws SukuDateException
	 *             the suku date exception
	 */
	SukuData updatePerson(boolean force) throws SukuDateException {

		SukuData req = checkIfPersonStructureChanged();
		SukuData resp = null;

		if (req != null && (req.resu != null || force)) {
			// req.persLong = persLong;
			//
			// req.persLong.setNotices(un.toArray(new UnitNotice[0]));

			try {
				resp = Suku.kontroller.getSukuData(req, "cmd=update",
						"type=person");
				if (resp.pers != null && resp.pers.length > 0) {

					PersonShortData shh = resp.pers[0];
					personPid = shh.getPid();

					personView.getSuku().updatePerson(shh);

					//
					// now reset the toBeUpdate value
					//
					persLong.resetModified();

					UnitNotice[] unn = persLong.getNotices();

					if (unn != null) {

						for (int i = 0; i < unn.length; i++) {
							unn[i].setModified(false);
							UnitLanguage[] ull = unn[i].getLanguages();
							if (ull != null) {
								for (int j = 0; j < ull.length; j++) {
									ull[j].resetModified();
								}
							}
						}
					}
				}

				Relation[] rr = req.relations;
				if (rr != null) {
					for (int i = 0; i < rr.length; i++) {
						rr[i].resetModified();
						RelationNotice[] rnn = rr[i].getNotices();
						if (rnn != null) {
							for (int j = 0; j < rnn.length; j++) {
								rnn[j].resetModified();
								RelationLanguage[] rll = rnn[j].getLanguages();
								if (rll != null) {
									for (int k = 0; k < rll.length; k++) {
										rll[k].resetModified();
									}
								}
							}
						}
					}
				}

				return resp;
			} catch (SukuException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(),
						Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);

				e.printStackTrace();
			}
		}

		return req;

	}

	/**
	 * update name notices fromn mainpane.
	 */
	public void updateNameNotices() {

		int lastNameidx = -1;
		int firstNameidx = -1;
		int noticeFirst = personView.getFirstNoticeIndex();
		int tabCount = personView.getTabCount();
		if (surname.isEnabled()) {

			int nameCount = 0;
			String names[] = surname.getText().split(";");

			boolean hasName = false;
			if (names.length > 1 || !names[0].equals("")) {
				hasName = true;
			}
			if (!givenname.getText().isEmpty()) {
				hasName = true;
			}
			if (!patronym.getText().isEmpty()) {
				hasName = true;
			}
			if (!postfix.getText().isEmpty()) {
				hasName = true;
			}

			for (int i = noticeFirst; i < tabCount; i++) {

				NoticePane pane = (NoticePane) personView.getPane(i).pnl;

				if (pane.notice.getTag().equals("NAME")) {
					if (firstNameidx < 0) {
						firstNameidx = i;
					}
					nameCount++;
					if (nameCount > names.length) {
						pane.setToBeDeleted(true, false);
					} else {
						if (!hasName) {
							lastNameidx = 0;
							pane.setToBeDeleted(true, false);
						} else {
							pane.setToBeDeleted(false, false);
							lastNameidx = i;
						}
					}
				}
			}
			if (lastNameidx < 0) {
				lastNameidx = noticeFirst - 1;
				firstNameidx = noticeFirst;
			}
			int namesCount = names.length;
			if (namesCount == 1 && names[0].equals("")) {
				if (givenname.getText().isEmpty()
						&& patronym.getText().isEmpty()) {
					namesCount = 0;
				} else {
					namesCount = 1;
				}
			}
			if (hasName) {
				for (int i = lastNameidx + 1; i < firstNameidx + namesCount; i++) {
					insertNamePane(i, "NAME");
				}
			}
			tabCount = personView.getTabCount();
			String tmp = null;
			for (int i = noticeFirst; i < tabCount
					&& i < noticeFirst + names.length; i++) {
				NoticePane pane = (NoticePane) personView.getPane(i).pnl;
				if (pane.notice.getTag().equals("NAME")) {
					pane.givenname.setText(Utils.toProper(givenname.getText()));
					if (pane.notice.getPnid() == 0) {
						tmp = Utils.toProper(patronym.getText());
					} else {
						tmp = patronym.getText();
					}
					pane.patronym.setText(tmp);
					String name = names[i - noticeFirst];

					int vonIndex = Utils.isKnownPrefix(name);
					if (vonIndex > 0 && vonIndex < name.length()) {
						pane.prefix.setText(name.substring(0, vonIndex));
						name = name.substring(vonIndex + 1);
					} else {
						pane.prefix.setText("");
					}

					// pane.prefix.setText(name);

					if (pane.notice.getPnid() == 0) {
						tmp = Utils.toProper(name);
					} else {
						tmp = name;
					}
					pane.surname.setText(tmp);
					pane.postfix.setText(postfix.getText());
				}
			}
		} else {
			// System.out.println("Ei kosketa nimijuttuun");
		}

	}

	/**
	 * used for debugging to check sync of main and notices
	 */
	@SuppressWarnings("unused")
	private void listNoticePanels() {
		if (!listPanels)
			return;
		int midx = personView.getMainPaneIndex();
		int noteCount = personView.getTabCount();
		for (int i = midx + 2; i < noteCount; i++) {
			SukuTabPane sp = personView.getPane(i);

			if (sp.pnl instanceof NoticePane) {
				NoticePane pane = (NoticePane) sp.pnl;

				System.out.println("nn:tag " + i + ":" + pane.notice.getTag());

				System.out.println("nn:desc" + i + ":"
						+ pane.notice.getDescription() + "/"
						+ pane.description.getText());
				try {
					System.out.println("nn:date" + i + ":"
							+ pane.notice.getFromDate() + "/"
							+ pane.date.getFromDate());
				} catch (SukuDateException e) {
					System.out.println("dateex:" + e.getMessage());
				}
				System.out.println("nn:plac" + i + ":" + pane.notice.getPlace()
						+ "/" + pane.place.getText());
				System.out.println("nn:givn" + i + ":"
						+ pane.notice.getGivenname() + "/"
						+ pane.givenname.getText());
				System.out.println("nn:surn" + i + ":"
						+ pane.notice.getSurname() + "/"
						+ pane.surname.getText());
				System.out.println("-------------");
				// skipNextState = false;
			}

		}
	}

	/**
	 * Insert name pane.
	 * 
	 * @param noticeIndex
	 *            the notice index
	 * @param tag
	 *            the tag
	 */
	void insertNamePane(int noticeIndex, String tag) {
		UnitNotice notice = new UnitNotice(tag, persLong.getPid());
		NoticePane pane = new NoticePane(personView, persLong.getPid(), notice);
		String nimi = personView.getTypeValue(tag);
		personView.insertTab(new SukuTabPane(nimi, pane, tag), noticeIndex);

	}

	/**
	 * update non-name notices.
	 */
	public void updateRestNotices() {
		int birtCount = 0;
		int chrCount = 0;
		int deatCount = 0;
		int buriCount = 0;
		int occuCount = 0;
		int noteCount = 0;
		int noticeFirst = personView.getFirstNoticeIndex();
		int tabCount = personView.getTabCount();
		int lastNameIndex = noticeFirst;

		String[] occus = occupation.getText().split(";");

		for (int i = noticeFirst; i < tabCount; i++) {
			NoticePane pane = (NoticePane) personView.getPane(i).pnl;
			if (pane.notice.getTag().equals("BIRT"))
				birtCount++;
			if (pane.notice.getTag().equals("CHR"))
				chrCount++;
			if (pane.notice.getTag().equals("DEAT"))
				deatCount++;
			if (pane.notice.getTag().equals("BURI"))
				buriCount++;
			if (pane.notice.getTag().equals("OCCU"))
				occuCount++;
			if (pane.notice.getTag().equals("NOTE"))
				noteCount++;

			if (pane.notice.getTag().equals("NAME")) {
				if (i > lastNameIndex)
					lastNameIndex = i;
			}

		}

		int idx = personView.getFirstNoticeIndex();
		// if (idx <= lastNameIndex) {
		// idx = lastNameIndex + 1;
		// }

		if (birtDate.isEnabled()) {
			if (birtCount == 0
					&& (!birtDate.getText().isEmpty() || !birtPlace.getText()
							.isEmpty())) {
				insertNamePane(idx++, "BIRT");
				birtCount++;
			}
		}
		if (chrDate.isEnabled()) {
			if (chrCount == 0
					&& (!chrDate.getText().isEmpty() || !chrPlace.getText()
							.isEmpty())) {
				insertNamePane(idx++, "CHR");
				chrCount++;
			}
		}
		if (deatDate.isEnabled()) {
			if (deatCount == 0
					&& (!deatDate.getText().isEmpty() || !deatPlace.getText()
							.isEmpty())) {
				insertNamePane(idx++, "DEAT");
				deatCount++;
			}
		}
		if (buriDate.isEnabled()) {
			if (buriCount == 0
					&& (!buriDate.getText().isEmpty() || !buriPlace.getText()
							.isEmpty())) {
				insertNamePane(idx++, "BURI");
				buriCount++;
			}
		}
		if (occupation.isEnabled()) {

			if (!occupation.getText().isEmpty()) {
				for (int i = occuCount; i < occus.length; i++) {
					insertNamePane(idx++, "OCCU");
				}
			}
		}
		if (notetext.isEnabled()) {
			if (noteCount == 0 && !notetext.getText().isEmpty()) {
				insertNamePane(idx++, "NOTE");
				noteCount++;
			}
		}
		tabCount = personView.getTabCount();
		int occuIndex = 0;
		for (int i = noticeFirst; i < tabCount; i++) {
			NoticePane pane = (NoticePane) personView.getPane(i).pnl;
			String tag = pane.notice.getTag();
			if (birtCount == 1 && tag.equals("BIRT")) {
				pane.setToBeDeleted(birtDate.getText().isEmpty()
						&& birtPlace.getText().isEmpty(), true);
				pane.date.setTextFromDate(birtDate.getText());
				pane.place.setText(birtPlace.getText().trim());
			}
			if (chrCount == 1 && tag.equals("CHR")) {
				pane.setToBeDeleted(chrDate.getText().isEmpty()
						&& chrPlace.getText().isEmpty(), true);
				pane.date.setTextFromDate(chrDate.getText());
				pane.place.setText(chrPlace.getText().trim());
			}
			if (deatCount == 1 && tag.equals("DEAT")) {
				pane.setToBeDeleted(deatDate.getText().isEmpty()
						&& deatPlace.getText().isEmpty(), true);
				pane.date.setTextFromDate(deatDate.getText());
				pane.place.setText(deatPlace.getText().trim());
			}
			if (buriCount == 1 && tag.equals("BURI")) {
				pane.setToBeDeleted(buriDate.getText().isEmpty()
						&& buriPlace.getText().isEmpty(), true);
				pane.date.setTextFromDate(buriDate.getText());
				pane.place.setText(buriPlace.getText().trim());
			}
			if (tag.equals("OCCU")) {
				if (pane.notice.isToBeDeleted() || occuIndex >= occus.length) {
					pane.setToBeDeleted(true, false);
				} else {
					pane.setToBeDeleted(occus[occuIndex].equals(""), true);
					pane.description.setText(occus[occuIndex].trim());
				}
				occuIndex++;
			}
			if (tag.equals("NOTE")) {
				if (noteCount == 1) {
					// pane.setToBeDeleted(notetext.getText().isEmpty());
					pane.noteText.setText(notetext.getText());
				}
			}
		}
		// reorderRestNotices();
	}

	/**
	 * reorder notices in preset order.
	 * 
	 * @param unotices
	 *            the unotices
	 * @param wn
	 *            the wn
	 * @return true if notice order has changed
	 */
	public boolean reorderNotices(Vector<UnitNotice> unotices, String[] wn) {

		int lastCheckedIndex = -1;

		boolean hasSorted = false;
		for (int tagIdx = 0; tagIdx < wn.length; tagIdx++) {
			String tag = wn[tagIdx];

			for (int i = 0; i < unotices.size(); i++) {
				UnitNotice un = unotices.get(i);
				if (un.getTag().equals(tag)) {
					if (i > lastCheckedIndex) {
						lastCheckedIndex++;
						UnitNotice t = unotices.remove(i);
						unotices.insertElementAt(t, lastCheckedIndex);
						if (!tag.equals(t.getTag())) {
							hasSorted = true;
						}
					} else {
						lastCheckedIndex = i;
					}
				}
			}
		}
		return hasSorted;

	}

	/**
	 * update unit data from fields to pojo.
	 */
	public void updateUnit() {

		if (persLong != null) {
			// String newSex = sexes[sex.getSelectedIndex()];
			// persLong.setSex(newSex);

			ButtonModel model = sexGroup.getSelection();
			if (model != null) {
				persLong.setSex(model.getActionCommand());
			}

			String priva = privacy.isSelected() ? "P" : null;
			persLong.setPrivacy(priva);

			String grp = groupid.getText();
			persLong.setGroupId(grp);

			String rf = refn.getText();
			persLong.setUserRefn(rf);

			String sou = source.getText();
			persLong.setSource(sou);

			String prit = privateText.getText();
			persLong.setPrivateText(prit);

		}

	}

	/**
	 * Verify local person dates.
	 * 
	 * @throws SukuDateException
	 *             the suku date exception
	 */
	void verifyLocalPersonDates() throws SukuDateException {
		if (persLong != null) {
			PersonShortData shortie = new PersonShortData(persLong);

			int birthYear = shortie.getBirtYear();
			int deathYear = shortie.getDeatYear();
			if (birthYear > 0 && deathYear > 0) {
				if (birthYear > deathYear) {
					throw new SukuDateException(
							Resurses.getString("ERROR_DEAT_BEF_BIRT"));
				}
				if (deathYear > birthYear + 150) {
					throw new SukuDateException(
							Resurses.getString("ERROR_TOO_OLD"));
				}

			}

		}
	}

	// private int lcol = 75;
	//
	// private int rwidth = 70;
	// // private int rcol = lwidth+lcol+10;
	//
	// private int gnlen = 150;
	// private int postlen = 60;
	// private int surlen = 240;
	// private int datelen = 80;
	// private int colbet = 2;
	// private int placlen = 213;
	// private int rcol = lcol + datelen + placlen + colbet * 2; // 460; //
	// private int biglen = datelen + placlen + colbet * 2 + rwidth * 2;

	/**
	 * Resize main pane.
	 */
	public void resizeMainPane() {
		Dimension currSize = getSize();
		int lwidth = 0;
		int rwidth = 70;
		int rcol = 0;
		int lcol = 75;
		int gnlen = 0;
		int postlen = 60;
		int surlen = 240;
		int placlen = 213;
		// System.out.println("LEVEYS: "+currSize.width);
		if (currSize.width > 525) {

			rwidth = currSize.width / 8;
			lwidth = currSize.width - lcol - colbet - rwidth * 2;
			gnlen = lwidth / 2 - colbet;
			surlen = lwidth - postlen - colbet;
			placlen = lwidth - datelen - colbet;
		} else {
			gnlen = 150;
			placlen = 213;
			rwidth = 70;
			lwidth = 525 - lcol - 10 - rwidth * 2;

		}
		rcol = lwidth + lcol + 5;
		int rivi = 10;
		int rrivi = 30;

		privacy.setBounds(rcol, rrivi, 100, 20);

		rrivi += 24;
		groupLbl.setBounds(rcol, rrivi, 100, 20);
		rrivi += 20;

		groupid.setBounds(rcol, rrivi, rwidth * 2, 20);
		// groupid.setEditable(false);

		rrivi += 24;
		refnLbl.setBounds(rcol, rrivi, 100, 20);
		rrivi += 20;

		refn.setBounds(rcol, rrivi, rwidth * 2, 20);

		rrivi += 20;
		createdLbl.setBounds(rcol, rrivi, 200, 20);
		rrivi += 20;

		created.setBounds(rcol, rrivi, rwidth * 2, 20);

		rrivi += 28;
		modifiedLbl.setBounds(rcol, rrivi, 200, 20);
		rrivi += 20;

		modified.setBounds(rcol, rrivi, rwidth * 2, 20);

		rrivi += 22;

		close.setBounds(rcol, rrivi, rwidth, 24);
		update.setBounds(rcol + rwidth, rrivi, rwidth, 24);

		rivi += 24;
		givenname.setBounds(lcol, rivi, gnlen, 20);

		patronym.setBounds(lcol + gnlen + colbet, rivi, gnlen - 5, 20);

		rivi += 20;

		rivi += 20;
		surname.setBounds(lcol, rivi, lwidth - postlen - 10, 20);

		postfix.setBounds(lcol + surlen + colbet, rivi, postlen - 10, 20);

		rivi += 20;
		rivi += 24;
		// sex.setBounds(lcol + 300, rivi, 80, 20);
		sexr.setBounds(lcol - 10, rivi - 10, 300, 24);
		rivi += 20;
		rivi += 24;
		birtDate.setBounds(lcol, rivi, datelen, 20);

		birtPlace.setBounds(lcol + datelen + colbet, rivi, placlen, 20);

		rivi += 24;

		chrDate.setBounds(lcol, rivi, datelen, 20);

		chrPlace.setBounds(lcol + datelen + colbet, rivi, placlen, 20);

		rivi += 24;

		deatDate.setBounds(lcol, rivi, datelen, 20);

		deatPlace.setBounds(lcol + datelen + colbet, rivi, placlen, 20);

		rivi += 24;

		buriDate.setBounds(lcol, rivi, datelen, 20);

		buriPlace.setBounds(lcol + datelen + colbet, rivi, placlen, 20);

		rivi += 24;

		rivi += 20;

		occupation.setBounds(lcol, rivi, datelen + placlen + colbet, 20);

		rivi += 24;

		scrollNote.setBounds(lcol, rivi, lwidth + rwidth * 2, 80);

		rivi += 85;

		scrollSource.setBounds(lcol, rivi, lwidth + rwidth * 2, 80);

		rivi += 85;

		scrollPrivate.setBounds(lcol, rivi, lwidth + rwidth * 2, 80);

		rivi += 100;

		setPreferredSize(new Dimension(lcol + biglen + colbet, rivi + 30));
		// updateUI();
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
		resizeMainPane();
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
