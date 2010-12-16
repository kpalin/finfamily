package fi.kaila.suku.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import fi.kaila.suku.ant.AntVersion;
import fi.kaila.suku.swing.Suku;

/**
 * 
 * Called to check the version of calling program against version in file at
 * http://www.sukuohjelmisto.fi/version/version.properties
 * 
 * @author kalle
 * 
 */
public class VersionChecker {

	private Suku suku = null;
	private Logger logger = null;

	public VersionChecker(Suku suku) {
		logger = Logger.getLogger(this.getClass().getName());
		this.suku = suku;
		runMe();
	}

	private void runMe() {

		//
		// first decide if now is good time to check
		//
		String lastRevision = Suku.kontroller.getPref(this, "Revision", "0");
		String lastTry = Suku.kontroller.getPref(this, "lastTime", "0");
		String ant = AntVersion.antVersion;

		// System.out.println("here is " + ant);

		String requri = "http://www.sukuohjelmisto.fi/version/version.properties";

		long nowTime = System.currentTimeMillis();

		long lastTime = 0;
		try {
			lastTime = Long.parseLong(lastTry);
		} catch (NumberFormatException ne) {
			logger.info("failed to parse lastTry " + lastTry);
			return;
		}

		if (lastTime + (24 * 60 * 60 * 1000) > nowTime) {
			return;
		}
		Suku.kontroller.putPref(this, "lastTime", "" + nowTime);
		int resu;
		String serverVer = null;
		String serverRevision = null;
		try {

			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();

			resu = uc.getResponseCode();
			if (resu == 200) {

				InputStream in = uc.getInputStream();

				byte b[] = new byte[2048];

				int pit = in.read(b);

				String aux = new String(b, 0, pit);

				String auxes[] = aux.split("\n");

				for (int i = 0; i < auxes.length; i++) {
					String parts[] = auxes[i].split("=");
					if (parts.length == 2) {
						int plen = parts[1].length();
						if (parts[1].charAt(plen - 1) == '\r' && plen > 1) {
							parts[1] = parts[1].substring(0, plen - 1);
						}

						if (parts[0].equalsIgnoreCase("app.version")) {
							serverVer = parts[1];
						}
						if (parts[0].equalsIgnoreCase("revision.version")) {
							serverRevision = parts[1];
						}

					}
				}

				in.close();

			}

		} catch (Exception e) {

			logger.info(e.toString());

		}

		if (serverRevision == null) {
			return;
		}
		int currRev = 0;
		int serRev = 0;
		int lastRev = 0;
		int revDot = ant.lastIndexOf(".");
		try {
			currRev = Integer.parseInt(ant.substring(revDot + 1));
			serRev = Integer.parseInt(serverRevision);
			lastRev = Integer.parseInt(lastRevision);
		} catch (NumberFormatException ne) {
			return;
		}
		Suku.kontroller.putPref(this, "Revision", "" + serRev);
		if (lastRev >= serRev) {
			return;
		}

		if (serRev > currRev) {
			int resux = JOptionPane.showConfirmDialog(
					suku,
					Resurses.getString("CONFIRM_DOWNLOAD") + " [" + serverVer
							+ "." + serverRevision + "]\n"
							+ Resurses.getString("CONFIRM_NEW") + " [" + ant
							+ "]\n" + Resurses.getString("CONFIRM_GO"),
					Resurses.getString(Resurses.SUKU),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (resux == JOptionPane.YES_OPTION) {
				String updateSite = "https://sourceforge.net/projects/finfamily/";
				Utils.openExternalFile(updateSite);
			}
		}

	}

}
