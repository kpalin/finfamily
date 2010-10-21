package fi.kaila.suku.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.report.style.ImageText;
import fi.kaila.suku.swing.doc.SukuDocument;
import fi.kaila.suku.swing.text.DocumentSukuFilter;
import fi.kaila.suku.util.Resurses;

/**
 * The Java report is a separate window that receives the report in a java
 * TextPane format. This format is easy for the user and should be considered as
 * a preview of the report
 * 
 * @author Kalle
 * 
 */
public class JavaReport extends JFrame implements ActionListener,
		ReportInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextPane text;

	private final Logger logger = Logger.getLogger(this.getName());

	private boolean reportClosed = true;
	private ReportWorkerDialog parent = null;

	/**
	 * default constructor to initialize frame.
	 * 
	 * @param parent
	 *            the parent
	 */
	public JavaReport(ReportWorkerDialog parent) {
		this.parent = parent;
		initMe();
	}

	private void initMe() {

		setLayout(new BorderLayout());

		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		JMenu mFile = new JMenu(Resurses.getString(Resurses.FILE));
		menubar.add(mFile);

		JMenuItem mPrint = new JMenuItem(
				Resurses.getString(Resurses.PRINT_REPORT));
		mFile.add(mPrint);
		mPrint.setActionCommand(Resurses.PRINT_REPORT);
		mPrint.addActionListener(this);

		mFile.addSeparator();
		JMenuItem mExit = new JMenuItem(Resurses.getString(Resurses.EXIT));
		mFile.add(mExit);
		mExit.setActionCommand(Resurses.EXIT);
		mExit.addActionListener(this);

		// this.textPerson = new PersonPane();

		this.text = new JTextPane();
		JScrollPane textScroll = new JScrollPane(this.text);
		add(textScroll);

		// setLayout(null);
		// setLocation(200, 200);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		int dd = d.width - 100;
		if (dd > 800)
			dd = 800;
		setBounds(d.width / 2 - dd / 2, 20, dd, d.height - 100);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				// if(parent != null) {
				// parent.ReportFrameClosing();
				//
				// }
				e.getClass();

			}
		});

	}

	private AbstractDocument doc;

	/**
	 * java styled report is created here.
	 */
	@Override
	public void createReport() {
		reportClosed = false;
		doc = new SukuDocument();
		text.setDocument(doc);
		StyledDocument styledDoc = text.getStyledDocument();

		if (styledDoc instanceof AbstractDocument) {
			doc = (AbstractDocument) styledDoc;
			doc.setDocumentFilter(new DocumentSukuFilter());
		} else {
			JOptionPane.showMessageDialog(this,
					"Text pane's document isn't an AbstractDocument!");
			// of course this is programming error and shouldn't ever come here
			return;
		}

		setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.report.ReportInterface#addText(fi.kaila.suku.report.style
	 * .BodyText)
	 */
	@Override
	public void addText(BodyText bt) {
		int alkulen = doc.getLength();
		SimpleAttributeSet myStyle = null;
		SimpleAttributeSet sas = null;

		for (int j = 0; j < bt.getCount(); j++) {
			String extra = "";
			myStyle = new SimpleAttributeSet();
			StyleConstants.setFontFamily(myStyle, bt.getFontName());
			StyleConstants.setFontSize(myStyle, (int) (bt.getFontSize() * 1.4));
			StyleConstants.setBold(myStyle, bt.isBold(j));
			StyleConstants.setUnderline(myStyle, bt.isUnderline(j));
			StyleConstants.setItalic(myStyle, bt.isItalic(j));
			String imgTitle = "";
			if (bt.getImage() != null && j == 0) {
				ImageText it = (ImageText) bt;
				int imgWidth = it.getWidth();
				int imgHeight = it.getHeight();
				imgTitle = it.getImageTitle();
				Dimension maxSize = parent.getImageMaxSize();

				if (it.isPersonImage()) {
					maxSize = parent.getPersonImageMaxSize();
				}
				float w;
				float h;
				if (maxSize.width == 0 && maxSize.height == 0) {
					w = imgWidth;
					h = imgHeight;
				} else if (maxSize.height == 0) {
					if (imgWidth > maxSize.width) {
						float mw = maxSize.width;
						float multip = mw / imgWidth;
						w = imgWidth * multip;
						h = imgHeight * multip;
					} else {
						w = imgWidth;
						h = imgHeight;
					}
				} else if (maxSize.width == 0) {
					if (imgHeight > maxSize.height) {
						float mh = maxSize.height;
						float multip = mh / imgHeight;
						w = imgWidth * multip;
						h = imgHeight * multip;
					} else {
						w = imgWidth;
						h = imgHeight;
					}
				} else {
					float mw = maxSize.width;
					float mh = maxSize.height;
					float multiw = mw / imgWidth;
					float multih = mh / imgHeight;
					float multip = (multiw < multih) ? multiw : multih;
					if (multip < 1) {
						w = imgWidth * multip;
						h = imgHeight * multip;
					} else {
						w = imgWidth;
						h = imgHeight;
					}
				}

				try {
					Image img = scaleImage(bt.getImage(), (int) w, (int) h);
					ImageIcon icon = new ImageIcon(img);
					// ImageIcon icon = new ImageIcon(bt.getImage());
					StyleConstants.setIcon(myStyle, icon);

					extra = "\n";
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
			// this code removes the image from the style
			// w/o this the image may be printed twice
			appendString(bt.getText(j) + extra, myStyle);

			myStyle = new SimpleAttributeSet();
			StyleConstants.setFontFamily(myStyle, bt.getFontName());
			StyleConstants.setFontSize(myStyle, (int) (bt.getFontSize() * 1.2));
			StyleConstants.setBold(myStyle, bt.isBold(j));
			StyleConstants.setUnderline(myStyle, bt.isUnderline(j));
			appendString(imgTitle, myStyle);
		}

		appendString("\n", myStyle);
		int loppulen = doc.getLength();
		text.setSelectionStart(alkulen);
		text.setSelectionEnd(loppulen);
		sas = new SimpleAttributeSet();
		int style = StyleConstants.ALIGN_LEFT;
		if (bt.getParaAlignment() == BodyText.ALIGN_CENTER) {
			style = StyleConstants.ALIGN_CENTER;
		}
		StyleConstants.setAlignment(sas, style);
		StyleConstants.setSpaceAbove(sas, bt.getParaSpacingBefore());
		StyleConstants.setSpaceBelow(sas, bt.getParaSpacingAfter());
		StyleConstants.setLeftIndent(sas, bt.getParaIndentLeft());
		text.setParagraphAttributes(sas, true);

		bt.reset();
	}

	private void appendString(String text, SimpleAttributeSet style) {
		try {
			doc.insertString(doc.getLength(), text, style);
		} catch (BadLocationException e) {
			logger.warning("appendReport failed " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.report.ReportInterface#closeReport()
	 */
	@Override
	public void closeReport() {
		if (reportClosed)
			return;
		reportClosed = true;
		setVisible(true);
	}

	// @Override
	// public void createNewReport(ISuku parent) {
	// repo = new ReportFrame(parent);
	// repo.createReport();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd == null)
			return;
		if (cmd.equals(Resurses.EXIT)) {
			// parent.ReportFrameClosing();
			setVisible(false);
		} else if (cmd.equals(Resurses.PRINT_REPORT)) {
			try {
				this.text.print();
			} catch (PrinterException ep) {
				JOptionPane.showMessageDialog(this, ep.getMessage());
				logger.log(Level.INFO, "Print error", ep);
			}

		}

	}

	/**
	 * This method has been copied from
	 * http://www.webmaster-talk.com/coding-forum
	 * /63227-image-resizing-in-java.html#post301529 made by Rick Palmer
	 * 
	 * @param image
	 * @param p_width
	 * @param p_height
	 * @return the scaled image
	 * @throws Exception
	 */
	private Image scaleImage(Image image, int p_width, int p_height)
			throws Exception {

		int thumbWidth = p_width;
		int thumbHeight = p_height;

		// Make sure the aspect ratio is maintained, so the image is not skewed
		double thumbRatio = (double) thumbWidth / (double) thumbHeight;
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		double imageRatio = (double) imageWidth / (double) imageHeight;
		if (thumbRatio < imageRatio) {
			thumbHeight = (int) (thumbWidth / imageRatio);
		} else {
			thumbWidth = (int) (thumbHeight * imageRatio);
		}

		// Draw the scaled image
		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

		// Write the scaled image to the output stream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage);
		int quality = 100; // Use between 1 and 100, with 100 being highest
		quality = Math.max(0, Math.min(quality, 100));
		param.setQuality(quality / 100.0f, false);
		encoder.setJPEGEncodeParam(param);
		encoder.encode(thumbImage);
		return thumbImage;

	}
}
