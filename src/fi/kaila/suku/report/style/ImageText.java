package fi.kaila.suku.report.style;

import java.awt.Image;

import fi.kaila.suku.util.Utils;

/**
 * Images are implemented as this special styles.
 * 
 * @author Kalle
 */
public class ImageText extends BodyText {

	/**
	 * Style contains only an image with optional title.
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
	 * Set the image for reports.
	 * 
	 * @param img
	 *            the img
	 * @param data
	 *            the data
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param name
	 *            the name
	 * @param title
	 *            the title
	 * @param tag
	 *            the tag
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
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the image name.
	 * 
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * Gets the image title.
	 * 
	 * @return the imageTitle
	 */
	public String getImageTitle() {
		return imageTitle;
	}

	/**
	 * Checks if is person image.
	 * 
	 * @return the isPersonImage
	 */
	public boolean isPersonImage() {
		return isPersonImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return imageName + ":" + imageTitle;

	}

}
