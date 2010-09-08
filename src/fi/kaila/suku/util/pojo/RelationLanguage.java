package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Container class for RelationLanguage table.
 * 
 * @author Kalle
 */
public class RelationLanguage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean toBeDeleted = false;
	private boolean toBeUpdated = false;

	/** The rnid. */
	int rnid = 0;

	/** The rid. */
	int rid = 0;

	/** The lang code. */
	String langCode = null;
	// String tag = null; // -- Tag of the Notice, Mostly Level 1 GEDCOM tags
	/** The relation type. */
	String relationType = null; // -- Notice type (L)

	/** The description. */
	String description = null; // -- Description or remark (L)

	/** The place. */
	String place = null; // -- Place

	/** The note text. */
	String noteText = null; // varchar, -- Note textfield (L)

	/** The modified. */
	Timestamp modified = null; // timestamp, -- timestamp modified

	/** The create date. */
	Timestamp createDate = null; // timestamp not null default now() --

	/**
	 * Instantiates a new relation language.
	 * 
	 * @param rs
	 *            the rs
	 * @throws SQLException
	 *             the sQL exception
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
	 * Instantiates a new relation language.
	 * 
	 * @param langCode
	 *            the lang code
	 */
	public RelationLanguage(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * Sets the to be deleted.
	 * 
	 * @param value
	 *            true if to be deleted
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
	 * Gets the rnid.
	 * 
	 * @return rnid = relation notice id
	 */
	public int getRnid() {
		return rnid;
	}

	/**
	 * Gets the rid.
	 * 
	 * @return rid = relation id
	 */
	public int getRid() {
		return rid;
	}

	/**
	 * Gets the lang code.
	 * 
	 * @return langcode of this language
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * Gets the relation type.
	 * 
	 * @return type field
	 */
	public String getRelationType() {
		return trim(relationType);
	}

	/**
	 * Sets the relation type.
	 * 
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
	 * Gets the description.
	 * 
	 * @return dwscription
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
	 *            = place
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
