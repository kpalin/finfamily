package fi.kaila.suku.report;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.util.SukuException;

public class XmlReport implements ReportInterface {

	private String translator = null;
	private String report = null;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private boolean reportClosed = false;

	public XmlReport(int translatorIdx) {

		switch (translatorIdx) {
		case 1:
			translator = "resources/xml/docx.xsl";
			report = "report.xml";
			break;
		case 2:
			translator = "resources/xml/html.xsl";
			report = "report.html";
			break;
		default:
			translator = null;
			report = "report.xml";
			break;

		}
	}

	@Override
	public void addText(BodyText bt) {
		Element ele;
		Element tele;

		ele = doc.createElement("chapter");
		String style = bt.getClass().getName();
		int lastDot = style.lastIndexOf(".");
		ele.setAttribute("style", style.substring(lastDot + 1));
		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < text.getCount(); i++) {
		// String tmp = text.getText(i);
		// sb.append(tmp);
		// }
		// ele.setTextContent(sb.toString());
		String prevStyle = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bt.getCount(); i++) {
			String value = bt.getText(i);
			String currStyle;
			if (value != null && value != "") {
				if (bt.isBold(i) && bt.isUnderline(i)) {
					currStyle = "bu";
				} else if (bt.isBold(i)) {
					currStyle = "b";
				} else if (bt.isUnderline(i)) {
					currStyle = "ul";
				} else {
					currStyle = "n";
				}
				if (!currStyle.equals(prevStyle)) {
					if (sb.length() > 0) {
						tele = doc.createElement(prevStyle);
						tele.setTextContent(sb.toString());
						ele.appendChild(tele);

						sb = new StringBuffer();
					}
					prevStyle = currStyle;

				}

				sb.append(bt.getText(i));

				// if (text.isBold(i) && text.isUnderline(i)) {
				// tele = doc.createElement("bu");
				// tele.setTextContent(text.getText(i));
				// ele.appendChild(tele);
				//
				// } else if (text.isBold(i)) {
				// tele = doc.createElement("b");
				// tele.setTextContent(text.getText(i));
				// ele.appendChild(tele);
				// } else if (text.isUnderline(i)) {
				// tele = doc.createElement("ul");
				// tele.setTextContent(text.getText(i));
				// ele.appendChild(tele);
				// } else {
				// t = doc.createTextNode(text.getText(i));
				// ele.appendChild(t);
				// }
				// }
			}
		}
		if (sb.length() > 0) {
			tele = doc.createElement(prevStyle);
			tele.setTextContent(sb.toString());
			ele.appendChild(tele);
		}
		body.appendChild(ele);
		bt.reset();
	}

	@Override
	public void closeReport() throws SukuException {
		try {
			if (reportClosed)
				return;
			reportClosed = true;
			if (translator != null) {
				// System.setProperty("javax.xml.transform.TransformerFactory",
				// "net.sf.saxon.TransformerFactoryImpl");

				DOMSource docw = new DOMSource(doc);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				TransformerFactory tfactory = TransformerFactory.newInstance();

				// Create a transformer for the stylesheet.
				Transformer transformer = tfactory
						.newTransformer(new StreamSource(translator));

				// transformer.transform(docw, new StreamResult(System.out));

				transformer.transform(docw, new StreamResult(bout));
				FileOutputStream fos = new FileOutputStream(report);
				fos.write(bout.toByteArray());
			} else {
				// // Transform the source XML to System.out.
				// transformer.transform(new StreamSource(sourceID),
				// new StreamResult(System.out));
				//		        

				DOMSource docw = new DOMSource(doc);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();

				TransformerFactory tfactory = TransformerFactory.newInstance();
				// Templates templates = tfactory.newTemplates(new
				// StreamSource(xslt));
				Transformer transformer = tfactory.newTransformer();//

				transformer.transform(docw, new StreamResult(bout));

				FileOutputStream fos = new FileOutputStream(report);

				fos.write(bout.toByteArray());
			}
		} catch (Throwable e) {

			logger.log(Level.WARNING, e.getMessage(), e);
			String messu = e.getMessage();

			throw new SukuException(messu);
		}
	}

	private Document doc = null;
	private Element body = null;

	@Override
	public void createReport() {

		Element mini;
		Element header;
		Element ele;
		reportClosed = false;
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();

			doc = builder.newDocument();
			mini = doc.createElement("finfamily");
			doc.appendChild(mini);
			// doc.setRootElement(mini);
			header = doc.createElement("header");
			mini.appendChild(header);
			header.setAttribute("CMD", "ABC");
			header.setAttribute("LANG", "XYZ");
			ele = doc.createElement("copyright");
			header.appendChild(ele);
			body = doc.createElement("body");

			mini.appendChild(body);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
