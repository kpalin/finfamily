package fi.kaila.suku.report;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
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
 * .
 * 
 * @author Kalle
 */
public class PersonInTables implements Comparable<PersonInTables> {

	/** owner pid. */
	public int pid = 0;

	/** name used for index. */
	public PersonShortData shortPerson = null;

	/** references as owner. */
	// public long asOwner = 0;
	public String givenName = null;

	public String surName = null;
	/**
	 * references as child
	 */
	public Vector<Long> asChildren = new Vector<Long>();

	/** references as parents. */
	public Vector<Long> asParents = new Vector<Long>();

	/** other references like spouses parents etc. */
	public Vector<Long> references = new Vector<Long>();

	// private Vector<Long> asOwners = new Vector<Long>();
	private final LinkedHashMap<Long, Long> asOwners = new LinkedHashMap<Long, Long>();

	private long myTable = 0;

	// /**
	// * persons from note text fields i.e. from list of refNames
	// */
	// public Vector<Long> textReferences = new Vector<Long>();

	/**
	 * Constructor.
	 * 
	 * @param pid
	 *            i.e. person id whom this concerns
	 */
	public PersonInTables(int pid) {
		this.pid = pid;
		if (fiCollator == null) {
			collateLangu = Resurses.getLanguage();
			Locale ll = new Locale(collateLangu);
			fiCollator = Collator.getInstance(ll);

		}
	}

	private static String collateLangu = "";

	/**
	 * The report dialog contains a table with the names of the notices
	 * 
	 * <ul>
	 * <li>column 1 contains the name of the notice</li>
	 * <li>column 2 contains settings for main person</li>
	 * <li>column 3 contains settings for person who is a main person elsewere</li>
	 * <li>column 4 contains settings for non relatives</li>
	 * <li>column 5 contains value to be used instead of name in report</li>
	 * </ul>
	 * .
	 * 
	 * @param table
	 *            the table
	 * @param alsoSpouse
	 *            the also spouse
	 * @param alsoChild
	 *            the also child
	 * @param alsoOther
	 *            the also other
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
	 * Get a list of tables where person also exists
	 * 
	 * Checks if tableNo has other references.
	 * 
	 * @param table
	 *            the table
	 * @param alsoSpouse
	 *            the also spouse
	 * @param alsoChild
	 *            the also child
	 * @param alsoOther
	 *            the also other
	 * @param tableOffset
	 *            the table offset
	 * @return a comma separated list of tables
	 */
	public String getReferences(long table, boolean alsoSpouse,
			boolean alsoChild, boolean alsoOther, int tableOffset) {
		StringBuilder sx = new StringBuilder();

		HashMap<Long, Long> icheck = new HashMap<Long, Long>();
		if (alsoChild) {
			for (int i = 0; i < asChildren.size(); i++) {
				long ll = asChildren.get(i);
				Long lll = icheck.put(ll, ll);
				if (lll == null) {
					if (asChildren.get(i) != table) {
						if (sx.length() > 0)
							sx.append(",");
						sx.append((asChildren.get(i) + tableOffset));
					}
				}
			}
		}

		if (alsoSpouse) {
			for (int i = 0; i < asParents.size(); i++) {
				long ll = asParents.get(i);
				Long lll = icheck.put(ll, ll);
				if (lll == null) {

					if (asParents.get(i) != table) {
						if (sx.length() > 0)
							sx.append(",");
						sx.append((asParents.get(i) + tableOffset));
					}
				}
			}
		}
		if (alsoOther) {
			for (int i = 0; i < references.size(); i++) {
				long ll = references.get(i);
				Long lll = icheck.put(ll, ll);
				if (lll == null) {
					if (references.get(i) != table) {
						long lxl = references.get(i);
						int j = 0;
						for (j = 0; j < asParents.size(); j++) {
							if (asParents.get(j) != null) {
								break;
							}
						}
						if (j == asParents.size()) {
							if (sx.length() > 0) {
								sx.append(",");
							}
							sx.append((lxl + tableOffset));
						}
					}
				}
			}
		}
		return sx.toString();
	}

	/**
	 * Gets the owner array.
	 * 
	 * @return comma separated list of owners
	 */
	public Long[] getOwnerArray() {
		ArrayList<Long> kk = new ArrayList<Long>();
		Iterator<Long> ki = asOwners.keySet().iterator();

		while (ki.hasNext()) {
			kk.add(ki.next());
		}

		return kk.toArray(new Long[0]);
	}

	/**
	 * Gets the owner string.
	 * 
	 * @return comma separated list of owners
	 */
	public String getOwnerString(long tableOffset) {
		boolean addComma = false;
		StringBuilder sb = new StringBuilder();
		Long[] xx = getOwnerArray();
		for (Long x : xx) {
			if (addComma) {
				sb.append(",");
			}
			addComma = true;
			sb.append(x + tableOffset);
		}
		return sb.toString();
	}

	/**
	 * Add owner to index item.
	 * 
	 * @param owner
	 *            the owner
	 */
	public void addOwner(long owner) {
		asOwners.put(owner, owner);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
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
		sb.append(")\n");
		return sb.toString();
	}

	/** collator according to language. */
	public static Collator fiCollator = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PersonInTables o) {
		if (shortPerson == null || o.shortPerson == null)
			return 0;

		int cl = compareFF(Utils.nv(shortPerson.getSurname()),
				Utils.nv(o.shortPerson.getSurname()));
		if (cl != 0) {
			return cl;
		}

		cl = compareFF(Utils.nv(shortPerson.getGivenname()),
				Utils.nv(o.shortPerson.getGivenname()));

		if (cl != 0) {
			return cl;
		}
		cl = (Utils.nv(shortPerson.getBirtDate()).compareTo(Utils
				.nv(o.shortPerson.getBirtDate())));

		if (cl != 0) {
			return cl;
		}

		if (shortPerson.getPid() < o.shortPerson.getPid()) {
			return -1;
		} else if (shortPerson.getPid() > o.shortPerson.getPid()) {
			return 1;
		} else {
			return 0;
		}

	}

	private int compareFF(String uno, String duo) {

		if (uno == null || duo == null)
			return 0;

		String nuno = uno.trim().replace(' ', '!');
		String nduo = duo.trim().replace(' ', '!');
		if (collateLangu.equals("fi") || collateLangu.equals("sv")) {

			nuno = collatable(nuno.toLowerCase());
			nduo = collatable(nduo.toLowerCase());
		}

		return fiCollator.compare(nuno, nduo);

	}

	private String collatable(String origin) {
		if (origin == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < origin.length(); i++) {
			char c = origin.charAt(i);

			if ("àáâãāăăă".indexOf(c) >= 0) {
				sb.append('a');
			} else if ("ç".indexOf(c) >= 0) {
				sb.append('c');
			} else if ("èéêë".indexOf(c) >= 0) {
				sb.append('e');
			} else if ("ìíîï".indexOf(c) >= 0) {
				sb.append('i');
			} else if ("ñ".indexOf(c) >= 0) {
				sb.append('n');
			} else if ("òóôõ".indexOf(c) >= 0) {
				sb.append('o');
			} else if ("ùúûü".indexOf(c) >= 0) {
				sb.append('u');
			} else if ("ýÿ".indexOf(c) >= 0) {
				sb.append('y');
			} else if (c == 'w') {
				sb.append('v');

			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * @param myTable
	 *            the myTable to set
	 */
	public void setMyTable(long myTable) {
		if (this.myTable == 0) {
			this.myTable = myTable;
		} else {
			System.out.println("Tries to set table for " + this.pid + " to "
					+ myTable);
		}
	}

	/**
	 * @return the myTable
	 */
	public long getMyTable() {
		return myTable;
	}
}
