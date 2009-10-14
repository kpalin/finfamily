package fi.kaila.suku.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * User verification utilities
 * 
 * @author FIKAAKAIL
 *
 */
public class UserVerifier {

	
	/**
	 * Verifies the user passwd
	 * 
	 * @param passwd
	 * @param mdPasswd
	 * @return true if passwd matched mdPasswd
	 * @throws SukuException 
	 */
	public boolean verifyPassword(String passwd,String mdPasswd) throws SukuException{
		
		
		String encrypted = encryptPassword(passwd);
		if (encrypted.equals(mdPasswd)) return true;
		return false;
		
		

	}
	
	/**
	 * 
	 * Encrypt password using md5 algorithm
	 * 
	 * @param passwd
	 * @return encrypted passwd
	 * @throws SukuException
	 */
	public String encryptPassword(String passwd) throws SukuException{
		byte[] tunnus = passwd.getBytes();						
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new SukuException(e);
			
		}						
		md.update(tunnus);
		byte digest[] = md.digest();
		
		StringBuffer pw = new StringBuffer();
		
		for (byte b: digest) {
			String a = "00" + Integer.toHexString(b & 0xff);
			
			pw.append(a.substring(a.length()-2));
		}
		return pw.toString();
	}
	
	
}
