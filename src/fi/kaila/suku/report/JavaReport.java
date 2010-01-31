package fi.kaila.suku.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
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

import fi.kaila.suku.report.style.BodyText;
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

	private JMenuBar menubar;
	private JMenu mFile;
	private JMenuItem mPrint;
	private JMenuItem mExit;

	private JTextPane text;
	private JScrollPane textScroll;

	private Logger logger = Logger.getLogger(this.getName());

	private boolean reportClosed = true;

	/**
	 * default constructor to initlaize frame
	 */
	public JavaReport() {
		initMe();
	}

	private void initMe() {

		setLayout(new BorderLayout());

		this.menubar = new JMenuBar();
		setJMenuBar(this.menubar);
		this.mFile = new JMenu(Resurses.getString(Resurses.FILE));
		this.menubar.add(this.mFile);

		this.mPrint = new JMenuItem(Resurses.getString(Resurses.PRINT_REPORT));
		this.mFile.add(this.mPrint);
		this.mPrint.setActionCommand(Resurses.PRINT_REPORT);
		this.mPrint.addActionListener(this);

		this.mFile.addSeparator();
		this.mExit = new JMenuItem(Resurses.getString(Resurses.EXIT));
		this.mFile.add(this.mExit);
		this.mExit.setActionCommand(Resurses.EXIT);
		this.mExit.addActionListener(this);

		// this.textPerson = new PersonPane();

		this.text = new JTextPane();
		this.textScroll = new JScrollPane(this.text);
		add(this.textScroll);

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
	 * java styled report is created here
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

	@Override
	public void addText(BodyText bt) {
		int alkulen = doc.getLength();
		SimpleAttributeSet myStyle = null;
		SimpleAttributeSet sas = null;

		for (int j = 0; j < bt.getCount(); j++) {
			String extra = "";
			myStyle = new SimpleAttributeSet();
			StyleConstants.setFontFamily(myStyle, bt.getFontName());
			StyleConstants.setFontSize(myStyle, (int) (bt.getFontSize() * 1.2));
			StyleConstants.setBold(myStyle, bt.isBold(j));
			StyleConstants.setUnderline(myStyle, bt.isUnderline(j));
			StyleConstants.setItalic(myStyle, bt.isItalic(j));
			// Icon tst = StyleConstants.getIcon(myStyle);
			if (bt.getImage() != null && j == 0) {

				ImageIcon icon = new ImageIcon(bt.getImage());

				StyleConstants.setIcon(myStyle, icon);

				extra = "\n";
			}

			appendString(bt.getText(j) + extra, myStyle);

			// myStyle = new SimpleAttributeSet();
			// StyleConstants.setFontFamily(myStyle, bt.getFontName());
			// StyleConstants.setFontSize(myStyle, bt.getFontSize());
			// StyleConstants.setBold(myStyle, bt.isBold(j));
			// StyleConstants.setUnderline(myStyle, bt.isUnderline(j));

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

}
