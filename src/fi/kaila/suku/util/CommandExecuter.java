package fi.kaila.suku.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Executor of operating system commands.
 * 
 * @author Kalle
 */
public class CommandExecuter {

	private static Logger logger = Logger.getLogger(CommandExecuter.class
			.getName());

	/**
	 * execute the command.
	 * 
	 * @param cmd
	 *            each command part is in seperate string
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws SukuException
	 *             the suku exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void executeTheCommnad(String[] cmd)
			throws InterruptedException, SukuException, IOException {

		Process process = null;
		long execStarted = System.currentTimeMillis();

		// Execute the native command
		process = Runtime.getRuntime().exec(cmd);

		StringBuffer stdoutBuffer = new StringBuffer();
		StringBuffer stderrBuffer = new StringBuffer();
		streamToBuffer(process.getInputStream(), stdoutBuffer);
		streamToBuffer(process.getErrorStream(), stderrBuffer);

		// Wait for process to complete
		process.waitFor();

		// Get the exit value
		int exitValue = process.exitValue();

		// logger.debug("ArchiveFileStorageService :: Execution exit value=" +
		// exitValue);
		// logger.debug("ArchiveFileStorageService :: Execution result=" +
		// stdoutBuffer.toString());
		// logger.debug("ArchiveFileStorageService :: Execution error=" +
		// stderrBuffer.toString());

		// Check the exit value. It needs to be 0
		if (exitValue != 0) {
			throw new SukuException(stderrBuffer.toString());
		}

		long execEnded = System.currentTimeMillis();
		@SuppressWarnings("unused")
		long execTimeInS = 0;

		try {
			execTimeInS = (execEnded - execStarted) / 1000L;
		} catch (Exception ignorE) {
			//
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cmd.length; i++) {
			if (i > 0)
				sb.append(" ");
			sb.append(cmd[i]);
		}

		logger.info("Command '" + sb.toString() + "'  executed in "
				+ (execEnded - execStarted) + " ms.");

	}

	private static void streamToBuffer(final InputStream input,
			final StringBuffer buffer) {
		new Thread(new Runnable() {

			public void run() {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(input));
					int i = -1;
					while ((i = reader.read()) != -1) {
						buffer.append((char) i);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
