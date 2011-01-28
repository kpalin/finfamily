package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Container for relationNotice.
 * 
 * @author Kalle
 */
public class RelationNotice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean toBeDeleted = false;
	private boolean toBeUpdated = false;

	/** The rnid. */
	int rnid = 0; // notice id

	/** The rid. */
	int rid = 0; // rela id

	/** The surety. */
	int surety = 100;

	/** The tag. */
	String tag = null;

	/** The type. */
	String type = null;

	/** The description. */
	String description = null;

	/** The date prefix. */
	String datePrefix = null;

	/** The from date. */
	String fromDate = null;

	/** The to date. */
	String toDate = null;

	/** The place. */
	String place = null;

	/** The note text. */
	String noteText = null;

	/** The source text. */
	String sourceText = null;

	/** The private text. */
	String privateText = null;

	/** The modified. */
	Timestamp modified = null;

	/** The modified by userid. */
	String modifiedBy = null;

	/** The created. */
	Timestamp created = null;

	/** The created by userid. */
	String createdBy = null;

	/** The languages. */
	RelationLanguage[] languages = null;

	/**
	 * Instantiates a new relation notice.
	 * 
	 * @param rnid
	 *            the rnid
	 * @param rid
	 *            the rid
	 * @param surety
	 *            the surety
	 * @param tag
	 *            the tag
	 * @param type
	 *            the type
	 * @param description
	 *            the description
	 * @param datePrefix
	 *            the date prefix
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @param place
	 *            the place
	 * @param noteText
	 *            the note text
	 * @param sourceText
	 *            the source text
	 * @param privateText
	 *            the private text
	 * @param modified
	 *            the modified
	 * @param created
	 *            the created
	 */
	public RelationNotice(int rnid, int rid, int surety, String tag,
			String type, String description, String datePrefix,
			String fromDate, String toDate, String place, String noteText,
			String sourceText, String privateText, Timestamp modified,
			Timestamp created, String modifiedBy, String createdBy) {
		this.rnid = rnid;
		this.rid = rid;
		this.setSurety(surety);
		this.tag = tag;
		this.type = type;
		this.description = description;
		this.datePrefix = datePrefix;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.place = place;
		this.noteText = noteText;
		this.sourceText = sourceText;
		this.privateText = privateText;
		this.modified = modified;
		this.created = created;
		this.modifiedBy = modifiedBy;
		this.createdBy = createdBy;
		this.toBeUpdated = false;
		this.toBeDeleted = false;
	}

	/**
	 * Instantiates a new relation notice.
	 * 
	 * @param tag
	 *            the tag
	 */
	public RelationNotice(String tag) {
		this.tag = tag;
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
	 * reset modified status.
	 */
	public void resetModified() {
		toBeUpdated = false;
	}

	/**
	 * Gets the rnid.
	 * 
	 * @return relation notice id
	 */
	public int getRnid() {
		return rnid;
	}

	/**
	 * Checks if is to be deleted.
	 * 
	 * @return is this to be deleted
	 */
	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	/**
	 * Checks if is to be updated.
	 * 
	 * @return is this to be updated
	 */
	public boolean isToBeUpdated() {
		return toBeUpdated;
	}

	/**
	 * Gets the languages.
	 * 
	 * @return array of languages
	 */
	public RelationLanguage[] getLanguages() {
		return languages;
	}

	/**
	 * Sets the languages.
	 * 
	 * @param langu
	 *            array of languages for this relation notice
	 */
	public void setLanguages(RelationLanguage[] langu) {
		languages = langu;
	}

	/**
	 * Sets the type.
	 * 
	 * @param text
	 *            = the type
	 */
	public void setType(String text) {
		if (!nv(this.type).equals(nv(text))) {
			toBeUpdated = true;
			this.type = vn(text);
		}

	}

	/**
	 * Sets the description.
	 * 
	 * @param text
	 *            = description
	 */
	public void setDescription(String text) {
		if (!nv(this.description).equals(nv(text))) {
			toBeUpdated = true;
			this.description = vn(text);
		}

	}

	/**
	 * Sets the from date.
	 * 
	 * @param text
	 *            first part of date
	 */
	public void setFromDate(String text) {
		if (!nv(this.fromDate).equals(nv(text))) {
			toBeUpdated = true;
			this.fromDate = vn(text);
		}

	}

	/**
	 * Sets the place.
	 * 
	 * @param text
	 *            = place
	 */
	public void setPlace(String text) {
		if (!nv(this.place).equals(nv(text))) {
			toBeUpdated = true;
			this.place = vn(text);
		}

	}

	/**
	 * Sets the source.
	 * 
	 * @param text
	 *            source
	 */
	public void setSource(String text) {
		if (!nv(this.sourceText).equals(nv(text))) {
			toBeUpdated = true;
			this.sourceText = vn(text);
		}

	}

	/**
	 * Sets the private text.
	 * 
	 * @param text
	 *            private text
	 */
	public void setPrivateText(String text) {
		if (!nv(this.privateText).equals(nv(text))) {
			toBeUpdated = true;
			this.privateText = vn(text);
		}

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
	 * Gets the type.
	 * 
	 * @return type of relationnotice
	 */
	public String getType() {
		return trim(type);
	}

	/**
	 * Gets the description.
	 * 
	 * @return dscription
	 */
	public String getDescription() {
		return trim(description);
	}

	/**
	 * Gets the date prefix.
	 * 
	 * @return date prefix
	 */
	public String getDatePrefix() {
		return datePrefix;
	}

	/**
	 * Sets the date prefix.
	 * 
	 * @param text
	 *            Gedcom style date prefix
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
	 * @return first date
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * Gets the to date.
	 * 
	 * @return second part of dateinterval
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 * 
	 * @param text
	 *            second part of dateinterval
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
		return place;
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
	 *            = note text
	 */
	public void setNoteText(String text) {
		if (!nv(this.noteText).equals(nv(text))) {
			toBeUpdated = true;
			this.noteText = vn(text);
		}

	}

	/**
	 * Sets the surety.
	 * 
	 * @param surety
	 *            the new surety
	 */
	public void setSurety(int surety) {
		if (this.surety != surety) {
			toBeUpdated = true;
		}
		this.surety = surety;
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
	 * Gets the private text.
	 * 
	 * @return private text
	 */
	public String getPrivateText() {
		return trim(privateText);
	}

	/**
	 * Gets the created.
	 * 
	 * @return when created
	 */
	public Timestamp getCreated() {
		return created;
	}

	/**
	 * 
	 * @return created by userid
	 */
	public String getCreatedBy() {
		return createdBy;
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
	 * 
	 * @return modifiedBy userid
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	private String trim(String text) {
		return text;
	}

	private String trimx(String text) {
		if (text == null)
			return null;

		String tek = text.trim();
		if (tek.endsWith(".")) {
			tek = tek.substring(0, tek.length() - 1);
		}
		return tek.trim();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return tag + "/" + type + "/" + description;
	}

	/**
	 * Gets the surety.
	 * 
	 * @return surety (0,20,40,60,80,100)
	 */
	public int getSurety() {
		return surety;
	}

	private String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

	private String vn(String text) {
		if (text == null || text.isEmpty())
			return null;
		return text;
	}

}
