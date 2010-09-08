package fi.kaila.suku.report.dialog;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;

/**
 * The descendant part of report settings.
 * 
 * @author Kalle
 */
public class DescendantPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField generations = null;
	private JCheckBox adopted = null;
	private JTextField spouseAncestors = null;
	private JTextField childAncestors = null;
	private JTextField startTable = null;
	private ButtonGroup tableOrder = null;

	/**
	 * setup pane.
	 */
	public DescendantPane() {

		int rtypx = 10;
		int rtypy = 10;

		setLayout(null);
		// reportTypePane.addTab(Resurses.getString("REPORT.DESCENDANT"), icon1,
		// panel1,
		// Resurses.getString("REPORT.TIP.DESCENDANT"));
		//
		// reportTypePane.setMnemonicAt(0, KeyEvent.VK_1);
		JPanel pane = new JPanel();
		JPanel pp = new JPanel();
		pp.setLayout(null);
		pp.setBounds(rtypx, rtypy, 200, 20);
		generations = new JTextField();
		generations.setText("" + 99);
		pp.add(generations);
		generations.setBounds(0, 0, 40, 20);
		JLabel lb = new JLabel(Resurses.getString("REPORT.DESC.GENERATIONS"));
		pp.add(lb);
		lb.setBounds(50, 0, 180, 20);
		add(pp);
		rtypy += 20;
		pp = new JPanel();
		pp.setLayout(null);
		pp.setBounds(rtypx, rtypy, 200, 20);
		startTable = new JTextField();
		startTable.setText("1");
		pp.add(startTable);
		startTable.setBounds(0, 0, 40, 20);
		lb = new JLabel(Resurses.getString("REPORT.DESC.STARTTABLE"));
		pp.add(lb);
		lb.setBounds(50, 0, 180, 20);
		add(pp);

		rtypy += 24;

		adopted = new JCheckBox(Resurses.getString("REPORT.DESC.ADOPTEDALSO"));
		adopted.setBounds(rtypx, rtypy, 200, 20);
		add(adopted);
		rtypy += 20;
		pp = new JPanel();
		pp.setLayout(null);
		pp.setBounds(0, 0, 200, 50);
		pane.add(pp);
		spouseAncestors = new JTextField();// (NumberFormat.getIntegerInstance());
		spouseAncestors.setText("0");

		// descendantSpouseAncestors.setPreferredSize(new Dimension(40,20));
		pp.add(spouseAncestors);
		spouseAncestors.setBounds(0, 0, 40, 20);
		lb = new JLabel(Resurses.getString("REPORT.DESC.SPOUSEANC"));
		pp.add(lb);
		lb.setBounds(50, 0, 180, 20);

		pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("REPORT.DESC.LISTA")));
		pane.setLayout(new GridLayout(0, 1));
		pane.setBounds(rtypx, rtypy, 250, 70);

		pp = new JPanel();
		pp.setLayout(null);
		pp.setBounds(0, 0, 200, 50);
		pane.add(pp);
		spouseAncestors = new JTextField();// NumberFormat.getIntegerInstance());
		spouseAncestors.setText("0");

		// descendantSpouseAncestors.setPreferredSize(new Dimension(40,20));
		pp.add(spouseAncestors);
		spouseAncestors.setBounds(0, 0, 40, 20);
		lb = new JLabel(Resurses.getString("REPORT.DESC.SPOUSEANC"));
		pp.add(lb);
		lb.setBounds(50, 0, 180, 20);

		pp = new JPanel();
		pp.setLayout(null);
		pane.add(pp);
		childAncestors = new JTextField();
		childAncestors.setText("0");
		// descendantChildAncestors.setPreferredSize(new Dimension(40,20));
		pp.add(childAncestors);
		childAncestors.setBounds(0, 0, 40, 20);

		lb = new JLabel(Resurses.getString("REPORT.DESC.CHILDANC"));
		pp.add(lb);
		lb.setBounds(50, 0, 180, 20);

		add(pane);

		rtypy += 70;

		pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("REPORT.DESC.ORDER")));
		pane.setLayout(new GridLayout(0, 1));

		pane.setBounds(rtypx, rtypy, 250, 130);

		tableOrder = new ButtonGroup();
		JRadioButton radio = new JRadioButton(
				Resurses.getString("REPORT.DESC.ORDER.TABLE"));
		tableOrder.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_ORDER_TAB);
		radio.setSelected(true);
		pane.add(radio);
		radio = new JRadioButton(Resurses.getString("REPORT.DESC.ORDER.MALE"));
		tableOrder.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_ORDER_MALE);
		pane.add(radio);
		radio = new JRadioButton(Resurses.getString("REPORT.DESC.ORDER.FEMALE"));
		tableOrder.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_ORDER_FEMALE);
		pane.add(radio);
		radio = new JRadioButton(
				Resurses.getString("REPORT.DESC.ORDER.MALEFIRST"));
		tableOrder.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_ORDER_FIRSTMALE);
		pane.add(radio);
		radio = new JRadioButton(
				Resurses.getString("REPORT.DESC.ORDER.REGISTER"));
		tableOrder.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_ORDER_REG);
		pane.add(radio);
		add(pane);

		radio = new JRadioButton(Resurses.getString("REPORT.LISTA.DESCLISTA"));
		tableOrder.add(radio);
		rtypy += 150;
		radio.setBounds(rtypx, rtypy, 200, 20);
		radio.setActionCommand("REPORT.LISTA.DESCLISTA");
		add(radio);

	}

	/**
	 * Gets the generations.
	 * 
	 * @return no of generations
	 */
	public int getGenerations() {

		int gen = 0;
		try {
			gen = Integer.parseInt(generations.getText());
		} catch (NumberFormatException ne) {
			gen = 99;
			generations.setText("99");
		}
		return gen;
	}

	/**
	 * Gets the start table.
	 * 
	 * @return start table number
	 */
	public int getStartTable() {
		int tab = 1;
		try {
			tab = Integer.parseInt(startTable.getText());
		} catch (NumberFormatException ne) {
			tab = 1;
			generations.setText("1");
		}
		return tab;
	}

	/**
	 * Gets the spouse ancestors.
	 * 
	 * @return no of spouse ancestor generations
	 */
	public int getSpouseAncestors() {

		int gen = 0;
		try {
			gen = Integer.parseInt(spouseAncestors.getText());
		} catch (NumberFormatException ne) {
			gen = 99;
			spouseAncestors.setText("0");
		}
		return gen;
	}

	/**
	 * Gets the child ancestors.
	 * 
	 * @return no of child ancestor generations
	 */
	public int getChildAncestors() {

		int gen = 0;
		try {
			gen = Integer.parseInt(childAncestors.getText());
		} catch (NumberFormatException ne) {
			gen = 99;
			childAncestors.setText("0");
		}
		return gen;
	}

	/**
	 * Gets the table order.
	 * 
	 * @return buttongroup for tableorder
	 */
	public ButtonGroup getTableOrder() {
		return tableOrder;
	}

	/**
	 * set generations.
	 * 
	 * @param string
	 *            the new generations
	 */
	void setGenerations(String string) {
		generations.setText(string);

	}

	/**
	 * set start table number.
	 * 
	 * @param tab
	 *            the new start table
	 */
	void setStartTable(String tab) {
		startTable.setText(tab);
	}

	/**
	 * Sets the spouse ancestors.
	 * 
	 * @param string
	 *            the new spouse ancestors
	 */
	void setSpouseAncestors(String string) {
		spouseAncestors.setText(string);

	}

	/**
	 * Sets the child ancestors.
	 * 
	 * @param string
	 *            the new child ancestors
	 */
	void setChildAncestors(String string) {
		childAncestors.setText(string);

	}

	/**
	 * Gets the adopted.
	 * 
	 * @return treu to show also adoped
	 */
	public boolean getAdopted() {
		return adopted.isSelected();
	}

	/**
	 * Sets the adopted.
	 * 
	 * @param value
	 *            the new adopted
	 */
	void setAdopted(boolean value) {
		adopted.setSelected(value);

	}

}
