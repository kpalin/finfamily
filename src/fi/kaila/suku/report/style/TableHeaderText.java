package fi.kaila.suku.report.style;

import java.awt.Font;

/**
 * table header style
 * 
 * @author Kalle
 * 
 */
public class TableHeaderText extends BodyText {
	/**
	 * The table header style
	 */
	public TableHeaderText() {
		fontName = FONT_SERIF;
		fontSize = 14;
		fontStyle = Font.BOLD;
		paraAlignment = ALIGN_LEFT;
		paraSpacingAfter = 12;
		paraSpacingBefore = 12;
	}
}
