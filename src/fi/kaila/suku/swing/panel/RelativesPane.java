package fi.kaila.suku.swing.panel;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.dialog.AddRelationNotice;
import fi.kaila.suku.swing.dialog.RelationDialog;
import fi.kaila.suku.swing.util.RelativePopupMenu;
import fi.kaila.suku.swing.util.SukuSuretyField;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateException;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Relatives pane contains lists of relatives
 * 
 * @author Kalle
 * 
 */
public class RelativesPane extends JPanel implements ActionListener,
		ComponentListener, MouseListener {

	private static final long serialVersionUID = 1L;

	PersonView personView = null;

	PersonLongData longPers = null;
	private Relation[] relations = null;
	PersonShortData[] pers = null;

	static Logger logger = Logger.getLogger(RelativesPane.class.getName());

	RelativesPane relaMe = this;

	JButton close;
	JButton update;

	MyRelationModel parents = null;
	JTable pareTab = null;
	JScrollPane pareScroll = null;

	MyRelationModel spouses = null;
	JTable spouTab = null;
	JScrollPane spouScroll = null;

	MyRelationModel children = null;
	JTable chilTab = null;
	JScrollPane chilScroll = null;

	Vector<Relation> otherRelations = null;

	JPanel relaPane = null;
	JButton addData = null;
	JButton delRelation;
	JTextField spouseName = null;

	MyNoticeModel notices = null;
	JTable noticeTab = null;
	JScrollPane noticeScroll = null;
	JLabel suretyLbl = null;
	SukuSuretyField surety;
	JLabel creLabel = null;
	JLabel modLabel = null;
	JTextField created = null;
	JTextField modified = null;

	private RelativePopupListener popupListener = null;

	/** woman icon for database list */
	public static ImageIcon womanIcon = null;
	/** male icon for database list */
	public static ImageIcon manIcon = null;
	/** unknown sex icon for database list */
	public static ImageIcon unknownIcon = null;

	Relation activeRelation = null;

	private RelativesPane() {
		initMe();
	}

	private static RelativesPane me = null;

	/**
	 * relatives pane is singleton as else there is problems with listeners
	 * 
	 * @param peronView
	 * @param longPers
	 * @param relas
	 * @param pers
	 * @return relatives pane instance
	 */
	public static RelativesPane getInstance(PersonView peronView,
			PersonLongData longPers, Relation[] relas, PersonShortData[] pers) {

		if (me == null) {
			me = new RelativesPane();
		}

		me.personView = peronView;
		me.longPers = longPers;
		me.relations = relas;
		me.pers = pers;
		me.persMap = new HashMap<Integer, PersonShortData>();
		me.parents.list.removeAllElements();
		me.spouses.list.removeAllElements();
		me.children.list.removeAllElements();
		me.relaPane.setVisible(false);
		for (int i = 0; i < me.pers.length; i++) {
			me.persMap.put(pers[i].getPid(), pers[i]);
		}

		for (int i = 0; i < me.relations.length; i++) {
			Relation r = me.relations[i];
			if (r.getTag().equals("FATH") || r.getTag().equals("MOTH")) {
				PersonShortData pp = me.persMap.get(r.getRelative());
				r.setShortPerson(pp);
				pp.setAdopted(r.getAdopted());
				me.parents.list.add(r);

			}
		}

		for (int i = 0; i < me.relations.length; i++) {
			Relation r = me.relations[i];
			if (r.getTag().equals("HUSB") || r.getTag().equals("WIFE")) {
				PersonShortData pp = me.persMap.get(r.getRelative());
				r.setShortPerson(pp);
				me.spouses.list.add(r);
				pp.setParentPid(pp.getPid());
			}
		}

		for (int i = 0; i < me.relations.length; i++) {
			Relation r = me.relations[i];
			if (r.getTag().equals("CHIL")) {

				PersonShortData pp = me.persMap.get(r.getRelative());
				r.setShortPerson(pp);
				pp.setAdopted(r.getAdopted());
				me.children.list.add(r);

				int parePid = 0;
				try {
					String tag = (longPers.getSex().equals("M")) ? "MOTH"
							: "FATH";
					// if (longPers.getSex().equals("M")){
					// tag = "MOTH";
					// } else {
					// tag="FATH";
					// }
					SukuData pareDat = Suku.kontroller.getSukuData(
							"cmd=relatives", "pid=" + r.getRelative(), "tag="
									+ tag);
					for (int j = 0; j < me.spouses.list.size(); j++) {
						PersonShortData sh = me.spouses.list.get(j)
								.getShortPerson();
						for (int k = 0; k < pareDat.pidArray.length; k++) {
							if (pareDat.pidArray[k] == sh.getPid()) {
								parePid = sh.getPid();
							}
						}
					}

				} catch (SukuException e) {
					logger.log(Level.WARNING, "init failed getting relatives",
							e);
					// JOptionPane.showMessageDialog(this, e.getMessage(),
					// Resurses.getString(Resurses.SUKU),
					// JOptionPane.ERROR_MESSAGE);

				}

				pp.setParentPid(parePid);
			}
		}

		return me;

	}

	private HashMap<Integer, PersonShortData> persMap = null;// new
	// HashMap<Integer,PersonShortData>
	// ();
	RelativePopupMenu pop = null;

	private void initMe() {

		setLayout(null);

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
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
		popupListener = new RelativePopupListener(this);
		pop = RelativePopupMenu.getInstance(popupListener);

		parents = new MyRelationModel();

		pareTab = setupTable(parents);

		pareTab.addMouseListener(this);

		pareTab.addMouseListener(popupListener);

		// Create the scroll pane and add the table to it.
		pareScroll = new JScrollPane(pareTab);
		add(pareScroll);

		pareTab.setDropMode(DropMode.INSERT);

		pareTab.setTransferHandler(new TransferHandler() {

			private static final long serialVersionUID = 1L;

			public boolean canImport(TransferHandler.TransferSupport info) {
				// we only import PersonShortData
				if (!info.isDataFlavorSupported(PersonShortData
						.getPersonShortDataFlavour())) {
					return false;
				}

				Transferable t = info.getTransferable();
				PersonShortData dd;

				try {
					dd = (PersonShortData) t.getTransferData(PersonShortData
							.getPersonShortDataFlavour());
				} catch (Exception e) {
					return false;
				}
				if (dd.getDragSource() == Utils.PersonSource.SPOUSE
						|| dd.getDragSource() == Utils.PersonSource.CHILD
						|| dd.getDragSource() == Utils.PersonSource.PARENT) {
					return false;
				}

				JTable.DropLocation dl = (JTable.DropLocation) info
						.getDropLocation();
				if (dl.getRow() == -1) {
					return false;
				}
				return true;
			}

			public boolean importData(TransferHandler.TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}

				JTable.DropLocation dl = (JTable.DropLocation) info
						.getDropLocation();
				int index = dl.getRow();

				Transferable t = info.getTransferable();

				PersonShortData dd;

				try {
					dd = (PersonShortData) t.getTransferData(PersonShortData
							.getPersonShortDataFlavour());
				} catch (Exception e) {
					return false;
				}

				if (dl.isInsertRow()) {

					insertIntoParentTable(dd, index);
					return true;
				}

				return false;
			}

			public int getSourceActions(JComponent c) {
				return COPY;
			}
		});

		spouses = new MyRelationModel();

		spouTab = setupTable(spouses);
		spouTab.addMouseListener(this);
		spouTab.addMouseListener(popupListener);
		spouScroll = new JScrollPane(spouTab);
		spouTab.setDragEnabled(true);
		spouTab.setDropMode(DropMode.INSERT);

		add(spouScroll);

		spouTab.setTransferHandler(new TransferHandler() {

			private static final long serialVersionUID = 1L;

			public boolean canImport(TransferHandler.TransferSupport info) {
				// we only import PersonShortData

				if (!info.isDataFlavorSupported(PersonShortData
						.getPersonShortDataFlavour())) {
					return false;
				}

				Transferable t = info.getTransferable();
				PersonShortData dd;

				try {
					dd = (PersonShortData) t.getTransferData(PersonShortData
							.getPersonShortDataFlavour());
				} catch (Exception e) {
					return false;
				}
				if (dd.getDragSource() == Utils.PersonSource.CHILD
						|| dd.getDragSource() == Utils.PersonSource.PARENT) {
					return false;
				}

				JTable.DropLocation dl = (JTable.DropLocation) info
						.getDropLocation();
				if (dl.getRow() == -1) {
					return false;
				}
				return true;
			}

			public boolean importData(TransferHandler.TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}

				JTable.DropLocation dl = (JTable.DropLocation) info
						.getDropLocation();
				int index = dl.getRow();

				Transferable t = info.getTransferable();

				PersonShortData dd;

				try {
					dd = (PersonShortData) t.getTransferData(PersonShortData
							.getPersonShortDataFlavour());
				} catch (Exception e) {
					return false;
				}
				// if (dd.getDragSource() != Utils.PersonSource.CHILD) {
				// // JOptionPane.showMessageDialog(null, "on lapselta");
				// return false;
				// } else
				if (dd.getDragSource() == Utils.PersonSource.SPOUSE) {

					for (int i = 0; i < spouses.list.size(); i++) {
						Relation rl = spouses.list.get(i);
						if (rl.getRelative() == dd.getPid()) {
							spouses.list.remove(i);
							int modI = (i < index) ? -1 : 0;
							spouses.list.add(index + modI, rl);
							spouTab.updateUI();
							chilTab.updateUI();
							return true;

						}

					}

					return false;
				}

				if (dl.isInsertRow()) {

					insertIntoSpouseTable(dd, index);
					return true;
				}

				return false;
			}

			protected Transferable createTransferable(JComponent c) {
				if (c instanceof JTable) {
					JTable t = (JTable) c;
					int ii = t.getSelectedRow();
					if (ii >= 0) {
						Relation r = spouses.list.get(ii);
						PersonShortData ps = r.getShortPerson();
						ps.setDragSource(Utils.PersonSource.SPOUSE);
						return ps;

					}
				}
				return null;

			}

			public int getSourceActions(JComponent c) {
				return COPY;
			}
		});

		otherRelations = new Vector<Relation>();

		children = new MyRelationModel();

		chilTab = setupTable(children);
		chilTab.addMouseListener(this);
		chilTab.addMouseListener(popupListener);
		chilScroll = new JScrollPane(chilTab);
		chilTab.setDragEnabled(true);
		chilTab.setDropMode(DropMode.INSERT);
		// chilTab.setDragEnabled(true);
		chilTab.setTransferHandler(new TransferHandler() {

			private static final long serialVersionUID = 1L;

			public boolean canImport(TransferHandler.TransferSupport info) {
				// we only import PersonShortData

				if (!info.isDataFlavorSupported(PersonShortData
						.getPersonShortDataFlavour())) {
					return false;
				}

				Transferable t = info.getTransferable();
				PersonShortData dd;

				try {
					dd = (PersonShortData) t.getTransferData(PersonShortData
							.getPersonShortDataFlavour());
				} catch (Exception e) {
					return false;
				}
				if (dd.getDragSource() == Utils.PersonSource.SPOUSE
						|| dd.getDragSource() == Utils.PersonSource.PARENT) {
					return false;
				}

				JTable.DropLocation dl = (JTable.DropLocation) info
						.getDropLocation();
				if (dl.getRow() == -1) {
					return false;
				}
				return true;
			}

			public boolean importData(TransferHandler.TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}

				JTable.DropLocation dl = (JTable.DropLocation) info
						.getDropLocation();

				int index = dl.getRow();

				// Get the string that is being dropped.
				Transferable t = info.getTransferable();

				PersonShortData dd;

				try {
					dd = (PersonShortData) t.getTransferData(PersonShortData
							.getPersonShortDataFlavour());
				} catch (Exception e) {
					return false;
				}

				if (dd.getDragSource() == Utils.PersonSource.CHILD) {

					for (int i = 0; i < children.list.size(); i++) {
						Relation rl = children.list.get(i);
						if (rl.getRelative() == dd.getPid()) {
							children.list.remove(i);
							int modI = (i < index) ? -1 : 0;
							children.list.add(index + modI, rl);
							chilTab.updateUI();
							return true;

						}

					}
					return false;
				}
				// Display a dialog with the drop information.
				if (dl.isInsertRow()) {

					insertIntoChildTable(dd, index);
					return true;
				}

				return false;
			}

			public int getSourceActions(JComponent c) {
				return COPY;
			}

			protected Transferable createTransferable(JComponent c) {
				if (c instanceof JTable) {
					JTable t = (JTable) c;
					int ii = t.getSelectedRow();
					if (ii >= 0) {
						Relation r = children.list.get(ii);
						PersonShortData ps = r.getShortPerson();
						ps.setDragSource(Utils.PersonSource.CHILD);
						return ps;

					}
				}
				return null;
			}

		});

		add(chilScroll);

		addComponentListener(this);

		close = new JButton(Resurses.getString(Resurses.CLOSE));
		add(this.close);
		close.setActionCommand(Resurses.CLOSE);
		close.addActionListener(this);

		update = new JButton(Resurses.getString(Resurses.UPDATE));

		add(this.update);
		update.setActionCommand(Resurses.UPDATE);
		update.addActionListener(this);

		relaPane = new JPanel();
		add(relaPane);
		relaPane.setVisible(false);
		// relaPane.setBackground(Color.blue);
		relaPane.setLayout(null);
		spouseName = new JTextField();
		relaPane.add(spouseName);
		spouseName.setEditable(false);
		addData = new JButton(Resurses.getString("RELA_ADD_NOTICE"));
		addData.addActionListener(this);
		addData.setActionCommand("ADD");
		relaPane.add(addData);

		delRelation = new JButton(Resurses.getString("RELA_DEL_RELATION"));
		delRelation.addActionListener(this);
		delRelation.setActionCommand("DEL");
		relaPane.add(delRelation);

		suretyLbl = new JLabel(Resurses.getString("RELA_SURETY"));
		relaPane.add(suretyLbl);
		surety = new SukuSuretyField();
		relaPane.add(surety);

		creLabel = new JLabel(Resurses.getString("DATA_CREATED"));
		relaPane.add(creLabel);
		created = new JTextField();
		created.setEditable(false);
		relaPane.add(created);

		modLabel = new JLabel(Resurses.getString("DATA_MODIFIED"));
		relaPane.add(modLabel);
		modified = new JTextField();
		modified.setEditable(false);
		relaPane.add(modified);

		notices = new MyNoticeModel();
		noticeTab = setupNoticeTable(notices);
		noticeTab.addMouseListener(this);
		noticeScroll = new JScrollPane(noticeTab);
		relaPane.add(noticeScroll);

	}

	// private int fetchOtherParentPid(Relation r) {
	// int parePid=0;
	// try {
	// String tag;
	// if (longPers.getSex().equals("M")){
	// tag = "MOTH";
	// } else {
	// tag="FATH";
	// }
	// SukuData pareDat = Suku.kontroller.getSukuData("cmd=relatives",
	// "pid="+r.getRelative(),"tag="+tag);
	// // System.out.println("sd:" + pareDat.pidArray);
	//			
	// for (int j = 0; j < spouses.list.size(); j++) {
	// PersonShortData sh = spouses.list.get(j).getShortPerson();
	// for (int k = 0; k < pareDat.pidArray.length; k++) {
	// if (pareDat.pidArray[k] == sh.getPid() ){
	// parePid = sh.getPid();
	// }
	// }
	// }
	//			
	//
	// } catch (SukuException e) {
	//		
	// e.printStackTrace();
	// }
	// return parePid;
	// }

	private JTable setupTable(MyRelationModel model) {
		JTable tab = new JTable(model);
		tab.setPreferredScrollableViewportSize(new Dimension(500, 70));
		tab.setFillsViewportHeight(true);

		TableColumnModel modl = tab.getColumnModel();
		int checkWidth = 40;
		TableColumn c = modl.getColumn(0);
		c.setMaxWidth(20);
		c = modl.getColumn(1);
		c.setMaxWidth(10);
		c = modl.getColumn(3);
		c.setMaxWidth(checkWidth);
		c = modl.getColumn(4);
		c.setMaxWidth(checkWidth);
		tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return tab;
	}

	private JTable setupNoticeTable(MyNoticeModel model) {
		JTable tab = new JTable(model);
		// tab.setPreferredScrollableViewportSize(new Dimension(500, 70));
		// tab.setFillsViewportHeight(true);

		// TableColumnModel modl = tab.getColumnModel();
		// int checkWidth=40;
		// TableColumn c = modl.getColumn(0);
		// c.setMaxWidth(checkWidth);

		// c = modl.getColumn(1);
		// c.setMaxWidth(checkWidth);

		// c = modl.getColumn(3);
		// FIX-ME: Not used. Can be removed?
		// if (false)
		// System.out.println(c);

		// c.setMaxWidth(checkWidth);
		return tab;
	}

	class MyRelationModel extends AbstractTableModel {

		private String[] columnNames = null;

		MyRelationModel() {
			columnNames = Resurses.getString("RELA_HEADER").split(";");
		}

		private static final long serialVersionUID = 1L;
		Vector<Relation> list = new Vector<Relation>();

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public int getRowCount() {

			return list.size();
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int rivi, int colo) {
			PersonShortData p = list.get(rivi).getShortPerson();
			switch (colo) {
			case 0: // return p.getSex();
				if (p.getSex().equals("M")) {
					return manIcon;
				} else if (p.getSex().equals("F")) {
					return womanIcon;
				}
				return unknownIcon;
			case 1:
				int num = p.getParentPid();
				String adop = Utils.nv(p.getAdopted());
				if (num == 0)
					return adop;
				for (int i = 0; i < spouses.list.size(); i++) {
					if (num == spouses.list.get(i).getRelative()) {
						return adop + (i + 1);
					}
				}
				return adop;
			case 2:
				return p.getAlfaName();
			case 3:
				return nv4(p.getBirtDate());
			case 4:
				return nv4(p.getDeatDate());

			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class getColumnClass(int idx) {

			if (idx == 0) {
				return womanIcon.getClass();
			}
			return "".getClass();

		}

	}

	class MyNoticeModel extends AbstractTableModel {

		private String[] columnNames = null;

		MyNoticeModel() {
			columnNames = Resurses.getString("RELA_HEADER_NOTICE").split(";");
		}

		private static final long serialVersionUID = 1L;
		Vector<RelationNotice> list = new Vector<RelationNotice>();

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {

			return list.size();
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int rivi, int colo) {
			RelationNotice rn = list.get(rivi);
			switch (colo) {
			case 0:
				return Resurses.getString("RELA_TAG_" + rn.getTag());
			case 1:
				return rn.getType();
			case 2:
				return Utils.textDate(rn.getFromDate(), false);
			case 3:
				return rn.getPlace();
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class getColumnClass(int idx) {
			return "".getClass();

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		// System.out.println("closataan " + cmd);

		if (cmd.equals("ADD")) {

			boolean isMarr = false;

			if (activeRelation.getTag().equals("WIFE")
					|| activeRelation.getTag().equals("HUSB")) {
				isMarr = true;
			}
			AddRelationNotice an;
			try {
				an = new AddRelationNotice(personView.getSuku(), isMarr);

				Rectangle r = addData.getBounds();
				Point pt = new Point(r.x, r.y);
				SwingUtilities.convertPointToScreen(pt, relaPane);
				r.x = pt.x - 40;
				r.y = pt.y + r.height;
				r.height = 160;
				r.width = 120;
				an.setBounds(r);
				an.setVisible(true);

				if (an.getSelectedTag() != null) {
					// System.out.println("Valittiin " + an.getSelectedTag());
					RelationNotice rn = new RelationNotice(an.getSelectedTag());
					notices.list.add(rn);
					noticeTab.updateUI();

					activeRelation.setNotices(notices.list
							.toArray(new RelationNotice[0]));

					// personView.addNotice(-1, an.getSelectedTag());
				}

			} catch (SukuException e1) {
				logger.log(Level.WARNING, "Add new dialog error", e1);
				JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

			}

		} else if (cmd.equals("DEL") || cmd.equals(Resurses.CLOSE)
				|| cmd.equals(Resurses.UPDATE)) {
			relaPane.setVisible(false);

			if (cmd.equals("DEL")) {
				activeRelation.setToBeDeleted(true);
			}

			boolean reOpen = true;
			if (cmd.equals(Resurses.CLOSE)) {
				reOpen = false;
			}

			boolean askChanges = true;
			if (cmd.equals(Resurses.UPDATE) || cmd.equals("DEL")) {
				askChanges = false;
			}
			checkActiveRelationSurety();
			refreshRelativesPane(reOpen, askChanges);

		}

	}

	/**
	 * Refresh the relatives lists from db
	 * 
	 * @param reOpen
	 */
	public void refreshRelativesPane(boolean reOpen, boolean askChanges) {
		int midx = personView.getMainPaneIndex();
		if (midx < 0)
			return;
		SukuTabPane pan = personView.getPane(midx);
		PersonMainPane main = (PersonMainPane) pan.pnl;
		int personPid = main.getPersonPid();

		try {
			personView.closePersonPane(askChanges);
			personView.displayPersonPane(personPid);
			personView.closeMainPane(reOpen);
			if (reOpen) {
				personView.selectRelativesPane();

			}

		} catch (SukuException e1) {
			JOptionPane.showMessageDialog(this, e1.toString(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			logger.log(Level.WARNING, "Closing relatives", e1);

			e1.printStackTrace();
		}
	}

	private void insertIntoChildTable(PersonShortData persShort, int row) {

		Relation rel = new Relation(0, longPers.getPid(), persShort.getPid(),
				"CHIL", 100, null, null);
		rel.setShortPerson(persShort);
		String myRelTag = (longPers.getSex().equals("M")) ? "FATH" : "MOTH";
		String tag = (longPers.getSex().equals("M")) ? "MOTH" : "FATH";
		String pareTag = Resurses.getString("AS_" + tag);

		boolean hasParent = false;
		int parentSurety = 100;
		try {
			SukuData pareDat = Suku.kontroller.getSukuData("cmd=relatives",
					"pid=" + persShort.getPid(), "tag=" + tag);
			if (pareDat.pidArray.length > 0) {
				hasParent = true;
				persShort.setParentPid(pareDat.pidArray[0]);
			}
			for (int j = 0; j < me.spouses.list.size(); j++) {
				PersonShortData sh = me.spouses.list.get(j).getShortPerson();
				for (int k = 0; k < pareDat.pidArray.length; k++) {
					if (pareDat.pidArray[k] == sh.getPid()) {
						persShort.setParentPid(sh.getPid());
						break;
					}
				}
			}

		} catch (SukuException e1) {
			// if problem then don't add Mother / father id
		}

		try {
			checkLocalRelation(new PersonShortData(longPers), rel, persShort);
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(personView, e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			return;
		}

		String newDate = persShort.getBirtDate();
		int newRow = 0;
		if (newDate == null) {
			children.list.add(rel);
		} else {
			for (int i = 0; i < children.list.size(); i++) {
				PersonShortData rowPers = children.list.get(i).getShortPerson();
				String rowDate = rowPers.getBirtDate();
				if (rowDate == null) {
					newRow = -1;
					break;
				}
				if (rowDate.compareTo(newDate) < 0) {
					newRow = i + 1;
				}

			}
		}
		try {
			SukuData chilDat = Suku.kontroller.getSukuData("cmd=person", "pid="
					+ persShort.getPid());

			if (chilDat.relations != null) {
				for (int i = 0; i < chilDat.relations.length; i++) {
					Relation chrel = chilDat.relations[i];
					if (myRelTag.equals(chrel.getTag())) {
						if (chrel.getNotices() == null
								|| chrel.getNotices().length == 0) {
							if (chrel.getSurety() > 50) {
								parentSurety = 40;
								rel.setSurety(parentSurety);

								for (int j = 0; j < chilDat.pers.length; j++) {
									PersonShortData pps = chilDat.pers[j];
									if (chrel.getRelative() == pps.getPid()) {
										chrel.setShortPerson(pps);
										break;
									}
								}

								String[] sures = Resurses.getString(
										"DATA_SURETY_VALUES").split(";");
								StringBuilder sb = new StringBuilder();
								sb.append(Resurses
										.getString("RELA_" + myRelTag));
								sb.append(" [");
								if (chrel.getShortPerson() != null) {
									sb.append(chrel.getShortPerson()
											.getAlfaName());
								} else {
									sb.append("XXX");
								}
								sb.append("] ");
								sb.append(Resurses.getString("PARE_DOUBT"));
								sb.append(" [");
								sb.append(sures[3]);
								sb.append("]?");

								int resu = JOptionPane.showConfirmDialog(
										personView, sb.toString(), Resurses
												.getString(Resurses.SUKU),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
								if (resu == JOptionPane.YES_OPTION) {
									hasParent = false;

									break;
								} else {
									return;
								}
							}
						}
					}
				}
			}

		} catch (SukuException e1) {
			logger.log(Level.WARNING, "Check parent", e1);
		}

		if (newRow < 0) {
			children.list.add(rel);
		} else {
			children.list.insertElementAt(rel, newRow);

		}

		if (!hasParent && spouses.list.size() > 0) {
			PersonShortData pare = null;
			if (spouses.list.size() >= 1) {
				pare = spouses.list.get(0).getShortPerson();

				String[] pares = new String[spouses.list.size()];
				for (int j = 0; j < spouses.list.size(); j++) {
					pares[j] = spouses.list.get(j).getShortPerson()
							.getAlfaName();
				}
				Object par = JOptionPane.showInputDialog(personView, Resurses
						.getString("QUESTION_ADD")
						+ " " + pareTag, Resurses.getString(Resurses.SUKU),
						JOptionPane.QUESTION_MESSAGE, null, pares, pares[0]);

				if (par != null) {
					int spouRow = -1;
					for (int j = 0; j < spouses.list.size(); j++) {
						if (par == pares[j]) {
							spouRow = j;
							break;
						}

					}
					pare = spouses.list.get(spouRow).getShortPerson();

					logger.info("Adding " + pare.getAlfaName() + " as "
							+ pareTag);
					Relation rpare = new Relation(0, persShort.getPid(), pare
							.getPid(), tag, parentSurety, null, null);
					persShort.setParentPid(pare.getPid());
					rpare.setShortPerson(pare);
					pare.setParentPid(pare.getPid());

					try {
						checkLocalRelation(persShort, rpare, pare);
					} catch (SukuException e) {
						JOptionPane.showMessageDialog(personView, e
								.getMessage(), Resurses
								.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					otherRelations.add(rpare);

				}

			}

		}

		chilTab.updateUI();
	}

	private void insertIntoSpouseTable(PersonShortData persShort, int row) {
		// System.out.println("Lisätään siis puolisoksi " + showNewPerson);
		String tag = "WIFE";
		if (persShort.getSex().equals("M")) {
			tag = "HUSB";
		}

		Relation rel = new Relation(0, longPers.getPid(), persShort.getPid(),
				tag, 100, null, null);
		rel.setShortPerson(persShort);

		try {
			String ptag = (longPers.getSex().equals("M")) ? "MOTH" : "FATH";

			for (int j = 0; j < me.children.list.size(); j++) {
				PersonShortData child = me.children.list.get(j)
						.getShortPerson();

				SukuData pareDat = Suku.kontroller.getSukuData("cmd=relatives",
						"pid=" + child.getPid(), "tag=" + ptag);

				for (int k = 0; k < pareDat.pidArray.length; k++) {
					if (pareDat.pidArray[k] == persShort.getPid()) {

						child.setParentPid(persShort.getPid());
						break;
					}
				}
			}

		} catch (SukuException e1) {
			// if problem then don't add Mother / father id
		}

		persShort.setParentPid(persShort.getPid());
		try {
			checkLocalRelation(new PersonShortData(longPers), rel, persShort);
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(personView, e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

			return;
		}

		// for (int i = 0; i < children.list.size(); i++) {
		// System.out.println("CHILIST:" + i + "["
		// + children.list.get(i).getPid() + "/"
		// + children.list.get(i).getRelative() + "/"
		// + children.list.get(i).getShortPerson().getParentPid()
		// + "]");
		// }

		if (row >= 0 && row < spouses.list.size()) {
			spouses.list.insertElementAt(rel, row);
		} else {
			spouses.list.add(rel);
		}

		spouTab.updateUI();
		chilTab.updateUI();
	}

	private void insertIntoParentTable(PersonShortData persShort, int row) {
		// System.out.println("Lisätään siis vanhemmaksi " + showNewPerson);
		String tag = "MOTH";
		if (persShort.getSex().equals("M")) {
			tag = "FATH";
		}
		Relation rel = new Relation(0, longPers.getPid(), persShort.getPid(),
				tag, 100, null, null);
		rel.setShortPerson(persShort);
		try {
			checkLocalRelation(new PersonShortData(longPers), rel, persShort);
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(personView, e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

			return;
		}

		for (int i = 0; i < parents.list.size(); i++) {
			Relation rowrel = parents.list.get(i);
			if (rowrel.getTag().equals(tag)) {
				if (rowrel.getNotices() == null
						|| rowrel.getNotices().length == 0) {
					// not adopted
					if (rowrel.getSurety() > 50) {
						// surety exists > 50%
						rel.setSurety(40);

						String[] sures = Resurses.getString(
								"DATA_SURETY_VALUES").split(";");
						StringBuilder sb = new StringBuilder();
						sb.append(Resurses.getString("RELA_" + tag));
						sb.append(" [");
						sb.append(rowrel.getShortPerson().getAlfaName());
						sb.append("] ");
						sb.append(Resurses.getString("PARE_DOUBT"));
						sb.append(" [");
						sb.append(sures[3]);
						sb.append("]");
						JOptionPane.showMessageDialog(personView,
								sb.toString(), Resurses
										.getString(Resurses.SUKU),
								JOptionPane.WARNING_MESSAGE);

						break;
					}
				}
			}

		}

		if (parents.list.size() == 0 || tag.equals("MOTH")) {
			parents.list.add(rel);
		} else {
			parents.list.insertElementAt(rel, 0);
		}
		pareTab.updateUI();
	}

	private String nv4(String text) {
		if (text == null)
			return "";
		if (text.length() > 4)
			return text.substring(0, 4);
		return text;
	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentResized(ComponentEvent e) {
		Dimension currSize = getSize();
		int leveys = currSize.width / 2 - 10;
		int ph = (currSize.height * 2) / 7 - 30;
		int ch = currSize.height - ph - 30;
		pareScroll.setBounds(10, 10, leveys, ph);
		spouScroll.setBounds(leveys + 15, 10, leveys, ph);
		chilScroll.setBounds(10, ph + 20, leveys, ch);

		close.setBounds(leveys + 30, ph + 20, 80, 24);
		update.setBounds(leveys + 30 + 80, ph + 20, 80, 24);

		relaPane.setBounds(leveys + 15, ph + 44, leveys, ch);

		//
		// below locations are relative to relaPane
		//
		spouseName.setBounds(0, 10, leveys - 20, 20);
		int halfx = (leveys) / 2;
		suretyLbl.setBounds(0, 32, halfx + 30, 20);
		surety.setBounds(0, 52, halfx - 20, 20);
		creLabel.setBounds(0, 74, halfx - 20, 20);
		created.setBounds(0, 94, halfx - 10, 20);
		modLabel.setBounds(0, 116, halfx - 20, 20);
		modified.setBounds(0, 136, halfx - 10, 20);

		addData.setBounds(0, 162, (leveys - 20) / 2, 20);
		delRelation.setBounds((leveys - 20) / 2, 162, (leveys - 20) / 2, 20);
		noticeScroll.setBounds(0, 186, leveys, ph);
		updateUI();
	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() != 1)
			return;

		checkActiveRelationSurety();

		if (e.getSource() == spouTab || e.getSource() == chilTab
				|| e.getSource() == pareTab) {
			if (e.getSource() == chilTab) {
				int ii = chilTab.getSelectedRow();
				if (ii < 0 || ii >= children.list.size())
					return;

				activeRelation = children.list.get(ii);
			} else if (e.getSource() == pareTab) {
				int ii = pareTab.getSelectedRow();
				if (ii < 0 || ii >= parents.list.size())
					return;
				// System.out.println("vanhemmilta tuli riviltä " + ii);
				activeRelation = parents.list.get(ii);
			} else {
				int ii = spouTab.getSelectedRow();
				if (ii < 0 || ii >= spouses.list.size())
					return;
				// System.out.println("puolisolta tuli riviltä " + ii);
				activeRelation = spouses.list.get(ii);
			}
			notices.list.removeAllElements();
			relaPane.setVisible(true);
			spouseName.setText(activeRelation.getShortPerson().getAlfaName());
			noticeScroll.setVisible(true);
			surety.setSurety(activeRelation.getSurety());
			addData.setVisible(true);
			if (activeRelation.getNotices() != null) {
				for (int i = 0; i < activeRelation.getNotices().length; i++) {
					notices.list.add(activeRelation.getNotices()[i]);
				}
			}
			noticeTab.updateUI();
			noticeScroll.updateUI();
			if (activeRelation.getModified() == null) {
				modified.setText("");
			} else {
				modified.setText(activeRelation.getModified().toString());
			}
			if (activeRelation.getCreated() == null) {
				created.setText("");
			} else {
				created.setText(activeRelation.getCreated().toString());
			}
		}

		else if (e.getSource() == noticeTab && activeRelation != null) {
			int ii = noticeTab.getSelectedRow();
			// System.out.println("Avataan tietojakso " + ii);
			// boolean bb =
			openRelaNotice(ii);
			// if (bb) {
			// activeRelation.setToBeUpdated();
			// }

		}
		// System.out.println("mc:" + e);

	}

	private void checkActiveRelationSurety() {
		if (activeRelation == null)
			return;

		int sure = surety.getSurety();
		if (sure != activeRelation.getSurety()) {
			activeRelation.setSurety(sure);
			activeRelation.setToBeUpdated(true);
		}

	}

	private boolean openRelaNotice(int ii) {
		RelationNotice rn = notices.list.get(ii);

		RelationDialog lan = new RelationDialog(personView.getSuku());

		Rectangle r = personView.getSuku().getDbWindow();

		lan.setBounds(r);
		lan.setRelation(rn);
		lan.showMe();
		lan.setVisible(true);

		try {
			lan.updateData();
			return true;
		} catch (SukuDateException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
		}
		return false;

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// if (e.getButton()!= 7) return;
		// if (e.getSource() == spouTab || e.getSource() == chilTab ||
		// e.getSource() == pareTab) {
		// if ( e.getSource() == chilTab){
		//
		// Point clickPoint = e.getPoint();
		// int rowAtPoint = chilTab.rowAtPoint(clickPoint);
		// if (rowAtPoint < 0)return;
		// // int ii = chilTab.getSelectedRow();
		// System.out.println("lapselta tuli Mainoikea " + rowAtPoint);
		// activeRelation = children.list.get(rowAtPoint);
		// System.out.println("lapsino on " + activeRelation.getPid() + "/" +
		// activeRelation.getRelative());
		// } else if ( e.getSource() == pareTab){
		// int ii = pareTab.getSelectedRow();
		// if (ii < 0) return;
		// System.out.println("vanhemmilta tuli oikea " + ii);
		// activeRelation = parents.list.get(ii);
		// } else {
		// int ii = spouTab.getSelectedRow();
		// if (ii < 0) return;
		// System.out.println("puolisolta tuli oikea " + ii);
		// activeRelation = spouses.list.get(ii);
		// }
		// }
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	void checkLocalRelation(PersonShortData pers, Relation r,
			PersonShortData rela) throws SukuException {
		if (pers == null || r == null || rela == null)
			return;
		if (pers.getPid() == rela.getPid()) {
			throw new SukuException(Resurses.getString("CHECK_SELF"));
		}
		if (r.getTag().equals("CHIL")) {
			int pyear = pers.getBirtYear();
			int cyear = rela.getBirtYear();
			compareParentYears(cyear, pyear);

		}
		if (r.getTag().equals("FATH") || r.getTag().equals("MOTH")) {
			int cyear = pers.getBirtYear();
			int pyear = rela.getBirtYear();
			compareParentYears(cyear, pyear);
		}
		if ((r.getTag().equals("HUSB") && pers.getSex().equals("M"))
				|| (r.getTag().equals("WIFE") && pers.getSex().equals("F"))) {
			throw new SukuException(Resurses.getString("CHECK_SPOUSE_SEX"));
		}

		int relapid = rela.getPid();
		if (pers.getPid() == longPers.getPid()) {
			for (int i = 0; i < children.list.size(); i++) {
				Relation rr = children.list.get(i);
				if (rr.getRelative() == relapid && !rr.isToBeDeleted()) {
					throw new SukuException(Resurses
							.getString("CHECK_EXISTS_AS_CHILD"));
				}
			}
			for (int i = 0; i < spouses.list.size(); i++) {
				if (spouses.list.get(i).getRelative() == relapid
						&& !spouses.list.get(i).isToBeDeleted()) {
					throw new SukuException(Resurses
							.getString("CHECK_EXISTS_AS_SPOUSE"));
				}
			}
			for (int i = 0; i < parents.list.size(); i++) {
				if (parents.list.get(i).getRelative() == relapid
						&& !parents.list.get(i).isToBeDeleted()) {
					throw new SukuException(Resurses
							.getString("CHECK_EXISTS_AS_PARENT"));
				}
			}
		}

	}

	private void compareParentYears(int cyear, int pyear) throws SukuException {
		if (pyear == 0 || cyear == 0)
			return;
		if (pyear + 10 > cyear) {
			throw new SukuException(Resurses
					.getString("CHECK_PARENT_TOO_YOUNG"));
		}
		if (cyear > pyear + 100) {
			throw new SukuException(Resurses.getString("CHECK_PARENT_TOO_OLD"));
		}

	}

}
