package fi.kaila.suku.report.style;

import java.awt.Image;

import fi.kaila.suku.util.Utils;

/**
 * Images are implemented as this special styles
 * 
 * @author Kalle
 * 
 */
public class ImageText extends BodyText {

	/**
	 * Style contains only an image with optional title
	 */
	public ImageText() {
		fontName = FONT_SERIF;
		paraAlignment = ALIGN_CENTER;
	}

	private boolean isPersonImage = true;
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
	 * @param title
	 * @param tag
	 */
	public void setImage(Image img, byte[] data, int width, int height,
			String name, String title, String tag) {
		image = img;
		this.data = data;
		this.width = width;
		this.height = height;
		this.imageName = name;
		this.imageTitle = title;
		this.isPersonImage = Utils.nv(tag).equals("PHOT") ? true : false;

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

	/**
	 * @return the isPersonImage
	 */
	public boolean isPersonImage() {
		return isPersonImage;
	}

}
