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
import java.util.Arrays;
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
import javax.swing.JTextField;
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
import fi.kaila.suku.kontroller.SukuKontrollerLocalImpl;
import fi.kaila.suku.report.AncestorReport;
import fi.kaila.suku.report.CommonReport;
import fi.kaila.suku.report.DescendantLista;
import fi.kaila.suku.report.DescendantReport;
import fi.kaila.suku.report.JavaReport;
import fi.kaila.suku.report.PersonInTables;
import fi.kaila.suku.report.ReportInterface;
import fi.kaila.suku.report.XmlReport;
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

/**
 * 
 * All reports are requested using this dialog and settings in it
 * 
 * @author Kalle
 * 
 */
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

	/**
	 * Stradoniz ancestor numbering command
	 */
	public static final String SET_ANC_STRADONIZ = "STRADONIZ";
	/**
	 * Hager ancestor numbering command
	 */
	public static final String SET_ANC_HAGER = "HAGER";
	/**
	 * Espolin ancestor numbering command
	 */
	public static final String SET_ANC_ESPOLIN = "ESPOLIN";

	/**
	 * Table descendant table numbering
	 */
	public static final String SET_ORDER_TAB = "TAB";
	/**
	 * Table descendant table numbering with male descendants only
	 */
	public static final String SET_ORDER_MALE = "MALE";
	/**
	 * Table descendant table numbering with female descendants only
	 */
	public static final String SET_ORDER_FEMALE = "FEMALE";
	/**
	 * Table descendant table numbering with male descendants first if both
	 * spouses are in report as relatives
	 */
	public static final String SET_ORDER_FIRSTMALE = "FIRSTMALE";
	/**
	 * When male first table order the numbering must be calculated twice. This
	 * is for the second round
	 */
	public static final String SET_ORDER_NEWMALE = "NEWMALE";
	/**
	 * Reguistry tabel ordering. Popular in the anglosaxon world
	 */
	public static final String SET_ORDER_REG = "REG";

	/**
	 * No marriages dates are shown
	 */
	public static final String SET_SPOUSE_NONE = "NONE";
	/**
	 * Only marriage yer ius printed
	 */
	public static final String SET_SPOUSE_YEAR = "YEAR";
	/**
	 * Full marriage date is printed
	 */
	public static final String SET_SPOUSE_DATE = "DATE";
	/**
	 * All marriage data is printed
	 */
	public static final String SET_SPOUSE_FULL = "FULL";

	private static final String ACTION_INDEX = "ACTION_INDEX";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean DEBUG = false;
	private static final String EXIT = "EXIT";
	private static final String START = "START";
	private static final String LISTA = "REPORT.INDEX";
	private JLabel textContent;

	private JButton cancel;
	private JButton start;
	private JButton lista;
	private static Logger logger = Logger.getLogger(ReportWorkerDialog.class
			.getName());

	private SukuKontroller kontroller = null;

	private JProgressBar progressBar;
	private Task task;
	private TaskLista taskLista;
	private TaskCards taskCards;
	private TaskIndex taskIndex;
	private ReportWorkerDialog self;

	JTabbedPane reportTypePane = null;

	/**
	 * @return this class instance. Used to show the progressbar in local
	 *         kontrolelr only
	 */
	public static ReportWorkerDialog getRunner() {
		return runner;
	}

	private JCheckBox commonWithImages = null;
	private JTextField commonImageHeight = null;
	private JTextField commonPersonImageHeight = null;
	private JCheckBox commonNumberImages = null;
	private JCheckBox commonBendNames = null;
	private JCheckBox commonSeparateNotices = null;
	private ButtonGroup commonDateFormatGroup = null;
	private ButtonGroup commonSourcesFormatGroup = null;
	private JComboBox commonReportFormatList = null;
	private JCheckBox commonDebugCheck = null;
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

	String[] viewnames = null;
	int[] viewids = null;

	private ButtonGroup listaGroup = null;

	private JComboBox viewlist = null;
	private ButtonGroup spouseData = null;

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
	 * Constructor
	 * 
	 * @param owner
	 * @param kontroller
	 * @param pers
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

	/**
	 * @return Suku main program
	 */
	public ISuku getSukuParent() {
		return parent;
	}

	/**
	 * @return max height of image (in pixels)
	 */
	public int getImageMaxHeight() {
		try {
			return Integer.parseInt(commonImageHeight.getText());
		} catch (NumberFormatException ne) {
			return 0;
		}
	}

	/**
	 * @return max height of pewrsonimage (in pixels)
	 */
	public int getPersonImageMaxHeight() {
		try {
			return Integer.parseInt(commonPersonImageHeight.getText());
		} catch (NumberFormatException ne) {
			return 0;
		}
	}

	/**
	 * @return true if images are to be numbered
	 */
	public boolean isNumberingImages() {
		return commonNumberImages.isSelected();
	}

	/**
	 * @return subject pid
	 */
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
	 * @param idx
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

	/**
	 * @param tag
	 * @return name of tag e.g. BIRT tag returns Birth in English
	 */
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

	/**
	 * @return true if bold names box is set
	 */
	public boolean showBoldNames() {
		return (commonNamesBold.getSelectedObjects() != null);
	}

	/**
	 * @return true is underline usename is set
	 */
	public boolean showUnderlineNames() {
		return (commonNamesUnderline.getSelectedObjects() != null);
	}

	/**
	 * @return true if selected to print notices on separate line
	 */
	public boolean showOnSeparateLines() {
		return (commonSeparateNotices.getSelectedObjects() != null);
	}

	/**
	 * @return true is selected to print images
	 */
	public boolean showImages() {
		return (commonWithImages.getSelectedObjects() != null);
	}

	/**
	 * @return true to print address fields
	 */
	public boolean showAddress() {
		return (commonWithAddress.getSelectedObjects() != null);
	}

	/**
	 * @return dateformat selected
	 */
	public String getDateFormat() {
		ButtonModel model = commonDateFormatGroup.getSelection();
		if (model != null) {
			return model.getActionCommand();
		}
		return "FI";
	}

	/**
	 * @return selected kontroller
	 */
	public SukuKontroller getKontroller() {
		return this.kontroller;
	}

	/**
	 * @return progressbar object
	 */
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

		commonImageHeight = new JTextField();
		commonImageHeight.setBounds(x4, y1 + 22, 60, 20);
		add(commonImageHeight);

		lb = new JLabel(Resurses.getString("REPORT.IMAGE.HEIGHT"));
		add(lb);
		lb.setBounds(x4 + 64, y1 + 22, 100, 20);

		commonPersonImageHeight = new JTextField();
		commonPersonImageHeight.setBounds(x4, y1 + 44, 60, 20);
		add(commonPersonImageHeight);

		lb = new JLabel(Resurses.getString("REPORT.PERSONIMAGE.HEIGHT"));
		add(lb);
		lb.setBounds(x4 + 64, y1 + 44, 100, 20);

		commonNumberImages = new JCheckBox(Resurses
				.getString("REPORT.IMAGE.NUMBER"), true);
		commonNumberImages.setBounds(x4, y1 + 66, 160, 20);
		add(commonNumberImages);

		commonBendNames = new JCheckBox(Resurses.getString("REPORT.BENDNAMES"),
				true);
		commonBendNames.setBounds(x4, y1 + 88, 160, 20);
		add(commonBendNames);

		commonSeparateNotices = new JCheckBox(Resurses
				.getString("REPORT.SEPARATENOTICES"), true);
		commonSeparateNotices.setBounds(x4, y1 + 110, 160, 20);
		add(commonSeparateNotices);

		commonDateFormatGroup = new ButtonGroup();

		commonNamesBold = new JCheckBox(Resurses.getString("REPORT.NAME.BOLD"));
		commonNamesBold.setBounds(x4, y1 + 132, 160, 20);
		add(commonNamesBold);

		commonNamesUnderline = new JCheckBox(Resurses
				.getString("REPORT.NAME.UNDERLINE"));
		commonNamesUnderline.setBounds(x4, y1 + 154, 160, 20);
		add(commonNamesUnderline);

		commonWithAddress = new JCheckBox(Resurses
				.getString("REPORT.WITHADDERSS"), true);
		commonWithAddress.setBounds(x4, y1 + 176, 160, 20);
		add(commonWithAddress);

		spouseData = new ButtonGroup();

		int rtypy = y1 + 198;
		pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("REPORT.DESC.SPOUSE")));
		pane.setLayout(new GridLayout(0, 1));

		pane.setBounds(x4 - 5, rtypy, 160, 120);

		JRadioButton radio = new JRadioButton(Resurses
				.getString("REPORT.DESC.SPOUSE.NONE"));
		spouseData.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_SPOUSE_NONE);
		pane.add(radio);

		radio = new JRadioButton(Resurses.getString("REPORT.DESC.SPOUSE.YEAR"));
		radio.setSelected(true);
		spouseData.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_SPOUSE_YEAR);
		pane.add(radio);

		radio = new JRadioButton(Resurses.getString("REPORT.DESC.SPOUSE.DATE"));
		spouseData.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_SPOUSE_DATE);
		pane.add(radio);

		radio = new JRadioButton(Resurses.getString("REPORT.DESC.SPOUSE.FULL"));
		spouseData.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_SPOUSE_FULL);
		pane.add(radio);
		add(pane);

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
		if (kontroller instanceof SukuKontrollerLocalImpl) {
			v.add(Resurses.getString("REPORT.FORMAT.WORD2003"));
			v.add(Resurses.getString("REPORT.FORMAT.HTML"));
			// v.add(Resurses.getString("REPORT.FORMAT.XML"));
		}

		lb = new JLabel(Resurses.getString("REPORT.FORMAT"));
		lb.setBounds(x3, footery, 200, 20);
		add(lb);

		commonReportFormatList = new JComboBox(v);
		commonReportFormatList.setBounds(x3, footery + 25, 200, 20);
		add(commonReportFormatList);

		commonDebugCheck = new JCheckBox(Resurses.getString("REPORT.DEBUG"));
		commonDebugCheck.setBounds(x3, footery + 50, 200, 20);
		add(commonDebugCheck);

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

		try {
			SukuData vlist = kontroller.getSukuData("cmd=viewlist");
			viewnames = new String[vlist.generalArray.length + 1];

			viewids = new int[vlist.generalArray.length + 1];
			viewnames[0] = "";
			viewids[0] = 0;
			for (int i = 0; i < vlist.generalArray.length; i++) {
				String[] parts = vlist.generalArray[i].split(";");
				viewnames[i + 1] = parts[1];
				viewids[i + 1] = Integer.parseInt(parts[0]);
			}

		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

		viewlist = new JComboBox(viewnames);
		listaPanel.add(viewlist);
		viewlist.setBounds(10, tabh - 60, 200, 20);

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

		this.lista = new JButton(Resurses.getString(LISTA));
		getContentPane().add(this.lista);
		this.lista.setBounds(140, footery + 60, 100, 24);
		this.lista.setActionCommand(LISTA);
		this.lista.addActionListener(this);
		this.lista.setEnabled(false);

		this.cancel = new JButton(Resurses.getString(EXIT));
		getContentPane().add(this.cancel);
		this.cancel.setBounds(250, footery + 60, 100, 24);
		this.cancel.setActionCommand(EXIT);
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

	/**
	 * @return descendat panel
	 */
	public DescendantPane getDescendantPane() {
		return descendantPanel;
	}

	/**
	 * @return ancestor panel
	 */
	public AncestorPane getAncestorPane() {
		return ancestorPanel;
	}

	/**
	 * Debug state is used to get some extra output for debugging such as raw
	 * xml-file
	 * 
	 * @return true if debug state is checked
	 */
	public boolean getDebugState() {
		if (commonDebugCheck.isSelected()) {
			return true;
		}
		return false;
	}

	boolean isLoadingTheSettings = false;

	private void loadReportSettings() {
		isLoadingTheSettings = true;
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
			boolean numberImages = false;
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
			int vid = 0;
			for (int i = 0; i < sets.vvTypes.size(); i++) {

				vx = sets.vvTypes.get(i);
				if (vx[0].equals("bend")) {
					bendNames = true;
				} else if (vx[0].equals("imagenumber")) {
					numberImages = true;
				} else if (vx[0].equals("images")) {
					withImages = true;
				} else if (vx[0].equals("viewId")) {
					try {
						vid = Integer.parseInt(vx[1]);
					} catch (NumberFormatException ne) {
					}
					for (int j = 0; j < viewids.length; j++) {
						if (viewids[j] == vid) {
							viewlist.setSelectedIndex(j);
							break;
						}
					}
				} else if (vx[0].equals("imagewidth")) {
					commonImageHeight.setText(vx[1]);
				} else if (vx[0].equals("personimagewidth")) {
					commonPersonImageHeight.setText(vx[1]);
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
				} else if (vx[0].equals("ancgen")) {
					ancestorPanel.setGenerations(vx[1]);
				} else if (vx[0].equals("descadopted")) {
					descendantAdopted = true;

				} else if (vx[0].equals("descspanc")) {

					descendantPanel.setSpouseAncestors(vx[1]);
				} else if (vx[0].equals("descchanc")) {

					descendantPanel.setChildAncestors(vx[1]);

				} else if (vx[0].equals("descTableOrder")) {
					setRadioButton(descendantPanel.getTableOrder(), vx[1]);
				} else if (vx[0].equals("descSpouseData")) {
					setRadioButton(getSpouseData(), vx[1]);
				} else if (vx[0].equals("listaGroup")) {
					setRadioButton(listaGroup, vx[1]);
				} else if (vx[0].equals("ancFamily")) {
					ancestorFamily = true;
				} else if (vx[0].equals("ancNumbering")) {
					setRadioButton(ancestorPanel.getNumberingFormat(), vx[1]);
				} else if (vx[0].equals("ancDesc")) {
					ancestorPanel.setDescGen(vx[1]);
				} else if (vx[0].equals("format")) {
					int formIdx;
					try {
						formIdx = Integer.parseInt(vx[1]);
						if (formIdx >= commonReportFormatList.getItemCount()) {
							formIdx = 0;
						}
					} catch (NumberFormatException ne) {
						formIdx = 0;
					}

					commonReportFormatList.setSelectedIndex(formIdx);
				} else if (vx[0].startsWith("t:")) {

					int typeCount = typesTable.getRowCount();
					for (int row = 0; row < typeCount; row++) {

						String tag = typesTags[row];

						if (vx[0].substring(2).equals(tag)
								&& vx[1].length() > 3) {
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
							if (vx[1].substring(2, 3).equals("X")) {
								b = true;
							}
							typesTable.setValueAt(b, row, 3);

							b = false;
							if (vx[1].substring(3, 4).equals("X")) {
								b = true;
							}
							typesTable.setValueAt(b, row, 4);
							// String newText = "";
							// if (vx[1].length() > 4) {
							// newText = vx[1].substring(4);
							// }
							// typesTable.setValueAt(newText, row, 5);
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
			commonNumberImages.setSelected(numberImages);
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
		isLoadingTheSettings = false;
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
			if (!isLoadingTheSettings && settingsName.getSelectedIndex() >= 0) {
				settingsIndex = settingsName.getSelectedIndex();
				loadReportSettings();

			}

			return;
		}
		if (cmd.equals(EXIT)) {
			if (this.task == null) {
				setVisible(false);
			} else {
				this.task.cancel(true);
				this.task = null;
				setVisible(false);
			}
		}

		if (cmd.equals(START)) {

			int i = reportTypePane.getSelectedIndex();

			String listSele = null;
			switch (i) {
			case 0:
			case 1:
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
		} else if (cmd.equals(LISTA)) {
			taskIndex = new TaskIndex();
			taskIndex.addPropertyChangeListener(this);
			taskIndex.execute();
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
		try {
			int ww = Integer.parseInt(commonImageHeight.getText());
			v.add("imagewidth=" + ww);
		} catch (NumberFormatException ne) {
		}
		try {
			int ww = Integer.parseInt(commonPersonImageHeight.getText());
			v.add("personimagewidth=" + ww);
		} catch (NumberFormatException ne) {
		}
		v.add("format=" + "" + commonReportFormatList.getSelectedIndex());

		if (commonBendNames.getSelectedObjects() != null) {
			v.add("bend=true");
		}
		if (commonNumberImages.getSelectedObjects() != null) {
			v.add("imagenumber=true");
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
		v.add("ancgen=" + ancestorPanel.getGenerations());
		if (descendantPanel.getAdopted()) {
			v.add("descadopted=true");
		}
		v.add("descspanc=" + descendantPanel.getSpouseAncestors());
		v.add("descchanc=" + descendantPanel.getChildAncestors());

		model = descendantPanel.getTableOrder().getSelection();
		if (model != null) {
			v.add("descTableOrder=" + model.getActionCommand());
		}

		model = getSpouseData().getSelection();
		if (model != null) {
			v.add("descSpouseData=" + model.getActionCommand());
		}

		model = ancestorPanel.getNumberingFormat().getSelection();
		if (model != null) {
			v.add("ancNumbering=" + model.getActionCommand());
		}
		if (ancestorPanel.getShowfamily()) {
			v.add("ancFamily=true");
		}
		String tmp = ancestorPanel.getShowDescGen();
		if (tmp != null && tmp.length() > 0) {
			v.add("ancDesc=" + tmp);
		}
		model = listaGroup.getSelection();
		if (model != null) {
			v.add("listaGroup=" + model.getActionCommand());
		}

		int vidx = viewlist.getSelectedIndex();
		int viid = viewids[vidx];
		v.add("viewId=" + viid);
		int typeCount = typesTable.getRowCount();

		for (int row = 0; row < typeCount; row++) {
			StringBuffer sb = new StringBuffer();
			sb.append("t:");
			sb.append(typesTags[row]);
			sb.append("=");
			sb.append(((Boolean) typesTable.getValueAt(row, 1)) ? "X" : "O");
			sb.append(((Boolean) typesTable.getValueAt(row, 2)) ? "X" : "O");
			sb.append(((Boolean) typesTable.getValueAt(row, 3)) ? "X" : "O");
			sb.append(((Boolean) typesTable.getValueAt(row, 4)) ? "X" : "O");
			sb.append(typesTable.getValueAt(row, 5));
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
	 * @return spouse data button group
	 */
	public ButtonGroup getSpouseData() {
		return spouseData;
	}

	CommonReport dr = null;

	class Task extends SwingWorker<Void, Void> {

		int reportFormatidx = 0;

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {

			try {

				ReportInterface repo = null;

				reportFormatidx = commonReportFormatList.getSelectedIndex();

				if (reportFormatidx == 0) {
					repo = new JavaReport();
				} else {
					try {
						repo = new XmlReport(runner, reportFormatidx, self.pers
								.getAlfaName(true));
					} catch (SukuException se) {
						JOptionPane.showMessageDialog(runner, se.getMessage(),
								Resurses.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);
						logger.log(Level.WARNING,
								"Exception in background thread", se);
						return null;
					}
				}
				setProgress(0);

				if (reportTypePane.getSelectedIndex() == 0) {

					dr = new DescendantReport(self, repo);
				} else {
					dr = new AncestorReport(self, repo);
				}
				dr.executeReport();

			} catch (Exception e) {
				JOptionPane.showMessageDialog(runner, e.getMessage(), Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
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
			start.setEnabled(false);
			lista.setEnabled(true);
			if (reportFormatidx == 0) {
				setVisible(false);
			}

			if (dr != null) {
				try {
					dr.getWriter().closeReport();
				} catch (SukuException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							Resurses.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);
				}
				dr.setVisible(true);
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
					}
				}

				workbook.write();
				workbook.close();
				bstr.close();

			} catch (Throwable e) {
				// FIX-ME: Spaghetti code. Could be.
				// Changed to Throwable. I assume that should catch anything
				// Withaout a catch here We wouöld not get anything into the log
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

	/**
	 * Split the value for the progressbar and text in two parts using ; if no ;
	 * then value is text shown and progressbar shows only movement if ; in text
	 * then first part must be a number between 0 and 100. Second part is text
	 * to show
	 * 
	 * @param juttu
	 */
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

	/**
	 * Create index for the report executed
	 */
	class TaskIndex extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			try {

				if (!Suku.kontroller.createLocalFile("xls")) {
					return null;
				}

				setProgress(0);

				BufferedOutputStream bstr = new BufferedOutputStream(
						Suku.kontroller.getOutputStream());
				WritableWorkbook workbook = Workbook.createWorkbook(bstr);

				WritableFont arial10bold = new WritableFont(WritableFont.ARIAL,
						10, WritableFont.BOLD, true);
				WritableFont arial10 = new WritableFont(WritableFont.ARIAL, 10,
						WritableFont.NO_BOLD, false);
				WritableCellFormat arial0bold = new WritableCellFormat(
						arial10bold);
				WritableCellFormat arial0 = new WritableCellFormat(arial10);
				WritableSheet sheet = workbook.createSheet("Index", 0);

				Label label = new Label(0, 0, "Nimi", arial0bold);
				sheet.addCell(label);

				label = new Label(1, 0, "Taulu", arial0bold);
				sheet.addCell(label);

				int row = 2;
				if (dr != null) {

					// Set<Map.Entry<Integer, PersonInTables>> entries = dr
					// .getPersonReferences().entrySet();
					// Iterator<Map.Entry<Integer, PersonInTables>> ee = entries
					// .iterator();
					Vector<PersonInTables> vv = dr.getPersonReferences();
					// new Vector<PersonInTables>();
					float runnervalue = 0;
					float mapsize = dr.getPersonReferences().size();
					// while (ee.hasNext()) {
					// Map.Entry<Integer, PersonInTables> entry =
					// (Map.Entry<Integer, PersonInTables>) ee
					// .next();
					//
					// PersonInTables pit = entry.getValue();
					for (int j = 0; j < vv.size(); j++) {
						PersonInTables pit = vv.get(j);
						// vv.add(pit);
						if (pit.shortPerson == null) {
							SukuData resp = Suku.kontroller.getSukuData(
									"cmd=person", "mode=short", "pid="
											+ pit.pid);

							if (resp.pers != null) {
								pit.shortPerson = resp.pers[0];

								for (int i = 1; i < pit.shortPerson
										.getNameCount(); i++) {
									PersonInTables pitt = new PersonInTables(
											pit.shortPerson.getPid());
									pitt.asChildren = pit.asChildren;
									pitt.asOwner = pit.asOwner;
									pitt.asParents = pit.asParents;
									PersonShortData p = pit.shortPerson;
									PersonShortData alias = new PersonShortData(
											p.getPid(), p.getGivenname(i), p
													.getPatronym(i), p
													.getPrefix(i), p
													.getSurname(i), p
													.getPostfix(i), p
													.getBirtDate(), p
													.getDeatDate());
									pitt.shortPerson = alias;
									// vv.add(pitt);
								}

							}

						}

						float prose = (runnervalue * 100f) / mapsize;
						if (prose > 100)
							prose = 100;
						setRunnerValue("" + (int) prose + ";"
								+ pit.shortPerson.getAlfaName());
						runnervalue++;
					}
					PersonInTables[] pits = vv.toArray(new PersonInTables[0]);
					Arrays.sort(pits);

					for (int i = 0; i < pits.length; i++) {
						PersonInTables pit = pits[i];
						label = new Label(0, row, ""
								+ pit.shortPerson.getAlfaName(), arial0);
						sheet.addCell(label);
						String kefe = pit.getReferences(0, true, false, false);
						String cefe = pit.getReferences(0, false, true, false);
						String refe = kefe;

						if (pit.asOwner == 0) {

							if (refe.equals("")) {
								refe = cefe;
							} else {
								if (!cefe.equals("")) {
									refe += "," + cefe;
								}
							}
						}
						// System.out.println("RUN:"
						// + pit.shortPerson.getAlfaName());
						String mefe = pit.getReferences(0, false, false, true);

						if (!mefe.equals("")) {
							if (!refe.equals("")) {
								refe += "," + mefe;
							} else {
								refe = mefe;
							}
						}

						label = new Label(1, row, "" + refe, arial0);
						sheet.addCell(label);

						if (pit.shortPerson.getBirtDate() != null) {
							label = new Label(2, row, Utils.nv4(pit.shortPerson
									.getBirtDate()), arial0);
							sheet.addCell(label);
						}
						if (pit.shortPerson.getDeatDate() != null) {
							label = new Label(3, row, Utils.nv4(pit.shortPerson
									.getDeatDate()), arial0);
							sheet.addCell(label);
						}

						if (pit.asOwner > 0) {
							label = new Label(5, row, "" + pit.asOwner, arial0);
							sheet.addCell(label);
						}

						label = new Label(6, row, "" + pit.pid, arial0);
						sheet.addCell(label);
						label = new Label(7, row, "" + kefe, arial0);
						sheet.addCell(label);
						label = new Label(8, row, "" + cefe, arial0);
						sheet.addCell(label);
						label = new Label(9, row, "" + mefe, arial0);
						sheet.addCell(label);

						float prose = (i * 100f) / pits.length;
						setRunnerValue("" + (int) prose + ";"
								+ pit.shortPerson.getAlfaName());

						// label = new Label(3, row, ""
						// + pit.getReferences(0, false, true, false),
						// arial0);
						// sheet.addCell(label);
						// label = new Label(3, row, ""
						// + pit.getReferences(0, false, false, true),
						// arial0);
						// sheet.addCell(label);
						row++;
					}
				}
				workbook.write();
				workbook.close();
				bstr.close();

			} catch (Throwable e) {

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

		}
	}

}
