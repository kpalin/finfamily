package fi.kaila.suku.report.style;

import java.awt.Font;
import java.awt.Image;
import java.util.Vector;

/**
 * Base class for styles in Suku11 reports A BodyText style element consists of
 * a vector of Text elements.
 * 
 * @author Kalle
 */
public class BodyText {

	/** Align left value for paraAlignment. */
	public static final int ALIGN_LEFT = 0;

	/** Align center value for paraAlignment. */
	public static final int ALIGN_CENTER = 1;

	/** Serif Font value for fontName. */
	public static final String FONT_SERIF = "Times new Roman";

	/** SansSerif Font value for fontName. */
	public static final String FONT_SANS_SERIF = "Arial";

	/** The font name. */
	protected String fontName = FONT_SANS_SERIF;

	/** The font size. */
	protected int fontSize = 10;

	/** The font style. */
	protected int fontStyle = Font.PLAIN;
	// protected boolean fontUnderline=false;

	/** The para alignment. */
	protected int paraAlignment = ALIGN_LEFT;

	/** The para indent left. */
	protected float paraIndentLeft = 6; // left alignment in pt

	/** The para spacing before. */
	protected float paraSpacingBefore = 0; // spacing before in pt

	/** The para spacing after. */
	protected float paraSpacingAfter = 6; // spacing after in pt

	/** The image. */
	protected Image image = null;

	/** The imagedata. */
	protected byte[] imagedata = null;

	/** The image name. */
	protected String imageName = null;

	private Vector<Text> txt = new Vector<Text>();

	/**
	 * Gets the font name.
	 * 
	 * @return fontname for this style
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * font size in pt (1/72) inch return fontSize.
	 * 
	 * @return fontsize for this style
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * set (different) font size.
	 * 
	 * @param fontSize
	 *            the new font size
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * Gets the image.
	 * 
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * reset container.
	 */
	public void reset() {
		txt = new Vector<Text>();
	}

	/**
	 * alignment of paragraph ALIGN_LEFT=0 ALIGN_CENTER=1.
	 * 
	 * @return paraAlignment
	 */
	public int getParaAlignment() {
		return paraAlignment;
	}

	/**
	 * Left ident of paragraph in cm.
	 * 
	 * @return paraIndentLeft
	 */
	public float getParaIndentLeft() {
		return paraIndentLeft;
	}

	/**
	 * Paragraph spacing in pt (1/72 inch).
	 * 
	 * @return paraSpacingBefore
	 */
	public float getParaSpacingBefore() {
		return paraSpacingBefore;
	}

	/**
	 * Paragraph spacing in pt (1/72 inch).
	 * 
	 * @return paraSpacingAfter
	 */
	public float getParaSpacingAfter() {
		return paraSpacingAfter;
	}

	/**
	 * add text string to style element.
	 * 
	 * @param text
	 *            the text
	 */
	public void addText(String text) {
		Text t = new Text(text);
		txt.add(t);
	}

	/**
	 * add text string with formatting options to style element.
	 * 
	 * @param text
	 *            the text
	 * @param isBold
	 *            the is bold
	 * @param isUnderline
	 *            the is underline
	 */
	public void addText(String text, boolean isBold, boolean isUnderline) {
		Text t = new Text(text);
		t.isBold = isBold;
		t.isUnderline = isUnderline;
		txt.add(t);
	}

	/**
	 * add text string with formatting options to style element.
	 * 
	 * @param text
	 *            the text
	 * @param isBold
	 *            the is bold
	 * @param isUnderline
	 *            the is underline
	 * @param isItalic
	 *            the is italic
	 */
	public void addText(String text, boolean isBold, boolean isUnderline,
			boolean isItalic) {
		Text t = new Text(text);
		t.isBold = isBold;
		t.isUnderline = isUnderline;
		t.isItalic = isItalic;
		txt.add(t);
	}

	/**
	 * Add link with text
	 * 
	 * @param text
	 * @param isBold
	 * @param isUnderline
	 * @param isItalic
	 * @param link
	 */
	public void addLink(String text, boolean isBold, boolean isUnderline,
			boolean isItalic, String link) {
		Text t = new Text(text);
		t.isBold = isBold;
		t.isUnderline = isUnderline;
		t.isItalic = isItalic;
		if (link != null) {
			t.link = link;
		}
		txt.add(t);

	}

	public void addAnchor(String anchor) {
		Text t = new Text(null);
		t.anchor = anchor;
		txt.add(t);
	}

	public String getAnchor(int idx) {
		Text t = txt.get(idx);
		return t.anchor;
	}

	/**
	 * Gets the count.
	 * 
	 * @return text size in style element
	 */
	public int getCount() {
		return txt.size();
	}

	/**
	 * Gets the text.
	 * 
	 * @param idx
	 *            the idx
	 * @return text content of text element
	 */
	public String getText(int idx) {
		Text t = txt.get(idx);
		return t.text;
	}

	/**
	 * 
	 * @param idx
	 * @return link from text element or null
	 */
	public String getLink(int idx) {
		Text t = txt.get(idx);
		return t.link;
	}

	/**
	 * Checks if is bold.
	 * 
	 * @param idx
	 *            the idx
	 * @return true if the indexed text element is bold
	 */
	public boolean isBold(int idx) {
		if ((fontStyle & Font.BOLD) != 0)
			return true;

		Text t = txt.get(idx);
		return t.isBold;
	}

	/**
	 * Checks if is underline.
	 * 
	 * @param idx
	 *            the idx
	 * @return true if the indexed text elemet is underlined
	 */
	public boolean isUnderline(int idx) {
		Text t = txt.get(idx);
		return t.isUnderline;
	}

	/**
	 * Checks if is italic.
	 * 
	 * @param idx
	 *            the idx
	 * @return true if the indexed text elemet is underlined
	 */
	public boolean isItalic(int idx) {
		Text t = txt.get(idx);
		return t.isItalic;
	}

	public boolean endsWithText(String suffix) {

		StringBuilder sb = new StringBuilder();

		for (Text t : txt) {
			sb.append(t.text);
		}
		if (sb.toString().trim().endsWith(suffix)) {
			return true;
		}
		return false;
	}

	private class Text {
		String text;
		boolean isBold = false;
		boolean isUnderline = false;
		boolean isItalic = false;
		String link = null;
		String anchor = null;

		Text(String text) {
			addTxt(text);
		}

		private void addTxt(String text) {
			if (text == null) {
				text = "";
				return;
			}

			int i = text.indexOf("\n");

			if (i < 0) {
				this.text = text;
				return;
			}
			int lfCount = 0;
			StringBuilder sb = new StringBuilder();

			for (int j = 0; j < i; j++) {
				if (text.charAt(j) != '\r') {
					sb.append(text.charAt(j));
				}
			}

			for (int j = i; j < text.length(); j++) {
				if (text.charAt(j) != '\r') {
					if (text.charAt(j) == '\n') {
						lfCount++;
						if (lfCount > 1) {
							lfCount = 0;
							sb.append('\n');
						}
					} else if (text.charAt(j) == ' ') {
						if (lfCount == 0) {
							sb.append(' ');
						}
					} else {
						if (lfCount > 0) {
							lfCount = 0;
							sb.append(' ');
						}
						sb.append(text.charAt(j));
					}
				}
			}

			this.text = sb.toString();

		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (imageName != null) {
			sb.append("Image: ");
			sb.append(imageName);
			sb.append("\n");
		}

		for (Text t : txt) {
			sb.append(t.text);
		}
		return sb.toString();
	}

}
