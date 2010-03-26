package fi.kaila.suku.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

	public SukuData exportGedcom(String path) {
		SukuData result = new SukuData();
		if (path == null || path.lastIndexOf(".") < 1) {
			result.resu = "output filename missing";
		}
		String simple = path.substring(0, path.lastIndexOf("."));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ZipOutputStream zip = new ZipOutputStream(bos);
		String fileName = simple + "/" + simple + ".ged";

		ZipEntry entry = new ZipEntry(fileName);
		try {
			zip.putNextEntry(entry);
			zip.write("This is not yet ready\r\nbut it will be later"
					.getBytes());
			zip.closeEntry();
			zip.close();

			result.buffer = bos.toByteArray();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// result.buffer =
		// "This is not yet ready but it will be later".getBytes();

		return result;
	}

}
