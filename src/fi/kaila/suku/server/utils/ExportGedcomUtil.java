package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.util.logging.Logger;

import fi.kaila.suku.exports.ExportGedcomDialog;
import fi.kaila.suku.util.pojo.SukuData;

public class ExportGedcomUtil {

	private Connection con;

	private ExportGedcomDialog runner = null;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private enum GedSet {
		Set_None, Set_Ascii, Set_Ansel, Set_Utf8, Set_Utf16le, Set_Utf16be
	}

	private GedSet thisSet = GedSet.Set_None;

	/**
	 * Constructor with connection
	 * 
	 * @param con
	 */
	public ExportGedcomUtil(Connection con) {
		this.con = con;
		this.runner = ExportGedcomDialog.getRunner();
	}

	public SukuData exportGedcom() {
		SukuData result = new SukuData();

		result.buffer = "This is not yet ready but it will be later".getBytes();

		return result;
	}

}
