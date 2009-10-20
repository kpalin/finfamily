package fi.kaila.suku.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author FIKAAKAIL
 * 
 *         JSuku setiing filter for selecting files
 */
public class SettingFilter extends FileFilter {

	private String[] ftype = null;

	private boolean showDirectories = true;

	/**
	 * constructor
	 * 
	 * @param filetype
	 *            or list of accepted filetype separated by semicolon (;)
	 */
	public SettingFilter(String filetype) {
		if (filetype != null) {
			this.ftype = filetype.split(";");
		}
	}

	@Override
	public boolean accept(File arg) {
		int i;

		String tmp;
		if (arg != null) {
			if (arg.isDirectory()) {
				return this.showDirectories;
			}
			if (this.ftype == null) {
				return true;
			}
			tmp = arg.getName().toLowerCase();

			for (i = 0; i < this.ftype.length; i++) {
				if (tmp.endsWith("." + this.ftype[i])) {
					return true;
				}

			}

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {

		return "SukuJ files";

	}

}
