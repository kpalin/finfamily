package fi.kaila.suku.util;

import java.text.Collator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Commonly used constants and access to resourceBudle.
 * 
 * @author FIKAAKAIL
 */
public class Resurses {

	private static Logger logger = Logger.getLogger(Resurses.class.getName());

	/** The Constant FILE. */
	public static final String FILE = "FILE";

	/** The Constant IMPORT_SUKU. */
	public static final String IMPORT_SUKU = "IMPORT_SUKU";

	/** The Constant QUERY. */
	public static final String QUERY = "QUERY";

	/** The Constant CONNECT. */
	public static final String CONNECT = "CONNECT";

	/** The Constant DISCONNECT. */
	public static final String DISCONNECT = "DISCONNECT";

	/** The Constant HELP. */
	public static final String HELP = "HELP";

	/** The Constant ABOUT. */
	public static final String ABOUT = "ABOUT";

	/** The Constant SUKU. */
	public static final String SUKU = "SUKUOHJELMISTO";

	/** The Constant PRINT_PERSON. */
	public static final String PRINT_PERSON = "PRINT_PERSON";

	/** The Constant SHOWINMAP. */
	public static final String SHOWINMAP = "SHOWINMAP";

	/** The Constant SHOWGRID. */
	public static final String SHOWGRID = "SHOWGRID";

	/** The Constant SETTINGS. */
	public static final String SETTINGS = "SETTINGS";

	/** The Constant MENU_COPY. */
	public static final String MENU_COPY = "MENU_COPY";

	/** The Constant MENU_PASTE. */
	public static final String MENU_PASTE = "MENU_PASTE";

	/** The Constant MENU_NEEDLE. */
	public static final String MENU_NEEDLE = "MENU_NEEDLE";

	/** The Constant MENU_PASTE_BEFORE. */
	public static final String MENU_PASTE_BEFORE = "MENU_PASTE_BEFORE";

	/** The Constant MENU_PASTE_AFTER. */
	public static final String MENU_PASTE_AFTER = "MENU_PASTE_AFTER";

	/** The Constant ABOUT_SERVER_VERSION. */
	public static final String ABOUT_SERVER_VERSION = "ABOUT.SERVER_VERSION";

	/** The Constant ABOUT_SUKU_VERSION. */
	public static final String ABOUT_SUKU_VERSION = "ABOUT.SUKU_VERSION";

	/** The Constant CRITERIA_CAPTION. */
	public static final String CRITERIA_CAPTION = "CRITERIA.CAPTION";

	/** The Constant CRITERIA_SURNAME. */
	public static final String CRITERIA_SURNAME = "CRITERIA.SURNAME";

	/** The Constant CRITERIA_GIVENNAME. */
	public static final String CRITERIA_GIVENNAME = "CRITERIA.GIVENNAME";

	/** The Constant CRITERIA_PATRONYME. */
	public static final String CRITERIA_PATRONYME = "CRITERIA.PATRONYME";

	/** The Constant CRITERIA_NAME. */
	public static final String CRITERIA_NAME = "CRITERIA.NAME";

	/** The Constant CRITERIA_BIRT. */
	public static final String CRITERIA_BIRT = "CRITERIA.BIRT";

	/** The Constant CRITERIA_DEAT. */
	public static final String CRITERIA_DEAT = "CRITERIA.DEAT";

	/** The Constant CRITERIA_CREATED. */
	public static final String CRITERIA_CREATED = "CRITERIA.CREATED";

	/** The Constant CRITERIA_VIEW. */
	public static final String CRITERIA_VIEW = "CRITERIA.VIEW";

	/** The Constant CRITERIA_GROUP. */
	public static final String CRITERIA_GROUP = "CRITERIA.GROUP";

	/** The Constant CRITERIA_BIRT_FROM. */
	public static final String CRITERIA_BIRT_FROM = "CRITERIA.BIRT_FROM";

	/** The Constant CRITERIA_BIRT_TO. */
	public static final String CRITERIA_BIRT_TO = "CRITERIA.BIRT_TO";

	/** The Constant CRITERIA_BIRT_PLACE. */
	public static final String CRITERIA_BIRT_PLACE = "CRITERIA.BIRT_PLACE";

	/** The Constant CRITERIA_DEAT_FROM. */
	public static final String CRITERIA_DEAT_FROM = "CRITERIA.DEAT_FROM";

	/** The Constant CRITERIA_DEAT_TO. */
	public static final String CRITERIA_DEAT_TO = "CRITERIA.DEAT_TO";

	/** The Constant CRITERIA_DEAT_PLACE. */
	public static final String CRITERIA_DEAT_PLACE = "CRITERIA.DEAT_PLACE";

	/** The Constant CRITERIA_CREATED_FROM. */
	public static final String CRITERIA_CREATED_FROM = "CRITERIA.CREATED_FROM";

	/** The Constant CRITERIA_CREATED_TO. */
	public static final String CRITERIA_CREATED_TO = "CRITERIA.CREATED_TO";

	/** The Constant CRITERIA_RELATIVE_INFO. */
	public static final String CRITERIA_RELATIVE_INFO = "CRITERIA.RELATIVE_INFO";

	/** The Constant CRITERIA_INIT_ERROR. */
	public static final String CRITERIA_INIT_ERROR = "CRITERIA.INIT_ERROR";

	/** Full column for SEX. */
	public static final String COLUMN_T_SEX = "T_SEX";

	/** Full column for MARRIAGES. */
	public static final String COLUMN_T_ISMARR = "T_ISMARR";

	/** Full column for CHILDREN. */
	public static final String COLUMN_T_ISCHILD = "T_ISCHILD";

	/** Full column for PARENTS. */
	public static final String COLUMN_T_ISPARE = "T_ISPARE";

	/** Full column for TO-DO. */
	public static final String COLUMN_T_UNKN = "T_UNKN";

	/** Full column for NAME. */
	public static final String COLUMN_T_NAME = "T_NAME";

	/** Full column for PATRONYME. */
	public static final String COLUMN_T_PATRONYME = "T_PATRONYME";

	/** Full column for BIRT. */
	public static final String COLUMN_T_BIRT = "T_BIRT";

	/** The Constant COLUMN_T_BIRTPLACE. */
	public static final String COLUMN_T_BIRTPLACE = "T_BIRTPLACE";

	/** Full column for DEATH. */
	public static final String COLUMN_T_DEAT = "T_DEAT";

	/** The Constant COLUMN_T_DEATPLACE. */
	public static final String COLUMN_T_DEATPLACE = "T_DEATPLACE";

	/** The Constant COLUMN_T_OCCUPATION. */
	public static final String COLUMN_T_OCCUPATION = "T_OCCUPATION";

	/** Full column for GROUP. */
	public static final String COLUMN_T_GROUP = "T_GROUP";

	/** Full column for System ID. */
	public static final String COLUMN_T_PID = "T_PID";

	/** Full column for User ID. */
	public static final String COLUMN_T_REFN = "T_REFN";

	/** Show all surnames in name/surname column. */
	public static final String COLUMN_T_ALL_NAMES = "T_ALL_NAMES";

	/** Show baptized as birth if birth not existing. */
	public static final String COLUMN_T_BIRT_CHR = "T_BIRT_CHR";

	/** Show buried as death if death is missing. */
	public static final String COLUMN_T_DEAT_BURI = "T_DEAT_BURI";

	/** The Constant OK. */
	public static final String OK = "OK";

	/** The Constant RESET. */
	public static final String RESET = "RESET";

	/** The Constant CLOSE. */
	public static final String CLOSE = "CLOSE";

	/** The Constant UPDATE. */
	public static final String UPDATE = "UPDATE";

	/** The Constant PLACECURRENT. */
	public static final String PLACECURRENT = "PLACECURRENT";

	/** The Constant PLACEMISSING. */
	public static final String PLACEMISSING = "PLACEMISSING";

	/** The Constant ADMIN. */
	public static final String ADMIN = "ADMIN";

	/** The Constant DATABASE. */
	public static final String DATABASE = "DATABASE";

	/** The Constant CONNEADMIN. */
	public static final String CONNEADMIN = "CONNEADMIN";

	/** The Constant DATABASES. */
	public static final String DATABASES = "DATABASES";

	/** The Constant USERS. */
	public static final String USERS = "USERS";

	/** The Constant EXIT. */
	public static final String EXIT = "EXIT";

	/** The Constant NEWUSER. */
	public static final String NEWUSER = "NEWUSER";

	/** The Constant CHANGEPASSWORD. */
	public static final String CHANGEPASSWORD = "CHANGEPASSWORD";

	/** The Constant NEWDB. */
	public static final String NEWDB = "NEWDB";

	/** The Constant DROPDB. */
	public static final String DROPDB = "DROPDB";

	/** The Constant VERIFYPWD. */
	public static final String VERIFYPWD = "VERIFYPWD";

	/** The Constant PASSWORDNOTVERIFY. */
	public static final String PASSWORDNOTVERIFY = "PASSWORDNOTVERIFY";

	/** The Constant TAB_PERSON. */

	public static final String TAB_PERSON = "TAB_PERSON";

	/** The Constant TAB_RELATIVES. */
	public static final String TAB_RELATIVES = "TAB_RELATIVES";

	/** The Constant TAB_PERSON_TEXT. */
	/** */
	public static final String TAB_PERSON_TEXT = "TAB_PERSON_TEXT";

	/** The Constant TAB_FAMILY. */
	public static final String TAB_FAMILY = "TAB_FAMILY";

	/** The Constant IMPORT_HISKI. */
	public static final String IMPORT_HISKI = "IMPORT_HISKI";

	/** The Constant GET_HISKI. */
	public static final String GET_HISKI = "GET_HISKI";

	/** The Constant HISKI_NUMBER. */
	public static final String HISKI_NUMBER = "HISKI_NUMBER";

	/** The Constant LOCALE. */
	public static final String LOCALE = "LOCALE";

	/** The Constant REPOLANG. */
	public static final String REPOLANG = "REPOLANG";

	/** The Constant DATEFORMAT. */
	public static final String DATEFORMAT = "DATEFORMAT";

	/** The Constant HISKI_UPLOAD. */
	public static final String HISKI_UPLOAD = "HISKI_UPLOAD";

	/** The Constant ABOUT_DB_VERSION. */
	public static final String ABOUT_DB_VERSION = "ABOUT_DB_VERSION";

	/** The Constant PRINT_REPORT. */
	public static final String PRINT_REPORT = "PRINT_REPORT";

	/** The Constant CREATE_REPORT. */
	public static final String CREATE_REPORT = "CREATE_REPORT";

	/** The Constant IMPORT_GEDCOM. */
	public static final String IMPORT_GEDCOM = "IMPORT_GEDCOM";

	/** The Constant DESC. */
	public static final String DESC = "DESC";

	/** The Constant REPORT_SETTINGS_SAVE. */
	public static final String REPORT_SETTINGS_SAVE = "REPORT.SETTINGS.SAVE";

	/** The Constant SETTING_IDX. */
	public static final String SETTING_IDX = "SETTING_INDEX";

	/** The Constant NOTICES_BUTTON. */
	public static final String NOTICES_BUTTON = "NOTICES_BUTTON";

	/**
	 * commands for getSukuData. All commands and their types should be
	 * collected here
	 */
	public static final String CMD_CREATE_TABLES = "crtables";

	/** The Constant CMD_DESC_TYPE. */
	public static final String CMD_DESC_TYPE = "desc";

	/** The Constant TOOLBAR_QUERY_IMAGE. */
	public static final String TOOLBAR_QUERY_IMAGE = "hae";

	/** The Constant TOOLBAR_QUERY_ACTION. */
	public static final String TOOLBAR_QUERY_ACTION = "QUERY";

	/** The Constant TOOLBAR_PERSON_IMAGE. */
	public static final String TOOLBAR_PERSON_IMAGE = "person";

	/** The Constant TOOLBAR_NEWPERSON_ACTION. */
	public static final String TOOLBAR_NEWPERSON_ACTION = "NEWPERSON";

	/** The Constant TOOLBAR_MAP_IMAGE. */
	public static final String TOOLBAR_MAP_IMAGE = "kartta";

	/** The Constant TOOLBAR_MAP_ACTION. */
	public static final String TOOLBAR_MAP_ACTION = "SHOWINMAP";

	/** The Constant TOOLBAR_REMPERSON_IMAGE. */
	public static final String TOOLBAR_REMPERSON_IMAGE = "PersonCross";

	/** The Constant TOOLBAR_REMPERSON_ACTION. */
	public static final String TOOLBAR_REMPERSON_ACTION = "REMPERSON";

	/** The Constant TOOLBAR_ADDNOTICE_IMAGE. */
	public static final String TOOLBAR_ADDNOTICE_IMAGE = "tab_new";

	/** The Constant TOOLBAR_ADDNOTICE_ACTION. */
	public static final String TOOLBAR_ADDNOTICE_ACTION = "ADDNOTICE";

	/** The Constant TOOLBAR_NOTICES_ACTION. */
	public static final String TOOLBAR_NOTICES_ACTION = "SHOW_NOTICES";

	/** The Constant TOOLBAR_NOTE_ACTION. */
	public static final String TOOLBAR_NOTE_ACTION = "SHOW_NOTE";

	/** The Constant TOOLBAR_ADDRESS_ACTION. */
	public static final String TOOLBAR_ADDRESS_ACTION = "SHOW_ADDRESS";

	/** The Constant TOOLBAR_FARM_ACTION. */
	public static final String TOOLBAR_FARM_ACTION = "SHOW_FARM";

	/** The Constant TOOLBAR_IMAGE_ACTION. */
	public static final String TOOLBAR_IMAGE_ACTION = "SHOW_IMAGE";

	/** The Constant TOOLBAR_PRIVATE_ACTION. */
	public static final String TOOLBAR_PRIVATE_ACTION = "SHOW_PRIVATE";

	/** The Constant TOOLBAR_SUBJECT_DOWN_IMAGE. */
	public static final String TOOLBAR_SUBJECT_DOWN_IMAGE = "NeulaAlas";

	/** The Constant TOOLBAR_SUBJECT_UP_IMAGE. */
	public static final String TOOLBAR_SUBJECT_UP_IMAGE = "NeulaYlos";

	/** The Constant TOOLBAR_SUBJECT_ON_IMAGE. */
	public static final String TOOLBAR_SUBJECT_ON_IMAGE = "NeulaOn";

	/** The Constant TOOLBAR_SUBJECT_DOWN_ACTION. */
	public static final String TOOLBAR_SUBJECT_DOWN_ACTION = "SUBJECTC";

	/** The Constant TOOLBAR_SUBJECT_UP_ACTION. */
	public static final String TOOLBAR_SUBJECT_UP_ACTION = "SUBJECTP";

	/** The Constant HISKI_NORMALIZE. */
	public static final String HISKI_NORMALIZE = "HISKI_NORMALIZE";

	/** The Constant CMD_ANC_TYPE. */
	public static final String CMD_ANC_TYPE = "anc";

	/** The Constant PRIVACY_TEXT. */
	public static final String PRIVACY_TEXT = "T";

	/** The Constant PRIVACY_INDEX. */
	public static final String PRIVACY_INDEX = "I";

	/** The Constant PRIVACY_PRIVACY. */
	public static final String PRIVACY_PRIVACY = "P";

	/** The Constant MENU_LISTA. */
	public static final String MENU_LISTA = "MENU_LISTA";

	/** The Constant MENU_OPEN_PERSON. */
	public static final String MENU_OPEN_PERSON = "MENU_OPEN_PERSON";

	/** The Constant CRITERIA_PLACE. */
	public static final String CRITERIA_PLACE = "CRITERIA_PLACE";

	/** The Constant CRITERIA_NOTICE_MISSING. */
	public static final String CRITERIA_NOTICE_MISSING = "CRITERIA_NOTICE_MISSING";

	/** The Constant CRITERIA_FULL_TEXT. */
	public static final String CRITERIA_FULL_TEXT = "CRITERIA_FULL_TEXT";

	/** The Constant CRITERIA_NOTICE. */
	public static final String CRITERIA_NOTICE = "CRITERIA_NOTICE";

	/** The Constant CRITERIA_NOTICE_EXISTS. */
	public static final String CRITERIA_NOTICE_EXISTS = "CRITERIA_NOTICE_EXISTS";

	/** The Constant CRITERIA_SURETY. */
	public static final String CRITERIA_SURETY = "CRITERIA_SURETY";

	/** The Constant CRITERIA_SEX. */
	public static final String CRITERIA_SEX = "CRITERIA_SEX";

	/** The Constant SW_UPDATE. */
	public static final String SW_UPDATE = "SW_UPDATE";

	/** The Constant LICENSE. */
	public static final String LICENSE = "LICENSE";

	/** The Constant EXPORT_GEDCOM. */
	public static final String EXPORT_GEDCOM = "EXPORT_GEDCOM";

	/** The Constant WIKI. */
	public static final String WIKI = "WIKI";

	/** The Constant HISKI_BROWSER. */
	public static final String HISKI_BROWSER = "HISKI_BROWSER";

	/** The Constant EXPORT_BACKUP. */
	public static final String EXPORT_BACKUP = "EXPORT_BACKUP";

	/** The Constant IMPORT_OTHER. */
	public static final String IMPORT_OTHER = "IMPORT_OTHER";

	private static Resurses myself = null;
	private static ExcelBundle resources = null;

	private static Locale currentLocale = new Locale("en");

	private static String repoLangu = "fi";

	private static String dateFormat = "FI";

	/** The UPDATEDB. */
	public static String UPDATEDB = "MENU_UPDATEDB";

	/** recognized gedcom events/attributes. */
	public static String gedcomTags = "|OCCU|EDUC|TITL|RESI|PROP|FACT"
			+ "|BIRT|CHR|DEAT|BURI|EVEN|EMIG|IMMI|CAST|DSCR|EDUC|IDNO"
			+ "|NATI|NCHI|NMR|RELI|SSN|CREM|BAPM|BASM|BLES|BARM"
			+ "|CHRA|CONF|FCOM|ORND|NATU|CENS|PROB|WILL|GRAD|RETI|";

	/** The gedcom attributes. */
	public static String gedcomAttributes = "|OCCU|EDUC|TITL|PROP|FACT"
			+ "|CAST|DSCR|EDUC|IDNO|NATI|RELI";

	private Resurses() {
		if (resources == null) {
			// Locale.setDefault(currentLocale);
			resources = new ExcelBundle();
			resources.importBundle("excel/FinFamily", "Program", currentLocale);
			// resources = ExcelBundle.getBundle("excel/FinFamily", "Program",
			// currentLocale);
		}
	}

	/**
	 * Change locale for Excel bundle.
	 * 
	 * @param newLocale
	 *            the new locale
	 */
	public static synchronized void setLocale(String newLocale) {
		currentLocale = new Locale(newLocale);
		repoLangu = newLocale;
		myself = null;
		resources = new ExcelBundle();

		resources.importBundle("excel/FinFamily", "Program", currentLocale);

	}

	private static ExcelBundle repoTexts = null; // new

	// HashMap<String,
	// String>();

	/**
	 * Gets the report string.
	 * 
	 * @param tag
	 *            the tag
	 * @return report text for tag
	 */
	public static synchronized String getReportString(String tag) {
		if (repoTexts == null) {
			repoTexts = new ExcelBundle();
			Locale locRepo = new Locale(getLanguage());
			repoTexts.importBundle("excel/FinFamily", "Report", locRepo);
		}
		if (repoTexts == null) {
			return tag;
		}
		return repoTexts.getString(tag);
	}

	/**
	 * Set report language.
	 * 
	 * @param langu
	 *            the new language
	 */
	public static synchronized void setLanguage(String langu) {
		repoLangu = langu;
		Locale lloca = new Locale(repoLangu);
		PersonShortData.fiCollator = Collator.getInstance(lloca);
		repoTexts = null;
	}

	/**
	 * Gets the default country.
	 * 
	 * @return the default country
	 */
	public static synchronized String getDefaultCountry() {
		if (myself == null) {
			myself = new Resurses();
		}
		SukuData sets;
		try {
			sets = Suku.kontroller.getSukuData("cmd=getsettings",
					"type=country", "index=0");

			if (sets.vvTypes != null && sets.vvTypes.size() > 0) {
				String[] parts = sets.vvTypes.get(0);
				return parts[1];
			}
		} catch (SukuException e) {

			logger.log(Level.WARNING, "Failed to get default country", e);

		}
		return "FI";

	}

	/**
	 * Sets the default country.
	 * 
	 * @param countryCode
	 *            the new default country
	 * @throws SukuException
	 *             the suku exception
	 */
	public static synchronized void setDefaultCountry(String countryCode)
			throws SukuException {
		if (myself == null) {
			myself = new Resurses();
		}

		Vector<String> v = new Vector<String>();

		v.add("cmd=savesettings");
		v.add("type=country");
		v.add("index=0");
		v.add("country=" + countryCode);

		Suku.kontroller.getSukuData(v.toArray(new String[0]));

	}

	/**
	 * Gets the language.
	 * 
	 * @return the report languagecode
	 */
	public static synchronized String getLanguage() {
		return repoLangu;
	}

	/**
	 * set data format.
	 * 
	 * @param format
	 *            the new date format
	 */
	public static synchronized void setDateFormat(String format) {
		dateFormat = format;
	}

	/**
	 * date format is one of "FI","SE","UK","US".
	 * 
	 * @return the date format
	 */
	public static synchronized String getDateFormat() {
		return dateFormat;
	}

	/**
	 * gets string from resource bundle.
	 * 
	 * @param name
	 *            the name
	 * @return value of name
	 */
	public static String getString(String name) {
		if (myself == null) {
			myself = new Resurses();
		}
		try {
			return resources.getString(name);
		} catch (MissingResourceException e) {
			logger.warning("Missing resource[" + name + "]");
			return name;
		}
	}

}
