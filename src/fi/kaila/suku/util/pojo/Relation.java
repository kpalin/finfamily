package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Relation table row as POJO object
 * 
 * @author Kalle
 * 
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

	int aid = 0; // subject pid
	int bid = 0; // relative pid
	int rid = 0;
	String rtag = null; // subject tag
	int surety = 100; // relation surety
	Timestamp modified = null;
	Timestamp created = null;

	RelationNotice[] notices = null;
	//
	// used in RelaticesPane. Does not come from server
	//
	private PersonShortData pers = null;

	/**
	 * @param rid
	 * @param aid
	 * @param bid
	 * @param tag
	 * @param surety
	 * @param modified
	 * @param created
	 */
	public Relation(int rid, int aid, int bid, String tag, int surety,
			Timestamp modified, Timestamp created) {
		this.rid = rid;
		this.aid = aid;
		this.bid = bid;
		this.rtag = tag;
		this.surety = surety;
		this.modified = modified;
		this.created = created;
		if (rid == 0) {
			toBeUpdated = true;
		}
	}

	/**
	 * @param rid
	 */
	public void setRid(int rid) {
		if (this.rid == 0) {
			this.rid = rid;
		}
	}

	/**
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
	 * Set value for relation surety
	 * 
	 * @param surety
	 */
	public void setSurety(int surety) {
		this.surety = surety;
	}

	/**
	 * reset modified status
	 */
	public void resetModified() {
		toBeUpdated = false;
	}

	/**
	 * @param notices
	 *            an array of relationNotice objects for the relation
	 */
	public void setNotices(RelationNotice[] notices) {
		this.notices = notices;
	}

	/**
	 * @return the array of relationnotices
	 */
	public RelationNotice[] getNotices() {
		return notices;
	}

	/**
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
		toBeUpdated = true;
	}

	/**
	 * @param value
	 *            true if this is to be deleted
	 */
	public void setToBeUpdated(boolean value) {

		toBeUpdated = value;
	}

	/**
	 * @return true if it is to be deleted
	 */
	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	/**
	 * @return true if is to be updated
	 */
	public boolean isToBeUpdated() {
		return toBeUpdated;
	}

	/**
	 * @return tag
	 */
	public String getTag() {
		return rtag;
	}

	/**
	 * @return rid
	 */
	public int getRid() {
		return rid;
	}

	/**
	 * @return relative pid
	 */
	public int getRelative() {
		return bid;
	}

	/**
	 * @return pid
	 */
	public int getPid() {
		return aid;
	}

	/**
	 * @param pid
	 */
	public void setPid(int pid) {
		if (this.aid != pid) {
			toBeUpdated = true;
		}
		this.aid = pid;
	}

	/**
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
	 * @return (0,20,40,60,80,100)
	 */
	public int getSurety() {
		return surety;
	}

	/**
	 * @return created time
	 */
	public Timestamp getCreated() {
		return created;
	}

	/**
	 * @return modifieud time
	 */
	public Timestamp getModified() {
		return modified;
	}

	/**
	 * @param pers
	 *            teh short person for the relation
	 */
	public void setShortPerson(PersonShortData pers) {

		this.pers = pers;
	}

	/**
	 * @return get the short person of this relation
	 */
	public PersonShortData getShortPerson() {
		return pers;
	}

	@Override
	public String toString() {
		return "rel " + aid + "/" + bid + "/" + rtag;
	}

}
