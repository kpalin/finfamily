package fi.kaila.suku.swing.panel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.dialog.AddNotice;
import fi.kaila.suku.swing.dialog.LanguageDialog;
import fi.kaila.suku.swing.util.SukuDateField;
import fi.kaila.suku.swing.util.SukuSuretyField;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateException;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTextArea;
import fi.kaila.suku.util.SukuTextField;
import fi.kaila.suku.util.SukuTextField.Field;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * pane for the single notice
 * 
 * @author Kalle
 * 
 */
public class NoticePane extends JPanel implements ActionListener,
		ComponentListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private Logger logger;

	JButton moveToLeft;
	JButton moveToRight;
	JButton toDelete;
	JButton toAdd;
	JButton close;
	JButton update;
	// JLabel toBeDeleted;
	SukuSuretyField surety;
	JComboBox privacy;

	JLabel suretyLbl;
	JLabel pricavyLbl;
	JLabel createdLbl;
	JLabel modifiedLbl;
	JTextField created;
	JTextField modified;

	JLabel typeLbl;
	JLabel descLbl;

	SukuTextField noticeType;
	SukuTextField description;

	JLabel dateLbl;
	JLabel placeLbl;
	SukuDateField date;
	SukuTextField place;

	JLabel villageLbl;
	JLabel farmLbl;
	JLabel croftLbl;

	JTextField village;
	JTextField farm;
	JTextField croft;

	JLabel addressLbl;
	JLabel postalCodeLbl;
	JLabel postOfficeLbl;
	JLabel stateLbl;
	JLabel countryLbl;
	JLabel emailLbl;

	JTextArea address;
	JTextField postalCode;
	SukuTextField postOffice;
	JTextField state;
	JTextField country;
	JTextField email;
	JScrollPane scrollAddress;
	JScrollPane scrollNote;

	JLabel noteLbl;
	JLabel mediaFilenameLbl;
	JLabel mediaTitleLbl;

	JTextArea noteText;
	JTextField mediaFilename;
	JTextField mediaTitle;
	JButton mediaOpenFile;
	MyImage image;

	JLabel givLbl;
	JLabel patLbl;
	JLabel preLbl;
	JLabel surLbl;
	JLabel postLbl;

	SukuTextField givenname;
	SukuTextField patronym;
	JTextField prefix;
	SukuTextField surname;
	JTextField postfix;
	JLabel sourceLbl;
	JLabel privateLbl;
	JScrollPane scrollSource;
	JScrollPane scrollPrivate;
	SukuTextArea source;
	JTextArea privateText;

	JList nameList;
	JScrollPane scrollNames;
	JLabel nameLabel;
	Vector<String> namesVector = null;
	JTextField listaName;
	int listaSelectedName = -1;
	int listaSelectedPlace = -1;
	JButton listaAddname;
	JLabel addLabel;
	JList placeList;
	JScrollPane scrollPlaces;
	JLabel placeLabel;
	Vector<String> placesVector = null;
	JTextField listaPlace;
	JButton listaAddplace;
	JButton noteTextLang = null;

	Rectangle noteLoc = null;

	PersonView personView = null;

	int pid;
	UnitNotice notice = null;

	/**
	 * @param peronView
	 * @param pid
	 * @param notice
	 */
	public NoticePane(PersonView peronView, int pid, UnitNotice notice) {
		this.personView = peronView;
		this.pid = pid;
		this.notice = notice;
		logger = Logger.getLogger(this.getClass().getName());

		initMe();

		updateMe();
	}

	private void initMe() {

		setLayout(null);

		// JLabel lbl;
		// JTextField t;

		// int rrivi=10;
		addComponentListener(this);

		moveToLeft = new JButton("<==");
		moveToLeft.addActionListener(this);
		moveToLeft.setActionCommand("<");
		add(moveToLeft);

		moveToRight = new JButton("==>");
		moveToRight.addActionListener(this);
		moveToRight.setActionCommand(">");
		add(moveToRight);

		toAdd = new JButton(Resurses.getString("DATA_ADD"));
		toAdd.addActionListener(this);
		toAdd.setActionCommand("ADD");
		add(toAdd);

		toDelete = new JButton(Resurses.getString("DATA_DELETE"));
		toDelete.addActionListener(this);
		toDelete.setActionCommand("DELETE");
		add(toDelete);

		close = new JButton(Resurses.getString(Resurses.CLOSE));
		add(this.close);
		close.setActionCommand(Resurses.CLOSE);
		close.addActionListener(this);

		update = new JButton(Resurses.getString(Resurses.UPDATE));

		add(this.update);
		update.setActionCommand(Resurses.UPDATE);
		update.addActionListener(this);

		suretyLbl = new JLabel(Resurses.getString("DATA_SURETY"));

		add(suretyLbl);

		surety = new SukuSuretyField();

		add(surety);

		createdLbl = new JLabel(Resurses.getString("DATA_CREATED"));

		add(createdLbl);

		if (notice.getCreated() == null) {
			created = new JTextField();
		} else {
			created = new JTextField(notice.getCreated().toString());
		}

		created.setEditable(false);
		add(created);

		modifiedLbl = new JLabel(Resurses.getString("DATA_MODIFIED"));

		add(modifiedLbl);

		modified = new JTextField();

		modified.setEditable(false);
		add(modified);

		if (notice.getModified() != null) {
			modified.setText(notice.getModified().toString());
		}
		String[] privacies = Resurses.getString("DATA_PRIVACY_LEVEL")
				.split(";");
		privacy = new JComboBox(privacies);
		// privacy = new JCheckBox(Resurses.getString("DATA_PRIVACY"));

		add(privacy);
		int privacyIdx = 0;
		if (notice.getPrivacy() == null) {
			privacyIdx = 0;
			// privacy.setSelected(true);
		} else if (notice.getPrivacy().equals(Resurses.PRIVACY_TEXT)) {
			privacyIdx = 1;
		} else if (notice.getPrivacy().equals(Resurses.PRIVACY_INDEX)) {
			privacyIdx = 2;
		} else if (notice.getPrivacy().equals(Resurses.PRIVACY_PRIVACY)) {
			privacyIdx = 3;
		}
		privacy.setSelectedIndex(privacyIdx);
		image = new MyImage();

		add(image);

		//
		// left column creates first all fields
		// then positions them according to tag
		//

		date = new SukuDateField();
		add(date);
		place = new SukuTextField(null, Field.Fld_Place);
		add(place);

		village = new JTextField();
		add(village);

		// lbl = new JLabel(Resurses.getString("DATA_FARM"));
		// add(lbl);
		//
		farm = new JTextField();
		add(farm);
		// lbl = new JLabel(Resurses.getString("DATA_CROFT"));
		// add(lbl);

		croft = new JTextField();
		add(croft);

		// lbl = new JLabel(Resurses.getString("DATA_ADDRESS"));
		// add(lbl);

		address = new JTextArea();
		address.setLineWrap(true);
		scrollAddress = new JScrollPane(address,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollAddress);

		postalCode = new JTextField();
		add(postalCode);
		postOffice = new SukuTextField(null, Field.Fld_Place);
		add(postOffice);

		country = new JTextField();
		add(country);
		state = new JTextField();
		add(state);

		email = new JTextField();
		add(email);

		noteText = new JTextArea();
		noteText.setLineWrap(true);
		noteText.setWrapStyleWord(true);
		scrollNote = new JScrollPane(noteText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollNote);

		// noteTextLang = makeLangButton("NOTE");
		noteTextLang = new JButton(Resurses.getString("DATA_LANGUAGE"));
		add(noteTextLang);
		noteTextLang.addActionListener(this);
		noteTextLang.setActionCommand("NOTE_LANG");

		mediaFilename = new JTextField();
		mediaFilename.setEditable(false);
		add(mediaFilename);

		mediaOpenFile = new JButton(Resurses.getString("DATA_IMAGE_OPEN"));
		mediaOpenFile.setActionCommand("IMAGE_OPEN");
		mediaOpenFile.addActionListener(this);
		add(mediaOpenFile);

		mediaTitle = new JTextField();
		add(mediaTitle);

		givenname = new SukuTextField(null, Field.Fld_Givenname);
		add(givenname);

		patronym = new SukuTextField(null, Field.Fld_Patronyme);
		add(patronym);

		prefix = new JTextField();
		add(prefix);

		surname = new SukuTextField(null, Field.Fld_Surname);
		add(surname);

		postfix = new JTextField();
		add(postfix);

		source = new SukuTextArea();
		source.setLineWrap(true);
		scrollSource = new JScrollPane(source,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollSource);

		privateText = new JTextArea();
		privateText.setLineWrap(true);
		scrollPrivate = new JScrollPane(privateText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollPrivate);

		typeLbl = new JLabel(Resurses.getString("DATA_TYPE"));

		add(typeLbl);

		// newTextLang.setBounds(500,lrivi,140,20);
		noticeType = new SukuTextField(null, Field.Fld_Type);
		add(noticeType);

		descLbl = new JLabel(Resurses.getString("DATA_DESCRIPTION"));
		add(descLbl);

		description = new SukuTextField(notice.getTag(), Field.Fld_Description);
		add(description);

		dateLbl = new JLabel(Resurses.getString("DATA_DATE"));
		add(dateLbl);

		placeLbl = new JLabel(Resurses.getString("DATA_PLACE"));
		add(placeLbl);

		villageLbl = new JLabel(Resurses.getString("DATA_VILLAGE"));
		add(villageLbl);

		farmLbl = new JLabel(Resurses.getString("DATA_FARM"));
		add(farmLbl);

		croftLbl = new JLabel(Resurses.getString("DATA_CROFT"));
		add(croftLbl);

		addressLbl = new JLabel(Resurses.getString("DATA_ADDRESS"));
		add(addressLbl);

		postalCodeLbl = new JLabel(Resurses.getString("DATA_POSTCODE"));

		add(postalCodeLbl);

		postOfficeLbl = new JLabel(Resurses.getString("DATA_POSTOFF"));
		add(postOfficeLbl);

		stateLbl = new JLabel(Resurses.getString("DATA_STATE"));
		add(stateLbl);
		countryLbl = new JLabel(Resurses.getString("DATA_COUNTRY"));
		add(countryLbl);

		emailLbl = new JLabel(Resurses.getString("DATA_EMAIL"));
		add(emailLbl);

		noteLbl = new JLabel(Resurses.getString("DATA_NOTE"));
		add(noteLbl);

		mediaFilenameLbl = new JLabel(Resurses.getString("DATA_MEDIA_FILENAME"));
		add(mediaFilenameLbl);

		mediaTitleLbl = new JLabel(Resurses.getString("DATA_MEDIA_TITLE"));
		add(mediaTitleLbl);

		givLbl = new JLabel(Resurses.getString("DATA_GIVENNAME"));
		add(givLbl);

		patLbl = new JLabel(Resurses.getString("DATA_PATRONYM"));
		add(patLbl);

		preLbl = new JLabel(Resurses.getString("DATA_PREFIX"));
		add(preLbl);

		surLbl = new JLabel(Resurses.getString("DATA_SURNAME"));
		add(surLbl);

		postLbl = new JLabel(Resurses.getString("DATA_POSTFIX"));
		add(postLbl);

		sourceLbl = new JLabel(Resurses.getString("DATA_SOURCE"));

		add(sourceLbl);
		privateLbl = new JLabel(Resurses.getString("DATA_PRIVATETEXT"));

		add(privateLbl);

		addLabel = new JLabel(Resurses.getString("DATA_ADDLIST"));
		add(addLabel);

		nameLabel = new JLabel(Resurses.getString("DATA_NAMES"));
		add(nameLabel);

		namesVector = new Vector<String>();

		nameList = new JList(namesVector);

		nameList.addListSelectionListener(this);

		scrollNames = new JScrollPane(nameList);
		add(scrollNames);
		listaAddname = new JButton(Resurses.getString("DATA_ADDNAME"));
		listaAddname.setActionCommand("DATA_ADDNAME");
		listaAddname.addActionListener(this);
		add(listaAddname);
		listaName = new JTextField();
		add(listaName);

		placeLabel = new JLabel(Resurses.getString("DATA_PLACES"));

		add(placeLabel);

		placesVector = new Vector<String>();

		placeList = new JList(placesVector);
		placeList.addListSelectionListener(this);

		scrollPlaces = new JScrollPane(placeList);
		add(scrollPlaces);
		listaAddplace = new JButton(Resurses.getString("DATA_ADDPLACE"));
		listaAddplace.setActionCommand("DATA_ADDPLACE");
		listaAddplace.addActionListener(this);
		add(listaAddplace);
		listaPlace = new JTextField();
		add(listaPlace);

		// setPreferredSize(new Dimension(rcol+rwidth*2, lrivi));

	}

	private void updateMe() {

		surety.setSurety(notice.getSurety());

		String tmp = notice.getNoticeType();
		if (tmp == null)
			tmp = "";
		noticeType.setText(tmp);

		tmp = notice.getDescription();
		if (tmp == null)
			tmp = "";
		description.setText(tmp);

		// setDate();
		date.setDate(notice.getDatePrefix(), notice.getFromDate(), notice
				.getToDate());
		tmp = notice.getPlace();
		if (tmp == null)
			tmp = "";
		place.setText(tmp);

		tmp = notice.getVillage();
		if (tmp == null)
			tmp = "";
		village.setText(tmp);

		tmp = notice.getFarm();
		if (tmp == null)
			tmp = "";
		farm.setText(tmp);

		tmp = notice.getCroft();
		if (tmp == null)
			tmp = "";
		croft.setText(tmp);

		tmp = notice.getAddress();
		if (tmp == null)
			tmp = "";
		address.setText(tmp);

		tmp = notice.getPostalCode();
		if (tmp == null)
			tmp = "";
		postalCode.setText(tmp);
		tmp = notice.getPostOffice();
		if (tmp == null)
			tmp = "";
		postOffice.setText(tmp);
		tmp = notice.getState();
		if (tmp == null)
			tmp = "";
		state.setText(tmp);
		tmp = notice.getCountry();
		if (tmp == null)
			tmp = "";
		country.setText(tmp);
		tmp = notice.getEmail();
		if (tmp == null)
			tmp = "";
		email.setText(tmp);

		tmp = notice.getNoteText();
		if (tmp == null)
			tmp = "";
		noteText.setText(tmp);

		if (notice.getLanguages() == null) {

			noteTextLang.setText(Resurses.getString("DATA_ADD_LANGUAGE"));
		}

		tmp = notice.getMediaFilename();
		if (tmp == null)
			tmp = "";
		mediaFilename.setText(tmp);

		tmp = notice.getMediaTitle();
		if (tmp == null)
			tmp = "";
		mediaTitle.setText(tmp);

		getImage();

		tmp = notice.getGivenname();
		if (tmp == null)
			tmp = "";
		givenname.setText(tmp);

		tmp = notice.getPatronym();
		if (tmp == null)
			tmp = "";
		patronym.setText(tmp);

		tmp = notice.getPrefix();
		if (tmp == null)
			tmp = "";
		prefix.setText(tmp);
		tmp = notice.getSurname();
		if (tmp == null)
			tmp = "";
		surname.setText(tmp);
		tmp = notice.getPostfix();
		if (tmp == null)
			tmp = "";
		postfix.setText(tmp);

		tmp = notice.getSource();
		if (tmp == null)
			tmp = "";
		source.setText(tmp);
		tmp = notice.getPrivateText();
		if (tmp == null)
			tmp = "";
		privateText.setText(tmp);

		String[] arra = notice.getRefNames();
		if (arra != null) {
			for (int i = 0; i < arra.length; i++) {
				String[] vahasuku = arra[i].split("/");
				String[] etusuku = arra[i].split(",");
				if (vahasuku.length > 1) {
					etusuku = new String[2];
					etusuku[0] = vahasuku[1];
					etusuku[1] = vahasuku[0];
				}
				StringBuilder sb = new StringBuilder();
				if (etusuku.length > 1) {
					sb.append(etusuku[0]);
				}
				sb.append(",");
				sb.append(etusuku[1]);
				namesVector.add(sb.toString());
			}
		}

		arra = notice.getRefPlaces();
		if (arra != null) {
			for (int i = 0; i < arra.length; i++) {
				placesVector.add(arra[i]);
			}
		}

	}

	/**
	 * This checks if the fields that are not visible on the MainPerson are
	 * empty
	 * 
	 * @return true if all non visible fields are empty
	 */
	boolean isPlain() {

		if (surety.getSurety() != 100)
			return false;
		if (privacy.getSelectedIndex() > 0)
			return false;
		if (noticeType.getText().length() > 0)
			return false;
		if (description.getText().length() > 0) {
			if (!notice.getTag().equals("OCCU"))
				return false;
		}
		if (!date.isPlain())
			return false;
		// if (datePref.getSelectedIndex() != 0) return false;

		// if (dateTo.getText().length() > 0) return false;
		if (notice.getTag().equals("OCCU") || notice.getTag().equals("NOTE")) {
			try {
				String tmp = date.getFromDate();
				if (nv(tmp).length() > 0)
					return false;
			} catch (SukuDateException e) {
				return false;
			}
		}

		if (village.getText().length() > 0)
			return false;
		if (farm.getText().length() > 0)
			return false;
		if (croft.getText().length() > 0)
			return false;
		if (address.getText().length() > 0)
			return false;
		if (postalCode.getText().length() > 0)
			return false;
		if (postOffice.getText().length() > 0)
			return false;
		if (state.getText().length() > 0)
			return false;
		if (country.getText().length() > 0)
			return false;
		if (email.getText().length() > 0)
			return false;
		if (!notice.getTag().equals("NOTE")) {
			if (noteText.getText().length() > 0)
				return false;
		}
		if (mediaFilename.getText().length() > 0)
			return false;
		if (mediaTitle.getText().length() > 0)
			return false;
		if (source.getText().length() > 0)
			return false;
		if (privateText.getText().length() > 0)
			return false;
		if (image.img != null)
			return false;

		return true;
	}

	private Dimension imageSize = null;

	private void getImage() {

		BufferedImage img = notice.getMediaImage();
		if (img != null) {
			double imh = img.getHeight();
			double imw = img.getWidth();
			imageSize = new Dimension((int) imw, (int) imh);
			double neww = getRColWidth() * 2;
			double newh = imh / imw * neww;

			Image imgs = img.getScaledInstance((int) neww, (int) newh,
					Image.SCALE_DEFAULT);

			image.img = imgs;
		} else {
			image.img = null;
		}
	}

	void setToBeDeleted(boolean value) {

		if (value) {

			toDelete.setText(Resurses.getString("DATA_DELETED"));
			if (notice.getTag().equals("NOTE")) {
				noteText.setText("");
			}
		} else {
			toDelete.setText(Resurses.getString("DATA_DELETE"));
		}
		toDelete.setEnabled(!value);
		notice.setToBeDeleted(value);
	}

	enum TagType {
		STANDARD, NAME, NOTE, RESI, PHOT
	}

	// String getDatePrefTag(){
	//
	// int idx = datePref.getSelectedIndex();
	// switch (idx) {
	// case 1: return "ABT";
	// case 2: return "CAL";
	// case 3: return "EST";
	// case 4: return "BET";
	// case 5: return "FROM";
	// case 6: return "BEF";
	// case 7: return "AFT";
	// default: return null;
	// }
	// }

	String getUnitNoticeError() {
		String theDate = null;
		try {
			// This checks both dates for error. in case of error
			// an SukuDateException is thrown
			// if ok returns null
			// FIX-ME: Dead store to theDate.
			// Is there better way to code this?
			// removed the second store as that is obviously never used
			// first one is return from catch block if second date has an error
			theDate = date.getFromDate();
			date.getToDate();
			return null;
		} catch (SukuDateException e) {
			// e.printStackTrace();
			return e.getMessage();

		}

	}

	void verifyUnitNotice() throws SukuDateException {

		date.getFromDate();
		date.getToDate();
	}

	void copyToUnitNotice() throws SukuDateException {
		int sureIdx = surety.getSurety();

		String fromDate = date.getFromDate();
		String toDate = date.getToDate();

		notice.setSurety(sureIdx);
		String privacyCode = null;
		switch (privacy.getSelectedIndex()) {
		case 1:
			privacyCode = Resurses.PRIVACY_TEXT;
			break;
		case 2:
			privacyCode = Resurses.PRIVACY_INDEX;
			break;
		case 3:
			privacyCode = Resurses.PRIVACY_PRIVACY;
			break;
		}

		notice.setPrivacy(privacyCode);

		notice.setNoticeType(noticeType.getText());

		notice.setDescription(description.getText());

		String datePre = date.getDatePrefTag();
		// if (datePre != null || notice.getDatePrefix() != null) {

		notice.setDatePrefix(datePre);
		notice.setFromDate(fromDate);
		notice.setToDate(toDate);
		notice.setPlace(place.getText());
		notice.setVillage(village.getText());
		notice.setFarm(farm.getText());
		notice.setCroft(croft.getText());
		notice.setAddress(address.getText());
		notice.setPostalCode(postalCode.getText());
		notice.setPostOffice(postOffice.getText());
		notice.setState(state.getText());
		notice.setCountry(country.getText());
		notice.setEmail(email.getText());
		notice.setNoteText(noteText.getText());
		notice.setMediaFilename(mediaFilename.getText());
		notice.setMediaTitle(mediaTitle.getText());
		notice.setGivenname(givenname.getText());
		notice.setPatronym(patronym.getText());
		notice.setPrefix(prefix.getText());
		notice.setSurname(surname.getText());
		notice.setPostfix(postfix.getText());
		notice.setSource(source.getText());
		notice.setPrivateText(privateText.getText());

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		// System.out.println("Notice closataan " + cmd);
		if (cmd.equals("NOTE_LANG")) {

			LanguageDialog lan = new LanguageDialog(personView.getSuku());

			Rectangle r = personView.getSuku().getDbWindow();

			lan.setBounds(r);

			lan.setLanguages(notice.getTag(), notice.getLanguages());

			lan.setVisible(true);
			lan.langTextToPojo(null);

			notice.setLanguages(lan.getLanguages());

		} else if (cmd.equals("IMAGE_OPEN")) {
			boolean openedImage = Suku.kontroller.openLocalFile("jpg;png;gif");
			if (openedImage) {
				long filesize = Suku.kontroller.getFileLength();
				if (filesize > 0) {
					BufferedInputStream bstr = new BufferedInputStream(
							Suku.kontroller.getInputStream());
					// System.out.println("OPEN: " + openedImage);

					byte buffer[] = new byte[(int) filesize];

					try {
						int luettu = bstr.read(buffer);
						if (luettu == filesize) {
							notice.setMediaData(buffer);
						} else {
							logger.warning("Filesize expected " + filesize
									+ " read " + luettu);
						}
						bstr.close();
						notice.setMediaFilename(Suku.kontroller.getFileName());
						getImage();
						notice.setMediaSize(imageSize);

						mediaFilename.setText(notice.getMediaFilename());

						updateUI();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(this, e1.getMessage(),
								Resurses.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);

						e1.printStackTrace();
					}
				}
			}
		} else if (cmd.equals("DELETE")) {
			setToBeDeleted(true);
		} else if (cmd.equals("ADD")) {
			AddNotice an;
			try {
				an = new AddNotice(personView.getSuku());
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

				an.setBounds(d.width / 2 - 300, d.height / 2 - 200, 300, 400);

				Rectangle r = toAdd.getBounds();
				Point pt = new Point(r.x, r.y);
				SwingUtilities.convertPointToScreen(pt, this);
				r.x = pt.x - 40;
				r.y = pt.y + r.height;
				r.height = 400;
				r.width = 120;
				an.setBounds(r);
				an.setVisible(true);

				if (an.getSelectedTag() != null) {
					// System.out.println("Valittiin " + an.getSelectedTag());
					personView.addNotice(-1, an.getSelectedTag());
				}

			} catch (SukuException e1) {
				logger.log(Level.WARNING, "Add new dialog error", e1);
				JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

			}

		} else if (cmd.equals("<")) {
			personView.moveSelectedPane(-1);
		} else if (cmd.equals(">")) {
			personView.moveSelectedPane(+1);
		}
		if (cmd.equals("DATA_ADDNAME")) {

			String gv = listaName.getText();
			int idt = gv.indexOf(",");
			if (idt < 0) {
				int ids = gv.lastIndexOf(" ");
				if (ids > 1) {
					gv = gv.substring(ids + 1).trim() + ","
							+ gv.substring(0, ids).trim();
				}
			} else {
				gv = gv.substring(0, idt).trim() + ","
						+ gv.substring(idt + 1).trim();
			}

			if (gv.isEmpty()) {
				if (listaSelectedName >= 0) {
					namesVector.removeElementAt(listaSelectedName);
				}
			} else {
				if (listaSelectedName >= 0) {
					namesVector.removeElementAt(listaSelectedName);
					namesVector.insertElementAt(gv, listaSelectedName);
				} else {
					namesVector.add(gv);
				}
			}
			listaSelectedName = -1;
			nameList.clearSelection();
			nameList.updateUI();
			listaName.setText("");

			notice.setRefNames(namesVector.toArray(new String[0]));

		}
		if (cmd.equals("DATA_ADDPLACE")) {

			String pl = listaPlace.getText();

			if (pl.isEmpty()) {
				if (listaSelectedPlace >= 0) {
					placesVector.removeElementAt(listaSelectedPlace);
				}
			} else {

				if (listaSelectedPlace >= 0) {
					placesVector.removeElementAt(listaSelectedPlace);
					placesVector.insertElementAt(pl, listaSelectedPlace);
				} else {
					placesVector.add(pl);
				}

			}
			listaSelectedPlace = -1;
			placeList.clearSelection();
			placeList.updateUI();
			listaPlace.setText("");
			notice.setRefPlaces(placesVector.toArray(new String[0]));

		}
		if (cmd.equals(Resurses.UPDATE)) {

			int midx = personView.getMainPaneIndex();
			if (midx < 0)
				return;
			SukuTabPane pan = personView.getPane(midx);
			PersonMainPane main = (PersonMainPane) pan.pnl;
			int personPid = main.getPersonPid();

			try {
				verifyUnitNotice();
			} catch (SukuDateException se) {
				JOptionPane.showMessageDialog(this, se.getMessage(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				personView.closePersonPane(false);
				personView.displayPersonPane(personPid);
				personView.closeMainPane(true);

			} catch (SukuException e1) {
				JOptionPane.showMessageDialog(this, e1.toString(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				logger.log(Level.WARNING, "Closing notice", e1);

				// e1.printStackTrace();
			}
		} else if (cmd.equals(Resurses.CLOSE)) {

			int midx = personView.getMainPaneIndex();
			if (midx < 0)
				return;
			SukuTabPane pan = personView.getPane(midx);
			PersonMainPane main = (PersonMainPane) pan.pnl;
			int personPid = main.getPersonPid();

			try {
				verifyUnitNotice();
			} catch (SukuDateException se) {
				JOptionPane.showMessageDialog(this, se.getMessage(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				personView.closePersonPane(true);
				personView.closeMainPane(false);

			} catch (SukuException e1) {
				JOptionPane.showMessageDialog(this, e1.toString(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				logger.log(Level.WARNING, "Closing notice", e1);

				// e1.printStackTrace();
			}

			// } catch (SukuDateException e1) {
			// JOptionPane.showMessageDialog(this, e1.getMessage(),
			// Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			//
			// e1.printStackTrace();
			// } catch (Exception e1) {
			// JOptionPane.showMessageDialog(this, e1.toString(),
			// Resurses.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			// logger.log(Level.WARNING,"Closing person",e1);
			// e1.printStackTrace();
			// personView.closeMainPane(0);
			// }
		}
	}

	class MyImage extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Image img = null;

		@Override
		public void paintComponent(Graphics g) {
			if (img != null) {
				g.drawImage(img, 0, 0, null);
			} else {
				super.paintComponent(g);
			}
		}
	}

	String nv(String text) {
		if (text == null)
			return "";
		else
			return text;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {

	}

	private int lcol = 75;
	private int lwidth = 300;
	// private int rwidth = 70;
	private int rcol = lwidth + lcol + 5;
	private int lbuttoncol = 240;

	@Override
	public void componentResized(ComponentEvent arg0) {
		resizeNoticePane();
	}

	private int getRColWidth() {

		Dimension currSize = getSize();

		if (currSize.width > 525) {
			return currSize.width / 8;
		}
		return 70;
	}

	/**
	 * resize the pane and check what is to be shown
	 */
	public void resizeNoticePane() {
		Dimension currSize = getSize();
		int rwidth = 70;
		if (currSize.width > 525) {
			rwidth = currSize.width / 8;
			lwidth = currSize.width - lcol - 10 - rwidth * 2;

			// setPreferredSize(new Dimension(rcol+rwidth*2, lrivi));
		} else {
			rwidth = 70;
			lwidth = 525 - lcol - 10 - rwidth * 2;
		}

		getImage();
		rcol = lwidth + lcol + 5;
		int listY1 = 10;
		int listY2 = 34;
		TagType showType;

		if (notice.getTag().equals("NOTE")) {
			showType = TagType.NOTE;
		} else if (notice.getTag().equals("RESI")) {
			showType = TagType.RESI;
		} else if (notice.getTag().equals("NAME")) {
			showType = TagType.NAME;
		} else if (notice.getTag().startsWith("PHOT")) {
			showType = TagType.PHOT;
		} else {
			showType = TagType.STANDARD;
		}

		boolean mustNote = personView.getSuku().isShowNote();
		if (notice.getNoteText() != null) {
			mustNote = true;
		}

		boolean mustAddress = personView.getSuku().isShowAddress();
		if (notice.getAddress() != null || notice.getPostalCode() != null
				|| notice.getPostOffice() != null
				|| notice.getCountry() != null || notice.getEmail() != null) {
			mustAddress = true;
		}
		boolean mustFarm = personView.getSuku().isShowFarm();
		if (notice.getVillage() != null || notice.getFarm() != null
				|| notice.getCroft() != null) {
			mustFarm = true;
		}

		boolean mustPrivate = personView.getSuku().isShowPrivate();
		if (notice.getPrivateText() != null) {
			mustPrivate = true;
		}

		boolean mustImage = personView.getSuku().isShowImage();
		if (notice.getMediaFilename() != null || notice.getMediaImage() != null
				|| notice.getMediaTitle() != null) {
			mustImage = true;
		}
		int rrNameList = 0;
		int rrivi = 10;
		moveToLeft.setBounds(rcol, rrivi, rwidth, 20);
		moveToRight.setBounds(rcol + rwidth, rrivi, rwidth, 20);
		rrivi += 24;
		toAdd.setBounds(rcol, rrivi, rwidth, 20);
		toDelete.setBounds(rcol + rwidth, rrivi, rwidth, 20);
		rrivi += 24;
		close.setBounds(rcol, rrivi, rwidth, 24);
		update.setBounds(rcol + rwidth, rrivi, rwidth, 24);
		rrivi += 24;
		noteTextLang.setBounds(rcol, rrivi, rwidth * 2, 20);
		rrivi += 24;
		suretyLbl.setBounds(rcol, rrivi, 100, 20);
		rrivi += 20;
		surety.setBounds(rcol, rrivi, 100, 20);

		rrivi += 24;
		createdLbl.setBounds(rcol, rrivi, 100, 20);
		rrivi += 20;
		created.setBounds(rcol, rrivi, rwidth * 2, 20);
		rrivi += 24;
		modifiedLbl.setBounds(rcol, rrivi, 100, 20);
		rrivi += 20;
		modified.setBounds(rcol, rrivi, rwidth * 2, 20);
		rrivi += 24;
		privacy.setBounds(rcol, rrivi, rwidth * 2, 20);
		rrivi += 24;
		rrNameList = rrivi;
		image.setBounds(rcol, rrivi, rwidth * 2, 400);

		int lrivi = 10;
		typeLbl.setBounds(10, lrivi, 70, 20);

		noticeType.setBounds(lcol, lrivi, 150, 20);
		// noteTextLang.setBounds(lcol + lwidth - 150, lrivi, 150, 20);
		lrivi += 24;
		descLbl.setBounds(10, lrivi, 70, 20);
		description.setBounds(lcol, lrivi, lwidth, 20);

		lrivi += 24;

		switch (showType) {
		case NOTE:
			description.setVisible(false);
			descLbl.setVisible(false);
			noticeType.setVisible(false);
			typeLbl.setVisible(false);
			// FIXME: This method contains a switch statement where one case
			// branch will fall through to the next case. Usually you need to
			// end this case with a break or return.
		case NAME:

			// place.setVisible(false);
			village.setVisible(false);
			farm.setVisible(false);
			croft.setVisible(false);
			// placeLbl.setVisible(false);
			villageLbl.setVisible(false);
			farmLbl.setVisible(false);
			croftLbl.setVisible(false);
			break;
		case RESI:
			// place.setVisible(false);
			village.setVisible(false);
			farm.setVisible(false);
			croft.setVisible(false);
			// placeLbl.setVisible(false);
			villageLbl.setVisible(false);
			farmLbl.setVisible(false);
			croftLbl.setVisible(false);
			dateLbl.setBounds(10, lrivi, 70, 20);
			date.setBounds(lcol, lrivi, 283, 20);
			lrivi += 24;
			placeLbl.setBounds(10, lrivi, 70, 20);
			place.setBounds(lcol, lrivi, lwidth, 20);

			lrivi += 24;
			break;
		default:

			dateLbl.setBounds(10, lrivi, 70, 20);
			date.setBounds(lcol, lrivi, 283, 20);
			lrivi += 24;
			placeLbl.setBounds(10, lrivi, 70, 20);
			place.setBounds(lcol, lrivi, lwidth, 20);

			lrivi += 24;
			boolean farmShow = mustFarm;
			if (mustFarm) {

				villageLbl.setBounds(10, lrivi, 70, 20);
				village.setBounds(lcol, lrivi, lwidth, 20);

				lrivi += 24;

				farmLbl.setBounds(10, lrivi, 70, 20);
				farm.setBounds(lcol, lrivi, lwidth, 20);
				lrivi += 24;

				croftLbl.setBounds(10, lrivi, 70, 20);
				croft.setBounds(lcol, lrivi, lwidth, 20);
				lrivi += 24;
			}
			villageLbl.setVisible(farmShow);
			farmLbl.setVisible(farmShow);
			croftLbl.setVisible(farmShow);
			village.setVisible(farmShow);
			farm.setVisible(farmShow);
			croft.setVisible(farmShow);

		}

		boolean addressShow = false;
		if ((showType == TagType.RESI || mustAddress)
				&& (showType != TagType.NAME && showType != TagType.NOTE)) {
			addressShow = true;

			addressLbl.setBounds(10, lrivi, 70, 20);
			scrollAddress.setBounds(lcol, lrivi, lwidth, 60);

			lrivi += 64;

			postalCodeLbl.setBounds(10, lrivi - 20, 70, 20);
			postOfficeLbl.setBounds(10, lrivi, 70, 20);
			postalCode.setBounds(lcol, lrivi, 80, 20);
			postOffice.setBounds(lcol + 83, lrivi, lwidth - 83, 20);

			lrivi += 24;

			emailLbl.setBounds(10, lrivi, 70, 20);
			email.setBounds(lcol, lrivi, lwidth, 20);

			lrivi += 24;

			stateLbl.setBounds(10, lrivi, 70, 20);
			countryLbl.setBounds(lcol + lwidth / 3 + 10, lrivi, 70, 20);
			state.setBounds(lcol, lrivi, lwidth / 3, 20);
			country.setBounds(lcol + lwidth / 3 + 80, lrivi,
					lwidth * 2 / 3 - 80, 20);
			lrivi += 24;
		}

		scrollAddress.setVisible(addressShow);
		postalCode.setVisible(addressShow);
		postOffice.setVisible(addressShow);
		postalCodeLbl.setVisible(addressShow);
		postOfficeLbl.setVisible(addressShow);

		email.setVisible(addressShow);

		emailLbl.setVisible(addressShow);
		addressLbl.setVisible(addressShow);
		postalCodeLbl.setVisible(addressShow);
		postOfficeLbl.setVisible(addressShow);
		// countryLbl.setVisible(addressShow);
		emailLbl.setVisible(addressShow);
		stateLbl.setVisible(addressShow);
		countryLbl.setVisible(addressShow);
		state.setVisible(addressShow);
		country.setVisible(addressShow);

		scrollNames.setVisible(showType == TagType.NOTE);
		listaAddname.setVisible(showType == TagType.NOTE);
		listaName.setVisible(showType == TagType.NOTE);

		listaAddplace.setVisible(showType == TagType.NOTE);
		listaPlace.setVisible(showType == TagType.NOTE);
		addLabel.setVisible(showType == TagType.NOTE);

		boolean mediaShow = false;
		if ((showType == TagType.PHOT || mustImage)
				&& (showType != TagType.NAME && showType != TagType.NOTE)) {

			// if (!(notice.getTag().equals("NOTE") &&
			// notice.getTag().equals("NAME")) &&
			// notice.getTag().startsWith("PHOT") || mustImage){
			mediaShow = true;
			mediaFilenameLbl.setBounds(10, lrivi, 70, 20);

			mediaFilename.setBounds(lcol, lrivi, 160, 20);
			mediaOpenFile.setBounds(lbuttoncol, lrivi, 100, 20);

			lrivi += 24;

			mediaTitleLbl.setBounds(10, lrivi, 70, 20);

			mediaTitle.setBounds(lcol, lrivi, lwidth, 20);

			lrivi += 24;
		}
		mediaOpenFile.setVisible(mediaShow);
		mediaFilenameLbl.setVisible(mediaShow);
		mediaTitleLbl.setVisible(mediaShow);
		mediaFilename.setVisible(mediaShow);
		mediaTitle.setVisible(mediaShow);

		switch (showType) {
		case NOTE:

			noteLbl.setBounds(10, lrivi, 70, 20);

			noteLoc = new Rectangle(lcol, lrivi, lwidth, 424);

			scrollNote.setBounds(noteLoc);
			image.setVisible(false);
			lrivi += 428;

			addLabel.setBounds(10, listY1, 70, 20);
			listaName.setBounds(lcol, listY1, 200, 20);

			listaAddname.setBounds(lcol + 205, listY1, 100, 20);

			listaPlace.setBounds(lcol, listY2, 200, 20);
			listaAddplace.setBounds(lcol + 205, listY2, 100, 20);

			nameLabel.setBounds(rcol, rrNameList, rwidth * 2, 20);
			scrollNames.setBounds(rcol, rrNameList + 20, rwidth * 2, 60);

			placeLabel.setBounds(rcol, rrNameList + 90, rwidth * 2, 20);
			scrollPlaces.setBounds(rcol, rrNameList + 110, rwidth * 2, 60);

			// lrivi += 24;
			break;
		case NAME:
			scrollNote.setVisible(false);
			noteLbl.setVisible(false);
			break;
		default:
			boolean noteShow = false;
			if (mustNote) {
				noteShow = true;

				noteLbl.setBounds(10, lrivi, 70, 20);
				noteLoc = new Rectangle(lcol, lrivi, lwidth, 80);
				scrollNote.setBounds(noteLoc);

				lrivi += 84;
			}
			scrollNote.setVisible(noteShow);
			noteLbl.setVisible(noteShow);

		}

		switch (showType) {
		case NAME:

			givLbl.setBounds(10, lrivi, 70, 20);

			givenname.setBounds(lcol, lrivi, lwidth, 20);
			lrivi += 24;

			patLbl.setBounds(10, lrivi, 70, 20);
			patronym.setBounds(lcol, lrivi, lwidth, 20);

			lrivi += 24;

			preLbl.setBounds(10, lrivi, 80, 20);
			prefix.setBounds(lcol, lrivi, 60, 20);
			lrivi += 24;

			surLbl.setBounds(10, lrivi, 80, 20);
			surname.setBounds(lcol, lrivi, lwidth, 20);
			lrivi += 24;

			postLbl.setBounds(10, lrivi, 80, 20);

			postfix.setBounds(lcol, lrivi, 80, 20);
			lrivi += 30;
			break;
		default:
			givenname.setVisible(false);
			patronym.setVisible(false);
			prefix.setVisible(false);
			surname.setVisible(false);
			postfix.setVisible(false);
			givLbl.setVisible(false);
			patLbl.setVisible(false);
			preLbl.setVisible(false);
			surLbl.setVisible(false);
			postLbl.setVisible(false);
		}

		sourceLbl.setBounds(10, lrivi, 70, 20);
		scrollSource.setBounds(lcol, lrivi, lwidth, 60);
		boolean privateShow = mustPrivate;
		if (mustPrivate) {
			lrivi += 64;
			privateLbl.setBounds(10, lrivi, 70, 20);

			scrollPrivate.setBounds(lcol, lrivi, lwidth, 60);
		}
		privateLbl.setVisible(privateShow);
		scrollPrivate.setVisible(privateShow);

		lrivi += 64;

		setPreferredSize(new Dimension(rcol + rwidth * 2, lrivi));

	}

	@Override
	public void componentShown(ComponentEvent arg0) {

	}

	@Override
	public void valueChanged(ListSelectionEvent lse) {
		if (lse.getSource() == nameList) {
			listaSelectedName = nameList.getSelectedIndex();
			String text = (String) nameList.getSelectedValue();
			listaName.setText(text);
		} else if (lse.getSource() == placeList) {
			listaSelectedPlace = placeList.getSelectedIndex();
			String text = (String) placeList.getSelectedValue();
			listaPlace.setText(text);
		}

	}

	@Override
	public String toString() {
		return notice.getTag();
	}

}
