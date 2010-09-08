package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Container class for UnitLanguage table.
 * 
 * @author Kalle
 */
public class UnitLanguage implements Serializable {

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

	/** The lang code. */
	String langCode = null;

	/** The tag. */
	String tag = null; // -- Tag of the Notice, Mostly Level 1 GEDCOM tags

	/** The notice type. */
	String noticeType = null; // -- Notice type (L)

	/** The description. */
	String description = null; // -- Description or remark (L)

	/** The place. */
	String place = null; // -- Place

	/** The note text. */
	String noteText = null; // varchar, -- Note textfield (L)

	/** The media title. */
	String mediaTitle = null; // varchar, -- text describing the multimedia file
	// (L)
	/** The modified. */
	Timestamp modified = null; // timestamp, -- timestamp modified

	/** The create date. */
	Timestamp createDate = null; // timestamp not null default now() --

	// timestamp created

	/**
	 * Instantiates a new unit language.
	 * 
	 * @param rs
	 *            the rs
	 * @throws SQLException
	 *             the sQL exception
	 */
	public UnitLanguage(ResultSet rs) throws SQLException {
		pnid = rs.getInt("pnid");
		pid = rs.getInt("pid");
		langCode = rs.getString("langcode");
		tag = rs.getString("tag");
		noticeType = rs.getString("noticetype");
		description = rs.getString("description");
		place = rs.getString("place");
		noteText = rs.getString("notetext");
		mediaTitle = rs.getString("mediatitle");
		@SuppressWarnings("unused")
		String[] RefNames = null; // varchar, -- List of names within notice for
		// index
		modified = rs.getTimestamp("modified");
		createDate = rs.getTimestamp("createDate");

	}

	/**
	 * Instantiates a new unit language.
	 * 
	 * @param langCode
	 *            the lang code
	 */
	public UnitLanguage(String langCode) {
		this.langCode = langCode;
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
	 * reet modifeid status.
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
		return toBeUpdated;
	}

	/**
	 * Gets the pnid.
	 * 
	 * @return person notice id
	 */
	public int getPnid() {
		return pnid;
	}

	/**
	 * Gets the pid.
	 * 
	 * @return pid (person id)
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * Gets the lang code.
	 * 
	 * @return langcode
	 */
	public String getLangCode() {
		return langCode;
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
	 *            the new notice type
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
	 * Gets the modified.
	 * 
	 * @return time when modified
	 */
	public Timestamp getModified() {
		return modified;
	}

	/**
	 * Gets the created.
	 * 
	 * @return time when created
	 */
	public Timestamp getCreated() {
		return createDate;
	}

	private String trim(String text) {
		if (text == null)
			return null;

		String tek = text.trim();
		if (tek.endsWith(".")) {
			tek = tek.substring(0, tek.length() - 1);
		}
		return tek.trim();
	}

	private String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

	private String vn(String text) {
		if (text.length() == 0) {
			text = null;
		}
		return text;
	}

}
