package fi.kaila.suku.swing.worker;

import java.util.Vector;

public class PersonInTables {

	int pid = 0;
	Vector<Long> asChildren = new Vector<Long>();
	Vector<Long> asParents = new Vector<Long>();
	Vector<Long> references = new Vector<Long>();

	public PersonInTables(int pid) {
		this.pid = pid;
	}

	/**
	 * Checks if tableNo has other references
	 * 
	 * @param table
	 * @param alsoOwner
	 *            is true to list also references as owner
	 * @param alsoChild
	 *            is true to list also references as child
	 * @param alsoSpouse
	 *            is true to list also references as spouse
	 * @return a comma seperated list of other table numbers
	 */
	public String getReferences(long table, boolean alsoOwner,
			boolean alsoChild, boolean alsoSpouse) {
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

		for (int i = 0; i < references.size(); i++) {
			if (references.get(i) != table) {
				if (sx.length() > 0)
					sx.append(",");
				sx.append("" + references.get(i));
			}
		}
		return sx.toString();
	}

}
