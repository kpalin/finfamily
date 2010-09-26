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
import javax.swing.JOptionPane;

import fi.kaila.suku.server.SukuServer;
import fi.kaila.suku.server.SukuServerImpl;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class SukuKontrollerLocalImpl.
 * 
 * @author FIKAAKAIL
 * 
 *         Controller class to implement local application
 */
public class SukuKontrollerLocalImpl implements SukuKontroller {

	private static Preferences sr = null;

	private SukuServer server = null;
	private static Logger logger = null;

	private File file = null;
	private Suku host = null;

	/**
	 * Instantiates a new suku kontroller local impl.
	 * 
	 * @param host
	 *            the host
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuKontrollerLocalImpl(Suku host) throws SukuException {
		this.host = host;
		this.server = new SukuServerImpl("public");
		sr = Preferences.userRoot();
		logger = Logger.getLogger(this.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getConnection(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void getConnection(String host, String dbname, String userid,
			String passwd) throws SukuException {

		this.server.getConnection(host, dbname, userid, passwd);

	}

	/**
	 * local method for Junit.
	 */
	public void resetConnection() {
		this.server.resetConnection();
	}

	/**
	 * get stored parameter from user preferences.
	 * 
	 * @param o
	 *            (owner name)
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return value
	 */
	@Override
	public String getPref(Object o, String key, String def) {

		return sr.get(o.getClass().getName() + "." + key, def);
	}

	/**
	 * store value in user preferences.
	 * 
	 * @param o
	 *            the o
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	@Override
	public void putPref(Object o, String key, String value) {

		sr.put(o.getClass().getName() + "." + key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(String... params) throws SukuException {
		return this.server.getSukuData(params);
	}

	/**
	 * local method for Junit use only.
	 * 
	 * @param filename
	 *            the new local file
	 */
	public void setLocalFile(String filename) {
		this.server.setOpenFile(filename);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#openLocalFile(java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(fi.kaila.suku.util
	 * .pojo.SukuData, java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException {
		return this.server.getSukuData(request, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileLength()
	 */
	@Override
	public long getFileLength() {

		if (file != null) {
			return file.length();
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getInputStream()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileName()
	 */
	@Override
	public String getFileName() {
		if (file != null) {
			return file.getName();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#createLocalFile(java.lang.String)
	 */
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
		this.server.setOpenFile(filename);

		logger.info("Valittiin: " + filename);

		String tmp = f.getAbsolutePath().replace("\\", "/");
		int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws FileNotFoundException {
		if (file != null) {

			return new FileOutputStream(file);

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#openFile(java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFilePath()
	 */
	@Override
	public String getFilePath() {
		String tmp = file.getAbsolutePath().replace("\\", "/");
		return tmp;
	}

}
