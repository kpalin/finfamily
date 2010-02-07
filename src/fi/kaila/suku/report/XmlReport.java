package fi.kaila.suku.report;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.report.style.ImageText;
import fi.kaila.suku.swing.worker.ReportWorkerDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;

/**
 * 
 * <h1>XmlReport class</h1>
 * 
 * <p>
 * Many of the reports are first cerated as an XML tree and are after that
 * usually transformed int it's requested form using an XSLT stylesheet in
 * resources/xml
 * </p>
 * 
 * <p>
 * It seems that this process cannot fully be performed from a WebStart
 * application and thus these are only available for the application with the
 * local kontroller. The problem lies in the feature that webstart does not
 * support writing multiple files on disk with one request. Some repports
 * require this such as image files, multiple data files etc.
 * </p>
 * 
 * 
 * @author Kalle
 * 
 */
public class XmlReport implements ReportInterface {

	private String translator = null;
	private String report = null;
	private String folder = null;
	private int maxImageHeight = 0;
	private int maxPersonImageHeight = 0;
	private int translatorIdx;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	int imageCounter = 0;
	private boolean reportClosed = false;
	private boolean debugState = false;
	private ReportWorkerDialog parent;
	String title;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parent
	 * @param translatorIdx
	 * @param title
	 * @throws SukuException
	 */
	public XmlReport(ReportWorkerDialog parent, int translatorIdx, String title)
			throws SukuException {
		this.parent = parent;
		this.title = title;
		this.translatorIdx = translatorIdx;
		maxImageHeight = parent.getImageMaxHeight();
		maxPersonImageHeight = parent.getPersonImageMaxHeight();
		debugState = parent.getDebugState();
		switch (translatorIdx) {
		case 1:
			translator = "resources/xml/docSingle.xsl";
			report = createFile("xml");
			break;
		case 2:
			translator = "resources/xml/htmlSingle.xsl";
			report = createFile("html");
			break;
		default:
			translator = null;
			report = createFile("xml");
			break;

		}
		if (report != null) {

			File f = new File(report);
			boolean fileExists = false;
			if (f.isFile()) {
				fileExists = true;
				int resu = JOptionPane.showConfirmDialog(this.parent, Resurses
						.getString("CONFIRM_REPLACE_REPORT"), Resurses
						.getString(Resurses.SUKU), JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resu != JOptionPane.YES_OPTION) {
					throw new SukuException("");
				}
				f.delete();

			}
			if (translatorIdx > 1) { // word 2003 format does not use folder
				int lastIdx = report.replace('\\', '/').lastIndexOf('.');
				if (lastIdx > 0) {
					folder = report.substring(0, lastIdx) + "_files";
					File d = new File(folder);
					if (d.exists()) {
						if (!fileExists && d.isDirectory()) {
							int resu = JOptionPane
									.showConfirmDialog(
											parent,
											Resurses
													.getString("CONFIRM_REPLACE_REPORTDIR"),
											Resurses.getString(Resurses.SUKU),
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE);
							if (resu != JOptionPane.YES_OPTION) {
								throw new SukuException("");
							}
							fileExists = true;
						}
						if (fileExists && d.isDirectory()) {

							String[] files = d.list();

							for (int i = 0; i < files.length; i++) {
								File df = new File(folder + "/" + files[i]);
								df.delete();
							}

						}
					}
					d.mkdirs();
				}
			}
			//
			// css-file is currently embedded in html-file
			// and separate css-file is not needed
			//
			if (translatorIdx == 2 && false) {

				InputStream in = this.getClass().getResourceAsStream(
						"/xml/finfamily.css");

				BufferedInputStream bis = new BufferedInputStream(in);

				File ff = new File(folder + "/finfamily.css");
				byte[] buffi = new byte[1024];
				FileOutputStream fos;

				try {
					fos = new FileOutputStream(ff);

					while (true) {

						int retu = in.read(buffi);
						if (retu == -1) {
							break;
						}
						fos.write(buffi, 0, retu);

					}
				} catch (FileNotFoundException e) {
					logger.log(Level.WARNING, "css-file", e);
				} catch (IOException e) {
					logger.log(Level.WARNING, "css-file", e);
				}

			}

			return;
		}

		throw new SukuException(Resurses.getString("WARN_REPORT_NOT_SELECTED"));

	}

	private String createFile(String filter) {
		Preferences sr = Preferences.userRoot();

		String[] filters = filter.split(";");

		String koe = sr.get("report", ".");
		// logger.fine("report to: " + koe);

		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Create " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		File f = chooser.getSelectedFile();
		if (f == null) {
			return null;
		}

		String filename = f.getAbsolutePath();
		if (filename == null)
			return null;
		if (filters.length == 1) {
			if (!filename.toLowerCase().endsWith(filters[0].toLowerCase())) {
				filename += "." + filters[0];
			}
		}

		logger.info("requested report : " + filename);

		String tmp = f.getAbsolutePath().replace("\\", "/");
		int i = tmp.lastIndexOf("/");

		sr.put("report", tmp.substring(0, i));

		return filename;
	}

	/**
	 * Add the text to the body element. For images copy the images to output
	 * folder
	 */
	@Override
	public void addText(BodyText bt) {
		Element ele;
		Element tele;
		Element iii;
		String imgName = null;
		String imgTitle = null;
		int imgWidth = 0;
		int imgHeight = 0;
		String img = null;
		boolean isPersonImage = false;
		if (bt instanceof ImageText) {
			ImageText it = (ImageText) bt;
			imgWidth = it.getWidth();
			imgHeight = it.getHeight();
			imgTitle = it.getImageTitle();
			isPersonImage = it.isPersonImage();
			imageCounter++;
			imgName = "" + imageCounter + "_" + it.getImageName();
			File ff = new File(folder + "/" + imgName);

			FileOutputStream fos;
			try {

				if (translatorIdx == 1) {
					img = convertTo64(it);

				} else {
					fos = new FileOutputStream(ff);
					fos.write(it.getData());
					fos.close();
				}

			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING, "Image", e);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Image", e);
			}

		}

		ele = doc.createElement("chapter");
		String style = bt.getClass().getName();
		int lastDot = style.lastIndexOf(".");
		ele.setAttribute("style", style.substring(lastDot + 1));
		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < text.getCount(); i++) {
		// String tmp = text.getText(i);
		// sb.append(tmp);
		// }
		// ele.setTextContent(sb.toString());
		if (imgName != null) {
			ele.setAttribute("image", imgName);
			ele.setAttribute("title", imgTitle);
			if (parent.isNumberingImages()) {
				ele.setAttribute("imageNo", "" + imageCounter);
			}
			ele.setAttribute("imageName", Resurses.getString("REPORT.IMAGE"));
			int maxHeight = maxImageHeight;
			if (isPersonImage) {
				maxHeight = maxPersonImageHeight;
			}

			if (imgHeight > maxHeight) {

				float mh = maxHeight;

				float multip = mh / imgHeight;
				float w = imgWidth * multip;
				float h = imgHeight * multip;

				if (h > 10) {
					ele.setAttribute("width", "" + w);
					ele.setAttribute("height", "" + h);
				}

			} else {
				ele.setAttribute("width", "" + imgWidth);
				ele.setAttribute("height", "" + imgHeight);
			}

			if (img != null) {
				iii = doc.createElement("media");
				iii.setTextContent(img);
				ele.appendChild(iii);
			}
		}
		String prevStyle = "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bt.getCount(); i++) {
			String value = bt.getText(i);
			String currStyle;
			if (value != null && value != "") {
				if (bt.isBold(i) && bt.isUnderline(i) && bt.isItalic(i)) {
					currStyle = "bui";
				} else if (bt.isBold(i) && bt.isUnderline(i)) {
					currStyle = "bu";
				} else if (bt.isBold(i)) {
					currStyle = "b";
				} else if (bt.isUnderline(i)) {
					currStyle = "ul";
				} else if (bt.isBold(i) && bt.isItalic(i)) {
					currStyle = "bi";
				} else if (bt.isBold(i) && bt.isUnderline(i)) {
					currStyle = "ui";
				} else if (bt.isItalic(i)) {
					currStyle = "i";
				} else {
					currStyle = "n";
				}
				if (!currStyle.equals(prevStyle)) {
					if (sb.length() > 0) {
						tele = doc.createElement(prevStyle);
						tele.setTextContent(sb.toString());
						ele.appendChild(tele);

						sb = new StringBuilder();
					}
					prevStyle = currStyle;

				}

				sb.append(bt.getText(i));

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

	/**
	 * Closing actions are made here In case of XML report it consists of
	 * <ul>
	 * <li>delete empty folder</li>
	 * <li>Translate to final format or store as raw xml as requested</li>
	 * <li>Start applictaion to open the report if supported</li>
	 * </ul>
	 */
	@Override
	public void closeReport() throws SukuException {
		PrintStream origErr = System.err;
		ByteArrayOutputStream barray = new ByteArrayOutputStream();
		try {
			if (reportClosed) {
				return;
			}
			reportClosed = true;
			//
			// delete folder if empty
			// note that delete does not delete it if it's not empty
			//
			if (folder != null) {
				File dd = new File(folder);
				dd.delete();
			}
			if (debugState) {
				logger.info("raw xml-file stored at " + report + ".debug");
				DOMSource docw = new DOMSource(doc);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				TransformerFactory tfactory = TransformerFactory.newInstance();

				Transformer transformer = tfactory.newTransformer();//
				transformer.transform(docw, new StreamResult(bout));
				FileOutputStream fos = new FileOutputStream(report + ".debug");
				fos.write(bout.toByteArray());
				fos.close();
			}

			if (translator != null) {
				logger.info("report will store at " + report);
				File f = new File(translator);
				logger.info("transformed with " + f.getAbsolutePath());
				// redirect stderr to get transformation error data to the
				// logger
				PrintStream stderr = new PrintStream(barray);

				System.setErr(stderr);
				DOMSource docw = new DOMSource(doc);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				TransformerFactory tfactory = TransformerFactory.newInstance();
				// Create a transformer for the stylesheet.
				Source src = new StreamSource(translator);
				Transformer transformer = tfactory.newTransformer(src);

				transformer.transform(docw, new StreamResult(bout));
				FileOutputStream fos = new FileOutputStream(report);
				fos.write(bout.toByteArray());
				fos.close();
				logger.fine(report + " will be opened");
				Utils.openExternalFile(report);
			}

		} catch (Throwable e) {
			logger.log(Level.WARNING, barray.toString());
			logger.log(Level.WARNING, e.getMessage(), e);
			String messu = e.getMessage();
			System.setErr(origErr);
			throw new SukuException(messu);
		}
		System.setErr(origErr);
	}

	private Document doc = null;
	private Element body = null;

	/**
	 * Create report prepares the report for input In case of Xml report this
	 * consists of
	 * <ul>
	 * <li>create the DOM tree</li>
	 * <li>create the document with finfamily main element</li>
	 * <li>create header element</li>
	 * <li>create body element for processing</li>
	 * </ul>
	 */
	@Override
	public void createReport() throws SukuException {

		Element mini;
		Element title;
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

			if (folder != null) {
				int fidx = folder.replace('\\', '/').lastIndexOf('/');
				if (fidx > 0) {
					header.setAttribute("folder", folder.substring(fidx + 1));
				}

			}
			// header.setAttribute("LANG", "XYZ");
			ele = doc.createElement("copyright");
			header.appendChild(ele);
			title = doc.createElement("title");
			if (this.title != null) {
				title.setTextContent(this.title);
			} else {
				title.setTextContent(Resurses.getString("SUKUOHJELMISTO"));
			}
			header.appendChild(title);
			body = doc.createElement("body");

			mini.appendChild(body);
		} catch (ParserConfigurationException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			String messu = e.getMessage();
			throw new SukuException(messu);
		}
	}

	static final String table64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	/**
	 * Copied from XmlReports::CopyBase64File
	 * 
	 * @param it
	 *            the Style with the image
	 * @param to
	 *            path for output file
	 */
	private String convertTo64(ImageText it) {

		Formatter ff = new Formatter();

		StringBuilder sbb = new StringBuilder();

		byte[] ibuf = new byte[3];
		byte[] obuf = new byte[4];

		int inputparts;
		int colPos = 0;
		int insize = it.getData().length;
		int indata = 0;

		while (insize > 0) {
			for (int i = inputparts = 0; i < 3; i++) {
				if (insize > 0) {
					inputparts++;

					ibuf[i] = it.getData()[indata];
					indata++;
					insize--;
				} else
					ibuf[i] = 0;
			}

			obuf[0] = (byte) ((ibuf[0] & 0xFC) >> 2);
			obuf[1] = (byte) (((ibuf[0] & 0x03) << 4) | ((ibuf[1] & 0xF0) >> 4));
			obuf[2] = (byte) (((ibuf[1] & 0x0F) << 2) | ((ibuf[2] & 0xC0) >> 6));
			obuf[3] = (byte) (ibuf[2] & 0x3F);

			char c0, c1, c2, c3;
			switch (inputparts) {
			case 1: /* only one byte read */
				c0 = table64.charAt(obuf[0]);
				c1 = table64.charAt(obuf[1]);
				ff.format("%c%c==", c0, c1);

				break;
			case 2: /* two bytes read */
				c0 = table64.charAt(obuf[0]);
				c1 = table64.charAt(obuf[1]);
				c2 = table64.charAt(obuf[2]);
				ff.format("%c%c%c=", c0, c1, c2);
				break;
			default:
				c0 = table64.charAt(obuf[0]);
				c1 = table64.charAt(obuf[1]);
				c2 = table64.charAt(obuf[2]);
				c3 = table64.charAt(obuf[3]);
				ff.format("%c%c%c%c", c0, c1, c2, c3);
				break;
			}

			if (colPos >= 76) {
				ff.format("\n");

				colPos = 0;
			} else {
				colPos += 4;
			}

		}

		sbb.append(ff.out());

		return sbb.toString();

	}

}
