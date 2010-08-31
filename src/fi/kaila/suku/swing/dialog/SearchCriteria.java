package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.jbundle.thin.base.screen.jcalendarbutton.JCalendarButton;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.util.SukuSuretyField;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesModel;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Window that is shown before database search is made Windows contains the
 * search criterias used in the search and columns to display
 * 
 * @author FIKAAKAIL
 * 
 */
public class SearchCriteria extends JDialog implements ActionListener {

	private static Logger logger = Logger.getLogger(SearchCriteria.class
			.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField surname;
	private JTextField givenname;
	private JTextField patronyme;

	private JTextField birtFromDate;
	private JTextField birtToDate;
	private JTextField birtPlace;

	private JTextField deatFromDate;
	private JTextField deatToDate;
	private JTextField deatPlace;

	private DateTextField createdFromDate;

	private DateTextField createdToDate;

	private JComboBox viewList;
	private String[] viewArray = null;
	private JTextField viewGroup;
	private String preferredView = null;

	private JTextField place;
	private JComboBox noticeList;
	private JCheckBox noticeExist;
	private JComboBox sex;
	private SukuSuretyField surety;
	private JTextField fullText;

	JPanel colpanel;

	JPanel namePanel;
	JPanel birtPanel;
	JPanel deatPanel;
	JPanel createdPanel;
	JPanel viewPanel;
	JPanel placePanel;
	JPanel sexPanel;
	JPanel textPanel;

	private JButton ok;
	private JButton reset;

	private char[] sexcodes = { 0, 'M', 'F', 'U' };

	private ColTable[] coltables = { new ColTable(Resurses.COLUMN_T_SEX, true),
			new ColTable(Resurses.COLUMN_T_ISMARR, true),
			new ColTable(Resurses.COLUMN_T_ISCHILD, true),
			new ColTable(Resurses.COLUMN_T_ISPARE, true),
			new ColTable(Resurses.COLUMN_T_TODO, true),
			new ColTable(Resurses.COLUMN_T_NAME, true),
			new ColTable(Resurses.COLUMN_T_PATRONYME, true),
			new ColTable(Resurses.COLUMN_T_BIRT, true),
			new ColTable(Resurses.COLUMN_T_BIRTPLACE, true),
			new ColTable(Resurses.COLUMN_T_DEAT, true),
			new ColTable(Resurses.COLUMN_T_DEATPLACE, true),
			new ColTable(Resurses.COLUMN_T_OCCUPATION, true),
			new ColTable(Resurses.COLUMN_T_GROUP, false),
			new ColTable(Resurses.COLUMN_T_REFN, false),
			new ColTable(Resurses.COLUMN_T_PID, false) };

	private ColTable[] proptables = {
			new ColTable(Resurses.COLUMN_T_ALL_NAMES, true),
			new ColTable(Resurses.COLUMN_T_BIRT_CHR, true),
			new ColTable(Resurses.COLUMN_T_DEAT_BURI, true) };

	/**
	 * 
	 * @param colName
	 * @return get full column index for column name tag
	 */
	public int getColIndex(String colName) {
		int i;

		for (i = 0; i < this.coltables.length; i++) {
			if (colName.equals(this.coltables[i].colname)) {
				// System.out.println("colidx:" + colName + "["+i+"]");
				return i;
			}
		}
		return -1;

	}

	public int getModelIndex(int viewIdx) {
		int compa = -1;
		for (int i = 0; i < coltables.length; i++) {
			if (coltables[i].getCurrentState()) {
				compa++;
			}
			if (compa == viewIdx) {
				return i;
			}

		}
		return -1;

	}

	public int getViewIndex(int modelIdx) {
		int compa = -1;
		for (int i = 0; i < coltables.length; i++) {

			if (coltables[i].getCurrentState()) {
				compa++;
			}
			if (modelIdx == i) {
				return compa;
			}
		}
		return -1;

	}

	private int getPropIndex(String colName) {
		int i;
		for (i = 0; i < this.proptables.length; i++) {
			if (colName.equals(this.proptables[i].colname)) {

				return i;
			}
		}
		return -1;

	}

	/**
	 * get index of named column
	 * 
	 * @param colname
	 * @return column index
	 */
	public int getCurrentIndex(String colname) {
		int i;
		int resu = 0;
		String nm;
		for (i = 0; i < coltables.length; i++) {
			if (coltables[i].getCurrentState()) {
				resu++;
			}
			nm = coltables[i].getColName();

			if (nm.equals(colname)) {
				return resu;
			}

		}
		return -1;
	}

	/**
	 * @param idx
	 * @return name of indexed column
	 */
	public String getColName(int idx) {
		return this.coltables[idx].getColName();
	}

	// /**
	// *
	// * @param idx
	// * @return absolute SukuModel column index
	// */
	// public int getColAbsIndex(int idx) {
	// int index = 0;
	//
	// for (int i = 0; i < this.coltables.length; i++) {
	// if (this.coltables[i].currState) {
	//
	// if (i >= idx) {
	// return index;
	// }
	// index++;
	// }
	// }
	//
	// return index;
	// }

	/**
	 * @param prop
	 *            property name
	 * @return true if all names is set for pro
	 */
	public boolean isPropertySet(String prop) {
		int col = getPropIndex(prop);

		return this.proptables[col].currState;
	}

	private static SearchCriteria myself = null;

	/**
	 * @param owner
	 * @return Singleton instance of SearchCriteria
	 * @throws SukuException
	 */
	public static SearchCriteria getCriteria(JFrame owner) throws SukuException {
		Object o = new Object();

		synchronized (o) {
			if (myself == null) {
				if (owner == null) {
					throw new SukuException(Resurses
							.getString(Resurses.CRITERIA_INIT_ERROR));
				}
				myself = new SearchCriteria(owner);
			}
		}
		return myself;

	}

	SearchCriteria me = null;

	/**
	 * @param owner
	 *            of dialog
	 */
	private SearchCriteria(JFrame owner) {
		super(owner, Resurses.getString(Resurses.CRITERIA_CAPTION), true);
		me = this;
		setLayout(null);
		int y = 20;
		int idx;
		ColTable tbl;
		JLabel lbl;
		Border bvlr = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		Border bvll = BorderFactory.createBevelBorder(BevelBorder.LOWERED);

		Border bvl = BorderFactory.createCompoundBorder(bvlr, bvll);
		this.colpanel = new JPanel(new GridLayout(0, 1));
		getContentPane().add(this.colpanel);
		this.colpanel.setBounds(700, y + 20, 150, 300);
		this.colpanel.setBorder(bvl);

		for (idx = 0; idx < this.coltables.length; idx++) {
			tbl = this.coltables[idx];

			boolean bb = true;
			if (idx == 0 || idx == 5) {
				tbl.getChkBox().setSelected(true);
				tbl.getChkBox().setVisible(false);
				tbl.setCurrentState(true);
			} else {
				this.colpanel.add(tbl.getChkBox());
				bb = Utils.getBooleanPref(this, tbl.getColName(), tbl
						.getCurrentState());
				// System.out.println("BB on " + bb + "/" + idx + "/" +
				// tbl.getColName() + "/" + tbl.getCurrentState());

				tbl.getChkBox().setSelected(bb);
				tbl.setCurrentState(true);
				// System.out.println("ContructColtable:" + idx + "=" + bb);
			}

		}

		this.colpanel.add(new JSeparator());

		for (idx = 0; idx < this.proptables.length; idx++) {
			tbl = this.proptables[idx];
			this.colpanel.add(tbl.getChkBox());
			boolean bb = Utils.getBooleanPref(this, tbl.getColName(), tbl
					.getCurrentState());
			tbl.getChkBox().setSelected(bb);
			tbl.setCurrentState(bb);
		}

		TitledBorder tit = BorderFactory.createTitledBorder(bvl, Resurses
				.getString(Resurses.CRITERIA_NAME));

		this.namePanel = new JPanel(new GridLayout(2, 0, 10, 10));
		getContentPane().add(this.namePanel);
		this.namePanel.setBounds(20, y, 600, 80);
		this.namePanel.setBorder(tit);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_SURNAME));
		this.namePanel.add(lbl);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_GIVENNAME));
		this.namePanel.add(lbl);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_PATRONYME));
		this.namePanel.add(lbl);

		// this.surname = new SukuTextField(null, Field.Fld_Surname);
		this.surname = new JTextField();
		this.namePanel.add(this.surname);

		// this.givenname = new SukuTextField(null, Field.Fld_Givenname);
		this.givenname = new JTextField();
		this.namePanel.add(this.givenname);

		// this.patronyme = new SukuTextField(null, Field.Fld_Patronyme);
		this.patronyme = new JTextField();
		this.namePanel.add(this.patronyme);

		y += 80;

		this.birtPanel = new JPanel(new GridLayout(2, 3, 10, 10));
		getContentPane().add(this.birtPanel);
		this.birtPanel.setBounds(20, y, 600, 80);

		tit = BorderFactory.createTitledBorder(bvl, Resurses
				.getString(Resurses.CRITERIA_BIRT));

		this.birtPanel.setBorder(tit);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_BIRT_FROM));
		this.birtPanel.add(lbl);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_BIRT_TO));
		this.birtPanel.add(lbl);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_BIRT_PLACE));
		this.birtPanel.add(lbl);

		this.birtFromDate = new JTextField();
		this.birtPanel.add(this.birtFromDate);
		this.birtToDate = new JTextField();
		this.birtPanel.add(this.birtToDate);
		// this.birtPlace = new SukuTextField(null, Field.Fld_Place);
		this.birtPlace = new JTextField();
		this.birtPanel.add(this.birtPlace);

		y += 80;

		tit = BorderFactory.createTitledBorder(bvl, Resurses
				.getString(Resurses.CRITERIA_DEAT));

		this.deatPanel = new JPanel(new GridLayout(2, 3, 10, 10));
		getContentPane().add(this.deatPanel);
		this.deatPanel.setBounds(20, y, 600, 80);
		this.deatPanel.setBorder(tit);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_DEAT_FROM));
		this.deatPanel.add(lbl);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_DEAT_TO));
		this.deatPanel.add(lbl);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_DEAT_PLACE));
		this.deatPanel.add(lbl);

		this.deatFromDate = new JTextField();
		this.deatPanel.add(this.deatFromDate);
		this.deatToDate = new JTextField();
		this.deatPanel.add(this.deatToDate);
		// this.deatPlace = new SukuTextField(null, Field.Fld_Place);
		this.deatPlace = new JTextField();
		this.deatPanel.add(this.deatPlace);

		y += 80;

		tit = BorderFactory.createTitledBorder(bvl, Resurses
				.getString(Resurses.CRITERIA_CREATED));

		this.createdPanel = new JPanel(new GridLayout(2, 3, 2, 2));
		getContentPane().add(this.createdPanel);
		this.createdPanel.setBounds(20, y, 400, 80);
		this.createdPanel.setBorder(tit);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_CREATED_FROM));
		this.createdPanel.add(lbl);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_CREATED_TO));
		this.createdPanel.add(lbl);

		this.createdFromDate = new DateTextField();
		this.createdPanel.add(this.createdFromDate);
		this.createdToDate = new DateTextField();
		this.createdPanel.add(this.createdToDate);

		tit = BorderFactory.createTitledBorder(bvl, Resurses
				.getString(Resurses.CRITERIA_VIEW));

		this.viewPanel = new JPanel(new GridLayout(2, 4, 10, 10));
		getContentPane().add(this.viewPanel);
		this.viewPanel.setBounds(420, y, 200, 80);
		this.viewPanel.setBorder(tit);

		this.viewList = new JComboBox();
		this.viewPanel.add(this.viewList);

		// viewGroup = new SukuTextField(null, Field.Fld_Group);
		this.viewGroup = new JTextField();
		this.viewPanel.add(viewGroup);

		y += 80;

		tit = BorderFactory.createTitledBorder(bvl, Resurses
				.getString("CRITERIA_PLACE_NOTICE"));

		placePanel = new JPanel(new GridLayout(2, 3, 10, 10));
		getContentPane().add(this.placePanel);
		placePanel.setBounds(20, y, 400, 80);
		placePanel.setBorder(tit);

		lbl = new JLabel(Resurses.getString(Resurses.CRITERIA_PLACE));
		placePanel.add(lbl);

		noticeExist = new JCheckBox(Resurses
				.getString(Resurses.CRITERIA_NOTICE_MISSING));
		placePanel.add(noticeExist);

		place = new JTextField();
		placePanel.add(place);

		noticeList = new JComboBox();
		placePanel.add(noticeList);

		tit = BorderFactory.createTitledBorder(bvl, Resurses
				.getString("CRITERIA_SEX_SURETY"));

		sexPanel = new JPanel(new GridLayout(2, 0, 10, 10));
		getContentPane().add(sexPanel);
		sexPanel.setBounds(420, y, 200, 80);
		sexPanel.setBorder(tit);

		surety = new SukuSuretyField();
		sexPanel.add(surety);

		String sexes[] = new String[4];
		sexes[0] = "";
		sexes[1] = Resurses.getString("SEX_" + sexcodes[1]);
		sexes[2] = Resurses.getString("SEX_" + sexcodes[2]);
		sexes[3] = Resurses.getString("SEX_" + sexcodes[3]);
		sex = new JComboBox(sexes);
		sexPanel.add(sex);

		y += 80;

		tit = BorderFactory.createTitledBorder(bvl, Resurses
				.getString(Resurses.CRITERIA_FULL_TEXT));

		textPanel = new JPanel(new GridLayout(2, 1, 10, 10));
		getContentPane().add(textPanel);
		textPanel.setBounds(20, y, 400, 80);
		textPanel.setBorder(tit);

		lbl = new JLabel(Resurses.getString("CRITERIA_FILL_TEXT_DESCRIPTION"));
		textPanel.add(lbl);

		fullText = new JTextField();
		textPanel.add(fullText);

		y = 460;

		this.ok = new JButton(Resurses.OK);
		// this.ok.setDefaultCapable(true);
		getContentPane().add(this.ok);
		this.ok.setActionCommand(Resurses.OK);
		this.ok.addActionListener(this);
		this.ok.setBounds(560, y, 140, 24);

		this.reset = new JButton(Resurses.getString(Resurses.RESET));
		// this.ok.setDefaultCapable(true);
		getContentPane().add(this.reset);
		this.reset.setActionCommand(Resurses.RESET);
		this.reset.addActionListener(this);
		this.reset.setBounds(710, y, 140, 24);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width / 2 - 450, d.height / 2 - 300, 900, y + 100);
		getRootPane().setDefaultButton(this.ok);
	}

	/**
	 * @param idx
	 * @return column at idx
	 */
	public ColTable getColTable(int idx) {
		return this.coltables[idx];
	}

	/**
	 * @return count of columns
	 */
	public int getColTableCount() {
		return this.coltables.length;
	}

	/**
	 * populate combobox lists
	 * 
	 */
	public void populateFields() {

		try {
			SukuTypesModel typesModel = Utils.typeInstance();
			noticeList.removeAllItems();
			// String[] names = new String[typesModel.getTypesTagsCount()];
			noticeList.addItem("");
			for (int i = 0; i < typesModel.getTypesTagsCount(); i++) {
				noticeList.addItem(typesModel.getTypesName(i));
			}

			SukuData sets = Suku.kontroller.getSukuData("cmd=getsettings",
					"type=query");
			resetArguments();
			int preferredVid = 0;
			int preferredIndex = -1;
			viewList.removeAllItems();
			for (int i = 0; i < sets.generalArray.length; i++) {

				String[] parts = sets.generalArray[i].split("=");
				if (parts.length == 2) {
					if (parts[0].equals("patronyme")) {
						patronyme.setText(parts[1]);
					} else if (parts[0].equals("surname")) {
						surname.setText(parts[1]);
					} else if (parts[0].equals("givenname")) {
						givenname.setText(parts[1]);

					} else if (parts[0].equals("birtFromDate")) {
						birtFromDate.setText(Utils.textDate(parts[1], false));

					} else if (parts[0].equals("birtToDate")) {
						birtToDate.setText(Utils.textDate(parts[1], false));

					} else if (parts[0].equals("birtPlace")) {
						birtPlace.setText(parts[1]);

					} else if (parts[0].equals("deatFromDate")) {
						deatFromDate.setText(Utils.textDate(parts[1], false));

					} else if (parts[0].equals("deatToDate")) {
						deatToDate.setText(Utils.textDate(parts[1], false));

					} else if (parts[0].equals("deatPlace")) {
						deatPlace.setText(parts[1]);

					} else if (parts[0].equals("createdFromDate")) {
						createdFromDate.setText(parts[1]);
					} else if (parts[0].equals("createdToDate")) {
						createdToDate.setText(parts[1]);
					} else if (parts[0].equals("viewGroup")) {
						viewGroup.setText(parts[1]);
					} else if (parts[0].equals("viewId")) {

						String[] vnum = null;

						if (preferredView != null) {
							vnum = parts[1].split(";");
							try {
								preferredVid = Integer.parseInt(vnum[0]);

							} catch (NumberFormatException ne) {
							}

						}

					} else if (parts[0].equals("place")) {
						place.setText(parts[1]);
					} else if (parts[0].equals("notice")) {
						SukuTypesModel model = Utils.typeInstance();
						int indx = 0;
						for (int j = 0; j < model.getTypesTagsCount(); j++) {
							if (parts[1].equals(model.getTypesTag(j))) {
								indx = j + 1;
							}
						}
						if (indx < noticeList.getItemCount()) {
							noticeList.setSelectedIndex(indx);
						}

					} else if (parts[0].equals("noticeExists")) {
						noticeExist.setSelected(true);
					} else if (parts[0].equals("surety")) {
						int suretyValue = 100;
						try {
							suretyValue = Integer.parseInt(parts[1]);
						} catch (NumberFormatException ne) {
						}
						surety.setSurety(suretyValue);

					} else if (parts[0].equals("sex")) {
						int sexIndex = 0;
						char s = parts[1].charAt(0);
						for (int j = 0; j < sexcodes.length; j++) {
							if (s == sexcodes[j]) {
								sexIndex = j;
								break;
							}
						}

						sex.setSelectedIndex(sexIndex);
					} else if (parts[0].equals("fullText")) {
						fullText.setText(parts[1]);
					}

				}

			}

			SukuData vlist = Suku.kontroller.getSukuData("cmd=viewlist");
			String[] lista = vlist.generalArray;
			viewList.removeAllItems();
			viewArray = lista;
			viewList.addItem("");
			for (int i = 0; i < viewArray.length; i++) {
				String[] pp = viewArray[i].split(";");
				if (pp.length > 1) {
					int vid = 0;
					try {
						vid = Integer.parseInt(pp[0]);
					} catch (NumberFormatException ne) {
					}
					if (vid == preferredVid) {
						preferredIndex = i;
					}

					viewList.addItem(pp[1]);
				}

			}

			if (preferredIndex >= 0
					&& preferredIndex < viewList.getItemCount() - 1) {
				viewList.setSelectedIndex(preferredIndex + 1);
			}

		} catch (SukuException e) {
			//
		}
	}

	private int getViewId() {
		if (viewArray == null || viewArray.length == 0) {
			return 0;
		}
		int jidx = this.viewList.getSelectedIndex();
		if (jidx > 0 && jidx <= viewArray.length) {
			String aux = viewArray[jidx - 1];
			int ppidx = aux.indexOf(';');
			if (ppidx > 0) {
				return Integer.parseInt(aux.substring(0, ppidx));
			}
		}
		return 0;

	}

	/**
	 * @return true if person has any relatives
	 */
	public boolean hasRelativeInfo() {

		if (this.coltables[getColIndex(Resurses.COLUMN_T_ISMARR)]
				.getCurrentState()) {
			return true;
		}
		if (this.coltables[getColIndex(Resurses.COLUMN_T_ISPARE)]
				.getCurrentState()) {
			return true;
		}
		if (this.coltables[getColIndex(Resurses.COLUMN_T_ISCHILD)]
				.getCurrentState()) {
			return true;
		}
		return false;
	}

	/**
	 * @return Number of active (current) columns
	 */
	public int getColumnCount() {
		return this.coltables.length;
	}

	/**
	 * Fetch current column
	 * 
	 * @param idx
	 * @return column at current idx
	 */
	public ColTable getCurrentColTable(int idx) {

		return this.coltables[idx];
		// ColTable col = null;
		// int i;
		// int curre = 0;
		// for (i = 0; i < this.coltables.length; i++) {
		// col = getColTable(i);
		// if (col.getCurrentState()) {
		// if (idx == curre) {
		// return col;
		// }
		// curre++;
		// }
		// }
		// return col;
	}

	/**
	 * @author FIKAAKAIL inner class for column for the table
	 */
	public class ColTable {
		JCheckBox chk;
		String colname;
		boolean defState;
		boolean currState;

		ColTable(String colname, boolean defState) {
			this.chk = new JCheckBox(Resurses.getString(colname));
			this.colname = colname;
			if (me == null) {
				this.defState = defState;
			} else {
				this.defState = Utils.getBooleanPref(me, getColName(),
						getCurrentState());
			}
			this.currState = defState;

		}

		JCheckBox getChkBox() {
			return this.chk;
		}

		/**
		 * @param state
		 *            the column state (true = show column)
		 */
		public void setCurrentState(boolean state) {
			this.currState = state;
		}

		/**
		 * @return current state of column
		 */
		public boolean getCurrentState() {
			return this.currState;
		}

		/**
		 * @return default state for the column
		 */
		public boolean getDefaultState() {
			return this.defState;
		}

		/**
		 * @return name id of column
		 */
		public String getColName() {
			return this.colname;
		}

	}

	/**
	 * get file contents
	 * 
	 * @param fieldId
	 * @return nameId of field
	 * 
	 */
	public String getCriteriaField(String fieldId) {
		if (fieldId.equals(Resurses.CRITERIA_SURNAME)) {
			return this.surname.getText();
		} else if (fieldId.equals(Resurses.CRITERIA_GIVENNAME)) {
			return this.givenname.getText();
		} else if (fieldId.equals(Resurses.CRITERIA_PATRONYME)) {
			return this.patronyme.getText();
		}
		return null;
	}

	/**
	 * @return no of fields
	 */
	public int getFieldCount() {
		int addCol = 0;
		for (int i = 1; i < 4; i++) {
			ColTable tbl = this.coltables[i];
			if (tbl.getChkBox().isSelected()) {
				addCol = 1;
				break;
			}
		}
		return 19 + addCol;
	}

	/**
	 * @param idx
	 * @return contents of requested field idx
	 */
	public String getCriteriaField(int idx) {
		int vid;
		switch (idx) {
		case 0:
			return this.surname.getText();
		case 1:
			return this.givenname.getText();
		case 2:
			return this.patronyme.getText();
		case 3:
			return Utils.dbTryDate(birtFromDate.getText());
		case 4:
			return Utils.dbTryDate(birtToDate.getText());
		case 5:
			return this.birtPlace.getText();
		case 6:
			return Utils.dbTryDate(deatFromDate.getText());
		case 7:
			return Utils.dbTryDate(deatToDate.getText());
		case 8:
			return this.deatPlace.getText();
		case 9:
			return this.createdFromDate.getText();
		case 10:
			return this.createdToDate.getText();
		case 11:
			vid = this.getViewId();
			if (vid == 0)
				return null;
			else
				return "" + vid;
		case 12:
			return this.viewGroup.getText();
		case 13:
			return "yes";
		case 14:
			return place.getText();
		case 15:
			int noticeIdx = noticeList.getSelectedIndex();
			if (noticeIdx > 0) {
				SukuTypesModel model = Utils.typeInstance();
				return model.getTypesTag(noticeIdx - 1);
			}
			return null;
		case 16:
			return (noticeExist.isSelected()) ? "true" : null;

		case 17:
			return "" + surety.getSurety();
		case 18:
			int sexIdx = sex.getSelectedIndex();
			if (sexIdx > 0) {
				return "" + sexcodes[sexIdx];
			}
			return null;

		case 19:
			return fullText.getText();

		default:
			return null;
		}
	}

	/**
	 * @param idx
	 * @return name of requested field idx
	 */
	public String getFieldName(int idx) {
		switch (idx) {
		case 0:
			return Resurses.CRITERIA_SURNAME;
		case 1:
			return Resurses.CRITERIA_GIVENNAME;
		case 2:
			return Resurses.CRITERIA_PATRONYME;
		case 3:
			return Resurses.CRITERIA_BIRT_FROM;
		case 4:
			return Resurses.CRITERIA_BIRT_TO;
		case 5:
			return Resurses.CRITERIA_BIRT_PLACE;
		case 6:
			return Resurses.CRITERIA_DEAT_FROM;
		case 7:
			return Resurses.CRITERIA_DEAT_TO;
		case 8:
			return Resurses.CRITERIA_DEAT_PLACE;
		case 9:
			return Resurses.CRITERIA_CREATED_FROM;
		case 10:
			return Resurses.CRITERIA_CREATED_TO;
		case 11:
			return Resurses.CRITERIA_VIEW;
		case 12:
			return Resurses.CRITERIA_GROUP;
		case 13:
			return Resurses.CRITERIA_RELATIVE_INFO;
		case 14:
			return Resurses.CRITERIA_PLACE;
		case 15:
			return Resurses.CRITERIA_NOTICE;
		case 16:
			return Resurses.CRITERIA_NOTICE_EXISTS;
		case 17:
			return Resurses.CRITERIA_SURETY;
		case 18:
			return Resurses.CRITERIA_SEX;
		case 19:
			return Resurses.CRITERIA_FULL_TEXT;
		default:
			return null;
		}
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		int idx;
		boolean bvalue;
		if (cmd == null)
			return;
		if (cmd.equals(Resurses.RESET)) {

			resetArguments();
		} else if (cmd.equals(Resurses.OK)) {

			for (idx = 0; idx < this.coltables.length; idx++) {
				ColTable tbl = this.coltables[idx];

				bvalue = tbl.getChkBox().isSelected();
				logger.finest("VCOL: " + tbl.getColName() + "/" + bvalue);

				Utils.putBooleanPref(this, tbl.getColName(), bvalue);
				tbl.setCurrentState(bvalue);
			}

			for (idx = 0; idx < this.proptables.length; idx++) {
				ColTable tbl = this.proptables[idx];

				bvalue = tbl.getChkBox().isSelected();
				logger.finest("VCOLP: " + tbl.getColName() + "/" + bvalue);

				Utils.putBooleanPref(this, tbl.getColName(), bvalue);
				tbl.setCurrentState(bvalue);
			}

			Vector<String> v = new Vector<String>();
			String tmpDate;
			v.add("patronyme=" + patronyme.getText());
			v.add("surname=" + surname.getText());
			v.add("givenname=" + givenname.getText());
			tmpDate = Utils.dbTryDate(birtFromDate.getText());
			if (tmpDate != null) {
				v.add("birtFromDate=" + tmpDate);
			}
			tmpDate = Utils.dbTryDate(birtToDate.getText());
			if (tmpDate != null) {
				v.add("birtToDate=" + tmpDate);
			}
			v.add("birtPlace=" + birtPlace.getText());

			tmpDate = Utils.dbTryDate(deatFromDate.getText());
			if (tmpDate != null) {
				v.add("deatFromDate=" + tmpDate);
			}
			tmpDate = Utils.dbTryDate(deatToDate.getText());
			if (tmpDate != null) {
				v.add("deatToDate=" + tmpDate);
			}
			v.add("deatPlace=" + deatPlace.getText());
			v.add("createdFromDate=" + createdFromDate.getText());
			v.add("createdToDate=" + createdToDate.getText());
			v.add("viewGroup=" + viewGroup.getText());

			int vid = this.getViewId();
			preferredView = null;
			if (vid > 0) {

				int ppnum = 0;

				for (int i = 0; i < viewArray.length; i++) {
					String[] pp = viewArray[i].split(";");

					try {
						ppnum = Integer.parseInt(pp[0]);

					} catch (NumberFormatException ne) {
					}

					if (vid == ppnum) {
						preferredView = viewArray[i];
						break;

					}

				}
			}
			if (preferredView != null) {
				v.add("viewId=" + preferredView);
			}
			v.add("place=" + place.getText());

			int noticeIdx = noticeList.getSelectedIndex();
			if (noticeIdx > 0) {
				SukuTypesModel model = Utils.typeInstance();
				String tag = model.getTypesTag(noticeIdx - 1);
				if (tag != null) {
					v.add("notice=" + tag);
				}

			}
			if (noticeExist.isSelected()) {
				v.add("noticeExists=false");
			}
			int suretyValue = surety.getSurety();
			if (suretyValue < 100) {
				v.add("surety=" + suretyValue);
			}
			int sexIndex = sex.getSelectedIndex();
			if (sexIndex > 0 && sexIndex < sexcodes.length) {

				char sexValue = sexcodes[sexIndex];

				if (sexValue != 0) {
					v.add("sex=" + sexValue);
				}
			}
			v.add("fullText=" + fullText.getText());
			SukuData request = new SukuData();
			request.generalArray = v.toArray(new String[0]);
			try {
				Suku.kontroller.getSukuData(request, "cmd=updatesettings",
						"type=query");
			} catch (SukuException e1) {
				logger
						.log(Level.WARNING, "Failed to write query settings ",
								e1);
			}
			// Suku.kontroller.putPref(this, "viewId", preferredView);
			// this.surnameTx = this.surname.getText();
			// this.givennameTx = this.givenname.getText();
			// this.patronymeTx = this.patronyme.getText();
			//			
			this.setVisible(false);
		}

	}

	private void resetArguments() {
		surname.setText("");
		givenname.setText("");
		patronyme.setText("");

		birtFromDate.setText("");
		birtToDate.setText("");
		birtPlace.setText("");

		deatFromDate.setText("");
		deatToDate.setText("");
		deatPlace.setText("");

		createdFromDate.setText("");
		createdToDate.setText("");
		if (viewList.getItemCount() > 0) {
			viewList.setSelectedIndex(0);
		}
		viewGroup.setText("");
		preferredView = "";
		place.setText("");
		if (noticeList.getItemCount() > 0) {
			noticeList.setSelectedIndex(0);
		}
		if (sex.getItemCount() > 0) {
			sex.setSelectedIndex(0);
		}
		surety.setSurety(100);
		noticeExist.setSelected(false);
		fullText.setText("");

	}

	class DateTextField extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextField createdDate;
		private JCalendarButton calDate;

		DateTextField() {
			this.setLayout(null);
			this.createdDate = new JTextField();
			this.add(createdDate);
			this.calDate = new JCalendarButton();
			this.add(calDate);

			calDate
					.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
						public void propertyChange(
								java.beans.PropertyChangeEvent evt) {
							if (evt.getNewValue() instanceof Date) {
								String df = Resurses.getDateFormat();
								String dff = "yyyy.MM.dd";
								if (df != null) {
									if (df.equals("FI")) {
										dff = "dd.MM.yyyy";
									} else if (df.equals("UK")) {
										dff = "dd/MM/yyyy";
									} else if (df.equals("US")) {
										dff = "MM/dd/yyyy";
									}
								}
								SimpleDateFormat sf = new SimpleDateFormat(dff);
								Date dat = (Date) evt.getNewValue();
								StringBuffer sb = new StringBuffer();
								sb = sf.format(dat, sb, new FieldPosition(0));
								createdDate.setText(sb.toString());
							}
						}
					});

			Dimension dd = this.calDate.getPreferredSize();
			this.createdDate.setBounds(0, 0, 155, dd.height);
			this.calDate.setBounds(160, 0, dd.width, dd.height);

		}

		public void setText(String text) {
			createdDate.setText(text);
		}

		public String getText() {
			return createdDate.getText();
		}

	}

}
