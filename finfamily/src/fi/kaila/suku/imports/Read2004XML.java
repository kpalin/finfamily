package fi.kaila.suku.imports;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fi.kaila.suku.util.NameArray;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * <h1>Import Suku 2004 backup file</h1>
 * 
 * <p>
 * Importing of the backup file is executed using XML SAX methods
 * </p>
 * 
 * @author Kaarle Kaila
 * 
 * 
 * 
 */
public class Read2004XML extends DefaultHandler {

	private static Logger logger = Logger
			.getLogger(Read2004XML.class.getName());

	int laskuriUnits = 0;
	int laskuriRelations = 0;
	int laskuriGroups = 0;
	int laskuriConversion = 0;
	int laskuriViews = 0;

	private String qName = null;
	private StringBuffer currentChars = null;

	private Connection con = null;
	private String urli = null;
	private String databaseFolder = null;
	private boolean databaseHasImages = false;
	Vector<String> errorLine = new Vector<String>();
	private volatile String currentStatus = null;

	SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd");

	private static final String INSERT_UNIT = "insert into Unit "
			+ "(PID,tag,sex,groupid,privacy,sid,createdate)"
			+ " values (?,'INDI',?,?,?,?,?)";

	private static final String UPDATE_UNIT = "update Unit set userrefn = ?,  sourcetext = ?, privatetext = ? "
			+ "where pid = ? ";

	private static final String INSERT_NAME_NOTICE = "insert into UnitNotice "
			+ "(PID,PNID,tag,privacy,givenname,patronym,prefix,surname,postfix,createdate)"
			+ " values (?,?,?,?,?,?,?,?,?,?)";

	private static final String INSERT_UNIT_NOTICE = "insert into UnitNotice "
			+ "(PID,PNID,tag,privacy,noticerow,noticetype,"
			+ "description,dateprefix,fromdate,todate,place,"
			+ "address,postalcode,postoffice,country,email,"
			+ "notetext,mediafilename,mediatitle,givenname,patronym,prefix,"
			+ "surname,postfix,SID,village,farm,croft,sourcetext,privatetext,createdate) "
			+ "values (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?)";

	private static final String INSERT_UNIT_LANGUAGE = "insert into UnitLanguage "
			+ "(PID,PNID,tag,langCode,noticetype,"
			+ "description,place,notetext,mediatitle) "
			+ "values (?,?,?,?,?,?,?,?,?)";

	private static final String UPDATE_IMAGE_DATA = "update UnitNotice set MediaData = ?,mediaWidth = ?"
			+ ",mediaheight = ? where PNID = ? ";

	private static final String UPDATE_UNIT_INDEX_DATA = "update UnitNotice set RefNames = ?,RefPlaces = ? where PNID = ? ";

	private static final String INSERT_RELATION = "insert into Relation (RID,PID,tag,relationrow,createdate) values (?,?,?,?,?)";

	private static final String INSERT_RELATION_NOTICE = "insert into RelationNotice "
			+ "(RNID,RID,tag,noticerow,description,relationtype,"
			+ "dateprefix,fromdate,todate,place,notetext,"
			+ "SID,sourcetext,privatetext,createdate) values (?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?)";

	private static final String INSERT_RELATION_ADOPTION = "insert into RelationNotice "
			+ "(RNID,RID,tag,noticerow,createdate) values (?,?,?,?,?)";

	private static final String INSERT_RELATION_LANGUAGE = "insert into RelationLanguage "
			+ "(RNID,RID,langCode,relationtype,"
			+ "description,place,notetext) " + "values (?,?,?,?,?,?,?)";

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
	private static final String noticePlaceTG = "|genealog|units|unit|notices|notice|place";
	private static final String noticeGivenNameTG = "|genealog|units|unit|notices|notice|name|givenname";
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
	private static final String noticeCountryTG = "|genealog|units|unit|notices|notice|address|country";
	private static final String noticeEmailTG = "|genealog|units|unit|notices|notice|address|email";

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

	private static final String groupTG = "|genealog|groups|group";
	private static final String groupNameTG = "|genealog|groups|group|groupname";
	private static final String groupDescriptionTG = "|genealog|groups|group|description";

	private static final String sourceTG = "|genealog|sources|source-data";
	// private static final String sourceNoteTextTG =
	// "|genealog|sources|source-data|notetext";

	private static final String conversionsTG = "|genealog|conversions|conversion";
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

	private String oldCode = null;

	private String currentEle = "";

	private String unitGivenName = null;
	private String unitSurName = null;
	private String unitPrefix = null;
	private String unitPostfix = null;
	private String unitGroupId = null;
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
	private String noticeDescription = null;
	private String noticeDatePrefix = null;
	private String noticeDateFrom = null;
	private String noticeDateTo = null;
	private String noticePlace = null;
	private String noticeAddress = null;
	private String noticePostOffice = null;
	private String noticePostalCode = null;
	private String noticeCountry = null;
	private String noticeEmail = null;
	private String noticeNoteText = null;
	private String noticeMediaFilename = null;
	private String noticeMediaTitle = null;
	private String noticeGivenName = null;
	private String noticeSurname = null;
	private String noticePrefix = null;
	private String noticePostfix = null;
	private String noticeSourceId = null;
	private String noticePrivateText = null;
	private String noticeSourceText = null;

	private String noticeCreateDate = null;

	private String relationTag = null;
	private String relationIdA = null;
	private String relationIdB = null;
	private String relationRowA = null;
	private String relationRowB = null;
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
	private String relationCreateDate = null;

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

	PreparedStatement pstm;

	private HashMap<String, String> nameCollector = null;
	private HashMap<String, String> placeCollector = null;

	private Import2004Dialog runner = null;

	/**
	 * * <h1>Constructor to setup for thread</h1>
	 * 
	 * <p>
	 * The SAX importing is done as a separate thread
	 * </p>
	 * 
	 * @param urli
	 *            address of the backup file
	 * @param con
	 *            connection instance to the PostgreSQL database
	 * @param oldCode
	 *            the language code used for the main language in Suku 2004
	 * @throws SukuException
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
	 * @return current state of process
	 */
	public synchronized String getCurrentStatus() {
		return this.currentStatus;
	}

	/**
	 * Method that does the import of file at urli
	 * 
	 * @throws SukuException
	 * 
	 */
	public SukuData importFile() throws SukuException {

		SukuData resp = new SukuData();
		;

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

			if (this.urli.endsWith(".gz")) {
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

			stm.executeUpdate(UPDATE_GROUPS);

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

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		this.currentEle += "|" + qName;

		this.currentChars = new StringBuffer();
		this.qName = qName;

		if (this.currentEle.equals(unitTG)) {

			this.unitSex = attributes.getValue("sex");
			this.unitId = attributes.getValue("unitid");
			this.unitTag = attributes.getValue("tag");
			this.unitPrivacy = attributes.getValue("privacy");
			this.unitGroupId = attributes.getValue("groupid");
			this.unitCreateDate = attributes.getValue("createdate");
		}
		if (this.currentEle.equals(unitSourceTG)) {
			this.unitSourceId = attributes.getValue("sourceid");
			// logger.fine("Unit: " + this.unitId + "/" + this.unitSex +"/" +
			// this.unitSourceId);

		} else if (this.currentEle.equals(unitRefnTG)) {
			this.unitRefn = attributes.getValue("refn");
		} else if (this.currentEle.equals(noticeTG)) {
			this.noticeRow = attributes.getValue("row");
			this.noticeTag = attributes.getValue("tag");
			this.noticePrivacy = attributes.getValue("privacy");
			this.noticeSourceId = attributes.getValue("sourceid");
			this.noticeCreateDate = attributes.getValue("createdate");
		} else if (this.currentEle.equals(noticeSourceTG)) {
			this.noticeSourceId = attributes.getValue("sourceid");
			logger.fine("UnitNotice: " + this.unitId + "/"
					+ this.noticeSourceId);
		} else if (this.currentEle.equals(noticeDateTG)) {
			this.noticeDatePrefix = attributes.getValue("type");
		} else if (this.currentEle.equals(relationTG)) {
			this.relationIdA = attributes.getValue("unitida");
			this.relationIdB = attributes.getValue("unitidb");
			this.relationTag = attributes.getValue("tag");
			this.relationRowA = attributes.getValue("rowa");
			this.relationRowB = attributes.getValue("rowb");
			this.relationSourceId = attributes.getValue("sourceid");
			this.relationCreateDate = attributes.getValue("createdate");

		} else if (this.currentEle.equals(relationSourceTG)) {
			this.relationSourceId = attributes.getValue("sourceid");
			logger.fine("Relation: " + this.relationIdA + "/"
					+ this.relationSourceId);
		} else if (this.currentEle.equals(relationBegDateTG)) {
			this.relationBegDatePrefix = attributes.getValue("type");
		} else if (this.currentEle.equals(relationEndDateTG)) {
			this.relationEndDatePrefix = attributes.getValue("type");
		} else if (this.currentEle.equals(sourceTG)) {
			this.sourceId = attributes.getValue("sourceid");

			this.sourceNoteText = null;
		} else if (this.currentEle.equals(groupTG)) {
			this.groupId = attributes.getValue("groupid");
		} else if (this.currentEle.equals(conversionsTG)) {
			this.conversionsRule = attributes.getValue("rule");
		} else if (this.currentEle.equals(conversionsToTG)) {
			this.conversionsLang = attributes.getValue("language");
		} else if (this.currentEle.equals(viewTG)) {
			this.viewId = attributes.getValue("viewid");
			this.viewCreateDate = attributes.getValue("createdate");
		} else if (this.currentEle.equals(viewUnitTG)) {
			this.viewUnitPid = attributes.getValue("unitid");
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (this.currentChars != null) {

			this.currentChars.append(ch, start, length);
			// this.fileLength += length;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		int id, l, i;
		String aux;
		// System.out.println("ENDE: " + this.currentEle + "/" + qName);
		if (this.qName != null) {
			// System.out.println("Ele: "+ this.currentEle + " = " +
			// this.currentChars.toString());
			this.qName = null;
		}
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

			if (this.unitId == null) {
				throw new SAXException("UnitId is null");
			}

			try {
				id = Integer.parseInt(this.unitId.substring(1));
			} catch (NumberFormatException ne) {
				throw new SAXException("UnitId " + this.unitId
						+ " is not numeric");
			}

			try {

				this.currentStatus = "Unit " + id;
				this.pstm = this.con.prepareStatement(INSERT_UNIT);

				this.pstm.setInt(1, id);
				this.pstm.setString(2, this.unitSex);

				this.pstm.setString(3, this.unitGroupId);
				this.pstm.setString(4, this.unitPrivacy);

				if (this.unitSourceId != null) {
					this.pstm.setInt(5, idToInt(this.unitSourceId));
				} else {
					this.pstm.setNull(5, Types.INTEGER);
				}
				this.pstm.setTimestamp(6, toTimestamp(this.unitCreateDate));
				this.pstm.executeUpdate();

				logger.fine("Unit: " + this.unitId + "/" + this.unitSex + "/"
						+ this.unitSourceId);

				laskuriUnits++;
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "importing unit failed", e);
				throw new SAXException(e);
			}

			if (this.unitGivenName != null || this.unitSurName != null) {
				try {
					this.pstm = this.con
							.prepareStatement("select nextval('unitnoticeseq')");
					int pnid = 0;
					ResultSet rs = this.pstm.executeQuery();
					if (rs.next()) {
						pnid = rs.getInt(1);
					} else {
						throw new SAXException("Sequence unitnoticeseq error");
					}
					rs.close();

					this.pstm = this.con.prepareStatement(INSERT_NAME_NOTICE);

					this.pstm.setInt(1, id);
					this.pstm.setInt(2, pnid);
					this.pstm.setString(3, "NAME");
					this.pstm.setString(4, this.unitPrivacy);
					this.pstm.setString(5, Utils.extractPatronyme(
							this.unitGivenName, false));
					this.pstm.setString(6, Utils.extractPatronyme(
							this.unitGivenName, true));
					this.pstm.setString(7, this.unitPrefix);
					this.pstm.setString(8, this.unitSurName);
					this.pstm.setString(9, this.unitPostfix);
					this.pstm
							.setTimestamp(10, toTimestamp(this.unitCreateDate));
					this.pstm.executeUpdate();

					logger.fine("UnitName: " + this.unitId + "/"
							+ this.unitGivenName + "/" + this.unitPrefix + "/"
							+ this.unitSurName + "/" + unitPostfix);

					if (this.runner != null) {
						StringBuffer sb = new StringBuffer();

						sb.append(this.unitId + ":  ");
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
							throw new SAXException(Resurses
									.getString("SUKU_CANCELLED"));
						}
					}

				} catch (SQLException e) {
					logger.log(Level.SEVERE, "importing unit name failed", e);
					throw new SAXException(e);
				}
			}

			logger.fine("UNIT: " + this.unitId + "/" + this.unitTag + "/"
					+ this.unitSex + "/" + this.unitRefn + "/"
					+ this.unitGivenName + "/" + this.unitPrefix + "/"
					+ this.unitSurName + "/" + this.unitPostfix + "/"
					+ this.unitCreateDate);

			this.unitGivenName = null;
			this.unitSurName = null;
			this.unitPrefix = null;
			this.unitPostfix = null;
			this.unitGroupId = null;
			this.unitPrivacy = null;
			this.unitSourceId = null;

		}

		if (this.currentEle.equals(unitTG)) {
			logger.fine("UNIT: " + this.unitId + "/" + this.unitTag + "/"
					+ this.unitSex + "/" + this.unitRefn + "/"
					+ this.unitGivenName + "/" + this.unitPrefix + "/"
					+ this.unitSurName + "/" + this.unitPostfix + "/"
					+ this.unitCreateDate);

			try {
				this.pstm = this.con.prepareStatement(UPDATE_UNIT);

				try {
					id = Integer.parseInt(this.unitId.substring(1));
				} catch (NumberFormatException ne) {
					throw new SAXException("UnitId " + this.unitId
							+ " is not numeric");
				}
				this.pstm.setString(1, this.unitRefn);
				this.unitRefn = null;
				this.pstm.setString(2, this.unitSourceText);
				this.pstm.setString(3, this.unitPrivateText);
				this.pstm.setInt(4, id);
				int resu = this.pstm.executeUpdate();
				if (resu != 1) {
					throw new SAXException("update of unit " + this.unitId
							+ " failed. count = " + resu);
				}

			} catch (SQLException e) {
				logger.log(Level.SEVERE, "update unit source failed", e);
			}

			this.unitPrivateText = null;
			this.unitSourceText = null;

		}

		if (this.currentEle.equals(noticeTypeTG)) {
			this.noticeType = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeDescriptTG)) {
			this.noticeDescription = this.currentChars.toString();
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
		if (this.currentEle.equals(noticeGivenNameTG)) {
			this.noticeGivenName = this.currentChars.toString();
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
		if (this.currentEle.equals(noticeCountryTG)) {
			this.noticeCountry = this.currentChars.toString();
		}
		if (this.currentEle.equals(noticeEmailTG)) {
			this.noticeEmail = this.currentChars.toString();
		}

		if (this.currentEle.equals(noticeTG)) { // end of notice

			StringBuffer allText = new StringBuffer();
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
				int pnid = 0;
				int rowno = 0;
				this.pstm = this.con
						.prepareStatement("select nextval('unitnoticeseq')");

				ResultSet rs = this.pstm.executeQuery();
				if (rs.next()) {
					pnid = rs.getInt(1);
				} else {
					throw new SAXException("Sequence unitnoticeseq error");
				}
				rs.close();
				this.pstm = this.con.prepareStatement(INSERT_UNIT_NOTICE);
				try {
					id = Integer.parseInt(this.unitId.substring(1));
				} catch (NumberFormatException ne) {
					throw new SAXException("UnitId " + this.unitId
							+ " is not numeric");
				}
				this.pstm.setInt(1, id);
				this.pstm.setInt(2, pnid);
				this.pstm.setString(3, this.noticeTag);
				this.pstm.setString(4, this.noticePrivacy);
				try {
					rowno = Integer.parseInt(this.noticeRow);
				} catch (NumberFormatException ne) {
					throw new SAXException("Rownumber " + this.unitId + "/"
							+ this.noticeRow + " is not numeric");
				}
				this.pstm.setInt(5, rowno);
				this.pstm.setString(6, langText(this.noticeType, this.oldCode));
				this.pstm.setString(7, langText(this.noticeDescription,
						this.oldCode));
				this.pstm.setString(8, this.noticeDatePrefix);
				this.pstm.setString(9, this.noticeDateFrom);
				this.pstm.setString(10, this.noticeDateTo);
				this.pstm.setString(11,
						langText(this.noticePlace, this.oldCode));
				this.pstm.setString(12, extractCSVPart(this.noticeTag,
						this.noticeAddress, 0));
				this.pstm.setString(13, this.noticePostalCode);
				this.pstm.setString(14, this.noticePostOffice);
				this.pstm.setString(15, this.noticeCountry);
				this.pstm.setString(16, this.noticeEmail);
				// String t1 = langText(this.noticeNoteText,this.oldCode);
				this.pstm.setString(17, langText(this.noticeNoteText,
						this.oldCode));
				this.pstm.setString(18, this.noticeMediaFilename);
				this.pstm.setString(19, langText(this.noticeMediaTitle,
						this.oldCode));
				this.pstm.setString(20, Utils.extractPatronyme(
						this.noticeGivenName, false));
				this.pstm.setString(21, Utils.extractPatronyme(
						this.noticeGivenName, true));
				this.pstm.setString(22, this.noticePrefix);
				this.pstm.setString(23, this.noticeSurname);
				this.pstm.setString(24, this.noticePostfix);
				if (this.noticeSourceId != null) {
					this.pstm.setInt(25, idToInt(this.noticeSourceId)); // langText(this.noticeSourceText,this.oldCode));
				} else {
					this.pstm.setNull(25, Types.INTEGER);
				}

				this.pstm.setString(26, extractCSVPart(this.noticeTag,
						this.noticeAddress, 1));
				this.pstm.setString(27, extractCSVPart(this.noticeTag,
						this.noticeAddress, 2));
				this.pstm.setString(28, extractCSVPart(this.noticeTag,
						this.noticeAddress, 3));

				this.pstm.setString(29, this.noticeSourceText);
				this.pstm.setString(30, this.noticePrivateText);

				this.pstm.setTimestamp(31, toTimestamp(this.noticeCreateDate));

				this.pstm.executeUpdate();

				if (this.placeCollector.size() > 0
						|| this.nameCollector.size() > 0) {
					// StringBuffer sn = new StringBuffer();
					NameArray asn = new NameArray();
					;
					Iterator<String> it = this.nameCollector.keySet()
							.iterator();
					while (it.hasNext()) {
						// if (sn.length() == 0){
						// sn.append("{");
						// } else {
						// sn.append(",");
						// }
						asn.append(it.next());
						// sn.append("\""+it.next()+"\"");
					}

					// sn.append("}");
					NameArray asp = new NameArray();

					// StringBuffer sp = new StringBuffer();
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
					if (this.databaseHasImages) {
						FileInputStream fis;
						logger.fine("Trying image file: " + this.databaseFolder
								+ "/" + this.sukuMediaFolder + "/"
								+ this.noticeMediaFilename);
						File file = new File(this.databaseFolder + "/"
								+ this.sukuMediaFolder + "/"
								+ this.noticeMediaFilename);
						if (file.exists() && file.isFile()) {
							BufferedImage sourceImage = ImageIO.read(file);
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
								logger.warning("Image file "
										+ this.noticeMediaFilename
										+ " not found");
								e.printStackTrace();
							} catch (IOException e) {
								logger.log(Level.WARNING, "Image file "
										+ this.noticeMediaFilename, e);
								e.printStackTrace();
							}

							// int mediaType = sourceImage.getType();
							// System.out.println("kuva: " +
							// this.noticeMediaFilename + " = " + mediaWidth +
							// "/" + mediaHeight );
						} else {
							logger.warning("Image file "
									+ this.noticeMediaFilename + " is missing");
						}
					} else {
						logger.warning("Image folder for "
								+ this.noticeMediaFilename + " is missing");
					}

				}

				if (langus != null && langus.length > 1) {
					for (i = 0; i < langus.length; i++) {
						if (!langus[i].equals(this.oldCode)) {
							this.pstm = this.con
									.prepareStatement(INSERT_UNIT_LANGUAGE);

							this.pstm.setInt(1, id);
							this.pstm.setInt(2, pnid);
							this.pstm.setString(3, this.noticeTag);
							this.pstm.setString(4, toLangCode(langus[i]));

							this.pstm.setString(5, langText(this.noticeType,
									langus[i]));
							this.pstm.setString(6, langText(
									this.noticeDescription, langus[i]));

							this.pstm.setString(7, langText(this.noticePlace,
									langus[i]));
							this.pstm.setString(8, langText(
									this.noticeNoteText, langus[i]));
							this.pstm.setString(9, langText(
									this.noticeMediaTitle, langus[i]));
							// this.pstm.setInt (11,
							// this.langText(this.noticeSourceText,langus[i]));

							this.pstm.executeUpdate();
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
				this.noticeAddress = null;
				this.noticePostOffice = null;
				this.noticePostalCode = null;
				this.noticeCountry = null;
				this.noticeEmail = null;
				this.noticeNoteText = null;
				this.noticeMediaFilename = null;
				this.noticeMediaTitle = null;
				this.noticeGivenName = null;
				this.noticePrefix = null;
				this.noticeSurname = null;
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
			int rid;
			int aid = 0;
			int bid = 0;
			try {
				this.pstm = this.con
						.prepareStatement("select nextval('relationseq')");

				ResultSet rs = this.pstm.executeQuery();
				if (rs.next()) {
					rid = rs.getInt(1);
				} else {
					throw new SAXException("Sequence relationseq error");
				}
				rs.close();

				if (this.relationTag.equals("CHIL")) {

					this.currentStatus = "Chil " + rid;

					this.pstm = this.con.prepareStatement(INSERT_RELATION);

					this.pstm.setInt(1, rid);
					if (this.relationIdA != null) {
						try {
							aid = Integer.parseInt(this.relationIdA
									.substring(1));

							this.pstm.setInt(2, aid);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationIdA "
									+ this.relationIdA + " not numeric");
						}
					}
					this.pstm.setString(3, "FATH");
					l = 0;
					if (this.relationRowA != null) {
						try {
							l = Integer.parseInt(this.relationRowA);
							this.pstm.setInt(4, l);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationRowA "
									+ this.relationRowA + " not numeric");
						}
					}
					this.pstm.setTimestamp(5,
							toTimestamp(this.relationCreateDate));
					try {
						this.pstm.executeUpdate();
					} catch (SQLException se) {
						String err = "Relative aPID = " + aid
								+ " fails for RID = " + rid;
						errorLine.add(err + " [" + se.getMessage() + "]");

						logger.log(Level.WARNING, err, se);

					}

					this.pstm.setInt(1, rid);
					if (this.relationIdB != null) {
						try {
							bid = Integer.parseInt(this.relationIdB
									.substring(1));

							this.pstm.setInt(2, bid);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationIdB "
									+ this.relationIdB + " not numeric");
						}
					}
					this.pstm.setString(3, "CHIL");
					if (this.relationRowB != null) {
						try {
							l = Integer.parseInt(this.relationRowB);
							this.pstm.setInt(4, l);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationRowB "
									+ this.relationRowB + " not numeric");
						}
					}
					this.pstm.setTimestamp(5,
							toTimestamp(this.relationCreateDate));

					try {
						this.pstm.executeUpdate();
					} catch (SQLException se) {
						String err = "Relative bPID = " + bid
								+ " fails for RID = " + rid;
						errorLine.add(err + " [" + se.getMessage() + "]");

						logger.log(Level.WARNING, err, se);
					}

					if (this.relationDescription != null) {

						this.pstm = this.con
								.prepareStatement("select nextval('relationnoticeseq')");
						int rnid = 0;
						rs = this.pstm.executeQuery();
						if (rs.next()) {
							rnid = rs.getInt(1);
						} else {
							throw new SAXException(
									"Sequence relationnoticeseq error");
						}
						rs.close();

						this.pstm = this.con
								.prepareStatement(INSERT_RELATION_ADOPTION);
						this.pstm.setInt(1, rnid);
						this.pstm.setInt(2, rid);
						this.pstm.setString(3, "ADOP");
						this.pstm.setInt(4, 1); // rownumber
						this.pstm.setTimestamp(5,
								toTimestamp(this.relationCreateDate));
						this.pstm.executeUpdate();
					}

				} else {
					this.currentStatus = "Marr " + rid;
					this.pstm = this.con.prepareStatement(INSERT_RELATION);

					this.pstm.setInt(1, rid);
					if (this.relationIdA != null) {
						try {
							aid = Integer.parseInt(this.relationIdA
									.substring(1));
							this.pstm.setInt(2, aid);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationIdA "
									+ this.relationIdA + " not numeric");
						}
					}
					this.pstm.setString(3, "MARR");
					if (this.relationRowA != null) {
						try {
							l = Integer.parseInt(this.relationRowA);
							this.pstm.setInt(4, l);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationRowA "
									+ this.relationRowA + " not numeric");
						}
					}
					this.pstm.setTimestamp(5,
							toTimestamp(this.relationCreateDate));
					try {
						this.pstm.executeUpdate();
					} catch (SQLException se) {
						String err = "Spouse aPID = " + aid
								+ " fails for RID = " + rid;
						errorLine.add(err + " [" + se.getMessage() + "]");
						logger.log(Level.WARNING, err, se);
					}

					this.pstm.setInt(1, rid);
					if (this.relationIdB != null) {
						try {
							bid = Integer.parseInt(this.relationIdB
									.substring(1));
							this.pstm.setInt(2, bid);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationIdB "
									+ this.relationIdB + " not numeric");
						}
					}
					this.pstm.setString(3, "MARR");
					if (this.relationRowB != null) {
						try {
							l = Integer.parseInt(this.relationRowB);
							this.pstm.setInt(4, l);
						} catch (NumberFormatException ne) {
							throw new SAXException("RelationRowB "
									+ this.relationRowB + " not numeric");
						}
					}
					this.pstm.setTimestamp(5,
							toTimestamp(this.relationCreateDate));
					try {
						this.pstm.executeUpdate();
					} catch (SQLException se) {
						String err = "Spouse bPID = " + bid
								+ " fails for RID = " + rid;
						errorLine.add(err + " [" + se.getMessage() + "]");
						logger.log(Level.WARNING, err, se);
					}
					if (this.runner != null) {
						if (this.runner.setRunnerValue("RelationId: " + rid)) {
							throw new SAXException(Resurses
									.getString("SUKU_CANCELLED"));
						}

					}

					if (this.relationBegDateFrom != null
							|| this.relationBegPlace != null
							|| this.relationBegType != null
							|| this.relationDescription != null) {

						StringBuffer allText = new StringBuffer();
						;
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

						this.pstm = this.con
								.prepareStatement("select nextval('relationnoticeseq')");
						int rnid = 0;
						rs = this.pstm.executeQuery();
						if (rs.next()) {
							rnid = rs.getInt(1);
						} else {
							throw new SAXException(
									"Sequence relationnoticeseq error");
						}
						rs.close();

						this.pstm = this.con
								.prepareStatement(INSERT_RELATION_NOTICE);
						this.pstm.setInt(1, rnid);
						this.pstm.setInt(2, rid);
						this.pstm.setString(3, "MARR");
						this.pstm.setInt(4, 1); // rownumber
						this.pstm.setString(5, langText(
								this.relationDescription, this.oldCode));
						this.pstm.setString(6, langText(this.relationBegType,
								this.oldCode));
						this.pstm.setString(7, this.relationBegDatePrefix);
						this.pstm.setString(8, this.relationBegDateFrom);
						this.pstm.setString(9, this.relationBegDateTo);
						this.pstm.setString(10, langText(this.relationBegPlace,
								this.oldCode));
						this.pstm.setString(11, langText(this.relationNoteText,
								this.oldCode));
						if (this.relationSourceId != null) {
							this.pstm
									.setInt(12, idToInt(this.relationSourceId)); // langText(this.noticeSourceText,this.oldCode));
						} else {
							this.pstm.setNull(12, Types.INTEGER);
						}

						this.pstm.setString(13, this.relationSourceText);
						this.pstm.setString(14, this.relationPrivateText);
						this.pstm.setTimestamp(15,
								toTimestamp(this.relationCreateDate));
						this.pstm.executeUpdate();
						laskuriRelations++;

						if (langus != null && langus.length > 1) {
							for (i = 0; i < langus.length; i++) {
								if (!langus[i].equals(this.oldCode)) {
									// this.pstm =
									// this.con.prepareStatement("select nextval('relationseq')");
									//									
									// rs = this.pstm.executeQuery();
									// if (rs.next()){
									// rid = rs.getInt(1);
									// } else {
									// throw new SAXException
									// ("Sequence relationseq error");
									// }
									// rs.close();

									this.pstm = this.con
											.prepareStatement(INSERT_RELATION_LANGUAGE);
									this.pstm.setInt(1, rnid);
									this.pstm.setInt(2, rid);
									this.pstm.setString(3,
											toLangCode(langus[i]));
									this.pstm.setString(4, langText(
											this.relationBegType, langus[i]));
									this.pstm.setString(5,
											langText(this.relationDescription,
													langus[i]));
									this.pstm.setString(6, langText(
											this.relationBegPlace, langus[i]));
									this.pstm.setString(7, langText(
											this.relationNoteText, langus[i]));
									// this.pstm.setString(9,
									// langText(this.relationSourceText,langus[i]));

									this.pstm.executeUpdate();
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

						this.pstm = this.con
								.prepareStatement("select nextval('relationnoticeseq')");
						int rnid = 0;
						rs = this.pstm.executeQuery();
						if (rs.next()) {
							rnid = rs.getInt(1);
						} else {
							throw new SAXException(
									"Sequence relationnoticeseq error");
						}
						rs.close();

						this.pstm = this.con
								.prepareStatement(INSERT_RELATION_NOTICE);
						this.pstm.setInt(1, rnid);
						this.pstm.setInt(2, rid);
						this.pstm.setString(3, "DIV");
						this.pstm.setInt(4, 2); // rownumber
						this.pstm.setString(5, null);
						this.pstm.setString(6, this.relationEndType);
						this.pstm.setString(7, this.relationEndDatePrefix);
						this.pstm.setString(8, this.relationEndDateFrom);
						this.pstm.setString(9, this.relationEndDateTo);
						this.pstm.setString(10, this.relationEndPlace);
						this.pstm.setString(11, null); // note text
						this.pstm.setNull(12, Types.INTEGER);
						this.pstm.setNull(13, Types.VARCHAR);
						this.pstm.setNull(14, Types.VARCHAR);

						this.pstm.setTimestamp(15,
								toTimestamp(this.relationCreateDate));
						this.pstm.executeUpdate();

						if (langus != null && langus.length > 1) {
							for (i = 0; i < langus.length; i++) {
								if (!langus[i].equals(this.oldCode)) {
									this.pstm = this.con
											.prepareStatement("select nextval('relationseq')");

									rs = this.pstm.executeQuery();
									if (rs.next()) {
										rid = rs.getInt(1);
									} else {
										throw new SAXException(
												"Sequence relationseq error");
									}
									rs.close();

									this.pstm = this.con
											.prepareStatement(INSERT_RELATION_LANGUAGE);
									this.pstm.setInt(1, rnid);

									this.pstm.setString(2,
											toLangCode(langus[i]));

									this.pstm.setString(3, langText(
											this.relationEndType, langus[i]));
									this.pstm.setString(4, null);
									this.pstm.setString(5, langText(
											this.relationEndPlace, langus[i]));
									this.pstm.setString(6, null);

									this.pstm.executeUpdate();
								}
							}
						}
					}
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "importing relation failed", e);
				throw new SAXException(e);
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

					this.pstm = this.con.prepareStatement(INSERT_SOURCES);
					this.pstm.setInt(1, sid);
					this.pstm.setString(2, this.sourceNoteText);

					this.pstm.executeUpdate();
					if (this.runner != null) {

						if (this.runner.setRunnerValue("SourceId: " + sid)) {
							throw new SAXException(Resurses
									.getString("SUKU_CANCELLED"));
						}
					}

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

				this.pstm = this.con.prepareStatement(INSERT_GROUP);
				this.pstm.setString(1, this.groupId);
				this.pstm.setString(2, this.groupName);
				this.pstm.setString(3, this.groupDescription);
				this.pstm.executeUpdate();
				if (this.runner != null) {
					if (this.runner.setRunnerValue("GroupId: " + this.groupId)) {
						throw new SAXException(Resurses
								.getString("SUKU_CANCELLED"));
					}
				}
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

				this.pstm = this.con.prepareStatement(INSERT_SUKU_VARIABLES);
				this.pstm.setString(1, this.sukuName);
				this.pstm.setString(2, this.sukuInfo);
				this.pstm.setString(3, this.sukuAddress);
				this.pstm.setString(4, this.sukuPostalCode);
				this.pstm.setString(5, this.sukuPostOffice);
				this.pstm.setString(6, this.sukuCountry);
				this.pstm.setString(7, this.sukuEmail);
				this.pstm.executeUpdate();

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

		if (this.currentEle.equals(conversionsTG)) {
			if (this.conversionsFrom != null && this.conversionsLang != null
					&& this.conversionsRule != null
					&& this.conversionsTo != null) {

				try {
					this.pstm = this.con.prepareStatement(INSERT_CONVERSION);
					this.pstm.setString(1, this.conversionsFrom);
					this.pstm.setString(2, this.conversionsLang);
					this.pstm.setString(3, this.conversionsRule);
					this.pstm.setString(4, this.conversionsTo);
					this.pstm.executeUpdate();
					laskuriConversion++;
					if (this.runner != null) {
						if (this.runner.setRunnerValue("ConversionId: "
								+ laskuriConversion)) {
							throw new SAXException(Resurses
									.getString("SUKU_CANCELLED"));
						}
					}
				} catch (SQLException e) {
					logger.log(Level.SEVERE, "importing conversion failed", e);
					throw new SAXException(e);

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

					this.pstm = this.con.prepareStatement(INSERT_VIEW);

					this.pstm.setInt(1, vid);
					this.pstm.setString(2, this.viewName);
					this.pstm.setTimestamp(3, toTimestamp(this.viewCreateDate));
					this.pstm.executeUpdate();
					laskuriViews++;

					if (this.runner != null) {
						if (this.runner.setRunnerValue("ViewId: " + vid)) {
							throw new SAXException(Resurses
									.getString("SUKU_CANCELLED"));
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

					this.pstm = this.con.prepareStatement(INSERT_VIEW_UNIT);

					this.pstm.setInt(1, vid);
					this.pstm.setInt(2, vpid);

					this.pstm.executeUpdate();

				} catch (SQLException e) {
					logger.log(Level.SEVERE, "importing viewunits failed", e);
					throw new SAXException(e);
				}
			}

			this.viewUnitPid = null;
		}

		if (this.currentEle.endsWith(qName)) {
			l = this.currentEle.length();
			this.currentEle = this.currentEle.substring(0, l - qName.length()
					- 1);
		} else {
			System.out.println("BAD XML: " + this.currentEle + "/" + qName);
			return;
		}

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
		StringBuffer sb = new StringBuffer();

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
		sb = new StringBuffer();

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
					StringBuffer tnimi = new StringBuffer();
					String etu = null, suku = null;
					StringBuffer tuloste = new StringBuffer();
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
							this.nameCollector.put(tnimi.toString(), tnimi
									.toString());
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

	private Timestamp toTimestamp(String date) {
		if (date == null)
			return null;
		java.util.Date dd;
		try {
			dd = cdf.parse(date);
		} catch (ParseException e) {

			dd = new java.util.Date();
		}

		Timestamp tms = new Timestamp(dd.getTime());
		return tms;
	}

}
