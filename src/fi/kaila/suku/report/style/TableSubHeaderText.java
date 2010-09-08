package fi.kaila.suku.report.style;

/**
 * table sub header style.
 * 
 * @author Kalle
 */
public class TableSubHeaderText extends BodyText {

	/**
	 * Table subheader is usually the place with reference from where the parent
	 * table is.
	 */
	public TableSubHeaderText() {
		fontName = FONT_SERIF;
		fontSize = 12;
		paraAlignment = ALIGN_CENTER;
		paraSpacingAfter = 12;
	}
}
