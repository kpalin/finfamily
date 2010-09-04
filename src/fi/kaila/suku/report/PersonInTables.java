package fi.kaila.suku.report;

import java.text.Collator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Vector;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;

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
public class PersonInTables implements Comparable<PersonInTables> {

	/**
	 * owner pid
	 */
	public int pid = 0;
	/**
	 * name used for index
	 */
	public PersonShortData shortPerson = null;

	/**
	 * references as owner
	 */
	// public long asOwner = 0;
	/**
	 * references as child
	 */
	public Vector<Long> asChildren = new Vector<Long>();
	/**
	 * references as parents
	 */
	public Vector<Long> asParents = new Vector<Long>();
	/**
	 * other references like spouses parents etc
	 */
	public Vector<Long> references = new Vector<Long>();

	// private Vector<Long> asOwners = new Vector<Long>();
	private LinkedHashMap<Long, Long> asOwners = new LinkedHashMap<Long, Long>();

	// /**
	// * persons from note text fields i.e. from list of refNames
	// */
	// public Vector<Long> textReferences = new Vector<Long>();

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
			boolean alsoChild, boolean alsoOther, int tableOffset) {
		StringBuilder sx = new StringBuilder();

		if (alsoChild) {
			for (int i = 0; i < asChildren.size(); i++) {
				if (asChildren.get(i) != table) {
					if (sx.length() > 0)
						sx.append(",");
					sx.append("" + (asChildren.get(i) + tableOffset));
				}
			}
		}

		if (alsoSpouse) {
			for (int i = 0; i < asParents.size(); i++) {
				if (asParents.get(i) != table) {
					if (sx.length() > 0)
						sx.append(",");
					sx.append("" + (asParents.get(i) + tableOffset));
				}
			}
		}
		if (alsoOther) {
			for (int i = 0; i < references.size(); i++) {
				if (references.get(i) != table) {
					if (sx.length() > 0)
						sx.append(",");
					sx.append("" + (references.get(i) + tableOffset));
				}
			}
		}
		return sx.toString();
	}

	/**
	 * 
	 * @return comma separated list of owners
	 */
	public Long[] getOwnerArray() {
		Vector<Long> kk = new Vector<Long>();
		Iterator<Long> ki = asOwners.keySet().iterator();

		while (ki.hasNext()) {
			kk.add(ki.next());
		}

		return kk.toArray(new Long[0]);
	}

	/**
	 * 
	 * @return comma separated list of owners
	 */
	public String getOwnerString() {
		boolean addComma = false;
		StringBuilder sb = new StringBuilder();
		Long[] xx = getOwnerArray();
		for (Long x : xx) {
			if (addComma) {
				sb.append(",");
			}
			addComma = true;
			sb.append("" + x);
		}
		return sb.toString();
	}

	/**
	 * Add owner to index item
	 * 
	 * @param owner
	 */
	public void addOwner(long owner) {
		asOwners.put(owner, owner);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[" + pid + "]:");
		sb.append("chils:(");
		for (int i = 0; i < asChildren.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append("" + asChildren.get(i));
		}
		sb.append(")");
		sb.append(",pars:(");
		for (int i = 0; i < asParents.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append("" + asParents.get(i));
		}
		sb.append(")");
		sb.append(",refs:(");
		for (int i = 0; i < references.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append("" + references.get(i));
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * collator according to language
	 */
	public static Collator fiCollator = Collator.getInstance(new Locale(
			Resurses.getLanguage()));

	@Override
	public int compareTo(PersonInTables o) {
		if (shortPerson == null || o.shortPerson == null)
			return 0;
		int cl = fiCollator.compare(Utils.nv(shortPerson.getSurname()), Utils
				.nv(o.shortPerson.getSurname()));
		if (cl != 0) {
			return cl;
		}
		cl = fiCollator.compare(Utils.nv(shortPerson.getGivenname()), Utils
				.nv(o.shortPerson.getGivenname()));
		if (cl != 0) {
			return cl;
		}
		return (Utils.nv(shortPerson.getBirtDate()).compareTo(Utils
				.nv(o.shortPerson.getBirtDate())));

	}
}
