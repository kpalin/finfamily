package fi.kaila.suku.util;

import java.text.Collator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.logging.Logger;

import fi.kaila.suku.util.pojo.PersonShortData;

/**
 *Commonly used constants and access to resourceBudle
 * 
 * 
 * @author FIKAAKAIL
 * 
 * 
 */
public class Resurses {

	private static Logger logger = Logger.getLogger(Resurses.class.getName());

	/**
	 * 
	 */
	public static final String FILE = "FILE";
	/**
	 * 
	 */
	public static final String IMPORT_SUKU = "IMPORT_SUKU";
	/** */
	public static final String QUERY = "QUERY";
	/** */
	public static final String CONNECT = "CONNECT";
	/** */
	public static final String DISCONNECT = "DISCONNECT";
	/** */
	public static final String HELP = "HELP";
	/** */
	public static final String ABOUT = "ABOUT";
	/** */
	public static final String SUKU = "SUKUOHJELMISTO";
	/** */
	public static final String PRINT_PERSON = "PRINT_PERSON";
	/** */
	public static final String SHOWINMAP = "SHOWINMAP";
	/** */
	public static final String SHOWGRID = "SHOWGRID";
	/** */
	public static final String SETTINGS = "SETTINGS";
	/** */
	public static final String MENU_COPY = "MENU_COPY";
	/** */
	public static final String MENU_PASTE = "MENU_PASTE";
	/** */
	public static final String MENU_NEEDLE = "MENU_NEEDLE";
	/** */
	public static final String MENU_PASTE_BEFORE = "MENU_PASTE_BEFORE";
	/** */
	public static final String MENU_PASTE_AFTER = "MENU_PASTE_AFTER";
	/** */
	public static final String ABOUT_SERVER_VERSION = "ABOUT.SERVER_VERSION";
	/** */
	public static final String ABOUT_SUKU_VERSION = "ABOUT.SUKU_VERSION";
	/** */
	public static final String CRITERIA_CAPTION = "CRITERIA.CAPTION";
	/** */
	public static final String CRITERIA_SURNAME = "CRITERIA.SURNAME";
	/** */
	public static final String CRITERIA_GIVENNAME = "CRITERIA.GIVENNAME";
	/** */
	public static final String CRITERIA_PATRONYME = "CRITERIA.PATRONYME";
	/** */
	public static final String CRITERIA_NAME = "CRITERIA.NAME";
	/** */
	public static final String CRITERIA_BIRT = "CRITERIA.BIRT";
	/** */
	public static final String CRITERIA_DEAT = "CRITERIA.DEAT";
	/** */
	public static final String CRITERIA_CREATED = "CRITERIA.CREATED";
	/** */
	public static final String CRITERIA_VIEW = "CRITERIA.VIEW";
	/** */
	public static final String CRITERIA_GROUP = "CRITERIA.GROUP";
	/** */
	public static final String CRITERIA_BIRT_FROM = "CRITERIA.BIRT_FROM";
	/** */
	public static final String CRITERIA_BIRT_TO = "CRITERIA.BIRT_TO";
	/** */
	public static final String CRITERIA_BIRT_PLACE = "CRITERIA.BIRT_PLACE";
	/** */
	public static final String CRITERIA_DEAT_FROM = "CRITERIA.DEAT_FROM";
	/** */
	public static final String CRITERIA_DEAT_TO = "CRITERIA.DEAT_TO";
	/** */
	public static final String CRITERIA_DEAT_PLACE = "CRITERIA.DEAT_PLACE";
	/** */
	public static final String CRITERIA_CREATED_FROM = "CRITERIA.CREATED_FROM";
	/** */
	public static final String CRITERIA_CREATED_TO = "CRITERIA.CREATED_TO";
	/** */
	public static final String CRITERIA_RELATIVE_INFO = "CRITERIA.RELATIVE_INFO";

	/**  */
	public static final String CRITERIA_INIT_ERROR = "CRITERIA.INIT_ERROR";

	/**
	 * Full column for SEX
	 */
	public static final String COLUMN_T_SEX = "T_SEX";
	/**
	 * Full column for MARRIAGES
	 */
	public static final String COLUMN_T_ISMARR = "T_ISMARR";
	/**
	 * Full column for CHILDREN
	 */
	public static final String COLUMN_T_ISCHILD = "T_ISCHILD";
	/**
	 * Full column for PARENTS
	 */
	public static final String COLUMN_T_ISPARE = "T_ISPARE";
	/**
	 * Full column for TO-DO
	 */
	public static final String COLUMN_T_TODO = "T_TODO";
	/**
	 * Full column for NAME
	 */
	public static final String COLUMN_T_NAME = "T_NAME";

	/**
	 * Full column for PATRONYME
	 */
	public static final String COLUMN_T_PATRONYME = "T_PATRONYME";
	/**
	 * Full column for BIRT
	 */
	public static final String COLUMN_T_BIRT = "T_BIRT";
	/** */
	public static final String COLUMN_T_BIRTPLACE = "T_BIRTPLACE";

	/**
	 * Full column for DEATH
	 */
	public static final String COLUMN_T_DEAT = "T_DEAT";
	/** */
	public static final String COLUMN_T_DEATPLACE = "T_DEATPLACE";
	/** */
	public static final String COLUMN_T_OCCUPATION = "T_OCCUPATION";
	/**
	 * Full column for GROUP
	 */
	public static final String COLUMN_T_GROUP = "T_GROUP";
	/**
	 * Full column for System ID
	 */
	public static final String COLUMN_T_PID = "T_PID";
	/**
	 * Full column for User ID
	 */
	public static final String COLUMN_T_REFN = "T_REFN";

	/**
	 * Show all surnames in name/surname column
	 */
	public static final String COLUMN_T_ALL_NAMES = "T_ALL_NAMES";
	/**
	 * Show baptized as birth if birth not existing
	 */
	public static final String COLUMN_T_BIRT_CHR = "T_BIRT_CHR";
	/**
	 * Show buried as death if death is missing
	 */
	public static final String COLUMN_T_DEAT_BURI = "T_DEAT_BURI";
	/** */
	public static final String OK = "OK";
	/** */
	public static final String RESET = "RESET";
	/** */
	public static final String CLOSE = "CLOSE";
	/** */
	public static final String UPDATE = "UPDATE";
	/** */
	public static final String PLACECURRENT = "PLACECURRENT";
	/** */
	public static final String PLACEMISSING = "PLACEMISSING";
	/** */
	public static final String ADMIN = "ADMIN";
	/** */
	public static final String DATABASE = "DATABASE";
	/** */
	public static final String CONNEADMIN = "CONNEADMIN";
	/** */
	public static final String DATABASES = "DATABASES";
	/** */
	public static final String USERS = "USERS";
	/** */
	public static final String EXIT = "EXIT";
	/** */
	public static final String NEWUSER = "NEWUSER";
	/** */
	public static final String CHANGEPASSWORD = "CHANGEPASSWORD";
	/** */
	public static final String NEWDB = "NEWDB";
	/** */
	public static final String DROPDB = "DROPDB";
	/** */
	public static final String VERIFYPWD = "VERIFYPWD";
	/** */
	public static final String PASSWORD = "PASSWORD";
	/** */
	public static final String PASSWORDNOTVERIFY = "PASSWORDNOTVERIFY";
	/** */

	public static final String TAB_PERSON = "TAB_PERSON";
	/** */
	public static final String TAB_PERSON_TEXT = "TAB_PERSON_TEXT";
	/** */
	public static final String TAB_FAMILY = "TAB_FAMILY";
	/** */
	public static final String IMPORT_HISKI = "IMPORT_HISKI";
	/** */
	public static final String GET_HISKI = "GET_HISKI";
	/** */
	public static final String HISKI_NUMBER = "HISKI_NUMBER";
	/** */
	public static final String LOCALE = "LOCALE";
	/** */
	public static final String REPOLANG = "REPOLANG";
	/** */
	public static final String DATEFORMAT = "DATEFORMAT";

	/** */
	public static final String HISKI_UPLOAD = "HISKI_UPLOAD";
	/** */
	public static final String ABOUT_DB_VERSION = "ABOUT_DB_VERSION";
	/** */
	public static final String PRINT_REPORT = "PRINT_REPORT";
	/** */
	public static final String CREATE_REPORT = "CREATE_REPORT";
	/** */
	public static final String IMPORT_GEDCOM = "IMPORT_GEDCOM";
	/** */
	public static final String DESC = "DESC";
	/** */
	public static final String REPORT_SETTINGS_SAVE = "REPORT.SETTINGS.SAVE";
	/** */
	public static final String SETTING_IDX = "SETTING_INDEX";
	/** */
	public static final String NOTICES_BUTTON = "NOTICES_BUTTON";

	/**
	 * commands for getSukuData. All commands and their types should be
	 * collected here
	 */
	public static final String CMD_CREATE_TABLES = "crtables";
	/** */
	public static final String CMD_DESC_TYPE = "desc";
	/** */
	public static final String TOOLBAR_QUERY_IMAGE = "hae";
	/** */
	public static final String TOOLBAR_QUERY_ACTION = "QUERY";
	/** */
	public static final String TOOLBAR_PERSON_IMAGE = "person";
	/** */
	public static final String TOOLBAR_NEWPERSON_ACTION = "NEWPERSON";
	/** */
	public static final String TOOLBAR_MAP_IMAGE = "kartta";
	/** */
	public static final String TOOLBAR_MAP_ACTION = "SHOWINMAP";
	/** */
	public static final String TOOLBAR_REMPERSON_IMAGE = "PersonCross";
	/** */
	public static final String TOOLBAR_REMPERSON_ACTION = "REMPERSON";
	/** */
	public static final String TOOLBAR_ADDNOTICE_IMAGE = "tab_new";
	/** */
	public static final String TOOLBAR_ADDNOTICE_ACTION = "ADDNOTICE";
	/** */
	public static final String TOOLBAR_NOTICES_ACTION = "SHOW_NOTICES";
	/** */
	public static final String TOOLBAR_NOTE_ACTION = "SHOW_NOTE";
	/** */
	public static final String TOOLBAR_ADDRESS_ACTION = "SHOW_ADDRESS";
	/** */
	public static final String TOOLBAR_FARM_ACTION = "SHOW_FARM";
	/** */
	public static final String TOOLBAR_IMAGE_ACTION = "SHOW_IMAGE";
	/** */
	public static final String TOOLBAR_PRIVATE_ACTION = "SHOW_PRIVATE";
	/** */
	public static final String TOOLBAR_SUBJECT_DOWN_IMAGE = "NeulaAlas";
	/** */
	public static final String TOOLBAR_SUBJECT_UP_IMAGE = "NeulaYlos";
	/** */
	public static final String TOOLBAR_SUBJECT_ON_IMAGE = "NeulaOn";
	/** */
	public static final String TOOLBAR_SUBJECT_DOWN_ACTION = "SUBJECTC";
	/** */
	public static final String TOOLBAR_SUBJECT_UP_ACTION = "SUBJECTP";
	/** */
	public static final String HISKI_NORMALIZE = "HISKI_NORMALIZE";
	/** */
	public static final String CMD_ANC_TYPE = "anc";
	/** */
	public static final String PRIVACY_TEXT = "T";
	/** */
	public static final String PRIVACY_INDEX = "I";
	/** */
	public static final String PRIVACY_PRIVACY = "P";
	/** */
	public static final String MENU_LISTA = "MENU_LISTA";
	/** */
	public static final String MENU_OPEN_PERSON = "MENU_OPEN_PERSON";

	public static final String CRITERIA_PLACE = "CRITERIA_PLACE";

	public static final String CRITERIA_NOTICE_MISSING = "CRITERIA_NOTICE_MISSING";

	public static final String CRITERIA_FULL_TEXT = "CRITERIA_FULL_TEXT";

	public static final String CRITERIA_NOTICE = "CRITERIA_NOTICE";

	public static final String CRITERIA_NOTICE_EXISTS = "CRITERIA_NOTICE_EXISTS";

	public static final String CRITERIA_SURETY = "CRITERIA_SURETY";

	public static final String CRITERIA_SEX = "CRITERIA_SEX";

	private static Resurses myself = null;
	private static ExcelBundle resources = null;

	private static Locale currentLocale = new Locale("en");

	private static String repoLangu = "fi";

	private static String dateFormat = "FI";
	/** */
	public static String UPDATEDB = "MENU_UPDATEDB";

	private Resurses() {
		if (resources == null) {
			Locale.setDefault(currentLocale);
			resources = ExcelBundle.getBundle("excel/FinFamily", currentLocale);
		}
	}

	/**
	 * Change locale for Resource Bundle
	 * 
	 * @param newLocale
	 */
	public static synchronized void setLocale(String newLocale) {
		currentLocale = new Locale(newLocale);
		Locale.setDefault(currentLocale);
		repoLangu = newLocale;
		myself = null;
		resources = ExcelBundle.getBundle("excel/FinFamily", currentLocale);

	}

	/**
	 * Set report language
	 * 
	 * @param langu
	 */
	public static synchronized void setLanguage(String langu) {
		repoLangu = langu;
		Locale lloca = new Locale(repoLangu);
		PersonShortData.fiCollator = Collator.getInstance(lloca);
	}

	/**
	 * 
	 * @return the report languagecode
	 */
	public static synchronized String getLanguage() {
		return repoLangu;
	}

	/**
	 * set data format
	 * 
	 * @param format
	 */
	public static synchronized void setDateFormat(String format) {
		dateFormat = format;
	}

	/**
	 * date format is one of "FI","SE","UK","US"
	 * 
	 * @return the date format
	 */
	public static synchronized String getDateFormat() {
		return dateFormat;
	}

	/**
	 * 
	 * gets string from resource bundle
	 * 
	 * @param name
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
