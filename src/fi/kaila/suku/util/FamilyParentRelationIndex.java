package fi.kaila.suku.util;

import fi.kaila.suku.util.pojo.RelationShortData;

/**
 * TODO This will most probably be replaced with something more sophisticated.
 * 
 * @author FIKAAKAIL
 */
public class FamilyParentRelationIndex {
	private int childIdx = 0;
	private int parentIdx = 0;
	private RelationShortData rela = null;

	/**
	 * Instantiates a new family parent relation index.
	 * 
	 * @param childIdx
	 *            the child idx
	 * @param parentIdx
	 *            the parent idx
	 * @param rela
	 *            the rela
	 */
	public FamilyParentRelationIndex(int childIdx, int parentIdx,
			RelationShortData rela) {
		this.childIdx = childIdx;
		this.parentIdx = parentIdx;
		this.rela = rela;
	}

	/**
	 * Gets the child idx.
	 * 
	 * @return child index
	 */
	public int getChildIdx() {
		return childIdx;
	}

	/**
	 * Gets the parent idx.
	 * 
	 * @return parent index
	 */
	public int getParentIdx() {
		return parentIdx;
	}

	/**
	 * Gets the surety.
	 * 
	 * @return the surety
	 */
	public int getSurety() {
		if (rela != null) {
			return rela.getSurety();
		}
		return 0;
	}

}
