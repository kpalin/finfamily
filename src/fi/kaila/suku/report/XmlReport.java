package fi.kaila.suku.report;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
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

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.report.style.ImageText;
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
	private Dimension maxImageSize = new Dimension(0, 0);
	private Dimension maxPersonImageSize = new Dimension(0, 0);
	private int translatorIdx;
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private int imageScaleIndex = 0;
	/** The image counter. */
	// int imageCounter = 0;
	private boolean reportClosed = false;
	private boolean debugState = false;
	private ReportWorkerDialog parent;

	/** The title. */
	String title;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent
	 * @param translatorIdx
	 *            the translator idx
	 * @param title
	 *            the title
	 * @throws SukuException
	 *             the suku exception
	 */
	public XmlReport(ReportWorkerDialog parent, int translatorIdx, String title)
			throws SukuException {
		this.parent = parent;
		this.title = title;
		this.translatorIdx = translatorIdx;
		maxImageSize = parent.getImageMaxSize();
		maxPersonImageSize = parent.getPersonImageMaxSize();
		debugState = parent.getDebugState();
		imageScaleIndex = parent.getSukuParent().getImageScalerIndex();

		switch (translatorIdx) {
		case 1:
			translator = "resources/xml/docx.xsl";
			report = createFile("xml");
			break;
		case 2:
			translator = "resources/xml/html.xsl";
			report = createFile("html");
			break;
		default:
			translator = null;
			report = createFile("doc");
			break;

		}
		if (report != null) {

			File f = new File(report);
			boolean fileExists = false;
			if (f.isFile()) {
				fileExists = true;
				int resu = JOptionPane
						.showConfirmDialog(parent,
								Resurses.getString("CONFIRM_REPLACE_REPORT"),
								Resurses.getString(Resurses.SUKU),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (resu != JOptionPane.YES_OPTION) {

					throw new SukuException(
							Resurses.getString("WARN_REPORT_NOT_SELECTED"));
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
											Resurses.getString("CONFIRM_REPLACE_REPORTDIR"),
											Resurses.getString(Resurses.SUKU),
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE);
							if (resu != JOptionPane.YES_OPTION) {

								throw new SukuException(
										Resurses.getString("WARN_REPORT_NOT_SELECTED"));
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

		if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
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
	 * 
	 * @param bt
	 *            the bt
	 */
	@Override
	public void addText(BodyText bt) {
		Element ele;
		Element tele;
		Element anc;
		Element iii;
		String imgName = null;
		String imgTitle = null;
		int imgWidth = 0;
		int imgHeight = 0;
		String img = null;
		Dimension maxSize = maxImageSize;
		Dimension displaySize = new Dimension();
		int outHeight = 0;
		int outWidth = 0;
		boolean isPersonImage = false;
		if (bt instanceof ImageText) {
			ImageText it = (ImageText) bt;
			isPersonImage = it.isPersonImage();
			if (isPersonImage) {
				maxSize = maxPersonImageSize;
			} else {
				maxSize = maxImageSize;
			}
			imgWidth = it.getWidth();
			imgHeight = it.getHeight();
			imgTitle = it.getImageTitle();
			float w;
			float h;
			int widthMultiplier = imageScaleIndex;
			if (widthMultiplier > 5) {
				widthMultiplier = 0;
			}
			if (maxSize.width == 0 && maxSize.height == 0) {
				w = imgWidth;
				h = imgHeight;
				displaySize.width = imgWidth;
				displaySize.height = imgHeight;

			} else if (maxSize.width > 0) {
				if (imgWidth > maxSize.width * widthMultiplier) {

					float mw = maxSize.width;
					float multip = mw / imgWidth;
					float mh = maxSize.height;
					float multih = mh / imgHeight;
					if (multih > 0 && multih < multip) {
						multip = multih;
					}

					w = imgWidth * multip;
					h = imgHeight * multip;
					displaySize.width = (int) w;
					displaySize.height = (int) h;
					w *= widthMultiplier;
					h *= widthMultiplier;
					outHeight = displaySize.height;
					outWidth = displaySize.width;
				} else {

					float mw = maxSize.width;
					float multip = mw / imgWidth;

					float mh = maxSize.height;
					float multih = mh / imgHeight;
					if (multih > 0 && multih < multip) {
						multip = multih;
					}

					w = imgWidth * multip;
					h = imgHeight * multip;

					displaySize.width = (int) w;
					displaySize.height = (int) h;
					outWidth = 0;
					outHeight = 0;

				}
			} else {
				if (imgHeight > maxSize.height) {
					float mh = maxSize.height * widthMultiplier;
					float multip = mh / imgHeight;
					w = imgWidth * multip;
					h = imgHeight * multip;
					displaySize.width = (int) w;
					displaySize.height = (int) h;
					w *= widthMultiplier;
					h *= widthMultiplier;
					outHeight = displaySize.height;
					outWidth = displaySize.width;
				} else {
					float mh = maxSize.height;
					float multip = mh / imgHeight;
					w = imgWidth * multip;
					h = imgHeight * multip;
					displaySize.width = (int) w;
					displaySize.height = (int) h;
					outWidth = 0;
					outHeight = 0;
				}
			}

			if (outHeight < 10) {
				outWidth = 0;
				outHeight = 0;

			}

			// imageCounter++;
			imgName = /* "" + imageCounter + "_" + */it.getImageName();
			File ff = new File(folder + "/" + imgName);
			if (it.getData() != null) {
				FileOutputStream fos;
				try {

					if (translatorIdx == 1) {

						img = convertTo64(it, outWidth, outHeight);

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
		}

		ele = doc.createElement("chapter");

		String style = bt.getClass().getName();

		int lastDot = style.lastIndexOf(".");
		style = style.substring(lastDot + 1);
		ele.setAttribute("style", style);
		if ("NameIndexText".equals(style)) {
			if (nameIndex == null) {
				nameIndex = doc.createElement("nameIndex");
				body.appendChild(nameIndex);
			}

		} else {
			if (style != null && !"NameIndexText".equals(style)) {
				nameIndex = null;

			}
		}
		// String anchor = bt.getAnchor();
		// if (anchor != null) {
		// ele.setAttribute("anchor", anchor);
		// }

		if (imgName != null) {
			ele.setAttribute("image", imgName);
			ele.setAttribute("title", imgTitle);

			ele.setAttribute("imageName", Resurses.getString("REPORT.IMAGE"));

			ele.setAttribute("width", "" + displaySize.width);
			ele.setAttribute("height", "" + displaySize.height);

			if (img != null) {
				iii = doc.createElement("media");
				iii.setTextContent(img);
				ele.appendChild(iii);
			}
		}
		String prevStyle = "";

		String link = null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bt.getCount(); i++) {
			String value = bt.getText(i);
			String anchor = bt.getAnchor(i);
			if (anchor != null) {
				anc = doc.createElement("anchor");
				anc.setTextContent(anchor);
				ele.appendChild(anc);
			}
			String currStyle;
			if (value != null && !value.isEmpty()) {
				if (bt.isBold(i) && bt.isUnderline(i) && bt.isItalic(i)) {
					currStyle = "bui";
				} else if (bt.isBold(i) && bt.isUnderline(i)) {
					currStyle = "bu";
				} else if (bt.isBold(i)) {
					currStyle = "b";
				} else if (bt.isUnderline(i)) {
					currStyle = "u";
				} else if (bt.isBold(i) && bt.isItalic(i)) {
					currStyle = "bi";
				} else if (bt.isBold(i) && bt.isUnderline(i)) {
					currStyle = "ui";
				} else if (bt.isItalic(i)) {
					currStyle = "i";
				} else {
					currStyle = "n";
				}
				link = bt.getLink(i);
				if (link == null) {
					if (!currStyle.equals(prevStyle)) {
						if (sb.length() > 0) {
							tele = doc.createElement(prevStyle);

							tele.setTextContent(sb.toString());
							ele.appendChild(tele);

							sb = new StringBuilder();
						}
						prevStyle = currStyle;

					}
					sb.append(value);

				} else {
					if (sb.length() > 0) {
						tele = doc.createElement(prevStyle);
						tele.setTextContent(sb.toString());
						ele.appendChild(tele);

					}
					prevStyle = currStyle;

					tele = doc.createElement(prevStyle);
					tele.setTextContent(value);
					ele.appendChild(tele);

					tele.setAttribute("link", link);
					sb = new StringBuilder();
					link = null;
					// sb.append(value);
				}

				// link = bt.getLink(i);

			}
		}
		if (sb.length() > 0) {
			tele = doc.createElement(prevStyle);

			tele.setTextContent(sb.toString());
			ele.appendChild(tele);
		}
		if (nameIndex != null) {
			nameIndex.appendChild(ele);
		} else {
			body.appendChild(ele);
		}
		bt.reset();
	}

	/**
	 * Closing actions are made here In case of XML report it consists of
	 * <ul>
	 * <li>delete empty folder</li>
	 * <li>Translate to final format or store as raw xml as requested</li>
	 * <li>Start application to open the report if supported</li>
	 * </ul>
	 * .
	 * 
	 * @throws SukuException
	 *             the suku exception
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
				parent.addRepoForDisplay(report);
				// Utils.openExternalFile(report);
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
	private Element nameIndex = null;

	/**
	 * Create report prepares the report for input In case of Xml report this
	 * consists of
	 * <ul>
	 * <li>create the DOM tree</li>
	 * <li>create the document with finfamily main element</li>
	 * <li>create header element</li>
	 * <li>create body element for processing</li>
	 * </ul>
	 * .
	 * 
	 * @throws SukuException
	 *             the suku exception
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

	/** The Constant table64. */
	static final String table64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	/**
	 * Copied from XmlReports::CopyBase64File
	 * 
	 * @param it
	 *            the Style with the image
	 * @param outHeight
	 * @param outWidth
	 * @param to
	 *            path for output file
	 */
	private String convertTo64(ImageText it, int outWidth, int outHeight) {

		if (it == null) {
			logger.warning("Image is null");
			return "";
		}
		if (it.getData() == null) {
			logger.warning("Image data is null" + it);
			return "";
		}

		if (imageScaleIndex == 0) {
			outWidth = 0;
		}

		if (outWidth != 0) {
			BufferedImage img = null;

			try {
				img = Utils.scaleImage(it.getImage(), outWidth, outHeight);

			} catch (Exception e) {
				return e.toString();
			}

			// O P E N
			// converting to bytes : copy-paste from
			// http://mindprod.com/jgloss/imageio.html#TOBYTES
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
			byte[] resultImageAsRawBytes = null;
			// W R I T E
			try {
				ImageIO.write(img, "jpeg", baos);
				// C L O S E
				baos.flush();
				img.flush();
				resultImageAsRawBytes = baos.toByteArray();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Raster raster = img.getRaster();
			// DataBufferByte dbuf = (DataBufferByte) raster.getDataBuffer();
			Formatter ff = new Formatter();

			StringBuilder sbb = new StringBuilder();

			byte[] ibuf = new byte[3];
			byte[] obuf = new byte[4];

			int inputparts;
			int colPos = 0;
			int insize = resultImageAsRawBytes.length;
			int indata = 0;

			while (insize > 0) {
				for (int i = inputparts = 0; i < 3; i++) {
					if (insize > 0) {
						inputparts++;

						ibuf[i] = resultImageAsRawBytes[indata];
						// it.getData()[indata];
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
		} else {

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

	@Override
	public String toString() {
		return report;
	}

	@Override
	public void closeReport(long tabNo) throws SukuException {
		PrintStream origErr = System.err;
		ByteArrayOutputStream barray = new ByteArrayOutputStream();
		int dotPos = report.lastIndexOf(".");
		int slashPos = report.replace("\\", "/").lastIndexOf("/");
		if (folder == null || slashPos <= 0 || dotPos < slashPos)
			return;
		String myreport;

		if (dotPos > 0) {
			myreport = folder + "/" + report.substring(slashPos + 1, dotPos)
					+ tabNo + "." + report.substring(dotPos + 1);
			// myreport = report.substring(0, dotPos) + tabNo + "."
			// + report.substring(dotPos + 1);
		} else {
			return;
		}

		try {
			if (reportClosed) {
				return;
			}
			reportClosed = true;
			//
			// delete folder if empty
			// note that delete does not delete it if it's not empty
			//
			// if (folder != null) {
			// File dd = new File(folder);
			// dd.delete();
			// }
			if (debugState) {
				logger.info("raw xml-file stored at " + myreport + ".debug");
				DOMSource docw = new DOMSource(doc);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				TransformerFactory tfactory = TransformerFactory.newInstance();

				Transformer transformer = tfactory.newTransformer();//
				transformer.transform(docw, new StreamResult(bout));
				FileOutputStream fos = new FileOutputStream(report + ".debug");
				fos.write(bout.toByteArray());
				fos.close();
			}
			translator = "resources/xml/export.xsl";
			// if (translator != null) {
			logger.info("report will store at " + myreport);
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
			FileOutputStream fos = new FileOutputStream(myreport);
			fos.write(bout.toByteArray());
			fos.close();
			logger.fine(myreport + " will be opened");
			// parent.addRepoForDisplay(report); // is this needed ?
			// Utils.openExternalFile(report);
			// }

		} catch (Throwable e) {
			logger.log(Level.WARNING, barray.toString());
			logger.log(Level.WARNING, e.getMessage(), e);
			String messu = e.getMessage();
			System.setErr(origErr);
			throw new SukuException(messu);
		}
		System.setErr(origErr);

	}

}
