package fi.kaila.suku.swing.worker;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;

public class DescendantPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField generations = null;
	private JCheckBox adopted = null;
	private JTextField spouseAncestors = null;
	private JTextField childAncestors = null;

	private ButtonGroup listFormatGroup = null;
	private ButtonGroup tableOrder = null;

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
		pane.setBounds(rtypx, rtypy, 250, 130);

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

		listFormatGroup = new ButtonGroup();
		JRadioButton formd = new JRadioButton(Resurses
				.getString("REPORT.DESC.LISTAPAFT"));
		formd.setActionCommand(ReportWorkerDialog.SET_PAFT);
		listFormatGroup.add(formd);
		pane.add(formd);
		formd = new JRadioButton(Resurses.getString("REPORT.DESC.LISTATAFT"));
		formd.setActionCommand(ReportWorkerDialog.SET_TAFT);
		listFormatGroup.add(formd);
		pane.add(formd);
		formd = new JRadioButton(Resurses.getString("REPORT.DESC.LISTARAFT"));
		formd.setActionCommand(ReportWorkerDialog.SET_RAFT);
		listFormatGroup.add(formd);
		pane.add(formd);

		add(pane);

		rtypy += 130;

		pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("REPORT.DESC.ORDER")));
		pane.setLayout(new GridLayout(0, 1));

		pane.setBounds(rtypx, rtypy, 250, 124);

		tableOrder = new ButtonGroup();
		JRadioButton radio = new JRadioButton(Resurses
				.getString("REPORT.DESC.ORDER.TABLE"));
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
		radio = new JRadioButton(Resurses
				.getString("REPORT.DESC.ORDER.MALEFIRST"));
		tableOrder.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_ORDER_FIRSTMALE);
		pane.add(radio);
		radio = new JRadioButton(Resurses
				.getString("REPORT.DESC.ORDER.REGISTER"));
		tableOrder.add(radio);
		radio.setActionCommand(ReportWorkerDialog.SET_ORDER_REG);
		pane.add(radio);

		add(pane);
	}

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

	public ButtonGroup getListFormatGroup() {
		return listFormatGroup;
	}

	public ButtonGroup getTableOrder() {
		return tableOrder;
	}

	public void setGenerations(String string) {
		generations.setText(string);

	}

	public void setSpouseAncestors(String string) {
		spouseAncestors.setText(string);

	}

	public void setChildAncestors(String string) {
		childAncestors.setText(string);

	}

	public boolean getAdopted() {
		return adopted.isSelected();
	}

	public void setAdopted(boolean value) {
		adopted.setSelected(value);

	}

}
