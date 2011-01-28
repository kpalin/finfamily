package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Relation table row as POJO object.
 * 
 * @author Kalle
 */
public class Relation implements Serializable {

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

	/** The aid. */
	int aid = 0; // subject pid

	/** The bid. */
	int bid = 0; // relative pid

	/** The rid. */
	int rid = 0;

	/** The rtag. */
	String rtag = null; // subject tag

	/** The surety. */
	int surety = 100; // relation surety

	/** The modified. */
	Timestamp modified = null;

	/** The created. */
	Timestamp created = null;

	/** The modified by userid. */
	String modifiedBy = null;

	/** The created by userid. */
	String createdBy = null;

	/** The notices. */
	RelationNotice[] notices = null;
	//
	// used in RelaticesPane. Does not come from server
	//
	private PersonShortData pers = null;

	/**
	 * Instantiates a new relation.
	 * 
	 * @param rid
	 *            the rid
	 * @param aid
	 *            the aid
	 * @param bid
	 *            the bid
	 * @param tag
	 *            the tag
	 * @param surety
	 *            the surety
	 * @param modified
	 *            the modified
	 * @param created
	 *            the created
	 */
	public Relation(int rid, int aid, int bid, String tag, int surety,
			Timestamp modified, Timestamp created, String modifiedBy,
			String createdBy) {
		this.rid = rid;
		this.aid = aid;
		this.bid = bid;
		this.rtag = tag;
		this.surety = surety;
		this.modified = modified;
		this.created = created;
		this.modifiedBy = modifiedBy;
		this.createdBy = createdBy;
		if (rid == 0) {
			toBeUpdated = true;
		}
	}

	/**
	 * Sets the rid.
	 * 
	 * @param rid
	 *            the new rid
	 */
	public void setRid(int rid) {
		if (this.rid == 0) {
			this.rid = rid;
		}
	}

	/**
	 * Gets the adopted.
	 * 
	 * @return adopted status
	 */
	public String getAdopted() {
		if (notices != null) {
			for (int i = 0; i < notices.length; i++) {
				if (notices[i].getTag().equals("ADOP")) {
					return "a";
				}
			}
		}
		return null;
	}

	/**
	 * Set value for relation surety.
	 * 
	 * @param surety
	 *            the new surety
	 */
	public void setSurety(int surety) {
		this.surety = surety;
	}

	/**
	 * reset modified status.
	 */
	public void resetModified() {
		toBeUpdated = false;
	}

	/**
	 * Sets the notices.
	 * 
	 * @param notices
	 *            an array of relationNotice objects for the relation
	 */
	public void setNotices(RelationNotice[] notices) {
		this.notices = notices;
	}

	/**
	 * Gets the notices.
	 * 
	 * @return the array of relationnotices
	 */
	public RelationNotice[] getNotices() {
		return notices;
	}

	/**
	 * Sets the to be deleted.
	 * 
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
		toBeUpdated = true;
	}

	/**
	 * Sets the to be updated.
	 * 
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeUpdated(boolean value) {

		toBeUpdated = value;
	}

	/**
	 * Checks if is to be deleted.
	 * 
	 * @return true if it is to be deleted
	 */
	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	/**
	 * Checks if is to be updated.
	 * 
	 * @return true if is to be updated
	 */
	public boolean isToBeUpdated() {
		return toBeUpdated;
	}

	/**
	 * Gets the tag.
	 * 
	 * @return tag
	 */
	public String getTag() {
		return rtag;
	}

	/**
	 * Gets the rid.
	 * 
	 * @return rid
	 */
	public int getRid() {
		return rid;
	}

	/**
	 * Gets the relative.
	 * 
	 * @return relative pid
	 */
	public int getRelative() {
		return bid;
	}

	/**
	 * Gets the pid.
	 * 
	 * @return pid
	 */
	public int getPid() {
		return aid;
	}

	/**
	 * Sets the pid.
	 * 
	 * @param pid
	 *            the new pid
	 */
	public void setPid(int pid) {
		if (this.aid != pid) {
			toBeUpdated = true;
		}
		this.aid = pid;
	}

	/**
	 * Sets the relative.
	 * 
	 * @param pid
	 *            for relative
	 */
	public void setRelative(int pid) {
		if (this.bid != pid) {
			toBeUpdated = true;
		}
		this.bid = pid;
	}

	/**
	 * Gets the surety.
	 * 
	 * @return (0,20,40,60,80,100)
	 */
	public int getSurety() {
		return surety;
	}

	/**
	 * Gets the created.
	 * 
	 * @return created time
	 */
	public Timestamp getCreated() {
		return created;
	}

	/**
	 * 
	 * @return userid of creater
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Gets the modified.
	 * 
	 * @return modifieud time
	 */
	public Timestamp getModified() {
		return modified;
	}

	/**
	 * 
	 * @return userid of modifier
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * Sets the short person.
	 * 
	 * @param pers
	 *            teh short person for the relation
	 */
	public void setShortPerson(PersonShortData pers) {

		this.pers = pers;
	}

	/**
	 * Gets the short person.
	 * 
	 * @return get the short person of this relation
	 */
	public PersonShortData getShortPerson() {
		return pers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "rel " + aid + "/" + bid + "/" + rtag;
	}

}
