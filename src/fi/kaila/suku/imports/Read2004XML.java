package fi.kaila.suku.imports;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.NameArray;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * <h1>Import Suku 2004 backup file</h1>
 * 
 * <p>
 * Importing of the backup file is executed using XML SAX methods
 * </p>
 * .
 * 
 * @author Kaarle Kaila
 */
public class Read2004XML extends DefaultHandler {

	private static Logger logger = Logger
			.getLogger(Read2004XML.class.getName());

	/** The laskuri units. */
	int laskuriUnits = 0;

	/** The laskuri relations. */
	int laskuriRelations = 0;

	/** The laskuri groups. */
	int laskuriGroups = 0;

	/** The laskuri conversion. */
	int laskuriConversion = 0;

	/** The laskuri views. */
	int laskuriViews = 0;

	/** The last unit id. */
	String lastUnitId = null;

	/** The last max aid. */
	int lastMaxAid = 0;

	/** The base folder. */
	String baseFolder = "";

	private String qName = null;
	private StringBuffer currentChars = null;
	private String finFamilyVersion = null;
	private Connection con = null;
	private String urli = null;
	private String databaseFolder = null;
	private boolean databaseHasImages = false;

	/** The error line. */
	Vector<String> errorLine = new Vector<String>();
	private volatile String currentStatus = null;
	private Vector<String> namelist = null;
	private Vector<String> placelist = null;
	/** The cdf. */
	SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd");

	private static final String INSERT_UNIT = "insert into Unit "
			+ "(PID,tag,sex,groupid,privacy,sid,createdate)"
			+ " values (?,'INDI',?,?,?,?,?)";

	private static final String UPDATE_UNIT = "update Unit set userrefn = ?,  sourcetext = ?, privatetext = ? "
			+ "where pid = ? ";

	private static final String INSERT_NAME_NOTICE = "insert into UnitNotice "
			+ "(PID,PNID,tag,privacy,givenname,patronym,prefix,surname,postfix,createdate)"
			+ " values (?,?,?,?,?,?,?,?,?,?)";

	private static final String INIT_UNIT_NOTICE = "insert into UnitNotice "
			+ "(PID,PNID,tag,privacy,noticerow,surety,modified,createdate) "
			+ "values (?,?,?,?,?, ?,?,?)";

	private static final String UPDATE_UNIT_NOTICE = "update UnitNotice "
			+ "set noticetype=?,description=?,dateprefix=?,fromdate=?,todate=?,"
			+ "place=?,address=?,postalcode=?,postoffice=?,state=?,country=?,email=?,"
			+ "notetext=?,mediafilename=?,mediatitle=?,givenname=?,patronym=?,prefix=?,"
			+ "surname=?,postfix=?,SID=?,village=?,farm=?,croft=?,sourcetext=?,privatetext=? "
			+ "where pnid=?";

	private static final String INSERT_UNIT_LANGUAGE = "insert into UnitLanguage "
			+ "(PID,PNID,tag,langCode,noticetype,"
			+ "description,place,notetext,mediatitle,modified,createdate) "
			+ "values (?,?,?,?,?, ?,?,?,?,?, ?)";

	private static final String UPDATE_IMAGE_DATA = "update UnitNotice set MediaData = ?,mediaWidth = ?"
			+ ",mediaheight = ? where PNID = ? ";

	private static final String UPDATE_UNIT_INDEX_DATA = "update UnitNotice set RefNames = ?,RefPlaces = ? where PNID = ? ";

	private static final String INSERT_RELATION = "insert into Relation "
			+ "(RID,PID,tag,relationrow,surety,modified,createdate) values (?,?,?,?,?,?,?)";

	private static final String INSERT_RELATION_NOTICE = "insert into RelationNotice "
			+ "(RNID,RID,tag,noticerow,surety,description,relationtype,"
			+ "dateprefix,fromdate,todate,place,notetext,"
			+ "SID,sourcetext,privatetext,modified,createdate) values (?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)";

	private static final String INSERT_RELATION_ADOPTION = "insert into RelationNotice "
			+ "(RNID,RID,tag,noticerow,createdate) values (?,?,?,?,?)";

	private static final String INSERT_RELATION_LANGUAGE = "insert into RelationLanguage "
			+ "(RNID,RID,langCode,relationtype,"
			+ "description,place,notetext,modified,createdate) "
			+ "values (?,?,?,?,?,?,?,?,?)";

	private static final String INSERT_GROUP = "insert into groups (groupid,name,description) "
			+ "values (?,?,?) ";

	private static final String INSERT_SOURCES = "insert into sources (SID,sourcenote) values (?,?) ";

	private static final String INSERT_SUKU_VARIABLES = "insert into sukuvariables "
			+ "(owner_name,owner_info,owner_address,owner_postalcode,owner_postoffice,owner_country,owner_email) "
			+ "values (?,?,?,?,?,?,?) ";

	private static final String INSERT_CONVERSION = "insert into conversions "
			+ "(fromtext,langcode,rule,totext) values (?,?,?,?)";

	private static final String INSERT_VIEW = "insert into views (vid,name,createdate) values (?,?,?) ";

	private static final String INSERT_VIEW_UNIT = "insert into viewunits (vid,pid) values (?,?) ";

	private static final String UPDATE_WIFE_REL = "update relation set tag = 'WIFE' where tag = 'MARR' and pid in "
			+ "(select pid from unit where sex = 'M')";
	private static final String UPDATE_HUSB_REL = "update relation set tag = 'HUSB' where tag = 'MARR' and pid in "
			+ "(select pid from unit where sex = 'F')";

	private static final String UPDATE_MOTH_REL = "update relation set tag = 'MOTH' where tag = 'FATH' and "
			+ "rid in (select rid from relation inner join unit on relation.pid = unit.pid and sex = 'F' and relation.tag='CHIL')";

	private static final String CREATE_GROUPS = "create table groups "
			+ "(groupid varchar primary key," + "name varchar,"
			+ "description varchar" + ") ";
	private static final String DROP_GROUPS = "drop table if exists groups ";

	private static final String CREATE_SOURCES = "create table sources "
			+ "(sid integer primary key," + "sourcenote varchar" + ") ";
	private static final String ADD_UNIT_SID = "alter table unit add column sid integer";
	private static final String ADD_UNITNOTICE_SID = "alter table unitnotice add column sid integer";
	private static final String ADD_RELATIONNOTICE_SID = "alter table relationnotice add column sid integer";

	private static final String DROP_UNIT_SID = "alter table unit drop column sid";
	private static final String DROP_UNITNOTICE_SID = "alter table unitnotice drop column sid";
	private static final String DROP_RELATIONNOTICE_SID = "alter table relationnotice drop column sid";

	private static final String DROP_SOURCES = "drop table if exists sources ";

	private static final String UPDATE_UNIT_SOURCES = "update unit set sourcetext "
			+ "= (select sourcenote from sources where sources.sid = unit.sid) "
			+ "|| ' ' || coalesce(sourcetext,'') 	where unit.sid is not null ";

	private static final String UPDATE_NOTICE_SOURCES = "update unitnotice set sourcetext "
			+ "= (select sourcenote from sources where sources.sid = unitnotice.sid) "
			+ "|| ' ' || coalesce(sourcetext,'') 	where unitnotice.sid is not null ";

	private static final String UPDATE_RELATION_SOURCES = "update relationnotice set sourcetext "
			+ "= (select sourcenote from sources where sources.sid = relationnotice.sid) "
			+ "|| ' ' || coalesce(sourcetext,'') 	where relationnotice.sid is not null ";

	private static final String UPDATE_GROUPS = "update Unit as U  set groupid = "
			+ "(select name from groups where U.groupId = groups.groupid) where U.groupid is not null";

	private static final String VACUUM = "vacuum full analyze;";

	private static final String unitGivenNameTG = "|genealog|units|unit|name|givenname";
	private static final String unitPrefixTG = "|genealog|units|unit|name|prefix";
	private static final String unitSurnameTG = "|genealog|units|unit|name|surname";
	private static final String unitPostfixTG = "|genealog|units|unit|name|postfix";
	private static final String unitNameTG = "|genealog|units|unit|name";
	private static final String unitRefnTG = "|genealog|units|unit|userrefn";
	private static final String genealogTG = "|genealog";
	private static final String unitTG = "|genealog|units|unit";
	private static final String unitSourceTG = "|genealog|units|unit|source";
	private static final String unitPrivateTextTG = "|genealog|units|unit|privatetext";

	private static final String noticeTG = "|genealog|units|unit|notices|notice";
	private static final String noticeTypeTG = "|genealog|units|unit|notices|notice|noticetype";

	private static final String noticeDateTG = "|genealog|units|unit|notices|notice|date";
	private static final String noticeDateStartTG = "|genealog|units|unit|notices|notice|date|start";
	private static final String noticeDateEndTG = "|genealog|units|unit|notices|notice|date|end";
	private static final String noticeDescriptTG = "|genealog|units|unit|notices|notice|description";
	private static final String noticeNoteTextTG = "|genealog|units|unit|notices|notice|notetext";
	private static final String noticeNamelistTG = "|genealog|units|unit|notices|notice|namelist";
	private static final String noticeNamelistNameTG = "|genealog|units|unit|notices|notice|namelist|name";
	private static final String noticePlacelistTG = "|genealog|units|unit|notices|notice|placelist";
	private static final String noticePlacelistPlaceTG = "|genealog|units|unit|notices|notice|placelist|place";
	private static final String noticePlaceTG = "|genealog|units|unit|notices|notice|place";
	private static final String noticeVillageTG = "|genealog|units|unit|notices|notice|village";
	private static final String noticeFarmTG = "|genealog|units|unit|notices|notice|farm";
	private static final String noticeCroftTG = "|genealog|units|unit|notices|notice|croft";
	private static final String noticeGivenNameTG = "|genealog|units|unit|notices|notice|name|givenname";
	private static final String noticeFirstNameTG = "|genealog|units|unit|notices|notice|name|firstname";
	private static final String noticePatronymTG = "|genealog|units|unit|notices|notice|name|patronym";
	private static final String noticePrefixTG = "|genealog|units|unit|notices|notice|name|prefix";
	private static final String noticeSurnameTG = "|genealog|units|unit|notices|notice|name|surname";
	private static final String noticePostfixTG = "|genealog|units|unit|notices|notice|name|postfix";
	private static final String noticeSourceTG = "|genealog|units|unit|notices|notice|source";
	private static final String noticePrivateTextTG = "|genealog|units|unit|notices|notice|privatetext";
	private static final String noticeMediaTitleTG = "|genealog|units|unit|notices|notice|media|mediatitle";
	private static final String noticeMediaFilenameTG = "|genealog|units|unit|notices|notice|media|mediafilename";
	private static final String noticeAddressTG = "|genealog|units|unit|notices|notice|address|street";
	private static final String noticePostalCodeTG = "|genealog|units|unit|notices|notice|address|postalcode";
	private static final String noticePostOfficeTG = "|genealog|units|unit|notices|notice|address|postoffice";
	private static final String noticeStateTG = "|genealog|units|unit|notices|notice|address|state";
	private static final String noticeCountryTG = "|genealog|units|unit|notices|notice|address|country";
	private static final String noticeEmailTG = "|genealog|units|unit|notices|notice|address|email";

	private static final String noticeLanguageTG = "|genealog|units|unit|notices|notice|language";
	private static final String noticeLanguageTypeTG = "|genealog|units|unit|notices|notice|language|relationtype";
	private static final String noticeLanguageDescriptionTG = "|genealog|units|unit|notices|notice|language|description";
	private static final String noticeLanguagePlaceTG = "|genealog|units|unit|notices|notice|language|place";
	private static final String noticeLanguageMediaTitleTG = "|genealog|units|unit|notices|notice|language|mediatitle";
	private static final String noticeLanguageNoteTextTG = "|genealog|units|unit|notices|notice|language|notetext";

	private static final String relationTG = "|genealog|relations|relation";
	private static final String relationDescriptionTG = "|genealog|relations|relation|description";
	private static final String relationBegTypeTG = "|genealog|relations|relation|begintype";
	private static final String relationBegDateTG = "|genealog|relations|relation|begindate";
	private static final String relationBegDateStartTG = "|genealog|relations|relation|begindate|start";
	private static final String relationBegDateEndTG = "|genealog|relations|relation|begindate|end";
	private static final String relationBegPlaceTG = "|genealog|relations|relation|beginplace";
	private static final String relationEndTypeTG = "|genealog|relations|relation|endtype";
	private static final String relationEndDateTG = "|genealog|relations|relation|enddate";
	private static final String relationEndDateStartTG = "|genealog|relations|relation|enddate|start";
	private static final String relationEndDateEndTG = "|genealog|relations|relation|enddate|end";
	private static final String relationEndPlaceTG = "|genealog|relations|relation|endplace";
	private static final String relationSourceTG = "|genealog|relations|relation|source";
	private static final String relationPrivateTextTG = "|genealog|relations|relation|privatetext";
	private static final String relationNoteTextTG = "|genealog|relations|relation|notetext";

	private static final String relationNoticeTG = "|genealog|relations|relation|relationnotice";
	private static final String relationNoticeTypeTG = "|genealog|relations|relation|relationnotice|relationtype";
	private static final String relationNoticeDescriptionTG = "|genealog|relations|relation|relationnotice|description";
	private static final String relationNoticeDateTG = "|genealog|relations|relation|relationnotice|date";
	private static final String relationNoticeDateStartTG = "|genealog|relations|relation|relationnotice|date|start";
	private static final String relationNoticeDateEndTG = "|genealog|relations|relation|relationnotice|date|end";
	private static final String relationNoticePlaceTG = "|genealog|relations|relation|relationnotice|place";
	private static final String relationNoticeNoteTextTG = "|genealog|relations|relation|relationnotice|notetext";
	private static final String relationNoticeSourceTG = "|genealog|relations|relation|relationnotice|sourcetext";
	private static final String relationNoticePrivateTextTG = "|genealog|relations|relation|relationnotice|privatetext";

	private static final String relationLanguageTG = "|genealog|relations|relation|relationnotice|language";
	private static final String relationLanguageTypeTG = "|genealog|relations|relation|relationnotice|language|relationtype";
	private static final String relationLanguageDescriptionTG = "|genealog|relations|relation|relationnotice|language|description";
	private static final String relationLanguagePlaceTG = "|genealog|relations|relation|relationnotice|language|place";
	private static final String relationLanguageNoteTextTG = "|genealog|relations|relation|relationnotice|language|notetext";

	private static final String groupTG = "|genealog|groups|group";
	private static final String groupNameTG = "|genealog|groups|group|groupname";
	private static final String groupDescriptionTG = "|genealog|groups|group|description";

	private static final String sourceTG = "|genealog|sources|source-data";
	// private static final String sourceNoteTextTG =
	// "|genealog|sources|source-data|notetext";
	private static final String conversionsTG = "|genealog|conversions";
	private static final String conversionTG = "|genealog|conversions|conversion";
	private static final String conversionsFromTG = "|genealog|conversions|conversion|fromtext";
	private static final String conversionsToTG = "|genealog|conversions|conversion|totext";

	private static final String sukuTG = "|genealog|owner";
	private static final String sukuNameTG = "|genealog|owner|ownername";
	private static final String sukuInfoTG = "|genealog|owner|ownerinfo";
	private static final String sukuAddressTG = "|genealog|owner|address|street";
	private static final String sukuPostalCodeTG = "|genealog|owner|address|postalcode";
	private static final String sukuPostOfficeTG = "|genealog|owner|address|postoffice";
	private static final String sukuCountryTG = "|genealog|owner|address|country";
	private static final String sukuEmailTG = "|genealog|owner|address|email";
	private static final String sukuMediaPathTG = "|genealog|owner|mediapath";

	private static final String viewTG = "|genealog|views|view";
	private static final String viewNameTG = "|genealog|views|view|description";
	private static final String viewUnitTG = "|genealog|views|view|viewunits|ref";

	private static final String typesTG = "|genealog|types";
	private static final String typeTG = "|genealog|types|type";
	private static final String typeNameTG = "|genealog|types|type|name";

	private String oldCode = null;

	private String currentEle = "";

	private String unitGivenName = null;
	private String unitSurName = null;
	private String unitPrefix = null;
	private String unitPostfix = null;
	private String unitGroupId = null;
	private String unitGroup = null;
	private String unitSex = null;
	private String unitRefn = null;
	private String unitId = null;
	private String unitTag = null;
	private String unitPrivacy = null;
	private String unitSourceId = null;
	private String unitPrivateText = null;
	private String unitSourceText = null;
	private String unitCreateDate = null;

	private String noticeTag = null;
	private String noticeRow = null;
	private String noticeType = null;
	private String noticePrivacy = null;
	private String noticeSurety = null;
	private String noticeDescription = null;
	private String noticeDatePrefix = null;
	private String noticeDateFrom = null;
	private String noticeDateTo = null;
	private String noticePlace = null;
	private String noticeVillage = null;
	private String noticeFarm = null;
	private String noticeCroft = null;
	private String noticeAddress = null;
	private String noticePostOffice = null;
	private String noticePostalCode = null;
	private String noticeState = null;
	private String noticeCountry = null;
	private String noticeEmail = null;
	private String noticeNoteText = null;
	private String noticeMediaFilename = null;
	private String noticeMediaTitle = null;
	private String noticeGivenName = null;
	private String noticePatronym = null;
	private String noticeSurname = null;
	private String noticePrefix = null;
	private String noticePostfix = null;
	private String noticeSourceId = null;
	private String noticePrivateText = null;
	private String noticeSourceText = null;
	private String noticeModifiedDate = null;
	private String noticeCreateDate = null;

	private String noticeLanguage = null;
	private String noticeLanguageType = null;
	private String noticeLanguageDescription = null;
	private String noticeLanguagePlace = null;
	private String noticeLanguageMediaTitleText = null;
	private String noticeLanguageNoteText = null;
	private String noticeLanguageModifiedDate = null;
	private String noticeLanguageCreateDate = null;
	private int pnid = 0;
	private int unitPid = 0;

	private int rid;

	private String relationTag = null;
	private String relationTaga = null;
	private String relationTagb = null;
	private String relationIdA = null;
	private String relationIdB = null;
	private String relationRowA = null;
	private String relationRowB = null;
	private String relationSurety = null;
	private String relationDescription = null;
	private String relationBegType = null;
	private String relationBegDatePrefix = null;
	private String relationBegDateFrom = null;
	private String relationBegDateTo = null;
	private String relationBegPlace = null;
	private String relationEndType = null;
	private String relationEndDatePrefix = null;
	private String relationEndDateFrom = null;
	private String relationEndDateTo = null;
	private String relationEndPlace = null;
	private String relationNoteText = null;
	private String relationSourceId = null;
	private String relationSourceText = null;
	private String relationPrivateText = null;
	private String relationModifiedDate = null;
	private String relationCreateDate = null;

	private String relationNoticeModifiedDate = null;
	private String relationNoticeCreateDate = null;
	private String relationNoticeRow = null;
	private String relationNoticeSurety = null;
	private String relationNoticeTag = null;

	/** The rnid. */
	int rnid = 0;
	private String relationLanguage = null;
	private String relationLanguageType = null;
	private String relationLanguageDescription = null;
	private String relationLanguagePlace = null;
	private String relationLanguageNoteText = null;
	private String relationLanguageModifiedDate = null;
	private String relationLanguageCreateDate = null;

	private String groupId = null;
	private String groupName = null;
	private String groupDescription = null;

	private String sourceId = null;
	private String sourceNoteText = null;

	private String conversionsFrom = null;
	private String conversionsTo = null;
	private String conversionsLang = null;
	private String conversionsRule = null;

	private String sukuName = null;
	private String sukuInfo = null;
	private String sukuAddress = null;
	private String sukuPostalCode = null;
	private String sukuPostOffice = null;
	private String sukuCountry = null;
	private String sukuEmail = null;
	private String sukuMediaFolder = null;

	private String viewId = null;
	private String viewCreateDate = null;
	private String viewName = null;
	private String viewUnitPid = null;

	private String typeRule = null;
	private String typeTag = null;

	private HashMap<String, String> nameCollector = null;
	private HashMap<String, String> placeCollector = null;

	private HashMap<String, String> conversionsChecker = null;
	/** The images. */
	LinkedHashMap<String, String> images = null;
	/** The is zip file. */
	boolean isZipFile = false;
	ZipInputStream zipIn = null;
	private Import2004Dialog runner = null;

	/**
	 * * <h1>Constructor to setup for thread</h1>
	 * 
	 * <p>
	 * The SAX importing is done as a separate thread
	 * </p>
	 * .
	 * 
	 * @param urli
	 *            address of the backup file
	 * @param con
	 *            connection instance to the PostgreSQL database
	 * @param oldCode
	 *            the language code used for the main language in Suku 2004
	 * @throws SukuException
	 *             the suku exception
	 */
	public Read2004XML(String urli, Connection con, String oldCode)
			throws SukuException {
		this.con = con;
		this.urli = urli;
		try {
			this.runner = Import2004Dialog.getRunner();

			DocumentBuilderFactory domfactory = DocumentBuilderFactory
					.newInstance();
			domfactory.setValidating(false);
			this.oldCode = oldCode;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Import of Suku 2004 backup", e);
			e.printStackTrace();
			throw new SukuException(e);
		}
		// run();
	}

	/**
	 * Gets the current status.
	 * 
	 * @return current state of process
	 */
	public synchronized String getCurrentStatus() {
		return this.currentStatus;
	}

	/**
	 * Method that does the import of file at urli.
	 * 
	 * @return the suku data
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData importFile() throws SukuException {

		SukuData resp = new SukuData();
		images = new LinkedHashMap<String, String>();
		SAXParser parser = null;

		SAXParserFactory dbfactory = SAXParserFactory.newInstance();

		logger.fine("Import from file: " + urli);

		String aux = this.urli.replace('\\', '/');
		int auxi = aux.lastIndexOf('/');
		this.databaseFolder = aux.substring(0, auxi);

		try {
			parser = dbfactory.newSAXParser();

		} catch (Exception e) {
			throw new SukuException(e);

		}

		try {

			long started = System.currentTimeMillis();
			GZIPInputStream gz = null;

			Statement stm = this.con.createStatement();

			stm.executeUpdate(DROP_GROUPS);
			stm.executeUpdate(CREATE_GROUPS);
			stm.executeUpdate(DROP_SOURCES);
			stm.executeUpdate(CREATE_SOURCES);

			stm.executeUpdate(ADD_UNIT_SID);
			stm.executeUpdate(ADD_UNITNOTICE_SID);
			stm.executeUpdate(ADD_RELATIONNOTICE_SID);

			if (this.urli.endsWith(".zip")) {
				String gedFile = null;
				isZipFile = true;

				ZipEntry zipEntry = null;
				File xmlFile = null;
				zipIn = new ZipInputStream(Suku.kontroller.getInputStream());
				BufferedInputStream bis = new BufferedInputStream(zipIn);
				while ((zipEntry = zipIn.getNextEntry()) != null) {
					String entryName = zipEntry.getName();
					if (entryName.toLowerCase().endsWith(".xml")) {
						int li = entryName.replace('\\', '/').lastIndexOf('/');
						if (li > 0) {
							baseFolder = entryName.substring(0, li + 1);
						}
						xmlFile = copyToTempfile(zipIn, entryName);
					} else if (entryName.toLowerCase().endsWith(".ged")) {
						gedFile = entryName;
					} else {
						copyToTempfile(zipIn, entryName);
					}
				}
				bis.close();
				if (this.runner.setRunnerValue(Resurses
						.getString("GETSUKU_INPUT_FINISHED"))) {
					throw new SukuException(
							Resurses.getString("GETSUKU_CANCELLED"));
				}
				if (xmlFile != null) {
					parser.parse(xmlFile, this);
				} else {
					errorLine.add(Resurses.getString("GETSUKU_BACKUP_MISSING"));
					if (gedFile != null) {
						errorLine.add(Resurses.getString("GETSUKU_TRY_GEDCOM")
								+ " " + gedFile);
					}

				}

			} else if (this.urli.endsWith(".gz")) {
				gz = new GZIPInputStream(new FileInputStream(this.urli));
				parser.parse(gz, this);
				gz.close();
			} else {
				parser.parse(this.urli, this);
			}

			stm.executeUpdate(UPDATE_WIFE_REL);
			stm.executeUpdate(UPDATE_HUSB_REL);
			stm.executeUpdate(UPDATE_MOTH_REL);

			stm.executeUpdate(UPDATE_UNIT_SOURCES);
			stm.executeUpdate(UPDATE_NOTICE_SOURCES);
			stm.executeUpdate(UPDATE_RELATION_SOURCES);
			if (finFamilyVersion == null) {
				stm.executeUpdate(UPDATE_GROUPS);
			}
			stm.executeUpdate(DROP_GROUPS);
			// initialize pid sequence
			String sql = "select max(pid) from unit";
			ResultSet rs = stm.executeQuery(sql);
			int maxpid = 0;
			if (rs.next()) {
				maxpid = rs.getInt(1);
			}
			rs.close();
			if (maxpid > 0) {
				sql = "SELECT setval('unitseq'," + maxpid + ")";
				rs = stm.executeQuery(sql);
				rs.close();
			}
			// initializize also vid sequence
			sql = "select max(vid) from views";
			rs = stm.executeQuery(sql);
			int maxvid = 0;
			if (rs.next()) {
				maxvid = rs.getInt(1);
			}
			rs.close();
			if (maxvid > 0) {
				sql = "SELECT setval('viewseq'," + maxvid + ")";
				rs = stm.executeQuery(sql);
				rs.close();
			}
			sql = "select u.pid from unit as u inner join relation as r on u.pid = r.pid "
					+ "where r.rid in (select rid from relation group by rid having count(*) <> 2)";
			rs = stm.executeQuery(sql);
			boolean foundOrphan = false;
			while (rs.next()) {
				foundOrphan = true;
				errorLine.add(Resurses.getString("SUKU2004_FAILED_PID") + "["
						+ rs.getInt(1) + "]");
			}
			rs.close();
			if (foundOrphan) {
				sql = "delete from relation where rid in "
						+ "(select rid from relation group by rid having count(*) <> 2)";
				int deletedRels = stm.executeUpdate(sql);

				errorLine.add(Resurses.getString("SUKU2004_DELETED_RID") + " ["
						+ deletedRels + "]");
			}

			stm.executeUpdate(DROP_UNIT_SID);
			stm.executeUpdate(DROP_UNITNOTICE_SID);
			stm.executeUpdate(DROP_RELATIONNOTICE_SID);

			stm.executeUpdate(VACUUM);
			long ended = System.currentTimeMillis();
			logger.info("Backup " + this.urli + " converted in "
					+ (ended - started) + " ms");
			logger.info("Restore suku10 had units[" + laskuriUnits
					+ "]; relations[" + laskuriRelations + "]; groups["
					+ laskuriGroups + "]; views[" + laskuriViews
					+ "]; conversions[" + laskuriConversion + "]");
			stm.close();

			if (gz != null) {
				gz.close();
			}
			resp.generalArray = errorLine.toArray(new String[0]);
			return resp;
		} catch (Throwable e) {
			errorLine.add(e.getMessage());
			resp.generalArray = errorLine.toArray(new String[0]);
			throw new SukuException(e);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		this.currentEle += "|" + qName;

		this.currentChars = new StringBuffer();
		this.qName = qName;

		if (this.currentEle.equals(genealogTG)) {
			finFamilyVersion = attributes.getValue("finfamily");
		}

		if (this.currentEle.equals(unitTG)) {

			this.unitSex = attributes.getValue("sex");
			this.unitId = attributes.getValue("unitid");
			try {
				unitPid = Integer.parseInt(this.unitId.substring(1));
			} catch (NumberFormatException ne) {
				throw new SAXException("UnitId " + this.unitId
						+ " is not numeric");
			}
			this.unitTag = attributes.getValue("tag");
			this.unitPrivacy = attributes.getValue("privacy");
			if (finFamilyVersion == null) {
				this.unitGroupId = attributes.getValue("groupid");
			} else {
				this.unitGroup = attributes.getValue("group");
			}
			this.unitCreateDate = attributes.getValue("created");
			if (this.unitCreateDate == null) {
				this.unitCreateDate = attributes.getValue("createdate");
			}

		}
		if (this.currentEle.equals(unitSourceTG)) {
			this.unitSourceId = attributes.getValue("sourceid");
		} else if (this.currentEle.equals(unitRefnTG)) {
			this.unitRefn = attributes.getValue("refn");
		} else if (this.currentEle.equals(noticeTG)) {

			this.noticeRow = attributes.getValue("row");
			String ntag = attributes.getValue("tag");
			if (finFamilyVersion != null && ntag.equals("INDI")) {
				ntag = "NAME";
			}
			this.noticeTag = ntag;
			this.noticePrivacy = attributes.getValue("privacy");
			this.noticeSurety = attributes.getValue("surety");
			this.noticeSourceId = attributes.getValue("sourceid");
			this.noticeModifiedDate = attributes.getValue("modified");
			this.noticeCreateDate = attributes.getValue("created");
			if (this.noticeCreateDate == null) {
				this.noticeCreateDate = attributes.getValue("createdate");
			}
			initUnitNotice();
		} else if (this.currentEle.equals(noticeSourceTG)) {
			this.noticeSourceId = attributes.getValue("sourceid");
			logger.fine("UnitNotice: " + this.unitId + "/"
					+ this.noticeSourceId);
		} else if (this.currentEle.equals(noticeDateTG)) {
			this.noticeDatePrefix = attributes.getValue("type");
		} else if (this.currentEle.equals(noticeLanguageTG)) {
			this.noticeLanguage = attributes.getValue("langcode");
			this.noticeLanguageModifiedDate = attributes.getValue("modified");
			this.noticeLanguageCreateDate = attributes.getValue("created");
		} else if (this.currentEle.equals(noticeNamelistTG)) {
			namelist = new Vector<String>();
		} else if (this.currentEle.equals(noticePlacelistTG)) {
			placelist = new Vector<String>();
		} else if (this.currentEle.equals(relationTG)) {
			this.relationIdA = attributes.getValue("unitida");
			this.relationIdB = attributes.getValue("unitidb");
			this.relationTag = attributes.getValue("tag");
			this.relationTaga = attributes.getValue("taga");
			this.relationTagb = attributes.getValue("tagb");
			this.relationRowA = attributes.getValue("rowa");
			this.relationRowB = attributes.getValue("rowb");
			this.relationSurety = attributes.getValue("surety");
			this.relationSourceId = attributes.getValue("sourceid");
			this.relationModifiedDate = attributes.getValue("modified");
			this.relationCreateDate = attributes.getValue("created");
			if (this.relationCreateDate == null) {
				this.relationCreateDate = attributes.getValue("createdate");
			}

			ResultSet rs;
			PreparedStatement pstm;
			try {
				pstm = this.con
						.prepareStatement("select nextval('relationseq')");

				rs = pstm.executeQuery();
				if (rs.next()) {
					rid = rs.getInt(1);
				} else {
					throw new SAXException("Sequence relationseq error");
				}
				rs.close();
			} catch (SQLException e) {

				e.printStackTrace();
				throw new SAXException("Sequence relationseq sql error");
			}

		} else if (this.currentEle.equals(relationNoticeTG)) {
			this.relationNoticeRow = attributes.getValue("row");
			this.relationNoticeSurety = attributes.getValue("surety");
			this.relationNoticeTag = attributes.getValue("tag");
			this.relationNoticeModifiedDate = attributes.getValue("modified");
			this.relationNoticeCreateDate = attributes.getValue("created");

			PreparedStatement pst;
			try {
				pst = this.con
						.prepareStatement("select nextval('relationnoticeseq')");
				rnid = 0;
				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					rnid = rs.getInt(1);
				} else {
					throw new SAXException("Sequence relationnoticeseq error");
				}
				rs.close();
			} catch (SQLException e) {

				e.printStackTrace();
				throw new SAXException("Sequence relationoticeseq sql error");
			}

		} else if (this.currentEle.equals(relationNoticeDateTG)) {
			this.relationBegDatePrefix = attributes.getValue("type");
		} else if (this.currentEle.equals(relationSourceTG)) {
			this.relationSourceId = attributes.getValue("sourceid");
			logger.fine("Relation: " + this.relationIdA + "/"
					+ this.relationSourceId);
		} else if (this.currentEle.equals(relationBegDateTG)) {
			this.relationBegDatePrefix = attributes.getValue("type");
		} else if (this.currentEle.equals(relationEndDateTG)) {
			this.relationEndDatePrefix = attributes.getValue("type");
		} else if (this.currentEle.equals(relationLanguageTG)) {
			this.relationLanguage = attributes.getValue("langcode");
			this.relationLanguageModifiedDate = attributes.getValue("modified");
			this.relationLanguageCreateDate = attributes.getValue("created");
		} else if (this.currentEle.equals(sourceTG)) {
			this.sourceId = attributes.getValue("sourceid");

			this.sourceNoteText = null;
		} else if (this.currentEle.equals(groupTG)) {
			this.groupId = attributes.getValue("groupid");
		} else if (this.currentEle.equals(conversionsTG)) {
			this.conversionsChecker = new HashMap<String, String>();
		} else if (this.currentEle.equals(conversionTG)) {
			this.conversionsRule = attributes.getValue("rule");
		} else if (this.currentEle.equals(conversionsToTG)) {
			this.conversionsLang = attributes.getValue("language");
		} else if (this.currentEle.equals(viewTG)) {
			this.viewId = attributes.getValue("viewid");
			this.viewCreateDate = attributes.getValue("created");
			if (this.viewCreateDate == null) {
				this.viewCreateDate = attributes.getValue("createdate");
			}

		} else if (this.currentEle.equals(viewUnitTG)) {
			this.viewUnitPid = attributes.getValue("unitid");
		} else if (this.currentEle.equals(typesTG)) {
			String sql = "delete from types";
			try {
				Statement stm = con.createStatement();
				stm.executeUpdate(sql);
				logger.log(Level.FINE, "deleted default types");
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "deleting default types failed", e);
				throw new SAXException(e);
			}

		} else if (this.currentEle.equals(typeTG)) {
			this.typeRule = attributes.getValue("rule");
			this.typeTag = attributes.getValue("tag");
		} else if (this.currentEle.equals(typeNameTG)) {

			String langcode = attributes.getValue("langcode");
			String name = attributes.getValue("name");
			String reportname = attributes.getValue("reportname");
			String sql = "insert into types (tagtype,tag,rule,langcode,name,reportname) "
					+ "values ('Notices',?,?,?,?,?) ";
			try {
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setString(1, this.typeTag);
				pst.setString(2, this.typeRule);
				pst.setString(3, langcode);
				pst.setString(4, name);
				pst.setString(5, reportname);

				pst.executeUpdate();
				logger.log(Level.FINEST, "Added type '" + this.typeTag + "' ["
						+ langcode + "]");
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "deleting default types failed", e);
				throw new SAXException(e);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (this.currentChars != null) {

			this.currentChars.append(ch, start, length);
			// this.fileLength += length;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		PreparedStatement pst;
		String aux;
		if (this.qName != null) {
			this.qName = null;
		}
		// if (this.currentEle.equals(genealogTG)) {
		// try {
		// zipIn.closeEntry();
		// } catch (IOException e) {
		// logger.log(Level.WARNING, "", e);
		// throw new SAXException("ZIP_INPUTSTREAM_CLOSE_FAILED");
		//
		// }
		// throw new SAXException("ZIP");
		// }

		if (this.currentEle.equals(unitSourceTG)) {
			String saux = this.currentChars.toString();
			if (saux.length() > 0) {
				this.unitSourceText = saux;
			}

		}
		if (this.currentEle.equals(unitPrivateTextTG)) {
			String saux = this.currentChars.toString();
			if (saux.length() > 0) {
				this.unitPrivateText = saux;
			}

		}
		if (this.currentEle.equals(unitGivenNameTG)) {
			this.unitGivenName = this.currentChars.toString();
		}
		if (this.currentEle.equals(unitSurnameTG)) {
			this.unitSurName = this.currentChars.toString();
		}
		if (this.currentEle.equals(unitPrefixTG)) {
			this.unitPrefix = this.currentChars.toString();
		}
		if (this.currentEle.equals(unitPostfixTG)) {
			this.unitPostfix = this.currentChars.toString();
		}

		if (this.currentEle.equals(unitNameTG)) {
			insertUnitName();
		}

		if (this.currentEle.equals(unitTG)) {
			updateUnit();

		}

		if (this.currentEle.equals(noticeTypeTG)) {
			this.noticeType = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeDescriptTG)) {
			this.noticeDescription = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeNamelistNameTG)) {
			String tmp = this.currentChars.toString();
			if (!tmp.isEmpty()) {
				namelist.add(tmp);
			}
		}
		if (this.currentEle.equals(noticePlacelistPlaceTG)) {
			String tmp = this.currentChars.toString();
			if (!tmp.isEmpty()) {
				placelist.add(tmp);
			}
		}
		if (this.currentEle.equals(noticeDateStartTG)) {
			this.noticeDateFrom = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeDateEndTG)) {
			this.noticeDateTo = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticePlaceTG)) {
			this.noticePlace = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeVillageTG)) {
			this.noticeVillage = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeFarmTG)) {
			this.noticeFarm = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeCroftTG)) {
			this.noticeCroft = this.currentChars.toString();
		}

		if (finFamilyVersion == null) {
			if (this.currentEle.equals(noticeGivenNameTG)) {
				this.noticeGivenName = this.currentChars.toString();
			}
		} else {
			if (this.currentEle.equals(noticeFirstNameTG)) {
				this.noticeGivenName = this.currentChars.toString();
			}
		}
		if (this.currentEle.equals(noticePatronymTG)) {
			this.noticePatronym = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticePrefixTG)) {
			this.noticePrefix = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeSurnameTG)) {
			this.noticeSurname = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticePostfixTG)) {
			this.noticePostfix = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeNoteTextTG)) {
			aux = this.currentChars.toString();
			this.noticeNoteText = aux;

		}
		if (this.currentEle.equals(noticeSourceTG)) {
			if (this.currentChars.length() > 0) {
				this.noticeSourceText = this.currentChars.toString();
			}
		}

		if (this.currentEle.equals(noticePrivateTextTG)) {
			this.noticePrivateText = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeMediaTitleTG)) {
			this.noticeMediaTitle = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeMediaFilenameTG)) {
			this.noticeMediaFilename = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeAddressTG)) {
			this.noticeAddress = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticePostalCodeTG)) {
			this.noticePostalCode = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticePostOfficeTG)) {
			this.noticePostOffice = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeStateTG)) {
			this.noticeState = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeCountryTG)) {
			this.noticeCountry = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeEmailTG)) {
			this.noticeEmail = this.currentChars.toString();
		}

		if (this.currentEle.equals(noticeTG)) { // end of notice
			updateUnitNotice();
		}
		if (this.currentEle.equals(noticeLanguageTypeTG)) {
			this.noticeLanguageType = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeLanguageDescriptionTG)) {
			this.noticeLanguageDescription = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeLanguagePlaceTG)) {
			this.noticeLanguagePlace = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeLanguageMediaTitleTG)) {
			this.noticeLanguageMediaTitleText = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeLanguageNoteTextTG)) {
			this.noticeLanguageNoteText = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeLanguageTG)) {
			insertUnitLanguage();
		}

		if (this.currentEle.equals(relationDescriptionTG)) {
			this.relationDescription = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationBegTypeTG)) {
			this.relationBegType = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationBegDateStartTG)) {
			this.relationBegDateFrom = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationBegDateEndTG)) {
			this.relationBegDateTo = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationBegPlaceTG)) {
			this.relationBegPlace = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationEndTypeTG)) {
			this.relationEndType = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationEndDateStartTG)) {
			this.relationEndDateFrom = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationEndDateEndTG)) {
			this.relationEndDateTo = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationEndPlaceTG)) {
			this.relationEndPlace = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationNoteTextTG)) {
			this.relationNoteText = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationSourceTG)) {
			if (this.currentChars.length() > 0) {
				this.relationSourceText = this.currentChars.toString();
			}
		}
		if (this.currentEle.equals(relationPrivateTextTG)) {
			this.relationPrivateText = this.currentChars.toString();
		}

		if (this.currentEle.equals(relationTG)) {

			insertRelation();

		}
		if (this.currentEle.equals(relationNoticeTypeTG)) {
			this.relationBegType = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationNoticeDescriptionTG)) {
			this.relationDescription = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationNoticeDateStartTG)) {
			this.relationBegDateFrom = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationNoticeDateEndTG)) {
			this.relationBegDateTo = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationNoticePlaceTG)) {
			this.relationBegPlace = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationNoticeNoteTextTG)) {
			this.relationNoteText = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationNoticeSourceTG)) {
			this.relationSourceText = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationNoticePrivateTextTG)) {
			this.relationPrivateText = this.currentChars.toString();
		}

		if (this.currentEle.equals(relationNoticeTG)) {

			insertRelationNotice();

		}
		if (this.currentEle.equals(relationLanguageTypeTG)) {
			this.relationLanguageType = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationLanguageDescriptionTG)) {
			this.relationLanguageDescription = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationLanguagePlaceTG)) {
			this.relationLanguagePlace = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationLanguageNoteTextTG)) {
			this.relationLanguageNoteText = this.currentChars.toString();
		}
		if (this.currentEle.equals(relationLanguageTG)) {

			insertRelationLanguage();

		}

		if (this.currentEle.equals(sourceTG)) {
			this.sourceNoteText = this.currentChars.toString();

			if (this.sourceNoteText != null && this.sourceNoteText.length() > 0) {
				try {
					int sid;
					try {
						sid = Integer.parseInt(this.sourceId.substring(1));
					} catch (NumberFormatException ne) {
						throw new SAXException("SourceId " + this.sourceId
								+ " not numeric");
					}

					pst = this.con.prepareStatement(INSERT_SOURCES);
					pst.setInt(1, sid);
					pst.setString(2, this.sourceNoteText);

					pst.executeUpdate();
					// if (this.runner != null) {
					//
					// if (this.runner.setRunnerValue("SourceId: " + sid)) {
					// throw new SAXException(Resurses
					// .getString("SUKU_CANCELLED"));
					// }
					// }

				} catch (SQLException e) {
					logger.log(Level.SEVERE, "importing sources failed", e);
					throw new SAXException(e);
				}

			}

		}

		if (this.currentEle.equals(groupNameTG)) {
			this.groupName = this.currentChars.toString();
		}
		if (this.currentEle.equals(groupDescriptionTG)) {
			this.groupDescription = this.currentChars.toString();
		}
		if (this.currentEle.equals(groupTG)) {

			try {

				pst = this.con.prepareStatement(INSERT_GROUP);
				pst.setString(1, this.groupId);
				pst.setString(2, this.groupName);
				pst.setString(3, this.groupDescription);
				pst.executeUpdate();
				// if (this.runner != null) {
				// if (this.runner.setRunnerValue("GroupId: " + this.groupId)) {
				// throw new SAXException(Resurses
				// .getString("SUKU_CANCELLED"));
				// }
				// }
				laskuriGroups++;
			} catch (SQLException e) {
				logger.log(Level.WARNING, "Problem with inserting groups ", e);
				e.printStackTrace();
			}

		}
		if (this.currentEle.equals(conversionsFromTG)) {
			this.conversionsFrom = this.currentChars.toString();
		}
		if (this.currentEle.equals(conversionsToTG)) {
			this.conversionsTo = this.currentChars.toString();
		}
		if (this.currentEle.equals(sukuNameTG)) {
			this.sukuName = this.currentChars.toString();
		}
		if (this.currentEle.equals(sukuAddressTG)) {
			this.sukuAddress = this.currentChars.toString();
		}
		if (this.currentEle.equals(sukuPostalCodeTG)) {
			this.sukuPostalCode = this.currentChars.toString();
		}
		if (this.currentEle.equals(sukuPostOfficeTG)) {
			this.sukuPostOffice = this.currentChars.toString();
		}
		if (this.currentEle.equals(sukuCountryTG)) {
			this.sukuCountry = this.currentChars.toString();
		}
		if (this.currentEle.equals(sukuEmailTG)) {
			this.sukuEmail = this.currentChars.toString();
		}
		if (this.currentEle.equals(sukuInfoTG)) {
			this.sukuInfo = this.currentChars.toString();
		}
		if (this.currentEle.equals(sukuMediaPathTG)) {
			this.sukuMediaFolder = this.currentChars.toString();

			File f = new File(this.databaseFolder + "/" + this.sukuMediaFolder);
			this.databaseHasImages = f.isDirectory();
			if (!f.isDirectory()) {
				logger.warning("Image folder " + f.getAbsolutePath()
						+ " is not valid");
			}

		}
		if (this.currentEle.equals(sukuTG)) {
			try {

				pst = this.con.prepareStatement(INSERT_SUKU_VARIABLES);
				pst.setString(1, this.sukuName);
				pst.setString(2, this.sukuInfo);
				pst.setString(3, this.sukuAddress);
				pst.setString(4, this.sukuPostalCode);
				pst.setString(5, this.sukuPostOffice);
				pst.setString(6, this.sukuCountry);
				pst.setString(7, this.sukuEmail);
				pst.executeUpdate();

			} catch (SQLException e) {
				logger.log(Level.SEVERE, "importing sukuvariables failed", e);
				throw new SAXException(e);
			}

		}
		if (this.currentEle.equals(conversionsFromTG)) {
			this.conversionsFrom = this.currentChars.toString();
		}
		if (this.currentEle.equals(conversionsToTG)) {
			this.conversionsTo = this.currentChars.toString();
		}

		if (this.currentEle.equals(conversionTG)) {
			if (this.conversionsFrom != null && this.conversionsLang != null
					&& this.conversionsRule != null
					&& this.conversionsTo != null) {
				String newLang = this.conversionsLang.toLowerCase();
				if (newLang.equals("se")) {
					newLang = "sv";
				}
				String tmp = this.conversionsFrom + "/" + newLang
						+ this.conversionsRule;
				String xx = this.conversionsChecker.put(tmp.toLowerCase(),
						this.conversionsTo);

				if (xx == null) {
					// add to db only if not there yet
					// this.runner.setRunnerValue("conv [" + newLang + "]"
					// + this.conversionsFrom);
					try {
						pst = this.con.prepareStatement(INSERT_CONVERSION);
						pst.setString(1, this.conversionsFrom.toLowerCase());
						pst.setString(2, newLang);
						pst.setString(3, this.conversionsRule);
						pst.setString(4, this.conversionsTo);
						pst.executeUpdate();
						laskuriConversion++;
						if (this.runner != null) {
							if (this.runner.setRunnerValue("ConversionId: "
									+ laskuriConversion)) {
								throw new SAXException(
										Resurses.getString("SUKU_CANCELLED"));
							}
						}
					} catch (SQLException e) {
						logger.log(Level.SEVERE, "importing conversion failed",
								e);
						throw new SAXException(e);

					}
				}
				this.conversionsFrom = null;
				this.conversionsLang = null;
				this.conversionsRule = null;
				this.conversionsTo = null;
			}
		}
		if (this.currentEle.equals(viewNameTG)) {
			this.viewName = this.currentChars.toString();
		}

		int vid;

		if (this.currentEle.equals(viewTG)) {
			if (this.viewId != null && this.viewId.length() > 1) {
				try {

					try {
						vid = Integer.parseInt(this.viewId.substring(1));
					} catch (NumberFormatException ne) {
						throw new SAXException("ViewId " + this.viewId
								+ " or unitid " + this.viewUnitPid
								+ " is not numeric");
					}

					pst = this.con.prepareStatement(INSERT_VIEW);

					pst.setInt(1, vid);
					pst.setString(2, this.viewName);
					pst.setTimestamp(3, toTimestamp(this.viewCreateDate, true));
					pst.executeUpdate();
					laskuriViews++;
					this.runner.setRunnerValue("view [" + vid + "]"
							+ this.viewName);
					if (this.runner != null) {
						if (this.runner.setRunnerValue("ViewId: " + vid)) {
							throw new SAXException(
									Resurses.getString("SUKU_CANCELLED"));
						}
					}

				} catch (SQLException e) {
					logger.log(Level.SEVERE, "importing views failed", e);
					throw new SAXException(e);

				}
			}

			this.viewCreateDate = null;
			this.viewId = null;
			this.viewName = null;
			this.viewUnitPid = null;
		}

		if (this.currentEle.equals(viewUnitTG)) {
			if (this.viewId != null && this.viewId.length() > 1
					&& this.viewUnitPid != null
					&& this.viewUnitPid.length() > 1) {
				try {
					int vpid;
					try {
						vid = Integer.parseInt(this.viewId.substring(1));
						vpid = Integer.parseInt(this.viewUnitPid.substring(1));
					} catch (NumberFormatException ne) {
						throw new SAXException("ViewUnitId " + this.viewUnitPid
								+ " is not numeric");
					}

					pst = this.con.prepareStatement(INSERT_VIEW_UNIT);

					pst.setInt(1, vid);
					pst.setInt(2, vpid);

					pst.executeUpdate();

				} catch (SQLException e) {
					logger.log(Level.SEVERE, "importing viewunits failed", e);
					throw new SAXException(e);
				}
			}

			this.viewUnitPid = null;
		} else if (this.currentEle.equals(typeTG)) {
			this.typeRule = null;
			this.typeTag = null;
		}
		int k;
		if (this.currentEle.endsWith(qName)) {
			k = this.currentEle.length();
			this.currentEle = this.currentEle.substring(0, k - qName.length()
					- 1);
		} else {
			System.out.println("BAD XML: " + this.currentEle + "/" + qName);
			return;
		}

	}

	private void insertUnitLanguage() throws SAXException {
		try {

			PreparedStatement pst;
			pst = this.con.prepareStatement(INSERT_UNIT_LANGUAGE);

			pst.setInt(1, unitPid);
			pst.setInt(2, pnid);
			pst.setString(3, this.noticeTag);
			pst.setString(4, this.noticeLanguage);
			pst.setString(5, this.noticeLanguageType);
			pst.setString(6, this.noticeLanguageDescription);
			pst.setString(7, this.noticeLanguagePlace);
			pst.setString(8, this.noticeLanguageNoteText);
			pst.setString(9, this.noticeLanguageMediaTitleText);

			if (noticeLanguageModifiedDate == null) {
				pst.setNull(10, Types.TIMESTAMP);
			} else {
				pst.setTimestamp(10,
						toTimestamp(this.noticeLanguageModifiedDate, false));
			}
			pst.setTimestamp(11,
					toTimestamp(this.noticeLanguageCreateDate, true));
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "importing notice language notice failed",
					e);
			throw new SAXException(e);
		}
		noticeLanguage = null;
		noticeLanguageType = null;
		noticeLanguageDescription = null;
		noticeLanguagePlace = null;
		noticeLanguageNoteText = null;
		noticeLanguageMediaTitleText = null;
		noticeLanguageModifiedDate = null;
		noticeLanguageCreateDate = null;

	}

	private void insertRelationLanguage() throws SAXException {
		try {
			PreparedStatement pst;

			pst = this.con.prepareStatement(INSERT_RELATION_LANGUAGE);

			pst.setInt(1, rnid);
			pst.setInt(2, rid);
			pst.setString(3, this.relationLanguage);

			pst.setString(4, this.relationLanguageType);
			pst.setString(5, this.relationLanguageDescription);
			pst.setString(6, this.relationLanguagePlace);
			pst.setString(7, this.relationLanguageNoteText);
			if (relationLanguageModifiedDate == null) {
				pst.setNull(8, Types.TIMESTAMP);
			} else {
				pst.setTimestamp(8,
						toTimestamp(this.relationLanguageModifiedDate, false));
			}
			pst.setTimestamp(9,
					toTimestamp(this.relationLanguageCreateDate, true));
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			logger.log(Level.SEVERE,
					"importing relation language notice failed", e);
			throw new SAXException(e);
		}
		relationLanguage = null;
		relationLanguageType = null;
		relationLanguageDescription = null;
		relationLanguagePlace = null;
		relationLanguageNoteText = null;
		relationLanguageModifiedDate = null;
		relationLanguageCreateDate = null;

	}

	private void insertRelationNotice() throws SAXException {

		try {
			PreparedStatement pst;

			pst = this.con.prepareStatement(INSERT_RELATION_NOTICE);

			pst.setInt(1, rnid);
			pst.setInt(2, rid);
			pst.setString(3, this.relationNoticeTag);
			int row = 0;
			try {
				row = Integer.parseInt(this.relationNoticeRow);
			} catch (NumberFormatException ne) {
				// NumberFormatException ignored
			}
			// this.pstm.setTimestamp(16,
			// toTimestamp(this.relationCreateDate));
			pst.setInt(4, row);
			int surety = 100;
			try {
				surety = Integer.parseInt(this.relationNoticeSurety);
			} catch (NumberFormatException ne) {
				// NumberFormatException ignored
			}
			pst.setInt(5, surety);
			pst.setString(6, this.relationDescription);
			pst.setString(7, this.relationBegType);
			pst.setString(8, this.relationBegDatePrefix);
			pst.setString(9, this.relationBegDateFrom);
			pst.setString(10, this.relationBegDateTo);
			pst.setString(11, this.relationBegPlace);
			pst.setString(12, this.relationNoteText);
			pst.setInt(13, 0);
			pst.setString(14, this.relationSourceText);
			pst.setString(15, this.relationPrivateText);
			if (relationNoticeModifiedDate == null) {
				pst.setNull(16, Types.TIMESTAMP);
			} else {
				pst.setTimestamp(16,
						toTimestamp(this.relationNoticeModifiedDate, false));
			}

			pst.setTimestamp(17,
					toTimestamp(this.relationNoticeCreateDate, true));
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "importing relation notice failed", e);
			throw new SAXException(e);
		}
		relationNoticeTag = null;
		relationNoticeRow = null;
		relationDescription = null;
		relationBegType = null;
		relationBegDatePrefix = null;
		relationBegDateFrom = null;
		relationBegDateTo = null;
		relationBegPlace = null;
		relationNoteText = null;
		relationSourceText = null;
		relationPrivateText = null;
		relationNoticeModifiedDate = null;
		relationNoticeCreateDate = null;

	}

	private void insertRelation() throws SAXException {
		int i;
		int aid = 0;
		int bid = 0;
		int surety = 0;
		ResultSet rs;
		PreparedStatement pst;
		try {

			int relRow = 0;
			if (finFamilyVersion != null) {
				// this is the restore from FinFamily
				pst = this.con.prepareStatement(INSERT_RELATION);

				pst.setInt(1, rid);
				if (this.relationIdA != null) {
					try {
						aid = Integer.parseInt(this.relationIdA.substring(1));

						pst.setInt(2, aid);
					} catch (NumberFormatException ne) {
						throw new SAXException("RelationIdA "
								+ this.relationIdA + " not numeric");
					}
				}
				if (this.relationSurety != null) {
					try {
						surety = Integer.parseInt(this.relationSurety);
					} catch (NumberFormatException ne) {
						surety = 80;
					}
				} else {
					surety = 100;
				}

				pst.setString(3, this.relationTaga);
				relRow = 0;
				if (this.relationRowA != null) {
					try {
						relRow = Integer.parseInt(this.relationRowA);
						pst.setInt(4, relRow);
					} catch (NumberFormatException ne) {
						throw new SAXException("RelationRowA "
								+ this.relationRowA + " not numeric");
					}
				}
				pst.setInt(5, surety);
				if (this.relationModifiedDate == null) {
					pst.setNull(6, Types.TIMESTAMP);
				} else {
					pst.setTimestamp(6,
							toTimestamp(this.relationModifiedDate, false));
				}
				pst.setTimestamp(7, toTimestamp(this.relationCreateDate, true));
				try {
					pst.executeUpdate();
				} catch (SQLException se) {
					String err = "Relative aPID = " + aid + " fails for RID = "
							+ rid;
					errorLine.add(err + " [" + se.getMessage() + "]");

					logger.log(Level.WARNING, err, se);

				}
				// here the other relation
				pst.setInt(1, rid);
				if (this.relationIdB != null) {
					try {
						bid = Integer.parseInt(this.relationIdB.substring(1));

						pst.setInt(2, bid);
					} catch (NumberFormatException ne) {
						throw new SAXException("RelationIdB "
								+ this.relationIdB + " not numeric");
					}
				}
				pst.setString(3, this.relationTagb);
				if (this.relationRowB != null) {
					try {
						relRow = Integer.parseInt(this.relationRowB);
						pst.setInt(4, relRow);
					} catch (NumberFormatException ne) {
						throw new SAXException("RelationRowB "
								+ this.relationRowB + " not numeric");
					}
				}
				pst.setInt(5, surety);
				pst.setNull(6, Types.TIMESTAMP);
				pst.setTimestamp(7, toTimestamp(this.relationCreateDate, true));

				try {
					pst.executeUpdate();
				} catch (SQLException se) {
					String err = "Relative bPID = " + bid + " fails for RID = "
							+ rid;
					errorLine.add(err + " [" + se.getMessage() + "]");

					logger.log(Level.WARNING, err, se);
				}

			} else {
				// this is the import from Suku 2004
				if (this.relationTag.equals("CHIL")) {

					this.currentStatus = "Chil " + rid;

					pst = this.con.prepareStatement(INSERT_RELATION);

					pst.setInt(1, rid);
					if (this.relationIdA != null) {
						try {
							aid = Integer.parseInt(this.relationIdA
									.substring(1));

							pst.setInt(2, aid);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationIdA "
									+ this.relationIdA + " not numeric");
						}
					}
					if (this.relationSurety != null) {
						try {
							surety = Integer.parseInt(this.relationSurety);
						} catch (NumberFormatException ne) {
							surety = 80;
						}
					} else {
						surety = 100;
					}

					pst.setString(3, "FATH");
					relRow = 0;
					if (this.relationRowA != null) {
						try {
							relRow = Integer.parseInt(this.relationRowA);
							pst.setInt(4, relRow);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationRowA "
									+ this.relationRowA + " not numeric");
						}
					}
					pst.setInt(5, surety);
					pst.setNull(6, Types.TIMESTAMP);
					pst.setTimestamp(7,
							toTimestamp(this.relationCreateDate, true));
					try {
						pst.executeUpdate();
					} catch (SQLException se) {
						String err = "Relative aPID = " + aid
								+ " fails for RID = " + rid;
						errorLine.add(err + " [" + se.getMessage() + "]");

						logger.log(Level.WARNING, err, se);

					}

					pst.setInt(1, rid);
					if (this.relationIdB != null) {
						try {
							bid = Integer.parseInt(this.relationIdB
									.substring(1));

							pst.setInt(2, bid);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationIdB "
									+ this.relationIdB + " not numeric");
						}
					}
					pst.setString(3, "CHIL");
					if (this.relationRowB != null) {
						try {
							relRow = Integer.parseInt(this.relationRowB);
							pst.setInt(4, relRow);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationRowB "
									+ this.relationRowB + " not numeric");
						}
					}
					pst.setInt(5, surety);
					pst.setNull(6, Types.TIMESTAMP);
					pst.setTimestamp(7,
							toTimestamp(this.relationCreateDate, true));

					try {
						pst.executeUpdate();
					} catch (SQLException se) {
						String err = "Relative bPID = " + bid
								+ " fails for RID = " + rid;
						errorLine.add(err + " [" + se.getMessage() + "]");

						logger.log(Level.WARNING, err, se);
					}

					if (this.relationDescription != null) {

						pst = this.con
								.prepareStatement("select nextval('relationnoticeseq')");
						int rnid = 0;
						rs = pst.executeQuery();
						if (rs.next()) {
							rnid = rs.getInt(1);
						} else {
							throw new SAXException(
									"Sequence relationnoticeseq error");
						}
						rs.close();

						pst = this.con
								.prepareStatement(INSERT_RELATION_ADOPTION);
						pst.setInt(1, rnid);
						pst.setInt(2, rid);
						pst.setString(3, "ADOP");
						pst.setInt(4, 1); // rownumber
						pst.setTimestamp(5,
								toTimestamp(this.relationCreateDate, true));
						pst.executeUpdate();
					}

				} else {
					this.currentStatus = "Marr " + rid;
					pst = this.con.prepareStatement(INSERT_RELATION);

					pst.setInt(1, rid);
					if (this.relationIdA != null) {
						try {
							aid = Integer.parseInt(this.relationIdA
									.substring(1));
							pst.setInt(2, aid);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationIdA "
									+ this.relationIdA + " not numeric");
						}
					}
					pst.setString(3, "MARR");
					if (this.relationRowA != null) {
						try {
							relRow = Integer.parseInt(this.relationRowA);
							pst.setInt(4, relRow);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationRowA "
									+ this.relationRowA + " not numeric");
						}
					}

					if (this.relationSurety != null) {
						try {
							surety = Integer.parseInt(this.relationSurety);
						} catch (NumberFormatException ne) {
							surety = 80;
						}
					} else {
						surety = 100;
					}

					pst.setInt(5, surety);
					pst.setNull(6, Types.TIMESTAMP);
					pst.setTimestamp(7,
							toTimestamp(this.relationCreateDate, true));

					try {
						pst.executeUpdate();
					} catch (SQLException se) {
						String err = "Spouse aPID = " + aid
								+ " fails for RID = " + rid;
						errorLine.add(err + " [" + se.getMessage() + "]");
						logger.log(Level.WARNING, err, se);
					}

					pst.setInt(1, rid);
					if (this.relationIdB != null) {
						try {
							bid = Integer.parseInt(this.relationIdB
									.substring(1));
							pst.setInt(2, bid);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationIdB "
									+ this.relationIdB + " not numeric");
						}
					}
					pst.setString(3, "MARR");
					if (this.relationRowB != null) {
						try {
							relRow = Integer.parseInt(this.relationRowB);
							pst.setInt(4, relRow);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationRowB "
									+ this.relationRowB + " not numeric");
						}
					}
					pst.setInt(5, surety);
					pst.setNull(6, Types.TIMESTAMP);
					pst.setTimestamp(7,
							toTimestamp(this.relationCreateDate, true));

					try {
						pst.executeUpdate();
					} catch (SQLException se) {
						String err = "Spouse bPID = " + bid
								+ " fails for RID = " + rid;
						errorLine.add(err + " [" + se.getMessage() + "]");
						logger.log(Level.WARNING, err, se);
					}
					// if (this.runner != null) {
					// if (this.runner.setRunnerValue("RelationId: " + rid)) {
					// throw new SAXException(Resurses
					// .getString("SUKU_CANCELLED"));
					// }
					//
					// }

					if (this.relationBegDateFrom != null
							|| this.relationBegPlace != null
							|| this.relationBegType != null
							|| this.relationDescription != null) {

						StringBuilder allText = new StringBuilder();
						if (this.relationBegType != null)
							allText.append(this.relationBegType);
						if (this.relationDescription != null)
							allText.append(this.relationDescription);
						if (this.relationBegPlace != null)
							allText.append(this.relationBegPlace);
						if (this.relationNoteText != null)
							allText.append(this.relationNoteText);
						// if (this.relationSourceText != null) allText +=
						// this.relationSourceText;

						// System.out.println(allText.toString());
						String langus[] = extractLangs(allText.toString());

						this.nameCollector = new HashMap<String, String>();
						this.placeCollector = new HashMap<String, String>();

						pst = this.con
								.prepareStatement("select nextval('relationnoticeseq')");
						int rnid = 0;
						rs = pst.executeQuery();
						if (rs.next()) {
							rnid = rs.getInt(1);
						} else {
							throw new SAXException(
									"Sequence relationnoticeseq error");
						}
						rs.close();

						pst = this.con.prepareStatement(INSERT_RELATION_NOTICE);
						pst.setInt(1, rnid);
						pst.setInt(2, rid);
						pst.setString(3, "MARR");
						pst.setInt(4, 1); // rownumber
						pst.setInt(5, 100);
						pst.setString(
								6,
								langText(this.relationDescription, this.oldCode));
						pst.setString(7,
								langText(this.relationBegType, this.oldCode));
						pst.setString(8, this.relationBegDatePrefix);
						pst.setString(9, this.relationBegDateFrom);
						pst.setString(10, this.relationBegDateTo);
						pst.setString(11,
								langText(this.relationBegPlace, this.oldCode));
						pst.setString(12,
								langText(this.relationNoteText, this.oldCode));
						if (this.relationSourceId != null) {
							pst.setInt(13, idToInt(this.relationSourceId)); // langText(this.noticeSourceText,this.oldCode));
						} else {
							pst.setNull(13, Types.INTEGER);
						}

						pst.setString(14, this.relationSourceText);
						pst.setString(15, this.relationPrivateText);
						pst.setNull(16, Types.TIMESTAMP);
						pst.setTimestamp(17,
								toTimestamp(this.relationCreateDate, true));
						pst.executeUpdate();
						laskuriRelations++;

						if (langus != null && langus.length > 1) {
							for (i = 0; i < langus.length; i++) {
								if (!langus[i].equals(this.oldCode)) {
									// pstm =
									// this.con.prepareStatement("select nextval('relationseq')");
									//
									// rs = pstm.executeQuery();
									// if (rs.next()){
									// rid = rs.getInt(1);
									// } else {
									// throw new SAXException
									// ("Sequence relationseq error");
									// }
									// rs.close();

									pst = this.con
											.prepareStatement(INSERT_RELATION_LANGUAGE);
									pst.setInt(1, rnid);
									pst.setInt(2, rid);
									pst.setString(3, toLangCode(langus[i]));
									pst.setString(
											4,
											langText(this.relationBegType,
													langus[i]));
									pst.setString(
											5,
											langText(this.relationDescription,
													langus[i]));
									pst.setString(
											6,
											langText(this.relationBegPlace,
													langus[i]));
									pst.setString(
											7,
											langText(this.relationNoteText,
													langus[i]));
									pst.setNull(8, Types.TIMESTAMP);
									Timestamp now = new Timestamp(
											System.currentTimeMillis());
									pst.setTimestamp(9, now);
									// pstm.setString(9,
									// langText(this.relationSourceText,langus[i]));

									pst.executeUpdate();
								}
							}
						}
					}

					if (this.relationEndDateFrom != null
							|| this.relationEndPlace != null
							|| this.relationEndType != null) {

						String allText = "";
						if (this.relationEndType != null)
							allText += this.relationEndType;
						if (this.relationEndPlace != null)
							allText += this.relationEndPlace;

						String langus[] = extractLangs(allText);

						this.nameCollector = new HashMap<String, String>();
						this.placeCollector = new HashMap<String, String>();

						pst = this.con
								.prepareStatement("select nextval('relationnoticeseq')");
						int rnid = 0;
						rs = pst.executeQuery();
						if (rs.next()) {
							rnid = rs.getInt(1);
						} else {
							throw new SAXException(
									"Sequence relationnoticeseq error");
						}
						rs.close();

						pst = this.con.prepareStatement(INSERT_RELATION_NOTICE);
						pst.setInt(1, rnid);
						pst.setInt(2, rid);
						pst.setString(3, "DIV");
						pst.setInt(4, 2); // rownumber
						pst.setInt(5, 100);
						pst.setString(6, null);
						pst.setString(7, this.relationEndType);
						pst.setString(8, this.relationEndDatePrefix);
						pst.setString(9, this.relationEndDateFrom);
						pst.setString(10, this.relationEndDateTo);
						pst.setString(11, this.relationEndPlace);
						pst.setString(12, null); // note text
						pst.setNull(13, Types.INTEGER);
						pst.setNull(14, Types.VARCHAR);
						pst.setNull(15, Types.VARCHAR);
						pst.setNull(16, Types.TIMESTAMP);
						pst.setTimestamp(17,
								toTimestamp(this.relationCreateDate, true));
						pst.executeUpdate();

						if (langus != null && langus.length > 1) {
							for (i = 0; i < langus.length; i++) {
								if (!langus[i].equals(this.oldCode)) {

									pst = this.con
											.prepareStatement(INSERT_RELATION_LANGUAGE);
									pst.setInt(1, rnid);
									pst.setInt(2, rid);
									pst.setString(3, toLangCode(langus[i]));
									pst.setString(
											4,
											langText(this.relationEndType,
													langus[i]));
									pst.setString(5, null);
									pst.setString(
											6,
											langText(this.relationEndPlace,
													langus[i]));
									pst.setString(7, null);
									pst.setNull(8, Types.TIMESTAMP);
									Timestamp now = new Timestamp(
											System.currentTimeMillis());
									pst.setTimestamp(9, now);

									pst.executeUpdate();
								}
							}
						}
					}
				}
			}

			if (lastUnitId != null && lastUnitId.length() > 1) {
				int last = Integer.parseInt(lastUnitId.substring(1));
				if (aid >= lastMaxAid) {
					lastMaxAid = aid;
				}
				double prose = (lastMaxAid * 100) / last;
				int intprose = (int) prose;
				if (intprose < 100) {

					this.runner.setRunnerValue("" + intprose + ";" + rid);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "importing relation failed", e);
			throw new SAXException(e);
		}

		if (this.runner.setRunnerValue("Rid = " + this.rid)) {
			throw new SAXException(Resurses.getString("GETSUKU_CANCELLED"));
		}

		this.relationDescription = null;
		this.relationBegType = null;
		this.relationBegDateFrom = null;
		this.relationBegDateTo = null;
		this.relationBegPlace = null;
		this.relationEndType = null;
		this.relationEndDateFrom = null;
		this.relationEndDateTo = null;
		this.relationEndPlace = null;
		this.relationNoteText = null;
		this.relationSourceId = null;
		this.relationSourceText = null;
		this.relationPrivateText = null;
		this.rid = 0;
	}

	private void initUnitNotice() throws SAXException {
		try {
			PreparedStatement pst = null;

			try {
				pst = this.con
						.prepareStatement("select nextval('unitnoticeseq')");
				ResultSet rs = pst.executeQuery();
				if (rs.next()) {
					pnid = rs.getInt(1);
				} else {
					throw new SAXException("Sequence unitnoticeseq error");
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new SAXException("Sequence unitnoticeseq sql error");
			} finally {
				if (pst != null) {
					try {
						pst.close();
					} catch (SQLException ignored) {
						// SQLException ignored
					}
				}
			}

			pst = this.con.prepareStatement(INIT_UNIT_NOTICE);

			pst.setInt(1, unitPid);
			pst.setInt(2, pnid);
			pst.setString(3, this.noticeTag);

			pst.setString(4, this.noticePrivacy);
			int row = 0;
			try {
				row = Integer.parseInt(this.noticeRow);
			} catch (NumberFormatException ne) {
				// NumberFormatException ignored
			}
			pst.setInt(5, row);
			int surety = 100;
			try {
				row = Integer.parseInt(this.noticeSurety);
			} catch (NumberFormatException ne) {
				// NumberFormatException ignored
			}
			pst.setInt(6, surety);

			if (noticeModifiedDate == null) {
				pst.setNull(7, Types.TIMESTAMP);
			} else {
				pst.setTimestamp(7, toTimestamp(this.noticeModifiedDate, false));
			}
			pst.setTimestamp(8, toTimestamp(this.noticeCreateDate, true));
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "importing notice init failed", e);
			throw new SAXException(e);
		}

		noticeLanguage = null;
		noticeLanguageType = null;
		noticeLanguageDescription = null;
		noticeLanguagePlace = null;
		noticeLanguageMediaTitleText = null;
		noticeLanguageNoteText = null;
		noticeLanguageModifiedDate = null;
		noticeLanguageCreateDate = null;

	}

	private void updateUnitNotice() throws SAXException {
		int i;
		PreparedStatement pst;
		StringBuilder allText = new StringBuilder();
		if (this.noticeType != null)
			allText.append(this.noticeType);
		if (this.noticeDescription != null)
			allText.append(this.noticeDescription);
		if (this.noticePlace != null)
			allText.append(this.noticePlace);
		if (this.noticeNoteText != null)
			allText.append(this.noticeNoteText);
		// if (this.noticeSourceText != null) allText +=
		// this.noticeSourceText;
		if (this.noticeMediaTitle != null)
			allText.append(this.noticeMediaTitle);
		// System.out.println(allText.toString());
		String langus[] = extractLangs(allText.toString());

		this.nameCollector = new HashMap<String, String>();
		this.placeCollector = new HashMap<String, String>();

		try {

			// int rowno = 0;
			// int suretyValue = 100;

			pst = this.con.prepareStatement(UPDATE_UNIT_NOTICE);

			// this.pstm.setInt(1, unitPid);

			pst.setString(1, langText(this.noticeType, this.oldCode));
			pst.setString(2, langText(this.noticeDescription, this.oldCode));
			pst.setString(3, this.noticeDatePrefix);
			pst.setString(4, this.noticeDateFrom);
			pst.setString(5, this.noticeDateTo);
			pst.setString(6, langText(this.noticePlace, this.oldCode));

			pst.setString(7,
					extractCSVPart(this.noticeTag, this.noticeAddress, 0));
			pst.setString(8, this.noticePostalCode);
			pst.setString(9, this.noticePostOffice);
			pst.setString(10, this.noticeState);
			pst.setString(11, this.noticeCountry);
			pst.setString(12, this.noticeEmail);
			// String t1 = langText(this.noticeNoteText,this.oldCode);
			pst.setString(13, langText(this.noticeNoteText, this.oldCode));
			pst.setString(14, Utils.tidyFileName(this.noticeMediaFilename));
			pst.setString(15, langText(this.noticeMediaTitle, this.oldCode));

			if (finFamilyVersion == null) {
				pst.setString(16,
						Utils.extractPatronyme(this.noticeGivenName, false));
				pst.setString(17,
						Utils.extractPatronyme(this.noticeGivenName, true));
			} else {
				pst.setString(16, this.noticeGivenName);
				pst.setString(17, this.noticePatronym);

			}
			pst.setString(18, this.noticePrefix);
			pst.setString(19, this.noticeSurname);
			pst.setString(20, this.noticePostfix);
			if (this.noticeSourceId != null) {
				pst.setInt(21, idToInt(this.noticeSourceId)); // langText(this.noticeSourceText,this.oldCode));
			} else {
				pst.setNull(21, Types.INTEGER);
			}
			if (finFamilyVersion == null) {
				pst.setString(22,
						extractCSVPart(this.noticeTag, this.noticeAddress, 1));
				pst.setString(23,
						extractCSVPart(this.noticeTag, this.noticeAddress, 2));
				pst.setString(24,
						extractCSVPart(this.noticeTag, this.noticeAddress, 3));
			} else {
				pst.setString(22, this.noticeVillage);
				pst.setString(23, this.noticeFarm);
				pst.setString(24, this.noticeCroft);
			}
			pst.setString(25, this.noticeSourceText);
			pst.setString(26, this.noticePrivateText);

			pst.setInt(27, pnid);

			pst.executeUpdate();
			pst.close();

			if (this.placeCollector.size() > 0 || this.nameCollector.size() > 0
					|| (this.namelist != null && this.namelist.size() > 0)
					|| (this.placelist != null && this.placelist.size() > 0)) {

				NameArray asn = new NameArray();
				Iterator<String> it = this.nameCollector.keySet().iterator();
				while (it.hasNext()) {

					asn.append(it.next());

				}
				if (namelist != null) {
					for (String tmp : namelist) {
						asn.append(tmp);
					}
					namelist = null;
				}
				// sn.append("}");
				NameArray asp = new NameArray();

				// StringBuilder sp = new StringBuilder();
				it = this.placeCollector.keySet().iterator();
				while (it.hasNext()) {
					// if (sp.length() == 0){
					// sp.append("{");
					// } else {
					// sp.append(",");
					// }
					asp.append(it.next());
					// sp.append("\""+it.next()+"\"");
				}
				if (placelist != null) {
					for (String tmp : placelist) {
						asp.append(tmp);
					}
					placelist = null;
				}
				// sp.append("}");

				PreparedStatement ps = this.con
						.prepareStatement(UPDATE_UNIT_INDEX_DATA);
				ps.setArray(1, asn);
				ps.setArray(2, asp);
				// ps.setString(1, sn.toString());
				// ps.setString(2, sp.toString());
				ps.setInt(3, pnid);
				ps.executeUpdate();
				ps.close();
			}

			if (this.noticeMediaFilename != null) {
				File file = null;
				FileInputStream fis;
				if (isZipFile) {
					String path = images.get(this.sukuMediaFolder + "/"
							+ this.noticeMediaFilename);
					if (path != null) {

						file = new File(path);
					}
				} else if (this.databaseHasImages) {

					logger.fine("Trying image file: " + this.databaseFolder
							+ "/" + this.sukuMediaFolder + "/"
							+ this.noticeMediaFilename);

					file = new File(this.databaseFolder + "/"
							+ this.sukuMediaFolder + "/"
							+ this.noticeMediaFilename);
				}
				if (file != null && file.exists() && file.isFile()) {
					BufferedImage sourceImage = ImageIO.read(file);
					if (sourceImage == null) {
						String wrn = "Image file: " + this.databaseFolder + "/"
								+ this.sukuMediaFolder + "/"
								+ this.noticeMediaFilename + " read failed!!";
						logger.warning(wrn);
						errorLine.add(wrn);
					} else {
						int mediaWidth = sourceImage.getWidth(null);
						int mediaHeight = sourceImage.getHeight(null);
						try {
							fis = new FileInputStream(file);
							PreparedStatement ps = this.con
									.prepareStatement(UPDATE_IMAGE_DATA);

							ps.setBinaryStream(1, fis, (int) file.length());
							ps.setInt(2, mediaWidth);
							ps.setInt(3, mediaHeight);
							ps.setInt(4, pnid);
							ps.executeUpdate();
							ps.close();
							fis.close();
						} catch (FileNotFoundException e) {
							String wrn = "Image file "
									+ this.noticeMediaFilename + " not found";
							logger.warning(wrn);
							errorLine.add(wrn);
							e.printStackTrace();
						} catch (IOException e) {
							String wrn = "Image file "
									+ this.noticeMediaFilename + ":"
									+ e.getMessage();
							logger.log(Level.WARNING, "Image file "
									+ this.noticeMediaFilename, e);

							errorLine.add(wrn);
							e.printStackTrace();
						}
					}
					// int mediaType = sourceImage.getType();
					// System.out.println("kuva: " +
					// this.noticeMediaFilename + " = " + mediaWidth +
					// "/" + mediaHeight );
				} else {

					String wrn = "Image file " + this.noticeMediaFilename
							+ " is missing";
					logger.warning(wrn);
					errorLine.add(wrn);
				}

			}

			if (langus != null && langus.length > 1) {
				for (i = 0; i < langus.length; i++) {
					if (!langus[i].equals(this.oldCode)) {

						pst = this.con.prepareStatement(INSERT_UNIT_LANGUAGE);

						pst.setInt(1, unitPid);
						pst.setInt(2, pnid);
						pst.setString(3, this.noticeTag);
						pst.setString(4, toLangCode(langus[i]));

						pst.setString(5, langText(this.noticeType, langus[i]));
						pst.setString(6,
								langText(this.noticeDescription, langus[i]));

						pst.setString(7, langText(this.noticePlace, langus[i]));
						pst.setString(8,
								langText(this.noticeNoteText, langus[i]));
						pst.setString(9,
								langText(this.noticeMediaTitle, langus[i]));

						pst.setNull(10, Types.TIMESTAMP);

						Timestamp now = new Timestamp(
								System.currentTimeMillis());
						pst.setTimestamp(11, now);

						pst.executeUpdate();
						pst.close();
					}
				}
			}

			this.noticeTag = null;
			this.noticeRow = null;
			this.noticeType = null;
			this.noticeDescription = null;
			this.noticeDatePrefix = null;
			this.noticeDateFrom = null;
			this.noticeDateTo = null;
			this.noticePlace = null;
			this.noticeVillage = null;
			this.noticeFarm = null;
			this.noticeCroft = null;
			this.noticeAddress = null;
			this.noticePostOffice = null;
			this.noticePostalCode = null;
			this.noticeState = null;
			this.noticeCountry = null;
			this.noticeEmail = null;
			this.noticeNoteText = null;
			this.noticeMediaFilename = null;
			this.noticeMediaTitle = null;
			this.noticeGivenName = null;
			this.noticePrefix = null;
			this.noticeSurname = null;
			this.noticePatronym = null;
			this.noticePostfix = null;
			this.noticeSourceId = null;
			this.noticePrivateText = null;
			this.noticeSourceText = null;
			this.noticeCreateDate = null;

		} catch (SQLException e) {

			logger.log(Level.SEVERE, "importing notices failed", e);
			throw new SAXException(e);

		} catch (IOException e) {
			logger.log(Level.SEVERE, "importing image failed", e);
			throw new SAXException(e);
		}
	}

	private void updateUnit() throws SAXException {
		logger.fine("UNIT: " + this.unitId + "/" + this.unitTag + "/"
				+ this.unitSex + "/" + this.unitRefn + "/" + this.unitGivenName
				+ "/" + this.unitPrefix + "/" + this.unitSurName + "/"
				+ this.unitPostfix + "/" + this.unitCreateDate);
		int unitPid = 0;
		PreparedStatement pst;
		try {

			pst = this.con.prepareStatement(UPDATE_UNIT);

			try {
				unitPid = Integer.parseInt(this.unitId.substring(1));
			} catch (NumberFormatException ne) {
				throw new SAXException("UnitId " + this.unitId
						+ " is not numeric");
			}
			pst.setString(1, this.unitRefn);
			unitRefn = null;
			pst.setString(2, this.unitSourceText);
			pst.setString(3, this.unitPrivateText);
			pst.setInt(4, unitPid);
			int resu = pst.executeUpdate();
			if (resu != 1) {
				throw new SAXException("update of unit " + this.unitId
						+ " failed. count = " + resu);
			}
			pst.close();

			if (this.runner.setRunnerValue("Pid = " + this.unitId)) {
				throw new SAXException(Resurses.getString("GETSUKU_CANCELLED"));
			}

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "update unit source failed", e);
		}

		this.unitPrivateText = null;
		this.unitSourceText = null;
	}

	private void insertUnitName() throws SAXException {
		if (this.unitId == null) {
			throw new SAXException("UnitId is null");
		}
		PreparedStatement pst = null;
		try {

			this.currentStatus = "Unit " + unitPid;
			pst = this.con.prepareStatement(INSERT_UNIT);

			pst.setInt(1, unitPid);
			pst.setString(2, this.unitSex);
			if (finFamilyVersion == null) {
				pst.setString(3, this.unitGroupId);
			} else {
				pst.setString(3, this.unitGroup);
			}
			pst.setString(4, this.unitPrivacy);

			if (this.unitSourceId != null) {
				pst.setInt(5, idToInt(this.unitSourceId));
			} else {
				pst.setNull(5, Types.INTEGER);
			}
			pst.setTimestamp(6, toTimestamp(this.unitCreateDate, true));
			pst.executeUpdate();
			// this.runner.setRunnerValue("pid [" + unitPid + "]");
			logger.fine("Unit: " + this.unitId + "/" + this.unitSex + "/"
					+ this.unitSourceId);

			laskuriUnits++;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "importing unit failed", e);
			throw new SAXException(e);
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException ignored) {
					// SQLException ignored
				}
			}
		}
		if (finFamilyVersion == null) {
			if (this.unitGivenName != null || this.unitSurName != null) {
				try {
					pst = this.con
							.prepareStatement("select nextval('unitnoticeseq')");
					int pnid = 0;
					ResultSet rs = pst.executeQuery();
					if (rs.next()) {
						pnid = rs.getInt(1);
					} else {
						throw new SAXException("Sequence unitnoticeseq error");
					}
					rs.close();
					pst.close();

					pst = this.con.prepareStatement(INSERT_NAME_NOTICE);

					pst.setInt(1, unitPid);
					pst.setInt(2, pnid);
					pst.setString(3, "NAME");
					pst.setString(4, this.unitPrivacy);
					pst.setString(5,
							Utils.extractPatronyme(this.unitGivenName, false));
					pst.setString(6,
							Utils.extractPatronyme(this.unitGivenName, true));
					pst.setString(7, this.unitPrefix);
					pst.setString(8, this.unitSurName);
					pst.setString(9, this.unitPostfix);
					pst.setTimestamp(10, toTimestamp(this.unitCreateDate, true));
					pst.executeUpdate();
					pst.close();
					logger.fine("UnitName: " + this.unitId + "/"
							+ this.unitGivenName + "/" + this.unitPrefix + "/"
							+ this.unitSurName + "/" + unitPostfix);

					StringBuilder sb = new StringBuilder();

					sb.append(this.unitId + ":  ");
					lastUnitId = this.unitId;
					sb.append(this.unitGivenName);
					if (this.unitPrefix != null) {
						sb.append(" ");
						sb.append(this.unitPrefix);
					}
					if (this.unitSurName != null) {
						sb.append(" ");
						sb.append(this.unitSurName);
					}
					if (this.unitPostfix != null) {
						sb.append(" ");
						sb.append(this.unitPostfix);
					}
					if (this.runner.setRunnerValue(sb.toString())) {
						throw new SAXException(
								Resurses.getString("SUKU_CANCELLED"));
					}

				} catch (SQLException e) {
					logger.log(Level.SEVERE, "importing unit name failed", e);
					throw new SAXException(e);
				} finally {
					if (pst != null) {
						try {
							pst.close();
						} catch (SQLException ignored) {
							// SQLException ignored
						}
					}
				}
			}
		}
		logger.fine("UNIT: " + this.unitId + "/" + this.unitTag + "/"
				+ this.unitSex + "/" + this.unitRefn + "/" + this.unitGivenName
				+ "/" + this.unitPrefix + "/" + this.unitSurName + "/"
				+ this.unitPostfix + "/" + this.unitCreateDate);

		this.unitGivenName = null;
		this.unitSurName = null;
		this.unitPrefix = null;
		this.unitPostfix = null;
		this.unitGroupId = null;
		this.unitPrivacy = null;
		this.unitSourceId = null;
	}

	private String extractCSVPart(String tag, String noticeAddress, int i) {
		int jj;
		if (noticeAddress == null)
			return null;
		for (jj = 0; jj < farmTags.length; jj++) {
			if (farmTags[jj].equals(tag))
				break;
		}
		if (jj == farmTags.length) {
			if (i == 0)
				return noticeAddress;
			return null;
		}

		String parts[] = noticeAddress.split(",");

		if (parts.length == 1) {
			if (i == 0)
				return noticeAddress;
			return null;
		}
		if (i == 0)
			return null;

		if (i + 1 > parts.length)
			return null;

		return parts[i - 1];
	}

	private static final String[] farmTags = { "BIRT", "DEAT", "EMIG", "IMMI" };

	private String toLangCode(String oldCode) {
		String tocode = oldCode.toLowerCase();
		if (tocode.equals("se")) {
			tocode = "sv";
		}

		return tocode;
	}

	/**
	 * Extract text for selected language
	 * 
	 * @param text
	 * @param lang
	 * @return text for requested language
	 */
	private String langText(String text, String lang) {
		if (text == null)
			return null;

		HashMap<String, String> map = new HashMap<String, String>();

		String v;
		StringBuilder sb = new StringBuilder();

		int pnt = 0;
		int nxt = 0;
		int fine = 0;

		while (true) {
			nxt = text.indexOf("{$", pnt);

			if (nxt < 0) {

				if (pnt < text.length()) {
					sb.append(text.substring(pnt));
				}
				break;
			} else if (nxt > 0) {
				v = map.get(this.oldCode);
				v += text.substring(pnt, nxt);
				sb.append(text.substring(pnt, nxt));
				pnt = nxt + 1;
			}

			fine = text.indexOf("{$}", pnt);
			if (fine < 0) {
				fine = text.length();
			}

			if (fine > nxt) {
				String deffi = null;
				String mylan = null;

				String apu = text.substring(nxt, fine);
				int ix;// = apu.indexOf("}");
				int jx;
				String lan;
				String val;
				while (apu != null) {
					ix = apu.indexOf("}");
					if (ix < 0 || ix > 5)
						break;
					lan = apu.substring(2, ix);
					jx = apu.indexOf("{$", ix);
					if (jx < 0) {
						val = apu.substring(ix + 1);
						apu = null;
					} else {
						val = apu.substring(ix + 1, jx);
						apu = apu.substring(jx);
					}
					if (lan.equals("$$")) {
						deffi = val;
					} else if (lan.equals(lang)) {
						mylan = val;
					}

				}

				if (mylan != null) {
					sb.append(mylan);
				} else if (deffi != null) {
					sb.append(deffi);
				}

			}
			pnt = fine + 3;

		}

		if (sb.length() == 0) {
			return null;
		}

		//
		// Now lets remove name and address tags
		//

		String kieli = sb.toString();

		int i1 = 0;
		int i2 = 0;
		sb = new StringBuilder();

		while (true) {
			i2 = kieli.indexOf("{", i1);
			if (i2 < 0) {
				// System.out.println("X1:" + kieli.substring(i1));
				sb.append(kieli.substring(i1));
				break;
			}
			if (i2 > 0) {
				// System.out.println("X2:" + kieli.substring(i1,i2));
				sb.append(kieli.substring(i1, i2));
				i1 = i2;
			}
			i1 += 1;
			i2 = kieli.indexOf("}", i1);
			if (i2 < 0) {
				sb.append(kieli.substring(i1));
				break;
			}
			if (kieli.charAt(i1) == 'O') {
				if (i2 > i1 + 5) {
					String tmp = kieli.substring(i1 + 3, i2 - 1);
					this.placeCollector.put(tmp, tmp);
					sb.append(tmp);
				}
			} else if (kieli.charAt(i1) == 'P') {
				if (i2 > i1 + 5) {
					String nimi = kieli.substring(i1 + 1, i2);
					StringBuilder tnimi = new StringBuilder();
					String etu = null, suku = null;
					StringBuilder tuloste = new StringBuilder();
					int n1, n2;

					n1 = nimi.indexOf("<");
					if (n1 >= 0) {
						n2 = nimi.indexOf(">", n1);
						if (n2 > 0) {
							if (nimi.charAt(n1 + 1) == '2') {
								etu = nimi.substring(n1 + 2, n2);
							} else {
								suku = nimi.substring(n1 + 2, n2);
							}
							tuloste.append(nimi.substring(n1 + 2, n2));
						}

						n1 = nimi.indexOf("<", n2);
						if (n1 >= 0) {
							n2 = nimi.indexOf(">", n1);
							if (n2 > 0) {
								if (nimi.charAt(n1 + 1) == '2') {
									etu = nimi.substring(n1 + 2, n2);
								} else {
									suku = nimi.substring(n1 + 2, n2);
								}
								if (tuloste.length() > 0) {
									tuloste.append(" ");
								}
								tuloste.append(nimi.substring(n1 + 2, n2));
								// System.out.println("z2"+nimi.substring(n1+2,n2));
							}
						}

						if (etu != null || suku != null) {
							if (suku != null) {
								tnimi.append(suku);
							}
							tnimi.append(",");

							if (etu != null) {
								tnimi.append(etu);
							}

							sb.append(tuloste);
							this.nameCollector.put(tnimi.toString(),
									tnimi.toString());
						}
					}
				}
			}

			i1 = i2 + 1;

		}

		if (text.equals(sb.toString()) && !lang.equals(this.oldCode))
			return null;

		return sb.toString();

	}

	/**
	 * Extract the language codes existing withinh text
	 * 
	 * @param text
	 * @return a string array with language codes (oldCode) that text contains
	 * 
	 */
	private String[] extractLangs(String text) {
		if (text == null)
			return null;

		StringBuffer sbs[];
		String langs[];
		Iterator<String> it;
		int i;
		boolean isElse = false;
		HashMap<String, String> map = new HashMap<String, String>();
		String s;
		int i1 = 0;
		int i2 = 0;
		while (true) {

			i1 = text.indexOf("{$", i2);

			if (i1 >= 0) {
				i2 = text.indexOf("}", i1);
				if (i2 > i1) {
					s = text.substring(i1 + 2, i2);
					if (s.length() == 2) {
						map.put(s, s);
						if (s.equals("$$")) {
							isElse = true;
						}
					}
				} else {
					break;
				}

			} else {
				break;
			}
			i2++;

		}

		map.put(this.oldCode, this.oldCode);

		if (isElse) {
			if (map.get("FI") == null) {
				map.put("FI", "FI");
			}
			if (map.get("SE") == null) {
				map.put("SE", "SE");
			}
			if (map.get("EN") == null) {
				map.put("EN", "EN");
			}
			map.remove("$$");

		}

		sbs = new StringBuffer[map.size()];
		langs = new String[sbs.length];
		for (i = 0; i < sbs.length; i++) {
			sbs[i] = new StringBuffer();

		}
		i = 0;
		it = map.keySet().iterator();

		while (it.hasNext()) {
			langs[i++] = it.next();
			// System.out.println(":" + it.next() + ":");
		}
		return langs;

	}

	private int idToInt(String numero) {
		int id;
		if (numero == null || numero.length() < 2)
			return 0;
		try {
			id = Integer.parseInt(numero.substring(1));
		} catch (NumberFormatException ne) {
			id = -1; // throw new SAXException ("UnitId " + this.unitId +
			// " is not numeric");
		}
		return id;
	}

	private Timestamp toTimestamp(String date, boolean forceNow) {
		if (date == null) {
			if (forceNow) {
				Timestamp now = new Timestamp(System.currentTimeMillis());
				return now;
			} else {
				return null;
			}
		}
		java.util.Date dd;
		try {
			dd = cdf.parse(date);
		} catch (ParseException e) {

			dd = new java.util.Date();
		}

		Timestamp tms = new Timestamp(dd.getTime());
		return tms;
	}

	private File copyToTempfile(ZipInputStream zipIn, String imgName)
			throws IOException, FileNotFoundException, SukuException {
		int ldot = imgName.lastIndexOf(".");
		String imgSuffix = null;
		if (ldot > 0 && ldot > (imgName.length() - 6)) {
			imgSuffix = imgName.substring(ldot);
		}

		if (baseFolder.length() > 0) {
			if (imgName.substring(0, baseFolder.length()).equalsIgnoreCase(
					baseFolder)) {
				imgName = imgName.substring(baseFolder.length());
			}
		}
		if (this.runner.setRunnerValue(imgName)) {
			throw new SukuException(Resurses.getString("GEDCOM_CANCELLED"));
		}
		File tf = File.createTempFile("finFam", imgSuffix);
		BufferedOutputStream fos = new BufferedOutputStream(
				new FileOutputStream(tf));
		int dd = 0;
		while ((dd = zipIn.read()) >= 0) {
			fos.write(dd);
		}
		tf.deleteOnExit();
		fos.close();

		images.put(imgName.replace('\\', '/'), tf.getPath());
		zipIn.closeEntry();
		return tf;
	}

}
