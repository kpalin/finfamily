package fi.kaila.suku.report;

import fi.kaila.suku.report.style.BodyText;
import fi.kaila.suku.util.SukuException;

/**
 * 
 * Report writer implements the report interface. Report writers are planned for
 * Word (possibly Word 2003 xml-format as in Suku 2004) and html A report writer
 * using Java textpane as output is used for preview
 * 
 * 
 * @author Kalle
 * 
 */
public interface ReportInterface {

	/**
	 * Add text to the report
	 * 
	 * @param text
	 *            as a {@link BodyText } based object.
	 */
	public void addText(BodyText text);

	/**
	 * close the report
	 * 
	 * @throws SukuException
	 */
	public void closeReport() throws SukuException;

	/**
	 * create the report
	 * 
	 * @throws SukuException
	 */
	public void createReport() throws SukuException;

}
