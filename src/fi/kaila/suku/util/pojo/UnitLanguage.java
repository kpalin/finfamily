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

	public UnitLanguage(String langCode) {
		this.langCode = langCode;
	}

	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
	}

	public void resetModified() {
		toBeUpdated = false;
	}

	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	// public void setToBeUpdated(){
	// toBeUpdated=true;
	// }
	public boolean isToBeUpdated() {
		return toBeUpdated;
	}

	public int getPnid() {
		return pnid;
	}

	public int getPid() {
		return pid;
	}

	public String getLangCode() {
		return langCode;
	}

	public String getTag() {
		return tag;
	}

	public String getNoticeType() {
		return trim(noticeType);
	}

	public void setNoticeType(String text) {
		if (!nv(this.noticeType).equals(nv(text))) {
			toBeUpdated = true;
			this.noticeType = vn(text);
		}

	}

	public String getDescription() {
		return trim(description);
	}

	public void setDescription(String text) {
		if (!nv(this.description).equals(nv(text))) {
			toBeUpdated = true;
			this.description = vn(text);
		}

	}

	public String getPlace() {
		return trim(place);
	}

	public void setPlace(String text) {
		if (!nv(this.place).equals(nv(text))) {
			toBeUpdated = true;
			this.place = vn(text);
		}

	}

	public String getNoteText() {
		return trim(noteText);
	}

	public void setNoteText(String text) {
		if (!nv(this.noteText).equals(nv(text))) {
			toBeUpdated = true;
			this.noteText = vn(text);
		}

	}

	public String getMediaTitle() {
		return trim(mediaTitle);
	}

	public void setMediaTitle(String text) {
		if (!nv(this.mediaTitle).equals(nv(text))) {
			toBeUpdated = true;
			this.mediaTitle = vn(text);
		}

	}

	public Timestamp getModified() {
		return modified;
	}

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
