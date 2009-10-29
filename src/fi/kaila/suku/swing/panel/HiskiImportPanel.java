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

import fi.kaila.suku.swing.ISuku;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.util.SukuPopupMenu;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * Panel to do traffic to hiski
 * 
 * @author Kalle
 * 
 */
public class HiskiImportPanel extends JPanel implements ActionListener {

	private static Logger logger = Logger.getLogger(HiskiImportPanel.class
			.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int hiskiSurety = 40;

	JMenuBar menubar;
	JMenu mFile;
	JMenuItem mGrid;

	private JButton close;
	private JButton getHiski;
	private JTextField hiskiNumber;
	private JButton testDo;

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
	private JTextField eventRemark;

	private JLabel[] pNumero;
	private String[] pTypeName;
	private JLabel[] pType;
	private JComboBox[] pSex;
	private JLabel[] pSukuPid;
	private JLabel[] pSukuName;
	private JTextField[] pOccu;
	private JTextField[] pGivenname;
	private JTextField[] pPatronym;
	private JTextField[] pSurname;
	private JTextField[] pAge;
	private JTextField[] pReason;

	private String bookName = null;
	private String pvm1Name = null;
	private String pvm2Name = null;
	private int personCount = 0;
	private String eventId = null;
	private ISuku suku;

	private DocumentBuilderFactory factory = null;
	private DocumentBuilder bld = null;

	/**
	 * @param suku
	 */
	public HiskiImportPanel(ISuku suku) {
		this.suku = suku;
		initMe();

	}

	private String[] sexes = new String[4];

	private int[] hiskiPid = { 0, 0, 0 };

	// private String [] sukuName = {null,null,null};

	private void initMe() {

		setLayout(null);
		// setLocation(200, 200);

		JLabel lbl = new JLabel(Resurses.getString(Resurses.HISKI_NUMBER));
		add(lbl);
		lbl.setBounds(40, 20, 190, 20);

		this.hiskiNumber = new JTextField("23806");
		add(this.hiskiNumber);
		this.hiskiNumber.setBounds(40, 50, 150, 20);

		this.getHiski = new JButton(Resurses.getString(Resurses.GET_HISKI));
		// this.ok.setDefaultCapable(true);
		add(this.getHiski);
		this.getHiski.setActionCommand(Resurses.GET_HISKI);
		this.getHiski.addActionListener(this);
		this.getHiski.setBounds(40, 80, 150, 24);

		this.close = new JButton(Resurses.getString(Resurses.CLOSE));
		// this.ok.setDefaultCapable(true);
		add(this.close);
		this.close.setActionCommand(Resurses.CLOSE);
		this.close.addActionListener(this);
		this.close.setBounds(40, 110, 150, 24);

		this.testDo = new JButton(Resurses.getString(Resurses.TEST_DO));
		// this.ok.setDefaultCapable(true);
		add(this.testDo);
		this.testDo.setActionCommand(Resurses.TEST_DO);
		this.testDo.addActionListener(this);
		this.testDo.setBounds(40, 140, 150, 24);

		sexes[0] = "";
		sexes[1] = Resurses.getString("SEX_M");
		sexes[2] = Resurses.getString("SEX_F");
		sexes[3] = Resurses.getString("SEX_U");

		book = new JLabel();
		add(book);
		book.setBounds(200, 20, 100, 20);

		srkNo = new JLabel();
		add(srkNo);
		srkNo.setBounds(310, 20, 40, 20);

		srk = new JTextField();
		add(srk);
		srk.setBounds(360, 20, 150, 20);

		eventFirstType = new JLabel();
		add(eventFirstType);
		eventFirstType.setBounds(200, 50, 65, 20);

		eventFirstDate = new JTextField();
		add(eventFirstDate);
		eventFirstDate.setBounds(275, 50, 75, 20);

		eventLastType = new JLabel();
		add(eventLastType);
		eventLastType.setBounds(360, 50, 65, 20);

		eventLastDate = new JTextField();
		add(eventLastDate);
		eventLastDate.setBounds(435, 50, 75, 20);

		eventVillage = new JTextField();
		add(eventVillage);
		eventVillage.setBounds(200, 80, 150, 20);

		eventFarm = new JTextField();
		add(eventFarm);
		eventFarm.setBounds(360, 80, 150, 20);

		eventRemark = new JTextField();
		add(eventRemark);
		eventRemark.setBounds(200, 110, 310, 20);

		eventFrom = new JTextField();
		add(eventFrom);
		eventFrom.setBounds(200, 140, 150, 20);

		eventTo = new JTextField();
		add(eventTo);
		eventTo.setBounds(360, 140, 150, 20);

		lbl = new JLabel(Resurses.getString("HISKI_VILLAGEFARM"));
		add(lbl);
		lbl.setBounds(520, 80, 120, 20);
		lbl = new JLabel(Resurses.getString("HISKI_HUOM"));
		add(lbl);
		lbl.setBounds(520, 110, 120, 20);
		lbl = new JLabel(Resurses.getString("HISKI_MISTAMINNE"));
		add(lbl);
		lbl.setBounds(520, 140, 120, 20);

		initHiskiPersons(0);

		lbl = new JLabel(Resurses.getString("HISKI_TYPEOCCU"));
		add(lbl);
		lbl.setBounds(40, 180, 120, 20);

		lbl = new JLabel(Resurses.getString("HISKI_GIVEAGE"));
		add(lbl);
		lbl.setBounds(200, 180, 120, 20);

		lbl = new JLabel(Resurses.getString("HISKI_PATROREASON"));
		add(lbl);
		lbl.setBounds(360, 180, 120, 20);

		lbl = new JLabel(Resurses.getString("HISKI_SUR"));
		add(lbl);
		lbl.setBounds(520, 180, 120, 20);

		this.factory = DocumentBuilderFactory.newInstance();
		this.factory.setValidating(false);

		try {
			this.bld = this.factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}

	}

	private void initHiskiPersons(int luku) {
		pNumero = new JLabel[luku];
		pSukuPid = new JLabel[luku];
		pSukuName = new JLabel[luku];
		pTypeName = new String[luku];
		pType = new JLabel[luku];
		pSex = new JComboBox[luku];
		pOccu = new JTextField[luku];
		pGivenname = new JTextField[luku];
		pPatronym = new JTextField[luku];
		pSurname = new JTextField[luku];
		pAge = new JTextField[luku];
		pReason = new JTextField[luku];
		int i = 0;

		for (i = 0; i < luku; i++) {

			pSukuPid[i] = new JLabel();
			add(pSukuPid[i]);
			pSukuPid[i].setBounds(40, 210 + i * 70, 100, 20);

			pSukuName[i] = new JLabel();
			add(pSukuName[i]);
			pSukuName[i].setBounds(200, 210 + i * 70, 200, 20);

			pNumero[i] = new JLabel();
			add(pNumero[i]);
			pNumero[i].setBounds(40, 230 + i * 70, 40, 20);

			pTypeName[i] = null;

			pType[i] = new JLabel();
			add(pType[i]);
			pType[i].setBounds(60, 230 + i * 70, 40, 20);

			pSex[i] = new JComboBox(sexes);
			add(pSex[i]);
			pSex[i].setBounds(100, 230 + i * 70, 80, 20);

			pOccu[i] = new JTextField();
			add(pOccu[i]);
			pOccu[i].setBounds(40, 255 + i * 70, 150, 20);

			pGivenname[i] = new JTextField();
			add(pGivenname[i]);
			pGivenname[i].setBounds(200, 230 + i * 70, 150, 20);

			pPatronym[i] = new JTextField();
			add(pPatronym[i]);
			pPatronym[i].setBounds(360, 230 + i * 70, 150, 20);

			pSurname[i] = new JTextField();
			add(pSurname[i]);
			pSurname[i].setBounds(520, 230 + i * 70, 150, 20);

			pAge[i] = new JTextField();
			add(pAge[i]);
			pAge[i].setBounds(200, 255 + i * 70, 150, 20);

			pReason[i] = new JTextField();
			add(pReason[i]);
			pReason[i].setBounds(360, 255 + i * 70, 310, 20);

		}
		Dimension panelSize = new Dimension(740, 255 + i * 70);
		this.setPreferredSize(panelSize);
		updateUI();
	}

	/**
	 * reset all fields
	 */
	public void resetHiskiPids() {
		for (int i = 0; i < hiskiPid.length; i++) {
			hiskiPid[i] = 0;
			pSukuName[i].setText("");
			pSukuPid[i].setText("");

		}
	}

	/**
	 * set a hiskipanel person
	 * 
	 * @param idx
	 * @param pid
	 * @param nimi
	 */
	public void setHiskiPid(int idx, int pid, String nimi) {
		if (idx >= 0 && idx < hiskiPid.length) {
			hiskiPid[idx] = pid;
			// sukuName[idx]=nimi;
			pSukuPid[idx].setText("" + pid);
			pSukuName[idx].setText(nimi);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		if (cmd == null)
			return;

		if (cmd.equals(Resurses.CLOSE)) {
			suku.HiskiFormClosing();
			// if(parent != null) {
			// parent.HiskiFormClosing();
			// }
			// this.setVisible(false);
		} else if (cmd.equals(Resurses.GET_HISKI)) {

			fetchFromHiski();

		} else if (cmd.equals(Resurses.TEST_DO)) {
			if ("kastetut".equals(bookName)) {
				uploadKastetutToDb();
			} else if ("vihityt".equals(bookName)) {
				uploadVihitytToDb();
			} else if ("haudatut".equals(bookName)) {
				uploadHaudatutToDb();
			} else if ("umuutt".equals(bookName) || "smuutt".equals(bookName)) {
				uploadMuuttaneetToDb();
			}
		}

	}

	private void uploadMuuttaneetToDb() {

		SukuData kast = new SukuData();
		String hiskiSource = "Hiski muuttaneet [" + eventId + "]";
		kast.persons = new PersonLongData[personCount];
		Vector<UnitNotice> notices = null;
		// String ktype = eventFirstType.getText();
		String kdate = eventFirstDate.getText();
		// String htype = eventLastType.getText();
		String hdate = eventLastDate.getText();

		for (int i = 0; i < personCount; i++) {
			String aux = pType[i].getText();
			notices = new Vector<UnitNotice>();
			String occu;
			String etu;
			String patro;
			String suku;
			String age;
			String text;
			String village;
			String farm = null;
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
				JOptionPane.showMessageDialog(this, Resurses
						.getString("ERROR_MISSINGSEX"));
				return;
			}

			if ("muuttaja".equals(aux)) {
				int muuttaja = -1 - i;
				if (hiskiPid[0] > 0)
					muuttaja = hiskiPid[0];

				kast.persons[i] = new PersonLongData(muuttaja, "INDI", sex);
				kast.persons[i].setSource(hiskiSource);
				occu = pOccu[i].getText();
				if (!occu.equals("")) {
					notice = new UnitNotice("OCCU");
					notice.setDescription(occu);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				etu = pGivenname[i].getText();
				patro = pPatronym[i].getText();

				suku = pSurname[i].getText();
				if (!etu.equals("") || !patro.equals("") || !suku.equals("")) {
					notice = new UnitNotice("NAME");
					notice.setGivenname(etu);
					notice.setPatronym(patro);
					notice.setSurname(suku);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				age = pAge[i].getText();
				text = pReason[i].getText();
				if (!"".equals(age)) {
					text += ";" + age;
				}

				village = eventVillage.getText();
				farm = eventFarm.getText();

				if (!"".equals(kdate)) {

					notice = new UnitNotice("EMIG");
					notice.setFromDate(toTextDate(kdate));
					notice.setPlace(srk.getText());
					notice.setVillage(village);
					notice.setFarm(farm);
					notice.setNoteText(text);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				if (!"".equals(hdate)) {

					notice = new UnitNotice("IMMI");
					notice.setFromDate(toTextDate(hdate));
					notice.setPlace(srk.getText());
					notice.setVillage(village);
					notice.setFarm(farm);
					if ("".equals(kdate)) {
						notice.setNoteText(text);
					}
					notice.setSource(hiskiSource);
					notices.add(notice);
				}

				if (notices.size() > 0) {
					kast.persons[i].setNotices(notices
							.toArray(new UnitNotice[0]));
				}
			}
		}

		try {
			Suku.kontroller.getSukuData(kast, "cmd=upload");
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, "Hiski :" + e.getMessage());
			e.printStackTrace();
		}

	}

	private void uploadHaudatutToDb() {
		SukuData kast = new SukuData();
		String hiskiSource = "Hiski haudatut [" + eventId + "]";
		kast.persons = new PersonLongData[personCount];
		Vector<UnitNotice> notices = null;
		// String ktype = eventFirstType.getText();
		String kdate = eventFirstDate.getText();
		// String htype = eventLastType.getText();
		String hdate = eventLastDate.getText();

		for (int i = 0; i < personCount; i++) {
			String aux = pType[i].getText();
			notices = new Vector<UnitNotice>();
			String occu;
			String etu;
			String patro;
			String suku;
			String age;
			String text;
			String village;
			String farm = null;
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
				JOptionPane.showMessageDialog(this, Resurses
						.getString("ERROR_MISSINGSEX"));
				return;
			}

			if ("vainaja".equals(aux)) {

				int vainaa = -1 - i;
				if (hiskiPid[0] > 0)
					vainaa = hiskiPid[0];

				kast.persons[i] = new PersonLongData(vainaa, "INDI", sex);
				kast.persons[i].setSource(hiskiSource);
				occu = pOccu[i].getText();
				if (!occu.equals("")) {
					notice = new UnitNotice("OCCU");
					notice.setDescription(occu);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				etu = pGivenname[i].getText();
				patro = pPatronym[i].getText();

				suku = pSurname[i].getText();
				if (!etu.equals("") || !patro.equals("") || !suku.equals("")) {
					notice = new UnitNotice("NAME");
					notice.setGivenname(etu);
					notice.setPatronym(patro);
					notice.setSurname(suku);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				age = pAge[i].getText();
				text = pReason[i].getText();
				if (!"".equals(age)) {
					text += ";" + age;
				}

				village = eventVillage.getText();
				farm = eventFarm.getText();

				if (!"".equals(kdate)) {

					notice = new UnitNotice("DEAT");
					notice.setNoticeType(eventRemark.getText());
					notice.setFromDate(toTextDate(kdate));
					notice.setPlace(srk.getText());
					notice.setVillage(village);
					notice.setFarm(farm);
					notice.setNoteText(text);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				if (!"".equals(hdate)) {

					notice = new UnitNotice("BURI");
					notice.setFromDate(toTextDate(hdate));
					notice.setPlace(srk.getText());
					notice.setVillage(village);
					notice.setFarm(farm);
					if ("".equals(kdate)) {
						notice.setNoticeType(eventRemark.getText());
						notice.setNoteText(text);
					}
					notice.setSource(hiskiSource);
					notices.add(notice);
				}

				if (notices.size() > 0) {
					kast.persons[i].setNotices(notices
							.toArray(new UnitNotice[0]));
				}
			}
		}

		try {
			Suku.kontroller.getSukuData(kast, "cmd=upload");
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, "Hiski :" + e.getMessage());
			e.printStackTrace();
		}

	}

	private void uploadVihitytToDb() {
		SukuData kast = new SukuData();
		String hiskiSource = "Hiski vihityt [" + eventId + "]";
		kast.persons = new PersonLongData[personCount];
		int miesId = 0;
		int vaimoId = 0;
		String marrDate = null;
		String marrType = null;
		Vector<UnitNotice> notices = null;
		for (int i = 0; i < personCount; i++) {
			String aux = pType[i].getText();
			notices = new Vector<UnitNotice>();
			String occu;
			String etu;
			String patro;
			String suku;
			String age;
			String text;
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
				JOptionPane.showMessageDialog(this, Resurses
						.getString("ERROR_MISSINGSEX"));
				return;
			}

			if ("mies".equals(aux) || "vaimo".equals(aux)) {

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
				if (!occu.equals("")) {
					notice = new UnitNotice("OCCU");
					notice.setDescription(occu);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				etu = pGivenname[i].getText();
				patro = pPatronym[i].getText();
				suku = pSurname[i].getText();
				if (!etu.equals("") || !patro.equals("") || !suku.equals("")) {
					notice = new UnitNotice("NAME");
					notice.setGivenname(etu);
					notice.setPatronym(patro);
					notice.setSurname(suku);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				age = pAge[i].getText();
				text = pReason[i].getText();
				// kylä / talo
				if (!"".equals(eventLastDate.getText())) {
					marrDate = toTextDate(eventLastDate.getText());
				} else if (!"".equals(eventFirstDate.getText())) {
					marrDate = toTextDate(eventFirstDate.getText());
					marrType = eventFirstType.getText();
				}

				if (!age.equals("") || !text.equals("")) {
					notice = new UnitNotice("EVEN");

					notice.setNoticeType(marrType);
					notice.setFromDate(toTextDate(marrDate));

					notice.setVillage(age);
					notice.setFarm(text);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				if (notices.size() > 0) {
					kast.persons[i].setNotices(notices
							.toArray(new UnitNotice[0]));
					if ("mies".equals(aux))
						miesId = kast.persons[i].getPid();
					else
						vaimoId = kast.persons[i].getPid();
				}
			}
		}

		// TODO MARR notiisi pävämäärineen mukaan

		Vector<Relation> relations = new Vector<Relation>();
		Relation rel;

		RelationNotice[] rNotices = new RelationNotice[1];
		if (miesId != 0) {
			rel = new Relation(0, miesId, vaimoId, "WIFE", hiskiSurety, null,
					null);
			relations.add(rel);

			rNotices[0] = new RelationNotice("MARR");
			rNotices[0].setType(marrType);
			rNotices[0].setFromDate(marrDate);
			rNotices[0].setPlace(srk.getText());
			rNotices[0].setSource(hiskiSource);
			rel.setNotices(rNotices);

		}
		kast.relations = relations.toArray(new Relation[0]);

		try {
			Suku.kontroller.getSukuData(kast, "cmd=upload");
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
		Vector<UnitNotice> notices = null;
		for (int i = 0; i < personCount; i++) {
			String aux = pType[i].getText();
			notices = new Vector<UnitNotice>();
			String occu;
			String etu;
			String patro;
			String suku;
			String age;
			String text;
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
				JOptionPane.showMessageDialog(this, Resurses
						.getString("ERROR_MISSINGSEX"));
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
				//
				// if (hiskiPid[0] > 0) isaId=hiskiPid[0];
				// if (hiskiPid[1] > 0) aitiId=hiskiPid[1];

				kast.persons[i] = new PersonLongData(vanhempi, "INDI", sex);
				kast.persons[i].setSource(hiskiSource);
				occu = pOccu[i].getText();
				if (!occu.equals("")) {
					notice = new UnitNotice("OCCU");
					notice.setDescription(occu);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				etu = pGivenname[i].getText();
				patro = pPatronym[i].getText();
				suku = pSurname[i].getText();
				if (!etu.equals("") || !patro.equals("") || !suku.equals("")) {
					notice = new UnitNotice("NAME");
					notice.setGivenname(etu);
					notice.setPatronym(patro);
					notice.setSurname(suku);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				age = pAge[i].getText();
				text = pReason[i].getText();

				if (!age.equals("") || !text.equals("")) {
					notice = new UnitNotice("SPEC");
					StringBuffer sb = new StringBuffer();
					sb.append(age);
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(text);
					notice.setNoteText(sb.toString());
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				if (notices.size() > 0) {
					kast.persons[i].setNotices(notices
							.toArray(new UnitNotice[0]));
					// if ("isa".equals(aux)) isaId = -1-i;
					// else aitiId = -1-i;
				}
			} else if ("lapsi".equals(aux)) {
				// TODO lapsen sukupuoli
				lapsiId = -1 - i;
				if (hiskiPid[2] > 0)
					lapsiId = hiskiPid[2];

				//		

				kast.persons[i] = new PersonLongData(lapsiId, "INDI", sex);
				kast.persons[i].setSource(hiskiSource);
				etu = pGivenname[i].getText();
				patro = pPatronym[i].getText();
				suku = pSurname[i].getText();
				if (!etu.equals("") || !patro.equals("") || !suku.equals("")) {
					notice = new UnitNotice("NAME");
					notice.setGivenname(etu);
					notice.setPatronym(patro);
					notice.setSurname(suku);
					notice.setSource(hiskiSource);
					notices.add(notice);
				}
				String dat = toTextDate(eventFirstDate.getText());
				if (dat != null) {
					notice = new UnitNotice("synt".equals(pvm1Name) ? "BIRT"
							: "CHR");
					notice.setFromDate(dat);
					notice.setVillage(eventFarm.getText());
					notice.setFarm(eventFarm.getText());
					notice.setSource(hiskiSource);
					notices.add(notice);

				}
				dat = toTextDate(eventLastDate.getText());
				if (dat != null) {
					notice = new UnitNotice("synt".equals(pvm2Name) ? "BIRT"
							: "CHR");
					notice.setFromDate(dat);
					notice.setVillage(eventFarm.getText());
					notice.setFarm(eventFarm.getText());
					notice.setSource(hiskiSource);
					notices.add(notice);
				}

				kast.persons[i].setNotices(notices.toArray(new UnitNotice[0]));

			}
		}

		Vector<Relation> relations = new Vector<Relation>();
		Relation rel;
		if (isaId != 0) {
			rel = new Relation(0, lapsiId, isaId, "FATH", hiskiSurety, null,
					null);
			relations.add(rel);
		}
		if (aitiId != 0) {
			rel = new Relation(0, lapsiId, aitiId, "MOTH", hiskiSurety, null,
					null);
			relations.add(rel);
		}
		kast.relations = relations.toArray(new Relation[0]);

		try {
			@SuppressWarnings("unused")
			SukuData response = Suku.kontroller.getSukuData(kast, "cmd=upload");
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
		;
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
		eventRemark.setText("");

		for (int i = 0; i < pNumero.length; i++) {

			remove(pSukuPid[i]);
			remove(pSukuName[i]);
			remove(pNumero[i]);
			remove(pType[i]);
			remove(pSex[i]);
			remove(pOccu[i]);
			remove(pGivenname[i]);
			remove(pPatronym[i]);
			remove(pSurname[i]);
			remove(pAge[i]);
			remove(pReason[i]);

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
		pAge = new JTextField[0];
		pReason = new JTextField[0];

		StringBuffer sb = new StringBuffer();
		sb.append("http://hiski.genealogia.fi/");

		String requri; // http://hiski.genealogia.fi/hiski?fi+t23806+xml
		int resu;

		// String paras[] = new String[params.length];
		// "suku?userno="+this.userno+"&person=" + pid

		SukuPopupMenu pop = SukuPopupMenu.getInstance();

		for (int i = 0; i < 3; i++) {
			pop.enableHiskiPerson(i, false);
		}

		sb.append("hiski?fi+t");

		String hiskiNumStr = this.hiskiNumber.getText();

		int hiskiNum = 0;
		Document doc = null;
		try {
			hiskiNum = Integer.parseInt(hiskiNumStr);
		} catch (NumberFormatException ne) {
			JOptionPane.showMessageDialog(this, "'" + hiskiNumStr + "'"
					+ Resurses.getString("NOT_NUMBER"), Resurses
					.getString("HISKI_NUMBER"), JOptionPane.WARNING_MESSAGE);
			return;
		}
		sb.append("" + hiskiNum);
		sb.append("+xml");

		requri = sb.toString();
		try {

			System.out.println("URI on: " + requri);
			logger.fine("URILOG: " + requri);
			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			// String encoding = uc.getContentEncoding();

			resu = uc.getResponseCode();
			System.out.println("Resu = " + resu);
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
			JOptionPane.showMessageDialog(this, e.getMessage());
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

			NodeList henkNodes = docEle.getElementsByTagName("henkilo");
			personCount = henkNodes.getLength();
			initHiskiPersons(personCount);

			for (int pidx = 0; pidx < personCount; pidx++) {

				ele = (Element) henkNodes.item(pidx);

				pNumero[pidx].setText("" + pidx);
				String theType = ele.getAttribute("tyyppi");
				pType[pidx].setText(theType);
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
				}
				NodeList nlh = ele.getChildNodes();
				Element elp;
				StringBuffer muut = new StringBuffer();
				for (int j = 0; j < nlh.getLength(); j++) {
					if (nlh.item(j).getNodeType() == Node.ELEMENT_NODE) {
						elp = (Element) nlh.item(j);

						String elpNam = elp.getNodeName();

						if (elpNam == null) {
						} else if (elpNam.equals("kyla")) {
							pAge[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("talo")) {
							pReason[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("ammatti")) {
							pOccu[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("etunimi")) {
							pGivenname[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("patronyymi")) {
							pPatronym[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("sukunimi")) {
							pSurname[pidx].setText(elp.getTextContent());
						} else if (elpNam.equals("ika")) {
							StringBuffer age = new StringBuffer();

							String x = elp.getAttribute("vv");
							if (!x.equals(""))
								age.append("vv=" + x + ";");
							x = elp.getAttribute("kk");
							if (!x.equals(""))
								age.append("kk=" + x + ";");
							x = elp.getAttribute("vk");
							if (!x.equals(""))
								age.append("vk=" + x + ";");
							x = elp.getAttribute("pv");
							if (!x.equals(""))
								age.append("pv=" + x);
							if (age.length() > 0) {
								pAge[pidx].setText("ikä=" + age.toString());
							}
						} else {
							if (muut.length() > 0) {
								muut.append(";");
							}
							muut.append(elp.getTextContent());
						}
					}
					pReason[pidx].setText(muut.toString());
				}
				pop.enableHiskiPerson(pidx, true);
			}

			NodeList taplist = tap.getChildNodes();
			int pvmno = 0;

			StringBuffer remark = new StringBuffer();
			for (int i = 0; i < taplist.getLength(); i++) {
				if (taplist.item(i).getNodeType() == Node.ELEMENT_NODE) {
					ele = (Element) taplist.item(i);
					String eleName = ele.getNodeName();
					if (eleName == null) {

					} else if (eleName.equals("srk")) {
						srkNo.setText(ele.getAttribute("nro"));
						srk.setText(ele.getTextContent());
					} else if (eleName.equals("tapahtumatunniste")) {
						eventId = ele.getAttribute("id");
					} else if (eleName.equals("pvm")) {
						pvmno++;
						if (pvmno == 1) {
							try {
								pvm1Name = ele.getAttribute("tyyppi");
								eventFirstType.setText(Resurses
										.getString(pvm1Name));
							} catch (MissingResourceException mre) {
								eventFirstType.setText(pvm1Name);
							}
							eventFirstDate.setText(ele.getTextContent());
						} else {
							try {
								pvm2Name = ele.getAttribute("tyyppi");
								eventLastType.setText(Resurses
										.getString(pvm2Name));
							} catch (MissingResourceException mre) {
								eventLastType.setText(pvm2Name);
							}
							eventLastDate.setText(ele.getTextContent());

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

					} else if (eleName.equals("ika")) {
						remark.append("ikä=");
						remark.append("vv=" + ele.getAttribute("vv") + ";");
						remark.append("kk=" + ele.getAttribute("kk") + ";");
						remark.append("vk=" + ele.getAttribute("vk") + ";");
						remark.append("pv=" + ele.getAttribute("pv"));
						eventRemark.setText(remark.toString());

					} else {

						remark.append(ele.getTextContent());
						eventRemark.setText(remark.toString());
					}
				}
			}
		}
	}

	private String toTextDate(String hiskiDate) {
		if (hiskiDate == null || hiskiDate.equals(""))
			return null;
		String[] parts = hiskiDate.split("\\.");
		StringBuffer sb = new StringBuffer();

		for (int i = parts.length - 1; i >= 0; i--) {
			if (sb.length() == 0) { // year
				sb.append(parts[i]);
				if (parts.length == 3 && parts[0].equals("")
						&& parts[1].equals(""))
					break;
			} else {
				if (i == 0 && parts[i].equals("")) {
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
