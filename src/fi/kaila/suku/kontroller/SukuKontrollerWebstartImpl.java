package fi.kaila.suku.kontroller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class SukuKontrollerWebstartImpl.
 * 
 * @author FIKAAKAIL
 * 
 *         remote implementation for kontroller
 */
public class SukuKontrollerWebstartImpl implements SukuKontroller {

	private static Logger logger = null;

	private String filename = null;
	FileContents fc = null;

	// public void createSukuDb() throws SukuException {
	// // TODO Auto-generated method stub
	//
	// }

	/**
	 * constructor sets environment for remote.
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getConnection(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void getConnection(String host, String dbname, String userid,
			String passwd) throws SukuException {

		String requri = this.codebase + "SukuServlet?userid=" + userid
				+ "&passwd=" + passwd;

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

				String auxes[] = aux.split("/");

				this.userno = auxes[0];
				if (auxes.length > 1) {
					Suku.serverVersion = auxes[1];
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getPref(java.lang.Object,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String getPref(Object o, String key, String def) {
		// System.out.println("K1:" + key);
		if (!this.isWebStart) {
			return sr.get(o.getClass().getName() + "." + key, def);
		}
		// System.out.println("K2:" + o.toString());
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
			// System.out.println("K3:" + baseURL);
			URL editorURL = new URL(baseURL, key);
			// System.out.println("K3:" + editorURL);
			FileContents fc = ps.get(editorURL);
			DataInputStream is = new DataInputStream(fc.getInputStream());
			aux = is.readUTF();
			is.close();
			// System.out.println("Luki: " + key + "/" + aux);
			return aux;
		} catch (FileNotFoundException fe) {
			// System.out.println("NOT FOUND: key=" + key);
			return def;
		} catch (Exception e) {
			Utils.println(this, "Kaatui: e=" + e);
			e.printStackTrace();
		}

		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#putPref(java.lang.Object,
	 * java.lang.String, java.lang.String)
	 */
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
			DataOutputStream os = new DataOutputStream(
					fc.getOutputStream(false));

			os.writeUTF(value);
			os.flush();
			os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(String... params) throws SukuException {
		StringBuilder sb = new StringBuilder();
		sb.append(this.codebase);
		String requri;
		int resu;
		int i;
		SukuData errr = new SukuData();
		errr.resu = "ERROR";
		// String paras[] = new String[params.length];
		// "suku?userno="+this.userno+"&person=" + pid
		if (this.userno == null) {
			return errr;
		}
		String paras[] = params;

		sb.append("SukuServlet?userno=" + this.userno);

		for (i = 0; i < paras.length; i++) {
			sb.append("&" + paras[i]);
		}
		// if (this.filename != null) {
		// sb.append("&filename=" + this.filename);
		// }

		requri = sb.toString();
		try {

			// requri += para;
			// System.out.println();
			// JOptionPane.showMessageDialog(null, "URI on: " + requri);
			logger.fine("URILOG: " + requri);
			Utils.println(this, "URILOG: " + requri);
			URL url = new URL(requri);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			// String encoding = uc.getContentEncoding();

			resu = uc.getResponseCode();
			// JOptionPane.showMessageDialog(null, "Resu = " + resu);

			if (resu == 200) {
				String coding = uc.getHeaderField("Content-Encoding");
				// JOptionPane.showMessageDialog(null, "coding on: " + coding);
				// JOptionPane.showMessageDialog(null, "resu on: " + resu);

				StringBuilder xx = new StringBuilder();
				xx.append("Content-Encoding: " + coding);
				xx.append(";");
				for (int j = 0; j < params.length; j++) {
					xx.append(params[j]);
					xx.append(";");
				}
				Utils.println(this, xx.toString());

				InputStream in = null;
				if ("gzip".equals(coding)) {
					in = new GZIPInputStream(uc.getInputStream());
				} else {
					in = uc.getInputStream();
				}
				// InputStream in = uc.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(in);
				SukuData fam = null;
				try {
					fam = (SukuData) ois.readObject();
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new SukuException(e);
				}
				// this.filename = null;
				// JOptionPane.showMessageDialog(null, "se onnistui");
				return fam;

			}
			throw new SukuException("Network error " + resu);

		} catch (Exception e) {
			Utils.println(this, "Ilman dataa failed: " + e.toString());
			// JOptionPane.showMessageDialog(null,
			// "se xpäonnistui+" + e.toString());
			throw new SukuException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#openLocalFile(java.lang.String)
	 */
	@Override
	public boolean openFile(String filter) {
		this.filename = null;
		FileOpenService fos;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		DataOutputStream dos = null;
		InputStream iis = null;

		int resu;
		// System.out.println("huhuu");
		try {
			fos = (FileOpenService) ServiceManager
					.lookup("javax.jnlp.FileOpenService");

			// ask user to select a file through this service
			fc = fos.openFileDialog(null, null);
			if (fc == null) {
				return false;
			}
			filename = fc.getName();
			iis = fc.getInputStream();
			// ask user to select multiple files through this service
			// FileContents[] fcs = fos.openMultiFileDialog(null, null);

			String uri = this.codebase;
			String query;

			uri += "SukuServlet";
			// query = "intres";
			query = "cmd=file";
			// query += "&TRM_TP=" + URLEncoder.encode("Kaila", "UTF-8");
			// ///////////////////////////////////////
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
			// "multipart/form-data;boundary=" + boundary);
			//
			// con.setRequestMethod("POST");
			// con.setRequestProperty("Referer", "/SSS/" + this.userno + "/"
			// + fileName + "/");
			// con.setRequestProperty("Content-Length",
			// String.valueOf(bytes.length));
			// dos = new DataOutputStream(con.getOutputStream());
			// dos.writeBytes(twoHyphens + boundary + lineEnd);
			// dos.writeBytes("Content-Disposition: form-data; name=\"upload\";"
			// + " filename=\"" + fileName + "\"" + lineEnd);
			// dos.writeBytes(lineEnd);

			// //////////////////////////////////////

			byte[] bytes = query.getBytes();
			// JOptionPane.showMessageDialog(null, "bytes:" + bytes.length);
			URL url = new URL(uri);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			// JOptionPane.showMessageDialog(null, "setPostPian");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			con.setRequestProperty("Referer", "/SSS/" + this.userno + "/"
					+ filename + "/");
			con.setRequestProperty("Content-Length",
					String.valueOf(bytes.length));
			con.setRequestMethod("POST");

			dos = new DataOutputStream(con.getOutputStream());
			// JOptionPane.showMessageDialog(null, "dos");
			dos.write(bytes);
			dos.writeBytes(lineEnd);
			// dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes(lineEnd);

			// ////////////////////////////////////

			// dos.write(bytes);
			// dos.writeBytes(lineEnd);

			int nextByte;

			StringBuilder rivi = new StringBuilder();

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
			// this.filename = null;

			Utils.println(this, "resu: " + resu + "/" + inle);
		} catch (Exception e) {
			Utils.println(this, "w/o data " + e.toString());
			e.printStackTrace();
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(fi.kaila.suku.util
	 * .pojo.SukuData, java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(SukuData request, String... params)
			throws SukuException {

		SukuData errr = new SukuData();
		errr.resu = "ERROR";
		if (this.userno == null) {
			return errr;
		}

		if (request == null) {
			return getSukuData(params);
		}
		StringBuilder query = new StringBuilder();
		// query.append(this.codebase);

		int resu;

		String paras[] = params;
		try {
			query.append("userno=" + this.userno);

			for (int i = 0; i < paras.length; i++) {
				query.append("&" + URLEncoder.encode(paras[i], "UTF-8"));
			}

			// requri = query.toString();

			// requri += para;
			// System.out.println("URI on: " + requri);

			// JOptionPane.showMessageDialog(null, requri);

			// FileOpenService fos;
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";
			DataOutputStream dos = null;
			// InputStream iis = null;

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buff = null;

			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(request);
			buff = bos.toByteArray();
			oos.close();
			// JOptionPane.showMessageDialog(null, "oos-closed");
			InputStream gis = new ByteArrayInputStream(buff);

			// GZIPInputStream gis = new GZIPInputStream(new
			// ByteArrayInputStream(
			// buff));
			//
			String uri = this.codebase + "SukuServlet";
			// String query;
			//
			// uri += "SukuServlet";
			// // query = "intres";
			// query = "cmd=fff";
			// query += "&TRM_TP=" + URLEncoder.encode("Kaila", "UTF-8");
			// JOptionPane.showMessageDialog(null, "uri:" + uri);
			byte[] bytes = query.toString().getBytes();
			// JOptionPane.showMessageDialog(null, "bytes:" + bytes.length);
			URL url = new URL(uri);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			// JOptionPane.showMessageDialog(null, "setPostPian");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			con.setRequestProperty("Referer", "/SSS/" + this.userno + "/");
			con.setRequestProperty("Content-Length",
					String.valueOf(bytes.length));
			con.setRequestMethod("POST");
			// resu = con.getResponseCode();
			// JOptionPane.showMessageDialog(null, "resup:[" + resu + "]");

			// con.setRequestProperty(
			// "Content-Type",
			// "application/x-www-form-urlencoded; charset=UTF-8");

			// JOptionPane.showMessageDialog(null, "Content-Length");
			dos = new DataOutputStream(con.getOutputStream());
			// JOptionPane.showMessageDialog(null, "dos");
			dos.write(bytes);
			dos.writeBytes(lineEnd);
			// dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes(lineEnd);

			// dos.writeBytes("Content-Disposition: form-data; name=\"upload\";"
			// + " filename=\"" + "fileName" + "\"" + lineEnd);
			// dos.writeBytes(lineEnd);

			// JOptionPane.showMessageDialog(null, "write lineEnd");
			// dos.write(bytes);
			// dos.writeBytes(lineEnd);

			int nextByte;

			StringBuilder rivi = new StringBuilder();

			while ((nextByte = gis.read()) >= 0) {
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
			// JOptionPane.showMessageDialog(null, "write lineEnd2");
			// close streams

			dos.flush();
			dos.close();
			// JOptionPane.showMessageDialog(null, "streams-closed");
			// Read the response
			Utils.println(this, "withData getting response now");

			String coding = con.getHeaderField("Content-Encoding");

			InputStream in = null;
			if ("gzip".equals(coding)) {
				in = new GZIPInputStream(con.getInputStream());
			} else {
				in = con.getInputStream();
			}

			ObjectInputStream ois = new ObjectInputStream(in);
			SukuData fam = null;
			try {
				fam = (SukuData) ois.readObject();
				ois.close();
			} catch (Exception e) {
				Utils.println(this, e.toString());

				throw new SukuException(e);
			}
			// this.filename = null;

			return fam;

		} catch (Throwable e) {
			Utils.println(this, "Data failed+" + e.toString());
			// JOptionPane.showMessageDialog(null,
			// "datalla epäonnistui+" + e.toString());
			throw new SukuException(e);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileLength()
	 */
	@Override
	public long getFileLength() {
		if (fc != null) {
			try {
				return fc.getLength();
			} catch (IOException e) {
				Utils.println(this, e.toString());
			}
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {

		if (fc != null) {
			try {
				return fc.getInputStream();
			} catch (IOException e) {
				Utils.println(this, "getInputStream " + e.toString());
				return null;
			}
		}
		Utils.println(this, "getInputStream not found");
		logger.severe("getInputStream not found");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileName()
	 */
	@Override
	public String getFileName() {
		return filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#createLocalFile(java.lang.String)
	 */
	@Override
	public boolean createLocalFile(String filter) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#openFile(java.lang.String)
	 */
	@Override
	public InputStream openLocalFile(String path) {

		try {
			FileOpenService fos = (FileOpenService) ServiceManager
					.lookup("javax.jnlp.FileOpenService");

			fc = fos.openFileDialog(null, null);
			if (fc != null) {
				return fc.getInputStream();
			}
		} catch (Exception e1) {
			Utils.println(this, "openfile: " + e1.toString());

		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFilePath()
	 */
	@Override
	public String getFilePath() {
		// return null in webstart
		return null;
	}

	@Override
	public boolean isWebStart() {

		return true;
	}

}
