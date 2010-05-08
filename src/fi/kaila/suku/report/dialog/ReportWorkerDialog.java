package fi.kaila.suku.report.dialog;

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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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
import fi.kaila.suku.report.AncestorTableReport;
import fi.kaila.suku.report.CommonReport;
import fi.kaila.suku.report.DescendantLista;
import fi.kaila.suku.report.DescendantReport;
import fi.kaila.suku.report.GenGraphReport;
import fi.kaila.suku.report.JavaReport;
import fi.kaila.suku.report.PersonInTables;
import fi.kaila.suku.report.ReportInterface;
import fi.kaila.suku.report.XmlReport;
import fi.kaila.suku.swing.ISuku;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuTypesTable;
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

	// date formats
	private static final String SET_FI = "FI";
	private static final String SET_SE = "SE";
	private static final String SET_UK = "UK";
	private static final String SET_US = "US";
	// source format
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
	 * Registry table ordering. Popular in the anglosaxon world
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
	/** */
	public static final String SET_ANC_TABLES = "SET_ANC_TABLES";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private boolean DEBUG = false;
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
	private TaskSureties taskSureties;
	private TaskAddresses taskAddresses;
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
	private JTextField commonImageSize = null;
	private JTextField commonPersonImageSize = null;
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
	private SukuTypesTable typesTable = null;

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
	private OtherReportsPane otherPane;
	private JPanel listaPanel;
	// private ReportFrame repo;
	private PersonShortData pers = null;
	private static ReportWorkerDialog runner = null;

	private static final int x1 = 10;
	private static final int x2 = 250;
	private static final int x3 = 440;
	private static final int x4 = 620;
	private static final int xtype = 310;
	private static final int y1 = 20;
	// private static final int y2 = 250;
	private static final int y3 = 390;
	// private static final int y4 = 420;

	private static final int tabh = 360;
	private static final int tabw = 300;

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
	public Dimension getImageMaxSize() {
		String aux = commonImageSize.getText();
		return toImageDimension(aux);
	}

	private Dimension toImageDimension(String aux) {
		if (aux.isEmpty()) {
			return new Dimension(0, 0);
		}

		String[] parts = aux.split("x");
		int[] parties = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			try {
				parties[i] = Integer.parseInt(parts[i]);
			} catch (NumberFormatException ne) {
				parties[i] = 0;
			}

		}

		if (parts.length == 1) {
			if (aux.charAt(0) == 'x') {
				return new Dimension(0, parties[0]);
			} else {
				return new Dimension(parties[0], 0);
			}
		}

		if (parts.length == 2) {
			return new Dimension(parties[0], parties[1]);
		}
		return new Dimension(0, 0);
	}

	/**
	 * @return max height of person image (in pixels)
	 */
	public Dimension getPersonImageMaxSize() {

		String aux = commonPersonImageSize.getText();
		return toImageDimension(aux);
	}

	/**
	 * @return true if images are to be numbered
	 */
	public boolean isNumberingImages() {
		return commonNumberImages.isSelected();
	}

	/**
	 * @return true if place names are to be bent
	 */
	public boolean isBendPlaces() {
		return commonBendNames.isSelected();
	}

	/**
	 * @return subject pid
	 */
	public int getPid() {
		return this.pers.getPid();
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
	 * @return date format selected
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
	 * @return progress bar object
	 */
	public JProgressBar getProgressBar() {
		return this.progressBar;
	}

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
		if (pers == null) {
			lb = new JLabel("UNDER CONSTRUCTION");
		} else {
			lb = new JLabel(this.pers.getAlfaName(true));
		}
		add(lb);
		lb.setBounds(x1, y1 - 20, 300, 20);

		typesTable = new SukuTypesTable(new Dimension(500, 70));
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
		c = modl.getColumn(5);
		c.setMaxWidth(checkWidth);

		ImageIcon icon1 = createImageIcon("/images/jalkipolvi.gif");
		ImageIcon icon2 = createImageIcon("/images/esipolvi.gif");
		ImageIcon icon3 = createImageIcon("/images/muupolvi.gif");

		commonWithImages = new JCheckBox(Resurses
				.getString("REPORT.WITHIMAGES"), true);
		commonWithImages.setBounds(x4, y1, 160, 20);
		add(commonWithImages);

		commonImageSize = new JTextField();
		commonImageSize.setBounds(x4, y1 + 22, 60, 20);
		add(commonImageSize);

		lb = new JLabel(Resurses.getString("REPORT.IMAGE.HEIGHT"));
		add(lb);
		lb.setBounds(x4 + 64, y1 + 22, 100, 20);

		commonPersonImageSize = new JTextField();
		commonPersonImageSize.setBounds(x4, y1 + 44, 60, 20);
		add(commonPersonImageSize);

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

		if (pers != null) {

			descendantPanel = new DescendantPane();

			reportTypePane.addTab(Resurses.getString("REPORT.DESCENDANT"),
					icon1, descendantPanel, Resurses
							.getString("REPORT.TIP.DESCENDANT"));

			reportTypePane.setMnemonicAt(0, KeyEvent.VK_1);

			ancestorPanel = new AncestorPane();

			reportTypePane.addTab(Resurses.getString("REPORT.ANCESTOR"), icon2,
					ancestorPanel, Resurses.getString("REPORT.TIP.ANCESTOR"));
			reportTypePane.setMnemonicAt(1, KeyEvent.VK_2);

			otherPane = new OtherReportsPane();
			reportTypePane.addTab(Resurses.getString("REPORT.OTHER"), icon3,
					otherPane, Resurses.getString("REPORT.TIP.OTHER"));
			reportTypePane.setMnemonicAt(2, KeyEvent.VK_3);
		} else {
			listaPanel = new JPanel();
			listaPanel.setLayout(null);

			listaPanel.setPreferredSize(new Dimension(410, 50));
			reportTypePane.addTab(Resurses.getString("REPORT.LISTAT"), icon3,
					listaPanel, Resurses.getString("REPORT.TIP.LISTAT"));
			reportTypePane.setMnemonicAt(0, KeyEvent.VK_1);
		}
		//				
		// reportTypePane.addTab(Resurses.getString("REPORT.LISTAT"), icon3,
		// listaPanel,
		// Resurses.getString("REPORT.TIP.LISTAT"));

		// Add the tabbed pane to this panel.
		add(reportTypePane);

		// The following line enables to use scrolling tabs.
		reportTypePane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		reportTypePane.setBounds(x1, y1, tabw, tabh);

		if (pers == null) {
			listaGroup = new ButtonGroup();

			JRadioButton listad = new JRadioButton(Resurses
					.getString("REPORT.LISTA.PERSONCARDS"));
			listaPanel.add(listad);
			listad.setBounds(10, 20, 200, 20);
			listad.setActionCommand("REPORT.LISTA.PERSONCARDS");
			listaGroup.add(listad);

			listad = new JRadioButton(Resurses
					.getString("REPORT.LISTA.SURETIES"));
			listaPanel.add(listad);
			listad.setBounds(10, 44, 200, 20);
			listad.setActionCommand("REPORT.LISTA.SURETIES");
			listaGroup.add(listad);

			listad = new JRadioButton(Resurses
					.getString("REPORT.LISTA.ADDRESSES"));
			listaPanel.add(listad);
			listad.setBounds(10, 68, 200, 20);
			listad.setActionCommand("REPORT.LISTA.ADDRESSES");
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
		}
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
	 * 
	 * @return handle to other reports pane
	 */
	public OtherReportsPane getOtherPane() {
		return otherPane;
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
			String type = "report";
			if (pers == null) {
				type = "lista";
			}
			SukuData sets = Suku.kontroller.getSukuData("cmd=getsettings",
					"type=" + type, "index=" + settingsIndex);
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

				} else if (vx[0].equals("imagesize")) {
					commonImageSize.setText(vx[1]);
				} else if (vx[0].equals("personimagesize")) {
					commonPersonImageSize.setText(vx[1]);
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
				} else if (pers != null) {
					if (vx[0].equals("descgen")) {
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
					} else if (vx[0].equals("ancNumbering")) {
						setRadioButton(ancestorPanel.getNumberingFormat(),
								vx[1]);
					} else if (vx[0].equals("ancDesc")) {
						ancestorPanel.setDescGen(vx[1]);
					} else if (vx[0].equals("ancFamily")) {
						ancestorFamily = true;
					}
				} else if (pers == null) {
					if (vx[0].equals("viewId")) {
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
					} else if (vx[0].equals("listaGroup")) {
						setRadioButton(listaGroup, vx[1]);
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
			if (pers != null) {
				descendantPanel.setAdopted(descendantAdopted);
				ancestorPanel.setShowFamily(ancestorFamily);
			}
			typesTable.loadReportSettings(type + "types", settingsIndex);

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

			String listSele = null;
			if (pers != null) {

				String order = descendantPanel.getTableOrder().getSelection()
						.getActionCommand();
				if (order.equals("REPORT.LISTA.DESCLISTA")) {
					taskLista = new TaskLista();
					taskLista.addPropertyChangeListener(this);
					taskLista.execute();
				} else {
					// we create new instances as needed.
					task = new Task();
					task.addPropertyChangeListener(this);
					task.execute();
				}
			} else {
				if (listaGroup.getSelection() == null) {
					JOptionPane.showMessageDialog(this, Resurses
							.getString("REPORT.LISTA.NOLIST.SELECTED"));
					return;
				}
				listSele = listaGroup.getSelection().getActionCommand();
				if (listSele != null) {

					if (listSele.equals("REPORT.LISTA.PERSONCARDS")) {
						taskCards = new TaskCards();
						taskCards.addPropertyChangeListener(this);
						taskCards.execute();
					} else if (listSele.equals("REPORT.LISTA.SURETIES")) {
						taskSureties = new TaskSureties();
						taskSureties.addPropertyChangeListener(this);
						taskSureties.execute();
					} else if (listSele.equals("REPORT.LISTA.ADDRESSES")) {
						taskAddresses = new TaskAddresses();
						taskAddresses.addPropertyChangeListener(this);
						taskAddresses.execute();
					} else {
						JOptionPane.showMessageDialog(this, Resurses
								.getString("REPORT.LISTA.NOLIST.SELECTED"));
					}
				}
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
		String type = "report";
		if (pers == null) {
			type = "lista";
		}
		v.add("cmd=savesettings");
		v.add("type=" + type);
		v.add("index=" + settingsIndex);
		v.add("name=" + (String) settingsName.getSelectedItem());
		if (commonWithImages.getSelectedObjects() != null) {
			v.add("images=true");
		}
		try {
			v.add("imagesize=" + commonImageSize.getText());
		} catch (NumberFormatException ne) {
		}
		try {
			v.add("personimagesize=" + commonPersonImageSize.getText());
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
		if (pers != null) {
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
		} else {
			model = listaGroup.getSelection();
			if (model != null) {
				v.add("listaGroup=" + model.getActionCommand());
			}

			int vidx = viewlist.getSelectedIndex();
			int viid = viewids[vidx];
			v.add("viewId=" + viid);
		}

		try {
			SukuData reposet = Suku.kontroller.getSukuData(v
					.toArray(new String[0]));
			if (reposet.resu != null) {
				JOptionPane.showMessageDialog(this, reposet.resu, Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				return;
			}
			typesTable.saveReportSettings(type + "types", settingsIndex);

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
					repo = new JavaReport(runner);
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

					dr = new DescendantReport(self, typesTable, repo);
				} else if (reportTypePane.getSelectedIndex() == 1) {
					String order = getAncestorPane().getNumberingFormat()
							.getSelection().getActionCommand();
					if (SET_ANC_TABLES.equals(order)) {
						dr = new AncestorTableReport(self, typesTable, repo);
					} else {
						dr = new AncestorReport(self, typesTable, repo);
					}
				} else {
					dr = new GenGraphReport(self, typesTable, repo);
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

				dlista = new DescendantLista(self, typesTable, null);

				dlista.executeReport();

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
			// if (dlista != null) {
			// dlista.getWriter().closeReport();
			// // dr.getFrame().setVisible(true);
			// }
		}
	}

	class TaskSureties extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			try {

				HashMap<String, String> relaMap = new HashMap<String, String>();
				if (parent.getDatabaseRowCount() > 60000) {
					JOptionPane.showMessageDialog(null, Resurses
							.getString("DBLISTA_TOO_LARGE"), Resurses
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
				PersonLongData longs = null;
				Relation[] relas = null;
				for (int idx = 0; idx < shorts.length; idx++) {
					shorts[idx] = parent.getDatbasePerson(idx);
				}

				BufferedOutputStream bstr = new BufferedOutputStream(
						Suku.kontroller.getOutputStream());
				WritableWorkbook workbook = Workbook.createWorkbook(bstr);

				WritableFont arial10bold = new WritableFont(WritableFont.ARIAL,
						10, WritableFont.BOLD, true);
				WritableCellFormat arial0bold = new WritableCellFormat(
						arial10bold);

				WritableFont arial10 = new WritableFont(WritableFont.ARIAL, 10,
						WritableFont.NO_BOLD, false);
				WritableCellFormat arial0 = new WritableCellFormat(arial10);

				WritableSheet sheet = workbook.createSheet(typesTable
						.getTextValue("SURETY_LISTA"), 0);
				int rivi = 0;

				String sureties[] = typesTable.getTextValue("SURETY_VALUES")
						.split(";");

				jxl.write.Number nume = null;
				Label label = new Label(0, rivi, typesTable
						.getTextValue("SURETY_PID"), arial0bold);
				sheet.addCell(label);
				label = new Label(1, rivi, typesTable.getTagName("NAME"),
						arial0bold);
				sheet.addCell(label);
				label = new Label(2, rivi, typesTable.getTagName("BIRT"),
						arial0bold);
				sheet.addCell(label);
				label = new Label(3, rivi, typesTable.getTagName("DEAT"),
						arial0bold);
				sheet.addCell(label);
				label = new Label(4, rivi, typesTable
						.getTextValue("SURETY_TAG"), arial0bold);
				sheet.addCell(label);

				label = new Label(5, rivi, typesTable
						.getTextValue("SURETY_SURETY"), arial0bold);
				sheet.addCell(label);
				label = new Label(6, rivi, typesTable.getTagName("NAME"),
						arial0bold);
				sheet.addCell(label);
				label = new Label(7, rivi, typesTable.getTagName("BIRT"),
						arial0bold);
				sheet.addCell(label);
				label = new Label(8, rivi, typesTable.getTagName("DEAT"),
						arial0bold);
				sheet.addCell(label);
				label = new Label(9, rivi, typesTable
						.getTextValue("SURETY_DESCRIPTION"), arial0bold);
				sheet.addCell(label);
				label = new Label(10, rivi, typesTable
						.getTextValue("SURETY_DATE"), arial0bold);
				sheet.addCell(label);
				label = new Label(11, rivi, typesTable
						.getTextValue("SURETY_PLACE"), arial0bold);
				sheet.addCell(label);
				label = new Label(12, rivi, typesTable
						.getTextValue("SURETY_PRIVATE_TEXT"), arial0bold);
				sheet.addCell(label);

				rivi++;
				for (int i = 0; i < shorts.length; i++) {

					SukuData sdata = Suku.getKontroller().getSukuData(
							"cmd=person", "pid=" + shorts[i].getPid());
					longs = sdata.persLong;
					relas = sdata.relations;

					PersonShortData psp = new PersonShortData(longs);
					// System.out.println("SURETY=" + psp.getAlfaName());
					float prose = (i * 100f) / shorts.length;
					setRunnerValue("" + (int) prose + ";" + psp.getAlfaName());
					for (int j = 0; j < longs.getNotices().length; j++) {
						UnitNotice notice = longs.getNotices()[j];
						if (notice.getSurety() < 100) {
							rivi++;
							nume = new jxl.write.Number(0, rivi, psp.getPid());
							sheet.addCell(nume);
							label = new Label(1, rivi, psp.getAlfaName(),
									arial0);
							sheet.addCell(label);
							label = new Label(2, rivi, Utils.textDate(psp
									.getBirtDate(), false), arial0);
							sheet.addCell(label);
							label = new Label(3, rivi, Utils.textDate(psp
									.getDeatDate(), false), arial0);
							sheet.addCell(label);
							label = new Label(4, rivi, typesTable
									.getTextValue(typesTable.getTagName(notice
											.getTag())), arial0);
							sheet.addCell(label);

							int idx = 5 - (notice.getSurety() + 10) / 20;
							String aux = "";
							if (idx < sureties.length) {
								aux = sureties[idx];
							} else {
								aux = "" + notice.getSurety();
							}
							label = new Label(5, rivi, aux);
							sheet.addCell(label);

							StringBuilder sb = new StringBuilder();
							if (notice.getGivenname() != null) {
								sb.append(notice.getGivenname());
							}
							if (notice.getPrefix() != null) {
								if (sb.length() > 0) {
									sb.append(" ");
								}
								sb.append(notice.getPrefix());
							}
							if (notice.getSurname() != null) {
								if (sb.length() > 0) {
									sb.append(" ");
								}
								sb.append(notice.getSurname());
							}
							if (notice.getPostfix() != null) {
								if (sb.length() > 0) {
									sb.append(" ");
								}
								sb.append(notice.getPostfix());
							}
							if (sb.length() > 0) {
								label = new Label(6, rivi, sb.toString(),
										arial0);
								sheet.addCell(label);
							}

							label = new Label(9, rivi, notice.getDescription(),
									arial0);
							sheet.addCell(label);

							label = new Label(10, rivi, Utils.textDate(notice
									.getFromDate(), false), arial0);
							sheet.addCell(label);

							label = new Label(11, rivi, notice.getPlace(),
									arial0);
							sheet.addCell(label);
							label = new Label(12, rivi,
									notice.getPrivateText(), arial0);
							sheet.addCell(label);
						}

					}
					for (int j = 0; j < relas.length; j++) {
						Relation rela = relas[j];
						if (rela.getSurety() < 100) {
							String thisRela = "" + rela.getPid() + "_"
									+ rela.getRelative();
							String otherRela = "" + rela.getRelative() + "_"
									+ rela.getPid();
							if (relaMap.get(otherRela) == null) {
								relaMap.put(thisRela, "OK");

								rivi++;
								nume = new jxl.write.Number(0, rivi, psp
										.getPid());
								sheet.addCell(nume);
								label = new Label(1, rivi, psp.getAlfaName(),
										arial0);
								sheet.addCell(label);
								label = new Label(2, rivi, Utils.textDate(psp
										.getBirtDate(), false), arial0);
								sheet.addCell(label);
								label = new Label(3, rivi, Utils.textDate(psp
										.getDeatDate(), false), arial0);
								sheet.addCell(label);

								label = new Label(4, rivi, typesTable
										.getTextValue(rela.getTag()), arial0);
								sheet.addCell(label);

								int idx = 5 - (rela.getSurety() + 10) / 20;
								String aux = "";
								if (idx < sureties.length) {
									aux = sureties[idx];
								} else {
									aux = "" + rela.getSurety();
								}
								label = new Label(5, rivi, aux);
								sheet.addCell(label);

								SukuData rdata = Suku.getKontroller()
										.getSukuData("cmd=person",
												"pid=" + rela.getRelative());
								PersonShortData rsp = new PersonShortData(
										rdata.persLong);
								label = new Label(6, rivi, rsp.getAlfaName(),
										arial0);
								sheet.addCell(label);

								label = new Label(7, rivi, Utils.textDate(rsp
										.getBirtDate(), false), arial0);
								sheet.addCell(label);
								label = new Label(8, rivi, Utils.textDate(rsp
										.getDeatDate(), false), arial0);
								sheet.addCell(label);
							}
							if (rela.getNotices() != null) {
								for (int k = 0; k < rela.getNotices().length; k++) {
									RelationNotice relnoti = rela.getNotices()[k];
									if (relnoti.getSurety() < 100) {

										thisRela = "" + rela.getPid() + "_"
												+ rela.getRelative();
										otherRela = "" + rela.getRelative()
												+ "_" + rela.getPid();
										if (relaMap.get(otherRela) == null) {
											relaMap.put(thisRela, "OK");

											rivi++;
											nume = new jxl.write.Number(0,
													rivi, psp.getPid());
											sheet.addCell(nume);
											label = new Label(1, rivi, psp
													.getAlfaName(), arial0);
											sheet.addCell(label);
											label = new Label(2, rivi, Utils
													.textDate(
															psp.getBirtDate(),
															false), arial0);
											sheet.addCell(label);
											label = new Label(3, rivi, Utils
													.textDate(
															psp.getDeatDate(),
															false), arial0);
											sheet.addCell(label);

											label = new Label(
													4,
													rivi,
													typesTable
															.getTextValue(relnoti
																	.getTag()),
													arial0);
											sheet.addCell(label);

											int idx = 5 - (relnoti.getSurety() + 10) / 20;
											String aux = "";
											if (idx < sureties.length) {
												aux = sureties[idx];
											} else {
												aux = "" + relnoti.getSurety();
											}
											label = new Label(5, rivi, aux);
											sheet.addCell(label);

											SukuData rdata = Suku
													.getKontroller()
													.getSukuData(
															"cmd=person",
															"pid="
																	+ rela
																			.getRelative());
											PersonShortData rsp = new PersonShortData(
													rdata.persLong);
											label = new Label(6, rivi, rsp
													.getAlfaName(), arial0);
											sheet.addCell(label);

											label = new Label(7, rivi, Utils
													.textDate(
															rsp.getBirtDate(),
															false), arial0);
											sheet.addCell(label);
											label = new Label(8, rivi, Utils
													.textDate(
															rsp.getDeatDate(),
															false), arial0);
											sheet.addCell(label);

											label = new Label(12, rivi, relnoti
													.getPrivateText(), arial0);
											sheet.addCell(label);

										}
									}
								}
							}
						}
					}

				}
				setRunnerValue("100;OK");
				workbook.write();
				workbook.close();
				bstr.close();
				String report = Suku.kontroller.getFilePath();

				Utils.openExternalFile(report);

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

	class TaskAddresses extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {

			try {

				if (parent.getDatabaseRowCount() > 60000) {
					JOptionPane.showMessageDialog(null, Resurses
							.getString("DBLISTA_TOO_LARGE"), Resurses
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
				PersonLongData longs = null;

				for (int idx = 0; idx < shorts.length; idx++) {
					shorts[idx] = parent.getDatbasePerson(idx);
				}

				BufferedOutputStream bstr = new BufferedOutputStream(
						Suku.kontroller.getOutputStream());
				WritableWorkbook workbook = Workbook.createWorkbook(bstr);

				WritableFont arial10bold = new WritableFont(WritableFont.ARIAL,
						10, WritableFont.BOLD, true);
				WritableCellFormat arial0bold = new WritableCellFormat(
						arial10bold);

				WritableFont arial10 = new WritableFont(WritableFont.ARIAL, 10,
						WritableFont.NO_BOLD, false);
				WritableCellFormat arial0 = new WritableCellFormat(arial10);

				WritableSheet sheet = workbook.createSheet(typesTable
						.getTextValue("ADDRESS_LISTA"), 0);
				int rivi = 0;

				jxl.write.Number nume = null;
				Label label = new Label(0, rivi, typesTable
						.getTextValue("SURETY_PID"), arial0bold);
				sheet.addCell(label);
				label = new Label(1, rivi, typesTable.getTagName("NAME"),
						arial0bold);
				sheet.addCell(label);
				label = new Label(2, rivi, typesTable
						.getTextValue("ADDRESS_GIVENNAME"), arial0bold);
				sheet.addCell(label);
				label = new Label(3, rivi, typesTable
						.getTextValue("ADDRESS_PATRONYME"), arial0bold);
				sheet.addCell(label);
				label = new Label(4, rivi, typesTable
						.getTextValue("ADDRESS_SURNAME"), arial0bold);
				sheet.addCell(label);
				label = new Label(5, rivi, typesTable.getTagName("BIRT"),
						arial0bold);
				sheet.addCell(label);
				label = new Label(6, rivi, typesTable
						.getTextValue("ADDRESS_STREET")
						+ "1", arial0bold);
				sheet.addCell(label);
				label = new Label(7, rivi, typesTable
						.getTextValue("ADDRESS_STREET")
						+ "2", arial0bold);
				sheet.addCell(label);
				label = new Label(8, rivi, typesTable
						.getTextValue("ADDRESS_STREET")
						+ "3", arial0bold);
				sheet.addCell(label);
				label = new Label(9, rivi, typesTable
						.getTextValue("ADDRESS_POSTNO"), arial0bold);
				sheet.addCell(label);
				label = new Label(10, rivi, typesTable
						.getTextValue("ADDRESS_POSTOFFICE"), arial0bold);
				sheet.addCell(label);
				label = new Label(11, rivi, typesTable
						.getTextValue("ADDRESS_STATE"), arial0bold);
				sheet.addCell(label);
				label = new Label(12, rivi, typesTable
						.getTextValue("ADDRESS_COUNTRY"), arial0bold);
				sheet.addCell(label);
				label = new Label(13, rivi, typesTable
						.getTextValue("ADDRESS_EMAIL"), arial0bold);
				sheet.addCell(label);

				for (int i = 0; i < shorts.length; i++) {

					SukuData sdata = Suku.getKontroller().getSukuData(
							"cmd=person", "pid=" + shorts[i].getPid());
					longs = sdata.persLong;

					float prose = (i * 100f) / shorts.length;

					UnitNotice address = null;
					UnitNotice name = null;
					UnitNotice birt = null;
					setRunnerValue("" + (int) prose + ";" + " ");

					for (int j = 0; j < longs.getNotices().length; j++) {
						UnitNotice notice = longs.getNotices()[j];
						if (notice.getTag().equals("NAME")) {
							if (name == null) {
								name = notice;
							}
							if (notice.getNoticeType() == null) {
								name = notice;
							}
						}
						if (notice.getTag().equals("BIRT")) {
							if (birt == null) {
								birt = notice;
							}

						}
						if (notice.getTag().equals("RESI")) {
							address = notice;

						}
					}

					if (address != null && name != null) {
						rivi++;
						nume = new jxl.write.Number(0, rivi, name.getPid());
						sheet.addCell(nume);

						StringBuilder sb = new StringBuilder();
						if (name.getGivenname() != null) {
							sb.append(name.getGivenname());
						}
						if (name.getPatronym() != null) {
							if (sb.length() > 0) {
								sb.append(" ");

							}
							sb.append(name.getPatronym());
						}

						if (name.getPrefix() != null) {
							if (sb.length() > 0) {
								sb.append(" ");

							}
							sb.append(name.getPrefix());
						}
						if (name.getSurname() != null) {
							if (sb.length() > 0) {
								sb.append(" ");

							}
							sb.append(name.getSurname());
						}
						if (name.getPostfix() != null) {
							if (sb.length() > 0) {
								sb.append(" ");

							}
							sb.append(name.getPostfix());
						}
						if (sb.length() > 0) {
							label = new Label(1, rivi, sb.toString(), arial0);
							sheet.addCell(label);
						}

						if (name.getGivenname() != null) {
							label = new Label(2, rivi, name.getGivenname(),
									arial0);
							sheet.addCell(label);
						}

						if (name.getPatronym() != null) {
							label = new Label(3, rivi, name.getPatronym(),
									arial0);
							sheet.addCell(label);
						}
						sb = new StringBuilder();
						if (name.getPrefix() != null) {
							sb.append(name.getPrefix());
						}
						if (name.getSurname() != null) {
							if (sb.length() > 0) {
								sb.append(" ");
							}
							sb.append(name.getSurname());
						}
						label = new Label(4, rivi, sb.toString(), arial0);
						sheet.addCell(label);

						if (birt != null && birt.getFromDate() != null) {
							label = new Label(5, rivi, Utils.textDate(birt
									.getFromDate(), true), arial0);
							sheet.addCell(label);
						}
						if (address.getAddress() != null) {

							String adds[] = address.getAddress().split("\n");
							label = new Label(6, rivi, adds[0], arial0);
							sheet.addCell(label);
							if (adds.length > 1) {
								label = new Label(7, rivi, adds[1], arial0);
								sheet.addCell(label);
							}
							if (adds.length > 2) {
								sb = new StringBuilder();
								for (int k = 2; k < adds.length; k++) {
									if (sb.length() > 0) {
										sb.append(" ");
									}
									sb.append(adds[k]);
								}

								label = new Label(8, rivi, sb.toString(),
										arial0);
								sheet.addCell(label);
							}
						}

						if (address.getPostalCode() != null) {
							label = new Label(9, rivi, address.getPostalCode(),
									arial0);
							sheet.addCell(label);
						}
						if (address.getPostOffice() != null) {
							label = new Label(10, rivi,
									address.getPostOffice(), arial0);
							sheet.addCell(label);
						}
						if (address.getState() != null) {
							label = new Label(11, rivi, address.getState(),
									arial0);
							sheet.addCell(label);
						}

						if (address.getCountry() != null) {
							label = new Label(12, rivi, address.getCountry(),
									arial0);
							sheet.addCell(label);
						}

						if (address.getEmail() != null) {
							label = new Label(13, rivi, address.getEmail(),
									arial0);
							sheet.addCell(label);
						}
					}

				}
				setRunnerValue("100;OK");
				workbook.write();
				workbook.close();
				bstr.close();
				String report = Suku.kontroller.getFilePath();

				Utils.openExternalFile(report);

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

							label = new Label(1, rivi, typesTable
									.getTextValue(r.getTag()), arial0bold);
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

							label = new Label(1, rivi, typesTable
									.getTextValue(r.getTag()), arial0bold);
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

							label = new Label(1, rivi, typesTable
									.getTextValue(r.getTag()), arial0bold);
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
				String report = Suku.kontroller.getFilePath();
				Utils.openExternalFile(report);
			} catch (Throwable e) {
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
					label = new Label(1, ++rivi, typesTable.getTextValue(rn
							.getTag()));
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

				boolean showTag = typesTable.isType(tag, 2);

				if (showTag) {
					rivi++;
					String tagv = typesTable.getTagName(tag);
					label = new Label(1, rivi, tagv);
					sheet.addCell(label);

					if (tag.equals("NAME")) {
						nameFound = true;
						StringBuilder sb = new StringBuilder();
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
				String tagv = typesTable.getTagName("NAME");
				label = new Label(1, ++rivi, tagv);
				sheet.addCell(label);

			}

			if (!birtFound) {
				boolean showTag = typesTable.isType("BIRT", 2);
				if (showTag) {
					String tagv = typesTable.getTagName("BIRT");
					label = new Label(1, ++rivi, tagv);
					sheet.addCell(label);
				}
			}
			if (!deatFound) {
				boolean showTag = typesTable.isType("DEAT", 2);
				if (showTag) {
					String tagv = typesTable.getTagName("DEAT");
					label = new Label(1, ++rivi, tagv);
					sheet.addCell(label);
				}
			}
			if (!occuFound) {
				boolean showTag = typesTable.isType("OCCU", 2);
				if (showTag) {
					String tagv = typesTable.getTagName("OCCU");
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

							if (refe.isEmpty()) {
								refe = cefe;
							} else {
								if (!cefe.isEmpty()) {
									refe += "," + cefe;
								}
							}
						}
						// System.out.println("RUN:"
						// + pit.shortPerson.getAlfaName());
						String mefe = pit.getReferences(0, false, false, true);

						if (!mefe.isEmpty()) {
							if (!refe.isEmpty()) {
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
				String report = Suku.kontroller.getFilePath();
				Utils.openExternalFile(report);
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
