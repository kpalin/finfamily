package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Container class for UnitLanguage table
 * 
 * @author Kalle
 * 
 */
public class UnitLanguage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean toBeDeleted = false;
	private boolean toBeUpdated = false;

	int pnid = 0;
	int pid = 0;
	String langCode = null;
	String tag = null; // -- Tag of the Notice, Mostly Level 1 GEDCOM tags
	String noticeType = null; // -- Notice type (L)
	String description = null; // -- Description or remark (L)
	String place = null; // -- Place
	String noteText = null; // varchar, -- Note textfield (L)
	String mediaTitle = null; // varchar, -- text describing the multimedia file
	// (L)
	Timestamp modified = null; // timestamp, -- timestamp modified
	Timestamp createDate = null; // timestamp not null default now() --

	// timestamp created

	/**
	 * @param rs
	 * @throws SQLException
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
	 * @param langCode
	 */
	public UnitLanguage(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
	}

	/**
	 * reet modifeid status
	 */
	public void resetModified() {
		toBeUpdated = false;
	}

	/**
	 * @return true if this is to be deleted
	 */
	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	/**
	 * @return true if this is to be updated
	 */
	public boolean isToBeUpdated() {
		return toBeUpdated;
	}

	/**
	 * @return person notice id
	 */
	public int getPnid() {
		return pnid;
	}

	/**
	 * @return pid (person id)
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @return langcode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @return tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return notice type
	 */
	public String getNoticeType() {
		return trim(noticeType);
	}

	/**
	 * @param text
	 */
	public void setNoticeType(String text) {
		if (!nv(this.noticeType).equals(nv(text))) {
			toBeUpdated = true;
			this.noticeType = vn(text);
		}

	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return trim(description);
	}

	/**
	 * @param text
	 */
	public void setDescription(String text) {
		if (!nv(this.description).equals(nv(text))) {
			toBeUpdated = true;
			this.description = vn(text);
		}

	}

	/**
	 * @return place
	 */
	public String getPlace() {
		return trim(place);
	}

	/**
	 * @param text
	 */
	public void setPlace(String text) {
		if (!nv(this.place).equals(nv(text))) {
			toBeUpdated = true;
			this.place = vn(text);
		}

	}

	/**
	 * @return notetext
	 */
	public String getNoteText() {
		return trim(noteText);
	}

	/**
	 * @param text
	 */
	public void setNoteText(String text) {
		if (!nv(this.noteText).equals(nv(text))) {
			toBeUpdated = true;
			this.noteText = vn(text);
		}

	}

	/**
	 * @return media title
	 */
	public String getMediaTitle() {
		return trim(mediaTitle);
	}

	/**
	 * @param text
	 */
	public void setMediaTitle(String text) {
		if (!nv(this.mediaTitle).equals(nv(text))) {
			toBeUpdated = true;
			this.mediaTitle = vn(text);
		}

	}

	/**
	 * @return time when modified
	 */
	public Timestamp getModified() {
		return modified;
	}

	/**
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
		if ("".equals(text)) {
			text = null;
		}
		return text;
	}

}
