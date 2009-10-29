package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.util.Vector;

/**
 * ReportUnit contains whole structure of one table
 * 
 * @author Kalle
 * 
 */
public class ReportUnit implements Serializable {
	/**
	 * Container for a report table structure
	 */
	private static final long serialVersionUID = 1L;

	private int pid = 0;

	private long tableNo = 0;

	private int gen = 0;

	private Vector<ReportTableMember> asParent = new Vector<ReportTableMember>();
	private Vector<ReportTableMember> asChild = new Vector<ReportTableMember>();

	/**
	 * @return vecrot of parents
	 */
	public Vector<ReportTableMember> getParent() {
		return asParent;
	}

	/**
	 * @return vector of children
	 */
	public Vector<ReportTableMember> getChild() {
		return asChild;
	}

	/**
	 * @return count of members in table
	 */
	public int getMemberCount() {
		return asParent.size() + asChild.size();
	}

	/**
	 * @param idx
	 * @return member at idx
	 */
	public ReportTableMember getMember(int idx) {
		ReportTableMember mem = null;
		int pares = asParent.size();
		if (idx < pares) {
			mem = asParent.get(idx);
		} else if (idx - pares < asChild.size()) {
			mem = asChild.get(idx - pares);
		}
		return mem;

	}

	/**
	 * @param pare
	 *            at end
	 */
	public void addParent(ReportTableMember pare) {
		for (int i = 0; i < asParent.size(); i++) {
			if (pare.getPid() == asParent.get(i).getPid()) {
				return;
			}
		}
		asParent.add(pare);
	}

	/**
	 * @param chil
	 *            at end
	 */
	public void addChild(ReportTableMember chil) {
		for (int i = 0; i < asChild.size(); i++) {
			if (chil.getPid() == asChild.get(i).getPid()) {
				return;
			}
		}
		asChild.add(chil);
	}

	/**
	 * @param pid
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * @return pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @param tableNo
	 */
	public void setTableNo(long tableNo) {
		this.tableNo = tableNo;
	}

	/**
	 * @return tableno
	 */
	public long getTableNo() {
		return tableNo;
	}

	/**
	 * @param gen
	 * @param gen
	 *            = generation
	 */
	public void setGen(int gen) {
		this.gen = gen;
	}

	/**
	 * @return generation
	 */
	public int getGen() {
		return gen;
	}

	/**
	 * @param parentTab
	 *            tableno of parent table
	 */
	public void setParentTable(long parentTab) {
		// parentTable = parentTab;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("pid=" + pid + "/ tab=" + tableNo);
		// if (parentTable != 0){
		// sb.append("\n  paretab=" + parentTable);
		//			
		// for (int i = 0; i < parentTables.size(); i++) {
		// long ll =parentTables.get(i);
		// if (i == 0){
		// sb.append("\n  paretab=" + ll);
		// } else {
		// sb.append(","+ll);
		// }
		// }
		// }
		if (asParent.size() > 0) {
			for (int i = 0; i < asParent.size(); i++) {
				ReportTableMember rm = asParent.get(i);
				if (i == 0) {
					sb.append("\n  asparent=" + rm);
				} else {
					sb.append("," + rm);
				}
			}
		}

		if (asChild.size() > 0) {
			for (int i = 0; i < asChild.size(); i++) {
				ReportTableMember rm = asChild.get(i);
				if (i == 0) {
					sb.append("\n  aschild=" + rm);
				} else {
					sb.append("," + rm);
				}
			}
		}

		return sb.toString();
	}

}
