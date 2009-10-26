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

	private byte[] data = null;
	private int width = 0;
	private int height = 0;
	private String imageName = null;
	private String imageTitle = null;

	/**
	 * Set the image for reports
	 * 
	 * @param img
	 * @param data
	 * @param width
	 * @param height
	 * @param name
	 */
	public void setImage(Image img, byte[] data, int width, int height,
			String name, String title) {
		image = img;
		this.data = data;
		this.width = width;
		this.height = height;
		this.imageName = name;
		this.imageTitle = title;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @return the imageTitle
	 */
	public String getImageTitle() {
		return imageTitle;
	}

}
