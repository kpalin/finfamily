package fi.kaila.suku.report;

import java.util.Vector;

/**
 * <h1>Collector for person references in report</h1>
 * 
 * <p>
 * This is used both in server side and client side but are not transmitted
 * between the two layers
 * </p>
 * 
 * @author Kalle
 * 
 */
public class PersonInTables {

	int pid = 0;
	public Vector<Long> asChildren = new Vector<Long>();
	public Vector<Long> asParents = new Vector<Long>();
	public Vector<Long> references = new Vector<Long>();

	/**
	 * Constructor
	 * 
	 * @param pid
	 *            i.e. person id whom this concerns
	 */
	public PersonInTables(int pid) {
		this.pid = pid;
	}

	/**
	 * 
	 * 
	 * The report dialog contains a table with the names of the notices
	 * 
	 * <ul>
	 * <li>column 1 contains the name of the notice</li>
	 * <li>column 2 contains settings for main person</li>
	 * <li>column 3 contains settings for person who is a main person elsewere</li>
	 * <li>column 4 contains settings for non relatives</li>
	 * <li>column 5 contains value to be used instead of name in report</li>
	 * </ul>
	 * 
	 * @param table
	 * @param alsoSpouse
	 * @param alsoChild
	 * @param alsoOther
	 * @return 2 or 3
	 */
	public int getTypesColumn(long table, boolean alsoSpouse,
			boolean alsoChild, boolean alsoOther) {
		long firstTable = table;

		if (alsoChild) {
			for (int i = 0; i < asChildren.size(); i++) {
				if (asChildren.get(i) != table) {
					if (asChildren.get(i) < firstTable) {
						firstTable = asChildren.get(i);
					}
				}
			}
		}

		if (alsoSpouse) {
			for (int i = 0; i < asParents.size(); i++) {
				if (asParents.get(i) != table) {
					if (asParents.get(i) < firstTable) {
						firstTable = asParents.get(i);
					}
				}
			}
		}
		if (alsoOther) {
			for (int i = 0; i < references.size(); i++) {
				if (references.get(i) != table) {
					if (references.get(i) < firstTable) {
						firstTable = references.get(i);
					}
				}
			}
		}
		if (firstTable == 0 || firstTable == table)
			return 2;
		if (firstTable < table)
			return 2;
		return 3;

	}

	/**
	 * 
	 * Get a list of tables where person also exists
	 * 
	 * Checks if tableNo has other references
	 * 
	 * @param table
	 * @param alsoSpouse
	 * @param alsoChild
	 * @param alsoOther
	 * @return a comma separated list of tables
	 */
	public String getReferences(long table, boolean alsoSpouse,
			boolean alsoChild, boolean alsoOther) {
		StringBuffer sx = new StringBuffer();

		if (alsoChild) {
			for (int i = 0; i < asChildren.size(); i++) {
				if (asChildren.get(i) != table) {
					if (sx.length() > 0)
						sx.append(",");
					sx.append("" + asChildren.get(i));
				}
			}
		}

		if (alsoSpouse) {
			for (int i = 0; i < asParents.size(); i++) {
				if (asParents.get(i) != table) {
					if (sx.length() > 0)
						sx.append(",");
					sx.append("" + asParents.get(i));
				}
			}
		}
		if (alsoOther) {
			for (int i = 0; i < references.size(); i++) {
				if (references.get(i) != table) {
					if (sx.length() > 0)
						sx.append(",");
					sx.append("" + references.get(i));
				}
			}
		}
		return sx.toString();
	}

}
