package fi.kaila.suku.report.dialog;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.kaila.suku.util.Resurses;

public class OtherReportsPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OtherReportsPane() {
		setLayout(null);

		JLabel lb = new JLabel(Resurses.getString("REPORT.OTHER"));

		lb.setBounds(50, 0, 180, 20);
		add(lb);
	}

}
