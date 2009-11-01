package fi.kaila.suku.swing.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.print.PrinterException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.StyledDocument;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.FamilyParentRelationIndex;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateException;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.RelationShortData;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.TableShortData;

/**
 * @author FIKAAKAIL
 * 
 *         PersonWindow for suku11J
 * 
 */
public class PersonView extends JPanel implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane = null;

	private static Logger logger = Logger.getLogger(PersonView.class.getName());
	private Suku suku = null;

	static HashMap<String, String> typesTexts = new HashMap<String, String>();

	static String types[] = null;

	private Vector<SukuTabPane> paneTabs = new Vector<SukuTabPane>();

	/**
	 * @return main program handle
	 */
	public Suku getSuku() {
		return suku;
	}

	/**
	 * The tabbed panel constructor for person view
	 * 
	 * @param suku
	 *            = pointer to main program
	 * @throws SukuException
	 */
	public PersonView(Suku suku) throws SukuException {
		super(new GridLayout(1, 1));
		this.suku = suku;

		this.tabbedPane = new JTabbedPane(SwingConstants.TOP,
				JTabbedPane.SCROLL_TAB_LAYOUT);

		tabbedPane.addChangeListener(this);

		FamilyPanel famPanel = new FamilyPanel(this);

		famPanel.setLayout(null);
		famPanel.setPreferredSize(new Dimension(1200, 800));
		famPanel.setOpaque(false);

		addTab(new SukuTabPane(Resurses.TAB_FAMILY, famPanel));
		tabbedPane.setForegroundAt(0, Color.BLUE);
		PersonTextPane textPerson = new PersonTextPane();

		addTab(new SukuTabPane(Resurses.TAB_PERSON_TEXT, textPerson));
		tabbedPane.setForegroundAt(1, Color.BLUE);
		add(this.tabbedPane);

	}

	/**
	 * add pane to end of tabbedpane
	 * 
	 * @param pane
	 */
	public void addTab(SukuTabPane pane) {
		paneTabs.add(pane);
		boolean isNotice = pane.pnl instanceof NoticePane;
		if (!isNotice || getSuku().isShowNotices()) {

			tabbedPane.addTab(pane.title, null, pane, pane.tip);

		}
	}

	/**
	 * 
	 * insert pane to tabbedpane
	 * 
	 * @param pane
	 * @param index
	 */
	public void insertTab(SukuTabPane pane, int index) {
		paneTabs.insertElementAt(pane, index);
		boolean isNotice = pane.pnl instanceof NoticePane;
		if (!isNotice || getSuku().isShowNotices()) {

			tabbedPane.insertTab(pane.title, null, pane, pane.tip, index);

		}
	}

	/**
	 * @return count of tabs
	 */
	public int getTabCount() {
		return paneTabs.size();
	}

	/**
	 * @param idx
	 * @return tabe at index
	 */
	public SukuTabPane getPane(int idx) {
		return paneTabs.get(idx);
	}

	/**
	 * 
	 * move pane to new location
	 * 
	 * @param from
	 * @param to
	 */
	public void movePane(int from, int to) {
		SukuTabPane t = paneTabs.remove(from);
		paneTabs.insertElementAt(t, to);
	}

	/**
	 * tabs are shown or not shown as selected
	 * 
	 * @param value
	 */
	public void showNotices(boolean value) {

		int noteIdx = getFirstNoticeIndex();
		int paneCnt = tabbedPane.getTabCount();

		for (int i = paneCnt - 1; i >= 0 && i >= noteIdx && noteIdx > 0; i--) {
			tabbedPane.remove(i);
		}
		if (value) {
			for (int i = 0; i < paneTabs.size(); i++) {
				SukuTabPane pane = paneTabs.get(i);
				boolean isNotice = pane.pnl instanceof NoticePane;
				if (isNotice) {
					tabbedPane.addTab(pane.title, null, pane, pane.tip);
				}
			}
		}
	}

	/**
	 * set a hiskipanel person
	 * 
	 * @param idx
	 * @param pid
	 * @param nimi
	 */
	public void setHiskiPid(int idx, int pid, String nimi) {
		HiskiImportPanel hiskiPanel = null;

		for (int i = 0; i < paneTabs.size(); i++) {
			Component pan = paneTabs.get(i).pnl;
			if (pan instanceof HiskiImportPanel) {
				hiskiPanel = (HiskiImportPanel) pan;
				break;
			}
		}
		if (hiskiPanel != null) {
			hiskiPanel.setHiskiPid(idx, pid, nimi);
		}
	}

	/**
	 * display the hiskipanel
	 */
	public void displayHiskiPane() {
		HiskiImportPanel hiskiPanel = null;
		int hiskiIdx = 0;
		SukuTabPane spnl = null;

		for (int i = 0; i < paneTabs.size(); i++) {
			spnl = paneTabs.get(i);
			Component pan = spnl.pnl;
			if (pan instanceof HiskiImportPanel) {
				hiskiPanel = (HiskiImportPanel) pan;
				hiskiIdx = i;
				break;
			}
		}
		if (hiskiPanel == null) {
			hiskiPanel = new HiskiImportPanel(suku);

			insertTab(new SukuTabPane("TAB_HISKI", hiskiPanel), hiskiIdx);
			tabbedPane.setSelectedIndex(hiskiIdx);
		}
	}

	/**
	 * resize all notice panes. resize forces the panels also to display fields
	 * as defined by toolbox buttons
	 */
	public void resizeNoticePanes() {

		int fnotice = getFirstNoticeIndex();
		if (fnotice < 2)
			return;
		for (int i = fnotice; i < tabbedPane.getTabCount(); i++) {

			NoticePane pane = null;
			Component pan = paneTabs.get(i).pnl;
			if (pan instanceof NoticePane) {
				pane = (NoticePane) paneTabs.get(i).pnl;
				pane.resizeNoticePane();
			}
		}
	}

	/**
	 * Display person pane. I already open the close pervious first
	 * 
	 * @param pid
	 * @throws SukuException
	 */
	public void displayPersonPane(int pid) throws SukuException {
		int midx = getMainPaneIndex();
		if (midx > 0) {
			PersonMainPane main = (PersonMainPane) paneTabs.get(midx).pnl;

			// int isele = tabbedPane.getSelectedIndex();
			// int fnotice = getFirstNoticeIndex();
			int mnotice = getMainPaneIndex();
			if (main != null) {
				try {
					if (previousNoticeIndex == mnotice) {
						main.updateNameNotices();
						main.updateRestNotices();
					} else {
						main.updateName();
						main.updateRest();
					}
					main.updatePerson();
				} catch (SukuDateException e1) {
					if (previousNoticeIndex <= mnotice + 1) {
						previousNoticeIndex = mnotice;
						tabbedPane.setSelectedIndex(previousNoticeIndex);
					}
					JOptionPane.showMessageDialog(this, e1.getMessage(),
							Resurses.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);

					e1.printStackTrace();
					return;
				}
				closeMainPane(false);
			}
		}

		previousNoticeIndex = -1;
		PersonMainPane personMain = new PersonMainPane(this, pid);
		SukuTabPane pnl = new SukuTabPane(Resurses.TAB_PERSON, personMain);
		addTab(pnl);
		int mainIdx = getMainPaneIndex();

		personMain.openPersonNotices(pid);

		tabbedPane.setSelectedIndex(mainIdx);

		previousNoticeIndex = getMainPaneIndex();

		PersonMainPane main = (PersonMainPane) paneTabs.get(mainIdx).pnl;
		if (main != null) {
			PersonShortData ps = new PersonShortData(main.persLong);

			getSuku().setTitle(
					ps.getAlfaName() + " " + nv4(ps.getBirtDate()) + "-"
							+ nv4(ps.getDeatDate()));
			getSuku().setActivePerson(ps.getPid());

		}
	}

	void closePerson() {

		SukuTabPane pnl = null;
		tabbedPane.setSelectedIndex(1);
		for (int i = paneTabs.size() - 1; i > 0; i--) {

			Component pan = paneTabs.get(i).pnl;
			if (pan instanceof NoticePane || pan instanceof RelativesPane
					|| pan instanceof PersonMainPane) {
				pnl = paneTabs.get(i);
				tabbedPane.remove(pnl);
				paneTabs.remove(pnl);
			}
		}

	}

	/**
	 * Close the main pain. if pid is given then open new Mainpane with pid
	 * 
	 * @param reOpen
	 *            true if page to be reopened
	 * @throws SukuException
	 */
	public void closeMainPane(boolean reOpen) throws SukuException {
		int midx = getMainPaneIndex();
		if (midx < 0)
			return;
		SukuTabPane pan = paneTabs.get(midx);
		PersonMainPane main = (PersonMainPane) pan.pnl;
		int personPid = main.getPersonPid();
		// System.out.println("Talletetaan henkilö: " +personPid);

		main.closeNotices();

		tabbedPane.remove(pan);
		paneTabs.remove(pan);

		if (reOpen && personPid > 0) {
			displayPersonPane(personPid);
		} else {
			getSuku().setTitle(null);
		}

	}

	/**
	 * close the hiskipane
	 */
	public void closeHiskiPane() {

		SukuTabPane spnl = null;

		for (int i = 0; i < paneTabs.size(); i++) {

			Component pan = paneTabs.get(i).pnl;
			if (pan instanceof HiskiImportPanel) {
				spnl = paneTabs.get(i);

				break;
			}
		}

		if (spnl != null) {
			tabbedPane.remove(spnl);
			paneTabs.remove(spnl);
		}
	}

	/**
	 * @param tag
	 * @return name of the tag
	 */
	public String getTypeValue(String tag) {
		return typesTexts.get(tag);
	}

	/**
	 * @return count of types
	 */
	public int getTypesCount() {
		return types.length;
	}

	/**
	 * @param idx
	 * @return tag at idx
	 */
	public String getTypeTag(int idx) {
		return types[idx];
	}

	/**
	 * set familyview (graphic ) to show family
	 * 
	 * @param subject
	 * @throws SukuException
	 */
	public void setSubjectForFamily(PersonShortData subject)
			throws SukuException {

		SukuData family = Suku.kontroller.getSukuData("cmd=family", "pid="
				+ subject.getPid(), "parents=both");
		if (family.pers == null || family.pers.length < 1) {
			return;
		}
		PersonShortData familySubject = family.pers[0];
		Dimension reqDim;
		FamilyPanel famPanel = (FamilyPanel) paneTabs.get(0).pnl;

		if (family.pers == null || family.pers.length == 0) {

			famPanel.resetTable();
		} else {
			Graphics gg = this.getGraphics();
			TableShortData table = new TableShortData();
			table.setSubject(familySubject);
			table.initRelatives(family.pers, family.rels);
			table.setLocation(new Point(100, 300));
			famPanel.resetTable();
			famPanel.addTable(table);

			reqDim = table.getSize(gg);

			Vector<RelationShortData> grands = new Vector<RelationShortData>();

			Vector<TableShortData> parents = new Vector<TableShortData>();

			int fcount = table.getFatherCount();
			int mcount = table.getMotherCount();

			int y = 10;
			int maxheight = y;
			int x = 0;
			int pareWidth = 0;
			PersonShortData pers;
			for (int i = 0; i < fcount + mcount; i++) {
				if (i < fcount) {
					pers = table.getFather(i);
				} else {
					pers = table.getMother(i - fcount);
				}
				family = Suku.kontroller.getSukuData("cmd=family", "pid="
						+ pers.getPid());

				TableShortData ftab = new TableShortData();

				ftab.setSubject(pers);
				for (int j = 0; j < family.rels.length; j++) {
					RelationShortData rel = family.rels[j];
					if (rel.getTag().equals("FATH")
							|| rel.getTag().equals("MOTH")) {
						rel.setAux(parents.size() + 1);
						grands.add(rel);
					}
				}
				ftab.setLocation(new Point(x, y));
				parents.add(ftab);
				famPanel.addTable(ftab);
				famPanel.addRels(new FamilyParentRelationIndex(0, parents
						.size()));

				Dimension dd = ftab.getSize(gg);

				x += dd.width + 30;
				if (reqDim.width < x)
					reqDim.width = x;
				if (maxheight < dd.height)
					maxheight = dd.height;
				pareWidth = x;
			}
			x = 0;
			y = 10;
			int gheight = 0;
			for (int i = 0; i < grands.size(); i++) {
				RelationShortData rel = grands.get(i);

				family = Suku.kontroller.getSukuData("cmd=family", "pid="
						+ rel.getRelationPid());

				// PersonShortData pgran = family.pers[j];
				TableShortData ftab = new TableShortData();

				ftab.setSubject(family.pers[0]);
				// ftab.initRelatives(family.pers, family.rels);
				ftab.setLocation(new Point(x, y));
				famPanel.addTable(ftab);
				famPanel.addRels(new FamilyParentRelationIndex(rel.getAux(),
						famPanel.getTabSize() - 1));

				Dimension dd = ftab.getSize(gg);

				x += dd.width + 30;
				if (gheight < dd.height)
					gheight = dd.height;

				// }
			}

			if (reqDim.width < x)
				reqDim.width = x;
			reqDim.height += maxheight + 250;
			int xw = table.getSize(gg).width;
			int rw = reqDim.width;
			int pareAdd = (rw - pareWidth) / 2;
			for (int i = 0; i < parents.size(); i++) {
				TableShortData tt = parents.get(i);
				Point pp = tt.getLocation();
				pp.y = gheight + 30;
				pp.x += pareAdd;
				tt.setLocation(pp);
			}

			table.setLocation(new Point(rw / 2 - xw / 2, maxheight + gheight
					+ y + 50));

			famPanel.setPreferredSize(reqDim);

			famPanel.updateUI();

			gg.dispose();

		}
		tabbedPane.setSelectedIndex(0);
	}

	protected JPanel makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setBounds(10, 30, 300, 20);
		// filler.setHorizontalAlignment(SwingConstants.CENTER);
		panel.setLayout(null);
		panel.add(filler);
		return panel;
	}

	/**
	 * remove all objects
	 */
	public void reset() {
		try {

			closeMainPane(false);
		} catch (SukuException e) {
			logger.log(Level.WARNING, "closing main pane", e);

		}
		repaint();

	}

	/**
	 * print contents of database draft
	 */
	public void testMe() {
		// this.famPanel.setPreferredSize(new Dimension(1200, 1600));
		PersonTextPane textPerson = (PersonTextPane) paneTabs.get(1).pnl;
		try {
			textPerson.print();
		} catch (PrinterException e) {
			logger.log(Level.WARNING, "printing database draft", e);
			JOptionPane.showMessageDialog(this, "PRINT ERROR", Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

		}
		FamilyPanel famPanel = (FamilyPanel) paneTabs.get(0).pnl;
		famPanel.updateUI();

	}

	/**
	 * 
	 * set to show database draft
	 * 
	 * @param person
	 * @throws SukuException
	 */
	public void setTextForPerson(PersonShortData person) throws SukuException {

		SukuData pdata = Suku.kontroller.getSukuData("cmd=person", "pid="
				+ person.getPid(), "lang=" + Resurses.getLanguage());
		SukuData family = Suku.kontroller.getSukuData("cmd=family", "pid="
				+ person.getPid(), "parents=yes");
		PersonTextPane textPerson = (PersonTextPane) paneTabs.get(1).pnl;
		textPerson.initPerson(pdata.persLong, pdata.relations, family.pers);
		tabbedPane.setSelectedIndex(1);

	}

	/**
	 * @return the javatext document
	 */
	public StyledDocument getDoc() {
		PersonTextPane textPerson = (PersonTextPane) paneTabs.get(1).pnl;
		if (textPerson == null)
			return null;
		return textPerson.getStyledDocument();
	}

	/**
	 * 
	 * sets the javatext documnet
	 * 
	 * @param doc
	 */
	public void setDoc(StyledDocument doc) {
		PersonTextPane textPerson = (PersonTextPane) paneTabs.get(1).pnl;
		textPerson.setStyledDocument(doc);

	}

	private int previousNoticeIndex = -1;

	/**
	 * @return index where main pane is ( 2 or 3)
	 */
	public int getMainPaneIndex() {
		for (int i = 0; i < paneTabs.size(); i++) {
			Component pan = paneTabs.get(i).pnl;
			if (pan instanceof PersonMainPane) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return hiskipane idx if it ios open, else -1
	 */
	public int getHiskiPaneIndex() {
		for (int i = 0; i < paneTabs.size(); i++) {
			Component pan = paneTabs.get(i).pnl;
			if (pan instanceof HiskiImportPanel) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return index of first notice
	 */
	public int getFirstNoticeIndex() {
		int mainIndex = getMainPaneIndex();
		if (mainIndex < 0)
			return -1;
		return mainIndex + 2;

	}

	/**
	 * update notices from mainpane
	 */
	public void updateNotices() {
		int midx = getMainPaneIndex();

		PersonMainPane main = null;

		if (midx > 0) {
			main = (PersonMainPane) paneTabs.get(midx).pnl;
		}

		if (main != null) {
			main.updateNameNotices();
			main.updateRestNotices();
		}

	}

	/**
	 * move noticepane from
	 * 
	 * @param toDirection
	 *            + to left, - to right
	 */
	public void moveSelectedPane(int toDirection) {
		boolean isFromName = false;
		int isele = tabbedPane.getSelectedIndex();
		SukuTabPane pane = paneTabs.get(isele);
		int toIdx = isele + toDirection;
		if (pane.pnl instanceof NoticePane) {
			NoticePane n = (NoticePane) pane.pnl;
			if (n.notice.getTag().equals("NAME")) {
				isFromName = true;
			}
		}

		SukuTabPane desti = paneTabs.get(toIdx);
		if (desti.pnl instanceof NoticePane) {
			NoticePane n = (NoticePane) desti.pnl;
			if (n.notice.getTag().equals("NAME")) {
				if (!isFromName)
					return;

			} else {
				if (isFromName)
					return;
			}
		} else {
			return;
		}

		paneTabs.remove(isele);
		tabbedPane.remove(isele);
		;
		paneTabs.insertElementAt(pane, toIdx);
		tabbedPane.insertTab(pane.title, null, pane, pane.tip, toIdx);
		tabbedPane.setSelectedIndex(toIdx);

	}

	/**
	 * add a notice
	 * 
	 * @param idx
	 * @param tag
	 */
	public void addNotice(int idx, String tag) {
		if (tag == null)
			return;
		int isele = idx;
		if (isele < 0) {
			isele = tabbedPane.getSelectedIndex();
			isele++;
		}
		int mainIdx = getMainPaneIndex();
		if (mainIdx < 0)
			return;
		PersonMainPane main = (PersonMainPane) paneTabs.get(mainIdx).pnl;
		if (main == null)
			return;
		main.insertNamePane(isele, tag);
		tabbedPane.setSelectedIndex(isele);
		updateUI();
	}

	void selectRelativesPane() {
		int midx = getMainPaneIndex();
		if (midx < 0)
			return;
		int relaIdx = midx + 1;
		previousNoticeIndex = relaIdx;
		tabbedPane.setSelectedIndex(relaIdx);

	}

	boolean skipNextState = false;

	@Override
	public void stateChanged(ChangeEvent e) {

		int midx = getMainPaneIndex();
		if (midx < 0)
			return;
		PersonMainPane main = (PersonMainPane) paneTabs.get(midx).pnl;

		int isele = tabbedPane.getSelectedIndex();
		int fnotice = getFirstNoticeIndex();
		int mnotice = getMainPaneIndex();
		if (main != null) {
			if (previousNoticeIndex >= fnotice
					&& previousNoticeIndex < getTabCount()) { // greater than
				// sukulaiset
				NoticePane pane = null;
				String resu = null;
				Component pan = paneTabs.get(previousNoticeIndex).pnl;
				if (pan instanceof NoticePane) {
					pane = (NoticePane) paneTabs.get(previousNoticeIndex).pnl;
					resu = pane.getUnitNoticeError();
				}

				if (resu != null) {
					if (skipNextState) {
						skipNextState = false;
						return;
					}
					skipNextState = true;
					// TO-DO tarkista vieläkö tämä on tarpeen. Näyttäisi olevan
					// 14.9.09
					tabbedPane.setSelectedIndex(previousNoticeIndex);
					JOptionPane.showMessageDialog(this, resu, Resurses
							.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);

					return;
				}
			}

			if (isele > mnotice) {
				if (previousNoticeIndex == mnotice) {
					main.updateNameNotices();
					main.updateRestNotices();
				}

				previousNoticeIndex = isele;
			} else if (isele == mnotice) {
				main.updateName();
				main.updateRest();
				previousNoticeIndex = mnotice;
			}
			if (isele == mnotice + 1) {
				try {
					if (previousNoticeIndex == mnotice) {
						main.updateNameNotices();
						main.updateRestNotices();
					} else {
						main.updateName();
						main.updateRest();
					}
					main.updatePerson();
				} catch (SukuDateException e1) {
					if (previousNoticeIndex <= mnotice + 1) {
						previousNoticeIndex = mnotice;
						tabbedPane.setSelectedIndex(previousNoticeIndex);
					}
					JOptionPane.showMessageDialog(this, e1.getMessage(),
							Resurses.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);

					e1.printStackTrace();
				}
			}
		}
	}

	private String nv4(String text) {
		if (text == null)
			return "";
		if (text.length() < 4)
			return text;
		return text.substring(0, 4);
	}
}
