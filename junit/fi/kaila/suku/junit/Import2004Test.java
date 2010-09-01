package fi.kaila.suku.junit;

import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.junit.Test;

import fi.kaila.suku.kontroller.SukuKontrollerLocalImpl;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * tests for import from 2004
 * 
 * @author fikaakail
 * 
 */
public class Import2004Test extends TestCase {

	private String userid = null;
	private String password = null;
	private String dbname = null;
	private String host = null;
	private String filename = null;

	@Override
	protected void setUp() {
		ResourceBundle resources = ResourceBundle
				.getBundle("properties/junittests");

		this.userid = resources
				.getString("fi.kaila.suku.junit.Import2004Test.userid");
		this.password = resources
				.getString("fi.kaila.suku.junit.Import2004Test.password");
		this.host = resources
				.getString("fi.kaila.suku.junit.Import2004Test.host");
		this.dbname = resources
				.getString("fi.kaila.suku.junit.Import2004Test.dbname");
		this.filename = resources
				.getString("fi.kaila.suku.junit.Import2004Test.filename");

	}

	/**
	 * test import of suku 2004 backup
	 * 
	 * @throws SukuException
	 */
	@Test
	public void testImportTesti() throws SukuException {

		// SukuServer server = new SukuServerImpl();
		SukuKontrollerLocalImpl kontroller = new SukuKontrollerLocalImpl(null);

		kontroller.getConnection(this.host, this.dbname, this.userid,
				this.password);
		kontroller.setLocalFile(this.filename);

		kontroller.getSukuData("cmd=import", "type=backup", "lang=FI");
		// server.import2004Data("db/" + this.filename, "FI");

		SukuData data = kontroller.getSukuData("cmd=family", "pid=3");
		assertNotNull("Family must not be null");

		PersonShortData owner = data.pers[0];

		assertNotNull("Owner of family must not be null");

		assertTrue("Wrong ownere", owner.getGivenname().startsWith("Kaarle"));
		kontroller.resetConnection();

	}

}
