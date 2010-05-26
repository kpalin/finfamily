package fi.kaila.suku.server.utils;

import java.io.ByteArrayOutputStream;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fi.kaila.suku.exports.ExportFamilyDatabaseDialog;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Creator of backup of Family database
 * 
 * Backup is an xml file with joining images all packed in a zip-file
 * 
 * @author Kaarle Kaila
 * 
 */
public class ExportBackupUtil {

	private Connection con = null;

	private double dbSize = 0;
	private ExportFamilyDatabaseDialog runner = null;

	/**
	 * Constructor requires an open connection
	 * 
	 * @param con
	 */
	public ExportBackupUtil(Connection con) {
		this.con = con;
		this.runner = ExportFamilyDatabaseDialog.getRunner();
	}

	/**
	 * Method to execute building of backup
	 * 
	 * SukuData buffer will contain the backup if success SukuData resu will
	 * contain error info if failed
	 * 
	 * @return results
	 */
	public SukuData exportBackup() {
		SukuData dat = new SukuData();
		String root = "genealog";
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		try {
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element rootElement = document.createElement(root);
			document.appendChild(rootElement);

			createOwnerElement(document, rootElement);

			createUnitsElement(document, rootElement);

			createRelationsElement(document, rootElement);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			StreamResult result = new StreamResult(bos);
			transformer.transform(source, result);

			dat.buffer = bos.toByteArray();

			// dat.resu = "Under construction";
		} catch (ParserConfigurationException e) {
			dat.resu = e.getMessage();
			e.printStackTrace();
		} catch (TransformerException e) {
			dat.resu = e.getMessage();
			e.printStackTrace();
		} catch (SQLException e) {
			dat.resu = e.getMessage();
			e.printStackTrace();
		}

		return dat;

	}

	private void createUnitsElement(Document document, Element rootElement)
			throws SQLException {
		Element unitsEle = document.createElement("units");
		rootElement.appendChild(unitsEle);

		String sql = "select count(*) from unit";
		Statement stm = con.createStatement();
		ResultSet rs = stm.executeQuery(sql);
		if (rs.next()) {
			dbSize = rs.getInt(1);
		}
		rs.close();

		sql = "select * from unit order by pid";

		stm = con.createStatement();
		rs = stm.executeQuery(sql);

		Element ele;
		double unitCounter = 0;
		while (rs.next()) {
			unitCounter++;
			int pid = rs.getInt("pid");

			Element unitEle = document.createElement("unit");
			unitsEle.appendChild(unitEle);
			unitEle.setAttribute("unitid", "I" + pid);
			unitEle.setAttribute("tag", rs.getString("tag"));

			unitEle.setAttribute("sex", rs.getString("sex"));

			String tmp = rs.getString("groupid");
			if (tmp != null) {
				unitEle.setAttribute("group", tmp);
			}

			String created = rs.getString("createdate");
			unitEle.setAttribute("createdate", created.substring(0, 10));

			String modified = rs.getString("modified");
			if (modified != null) {
				unitEle.setAttribute("modified", modified);
			}

			tmp = rs.getString("userrefn");
			if (tmp != null) {
				ele = document.createElement("userrefn");
				ele.setTextContent(tmp);
				unitEle.appendChild(ele);
			}

			tmp = rs.getString("sourcetext");
			if (tmp != null) {
				ele = document.createElement("source");
				ele.setTextContent(tmp);
				unitEle.appendChild(ele);
			}
			tmp = rs.getString("privatetext");
			if (tmp != null) {
				ele = document.createElement("privatetext");
				ele.setTextContent(tmp);
				unitEle.appendChild(ele);
			}

			Element nameEle = document.createElement("name");

			unitEle.appendChild(nameEle);

			Element notices = createNoticesElement(document, pid);

			if (firstPrefix != null) {
				ele = document.createElement("prefix");
				ele.setTextContent(firstPrefix);
				nameEle.appendChild(ele);
			}
			if (firstGivenname != null) {
				ele = document.createElement("givenname");
				ele.setTextContent(firstGivenname);
				nameEle.appendChild(ele);
			}
			if (firstSurname != null) {
				ele = document.createElement("surname");
				ele.setTextContent(firstSurname);
				nameEle.appendChild(ele);
			}
			if (firstPostfix != null) {
				ele = document.createElement("postfix");
				ele.setTextContent(firstPostfix);
				nameEle.appendChild(ele);
			}

			unitEle.appendChild(nameEle);

			unitEle.appendChild(notices);
			double prossa = unitCounter / dbSize;
			int prose = (int) (prossa * 100);
			this.runner.setRunnerValue("" + prose + ";unit");

		}
		rs.close();
		stm.close();

	}

	private void createRelationsElement(Document document, Element rootElement)
			throws SQLException {
		Element relsEle = document.createElement("relations");
		rootElement.appendChild(relsEle);
		String sql = "select count(*) from relation";
		Statement stm = con.createStatement();
		ResultSet rs = stm.executeQuery(sql);

		if (rs.next()) {
			dbSize = rs.getInt(1);

		}
		rs.close();

		sql = "select a.rid,a.pid,b.pid,a.tag,b.tag,a.surety,"
				+ "a.relationrow,b.relationrow,a.createdate "
				+ "from relation as a inner join relation as b on a.rid=b.rid "
				+ "where a.pid <> b.pid  order by a.rid";

		stm = con.createStatement();
		rs = stm.executeQuery(sql);

		Element ele;
		double relaCounter = 0;
		int prevRid = 0;
		while (rs.next()) {
			relaCounter++;
			int rid = rs.getInt(1);
			if (rid == prevRid) {
				continue;
			}

			prevRid = rid;
			int apid = rs.getInt(2);
			int bpid = rs.getInt(3);
			String atag = rs.getString(4);
			String btag = rs.getString(5);
			int surety = rs.getInt(6);
			int arow = rs.getInt(7);
			int brow = rs.getInt(8);
			String created = rs.getString(9);
			int xpid = 0;
			int ypid = 0;
			String xtag = null;
			String axtag = null;
			String bxtag = null;
			int xrow = 0;
			int yrow = 0;
			if (atag.equals("WIFE")) {
				xpid = apid;
				ypid = bpid;
				xtag = "MARR";
				axtag = atag;
				bxtag = btag;
				xrow = arow;
				yrow = brow;

			} else if (atag.equals("HUSB")) {
				xpid = bpid;
				ypid = apid;
				xtag = "MARR";
				axtag = btag;
				bxtag = atag;
				xrow = brow;
				yrow = arow;
			} else if (atag.equals("CHIL")) {
				xpid = bpid;
				ypid = apid;
				xtag = "CHIL";
				axtag = btag;
				bxtag = atag;
				xrow = brow;
				yrow = arow;
			} else if (atag.equals("FATH") || atag.equals("MOTH")) {
				xpid = apid;
				ypid = bpid;
				xtag = "CHIL";
				axtag = atag;
				bxtag = btag;
				xrow = arow;
				yrow = brow;
			} else {
				xpid = apid;
				ypid = bpid;
				xtag = null;
				axtag = atag;
				bxtag = btag;
				xrow = arow;
				yrow = brow;
			}
			Element relEle = document.createElement("relation");
			relsEle.appendChild(relEle);
			relEle.setAttribute("unitida", "I" + xpid);
			relEle.setAttribute("unitidb", "I" + ypid);
			if (xtag != null) {
				relEle.setAttribute("tag", xtag);
			}
			relEle.setAttribute("atag", axtag);
			relEle.setAttribute("btag", bxtag);
			relEle.setAttribute("rowa", "" + xrow);
			relEle.setAttribute("rowb", "" + yrow);
			relEle.setAttribute("surety", "" + surety);
			relEle.setAttribute("createdate", created.substring(1, 10));

			double prossa = relaCounter / dbSize;
			int prose = (int) (prossa * 100);
			if (prose > 100)
				prose = 100;
			this.runner.setRunnerValue("" + prose + ";relation");
			createRelationNoticesElement(document, relEle, rid);

			if (beginDesc != null) {

				ele = document.createElement("description");
				relEle.appendChild(ele);
				ele.setTextContent(beginDesc);

			}
			if (beginType != null) {

				ele = document.createElement("begintype");
				relEle.appendChild(ele);
				ele.setTextContent(beginType);

			}

			if (beginStart != null) {
				Element ddEle = document.createElement("begindate");
				relEle.appendChild(ddEle);
				if (beginDateType != null) {
					ddEle.setAttribute("type", beginDateType);
				}
				ele = document.createElement("start");
				relEle.appendChild(ele);
				ele.setTextContent(beginStart);
				if (beginEnd != null) {
					ele = document.createElement("end");
					relEle.appendChild(ele);
					ele.setTextContent(beginEnd);
				}
			}
			if (beginPlace != null) {
				ele = document.createElement("beginplace");
				relEle.appendChild(ele);
				ele.setTextContent(beginPlace);
			}
			if (beginNote != null) {
				ele = document.createElement("notetext");
				relEle.appendChild(ele);
				ele.setTextContent(beginNote);
			}
			if (beginSource != null) {
				ele = document.createElement("sourcetext");
				relEle.appendChild(ele);
				ele.setTextContent(beginSource);
			}
			if (beginPrivate != null) {
				ele = document.createElement("privatetext");
				relEle.appendChild(ele);
				ele.setTextContent(beginPrivate);
			}
			if (endStart != null) {
				Element ddEle = document.createElement("enddate");
				relEle.appendChild(ddEle);
				if (endDateType != null) {
					ddEle.setAttribute("type", endDateType);
				}
				ele = document.createElement("start");
				relEle.appendChild(ele);
				ele.setTextContent(endStart);
				if (endEnd != null) {
					ele = document.createElement("end");
					relEle.appendChild(ele);
					ele.setTextContent(endEnd);
				}
			}
			if (endPlace != null) {
				ele = document.createElement("endplace");
				relEle.appendChild(ele);
				ele.setTextContent(beginPlace);
			}

		}
	}

	private String beginDesc = null;
	private String beginType = null;

	private String beginDateType = null;
	private String beginStart = null;
	private String beginEnd = null;
	private String beginPlace = null;
	private String beginNote = null;
	private String endDateType = null;
	private String endStart = null;
	private String endEnd = null;
	private String endPlace = null;
	private String beginSource = null;
	private String beginPrivate = null;

	private void createRelationNoticesElement(Document document,
			Element rootElement, int rid) throws SQLException {

		beginType = null;
		beginDesc = null;

		beginDateType = null;
		beginStart = null;
		beginEnd = null;
		beginPlace = null;
		beginNote = null;
		endDateType = null;
		endStart = null;
		endEnd = null;
		endPlace = null;

		beginSource = null;
		beginPrivate = null;

		String sql = "select * from relationnotice where rid=? order by noticerow";

		PreparedStatement pstm = con.prepareStatement(sql);
		pstm.setInt(1, rid);
		ResultSet rs = pstm.executeQuery();
		Element ele;
		while (rs.next()) {
			int rnid = rs.getInt("rnid");
			int nrow = rs.getInt("noticerow");
			String tag = rs.getString("tag");
			int surety = rs.getInt("surety");
			String desc = rs.getString("description");
			String rtype = rs.getString("relationtype");
			String dprefix = rs.getString("dateprefix");
			String fromdate = rs.getString("fromdate");
			String todate = rs.getString("todate");
			String place = rs.getString("place");
			String notetext = rs.getString("notetext");
			String sourcetext = rs.getString("sourcetext");
			String privatetext = rs.getString("privatetext");
			String modified = rs.getString("modified");
			String created = rs.getString("createdate");

			Element nEle = document.createElement("relationnotice");
			rootElement.appendChild(nEle);

			nEle.setAttribute("tag", tag);
			nEle.setAttribute("row", "" + nrow);
			nEle.setAttribute("surety", "" + surety);
			if (modified != null) {
				nEle.setAttribute("modified", modified);
			}
			nEle.setAttribute("createdate", created.substring(0, 10));
			if (rtype != null) {
				nEle.setAttribute("relationtype", rtype);
			}
			if (desc != null) {
				nEle.setAttribute("description", desc);
			}
			if (fromdate != null) {
				Element dEle = document.createElement("date");
				nEle.appendChild(dEle);
				if (dprefix != null) {
					dEle.setAttribute("type", dprefix);
				}
				ele = document.createElement("start");
				ele.setTextContent(fromdate);
				dEle.appendChild(ele);
				if (todate != null) {
					ele = document.createElement("end");
					ele.setTextContent(fromdate);
					dEle.appendChild(ele);
				}
			}
			if (place != null) {
				nEle.setAttribute("place", place);
			}
			if (notetext != null) {
				ele = document.createElement("notetext");
				ele.setTextContent(notetext);
				nEle.appendChild(ele);

			}
			if (sourcetext != null) {
				ele = document.createElement("sourcetext");
				ele.setTextContent(sourcetext);
				nEle.appendChild(ele);

			}
			if (privatetext != null) {
				ele = document.createElement("privatetext");
				ele.setTextContent(privatetext);
				nEle.appendChild(ele);

			}
			if (tag.equals("MARR") || tag.equals("ADOP")) {
				beginType = rtype;
				beginDesc = desc;

				beginDateType = dprefix;
				beginStart = fromdate;
				beginEnd = todate;
				beginPlace = place;
				beginNote = notetext;
				beginSource = sourcetext;
				beginPrivate = privatetext;
			} else if (tag.equals("DIV")) {
				endDateType = dprefix;
				endStart = fromdate;
				endEnd = todate;
				endPlace = place;
			}

			createRelationLanguageElements(document, nEle, rnid);

		}

	}

	private String firstPrefix = null;
	private String firstGivenname = null;
	private String firstSurname = null;
	private String firstPostfix = null;

	private Element createNoticesElement(Document document, int pid)
			throws SQLException {
		firstPrefix = null;
		firstGivenname = null;
		firstSurname = null;
		firstPostfix = null;
		boolean isFirstname = true;
		Element noticesEle = document.createElement("notices");

		String sql = "select * from unitnotice where pid = ? order by noticerow";

		PreparedStatement pstm = con.prepareStatement(sql);
		pstm.setInt(1, pid);
		ResultSet rs = pstm.executeQuery();
		String aux;
		Element ele;

		String tag = null;
		while (rs.next()) {
			Element noticeEle = document.createElement("notice");
			noticesEle.appendChild(noticeEle);
			int pnid = rs.getInt("pnid");
			tag = rs.getString("tag");
			noticeEle.setAttribute("tag", tag);

			noticeEle.setAttribute("row", "" + rs.getInt("noticerow"));
			noticeEle.setAttribute("surety", "" + rs.getInt("surety"));
			String tmp = rs.getString("privacy");
			if (tmp != null) {
				noticeEle.setAttribute("privacy", tmp);
			}

			String created = rs.getString("createdate");
			noticeEle.setAttribute("createdate", created.substring(0, 10));

			String modified = rs.getString("modified");
			if (modified != null) {
				noticeEle.setAttribute("modified", modified);
			}
			tmp = rs.getString("noticetype");
			if (tmp != null) {
				ele = document.createElement("noticetype");
				ele.setTextContent(tmp);
				noticeEle.appendChild(ele);
			}
			tmp = rs.getString("description");
			if (tmp != null) {
				ele = document.createElement("description");
				ele.setTextContent(tmp);
				noticeEle.appendChild(ele);
			}

			tmp = rs.getString("fromdate");
			if (tmp != null) {
				Element dateEle = document.createElement("date");
				noticeEle.appendChild(dateEle);
				aux = rs.getString("dateprefix");
				if (aux != null) {
					dateEle.setAttribute("type", aux);
				}
				ele = document.createElement("start");
				dateEle.appendChild(ele);
				ele.setTextContent(tmp);

				tmp = rs.getString("todate");
				if (tmp != null) {
					ele = document.createElement("end");
					dateEle.appendChild(ele);
					ele.setTextContent(tmp);
				}

			}

			tmp = rs.getString("place");
			if (tmp != null) {
				ele = document.createElement("place");
				ele.setTextContent(tmp);
				noticeEle.appendChild(ele);
			}
			tmp = rs.getString("village");
			if (tmp != null) {
				ele = document.createElement("village");
				ele.setTextContent(tmp);
				noticeEle.appendChild(ele);
			}
			tmp = rs.getString("farm");
			if (tmp != null) {
				ele = document.createElement("farm");
				ele.setTextContent(tmp);
				noticeEle.appendChild(ele);
			}
			tmp = rs.getString("croft");
			if (tmp != null) {
				ele = document.createElement("croft");
				ele.setTextContent(tmp);
				noticeEle.appendChild(ele);
			}

			String address = rs.getString("address");
			String postoff = rs.getString("postoffice");
			String postcode = rs.getString("postalcode");
			String state = rs.getString("state");
			String country = rs.getString("country");

			String email = rs.getString("email");

			if (address != null || postoff != null || postcode != null
					|| state != null || country != null || email != null) {
				Element addEle = document.createElement("address");
				noticeEle.appendChild(addEle);
				if (address != null) {
					ele = document.createElement("street");
					ele.setTextContent(address);
					noticeEle.appendChild(ele);
				}
				if (postoff != null) {
					ele = document.createElement("postoffice");
					ele.setTextContent(postoff);
					noticeEle.appendChild(ele);
				}
				if (postcode != null) {
					ele = document.createElement("postalcode");
					ele.setTextContent(postcode);
					noticeEle.appendChild(ele);
				}
				if (state != null) {
					ele = document.createElement("state");
					ele.setTextContent(state);
					noticeEle.appendChild(ele);
				}
				if (country != null) {
					ele = document.createElement("country");
					ele.setTextContent(country);
					noticeEle.appendChild(ele);
				}
				if (email != null) {
					ele = document.createElement("email");
					ele.setTextContent(email);
					noticeEle.appendChild(ele);
				}

			}

			tmp = rs.getString("notetext");
			if (tmp != null) {
				ele = document.createElement("notetext");
				ele.setTextContent(tmp);
				noticeEle.appendChild(ele);
			}
			String mediaFilename = rs.getString("mediafilename");
			String mediaTitle = rs.getString("mediatitle");

			if (mediaFilename != null || mediaTitle != null) {
				Element mediaEle = document.createElement("media");

				noticeEle.appendChild(mediaEle);
				if (mediaFilename != null) {
					ele = document.createElement("mediafilename");
					ele.setTextContent(mediaFilename);
					noticeEle.appendChild(ele);
				}
				if (mediaTitle != null) {
					ele = document.createElement("mediatitle");
					ele.setTextContent(mediaTitle);
					noticeEle.appendChild(ele);

					// TODO add here the image stuff

				}
			}

			if (tag.equals("NAME")) {
				Element nameEle = document.createElement("name");
				noticeEle.appendChild(nameEle);
				tmp = rs.getString("prefix");
				if (tmp != null) {
					if (isFirstname)
						firstPrefix = tmp;
					ele = document.createElement("prefix");
					ele.setTextContent(tmp);
					nameEle.appendChild(ele);
				}
				tmp = rs.getString("givenname");
				if (tmp != null) {
					if (isFirstname)
						firstGivenname = tmp;
					ele = document.createElement("givenname");
					ele.setTextContent(tmp);
					nameEle.appendChild(ele);
				}
				tmp = rs.getString("patronym");
				if (tmp != null) {
					if (isFirstname) {
						if (firstGivenname != null)
							firstGivenname += " " + tmp;
						else
							firstGivenname = tmp;
					}
					ele = document.createElement("patronym");
					ele.setTextContent(tmp);
					nameEle.appendChild(ele);
				}
				tmp = rs.getString("surname");
				if (tmp != null) {
					if (isFirstname)
						firstSurname = tmp;
					ele = document.createElement("surname");
					ele.setTextContent(tmp);
					nameEle.appendChild(ele);
				}
				tmp = rs.getString("postfix");
				if (tmp != null) {
					if (isFirstname)
						firstPostfix = tmp;
					ele = document.createElement("postfix");
					ele.setTextContent(tmp);
					nameEle.appendChild(ele);
				}

				isFirstname = false;
			}
			String[] refNames = null;
			String[] refPlaces = null;
			Array xx = rs.getArray("refnames");
			if (xx != null) {
				refNames = (String[]) xx.getArray();

			}
			if (refNames != null) {
				Element namesEle = document.createElement("namelist");
				noticeEle.appendChild(namesEle);
				for (int i = 0; i < refNames.length; i++) {
					ele = document.createElement("name");
					ele.setTextContent(refNames[i]);
					namesEle.appendChild(ele);
				}
			}

			xx = rs.getArray("refplaces");
			if (xx != null) {
				refPlaces = (String[]) xx.getArray();

			}
			if (refNames != null) {
				Element placesEle = document.createElement("placelist");
				noticeEle.appendChild(placesEle);
				for (int i = 0; i < refNames.length; i++) {
					ele = document.createElement("place");
					ele.setTextContent(refPlaces[i]);
					placesEle.appendChild(ele);
				}
			}

			tmp = rs.getString("sourcetext");
			if (tmp != null) {
				ele = document.createElement("source");
				ele.setTextContent(tmp);
				noticeEle.appendChild(ele);
			}
			tmp = rs.getString("privatetext");
			if (tmp != null) {
				ele = document.createElement("privatetext");
				ele.setTextContent(tmp);
				noticeEle.appendChild(ele);
			}
			createUnitLanguageElements(document, noticeEle, pnid);

		}
		return noticesEle;
	}

	private void createUnitLanguageElements(Document document,
			Element rootElement, int pnid) throws SQLException {
		String sql = "select * from unitlanguage where pnid=?";
		PreparedStatement pstm = con.prepareStatement(sql);
		pstm.setInt(1, pnid);
		ResultSet rs = pstm.executeQuery();
		Element ele;
		while (rs.next()) {
			String tag = rs.getString("tag");
			String langCode = rs.getString("langcode");
			String nType = rs.getString("noticetype");
			String desc = rs.getString("description");
			String place = rs.getString("place");
			String notetext = rs.getString("notetext");
			String mediatitle = rs.getString("mediatitle");
			String modified = rs.getString("modified");
			String created = rs.getString("createdate");

			Element langEle = document.createElement("language");
			rootElement.appendChild(langEle);
			langEle.setAttribute("tag", tag);
			langEle.setAttribute("langcode", langCode);
			if (nType != null) {
				langEle.setAttribute("noticetype", nType);
			}
			if (desc != null) {
				langEle.setAttribute("description", desc);
			}
			if (place != null) {
				ele = document.createElement("place");
				ele.setTextContent(place);
				langEle.appendChild(ele);
			}
			if (notetext != null) {
				ele = document.createElement("notetext");
				ele.setTextContent(notetext);
				langEle.appendChild(ele);
			}
			if (mediatitle != null) {
				ele = document.createElement("mediatitle");
				ele.setTextContent(mediatitle);
				langEle.appendChild(ele);
			}
			if (modified != null) {
				ele = document.createElement("modified");
				ele.setTextContent(modified);
				langEle.appendChild(ele);
			}

			ele = document.createElement("createdate");
			ele.setTextContent(created);
			langEle.appendChild(ele);

		}
		rs.close();
		pstm.close();
	}

	private void createRelationLanguageElements(Document document,
			Element rootElement, int rnid) throws SQLException {
		String sql = "select * from relationlanguage where rnid=?";
		PreparedStatement pstm = con.prepareStatement(sql);
		pstm.setInt(1, rnid);
		ResultSet rs = pstm.executeQuery();
		Element ele;
		while (rs.next()) {

			String langCode = rs.getString("langcode");
			String nType = rs.getString("relationtype");
			String desc = rs.getString("description");
			String place = rs.getString("place");
			String notetext = rs.getString("notetext");
			String modified = rs.getString("modified");
			String created = rs.getString("createdate");

			Element langEle = document.createElement("language");
			rootElement.appendChild(langEle);

			langEle.setAttribute("langcode", langCode);
			if (nType != null) {
				langEle.setAttribute("noticetype", nType);
			}
			if (desc != null) {
				langEle.setAttribute("description", desc);
			}
			if (place != null) {
				ele = document.createElement("place");
				ele.setTextContent(place);
				langEle.appendChild(ele);
			}
			if (notetext != null) {
				ele = document.createElement("notetext");
				ele.setTextContent(notetext);
				langEle.appendChild(ele);
			}

			if (modified != null) {
				ele = document.createElement("modified");
				ele.setTextContent(modified);
				langEle.appendChild(ele);
			}

			ele = document.createElement("createdate");
			ele.setTextContent(created);
			langEle.appendChild(ele);

		}
		rs.close();
		pstm.close();
	}

	private void createOwnerElement(Document document, Element rootElement)
			throws SQLException {
		String sql = "select * from sukuvariables";

		Statement stm = con.createStatement();
		ResultSet rs = stm.executeQuery(sql);
		Element ele;
		while (rs.next()) {
			Element ownerEle = document.createElement("owner");
			rootElement.appendChild(ownerEle);
			String tmp = rs.getString("owner_name");
			if (tmp != null) {
				ele = document.createElement("ownername");
				ele.setTextContent(tmp);
				ownerEle.appendChild(ele);
			}

			tmp = rs.getString("owner_info");
			if (tmp != null) {
				ele = document.createElement("ownerinfo");
				ele.setTextContent(tmp);
				ownerEle.appendChild(ele);
			}

			Element addressEle = document.createElement("address");
			boolean hasAddress = false;

			tmp = rs.getString("owner_address");
			if (tmp != null) {
				ele = document.createElement("street");
				ele.setTextContent(tmp);
				addressEle.appendChild(ele);
				hasAddress = true;
			}
			tmp = rs.getString("owner_postalcode");
			if (tmp != null) {
				ele = document.createElement("postalcode");
				ele.setTextContent(tmp);
				addressEle.appendChild(ele);
				hasAddress = true;
			}

			tmp = rs.getString("owner_postoffice");
			if (tmp != null) {
				ele = document.createElement("postoffice");
				ele.setTextContent(tmp);
				addressEle.appendChild(ele);
				hasAddress = true;
			}
			tmp = rs.getString("owner_state");
			if (tmp != null) {
				ele = document.createElement("state");
				ele.setTextContent(tmp);
				addressEle.appendChild(ele);
				hasAddress = true;
			}
			tmp = rs.getString("owner_country");
			if (tmp != null) {
				ele = document.createElement("country");
				ele.setTextContent(tmp);
				addressEle.appendChild(ele);
				hasAddress = true;
			}
			tmp = rs.getString("owner_email");
			if (tmp != null) {
				ele = document.createElement("email");
				ele.setTextContent(tmp);
				addressEle.appendChild(ele);
				hasAddress = true;
			}
			tmp = rs.getString("owner_webaddress");
			if (tmp != null) {
				ele = document.createElement("webaddress");
				ele.setTextContent(tmp);
				addressEle.appendChild(ele);
				hasAddress = true;
			}
			if (hasAddress) {
				ownerEle.appendChild(addressEle);
			}
		}
		rs.close();
		stm.close();
	}

}
