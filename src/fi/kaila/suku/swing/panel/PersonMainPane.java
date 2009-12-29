package fi.kaila.suku.swing.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.SukuTextField.Field;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationLanguage;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitLanguage;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * 
 * Main pane is the first of person windows panes with person main data
 * 
 * @author Kalle
 * 
 */
public class PersonMainPane extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	PersonView personView = null;
	private JButton close;
	private JButton update;
	private static Logger logger = Logger.getLogger(PersonMainPane.class
			.getName());

	private JCheckBox privacy;
	private String[] sextexts = { "Mies", "Nainen", "Tuntematon" };
	private String[] sexes = { "M", "F", "U" };
	private JComboBox sex;
	private JTextField groupid;
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

	private int personPid = 0;

	PersonLongData persLong = null;

	RelativesPane relas = null;

	/**
	 * @param peronView
	 * @param pid
	 * @throws SukuException
	 */
	public PersonMainPane(PersonView peronView, int pid) throws SukuException {
		this.personView = peronView;

		SukuData reposet = Suku.kontroller.getSukuData("cmd=gettypes", "lang="
				+ Resurses.getLanguage());

		PersonView.types = new String[reposet.vvTypes.size()];
		PersonView.typesTexts.clear();

		for (int i = 0; i < reposet.vvTypes.size(); i++) {

			String tag = reposet.vvTypes.get(i)[0];
			PersonView.types[i] = tag;
			String value = reposet.vvTypes.get(i)[1];
			PersonView.typesTexts.put(tag, value);
		}

		initMe();

	}

	int getPersonPid() {
		return personPid;
	}

	void updateName() {

		boolean firstName = true;
		boolean specialName = false;
		boolean specialFirst = false;
		String fgiven = "";
		String fpatro = "";
		String prexi = "";
		String posti = "";
		String comboname = "";
		String tmp;
		int noticeFirst = personView.getFirstNoticeIndex();
		int noticeCount = personView.getTabCount();
		givenname.setText("");
		patronym.setText("");
		surname.setText("");
		postfix.setText("");
		for (int i = noticeFirst; i < noticeCount; i++) {
			NoticePane pane = (NoticePane) personView.getPane(i).pnl;
			if (pane.notice.getTag().equals("NAME")) {
				if (!pane.notice.isToBeDeleted()) {

					prexi = pane.prefix.getText();

					if (firstName) {
						fgiven = pane.givenname.getText();
						fpatro = pane.patronym.getText();
						posti = pane.postfix.getText();
						givenname.setText(fgiven);
						patronym.setText(fpatro);
						tmp = prexi;
						if (!tmp.equals("")) {
							if (Utils.isKnownPrefix(tmp) > 0) {
								comboname += tmp + " ";
							} else {
								specialFirst = true;
							}
						}
						// prefix.setText(tmp);
						comboname += pane.surname.getText();
						surname.setText(comboname);
						tmp = pane.postfix.getText();
						// if (!tmp.equals("")){
						// specialFirst=true;
						// }
						postfix.setText(tmp);

						firstName = false;
					} else {
						if (!posti.equals(pane.postfix.getText())) {
							specialName = true;
						}
						if (specialFirst) {
							specialName = true;
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
						String nextpre = "";
						if (!prexi.equals("")) {

							if (Utils.isKnownPrefix(prexi) > 0) {
								nextpre = prexi + " ";
							} else {
								specialName = true;
							}
						}
						// if (!"".equals(pane.prefix.getText())){
						// specialName=true;
						// break;
						// }
						// if (!"".equals(pane.postfix.getText())){
						// specialName=true;
						// break;
						// }
						// comboname += ";" + nextpre
						comboname += ";" + nextpre + pane.surname.getText();
					}
				}
			}
		}
		givenname.setEnabled(!specialName);
		patronym.setEnabled(!specialName);
		// prefix.setEnabled(!specialName);
		surname.setEnabled(!specialName);
		postfix.setEnabled(!specialName);
		if (!specialName) {
			surname.setText(comboname);

		}

	}

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
			}

			if (pane.notice.getTag().equals("OCCU")) {
				// if (!pane.notice.isToBeDeleted()) {
				occuVec.add(pane.description.getText());
				// } else {
				// occuVec.add("");
				// }
				occuCount++;

				// occupation.setText(pane.description.getText());
				if (!pane.isPlain())
					occuCount += 100;
			}
			if (pane.notice.getTag().equals("NOTE")) {
				noteCount++;
				if (noteCount == 1) {
					if (!pane.notice.isToBeDeleted()) {
						notetext.setText(pane.noteText.getText());
					} else {
						notetext.setText("");
					}
				}
				if (!pane.isPlain())
					noteCount++;
			}
		}

		StringBuffer oc = new StringBuffer();
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

	private int lcol = 75;

	private int rwidth = 70;
	// private int rcol = lwidth+lcol+10;

	private int gnlen = 150;
	private int postlen = 60;
	private int surlen = 240;
	private int datelen = 80;
	private int colbet = 2;
	private int placlen = 213;
	private int rcol = lcol + datelen + placlen + colbet * 2; // 460; //
	private int biglen = datelen + placlen + colbet * 2 + rwidth * 2;

	private void initMe() {

		JScrollPane scrollPane;
		setLayout(null);

		JLabel lbl;
		int rivi = 10;
		int rrivi = 30;

		privacy = new JCheckBox(Resurses.getString("DATA_PRIVACY"));
		privacy.setBounds(rcol, rrivi, 100, 20);
		add(privacy);

		rrivi += 24;

		lbl = new JLabel(Resurses.getString("DATA_GROUP"));
		add(lbl);
		lbl.setBounds(rcol, rrivi, 100, 20);
		rrivi += 20;
		groupid = new JTextField();
		add(groupid);
		groupid.setBounds(rcol, rrivi, rwidth * 2, 20);
		// groupid.setEditable(false);

		rrivi += 24;

		lbl = new JLabel(Resurses.getString("DATA_REFN"));
		add(lbl);
		lbl.setBounds(rcol, rrivi, 100, 20);
		rrivi += 20;
		refn = new JTextField();
		add(refn);
		refn.setBounds(rcol, rrivi, rwidth * 2, 20);

		rrivi += 20;
		lbl = new JLabel(Resurses.getString("DATA_PERSON_CREATED"));
		lbl.setBounds(rcol, rrivi, 200, 20);
		add(lbl);
		rrivi += 20;
		created = new JTextField();
		created.setBounds(rcol, rrivi, rwidth * 2, 20);
		created.setEditable(false);
		add(created);

		rrivi += 28;
		lbl = new JLabel(Resurses.getString("DATA_PERSON_MODIFIED"));
		lbl.setBounds(rcol, rrivi, 200, 20);
		add(lbl);
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
		sex = new JComboBox(sextexts);
		add(sex);
		sex.setBounds(lcol, rivi, 80, 20);
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
		scrollPane = new JScrollPane(notetext,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollPane);
		scrollPane.setBounds(lcol, rivi, biglen, 80);

		rivi += 85;

		lbl = new JLabel(Resurses.getString("DATA_SOURCE"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 20);
		source = new SukuTextArea();

		source.setLineWrap(true);
		scrollPane = new JScrollPane(source,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollPane);
		scrollPane.setBounds(lcol, rivi, biglen, 80);

		rivi += 85;

		lbl = new JLabel(Resurses.getString("DATA_PRIVATETEXT"));
		add(lbl);
		lbl.setBounds(10, rivi, 100, 20);
		privateText = new JTextArea();

		privateText.setLineWrap(true);
		scrollPane = new JScrollPane(privateText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollPane);
		scrollPane.setBounds(lcol, rivi, biglen, 80);

		rivi += 100;

		setPreferredSize(new Dimension(lcol + biglen + colbet, rivi + 30));

		// update = new JButton(Resurses.getString(Resurses.UPDATE));
		//	
		// add(update);
		// update.setActionCommand(Resurses.UPDATE);
		// update.addActionListener(this);
		// update.setBounds(120, rivi, 100, 24);

	}

	boolean closeNotices() {

		personView.closePerson();
		persLong = null;
		relas = null;
		return true;

	}

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
		int sexidx = 0;
		for (int i = 0; i < sexes.length; i++) {
			if (currSex.equals(sexes[i])) {
				sexidx = i;
			}
		}
		sex.setSelectedIndex(sexidx);

		privacy.setSelected(persLong.getPrivacy() != null);

		groupid.setText(persLong.getGroupId());

		groupid.setEditable(persLong.getPid() == 0);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		try {

			if (cmd.equals(Resurses.CLOSE) || cmd.equals(Resurses.UPDATE)) {
				try {
					boolean reOpen = true;
					if (cmd.equals(Resurses.CLOSE))
						reOpen = false;
					SukuData resp = updatePerson();
					personView.closeMainPane(reOpen);
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
					logger.log(Level.WARNING, "CLOSE", e1);
					return;
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, e1.toString(), Resurses
							.getString(Resurses.SUKU),
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

	SukuData updatePerson() throws SukuDateException {
		SukuData resp = null;
		personView.updateNotices();
		int noticeFirst = personView.getFirstNoticeIndex();
		int tabCount = personView.getTabCount();

		boolean foundModification = false;
		if (persLong != null) {
			String newSex = sexes[sex.getSelectedIndex()];
			persLong.setSex(newSex);

			String priva = privacy.isSelected() ? "P" : null;
			persLong.setPrivacy(priva);

			if (persLong.getPid() == 0) {
				String grp = groupid.getText();
				persLong.setGroupId(grp);
			}

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
						foundModification = true;
						break;
					}
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

		SukuData req = new SukuData();

		if (relas != null) {
			Vector<Relation> rel = new Vector<Relation>();
			for (int i = 0; i < relas.parents.list.size(); i++) {
				Relation r = relas.parents.list.get(i);

				rel.add(r);
			}
			for (int i = 0; i < relas.spouses.list.size(); i++) {
				Relation r = relas.spouses.list.get(i);

				rel.add(r);
			}
			for (int i = 0; i < relas.children.list.size(); i++) {
				Relation r = relas.children.list.get(i);

				rel.add(r);
			}
			for (int i = 0; i < relas.otherRelations.size(); i++) {
				Relation r = relas.otherRelations.get(i);

				rel.add(r);
			}

			if (rel.size() > 0) {
				req.relations = rel.toArray(new Relation[0]);
				foundModification = true;
			}
		}

		if (persLong != null) {
			String[] notorder = null;
			try {
				resp = Suku.kontroller.getSukuData("cmd=getsettings",
						"type=order", "name=notice");
				notorder = resp.generalArray;
			} catch (SukuException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				logger.log(Level.WARNING, "get settings", e);
				return null;

			}

			String[] wn = new String[notorder.length + 1];
			wn[0] = "NAME";
			for (int i = 0; i < notorder.length; i++) {
				wn[i + 1] = notorder[i];
			}

			if (reorderNotices(un, wn)) {
				foundModification = true;
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
			if (foundModification) {
				req.persLong = persLong;

				req.persLong.setNotices(un.toArray(new UnitNotice[0]));

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
								unn[i].resetModified();
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
									RelationLanguage[] rll = rnn[j]
											.getLanguages();
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

		}

		return resp;

	}

	/**
	 * update name notices fromn mainpane
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
			if (names.length > 1) {
				hasName = true;
			}
			if (!givenname.getText().equals("")) {
				hasName = true;
			}
			if (!patronym.getText().equals("")) {
				hasName = true;
			}
			if (!postfix.getText().equals("")) {
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
						pane.setToBeDeleted(true);
					} else {
						if (!hasName) {
							lastNameidx = 0;
							pane.setToBeDeleted(true);
						} else {
							pane.setToBeDeleted(false);
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
				if (givenname.getText().equals("")
						&& patronym.getText().equals("")) {
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

			for (int i = noticeFirst; i < tabCount
					&& i < noticeFirst + names.length; i++) {
				NoticePane pane = (NoticePane) personView.getPane(i).pnl;
				if (pane.notice.getTag().equals("NAME")) {
					pane.givenname.setText(givenname.getText());
					pane.patronym.setText(patronym.getText());
					String name = names[i - noticeFirst];

					int vonIndex = Utils.isKnownPrefix(name);
					if (vonIndex > 0) {
						pane.prefix.setText(name.substring(0, vonIndex));
						name = name.substring(vonIndex + 1);
					}

					// pane.prefix.setText(name);
					pane.surname.setText(name);
					pane.postfix.setText(postfix.getText());
				}
			}
		} else {
			// System.out.println("Ei kosketa nimijuttuun");
		}
	}

	void insertNamePane(int noticeIndex, String tag) {
		UnitNotice notice = new UnitNotice(tag, persLong.getPid());
		NoticePane pane = new NoticePane(personView, persLong.getPid(), notice);
		String nimi = personView.getTypeValue(tag);
		personView.insertTab(new SukuTabPane(nimi, pane, tag), noticeIndex);

	}

	/**
	 * update non-name notices
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
					&& (!birtDate.getText().equals("") || !birtPlace.getText()
							.equals(""))) {
				insertNamePane(idx++, "BIRT");
			}
		}
		if (chrDate.isEnabled()) {
			if (chrCount == 0
					&& (!chrDate.getText().equals("") || !chrPlace.getText()
							.equals(""))) {
				insertNamePane(idx++, "CHR");
			}
		}
		if (deatDate.isEnabled()) {
			if (deatCount == 0
					&& (!deatDate.getText().equals("") || !deatPlace.getText()
							.equals(""))) {
				insertNamePane(idx++, "DEAT");
			}
		}
		if (buriDate.isEnabled()) {
			if (buriCount == 0
					&& (!buriDate.getText().equals("") || !buriPlace.getText()
							.equals(""))) {
				insertNamePane(idx++, "BURI");
			}
		}
		if (occupation.isEnabled()) {

			if (!occupation.getText().equals("")) {
				for (int i = occuCount; i < occus.length; i++) {
					insertNamePane(idx++, "OCCU");
				}
			}
		}
		if (notetext.isEnabled()) {
			if (noteCount == 0 && !notetext.getText().equals("")) {
				insertNamePane(idx++, "NOTE");
			}
		}
		tabCount = personView.getTabCount();
		int occuIndex = 0;
		for (int i = noticeFirst; i < tabCount; i++) {
			NoticePane pane = (NoticePane) personView.getPane(i).pnl;
			String tag = pane.notice.getTag();
			if (tag.equals("BIRT")) {
				pane.setToBeDeleted(birtDate.getText().equals("")
						&& birtPlace.getText().equals(""));
				pane.date.setTextFromDate(birtDate.getText());
				pane.place.setText(birtPlace.getText());
			}
			if (tag.equals("CHR")) {
				pane.setToBeDeleted(chrDate.getText().equals("")
						&& chrPlace.getText().equals(""));
				pane.date.setTextFromDate(chrDate.getText());
				pane.place.setText(chrPlace.getText());
			}
			if (tag.equals("DEAT")) {
				pane.setToBeDeleted(deatDate.getText().equals("")
						&& deatPlace.getText().equals(""));
				pane.date.setTextFromDate(deatDate.getText());
				pane.place.setText(deatPlace.getText());
			}
			if (tag.equals("BURI")) {
				pane.setToBeDeleted(buriDate.getText().equals("")
						&& buriPlace.getText().equals(""));
				pane.date.setTextFromDate(buriDate.getText());
				pane.place.setText(buriPlace.getText());
			}
			if (tag.equals("OCCU")) {
				if (pane.notice.isToBeDeleted() || occuIndex >= occus.length) {
					pane.setToBeDeleted(true);
				} else {
					pane.setToBeDeleted(occus[occuIndex].equals(""));
					pane.description.setText(occus[occuIndex]);
				}
				occuIndex++;
			}
			if (tag.equals("NOTE")) {
				if (noteCount == 1) {
					pane.setToBeDeleted(notetext.getText().equals(""));
					pane.noteText.setText(notetext.getText());
				}
			}
		}
		// reorderRestNotices();
	}

	/**
	 * reorder notices in preset order
	 * 
	 * @param unotices
	 * @param wn
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
						hasSorted = true;
					} else {
						lastCheckedIndex = i;
					}
				}
			}
		}
		return hasSorted;

	}

}
