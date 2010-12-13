package fi.kaila.suku.swing.panel;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import fi.kaila.suku.swing.doc.SukuDocument;
import fi.kaila.suku.swing.text.DocumentSukuFilter;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * database draft text is shown here.
 * 
 * @author Kalle
 */
public class PersonTextPane extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractDocument doc;
	private int currentPid = 0;
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * default constructor.
	 */
	public PersonTextPane() {

		doc = new SukuDocument();
		setDocument(doc);
		StyledDocument styledDoc = getStyledDocument();

		if (styledDoc instanceof AbstractDocument) {
			doc = (AbstractDocument) styledDoc;
			doc.setDocumentFilter(new DocumentSukuFilter());
		} else {
			JOptionPane.showMessageDialog(this, "FAIELD TO START DOCUMENT",
					Resurses.getString(Resurses.SUKU),
					JOptionPane.ERROR_MESSAGE);
			logger.log(Level.WARNING, "CLOSE");
			return;

		}
	}

	/**
	 * Gets the doc.
	 * 
	 * @return the doc
	 */
	public AbstractDocument getDoc() {
		return doc;
	}

	private void append(String text, AttributeSet a)
			throws BadLocationException {

		doc.insertString(doc.getLength(), text, a);

	}

	/**
	 * Gets the current pid.
	 * 
	 * @return current pid for page
	 */
	public int getCurrentPid() {
		return currentPid;
	}

	/**
	 * init the pane.
	 * 
	 * @param pers
	 *            the pers
	 * @param relations
	 *            the relations
	 * @param namlist
	 *            the namlist
	 */
	public void initPerson(PersonLongData pers, Relation[] relations,
			PersonShortData[] namlist) {

		SimpleAttributeSet headerArial = new SimpleAttributeSet();
		StyleConstants.setFontFamily(headerArial, "Arial");
		StyleConstants.setFontSize(headerArial, 16);

		SimpleAttributeSet bodyText = new SimpleAttributeSet();
		StyleConstants.setFontFamily(bodyText, "Times New Roman");
		StyleConstants.setFontSize(bodyText, 12);

		SimpleAttributeSet bodyBold = new SimpleAttributeSet();
		StyleConstants.setFontFamily(bodyBold, "Times New Roman");
		StyleConstants.setFontSize(bodyBold, 12);
		StyleConstants.setBold(bodyBold, true);

		SimpleAttributeSet underlinedBold = new SimpleAttributeSet();
		StyleConstants.setFontFamily(underlinedBold, "Times New Roman");
		StyleConstants.setFontSize(underlinedBold, 12);
		StyleConstants.setBold(underlinedBold, true);
		StyleConstants.setUnderline(underlinedBold, true);

		SimpleAttributeSet blueTag = new SimpleAttributeSet();
		StyleConstants.setFontFamily(blueTag, "Times New Roman");
		StyleConstants.setFontSize(blueTag, 12);
		StyleConstants.setForeground(blueTag, Color.blue);

		SimpleAttributeSet greenTag = new SimpleAttributeSet();
		StyleConstants.setFontFamily(greenTag, "Times New Roman");
		StyleConstants.setFontSize(greenTag, 12);
		StyleConstants.setForeground(greenTag, new Color(0, 98, 0));

		SimpleAttributeSet redForte = new SimpleAttributeSet();
		StyleConstants.setFontFamily(redForte, "Times New Roman");
		StyleConstants.setFontSize(redForte, 12);
		StyleConstants.setForeground(redForte, Color.red);

		ArrayList<String> sources = new ArrayList<String>();
		ArrayList<String> privateTexts = new ArrayList<String>();

		try {
			boolean t = false; // for debugging resurses

			doc.remove(0, doc.getLength());

			if (pers == null) {
				currentPid = 0;
				return;
			}
			currentPid = pers.getPid();
			// StringBuilder sb = new StringBuilder();

			doc.insertString(0, Resurses.getString("TEXT_HEADER") + "\n",
					headerArial);

			if (t || pers.getPrivacy() != null) {
				append(Resurses.getString("TEXT_PRIVACY") + "\n", bodyBold);
			}

			if (t || pers.getGroupId() != null) {
				append(Resurses.getString("TEXT_GROUP") + " \t= "
						+ pers.getGroupId() + "\n", bodyText);

			}

			append(Resurses.getString("TEXT_SEX") + " \t= "
					+ Resurses.getString("SEX_" + pers.getSex()) + "\n",
					bodyText);

			if (t || pers.getSource() != null) {
				sources.add(pers.getSource());
				append(Resurses.getString("TEXT_SOURCE") + " \t= ["
						+ sources.size() + "]\n", bodyText);

			}
			if (t || pers.getPrivateText() != null) {
				privateTexts.add(pers.getPrivateText());
				append(Resurses.getString("TEXT_PRIVATE") + " \t= {"
						+ privateTexts.size() + "}\n", bodyText);

			}
			if (t || pers.getRefn() != null) {
				append(Resurses.getString("TEXT_REFN") + " \t= "
						+ pers.getRefn() + "\n", bodyText);

			}

			append(Resurses.getString("TEXT_CREATED") + " \t= "
					+ pers.getCreated().toString(), bodyText);
			append("\n", bodyText);

			append("\n" + Resurses.getString("TEXT_NOTICES") + "\n\n",
					headerArial);

			UnitNotice[] notices = pers.getNotices();
			String bl = ""; // blank before next word
			for (int i = 0; i < notices.length; i++) {
				UnitNotice notice = notices[i];
				append("[" + notice.getTag(), blueTag);
				if (notice.getSurety() < 80) {
					append(";" + Resurses.getString("TEXT_SURETY") + "="
							+ notice.getSurety() + " %", blueTag);
				}
				append("]", blueTag);
				bl = "";
				if (notice.getNoticeType() != null) {
					append(notice.getNoticeType(), bodyText);
					bl = " ";
				}

				if (notice.getDescription() != null) {
					append(bl + notice.getDescription(), bodyText);
					bl = " ";
				}

				if (notice.getDatePrefix() != null) {
					append(bl
							+ Resurses.getString("DATE_"
									+ notice.getDatePrefix()), bodyText);
					bl = " ";
				}
				if (notice.getFromDate() != null) {
					append(bl + Utils.textDate(notice.getFromDate(), true),
							bodyText);
					bl = " ";
				}
				if (notice.getToDate() != null) {
					if (notice.getDatePrefix() != null) {
						if (notice.getDatePrefix().equals("BET")) {
							append(bl + Resurses.getString("DATE_AND"),
									bodyText);
						} else if (notice.getDatePrefix().equals("FROM")) {
							append(bl + Resurses.getString("DATE_TO"), bodyText);
						}
						append(bl + Utils.textDate(notice.getToDate(), true),
								bodyText);

					}

					bl = " ";
				}

				if (notice.getPlace() != null) {
					append(bl + notice.getPlace(), bodyText);
					bl = " ";
				}

				if (notice.getAddress() != null) {
					append(bl + notice.getAddress(), bodyText);
					bl = " ";
				}
				if (notice.getVillage() != null) {
					append(bl + notice.getVillage(), bodyText);
					bl = " ";
				}
				if (notice.getFarm() != null) {
					append(bl + notice.getFarm(), bodyText);
					bl = " ";
				}

				if (notice.getPostalCode() != null) {
					append(bl + notice.getPostalCode(), bodyText);
					bl = " ";
				}
				if (notice.getPostOffice() != null) {
					append(bl + notice.getPostOffice(), bodyText);
					bl = " ";
				}
				if (notice.getCountry() != null) {
					append(bl + notice.getCountry(), bodyText);
					bl = " ";
				}

				if (notice.getEmail() != null) {
					append(bl + notice.getEmail(), bodyText);
					bl = " ";
				}

				if (notice.getNoteText() != null) {
					append(bl + notice.getNoteText(), bodyText);
					bl = " ";
				}

				if (notice.getMediaFilename() != null) {

					// append("\n\n" + notice.getMediaFilename() + " (" +
					// notice.getMediaSize().width + "," +
					// notice.getMediaSize().height + ")\n",bodyText);
					append("\n\n          ", bodyText);
					BufferedImage img = null;
					try {
						img = notice.getMediaImage();
					} catch (IOException e) {

					}
					if (img != null) {
						double imh = img.getHeight();
						double imw = img.getWidth();
						double newh = 300;
						double neww = 300;

						if (imh <= newh) {
							if (imw <= neww) {
								newh = imh;
								neww = imw;

							} else {
								newh = imh * (neww / imw);
								neww = imw;
							}
						} else {
							// if (imw <= neww){
							neww = imw * (newh / imh);
							// newh = imh;

						}

						Image imgs = img.getScaledInstance((int) neww,
								(int) newh, Image.SCALE_DEFAULT);

						ImageIcon icon = new ImageIcon(imgs);
						SimpleAttributeSet bodyImage = new SimpleAttributeSet();
						StyleConstants.setFontFamily(bodyImage,
								"Times New Roman");
						StyleConstants.setFontSize(bodyImage, 12);
						StyleConstants.setAlignment(bodyImage,
								StyleConstants.ALIGN_CENTER);
						StyleConstants.setIcon(bodyImage, icon);
						append(notice.getMediaFilename(), bodyImage);
					}
					if (notice.getMediaTitle() != null) {
						append("\n          " + notice.getMediaTitle(),
								bodyText);

					}
					append("\n\n", bodyText);
					bl = "";

				}

				bl = appendName(bodyBold, underlinedBold, bl,
						notice.getGivenname(), notice.getPatronym(),
						notice.getPrefix(), notice.getSurname(),
						notice.getPostfix());

				if (!bl.isEmpty()) {
					append(". ", bodyText);
				}
				bl = "";

				if (notice.getSource() != null) {
					sources.add(notice.getSource());
					append(bl + "[" + sources.size() + "]", bodyText);
					bl = " ";
				}
				if (notice.getPrivateText() != null) {
					privateTexts.add(notice.getPrivateText());
					append(bl + "{" + privateTexts.size() + "}", redForte);
					bl = " ";
				}
			}

			// do relations now

			if (relations != null && relations.length > 0 && namlist != null) {

				HashMap<Integer, PersonShortData> map = new HashMap<Integer, PersonShortData>();

				for (int i = 0; i < namlist.length; i++) {
					map.put(Integer.valueOf(namlist[i].getPid()), namlist[i]);
				}
				append("\n", bodyText);

				Relation rel;
				PersonShortData relative;
				RelationNotice[] relNotices;
				boolean activate = true;

				for (int i = 0; i < relations.length; i++) {

					rel = relations[i];
					if (rel.getTag().equals("FATH")
							|| rel.getTag().equals("MOTH")) {
						if (activate) {
							append("\n", bodyText);
							append(Resurses.getString("TEXT_PARENTS"),
									headerArial);
							append("\n", bodyText);
							activate = false;
						}
						relative = map.get(Integer.valueOf(rel.getRelative()));
						append("[" + rel.getTag() + "]", greenTag);
						bl = " ";
						relNotices = rel.getNotices();
						if (relNotices != null && relNotices.length == 1) {
							append(" [" + relNotices[0].getTag() + "]", blueTag);
							bl = " ";
						}

						bl = appendName(bodyBold, underlinedBold, bl,
								relative.getGivenname(),
								relative.getPatronym(), relative.getPrefix(),
								relative.getSurname(), relative.getPostfix());

						// append(bl + relative.getAlfaName(),bodyText);
						append("\n", bodyText);
					}
				}

				int wifenum = 0;
				for (int i = 0; i < relations.length; i++) {
					rel = relations[i];
					if (rel.getTag().equals("WIFE")
							|| rel.getTag().equals("HUSB")) {
						if (wifenum == 0) {
							append("\n", bodyText);
							append(Resurses.getString("TEXT_SPOUSES"),
									headerArial);
							append("\n", bodyText);

						}
						wifenum++;
						relative = map.get(Integer.valueOf(rel.getRelative()));
						append("[" + rel.getTag() + "]", greenTag);
						bl = " ";
						// append(" ("+wifenum +")",bodyText);
						bl = appendName(bodyBold, underlinedBold, bl,
								relative.getGivenname(),
								relative.getPatronym(), relative.getPrefix(),
								relative.getSurname(), relative.getPostfix());

						// append(bl + relative.getAlfaName(),bodyText);
						RelationNotice rn;
						relNotices = rel.getNotices();
						if (relNotices != null) {
							for (int j = 0; j < relNotices.length; j++) {
								rn = relNotices[j];
								append("[" + rn.getTag() + "]", blueTag);
								if (rn.getType() != null) {
									append(bl + rn.getType(), bodyText);
									bl = " ";
								}
								if (rn.getDescription() != null) {
									append(bl + rn.getDescription(), bodyText);
								}

								if (rn.getDatePrefix() != null) {
									append(bl
											+ Resurses.getString("DATE_"
													+ rn.getDatePrefix()),
											bodyText);
									bl = " ";
								}
								if (rn.getFromDate() != null) {
									append(bl
											+ Utils.textDate(rn.getFromDate(),
													true), bodyText);
									bl = " ";
								}
								if (rn.getToDate() != null) {
									if (rn.getDatePrefix() != null) {
										if (rn.getDatePrefix().equals("BET")) {
											append(bl
													+ Resurses
															.getString("DATE_AND"),
													bodyText);
										} else if (rn.getDatePrefix().equals(
												"FROM")) {
											append(bl
													+ Resurses
															.getString("DATE_TO"),
													bodyText);
										}
										append(bl
												+ Utils.textDate(
														rn.getToDate(), true),
												bodyText);
									}
									bl = " ";
								}

								if (rn.getNoteText() != null) {
									append(bl + rn.getNoteText(), bodyText);
								}

								if (rn.getSource() != null) {
									sources.add(rn.getSource());
									append(bl + "[" + sources.size() + "]",
											bodyText);
									bl = " ";
								}
								if (rn.getPrivateText() != null) {
									privateTexts.add(rn.getPrivateText());
									append(bl + "{" + privateTexts.size() + "}",
											redForte);
									bl = " ";
								}
							}
						}

						append("\n", bodyText);
					}
				}

				activate = true;
				for (int i = 0; i < relations.length; i++) {
					rel = relations[i];
					if (rel.getTag().equals("CHIL")) {
						if (activate) {
							append("\n", bodyText);
							append(Resurses.getString("TEXT_CHILDREN"),
									headerArial);
							append("\n", bodyText);
							activate = false;
						}
						relative = map.get(Integer.valueOf(rel.getRelative()));
						append("[" + rel.getTag() + "]", greenTag);
						bl = " ";
						relNotices = rel.getNotices();
						if (relNotices != null && relNotices.length == 1) {
							append(" [" + relNotices[0].getTag() + "]", blueTag);
							bl = " ";
						}
						bl = appendName(bodyBold, underlinedBold, bl,
								relative.getGivenname(),
								relative.getPatronym(), relative.getPrefix(),
								relative.getSurname(), relative.getPostfix());
						// append(bl + relative.getAlfaName(),bodyText);
						append("\n", bodyText);
					}
				}

			}

			// do the sources now

			for (int i = 0; i < sources.size(); i++) {
				if (i == 0) {
					append("\n", bodyText);
					append("\n" + Resurses.getString("TEXT_SOURCES"),
							headerArial);

				}
				append("\n[" + (i + 1) + "]: " + sources.get(i), bodyText);

			}

			for (int i = 0; i < privateTexts.size(); i++) {
				if (i == 0) {
					append("\n", bodyText);
					append("\n" + Resurses.getString("TEXT_PRIVATETEXTS"),
							headerArial);

				}
				append("\n{" + (i + 1) + "}: " + privateTexts.get(i), bodyText);

			}

			// sb.append(Resurses.getString("TEXT_PID") + " = " +
			// pers.getPid()+"\n" );

			// doc.insertString(0, "Text koetta for " + pers.getPid()+"\n",
			// bigTimes);
			// doc.insertString(0, "Kallen koetta", attrs);

		} catch (BadLocationException e) {
			logger.log(Level.WARNING, "FAILED", e);

		}

	}

	private String appendName(SimpleAttributeSet bodyBold,
			SimpleAttributeSet underlinedBold, String bl, String givenname,
			String prefix, String patronym, String surname, String postfix)
			throws BadLocationException {

		if (givenname != null) {

			String[] parts = givenname.split(" ");

			for (int j = 0; j < parts.length; j++) {
				if (j > 0) {
					append(" ", bodyBold);
					bl = "";
				}
				if (parts[j].endsWith("*")) {
					append(bl + parts[j].substring(0, parts[j].length() - 1),
							underlinedBold);
				} else {
					append(bl + parts[j], bodyBold);
				}
			}

			bl = " ";
		}
		if (patronym != null) {
			append(bl + patronym, bodyBold);
			bl = " ";
		}

		if (prefix != null) {
			append(bl + prefix, bodyBold);
			bl = " ";
		}

		if (surname != null) {
			append(bl + surname, bodyBold);
			bl = " ";
		}

		if (postfix != null) {
			append(bl + postfix, bodyBold);
			bl = " ";
		}

		return bl;

	}

}
