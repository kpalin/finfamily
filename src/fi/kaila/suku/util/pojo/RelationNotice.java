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

	// select
	// a.rid,a.pid,b.pid,a.tag,rn.tag,rn.surety,rn.relationtype,rn.description,
	// rn.dateprefix,rn.fromdate,rn.todate,rn.notetext,rn.sourcetext,rn.privatetext,a.createdate,rn.modified,rn.createdate
	// from relation a inner join relation b on a.rid=b.rid left join
	// relationnotice rn on a.rid=rn.rid
	// where a.pid <> b.pid and a.pid=3 order by
	// b.pid,a.rid,a.relationrow,rn.noticerow
	private boolean toBeDeleted = false;
	private boolean toBeUpdated = false;
	int rnid = 0; // notice id
	int rid = 0; // rela id
	int surety = 0;
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
	}

	public RelationNotice(String tag) {
		this.tag = tag;
	}

	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
	}

	public void resetModified() {
		toBeUpdated = false;
	}

	public int getRnid() {
		return rnid;
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

	public RelationLanguage[] getLanguages() {
		return languages;
	}

	public void setLanguages(RelationLanguage[] langu) {
		languages = langu;
	}

	public void setType(String text) {
		if (!nv(this.type).equals(nv(text))) {
			toBeUpdated = true;
			this.type = vn(text);
		}

	}

	public void setDescription(String text) {
		if (!nv(this.description).equals(nv(text))) {
			toBeUpdated = true;
			this.description = vn(text);
		}

	}

	public void setFromDate(String text) {
		if (!nv(this.fromDate).equals(nv(text))) {
			toBeUpdated = true;
			this.fromDate = vn(text);
		}

	}

	public void setPlace(String text) {
		if (!nv(this.place).equals(nv(text))) {
			toBeUpdated = true;
			this.place = vn(text);
		}

	}

	public void setSource(String text) {
		if (!nv(this.sourceText).equals(nv(text))) {
			toBeUpdated = true;
			this.sourceText = vn(text);
		}

	}

	public String getTag() {
		return tag;
	}

	public String getType() {
		return trim(type);
	}

	public String getDescription() {
		return trim(description);
	}

	public String getDatePrefix() {
		return datePrefix;
	}

	public void setDatePrefix(String text) {
		if (!nv(this.datePrefix).equals(nv(text))) {
			toBeUpdated = true;
			this.datePrefix = vn(text);
		}

	}

	public String getFromDate() {
		return fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String text) {
		if (!nv(this.toDate).equals(nv(text))) {
			toBeUpdated = true;
			this.toDate = vn(text);
		}

	}

	public String getPlace() {
		return place;
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

	public String getSource() {
		return trim(sourceText);
	}

	public String getPrivateText() {
		return trim(privateText);
	}

	public Timestamp getCreated() {
		return created;
	}

	public Timestamp getModified() {
		return modified;
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

	public String toString() {
		return tag + "/" + type + "/" + description;
	}

	public void setSurety(int surety) {
		this.surety = surety;
	}

	public int getSurety() {
		return surety;
	}

	private String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

	private String vn(String text) {
		if (text == null || text.equals(""))
			return null;
		return text;
	}

}
