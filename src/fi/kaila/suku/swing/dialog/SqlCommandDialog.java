package fi.kaila.suku.swing.dialog;

import javax.swing.JDialog;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

public class SqlCommandDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This class executes sql-commands to db.
	 * 
	 * sql command is executed calling the server
	 * 
	 * SukuData resu = Suku.kontroll
	 * 
	 * @param parent
	 */
	public SqlCommandDialog(Suku parent) {
		System.out.println("ollaan konstructorissa");
		String sql = "select pid from unitnotice where tag='NOTE'";
		SukuData req = new SukuData();
		req.generalText = sql;
		try {
			SukuData resu = Suku.kontroller.getSukuData(req, "cmd=sql",
					"type=select");

			//
			// resu.vvTexts contains the result of the query

		} catch (SukuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
