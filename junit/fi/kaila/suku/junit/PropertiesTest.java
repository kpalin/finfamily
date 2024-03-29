package fi.kaila.suku.junit;

import java.util.MissingResourceException;

import junit.framework.TestCase;

import org.junit.Test;

import fi.kaila.suku.util.Resurses;

/**
 * 
 * Test of resourceBundle and other properties
 * 
 * @author fikaakail
 * 
 */
public class PropertiesTest extends TestCase {

	/**
	 * test that FILE exists
	 */
	@Test
	public void testFilePropertyFin() {
		Resurses.setLocale("fi");
		String file = Resurses.getString("FILE");
		assertNotNull("FILE resource not found", file);
		assertEquals("FILE väärä arvo", file, "Tiedosto");

	}

	/**
	 * test that FILE = Tiedosto in fi
	 */
	@Test
	public void testFilePropertyEn() {
		Resurses.setLocale("en");
		String file = Resurses.getString("FILE");
		assertNotNull("FILE resource not found", file);
		assertEquals("FILE väärä arvo", file, "File");

	}

	/**
	 * test that FILE = File in en
	 */
	@Test
	public void testNoexistatntProperty() {
		try {
			Resurses.getString("FILExxxxyyy");
		} catch (MissingResourceException e) {
			return;
		}
		fail("accepted non existant property");
	}

}
