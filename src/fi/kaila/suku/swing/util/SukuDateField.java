package fi.kaila.suku.swing.util;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuDateException;
import fi.kaila.suku.util.Utils;

public class SukuDateField extends JPanel {

	private static final long serialVersionUID = 1L;
	JComboBox datePref;
	JTextField dateFrom;
	JTextField dateTo;

	JLabel datePost;

	public SukuDateField() {

		setLayout(null);
		String dateprex[] = Resurses.getString("DATE_PREFS").split(";");
		datePref = new JComboBox(dateprex);
		add(datePref);
		dateFrom = new JTextField();
		add(dateFrom);
		datePost = new JLabel("TO");
		add(datePost);
		dateTo = new JTextField();
		add(dateTo);

		datePref.setBounds(0, 0, 78, 20);
		dateFrom.setBounds(80, 0, 80, 20);
		datePost.setBounds(162, 0, 20, 20);
		dateTo.setBounds(183, 0, 80, 20);

	}

	public String getDatePrefTag() {

		int idx = datePref.getSelectedIndex();
		switch (idx) {
		case 1:
			return "ABT";
		case 2:
			return "CAL";
		case 3:
			return "EST";
		case 4:
			return "BET";
		case 5:
			return "FROM";
		case 6:
			return "BEF";
		case 7:
			return "AFT";
		default:
			return null;
		}
	}

	public String getFromDate() throws SukuDateException {
		return Utils.dbDate(dateFrom.getText());
	}

	public boolean isPlain() {
		if (datePref.getSelectedIndex() == 0)
			return true;
		return false;
	}

	public String getTextFromDate() {
		return dateFrom.getText();
	}

	public void setTextFromDate(String text) {
		dateFrom.setText(text);
	}

	public String getToDate() throws SukuDateException {
		return Utils.dbDate(dateTo.getText());
	}

	public void setDate(String pre, String from, String to) {
		int preIdx = 0;

		if (from == null) {
			datePost.setText("");
			// dateTo.setVisible(false);
			return;
		}
		if (pre != null) {
			if (pre.equals("ABT"))
				preIdx = 1;
			if (pre.equals("CAL"))
				preIdx = 2;
			if (pre.equals("EST"))
				preIdx = 3;
			if (pre.equals("BET"))
				preIdx = 4;
			if (pre.equals("FROM"))
				preIdx = 5;
			if (pre.equals("BEF"))
				preIdx = 6;
			if (pre.equals("AFT"))
				preIdx = 7;

		}
		datePref.setSelectedIndex(preIdx);
		dateFrom.setText(Utils.textDate(from, true));
		datePost.setText("");
		if ("FROM".equals(pre) || "BET".equals(pre)) {
			dateTo.setVisible(true);
			if (pre.equals("FROM")) {
				datePost.setText(Resurses.getString("DATE_TO"));
			} else {
				datePost.setText(Resurses.getString("DATE_AND"));
			}
			if (to != null) {
				dateTo.setText(Utils.textDate(to, true));
			}
		} else {
			dateTo.setVisible(false);
		}

	}

}
