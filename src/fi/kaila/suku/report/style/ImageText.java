package fi.kaila.suku.report.style;

import java.awt.Image;

/**
 * Images are implemented as this special styles
 * 
 * @author Kalle
 * 
 */
public class ImageText extends BodyText {

	public ImageText() {
		fontName = FONT_SERIF;
		paraAlignment = ALIGN_CENTER;
	}

	public void setImage(Image img) {
		image = img;
	}

}
