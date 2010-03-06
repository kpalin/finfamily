package fi.kaila.suku.report.style;

/**
 * @author Kalle
 * 
 *         Text style for table / family texts
 */
public class TableText extends BodyText {

	/**
	 * This is the style used for the table owner and spouses
	 */
	public TableText() {
		fontName = FONT_SERIF;
		fontSize = 12;
		// fontStyle=Font.BOLD;
		// paraAlignment=ALIGN_CENTER;
		paraSpacingBefore = 12;
		paraSpacingAfter = 12;
	}

}
