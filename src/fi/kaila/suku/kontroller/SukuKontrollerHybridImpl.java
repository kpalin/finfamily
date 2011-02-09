package fi.kaila.suku.kontroller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

public class SukuKontrollerHybridImpl implements SukuKontroller {

	private static Logger logger = null;
	private static Preferences sr = null;
	private File file = null;
	private Suku host = null;
	private final String url;
	private String userno = null;
	private static final String hexi = "0123456789ABCDEF";
	private String schema = null;

	private boolean isConnected = false;

	public SukuKontrollerHybridImpl(Suku host, String url) {
		this.host = host;
		this.url = url;
		sr = Preferences.userRoot();
		logger = Logger.getLogger(this.getClass().getName());

	}

	@Override
	public void getConnection(String host, String dbname, String userid,
			String passwd) throws SukuException {
		String requri = this.url + "SukuServlet?userid=" + userid + "&passwd="
				+ passwd;
		schema = null;
		isConnected = false;
		int resu;

		try {

			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			// Iterator hf = uc.getHeaderFields().keySet().iterator();

			String encoding = uc.getContentEncoding();
			// String contentType = uc.getContentType();

			resu = uc.getResponseCode();

			if (resu == 200) {

				InputStream in;
				if ("gzip".equals(encoding)) {
					in = new java.util.zip.GZIPInputStream(uc.getInputStream());
				} else {
					in = uc.getInputStream();
				}

				byte b[] = new byte[1024];

				int pit = in.read(b);
				for (int i = 0; i < pit; i++) {
					if (b[i] == '\n' || b[i] == '\r') {
						pit = i;
						break;
					}
				}
				String aux = new String(b, 0, pit);

				String auxes[] = aux.split("/");

				this.userno = auxes[0];
				if (auxes.length > 1) {
					Suku.serverVersion = auxes[1];
				}

				in.close();
				isConnected = true;
				schema = userid;
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			throw new SukuException(Resurses.getString("ERR_NOT_CONNECTED")
					+ " [" + e.toString() + "]");
		}

	}

	@Override
	public void resetConnection() {
		isConnected = false;

	}

	@Override
	public SukuData getSukuData(String... params) throws SukuException {
		StringBuilder sb = new StringBuilder();
		sb.append(this.url);
		String requri;
		int resu;
		int i;
		SukuData errr = new SukuData();
		errr.resu = "ERROR";

		if (this.userno == null) {
			return errr;
		}
		String paras[] = params;

		sb.append("SukuServlet?userno=" + this.userno);

		for (i = 0; i < paras.length; i++) {
			sb.append("&" + paras[i]);
		}

		requri = sb.toString();
		try {

			logger.fine("URILOG: " + requri);
			Utils.println(this, "URILOG: " + requri);
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
				Utils.println(this, xx.toString());

				InputStream in = null;
				if ("gzip".equals(coding)) {
					in = new GZIPInputStream(uc.getInputStream());
				} else {
					in = uc.getInputStream();
				}
				Utils.println(this, "content coding is " + coding);
				// InputStream in = uc.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(in);
				SukuData fam = null;
				try {
					fam = (SukuData) ois.readObject();
					Utils.println(this, "Sukudata received is " + fam);
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new SukuException(e);
				}

				return fam;

			}
			throw new SukuException("Network error " + resu);

		} catch (Exception e) {
			Utils.println(this, "Ilman dataa failed: " + e.toString());

			throw new SukuException(e);
		}

	}

	@Override
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException {
		SukuData errr = new SukuData();
		errr.resu = "ERROR";
		if (this.userno == null) {
			return errr;
		}

		if (request == null) {
			return getSukuData(params);
		}
		StringBuilder query = new StringBuilder();

		String paras[] = params;
		try {
			query.append("userno=" + this.userno);

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

			String uri = this.url + "SukuServlet";

			byte[] bytes = query.toString().getBytes();

			URL url = new URL(uri);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			con.setRequestProperty("Referer", "/SSS/" + this.userno + "/");
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
			Utils.println(this, "withData getting response now");

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
				Utils.println(this, e.toString());

				throw new SukuException(e);
			}

			return fam;

		} catch (Throwable e) {
			Utils.println(this, "Data failed+" + e.toString());

			throw new SukuException(e);

		}

	}

	private String openDiskFile(String filter) {
		Preferences sr = Preferences.userRoot();

		String[] filters = filter.split(";");

		String koe = sr.get(filters[0], ".");
		logger.fine("Hakemisto on: " + koe);

		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Open " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		File f = chooser.getSelectedFile();
		if (f == null) {
			return null;
		}
		String filename = f.getAbsolutePath();
		file = new File(filename);

		logger.info("Valittiin: " + filename);

		String tmp = f.getAbsolutePath().replace("\\", "/");
		int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return tmp;
	}

	@Override
	public boolean openFile(String filter) {

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		DataOutputStream dos = null;
		InputStream iis = null;

		int resu;

		try {

			String path = openDiskFile(filter);
			if (path == null) {
				return false;
			}
			iis = new FileInputStream(path);
			// iis = openLocalFile(filter);
			String uri = this.url;
			String query;
			uri += "SukuServlet";
			query = "cmd=file";

			byte[] bytes = query.getBytes();

			URL url = new URL(uri);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			con.setRequestProperty("Referer", "/SSS/" + this.userno + "/"
					+ getFileName() + "/");
			con.setRequestProperty("Content-Length",
					String.valueOf(bytes.length));
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

			resu = con.getResponseCode();
			in.close();
			dos.close();
			con.disconnect();

			Utils.println(this, "resu: " + resu + "/" + inle);
		} catch (Exception e) {
			Utils.println(this, "w/o data " + e.toString());
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public long getFileLength() {
		if (file != null) {
			return file.length();
		}
		return 0;
	}

	@Override
	public InputStream getInputStream() {
		if (file != null) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING,
						"Failed to get input stream for file", e);
			}
		}
		return null;
	}

	@Override
	public String getFileName() {
		if (file != null) {
			return file.getName();
		}
		return null;
	}

	@Override
	public String getFilePath() {
		return file.getAbsolutePath().replace("\\", "/");
	}

	@Override
	public String getPref(Object o, String key, String def) {
		return sr.get(o.getClass().getName() + "." + key, def);
	}

	@Override
	public void putPref(Object o, String key, String value) {
		sr.put(o.getClass().getName() + "." + key, value);
	}

	@Override
	public boolean createLocalFile(String filter) {
		Preferences sr = Preferences.userRoot();

		String[] filters = filter.split(";");

		String koe = sr.get(filters[0], ".");
		logger.fine("Hakemisto on: " + koe);

		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Create " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showSaveDialog(host) != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		File f = chooser.getSelectedFile();
		if (f == null) {
			return false;
		}

		String filename = f.getAbsolutePath();
		if (filename == null)
			return false;
		if (f.exists()) {
			int answer = JOptionPane.showConfirmDialog(host,
					Resurses.getString("FILE_EXISTS") + " [" + filename
							+ "] \n" + Resurses.getString("FILE_OVERWRITE"),
					Resurses.getString(Resurses.SUKU),
					JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		if (filters.length == 1) {
			if (!filename.toLowerCase().endsWith(filters[0].toLowerCase())) {
				filename += "." + filters[0];
			}
		}

		file = new File(filename);
		// this.server.setLocalFile(filename);

		logger.info("Valittiin: " + filename);

		String tmp = f.getAbsolutePath().replace("\\", "/");
		int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return true;
	}

	@Override
	public boolean saveFile(String filter, InputStream in) {
		if (createLocalFile(filter)) {
			try {
				byte[] buffer = new byte[1024];

				int readBytes = 0;
				FileOutputStream fos = new FileOutputStream(file);
				while ((readBytes = in.read(buffer)) >= 0) {
					fos.write(buffer, 0, readBytes);
				}

				in.close();
				fos.close();
				return true;
			} catch (Exception e) {
				logger.log(Level.INFO, "saveFile", e);

			}
		}

		return false;
	}

	@Override
	public OutputStream getOutputStream() throws FileNotFoundException {
		if (file != null) {
			return new FileOutputStream(file);
		}
		return null;
	}

	@Override
	public InputStream openLocalFile(String path) {
		if (openFile(path)) {
			if (path != null) {
				if (file == null)
					return null;

				String mainPath = file.getAbsolutePath();

				try {
					return new FileInputStream(mainPath);
				} catch (FileNotFoundException e) {
					logger.log(Level.WARNING, e.getMessage());

				}
			}
		}
		return null;
	}

	@Override
	public boolean isRemote() {

		return true;
	}

	@Override
	public boolean isWebStart() {
		return false;
	}

	@Override
	public boolean isConnected() {

		return isConnected;
	}

	@Override
	public String getSchema() {

		return schema;
	}

	@Override
	public void setSchema(String schema) {

	}

}
