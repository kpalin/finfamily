package fi.kaila.suku.util;

import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * @author Kalle
 * 
 *         Model for Types table use.
 */
public class SukuTypesModel extends AbstractTableModel {

	private String[] typesTags = null;
	private String[] typesValues = null;
	private Object[][] typesData = null;
	private HashMap<String, Integer> typeTexts = new HashMap<String, Integer>();
	private HashMap<String, String> typeRule = new HashMap<String, String>();
	private HashMap<String, String> textTexts = new HashMap<String, String>();

	SukuTypesModel() {

		try {
			SukuData reposet = Suku.kontroller.getSukuData("cmd=get",
					"type=types", "lang=" + Resurses.getLanguage());

			typesValues = new String[reposet.vvTypes.size()];
			for (int i = 0; i < reposet.vvTypes.size(); i++) {
				String rrr[] = reposet.vvTypes.get(i);
				String tag = rrr[0];
				typeTexts.put(tag, i);

				typesValues[i] = rrr[1];
				String rule = rrr[4];
				if (rule != null) {
					typeRule.put(tag, rule);
				}

			}

			typesData = new Object[reposet.vvTypes.size()][6];
			typesTags = new String[reposet.vvTypes.size()];

			for (int i = 0; i < typesTags.length; i++) {
				String tag = reposet.vvTypes.get(i)[0];
				typesTags[i] = tag;
				typesData[i][0] = reposet.vvTypes.get(i)[1];
				typesData[i][1] = Boolean.valueOf(false);
				if ("|BIRT|DEAT|CHR|BURI|NAME|".indexOf(tag) > 0) {
					typesData[i][1] = Boolean.valueOf(true);
				}
				typesData[i][2] = Boolean.valueOf(true);
				typesData[i][3] = Boolean.valueOf(false);
				typesData[i][4] = Boolean.valueOf(false);
				if ("|BIRT|DEAT|OCCU|".indexOf(tag) > 0) {
					typesData[i][3] = Boolean.valueOf(true);
					typesData[i][4] = Boolean.valueOf(true);
				}
				typesData[i][5] = reposet.vvTypes.get(i)[2];

				if (typesData[i][5] == null) {
					typesData[i][5] = reposet.vvTypes.get(i)[1];
				}
			}

			for (int i = 0; i < reposet.vvTexts.size(); i++) {
				String tag = reposet.vvTexts.get(i)[0];
				String value = reposet.vvTexts.get(i)[1];
				if (value == null)
					value = "";
				textTexts.put(tag, value);
			}

		} catch (SukuException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
		}

	}

	private static final long serialVersionUID = 1L;

	private String[] columnNames = { "Tietojakso", "Nimi", "Päähenkilö",
			"Lapsi", "Muu", "Teksti" };

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return typesData.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return typesData[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		if (col < 1) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {

		typesData[row][col] = value;
		fireTableCellUpdated(row, col);

	}

	/**
	 * @param idx
	 * @return tag at idx
	 */
	public String getTypesTags(int idx) {
		return typesTags[idx];
	}

	/**
	 * @return # of tags
	 */
	public int getTypesTagsCount() {
		return typesTags.length;
	}

	/**
	 * @param tag
	 * @return the typeTexts
	 */
	public Integer getTypeText(String tag) {
		return typeTexts.get(tag);

	}

	/**
	 * @param row
	 * @param col
	 * @return contents of cell
	 */
	public Object getTypesData(int row, int col) {
		if (row < typesData.length) {
			return typesData[row][col];
		}
		return null;
	}

	/**
	 * @param row
	 * @return name of tag
	 */
	public String getTypesName(int row) {
		if (row < typesData.length) {
			return (String) typesData[row][0];
		}
		return null;
	}

	/**
	 * @return the typesValues
	 */
	public String[] getTypesValues() {
		return typesValues;
	}

	/**
	 * @param idx
	 * @return the tag
	 */
	public String getTypesTag(int idx) {
		return typesTags[idx];
	}

	/**
	 * @param idx
	 * @return theg text portion
	 */
	public String getTypesValue(int idx) {
		return typesValues[idx];
	}

	/**
	 * @param tag
	 * @return the textTexts
	 */
	public String getTextText(String tag) {
		String value = textTexts.get(tag);
		if (value == null)
			return tag;
		return value;
	}

	/**
	 * @param type
	 * @return the typeRule
	 */
	public String getTypeRule(String type) {
		return typeRule.get(type);

	}

}
