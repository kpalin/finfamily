package fi.kaila.suku.kontroller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * @author FIKAAKAIL
 * 
 *         remote implementation for kontroller
 */
public class SukuKontrollerWebstartImpl implements SukuKontroller {

	private static Logger logger = null;

	private String filename = null;

	// public void createSukuDb() throws SukuException {
	// // TODO Auto-generated method stub
	//		
	// }

	/**
	 * constructor sets environment for remote
	 */
	public SukuKontrollerWebstartImpl() {
		logger = Logger.getLogger(this.getClass().getName());
		try {
			BasicService bs = (BasicService) ServiceManager
					.lookup("javax.jnlp.BasicService");
			if (bs != null) {

				this.codebase = bs.getCodeBase().toString();
				// System.out.println("codebase=" + this.codebase);
			}

		} catch (UnavailableServiceException e) {
			// System.out.println("Basic service error "+ e);
			this.codebase = "http://localhost/suku/";
			this.isWebStart = false;
			sr = Preferences.userRoot();
			// JOptionPane.showMessageDialog(null, "Basic service error " +
			// e.toString());
			// throw new SukuException("Basic service error ", e);
		}
	}

	private static Preferences sr = null;// Preferences.userRoot();
	private String codebase = null;
	private String userno = null;
	private boolean isWebStart = true;

	// private String oldCode="FI";

	@Override
	public void getConnection(String host, String dbname, String userid,
			String passwd) throws SukuException {

		String requri = this.codebase + "suku?userid=" + userid + "&passwd="
				+ passwd;

		int resu;

		try {

			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			// Iterator hf = uc.getHeaderFields().keySet().iterator();

			String encoding = uc.getContentEncoding();
			// String contentType = uc.getContentType();

			resu = uc.getResponseCode();
			if (resu == 200) {

				InputStream in;
				if ("gzip".equals(encoding)) {
					in = new java.util.zip.GZIPInputStream(uc.getInputStream());
				} else {
					in = uc.getInputStream();
				}

				byte b[] = new byte[1024];

				int pit = in.read(b);
				for (int i = 0; i < pit; i++) {
					if (b[i] == '\n' || b[i] == '\r') {
						pit = i;
						break;
					}
				}
				String aux = new String(b, 0, pit);
				if (aux != null) {
					String auxes[] = aux.split("/");

					this.userno = auxes[0];
					if (auxes.length > 1) {
						Suku.serverVersion = auxes[1];
					}

				}
				// System.out.println("oli se: " + pit + "='" + this.userno +
				// "'");
				in.close();

			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static final String hexi = "0123456789ABCDEF";

	// /**
	// * import suku 2004 backup from local filesystem
	// * @throws SukuException
	// */
	//	
	// public void import2004Data() throws SukuException {
	//
	// FileOpenService fos;
	// String lineEnd = "\r\n";
	// String twoHyphens = "--";
	// String boundary = "*****";
	// DataOutputStream dos = null;
	// InputStream iis=null;
	// try {
	// fos =
	// (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
	// } catch (UnavailableServiceException e) {
	// fos = null;
	// }
	// FileContents fc =null;
	// String fileName = null;
	//
	// if (fos != null) {
	// try {
	// // ask user to select a file through this service
	// fc = fos.openFileDialog(null, null);
	// if (fc == null) {
	// return;
	// }
	// fileName = fc.getName();
	// iis = fc.getInputStream();
	// // ask user to select multiple files through this service
	// // FileContents[] fcs = fos.openMultiFileDialog(null, null);
	// } catch (Exception e) {
	// e.printStackTrace();
	// return;
	// }
	// }
	//
	//
	//
	// int resu;
	// try {
	// String uri = this.codebase;
	// String query;
	//
	// uri += "suku";
	// // query = "intres";
	// query = "cmd=fff";
	// query += "&TRM_TP=" + URLEncoder.encode("Kaila", "UTF-8");
	//
	//
	//
	//
	// byte[] bytes = query.getBytes();
	//
	// URL url = new URL(uri);
	// HttpURLConnection con = (HttpURLConnection) url.openConnection();
	// con.setDoOutput(true);
	// con.setDoInput(true);
	// // con.setRequestProperty(
	// // "Content-Type",
	// // "application/x-www-form-urlencoded; charset=UTF-8");
	//
	// con.setRequestProperty("Content-Type",
	// "multipart/form-data;boundary="+boundary);
	//
	// con.setRequestMethod("POST");
	// con.setRequestProperty("Referer", "/SSS/"+this.userno +"/"+ fileName +
	// "/");
	// con.setRequestProperty("Content-Length", String.valueOf(bytes.length));
	// dos = new DataOutputStream( con.getOutputStream() );
	// dos.writeBytes(twoHyphens + boundary + lineEnd);
	// dos.writeBytes("Content-Disposition: form-data; name=\"upload\";"
	// + " filename=\"" + fileName +"\"" + lineEnd);
	// dos.writeBytes(lineEnd);
	//
	// // dos.write(bytes);
	// // dos.writeBytes(lineEnd);
	//
	// int nextByte;
	//
	// StringBuilder rivi = new StringBuilder();
	// while ((nextByte = iis.read()) >= 0){
	// if (rivi.length() > 64){
	// dos.writeBytes(rivi.toString() + lineEnd);
	// rivi = new StringBuilder();
	// }
	// rivi.append(hexi.charAt((nextByte >> 4 )&0xf));
	// rivi.append(hexi.charAt((nextByte )&0xf));
	//
	// }
	//
	// if (rivi.length() > 0){
	// dos.writeBytes(rivi.toString() + lineEnd);
	// }
	//
	//
	//
	// // send multipart form data necesssary after file data...
	//
	// dos.writeBytes(lineEnd);
	// dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	//
	// // close streams
	//
	//
	// dos.flush();
	// dos.close();
	//
	//
	// // Read the response
	// InputStream in = con.getInputStream();
	// int inle=0;
	// while (true) {
	// int idata = in.read();
	// if (idata == -1)
	// break;
	// inle++;
	// }
	//
	// resu = con.getResponseCode();
	// in.close();
	// dos.close();
	// con.disconnect();
	// logger.finest("resu: " + resu + "/" + inle);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	//
	//
	// this.getSukuData("cmd=import","file="+fileName,"lang=" + this.oldCode);
	//
	//
	// }

	@Override
	public String getPref(Object o, String key, String def) {

		if (!this.isWebStart) {
			return sr.get(o.getClass().getName() + "." + key, def);
		}

		// java.net.URL url = javax.jnlp.BasicService

		// for (int i = 0;i < nimet.length;i++) {
		// if (nimet[i].equals(key)) return values[i];
		// }
		// return null;
		String aux;

		try {
			PersistenceService ps = (PersistenceService) ServiceManager
					.lookup("javax.jnlp.PersistenceService");
			BasicService bs = (BasicService) ServiceManager
					.lookup("javax.jnlp.BasicService");
			URL baseURL = bs.getCodeBase();
			URL editorURL = new URL(baseURL, key);
			FileContents fc = ps.get(editorURL);
			DataInputStream is = new DataInputStream(fc.getInputStream());
			aux = is.readUTF();
			is.close();
			// System.out.println("Luki: " + key + "/" + aux);
			return aux;
		} catch (FileNotFoundException fe) {
			// System.out.println("NOT FOUND: key=" + key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	@Override
	public void putPref(Object o, String key, String value) {
		// System.out.println("key=" + key + "/ value=" + value);
		if (!this.isWebStart) {
			sr.put(o.getClass().getName() + "." + key, value);
			return;
		}
		PersistenceService ps;
		try {
			ps = (PersistenceService) ServiceManager
					.lookup("javax.jnlp.PersistenceService");

			BasicService bs = (BasicService) ServiceManager
					.lookup("javax.jnlp.BasicService");
			URL baseURL = bs.getCodeBase();
			// System.out.println("CodeBase was " + baseURL);
			URL keyURL = new URL(baseURL, key);
			FileContents fc = null;
			try {
				fc = ps.get(keyURL);
				ps.delete(keyURL);
			} catch (FileNotFoundException fe) {

				// System.out.println("not ff ww: " + key);

			}
			ps.create(keyURL, 1024);
			fc = ps.get(keyURL);

			// FileContents fc = ps.get(keyURL);
			DataOutputStream os = new DataOutputStream(fc
					.getOutputStream(false));

			os.writeUTF(value);
			os.flush();
			os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public SukuData getSukuData(String... params) throws SukuException {
		StringBuilder sb = new StringBuilder();
		sb.append(this.codebase);
		String requri;
		int resu;
		int i;
		// String paras[] = new String[params.length];
		// "suku?userno="+this.userno+"&person=" + pid

		String paras[] = params;

		sb.append("suku?userno=" + this.userno);

		for (i = 0; i < paras.length; i++) {
			sb.append("&" + paras[i]);
		}
		if (this.filename != null) {
			sb.append("&filename=" + this.filename);
		}

		requri = sb.toString();
		try {

			// requri += para;
			System.out.println("URI on: " + requri);
			logger.fine("URILOG: " + requri);
			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			// String encoding = uc.getContentEncoding();

			resu = uc.getResponseCode();
			System.out.println("Resu = " + resu);
			if (resu == 200) {

				InputStream in = new GZIPInputStream(uc.getInputStream());
				ObjectInputStream ois = new ObjectInputStream(in);
				SukuData fam = null;
				try {
					fam = (SukuData) ois.readObject();
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new SukuException(e);
				}
				return fam;

			}
			throw new SukuException("Network error " + resu);

		} catch (Exception e) {
			throw new SukuException(e);
		}
	}

	@Override
	public boolean openLocalFile(String filter) {
		this.filename = null;
		FileOpenService fos;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		DataOutputStream dos = null;
		InputStream iis = null;
		try {
			fos = (FileOpenService) ServiceManager
					.lookup("javax.jnlp.FileOpenService");
		} catch (UnavailableServiceException e) {
			fos = null;
		}
		FileContents fc = null;
		String fileName = null;

		if (fos != null) {
			try {
				// ask user to select a file through this service
				fc = fos.openFileDialog(null, null);
				if (fc == null) {
					return false;
				}
				fileName = fc.getName();
				iis = fc.getInputStream();
				// ask user to select multiple files through this service
				// FileContents[] fcs = fos.openMultiFileDialog(null, null);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		int resu;
		try {
			String uri = this.codebase;
			String query;

			uri += "suku";
			// query = "intres";
			query = "cmd=fff";
			query += "&TRM_TP=" + URLEncoder.encode("Kaila", "UTF-8");

			byte[] bytes = query.getBytes();

			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			// con.setRequestProperty(
			// "Content-Type",
			// "application/x-www-form-urlencoded; charset=UTF-8");

			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			con.setRequestMethod("POST");
			con.setRequestProperty("Referer", "/SSS/" + this.userno + "/"
					+ fileName + "/");
			con.setRequestProperty("Content-Length", String
					.valueOf(bytes.length));
			dos = new DataOutputStream(con.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"upload\";"
					+ " filename=\"" + fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			// dos.write(bytes);
			// dos.writeBytes(lineEnd);

			int nextByte;

			StringBuilder rivi = new StringBuilder();
			// FIXME: Potential NPE
			while ((nextByte = iis.read()) >= 0) {
				if (rivi.length() > 64) {
					dos.writeBytes(rivi.toString() + lineEnd);
					rivi = new StringBuilder();
				}
				rivi.append(hexi.charAt((nextByte >> 4) & 0xf));
				rivi.append(hexi.charAt((nextByte) & 0xf));

			}

			if (rivi.length() > 0) {
				dos.writeBytes(rivi.toString() + lineEnd);
			}

			// send multipart form data necesssary after file data...

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams

			dos.flush();
			dos.close();

			// Read the response
			InputStream in = con.getInputStream();
			int inle = 0;
			while (true) {
				int idata = in.read();
				if (idata == -1)
					break;
				inle++;
			}

			resu = con.getResponseCode();
			in.close();
			dos.close();
			con.disconnect();
			this.filename = fileName;
			logger.finest("resu: " + resu + "/" + inle);
		} catch (Exception e) {
			// FIXME: Spaghetti code. This method uses a try-catch block that
			// catches Exception objects, but Exception is not thrown within the
			// try block, and RuntimeException is not explicitly caught. This
			// construct also accidentally catches RuntimeException as well,
			// masking potential bugs.
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException {
		throw new SukuException("Post method not yet implemented");

	}

	@Override
	public long getFileLength() {
		logger.severe("getFileLength not implemented");
		return 0;
	}

	@Override
	public InputStream getInputStream() {
		logger.severe("getInputStream not implemented");
		return null;
	}

	@Override
	public String getFileName() {

		return "not available yet";
	}

	@Override
	public boolean createLocalFile(String filter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OutputStream getOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream openFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

}
