package fi.kaila.suku.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.dialog.SearchCriteria;
import fi.kaila.suku.swing.dialog.SearchCriteria.ColTable;

/**
 * The Suku table model to display list of persons.
 * 
 * @author FIKAAKAIL
 */
public class SukuModel implements TableModel {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SukuModel.class.getName());

	/** woman icon for database list. */
	public static ImageIcon womanIcon = null;

	/** male icon for database list. */
	public static ImageIcon manIcon = null;

	/** unknown sex icon for database list. */
	public static ImageIcon unknownIcon = null;

	//

	private SearchCriteria crit = null;
	/**
	 * pointers from full column to current column index
	 */
	private Suku suku = null;

	/**
	 * load icons in consrtuctor.
	 * 
	 * @param suku
	 *            the suku
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuModel(Suku suku) throws SukuException {
		this.suku = suku;
		this.crit = SearchCriteria.getCriteria(null);
		byte imbytes[] = new byte[8192];
		int imsize;
		InputStream in = null;
		try {
			if (womanIcon == null) {
				in = this.getClass().getResourceAsStream(
						"/images/womanicon.png");

				imsize = in.read(imbytes);
				if (imsize < imbytes.length) {
					womanIcon = new ImageIcon(imbytes);
				}
				in.close();

				in = this.getClass().getResourceAsStream("/images/manicon.png");
				imsize = in.read(imbytes);
				if (imsize < imbytes.length) {
					manIcon = new ImageIcon(imbytes);
				}
				in.close();

				in = this.getClass().getResourceAsStream(
						"/images/unknownicon.png");
				imsize = in.read(imbytes);
				if (imsize < imbytes.length) {
					unknownIcon = new ImageIcon(imbytes);
				}
			}

			// initModel();

		} catch (IOException e) {
			throw new SukuException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
					// IOException ignored
				}
			}

		}
	}

	/**
	 * The contents of the table is in this vector The vector contains row
	 * vectors
	 */
	private final Vector<SukuRow> tab = new Vector<SukuRow>();

	private SukuRow row;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if (this.tab == null)
			return 0;

		return this.tab.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		SukuRow rw = this.tab.get(getRowCount() - 1);
		return "(" + (getRowCount() - 1) + ") " + rw.toString();
	}

	/**
	 * reset table model.
	 * 
	 */
	public void resetModel() {
		this.tab.clear();
		// = new Vector<SukuRow>();
		// initModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {

		return this.crit.getColumnCount();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int idx) {
		ColTable col = this.crit.getCurrentColTable(idx);

		if (col == null)
			return null;
		// System.out.println("getcolu:[" + idx + "]" + col.getColName());
		return Resurses.getString(col.getColName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int idx) {
		ColTable col = this.crit.getColTable(idx);

		if (idx == 0 && col.getCurrentState()) {
			return womanIcon.getClass();
		}
		return "".getClass();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	/** Pseudo row number that gets the whole SukuRow object. */
	public static final int SUKU_ROW = -1;

	/**
	 * Gets the value at.
	 * 
	 * @param argr
	 *            the argr
	 * @param argc
	 *            the argc
	 * @return the value at
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int argr, int argc) {
		if (argr >= getRowCount())
			return null;
		if (argc == SUKU_ROW) {
			return this.tab.get(argr);
		}
		if (argc >= getColumnCount())
			return null;

		this.row = this.tab.get(argr);
		Object o = this.row.get(argc);
		// if (o != null) {
		// System.out.println("getAt[" + argr + "," + argc + "]:" +
		// o.toString());
		// } else {
		// System.out.println("getAt[" + argr + "," + argc + "]:null" );
		//
		// }
		return o;

	}

	/**
	 * Adds the row.
	 * 
	 * @param row
	 *            the row
	 */
	public void addRow(SukuRow row) {
		this.row = row;
		int nxtRow = this.tab.size();
		this.tab.add(nxtRow, this.row);

	}

	/**
	 * insert row at specified position.
	 * 
	 * @param index
	 *            the index
	 * @param row
	 *            the row
	 */
	public void addRow(int index, SukuRow row) {
		this.row = row;

		this.tab.add(index, this.row);

	}

	/**
	 * remove row from model.
	 * 
	 * @param idx
	 *            the idx
	 */
	public void removeRow(int idx) {
		this.tab.remove(idx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object arg0, int argr, int argc) {

		if (argr < getRowCount()) {
			this.row = this.tab.get(argr);
			this.row.set(argc, arg0);
		} else if (argr == getRowCount()) {
			this.row = new SukuRow(suku);
			this.row.set(argc, arg0);
			this.tab.add(this.row);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableModel#addTableModelListener(javax.swing.event.
	 * TableModelListener)
	 */
	public void addTableModelListener(TableModelListener arg0) {
		// required by interface but not used here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableModel#removeTableModelListener(javax.swing.event
	 * .TableModelListener)
	 */
	public void removeTableModelListener(TableModelListener arg0) {
		// required by interface but not used here
	}

}
