package fi.kaila.suku.util;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Component contains the table for the notices types
 * 
 * @author Kalle
 * 
 */
public class SukuTypesTable extends JTable {

	/**  */
	private static final long serialVersionUID = 1L;

	SukuTypesModel model = null;

	// /**
	// * using default Dimension (500,70)
	// */
	// public SukuTypesTable() {
	// Dimension dim = new Dimension(500, 70);
	// initme(dim);
	// }

	/**
	 * @param dim
	 *            dimension of Preferred Scriollable Viewport Size
	 * 
	 */
	public SukuTypesTable(Dimension dim) {
		initme(dim);

	}

	private void initme(Dimension dim) {
		model = Utils.typeInstance();
		this.setModel(model);
		setPreferredScrollableViewportSize(new Dimension(500, 70));
		setFillsViewportHeight(true);
	}

	// Implement table header tool tips.

	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(this.columnModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent e) {

				java.awt.Point p = e.getPoint();
				int index = this.columnModel.getColumnIndexAtX(p.x);
				String tip = Resurses.getString("TYPES_COLUMN_" + index);
				return tip;
			}
		};
	}

	/**
	 * Gets the tag at the indicated index position
	 * 
	 * @param idx
	 * 
	 * @return the indexed tag
	 */
	public String getTypesTag(int idx) {
		return model.getTypesTags(idx);
	}

	/**
	 * Get report value for tag
	 * 
	 * @param tag
	 * @return name of type
	 */
	public String getTypeText(String tag) {
		Integer iidx = model.getTypeText(tag);
		if (iidx == null)
			return tag;
		int idx = iidx.intValue();
		if (idx >= 0) {
			String txt = (String) model.getTypesData(idx, 5);
			if (txt != null) {
				return txt;
			}
			// if (idx < typesModel.getTypesData().length) {
			// return (String) typesModel.getTypesData()[idx][5];
			// }
			return (String) model.getTypesValue(idx);
		}
		return null;
	}

	/**
	 * @param type
	 * @return rule for requested type
	 */
	public String getTypeRule(String type) {
		String rule = model.getTypeRule(type);
		return rule;

	}

	/**
	 * @param tag
	 * @return name of tag e.g. BIRT tag returns Birth in English
	 */
	public String getTagName(String tag) {
		Integer iidx = model.getTypeText(tag);
		if (iidx == null)
			return tag;
		int idx = iidx.intValue();
		if (idx >= 0) {
			String txt = model.getTypesName(idx);
			if (txt != null) {
				return txt;
			}
			// if (idx < typesModel.getTypesData().length) {
			// return (String) typesModel.getTypesData()[idx][0];
			// }
			return (String) model.getTypesValues()[idx];
		}
		return null;
	}

	/**
	 * Get report value for tag
	 * 
	 * @param tag
	 * @return value of tag
	 */
	public String getTextValue(String tag) {
		String value = model.getTextText(tag);
		if (value == null)
			return tag;
		return value;
	}

	/**
	 * get state of setting for tag
	 * 
	 * @param tag
	 * @param col
	 *            column in table. 1 = name, 2 = main, 3 = child, 4 = sub
	 * @return true if settings is on
	 */
	public boolean isType(String tag, int col) {
		if (col < 1 || col > 4)
			return false;
		Integer idxInt = model.getTypeText(tag);
		if (idxInt == null)
			return true;
		int idx = idxInt.intValue();
		if (idx >= 0) {

			Boolean value = (Boolean) model.getTypesData(idx, col);
			if (value != null) {
				return value;
			}

		}
		return false;

	}

	/**
	 * @param type
	 *            name of settingtype (e.g. reporttypes)
	 * @param settingsIndex
	 *            the settings index should be between 0-10
	 */
	public void saveReportSettings(String type, int settingsIndex) {

		Suku.kontroller.putPref(this, Resurses.SETTING_IDX, "" + settingsIndex);

		Vector<String> v = new Vector<String>();

		v.add("cmd=savesettings");
		v.add("type=" + type);
		v.add("index=" + settingsIndex);
		int typeCount = getRowCount();

		for (int row = 0; row < typeCount; row++) {
			StringBuilder sb = new StringBuilder();
			sb.append(getTypesTag(row));
			sb.append("=");
			sb.append(((Boolean) getValueAt(row, 1)) ? "X" : "O");
			sb.append(((Boolean) getValueAt(row, 2)) ? "X" : "O");
			sb.append(((Boolean) getValueAt(row, 3)) ? "X" : "O");
			sb.append(((Boolean) getValueAt(row, 4)) ? "X" : "O");
			sb.append(getValueAt(row, 5));
			v.add(sb.toString());

		}

		try {
			SukuData reposet = Suku.kontroller.getSukuData(v
					.toArray(new String[0]));
			if (reposet.resu != null && reposet.resu != Resurses.OK) {
				JOptionPane.showMessageDialog(this, reposet.resu, Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			}

		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	/**
	 * @param type
	 *            of setting
	 * @param settingsIndex
	 *            index betrween 0-10
	 */
	public void loadReportSettings(String type, int settingsIndex) {

		try {
			SukuData sets = Suku.kontroller.getSukuData("cmd=getsettings",
					"type=" + type, "index=" + settingsIndex);
			String vx[] = null;

			for (int i = 0; i < sets.vvTypes.size(); i++) {

				vx = sets.vvTypes.get(i);

				int typeCount = getRowCount();
				for (int row = 0; row < typeCount; row++) {

					String tag = getTypesTag(row);

					if (vx[0].equals(tag) && vx[1].length() > 3) {
						boolean b = false;
						if (vx[1].substring(0, 1).equals("X")) {
							b = true;
						}
						setValueAt(b, row, 1);
						b = false;
						if (vx[1].substring(1, 2).equals("X")) {
							b = true;
						}
						setValueAt(b, row, 2);
						b = false;
						if (vx[1].substring(2, 3).equals("X")) {
							b = true;
						}
						setValueAt(b, row, 3);

						b = false;
						if (vx[1].substring(3, 4).equals("X")) {
							b = true;
						}
						setValueAt(b, row, 4);
						// String newText = "";
						// if (vx[1].length() > 4) {
						// newText = vx[1].substring(4);
						// }
						// typesTable.setValueAt(newText, row, 5);
						break;
					}
				}
			}

		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, "error fetching setting "
					+ settingsIndex + ": " + e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
		}

	}

}
