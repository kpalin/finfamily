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
 * The Suku table model to display list of persons
 * 
 * @author FIKAAKAIL
 * 
 */
public class SukuModel implements TableModel {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SukuModel.class.getName());

	/** woman icon for database list */
	public static ImageIcon womanIcon = null;
	/** male icon for database list */
	public static ImageIcon manIcon = null;
	/** unknown sex icon for database list */
	public static ImageIcon unknownIcon = null;

	//

	private SearchCriteria crit = null;
	/**
	 * pointers from full column to current column index
	 */
	private Suku suku = null;

	/**
	 * 
	 *load icons in consrtuctor
	 * 
	 * @throws SukuException
	 */
	public SukuModel(Suku suku) throws SukuException {
		this.suku = suku;
		this.crit = SearchCriteria.getCriteria(null);
		byte imbytes[] = new byte[8192];
		int imsize;
		try {
			if (womanIcon == null) {
				InputStream in = this.getClass().getResourceAsStream(
						"/images/womanicon.png");

				imsize = in.read(imbytes);
				if (imsize < imbytes.length) {
					womanIcon = new ImageIcon(imbytes);
				}

				in = this.getClass().getResourceAsStream("/images/manicon.png");
				imsize = in.read(imbytes);
				if (imsize < imbytes.length) {
					manIcon = new ImageIcon(imbytes);
				}
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
		}
	}

	/**
	 * The contents of the table is in this vector The vector contains row
	 * vectors
	 */
	private Vector<SukuRow> tab = new Vector<SukuRow>();

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

	@Override
	public String toString() {
		SukuRow rw = this.tab.get(getRowCount() - 1);
		return "(" + (getRowCount() - 1) + ") " + rw.toString();
	}

	/**
	 * reset table model
	 * 
	 * @throws SukuException
	 */
	public void resetModel() {
		this.tab.removeAllElements();
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

	/**
	 * Pseudo row number that gets the whole SukuRow object
	 */
	public static final int SUKU_ROW = -1;

	/*
	 * (non-Javadoc)
	 * 
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
	 * @param row
	 */
	public void addRow(SukuRow row) {
		this.row = row;
		int nxtRow = this.tab.size();
		this.tab.add(nxtRow, this.row);

	}

	public void removeRow(int idx) {
		this.tab.remove(idx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object arg0, int argr, int argc) {
		// if (argc == SUKU_ROW) {
		// this.tab.remove(argr);
		// this.tab.insertElementAt((SukuRow)arg0,argr);
		//			
		// }
		if (argr < getRowCount()) {
			this.row = this.tab.get(argr);
			this.row.set(argc, arg0);
		} else if (argr == getRowCount()) {
			this.row = new SukuRow(suku);
			this.row.set(argc, arg0);
			this.tab.add(this.row);
			// VRAEmulator.this.table.addNotify();
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
