package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Vector;

import fi.kaila.suku.util.Resurses;

/**
 * report table structure element
 * 
 * @author Kalle
 * 
 */
public class ReportTableMember implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long tableNo = 0;

	/**
	 * @param table
	 */
	public void setTable(long table) {
		tableNo = table;
	}

	/**
	 * @return tableno
	 */
	public long getTable() {
		return tableNo;
	}

	/**
	 * ordering of tablemembers 0 = subject 1-n = spouse 1-n n+1-n+m child m
	 */
	private int rowNo = 0;

	/**
	 * Pid of person
	 */
	private int pid = 0;

	/**
	 * Sex of Pid
	 */
	private String sex = null;
	/**
	 * Tag of member null = Subject SPOU (WIFE/HISB) for spouses CHIL = child
	 * 
	 * FATH/MOTH for parents
	 */
	private String tag = null;

	private String reltag = null;
	/**
	 * Table no for childs own table
	 */
	private long refTableNo = 0;
	/**
	 * Table of childs table of spouse list
	 */
	private long myTable = 0;
	/**
	 * When Pid in more than one table the # of the occurrence 0 = The only
	 * occurrence 1 = The first occurrence 2 = second occurrence etc
	 */
	private int pidOrder = 0;

	private ReportTableMember[] spouses = null;

	private Vector<Long> alsoAsChild = new Vector<Long>();

	private Vector<SubPersonMember> subs = new Vector<SubPersonMember>();

	/**
	 * @param asChild
	 */
	public void addAsChild(long asChild) {
		for (int i = 0; i < alsoAsChild.size(); i++) {
			if (asChild == alsoAsChild.get(i))
				return;
		}
		alsoAsChild.add(asChild);
	}

	/**
	 * Add subperson i.e. non relative ancestor
	 * 
	 * @param pid
	 * @param sex
	 * @param strado
	 *            (2 = father, 3 = mother etc)
	 */
	public void addSub(int pid, String sex, long strado) {
		SubPersonMember sub = new SubPersonMember(pid, sex, strado);
		subs.add(sub);
	}

	/**
	 * sorts sub persons
	 */
	public void sortSubs() {
		SubPersonMember[] subarray = subs.toArray(new SubPersonMember[0]);

		Arrays.sort(subarray);
		subs = new Vector<SubPersonMember>();
		for (int i = 0; i < subarray.length; i++) {
			subs.add(subarray[i]);
		}
	}

	/**
	 * 
	 * @return # of ancestors available
	 */
	public int getSubCount() {
		return subs.size();
	}

	/**
	 * get ancestor pid
	 * 
	 * @param idx
	 *            index of ancestor
	 * @return pid
	 */
	public int getSubPid(int idx) {
		return subs.get(idx).getPid();
	}

	/**
	 * get sex of ancestor
	 * 
	 * @param idx
	 *            index of ancestor
	 * @return sex
	 */
	public String getSubSex(int idx) {
		return subs.get(idx).getSex();
	}

	/**
	 * Get stradoniz ancestior number of ancestor
	 * 
	 * @param idx
	 *            index of ancestor
	 * @return the stradoni number
	 */
	public long getSubStrado(int idx) {
		return subs.get(idx).getStrado();
	}

	/**
	 * @param idx
	 * @return dad or mom indicator for subperson
	 */
	public String getSubDadMom(int idx) {
		long strado = subs.get(idx).getStrado();
		String far = Resurses.getString("REPORT_F");
		String mor = Resurses.getString("REPORT_M");

		StringBuilder sb = new StringBuilder();

		long jako;

		while (strado > 1) {
			jako = strado % 2;
			strado = strado / 2;
			if (jako == 1)
				sb.append(mor);
			else
				sb.append(far);
		}
		String wrong = sb.toString();
		sb = new StringBuilder();
		for (int i = wrong.length() - 1; i >= 0; i--) {
			sb.append(wrong.charAt(i));
		}

		return sb.toString();
	}

	/**
	 * @return vector of tables where also as child
	 */
	public Vector<Long> getAlsoAsChild() {
		return alsoAsChild;
	}

	/**
	 * @return string with tableno, where as child
	 */
	public String getChildTables() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < alsoAsChild.size(); i++) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append("" + alsoAsChild.get(i));
		}
		return sb.toString();
	}

	/**
	 * @param rowNo
	 *            the rowNo to set
	 */
	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	/**
	 * @return the rowNo
	 */
	public int getRowNo() {
		return rowNo;
	}

	// /**
	// * @param parentTid
	// */
	// public void setParenstTid(int parentTid) {
	// this.parentTid = parentTid;
	// }
	// /**
	// * @return the parentTable
	// */
	// public int getParentTid() {
	// return parentTid;
	// }
	/**
	 * @param pid
	 *            the pid to set
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * @return the pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * set relative tag
	 * 
	 * @param value
	 */
	public void setRelTag(String value) {
		reltag = value;
	}

	/**
	 * @return the relative tag
	 */
	public String getRelTag() {
		return reltag;
	}

	/**
	 * @param refTableNo
	 *            the refTableNo to set
	 */
	public void setRefTableNo(int refTableNo) {
		this.refTableNo = refTableNo;
	}

	/**
	 * @return the refTableNo
	 */
	public long getRefTableNo() {
		return refTableNo;
	}

	/**
	 * @param refTable
	 *            the refTid to set
	 */
	public void setMyTable(long refTable) {
		this.myTable = refTable;
	}

	/**
	 * @return the refTid
	 */
	public long getMyTable() {
		return myTable;
	}

	/**
	 * @param pidOrder
	 *            the pidOrder to set
	 */
	public void setPidOrder(int pidOrder) {
		this.pidOrder = pidOrder;
	}

	/**
	 * @return the pidOrder
	 */
	public int getPidOrder() {
		return pidOrder;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("pid[" + pid + "/" + myTable + "]");

		for (int i = 0; i < subs.size(); i++) {
			sb.append("\n  " + subs.get(i).toString());
		}

		return sb.toString();
		// return "row["+rowNo+"];pid["+pid+"]";

	}

	/**
	 * @param spouses
	 *            the subMembers to set
	 */
	public void setSpouses(ReportTableMember[] spouses) {
		this.spouses = spouses;
	}

	/**
	 * @return the subMembers
	 */
	public ReportTableMember[] getSpouses() {
		return spouses;
	}

	/**
	 * 
	 * Container for persons ancestors Used for not relative spouse andcestors
	 * or not relative child ancestor numbering as in Stradoniz numbering scema
	 * father=2, mother = 3, fathersfather 4 etc
	 * 
	 */
	@SuppressWarnings("unchecked")
	class SubPersonMember implements Comparable {
		private int pid = 0;
		private String sex = null;
		private long stradoNum = 0;

		SubPersonMember(int pid, String sex, long strado) {
			this.pid = pid;
			this.sex = sex;
			this.stradoNum = strado;
		}

		int getPid() {
			return pid;
		}

		String getSex() {
			return sex;
		}

		long getStrado() {
			return stradoNum;
		}

		public String toString() {
			return "sub[" + pid + "/" + sex + "/" + stradoNum + "]";
		}

		@Override
		public int compareTo(Object arg) {
			SubPersonMember oth = (SubPersonMember) arg;
			long othernum = oth.stradoNum;
			if (stradoNum < othernum)
				return -1;
			if (stradoNum > othernum)
				return 1;
			return 0;
		}
	}

}
