package fi.kaila.suku.util;

/**
 * TODO This will most probably be replaced with something more sphisticated
 * 
 * @author FIKAAKAIL
 * 
 */
public class FamilyParentRelationIndex {
	private int childIdx = 0;
	private int parentIdx = 0;

	/**
	 * @param childIdx
	 * @param parentIdx
	 */
	public FamilyParentRelationIndex(int childIdx, int parentIdx) {
		this.childIdx = childIdx;
		this.parentIdx = parentIdx;
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

}
