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
	int surety = 0; // relation surety
	Timestamp modified = null;
	Timestamp created = null;

	RelationNotice[] notices = null;
	//
	// used in RelaticesPane. Does not come from server
	//
	private PersonShortData pers = null;

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

	public void setRid(int rid) {
		if (this.rid == 0) {
			this.rid = rid;
		}
	}

	public void resetModified() {
		toBeUpdated = false;
	}

	public void setNotices(RelationNotice[] notices) {
		this.notices = notices;
	}

	public RelationNotice[] getNotices() {
		return notices;
	}

	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
		toBeUpdated = true;
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

	public String getTag() {
		return rtag;
	}

	public int getRid() {
		return rid;
	}

	public int getRelative() {
		return bid;
	}

	public int getPid() {
		return aid;
	}

	public void setPid(int pid) {
		if (this.aid != pid) {
			toBeUpdated = true;
		}
		this.aid = pid;
	}

	public void setRelative(int pid) {
		if (this.bid != pid) {
			toBeUpdated = true;
		}
		this.bid = pid;
	}

	public int getSurety() {
		return surety;
	}

	public Timestamp getCreated() {
		return created;
	}

	public Timestamp getModified() {
		return modified;
	}

	public void setShortPerson(PersonShortData pers) {

		this.pers = pers;
	}

	public PersonShortData getShortPerson() {
		return pers;
	}

	@Override
	public String toString() {
		return "rel " + aid + "/" + bid + "/" + rtag;
	}

}
