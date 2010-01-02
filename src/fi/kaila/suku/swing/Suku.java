package fi.kaila.suku.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import fi.kaila.suku.imports.Import2004Dialog;
import fi.kaila.suku.imports.ImportGedcomDialog;
import fi.kaila.suku.kontroller.SukuKontroller;
import fi.kaila.suku.kontroller.SukuKontrollerLocalImpl;
import fi.kaila.suku.kontroller.SukuKontrollerWebstartImpl;
import fi.kaila.suku.swing.dialog.AboutDialog;
import fi.kaila.suku.swing.dialog.ConnectDialog;
import fi.kaila.suku.swing.dialog.GroupMgrWindow;
import fi.kaila.suku.swing.dialog.SearchCriteria;
import fi.kaila.suku.swing.dialog.SettingsDialog;
import fi.kaila.suku.swing.dialog.SukuPad;
import fi.kaila.suku.swing.dialog.ToolsDialog;
import fi.kaila.suku.swing.dialog.ViewMgrWindow;
import fi.kaila.suku.swing.dialog.SearchCriteria.ColTable;
import fi.kaila.suku.swing.panel.PersonView;
import fi.kaila.suku.swing.panel.SukuTabPane;
import fi.kaila.suku.swing.util.SukuPopupMenu;
import fi.kaila.suku.swing.worker.ReportWorkerDialog;
import fi.kaila.suku.util.CommandExecuter;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateComparator;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.SukuModel;
import fi.kaila.suku.util.SukuNameComparator;
import fi.kaila.suku.util.SukuNumStringComparator;
import fi.kaila.suku.util.SukuPidComparator;
import fi.kaila.suku.util.SukuRow;
import fi.kaila.suku.util.SukuSenser;
import fi.kaila.suku.util.SukuStringComparator;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.local.LocalAdminUtilities;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.PlaceLocationData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * 
 * 
 * <h1>FinFamily main program</h1>
 * 
 * <p>
 * Swing-based java-application for managing genealogical data.
 * </p>
 * <p>
 * The genealogical data is stored in a PostgreSQL database
 * </p>
 * 
 * <h2>See <a href="../../../../overview.html#lic">Finfamily License</a></h2>
 * 
 * <h2>Starting the application</h2>
 * 
 * <p>
 * The FinFamily application is distributed as a zip file. unzip that file and
 * you are ready to go assuming you have installed the PostgreSQL database as
 * described in the guide.
 * </p>
 * 
 * <h3>Windows</h3>
 * 
 * <p>
 * For windows users there is a convenience application Suku.exe that you use to
 * start the main application. Suku.exe reads the suku.sh command. Changes the
 * java command to javaw and executes the command. You can rename suku.sh to
 * suku.bat if you like to start it showing the command line output.
 * </p>
 * 
 * <p>
 * suku.sh contains something like the command below to start suku. If you are
 * familiar with java then this tells you how. Else you need not care for it.
 * </p>
 * 
 * java -Xms64m -Xmx500m
 * -Djava.util.logging.config.file=properties/logging.properties -jar suku.jar
 * 
 * <h3>Linux</h3>
 * 
 * <p>
 * In Linux (or similar) execute the suku.sh command. You should first chmod it
 * to be an executable file unless you start it using the sh command
 * </p>
 * 
 * @author Kaarle Kaila
 * 
 * 
 */
public class Suku extends JFrame implements ActionListener, ComponentListener,
		MenuListener, MouseListener, MouseMotionListener, KeyListener, ISuku,
		ClipboardOwner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Application version
	 */
	public static final String sukuVersion = "11.-3.0250";
	/**
	 * Server version
	 */
	public static String serverVersion = null;

	private static Logger logger = Logger.getLogger(Suku.class.getName());

	private JMenuBar menubar;
	private JMenu mFile;
	private JMenuItem mImport2004;
	private JMenuItem mImportGedcom;
	private JMenuItem mQuery;
	private JMenuItem mConnect;
	private JMenuItem mNewDatabase;
	private JMenuItem mDisconnect;
	private JMenuItem mPrintPerson;
	private JMenuItem mNewPerson;
	// private JMenuItem mTestSave;
	// private JMenuItem mTestOpen;
	private JMenuItem mShowInMap;
	// private JMenuItem mReport;
	private JMenuItem mExit;
	private JMenuItem mAdmin;
	// private JMenu mEdit;
	// private JMenuItem mCopy;
	private JMenu mTools;
	private JMenuItem mSettings;
	private JMenuItem mGroupMgr;
	private JMenuItem mViewMgr;
	private JMenuItem mLoadCoordinates;
	private JMenuItem mLoadTypes;
	private JMenuItem mDbWork;
	private JMenuItem mDbUpdate;
	private JMenuItem mStopPgsql;
	private JMenuItem mStartPgsql;
	private JMenu mHiski;
	private JMenuItem mImportHiski;
	private JMenu mHelp;
	private JMenuItem mAbout;

	private JToolBar toolbar;

	private JButton tQueryButton;
	private JButton tPersonButton;
	private JButton tSubjectButton;
	private JButton tSubjectPButton;
	// private JButton tSubjectName;
	private JButton tMapButton;
	private JButton tRemovePerson;
	private JButton tNoteButton;
	private JButton tAddressButton;
	private JButton tFarmButton;
	private JButton tImageButton;
	private JButton tNoticesButton;
	private JButton tPrivateButton;

	private Vector<String> needle = new Vector<String>();
	private static final int maxNeedle = 32;
	private boolean isConnected = false;
	boolean isExiting = false;
	private String databaseName = null;
	private PopupListener popupListener;

	private static final int SPLITTER_HORIZ_MARGIN = 10;

	private SukuModel tableModel;
	private DbTable table = null;
	private JScrollPane scrollPane = null;
	private HashMap<Integer, PersonShortData> tableMap = null;

	private JSplitPane splitPane = null;

	private PersonView personView = null;

	private JTextField statusPanel = null;
	private SuomiMap suomi = null;
	private GroupMgrWindow groupWin = null;
	private ViewMgrWindow viewWin = null;
	// private HiskiImporter hiski=null;
	private LocalAdminUtilities adminUtilities = null;
	/**
	 * A static variable thst contains the Suku kontroller in use
	 */
	public static SukuKontroller kontroller = null;

	/**
	 * During connect to dabatase the database version is stored here
	 */
	public static String postServerVersion = null;

	private static String[] repoLangList = null;

	/**
	 * A "clipboard" location where a person can be copied to
	 */
	public static Object sukuObject = null;

	private int activePersonPid = 0;
	private boolean isWebApp = false;

	/**
	 * Suku11 main program entry point when used as standard Swing application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Suku w = new Suku();
		try {
			w.startMe(args);
		} catch (SukuException e) {
			logger.log(Level.SEVERE, "Unable to start", e);
		}

	}

	private void startMe(String[] args) throws SukuException {

		try {

			if (java.io.File.pathSeparatorChar == ';') {
				// use Windows lookandfeel if Windows
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} else {
				// else use Metal

				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			}
			// UIManager.LookAndFeelInfo[] liz = UIManager
			// .getInstalledLookAndFeels();
			//						
			// for (int i = 0;i < liz.length;i++) {
			// logger.info("lndfeel " + liz[i].getClassName());
			//							
			// }
			// UIManager.setLookAndFeel(liz[0].getClassName());
		} catch (Exception e) {
			logger.log(Level.INFO, "look-and-feel virhe", e);

		}
		String arg1 = null;
		if (args.length > 0) {
			arg1 = args[0];

		}
		if ("web".equals(arg1)) {
			this.isWebApp = true;
			kontroller = new SukuKontrollerWebstartImpl();
		} else {
			kontroller = new SukuKontrollerLocalImpl();
		}

		String loca = kontroller.getPref(this, Resurses.LOCALE, "en");
		Resurses.setLocale(loca);
		String langu = kontroller.getPref(this, Resurses.REPOLANG, "fi");
		Resurses.setLanguage(langu);
		String datfo = kontroller.getPref(this, Resurses.DATEFORMAT, "FI");
		Resurses.setDateFormat(datfo);

		this.setTitle(null);

		this.menubar = new JMenuBar();
		setJMenuBar(this.menubar);
		this.mFile = new JMenu(Resurses.getString(Resurses.FILE));
		this.menubar.add(this.mFile);

		this.mConnect = new JMenuItem(Resurses.getString(Resurses.CONNECT));
		this.mFile.add(this.mConnect);
		this.mConnect.setActionCommand(Resurses.CONNECT);
		this.mConnect.addActionListener(this);
		this.mFile.addMenuListener(this);

		if (!isWebApp) {

			this.mAdmin = new JMenuItem(Resurses.getString(Resurses.ADMIN));
			this.mFile.add(this.mAdmin);
			this.mAdmin.setActionCommand(Resurses.ADMIN);
			this.mAdmin.addActionListener(this);
		}
		JMenu imp = new JMenu(Resurses.getString("IMPORT"));
		this.mFile.add(imp);

		this.mImport2004 = new JMenuItem(Resurses
				.getString(Resurses.IMPORT_SUKU));
		imp.add(this.mImport2004);
		this.mImport2004.setActionCommand(Resurses.IMPORT_SUKU);
		this.mImport2004.addActionListener(this);

		this.mImportGedcom = new JMenuItem(Resurses
				.getString(Resurses.IMPORT_GEDCOM));
		imp.add(this.mImportGedcom);
		this.mImportGedcom.setActionCommand(Resurses.IMPORT_GEDCOM);
		this.mImportGedcom.addActionListener(this);

		this.mNewDatabase = new JMenuItem(Resurses.getString(Resurses.NEWDB));
		this.mFile.add(this.mNewDatabase);
		this.mNewDatabase.setActionCommand(Resurses.NEWDB);
		this.mNewDatabase.addActionListener(this);
		this.mFile.addSeparator();
		this.mLoadCoordinates = new JMenuItem(Resurses
				.getString("MENU_TOOLS_LOAD_COORDINATES"));
		this.mFile.add(this.mLoadCoordinates);
		this.mLoadCoordinates.setActionCommand("MENU_TOOLS_LOAD_COORDINATES");
		this.mLoadCoordinates.addActionListener(this);

		this.mLoadTypes = new JMenuItem(Resurses
				.getString("MENU_TOOLS_LOAD_TYPES"));
		this.mFile.add(this.mLoadTypes);
		this.mLoadTypes.setActionCommand("MENU_TOOLS_LOAD_TYPES");
		this.mLoadTypes.addActionListener(this);

		this.mFile.addSeparator();

		this.mQuery = new JMenuItem(Resurses.getString(Resurses.QUERY));
		this.mFile.add(this.mQuery);
		this.mQuery.setActionCommand(Resurses.QUERY);
		this.mQuery.addActionListener(this);

		this.mNewPerson = new JMenuItem(Resurses
				.getString("TOOLBAR.NEWPERSON.TOOLTIP"));
		this.mFile.add(this.mNewPerson);
		this.mNewPerson.setActionCommand(Resurses.TOOLBAR_NEWPERSON_ACTION);
		this.mNewPerson.addActionListener(this);

		this.mPrintPerson = new JMenuItem(Resurses
				.getString(Resurses.PRINT_PERSON));
		this.mFile.add(this.mPrintPerson);
		this.mPrintPerson.setActionCommand(Resurses.PRINT_PERSON);
		this.mPrintPerson.addActionListener(this);

		// this.mTestOpen = new
		// JMenuItem(Resurses.getString(Resurses.TEST_OPEN));
		// this.mFile.add(this.mTestOpen);
		// this.mTestOpen.setActionCommand(Resurses.TEST_OPEN);
		// this.mTestOpen.addActionListener(this);
		//
		// this.mTestSave = new
		// JMenuItem(Resurses.getString(Resurses.TEST_SAVE));
		// this.mFile.add(this.mTestSave);
		// this.mTestSave.setActionCommand(Resurses.TEST_SAVE);
		// this.mTestSave.addActionListener(this);

		this.mShowInMap = new JMenuItem(Resurses.getString(Resurses.SHOWINMAP));
		this.mFile.add(this.mShowInMap);
		this.mShowInMap.setActionCommand(Resurses.SHOWINMAP);
		this.mShowInMap.addActionListener(this);

		this.mFile.addSeparator();

		this.mDisconnect = new JMenuItem(Resurses
				.getString(Resurses.DISCONNECT));
		this.mFile.add(this.mDisconnect);
		this.mDisconnect.setActionCommand(Resurses.DISCONNECT);
		this.mDisconnect.addActionListener(this);

		this.mExit = new JMenuItem(Resurses.getString(Resurses.EXIT));
		this.mFile.add(this.mExit);
		this.mExit.setActionCommand(Resurses.EXIT);
		this.mExit.addActionListener(this);

		this.mHiski = new JMenu("Hiski");
		this.menubar.add(this.mHiski);
		this.mImportHiski = new JMenuItem(Resurses
				.getString(Resurses.IMPORT_HISKI));
		this.mHiski.add(this.mImportHiski);
		this.mImportHiski.setActionCommand(Resurses.IMPORT_HISKI);
		this.mImportHiski.addActionListener(this);

		this.mTools = new JMenu(Resurses.getString("TOOLS"));
		this.menubar.add(this.mTools);
		this.mSettings = new JMenuItem(Resurses.getString(Resurses.SETTINGS));
		this.mTools.add(this.mSettings);
		this.mSettings.setActionCommand(Resurses.SETTINGS);
		this.mSettings.addActionListener(this);

		this.mDbWork = new JMenuItem(Resurses.getString("MENU_TOOLS_DBWORK"));
		this.mTools.add(this.mDbWork);
		this.mDbWork.setActionCommand("MENU_TOOLS_DBWORK");
		this.mDbWork.addActionListener(this);

		this.mDbUpdate = new JMenuItem(Resurses.getString(Resurses.UPDATEDB));
		this.mTools.add(this.mDbUpdate);
		this.mDbUpdate.setActionCommand(Resurses.UPDATEDB);
		this.mDbUpdate.addActionListener(this);

		this.mGroupMgr = new JMenuItem(Resurses
				.getString("MENU_TOOLS_GROUP_MGR"));
		this.mTools.add(this.mGroupMgr);
		this.mGroupMgr.setActionCommand("MENU_TOOLS_GROUP_MGR");
		this.mGroupMgr.addActionListener(this);

		this.mViewMgr = new JMenuItem(Resurses.getString("MENU_TOOLS_VIEW_MGR"));
		this.mTools.add(this.mViewMgr);
		this.mViewMgr.setActionCommand("MENU_TOOLS_VIEW_MGR");
		this.mViewMgr.addActionListener(this);

		if (System.getProperty("file.separator").charAt(0) == '\\') { // windows
			// only
			this.mStopPgsql = new JMenuItem(Resurses
					.getString(Resurses.PGSQL_STOP));
			this.mTools.add(this.mStopPgsql);
			this.mStopPgsql.setActionCommand(Resurses.PGSQL_STOP);
			this.mStopPgsql.addActionListener(this);

			this.mStartPgsql = new JMenuItem(Resurses
					.getString(Resurses.PGSQL_START));
			this.mTools.add(this.mStartPgsql);
			this.mStartPgsql.setActionCommand(Resurses.PGSQL_START);
			this.mStartPgsql.addActionListener(this);
		}

		this.mHelp = new JMenu(Resurses.getString(Resurses.HELP));
		this.menubar.add(this.mHelp);
		this.mAbout = new JMenuItem(Resurses.getString(Resurses.ABOUT));
		this.mHelp.add(this.mAbout);
		this.mAbout.setActionCommand(Resurses.ABOUT);
		this.mAbout.addActionListener(this);

		popupListener = new PopupListener();
		SukuPopupMenu pop = SukuPopupMenu.getInstance();
		pop.addActionListener(popupListener);

		SearchCriteria crit = SearchCriteria.getCriteria(this);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if (d.width > 1024)
			d.width = 1024;
		if (d.height > 600)
			d.height = 600;
		setLayout(null);
		setLocation(0, 0);
		setSize(d);
		setExtendedState(MAXIMIZED_BOTH);

		this.toolbar = new JToolBar("FinFamily tools");
		this.getContentPane().add(this.toolbar);
		this.toolbar.setBounds(0, 0, getWidth(), 50);
		this.toolbar.setFloatable(false);

		// first button
		try {

			tQueryButton = makeNavigationButton(Resurses.TOOLBAR_QUERY_IMAGE,
					Resurses.TOOLBAR_QUERY_ACTION, Resurses
							.getString("TOOLBAR.QUERY.TOOLTIP"), Resurses
							.getString("TOOLBAR.QUERY.ALTTEXT"));

			this.toolbar.add(tQueryButton);

			this.toolbar.addSeparator(new Dimension(20, 30));
			tSubjectButton = makeNavigationButton(
					Resurses.TOOLBAR_SUBJECT_DOWN_IMAGE,
					Resurses.TOOLBAR_SUBJECT_ON_IMAGE,
					Resurses.TOOLBAR_SUBJECT_DOWN_ACTION, Resurses
							.getString("TOOLBAR.SUBJECT.TOOLTIP"), Resurses
							.getString("TOOLBAR.SUBJECT.ALTTEXT"));
			this.toolbar.add(tSubjectButton);

			this.toolbar.addSeparator();
			// tSubjectName = new JButton("");
			// tSubjectName.setEnabled(false);
			//
			// this.toolbar.add(tSubjectName);

			tSubjectPButton = makeNavigationButton(
					Resurses.TOOLBAR_SUBJECT_UP_IMAGE,
					Resurses.TOOLBAR_SUBJECT_UP_ACTION, Resurses
							.getString("TOOLBAR.SUBJECTP.TOOLTIP"), Resurses
							.getString("TOOLBAR.SUBJECTP.ALTTEXT"));
			tSubjectPButton.setEnabled(false);
			this.toolbar.add(tSubjectPButton);

			this.toolbar.addSeparator(new Dimension(20, 30));
			tPersonButton = makeNavigationButton(Resurses.TOOLBAR_PERSON_IMAGE,
					Resurses.TOOLBAR_NEWPERSON_ACTION, Resurses
							.getString("TOOLBAR.NEWPERSON.TOOLTIP"), Resurses
							.getString("TOOLBAR.NEWPERSON.ALTTEXT"));

			this.toolbar.add(tPersonButton);

			tMapButton = makeNavigationButton(Resurses.TOOLBAR_MAP_IMAGE,
					Resurses.TOOLBAR_MAP_ACTION, Resurses
							.getString("TOOLBAR.MAP.TOOLTIP"), Resurses
							.getString("TOOLBAR.MAP.ALTTEXT"));

			this.toolbar.add(tMapButton);

			tRemovePerson = makeNavigationButton(
					Resurses.TOOLBAR_REMPERSON_IMAGE,
					Resurses.TOOLBAR_REMPERSON_ACTION, Resurses
							.getString("TOOLBAR.REMPERSON.TOOLTIP"), Resurses
							.getString("TOOLBAR.REMPERSON.ALTTEXT"));

			this.toolbar.add(tRemovePerson);

			this.toolbar.addSeparator(new Dimension(20, 30));

			tNoticesButton = makeNavigationButton("Tietojaksot24",
					"Tietojaksot24_nega", Resurses.TOOLBAR_NOTICES_ACTION,
					Resurses.getString("TOOLBAR.NOTICES.TOOLTIP"), Resurses
							.getString("TOOLBAR.NOTICES.ALTTEXT"));

			String tmp = kontroller.getPref(this, Resurses.NOTICES_BUTTON,
					"false");
			if (tmp.equals("true")) {
				tNoticesButton.setSelected(true);
			}
			this.toolbar.add(tNoticesButton);

			tNoteButton = makeNavigationButton("Teksti24", "Teksti24_nega",
					Resurses.TOOLBAR_NOTE_ACTION, Resurses
							.getString("TOOLBAR.NOTE.TOOLTIP"), Resurses
							.getString("TOOLBAR.NOTE.ALTTEXT"));
			tmp = kontroller.getPref(this, Resurses.TOOLBAR_NOTE_ACTION,
					"false");
			if (tmp.equals("true")) {
				tNoteButton.setSelected(true);
			}
			this.toolbar.add(tNoteButton);

			tAddressButton = makeNavigationButton("showAddress",
					"showAddress_nega", Resurses.TOOLBAR_ADDRESS_ACTION,
					Resurses.getString("TOOLBAR.ADDRESS.TOOLTIP"), Resurses
							.getString("TOOLBAR.ADDRESS.ALTTEXT"));
			tmp = kontroller.getPref(this, Resurses.TOOLBAR_ADDRESS_ACTION,
					"false");
			if (tmp.equals("true")) {
				tAddressButton.setSelected(true);
			}
			this.toolbar.add(tAddressButton);

			tFarmButton = makeNavigationButton("talo", "talo_nega",
					Resurses.TOOLBAR_FARM_ACTION, Resurses
							.getString("TOOLBAR.FARM.TOOLTIP"), Resurses
							.getString("TOOLBAR.FARM.ALTTEXT"));

			tmp = kontroller.getPref(this, Resurses.TOOLBAR_FARM_ACTION,
					"false");
			if (tmp.equals("true")) {
				tFarmButton.setSelected(true);
			}

			this.toolbar.add(tFarmButton);

			tImageButton = makeNavigationButton("kamera", "kamera_nega",
					Resurses.TOOLBAR_IMAGE_ACTION, Resurses
							.getString("TOOLBAR.IMAGE.TOOLTIP"), Resurses
							.getString("TOOLBAR.IMAGE.ALTTEXT"));
			tmp = kontroller.getPref(this, Resurses.TOOLBAR_IMAGE_ACTION,
					"false");
			if (tmp.equals("true")) {
				tImageButton.setSelected(true);
			}
			this.toolbar.add(tImageButton);

			tPrivateButton = makeNavigationButton("showPrivate",
					"showPrivate_nega", Resurses.TOOLBAR_PRIVATE_ACTION,
					Resurses.getString("TOOLBAR.PRIVATE.TOOLTIP"), Resurses
							.getString("TOOLBAR.PRIVATE.ALTTEXT"));
			tmp = kontroller.getPref(this, Resurses.TOOLBAR_PRIVATE_ACTION,
					"false");
			if (tmp.equals("true")) {
				tPrivateButton.setSelected(true);
			}
			this.toolbar.add(tPrivateButton);

		} catch (IOException e2) {
			throw new SukuException("Failed to create toolbar", e2);
		}

		// sukuClipboard = new Clipboard("suku");
		enableCommands();

		addComponentListener(this);

		initTable(crit);

		this.personView = new PersonView(this);

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				this.scrollPane, this.personView);

		getContentPane().add(this.splitPane);
		this.currentSize = new Dimension(
				getWidth() - SPLITTER_HORIZ_MARGIN * 3, 400);
		int splitterValue = currentSize.width / 2;
		if (currentSize.width > 522) {
			splitterValue = currentSize.width - 522;
		}
		this.splitPane.setDividerLocation(splitterValue);

		this.splitPane.setBounds(10, 20, this.currentSize.width,
				this.currentSize.height);

		this.statusPanel = new JTextField("");
		this.getContentPane().add(this.statusPanel);
		this.statusPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		this.statusPanel.setEditable(false);
		this.statusPanel.setBackground(Color.WHITE);
		this.statusPanel.setBounds(0, 420, 700, 20);

		setVisible(true);

		InputStream in = this.getClass().getResourceAsStream(
				"/images/sukuicon.gif");

		BufferedImage icon;
		try {
			icon = ImageIO.read(in);
			setIconImage(icon);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		logger.info("FinFamily [" + Resurses.getLanguage() + "] Version "
				+ sukuVersion + " - Java Version: "
				+ System.getProperty("java.version") + " from "
				+ System.getProperty("java.vendor"));
		// if (!this.isWebApp){
		connectDb();
		// }
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				e.getClass();
				System.exit(0);
			}

		});

	}

	/**
	 * Report languages is a two dimensional array with 0 = lancode and 1 =
	 * langname
	 * 
	 * @return count of languages available
	 */
	public static int getRepoLanguageCount() {
		if (repoLangList == null)
			return 0;
		return repoLangList.length;
	}

	/**
	 * Report languages is a two dimensional array with
	 * 
	 * @param idx
	 *            index into report language list
	 * @param theCode
	 *            true = lancode and false = language name
	 * @return the tag or the name of the requested text
	 */
	public static String getRepoLanguage(int idx, boolean theCode) {
		if (repoLangList == null || idx >= repoLangList.length)
			return null;
		String[] tmp = repoLangList[idx].split(";");
		if (theCode)
			return tmp[0];
		return tmp[1];
	}

	/**
	 * Get the index into the report language list for specified language
	 * 
	 * @param langCode
	 * @return index to language list
	 */
	public static int getRepoLanguageIndex(String langCode) {
		for (int i = 0; i < repoLangList.length; i++) {
			String[] tmp = repoLangList[i].split(";");
			if (langCode.equals(tmp[0]))
				return i;
		}
		return -1;
	}

	/**
	 * get absolute rectangle of current database window
	 * 
	 * @return the Rectangle in absolute coordinates
	 */
	public Rectangle getDbWindow() {

		Rectangle r = scrollPane.getBounds();
		Point pt = new Point(r.x, r.y);
		SwingUtilities.convertPointToScreen(pt, scrollPane);
		r.x = pt.x;
		r.y = pt.y;

		return r;
	}

	private void initTable(SearchCriteria crit) throws SukuException {
		this.tableModel = new SukuModel(this);
		this.tableMap = new HashMap<Integer, PersonShortData>();

		// TableModel myModel = createMyTableModel();
		// JTable table = new JTable(myModel);
		// table.setRowSorter(new TableRowSorter(myModel));

		this.table = new DbTable(this.tableModel) {
			private static final long serialVersionUID = 1L;

			// Implement table header tool tips.
			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(this.columnModel) {

					private static final long serialVersionUID = 1L;

					@Override
					public String getToolTipText(MouseEvent e) {

						java.awt.Point p = e.getPoint();
						int index = this.columnModel.getColumnIndexAtX(p.x);
						int realIndex = this.columnModel.getColumn(index)
								.getModelIndex();

						// int fullIdx =
						// Suku.this.tableModel.getFullIndex(realIndex);
						return Suku.this.tableModel.getColumnName(realIndex);
					}
				};
			}

			// Implement table cell tool tips.
			@Override
			public String getToolTipText(MouseEvent e) {
				// System.out.println("TTT: " + e);
				// String tip = null;
				// java.awt.Point p = e.getPoint();
				// int rowIndex = rowAtPoint(p);
				// int colIndex = columnAtPoint(p);
				// int realColumnIndex = convertColumnIndexToModel(colIndex);

				int yy = e.getY();
				int rh = Suku.this.table.getRowHeight();
				int ii = yy / rh;
				SukuRow row = (SukuRow) Suku.this.tableModel.getValueAt(ii,
						SukuModel.SUKU_ROW);
				if (row == null)
					return null;
				return row.getTodo();
			}
		};
		this.table.setRowSorter(new TableRowSorter<SukuModel>(this.tableModel));

		this.table.setDragEnabled(true);
		TransferHandler newHandler = new SukuTransferHandler();

		this.table.setTransferHandler(newHandler);

		initSorter(crit);

		TableColumnModel tc = this.table.getColumnModel();
		TableColumn cc;
		for (int k = crit.getColTableCount() - 1; k >= 0; k--) {
			cc = tc.getColumn(k);
			// if (k == 1) {
			//				
			// cc.setMinWidth(100);
			// }
			String colid = crit.getColTable(k).getColName();
			if (colid.equals(Resurses.COLUMN_T_NAME)) {
				cc.setMinWidth(120);
			}
			boolean bb = Utils.getBooleanPref(crit, colid, true);
			if (!bb) {
				crit.getColTable(k).setCurrentState(false);
				tc.removeColumn(cc);
			} else {
				if (colid.equals(Resurses.COLUMN_T_ISCHILD)
						|| colid.equals(Resurses.COLUMN_T_ISMARR)
						|| colid.equals(Resurses.COLUMN_T_ISPARE)
						|| colid.equals(Resurses.COLUMN_T_SEX)) {
					cc.setMaxWidth(35);
				}
				if ((!Resurses.getDateFormat().equals("SE") && (colid
						.equals(Resurses.COLUMN_T_BIRT) || colid
						.equals(Resurses.COLUMN_T_DEAT)))
						|| colid.equals(Resurses.COLUMN_T_PID)) {
					cc.setCellRenderer(new RightTableCellRenderer());
				}
			}
		}

		this.scrollPane = new JScrollPane(this.table);
		this.scrollPane.setMinimumSize(new Dimension(0, 0));
		this.getContentPane().add(this.scrollPane);

		this.table.addMouseListener(popupListener);

		this.table.addMouseListener(this);

	}

	@SuppressWarnings("unchecked")
	private void initSorter(SearchCriteria crit) {
		TableRowSorter<SukuModel> sorter = (TableRowSorter<SukuModel>) this.table
				.getRowSorter();

		int i;
		int curre = 0;

		Comparator sukucompa;
		for (i = 0; i < crit.getColTableCount(); i++) {
			ColTable col = crit.getColTable(i);
			if (col.getCurrentState()) {
				if (col.getColName().equals(Resurses.COLUMN_T_NAME)) {
					sukucompa = new SukuNameComparator(Resurses.getLanguage());
					sorter.setComparator(curre, sukucompa);
					this.table.setRowSorter(sorter);
				} else if (col.getColName().equals(Resurses.COLUMN_T_PID)) {
					sukucompa = new SukuPidComparator();
					sorter.setComparator(curre, sukucompa);
					this.table.setRowSorter(sorter);
				} else if (col.getColName().equals(Resurses.COLUMN_T_ISMARR)
						|| col.getColName().equals(Resurses.COLUMN_T_ISCHILD)
						|| col.getColName().equals(Resurses.COLUMN_T_ISPARE)) {
					sukucompa = new SukuNumStringComparator();
					sorter.setComparator(curre, sukucompa);
					this.table.setRowSorter(sorter);
				} else if (col.getColName().equals(Resurses.COLUMN_T_BIRT)
						|| col.getColName().equals(Resurses.COLUMN_T_DEAT)) {
					sukucompa = new SukuDateComparator();
					sorter.setComparator(curre, sukucompa);
					this.table.setRowSorter(sorter);
				} else {
					sukucompa = new SukuStringComparator();
					sorter.setComparator(curre, sukucompa);
					this.table.setRowSorter(sorter);

				}
				curre++;
			}
		}

	}

	/**
	 * @return the kontroller instance
	 */
	public static SukuKontroller getKontroller() {
		return kontroller;
	}

	@Override
	public void setTitle(String title) {
		StringBuffer sb = new StringBuffer();
		sb.append(Resurses.getString(Resurses.SUKU));
		if (isConnected) {
			sb.append(" [");
			sb.append(databaseName);
			sb.append("]");
		}
		if (title != null) {
			sb.append(" - ");
			sb.append(title);
		}

		super.setTitle(sb.toString());

	}

	protected JButton makeNavigationButton(String imageName,
			String selectedName, String actionCommand, String toolTipText,
			String altText) throws IOException {
		// Look for the image.
		String imgLocation = "/images/" + imageName + ".gif";
		String selectedLocation = "/images/" + selectedName + ".gif";
		ImageIcon icon = null;
		ImageIcon selectedIcon = null;
		// System.out.println("NAV1: " + imageName);
		byte imbytes[] = new byte[8192];

		InputStream in = null;
		int imsize;
		try {
			in = this.getClass().getResourceAsStream(imgLocation);
			// System.out.println("NAV2: " + imageName + ":"+in);
			imsize = in.read(imbytes);
			if (imsize < imbytes.length) {
				icon = new ImageIcon(imbytes, altText);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}

		}

		in = null;
		try {
			in = this.getClass().getResourceAsStream(selectedLocation);
			// System.out.println("NAV2: " + imageName + ":"+in);
			imsize = in.read(imbytes);
			if (imsize < imbytes.length) {
				selectedIcon = new ImageIcon(imbytes, altText);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}

		}

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		if (selectedIcon != null) {
			button.setSelectedIcon(selectedIcon);
		}

		if (icon != null) { // image found
			button.setIcon(icon); // new ImageIcon(imbytes, altText));
		} else { // no image found
			button.setText(altText);
			logger.info("Resource not found: " + imgLocation);
		}

		return button;
	}

	protected JButton makeNavigationButton(String imageName,
			String actionCommand, String toolTipText, String altText)
			throws IOException {
		// Look for the image.
		String imgLocation = "/images/" + imageName + ".gif";
		// String negaLocation = "/images/" + imageName + "nega.gif";
		ImageIcon icon = null;
		// ImageIcon selectedIcon=null;
		// System.out.println("NAV1: " + imageName );
		byte imbytes[] = new byte[8192];

		InputStream in = null;
		try {
			in = this.getClass().getResourceAsStream(imgLocation);
			// System.out.println("NAV2: " + imageName + ":"+in);
			int imsize = in.read(imbytes);
			if (imsize < imbytes.length) {
				icon = new ImageIcon(imbytes, altText);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}

		}

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		// if (selectedIcon != null) {
		// button.setSelectedIcon(selectedIcon);
		// }

		if (icon != null) { // image found
			button.setIcon(icon); // new ImageIcon(imbytes, altText));
		} else { // no image found
			button.setText(altText);
			logger.info("Resource not found: " + imgLocation);
		}

		return button;
	}

	private void connectDb() {
		sukuObject = null;
		ConnectDialog cdlg = new ConnectDialog(this, kontroller, this.isWebApp);

		cdlg.setVisible(true);
		if (cdlg.wasOk()) {
			String name = cdlg.getHost();
			databaseName = cdlg.getDbName();
			String userid = cdlg.getUserId();
			String password = cdlg.getPassword();

			try {
				kontroller.getConnection(name, databaseName, userid, password);
				// SukuData.instance().connectToDatabase(host, dbname, userid,
				// password);
				this.isConnected = true;
				enableCommands();
				setTitle(null);
				SukuData serverVersion = kontroller
						.getSukuData("cmd=dbversion");

				if (serverVersion.generalArray != null
						&& serverVersion.generalArray.length > 0) {
					postServerVersion = serverVersion.generalArray[0];
					postServerVersion += " " + serverVersion.generalArray[1];
				}

				SukuData dblist = kontroller.getSukuData("cmd=dblista");

				if (dblist.generalArray != null) {
					StringBuffer sb = new StringBuffer();
					sb.append(databaseName);
					for (int i = 0; i < dblist.generalArray.length; i++) {
						if (!dblist.generalArray[i]
								.equalsIgnoreCase(databaseName)) {
							sb.append(";");
							sb.append(dblist.generalArray[i]);
						}
					}

					kontroller.putPref(cdlg, "DBNAMES", sb.toString());
				}
				// copy report languages here to static
				SukuData rlang = kontroller.getSukuData("cmd=repolanguages");

				repoLangList = rlang.generalArray;

				SukuData resp = Suku.kontroller.getSukuData("cmd=getsettings",
						"type=needle", "name=needle");

				if (resp.generalArray != null) {

					for (int i = 0; i < resp.generalArray.length; i++) {
						needle.add(resp.generalArray[i]);
					}
					if (resp.generalArray.length > 0) {
						tSubjectPButton.setEnabled(true);
					}
				}

				long startOfIntelli = System.currentTimeMillis();
				SukuData dat = Suku.kontroller.getSukuData("cmd=intelli");

				SukuSenser sens = SukuSenser.getInstance();

				if (dat != null && dat.vvTexts != null
						&& dat.vvTexts.size() > 5) {
					sens.setPlaces(dat.vvTexts.get(0));
					sens.setGivennames(dat.vvTexts.get(1));
					sens.setPatronymes(dat.vvTexts.get(2));
					sens.setSurnames(dat.vvTexts.get(3));
					sens.setDescriptions(dat.vvTexts.get(4));
					sens.setNoticeTypes(dat.vvTexts.get(5));
				}
				long endOfIntelli = System.currentTimeMillis();
				long timeOfIntelli = (endOfIntelli - startOfIntelli) / 1000;
				postServerVersion += ", Intellisens [" + timeOfIntelli
						+ "] secs";

				// if (dat != null && dat.generalArray != null
				// && dat.generalArray.length > 1) {
				// sens.setPaikat(dat.generalArray);
				// }

				return;

			} catch (SukuException e) {
				String e1 = e.getMessage();
				String[] e2 = { "Connection failed" };
				if (e1 != null) {
					e2 = e1.split("\n");
				}

				JOptionPane.showMessageDialog(this, e2[0], Resurses
						.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}

		}
		this.isConnected = false;
		enableCommands();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		try {
			if (cmd.equals(Resurses.ABOUT)) {

				AboutDialog about = new AboutDialog(this);
				about.setVisible(true);

			}
			if (cmd.equals(Resurses.UPDATEDB)) {
				SukuData resp = kontroller.getSukuData("cmd=initdb",
						"path=/sql/dbupdates.sql");
				String resu = "OK";
				if (resp.resu != null) {
					resu = resp.resu;
				} else {
					kontroller.getSukuData("cmd=excel",
							"path=resources/excel/PaikatExcel.xls",
							"page=coordinates");
					kontroller
							.getSukuData("cmd=excel",
									"path=resources/excel/TypesExcel.xls",
									"page=types");
					kontroller
							.getSukuData("cmd=excel",
									"path=resources/excel/TextsExcel.xls",
									"page=texts");
				}
				JOptionPane.showMessageDialog(this, resu, Resurses
						.getString(Resurses.SUKU),
						JOptionPane.INFORMATION_MESSAGE);
			} else if (cmd.equals(Resurses.NEWDB)) {
				int resu = JOptionPane.showConfirmDialog(this, Resurses
						.getString("CONFIRM_NEWDB"), Resurses
						.getString(Resurses.SUKU), JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resu == JOptionPane.YES_OPTION) {

					try {
						this.tableModel.resetModel(); // clear contents of table
						// first
						this.personView.reset();
						this.table.updateUI();
						this.scrollPane.updateUI();

						kontroller.getSukuData("cmd=initdb");
						kontroller.getSukuData("cmd=excel",
								"path=resources/excel/PaikatExcel.xls",
								"page=coordinates");
						kontroller.getSukuData("cmd=excel",
								"path=resources/excel/TypesExcel.xls",
								"page=types");
						kontroller.getSukuData("cmd=excel",
								"path=resources/excel/TextsExcel.xls",
								"page=texts");
						JOptionPane.showMessageDialog(this, Resurses
								.getString("CREATED_NEWDB"), Resurses
								.getString(Resurses.SUKU),
								JOptionPane.INFORMATION_MESSAGE);

					} catch (SukuException e1) {
						JOptionPane.showMessageDialog(this, Resurses
								.getString(Resurses.NEWDB), Resurses
								.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);
						logger.log(Level.WARNING, Resurses
								.getString(Resurses.NEWDB), e1);

						e1.printStackTrace();
					}
				}

			}
			if (cmd.equals("MENU_TOOLS_DBWORK")) {

				executeDbWork();
			}
			if (cmd.equals("MENU_COPY")) {
				System.out.println("EDIT-COPY by ctrl/c");
			}
			if (cmd.equals(Resurses.TOOLBAR_REMPERSON_ACTION)) {

				int isele = table.getSelectedRow();
				if (isele < 0) {
					JOptionPane.showMessageDialog(this, Resurses
							.getString("MESSAGE_NO_PERSON_TO_DELETE"), Resurses
							.getString(Resurses.SUKU),
							JOptionPane.ERROR_MESSAGE);
					return;

				}
				SukuRow row = (SukuRow) tableModel.getValueAt(isele,
						SukuModel.SUKU_ROW);

				PersonShortData p = tableMap.get(row.getPid());

				int resu = JOptionPane.showConfirmDialog(this, Resurses
						.getString("CONFIRM_DELETE")
						+ " " + p.getAlfaName(), Resurses
						.getString(Resurses.SUKU), JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resu == JOptionPane.YES_OPTION) {

					try {
						SukuData result = kontroller.getSukuData("cmd=delete",
								"pid=" + p.getPid());
						if (result.resu != null) {
							JOptionPane.showMessageDialog(this, result.resu,
									Resurses.getString(Resurses.SUKU),
									JOptionPane.ERROR_MESSAGE);
							logger.log(Level.WARNING, result.resu);
						}

						int mainpaneidx = personView.getMainPaneIndex();
						if (mainpaneidx > 1) {
							SukuTabPane pane = personView.getPane(mainpaneidx);
							if (p.getPid() == pane.getPid()) {
								personView.closeMainPane(false);
							} else {
								personView.refreshRelativesPane();

							}
						}

						for (int i = 0; i < needle.size(); i++) {
							String[] dbl = needle.get(i).split(";");
							int dblid = Integer.parseInt(dbl[0]);
							if (p.getPid() == dblid) {
								needle.remove(i);
								break;
							}
						}
						tSubjectPButton.setEnabled(needle.size() > 0);

						tableModel.removeRow(isele);

						table.updateUI();
						scrollPane.updateUI();

					} catch (SukuException e1) {
						JOptionPane.showMessageDialog(this, e1.getMessage(),
								Resurses.getString(Resurses.SUKU),
								JOptionPane.ERROR_MESSAGE);
						logger.log(Level.WARNING, e1.getMessage(), e1);
						e1.printStackTrace();
					}

				}
			}
			if (cmd.equals(Resurses.CONNECT)) {
				connectDb();
			} else if (cmd.equals(Resurses.DISCONNECT)) {
				disconnectDb();
			} else if (cmd.equals(Resurses.IMPORT_SUKU)) {
				importSuku2004Backup();
			} else if (cmd.equals("MENU_TOOLS_LOAD_COORDINATES")) {
				importDefaultCoordinates();
			} else if (cmd.equals("MENU_TOOLS_LOAD_TYPES")) {
				importDefaultTypes();
			} else if (cmd.equals(Resurses.IMPORT_GEDCOM)) {
				importGedcom();
				// JOptionPane.showMessageDialog(this, "Under construction",
				// Resurses.getString(Resurses.SUKU),
				// JOptionPane.INFORMATION_MESSAGE);

			} else if (cmd.equals(Resurses.IMPORT_HISKI)) {
				importFromHiski();

			} else if (cmd.equals("MENU_TOOLS_GROUP_MGR")) {
				openGroupWin();
			} else if (cmd.equals("MENU_TOOLS_VIEW_MGR")) {
				openViewWin();
			}

			else if (cmd.equals(Resurses.ADMIN)) {
				adminDb();
			} else if (cmd.equals(Resurses.EXIT)) {
				System.exit(0);
			} else if (cmd.equals(Resurses.SETTINGS)) {
				SettingsDialog sets = new SettingsDialog(this);
				sets.setVisible(true);
			} else if (cmd.equals(Resurses.PGSQL_STOP)) {
				String[] netcmd = { "net", "stop", "pgsql-8.3" };
				try {
					CommandExecuter.executeTheCommnad(netcmd);
					JOptionPane.showMessageDialog(this, Resurses
							.getString(Resurses.PGSQL_STOP)
							+ ":" + "OK");

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, Resurses
							.getString(Resurses.PGSQL_STOP)
							+ ":" + e1.getMessage());
					e1.printStackTrace();
				}
			} else if (cmd.equals(Resurses.PGSQL_START)) {
				String[] netcmd = { "net", "start", "pgsql-8.3" };
				try {
					CommandExecuter.executeTheCommnad(netcmd);
					JOptionPane.showMessageDialog(this, Resurses
							.getString(Resurses.PGSQL_START)
							+ ":" + "OK");
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, Resurses
							.getString(Resurses.PGSQL_START)
							+ ":" + e1.getMessage());

					e1.printStackTrace();
				}
			} else if (cmd.equals(Resurses.PRINT_PERSON)) {

				this.personView.testMe();

			}

			else if (cmd.equals(Resurses.SHOWINMAP)) {
				displayMap();
			}

			else if (cmd.equals(Resurses.QUERY)) {
				queryDb();
			} else if (cmd.equals(Resurses.TOOLBAR_NEWPERSON_ACTION)) {

				if (!tSubjectButton.isSelected() && activePersonPid > 0) {

					tSubjectButton.setSelected(true);
					PersonShortData pp = tableMap.get(activePersonPid);
					if (pp != null) {
						addToNeedle(pp);

					}
				}

				activePersonPid = 0;
				showPerson(activePersonPid);

			} else if (cmd.equals(Resurses.TOOLBAR_NOTE_ACTION)) {
				boolean theButt = !tNoteButton.isSelected();
				tNoteButton.setSelected(theButt);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_NOTE_ACTION, ""
						+ theButt);
			} else if (cmd.equals(Resurses.TOOLBAR_ADDRESS_ACTION)) {
				boolean theButt = !tAddressButton.isSelected();
				tAddressButton.setSelected(theButt);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_ADDRESS_ACTION, ""
						+ theButt);
			} else if (cmd.equals(Resurses.TOOLBAR_FARM_ACTION)) {
				boolean theButt = !tFarmButton.isSelected();
				tFarmButton.setSelected(theButt);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_FARM_ACTION, ""
						+ theButt);
			} else if (cmd.equals(Resurses.TOOLBAR_IMAGE_ACTION)) {
				boolean theButt = !tImageButton.isSelected();
				tImageButton.setSelected(theButt);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_IMAGE_ACTION, ""
						+ theButt);
			} else if (cmd.equals(Resurses.TOOLBAR_PRIVATE_ACTION)) {
				boolean theButt = !tPrivateButton.isSelected();
				tPrivateButton.setSelected(theButt);
				personView.resizeNoticePanes();
				kontroller.putPref(this, Resurses.TOOLBAR_PRIVATE_ACTION, ""
						+ theButt);
			} else if (cmd.equals(Resurses.TOOLBAR_SUBJECT_DOWN_ACTION)) {

				if (activePersonPid > 0) {

					PersonShortData pp = tableMap.get(activePersonPid);
					if (pp != null) {

						addToNeedle(pp);
					}
				}

			} else if (cmd.equals(Resurses.TOOLBAR_SUBJECT_UP_ACTION)) {
				if (needle.size() > 0) {
					String[] subjes = null;

					subjes = new String[needle.size()];
					for (int i = 0; i < needle.size(); i++) {
						String[] dbl = needle.get(i).split(";");
						subjes[i] = dbl[1];
					}

					Object par = JOptionPane.showInputDialog(personView,
							Resurses.getString("SELECT_PERSON")

							, Resurses.getString(Resurses.SUKU),
							JOptionPane.QUESTION_MESSAGE, null, subjes,
							subjes[0]);

					if (par != null) {
						int subrow = -1;
						for (int j = 0; j < subjes.length; j++) {

							if (par == subjes[j]) {
								subrow = j;
								break;
							}

						}
						if (subrow >= 0) {
							String[] dbl = needle.get(subrow).split(";");
							showPerson(Integer.parseInt(dbl[0]));
						}
					}
					// if (subjectPid > 0) {
					// showPerson(subjectPid);
					// }
				}
			} else if (cmd.equals(Resurses.TOOLBAR_NOTICES_ACTION)) {
				boolean notiButt = !tNoticesButton.isSelected();
				tNoticesButton.setSelected(notiButt);
				personView.resizeNoticePanes();
				kontroller
						.putPref(this, Resurses.NOTICES_BUTTON, "" + notiButt);
				personView.showNotices(tNoticesButton.isSelected());
			}
		} catch (Throwable ex) {

			logger.log(Level.WARNING, "Suku action", ex);
			JOptionPane.showMessageDialog(personView.getSuku(), "Suku action"
					+ ":" + ex.getMessage());

		}
	}

	private void importGedcom() {

		boolean isOpened;

		isOpened = kontroller.openLocalFile("ged");
		String dbname = kontroller.getFileName();
		logger.finest("Opened GEDCOM FILE status " + isOpened);
		if (isOpened) {
			this.tableModel.resetModel(); // clear contents of table first
			this.personView.reset();
			this.table.updateUI();
			this.scrollPane.updateUI();

			ImportGedcomDialog dlg;
			try {
				dlg = new ImportGedcomDialog(this, dbname);
			} catch (SukuException e) {
				return;
			}
			dlg.setVisible(true);

			String[] failedLines = dlg.getResult();
			if (failedLines != null) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < failedLines.length; i++) {
					sb.append(failedLines[i]);
				}
				if (sb.length() > 0) {

					java.util.Date d = new java.util.Date();
					SukuPad pad = new SukuPad(this, kontroller.getFileName()
							+ "\n" + d.toString() + "\n"
							+ Resurses.getString("GEDCOM_IMPORT_IGNORED")
							+ "\n\n" + sb.toString());
					pad.setVisible(true);
				}

			}

		}

	}

	/**
	 * @param pp
	 */
	private void addToNeedle(PersonShortData pp) {
		String dd = "" + pp.getPid() + ";" + pp.getAlfaName(true) + " "
				+ Utils.nv4(pp.getBirtDate()) + "-"
				+ Utils.nv4(pp.getDeatDate());
		needle.insertElementAt(dd, 0);

		for (int i = needle.size() - 1; i > 0; i--) {
			String[] dbl = needle.get(i).split(";");
			int dblid = Integer.parseInt(dbl[0]);
			if (pp.getPid() == dblid || i >= maxNeedle) {
				needle.remove(i);

			}
		}
		tSubjectPButton.setEnabled(true);
	}

	private void importDefaultTypes() {
		try {
			boolean openedFile = Suku.kontroller.openLocalFile("xls");
			if (openedFile) {
				kontroller.getSukuData("cmd=excel", "page=types");
				// "path=resources/excel/TypesExcel.xls",);
				JOptionPane.showMessageDialog(this, Resurses
						.getString("IMPORTED_TYPES"), Resurses
						.getString(Resurses.SUKU),
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	private void importDefaultCoordinates() {
		try {
			boolean openedFile = Suku.kontroller.openLocalFile("xls");
			if (openedFile) {
				kontroller.getSukuData("cmd=excel", "page=coordinates");
				// kontroller.getSukuData("cmd=excel",
				// "path=resources/excel/PaikatExcel.xls", "page=coordinates");
				JOptionPane.showMessageDialog(this, Resurses
						.getString("IMPORTED_COORDINATES"), Resurses
						.getString(Resurses.SUKU),
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void openGroupWin() {

		if (this.groupWin == null) {
			try {
				groupWin = new GroupMgrWindow(this);
			} catch (SukuException e) {

				e.printStackTrace();
				JOptionPane.showMessageDialog(this, Resurses.getString("SUKU")
						+ ":" + e.getMessage());
				return;

			}
			groupWin.setVisible(true);
		} else {
			groupWin.setVisible(true);
		}

	}

	private void openViewWin() {

		if (viewWin == null) {
			try {
				viewWin = new ViewMgrWindow(this);

			} catch (Exception e) {

				e.printStackTrace();
				JOptionPane.showMessageDialog(this, Resurses.getString("SUKU")
						+ ":" + e.getMessage());
				return;

			}
			viewWin.setVisible(true);
		} else {
			viewWin.setVisible(true);
		}

	}

	private void executeDbWork() {
		ToolsDialog dlg = new ToolsDialog(this);
		dlg.setVisible(true);
	}

	/**
	 * The toolbox button that defines if Note field is to be visible
	 * 
	 * @return true if button is depressed
	 */
	public boolean isShowNote() {
		return tNoteButton.isSelected();
	}

	/**
	 * The toolbox button that defines if Address field is to be visible
	 * 
	 * @return true if button is depressed
	 */
	public boolean isShowAddress() {
		return tAddressButton.isSelected();
	}

	/**
	 * The toolbox button that defines if village, farm and Croft fields are to
	 * be visible
	 * 
	 * @return true if button is depressed
	 */
	public boolean isShowFarm() {
		return tFarmButton.isSelected();
	}

	/**
	 * The toolbox button that defines if Image field is to be visible
	 * 
	 * @return true if button is depressed
	 */
	public boolean isShowImage() {
		return tImageButton.isSelected();
	}

	/**
	 * The toolbox button that defines if Notices are to be visible
	 * 
	 * @return true if button is depressed
	 */
	public boolean isShowNotices() {
		return tNoticesButton.isSelected();
	}

	/**
	 * The toolbox button that defines if Private text field is to be visible
	 * 
	 * @return true if button is depressed
	 */
	public boolean isShowPrivate() {
		return tPrivateButton.isSelected();
	}

	private void createReport(PersonShortData pers) {
		// if (this.reportFrame == null){
		// this.reportFrame = new ReportFrame(this);
		// }
		//		
		// this.reportFrame.createReport();
		//		
		//
		//
		// this.reportFrame.resetReport();

		ReportWorkerDialog dlg = new ReportWorkerDialog(this, kontroller, pers);
		dlg.setVisible(true);

	}

	private void importFromHiski() {

		personView.displayHiskiPane();

	}

	private void showPerson(int pid) {
		try {

			personView.displayNewPersonPane(pid);
		} catch (SukuException e) {
			JOptionPane.showMessageDialog(this, Resurses
					.getString(Resurses.TAB_PERSON)
					+ ":" + e.getMessage());
			logger
					.log(Level.SEVERE, "Failed to create person [" + pid + "]",
							e);
			e.printStackTrace();
		}
	}

	/**
	 * Whenever a personview is opened this stores the activePerson pid for use
	 * by Subject button
	 * 
	 * @param pid
	 */
	public void setActivePerson(int pid) {
		this.activePersonPid = pid;

	}

	/**
	 * 
	 * gets from db view the selected persons name if one person is selected
	 * 
	 * @return the selected name if only one row is selected
	 */
	public PersonShortData getSelectedPerson() {

		int[] ii = table.getSelectedRows();
		if (ii.length == 0)
			return null;

		int tabsize = table.getRowCount();
		if (ii.length == 1) {
			if (ii[0] < tabsize) {
				SukuRow rivi = (SukuRow) table.getValueAt(ii[0],
						SukuModel.SUKU_ROW);
				if (rivi == null)
					return null;
				PersonShortData pers = rivi.getPerson();
				return pers;
			}
		}
		return null;

	}

	/**
	 * updates group id for person on database window
	 * 
	 * @param pid
	 * @param groupId
	 */
	public void updateDbGroup(int pid, String groupId) {
		PersonShortData p = tableMap.get(pid);
		if (p != null) {
			p.setGroup(groupId);
		}
	}

	/**
	 * Refresh the DBView with updated data
	 */
	public void refreshDbView() {
		table.updateUI();
		scrollPane.updateUI();
	}

	/**
	 * Get an array of pid's of the selected rows
	 * 
	 * @return an int[] array of selected pid's
	 */
	public int[] getSelectedPids() {
		int[] pids = new int[table.getSelectedRows().length];
		int[] rows = table.getSelectedRows();
		// System.out.print("(");
		// for (int ii=0;ii<pids.length; ii++){
		// if (ii>0) System.out.print(";");
		// System.out.print(""+rows[ii]);
		// }
		// System.out.println(")");
		for (int i = 0; i < pids.length; i++) {
			// System.out.println("i=" + i + ";ri=" + rows[i] );
			SukuRow rivi = (SukuRow) table.getValueAt(rows[i],
					SukuModel.SUKU_ROW);
			if (rivi == null) {
				return new int[0];
			}
			pids[i] = rivi.getPerson().getPid();
		}
		return pids;
	}

	// private void copyPerson(int pid) {
	// String koe = "Leikepöydälle [" + pid + "] kamaa";
	private void copyToClip(String koe) {
		StringSelection stringSelection = new StringSelection(koe);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);

	}

	private void adminDb() {
		try {
			if (this.adminUtilities == null) {
				this.adminUtilities = new LocalAdminUtilities(this);
			}

			if (this.adminUtilities.connectPostgres()) {
				this.adminUtilities.setAlwaysOnTop(true);
				this.adminUtilities.setVisible(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses
					.getString(Resurses.ADMIN)
					+ ":" + e.getMessage());

		}

	}

	private void displayMap() {
		if (this.databaseWindowPersons == null
				|| this.databaseWindowPersons.length == 0)
			return;
		if (this.suomi == null) {
			this.suomi = new SuomiMap(this);
		}

		HashMap<String, PlaceLocationData> paikat = new HashMap<String, PlaceLocationData>();
		int idx;
		String paikka;

		PlaceLocationData place;

		for (idx = 0; idx < databaseWindowPersons.length; idx++) {
			paikka = databaseWindowPersons[idx].getBirtPlace();
			if (paikka != null) {

				place = paikat.get(paikka.toUpperCase());
				if (place == null) {
					place = new PlaceLocationData(paikka);
					paikat.put(paikka.toUpperCase(), place);
				} else {
					place.increment();
				}
			}
		}

		SukuData request = new SukuData();
		request.places = new PlaceLocationData[paikat.size()];

		Iterator<String> it = paikat.keySet().iterator();
		idx = 0;
		while (it.hasNext()) {
			request.places[idx] = paikat.get(it.next());
			idx++;
			// System.out.println("paikka: " + place.getName() + "[" +
			// place.getCount() + "]");
		}

		try {
			SukuData response = kontroller.getSukuData(request, "cmd=places");

			// FIXME: Method call passes null for nonnull parameter of
			// SuomiMap.displaySuomiMap(PlaceLocationData[])
			suomi.displaySuomiMap((response != null) ? response.places : null);

		} catch (SukuException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses
					.getString(Resurses.SHOWINMAP)
					+ ":" + e.getMessage());

		}

	}

	private PersonShortData[] databaseWindowPersons;

	/**
	 * <h1>Database window access</h1>
	 * 
	 * @return number of rows in databasewindow
	 */
	public int getDatabaseRowCount() {
		if (databaseWindowPersons == null)
			return 0;
		return databaseWindowPersons.length;
	}

	/**
	 * <h1>Database window access.</h1>
	 * 
	 * database window consist of rows containing instances of class
	 * PersonShortData
	 * 
	 * @param idx
	 * @return the PersonShortData of the window on row
	 */
	public PersonShortData getDatbasePerson(int idx) {
		return databaseWindowPersons[idx];
	}

	private void queryDb() {
		int i;
		try {

			sukuObject = null; // reset program "clipboard"
			String[] viewlist = { "cmd=viewlist" };
			SukuData vlist = kontroller.getSukuData(viewlist);

			SearchCriteria crit = SearchCriteria.getCriteria(this);
			crit.populateFields(vlist.generalArray);
			crit.setVisible(true);

			TableColumnModel tc = this.table.getColumnModel();
			TableColumn cc;
			String colhdr, tabnm;
			int j, k;
			for (k = 0; k < crit.getColTableCount(); k++) {
				tabnm = Resurses.getString(crit.getColTable(k).getColName());

				for (j = 0; j < tc.getColumnCount(); j++) {
					cc = tc.getColumn(j);
					colhdr = (String) cc.getHeaderValue();
					if (tabnm.equals(colhdr)) {
						if (crit.getColTable(k).getCurrentState() == false) {
							tc.removeColumn(cc);
							logger.fine("let's poistaa " + tabnm);
						}
						break;
					}
				}
			}

			int newpos = 0;
			String tabId;
			for (k = 0; k < crit.getColTableCount(); k++) {
				tabId = crit.getColTable(k).getColName();
				tabnm = Resurses.getString(tabId);

				if (crit.getColTable(k).getCurrentState()) {
					newpos++;
				}

				for (j = 0; j < tc.getColumnCount(); j++) {
					cc = tc.getColumn(j);
					colhdr = (String) cc.getHeaderValue();
					if (tabnm.equals(colhdr)) {
						break;
					}
				}
				if (j == tc.getColumnCount()) {

					if (crit.getColTable(k).getCurrentState() == true) {
						logger.fine("let's add " + tabnm);

						int colidx = tc.getColumnCount();

						String colnm = crit.getColName(k);
						int curidx = crit.getCurrentIndex(colnm);

						TableColumn c = new TableColumn(k);
						if (tabId.equals(Resurses.COLUMN_T_ISCHILD)
								|| tabId.equals(Resurses.COLUMN_T_ISMARR)
								|| tabId.equals(Resurses.COLUMN_T_ISPARE)) {
							c.setMaxWidth(35);
						}
						c.setHeaderValue(tabnm);

						if ((!Resurses.getDateFormat().equals("SE") && (tabId
								.equals(Resurses.COLUMN_T_BIRT) || tabId
								.equals(Resurses.COLUMN_T_DEAT)))
								|| tabId.equals(Resurses.COLUMN_T_PID)) {
							c.setCellRenderer(new RightTableCellRenderer());
						}
						tc.addColumn(c);
						tc.moveColumn(colidx, curidx - 1);
						// initSorter(crit);
					}
				}

			}

			Vector<String> v = new Vector<String>();
			v.add("cmd=plist");
			for (i = 0; i < crit.getFieldCount(); i++) {
				if (crit.getCriteriaField(i) != null
						&& !crit.getCriteriaField(i).equals("")) {
					v.add(crit.getFieldName(i)
							+ "="
							+ URLEncoder.encode(crit.getCriteriaField(i),
									"UTF-8"));
				}
			}
			String[] auxes = v.toArray(new String[0]);
			SukuData fam = kontroller.getSukuData(auxes);

			// System.out.println("ROWS: " + table.getRowCount());
			// System.out.println("MAP: " + tableMap.size());
			// System.out.println("MODEL: " + tableModel.getRowCount());

			// initSorter(crit);
			this.databaseWindowPersons = fam.pers;
			Arrays.sort(databaseWindowPersons);

			this.tableModel.resetModel(); // clear contents of table first
			// this.table.removeAll();
			this.table.clearSelection();
			// this.personView.reset();
			this.tableMap.clear();

			for (i = 0; i < this.databaseWindowPersons.length; i++) {

				String bdate = null, ddate = null;
				// String birtPlace=null, deatPlace=null;

				bdate = this.databaseWindowPersons[i].getBirtDate();
				if (bdate == null) {
					if (crit.isPropertySet(Resurses.COLUMN_T_BIRT_CHR)) {
						bdate = this.databaseWindowPersons[i].getChrDate();
					}
				}
				// birtPlace = this.databaseWindowPersons[i].getBirtPlace();
				ddate = this.databaseWindowPersons[i].getDeatDate();
				if (ddate == null) {
					if (crit.isPropertySet(Resurses.COLUMN_T_DEAT_BURI)) {
						ddate = this.databaseWindowPersons[i].getBuriedDate();
					}
				}
				// deatPlace=this.databaseWindowPersons[i].getDeatPlace();

				appendToLocalview(this.databaseWindowPersons[i]);
				// row = new
				// SukuRow(this.tableModel,this.databaseWindowPersons[i]);
				//						
				// this.tableModel.addRow(row);
				// int key = this.databaseWindowPersons[i].getPid();
				// this.tableMap.put(key, this.databaseWindowPersons[i]);
			}

			table.getRowSorter().allRowsChanged();
			this.statusPanel.setText("" + this.databaseWindowPersons.length);
			this.table.setRowHeight(20);
			this.table.setShowVerticalLines(false);

			this.scrollPane.setVisible(true);
			this.table.updateUI();
			this.scrollPane.updateUI();

		} catch (SukuException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses
					.getString(Resurses.QUERY)
					+ ":" + e1.getMessage());
		} catch (UnsupportedEncodingException ue) {
			ue.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses
					.getString(Resurses.QUERY)
					+ ":" + ue.getMessage());
		}
	}

	/**
	 * <h1>Database window access.</h1>
	 * 
	 * Update database window with data for person
	 * 
	 * @param p
	 * @throws SukuException
	 */
	public void updatePerson(PersonShortData p) throws SukuException {

		int key = p.getPid();
		PersonShortData ret = this.tableMap.put(key, p);

		SukuData resp = kontroller.getSukuData("cmd=virtual", "pid=" + key);

		if (resp.pidArray != null && resp.pidArray.length == 3) {
			p.setChildCount(resp.pidArray[0]);
			p.setMarrCount(resp.pidArray[1]);
			p.setPareCount(resp.pidArray[2]);

		}
		if (ret == null) {
			SukuRow row = new SukuRow(this, this.tableModel, p);
			tableModel.addRow(0, row);
		}
		table.updateUI();
		scrollPane.updateUI();

	}

	/**
	 * <h1>Database window access.</h1>
	 * 
	 * get PersonShortData from database window for person pid (pid = person
	 * identification number integer database specific identifier
	 * 
	 * 
	 * 
	 * @param pid
	 * @return PersonShortData instance for requested person
	 */
	public PersonShortData getPerson(int pid) {
		PersonShortData ss = tableMap.get(pid);
		return ss;
	}

	// public void appendToDbview(PersonShortData p){
	// PersonShortData ret = appendToLocalview(p);
	// if (ret != null) {
	// // for ( int i = 0;i < this.databaseWindowPersons.length;i++) {
	// // int cpid = this.databaseWindowPersons[i].getPid();
	// // if (cpid == p.getPid()){
	// // SukuRow row = new SukuRow(this,this.tableModel,p);
	// // this.tableModel.setValueAt(row, i, SukuModel.SUKU_ROW);
	// // }
	// // }
	// //
	// }
	// this.table.updateUI();
	// this.scrollPane.updateUI();
	// }

	private PersonShortData appendToLocalview(PersonShortData p) {
		int key = p.getPid();
		PersonShortData ret = this.tableMap.put(key, p);
		if (ret == null) {

			SukuRow row = new SukuRow(this, this.tableModel, p);
			this.tableModel.addRow(row);
		}
		return ret;
	}

	private void disconnectDb() {
		this.isConnected = false;

		SukuData request = new SukuData();
		request.generalArray = needle.toArray(new String[0]);

		try {
			Suku.kontroller.getSukuData(request, "cmd=updatesettings",
					"type=needle", "name=needle");

		} catch (SukuException ee) {
			JOptionPane.showMessageDialog(this, ee.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			ee.printStackTrace();
		}

		try {
			kontroller.getSukuData("cmd=logout");
			this.tableModel.resetModel(); // clear contents of table first
			this.personView.reset();
			this.databaseWindowPersons = null;
			this.table.updateUI();
			this.scrollPane.updateUI();
			sukuObject = null;
			enableCommands();
			setTitle(null);
		} catch (SukuException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, Resurses
					.getString(Resurses.DISCONNECT)
					+ ":" + e1.getMessage());
		}
	}

	private void importSuku2004Backup() {
		boolean isOpened;
		try {
			this.tableModel.resetModel(); // clear contents of table first
			this.personView.reset();
			this.table.updateUI();
			this.scrollPane.updateUI();

			isOpened = kontroller.openLocalFile("xml;xml.gz");

			logger.finest("Opened IMPORT FILE status " + isOpened);

			Import2004Dialog dlg = null;
			try {
				dlg = new Import2004Dialog(this, kontroller);
			} catch (SukuException ex) {
				return;
			}

			dlg.setVisible(true);

			dlg.setRunnerValue(Resurses.getString("IMPORT_PAIKAT"));
			kontroller.getSukuData("cmd=excel",
					"path=resources/excel/PaikatExcel.xls", "page=coordinates");
			dlg.setRunnerValue(Resurses.getString("IMPORT_TYPES"));
			kontroller.getSukuData("cmd=excel",
					"path=resources/excel/TypesExcel.xls", "page=types");
			kontroller.getSukuData("cmd=excel",
					"path=resources/excel/TextsExcel.xls", "page=texts");

			queryDb();

		} catch (SukuException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), Resurses
					.getString(Resurses.SUKU), JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	private Dimension currentSize = new Dimension();

	private void calcSize() {
		if (this.scrollPane == null)
			return;
		int lastLoc = this.splitPane.getDividerLocation();
		if (lastLoc < 0)
			lastLoc = 100;
		int lastWidth = this.currentSize.width;

		if ((getExtendedState() & ICONIFIED) != 0) {
			return;
		}

		this.currentSize = getSize();

		Dimension splitterSize = new Dimension();

		splitterSize.height = this.currentSize.height - 120;
		splitterSize.width = this.currentSize.width - SPLITTER_HORIZ_MARGIN * 3;

		int rooty = getRootPane().getLocation().y;
		rooty += this.menubar.getSize().height;

		int rootw = getRootPane().getSize().width;

		this.statusPanel.setBounds(2, this.currentSize.height - rooty - 30,
				rootw - 3, 26);

		int splitWidth = splitterSize.width;
		if (splitWidth < 0)
			splitWidth = 10;
		this.splitPane.setBounds(10, 30, splitWidth, splitterSize.height - 30);

		this.toolbar.setBounds(10, 0, splitWidth, 30);

		// System.out.println("WIDTHN: " + currentSize.width + "/" +
		// this.splitPane.getSize().width + "/" + splitterSize.width);

		if (lastWidth > 0) {
			if ((Math.abs(lastWidth - this.currentSize.width) > 10)) {
				int locaNew = (splitWidth * lastLoc) / lastWidth;

				// System.out.println("LOCAN/W: " + locaNew + "/" + lastLoc);
				this.splitPane.setDividerLocation(locaNew);
			}
			this.splitPane.updateUI();
		}

		this.scrollPane.updateUI();

	}

	/**
	 * does nothing
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
		// 

	}

	/**
	 * does nothing
	 */
	@Override
	public void componentMoved(ComponentEvent e) {
		//

	}

	/**
	 * recalculates sizes
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		calcSize();

	}

	/**
	 * does nothing
	 */
	@Override
	public void componentShown(ComponentEvent e) {

	}

	/**
	 * does nothing
	 */
	@Override
	public void menuCanceled(MenuEvent e) {

	}

	/**
	 * does nothing
	 */
	@Override
	public void menuDeselected(MenuEvent e) {

	}

	/**
	 * does nothing
	 */
	@Override
	public void menuSelected(MenuEvent e) {
		enableCommands();
	}

	private void enableCommands() {
		this.mConnect.setEnabled(!this.isConnected);
		this.mImport2004.setEnabled(this.isConnected);
		this.mImportGedcom.setEnabled(this.isConnected);
		this.mQuery.setEnabled(this.isConnected);
		this.mSettings.setEnabled(this.isConnected);
		this.mNewDatabase.setEnabled(this.isConnected);
		this.mDisconnect.setEnabled(this.isConnected);
		this.tQueryButton.setEnabled(this.isConnected);
		this.mImportHiski.setEnabled(this.isConnected);
		this.mNewPerson.setEnabled(this.isConnected);
		this.mDbWork.setEnabled(this.isConnected);
		this.mDbUpdate.setEnabled(this.isConnected);
		this.tPersonButton.setEnabled(this.isConnected);
		this.tQueryButton.setEnabled(this.isConnected);
		this.tMapButton.setEnabled(this.isConnected);
		this.tRemovePerson.setEnabled(this.isConnected);
		this.mGroupMgr.setEnabled(this.isConnected);
		this.tSubjectButton.setEnabled(this.isConnected);
	}

	/**
	 * mouse clicked on database window
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		int ii = this.table.getSelectedRow();
		if (ii < 0)
			return;
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			SukuRow row = (SukuRow) this.table.getValueAt(ii,
					SukuModel.SUKU_ROW);
			if (row == null)
				return;
			try {
				this.personView.setSubjectForFamily(row.getPerson());
			} catch (SukuException e1) {
				JOptionPane.showMessageDialog(this, "show " + row + " error "
						+ e1.getMessage());
				e1.printStackTrace();
			}
			//				
			logger.fine("Do something to " + row);
		}
		// else if (e.getClickCount() == 1 && e.getButton() ==
		// MouseEvent.BUTTON1){
		// SukuRow row = (SukuRow)this.table.getValueAt(ii, SukuModel.SUKU_ROW);
		// if (row != null) {
		// PersonShortData perso = row.getPerson();
		// if (perso != null) {
		// Suku.sukuObject = perso;
		// logger.fine("Copied to clipboard [" + perso.getPid() + "]: " +
		// perso.getAlfaName());
		// }
		// }
		// }

	}

	/**
	 * does nothing
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// System.out.println("ENTER: " + e);

	}

	/**
	 * does nothing
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// System.out.println("EXIT: " + e);

	}

	/**
	 * does nothing
	 */
	@Override
	public void mousePressed(MouseEvent e) {

	}

	/**
	 * does nothing
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

		//		
		// if (e.getButton()== MouseEvent.BUTTON3 && e.getClickCount()==1){
		//			
		//			
		// int yy = e.getY();
		// int rh = this.table.getRowHeight();
		// int ii = yy/rh;
		//			
		// SukuRow row = (SukuRow)this.tableModel.getValueAt(ii,
		// SukuModel.SUKU_ROW);
		//			
		// System.out.println("row has: " + row.getPid() + "/" + row.getName());
		//			
		// System.out.println("rrR:" + yy + "/" + ii+"/" + e);
		//			
		// }
		//		

	}

	/**
	 * does nothing
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		//
	}

	/**
	 * does nothing
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// int ii = this.table.getSelectedRow();
		// System.out.println("KKK: " + ii );

	}

	/**
	 * does nothing
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// System.out.println("KTYP:" + e);

	}

	/**
	 * does nothing
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {

	}

	/**
	 * does nothing
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
	}

	/**
	 * release map window
	 */
	@Override
	public void SukuFormClosing(JFrame me) {
		this.suomi = null;

	}

	/**
	 * release admin window
	 */
	@Override
	public void AdminFormClosing(JFrame me) {
		this.adminUtilities = null;

	}

	/**
	 * release hiski window
	 */
	@Override
	public void HiskiFormClosing() {
		personView.closeHiskiPane();

	}

	class PopupListener extends MouseAdapter implements ActionListener {

		private SukuRow activeRow = null;

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {

				Point clickPoint = e.getPoint();

				int rowAtPoint = table.rowAtPoint(clickPoint);
				if (rowAtPoint < 0)
					return;

				activeRow = (SukuRow) table.getValueAt(rowAtPoint,
						SukuModel.SUKU_ROW);

				SukuPopupMenu pop = SukuPopupMenu.getInstance();
				pop.setPerson(activeRow.getPerson());
				pop.show(e, e.getX(), e.getY());
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd == null || activeRow == null)
				return;
			SukuPopupMenu pop = SukuPopupMenu.getInstance();
			if (pop.getPerson() != null) {
				if (cmd.equals(Resurses.TAB_PERSON_TEXT)) {

					try {
						personView.setTextForPerson(pop.getPerson());
					} catch (SukuException e1) {
						JOptionPane.showMessageDialog(null, "SHOW PERSON: "
								+ pop.getPerson().getAlfaName() + " error "
								+ e1.getMessage());
						e1.printStackTrace();
					}

					// JOptionPane.showMessageDialog(null, "SHOW PERSON: " +
					// pop.getPerson().getAlfaName());

				} else if (cmd.equals(Resurses.TAB_FAMILY)) {
					try {
						personView.setSubjectForFamily(pop.getPerson());

					} catch (SukuException e1) {
						JOptionPane.showMessageDialog(null, "SHOW FAMILY: "
								+ pop.getPerson().getAlfaName() + " error "
								+ e1.getMessage());

						e1.printStackTrace();
					}
					//
				} else if (cmd.startsWith("HISKI") && cmd.length() > 5) {
					int hiskino = Integer.parseInt(cmd.substring(5));
					personView.setHiskiPid(hiskino, pop.getPerson().getPid(),
							pop.getPerson().getAlfaName());
				} else if (cmd.equals(Resurses.CREATE_REPORT)) {
					createReport(pop.getPerson());
				} else if (cmd.equals(Resurses.TAB_PERSON)) {

					showPerson(pop.getPerson().getPid());
					// setTitle(pop.getPerson().getAlfaName() + " "
					// + nv4(pop.getPerson().getBirtDate()) + "-" +
					// nv4(pop.getPerson().getDeatDate()));

				}
				if (cmd.equals(Resurses.MENU_COPY)) {
					// copyPerson(pop.getPerson().getPid());

					// System.out.println("taulussa " + table.getRowCount());

					PersonShortData perso = pop.getPerson();
					if (perso != null) {
						Suku.sukuObject = perso;
						logger.fine("Copied to clipboard [" + perso.getPid()
								+ "]: " + perso.getAlfaName());
					} else {
						return;
					}

					int[] ii = table.getSelectedRows();
					if (ii.length == 0)
						return;
					StringBuffer sb = new StringBuffer();

					sb.append(perso.getHeader() + "\n");
					for (int i = 0; i < ii.length; i++) {
						SukuRow rivi = (SukuRow) table.getValueAt(ii[i],
								SukuModel.SUKU_ROW);
						PersonShortData pers = rivi.getPerson();

						sb.append(pers.toString() + "\n");

					}
					sb.append(Resurses.getString("TEXT_COPIED"));
					sb.append(" ");
					sb.append(Resurses.getString("SUKUOHJELMISTO"));
					sb.append(" ");
					java.util.Date now = new java.util.Date();
					sb.append(now.toString());

					copyToClip(sb.toString());

				}
				if (cmd.equals(Resurses.MENU_NEEDLE)) {
					PersonShortData pp = pop.getPerson();
					if (pp != null) {
						addToNeedle(pp);
					}
				}
			}

		}
	}

	/**
	 * This is used to align columns in database view to the right dated,
	 * numbers etc
	 * 
	 * @author fikaakail
	 * 
	 */
	class RightTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected RightTableCellRenderer() {
			setHorizontalAlignment(JLabel.RIGHT);
		}

	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// do nothing

	}

	@Override
	public void GroupWindowClosing() {
		groupWin = null;

	}

	class SukuTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		SukuTransferHandler() {
			super();
		}

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			if (c instanceof JTable) {
				int midx = personView.getMainPaneIndex();
				if (midx >= 2) {
					personView.setSelectedIndex(midx + 1);
				}

				PersonShortData ps = getSelectedPerson();
				if (ps != null) {
					ps.setDragSource(Utils.PersonSource.DATABASE);
					return ps;
				}

			}
			return null;

		}

		protected void exportDone(JComponent c, Transferable t, int action) {
			// nothing needs to be done here
		}

	}

	class DbTable extends JTable {

		private static final long serialVersionUID = 1L;
		SukuModel model = null;

		DbTable(SukuModel model) {
			super(model);
			this.model = model;
		}

	}
}
