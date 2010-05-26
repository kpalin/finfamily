package fi.kaila.suku.server.utils;

import java.io.ByteArrayOutputStream;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

			createNoticesElement(document, unitEle, pid);

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

			double prossa = unitCounter / dbSize;
			int prose = (int) (prossa * 100);
			this.runner.setRunnerValue("" + prose + ";baskup");

		}
		rs.close();
		stm.close();

	}

	private String firstPrefix = null;
	private String firstGivenname = null;
	private String firstSurname = null;
	private String firstPostfix = null;

	private void createNoticesElement(Document document, Element rootElement,
			int pid) throws SQLException {
		firstPrefix = null;
		firstGivenname = null;
		firstSurname = null;
		firstPostfix = null;
		boolean isFirstname = true;
		Element noticesEle = document.createElement("notices");
		rootElement.appendChild(noticesEle);
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
			// /////////////////TODO Add here the media stuff

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

			// TODO add here the refnames and refplaces stuff

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

		}

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
