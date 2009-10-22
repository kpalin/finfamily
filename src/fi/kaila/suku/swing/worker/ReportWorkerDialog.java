package fi.kaila.suku.swing.worker;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.report.DescendantLista;
import fi.kaila.suku.report.DescendantReport;
import fi.kaila.suku.report.JavaReport;
import fi.kaila.suku.swing.ISuku;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

public class ReportWorkerDialog extends JDialog implements ActionListener,
		PropertyChangeListener {

	// dateformats
	private static final String SET_FI = "FI";
	private static final String SET_SE = "SE";
	private static final String SET_UK = "UK";
	private static final String SET_US = "US";
	// sourceformat
	private static final String SET_NO = "NO";
	private static final String SET_TX1 = "TX1";
	private static final String SET_TX2 = "TX2";
	private static final String SET_AFT = "AFT";

	public static final String SET_PAFT = "PAFT";
	public static final String SET_TAFT = "TAFT";
	public static final String SET_RAFT = "RAFT";

	public static final String SET_ANC_STRADONIZ = "STRADONIZ";
	public static final String SET_ANC_HAGER = "HAGER";
	public static final String SET_ANC_ESPOLIN = "ESPOLIN";

	public static final String SET_ORDER_TAB = "TAB";
	public static final String SET_ORDER_MALE = "MALE";
	public static final String SET_ORDER_FEMALE = "FEMALE";
	public static final String SET_ORDER_FIRSTMALE = "FIRSTMALE";
	public static final String SET_ORDER_NEWMALE = "NEWMALE";
	public static final String SET_ORDER_REG = "REG";

	public static final String SET_SPOUSE_NONE = "NONE";
	public static final String SET_SPOUSE_YEAR = "YEAR";
	public static final String SET_SPOUSE_DATE = "DATE";
	public static final String SET_SPOUSE_FULL = "FULL";

	private static final String ACTION_INDEX = "ACTION_INDEX";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean DEBUG = false;
	private static final String CANCEL = "CANCEL";
	private static final String START = "START";
	private JLabel textContent;

	private JButton cancel;
	private JButton start;

	private static Logger logger = Logger.getLogger(ReportWorkerDialog.class
			.getName());

	private SukuKontroller kontroller = null;

	private JProgressBar progressBar;
	private Task task;
	private TaskLista taskLista;
	private TaskCards taskCards;
	private ReportWorkerDialog self;

	JTabbedPane reportTypePane = null;

	public static ReportWorkerDialog getRunner() {
		return runner;
	}

	private JCheckBox commonWithImages = null;
	private JCheckBox commonBendNames = null;
	private JCheckBox commonSeparateNotices = null;
	private ButtonGroup commonDateFormatGroup = null;
	private ButtonGroup commonSourcesFormatGroup = null;
	private JComboBox commonReportFormatList = null;
	private JCheckBox commonNamesBold = null;
	private JCheckBox commonNamesUnderline = null;
	private JCheckBox commonWithAddress = null;

	private JCheckBox commonIndexNames = null;
	private JCheckBox commonIndexPlaces = null;
	private JCheckBox commonIndexYears = null;
	private JTable typesTable = null;
	private String[] typesTags = null;
	private String[] typesValues = null;
	private Object[][] typesData = null;
	private HashMap<String, Integer> typeTexts = new HashMap<String, Integer>();
	private HashMap<String, String> textTexts = new HashMap<String, String>();

	private ButtonGroup listaGroup = null;

	private JComboBox settingsName = null;
	private int settingsIndex = 0;
	// private String[] settingList=null;

	private DefaultComboBoxModel settingModel = null;
	private DescendantPane descendantPanel;
	private AncestorPane ancestorPanel;
	private JPanel listaPanel;
	// private ReportFrame repo;
	private PersonShortData pers = null;
	private static ReportWorkerDialog runner = null;

	private static final int x1 = 10;
	private static final int x2 = 250;
	private static final int x3 = 440;
	private static final int x4 = 620;
	private static final int xtype = 320;
	private static final int y1 = 20;
	// private static final int y2 = 250;
	private static final int y3 = 390;
	// private static final int y4 = 420;

	private static final int tabh = 360;
	private static final int tabw = 280;
	private Suku parent;

	/**
	 * @param owner
	 */
	public ReportWorkerDialog(Suku owner, SukuKontroller kontroller,
			PersonShortData pers) {
		super(owner, Resurses.getString("REPORT_CREATING"), true);
		this.parent = owner;
		runner = this;
		this.kontroller = kontroller;

		this.pers = pers;
		self = this;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				// UIManager.put("swing.boldMetal", Boolean.FALSE);
				initMe();
			}

		});

	}

	public ISuku getSukuParent() {
		return parent;
	}

	public int getPid() {
		return this.pers.getPid();
	}

	/**
	 * get no of tags in use
	 * 
	 * @return the no of tags
	 */
	public int getTypesTagsCount() {
		return typesTags.length;
	}

	/**
	 * Gets the tag at the indicated index position
	 * 
	 * @return the indexed tag
	 */
	public String getTypesTag(int idx) {
		return typesTags[idx];
	}

	/**
	 * Get report value for tag
	 * 
	 * @param tag
	 * @return name of type
	 */
	public String getTypeText(String tag) {
		Integer iidx = typeTexts.get(tag);
		if (iidx == null)
			return tag;
		int idx = iidx.intValue();
		if (idx >= 0) {
			if (idx < typesData.length) {
				return (String) typesData[idx][5];
			}
			return (String) typesValues[idx];
		}
		return null;
	}

	public String getTagName(String tag) {
		Integer iidx = typeTexts.get(tag);
		if (iidx == null)
			return tag;
		int idx = iidx.intValue();
		if (idx >= 0) {
			if (idx < typesData.length) {
				return (String) typesData[idx][0];
			}
			return (String) typesValues[idx];
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
		String value = textTexts.get(tag);
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
		Integer idxInt = typeTexts.get(tag);
		if (idxInt == null)
			return true;
		int idx = idxInt.intValue();
		if (idx >= 0) {
			if (idx < typesData.length) {
				return (Boolean) typesData[idx][col];
			}
		}
		return false;

	}

	public boolean showBoldNames() {
		return (commonNamesBold.getSelectedObjects() != null);
	}

	public boolean showUnderlineNames() {
		return (commonNamesUnderline.getSelectedObjects() != null);
	}

	public boolean showOnSeparateLines() {
		return (commonSeparateNotices.getSelectedObjects() != null);
	}

	public boolean showImages() {
		return (commonWithImages.getSelectedObjects() != null);
	}

	public boolean showAddress() {
		return (commonWithAddress.getSelectedObjects() != null);
	}

	public String getDateFormat() {
		ButtonModel model = commonDateFormatGroup.getSelection();
		if (model != null) {
			return model.getActionCommand();
		}
		return "FI";
	}

	public SukuKontroller getKontroller() {
		return this.kontroller;
	}

	public JProgressBar getProgressBar() {
		return this.progressBar;
	}

	// public ReportFrame getRepo(){
	// return repo;
	// }

	private void initMe() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension sz = new Dimension(d.width - 200, d.height - 150);
		sz = new Dimension(800, 600);
		int footery = sz.height - 125;
		setBounds((d.width - sz.width) / 2, (d.height - sz.height) / 2,
				sz.width, sz.height);
		setLayout(null);
		JPanel pane;
		JLabel lb;

		String tmp = Suku.kontroller.getPref(this, Resurses.SETTING_IDX, "0");
		try {
			settingsIndex = Integer.parseInt(tmp);
		} catch (NumberFormatException ne) {
			settingsIndex = 0;
		}

		lb = new JLabel(this.pers.getAlfaName(true));
		add(lb);
		lb.setBounds(x1, y1 - 20, 300, 20);

		typesTable = new JTable(new MyTypesModel());
		typesTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		typesTable.setFillsViewportHeight(true);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(typesTable);
		scrollPane.setBounds(xtype, y1, tabw, tabh);
		// Add the scroll pane to this panel.
		add(scrollPane);

		TableColumnModel modl = typesTable.getColumnModel();
		int checkWidth = 30;
		TableColumn c = modl.getColumn(1);
		c.setMaxWidth(checkWidth);
		c = modl.getColumn(2);
		c.setMaxWidth(checkWidth);
		c = modl.getColumn(3);
		c.setMaxWidth(checkWidth);
		c = modl.getColumn(4);
		c.setMaxWidth(checkWidth);

		ImageIcon icon1 = createImageIcon("/images/jalkipolvi.gif");
		ImageIcon icon2 = createImageIcon("/images/esipolvi.gif");
		ImageIcon icon3 = createImageIcon("/images/muupolvi.gif");

		commonWithImages = new JCheckBox(Resurses
				.getString("REPORT.WITHIMAGES"), true);
		commonWithImages.setBounds(x4, y1, 160, 20);
		add(commonWithImages);

		commonBendNames = new JCheckBox(Resurses.getString("REPORT.BENDNAMES"),
				true);
		commonBendNames.setBounds(x4, y1 + 22, 160, 20);
		add(commonBendNames);

		commonSeparateNotices = new JCheckBox(Resurses
				.getString("REPORT.SEPARATENOTICES"), true);
		commonSeparateNotices.setBounds(x4, y1 + 44, 160, 20);
		add(commonSeparateNotices);

		commonDateFormatGroup = new ButtonGroup();

		commonNamesBold = new JCheckBox(Resurses.getString("REPORT.NAME.BOLD"));
		commonNamesBold.setBounds(x4, y1 + 66, 160, 20);
		add(commonNamesBold);

		commonNamesUnderline = new JCheckBox(Resurses
				.getString("REPORT.NAME.UNDERLINE"));
		commonNamesUnderline.setBounds(x4, y1 + 88, 160, 20);
		add(commonNamesUnderline);

		commonWithAddress = new JCheckBox(Resurses
				.getString("REPORT.WITHADDERSS"), true);
		commonWithAddress.setBounds(x4, y1 + 110, 160, 20);
		add(commonWithAddress);

		pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("REPORT.DATEFORMAT")));
		pane.setLayout(new GridLayout(0, 1));
		pane.setBounds(x2, y3, 160, 100);

		JRadioButton formd = new JRadioButton(Resurses
				.getString("REPORT.DATEFI"));
		formd.setActionCommand(SET_FI);
		formd.setSelected(true);
		commonDateFormatGroup.add(formd);
		pane.add(formd);
		formd = new JRadioButton(Resurses.getString("REPORT.DATESE"));
		formd.setActionCommand(SET_SE);
		commonDateFormatGroup.add(formd);
		pane.add(formd);
		formd = new JRadioButton(Resurses.getString("REPORT.DATEUK"));
		formd.setActionCommand(SET_UK);
		commonDateFormatGroup.add(formd);
		pane.add(formd);
		formd = new JRadioButton(Resurses.getString("REPORT.DATEUS"));
		formd.setActionCommand(SET_US);
		commonDateFormatGroup.add(formd);
		pane.add(formd);

		add(pane);

		commonSourcesFormatGroup = new ButtonGroup();

		pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("REPORT.SOURCES")));
		pane.setLayout(new GridLayout(0, 1));
		pane.setBounds(x3, y3, 160, 100);

		formd = new JRadioButton(Resurses.getString("REPORT.SOURCENO"));
		formd.setActionCommand(SET_NO);
		commonSourcesFormatGroup.add(formd);
		pane.add(formd);
		formd = new JRadioButton(Resurses.getString("REPORT.SOURCETX1"));
		formd.setActionCommand(SET_TX1);
		commonSourcesFormatGroup.add(formd);
		pane.add(formd);
		formd = new JRadioButton(Resurses.getString("REPORT.SOURCETX2"));
		formd.setActionCommand(SET_TX2);
		commonSourcesFormatGroup.add(formd);
		pane.add(formd);
		formd = new JRadioButton(Resurses.getString("REPORT.SOURCEAFT"));
		formd.setActionCommand(SET_AFT);
		commonSourcesFormatGroup.add(formd);
		pane.add(formd);

		add(pane);

		Vector<String> v = new Vector<String>();

		v.add(Resurses.getString("REPORT.FORMAT.JAVA"));
		v.add(Resurses.getString("REPORT.FORMAT.WORD2003"));
		v.add(Resurses.getString("REPORT.FORMAT.HTML"));
		v.add(Resurses.getString("REPORT.FORMAT.TEXT"));

		lb = new JLabel(Resurses.getString("REPORT.FORMAT"));
		lb.setBounds(x3, footery, 200, 20);
		add(lb);

		commonReportFormatList = new JComboBox(v);
		commonReportFormatList.setBounds(x3, footery + 25, 200, 20);
		add(commonReportFormatList);

		pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("REPORT.INDEX")));
		pane.setLayout(new GridLayout(0, 1));

		pane.setBounds(x4, y3, 160, 100);

		add(pane);

		commonIndexNames = new JCheckBox(Resurses
				.getString("REPORT.INDEX.NAMES"));
		commonIndexNames.setBounds(0, 0, 150, 0);
		pane.add(commonIndexNames);

		commonIndexPlaces = new JCheckBox(Resurses
				.getString("REPORT.INDEX.PLACES"));
		commonIndexPlaces.setBounds(0, 22, 150, 0);
		pane.add(commonIndexPlaces);

		commonIndexYears = new JCheckBox(Resurses
				.getString("REPORT.INDEX.YEARS"));
		commonIndexYears.setBounds(0, 44, 150, 0);
		pane.add(commonIndexYears);

		reportTypePane = new JTabbedPane();

		descendantPanel = new DescendantPane();

		reportTypePane.addTab(Resurses.getString("REPORT.DESCENDANT"), icon1,
				descendantPanel, Resurses.getString("REPORT.TIP.DESCENDANT"));

		reportTypePane.setMnemonicAt(0, KeyEvent.VK_1);

		ancestorPanel = new AncestorPane();

		reportTypePane.addTab(Resurses.getString("REPORT.ANCESTOR"), icon2,
				ancestorPanel, Resurses.getString("REPORT.TIP.ANCESTOR"));
		reportTypePane.setMnemonicAt(1, KeyEvent.VK_2);

		listaPanel = new JPanel();
		listaPanel.setLayout(null);

		listaPanel.setPreferredSize(new Dimension(410, 50));
		reportTypePane.addTab(Resurses.getString("REPORT.LISTAT"), icon3,
				listaPanel, Resurses.getString("REPORT.TIP.LISTAT"));
		reportTypePane.setMnemonicAt(2, KeyEvent.VK_3);

		//				
		// reportTypePane.addTab(Resurses.getString("REPORT.LISTAT"), icon3,
		// listaPanel,
		// Resurses.getString("REPORT.TIP.LISTAT"));

		// Add the tabbed pane to this panel.
		add(reportTypePane);

		// The following line enables to use scrolling tabs.
		reportTypePane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		reportTypePane.setBounds(x1, y1, tabw, tabh);

		listaGroup = new ButtonGroup();

		JRadioButton listad = new JRadioButton(Resurses
				.getString("REPORT.LISTA.DESCLISTA"));
		listaPanel.add(listad);
		listad.setBounds(10, 20, 200, 20);
		listad.setActionCommand("REPORT.LISTA.DESCLISTA");
		listaGroup.add(listad);

		listad = new JRadioButton(Resurses
				.getString("REPORT.LISTA.PERSONCARDS"));
		listaPanel.add(listad);
		listad.setBounds(10, 44, 200, 20);
		listad.setActionCommand("REPORT.LISTA.PERSONCARDS");
		listaGroup.add(listad);

		lb = new JLabel(Resurses.getString("REPORT.SETTINGS.NAME"));
		lb.setBounds(x1 + 20, y3, 100, 20);
		add(lb);

		JButton save = new JButton(Resurses
				.getString(Resurses.REPORT_SETTINGS_SAVE));
		save.setBounds(x1 + 20, y3 + 60, 80, 20);
		save.setActionCommand(Resurses.REPORT_SETTINGS_SAVE);
		save.addActionListener(this);
		add(save);

		setRadioButton(commonDateFormatGroup, SET_FI);
		setRadioButton(commonSourcesFormatGroup, SET_NO);

		textContent = new JLabel("x");
		getContentPane().add(textContent);
		this.textContent.setBounds(30, footery, 340, 20);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.progressBar.setBounds(30, footery + 25, 340, 20);
		getContentPane().add(this.progressBar);

		this.start = new JButton(Resurses.getString(START));
		getContentPane().add(this.start);
		this.start.setBounds(30, footery + 60, 100, 24);
		this.start.setActionCommand(START);
		this.start.addActionListener(this);

		this.cancel = new JButton(Resurses.getString(CANCEL));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(150, footery + 60, 100, 24);
		this.cancel.setActionCommand(CANCEL);
		this.cancel.addActionListener(this);

		this.task = null;

		settingModel = new DefaultComboBoxModel();

		settingsName = new JComboBox(settingModel);
		settingsName.setEditable(true);
		settingsName.setBounds(x1 + 20, y3 + 30, 200, 20);
		// settingsName.setSelectedIndex(settingsIndex);
		settingsName.addActionListener(this);
		settingsName.setActionCommand(ACTION_INDEX);
		add(settingsName);

		loadReportSettings();

	}

	public DescendantPane getDescendantPanel() {
		return descendantPanel;
	}

	private void loadReportSettings() {

		try {
			SukuData sets = Suku.kontroller.getSukuData("cmd=getsettings",
					"type=report", "index=" + settingsIndex);
			// settingsList = sets.generalArray;
			settingModel.removeAllElements();

			for (int i = 0; i < sets.generalArray.length; i++) {
				if (sets.generalArray[i] != null) {
					settingModel.addElement(sets.generalArray[i]);
				} else {
					settingModel.addElement("" + i);
				}
			}
			settingsName.setSelectedIndex(settingsIndex);
			if (sets.resu != null) {
				JOptionPane.showMessageDialog(this, "error fetching setting: "
						+ sets.resu, Resurses.getString(Resurses.SUKU),
						JOptionPane.ERROR_MESSAGE);
			}

			String vx[] = null;

			boolean bendNames = false;
			boolean withImages = false;
			boolean separateNotices = false;
			boolean boldNames = false;
			boolean underlineNames = false;
			boolean withAddress = false;
			boolean indexNames = false;
			boolean indexPlaces = false;
			boolean indexYears = false;
			boolean descendantAdopted = false;
			boolean ancestorFamily = false;
			for (int i = 0; i < sets.vvTypes.size(); i++) {

				vx = sets.vvTypes.get(i);
				if (vx[0].equals("bend")) {
					bendNames = true;
				} else if (vx[0].equals("images")) {
					withImages = true;
				} else if (vx[0].equals("separate")) {
					separateNotices = true;
				} else if (vx[0].equals("bold")) {
					boldNames = true;
				} else if (vx[0].equals("underline")) {
					underlineNames = true;
				} else if (vx[0].equals("address")) {
					withAddress = true;
				} else if (vx[0].equals("nameIndex")) {
					indexNames = true;
				} else if (vx[0].equals("placeIndex")) {
					indexPlaces = true;
				} else if (vx[0].equals("yearsIndex")) {
					indexYears = true;
				} else if (vx[0].equals("dateformat")) {
					setRadioButton(commonDateFormatGroup, vx[1]);

				} else if (vx[0].equals("sources")) {
					setRadioButton(commonSourcesFormatGroup, vx[1]);

				} else if (vx[0].equals("reportIndex")) {
					int ii;
					try {
						ii = Integer.parseInt(vx[1]);
					} catch (NumberFormatException ne) {
						ii = 0;
					}
					reportTypePane.setSelectedIndex(ii);
				} else if (vx[0].equals("descgen")) {
					descendantPanel.setGenerations(vx[1]);
				} else if (vx[0].equals("descadopted")) {
					descendantAdopted = true;

				} else if (vx[0].equals("descspanc")) {

					descendantPanel.setSpouseAncestors(vx[1]);
				} else if (vx[0].equals("descchanc")) {

					descendantPanel.setChildAncestors(vx[1]);

				} else if (vx[0].equals("descTableOrder")) {
					setRadioButton(descendantPanel.getTableOrder(), vx[1]);

				} else if (vx[0].equals("ancFamily")) {
					ancestorFamily = true;
				} else if (vx[0].equals("ancNumbering")) {
					setRadioButton(ancestorPanel.getNumberingFormat(), vx[1]);
				} else if (vx[0].equals("ancDesc")) {
					ancestorPanel.setDescGen(vx[1]);
				} else if (vx[0].startsWith("t:")) {

					int typeCount = typesTable.getRowCount();
					for (int row = 0; row < typeCount; row++) {

						String tag = typesTags[row];

						if (vx[0].substring(2).equals(tag)
								&& vx[1].length() == 3) {
							boolean b = false;
							if (vx[1].substring(0, 1).equals("X")) {
								b = true;
							}
							typesTable.setValueAt(b, row, 1);
							b = false;
							if (vx[1].substring(1, 2).equals("X")) {
								b = true;
							}
							typesTable.setValueAt(b, row, 2);
							b = false;
							if (vx[1].substring(2).equals("X")) {
								b = true;
							}
							typesTable.setValueAt(b, row, 3);
							;

							break;
						}
						// StringBuffer sb = new StringBuffer();
						// sb.append("t:");
						// sb.append(typesTags[row]);
						// sb.append("=");
						// sb.append(((Boolean)typesTable.getValueAt(row,
						// 1))?"X":"O");
						// sb.append(((Boolean)typesTable.getValueAt(row,
						// 2))?"X":"O");
						// sb.append(((Boolean)typesTable.getValueAt(row,
						// 3))?"X":"O");
						// v.add(sb.toString());
						//						
					}

				}

			}

			commonBendNames.setSelected(bendNames);
			commonWithImages.setSelected(withImages);
			commonSeparateNotices.setSelected(separateNotices);
			commonNamesBold.setSelected(boldNames);
			commonNamesUnderline.setSelected(underlineNames);
			commonWithAddress.setSelected(withAddress);
			commonIndexNames.setSelected(indexNames);
			commonIndexPlaces.setSelected(indexPlaces);
			commonIndexYears.setSelected(indexYears);
			descendantPanel.setAdopted(descendantAdopted);
			ancestorPanel.setShowFamily(ancestorFamily);

		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, "error fetching setting "
					+ settingsIndex + ": " + e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals(Resurses.REPORT_SETTINGS_SAVE)) {
			if (settingsName.getSelectedIndex() >= 0) {
				// settingsIndex = settingsName.getSelectedIndex();
				String tmp = (String) settingsName.getSelectedItem();
				settingModel.insertElementAt(tmp, settingsIndex);
				settingModel.removeElementAt(settingsIndex + 1);
				// int newidx=settingsIndex-1;
				// if (newidx < 0) newidx=0;
				// settingModel.insertElementAt((String)settingsName.getSelectedItem(),newidx);

			}
			saveReportSettings();
		}

		if (cmd.equals(ACTION_INDEX)) {
			if (settingsName.getSelectedIndex() >= 0) {
				settingsIndex = settingsName.getSelectedIndex();
				loadReportSettings();

			}

			return;
		}
		if (cmd.equals(CANCEL)) {
			if (this.task == null) {
				setVisible(false);
			} else {
				this.task.cancel(true);
			}
		}

		if (cmd.equals(START)) {

			int i = reportTypePane.getSelectedIndex();

			String listSele = null;
			switch (i) {
			case 0:
				// we create new instances as needed.
				task = new Task();
				task.addPropertyChangeListener(this);
				task.execute();
				break;
			case 2:
				listSele = listaGroup.getSelection().getActionCommand();
				if (listSele == null)
					return;

				if (listSele.equals("REPORT.LISTA.DESCLISTA")) {
					taskLista = new TaskLista();
					taskLista.addPropertyChangeListener(this);
					taskLista.execute();
				} else if (listSele.equals("REPORT.LISTA.PERSONCARDS")) {
					taskCards = new TaskCards();
					taskCards.addPropertyChangeListener(this);
					taskCards.execute();
					break;

				} else {
					JOptionPane.showMessageDialog(this, Resurses
							.getString("REPORT.LISTA.NOLIST.SELECTED"));
				}
				break;
			default:
				JOptionPane.showMessageDialog(this, Resurses
						.getString("REPORT.NOTSUPPORTED"));
				return;
			}
		}
	}

	private void saveReportSettings() {

		Suku.kontroller.putPref(this, Resurses.SETTING_IDX, "" + settingsIndex);

		Vector<String> v = new Vector<String>();

		v.add("cmd=saverepo");
		v.add("index=" + settingsIndex);
		v.add("name=" + (String) settingsName.getSelectedItem());
		if (commonWithImages.getSelectedObjects() != null) {
			v.add("images=true");
		}
		if (commonBendNames.getSelectedObjects() != null) {
			v.add("bend=true");
		}
		if (commonSeparateNotices.getSelectedObjects() != null) {
			v.add("separate=true");
		}
		if (commonNamesBold.getSelectedObjects() != null) {
			v.add("bold=true");
		}
		if (commonNamesUnderline.getSelectedObjects() != null) {
			v.add("underline=true");
		}
		if (commonWithAddress.getSelectedObjects() != null) {
			v.add("address=true");
		}
		ButtonModel model = commonDateFormatGroup.getSelection();
		if (model != null) {
			v.add("dateformat=" + model.getActionCommand());
		}
		model = commonSourcesFormatGroup.getSelection();
		if (model != null) {
			v.add("sources=" + model.getActionCommand());
		}
		if (commonIndexNames.getSelectedObjects() != null) {
			v.add("nameIndex=true");
		}
		if (commonIndexPlaces.getSelectedObjects() != null) {
			v.add("placeIndex=true");
		}
		if (commonIndexYears.getSelectedObjects() != null) {
			v.add("yearsIndex=true");
		}
		v.add("reportIndex=" + reportTypePane.getSelectedIndex());

		v.add("descgen=" + descendantPanel.getGenerations());
		if (descendantPanel.getAdopted()) {
			v.add("descadopted=true");
		}
		v.add("descspanc=" + descendantPanel.getSpouseAncestors());
		v.add("descchanc=" + descendantPanel.getChildAncestors());

		model = descendantPanel.getTableOrder().getSelection();
		if (model != null) {
			v.add("descTableOrder=" + model.getActionCommand());
		}

		model = ancestorPanel.getNumberingFormat().getSelection();
		if (model != null) {
			v.add("ancNumbering=" + model.getActionCommand());
		}
		if (ancestorPanel.getShowfamily()) {
			v.add("ancFamily=true");
		}
		String tmp = ancestorPanel.getShowDescGen();
		if (tmp != null && !"".equals(tmp)) {
			v.add("ancDesc=" + tmp);
		}

		int typeCount = typesTable.getRowCount();

		for (int row = 0; row < typeCount; row++) {
			StringBuffer sb = new StringBuffer();
			sb.append("t:");
			sb.append(typesTags[row]);
			sb.append("=");
			sb.append(((Boolean) typesTable.getValueAt(row, 1)) ? "X" : "O");
			sb.append(((Boolean) typesTable.getValueAt(row, 2)) ? "X" : "O");
			sb.append(((Boolean) typesTable.getValueAt(row, 3)) ? "X" : "O");
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

	class Task extends SwingWorker<Void, Void> {

		DescendantReport dr = null;

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {

			try {

				setProgress(0);

				dr = new DescendantReport(self, new JavaReport());

				dr.executeReport();

			} catch (Exception e) {
				logger.log(Level.WARNING, "Exception in background thread", e);
			}

			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setVisible(false);
			if (dr != null) {
				dr.getWriter().closeReport();
				// dr.getFrame().setVisible(true);
			}
		}
	}

	class TaskLista extends SwingWorker<Void, Void> {

		DescendantLista dlista = null;

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {

			try {

				setProgress(0);

				dlista = new DescendantLista(self, null);

				dlista.executeReport();

			} catch (Exception e) {
				logger.log(Level.WARNING, "Exception in background thread", e);
			}

			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setVisible(false);
			// if (dlista != null) {
			// dlista.getWriter().closeReport();
			// // dr.getFrame().setVisible(true);
			// }
		}
	}

	class TaskCards extends SwingWorker<Void, Void> {

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {

			try {

				if (parent.getDatabaseRowCount() > 100) {
					JOptionPane.showMessageDialog(null, Resurses
							.getString("CARD_TOO_MANY"), Resurses
							.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);
					return null;
				}

				if (!Suku.kontroller.createLocalFile("xls")) {
					return null;
				}

				setProgress(0);

				PersonShortData[] shorts = new PersonShortData[parent
						.getDatabaseRowCount()];
				PersonLongData[] longs = new PersonLongData[parent
						.getDatabaseRowCount()];
				Relation[][] relas = new Relation[parent.getDatabaseRowCount()][];
				for (int idx = 0; idx < shorts.length; idx++) {
					shorts[idx] = parent.getDatbasePerson(idx);
				}

				for (int i = 0; i < shorts.length; i++) {

					SukuData sdata = Suku.getKontroller().getSukuData(
							"cmd=person", "pid=" + shorts[i].getPid());
					longs[i] = sdata.persLong;
					relas[i] = sdata.relations;

					PersonShortData psp = new PersonShortData(longs[i]);
					System.out.println("PSP=" + psp.getAlfaName());
					float prose = (i * 100f) / shorts.length;
					setRunnerValue("" + (int) prose + ";" + psp.getAlfaName());
				}

				BufferedOutputStream bstr = new BufferedOutputStream(
						Suku.kontroller.getOutputStream());
				WritableWorkbook workbook = Workbook.createWorkbook(bstr);

				// Create a cell format for Times 16, bold and italic
				WritableFont arial10italic = new WritableFont(
						WritableFont.ARIAL, 10, WritableFont.NO_BOLD, true);
				WritableCellFormat arial10format = new WritableCellFormat(
						arial10italic);

				WritableFont arial10bold = new WritableFont(WritableFont.ARIAL,
						10, WritableFont.BOLD, true);
				WritableCellFormat arial0bold = new WritableCellFormat(
						arial10bold);

				for (int i = 0; i < shorts.length; i++) {
					float prose = (i * 100f) / shorts.length;
					setRunnerValue("" + (int) prose + ";2:"
							+ shorts[i].getAlfaName());
					int personNo = 1;
					int rivi = 0;
					PersonLongData thepers = longs[i];

					String cardTag = shorts[i].getRefn();
					if (cardTag == null) {
						cardTag = Resurses.getString("CARD_TAG") + " "
								+ (i + 1);
					}

					WritableSheet sheet = workbook.createSheet(cardTag, i);

					Label label = new Label(0, 0, Resurses
							.getString("CARD_NAME"), arial0bold);
					sheet.addCell(label);

					label = new Label(0, 1, "" + (personNo++));
					sheet.addCell(label);

					label = new Label(1, 1, Resurses
							.getString("CARD_PERSON_DATA"), arial0bold);
					sheet.addCell(label);

					rivi = 2;

					rivi = writePersonToSheet(rivi, thepers, sheet);

					rivi++;
					rivi++;
					Relation[] rr = relas[i];

					// first round = for parents
					for (int k = 0; k < rr.length; k++) {
						Relation r = rr[k];
						if (r.getTag().equals("FATH")
								|| r.getTag().equals("MOTH")) {

							rivi++;
							rivi++;
							label = new Label(0, rivi, "" + (personNo++));
							sheet.addCell(label);

							label = new Label(1, rivi,
									getTextValue(r.getTag()), arial0bold);
							sheet.addCell(label);
							label = new Label(2, rivi, Resurses
									.getString("CARD_PERSON_DATA"), arial0bold);
							sheet.addCell(label);

							rivi = writerRelationToExcel(rivi, sheet, r);
							SukuData ssdata = Suku.getKontroller().getSukuData(
									"cmd=person", "pid=" + r.getRelative());

							rivi++;
							rivi = writePersonToSheet(rivi, ssdata.persLong,
									sheet);

						}
						//						
					}

					for (int k = 0; k < rr.length; k++) {
						Relation r = rr[k];
						if (r.getTag().equals("WIFE")
								|| r.getTag().equals("HUSB")) {

							rivi++;
							rivi++;
							label = new Label(0, rivi, "" + (personNo++));
							sheet.addCell(label);

							label = new Label(1, rivi,
									getTextValue(r.getTag()), arial0bold);
							sheet.addCell(label);
							label = new Label(2, rivi, Resurses
									.getString("CARD_PERSON_DATA"), arial0bold);
							sheet.addCell(label);

							rivi = writerRelationToExcel(rivi, sheet, r);

							SukuData ssdata = Suku.getKontroller().getSukuData(
									"cmd=person", "pid=" + r.getRelative());

							rivi++;
							rivi = writePersonToSheet(rivi, ssdata.persLong,
									sheet);
						}
						//						
					}

					for (int k = 0; k < rr.length; k++) {
						Relation r = rr[k];
						if (r.getTag().equals("CHIL")) {
							rivi++;
							rivi++;
							label = new Label(0, rivi, "" + (personNo++));
							sheet.addCell(label);

							label = new Label(1, rivi,
									getTextValue(r.getTag()), arial0bold);
							sheet.addCell(label);
							label = new Label(2, rivi, Resurses
									.getString("CARD_PERSON_DATA"), arial0bold);
							sheet.addCell(label);

							rivi = writerRelationToExcel(rivi, sheet, r);

							SukuData ssdata = Suku.getKontroller().getSukuData(
									"cmd=person", "pid=" + r.getRelative());

							rivi++;
							rivi = writePersonToSheet(rivi, ssdata.persLong,
									sheet);
						}
						//						
					}

				}

				workbook.write();
				workbook.close();
				bstr.close();

			} catch (Exception e) {
				logger.log(Level.WARNING, "Exception in background thread", e);
			}

			return null;
		}

		private int writerRelationToExcel(int rivi, WritableSheet sheet,
				Relation r) throws WriteException, RowsExceededException {

			Label label;
			RelationNotice[] rnn = r.getNotices();

			if (rnn != null) {
				for (int l = 0; l < rnn.length; l++) {
					RelationNotice rn = rnn[l];
					label = new Label(1, ++rivi, getTextValue(rn.getTag()));
					sheet.addCell(label);
					if (rn.getFromDate() != null) {
						label = new Label(2, rivi, Utils.textDate(rn
								.getFromDate(), true));
						sheet.addCell(label);
					}

				}
			}
			return rivi;
		}

		private int writePersonToSheet(int rivi, PersonLongData thepers,
				WritableSheet sheet) throws WriteException,
				RowsExceededException {
			Label label;
			label = new Label(1, rivi, Resurses.getString("CARD_PERSON_SEX"));
			sheet.addCell(label);
			label = new Label(2, rivi, Resurses.getString("SEX_"
					+ thepers.getSex()));
			sheet.addCell(label);

			// label = new Label(1, 2,
			// Resurses.getString("CARD_PERSON_SURNAME"));
			// sheet.addCell(label);
			rivi++;
			UnitNotice unn[] = thepers.getNotices();
			boolean nameFound = false;
			boolean birtFound = false;
			boolean deatFound = false;
			boolean occuFound = false;

			for (int j = 0; j < unn.length; j++) {

				UnitNotice n = unn[j];
				String tag = n.getTag();
				if (tag.equals("BIRT"))
					birtFound = true;
				if (tag.equals("DEAT"))
					deatFound = true;
				if (tag.equals("OCCU"))
					occuFound = true;

				boolean showTag = isType(tag, 2);

				if (showTag) {
					rivi++;
					String tagv = getTagName(tag);
					label = new Label(1, rivi, tagv);
					sheet.addCell(label);

					if (tag.equals("NAME")) {
						nameFound = true;
						StringBuffer sb = new StringBuffer();
						if (n.getGivenname() == null) {
							sb.append("???");
						} else {
							sb.append(n.getGivenname());
						}
						if (n.getPatronym() != null) {
							sb.append(" ");
							sb.append(n.getPatronym());
						}
						if (n.getPrefix() != null) {
							sb.append(" ");
							sb.append(n.getPrefix());
						}
						sb.append(" ");
						sb.append(n.getSurname());
						if (n.getPostfix() != null) {
							sb.append(" ");
							sb.append(n.getPostfix());
						}

						label = new Label(3, rivi, sb.toString());
						sheet.addCell(label);
					} else {

						if (n.getNoticeType() != null) {
							label = new Label(2, rivi, n.getNoticeType());
							sheet.addCell(label);
						}
						if (n.getDescription() != null) {
							label = new Label(3, rivi, n.getDescription());
							sheet.addCell(label);
						}
						if (n.getFromDate() != null) {
							label = new Label(4, rivi, Utils.textDate(n
									.getFromDate(), true));
							sheet.addCell(label);
						}
						if (n.getPlace() != null) {
							label = new Label(5, rivi, n.getPlace());
							sheet.addCell(label);
						}
					}
				}

			}
			if (!nameFound) {
				String tagv = getTagName("NAME");
				label = new Label(1, ++rivi, tagv);
				sheet.addCell(label);

			}

			if (!birtFound) {
				boolean showTag = isType("BIRT", 2);
				if (showTag) {
					String tagv = getTagName("BIRT");
					label = new Label(1, ++rivi, tagv);
					sheet.addCell(label);
				}
			}
			if (!deatFound) {
				boolean showTag = isType("DEAT", 2);
				if (showTag) {
					String tagv = getTagName("DEAT");
					label = new Label(1, ++rivi, tagv);
					sheet.addCell(label);
				}
			}
			if (!occuFound) {
				boolean showTag = isType("OCCU", 2);
				if (showTag) {
					String tagv = getTagName("OCCU");
					label = new Label(1, ++rivi, tagv);
					sheet.addCell(label);
				}
			}
			return rivi;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setVisible(false);
			// if (dlista != null) {
			// dlista.getWriter().closeReport();
			// // dr.getFrame().setVisible(true);
			// }
		}
	}

	public void setRunnerValue(String juttu) {
		String[] kaksi = juttu.split(";");
		if (kaksi.length >= 2) {
			int progress = Integer.parseInt(kaksi[0]);
			progressBar.setIndeterminate(false);
			progressBar.setValue(progress);
			textContent.setText(kaksi[1]);

		} else {
			textContent.setText(juttu);
			progressBar.setIndeterminate(true);
			progressBar.setValue(0);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			String juttu = evt.getNewValue().toString();
			String[] kaksi = juttu.split(";");
			if (kaksi.length >= 2) {
				int progress = Integer.parseInt(kaksi[0]);
				progressBar.setIndeterminate(false);
				progressBar.setValue(progress);
				textContent.setText(kaksi[1]);
			} else {

				textContent.setText(juttu);
				int progre = progressBar.getValue();
				if (progre > 95) {
					progre = 0;

				} else {
					progre++;
				}
				progressBar.setIndeterminate(true);
				progressBar.setValue(progre);
			}
		}
	}

	class MyTypesModel extends AbstractTableModel {
		/**
		 * @throws SukuException
		 * 
		 */

		MyTypesModel() {

			try {
				SukuData reposet = Suku.kontroller.getSukuData("cmd=gettypes",
						"lang=" + Resurses.getLanguage());

				typesValues = new String[reposet.vvTypes.size()];
				for (int i = 0; i < reposet.vvTypes.size(); i++) {
					String tag = reposet.vvTypes.get(i)[0];
					typeTexts.put(tag, i);
					typesValues[i] = reposet.vvTypes.get(i)[1];

				}

				typesData = new Object[reposet.vvTypes.size()][6];
				typesTags = new String[reposet.vvTypes.size()];

				for (int i = 0; i < typesTags.length; i++) {
					String tag = reposet.vvTypes.get(i)[0];
					typesTags[i] = tag;
					typesData[i][0] = reposet.vvTypes.get(i)[1];
					typesData[i][1] = Boolean.valueOf(false);
					if ("|BIRT|DEAT|CHR|BURI|".indexOf(tag) > 0) {
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
					textTexts.put(tag, reposet.vvTexts.get(i)[1]);
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

		// private Object[][] typesData = {
		// {"Syntynyt", new Boolean(true),new Boolean(true), new Boolean(true)},
		// {"Kuollut", new Boolean(true),new Boolean(true), new Boolean(true)},
		// {"Nimi", new Boolean(false),new Boolean(true), new Boolean(true)},
		// {"Kastettu", new Boolean(true),new Boolean(true), new
		// Boolean(false)},
		// {"Haudattu", new Boolean(true),new Boolean(true), new
		// Boolean(false)},
		// {"Teksti", new Boolean(false),new Boolean(true), new Boolean(false)},
		// {"Ammatti", new Boolean(false),new Boolean(true), new Boolean(true)},
		// {"Elää", new Boolean(false),new Boolean(true), new Boolean(true)},
		// {"Tullut", new Boolean(false),new Boolean(true), new Boolean(true)},
		// {"Muuttanut pois", new Boolean(false),new Boolean(true), new
		// Boolean(true)},
		// {"Oppiarvo", new Boolean(false),new Boolean(true), new
		// Boolean(true)},
		// {"Kuva", new Boolean(false),new Boolean(true), new Boolean(true)},
		// {"Osoite", new Boolean(false),new Boolean(true), new Boolean(true)},
		// };

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
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
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
		 * Don't need to implement this method unless your table's data can
		 * change.
		 */
		public void setValueAt(Object value, int row, int col) {
			if (DEBUG) {
				System.out.println("Setting value at " + row + "," + col
						+ " to " + value + " (an instance of "
						+ value.getClass() + ")");
			}

			typesData[row][col] = value;
			fireTableCellUpdated(row, col);

			if (DEBUG) {
				System.out.println("New value of data:");
				printDebugData();
			}
		}

		private void printDebugData() {
			int numRows = getRowCount();
			int numCols = getColumnCount();

			for (int i = 0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j = 0; j < numCols; j++) {
					System.out.print("  " + typesData[i][j]);
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}
	}

	private void setRadioButton(ButtonGroup g, String name) {
		Enumeration<AbstractButton> e = g.getElements();
		while (e.hasMoreElements()) {
			JRadioButton rr = (JRadioButton) e.nextElement();
			if (name.equals(rr.getActionCommand())) {
				rr.setSelected(true);
				break;
			}
		}
	}

	// protected JComponent makeTextPanel(String text) {
	// JPanel panel = new JPanel(false);
	// // JLabel filler = new JLabel(text);
	// //filler.setHorizontalAlignment(JLabel.CENTER);
	// panel.setLayout(null);//new GridLayout(1, 1));
	// // panel.add(filler);
	// // filler.setBounds(10,10,100,20);
	// return panel;
	// }

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path) {

		InputStream in = this.getClass().getResourceAsStream(path);
		BufferedImage icon;
		try {
			icon = ImageIO.read(in);
			return new ImageIcon(icon);

		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}

	}

	// class SettingxModel implements ComboBoxModel {
	//
	// @Override
	// public Object getSelectedItem() {
	// if (settingsList != null && settingsIndex < settingsList.length){
	// return settingsList[settingsIndex];
	// }
	// return null;
	//			
	// }
	//
	// @Override
	// public void setSelectedItem(Object arg) {
	//			
	// // settingsList[settingsIndex] = (String)arg;
	//			
	// System.out.println("Tuli:   setSele [" + settingsIndex + "]: " +arg);
	//			
	// }
	//
	// @Override
	// public void addListDataListener(ListDataListener arg0) {
	// System.out.println("tuli add");
	//			
	// }
	//
	// @Override
	// public Object getElementAt(int index) {
	// if (settingsList != null && index < settingsList.length){
	// return settingsList[index];
	// }
	// return null;
	// }
	//
	// @Override
	// public int getSize() {
	// if (settingsList != null){
	// return settingsList.length;
	// }
	// return 0;
	// }
	//
	// @Override
	// public void removeListDataListener(ListDataListener arg0) {
	// System.out.println("tuli remove");
	//			
	// }
	//		
	// }

}
