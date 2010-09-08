package fi.kaila.suku.util.pojo;

/**
 * The Class ReportTable.
 * 
 * @author Kalle
 * @deprecated
 */
public class ReportTable {
	private int tid = 0;
	private int tableNo = 0;
	private int parentTableNo = 0;
	private int gen = 0;
	private int spouseCount = 0;
	private int childCount = 0;
	private String type = null;

	private ReportTableMember[] members = null;

	/**
	 * Sets the tid.
	 * 
	 * @param tid
	 *            the tid to set
	 */
	public void setTid(int tid) {
		this.tid = tid;
	}

	/**
	 * Gets the tid.
	 * 
	 * @return the tid
	 */
	public int getTid() {
		return tid;
	}

	/**
	 * Sets the table no.
	 * 
	 * @param tableNo
	 *            the tableNo to set
	 */
	public void setTableNo(int tableNo) {
		this.tableNo = tableNo;
	}

	/**
	 * Gets the table no.
	 * 
	 * @return the tableNo
	 */
	public int getTableNo() {
		return tableNo;
	}

	/**
	 * Sets the gen.
	 * 
	 * @param gen
	 *            the gen to set
	 */
	public void setGen(int gen) {
		this.gen = gen;
	}

	/**
	 * Gets the gen.
	 * 
	 * @return the gen
	 */
	public int getGen() {
		return gen;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the spouse count.
	 * 
	 * @param spouseCount
	 *            the spouseCount to set
	 */
	public void setSpouseCount(int spouseCount) {
		this.spouseCount = spouseCount;
	}

	/**
	 * Gets the spouse count.
	 * 
	 * @return the spouseCount
	 */
	public int getSpouseCount() {
		return spouseCount;
	}

	/**
	 * Sets the child count.
	 * 
	 * @param childCount
	 *            the childCount to set
	 */
	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	/**
	 * Gets the child count.
	 * 
	 * @return the childCount
	 */
	public int getChildCount() {
		return childCount;
	}

	/**
	 * Sets the parent table no.
	 * 
	 * @param parentTableNo
	 *            the parentTableNo to set
	 */
	public void setParentTableNo(int parentTableNo) {
		this.parentTableNo = parentTableNo;
	}

	/**
	 * Gets the parent table no.
	 * 
	 * @return the parentTableNo
	 */
	public int getParentTableNo() {
		return parentTableNo;
	}

	/**
	 * Sets the members.
	 * 
	 * @param members
	 *            the members to set
	 */
	public void setMembers(ReportTableMember[] members) {
		this.members = members;
	}

	/**
	 * Gets the members.
	 * 
	 * @return the members
	 */
	public ReportTableMember[] getMembers() {
		return members;
	}

}
