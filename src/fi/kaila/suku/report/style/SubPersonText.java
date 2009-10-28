package fi.kaila.suku.report.style;

/**
 * Sub person style
 * 
 * @author Kalle
 * 
 */
public class SubPersonText extends BodyText {
	/**
	 * Subperson is spouses or childrens parents
	 */
	public SubPersonText() {
		fontName = FONT_SERIF;
		fontSize = 8;
		// fontStyle=Font.BOLD;
		// paraAlignment=ALIGN_CENTER;
		paraIndentLeft = 36;
		paraSpacingAfter = 3;
	}
}
