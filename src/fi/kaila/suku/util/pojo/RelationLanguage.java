package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Container class for RelationLanguage table
 * 
 * @author Kalle
 * 
 */
public class RelationLanguage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean toBeDeleted = false;
	private boolean toBeUpdated = false;

	int rnid = 0;
	int rid = 0;
	String langCode = null;
	// String tag = null; // -- Tag of the Notice, Mostly Level 1 GEDCOM tags
	String relationType = null; // -- Notice type (L)
	String description = null; // -- Description or remark (L)
	String place = null; // -- Place
	String noteText = null; // varchar, -- Note textfield (L)
	Timestamp modified = null; // timestamp, -- timestamp modified
	Timestamp createDate = null; // timestamp not null default now() --

	/**
	 * @param rs
	 * @throws SQLException
	 */
	public RelationLanguage(ResultSet rs) throws SQLException {
		rnid = rs.getInt("rnid");
		rid = rs.getInt("rid");
		langCode = rs.getString("langcode");
		relationType = rs.getString("relationtype");
		description = rs.getString("description");
		place = rs.getString("place");
		noteText = rs.getString("notetext");
		modified = rs.getTimestamp("modified");
		createDate = rs.getTimestamp("createDate");

	}

	/**
	 * @param langCode
	 */
	public RelationLanguage(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * @param value
	 *            true if to be deleted
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
	 * @return rnid = relation notice id
	 */
	public int getRnid() {
		return rnid;
	}

	/**
	 * @return rid = relation id
	 */
	public int getRid() {
		return rid;
	}

	/**
	 * @return langcode of this language
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @return type field
	 */
	public String getRelationType() {
		return trim(relationType);
	}

	/**
	 * @param text
	 *            = relation type
	 */
	public void setRelationType(String text) {
		if (!nv(this.relationType).equals(nv(text))) {
			toBeUpdated = true;
			this.relationType = vn(text);
		}

	}

	/**
	 * @return dwscription
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
	 *            = place
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
	 * @return when modified
	 */
	public Timestamp getModified() {
		return modified;
	}

	/**
	 * @return when created
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
