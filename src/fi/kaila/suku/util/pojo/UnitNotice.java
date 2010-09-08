package fi.kaila.suku.util.pojo;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.imageio.ImageIO;

/**
 * Container class for UnitNotice table.
 * 
 * @author Kalle
 */
public class UnitNotice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean toBeDeleted = false;
	private boolean toBeUpdated = false;

	/** The pnid. */
	int pnid = 0;

	/** The pid. */
	int pid = 0;

	/** The surety. */
	int surety = 100; // -- surety indicator

	/** The notice row. */
	int noticeRow = 0; // -- Row # of the Notice for the unit

	/** The tag. */
	String tag = null; // -- Tag of the Notice, Mostly Level 1 GEDCOM tags

	/** The privacy. */
	String privacy = null; // -- Privacy indicator, null = Public

	/** The notice type. */
	String noticeType = null; // -- Notice type (L)

	/** The description. */
	String description = null; // -- Description or remark (L)

	/** The date prefix. */
	String datePrefix = null; // -- Prefix for the date (beginning date if date
	// period)
	/** The from date. */
	String fromDate = null; // -- Date for the event described in this notice

	/** The to date. */
	String toDate = null; // -- Date for the event described in this notice

	/** The place. */
	String place = null; // -- Place

	/** The village. */
	String village = null; // varchar, -- Kyl NEW

	/** The farm. */
	String farm = null; // varchar, -- Talo NEW

	/** The croft. */
	String croft = null; // varchar, -- Torppa NEW

	/** The address. */
	String address = null; // varchar, -- Address line 1 / Village/Kylä

	/** The post office. */
	String postOffice = null; // varchar, -- Place of the event, Postoffice,
	// City
	/** The postal code. */
	String postalCode = null; // varchar, -- Postal Code

	/** The state. */
	String state = null; // varchar, -- State

	/** The country. */
	String country = null; // varchar, -- Country

	/** The email. */
	String email = null; // varchar, -- Email-address or web-page of person

	/** The note text. */
	String noteText = null; // varchar, -- Note textfield (L)

	/** The media filename. */
	String mediaFilename = null; // varchar, -- Filename of the multimedia file

	/** The media data. */
	byte[] mediaData = null; // bytea, -- Container of image

	/** The media title. */
	String mediaTitle = null; // varchar, -- text describing the multimedia file
	// (L)
	/** The media width. */
	int mediaWidth = 0; // integer, -- media width in pixels

	/** The media height. */
	int mediaHeight = 0; // integer, -- media height in pixels

	/** The prefix. */
	String prefix = null; // varchar, -- Prefix of the surname

	/** The surname. */
	String surname = null; // varchar, -- Surname

	/** The givenname. */
	String givenname = null; // varchar, -- Givenname

	/** The patronym. */
	String patronym = null; // varchar, -- Patronyymi NEW

	/** The post fix. */
	String postFix = null; // varchar, -- Name Postfix

	/** The ref names. */
	String[] refNames = null; // varchar, -- List of names within notice for
	// index
	/** The ref places. */
	String[] refPlaces = null; // varchar, -- List of places within notice for
	// index
	/** The source text. */
	String sourceText = null; // varchar , -- Source as text

	/** The private text. */
	String privateText = null; // varchar, -- Private researcher information

	/** The modified. */
	Timestamp modified = null; // timestamp, -- timestamp modified

	/** The create date. */
	Timestamp createDate = null; // timestamp not null default now() --
	// timestamp created

	// FIXME: Class fi.kaila.suku.util.pojo.UnitNotice defines non-transient
	// non-serializable instance field image. This Serializable class defines a
	// non-primitive instance field which is neither transient, Serializable, or
	// java.lang.Object, and does not appear to implement the Externalizable
	// interface or the readObject() and writeObject() methods. Objects of this
	// class will not be deserialized correctly if a non-Serializable object is
	// stored in this field.
	// Mika: Maybe you should set it transient?
	private BufferedImage image = null;

	private UnitLanguage[] unitlanguages = null;

	/**
	 * create table UnitNotice ( PNID integer not null primary key, -- Numeric
	 * Id that identifies this Notice, supplied by the system PID integer not
	 * null references Unit(PID), -- Id of the Unit for this UnitNotice --
	 * active boolean not null default true, -- active when this notice is
	 * activated by reasearcher surety integer not null default 100, -- surety
	 * indicator MainItem boolean not null default false, -- true for primary
	 * name and other primary items NoticeRow integer not null default 0, -- Row
	 * # of the Notice for the unit Tag varchar not null, -- Tag of the Notice,
	 * Mostly Level 1 GEDCOM tags Privacy char, -- Privacy indicator, null =
	 * Public NoticeType varchar, -- Notice type (L) Description varchar, --
	 * Description or remark (L) FromDatePrefix varchar(8), -- Prefix for the
	 * date (beginning date if date period) DatePrefix varchar(8), -- Prefix for
	 * the date (beginning date if date period) FromDate varchar, -- Date for
	 * the event described in this notice ToDate varchar, -- Date for the event
	 * described in this notice Place varchar, -- Place Village varchar, -- Kyl
	 * NEW Farm varchar, -- Talo NEW Croft varchar, -- Torppa NEW Address
	 * varchar, -- Address line 1 / Village/Kylä PostOffice varchar, -- Place of
	 * the event, Postoffice, City PostalCode varchar, -- Postal Code Country
	 * varchar, -- Country Location point, -- Geographical location of place
	 * Email varchar, -- Email-address or web-page of person NoteText varchar,
	 * -- Note textfield (L) MediaFilename varchar, -- Filename of the
	 * multimedia file MediaData bytea, -- Container of image MediaTitle
	 * varchar, -- text describing the multimedia file (L) MediaWidth integer,
	 * -- media width in pixels MediaHeight integer, -- media height in pixels
	 * Prefix varchar, -- Prefix of the surname Surname varchar, -- Surname
	 * Givenname varchar, -- Givenname Patronym varchar, -- Patronyymi NEW
	 * PostFix varchar, -- Name Postfix RefNames varchar, -- List of names
	 * within notice for index RefPlaces varchar, -- List of places within
	 * notice for index SID integer, -- temp storage for sourcieid for now
	 * SourceText varchar , -- Source as text PrivateText varchar, -- Private
	 * researcher information Modified timestamp, -- timestamp modified
	 * CreateDate timestamp not null default now() -- timestamp created );
	 * 
	 * The sr has format select * from unitNotice.
	 * 
	 * @param rs
	 *            the rs
	 * @throws SQLException
	 *             the sQL exception
	 */

	public UnitNotice(ResultSet rs) throws SQLException {
		pnid = rs.getInt("pnid");
		pid = rs.getInt("pid");
		surety = rs.getInt("surety");
		noticeRow = rs.getInt("noticerow");
		tag = rs.getString("tag");
		privacy = rs.getString("privacy");
		noticeType = rs.getString("noticetype");
		description = rs.getString("description");
		datePrefix = rs.getString("dateprefix");
		fromDate = rs.getString("fromdate");
		toDate = rs.getString("todate");
		place = rs.getString("place");
		village = rs.getString("village");
		farm = rs.getString("farm");
		croft = rs.getString("croft");
		address = rs.getString("address");
		postOffice = rs.getString("postoffice");
		postalCode = rs.getString("postalcode");
		state = rs.getString("state");
		country = rs.getString("country");
		email = rs.getString("email");
		noteText = rs.getString("notetext");
		mediaFilename = rs.getString("mediafilename");
		mediaData = rs.getBytes("mediadata");
		mediaTitle = rs.getString("mediatitle");
		mediaWidth = rs.getInt("mediawidth");
		mediaHeight = rs.getInt("mediaheight");
		prefix = rs.getString("prefix");
		surname = rs.getString("surname");
		givenname = rs.getString("givenname");
		patronym = rs.getString("patronym");
		postFix = rs.getString("postfix");
		refNames = null;
		if ("NOTE".equals(tag)) {
			Array xx = rs.getArray("refnames");
			if (xx != null) {
				refNames = (String[]) xx.getArray();

			}
			xx = rs.getArray("refplaces");
			if (xx != null) {
				refPlaces = (String[]) xx.getArray();

			}

		}

		sourceText = rs.getString("sourcetext");
		privateText = rs.getString("privatetext");
		modified = rs.getTimestamp("modified");
		createDate = rs.getTimestamp("createDate");

	}

	/**
	 * Sets the to be deleted.
	 * 
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
	}

	/**
	 * reset modifeid flag.
	 */
	public void resetModified() {
		toBeUpdated = false;
	}

	/**
	 * Checks if is to be deleted.
	 * 
	 * @return true if this is to be deleted
	 */
	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	/**
	 * Checks if is to be updated.
	 * 
	 * @return true if this is to be updated
	 */
	public boolean isToBeUpdated() {
		if (toBeUpdated)
			return true;
		if (unitlanguages == null)
			return false;
		for (int i = 0; i < unitlanguages.length; i++) {
			if (unitlanguages[i].isToBeUpdated()
					|| unitlanguages[i].isToBeDeleted())
				return true;
		}
		return false;
	}

	/**
	 * Instantiates a new unit notice.
	 * 
	 * @param tag
	 *            the tag
	 */
	public UnitNotice(String tag) {
		this.tag = tag;
	}

	/**
	 * Instantiates a new unit notice.
	 * 
	 * @param tag
	 *            the tag
	 * @param pid
	 *            the pid
	 */
	public UnitNotice(String tag, int pid) {
		this.tag = tag;
		this.pid = pid;
	}

	/**
	 * Sets the languages.
	 * 
	 * @param languages
	 *            = array of language variants
	 */
	public void setLanguages(UnitLanguage[] languages) {
		this.unitlanguages = languages;
	}

	/**
	 * Gets the languages.
	 * 
	 * @return the array of existing language varianls
	 */
	public UnitLanguage[] getLanguages() {
		return this.unitlanguages;
	}

	/**
	 * Gets the pnid.
	 * 
	 * @return perrson notice id
	 */
	public int getPnid() {
		return pnid;
	}

	/**
	 * Gets the pid.
	 * 
	 * @return pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * Gets the tag.
	 * 
	 * @return tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Gets the surety.
	 * 
	 * @return surety
	 */
	public int getSurety() {
		return surety;
	}

	/**
	 * Sets the surety.
	 * 
	 * @param surety
	 *            (0,20,40,60,80,100)
	 */
	public void setSurety(int surety) {
		if (this.surety != surety) {
			this.toBeUpdated = true;
			this.surety = surety;
		}
	}

	/**
	 * Gets the privacy.
	 * 
	 * @return privact
	 */
	public String getPrivacy() {
		return privacy;
	}

	/**
	 * Sets the privacy.
	 * 
	 * @param text
	 *            = null,"P","T" or "I"
	 */
	public void setPrivacy(String text) {
		if (!nv(this.privacy).equals(nv(text))) {
			toBeUpdated = true;
			this.privacy = vn(text);
		}

	}

	/**
	 * Gets the notice type.
	 * 
	 * @return notice type
	 */
	public String getNoticeType() {
		return trim(noticeType);
	}

	/**
	 * Sets the notice type.
	 * 
	 * @param text
	 *            = notice type
	 */
	public void setNoticeType(String text) {
		if (!nv(this.noticeType).equals(nv(text))) {
			toBeUpdated = true;
			this.noticeType = vn(text);
		}

	}

	/**
	 * Gets the description.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return trim(description);
	}

	/**
	 * Sets the description.
	 * 
	 * @param text
	 *            the new description
	 */
	public void setDescription(String text) {
		if (!nv(this.description).equals(nv(text))) {
			toBeUpdated = true;
			this.description = vn(text);
		}

	}

	/**
	 * Gets the date prefix.
	 * 
	 * @return dateprefix
	 */
	public String getDatePrefix() {
		return datePrefix;
	}

	/**
	 * Sets the date prefix.
	 * 
	 * @param text
	 *            dateprefic (see GEDCOM)
	 */
	public void setDatePrefix(String text) {
		if (!nv(this.datePrefix).equals(nv(text))) {
			toBeUpdated = true;
			this.datePrefix = vn(text);
		}

	}

	/**
	 * Gets the from date.
	 * 
	 * @return main / first part of date
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the from date.
	 * 
	 * @param text
	 *            main date
	 */
	public void setFromDate(String text) {
		if (!nv(this.fromDate).equals(nv(text))) {
			toBeUpdated = true;
			this.fromDate = vn(text);
		}

	}

	/**
	 * Gets the to date.
	 * 
	 * @return second date of date interval
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 * 
	 * @param text
	 *            second part of date interval
	 */
	public void setToDate(String text) {
		if (!nv(this.toDate).equals(nv(text))) {
			toBeUpdated = true;
			this.toDate = vn(text);
		}

	}

	/**
	 * Gets the place.
	 * 
	 * @return place
	 */
	public String getPlace() {
		return trim(place);
	}

	/**
	 * Sets the place.
	 * 
	 * @param text
	 *            the new place
	 */
	public void setPlace(String text) {
		if (!nv(this.place).equals(nv(text))) {
			toBeUpdated = true;
			this.place = vn(text);
		}

	}

	/**
	 * Gets the village.
	 * 
	 * @return village
	 */
	public String getVillage() {
		return trim(village);
	}

	/**
	 * Sets the village.
	 * 
	 * @param text
	 *            the new village
	 */
	public void setVillage(String text) {
		if (!nv(this.village).equals(nv(text))) {
			toBeUpdated = true;
			this.village = vn(text);
		}

	}

	/**
	 * Gets the farm.
	 * 
	 * @return farm
	 */
	public String getFarm() {
		return trim(farm);
	}

	/**
	 * Sets the farm.
	 * 
	 * @param text
	 *            the new farm
	 */
	public void setFarm(String text) {
		if (!nv(this.farm).equals(nv(text))) {
			toBeUpdated = true;
			this.farm = vn(text);
		}

	}

	/**
	 * Gets the croft.
	 * 
	 * @return croft (torppa)
	 */
	public String getCroft() {
		return trim(croft);
	}

	/**
	 * Sets the croft.
	 * 
	 * @param text
	 *            the new croft
	 */
	public void setCroft(String text) {
		if (!nv(this.croft).equals(nv(text))) {
			toBeUpdated = true;
			this.croft = vn(text);
		}

	}

	/**
	 * Gets the address.
	 * 
	 * @return adderss
	 */
	public String getAddress() {
		return trim(address);
	}

	/**
	 * Sets the address.
	 * 
	 * @param text
	 *            the new address
	 */
	public void setAddress(String text) {
		if (!nv(this.address).equals(nv(text))) {
			toBeUpdated = true;
			this.address = vn(text);
		}

	}

	/**
	 * Sets the ref names.
	 * 
	 * @param names
	 *            new namelist
	 */
	public void setRefNames(String[] names) {

		toBeUpdated = true;
		this.refNames = names;
	}

	/**
	 * Sets the ref places.
	 * 
	 * @param places
	 *            ne place list
	 */
	public void setRefPlaces(String[] places) {

		toBeUpdated = true;
		this.refPlaces = places;
	}

	/**
	 * Gets the post office.
	 * 
	 * @return postoiffixe
	 */
	public String getPostOffice() {
		return trim(postOffice);
	}

	/**
	 * Sets the post office.
	 * 
	 * @param text
	 *            the new post office
	 */
	public void setPostOffice(String text) {
		if (!nv(this.postOffice).equals(nv(text))) {
			toBeUpdated = true;
			this.postOffice = vn(text);
		}

	}

	/**
	 * Gets the postal code.
	 * 
	 * @return postalcode/zip
	 */
	public String getPostalCode() {
		return trim(postalCode);
	}

	/**
	 * Sets the postal code.
	 * 
	 * @param text
	 *            the new postal code
	 */
	public void setPostalCode(String text) {
		if (!nv(this.postalCode).equals(nv(text))) {
			toBeUpdated = true;
			this.postalCode = vn(text);
		}

	}

	/**
	 * Gets the state.
	 * 
	 * @return state
	 */
	public String getState() {
		return trim(state);
	}

	/**
	 * Gets the country.
	 * 
	 * @return country
	 */
	public String getCountry() {
		return trim(country);
	}

	/**
	 * Sets the state.
	 * 
	 * @param text
	 *            the new state
	 */
	public void setState(String text) {
		if (!nv(this.state).equals(nv(text))) {
			toBeUpdated = true;
			this.state = vn(text);
		}

	}

	/**
	 * Sets the country.
	 * 
	 * @param text
	 *            the new country
	 */
	public void setCountry(String text) {
		if (!nv(this.country).equals(nv(text))) {
			toBeUpdated = true;
			this.country = vn(text);
		}

	}

	/**
	 * Gets the email.
	 * 
	 * @return emailaddress
	 */
	public String getEmail() {
		return trim(email);
	}

	/**
	 * Sets the email.
	 * 
	 * @param text
	 *            the new email
	 */
	public void setEmail(String text) {
		if (!nv(this.email).equals(nv(text))) {
			toBeUpdated = true;
			this.email = vn(text);
		}

	}

	/**
	 * Gets the note text.
	 * 
	 * @return notetext
	 */
	public String getNoteText() {
		return trim(noteText);
	}

	/**
	 * Sets the note text.
	 * 
	 * @param text
	 *            the new note text
	 */
	public void setNoteText(String text) {
		if (!nv(this.noteText).equals(nv(text))) {
			toBeUpdated = true;
			this.noteText = vn(text);
		}

	}

	/**
	 * Gets the media filename.
	 * 
	 * @return mediafilename
	 */
	public String getMediaFilename() {
		return trim(mediaFilename);
	}

	/**
	 * Sets the media filename.
	 * 
	 * @param text
	 *            the new media filename
	 */
	public void setMediaFilename(String text) {
		if (!nv(this.mediaFilename).equals(nv(text))) {
			toBeUpdated = true;
			this.mediaFilename = vn(text);
		}

	}

	/**
	 * Gets the media title.
	 * 
	 * @return media title
	 */
	public String getMediaTitle() {
		return trim(mediaTitle);
	}

	/**
	 * Sets the media title.
	 * 
	 * @param text
	 *            the new media title
	 */
	public void setMediaTitle(String text) {
		if (!nv(this.mediaTitle).equals(nv(text))) {
			toBeUpdated = true;
			this.mediaTitle = vn(text);
		}

	}

	/**
	 * Gets the media size.
	 * 
	 * @return media size
	 */
	public Dimension getMediaSize() {
		return new Dimension(mediaWidth, mediaHeight);
	}

	/**
	 * Sets the media size.
	 * 
	 * @param sz
	 *            the new media size
	 */
	public void setMediaSize(Dimension sz) {
		mediaWidth = sz.width;
		mediaHeight = sz.height;
		toBeUpdated = true;
	}

	/**
	 * Gets the media image.
	 * 
	 * @return image
	 */
	public BufferedImage getMediaImage() {
		if (mediaData == null)
			return null;
		ByteArrayInputStream bb = new ByteArrayInputStream(mediaData);

		if (this.image != null) {
			return this.image;
		}
		try {
			this.image = ImageIO.read(bb);
			return this.image;
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Sets the media data.
	 * 
	 * @param data
	 *            the new media data
	 */
	public void setMediaData(byte[] data) {
		mediaData = data;
		image = null;
		toBeUpdated = true;
	}

	/**
	 * Gets the media data.
	 * 
	 * @return media data
	 */
	public byte[] getMediaData() {
		return mediaData;
	}

	/**
	 * Gets the givenname.
	 * 
	 * @return givenname
	 */
	public String getGivenname() {

		return trim(givenname);
	}

	/**
	 * Sets the givenname.
	 * 
	 * @param text
	 *            the new givenname
	 */
	public void setGivenname(String text) {
		if (!nv(this.givenname).equals(nv(text))) {
			toBeUpdated = true;
			this.givenname = vn(text);
		}

	}

	/**
	 * Gets the patronym.
	 * 
	 * @return patronym
	 */
	public String getPatronym() {
		return trim(patronym);
	}

	/**
	 * Sets the patronym.
	 * 
	 * @param text
	 *            the new patronym
	 */
	public void setPatronym(String text) {
		if (!nv(this.patronym).equals(nv(text))) {
			toBeUpdated = true;
			this.patronym = vn(text);
		}
	}

	/**
	 * Gets the prefix.
	 * 
	 * @return name prefix
	 */
	public String getPrefix() {
		return trim(prefix);
	}

	/**
	 * Sets the prefix.
	 * 
	 * @param text
	 *            the new prefix
	 */
	public void setPrefix(String text) {
		if (!nv(this.prefix).equals(nv(text))) {
			toBeUpdated = true;
			this.prefix = vn(text);
		}

	}

	/**
	 * Gets the surname.
	 * 
	 * @return surname
	 */
	public String getSurname() {
		return trim(surname);
	}

	/**
	 * Sets the surname.
	 * 
	 * @param text
	 *            the new surname
	 */
	public void setSurname(String text) {
		if (!nv(this.surname).equals(nv(text))) {
			toBeUpdated = true;
			this.surname = vn(text);
		}

	}

	/**
	 * Gets the postfix.
	 * 
	 * @return name postfix
	 */
	public String getPostfix() {
		return trim(postFix);
	}

	/**
	 * Sets the postfix.
	 * 
	 * @param text
	 *            the new postfix
	 */
	public void setPostfix(String text) {
		if (!nv(this.postFix).equals(nv(text))) {
			toBeUpdated = true;
			this.postFix = vn(text);
		}

	}

	/**
	 * Gets the source.
	 * 
	 * @return source
	 */
	public String getSource() {
		return trim(sourceText);
	}

	/**
	 * Gets the ref names.
	 * 
	 * @return array of names in note text
	 */
	public String[] getRefNames() {
		return refNames;
	}

	/**
	 * Gets the ref places.
	 * 
	 * @return array of places in note text
	 */
	public String[] getRefPlaces() {
		return refPlaces;
	}

	/**
	 * Sets the source.
	 * 
	 * @param text
	 *            the new source
	 */
	public void setSource(String text) {
		if (!nv(this.sourceText).equals(nv(text))) {
			toBeUpdated = true;
			this.sourceText = vn(text);
		}

	}

	/**
	 * Gets the private text.
	 * 
	 * @return private text
	 */
	public String getPrivateText() {
		return trim(privateText);
	}

	/**
	 * Sets the private text.
	 * 
	 * @param text
	 *            the new private text
	 */
	public void setPrivateText(String text) {
		if (!nv(this.privateText).equals(nv(text))) {
			toBeUpdated = true;
			this.privateText = vn(text);
		}

	}

	/**
	 * Gets the modified.
	 * 
	 * @return when modified
	 */
	public Timestamp getModified() {
		return modified;
	}

	/**
	 * Gets the created.
	 * 
	 * @return when created
	 */
	public Timestamp getCreated() {
		return createDate;
	}

	private String trim(String text) {
		return text;
	}

	private String nv(String text) {
		if (text == null)
			return "";
		return text.trim();
	}

	private String vn(String text) {
		if (text == null || text.length() == 0) {
			text = null;
		}

		return text;

	}

	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		if (privacy != null)
			return false;
		if (noticeType != null)
			return false;
		if (description != null)
			return false;
		if (datePrefix != null)
			return false;
		if (fromDate != null)
			return false;
		if (toDate != null)
			return false;
		if (place != null)
			return false;
		if (village != null)
			return false;
		if (farm != null)
			return false;
		if (croft != null)
			return false;
		if (address != null)
			return false;
		if (postOffice != null)
			return false;
		if (postalCode != null)
			return false;
		if (state != null)
			return false;
		if (country != null)
			return false;
		if (email != null)
			return false;
		if (noteText != null)
			return false;
		if (mediaFilename != null)
			return false;
		if (mediaData != null)
			return false;
		if (mediaTitle != null)
			return false;
		if (prefix != null)
			return false;
		if (surname != null)
			return false;
		if (givenname != null)
			return false;

		if (patronym != null)
			return false;
		if (postFix != null)
			return false;
		if (refNames != null)
			return false;
		if (refPlaces != null)
			return false;
		if (sourceText != null)
			return false;
		if (privateText != null)
			return false;

		return true;
	}

}
