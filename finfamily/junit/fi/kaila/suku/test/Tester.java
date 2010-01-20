package fi.kaila.suku.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Tester {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Tester t = new Tester();
		t.doMe(args);
	}

	private void doMe(String[] args) throws IOException {

		ZipFile z = new ZipFile(args[0] + "/" + args[1]);

		System.out.println("z:" + z);
		File f;
		FileOutputStream os;
		Enumeration e = z.entries();
		ZipEntry ze;
		InputStream is;
		int lit;
		byte bbb[] = new byte[32 * 1024];
		while (e.hasMoreElements()) {
			ze = (ZipEntry) e.nextElement();
			System.out.println("zen:" + ze.getName());
			if (ze.isDirectory()) {
				f = new File(args[0] + "/" + ze.getName());
				if (!f.isDirectory()) {
					// FIXME: This method returns a value that is not checked.
					// The return value should be checked since it can indicate
					// an unusual or unexpected function execution.
					f.mkdirs();
				}

			} else {

				is = z.getInputStream(ze);
				os = new FileOutputStream(args[0] + "/" + ze.getName());

				while (true) {
					lit = is.read(bbb);
					if (lit > 0) {
						os.write(bbb, 0, lit);
					} else {
						break;
					}
				}
				os.close();

			}

			System.out
					.println("ze:" + ze.getName() + "DIR:" + ze.isDirectory());
		}

	}

}
