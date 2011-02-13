package fi.kaila.suku.kontroller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

public class KontrollerUtils {

	private static final String hexi = "0123456789ABCDEF";
	private static Logger logger = Logger.getLogger(KontrollerUtils.class
			.getName());

	public static int openFile(String uri, String userno, String filename,
			InputStream iis) throws IOException {

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		DataOutputStream dos = null;

		String query;
		uri += "SukuServlet";
		query = "cmd=file";

		byte[] bytes = query.getBytes();

		URL url = new URL(uri);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ boundary);

		con.setRequestProperty("Referer", "/SSS/" + userno + "/" + filename
				+ "/");
		con.setRequestProperty("Content-Length", String.valueOf(bytes.length));
		con.setRequestMethod("POST");

		dos = new DataOutputStream(con.getOutputStream());

		dos.write(bytes);
		dos.writeBytes(lineEnd);
		dos.writeBytes(lineEnd);

		int nextByte;

		StringBuilder rivi = new StringBuilder();

		while ((nextByte = iis.read()) >= 0) {
			if (rivi.length() > 64) {
				dos.writeBytes(rivi.toString() + lineEnd);
				rivi = new StringBuilder();
			}
			rivi.append(hexi.charAt((nextByte >> 4) & 0xf));
			rivi.append(hexi.charAt((nextByte) & 0xf));

		}

		if (rivi.length() > 0) {
			dos.writeBytes(rivi.toString() + lineEnd);
		}

		// send multipart form data necesssary after file data...

		dos.writeBytes(lineEnd);
		dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		// close streams

		dos.flush();
		dos.close();

		// Read the response
		InputStream in = con.getInputStream();
		int inle = 0;
		while (true) {
			int idata = in.read();
			if (idata == -1)
				break;
			inle++;
		}

		int resu = con.getResponseCode();
		in.close();
		dos.close();
		con.disconnect();

		return resu;
	}

	public static SukuData getSukuData(String uri, String userno,
			String... params) throws SukuException {
		StringBuilder sb = new StringBuilder();
		sb.append(uri);
		String requri;
		int resu;
		int i;
		SukuData errr = new SukuData();
		errr.resu = "ERROR";

		if (userno == null) {
			return errr;
		}
		String paras[] = params;

		sb.append("SukuServlet?userno=" + userno);

		for (i = 0; i < paras.length; i++) {
			sb.append("&" + paras[i]);
		}

		requri = sb.toString();
		try {

			logger.fine("URILOG: " + requri);

			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();

			resu = uc.getResponseCode();

			if (resu == 200) {
				String coding = uc.getHeaderField("Content-Encoding");

				StringBuilder xx = new StringBuilder();
				xx.append("Content-Encoding: " + coding);
				xx.append(";");
				for (int j = 0; j < params.length; j++) {
					xx.append(params[j]);
					xx.append(";");
				}

				InputStream in = null;
				if ("gzip".equals(coding)) {
					in = new GZIPInputStream(uc.getInputStream());
				} else {
					in = uc.getInputStream();
				}

				ObjectInputStream ois = new ObjectInputStream(in);
				SukuData fam = null;
				try {
					fam = (SukuData) ois.readObject();

					in.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new SukuException(e);
				}

				return fam;

			}
			throw new SukuException("Network error " + resu);

		} catch (Exception e) {

			throw new SukuException(e);
		}

	}

	public static SukuData getSukuData(String uri, String userno,
			SukuData request, String... params) throws SukuException {
		SukuData errr = new SukuData();
		errr.resu = "ERROR";
		if (userno == null) {
			return errr;
		}

		if (request == null) {
			return getSukuData(uri, userno, params);
		}
		StringBuilder query = new StringBuilder();

		String paras[] = params;
		try {
			query.append("userno=" + userno);

			for (int i = 0; i < paras.length; i++) {
				query.append("&" + URLEncoder.encode(paras[i], "UTF-8"));
			}

			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";
			DataOutputStream dos = null;

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buff = null;

			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(request);
			buff = bos.toByteArray();
			oos.close();

			InputStream gis = new ByteArrayInputStream(buff);

			String urix = uri + "SukuServlet";

			byte[] bytes = query.toString().getBytes();

			URL url = new URL(urix);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			con.setRequestProperty("Referer", "/SSS/" + userno + "/");
			con.setRequestProperty("Content-Length",
					String.valueOf(bytes.length));
			con.setRequestMethod("POST");

			dos = new DataOutputStream(con.getOutputStream());

			dos.write(bytes);
			dos.writeBytes(lineEnd);

			dos.writeBytes(lineEnd);

			int nextByte;

			StringBuilder rivi = new StringBuilder();

			while ((nextByte = gis.read()) >= 0) {
				if (rivi.length() > 64) {
					dos.writeBytes(rivi.toString() + lineEnd);
					rivi = new StringBuilder();
				}
				rivi.append(hexi.charAt((nextByte >> 4) & 0xf));
				rivi.append(hexi.charAt((nextByte) & 0xf));

			}

			if (rivi.length() > 0) {
				dos.writeBytes(rivi.toString() + lineEnd);
			}

			// send multipart form data necesssary after file data...

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams

			dos.flush();
			dos.close();

			// Read the response

			String coding = con.getHeaderField("Content-Encoding");

			InputStream in = null;
			if ("gzip".equals(coding)) {
				in = new GZIPInputStream(con.getInputStream());
			} else {
				in = con.getInputStream();
			}

			ObjectInputStream ois = new ObjectInputStream(in);
			SukuData fam = null;
			try {
				fam = (SukuData) ois.readObject();
				ois.close();
			} catch (Exception e) {

				throw new SukuException(e);
			}

			return fam;

		} catch (Throwable e) {

			throw new SukuException(e);

		}

	}

}
