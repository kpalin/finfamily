package fi.kaila.suku.server;


import fi.kaila.suku.util.SukuException;

import fi.kaila.suku.util.pojo.SukuData;

/**
 * @author FIKAAKAIL
 *
 *server interface
 */
public interface SukuServer {
	/**
	 * Connect to database. Database connection 
	 * 
	 * @param host
	 * @param dbname 
	 * @param userid
	 * @param passwd
	 * @throws SukuException If connection fails this is thrown with reason for failuye
	 */
	public void getConnection(String host,String dbname,String userid, String passwd) throws SukuException;
	
	/**
	 * disconnect from dabatase
	 */
	public void resetConnection(); 
	
	
	/**
	 * set open file handle for server
	 * @param f
	 */
	public void setOpenFile(String f);
	
	/**
	 * @param params search parameters
	 * @return Data in SukuDAO container
	 * @throws SukuException
	 */
	public SukuData   getSukuData(String... params) throws SukuException;

	/**
	 * @param params search parameters
	 * @return Data in SukuDAO container
	 * @throws SukuException
	 */
	public SukuData   getSukuData(SukuData request,String... params) throws SukuException;
	

	


	
	
}
