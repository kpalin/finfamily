package fi.kaila.suku.util;

import fi.kaila.suku.util.pojo.RelationShortData;

/**
 * TODO This will most probably be replaced with something more sophisticated
 * 
 * @author FIKAAKAIL
 * 
 */
public class FamilyParentRelationIndex {
	private int childIdx = 0;
	private int parentIdx = 0;
	private RelationShortData rela = null;

	/**
	 * @param childIdx
	 * @param parentIdx
	 */
	public FamilyParentRelationIndex(int childIdx, int parentIdx,
			RelationShortData rela) {
		this.childIdx = childIdx;
		this.parentIdx = parentIdx;
		this.rela = rela;
	}

	/**
	 * @return child index
	 */
	public int getChildIdx() {
		return childIdx;
	}

	/**
	 * @return parent index
	 */
	public int getParentIdx() {
		return parentIdx;
	}

	public int getSurety() {
		if (rela != null) {
			return rela.getSurety();
		}
		return 0;
	}

}
