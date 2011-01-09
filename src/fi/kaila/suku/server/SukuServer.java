package fi.kaila.suku.server;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Interface SukuServer.
 * 
 * @author FIKAAKAIL
 * 
 *         server interface
 */
public interface SukuServer {

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
	 * disconnect from dabatase.
	 */
	public void resetConnection();

	/**
	 * set open file handle for server.
	 * 
	 * @param f
	 *            the new open file
	 */
	public void setOpenFile(String f);

	/**
	 * Gets the suku data.
	 * 
	 * @param params
	 *            search parameters
	 * @return Data in SukuDAO container
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData getSukuData(String... params) throws SukuException;

	/**
	 * Gets the suku data.
	 * 
	 * @param request
	 *            the request
	 * @param params
	 *            search parameters
	 * @return Data in SukuDAO container
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException;

}
