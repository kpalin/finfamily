package fi.kaila.suku.kontroller;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * <h1>Controller interface</h1>
 * 
 * 
 * <p>
 * The UI interacts with the data using this interface There will be a separate
 * implementation for local and the webstart version
 * </p>
 * .
 * 
 * @author Kalle
 */
public interface SukuKontroller {

	/**
	 * Connect to database. Database connection
	 * 
	 * @param host
	 *            the host
	 * @param dbname
	 *            the dbname
	 * @param userid
	 *            the userid
	 * @param passwd
	 *            the passwd
	 * @throws SukuException
	 *             If connection fails this is thrown with reason for failuye
	 */
	public void getConnection(String host, String dbname, String userid,
			String passwd) throws SukuException;

	/**
	 * Reset database connection
	 */
	public void resetConnection();

	/**
	 * Gets the suku data.
	 * 
	 * @param params
	 *            variable # of request parameters
	 * @return SukuData object containing result
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData getSukuData(String... params) throws SukuException;

	/**
	 * <ul>
	 * <li>params request parameter data as a SukuData object</li>
	 * <li>param params variable # of request parameters</li>
	 * <li>return SukuData object containing result</li>
	 * <li>throws SukuException</li>
	 * </ul>
	 * .
	 * 
	 * @param request
	 *            the request
	 * @param params
	 *            the params
	 * @return the response as a SukuData "container"
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException;

	/**
	 * <h1>File management</h1>
	 * 
	 * Open of a file. This is used to enable server side to access the file.
	 * WebStart version sends file to server. Local version just opens it.
	 * 
	 * @param filter
	 *            the filter
	 * @return true if opened file selected
	 */
	public boolean openFile(String filter);

	/**
	 * <h1>Local file management</h1>.
	 * 
	 * @return length of opened file
	 */
	public long getFileLength();

	/**
	 * <h1>Local file management</h1>.
	 * 
	 * @return opened local file as an input stream
	 */
	public InputStream getInputStream();

	/**
	 * <h1>Local file management</h1>.
	 * 
	 * @return filename of opened file
	 */
	public String getFileName();

	/**
	 * this returns null for webstart.
	 * 
	 * used mainly to open the report when it has been created
	 * 
	 * @return filepath of opened file
	 */
	public String getFilePath();

	/**
	 * <h1>Local parameter management</h1>
	 * 
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
	public String getPref(Object o, String key, String def);

	/**
	 * <h1>Local parameter management</h1>
	 * 
	 * store value in user preferences.
	 * 
	 * @param o
	 *            the o
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void putPref(Object o, String key, String value);

	/**
	 * <h1>Local file management</h1>
	 * 
	 * Create a local file. To make webstart possible all local data management
	 * is made diffrently in the local Kontroller and the webstart kontrollr
	 * 
	 * @param filter
	 *            the filter
	 * @return true if file created
	 */
	public boolean createLocalFile(String filter);

	/**
	 * Method used in webstart version to save the buffer to a file on local
	 * disk
	 * 
	 * @param filter
	 * @param buffer
	 * @return
	 */
	public boolean saveFile(String filter, InputStream in);

	/**
	 * <h1>Local file management</h1>.
	 * 
	 * @return created local file as an output stream
	 * @throws FileNotFoundException
	 */
	public OutputStream getOutputStream() throws FileNotFoundException;

	/**
	 * Opening a named local file for GUI side.
	 * 
	 * @param filter
	 *            for opefile dialog
	 * 
	 * @return InputStream to the file
	 */
	public InputStream openLocalFile(String filter);

	/**
	 * 
	 * @return true if remote mode
	 */
	public boolean isRemote();

	/**
	 * 
	 * @return true if web start mode (running in sandbox)
	 */
	public boolean isWebStart();

	/**
	 * 
	 * @return true if connected to db (PostgreSQL)
	 */
	public boolean isConnected();

	/**
	 * 
	 * @return true if connected to valid family database
	 */
	public String getSchema();

	/**
	 * sets the valid family database schema
	 * 
	 * @param schema
	 */
	public void setSchema(String schema);

}
