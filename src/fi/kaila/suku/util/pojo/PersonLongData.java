package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * 
 * PersonLongData contains complete copy of person data from database
 * 
 * @author Kalle
 * 
 */
public class PersonLongData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int pid = 0;
	private String tag = null;
	private String privacy = null;
	private String groupId = null;
	private String sex = null;
	private String sourceText = null;
	private String privateText = null;
	private String userRefn = null;
	private Timestamp created = null;
	private Timestamp modified = null;
	private boolean orderModified = false; // order of notices has been modified
	private boolean mainModified = false; // main unit data has been modfied

	private UnitNotice[] notices = null;

	// private UnitLanguage[] unitlanguages=null;

	// private Relation[] relationNotices=null;

	/**
	 * @return pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @return tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * reset teh modified status
	 */
	public void resetModified() {
		mainModified = false;
	}

	/**
	 * set that order has been modified
	 */
	public void setOrderModified() {
		orderModified = true;
	}

	/**
	 * @return if order has been modified
	 */
	public boolean isOrderModified() {
		return orderModified;
	}

	/**
	 * @return true if main has been modified
	 */
	public boolean isMainModified() {
		return mainModified;
	}

	/**
	 * @return privacy
	 */
	public String getPrivacy() {
		return privacy;
	}

	/**
	 * @return groupid
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @return sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param text
	 *            = sex
	 */
	public void setSex(String text) {
		if (!nv(this.sex).equals(nv(text))) {
			mainModified = true;
			this.sex = vn(text);
		}

	}

	/**
	 * @return the source
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
	 * @return refn
	 */
	public String getRefn() {
		return userRefn;
	}

	/**
	 * @return time creted
	 */
	public Timestamp getCreated() {
		return created;
	}

	/**
	 * @return tiem modified
	 */
	public Timestamp getModified() {
		return modified;
	}

	/**
	 * @param pid
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * sets array of notices
	 * 
	 * @param notices
	 */
	public void setNotices(UnitNotice[] notices) {
		this.notices = notices;
	}

	/**
	 * @return array of notices
	 */
	public UnitNotice[] getNotices() {
		return this.notices;
	}

	/**
	 * @param text
	 */
	public void setSource(String text) {
		if (!nv(this.sourceText).equals(nv(text))) {
			mainModified = true;
			this.sourceText = vn(text);
		}

	}

	/**
	 * extract unit data from select * from unit
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public PersonLongData(ResultSet rs) throws SQLException {

		this.pid = rs.getInt("pid");
		this.tag = rs.getString("tag");
		this.privacy = rs.getString("privacy");
		this.groupId = rs.getString("groupId");
		this.sex = rs.getString("sex");
		this.sourceText = rs.getString("sourceText");
		this.privateText = rs.getString("privateText");
		this.userRefn = rs.getString("userrefn");
		this.created = rs.getTimestamp("createdate");
		this.modified = rs.getTimestamp("modified");

	}

	/**
	 * @param pid
	 * @param tag
	 * @param sex
	 */
	public PersonLongData(int pid, String tag, String sex) {
		this.pid = pid;
		this.tag = tag;
		this.sex = sex;

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

	/**
	 * @param text
	 *            "P" of null
	 */
	public void setPrivacy(String text) {
		if (!nv(this.privacy).equals(nv(text))) {
			mainModified = true;
			this.privacy = vn(text);
		}

	}

	/**
	 * @param text
	 */
	public void setGroupId(String text) {
		if (!nv(this.groupId).equals(nv(text))) {
			mainModified = true;
			this.groupId = vn(text);
		}

	}

	/**
	 * @param text
	 */
	public void setUserRefn(String text) {
		if (!nv(this.userRefn).equals(nv(text))) {
			mainModified = true;
			this.userRefn = vn(text);
		}

	}

	/**
	 * @param text
	 */
	public void setPrivateText(String text) {
		if (!nv(this.privateText).equals(nv(text))) {
			mainModified = true;
			this.privateText = vn(text);
		}

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
