package fi.kaila.suku.util.pojo;

import java.io.Serializable;

/**
 * The Class RelationShortData.
 * 
 * @author FIKAAKAIL
 * 
 *         Short Family POJO
 */
public class RelationShortData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The pid. */
	int pid = 0;

	/** The relation pid. */
	int relationPid = 0;

	/** The tag. */
	String tag = null;
	private String marrDate = null;
	private String divDate = null;
	private String adopted = null;

	/** The parent array. */
	int parentArray[] = null;

	/** The order. */
	int order = 0;

	/** The aux index. */
	int auxIndex = 0;

	/** The surety. */
	int surety = 0;

	/**
	 * Instantiates a new relation short data.
	 * 
	 * @param pid
	 *            the pid
	 * @param relationPid
	 *            the relation pid
	 * @param order
	 *            the order
	 * @param tag
	 *            the tag
	 * @param surety
	 *            the surety
	 */
	public RelationShortData(int pid, int relationPid, int order, String tag,
			int surety) {
		this.pid = pid;
		this.relationPid = relationPid;
		this.tag = tag;
		this.order = order;
		this.surety = surety;
	}

	/**
	 * Sets the aux.
	 * 
	 * @param auxIndex
	 *            the new aux
	 */
	public void setAux(int auxIndex) {
		this.auxIndex = auxIndex;
	}

	/**
	 * Gets the aux.
	 * 
	 * @return the auxnumber
	 */
	public int getAux() {
		return auxIndex;
	}

	/**
	 * Sets the marr date.
	 * 
	 * @param rDate
	 *            the new marr date
	 */
	public void setMarrDate(String rDate) {
		this.marrDate = rDate;
	}

	/**
	 * Sets the div date.
	 * 
	 * @param divDate
	 *            the new div date
	 */
	public void setDivDate(String divDate) {
		this.divDate = divDate;
	}

	/**
	 * Gets the pid.
	 * 
	 * @return pid of "owner"
	 */
	public int getPid() {
		return this.pid;
	}

	/**
	 * Gets the surety.
	 * 
	 * @return surety of relation
	 */
	public int getSurety() {
		return surety;
	}

	/**
	 * Gets the relation pid.
	 * 
	 * @return pid of relative
	 */
	public int getRelationPid() {
		return this.relationPid;
	}

	/**
	 * Gets the tag.
	 * 
	 * @return tag
	 */
	public String getTag() {
		return this.tag;
	}

	/**
	 * Gets the order.
	 * 
	 * @return order # in family
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Gets the date.
	 * 
	 * @return main date of relation
	 */
	public String getDate() {
		return this.marrDate;
	}

	/**
	 * Sets the parent array.
	 * 
	 * @param parentids
	 *            the new parent array
	 */
	public void setParentArray(int[] parentids) {
		parentArray = parentids;
	}

	/**
	 * Gets the parent array.
	 * 
	 * @return array of parent ids
	 */
	public int[] getParentArray() {
		return parentArray;
	}

	/**
	 * Gets the div date.
	 * 
	 * @return divorce date of relation
	 */
	public String getDivDate() {
		return this.divDate;
	}

	/**
	 * Sets the adopted.
	 * 
	 * @param adopted
	 *            the new adopted
	 */
	public void setAdopted(String adopted) {
		this.adopted = adopted;
	}

	/**
	 * Gets the adopted.
	 * 
	 * @return the adopted
	 */
	public String getAdopted() {
		return adopted;
	}
}
