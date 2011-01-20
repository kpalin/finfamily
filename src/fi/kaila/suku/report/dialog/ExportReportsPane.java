package fi.kaila.suku.report.dialog;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;

/**
 * The Class OtherReportsPane.
 */
public class ExportReportsPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField genAncestors = null;
	private JTextField genDescendants = null;
	private JTextField genYoungFrom = null;
	private JCheckBox adopted = null;
	private JCheckBox parents = null;
	private JCheckBox spouses = null;

	private JCheckBox givenName = null;
	private JCheckBox surName = null;
	private JCheckBox occu = null;
	private JCheckBox lived = null;
	private JCheckBox place = null;
	private JCheckBox married = null;

	/**
	 * Instantiates a new other reports pane.
	 */
	public ExportReportsPane() {
		setLayout(null);
		int rtypx = 10;
		int rtypy = 20;

		JPanel pp = new JPanel();
		pp.setLayout(null);
		pp.setBounds(10, 0, 280, 200);

		// pane = new JPanel();
		pp.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("TB.REPORT")));
		// pane.setLayout(new GridLayout(0, 1));
		// pp.setBounds(rtypx, rtypy, 250, 70);

		add(pp);

		JLabel lb = new JLabel(Resurses.getString("TB.GENERATIONS"));
		lb.setBounds(rtypx, rtypy, 200, 20);

		pp.add(lb);
		rtypy += 24;

		genAncestors = new JTextField();
		// genAncestors.setText();
		pp.add(genAncestors);
		genAncestors.setBounds(rtypx, rtypy, 40, 20);
		lb = new JLabel(Resurses.getString("TB.BACKWARDS"));
		pp.add(lb);
		lb.setBounds(rtypx + 50, rtypy, 200, 20);
		rtypy += 24;

		genDescendants = new JTextField();
		// genDescendants.setText();
		pp.add(genDescendants);
		genDescendants.setBounds(rtypx, rtypy, 40, 20);
		lb = new JLabel(Resurses.getString("TB.DESCFROM"));
		pp.add(lb);
		lb.setBounds(rtypx + 50, rtypy, 200, 20);
		rtypy += 24;
		genYoungFrom = new JTextField();
		// genYoungFrom.setText();
		pp.add(genYoungFrom);
		genYoungFrom.setBounds(rtypx, rtypy, 40, 20);
		lb = new JLabel(Resurses.getString("TB.YOUNGFORWARD"));
		pp.add(lb);
		lb.setBounds(rtypx + 50, rtypy, 200, 20);
		rtypy += 24;
		adopted = new JCheckBox(Resurses.getString("REPORT.DESC.ADOPTEDALSO"));
		adopted.setBounds(rtypx, rtypy, 200, 20);
		pp.add(adopted);
		rtypy += 24;
		parents = new JCheckBox(Resurses.getString("TB.SHOWPARENTS"));
		parents.setBounds(rtypx, rtypy, 200, 20);
		pp.add(parents);
		rtypy += 24;
		spouses = new JCheckBox(Resurses.getString("TB.SHOWSPOUSES"));
		spouses.setBounds(rtypx, rtypy, 200, 20);
		pp.add(spouses);

		pp = new JPanel();
		pp.setLayout(null);
		pp.setBounds(10, 200, 150, 130);
		pp.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("TB.SHOWSETS")));
		add(pp);
		int rtypsx = 5;
		int rtypsy = 13;

		givenName = new JCheckBox(Resurses.getString("TB.GIVENNAME"));
		givenName.setBounds(rtypsx, rtypsy, 140, 20);
		pp.add(givenName);
		rtypsy += 18;
		surName = new JCheckBox(Resurses.getString("TB.SURNAME"));
		surName.setBounds(rtypsx, rtypsy, 140, 20);
		pp.add(surName);
		rtypsy += 18;
		occu = new JCheckBox(Resurses.getString("TB.OCCU"));
		occu.setBounds(rtypsx, rtypsy, 140, 20);
		pp.add(occu);
		rtypsy += 18;
		lived = new JCheckBox(Resurses.getString("TB.LIVED"));
		lived.setBounds(rtypsx, rtypsy, 140, 20);
		pp.add(lived);
		rtypsy += 18;
		place = new JCheckBox(Resurses.getString("TB.PLACE"));
		place.setBounds(rtypsx, rtypsy, 140, 20);
		pp.add(place);
		rtypsy += 18;
		married = new JCheckBox(Resurses.getString("TB.MARRIED"));
		married.setBounds(rtypsx, rtypsy, 140, 20);
		pp.add(married);

	}

	/**
	 * Gets the ancestors.
	 * 
	 * @return number of ancestors
	 */
	public int getAncestors() {
		String aux = genAncestors.getText();
		try {
			return Integer.parseInt(aux);

		} catch (NumberFormatException ne) {
			return 0;
		}

	}

	/**
	 * Gets the descendants.
	 * 
	 * @return number of descendants
	 */
	public int getDescendants() {
		String aux = genDescendants.getText();
		try {
			return Integer.parseInt(aux);

		} catch (NumberFormatException ne) {
			return 0;
		}

	}

	/**
	 * Gets the young from.
	 * 
	 * @return number of ......
	 */
	public int getYoungFrom() {
		String aux = genYoungFrom.getText();
		try {
			return Integer.parseInt(aux);

		} catch (NumberFormatException ne) {
			return 0;
		}

	}

	/**
	 * Checks if is adopted.
	 * 
	 * @return true is also adopted are included
	 */
	public boolean isAdopted() {
		return adopted.isSelected();
	}

	/**
	 * Checks if is parents.
	 * 
	 * @return true to include also other parents
	 */
	public boolean isParents() {
		return parents.isSelected();
	}

	/**
	 * Checks if is spouses.
	 * 
	 * @return true if also other spouses are included
	 */
	public boolean isSpouses() {
		return spouses.isSelected();
	}

	/**
	 * Checks if is givenname.
	 * 
	 * @return to show given name
	 */
	public boolean isGivenname() {
		return givenName.isSelected();
	}

	/**
	 * Checks if is surname.
	 * 
	 * @return to show surname
	 */
	public boolean isSurname() {
		return surName.isSelected();
	}

	/**
	 * Checks if is occupation.
	 * 
	 * @return to show occupation
	 */
	public boolean isOccupation() {
		return occu.isSelected();
	}

	/**
	 * Checks if is lived.
	 * 
	 * @return to show birth and death years
	 */
	public boolean isLived() {
		return lived.isSelected();
	}

	/**
	 * Checks if is place.
	 * 
	 * @return to show place
	 */
	public boolean isPlace() {
		return place.isSelected();
	}

	/**
	 * Checks if is married.
	 * 
	 * @return to show married date
	 */
	public boolean isMarried() {
		return married.isSelected();
	}

}
