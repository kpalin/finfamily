package fi.kaila.suku.imports;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fi.kaila.suku.swing.ISuku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * 
 * <h1>Importing information from Hiski</h1>
 * 
 * <p>
 * You can import a Hiski element directly to Suku11. Check the guide how to get
 * the Hiski id for the data. This class fetches the data corresponding to the
 * given number and lets you edit it and finally insert it into the Suku11
 * database
 * </p>
 * 
 * @author Kalle
 * 
 */
public class HiskiImporter extends JFrame implements ActionListener {

	private static Logger logger = Logger.getLogger(HiskiImporter.class
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
	private JTextField[] pType;
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
	private ISuku parent;

	private DocumentBuilderFactory factory = null;
	private DocumentBuilder bld = null;

	/**
	 * The Hiski window keeps a connection to the main program that does not let
	 * you start more than one hiski-view at the time
	 * 
	 * @param parent
	 *            interface to the main window
	 */
	public HiskiImporter(ISuku parent) {
		this.parent = parent;

		initMe();

	}

	private void initMe() {

		setLayout(null);
		setLocation(200, 200);

		JLabel lbl = new JLabel(Resurses.getString(Resurses.HISKI_NUMBER));
		getContentPane().add(lbl);
		lbl.setBounds(40, 20, 190, 20);

		this.hiskiNumber = new JTextField("23806");
		getContentPane().add(this.hiskiNumber);
		this.hiskiNumber.setBounds(40, 50, 150, 20);

		this.getHiski = new JButton(Resurses.getString(Resurses.GET_HISKI));
		// this.ok.setDefaultCapable(true);
		getContentPane().add(this.getHiski);
		this.getHiski.setActionCommand(Resurses.GET_HISKI);
		this.getHiski.addActionListener(this);
		this.getHiski.setBounds(40, 80, 150, 24);

		this.close = new JButton(Resurses.getString(Resurses.CLOSE));
		// this.ok.setDefaultCapable(true);
		getContentPane().add(this.close);
		this.close.setActionCommand(Resurses.CLOSE);
		this.close.addActionListener(this);
		this.close.setBounds(40, 110, 150, 24);

		this.testDo = new JButton(Resurses.getString(Resurses.PRINT_PERSON));
		// this.ok.setDefaultCapable(true);
		getContentPane().add(this.testDo);
		this.testDo.setActionCommand(Resurses.PRINT_PERSON);
		this.testDo.addActionListener(this);
		this.testDo.setBounds(40, 140, 150, 24);

		book = new JLabel();
		getContentPane().add(book);
		book.setBounds(200, 20, 100, 20);

		srkNo = new JLabel();
		getContentPane().add(srkNo);
		srkNo.setBounds(340, 20, 40, 20);

		srk = new JTextField();
		getContentPane().add(srk);
		srk.setBounds(380, 20, 160, 20);

		eventFirstType = new JLabel();
		getContentPane().add(eventFirstType);
		eventFirstType.setBounds(200, 50, 75, 20);

		eventFirstDate = new JTextField();
		getContentPane().add(eventFirstDate);
		eventFirstDate.setBounds(285, 50, 75, 20);

		eventLastType = new JLabel();
		getContentPane().add(eventLastType);
		eventLastType.setBounds(380, 50, 75, 20);

		eventLastDate = new JTextField();
		getContentPane().add(eventLastDate);
		eventLastDate.setBounds(465, 50, 75, 20);

		eventVillage = new JTextField();
		getContentPane().add(eventVillage);
		eventVillage.setBounds(200, 80, 160, 20);

		eventFarm = new JTextField();
		getContentPane().add(eventFarm);
		eventFarm.setBounds(380, 80, 160, 20);

		eventRemark = new JTextField();
		getContentPane().add(eventRemark);
		eventRemark.setBounds(200, 110, 340, 20);

		eventFrom = new JTextField();
		getContentPane().add(eventFrom);
		eventFrom.setBounds(200, 140, 160, 20);

		eventTo = new JTextField();
		getContentPane().add(eventTo);
		eventTo.setBounds(380, 140, 160, 20);

		pNumero = new JLabel[3];
		pTypeName = new String[3];
		pType = new JTextField[3];
		pOccu = new JTextField[3];
		pGivenname = new JTextField[3];
		pPatronym = new JTextField[3];
		pSurname = new JTextField[3];
		pAge = new JTextField[3];
		pReason = new JTextField[3];

		for (int i = 0; i < pNumero.length; i++) {
			pNumero[i] = new JLabel();
			getContentPane().add(pNumero[i]);
			pNumero[i].setBounds(20, 180 + i * 50, 40, 20);

			pTypeName[i] = null;

			pType[i] = new JTextField();
			getContentPane().add(pType[i]);
			pType[i].setBounds(40, 180 + i * 50, 40, 20);

			pOccu[i] = new JTextField();
			getContentPane().add(pOccu[i]);
			pOccu[i].setBounds(90, 180 + i * 50, 100, 20);

			pGivenname[i] = new JTextField();
			getContentPane().add(pGivenname[i]);
			pGivenname[i].setBounds(200, 180 + i * 50, 160, 20);

			pPatronym[i] = new JTextField();
			getContentPane().add(pPatronym[i]);
			pPatronym[i].setBounds(380, 180 + i * 50, 160, 20);

			pSurname[i] = new JTextField();
			getContentPane().add(pSurname[i]);
			pSurname[i].setBounds(560, 180 + i * 50, 160, 20);

			pAge[i] = new JTextField();
			getContentPane().add(pAge[i]);
			pAge[i].setBounds(200, 205 + i * 50, 160, 20);

			pReason[i] = new JTextField();
			getContentPane().add(pReason[i]);
			pReason[i].setBounds(380, 205 + i * 50, 340, 20);

		}

		setVisible(true);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width / 2 - 450, d.height / 2 - 300, 900, 600);
		getRootPane().setDefaultButton(this.getHiski);

		this.factory = DocumentBuilderFactory.newInstance();
		this.factory.setValidating(false);

		try {
			this.bld = this.factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (parent != null) {
					parent.HiskiFormClosing();

				}
				e.getClass();

			}
		});

	}

	/**
	 * Sets the Hiski window at the front of the application
	 */
	public void wakeMeUp() {
		toFront();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		if (cmd == null)
			return;

		if (cmd.equals(Resurses.CLOSE)) {
			if (parent != null) {
				parent.HiskiFormClosing();
			}
			this.setVisible(false);
		} else if (cmd.equals(Resurses.GET_HISKI)) {

			fetchFromHiski();

		} else if (cmd.equals(Resurses.PRINT_PERSON)) {
			if ("kastetut".equals(bookName)) {
				uploadKastetutToDb();
			}
		}

	}

	private void uploadKastetutToDb() {
		SukuData kast = new SukuData();
		String hiskiSource = "Hiski [" + eventId + "]";
		kast.persons = new PersonLongData[personCount];
		int isaId = 0;
		int aitiId = 0;
		int lapsiId = 0;
		Vector<UnitNotice> notices = new Vector<UnitNotice>();
		for (int i = 0; i < personCount; i++) {
			String aux = pType[i].getText();
			String occu;
			String etu;
			String patro;
			String suku;
			String age;
			String text;
			UnitNotice notice;
			if ("isa".equals(aux) || "aiti".equals(aux)) {

				kast.persons[i] = new PersonLongData(-i, "INDI", "isa"
						.equals(aux) ? "M" : "F");
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
					if ("isa".equals(aux))
						isaId = -i;
					else
						aitiId = -i;
				}
			} else if ("lapsi".equals(aux)) {
				// TODO lapsen sukupuoli
				kast.persons[i] = new PersonLongData(-i, "INDI", "U");
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
				lapsiId = -i;

			}
			Vector<Relation> relations = new Vector<Relation>();
			Relation rel;
			if (isaId > 0) {
				rel = new Relation(0, lapsiId, isaId, "FATH", hiskiSurety,
						null, null);
				relations.add(rel);
			}
			if (aitiId > 0) {
				rel = new Relation(0, lapsiId, aitiId, "MOTH", hiskiSurety,
						null, null);
				relations.add(rel);
			}
			kast.relations = relations.toArray(new Relation[0]);

			// TODO send data to database now

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
			pNumero[i].setText("");
			pTypeName[i] = null;
			pType[i].setText("");
			pOccu[i].setText("");
			pGivenname[i].setText("");
			pPatronym[i].setText("");
			pSurname[i].setText("");
			pAge[i].setText("");
			pReason[i].setText("");
		}

		StringBuffer sb = new StringBuffer();
		sb.append("http://hiski.genealogia.fi/");

		String requri; // http://hiski.genealogia.fi/hiski?fi+t23806+xml
		int resu;

		// String paras[] = new String[params.length];
		// "suku?userno="+this.userno+"&person=" + pid

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
			e.printStackTrace();
		}
		// FIXME: Potential NPE
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
						if (personCount < pNumero.length) {
							pNumero[personCount].setText("" + personCount);
							NodeList nlh = ele.getChildNodes();
							Element elp;
							StringBuffer muut = new StringBuffer();
							for (int j = 0; j < nlh.getLength(); j++) {
								if (nlh.item(j).getNodeType() == Node.ELEMENT_NODE) {
									elp = (Element) nlh.item(j);

									String elpNam = elp.getNodeName();
									if (elpNam == null) {
									} else if (elpNam.equals("kyla")) {
										pAge[personCount].setText(elp
												.getTextContent());
									} else if (elpNam.equals("talo")) {
										pReason[personCount].setText(elp
												.getTextContent());
									} else if (elpNam.equals("ammatti")) {
										pOccu[personCount].setText(elp
												.getTextContent());
									} else if (elpNam.equals("etunimi")) {
										pGivenname[personCount].setText(elp
												.getTextContent());
									} else if (elpNam.equals("patronyymi")) {
										pPatronym[personCount].setText(elp
												.getTextContent());
									} else if (elpNam.equals("sukunimi")) {
										pSurname[personCount].setText(elp
												.getTextContent());
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
											pAge[personCount].setText("ikä="
													+ age.toString());
										}
									} else {
										if (muut.length() > 0) {
											muut.append(";");
										}
										muut.append(elp.getTextContent());
									}
								}
								pReason[personCount].setText(muut.toString());
							}

						}
						personCount++;
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
					sb.append("00" + parts[i].substring(parts[i].length()));
				}
			}
		}
		return sb.toString();

	}

}
