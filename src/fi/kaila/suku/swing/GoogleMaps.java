package fi.kaila.suku.swing;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * Retrieves static images from Google Maps service
 * 
 * @author halonmi
 * 
 */
public class GoogleMaps {

	/**
	 * @param key
	 */
	public GoogleMaps(String key) {
	}

	/**
	 * 
	 * Retrieves static images from Google Maps service
	 * 
	 * @param width
	 * @param height
	 * @param zoom
	 * @param format
	 * @param markers
	 * @return
	 * @throws IOException
	 */
	public Image retrieveStaticImage(int width, int height, int zoom,
			String format, String markers) throws IOException {
		byte[] imageData = loadHttpFile(getMapUrl(width, height, zoom, format,
				markers));
		return Toolkit.getDefaultToolkit().createImage(imageData, 0,
				imageData.length);
	}

	private String getMapUrl(int width, int height, int zoom, String format,
			String markers) {
		return "http://maps.google.com/maps/api/staticmap?format=" + format
				+ "&zoom=" + zoom + "&size=" + width + "x" + height + markers
				+ "&sensor=false";
	}

	private static byte[] loadHttpFile(String url) throws IOException {
		byte[] byteBuffer;
		URL serverAddress = new URL(url);
		HttpURLConnection hc = (HttpURLConnection) serverAddress
				.openConnection();
		hc.setRequestMethod("GET");
		hc.setDoOutput(true);
		hc.setReadTimeout(10000);
		hc.connect();
		try {
			BufferedInputStream in = new BufferedInputStream(
					hc.getInputStream());
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
			int c;
			while ((c = in.read()) != -1) {
				byteArrayOut.write(c);
			}
			byteBuffer = byteArrayOut.toByteArray();
		} finally {
			hc.disconnect();
		}
		return byteBuffer;
	}
}