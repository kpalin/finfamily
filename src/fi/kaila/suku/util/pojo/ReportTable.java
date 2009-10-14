package fi.kaila.suku.util.pojo;


/**
 * 
 * @author Kalle
 *@deprecated
 */
public class ReportTable {
	private int tid=0;
	private int tableNo=0;
	private int parentTableNo=0;
	private int gen=0;
	private int spouseCount=0;
	private int childCount=0;
	private String type=null;
	
	private ReportTableMember [] members =null;
	
	/**
	 * @param tid the tid to set
	 */
	public void setTid(int tid) {
		this.tid = tid;
	}
	/**
	 * @return the tid
	 */
	public int getTid() {
		return tid;
	}
	/**
	 * @param tableNo the tableNo to set
	 */
	public void setTableNo(int tableNo) {
		this.tableNo = tableNo;
	}
	/**
	 * @return the tableNo
	 */
	public int getTableNo() {
		return tableNo;
	}
	/**
	 * @param gen the gen to set
	 */
	public void setGen(int gen) {
		this.gen = gen;
	}
	/**
	 * @return the gen
	 */
	public int getGen() {
		return gen;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param spouseCount the spouseCount to set
	 */
	public void setSpouseCount(int spouseCount) {
		this.spouseCount = spouseCount;
	}
	/**
	 * @return the spouseCount
	 */
	public int getSpouseCount() {
		return spouseCount;
	}
	/**
	 * @param childCount the childCount to set
	 */
	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}
	/**
	 * @return the childCount
	 */
	public int getChildCount() {
		return childCount;
	}
	/**
	 * @param parentTableNo the parentTableNo to set
	 */
	public void setParentTableNo(int parentTableNo) {
		this.parentTableNo = parentTableNo;
	}
	/**
	 * @return the parentTableNo
	 */
	public int getParentTableNo() {
		return parentTableNo;
	}
	/**
	 * @param members the members to set
	 */
	public void setMembers(ReportTableMember []members) {
		this.members = members;
	}
	/**
	 * @return the members
	 */
	public ReportTableMember [] getMembers() {
		return members;
	}
	

	
	
}
