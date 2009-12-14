package fi.kaila.suku.kontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import fi.kaila.suku.server.SukuServer;
import fi.kaila.suku.server.SukuServerImpl;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * @author FIKAAKAIL
 * 
 *         Controller class to implement local application
 * 
 */
public class SukuKontrollerLocalImpl implements SukuKontroller {

	private static Preferences sr = null;// Preferences.userRoot();

	private SukuServer server = null;
	private static Logger logger = null;

	private File file = null;
	private File outFile = null;

	/**
	 * 
	 * 
	 * 
	 * @throws SukuException
	 */
	public SukuKontrollerLocalImpl() throws SukuException {
		this.server = new SukuServerImpl();
		sr = Preferences.userRoot();
		logger = Logger.getLogger(this.getClass().getName());
	}

	@Override
	public void getConnection(String host, String dbname, String userid,
			String passwd) throws SukuException {

		this.server.getConnection(host, dbname, userid, passwd);

	}

	/**
	 * local method for Junit
	 */
	public void resetConnection() {
		this.server.resetConnection();
	}

	/**
	 * get stored parameter from user preferences
	 * 
	 * @param o
	 *            (owner name)
	 * @param key
	 * @param def
	 * @return value
	 */
	@Override
	public String getPref(Object o, String key, String def) {

		return sr.get(o.getClass().getName() + "." + key, def);
	}

	/**
	 * store value in user preferences
	 * 
	 * @param o
	 * @param key
	 * @param value
	 */
	@Override
	public void putPref(Object o, String key, String value) {

		sr.put(o.getClass().getName() + "." + key, value);
	}

	@Override
	public SukuData getSukuData(String... params) throws SukuException {
		return this.server.getSukuData(params);
	}

	/**
	 * local method for Junit use only
	 * 
	 * @param filename
	 */
	public void setLocalFile(String filename) {
		this.server.setOpenFile(filename);
	}

	@Override
	public boolean openLocalFile(String filter) {
		// TODO Auto-generated method stub
		Preferences sr = Preferences.userRoot();

		String[] filters = filter.split(";");

		String koe = sr.get(filters[0], ".");
		logger.fine("Hakemisto on: " + koe);
		// Import2004Dialog d = new Import2004Dialog(null);
		// d.setVisible(true);

		// if (!d.isOK()) return false;

		// String langCode = d.getSelectedLang();
		// String oldCode = "FI"; //d.getSelected2004Lang();

		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Open " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		File f = chooser.getSelectedFile();
		if (f == null) {
			return false;
		}
		String filename = f.getAbsolutePath();
		file = new File(filename);
		this.server.setOpenFile(filename);

		logger.info("Valittiin: " + filename);

		String tmp = f.getAbsolutePath().replace("\\", "/");
		int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return true;
	}

	@Override
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException {
		return this.server.getSukuData(request, params);
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
	public boolean createLocalFile(String filter) {
		Preferences sr = Preferences.userRoot();

		String[] filters = filter.split(";");

		String koe = sr.get(filters[0], ".");
		logger.fine("Hakemisto on: " + koe);

		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Create " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		File f = chooser.getSelectedFile();
		if (f == null) {
			return false;
		}

		String filename = f.getAbsolutePath();
		if (filename == null)
			return false;
		if (filters.length == 1) {
			if (!filename.toLowerCase().endsWith(filters[0].toLowerCase())) {
				filename += "." + filters[0];
			}
		}

		outFile = new File(filename);
		this.server.setOpenFile(filename);

		logger.info("Valittiin: " + filename);

		String tmp = f.getAbsolutePath().replace("\\", "/");
		int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return true;
	}

	@Override
	public OutputStream getOutputStream() {
		if (outFile != null) {
			try {
				return new FileOutputStream(outFile);
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING,
						"Failed to get output stream for file", e);

			}

		}
		return null;
	}

	@Override
	public InputStream openFile(String path) {
		if (path != null) {

			if (file == null)
				return null;

			String mainPath = file.getAbsolutePath();
			int last = mainPath.replace('\\', '/').lastIndexOf('/');
			if (last < 0)
				return null;
			String absPath = mainPath.substring(0, last + 1) + path;

			try {
				return new FileInputStream(absPath);
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING, e.getMessage());

			}

		}
		return null;
	}

}
