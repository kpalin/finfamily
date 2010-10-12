package fi.kaila.suku.swing.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.util.SukuDateField;
import fi.kaila.suku.swing.util.SukuPopupMenu;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateException;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * Panel to do traffic to Hiski.
 * 
 * @author Kaarle Kaila
 */
public class HiskiImportPanel extends JPanel implements ActionListener {

	private static Logger logger = Logger.getLogger(HiskiImportPanel.class
			.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int hiskiSurety = 40;

	/** The menubar. */
	JMenuBar menubar;

	/** The m file. */
	JMenu mFile;

	/** The m grid. */
	JMenuItem mGrid;

	private JButton getHiski;
	private JTextField hiskiNumber;
	private JButton upload;
	private JButton normalize;
	private JButton showInBrowser;
	private JLabel book;
	private JTextField srk;
	private JLabel srkNo;

	private JLabel eventFirstType;
	private JTextField eventFirstDate;

	private JLabel eventLastType;
	private JTextField eventLastDate;

	private JTextField eventFrom;
	private JTextField eventTo;
	private JTextField eventVillage;
	private JTextField eventFarm;
	private JTextField eventNote;
	private JTextField eventReason;
	private JTextField eventUserComment;
	private JTextField eventOrigComment;
	private JLabel eventExtraType;
	private SukuDateField eventExtraDate;

	// private PersonLongData[] hpers = null;

	private JLabel[] pNumero;
	private String[] pTypeName;
	private JLabel[] pType;
	private JComboBox[] pSex;
	private JLabel[] pSukuPid;
	private JLabel[] pSukuName;

	private JTextField[] rOccu;
	private JTextField[] rGivenname;
	private JTextField[] rPatronym;
	private JTextField[] rSurname;

	private JTextField[] pOccu;
	private JTextField[] pGivenname;
	private JTextField[] pPatronym;
	private JTextField[] pSurname;
	private JTextField[] pAgeVillage;
	private JTextField[] pReasonFarm;

	private String bookName = null;
	private String pvm1Name = null;
	private String pvm2Name = null;
	private int personCount = 0;
	private String eventId = null;
	private final Suku suku;

	private DocumentBuilderFactory factory = null;
	private DocumentBuilder bld = null;

	private String hiskiBrowserUrl = null;

	/**
	 * Instantiates a new hiski import panel.
	 * 
	 * @param suku
	 *            the suku
	 */
	public HiskiImportPanel(Suku suku) {
		this.suku = suku;
		initMe();

	}

	private final String[] sexes = new String[4];

	private int[] hiskiPid = { 0, 0, 0 };
	private PersonShortData[] hiskiPers = { null, null, null };
	// private String [] sukuName = {null,null,null};
	/** The y. */
	int y = 20;
	int rh = 80;
	/** The buttony. */
	int buttony = 60;

	private void initMe() {
		int ydiff = 24;
		int ylabdiff = 20;
		rh = ydiff * 3 + 10;

		setLayout(null);

		// JTextField lblTest = new JTextField(
		// "Hiskinum: k: 23806 v: 8279487 8279489   h: 167312 167313 167314 167315 p: 178151 s: 176075 ");
		// add(lblTest);
		// lblTest.setEditable(false);
		// lblTest.setBounds(40, 0, 400, 20);

		sexes[0] = "";
		sexes[1] = Resurses.getString("SEX_M");
		sexes[2] = Resurses.getString("SEX_F");
		sexes[3] = Resurses.getString("SEX_U");

		JLabel lbl = new JLabel(Resurses.getString(Resurses.HISKI_NUMBER));
		add(lbl);
		lbl.setBounds(40, y, 190, 20);

		book = new JLabel();
		add(book);
		book.setBounds(200, y, 100, 20);

		srkNo = new JLabel();
		add(srkNo);
		srkNo.setBounds(310, y, 40, 20);

		srk = new JTextField();
		add(srk);
		srk.setBounds(360, y, 150, 20);

		y += ydiff;

		this.hiskiNumber = new JTextField();
		add(this.hiskiNumber);
		this.hiskiNumber.setBounds(40, y, 150, 20);

		eventFirstType = new JLabel();
		add(eventFirstType);
		eventFirstType.setBounds(200, y, 65, 20);

		eventFirstDate = new JTextField();
		add(eventFirstDate);
		eventFirstDate.setBounds(275, y, 75, 20);

		eventLastType = new JLabel();
		add(eventLastType);
		eventLastType.setBounds(360, y, 65, 20);

		eventLastDate = new JTextField();
		add(eventLastDate);
		eventLastDate.setBounds(435, y, 75, 20);

		y += ydiff;
		buttony = y;
		lbl = new JLabel(Resurses.getString("HISKI_VILLAGEFARM"));
		add(lbl);
		lbl.setBounds(520, y, 120, 20);

		eventVillage = new JTextField();
		add(eventVillage);
		eventVillage.setBounds(200, y, 150, 20);

		eventFarm = new JTextField();
		add(eventFarm);
		eventFarm.setBounds(360, y, 150, 20);

		y += ydiff;

		lbl = new JLabel(Resurses.getString("HISKI_HUOM"));
		add(lbl);
		lbl.setBounds(520, y, 120, 20);

		eventReason = new JTextField();
		add(eventReason);
		eventReason.setBounds(200, y, 310, 20);

		y += ydiff;
		//
		// eventRemark = new JTextField();
		// add(eventRemark);
		// eventRemark.setBounds(200, y, 310, 20);
		//
		// y += 30;
		lbl = new JLabel(Resurses.getString("HISKI_OMA"));
		add(lbl);
		lbl.setBounds(520, y, 120, 20);

		eventUserComment = new JTextField();
		add(eventUserComment);
		eventUserComment.setBounds(200, y, 310, 20);

		y += ydiff;
		lbl = new JLabel(Resurses.getString("HISKI_ORIG"));
		add(lbl);
		lbl.setBounds(520, y, 120, 20);
		eventOrigComment = new JTextField();
		add(eventOrigComment);
		eventOrigComment.setBounds(200, y, 310, 20);

		y += ydiff;
		lbl = new JLabel(Resurses.getString("HISKI_MUUT"));
		add(lbl);
		lbl.setBounds(520, y, 120, 20);
		eventNote = new JTextField();
		add(eventNote);
		eventNote.setBounds(200, y, 310, 20);
		y += ydiff;
		lbl = new JLabel(Resurses.getString("HISKI_MISTAMINNE"));
		add(lbl);
		lbl.setBounds(520, y, 120, 20);

		eventFrom = new JTextField();
		add(eventFrom);
		eventFrom.setBounds(200, y, 150, 20);

		eventTo = new JTextField();
		add(eventTo);
		eventTo.setBounds(360, y, 150, 20);

		y += ydiff;

		eventExtraType = new JLabel(Resurses.getString("DATA_BIRT"));
		add(eventExtraType);
		eventExtraType.setBounds(200, y, 65, 20);
		eventExtraType.setVisible(false);
		eventExtraDate = new SukuDateField();
		add(eventExtraDate);
		eventExtraDate.setBounds(275, y, 283, 20);
		eventExtraDate.setVisible(false);
		initHiskiPersons(0);

		y += ylabdiff;

		lbl = new JLabel(Resurses.getString("HISKI_TYPEOCCU"));
		add(lbl);
		lbl.setBounds(40, y, 120, 20);

		lbl = new JLabel(Resurses.getString("HISKI_GIVEAGE"));
		add(lbl);
		lbl.setBounds(200, y, 120, 20);

		lbl = new JLabel(Resurses.getString("HISKI_PATROREASON"));
		add(lbl);
		lbl.setBounds(360, y, 120, 20);

		lbl = new JLabel(Resurses.getString("HISKI_SUR"));
		add(lbl);
		lbl.setBounds(520, y, 120, 20);

		this.factory = DocumentBuilderFactory.newInstance();
		this.factory.setValidating(false);

		try {
			this.bld = this.factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}

		y += ylabdiff;

		this.getHiski = new JButton(Resurses.getString(Resurses.GET_HISKI));
		// this.ok.setDefaultCapable(true);
		add(this.getHiski);
		this.getHiski.setActionCommand(Resurses.GET_HISKI);
		this.getHiski.addActionListener(this);
		this.getHiski.setBounds(40, buttony, 150, 24);

		buttony += ydiff;

		upload = new JButton(Resurses.getString(Resurses.HISKI_UPLOAD));
		// this.ok.setDefaultCapable(true);
		add(upload);
		upload.setActionCommand(Resurses.HISKI_UPLOAD);
		upload.addActionListener(this);
		upload.setBounds(40, buttony, 150, 24);

		buttony += ydiff;
		normalize = new JButton(Resurses.getString(Resurses.HISKI_NORMALIZE));
		// this.ok.setDefaultCapable(true);
		add(normalize);
		normalize.setActionCommand(Resurses.HISKI_NORMALIZE);
		normalize.addActionListener(this);
		normalize.setBounds(40, buttony, 150, 24);

		buttony += ydiff;
		showInBrowser = new JButton(Resurses.getString(Resurses.HISKI_BROWSER));
		// this.ok.setDefaultCapable(true);
		add(showInBrowser);
		showInBrowser.setActionCommand(Resurses.HISKI_BROWSER);
		showInBrowser.addActionListener(this);
		showInBrowser.setBounds(40, buttony, 150, 24);

	}

	private void initHiskiPersons(int luku) {
		// hpers = new PersonLongData[luku];
		hiskiPid = new int[luku];
		hiskiPers = new PersonShortData[luku];
		pNumero = new JLabel[luku];
		pSukuPid = new JLabel[luku];
		pSukuName = new JLabel[luku];
		pTypeName = new String[luku];
		pType = new JLabel[luku];
		pSex = new JComboBox[luku];
		rOccu = new JTextField[luku];
		rGivenname = new JTextField[luku];
		rPatronym = new JTextField[luku];
		rSurname = new JTextField[luku];
		pOccu = new JTextField[luku];
		pGivenname = new JTextField[luku];
		pPatronym = new JTextField[luku];
		pSurname = new JTextField[luku];
		pAgeVillage = new JTextField[luku];
		pReasonFarm = new JTextField[luku];

		int i = 0;

		for (i = 0; i < luku; i++) {
			hiskiPid[i] = 0;
			hiskiPers[i] = null;
			pSukuPid[i] = new JLabel();
			add(pSukuPid[i]);
			pSukuPid[i].setBounds(150, y + i * rh, 100, 20);

			pSukuName[i] = new JLabel();
			add(pSukuName[i]);
			pSukuName[i].setBounds(200, y + i * rh, 200, 20);

			pNumero[i] = new JLabel();
			add(pNumero[i]);
			pNumero[i].setBounds(40, y + 0 + i * rh, 40, 20);

			pTypeName[i] = null;

			pType[i] = new JLabel();
			add(pType[i]);
			pType[i].setBounds(60, y + 0 + i * rh, 100, 20);

			pSex[i] = new JComboBox(sexes);
			add(pSex[i]);
			pSex[i].setBounds(100, y + 66 + i * rh, 80, 20);

			rOccu[i] = new JTextField();
			rOccu[i].setEditable(false);
			add(rOccu[i]);
			rOccu[i].setBounds(40, y + 22 + i * rh, 150, 20);

			rGivenname[i] = new JTextField();
			rGivenname[i].setEditable(false);
			add(rGivenname[i]);
			rGivenname[i].setBounds(200, y + 22 + i * rh, 150, 20);

			rPatronym[i] = new JTextField();
			rPatronym[i].setEditable(false);
			add(rPatronym[i]);
			rPatronym[i].setBounds(360, y + 22 + i * rh, 150, 20);

			rSurname[i] = new JTextField();
			rSurname[i].setEditable(false);
			add(rSurname[i]);
			rSurname[i].setBounds(520, y + 22 + i * rh, 150, 20);

			pOccu[i] = new JTextField();
			add(pOccu[i]);
			pOccu[i].setBounds(40, y + 44 + i * rh, 150, 20);

			pGivenname[i] = new JTextField();
			add(pGivenname[i]);
			pGivenname[i].setBounds(200, y + 44 + i * rh, 150, 20);

			pPatronym[i] = new JTextField();
			add(pPatronym[i]);
			pPatronym[i].setBounds(360, y + 44 + i * rh, 150, 20);

			pSurname[i] = new JTextField();
			add(pSurname[i]);
			pSurname[i].setBounds(520, y + 44 + i * rh, 150, 20);

			pAgeVillage[i] = new JTextField();
			add(pAgeVillage[i]);
			pAgeVillage[i].setBounds(200, y + 66 + i * rh, 150, 20);

			pReasonFarm[i] = new JTextField();
			add(pReasonFarm[i]);
			pReasonFarm[i].setBounds(360, y + 66 + i * rh, 310, 20);

		}
		Dimension panelSize = new Dimension(740, y + 45 + i * rh);
		this.setPreferredSize(panelSize);
		updateUI();
	}

	/**
	 * reset all fields.
	 */
	public void resetHiskiPids() {
		for (int i = 0; i < hiskiPid.length; i++) {
			hiskiPid[i] = 0;
			pSukuName[i].setText("");
			pSukuPid[i].setText("");
			hiskiPers[i] = null;
		}
	}

	/**
	 * set a hiskipanel person.
	 * 
	 * @param idx
	 *            the idx
	 * @param pid
	 *            the pid
	 * @param nimi
	 *            the nimi
	 */
	public void setHiskiPerson(int idx, PersonShortData pers) {
		if (idx >= 0 && idx < hiskiPid.length) {
			hiskiPers[idx] = pers;
			hiskiPid[idx] = pers.getPid();
			// sukuName[idx]=nimi;
			pSukuPid[idx].setText("" + pers.getPid());
			pSukuName[idx].setText(pers.getAlfaName(true));

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

		if (cmd.equals(Resurses.GET_HISKI)) {

			fetchFromHiski();

		} else if (cmd.equals(Resurses.HISKI_UPLOAD)) {

			if ("kastetut".equals(bookName)) {
				uploadKastetutToDb();
			} else if ("vihityt".equals(bookName)) {
				uploadVihitytToDb();
			} else if ("haudatut".equals(bookName)) {
				uploadHaudatutToDb();
			} else if ("umuutt".equals(bookName) || "smuutt".equals(bookName)) {
				uploadMuuttaneetToDb();
			}
			resetHiskiPids();
		} else if (cmd.equals(Resurses.HISKI_NORMALIZE)) {

			for (int i = 0; i < pNumero.length; i++) {

				// now lets just begin with conversting dr to dotter and s to
				// son at end of patronym

				String patro = rPatronym[i].getText();

				if (patro.endsWith(".") || patro.endsWith(":")) {
					patro = patro.substring(0, patro.length() - 1);
				}
				if (patro.endsWith("dr")) {
					patro = patro.substring(0, patro.length() - 1) + "otter";
				} else if (patro.endsWith("s")) {
					patro += "on";
				}

				pPatronym[i].setText(patro);

			}

		} else if (cmd.equals(Resurses.HISKI_BROWSER)) {
			if (hiskiBrowserUrl != null) {
				Utils.openExternalFile(hiskiBrowserUrl);
			}
		}

	}

	private void uploadMuuttaneetToDb() {

		SukuData kast = new SukuData();
		String hiskiSource = "Hiski muuttaneet [" + eventId + "]";
		kast.persons = new PersonLongData[personCount];
		Vector<UnitNotice> notices = null;
		Vector<String> refs = new Vector<String>();
		StringBuilder noteBuf = new StringBuilder();

		String kdate = eventFirstDate.getText();

		String hdate = eventLastDate.getText();

		for (int i = 0; i < personCount; i++) {
			String aux = pType[i].getText();
			notices = new Vector<UnitNotice>();
			String occu;
			String etu;
			String patro;
			String suku;

			String village;
			String farm = null;
			UnitNotice notice;
			String sex = null;
			StringBuilder hbh = new StringBuilder();
			switch (pSex[i].getSelectedIndex()) {
			case 1:
				sex = "M";
				break;
			case 2:
				sex = "F";
				break;
			case 3:
				sex = "U";
				break;
			default:
				if (hiskiPid[i] > 0) {
					PersonShortData curpers = this.suku.getPerson(hiskiPid[i]);
					if (curpers != null) {
						sex = curpers.getSex();
						break;
					}
				}
				JOptionPane.showMessageDialog(this,
						Resurses.getString("ERROR_MISSINGSEX"));
				return;
			}

			if ("muuttaja".equals(aux)) {
				int muuttaja = -1 - i;
				if (hiskiPid[i] > 0) {
					muuttaja = hiskiPid[i];
				}
				kast.persons[i] = new PersonLongData(muuttaja, "INDI", sex);
				kast.persons[i].setSource(hiskiSource);
				occu = pOccu[i].getText();
				if (!occu.isEmpty()) {
					notice = new UnitNotice("OCCU");
					notice.setDescription(occu);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				etu = pGivenname[i].getText();
				patro = pPatronym[i].getText();

				suku = pSurname[i].getText();
				if (!etu.isEmpty() || !patro.isEmpty() || !suku.isEmpty()) {
					if (kast.persons[i].getPid() > 0) {
						if (hbh.length() > 0) {
							hbh.append(".\n");
						}
						hbh.append(Resurses.getString("HISKI_NAME"));
						hbh.append(":");
						if (!etu.isEmpty()) {
							hbh.append(" ");
							hbh.append(etu);
						}
						if (!patro.isEmpty()) {
							hbh.append(" ");
							hbh.append(patro);
						}
						if (!suku.isEmpty()) {
							hbh.append(" ");
							hbh.append(suku);
						}
						hbh.append(".\n");
					} else {

						notice = new UnitNotice("NAME");
						notice.setGivenname(etu);
						notice.setPatronym(patro);
						notice.setSurname(suku);
						notice.setSource(hiskiSource);
						notices.add(notice);
					}
				}

				village = eventVillage.getText();
				if (village.equals("-")) {
					village = "";
				}
				farm = eventFarm.getText();
				if (farm.equals("-")) {
					farm = "";
				}

				if (kdate.length() > 0) {

					notice = new UnitNotice("EMIG");
					notice.setFromDate(toDbDate(kdate));
					notice.setPlace(srk.getText());
					notice.setVillage(village);
					notice.setFarm(farm);
					// notice.setNoteText(text);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				if (hdate.length() > 0) {

					notice = new UnitNotice("IMMI");
					notice.setFromDate(toDbDate(hdate));
					notice.setPlace(srk.getText());
					notice.setVillage(village);
					notice.setFarm(farm);
					// if (kdate.length() == 0) {
					// notice.setNoteText(text);
					// }
					notice.setSource(hiskiSource);
					notices.add(notice);
				}

				notice = new UnitNotice("HISKI");
				if (!kdate.isEmpty()) {
					noteBuf.append(eventFirstType.getText());
					noteBuf.append(" ");
					noteBuf.append(Utils.textDate(toDbDate(kdate), false));
					noteBuf.append(".\n");
				}
				if (!hdate.isEmpty()) {
					noteBuf.append(eventLastType.getText());
					noteBuf.append(" ");
					noteBuf.append(Utils.textDate(toDbDate(hdate), false));
					noteBuf.append(".\n");
				}

				if (!village.isEmpty()) {
					notice.setVillage(village);
				}
				if (!farm.isEmpty()) {
					notice.setFarm(farm);
				}

				String tmp = eventReason.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_HUOM"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				tmp = eventOrigComment.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_ORIG"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				tmp = eventUserComment.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_OMA"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				tmp = this.eventNote.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_MUUT"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}

				hbh.append(noteBuf);

				notice.setSource(hiskiSource + "\n" + hbh.toString());
				notices.add(notice);

				if (notices.size() > 0) {
					kast.persons[i].setNotices(notices
							.toArray(new UnitNotice[0]));
				}
			} else {
				addOtherPersonToText(refs, noteBuf, i);
			}

		}

		try {

			SukuData response = Suku.kontroller.getSukuData(kast, "cmd=upload");

			for (int i = 0; i < response.pers.length; i++) {
				suku.updatePerson(response.pers[i]);
			}
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, "Hiski :" + e.getMessage());
			e.printStackTrace();
		}

	}

	private void uploadHaudatutToDb() {
		SukuData kast = new SukuData();
		String hiskiSource = "Hiski haudatut [" + eventId + "]";
		kast.persons = new PersonLongData[1];
		Vector<UnitNotice> notices = null;

		String kdate = eventFirstDate.getText();

		String hdate = eventLastDate.getText();
		// String reason = eventReason.getText();
		String aux;
		notices = new Vector<UnitNotice>();
		Vector<String> refs = new Vector<String>();
		StringBuilder noteBuf = new StringBuilder();
		StringBuilder privBuf = new StringBuilder();
		for (int i = 0; i < personCount; i++) {
			String type = pType[i].getText();
			String occu;
			String etu;
			String patro;
			String suku;
			String reason;
			String village;
			String farm = null;
			UnitNotice notice;
			String sex = null;
			StringBuilder hbh = new StringBuilder();
			if ("vainaja".equals(type)) {

				switch (pSex[i].getSelectedIndex()) {
				case 1:
					sex = "M";
					break;
				case 2:
					sex = "F";
					break;
				case 3:
					sex = "U";
					break;
				default:
					if (hiskiPid[i] > 0) {
						PersonShortData curpers = this.suku
								.getPerson(hiskiPid[i]);
						if (curpers != null) {
							sex = curpers.getSex();
							break;
						}
					}
					JOptionPane.showMessageDialog(this,
							Resurses.getString("ERROR_MISSINGSEX"));
					return;
				}

				int vainaa = -1 - i;
				if (hiskiPid[i] > 0) {
					vainaa = hiskiPid[i];
				}
				kast.persons[i] = new PersonLongData(vainaa, "INDI", sex);

				kast.persons[i].setSource(hiskiSource);

				occu = pOccu[i].getText();
				if (!occu.isEmpty()) {
					notice = new UnitNotice("OCCU");
					notice.setDescription(occu);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				reason = pReasonFarm[i].getText();
				etu = pGivenname[i].getText();
				patro = pPatronym[i].getText();
				aux = pAgeVillage[i].getText();
				if (!aux.isEmpty()) {
					privBuf.append(aux);
				}

				if (!aux.isEmpty()) {
					privBuf.append(aux);
				}

				suku = pSurname[i].getText();
				if (!etu.isEmpty() || !patro.isEmpty() || !suku.isEmpty()) {
					if (kast.persons[i].getPid() > 0) {
						if (hbh.length() > 0) {
							hbh.append(".\n");
						}
						hbh.append(Resurses.getString("HISKI_NAME"));
						hbh.append(":");
						if (!etu.isEmpty()) {
							hbh.append(" ");
							hbh.append(etu);
						}
						if (!patro.isEmpty()) {
							hbh.append(" ");
							hbh.append(patro);
						}
						if (!suku.isEmpty()) {
							hbh.append(" ");
							hbh.append(suku);
						}
						hbh.append(".\n");
					} else {

						notice = new UnitNotice("NAME");
						notice.setGivenname(etu);
						notice.setPatronym(patro);
						notice.setSurname(suku);
						notice.setSource(hiskiSource);
						notices.add(notice);
					}
				}

				village = eventVillage.getText();
				farm = eventFarm.getText();
				village = eventVillage.getText();
				if (village.equals("-")) {
					village = "";
				}
				farm = eventFarm.getText();
				if (farm.equals("-")) {
					farm = "";
				}
				boolean hasDeath = false;
				boolean hasBirth = false;
				if (hiskiPid[i] > 0) {
					if (hiskiPers[i].getDeatDate() != null) {
						hasDeath = true;
					}
					if (hiskiPers[i].getBirtDate() != null) {
						hasBirth = true;
					}

				}
				if (kdate.length() > 0 || !reason.isEmpty()) {
					if (hasDeath) {
						hbh.append(Resurses.getString("CRITERIA.DEAT"));
						hbh.append(" ");
						hbh.append(Utils.textDate(kdate, false));
						hbh.append(" ");
						hbh.append(srk.getText());
						if (!village.isEmpty()) {
							hbh.append(" ");
							hbh.append(village);
						}
						if (!farm.isEmpty()) {
							hbh.append(" ");
							hbh.append(farm);
						}
						hbh.append(".\n");
					} else {
						notice = new UnitNotice("DEAT");
						if (!reason.isEmpty()) {
							notice.setDescription(reason);
						}
						if (kdate.length() > 0) {
							notice.setFromDate(toDbDate(kdate));
							notice.setPlace(srk.getText());
							notice.setVillage(village);
							notice.setFarm(farm);

							notice.setSource(hiskiSource);
						}
						notices.add(notice);
					}
				}
				if (hdate.length() > 0) {

					notice = new UnitNotice("BURI");
					notice.setFromDate(toDbDate(hdate));
					notice.setPlace(srk.getText());
					notice.setVillage(village);
					notice.setFarm(farm);

					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				String bdate = null;

				try {
					bdate = eventExtraDate.getFromDate();
				} catch (SukuDateException e) {
					bdate = null;
				}

				if (bdate != null && !bdate.isEmpty()) {
					if (hasBirth) {
						hbh.append(Resurses.getString("CRITERIA.BIRT"));
						hbh.append(": ");
						String bprefix = eventExtraDate.getDatePrefText();
						if (bprefix != null) {

							hbh.append(bprefix);
							hbh.append(" ");
						}

						hbh.append(Utils.textDate(bdate, false));
					} else {

						notice = new UnitNotice("BIRT");
						notice.setFromDate(bdate);
						notice.setDatePrefix(eventExtraDate.getDatePrefTag());
						notice.setSource(hiskiSource);
						notices.add(notice);
					}
				}
				notice = new UnitNotice("HISKI");

				notice.setNoticeType(eventLastType.getText());
				if (eventLastDate.getText().length() > 0) {
					notice.setFromDate(toDbDate(kdate));
				}
				if (!village.isEmpty()) {
					notice.setVillage(village);
				}
				if (!farm.isEmpty()) {
					notice.setFarm(farm);
				}

				String tmp = eventReason.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_HUOM"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				tmp = eventOrigComment.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_ORIG"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				tmp = eventUserComment.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_OMA"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				tmp = this.eventNote.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_MUUT"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}

				hbh.append(noteBuf);

				notice.setSource(hiskiSource + "\n" + hbh.toString());

				notices.add(notice);

			} else {

				addOtherPersonToText(refs, noteBuf, i);

			}

			kast.persons[i].setNotices(notices.toArray(new UnitNotice[0]));

		}

		try {
			SukuData response = Suku.kontroller.getSukuData(kast, "cmd=upload");

			for (int i = 0; i < response.pers.length; i++) {
				suku.updatePerson(response.pers[i]);
			}

		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, "Hiski :" + e.getMessage());
			e.printStackTrace();
		}

	}

	private void addOtherPersonToText(Vector<String> refs,
			StringBuilder noteBuf, int personIdx) {
		String occu;
		String etu;
		String patro;
		String suku;
		String type = pType[personIdx].getText();
		occu = pOccu[personIdx].getText();
		etu = pGivenname[personIdx].getText();
		patro = pPatronym[personIdx].getText();

		suku = pSurname[personIdx].getText();
		// text = pReason[i].getText();
		if (!etu.isEmpty() || !patro.isEmpty() || !suku.isEmpty()) {

			noteBuf.append(type);
			noteBuf.append(": ");
			StringBuilder refName = new StringBuilder();
			boolean addSpace = false;
			if (!Utils.nv(occu).isEmpty()) {
				noteBuf.append(occu);
				addSpace = true;

			}

			if (!Utils.nv(etu).isEmpty()) {
				if (addSpace) {
					noteBuf.append(" ");
				}
				noteBuf.append(etu);
				addSpace = true;
			}
			if (!Utils.nv(patro).isEmpty()) {
				if (addSpace) {
					noteBuf.append(" ");

				}
				noteBuf.append(patro);
				addSpace = true;
			}
			if (!Utils.nv(suku).isEmpty()) {
				if (addSpace) {
					noteBuf.append(" ");
				}
				noteBuf.append(suku);
				refName.append(suku);

			}
			noteBuf.append(". ");

			if (!Utils.nv(etu).isEmpty()) {
				if (refName.length() > 0) {
					refName.append(",");
				}
				refName.append(etu);
				if (!Utils.nv(patro).isEmpty()) {
					refName.append(" ");
				}

			}
			if (!Utils.nv(patro).isEmpty()) {
				refName.append(patro);
			}
			refs.add(refName.toString());

		}
	}

	private void uploadVihitytToDb() {
		SukuData kast = new SukuData();
		String hiskiSource = "Hiski vihityt [" + eventId + "]";
		kast.persons = new PersonLongData[personCount];
		int miesId = 0;
		int vaimoId = 0;
		int miesIdx = -1;
		int naisIdx = -1;
		String marrDate = null;

		Vector<String> refs = new Vector<String>();

		Vector<UnitNotice> noticesMan = new Vector<UnitNotice>();
		Vector<UnitNotice> noticesWoman = new Vector<UnitNotice>();
		Vector<UnitNotice> notices;
		for (int i = 0; i < personCount; i++) {
			String aux = pType[i].getText();
			String occu;
			String etu;
			String patro;
			String suku;
			String village;
			String farm;
			UnitNotice notice;
			String sex = null;
			StringBuilder noteBuf = new StringBuilder();
			StringBuilder hbp = new StringBuilder();
			switch (pSex[i].getSelectedIndex()) {
			case 1:
				sex = "M";
				break;
			case 2:
				sex = "F";
				break;
			case 3:
				sex = "U";
				break;
			default:
				JOptionPane.showMessageDialog(this,
						Resurses.getString("ERROR_MISSINGSEX"));
				return;
			}

			if ("mies".equals(aux) || "vaimo".equals(aux)) {
				if ("mies".equals(aux)) {
					notices = noticesMan;
					miesIdx = i;
				} else {
					notices = noticesWoman;
					naisIdx = i;
				}
				int puoliso = -1 - i;
				if ("mies".equals(aux)) {
					if (hiskiPid[0] > 0)
						puoliso = hiskiPid[0];
				} else {
					if (hiskiPid[1] > 0)
						puoliso = hiskiPid[1];
				}

				kast.persons[i] = new PersonLongData(puoliso, "INDI", sex);
				kast.persons[i].setSource(hiskiSource);
				occu = pOccu[i].getText();
				if (!occu.isEmpty()) {
					notice = new UnitNotice("OCCU");
					notice.setDescription(occu);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				etu = pGivenname[i].getText();
				patro = pPatronym[i].getText();
				suku = pSurname[i].getText();
				if (!etu.isEmpty() || !patro.isEmpty() || !suku.isEmpty()) {
					if (kast.persons[i].getPid() > 0) {
						if (hbp.length() > 0) {
							hbp.append(".\n");
						}
						hbp.append(Resurses.getString("HISKI_NAME"));
						hbp.append(":");
						if (!etu.isEmpty()) {
							hbp.append(" ");
							hbp.append(etu);
						}
						if (!patro.isEmpty()) {
							hbp.append(" ");
							hbp.append(patro);
						}
						if (!suku.isEmpty()) {
							hbp.append(" ");
							hbp.append(suku);
						}

					} else {
						notice = new UnitNotice("NAME");
						notice.setGivenname(etu);
						notice.setPatronym(patro);
						notice.setSurname(suku);
						notice.setSource(hiskiSource);
						notices.add(notice);
					}
				}
				village = pAgeVillage[i].getText();
				farm = pReasonFarm[i].getText();
				village = eventVillage.getText();
				if (village.equals("-")) {
					village = "";
				}
				farm = eventFarm.getText();
				if (farm.equals("-")) {
					farm = "";
				}
				// kylä / talo

				if (eventFirstDate.getText().length() > 0) {
					if (hbp.length() > 0) {
						hbp.append(".\n");
					}
					hbp.append(eventFirstType.getText());
					hbp.append(": ");
					hbp.append(Utils.textDate(
							toDbDate(eventFirstDate.getText()), false));

				}

				marrDate = eventLastDate.getText();

				notice = new UnitNotice("HISKI");

				notice.setNoticeType(eventLastType.getText());
				if (eventLastDate.getText().length() > 0) {
					notice.setFromDate(toDbDate(marrDate));
				}
				if (!village.isEmpty()) {
					notice.setVillage(village);
				}
				if (!farm.isEmpty()) {
					notice.setFarm(farm);
				}

				String tmp = eventReason.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_HUOM"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				tmp = eventOrigComment.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_ORIG"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				tmp = eventUserComment.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_OMA"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				tmp = this.eventNote.getText();
				if (!tmp.isEmpty()) {
					noteBuf.append(Resurses.getString("HISKI_MUUT"));
					noteBuf.append(": ");
					noteBuf.append(tmp);
					noteBuf.append(".\n");
				}
				if (hbp.length() > 0) {
					hbp.append(".\n");
				}
				hbp.append(noteBuf);

				if (hbp.length() > 0) {
					notice.setNoteText(hbp.toString());
				}
				notice.setSource(hiskiSource);
				notices.add(notice);

			} else {
				addOtherPersonToText(refs, noteBuf, i);
			}
		}

		if (noticesMan.size() > 0) {
			kast.persons[miesIdx].setNotices(noticesMan
					.toArray(new UnitNotice[0]));
			miesId = kast.persons[miesIdx].getPid();
		}
		if (noticesWoman.size() > 0) {
			kast.persons[naisIdx].setNotices(noticesWoman
					.toArray(new UnitNotice[0]));
			vaimoId = kast.persons[naisIdx].getPid();
		}

		Vector<Relation> relations = new Vector<Relation>();
		Relation rel;

		RelationNotice[] rNotices = new RelationNotice[1];
		if (miesId != 0 && vaimoId != 0) {
			rel = new Relation(0, miesId, vaimoId, "WIFE", hiskiSurety, null,
					null);
			relations.add(rel);

			rNotices[0] = new RelationNotice("MARR");
			rNotices[0].setFromDate(marrDate);
			rNotices[0].setPlace(srk.getText());
			rNotices[0].setSource(hiskiSource);
			rel.setNotices(rNotices);

		}
		kast.relations = relations.toArray(new Relation[0]);

		try {
			SukuData response = Suku.kontroller.getSukuData(kast, "cmd=upload");

			for (int i = 0; i < response.pers.length; i++) {
				suku.updatePerson(response.pers[i]);
			}

		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, "Hiski :" + e.getMessage());
			e.printStackTrace();
		}

	}

	private void uploadKastetutToDb() {
		SukuData kast = new SukuData();
		String hiskiSource = "Hiski kastetut [" + eventId + "]";
		kast.persons = new PersonLongData[personCount];
		int isaId = 0;
		int aitiId = 0;
		int lapsiId = 0;
		Vector<String> refs = new Vector<String>();
		StringBuilder noteBuf = new StringBuilder();
		// StringBuilder privBuf = new StringBuilder();
		Vector<UnitNotice> notices = null;
		int childIdx = -1;
		Vector<UnitNotice> noticesChild = new Vector<UnitNotice>();

		for (int i = 0; i < personCount; i++) {
			String aux = pType[i].getText();
			notices = new Vector<UnitNotice>();
			String occu;
			String etu;
			String patro;
			String suku;
			String age;
			StringBuilder hbp = new StringBuilder();

			UnitNotice notice;
			String sex = null;
			switch (pSex[i].getSelectedIndex()) {
			case 1:
				sex = "M";
				break;
			case 2:
				sex = "F";
				break;
			case 3:
				sex = "U";
				break;
			default:
				if (hiskiPers[i] != null) {
					sex = hiskiPers[i].getSex();
					break;
				}
				JOptionPane.showMessageDialog(this,
						Resurses.getString("ERROR_MISSINGSEX"));
				return;
			}

			if ("isa".equals(aux) || "aiti".equals(aux)) {

				int vanhempi = -1 - i;
				if (aux.equals("isa")) {
					if (hiskiPid[0] > 0) {
						vanhempi = hiskiPid[0];
					}
					isaId = vanhempi;
				} else {
					if (hiskiPid[1] > 0) {
						vanhempi = hiskiPid[1];
					}
					aitiId = vanhempi;
				}

				kast.persons[i] = new PersonLongData(vanhempi, "INDI", sex);
				kast.persons[i].setSource(hiskiSource);
				occu = pOccu[i].getText();
				if (!occu.isEmpty()) {
					notice = new UnitNotice("OCCU");
					notice.setDescription(occu);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				etu = pGivenname[i].getText();
				patro = pPatronym[i].getText();
				suku = pSurname[i].getText();
				if (!etu.isEmpty() || !patro.isEmpty() || !suku.isEmpty()) {
					if (kast.persons[i].getPid() > 0) {
						if (hbp.length() > 0) {
							hbp.append(".\n");
						}
						hbp.append(Resurses.getReportString("HISKI_NAME"));
						hbp.append(":");
						if (!etu.isEmpty()) {
							hbp.append(" ");
							hbp.append(etu);
						}
						if (!patro.isEmpty()) {
							hbp.append(" ");
							hbp.append(patro);
						}
						if (!suku.isEmpty()) {
							hbp.append(" ");
							hbp.append(suku);
						}

					} else {
						notice = new UnitNotice("NAME");
						notice.setGivenname(etu);
						notice.setPatronym(patro);
						notice.setSurname(suku);
						notice.setSource(hiskiSource);
						notices.add(notice);
					}
				}
				age = pAgeVillage[i].getText();

				if (!age.isEmpty()) {

					String datex = eventLastDate.getText();
					if (datex.isEmpty()) {
						datex = eventFirstDate.getText();
					}
					if (!datex.isEmpty()) {
						String parts[] = pAgeVillage[i].getText().split(";");
						if (parts.length > 0) {
							String vv = null;
							String kk = null;
							String vk = null;
							String pv = null;

							for (int j = 0; j < parts.length; j++) {
								String tmp[] = parts[j].split("=");
								if (tmp.length == 2) {
									if (tmp[0].equals("vv")) {
										vv = tmp[1];
									}
									if (tmp[0].equals("kk")) {
										kk = tmp[1];
									}
									if (tmp[0].equals("vk")) {
										vk = tmp[1];
									}
									if (tmp[0].equals("pv")) {
										pv = tmp[1];
									}
								}
							}
							String auxdate = toBirthDate(datex, vv, kk, vk, pv);
							if (auxdate != null && !auxdate.isEmpty()) {
								if (kast.persons[i].getPid() > 0) {
									if (hbp.length() > 0) {
										hbp.append(".\n");
									}
									hbp.append(Resurses
											.getReportString("HISKI_BIRT"));
									hbp.append(":");
									if (!auxdate.isEmpty()) {
										hbp.append(" ");
										hbp.append(Utils.textDate(auxdate,
												false));
									}
								} else {
									notice = new UnitNotice("BIRT");
									notice.setFromDate(auxdate);
									notice.setDatePrefix("CAL");
									notice.setSource(hiskiSource);
									notice.setPrivateText(age);
									notices.add(notice);
								}
							}
						}
					}
				}
				if (hbp.length() > 0) {
					notice = new UnitNotice("HISKI");

					notice.setSource(hiskiSource + "\n" + hbp.toString());
					notices.add(notice);
				}
				if (notices.size() > 0) {
					kast.persons[i].setNotices(notices
							.toArray(new UnitNotice[0]));
				}
			} else if ("lapsi".equals(aux)) {
				childIdx = i;
				lapsiId = -1 - i;
				if (hiskiPid[2] > 0)
					lapsiId = hiskiPid[2];

				kast.persons[childIdx] = new PersonLongData(lapsiId, "INDI",
						sex);
				kast.persons[childIdx].setSource(hiskiSource);
				etu = pGivenname[childIdx].getText();
				patro = pPatronym[childIdx].getText();
				suku = pSurname[childIdx].getText();
				if (!etu.isEmpty() || !patro.isEmpty() || !suku.isEmpty()) {
					if (kast.persons[childIdx].getPid() > 0) {
						if (noteBuf.length() > 0) {
							noteBuf.append(".\n");
						}
						noteBuf.append(Resurses.getString("HISKI_NAME"));
						noteBuf.append(":");
						if (!etu.isEmpty()) {
							noteBuf.append(" ");
							noteBuf.append(etu);
						}
						if (!patro.isEmpty()) {
							noteBuf.append(" ");
							noteBuf.append(patro);
						}
						if (!suku.isEmpty()) {
							noteBuf.append(" ");
							noteBuf.append(suku);
						}
					} else {
						notice = new UnitNotice("NAME");
						notice.setGivenname(etu);
						notice.setPatronym(patro);
						notice.setSurname(suku);
						notice.setSource(hiskiSource);
						noticesChild.add(notice);
					}
				}
				String dat = toDbDate(eventFirstDate.getText());
				if (dat != null) {

					if (kast.persons[i].getPid() > 0) {
						if (noteBuf.length() > 0) {
							noteBuf.append(".\n");
						}
						noteBuf.append(Resurses.getString("HISKI_" + pvm1Name));
						noteBuf.append(":");
						if (!dat.isEmpty()) {
							noteBuf.append(" " + Utils.textDate(dat, false));
							noteBuf.append(" " + srk.getText());
							if (!eventVillage.getText().isEmpty()) {
								noteBuf.append(" ");
								noteBuf.append(eventVillage.getText());
							}
							if (!eventFarm.getText().isEmpty()) {
								noteBuf.append(" ");
								noteBuf.append(eventFarm.getText());
							}
						}
					} else {

						notice = new UnitNotice(
								"synt".equals(pvm1Name) ? "BIRT" : "CHR");
						notice.setFromDate(dat);
						notice.setPlace(srk.getText());
						notice.setVillage(eventVillage.getText());
						notice.setFarm(eventFarm.getText());
						notice.setSource(hiskiSource);
						noticesChild.add(notice);
					}
				}
				dat = toDbDate(eventLastDate.getText());
				if (dat != null) {
					if (kast.persons[i].getPid() > 0) {
						if (noteBuf.length() > 0) {
							noteBuf.append(".\n");
						}
						noteBuf.append(Resurses.getString("HISKI_" + pvm2Name));
						noteBuf.append(":");
						if (!dat.isEmpty()) {
							noteBuf.append(" " + Utils.textDate(dat, false));
						}
						noteBuf.append(" " + srk.getText());
						if (!eventVillage.getText().isEmpty()) {
							noteBuf.append(" ");
							noteBuf.append(eventVillage.getText());
						}
						if (!eventFarm.getText().isEmpty()) {
							noteBuf.append(" ");
							noteBuf.append(eventFarm.getText());
						}
					} else {
						notice = new UnitNotice(
								"synt".equals(pvm2Name) ? "BIRT" : "CHR");
						notice.setFromDate(dat);
						notice.setPlace(srk.getText());
						notice.setVillage(eventVillage.getText());
						notice.setFarm(eventFarm.getText());
						notice.setSource(hiskiSource);
						noticesChild.add(notice);
					}
				}

			} else {
				addOtherPersonToText(refs, noteBuf, i);
			}
			// if (hbc.length() > 0) {
			// notice = new UnitNotice("HISKI");
			// notice.setNoteText(hbc.toString());
			// notice.setSource(hiskiSource);
			// noticesChild.add(notice);
			// }
		}

		String aux = eventReason.getText();
		if (!aux.isEmpty()) {
			noteBuf.append(Resurses.getString("HISKI_HUOM"));
			noteBuf.append(": ");
			noteBuf.append(aux);
			noteBuf.append(".\n");
		}
		aux = eventOrigComment.getText();
		if (!aux.isEmpty()) {
			noteBuf.append(Resurses.getString("HISKI_ORIG"));
			noteBuf.append(": ");
			noteBuf.append(aux);
			noteBuf.append(".\n");
		}
		aux = eventUserComment.getText();
		if (!aux.isEmpty()) {
			noteBuf.append(Resurses.getString("HISKI_OMA"));
			noteBuf.append(": ");
			noteBuf.append(aux);
			noteBuf.append(".\n");
		}
		aux = this.eventNote.getText();
		if (!aux.isEmpty()) {
			noteBuf.append(Resurses.getString("HISKI_MUUT"));
			noteBuf.append(": ");
			noteBuf.append(aux);
			noteBuf.append(".\n");
		}
		if (noteBuf.length() > 0) {
			UnitNotice note = new UnitNotice("HISKI");
			note.setSource(hiskiSource + "\n" + noteBuf.toString());
			if (refs.size() > 0) {
				note.setRefNames(refs.toArray(new String[0]));
			}
			noticesChild.add(note);
		}
		kast.persons[childIdx].setNotices(noticesChild
				.toArray(new UnitNotice[0]));
		Vector<Relation> relations = new Vector<Relation>();
		Relation rel;
		if (isaId < 0 || lapsiId < 0) {
			rel = new Relation(0, lapsiId, isaId, "FATH", hiskiSurety, null,
					null);
			relations.add(rel);
		}
		if (aitiId < 0 || lapsiId < 0) {
			rel = new Relation(0, lapsiId, aitiId, "MOTH", hiskiSurety, null,
					null);
			relations.add(rel);
		}
		if (isaId != 0 && aitiId != 0) {
			rel = new Relation(0, isaId, aitiId, "WIFE", hiskiSurety, null,
					null);
			relations.add(rel);
		}

		kast.relations = relations.toArray(new Relation[0]);

		try {

			SukuData response = Suku.kontroller.getSukuData(kast, "cmd=upload");

			for (int i = 0; i < response.pers.length; i++) {
				suku.updatePerson(response.pers[i]);
			}

		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, "Hiski :" + e.getMessage());
			e.printStackTrace();
		}

	}

	private void fetchFromHiski() {

		book.setText("");
		srk.setText("");
		srkNo.setText("");
		eventId = null;

		eventFirstType.setText("");
		eventFirstDate.setText("");
		eventLastType.setText("");
		eventLastDate.setText("");

		bookName = null;
		pvm1Name = null;
		pvm2Name = null;
		personCount = 0;

		eventFrom.setText("");
		eventTo.setText("");
		eventVillage.setText("");
		eventFarm.setText("");
		eventReason.setText("");
		eventUserComment.setText("");
		eventOrigComment.setText("");
		eventNote.setText("");

		for (int i = 0; i < pNumero.length; i++) {

			remove(pSukuPid[i]);
			remove(pSukuName[i]);
			remove(pNumero[i]);
			remove(pType[i]);
			remove(pSex[i]);
			remove(rOccu[i]);
			remove(rGivenname[i]);
			remove(rPatronym[i]);
			remove(rSurname[i]);
			remove(pOccu[i]);
			remove(pGivenname[i]);
			remove(pPatronym[i]);
			remove(pSurname[i]);
			remove(pAgeVillage[i]);
			remove(pReasonFarm[i]);

		}

		pNumero = new JLabel[0];
		pSukuPid = new JLabel[0];
		pSukuName = new JLabel[0];
		pTypeName = new String[0];
		pType = new JLabel[0];
		pSex = new JComboBox[0];
		pOccu = new JTextField[0];
		pGivenname = new JTextField[0];
		pPatronym = new JTextField[0];
		pSurname = new JTextField[0];
		pAgeVillage = new JTextField[0];
		pReasonFarm = new JTextField[0];

		StringBuilder sb = new StringBuilder();
		sb.append("http://hiski.genealogia.fi/");

		String requri; // http://hiski.genealogia.fi/hiski?fi+t23806+xml
		int resu;

		// String paras[] = new String[params.length];
		// "suku?userno="+this.userno+"&person=" + pid

		SukuPopupMenu pop = SukuPopupMenu.getInstance();

		for (int i = 0; i < 3; i++) {
			pop.enableHiskiPerson(i, false, null);
		}

		sb.append("hiski?fi+t");

		String hiskiNumStr = this.hiskiNumber.getText().trim();

		int hiskiNum = 0;
		Document doc = null;
		try {
			hiskiNum = Integer.parseInt(hiskiNumStr);
		} catch (NumberFormatException ne) {
			JOptionPane.showMessageDialog(this, "'" + hiskiNumStr + "'"
					+ Resurses.getString("NOT_NUMBER"),
					Resurses.getString("HISKI_NUMBER"),
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		sb.append("" + hiskiNum);
		hiskiBrowserUrl = sb.toString();
		sb.append("+xml");

		requri = sb.toString();
		try {

			// logger.fine("URILOG: " + requri);
			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			// String encoding = uc.getContentEncoding();

			resu = uc.getResponseCode();
			// System.out.println("Resu = " + resu);
			if (resu == 200) {

				InputStream in = uc.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(in);
				// SukuData fam = null;
				try {

					doc = this.bld.parse(bis);

					bis.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new SukuException(e);
				}
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.toString());
			logger.log(Level.WARNING, "hiski", e);
			return;
		}

		Element docEle = doc.getDocumentElement();
		Element ele;
		NodeList nl = docEle.getElementsByTagName("tapahtuma");
		if (nl.getLength() != 1) {
			logger.warning("tapahtuma count = " + nl.getLength());
		}
		if (nl.getLength() > 0) {
			Element tap = (Element) nl.item(0);
			try {
				bookName = tap.getAttribute("kirja");
				book.setText(Resurses.getString(bookName));
			} catch (MissingResourceException mre) {
				book.setText(bookName);
			}

			eventVillage.setEnabled(false);
			eventFarm.setEnabled(false);
			eventFrom.setEnabled(false);
			eventTo.setEnabled(false);
			eventReason.setEnabled(false);
			if (bookName.equals("kastetut")) {
				eventVillage.setEnabled(true);
				eventFarm.setEnabled(true);
				eventReason.setEnabled(true);
			} else if (bookName.equals("vihityt")) {
				eventReason.setEnabled(true);
			} else if (bookName.equals("haudatut")) {
				eventVillage.setEnabled(true);
				eventFarm.setEnabled(true);

			} else {
				eventVillage.setEnabled(true);
				eventFarm.setEnabled(true);
				eventFrom.setEnabled(true);
				eventTo.setEnabled(true);
				eventReason.setEnabled(true);
			}

			NodeList henkNodes = docEle.getElementsByTagName("henkilo");
			personCount = henkNodes.getLength();
			initHiskiPersons(personCount);

			String vv = null;
			String kk = null;
			String vk = null;
			String pv = null;

			for (int pidx = 0; pidx < personCount; pidx++) {

				ele = (Element) henkNodes.item(pidx);

				pNumero[pidx].setText("" + pidx);
				String theType = ele.getAttribute("tyyppi");
				pType[pidx].setText(theType);
				boolean showMenu = true;
				if ("isa".equals(theType)) {

					pSex[pidx].setSelectedIndex(1);
					pSex[pidx].setEnabled(false);
				} else if ("aiti".equals(theType)) {

					pSex[pidx].setSelectedIndex(2);
					pSex[pidx].setEnabled(false);

				} else if ("mies".equals(theType)) {

					pSex[pidx].setSelectedIndex(1);
					pSex[pidx].setEnabled(false);
				} else if ("vaimo".equals(theType)) {

					pSex[pidx].setSelectedIndex(2);
					pSex[pidx].setEnabled(false);
				} else if ("omainen".equals(theType)) {
					pSex[pidx].setVisible(false);
					showMenu = false;
				}
				NodeList nlh = ele.getChildNodes();
				Element elp;

				StringBuilder muut = new StringBuilder();
				for (int j = 0; j < nlh.getLength(); j++) {
					if (nlh.item(j).getNodeType() == Node.ELEMENT_NODE) {
						elp = (Element) nlh.item(j);

						String elpNam = elp.getNodeName();

						if (elpNam == null) {
							// TODO:
						} else if (elpNam.equals("kyla")) {
							pAgeVillage[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("talo")) {
							pReasonFarm[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("lisat")) {
							// TODO:
						} else if (elpNam.equals("ammatti")) {
							rOccu[pidx].setText(elp.getTextContent());
							pOccu[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("etunimi")) {
							rGivenname[pidx].setText(elp.getTextContent());
							pGivenname[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("patronyymi")) {
							rPatronym[pidx].setText(elp.getTextContent());
							pPatronym[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("sukunimi")) {
							rSurname[pidx].setText(elp.getTextContent());
							pSurname[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("ika")) {
							StringBuilder age = new StringBuilder();

							vv = elp.getAttribute("vv");
							if (!vv.isEmpty()) {
								age.append("vv=" + vv + ";");
							}
							kk = elp.getAttribute("kk");
							if (!kk.isEmpty()) {
								age.append("kk=" + kk + ";");
							}
							vk = elp.getAttribute("vk");
							if (!vk.isEmpty()) {
								age.append("vk=" + vk + ";");
							}
							pv = elp.getAttribute("pv");
							if (!pv.isEmpty()) {
								age.append("pv=" + pv);
							}

							if (age.length() > 0) {
								pAgeVillage[pidx].setText(age.toString());
							}
						} else {
							if (muut.length() > 0) {
								muut.append(";");
							}
							muut.append(elp.getTextContent());
						}
					}

				}
				if (showMenu) {
					pop.enableHiskiPerson(pidx, true, theType);
				}
			}

			NodeList taplist = tap.getChildNodes();
			int pvmno = 0;

			StringBuilder remark = new StringBuilder();
			for (int i = 0; i < taplist.getLength(); i++) {
				if (taplist.item(i).getNodeType() == Node.ELEMENT_NODE) {
					ele = (Element) taplist.item(i);
					String eleName = ele.getNodeName();

					if (eleName == null) {
						// shouldn't come here anyway
					} else if (eleName.equals("srk")) {
						srkNo.setText(ele.getAttribute("nro"));

						String ll = Resurses.getLanguage();
						String tmp = ele.getTextContent();
						int llidx = tmp.indexOf("-");
						if (llidx > 0) {
							if (ll.equals("sv") && llidx < tmp.length() + 1) {
								tmp = tmp.substring(llidx + 1).trim();
							} else {
								tmp = tmp.substring(0, llidx).trim();
							}
						}
						srk.setText(tmp);
					} else if (eleName.equals("tapahtumatunniste")) {
						eventId = ele.getAttribute("id");
					} else if (eleName.equals("pvm")) {
						pvmno++;
						String datex = null;
						if (pvmno == 1) {
							try {
								pvm1Name = ele.getAttribute("tyyppi");
								eventFirstType.setText(Resurses
										.getString(pvm1Name));
							} catch (MissingResourceException mre) {
								eventFirstType.setText(pvm1Name);
							}
							datex = ele.getTextContent();
							eventFirstDate.setText(datex);
						} else {
							try {
								pvm2Name = ele.getAttribute("tyyppi");
								eventLastType.setText(Resurses
										.getString(pvm2Name));
							} catch (MissingResourceException mre) {
								eventLastType.setText(pvm2Name);
							}
							datex = ele.getTextContent();
							eventLastDate.setText(datex);

						}

						if ("haudatut".equals(bookName)
								&& ((vv + kk + vk + pv).length() > 0)) {
							String aux = toBirthDate(datex, vv, kk, vk, pv);
							if (aux != null && !aux.isEmpty()) {
								eventExtraType.setVisible(true);
								eventExtraDate.setVisible(true);
								// eventExtraDate.setText(aux);
								eventExtraDate.setDate("CAL", aux, "");
							}

						} else {
							eventExtraType.setVisible(false);
							eventExtraDate.setVisible(false);
						}

					} else if (eleName.equals("kyla")) {
						eventVillage.setText(ele.getTextContent());
					} else if (eleName.equals("talo")) {
						eventFarm.setText(ele.getTextContent());
					} else if (eleName.equals("mista")) {
						eventFrom.setText(ele.getTextContent());
					} else if (eleName.equals("minne")) {
						eventTo.setText(ele.getTextContent());
					} else if (eleName.equals("henkilo")) {
						// TODO:
					} else if (eleName.equals("kuolinsyy")) {
						eventReason.setText(ele.getTextContent());
					} else if (eleName.equals("oma_kommentti")) {
						eventUserComment.setText(ele.getTextContent());
					} else if (eleName.equals("alkup_kommentti")) {
						eventOrigComment.setText(ele.getTextContent());

					} else {

						remark.append(ele.getTextContent());
						eventNote.setText(remark.toString());
					}
				}
			}

		}
	}

	private String toBirthDate(String eventDate, String ageYy, String ageMm,
			String ageWk, String ageDy) {
		String auxDate = toDbDate(eventDate);

		// first subtract year

		int year = 0;
		int month = 0;
		int day = 0;
		int yy, mm, wk, dy;
		if (auxDate == null || auxDate.length() < 4) {
			return "";
		}
		try {
			year = Integer.parseInt(auxDate.substring(0, 4));
		} catch (NumberFormatException ne) {
			return "";
		}

		try {
			yy = Integer.parseInt(ageYy);
		} catch (NumberFormatException ne) {
			yy = 0;
		}
		year -= yy;

		if (auxDate.length() > 5) {
			try {
				month = Integer.parseInt(auxDate.substring(4, 6));

			} catch (NumberFormatException ne) {
				month = 0;
				day = 0;
			}
			try {
				mm = Integer.parseInt(ageMm);
			} catch (NumberFormatException ne) {
				mm = 0;
			}
			try {
				wk = Integer.parseInt(ageWk);
			} catch (NumberFormatException ne) {
				wk = 0;

			}
			try {
				dy = Integer.parseInt(ageDy);
			} catch (NumberFormatException ne) {
				dy = 0;
			}
			if (yy == 0 && mm == 0 && wk == 0 && dy == 0) {
				return "";
			}

			if (month > 0) {

				if (auxDate.length() > 7) {
					try {
						day = Integer.parseInt(auxDate.substring(6, 8));
					} catch (NumberFormatException ne) {
						day = 0;
					}
				}
				if (month > 0) {
					while (mm > month) {
						year--;
						mm -= 12;
					}
					month -= mm;

					if (wk > 0) {
						dy += wk * 7;
						// int mm1 = ((int) (wk * (30. / 7.))) / 30;
						// int mm2 = ((int) (wk * (30. / 7.))) % 30;
						//
						// while (mm1 > month) {
						// year--;
						// mm1 -= 12;
						// }
						// month -= mm1;
						// dy += mm2;
					}

					if (day > 0 && dy > 0) {
						while (dy >= day) {
							if (month > 1) {
								month--;
							} else {
								year--;
								month = 12;
							}
							switch (month) {
							case 2:
							case 4:
							case 6:
							case 7:
							case 9:
							case 11:
							case 12:
								dy -= 31;
								break;
							case 1:
								dy -= 28;
								break;
							default:
								dy -= 30;
							}
						}
						day -= dy;
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		String aux = "0000" + year;
		String tmp = aux.substring(aux.length() - 4);
		sb.append(tmp);
		if (month > 0) {
			aux = "00" + month;
			tmp = aux.substring(aux.length() - 2);
			sb.append(tmp);
			if (day > 0) {
				aux = "00" + day;
				tmp = aux.substring(aux.length() - 2);
				sb.append(tmp);
			}
		}
		return sb.toString();
	}

	private String toDbDate(String hiskiDate) {
		if (hiskiDate == null || hiskiDate.isEmpty())
			return null;
		String[] parts = hiskiDate.split("\\.");
		StringBuilder sb = new StringBuilder();

		for (int i = parts.length - 1; i >= 0; i--) {
			if (sb.length() == 0) { // year
				sb.append(parts[i]);
				if (parts.length == 3
						&& (parts[0].equals("") || parts[0].equals("0"))
						&& (parts[1].equals("") || parts[1].equals("0")))
					break;
			} else {
				if (i == 0 && parts[i].equals("") || parts[i].equals("0")) {
					// if date missing dont add it
				} else {
					String aux = "00" + parts[i];
					String tmp = aux.substring(parts[i].length());
					sb.append(tmp);
				}
			}
		}
		return sb.toString();

	}

}
