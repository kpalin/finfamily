package fi.kaila.suku.report.style;

import java.awt.Font;
import java.awt.Image;
import java.util.Vector;

/**
 * Base class for styles in Suku11 reports A BodyText style element consists of
 * a vector of Text elements
 * 
 * @author Kalle
 * 
 */
public class BodyText {

	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_CENTER = 1;

	public static final String FONT_SERIF = "Times new Roman";
	public static final String FONT_SANS_SERIF = "Arial";

	protected String fontName = FONT_SANS_SERIF;
	protected int fontSize = 10;
	protected int fontStyle = Font.PLAIN;
	// protected boolean fontUnderline=false;

	protected int paraAlignment = ALIGN_LEFT;
	protected float paraIndentLeft = 6; // left alignment in pt
	protected float paraSpacingBefore = 0; // spacing before in pt
	protected float paraSpacingAfter = 6; // spacing after in pt

	protected Image image = null;

	private Vector<Text> txt = new Vector<Text>();

	public String getFontName() {
		return fontName;
	}

	/**
	 * font size in pt (1/72) inch return fontSize
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * set (different) font size
	 * 
	 * @param fontSize
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * 
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	public void reset() {
		txt = new Vector<Text>();
	}

	// public int getFontStyle() {
	// return fontStyle;
	// }

	// public boolean isFontUnderline() {
	// return fontUnderline;
	// }
	/**
	 * alignment of paragraph ALIGN_LEFT=0 ALIGN_CENTER=1
	 * 
	 * @return paraAlignment
	 */
	public int getParaAlignment() {
		return paraAlignment;
	}

	/**
	 * Left ident of paragraph in cm
	 * 
	 * @return paraIndentLeft
	 */
	public float getParaIndentLeft() {
		return paraIndentLeft;
	}

	/**
	 * Paragraph spacing in pt (1/72 inch)
	 * 
	 * @return paraSpacingBefore
	 */
	public float getParaSpacingBefore() {
		return paraSpacingBefore;
	}

	/**
	 * Paragraph spacing in pt (1/72 inch)
	 * 
	 * @return paraSpacingAfter
	 */
	public float getParaSpacingAfter() {
		return paraSpacingAfter;
	}

	/**
	 * add text string to style element
	 * 
	 * @param text
	 */
	public void addText(String text) {
		Text t = new Text(text);
		txt.add(t);
	}

	/**
	 * add text string with formatting options to style element
	 * 
	 * @param text
	 * @param isBold
	 * @param isUnderline
	 */
	public void addText(String text, boolean isBold, boolean isUnderline) {
		Text t = new Text(text);
		t.isBold = isBold;
		t.isUnderline = isUnderline;
		txt.add(t);
	}

	/**
	 * 
	 * @return text size in style element
	 */
	public int getCount() {
		return txt.size();
	}

	/**
	 * 
	 * @param idx
	 * @return text content of text element
	 */
	public String getText(int idx) {
		Text t = txt.get(idx);
		return t.text;
	}

	/**
	 * 
	 * @param idx
	 * @return true if the indexed text element is bold
	 */
	public boolean isBold(int idx) {
		if ((fontStyle & Font.BOLD) != 0)
			return true;

		Text t = txt.get(idx);
		return t.isBold;
	}

	/**
	 * 
	 * @param idx
	 * @return true if the indexed text elemet is underlined
	 */
	public boolean isUnderline(int idx) {
		Text t = txt.get(idx);
		return t.isUnderline;
	}

	private class Text {
		String text;
		boolean isBold = false;
		boolean isUnderline = false;

		Text(String text) {
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
			StringBuffer sb = new StringBuffer();

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

}
