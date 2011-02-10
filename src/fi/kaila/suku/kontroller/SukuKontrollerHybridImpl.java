package fi.kaila.suku.kontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Hybrid kontroller accesses the remote service from a locally installed
 * FinFamily
 * 
 * @author kalle
 * 
 */
public class SukuKontrollerHybridImpl implements SukuKontroller {

	private static Logger logger = null;
	private static Preferences sr = null;
	private File file = null;
	private Suku host = null;
	private final String url;
	private String userno = null;

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

			String encoding = uc.getContentEncoding();

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

		return KontrollerUtils.getSukuData(this.url, this.userno, params);

	}

	@Override
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException {
		return KontrollerUtils.getSukuData(this.url, this.userno, request,
				params);

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

		String path = openDiskFile(filter);
		if (path == null) {
			return false;
		}
		try {
			InputStream iis = new FileInputStream(path);
			int resu = KontrollerUtils.openFile(this.url, this.userno,
					getFileName(), iis);
			if (resu == 200) {
				return true;
			}
			logger.warning("openFile returnded response " + resu);
		} catch (Exception e) {
			logger.log(Level.WARNING, "open file", e);
		}
		return false;

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
