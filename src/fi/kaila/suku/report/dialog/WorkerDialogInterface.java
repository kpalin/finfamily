package fi.kaila.suku.report.dialog;

import javax.swing.JDialog;

/**
 * @author Kalle
 * 
 */
public interface WorkerDialogInterface {

	/**
	 * @return height of image
	 */
	public int getImageMaxHeight();

	/**
	 * @return height of person image
	 */
	public int getPersonImageMaxHeight();

	/**
	 * @return true for debugmode
	 */
	public boolean getDebugState();

	/**
	 * @return the dialog item
	 */
	public JDialog getWorkerDialog();

	/**
	 * @return true if images are to numbered
	 */
	public boolean isNumberingImages();

}
