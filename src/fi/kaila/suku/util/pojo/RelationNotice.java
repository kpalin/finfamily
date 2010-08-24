package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Container for relationNotice
 * 
 * @author Kalle
 * 
 */
public class RelationNotice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean toBeDeleted = false;
	private boolean toBeUpdated = false;
	int rnid = 0; // notice id
	int rid = 0; // rela id
	int surety = 100;
	String tag = null;
	String type = null;
	String description = null;
	String datePrefix = null;
	String fromDate = null;
	String toDate = null;
	String place = null;
	String noteText = null;
	String sourceText = null;
	String privateText = null;
	Timestamp modified = null;
	Timestamp created = null;
	RelationLanguage[] languages = null;

	/**
	 * @param rnid
	 * @param rid
	 * @param surety
	 * @param tag
	 * @param type
	 * @param description
	 * @param datePrefix
	 * @param fromDate
	 * @param toDate
	 * @param place
	 * @param noteText
	 * @param sourceText
	 * @param privateText
	 * @param modified
	 * @param created
	 */
	public RelationNotice(int rnid, int rid, int surety, String tag,
			String type, String description, String datePrefix,
			String fromDate, String toDate, String place, String noteText,
			String sourceText, String privateText, Timestamp modified,
			Timestamp created) {
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
		this.toBeUpdated = false;
		this.toBeDeleted = false;
	}

	/**
	 * @param tag
	 */
	public RelationNotice(String tag) {
		this.tag = tag;
	}

	/**
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
	}

	/**
	 * reset modified status
	 */
	public void resetModified() {
		toBeUpdated = false;
	}

	/**
	 * @return relation notice id
	 */
	public int getRnid() {
		return rnid;
	}

	/**
	 * @return is this to be deleted
	 */
	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	/**
	 * @return is this to be updated
	 */
	public boolean isToBeUpdated() {
		return toBeUpdated;
	}

	/**
	 * @return array of languages
	 */
	public RelationLanguage[] getLanguages() {
		return languages;
	}

	/**
	 * @param langu
	 *            array of languages for this relation notice
	 */
	public void setLanguages(RelationLanguage[] langu) {
		languages = langu;
	}

	/**
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
	 * @return tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return type of relationnotice
	 */
	public String getType() {
		return trim(type);
	}

	/**
	 * @return dscription
	 */
	public String getDescription() {
		return trim(description);
	}

	/**
	 * @return date prefix
	 */
	public String getDatePrefix() {
		return datePrefix;
	}

	/**
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
	 * @return first date
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * @return second part of dateinterval
	 */
	public String getToDate() {
		return toDate;
	}

	/**
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
	 * @return place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @return notetext
	 */
	public String getNoteText() {
		return trim(noteText);
	}

	/**
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
	 * @param surety
	 */
	public void setSurety(int surety) {
		if (this.surety != surety) {
			toBeUpdated = true;
		}
		this.surety = surety;
	}

	/**
	 * @return source
	 */
	public String getSource() {
		return trim(sourceText);
	}

	/**
	 * @return private text
	 */
	public String getPrivateText() {
		return trim(privateText);
	}

	/**
	 * @return when created
	 */
	public Timestamp getCreated() {
		return created;
	}

	/**
	 * @return when modified
	 */
	public Timestamp getModified() {
		return modified;
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

	public String toString() {
		return tag + "/" + type + "/" + description;
	}

	/**
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
