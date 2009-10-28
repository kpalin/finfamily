package fi.kaila.suku.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * A servlet for tomcat that is used in the webstart version The webstart
 * version implementation is not in a very active state at the moment Thus fixme
 * and warnings may not be
 * 
 * @author FIKAAKAIL
 * 
 */
public class SukuServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// FIXME:This Serializable class defines a non-primitive instance field
	// which is neither transient, Serializable, or java.lang.Object, and does
	// not appear to implement the Externalizable interface or the readObject()
	// and writeObject() methods. Objects of this class will not be deserialized
	// correctly if a non-Serializable object is stored in this field.
	private Context ctx = null;

	private HashMap<String, UserInfo> usermap = null;
	private static volatile int usercount = 0;

	private static Logger logger;

	private String dbServer = null;
	private String dbDatabase = null;
	private String dbUser = null;
	private String dbPassword = null;
	private String uploadFolder = null;

	@Override
	public void init() {
		Context ic = null;
		// String tmp;
		try {
			ic = new InitialContext();
			this.ctx = (Context) ic.lookup("java:comp/env");

			// tmp = initEnv("suku.db.driver");
			// if (tmp != null) this.dbDriver = tmp;

			this.dbServer = initEnv("suku.db.server");
			this.dbDatabase = initEnv("suku.db.database");
			this.dbUser = initEnv("suku.db.user");
			this.dbPassword = initEnv("suku.db.password");
			this.uploadFolder = initEnv("suku.upload.folder");

			// myTablePrefix = initEnv("forum.table.prefix");

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.usermap = new HashMap<String, UserInfo>();
		logger = Logger.getLogger(this.getClass().getName());

	}

	private static final String hexi = "0123456789ABCDEF";

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("POSTX2");
		String referer = req.getHeader("referer");
		logger.finest("referer on " + referer);
		if (referer != null) {
			String parts[] = referer.split("/");
			if (parts.length >= 2) {

				UserInfo ui = this.usermap.get("" + parts[2]);
				if (ui == null) {
					// logger.finest("parts2 " + parts[2]);
				} else {
					extractFile(req, this.uploadFolder + ui.getUserId() + "/",
							parts[3]);
				}
			}

		}

		processRequest(req, resp);

	}

	private void extractFile(HttpServletRequest req, String outputPath,
			String fileName) throws IOException, UnsupportedEncodingException,
			FileNotFoundException {

		InputStream is = req.getInputStream();

		File dir = new File(outputPath);
		if (!dir.exists()) {

			dir.mkdir();
		}

		logger.finest("File entering: " + outputPath + fileName);

		// FileOutputStream fos = new
		// FileOutputStream("C:/Tomcat6.0/logs/kk.log");
		int leni = 0;
		int pos = 0;
		int endi = 0;
		int idx;
		int j;
		int koko = 0;
		String rivi = "XXX";
		StringBuffer sb;
		String filename = null;
		FileOutputStream fos = null;
		byte bbb[] = new byte[32 * 1024];

		byte brivi[] = new byte[32 * 1024];
		while (true) {

			leni = is.read(bbb, pos, bbb.length - pos);
			if (leni > 0) {
				// System.out.println("XXXXXXXX");
				//				
				// for (j = pos; j < pos+leni; j++) {
				// System.out.print(bbb[j]);
				// }
				// System.out.println("YYYYYYYY");
				endi = endi + leni;
			}
			// System.out.println("leini: " + endi);
			String tmp;
			sb = new StringBuffer();
			for (idx = 0; idx < endi; idx++) {
				if ((bbb[idx] == '\n')) {
					for (j = 0; j < idx; j++) {
						if (bbb[j] != '\r') {
							tmp = new String(bbb, j, 1, "US-ASCII");
							sb.append(tmp);
						}
					}
					rivi = sb.toString();
					for (j = idx + 1; j < endi; j++) {
						bbb[j - idx - 1] = bbb[j];
					}
					pos = j - idx - 1;
					endi = endi - idx - 1;
					break;
				}
			}
			// System.out.println("rivi: "+filename + "/"+rivi.length()+"/" +
			// rivi);

			if (filename == null) {
				// System.out.println("R0:" + rivi);
				if (rivi.indexOf("Content-Disposition:") >= 0) {
					filename = rivi;
					fos = new FileOutputStream(outputPath + fileName);

					// // System.out.println("R1:" + rivi);
					// j = rivi.indexOf("filename");
					// if (j > 0) {
					// // System.out.println("R2:" + j);
					// i = rivi.indexOf("\"", j);
					// j = rivi.indexOf("\"", i+1);
					// // System.out.println("R3:" + i + "/"+ j);
					// if (j > i) {
					// filename = rivi.substring(i+1,j);
					// // System.out.println("FILNM:" + filename + "Ä");
					// fos = new FileOutputStream(this.uploadFolder + filename);
					//							
					// }
					//						
					// }
				}
			} else if (rivi.length() > 0 && (rivi.length() & 1) == 0) {
				int bi = 0;
				int x1, x2;
				if (hexi.indexOf(rivi.charAt(0)) >= 0) {
					for (j = 0; j < rivi.length() - 1; j += 2) {
						x1 = Integer.parseInt(rivi.substring(j, j + 1), 16);
						x2 = Integer.parseInt(rivi.substring(j + 1, j + 2), 16);
						// System.out.println("x:" + j + "/" + bi + "/" + x1 +
						// "/" + x2);
						brivi[bi++] = (byte) (((x1 << 4) & 0xf0) | (x2 & 0xf));

					}
					// FIXME: Potential NPE
					fos.write(brivi, 0, bi);
					koko += bi;

				}
			}

			// System.out.println("endi: " + endi);

			if (endi <= 0)
				break;
		}
		// FIXME: Potential NPE
		fos.close();
		is.close();

		logger.fine("File " + filename + " size " + koko + " to " + outputPath
				+ fileName);

		if (fileName != null && fileName.toLowerCase().endsWith("zip")) {

			ZipFile z = new ZipFile(outputPath + "/" + fileName);

			System.out.println("z:" + z);
			File f;
			FileOutputStream os;
			Enumeration e = z.entries();
			ZipEntry ze;
			InputStream ins;
			int lit;
			byte bbbb[] = new byte[32 * 1024];
			while (e.hasMoreElements()) {
				ze = (ZipEntry) e.nextElement();
				System.out.println("zen:" + ze.getName());
				if (ze.isDirectory()) {
					f = new File(outputPath + "/" + ze.getName());
					if (!f.isDirectory()) {
						// FIXME: This method returns a value that is not
						// checked. The return value should be checked since it
						// can indicate an unusual or unexpected function
						// execution.
						f.mkdirs();
					}

				} else {

					ins = z.getInputStream(ze);
					os = new FileOutputStream(outputPath + "/" + ze.getName());

					while (true) {
						lit = ins.read(bbbb);
						if (lit > 0) {
							os.write(bbbb, 0, lit);
						} else {
							break;
						}
					}
					os.close();

				}
			}
			z.close();

		}

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// System.out.println("GET");
		processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// Enumeration enu = req.getHeaderNames();
		// String nimi;
		// String value;
		// String refers[]=null;
		// while (enu.hasMoreElements()){
		// nimi = (String)enu.nextElement();
		// value = req.getHeader(nimi);
		// System.out.println("x:" + nimi + ":" + value);
		// if (nimi.equalsIgnoreCase("referer")){
		// refers = value.split("/") ;
		// }
		//			
		// }
		// System.out.println("-------------");
		// enu = req.getAttributeNames();
		// while (enu.hasMoreElements()){
		// nimi = (String)enu.nextElement();
		// value = req.getHeader(nimi);
		// System.out.println("x:" + nimi + ";" + value);
		// }
		// System.out.println("-------------");
		// enu = req.getParameterNames();
		// while (enu.hasMoreElements()){
		// nimi = (String)enu.nextElement();
		// value = req.getParameter(nimi);
		// System.out.println("x:" + nimi + ";" + value);
		// }
		// System.out.println("-------------");
		//		
		//		

		// System.out.println(new Date());

		String cmd;
		String userid;
		String passwd;
		String pid;
		String file;
		String lang;
		String filename;

		String uno;
		int userno = 0;
		LinkedHashMap<String, String> vpara = new LinkedHashMap<String, String>();

		Enumeration enu = req.getParameterNames();
		String key;
		StringBuffer sbx = new StringBuffer();
		while (enu.hasMoreElements()) {
			key = (String) enu.nextElement();
			vpara.put(key, req.getParameter(key));
			if (sbx.length() > 0)
				sbx.append(";");
			sbx.append(key + "=" + req.getParameter(key));
		}

		logger.fine("parms: " + sbx.toString());

		cmd = req.getParameter("cmd");
		vpara.remove("cmd");
		file = req.getParameter("file");
		vpara.remove("file");

		lang = req.getParameter("lang");
		vpara.remove("lang");
		filename = req.getParameter("filename");
		vpara.remove("filename");
		userid = req.getParameter("userid");
		vpara.remove("userid");
		passwd = req.getParameter("passwd");
		vpara.remove("passwd");
		uno = req.getParameter("userno");
		vpara.remove("userno");
		pid = req.getParameter("pid");
		vpara.remove("pid");

		logger.fine("cmd=" + cmd);

		// if (uno == null) {
		// if (refers != null && refers.length > 1) {
		// uno = refers[0];
		// file = refers[1];
		// }
		// }
		String params[] = null;

		// System.out.println("NYT TULEE: " + uno + "/" + file);

		// System.out.println("FOLDER:" + this.uploadFolder + "|" + file);

		if (uno != null) {
			try {
				userno = Integer.parseInt(uno);
			} catch (NumberFormatException e) {
				//
			}
		}

		if (userid != null && passwd != null) {
			userno = ++usercount;
			UserInfo ui = new UserInfo("" + userno, userid, passwd);
			this.usermap.put("" + userno, ui);
			PrintWriter out = resp.getWriter();
			resp.setHeader("Content-Type", "text/html");
			out.println("" + userno + "/" + Suku.sukuVersion);

			Cookie cok = new Cookie("userid", "" + userno);
			cok.setMaxAge(1800);
			resp.addCookie(cok);

			return;
		}

		UserInfo ui = this.usermap.get("" + userno);

		if (file != null) {
			file = this.uploadFolder + ui.getUserId() + "/" + file;
		}
		if (filename != null) {
			filename = this.uploadFolder + ui.getUserId() + "/" + filename;
		}

		Vector<String> v = new Vector<String>();
		if (cmd != null)
			v.add("cmd=" + cmd);
		if (pid != null)
			v.add("pid=" + pid);
		if (file != null)
			v.add("file=" + file);
		if (lang != null)
			v.add("lang=" + lang);
		if (filename != null)
			v.add("filename=" + filename);

		Set<Map.Entry<String, String>> entries = vpara.entrySet();
		Iterator<Map.Entry<String, String>> it = entries.iterator();

		while (it.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) it
					.next();
			v
					.add(entry.getKey().toString() + "="
							+ entry.getValue().toString());
		}

		String[] ss = new String[0];
		params = v.toArray(ss);

		if (cmd == null) {

			PrintWriter out = resp.getWriter();
			resp.setHeader("Content-Type", "text/html");
			out.println("sukuohjelmisto");
			return;
		}

		if (cmd.equals("logout")) { // logout request

			// resp.setHeader("Content-Type", "text/html");

			this.usermap.remove("" + userno);

		}

		// String koe = req.getHeader("x-userno");
		//		
		// System.out.println("koe=" + koe);
		//		
		// Iterator<String>iit = this.usermap.keySet().iterator();
		// while (iit.hasNext()){
		// String key = iit.next();
		//			
		// UserInfo uu = this.usermap.get(key);
		//			
		// System.out.println("Coo: " + key + "/" + uu.toString());
		//			
		//			
		// }

		// Cookie cook[] = req.getCookies();
		// if (cook != null){
		// for (int i = 0;i < cook.length;i++){
		// System.out.println("cookie [" + i + "] " + cook[i].getName() + "/" +
		// cook[i].getValue());
		// }
		// } else {
		// System.out.println("cooks was nulli");
		// }

		resp.setHeader("Content-Type", "text/html");
		SukuServer sk;

		try {
			sk = new SukuServerImpl(ui.getUserId());
			ServletOutputStream sos = resp.getOutputStream();
			logger.fine("log0: " + this.dbServer + "/" + this.dbDatabase + "/"
					+ this.dbUser + "/" + this.dbPassword);

			sk.getConnection(this.dbServer, this.dbDatabase, this.dbUser,
					this.dbPassword);
			// System.out.println("GETx1:");
			StringBuffer sb = new StringBuffer();
			sb.append("log1");
			for (int i = 0; i < params.length; i++) {
				sb.append(";" + params[i]);
			}
			logger.fine(sb.toString());

			SukuData fam = sk.getSukuData(params);
			// if (cmd.equals("logout")){
			// fam = sk.getSukuData("cmd=logout") ;
			// } else if (cmd.equals("plist")){
			// fam = sk.getSukuData("cmd=plist") ;
			// } else if (cmd.equals("family") && pid != null){
			// fam = sk.getSukuData("cmd=family","pid=" + pid) ;
			// } else {
			// fam = sk.getSukuData(params);
			// }

			logger.fine("Returning fam: " + fam);

			GZIPOutputStream gos;
			try {
				gos = new GZIPOutputStream(sos);
				ObjectOutputStream oos = new ObjectOutputStream(gos);
				oos.writeObject(fam);
				oos.close();
				logger.fine("Returned fam: " + fam);

			} catch (Exception e) {
				throw new SukuException("getShortPersonList", e);

			}

		} catch (SukuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;

	}

	private String initEnv(String text) {
		try {
			return (String) this.ctx.lookup(text);
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}

	class UserInfo {
		private String userno = null;
		private String userid = null;
		private String passwd = null;
		private long lastUsed = 0;

		UserInfo(String userno, String userid, String passwd) {
			this.userno = userno;
			this.userid = userid;
			this.passwd = passwd;
			this.lastUsed = System.currentTimeMillis();
		}

		String getUserNo() {
			this.lastUsed = System.currentTimeMillis();
			return this.userno;
		}

		String getUserId() {
			return this.userid;
		}

		@Override
		public String toString() {
			return this.userno + "/" + this.userid + "/" + this.passwd + "/"
					+ this.lastUsed;

		}

	}

}
